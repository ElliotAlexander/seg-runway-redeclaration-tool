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

import static jdk.nashorn.internal.objects.NativeString.substring;

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

            // Build the databsae URL from the config file.
            DB_NAME = config.getConfigurationValue("DatabaseName");
            DB_FOLDER = config.getConfigurationValue("DatabaseFolder");
            DB_URL =config.getConfigurationValue("DatabaseURL");

            if(DB_FOLDER.charAt(DB_FOLDER.length() - 1) != '/'){
                DB_FOLDER += "/";
            }

            // Completed database URL
            Logger.Log("Using Database Folder: " + DB_FOLDER);
            Logger.Log("Using Database File Name : " + DB_NAME);
            Logger.Log("DB URL : " + DB_URL + DB_FOLDER + DB_NAME);

        } catch (ConfigurationKeyNotFound configurationKeyNotFound) {
            configurationKeyNotFound.printStackTrace();
            Logger.Log(Logger.Level.ERROR, "Failed to load configuration key!");
        }

        // init handles the heavy lifting.
        init();
    }

    private void init(){
        try {


            // open the database folder, create one if it doesn't exist.
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

            // check if the database exists -but is empty.
            ResultSet rs = md.getTables(null, null, "%", null);
            Boolean not_null = false;
            while (rs != null && rs.next()) {
                not_null = true;
            }

            // If the database is empty - rebuild the database.
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

    /**
     *  This method is important for maintaining an 'in memory' reference to the highest obstacle/runway id present in the databse.
        This allows us to give each new runway/obstacle a unique id.
     */
    public void refresh_ids(){
        try {
            // Refresh unique runway ID'
            ResultSet rs = execute(true, "SELECT MAX(runway_id),MAX(physical_runway_id) FROM runway;");
            runway_id = rs.getInt("MAX(runway_id)") + 1;
            physical_runway_id = rs.getInt("MAX(physical_runway_id)") + 1;
            Logger.Log("Updated runway ID to reflect database. [new_id=="+runway_id+"].");
            Logger.Log("Updated physical runway ID to reflect database. [new_id=="+physical_runway_id+"].");

            // Refresh Obstacle ID's
            rs = execute(true, "SELECT MAX(obstacle_id) FROM obstacle;");
            obstacle_id = rs.getInt("MAX(obstacle_id)") + 1;
            Logger.Log("Updated Obstacle ID to reflect database. [new_id=="+obstacle_id+"].");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Add a new runway to the database, attatched to an airport ID.
     * @param runway
     * @param airport_id
     * @return
     */
    public boolean add_Runway(Runway runway, String airport_id){
        RunwayParameters R = runway.rightRunway.getOrigParams();
        RunwayParameters L = runway.leftRunway.getOrigParams();
        // Remove the runway String, i.e. Runway 09L/27R -> 09L/27R
        String useful = runway.toString().replace("Runway ", "");


        // Build the left string.
        String query_left = "INSERT INTO runway VALUES (" +
                runway_id  + ", \'" +
                airport_id + "\', " +
                physical_runway_id + ", \'" +
                useful.split("/")[0] + "\'," +
                L.getTORA() + ", " +
                L.getTODA() + ", " +
                L.getASDA() + ", " +
                L.getLDA() + ",\' None\' " +
                ");";

        // Build the right string
        String query_right = "INSERT INTO runway VALUES (" +
                (runway_id + 1) + ", \'" +
                airport_id + "\', " +
                physical_runway_id + ", \'" +
                useful.split("/")[1] + "\'," +
                R.getTORA() + ", " +
                R.getTODA() + ", " +
                R.getASDA() + ", " +
                R.getLDA() + ", \'None\'" +
                ");";

        Logger.Log("Adding VR " + useful.split("/")[1] + " with Params [TODA=\'" + L.getTODA()+"\', TORA="+L.getTORA()+"\', ASDA=\'"+L.getASDA()+"\', LDA=\'"+L.getLDA()+"\'] to Database.");
        Logger.Log("Adding VR " + useful.split("/")[0] + " with Params [TODA=\'" + R.getTODA()+"\', TORA="+R.getTORA()+"\', ASDA=\'"+R.getASDA()+"\', LDA=\'"+R.getLDA()+"\'] to Database.");

        // Execute queries
        execute(false,query_left);
        execute(false, query_right);

        // Update physical IDs.
        runway_id += 2;
        physical_runway_id += 1;
        return true;
    }

    // Get all runways
    public Runway[] get_runways(){
        return get_runways("");
    }

    /**
     *  Return all runways matching a given Airport ID.
     * @param airport_id
     * @return
     */
    public Runway[] get_runways(String airport_id){
        ArrayList<Runway> return_array = new ArrayList<Runway>();
        // Map
        HashMap<Integer, ArrayList<VirtualRunway>> runways = new HashMap<Integer, ArrayList<VirtualRunway>>();
        try {

            // Get list of Virtual Runways
            String query = airport_id=="" ? "SELECT * from runway" : "SELECT * from runway WHERE airport_id=\'"+airport_id+"\'";
            ResultSet rs = execute(true, query);

            // Build Virtual Runway Objects
            while(rs != null && rs.next()){
                String designator = rs.getString("runway_designator");
                int TORA = rs.getInt("tora");
                int TODA = rs.getInt("toda");
                int ASDA = rs.getInt("asda");
                int LDA = rs.getInt("lda");
                RunwayParameters rp = new RunwayParameters(TORA, TODA, ASDA, LDA);
                VirtualRunway vr = new VirtualRunway(designator, rp);

                // Map each physical runway ID with a list of virtual runways.
                int key = rs.getInt("physical_runway_id");
                // Skip duplicates
                if(!(runways.keySet().contains(key))) {
                    runways.put(key, new ArrayList<VirtualRunway>());
                }
                runways.get(key).add(vr);
            }


            // Build Runway Object from two virtual runway objects.
            // For each physical runway
            for(int i : runways.keySet()){
                ArrayList<VirtualRunway> vrs = runways.get(i);
                if(vrs.size() !=  2){
                    throw new MalformattedDataException("Found > 2 virtual runways with the same physical ID.");
                } else {

                    //Build runway object.
                    int desg_int_1 = Integer.parseInt(vrs.get(0).getDesignator().replaceAll("[^0-9]", ""));
                    int desg_int_2 = Integer.parseInt(vrs.get(1).getDesignator().replaceAll("[^0-9]", ""));
                    Runway new_runway = desg_int_1 < desg_int_2 ? new Runway(vrs.get(0), vrs.get(1)) : new Runway(vrs.get(0), vrs.get(1));
                    return_array.add(new_runway);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (MalformattedDataException e) {
            e.printStackTrace();
        }

        // Build the return array.
        return return_array.toArray(new Runway[return_array.size()]);
    }


    /**
     * Add an airport to the databsae.
     * @param airport
     */
    public void add_airport(Airport airport) {
        // Build query
        String airport_query = "INSERT INTO airport VALUES (\'" +
                airport.airport_id + "\', \'" +
                airport.airport_name + "\', " +
                0 + ");";
        Logger.Log("Adding airport to Database with values [Name=\'" + airport.getAirport_name() + "\', ID=\'" + airport.getAirport_id() + "\'].");
        execute(false, airport_query);
    }

    /**
     * Remove a runway from the database.
     * @param runway
     */
    public void remove_Runway(Runway runway){
        String useful = runway.toString().replace("Runway ", "");

        // note that we need to remove both virtual runways - runways are stored in the databsae as two rows.
        // one for each virtual runway.
        String remove_query = "DELETE FROM runway WHERE runway_designator=\'" + useful.split("/")[0] + "\';";
        Logger.Log("Executing: " + remove_query);
        execute(false, remove_query);

         remove_query = "DELETE FROM runway WHERE runway_designator=\'" + useful.split("/")[1] + "\';";
        Logger.Log("Executing: " + remove_query);
        execute(false, remove_query);
    }

    /**
     * Remove an airprot from the database.
     * @param airport
     */
    public void remove_Airport(Airport airport){
        String remove_airport = "DELETE FROM airport WHERE airport_id=\'" + airport.getAirport_id() + "\';";
        Logger.Log("Removing all Runways with Airport ID " + airport.getAirport_id());
        Logger.Log("Removing airport " + airport.getAirport_name());
        execute(false, remove_airport);
    }

    /**
     * Return all airports in the database
     * @return airports[]
     */
    public Airport[] get_airports(){

        // Build a list of airports to return
        ArrayList<Airport> return_list = new ArrayList<Airport>();
        try {

            // Iterate through the results set. .
            ResultSet rs = execute(true,"SELECT * from airport;");
            while(rs != null && rs.next()){
                // Build the airport object -  add to array.
                Airport new_airport = new Airport(rs.getString("airport_name"), rs.getString("airport_id"));
                return_list.add(new_airport);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return our array.
        return return_list.toArray(new Airport[return_list.size()]);
    }


    /**
     * Add an obstacle object to the database.
     * @param obstacle
     */
    public void add_obstacle(Obstacle obstacle){
        // Build the query.
        String add_query = "INSERT INTO obstacle VALUES (" +
                obstacle_id + ", \'" +
                obstacle.getName() + "\', " +
                obstacle.getHeight() + ");";
        Logger.Log("Adding obstacle to DB [Name=\'"+obstacle.getName()+"\', Height=\'" + obstacle.getHeight() + "\'].");
        execute(false, add_query);
        obstacle_id += 1;
    }

    /**
     * Return all obstacles
     * @return
     */
    public Obstacle[] get_obstacles(){
        // Build a return list
        ArrayList<Obstacle> return_list = new ArrayList<Obstacle>();

        // Iterate through Results
        ResultSet rs = execute(true, "SELECT * from obstacle;");
        try {
            while(rs != null && rs.next()){
                // Build a new obstacle and add to our return list
                Obstacle new_obstacle = new Obstacle(rs.getString("obstacle_name"), rs.getInt("height"));
                return_list.add(new_obstacle);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        // Return out new list of obstacle objects
        return return_list.toArray(new Obstacle[return_list.size()]);
    }

    public void remove_obstacle(Obstacle o){
        String remove_obstacle = "DELETE FROM obstacle WHERE obstacle_name=\'" + o.getName() + "\';";
        execute(false, remove_obstacle);
        Logger.Log("Executing: " + remove_obstacle);
        Logger.Log("Removing obstacle [" + o.toString() + "].");

    }

    /**
     * This method handles all interaction with the database.
     * @param response - represents whether we want a response from our query or not
     * @param query - the SQL query to execute
     * @return
     */
    private ResultSet execute(boolean response, String query){
        Statement stmt;
        try {

            // If we want a response, return a results set
            if(response){
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                return rs;
                // Else return null
            } else {
                stmt = conn.createStatement();
                stmt.execute(query);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method rebuilds the database from the scripts included in db_config.sql.
     * This is only called if the database is null, empty, or cannot be opened.
     * @param connection
     */
    private void rebuild_db(Connection connection){

        // This presumes that maven has already moved resources into /target, so may not work in IDE - depending on your config.
        InputStream in = DB_controller.class.getClassLoader().getResourceAsStream("db_config.sql");

        // Rebuild the database from db_config.sql
        try {
            DB_Import db_import = new DB_Import();
            db_import.importSQL(connection, in);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
