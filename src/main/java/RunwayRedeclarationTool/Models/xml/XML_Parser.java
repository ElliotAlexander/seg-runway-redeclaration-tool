package RunwayRedeclarationTool.Models.xml;

import RunwayRedeclarationTool.Controllers.MainWindowController;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.*;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.View.PopupNotification;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XML_Parser {

    protected final DB_controller controller;
    protected final MainWindowController mwc;

    public XML_Parser(DB_controller controller, MainWindowController mwc){
        this.controller = controller;
        this.mwc = mwc;
    }

    public void parse_xml(File xml_file){
        try {

            Logger.Log("Loaded file :"+ xml_file.getName() + " for parsing.");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(xml_file);
            Element rootElement = document.getRootElement();
            Logger.Log("Found " + rootElement.getChildren("Airport").size() + " Airport Elements.");
            parse_airports(rootElement.getChildren("Airport").toArray(new Element[rootElement.getChildren("Airport").size()]));
            Logger.Log("Found " + rootElement.getChildren("Obstacle").size() + " Obstacle Elements.");
            parse_obstacles(rootElement.getChildren("Obstacle").toArray(new Element[rootElement.getChildren("Obstacle").size()]));

            Logger.Log("Found " + rootElement.getChildren("ObstaclePosition").size() + " ObstaclePosition Elements. Only the first declared ObstaclePosition will be imported.");
            parse_obstacle_position(rootElement.getChildren("ObstaclePosition").toArray(new Element[rootElement.getChildren("ObstaclePosition").size()]));

        } catch (JDOMException e1) {
            Logger.Log("Catching JDOMException - printing stack trace, displaying popup notification to the user.");
            PopupNotification.error("JDOM Error when loading file!", "There was an error parsing XML from the file, is it formatted correctly?");
            e1.printStackTrace();
            // Return an empty hashmap.
        } catch (IOException e1) {
            Logger.Log("Catching IOException - printing stack trace, displaying popup notification to the user.");
            PopupNotification.error("IO Error when loading file!", "There was an IO error when loading the XML file.");
            e1.printStackTrace();
            // Return an empty hashmap.
        }
    }


    private void parse_airports(Element[] airport_nodes){
        ArrayList<Airport> duplicates = new ArrayList<>();

        int imported_airports = 0;

        nodeloop:
        for(Element child : airport_nodes) {

            // Load airport object parameters.
            String airport_name = child.getAttribute("airport_name").getValue();
            String airport_id = child.getChild("airport_id").getValue();


            // Check that the airport loaded doesn't already exist in the database.
            for(Airport x : controller.get_airports())
            {
                if(x.getAirport_id().equalsIgnoreCase(airport_id)){
                    Logger.Log("Ignoring Airport with parameters [Name=\'"+airport_name+"\', ID=\'"+airport_id+"\'], already exists in database.");
                    duplicates.add(x);
                    continue nodeloop;
                }
            }


            //Build airport object.
            Airport new_airport = new Airport(airport_name, airport_id);

            // Add airport object to db.
            Logger.Log("Building new airport from XML: airport_name=\'" + airport_name + "\', airport_id=\'" + airport_id + "\'.");
            controller.add_airport(new_airport);

            // Build runway objects
            // Note that runway objects are attached to airports via airport_id.
            List<Element> runways_xml = child.getChildren("Runway");
            for (Element runway : runways_xml) {
                Element[] vrs = runway.getChildren().toArray(new Element[runway.getChildren().size() - 1]);

                if (vrs.length < 2) {
                    Logger.Log(Logger.Level.ERROR, "Error in Runway Formatting under airport " + airport_name);
                }

                // Build both new virtual runway objects.
                String vr1_designator = vrs[0].getAttribute("designator").getValue();
                Integer TORA_1 = Integer.parseInt(vrs[0].getChild("TORA").getValue());
                Integer TODA_1 = Integer.parseInt(vrs[0].getChild("TODA").getValue());
                Integer ASDA_1 = Integer.parseInt(vrs[0].getChild("ASDA").getValue());
                Integer LDA_1 = Integer.parseInt(vrs[0].getChild("LDA").getValue());
                VirtualRunway vr1 = new VirtualRunway(vr1_designator, new RunwayParameters(TORA_1, TODA_1, ASDA_1, LDA_1));
                Logger.Log("Loaded Virtual Runway " + vr1_designator + " [ " + TORA_1 + ", " + TODA_1 + ", " + ASDA_1 + ", " + LDA_1 + "].");


                String vr2_designator = vrs[1].getAttribute("designator").getValue();
                Integer TORA_2 = Integer.parseInt(vrs[1].getChild("TORA").getValue());
                Integer TODA_2 = Integer.parseInt(vrs[1].getChild("TODA").getValue());
                Integer ASDA_2 = Integer.parseInt(vrs[1].getChild("ASDA").getValue());
                Integer LDA_2 = Integer.parseInt(vrs[1].getChild("LDA").getValue());
                VirtualRunway vr2 = new VirtualRunway(vr2_designator, new RunwayParameters(TORA_2, TODA_2, ASDA_2, LDA_2));
                Logger.Log("Loaded Virtual Runway " + vr2_designator + " [ " + TODA_2 + ", " + TORA_2 + ", " + ASDA_2 + ", " + LDA_2 + "] from XML.");

                int desg_int_1 = Integer.parseInt(vr1.getDesignator().replaceAll("[^0-9]", ""));
                int desg_int_2 = Integer.parseInt(vr2.getDesignator().replaceAll("[^0-9]", ""));
                Runway new_runway = desg_int_1 < desg_int_2 ? new Runway(vr1, vr2) : new Runway(vr2, vr1);

                Logger.Log("Placing " + new_runway.leftRunway.getDesignator() + " on the left hand side (Smaller designator).");
                controller.add_Runway(new_runway, airport_id);
            }
            imported_airports++;
        }

        PopupNotification.display("Imported Airports", "Successfully imported " + imported_airports + " airports into database.");

        if(duplicates.size() > 0){
            String list_str = "";
            for(Airport a : duplicates){
                list_str += "\t" + a.getAirport_name() + "/" + a.getAirport_id() + "\n ";
            }
            Alert alert = new Alert(Alert.AlertType.WARNING, "Skipping airports already imported into database. Duplicate Airports::\n "  + list_str, ButtonType.CLOSE);
            alert.setTitle("Duplicate Airport");
            alert.showAndWait();
        }


    }


    private void parse_obstacles(Element[] obstacle_nodes){

        int imported_obstacles = 0;

        ArrayList<Obstacle> duplicates = new ArrayList<>();
        nodeloop:
        for(Element child : obstacle_nodes) {
            // Build airport object
            String obstacle_name = child.getAttribute("obstacle_name").getValue();
            for(Obstacle o : controller.get_obstacles()){
                if(o.getName().equalsIgnoreCase(obstacle_name)){
                    Logger.Log("Ignoring obstacle with parameters [Name=\'"+obstacle_name+"\'], already exists in database.");
                    duplicates.add(o);
                    continue nodeloop;
                }
            }
            int height = Integer.parseInt(child.getChild("height").getValue());
            Obstacle obstacle = new Obstacle(obstacle_name, height);
            Logger.Log("Loaded obstacle from XML with parameters [Name='"+obstacle_name+"\', Height=" + height + "].");
            controller.add_obstacle(obstacle);
            imported_obstacles++;
        }

        PopupNotification.display("Successfully imported Obstacles", "Successfully imported " + imported_obstacles + " obstacles into the database.");

        if(duplicates.size() > 0){
            String list_str = "";
            for(Obstacle o : duplicates){
                list_str += "\t" + o.getName() + "\n ";
            }
            Alert alert = new Alert(Alert.AlertType.WARNING, "Skipping obstacles already imported into database. Duplicate Obstacles:\n "  + list_str, ButtonType.CLOSE);
            alert.setTitle("Duplicate obstacle");
            alert.showAndWait();
        }

    }

    private void parse_obstacle_position(Element[] obstacle_positions){
        if(obstacle_positions.length > 1){
            Logger.Log(Logger.Level.ERROR, "Found more than one obstacle position when importing.\nOnly the first obstacle position within the file will be imported.");
        }

        if(obstacle_positions.length == 0){
            Logger.Log("Found no obstacle positions within file Skipping.");
            return;
        }

        try {
            Element import_position = obstacle_positions[0];
            int width = Integer.parseInt(import_position.getChild("width").getValue());
            int DistanceFromCL = Integer.parseInt(import_position.getChild("DistanceFromCL").getValue());
            int DistanceLeftTSH = Integer.parseInt(import_position.getChild("DistanceLeftTSH").getValue());
            int DistanceRightTSH = Integer.parseInt(import_position.getChild("DistanceRightTSH").getValue());

            Logger.Log("Loaded ObstaclePosition Values [width=" + width + ", dCL=" + DistanceFromCL + ", dLTSH=" + DistanceLeftTSH + ", dRTSH=" + DistanceRightTSH + "].");

            String side = import_position.getChild("RunwaySide").getValue();
            RunwaySide side_val;
            if (side.equalsIgnoreCase("RIGHT")){

                side_val = RunwaySide.RIGHT;
            } else if (side.equalsIgnoreCase("LEFT")){
                side_val = RunwaySide.LEFT;
            } else {
                side_val = RunwaySide.CENTER;
            }


            Logger.Log("Side Val: " + side_val.toString());

            ObstaclePosition newOP = new ObstaclePosition(mwc.getObstacle(), DistanceLeftTSH, DistanceRightTSH, width, DistanceFromCL, side_val);
            mwc.setObstaclePosition(newOP);
            Logger.Log("Successfully imported obstacle position [" + newOP.toString() + "] into database.");
            PopupNotification.display("Successfully imported Obstacle Position", "Obstacle Position successfully imported.");
        } catch(NumberFormatException e){
            Logger.Log(Logger.Level.ERROR, "Failed to parse values in ObstaclePosition XML data.");
            Logger.Log(Logger.Level.ERROR, "Failed at: [" + obstacle_positions[0].coalesceText(true) + "].");
        }


    }

}
