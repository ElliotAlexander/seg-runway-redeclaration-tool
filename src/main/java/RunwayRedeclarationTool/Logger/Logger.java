package RunwayRedeclarationTool.Logger;

import RunwayRedeclarationTool.Models.config.Configuration;
import RunwayRedeclarationTool.Exceptions.ConfigurationKeyNotFound;

public class Logger {

    public enum Level{INFO, WARNING, ERROR}
    private static FileLogger fl = null;
    private static String buffered_output = "";

    public Logger(Configuration config){
        try {
            fl = new FileLogger(config.getConfigurationValue("LogDirectory"), config.getConfigurationValue("LogFile"));
            fl.Log(buffered_output);
        } catch (ConfigurationKeyNotFound configurationKeyNotFound) {
            configurationKeyNotFound.printStackTrace();
        }
    }

    public static void Log(Level l, String message){
        System.out.println("[" + l + "] " + message);

        // This is honestly pretty sketch but should be safe.
        if(fl != null){
            fl.Log("[" + l + "] " +  message + "\n");
        } else {
            buffered_output += "[" + l + "] " +  message + "\n";
        }
    }


    // Defaults to info
    public static void Log(String message){
        System.out.println("[INFO] " + message);
        if(fl != null){
            fl.Log("[" + Level.INFO + "] " +  message + "\n");
        } else {
            buffered_output += "[INFO] " +  message + "\n";
        }
    }
}
