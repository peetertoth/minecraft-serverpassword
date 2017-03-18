package hu.peetertoth.serverpassword.lce;

import hu.peetertoth.serverpassword.data.ScoreboardInformation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by kyle on 2017. 03. 18..
 */
public class EntityTargetAlertLCE implements Listener, CommandExecutor, Logging, Runnable {

    public static final String OBJECTIVE_NAME = "Mob alert";
    private static final String CRITERIA = "dummy";

    /**
     * Player name - ScoreboardInformation
     */
    private final Map<String, ScoreboardInformation> scoreboards;

    private final ScoreboardManager scoreboardManager;

    private final Collection<? extends Player> onlinePlayers;

    public EntityTargetAlertLCE(Collection<? extends Player> onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
        this.scoreboardManager = Bukkit.getScoreboardManager();

        this.scoreboards = new TreeMap<>();
    }

    @Override
    public Class getImplementerClass() {
        return getClass();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (this.scoreboards.containsKey(player.getName())) {
                onDisable(player);
                sender.sendMessage("[EntityTargetAlert] Disabled");
            } else {
                onEnable(player);
                sender.sendMessage("[EntityTargetAlert] Enabled");
            }
            return true;
        }
        return false;
    }

    private void onEnable(Player player) {
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        player.setScoreboard(scoreboard);
        this.scoreboards.put(player.getName(), new ScoreboardInformation(player.getName(), scoreboard));
    }

    private void onDisable(Player player) {
        this.scoreboards.remove(player.getName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (!event.getEntity().getType().equals(EntityType.EXPERIENCE_ORB) &&
                event.getTarget() != null &&
                event.getTarget() instanceof Player) {
            setTargetOnPlayer(event.getTarget(), event.getEntity());
        } else {
            removeTarget(event.getEntity());
        }
    }

    private void setTargetOnPlayer(Entity target, Entity entity) {
        String name = target.getName();
        ScoreboardInformation sInfo = scoreboards.get(name);
        if (sInfo != null) {
            sInfo.addIntruder(entity);
        }
    }

    private void removeTarget(Entity entity) {
        scoreboards.forEach((name, sInfo) -> {
            sInfo.resetScores(entity);
        });
    }

    @Override
    public void run() {
        onlinePlayers.forEach(player -> {
            String name = player.getName();
            ScoreboardInformation scoreboard = this.scoreboards.get(name);
            if (scoreboard == null)
                return;

            Set<Entity> intruders = scoreboard.getIntruders();
            Set<Entity> removableIntruders = intruders
                    .stream()
                    .filter(intruder -> intruder.isDead())
                    .collect(Collectors.toSet());
            removableIntruders.forEach(entity -> {
                scoreboard.resetScores(entity);
            });

            if (scoreboard.getScoreboard().getObjectives().isEmpty()) {
                Objective objective = scoreboard.getScoreboard().registerNewObjective(OBJECTIVE_NAME, CRITERIA);
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }

            Objective objective = scoreboard.getScoreboard().getObjective(OBJECTIVE_NAME);

            intruders.forEach(intruder -> {
                int distance = (int) player.getLocation().distance(intruder.getLocation());
                scoreboard.setScores(intruder, distance);
            });
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (scoreboards.containsKey(event.getPlayer().getName())) {
            event.getPlayer().sendMessage("[EntityTargetAlert] Enabled");
        }
    }
}
