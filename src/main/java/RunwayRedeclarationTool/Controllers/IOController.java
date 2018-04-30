package RunwayRedeclarationTool.Controllers;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.ImageExport;
import RunwayRedeclarationTool.Models.Obstacle;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.Models.xml.XML_File_Loader;
import RunwayRedeclarationTool.Models.xml.XML_Parser;
import RunwayRedeclarationTool.View.ExportToTextWindow;
import RunwayRedeclarationTool.View.RunwayView;
import RunwayRedeclarationTool.View.TopDownView;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.text.Text;

import java.io.File;

public class IOController {

    private final MainWindowController mwc;
    private final DB_controller db;

    public IOController(MainWindowController mwc, DB_controller db){
        this.mwc = mwc;
        this.db = db;
    }

    public void importXMLFile(){
        File f = new XML_File_Loader().load_file();
        if(f == null){
            return;
        } else {
            new XML_Parser(db, mwc).parse_xml(f);
        }
    }


    public void importXMLFolder(){
        XML_Parser parser = new XML_Parser(db, mwc);
        File dir = new XML_File_Loader().load_directory();
        if(dir.exists()){
            for(File f : dir.listFiles()){
                parser.parse_xml(f);
            }
        }
    }


    public void exportAsText(ObservableList<Node> declaredDistances, ObservableList<Node> calculationsBreakdown, Obstacle currentlySelected){
        String outputString = "";

        for (Node n : declaredDistances) {
            if (n instanceof Text) {
                outputString += ((Text) n).getText();
            }
        }

        for (Node n : calculationsBreakdown) {
            if (n instanceof Text) {
                outputString += ((Text) n).getText();
            }
        }

        outputString += "\n\nCurrent Obstacle: \n" + currentlySelected;
        outputString += "\n" + mwc.getObstaclePosition();
        ExportToTextWindow popup = new ExportToTextWindow();
        popup.display(outputString);
    }

    public void exportImage(RunwayView runwayView){
        if(runwayView instanceof TopDownView){
            Logger.Log("Running Image Exporter for top down view.");
        } else {
            Logger.Log("Running image exporter for side on view.");
        }
        new ImageExport().export(runwayView);
    }



}
