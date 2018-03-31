package RunwayRedeclarationTool.Models.config;

import RunwayRedeclarationTool.Exceptions.ConfigurationKeyNotFound;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.Models.xml.XML_Parser;

import java.io.File;

public class PredefinedObstacles {


    private final String Default_Obstacles_File = "default_obstacles.xml";
    private final String Default_Airports_File = "default_airports.xml";


    public void addDefaults(DB_controller dbc, Configuration configuration){

    }

}
