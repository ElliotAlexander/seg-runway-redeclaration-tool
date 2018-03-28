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

    public XML_File_Loader(){
        // Setup
        Logger.Log("Loading XML parser. Opening File Chooser window...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }


    public HashMap<Airport, List<Runway>> load_file(){
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
        XML_Parser x = new XML_Parser();
        HashMap<Airport, List<Runway>> parsed = x.parse_xml(f);
        return parsed;
    }

    public HashMap<Airport, List<Runway>>[] load_directory(){
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setApproveButtonText("Open Folder");
        fc.setDialogTitle("Open a folder");

        ArrayList<HashMap<Airport, List<Runway>>> return_list = new ArrayList<>();

        fc.showOpenDialog(null);
        File dir = fc.getSelectedFile();
        Logger.Log("Loaded directory :"+ dir.getName() + " for parsing.");
        XML_Parser xml_parser = new XML_Parser();
        for(File f : dir.listFiles()){
            if(f.getName().endsWith(".xml")){
                return_list.add(xml_parser.parse_xml(f));
            }
        }
        return return_list.toArray(new HashMap[return_list.size()]);
    }
}
