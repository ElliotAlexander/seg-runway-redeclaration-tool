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
        FileChooser fd = new FileChooser();
        fd.setInitialDirectory(new File(System.getProperty("user.home")));
        fd.setTitle("Select XML file to import:");
        fd.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml")
        );

        File file = fd.showOpenDialog(null);

        if (file == null) {
            System.out.println("You cancelled the choice");
            return null;
        } else {
            Logger.Log("Selected file: " + file.getName());
            if(!file.exists()){
                Logger.Log(Logger.Level.ERROR, "File doesn't exist!");
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
        if(dir == null){
            PopupNotification.error("Failed to load directory!", "XML import failed.");
            return null;
        }
        Logger.Log("Loaded directory :"+ dir.getName() + " for parsing.");
        return dir;
    }
}
