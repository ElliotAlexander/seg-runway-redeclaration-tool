package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Exceptions.ConfigurationKeyNotFound;
import RunwayRedeclarationTool.Models.config.Config_Manager;
import RunwayRedeclarationTool.Models.config.Configuration;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.fail;

public class ConfigurationTest {

    private Config_Manager config_manager;
    private Configuration config;

    @org.junit.Before
    public void setUp() throws Exception {
        this.config_manager = new Config_Manager();
        config = config_manager.load_config();
        System.out.println("Loaded configuration");
    }

    @Test
    public void check_config_exists(){
        String userdata_path, config_file_string;
        String user_home = System.getProperty("user.home");
        user_home = user_home.replaceAll("\\\\", "/");
        userdata_path = user_home + "/" + "Runway_Redeclaration_Tool";
        File dir = new File(userdata_path);
        if(!dir.exists()){
            fail("Configuration Folder does not exist!");
        }
        File f = new File(userdata_path + "/config.txt");
        if(!f.exists()){
            fail("Configuration file does not exist!");
        }

    }

    @Test
    public void check_keys(){
        try {
            for(String s : config.getKeys()){
                Assert.assertNotNull(config.getConfigurationValue(s));

            }
        } catch (ConfigurationKeyNotFound configurationKeyNotFound) {
            fail("Failed to load all configuration keys from the config file!");
        }
    }
}
