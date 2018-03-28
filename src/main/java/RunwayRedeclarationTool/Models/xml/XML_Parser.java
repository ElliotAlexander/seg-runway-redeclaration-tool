package RunwayRedeclarationTool.Models.xml;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.Airport;
import RunwayRedeclarationTool.Models.Runway;
import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.VirtualRunway;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XML_Parser {

    public HashMap<Airport, List<Runway>> parse_xml(File xml_file){
        try {

            Logger.Log("Loaded file :"+ xml_file.getName() + " for parsing.");

            HashMap<Airport, List<Runway>> return_list = new HashMap<>();

            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(xml_file);
            Element rootElement = document.getRootElement();


            for(Element child : rootElement.getChildren("Airport")) {
                String airport_name = child.getAttribute("airport_name").getValue();
                String airport_id = child.getChild("airport_id").getValue();
                Airport new_airport = new Airport(airport_name, airport_id);
                Logger.Log("Building new airport from XML: airport_name=\'" + airport_name + "\', airport_id=\'" + airport_id + "\'.");
                List<Element> runways_xml = child.getChildren("Runway");
                List<Runway> runway_objects = new ArrayList<>();
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


                    runway_objects.add(new Runway(vr1, vr2));
                }
                return_list.put(new_airport, runway_objects);
            }


            return return_list;
        } catch (JDOMException e1) {
            e1.printStackTrace();
            // Return an empty hashmap.
            return new HashMap <Airport, List<Runway>>();
        } catch (IOException e1) {
            e1.printStackTrace();
            // Return an empty hashmap.
            return new HashMap <Airport, List<Runway>>();
        }
    }

}
