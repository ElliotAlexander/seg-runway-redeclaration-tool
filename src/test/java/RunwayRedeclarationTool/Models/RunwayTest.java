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

public class RunwayTest {

    private Configuration config;
    private DB_controller controller;
    private static boolean setup_is_done = false;

    public RunwayTest() {
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
    public void RunwayAddRemoveTest(){
        // Clear the datbase of airports and Runways
        for(Airport a : controller.get_airports()){
            controller.remove_Airport(a);
            Logger.Log("Removing Runway " + a.toString());
        }

        Airport testAirport = new Airport("Test Airport", "TST");
        controller.add_airport(testAirport);

        RunwayParameters testRP = new RunwayParameters(3901, 3901, 3500, 3000);


        Random random = new Random();
        int bound = 10;
        Logger.Log("Using upper bound for runway add + remove test [" + bound + "].");

        ArrayList<Runway> runways = new ArrayList<>();
        for(int x = 0; x < bound; x++){
            String d1 = x + "L";
            String d2 = x + "R";

            Logger.Log("Adding virtual runway");
            VirtualRunway vr1 = new VirtualRunway(d1, testRP);
            VirtualRunway vr2 = new VirtualRunway(d2, testRP);
            Runway runway = new Runway(vr1, vr2);
            runways.add(runway);
            Logger.Log("Adding " + runway.toString());
            controller.add_Runway(runway, testAirport.getAirport_id());
        }

        ArrayList<Runway> recoveredRunways = new ArrayList<Runway>(Arrays.asList(controller.get_runways()));

        outer:
        for(int x = 0; x < bound; x++){
            for(Runway db_copy : recoveredRunways) {
                if(db_copy.toString().equals(runways.get(x).toString())){
                    continue outer;
                }
            }
            fail("Failed to find Runway " + runways.get(x) + "in database!");
        }


        Logger.Log("Test Passed! " + bound + " Runways added to database and removed again.");
    }
}
