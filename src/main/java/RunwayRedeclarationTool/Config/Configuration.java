package RunwayRedeclarationTool.Config;

import RunwayRedeclarationTool.Exceptions.ConfigurationKeyNotFound;
import RunwayRedeclarationTool.Logger.Logger;

import java.util.HashMap;

public class Configuration {

    private final HashMap<String, String> configuration_values;

    public Configuration(HashMap<String, String> map){
        this.configuration_values = map;
        Logger.Log("Setup new configuration object.");
    }

    public String getConfigurationValue(String key) throws ConfigurationKeyNotFound {
        if(configuration_values.keySet().contains(key)){
            return configuration_values.get(key);
        } else {
            throw new ConfigurationKeyNotFound("Failed to find configuration key: " + key);
        }
    }


}
