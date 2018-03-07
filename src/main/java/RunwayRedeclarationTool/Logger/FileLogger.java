package RunwayRedeclarationTool.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileLogger {

    public static final String LOG_FILE = "log.txt";

    private FileOutputStream os = null;


    // Protected constructor, instantiated by logger.
    protected FileLogger(){
        try {
            // will have no effect if dir already exists
            File log_dir = new File("logs/");
            log_dir.mkdir();

            File f = new File(log_dir.getAbsolutePath() + "/" + LOG_FILE);
            f.createNewFile();
            os = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            Logger.Log(Logger.Level.ERROR, "Cannot open log file for writing!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void Log(String s){
        try {
            os.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
