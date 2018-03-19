package RunwayRedeclarationTool.Exceptions;

import RunwayRedeclarationTool.Logger.Logger;

public class ConfigurationKeyNotFound extends Exception {

    public ConfigurationKeyNotFound(String message){
        super();
        Logger.Log(message);
    }


    public ConfigurationKeyNotFound(){
        super();
    }

}
