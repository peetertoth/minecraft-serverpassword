package hu.peetertoth.serverpassword;

/**
 * Created by tpeter on 2017.03.11..
 */
public class CONSTANTS {
    public static String LOGGER_PREFIX = "[ServerPassword]";

    public static class Config {
        public static class KEY {
            public static final String REQUIRE_PASSWORD = "require_password";
            public static final String PASSWORD = "password";
            public static final String LOGIN_COMMAND = "login_command";
            public static final String LOGIN_MESSAGE = "login_message";
            public static final String SUCCESSFUL_LOGIN_MESSAGE = "login_message_successful";
        }
    }
    public static class PlayerData {
        public static final String FILE_NAME = "player_data.yml";
    }
    public static class CakeData {
        public static final String FILE_NAME = "c_data.yml";
        public static final String CAKE_ARRAY = "c_array";
    }
}
