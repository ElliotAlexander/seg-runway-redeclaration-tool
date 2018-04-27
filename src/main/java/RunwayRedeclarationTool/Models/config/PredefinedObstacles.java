package RunwayRedeclarationTool.Models.config;

import RunwayRedeclarationTool.Exceptions.ConfigurationKeyNotFound;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.Obstacle;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.Models.xml.XML_Parser;
import jdk.nashorn.internal.runtime.arrays.ArrayIndex;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class PredefinedObstacles {

    private final String default_obstacles_file = "default_obstacles.txt";

    public void addDefaults(DB_controller dbc, Configuration configuration){

        try {
            if(configuration.getConfigurationValue("LoadpredefinedAirportsfromfile").equalsIgnoreCase("yes")){
                obstacle_loop:
                for(Obstacle o : load_obstacles_file()){
                    for(Obstacle o2 : dbc.get_obstacles()){
                        if(o2.getName().equalsIgnoreCase(o.getName())){
                            Logger.Log("Skipping already existing obstacle \'" + o.getName() + "\'.");
                            continue obstacle_loop;
                        }
                    }
                    dbc.add_obstacle(o);
                }
            }
        } catch (ConfigurationKeyNotFound configurationKeyNotFound) {
            configurationKeyNotFound.printStackTrace();
        }
    }



    private Obstacle[] load_obstacles_file(){
        ArrayList<Obstacle> return_list = new ArrayList<Obstacle>();
        //Get file from resources folder
        InputStream is = DB_controller.class.getClassLoader().getResourceAsStream(default_obstacles_file);
        try {
            if(!(is.available() == 0)){
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(":");
                        String name = values[0];
                        Integer height = Integer.parseInt(values[1]);
                        return_list.add(new Obstacle(name, height));
                    }
                    return return_list.toArray(new Obstacle[return_list.size()]);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e ){
                    Logger.Log(Logger.Level.ERROR, "Error Loading predefined obstacles file!\nArrayIndexOutOfBoundsException");
                    e.printStackTrace();
                } catch (NumberFormatException e){
                    Logger.Log(Logger.Level.ERROR, "NumberFormatException when processing default_obstacles file.");
                    e.printStackTrace();
                }
            } else {
                Logger.Log(Logger.Level.ERROR, "Failed to load default obstacles file! [Filename=\'"+default_obstacles_file+"\'].");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
