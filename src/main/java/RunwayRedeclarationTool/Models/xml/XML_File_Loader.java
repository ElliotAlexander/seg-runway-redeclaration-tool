package RunwayRedeclarationTool.Models.xml;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.Airport;
import RunwayRedeclarationTool.Models.Runway;
import RunwayRedeclarationTool.Models.db.DB_controller;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XML_File_Loader {


    private final XML_Parser parser;

    public XML_File_Loader(DB_controller controller){
        // Setup
        // Note that controller is not stored outside the scope of the contructor.
        parser = new XML_Parser(controller);

        Logger.Log("Loading XML parser. Opening File Chooser window...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }


    public void load_file(){
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Open a file");
        fc.setFileFilter(new FileFilter() {

            public String getDescription() {
                return "XML Document (*.xml)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(".xml");
                }
            }
        });

        fc.showOpenDialog(null);
        File f = fc.getSelectedFile();
        parser.parse_xml(f);
    }

    public void load_directory(){
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setApproveButtonText("Open Folder");
        fc.setDialogTitle("Open a folder");

        fc.showOpenDialog(null);
        File dir = fc.getSelectedFile();
        Logger.Log("Loaded directory :"+ dir.getName() + " for parsing.");
        for(File f : dir.listFiles()){
            if(f.getName().endsWith(".xml")){
                parser.parse_xml(f);
            }
        }
    }
}
