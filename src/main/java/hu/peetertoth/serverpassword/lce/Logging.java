package hu.peetertoth.serverpassword.lce;

import hu.peetertoth.serverpassword.CONSTANTS;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kyle on 2017. 03. 15..
 */
public interface Logging
{
    default void logInfo(String message) {
        getLogger().info(prefix() + message);
    }

    default void logError(String message, Exception e) {
        getLogger().log(Level.ALL, prefix() + message, e);
    }

    default String prefix() {
        return CONSTANTS.LOGGER_PREFIX + "[" + getImplementerClass().getSimpleName() + "] ";
    }

    default Logger getLogger() {
        return Logger.getLogger(getImplementerClass().getSimpleName());
    }

    Class getImplementerClass();
}
