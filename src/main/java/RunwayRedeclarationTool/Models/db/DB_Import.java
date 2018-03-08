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


    protected static void setupDBHARDCODE(Connection conn){

        try {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS `airport` (\n" +
                    " `airport_id` VARCHAR(4) NOT NULL,\n" +
                    " `airport_name` VARCHAR(45) NULL,\n" +
                    " `no_runways` INT NULL,\n" +
                    " PRIMARY KEY (`airport_id`))\n");

            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS `runway` (\n" +
                    " `runway_id` INT NOT NULL,\n" +
                    " `airport_id` VARCHAR NULL,\n" +
                    " `physical_runway_id` INT NOT NULL,\n" +
                    " `runway_designator` VARCHAR(45) NULL,\n" +
                    " `tora` INT NULL,\n" +
                    " `toda` INT NULL,\n" +
                    " `asda` INT NULL,\n" +
                    " `lda` INT NULL,\n" +
                    " `remarks` VARCHAR(45) NULL,\n" +
                    " PRIMARY KEY (`runway_id`),\n" +
                    " CONSTRAINT `airport_id`\n" +
                    "   FOREIGN KEY (`runway_id`)\n" +
                    "   REFERENCES `airport` (`airport_id`)\n" +
                    "   ON DELETE NO ACTION\n" +
                    "   ON UPDATE NO ACTION)\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // TODO Fix this
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
                Logger.Log("Entering while loop");
                String line = s.next();
                //if (line.startsWith("/*!") && line.endsWith("*/"))
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
