package RunwayRedeclarationTool.Logger;

import RunwayRedeclarationTool.Config.Configuration;
import RunwayRedeclarationTool.Exceptions.ConfigurationKeyNotFound;

public class Logger {

    public enum Level{INFO, WARNING, ERROR}
    private static FileLogger fl = null;

    // TODO maybe tidy this class a lot

    public Logger(Configuration config){
        try {
            fl = new FileLogger(config.getConfigurationValue("LogDirectory"), config.getConfigurationValue("LogFile"));
        } catch (ConfigurationKeyNotFound configurationKeyNotFound) {
            configurationKeyNotFound.printStackTrace();
        }
    }

    public static void Log(Level l, String message){
        System.out.println("[" + l + "] " + message);

        // This is honestly pretty sketch but should be safe.
        if(fl != null){
            fl.Log("[" + l + "] \n" +  message + "\n");
        }
    }


    // Defaults to info
    public static void Log(String message){
        System.out.println("[INFO] " + message);
        if(fl != null){
            fl.Log("[" + Level.INFO + "] " +  message + "\n");
        }
    }
}
