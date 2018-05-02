package RunwayRedeclarationTool.Controllers;

import RunwayRedeclarationTool.Exceptions.ConfigurationKeyNotFound;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.ImageExport;
import RunwayRedeclarationTool.Models.Obstacle;
import RunwayRedeclarationTool.Models.config.Configuration;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.Models.xml.XML_File_Loader;
import RunwayRedeclarationTool.Models.xml.XML_Parser;
import RunwayRedeclarationTool.View.ExportToTextWindow;
import RunwayRedeclarationTool.View.PopupNotification;
import RunwayRedeclarationTool.View.RunwayView;
import RunwayRedeclarationTool.View.TopDownView;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * This class deals with importing/exporting xml files/folders, exporting images and text
 */
public class IOController {

    private final MainWindowController mwc;
    private final DB_controller db;

    public IOController(MainWindowController mwc, DB_controller db) {
        this.mwc = mwc;
        this.db = db;
    }

    public void importXMLFile() {
        File f = new XML_File_Loader().load_file();
        if (f == null) {
            return;
        } else {
            new XML_Parser(db, mwc).parse_xml(f);
        }
    }


    public void importXMLFolder() {
        XML_Parser parser = new XML_Parser(db, mwc);
        File dir = new XML_File_Loader().load_directory();
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                parser.parse_xml(f);
            }
        }
    }


    /**
     *  Formats and displays the calculation breakdown, as well as details on airports + obstacles in a popup window.
     * @param declaredDistances
     * @param calculationsBreakdown
     * @param currentlySelected
     */
    public void exportAsText(ObservableList<Node> declaredDistances, ObservableList<Node> calculationsBreakdown, Obstacle currentlySelected) {
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

    /**
     * Exports the currently selected runway view as an image.
     * Only the selected view iwll be exported.
     * @param runwayView
     */

    public void exportImage(RunwayView runwayView) {
        if (runwayView instanceof TopDownView) {
            Logger.Log("Running Image Exporter for top down view.");
        } else {
            Logger.Log("Running image exporter for side on view.");
        }
        new ImageExport().export(runwayView);
    }


    /**
     * Platform independent implementation to open the log file in the default text editor.
     * @param config The configuration file that specifies the location of the logfile
     */
    public void openLogFile(Configuration config) {
        String file = null, dir = null;
        try {
            dir = config.getConfigurationValue("LogDirectory");
            file = config.getConfigurationValue("LogFile");
        } catch (ConfigurationKeyNotFound configurationKeyNotFound) {
            // This would've been caught at init.
            configurationKeyNotFound.printStackTrace();
        }

        // Build the logfile string.
        File f = new File(dir + "/" + file);
        if (f.exists()) {
            try {
                // Java's Desktop API is cross platform.
                Desktop.getDesktop().edit(f);
            } catch (IOException e) {
                // For some reason, we can't open the file - print details and inform the user.
                Logger.Log("IOException when trying to open file [" + f.getName() + "].");
                PopupNotification.error("Failed to open log file", "IO Exception when attempting to open log file.");
                e.printStackTrace();
            }
        } else {
            Logger.Log("Failed to find log file!");
            PopupNotification.error("Failed to open log file!", "");
        }
    }


    /**
     * Cross platform implementation to open the directory where log files are stored.
     * Recall that all former log files are still saved, albeit renamed.
     * @param config The configuration file that specifies the location of the logfile
     */
    public void openLogDirectory(Configuration config){
        String dir = null;
        try {
            dir = config.getConfigurationValue("LogDirectory");
        } catch (ConfigurationKeyNotFound configurationKeyNotFound) {
            // This would've been caught at init.
            configurationKeyNotFound.printStackTrace();
        }

        File f = new File(dir);
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
