package RunwayRedeclarationTool.Models.xml;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.*;
import RunwayRedeclarationTool.Models.db.DB_controller;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XML_Parser {

    public final DB_controller controller;

    public XML_Parser(DB_controller controller){
        this.controller = controller;
    }

    public void parse_xml(File xml_file){
        try {

            Logger.Log("Loaded file :"+ xml_file.getName() + " for parsing.");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(xml_file);
            Element rootElement = document.getRootElement();
            parse_airports(rootElement.getChildren("Airport").toArray(new Element[rootElement.getChildren("Airport").size()]));
            parse_obstacles(rootElement.getChildren("Obstacle").toArray(new Element[rootElement.getChildren("Obstacle").size()]));
        } catch (JDOMException e1) {
            e1.printStackTrace();
            // Return an empty hashmap.
        } catch (IOException e1) {
            e1.printStackTrace();
            // Return an empty hashmap.
        }
    }


    private void parse_airports(Element[] airport_nodes){
        nodeloop:
        for(Element child : airport_nodes) {

            // Load airport object parameters.
            String airport_name = child.getAttribute("airport_name").getValue();
            String airport_id = child.getChild("airport_id").getValue();


            // Check that the airport loaded doesn't already exist in the database.
            for(Airport x : controller.get_airports())
            {
                if(x.getAirport_id().equalsIgnoreCase(airport_id)){
                    Logger.Log(Logger.Level.ERROR, "Airport " + airport_id + "/" + airport_name + " already exists in Database! Skipping.");
                    JOptionPane.showMessageDialog(null, "An airport with identifier "  + airport_id + "/" + airport_name + " already exists in the Database! Skipping.");
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
                Integer TORA_1 = Integer.parseInt(vrs[0].getChild("tora").getValue());
                Integer TODA_1 = Integer.parseInt(vrs[0].getChild("toda").getValue());
                Integer ASDA_1 = Integer.parseInt(vrs[0].getChild("asda").getValue());
                Integer LDA_1 = Integer.parseInt(vrs[0].getChild("lda").getValue());
                VirtualRunway vr1 = new VirtualRunway(vr1_designator, new RunwayParameters(TODA_1, TORA_1, ASDA_1, LDA_1));
                Logger.Log("Loaded Virtual Runway " + vr1_designator + " [ " + TODA_1 + ", " + TORA_1 + ", " + ASDA_1 + ", " + LDA_1 + "].");


                String vr2_designator = vrs[1].getAttribute("designator").getValue();
                Integer TORA_2 = Integer.parseInt(vrs[1].getChild("tora").getValue());
                Integer TODA_2 = Integer.parseInt(vrs[1].getChild("toda").getValue());
                Integer ASDA_2 = Integer.parseInt(vrs[1].getChild("asda").getValue());
                Integer LDA_2 = Integer.parseInt(vrs[1].getChild("lda").getValue());
                VirtualRunway vr2 = new VirtualRunway(vr2_designator, new RunwayParameters(TODA_2, TORA_2, ASDA_2, LDA_2));
                Logger.Log("Loaded Virtual Runway " + vr2_designator + " [ " + TODA_2 + ", " + TORA_2 + ", " + ASDA_2 + ", " + LDA_2 + "].");
                controller.add_Runway(new Runway(vr1, vr2), airport_id);
            }
        }
    }


    private void parse_obstacles(Element[] obstacle_nodes){
        for(Element child : obstacle_nodes) {
            // Build airport object
            String obstacle_name = child.getAttribute("obstacle_name").getValue();
            List<Element> obstacle_xml = child.getChildren("Obstacle");
            for (Element obstacle_element : obstacle_xml) {

                int Height = Integer.parseInt(obstacle_element.getChild("Height").getValue());
                Obstacle obstacle = new Obstacle(obstacle_name, Height);
                controller.add_obstacle(obstacle);
            }
        }
    }

}
