package RunwayRedeclarationTool.Models.xml;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.View.PopupNotification;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
public class XML_File_Loader {


    private final XML_Parser parser;

    public XML_File_Loader(DB_controller controller){
        // Setup
        // Note that controller is not stored outside the scope of the contructor.
        parser = new XML_Parser(controller);
    }


    public boolean load_file(){
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
            return false;
        } else {
            Logger.Log("Selected file: " + file.getName());
            if(!file.exists()){
                Logger.Log(Logger.Level.ERROR, "File doesn't exist!");
                return false;
            } else {
                Logger.Log("Parsing file...");
                parser.parse_xml(file);
                PopupNotification.display("Success - XML File imported", "Successfully imported XML file:    " + file.getName());
                return true;
            }
        }
    }

    public boolean load_directory(){
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select a folder to import");
        // Multi platform
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File dir = dc.showDialog(null);
        if(dir == null){
            return false;
        }
        Logger.Log("Loaded directory :"+ dir.getName() + " for parsing.");
        for(File f : dir.listFiles()){
            if(f.getName().endsWith(".xml")){
                parser.parse_xml(f);
            }
        }
        return true;
    }
}
