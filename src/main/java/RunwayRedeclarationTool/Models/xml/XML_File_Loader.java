package RunwayRedeclarationTool.Models.xml;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.View.PopupNotification;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
public class XML_File_Loader {




    public File load_file(){
        Logger.Log("Opening File Chooser Window.");

        // Configure file chooser window.
        FileChooser fd = new FileChooser();
        fd.setInitialDirectory(new File(System.getProperty("user.home")));
        fd.setTitle("Select XML file to import:");
        fd.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml")
        );


        File file = fd.showOpenDialog(null);

        if (file == null) {
            Logger.Log("File [" + file.getName() + "] doesn't exist. Cancelled import process.");
            PopupNotification.error("XML import cancelled", "The XML import process has been cancelled.");
            return null;
        } else {
            Logger.Log("Selected file: " + file.getName());
            if(!file.exists()){
                Logger.Log(Logger.Level.ERROR, "File doesn't exist!");
                PopupNotification.error("File doesn't exist!", "The selected file doesn't exist! Try again.");
                return null;
            } else {
               return file;
            }
        }
    }

    public File load_directory(){
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select a folder to import");
        // Multi platform
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File dir = dc.showDialog(null);

        // Catch possible filesystem errors.
        if(dir == null){
            PopupNotification.error("Failed to load directory!", "XML import failed.");
            return null;
        }

        Logger.Log("Loaded directory :"+ dir.getName() + " for parsing.");
        return dir;
    }
}
