package io.starseed.eliteNeth.settings;

import org.mineacademy.fo.settings.SimpleSettings;
import org.mineacademy.fo.settings.YamlConfig;

public class Settings extends SimpleSettings {

    public static void reload() {
    }

    @Override
    protected int getConfigVersion() {
        return 1;
    }
    public static class TradeSettings {
        public static String MENU_TITLE;

    }

    public static class NetherSettings {
        public static String WORLD_NAME;
        public static int MAX_TIME_MINUTES;
        public static int COOLDOWN_HOURS;
        public static String PREFIX;
    }

    public static class Messages {
        public static String PLAYERS_ONLY = "Only Players Can Use This Command!";
        public static String NOT_IN_NETHER = "You Are Not In The Nether!";
        public static String NO_PERMISSION;
        public static String TIME_LEFT;
        public static String TIME_EXPIRED;
        public static String COOLDOWN_ACTIVE;
        public static String ENTERED_NETHER;
        public static String ALREADY_IN_NETHER;
        public static String TRADE_SUCCESS;
        public static String TRADE_FAILED;
    }

    private static void init() {
        pathPrefix("Settings");
        NetherSettings.WORLD_NAME = getString("Nether_World_Name");
        NetherSettings.MAX_TIME_MINUTES = getInteger("Max_Time_Minutes");
        NetherSettings.COOLDOWN_HOURS = getInteger("Cooldown_Hours");
        NetherSettings.PREFIX = getString("Prefix");

        pathPrefix("Messages");
        Messages.NO_PERMISSION = getString("No_Permission");
        Messages.TIME_LEFT = getString("Time_Left");
        Messages.TIME_EXPIRED = getString("Time_Expired");
        Messages.COOLDOWN_ACTIVE = getString("Cooldown_Active");
        Messages.ENTERED_NETHER = getString("Entered_Nether");
        Messages.ALREADY_IN_NETHER = getString("Already_In_Nether");
        Messages.PLAYERS_ONLY = getString("Players_Only");
        Messages.NOT_IN_NETHER = getString("Not_In_Nether");
        Messages.TRADE_SUCCESS = getString("Trade_Success");
        Messages.TRADE_FAILED = getString("Trade_Failed");
    }
}