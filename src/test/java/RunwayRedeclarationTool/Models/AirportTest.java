package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Exceptions.ConfigurationFileNotFound;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.config.Config_Manager;
import RunwayRedeclarationTool.Models.config.Configuration;
import RunwayRedeclarationTool.Models.db.DB_controller;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.fail;

public class AirportTest {

    private Configuration config;
    private DB_controller controller;
    private static boolean setup_is_done = false;

    public AirportTest() {
        Config_Manager config_manager = new Config_Manager();
        try {
            config = config_manager.load_config();
            this.controller = new DB_controller(config);
        } catch (ConfigurationFileNotFound configurationFileNotFound) {
            configurationFileNotFound.printStackTrace();
            return;
        }
    }

    @Test
    public void AirportRemoveAddTest(){
        // Clear the datbase of airports
        for(Airport a : controller.get_airports()){
            controller.remove_Airport(a);
            Logger.Log("Removing airport " + a.toString());
        }

        int bound = new Random().nextInt(100);
        Logger.Log("Using upper bound for airport add + remove test [" + bound + "].");
        ArrayList<Airport> airports = new ArrayList<>();
        for(int x = 0; x < bound; x++){
            Airport a = new Airport("Test" + x, "TS" + x);
            airports.add(a);
            controller.add_airport(a);
        }

        ArrayList<Airport> recoveredAirports = new ArrayList<Airport>(Arrays.asList(controller.get_airports()));

        outer:
        for(int x = 0; x < bound; x++){
            for(Airport db_copy : recoveredAirports) {
                if(db_copy.getAirport_id().equals(airports.get(x).getAirport_id()) && db_copy.getAirport_name().equals(airports.get(x).getAirport_name())){
                    continue outer;
                }
            }
            fail("Failed to find Airport " + airports.get(x) + "in database!");
        }


        Logger.Log("Test Passed! " + bound + " airports added to database and removed again.");
    }


}
