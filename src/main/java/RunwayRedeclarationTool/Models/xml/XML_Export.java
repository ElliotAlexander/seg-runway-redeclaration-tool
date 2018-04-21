package RunwayRedeclarationTool.Models.xml;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.Airport;
import RunwayRedeclarationTool.Models.Runway;
import RunwayRedeclarationTool.Models.VirtualRunway;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.View.SelectAirportPopup;
import javafx.stage.FileChooser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XML_Export {
    
    
    private final DB_controller db_controller;
    
    
    public XML_Export(DB_controller db_controller){
        this.db_controller = db_controller;
        export();
    }

    private void export(){

        FileChooser fd = new FileChooser();
        fd.setInitialDirectory(new File(System.getProperty("user.home")));
        fd.setTitle("Select a save location");
        fd.setInitialFileName("exported_airports.xml");
        fd.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml")
        );

        File file = fd.showSaveDialog(null);

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document document = docBuilder.newDocument();
            Element rootElement = document.createElement("class");
            document.appendChild(rootElement);

            for(Airport airport : SelectAirportPopup.display(db_controller, "Select Airports to export")){
                buildAirportElement(airport, document, rootElement);
            }


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            Logger.Log("Saving XML file [File=" + file.getAbsolutePath() + "].");


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


    private Document buildAirportElement(Airport airport, Document document, Element rootElement){

        // Root airport element + name attribute
        Element airport_element = document.createElement("Airport");
        airport_element.setAttribute("airport_name", airport.getAirport_name());
        rootElement.appendChild(airport_element);


        // Airport id element

        Element airport_id = document.createElement("airport_id");
        airport_id.appendChild(document.createTextNode(airport.getAirport_id()));
        airport_element.appendChild(airport_id);

        // Runway and vr elements

            for (Runway runway : db_controller.get_runways(airport.getAirport_id())){
                Element runway_Element = document.createElement("Runway");
                for (VirtualRunway vr : new VirtualRunway[]{runway.leftRunway, runway.rightRunway}){
                    Element vr_node = document.createElement("vr");
                    vr_node.setAttribute("designator", vr.getDesignator());


                    Element TORA = document.createElement("tora");
                    TORA.appendChild(document.createTextNode(String.valueOf(vr.getOrigParams().getTORA())));
                    vr_node.appendChild(TORA);

                    Element TODA = document.createElement("toda");
                    TODA.appendChild(document.createTextNode(String.valueOf(vr.getOrigParams().getTODA())));
                    vr_node.appendChild(TODA);

                    Element ASDA = document.createElement("asda");
                    ASDA.appendChild(document.createTextNode(String.valueOf(vr.getOrigParams().getASDA())));
                    vr_node.appendChild(ASDA);

                    Element LDA = document.createElement("lda");
                    LDA.appendChild(document.createTextNode(String.valueOf(vr.getOrigParams().getLDA())));
                    vr_node.appendChild(LDA);


                    runway_Element.appendChild(vr_node);
                }

                airport_element.appendChild(runway_Element);
            }
        return document;
    }
    

}
