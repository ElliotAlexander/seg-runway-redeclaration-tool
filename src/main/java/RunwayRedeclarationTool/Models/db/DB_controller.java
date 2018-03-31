package RunwayRedeclarationTool.Models.db;

import RunwayRedeclarationTool.Models.*;
import RunwayRedeclarationTool.Models.config.Configuration;
import RunwayRedeclarationTool.Exceptions.ConfigurationKeyNotFound;
import RunwayRedeclarationTool.Exceptions.MalformattedDataException;
import RunwayRedeclarationTool.Logger.Logger;

import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DB_controller
{
    private String DB_NAME, DB_FOLDER, DB_URL;

    private int runway_id = 0;
    private int obstacle_id = 0;
    private int physical_runway_id = 0;
    private Connection conn;

    public DB_controller(Configuration config){

        Logger.Log("Running DB_Controller...");

        // Setup config
        try {
            DB_NAME = config.getConfigurationValue("DatabaseName");
            DB_FOLDER = config.getConfigurationValue("DatabaseFolder");
            DB_URL =config.getConfigurationValue("DatabaseURL");

            if(DB_FOLDER.charAt(DB_FOLDER.length() - 1) != '/'){
                DB_FOLDER += "/";
            }

            Logger.Log("Using Database Folder: " + DB_FOLDER);
            Logger.Log("Using Database File Name : " + DB_NAME);
            Logger.Log("DB URL : " + DB_URL + DB_FOLDER + DB_NAME);

        } catch (ConfigurationKeyNotFound configurationKeyNotFound) {
            configurationKeyNotFound.printStackTrace();
            Logger.Log(Logger.Level.ERROR, "Failed to load configuration key!");
        }

        // init handles the heavy lifting.
        init();

        for(Runway r : get_runways()){
            Logger.Log(r.toString());
        }
    }

    private void init(){
        try {


            File db_folder = new File(DB_FOLDER);

            if(!db_folder.exists())
            {
                Logger.Log("Database folder doesn't exist, creating one.");
                db_folder.mkdir();
            }

            // db parameters
            String url = DB_URL + DB_FOLDER + DB_NAME;
            Logger.Log("Attempting to open database at " + url);

            // create a connection to the database
            conn = DriverManager.getConnection(url);
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            Boolean not_null = false;
            while (rs.next()) {
                not_null = true;
            }

            if(!not_null){
                Logger.Log(Logger.Level.WARNING, DB_NAME + " is empty, rebuilding from included scripts.");
                rebuild_db(conn);
            }
            refresh_ids();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // This method is important for maintaining an 'in memory' reference to the highest obstacle/runway id present in the databse.
    // This allows us to give each new runway/obstacle a unique id.
    public void refresh_ids(){
        try {
            // Refresh unique runway ID's
            ResultSet rs = execute_query("SELECT MAX(runway_id),MAX(physical_runway_id) FROM runway");
            runway_id = rs.getInt("MAX(runway_id)") + 1;
            physical_runway_id = rs.getInt("MAX(physical_runway_id)") + 1;
            Logger.Log("Updated runway ID to reflect database. [new_id=="+runway_id+"].");
            Logger.Log("Updated physical runway ID to reflect database. [new_id=="+physical_runway_id+"].");

            // Refresh Obstacle ID's
            rs = execute_query("SELECT MAX(obstacle_id) FROM obstacle");
            obstacle_id = rs.getInt("MAX(obstacle_id)") + 1;
            Logger.Log("Updated Obstacle ID to reflect database. [new_id=="+obstacle_id+"].");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean add_Runway(Runway runway, String airport_id){
        RunwayParameters R = runway.rightRunway.getOrigParams();
        RunwayParameters L = runway.leftRunway.getOrigParams();

        String useful = runway.toString().replace("Runway ", "");

        String query_right = "INSERT INTO runway VALUES (" +
                runway_id + ", \'" +
                airport_id + "\', " +
                physical_runway_id + ", \'" +
                useful.split("/")[0] + "\'," +
                R.getTORA() + ", " +
                R.getTODA() + ", " +
                R.getASDA() + ", " +
                R.getLDA() + ", \'None\'" +
                ");";

        Logger.Log(query_right);

        String query_left = "INSERT INTO runway VALUES (" +
                (runway_id + 1) + ", \'" +
                airport_id + "\', " +
                physical_runway_id + ", \'" +
                useful.split("/")[1] + "\'," +
                L.getTORA() + ", " +
                L.getTODA() + ", " +
                L.getASDA() + ", " +
                L.getLDA() + ",\' None\' " +
                ");";

        Logger.Log(query_left);
        execute_query(query_left);
        execute_query(query_right);

        runway_id += 2;
        physical_runway_id += 1;
        return true;
    }

    public Runway[] get_runways(){
        return get_runways("");
    }


    public Runway[] get_runways(String airport_id){
        ArrayList<Runway> return_array = new ArrayList<Runway>();
        HashMap<Integer, ArrayList<VirtualRunway>> runways = new HashMap<Integer, ArrayList<VirtualRunway>>();
        try {
            String query = airport_id=="" ? "SELECT * from runway" : "SELECT * from runway WHERE airport_id=\'"+airport_id+"\'";
            ResultSet rs = execute_query(query);
            while(rs.next()){
                String designator = rs.getString("runway_designator");
                int TORA = rs.getInt("tora");
                int TODA = rs.getInt("toda");
                int ASDA = rs.getInt("asda");
                int LDA = rs.getInt("lda");
                RunwayParameters rp = new RunwayParameters(TORA, TODA, ASDA, LDA);
                VirtualRunway vr = new VirtualRunway(designator, rp);
                int key = rs.getInt("physical_runway_id");
                if(!(runways.keySet().contains(key))) {
                    runways.put(key, new ArrayList<VirtualRunway>());
                }
                runways.get(key).add(vr);
            }

            for(int i : runways.keySet()){
                ArrayList<VirtualRunway> vrs = runways.get(i);
                if(vrs.size() > 2){
                    throw new MalformattedDataException("Found > 2 virtual runways with the same physical ID.");
                } else {
                    Logger.Log("Building new runway...");
                    Runway new_runway = new Runway(vrs.get(0), vrs.get(1));
                    return_array.add(new_runway);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (MalformattedDataException e) {
            e.printStackTrace();
        }

        return return_array.toArray(new Runway[return_array.size()]);
    }

    public void add_airport(Airport airport) {
        String airport_query = "INSERT INTO airport VALUES (\'" +
                airport.airport_id + "\', \'" +
                airport.airport_name + "\', " +
                0 + ");";
        execute_query(airport_query);
    }

    public Airport[] get_airports(){
        ArrayList<Airport> return_list = new ArrayList<Airport>();
        ResultSet rs = execute_query("SELECT * from airport");
        try {
            while(rs.next()){
                Airport new_airport = new Airport(rs.getString("airport_name"), rs.getString("airport_id"));
                return_list.add(new_airport);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return return_list.toArray(new Airport[return_list.size()]);
    }

    public void add_obstacle(Obstacle obstacle){
        String add_query = "INSERT INTO obstacle VALUES (" +
                obstacle_id + ", \'" +
                obstacle.getName() + "\', " +
                obstacle.getHeight() + ");";
        execute_query(add_query);
    }

    public Obstacle[] get_obstacles(){
        ArrayList<Obstacle> return_list = new ArrayList<Obstacle>();
        ResultSet rs = execute_query("SELECT * from obstacle");
        try {
            while(rs.next()){
                Obstacle new_obstacle = new Obstacle(rs.getString("obstacle_name"), rs.getInt("height"));
                return_list.add(new_obstacle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return return_list.toArray(new Obstacle[return_list.size()]);
    }

    private void rebuild_db(Connection connection){

        // This presumes that maven has already moved resources into /target, so may not work in IDE - depending on your config.
        //
        InputStream in = DB_controller.class.getClassLoader().getResourceAsStream("db_config.sql");
        try {
            DB_Import.importSQL(connection, in);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private ResultSet execute_query(String query){
        try {
            Statement stmt = conn.createStatement();

            // THIS MIGHT BREAK THINGS
            // note to self, if this doesn't work use stmt.execute instead.
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            Logger.Log("Failed to execute query: \n " + query + "\nPrining exception:: \n");
            e.printStackTrace();
            return null;
        }
    }
}
