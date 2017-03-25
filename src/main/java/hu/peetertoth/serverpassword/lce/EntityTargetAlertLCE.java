package hu.peetertoth.serverpassword.lce;

import hu.peetertoth.serverpassword.data.ScoreboardInformation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;
import java.util.function.Predicate;
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
                if (args.length == 0) {
                    onDisable(player);
                    sender.sendMessage("[EntityTargetAlert] Disabled");
                } else if (args[0].equals("list")) {
                    this.scoreboards.get(player.getName()).getIntruders().forEach(intruder -> sender.sendMessage(intruder.toString()));
                }
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

        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        player.setScoreboard(scoreboard);
    }

    private void setTargetOnPlayer(Entity target, Entity entity) {
        String name = target.getName();
        ScoreboardInformation sInfo = scoreboards.get(name);
        if (sInfo != null) {
            sInfo.addIntruder(entity);
        }
    }

    private void removeTarget(Entity target, Entity entity) {
        String name = target.getName();
        ScoreboardInformation sInfo = scoreboards.get(name);
        if (sInfo != null) {
            sInfo.removeIntruder(entity);
        }
    }

    @Override
    public void run() {
        onlinePlayers.parallelStream().forEach(player -> {
            String name = player.getName();
            ScoreboardInformation scoreboard = this.scoreboards.get(name);
            if (scoreboard == null)
                return;

            Location playerLocation = player.getLocation();

            Predicate<Entity> withHeight = (intruder) -> !isFriendlyEntity(intruder.getType())
                    && playerLocation.distance(intruder.getLocation()) < 20
                    && Math.abs(playerLocation.getY() - intruder.getLocation().getY()) < 4;
            Predicate<Entity> withoutHeight = (intruder) -> !isFriendlyEntity(intruder.getType())
                    && playerLocation.distance(intruder.getLocation()) < 20;

            Predicate<Entity> twoInOne = (intruder) ->
                    canFly(intruder.getType()) ? withoutHeight.test(intruder) : withHeight.test(intruder);

            player.getLocation().getWorld().getEntities().forEach(entity -> {
                if (twoInOne.test(entity)) {
                    scoreboard.addIntruder(entity);
                } else {
                    scoreboard.removeIntruder(entity);
                }
            });

            Set<Entity> intruders = scoreboard.getIntruders();
            Set<Entity> removableIntruders = intruders
                    .stream()
                    .filter(intruder -> intruder.isDead() || !twoInOne.test(intruder))
                    .collect(Collectors.toSet());
            removableIntruders.forEach(entity -> {
                removeTarget(player, entity);
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

    private boolean isFriendlyEntity(EntityType type) {
        switch (type) {
            case CAVE_SPIDER:
            case ENDERMAN:
            case POLAR_BEAR:
            case SPIDER:
            case PIG_ZOMBIE:
            case BLAZE:
            case CREEPER:
            case ELDER_GUARDIAN:
            case ENDERMITE:
            case EVOKER:
            case GHAST:
            case GUARDIAN:
            case HUSK:
            case MAGMA_CUBE:
            case SHULKER:
            case SILVERFISH:
            case SKELETON:
            case SLIME:
            case STRAY:
            case VEX:
            case VINDICATOR:
            case WITCH:
            case WITHER_SKELETON:
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case ENDER_DRAGON:
            case WITHER:
            case GIANT:
            case LLAMA_SPIT:
            case ZOMBIE_HORSE:
                return false;
            default:
                return true;
        }
    }

    private boolean canFly(EntityType type) {
        switch (type) {
            case ENDER_DRAGON:
            case GUARDIAN:
            case ELDER_GUARDIAN:
            case GHAST:
            case BLAZE:
            case VEX:
                return true;
            default:
                return false;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();

        if (scoreboards.containsKey(name)) {
            player.setScoreboard(scoreboards.get(name).getScoreboard());
            player.sendMessage("[EntityTargetAlert] Enabled");
        }
    }
}
