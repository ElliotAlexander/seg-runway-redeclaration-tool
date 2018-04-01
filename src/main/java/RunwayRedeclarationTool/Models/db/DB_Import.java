package RunwayRedeclarationTool.Models.db;

import RunwayRedeclarationTool.Logger.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Import {

    protected static void importSQL(Connection c, InputStream in) throws SQLException
    {
        String s = new String();
        StringBuffer sb = new StringBuffer();


        try
        {
            // Wrapping the inputstream in a buffered reader because im psychopathic
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

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
