package hu.peetertoth.serverpassword.main;

import hu.peetertoth.serverpassword.CONSTANTS;
import hu.peetertoth.serverpassword.data.CustomConfig;
import hu.peetertoth.serverpassword.data.PlayerInformation;
import hu.peetertoth.serverpassword.lce.EndermanLCE;
import hu.peetertoth.serverpassword.lce.RequireLoginLCE;
import hu.peetertoth.serverpassword.lce.WalkingOnWaterLCE;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

public final class ServerPassword extends JavaPlugin implements Listener {

    /**
     * Stores player data
     */
    private CustomConfig playersData;

    @Override
    public void onEnable() {
        // Register class for serialization
        ConfigurationSerialization.registerClass(PlayerInformation.class);

        // Initialize config
        initConfig();

        // Read players data
        this.playersData = new CustomConfig(getDataFolder() + "/" + CONSTANTS.PlayerData.FILE_NAME);

        RequireLoginLCE requireLoginLCE = new RequireLoginLCE(this.playersData, this.getConfig());

        // Require login
        if (getConfig().getBoolean(CONSTANTS.Config.KEY.REQUIRE_PASSWORD))
            getServer().getPluginManager().registerEvents(requireLoginLCE, this);
        getCommand(getConfig().getString(CONSTANTS.Config.KEY.LOGIN_COMMAND)).setExecutor(requireLoginLCE);

        // Walking on water
        WalkingOnWaterLCE walkingOnWaterLCE = new WalkingOnWaterLCE(getServer().getOnlinePlayers());
        getServer().getPluginManager().registerEvents(walkingOnWaterLCE, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, walkingOnWaterLCE, 0L, 1L);
        getCommand("run").setExecutor(walkingOnWaterLCE);

        // Enderman
        EndermanLCE endermanLCE = new EndermanLCE(getServer().getOnlinePlayers());
        getServer().getPluginManager().registerEvents(endermanLCE, this);
        getCommand("enderman").setExecutor(endermanLCE);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.playersData.save();
    }

    private void initConfig() {
        Map<String, Object> defaults = new TreeMap<>();
        defaults.put(CONSTANTS.Config.KEY.REQUIRE_PASSWORD, true);
        defaults.put(CONSTANTS.Config.KEY.PASSWORD, "changeMe123");
        defaults.put(CONSTANTS.Config.KEY.LOGIN_COMMAND, "login");
        defaults.put(CONSTANTS.Config.KEY.LOGIN_MESSAGE, "Please use /login <password> command to log in!");
        defaults.put(CONSTANTS.Config.KEY.SUCCESSFUL_LOGIN_MESSAGE, "You have successfully logged in.");

        getConfig().addDefaults(defaults);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void logError(String s, IOException e) {
        getLogger().log(Level.ALL, s, e);
    }

    private void logInfo(String s) {
        getLogger().info(s);
    }
}
