package RunwayRedeclarationTool.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileLogger {


    private FileOutputStream os = null;


    // Protected constructor, instantiated by logger.
    protected FileLogger(String logdir, String logstr){
        try {
            // will have no effect if dir already exists
            File log_dir = new File(logdir);
            log_dir.mkdir();

            File f = new File(log_dir.getAbsolutePath() + "/" + logstr);
            f.createNewFile();
            os = new FileOutputStream(f);
            os.write("[WARNING] Logs may have been missed before this point. Run the Jar from the command line for full output prior to File Logger starting up.".getBytes());
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
