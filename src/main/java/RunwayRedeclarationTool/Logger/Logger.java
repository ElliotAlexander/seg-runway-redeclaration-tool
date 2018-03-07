package RunwayRedeclarationTool.Logger;

public class Logger {

    public enum Level{INFO, WARNING, ERROR}
    private static final FileLogger fl = new FileLogger();

    public static void Log(Level l, String message){
        System.out.println("[SERVER " + l + "] " + message);
        fl.Log("[" + l + "] " +  message);
    }


    // Defaults to info
    public static void Log(String message){
        System.out.println("[SERVER INFO] " + message);
        fl.Log("[" + Level.INFO + "] " +  message);
    }
}
