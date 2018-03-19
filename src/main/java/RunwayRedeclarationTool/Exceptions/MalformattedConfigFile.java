package RunwayRedeclarationTool.Exceptions;

import RunwayRedeclarationTool.Logger.Logger;

public class MalformattedConfigFile extends Exception {

    public MalformattedConfigFile(){
        super();
    }


    public MalformattedConfigFile(String message){
        super();
        Logger.Log(Logger.Level.ERROR, message);
    }

}
