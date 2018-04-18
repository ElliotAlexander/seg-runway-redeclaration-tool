package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Exceptions.ConfigurationKeyNotFound;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.config.Config_Manager;
import RunwayRedeclarationTool.Models.config.Configuration;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.fail;

public class FileLoggerTest {

    private Logger logger;
    private Configuration config ;

    @org.junit.Before
    public void setUp() throws Exception {
        Config_Manager config_manager = new Config_Manager();
        config = config_manager.load_config();
        Logger logger = new Logger(config);

    }


    @Test
    public void logFileExists(){
        try {
            String logdir = config.getConfigurationValue("LogDirectory");
            String logfile = config.getConfigurationValue("LogFile");

            File log_dir_file = new File(logdir);


            String logstr = log_dir_file.getAbsolutePath() + "/" + logfile;
            if(!(new File(logstr).exists())){
                fail("Couldn't find logfile.");
            }
        } catch (ConfigurationKeyNotFound configurationKeyNotFound) {
            fail("Failed to load configuration vales when setting up loggers.");
        }


    }

}
