package hu.peetertoth.serverpassword.data;

import hu.peetertoth.serverpassword.lce.EntityTargetAlertLCE;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.bukkit.entity.EntityType.SLIME;

/**
 * Created by kyle on 2017. 03. 18..
 */
public class ScoreboardInformation {
    private String playerName;
    private Scoreboard scoreboard;
    private Set<Entity> intruders;
    private Map<String, String> entityIdAndDisplayName;

    public ScoreboardInformation(String playerName, Scoreboard scoreboard) {
        this.playerName = playerName;
        this.scoreboard = scoreboard;
        this.intruders = new HashSet<>();
        this.entityIdAndDisplayName = new HashMap<>();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Set<Entity> getIntruders() {
        return intruders;
    }

    public String addIntruder(Entity intruder) {
        intruders.add(intruder);

        String displayName = getColoredNameOfIntruder(intruder);

        if (entityIdAndDisplayName.containsValue(displayName)) {
            int index = 1;
            while (entityIdAndDisplayName.containsValue(displayName + "(" + index + ")")) {
                index++;
            }
            displayName += "(" + index + ")";
        }

        return entityIdAndDisplayName.put(intruder.getUniqueId().toString(), displayName);
    }

    private String getColoredNameOfIntruder(Entity intruder) {
        ChatColor chatColor;
        switch (intruder.getType()) {
            case SLIME:
            case CREEPER:
                chatColor = ChatColor.GREEN;
                break;
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case PIG_ZOMBIE:
                chatColor = ChatColor.DARK_GREEN;
                break;
            case HUSK:
                chatColor = ChatColor.DARK_GRAY;
                break;
            case SKELETON:
            case GHAST:
                chatColor = ChatColor.GRAY;
                break;
            case SPIDER:
            case CAVE_SPIDER:
                chatColor = ChatColor.DARK_RED;
                break;
            case WITCH:
                chatColor = ChatColor.DARK_PURPLE;
                break;
            case ENDERMAN:
                chatColor = ChatColor.DARK_BLUE;
                break;
            case BLAZE:
            case MAGMA_CUBE:
                chatColor = ChatColor.YELLOW;
                break;
            default:
                chatColor = ChatColor.RESET;
                break;
        }
        return chatColor + intruder.getName() + ChatColor.RESET;
    }

    private boolean removeIntruder(Entity intruder) {
        if (!intruders.contains(intruder)) {
            return false;
        }
        intruders.remove(intruder);
        entityIdAndDisplayName.remove(String.valueOf(intruder.getUniqueId()));

        return true;
    }

    public String getIntruderName(Entity intruder) {
        return entityIdAndDisplayName.get(String.valueOf(intruder.getUniqueId()));
    }

    public void resetScores(Entity entity) {
        if (intruders.contains(entity)) {
            scoreboard.resetScores(getIntruderName(entity));
            removeIntruder(entity);
        }
    }

    public void setScores(Entity entity, int score) {
        scoreboard.getObjective(EntityTargetAlertLCE.OBJECTIVE_NAME).getScore(getIntruderName(entity)).setScore(score);
    }
}
