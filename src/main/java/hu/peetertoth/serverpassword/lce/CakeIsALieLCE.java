package hu.peetertoth.serverpassword.lce;

import hu.peetertoth.serverpassword.CONSTANTS;
import hu.peetertoth.serverpassword.data.CustomConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.*;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by kyle on 2017. 03. 22..
 */
public class CakeIsALieLCE implements Listener, Logging, CommandExecutor {

    private Set<String> using;
    private CustomConfig cakeData;

    public CakeIsALieLCE(CustomConfig cakeData) {
        this.using = new HashSet<>();
        this.cakeData = cakeData;
    }

    @Override
    public Class getImplementerClass() {
        return getClass();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            this.using.add(sender.getName());
            sender.sendMessage("Click on cake!");
            return true;
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();

        List<Location> locations = (List<Location>) Optional
                .ofNullable(this.cakeData.getConfiguration().get(CONSTANTS.CakeData.CAKE_ARRAY))
                .orElse(new ArrayList<>());

        if (locations.contains(event.getClickedBlock().getLocation())) {
            player.sendMessage("This cake is a lie!");
            Location location = player.getLocation();
            location.setY(location.getY() + 3);

            location.getBlock().getWorld().spawnArrow(location, new Vector(0, -1, 0), 1f, 1f);
        }

        if (this.using.contains(name)) {
            this.using.remove(name);
            if (event.getClickedBlock().getType().equals(Material.CAKE_BLOCK)) {
                locations.add(event.getClickedBlock().getLocation());
                this.cakeData.getConfiguration().set(CONSTANTS.CakeData.CAKE_ARRAY, locations);

                player.sendMessage("Cake has been set.");
            } else {
                player.sendMessage("That's not a cake, try again.");
            }
        }


    }


}
