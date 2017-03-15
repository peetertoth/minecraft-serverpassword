package hu.peetertoth.serverpassword.lce;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kyle on 2017. 03. 15..
 */
public class EndermanLCE implements Listener, CommandExecutor, Logging {

    private Set<String> enabled;
    private Set<Material> trasparentMaterials;

    public EndermanLCE(Collection<? extends Player> onlinePlayers) {
        this.enabled = new HashSet<>();
        this.trasparentMaterials = new HashSet<>();
        {
            trasparentMaterials.add(Material.AIR);
            trasparentMaterials.add(Material.WATER);
        }
    }

    @Override
    public Class getImplementerClass() {
        return getClass();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (enabled.contains(sender.getName())) {
            enabled.remove(sender.getName());
            sender.sendMessage("[Enderman] disabled");
        } else {
            enabled.add(sender.getName());
            sender.sendMessage("[Enderman] enabled");
        }
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("interact");
        if (enabled.contains(player.getName())) {
            player.sendMessage("teleport");

            player.teleport(player.getTargetBlock(this.trasparentMaterials, 1000)
                    .getLocation()
                    .add(0, 1, 0)
                    .setDirection(player.getLocation().getDirection()));

        }
    }
}
