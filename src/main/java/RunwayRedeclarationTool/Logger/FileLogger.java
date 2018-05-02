package RunwayRedeclarationTool.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FileLogger {


    private FileOutputStream os = null;

    // Protected constructor, instantiated by logger.
    protected FileLogger(String logdir, String logstr){
        try {
            // will have no effect if dir already exists
            File log_dir = new File(logdir);
            log_dir.mkdir();

            File f = new File(log_dir.getAbsolutePath() + "/" + logstr);

            // if a log file already exists -  we rename it ot the date it was created.
            if(f.exists()){

                // Rename the file based on the date it was created.
                BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                String dateCreated = dtf.format(LocalDateTime.ofInstant(attr.lastAccessTime().toInstant(), ZoneId.systemDefault()));
                Files.move(f.toPath(), f.toPath().resolveSibling("Logfile - " + dateCreated + ".txt"));
                Logger.Log("Log file already exists, renaming to \'Logfile - " + dateCreated + ".txt\'");
                f.createNewFile();
            } else {
                f.createNewFile();
            }
            os = new FileOutputStream(f);
            os.write("[WARNING] Logs may have been missed before this point. Run the Jar from the command line for full output prior to File Logger starting up.".getBytes());
        } catch (NoSuchFileException e){
            Logger.Log(Logger.Level.ERROR, "Failed to load logfile!");
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
