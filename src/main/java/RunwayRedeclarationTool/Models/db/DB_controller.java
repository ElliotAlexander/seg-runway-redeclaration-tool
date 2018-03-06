package RunwayRedeclarationTool.Models.db;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.File;
import java.sql.*;

public class DB_controller
{


    private static final String DB_NAME = "airports.db";

    // Testing
    public static void main(String[] args){

    }

    // singleton
    public static final DB_controller instance = new DB_controller();


    private DB_controller(){
        init();
    }


    private void init(){
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:db/" + DB_NAME;
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

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
                System.out.println(DB_NAME + " is empty, rebuilding from included scripts.");
                rebuild_db(conn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void rebuild_db(Connection connection){
        try {
            File dir = new File("scripts/");
            for(File f : dir.listFiles()){
                DB_Import.importSQL(connection, f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
