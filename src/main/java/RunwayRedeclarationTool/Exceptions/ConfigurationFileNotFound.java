package RunwayRedeclarationTool.Exceptions;

import RunwayRedeclarationTool.Logger.Logger;

public class ConfigurationFileNotFound extends Exception {

    public ConfigurationFileNotFound(){
        super();
    }


    public ConfigurationFileNotFound(String message){
        super(message);
        Logger.Log(Logger.Level.ERROR, message);
    }
}
