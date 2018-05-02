package RunwayRedeclarationTool.Models.db;

import RunwayRedeclarationTool.Logger.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Import {


    /**
     *  The database setup is dependent on a file (In this case, InputStream in).
     *  The file contains a series of statemnets required to configure the database
     *  This class formats, builds, and executes these statements.
     * @param c
     * @param in
     * @throws SQLException
     */
    protected void importSQL(Connection c, InputStream in) throws SQLException
    {
        String s = new String();
        StringBuffer sb = new StringBuffer();

        try
        {
            // Read the file line by line.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            // Whole SQL statements are split by semi-colons, append into one long string:
            // i.e. <STATEMENT>;<STATEMENT>;<STATEMENT>
            while((s = br.readLine()) != null)
            {
                sb.append(s);
            }
            Logger.Log("Closing file");
            br.close();

            // Split the statements into their own strings, create statements.
            String[] inst = sb.toString().split(";");
            Statement st = c.createStatement();

            // For each statement from the DB_load file, execute the statement.
            for(int i = 0; i<inst.length; i++)
            {
                if(!inst[i].trim().equals(""))
                {
                    st.executeUpdate(inst[i]);
                    Logger.Log("Executing statement:");
                    Logger.Log(inst[i]);
                }
            }
        }
        catch(Exception e)
        {
            Logger.Log(Logger.Level.ERROR, "*** Error : " + e.toString());
            e.printStackTrace();
            Logger.Log(Logger.Level.ERROR, sb.toString());
        }

    }

}
