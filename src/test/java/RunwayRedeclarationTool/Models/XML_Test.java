package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Controllers.MainWindowController;
import RunwayRedeclarationTool.Exceptions.ConfigurationFileNotFound;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.config.Config_Manager;
import RunwayRedeclarationTool.Models.config.Configuration;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.Models.xml.XML_Parser;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.fail;

public class XML_Test {


    private Logger logger;
    private Configuration config ;
    private MainWindowController mainWindowController;
    private DB_controller controller;

    public XML_Test() {
        Config_Manager config_manager = new Config_Manager();
        try {
            config = config_manager.load_config();
        } catch (ConfigurationFileNotFound configurationFileNotFound) {
            configurationFileNotFound.printStackTrace();
        }
        this.logger = new Logger(config);
        this.controller = new DB_controller(config);
        this.mainWindowController = new MainWindowController(config, controller);
    }

    @Test
    public void test_airport_import(){
        try {
            // Wipe the database
            for(Airport a : controller.get_airports()){
                controller.remove_Airport(a);
            }

            for(Obstacle o : controller.get_obstacles()){
                controller.remove_obstacle(o);
            }

            Logger.Log("Sleeping to avoid overloading database.");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            File file = new File("example_xml_import.xml");
            if(file.exists()){
                new XML_Parser(controller, mainWindowController).parse_xml(file);
            }

            if(controller.get_airports().length != 3){
                for(Airport a : controller.get_airports()){
                    Logger.Log("Airport " + a.toString() + " was imported successfully.");
                }
                fail("Failed to import all airports successfully!");
            }

            if(controller.get_obstacles().length != 3){
                for(Obstacle o : controller.get_obstacles()){
                    Logger.Log("Obstacle " + o.toString() + " was imported successfully.");
                }
                fail("Failed to import all obstacles successfully!");
            }

            Logger.Log("XML importing passed successfully!");
            controller = null;

        } catch (ExceptionInInitializerError e) {

        }


    }
}
