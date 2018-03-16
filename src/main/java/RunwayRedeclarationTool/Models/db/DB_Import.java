package RunwayRedeclarationTool.Models.db;

import RunwayRedeclarationTool.Logger.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Import {

    public static void importSQL(Connection c, File f) throws SQLException
    {
        String s = new String();
        StringBuffer sb = new StringBuffer();


        Logger.Log("Importing file " + f.getName() + " into database.");
        try
        {
            FileReader fr = new FileReader(f);
            // be sure to not have line starting with "--" or "/*" or any other non aplhabetical character

            BufferedReader br = new BufferedReader(fr);

            while((s = br.readLine()) != null)
            {
                sb.append(s);
            }
            Logger.Log("Closing file");
            br.close();
            String[] inst = sb.toString().split(";");
            Statement st = c.createStatement();

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
