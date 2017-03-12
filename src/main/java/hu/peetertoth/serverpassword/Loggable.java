package hu.peetertoth.serverpassword;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tpeter on 2017.03.11..
 */
public abstract class Loggable {

    private Logger LOGGER;
    private String prefix;

    public void logError(String s, IOException e) {
        getLogger().log(Level.ALL, prefix + " " + s, e);
    }

    public void logInfo(String s) {
        getLogger().info(prefix + " " + s);
    }

    private Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = Logger.getLogger(getImplementerClass().getCanonicalName());
            prefix = CONSTANTS.LOGGER_PREFIX + "[" + getImplementerClass().getSimpleName() + "]";
        }
        return LOGGER;
    }

    protected abstract Class getImplementerClass();
}
