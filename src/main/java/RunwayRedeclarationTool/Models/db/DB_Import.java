package RunwayRedeclarationTool.Models.db;

import RunwayRedeclarationTool.Logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DB_Import {


    // TODO Fix this
    protected static void importSQL(Connection conn, File f) throws SQLException
    {
        Statement st = null;
        try
        {
            InputStream targetStream = new FileInputStream(f);
            Scanner s = new Scanner(targetStream);
            s.useDelimiter("(;(\r)?\n)|(--\n)");
            Logger.Log("Setup input stream.");

            st = conn.createStatement();
            Logger.Log("Creating statement");

            boolean test = true;
            Logger.Log("Test is true?!");
            while (test);
            {
                Logger.Log("Hello World");
                if(s.hasNext() == true){
                    String line = s.next();
                    if (line.startsWith("/*!") && line.endsWith("*/"))
                    {
                        int i = line.indexOf(' ');
                        line = line.substring(i + 1, line.length() - " */".length());
                    }

                    if (line.trim().length() > 0)
                    {
                        st.execute(line);
                    }
                } else {
                   test = false;
                }
            }
            Logger.Log("Exiting while loop");
        } catch (FileNotFoundException e) {
            System.out.println("Error! Failed to open input stream for file.");
            e.printStackTrace();
        } finally
        {
            if (st != null) st.close();
        }
    }
}
