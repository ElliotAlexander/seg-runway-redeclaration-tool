package RunwayRedeclarationTool.Models.db;

import RunwayRedeclarationTool.Exceptions.MalformattedDataException;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.Runway;
import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.VirtualRunway;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DB_controller
{


    // TODO - Config file?
    private final String DB_NAME = "airports.db";
    private final String SCRIPTS_FOLDER = "scripts/";
    private final String DB_URL = "jdbc:sqlite:db/";


    // temporary?????
    private int runway_id = 0;
    private int physical_runway_id = 0;
    private Connection conn;

    // singleton
    public static final DB_controller instance = new DB_controller();


    private DB_controller(){
        init();
        Logger.Log("Running DB_Controller...");

        for(Runway r : get_runways()){
            Logger.Log(r.toString());
        }
    }


    private void init(){
        try {
            // db parameters
            String url = DB_URL + DB_NAME;
            // create a connection to the database
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        DatabaseMetaData md = null;
        try {
            md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            Boolean not_null = false;
            while (rs.next()) {
                not_null = true;
            }
            if(!not_null){
                Logger.Log(Logger.Level.WARNING, DB_NAME + " is empty, rebuilding from included scripts.");
                rebuild_db(conn);
            }
            refresh_runway_id();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refresh_runway_id(){

        // TODO move queries into scripts for my own sanity.
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(runway_id),MAX(physical_runway_id) FROM runway");
            runway_id = rs.getInt("MAX(runway_id)") + 1;
            physical_runway_id = rs.getInt("MAX(physical_runway_id)") + 1;
            Logger.Log("Updated runway ID to reflect database. [new_id=="+runway_id+"].");
            Logger.Log("Updated physical runway ID to reflect database. [new_id=="+physical_runway_id+"].");
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

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(query_right);
            stmt.execute(query_left);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        runway_id += 2;
        physical_runway_id += 1;

        return true;
    }

    public boolean add_airport(){

        return true;
    }

    public Runway[] get_runways(){
        ArrayList<Runway> return_array = new ArrayList<Runway>();
        HashMap<Integer, ArrayList<VirtualRunway>> runways = new HashMap<Integer, ArrayList<VirtualRunway>>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from runway");
            while(rs.next()){
                Logger.Log("Loaded new row");
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

    private void rebuild_db(Connection connection){
        try {
            File dir = new File(SCRIPTS_FOLDER);
            for(File f : dir.listFiles()){
                Logger.Log("Loading " + f.getName());
                DB_Import.importSQL(connection, f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
