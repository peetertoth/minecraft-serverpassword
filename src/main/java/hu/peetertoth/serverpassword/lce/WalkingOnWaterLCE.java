package hu.peetertoth.serverpassword.lce;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by tpeter on 2017.03.11..
 */
public class WalkingOnWaterLCE implements Listener, CommandExecutor, Runnable {

    private Set<String> enabled;
    private Set<String> usingAbility;
    private Map<String, Long> usedAbility;

    private final Collection<? extends Player> players;


    public WalkingOnWaterLCE(Collection<? extends Player> onlinePlayers) {
        this.enabled = new HashSet<>();
        this.usingAbility = new HashSet<>();
        this.usedAbility = new HashMap<>();
        this.players = onlinePlayers;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (enabled.contains(sender.getName())) {
            enabled.remove(sender.getName());
            sender.sendMessage("[WalkingOnWater] Disabled");
        } else {
            enabled.add(sender.getName());
            sender.sendMessage("[WalkingOnWater] Enabled");
        }
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        String name = event.getPlayer().getName();
        if (event.isSneaking()) {
            usingAbility.add(name);
        } else {
            usingAbility.remove(name);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        String name = event.getEntity().getName();
        if (event.getEntityType().equals(EntityType.PLAYER) &&
                event.getCause().equals(EntityDamageEvent.DamageCause.FALL) &&
                usedAbility.containsKey(name)) {
            if (Calendar.getInstance().getTimeInMillis() - usedAbility.get(name) < 5000) {
                event.setCancelled(true);
                event.setDamage(0);
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void run() {
        players
                .parallelStream()
                .filter(player -> enabled.contains(player.getName()) && usingAbility.contains(player.getName()))
                .forEach(player -> {
                    Location loc = player.getLocation().getBlock().getLocation();
                    List<Material> materialsUnderPlayer = new ArrayList<>();
                    materialsUnderPlayer.add(loc.getBlock().getType());
                    loc.setY(loc.getY() - 1);
                    materialsUnderPlayer.add(loc.getBlock().getType());
                    loc.setY(loc.getY() - 1);
                    materialsUnderPlayer.add(loc.getBlock().getType());

                    if (materialsUnderPlayer.stream().anyMatch(material -> !material.equals(Material.AIR))) {

                        ItemStack heldItems = player.getInventory().getItemInMainHand();
                        if (heldItems.getType().equals(Material.STICK)) {
                            double amount = heldItems.getAmount() * 0.1;
                            player.setVelocity(player.getEyeLocation().getDirection().multiply(amount));
                        } else {
                            player.setVelocity(player.getEyeLocation().getDirection());
                        }
                        usedAbility.put(player.getName(), Calendar.getInstance().getTimeInMillis());
                    }

                });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (enabled.contains(event.getPlayer().getName()))
            event.getPlayer().sendMessage("[WalkingOnWater] Enabled");
    }
}
