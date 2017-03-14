package hu.peetertoth.serverpassword;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Created by tpeter on 2017.03.11..
 */
public class PlayerInformation implements ConfigurationSerializable {

    private static final String KEY_NAME = "name";
    private static final String KEY_HOST_NAME = "hostName";

    private String name;

    private String hostName;

    public PlayerInformation() {
    }

    public PlayerInformation(String name) {
        this.name = name;
        this.hostName = "notSpecified";
    }

    public PlayerInformation(String name, String hostName) {
        this.name = name;
        this.hostName = hostName;
    }

    public PlayerInformation(Map<String, Object> map) {
        PlayerInformation playerInformation = PlayerInformation.deserialize(map);
        this.name = playerInformation.name;
        this.hostName = playerInformation.hostName;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new TreeMap<>();
        map.put(KEY_NAME, name);
        map.put(KEY_HOST_NAME, hostName);

        return map;
    }

    public static PlayerInformation deserialize(Map<String, Object> map) {
        return new PlayerInformation(
                Optional
                        .of((String) map.get(KEY_NAME))
                        .orElse(KEY_NAME + "_undef"),
                Optional
                        .of((String) map.get(KEY_HOST_NAME))
                        .orElse(KEY_HOST_NAME + "_undef"));
    }

    public static PlayerInformation valueOf(Map<String, Object> map) {
        return deserialize(map);
    }
}
