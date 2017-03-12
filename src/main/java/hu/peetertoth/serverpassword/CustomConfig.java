package hu.peetertoth.serverpassword;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by tpeter on 2017.03.11..
 */
public class CustomConfig extends Loggable {

    private String filePath;
    private FileConfiguration configuration;

    private Logger LOGGER = Logger.getLogger(CustomConfig.class.getCanonicalName());

    public CustomConfig(String filePath) {
        this.filePath = filePath;
        reload();
    }

    /**
     * Reload config content from file
     */
    public void reload() {
        File file = new File(filePath);
        if (file.exists()) {
            this.configuration = YamlConfiguration.loadConfiguration(file);
            logInfo("File loaded. { name = " + file.getName() + " }");
        } else {
            this.configuration = new YamlConfiguration();
            try {
                this.configuration.save(file);
                logInfo("Empty file created. { name = " + file.getName() + " }");
            } catch (IOException e) {
                logError("Failed to save empty file. { name = " + file.getName() + " }", e);
            }
        }
    }

    public void save() {
        File file = new File(filePath);
        try {
            this.configuration.save(file);
            logInfo("File saved successfully. { name = " + file.getName() + " }");
        } catch (IOException e) {
            logError("Failed to save file. { name = " + file.getName() + " }", e);
        }
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    protected Class getImplementerClass() {
        return getClass();
    }
}
