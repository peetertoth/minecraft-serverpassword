package hu.peetertoth.serverpassword.data;

import hu.peetertoth.serverpassword.lce.EntityTargetAlertLCE;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        String displayName = intruder.getName();
        if (entityIdAndDisplayName.containsValue(displayName)) {
            int index = 1;
            while (entityIdAndDisplayName.containsValue(displayName + "(" + index + ")")) {
                index++;
            }
            displayName += "(" + index + ")";
        }

        return entityIdAndDisplayName.put(intruder.getUniqueId().toString(), displayName);
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
