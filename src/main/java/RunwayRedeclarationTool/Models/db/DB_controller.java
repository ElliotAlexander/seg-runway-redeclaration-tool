package RunwayRedeclarationTool.Models.db;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.Runway;

import java.io.File;
import java.sql.*;

public class DB_controller
{


    // TODO - Config file?
    private final String DB_NAME = "airports.db";
    private final String SCRIPTS_FOLDER = "scripts/";
    private final String DB_URL = "jdbc:sqlite:db/";

    private Connection conn;

    // singleton
    public static final DB_controller instance = new DB_controller();


    private DB_controller(){
        init();
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



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean add_Runway(){
        return true;
    }


    public Runway[] get_Runways(){
        return null;
    }




    private void rebuild_db(Connection connection){
        try {
            File dir = new File(SCRIPTS_FOLDER);
            for(File f : dir.listFiles()){
                DB_Import.importSQL(connection, f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
