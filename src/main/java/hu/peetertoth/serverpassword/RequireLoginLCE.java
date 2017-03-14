package hu.peetertoth.serverpassword;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by tpeter on 2017.03.11..
 */
public class RequireLoginLCE extends Loggable implements Listener, CommandExecutor {

    private CustomConfig playersData;
    private List<String> authenticatedUsers;

    private final String password;
    private final String loginMessage;
    private final String successfulLoginMessage;

    public RequireLoginLCE(CustomConfig playersData, FileConfiguration configuration) {
        this.playersData = playersData;
        this.password = configuration.getString(CONSTANTS.Config.KEY.PASSWORD);
        this.loginMessage = configuration.getString(CONSTANTS.Config.KEY.LOGIN_MESSAGE);
        this.successfulLoginMessage = configuration.getString(CONSTANTS.Config.KEY.SUCCESSFUL_LOGIN_MESSAGE);

        this.authenticatedUsers = new ArrayList<>();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        String hostName = player.getAddress().getHostName();

        PlayerInformation playerInformation = getPlayerInformation(name);
        if (playerInformation != null) {
            if (playerInformation.getHostName().equals(hostName)) {
                authenticatedUsers.add(name);
            } else {
                player.sendMessage(loginMessage);
            }
        }
    }

    private PlayerInformation getPlayerInformation(String name) {
        return (PlayerInformation) playersData.getConfiguration().get(name);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
    }

    @Override
    protected Class getImplementerClass() {
        return getClass();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equals(password)) {
                final String name = sender.getName();

                this.authenticatedUsers.add(name);
                PlayerInformation playerInformation = Optional.of(getPlayerInformation(name)).orElse(new PlayerInformation(name));
                playerInformation.setHostName(sender.getServer().getOnlinePlayers()
                        .stream()
                        .filter(onlinePlayer -> onlinePlayer.getName().equals(name))
                        .findFirst()
                        .get()
                        .getAddress()
                        .getHostName());
                sender.sendMessage(successfulLoginMessage);
                return true;
            }
        } else {
            return false;
        }

        sender.sendMessage(loginMessage);
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!authenticatedUsers.contains(event.getPlayer().getName()) &&
                !event.getMessage().startsWith("/login")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(loginMessage);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!authenticatedUsers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!authenticatedUsers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!authenticatedUsers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!authenticatedUsers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            if (!authenticatedUsers.contains(event.getEntity().getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!authenticatedUsers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        if (!authenticatedUsers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            if (!authenticatedUsers.contains(event.getEntity().getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER) && !authenticatedUsers.contains(event.getEntity().getName()) ||
                event.getDamager().getType().equals(EntityType.PLAYER) && !authenticatedUsers.contains(event.getDamager().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            if (!authenticatedUsers.contains(event.getEntity().getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            if (!authenticatedUsers.contains(event.getEntity().getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (!authenticatedUsers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!authenticatedUsers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getType().equals(EntityType.PLAYER)) {
            if (!authenticatedUsers.contains(event.getWhoClicked().getName())) {
                event.setCancelled(true);
            }
        }
    }
}
