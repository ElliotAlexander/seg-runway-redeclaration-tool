package RunwayRedeclarationTool.Models.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DB_Import {

    protected static void importSQL(Connection conn, File f) throws SQLException
    {
        Statement st = null;
        try
        {
            InputStream targetStream = new FileInputStream(f);
            Scanner s = new Scanner(targetStream);
            s.useDelimiter("(;(\r)?\n)|(--\n)");
            st = conn.createStatement();
            while (s.hasNext());
            {
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
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error! Failed to open input stream for file.");
            e.printStackTrace();
        } finally
        {
            if (st != null) st.close();
        }
    }


}
