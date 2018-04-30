package RunwayRedeclarationTool.Models.xml;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.*;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.View.PopupNotification;
import RunwayRedeclarationTool.View.SelectAirportPopup;
import RunwayRedeclarationTool.View.SelectObstaclePopup;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tray.animations.AnimationType;
import tray.notification.TrayNotification;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class XML_Export {
    
    
    private final DB_controller db_controller;
    private final ObstaclePosition obstaclePosition;

    public static boolean force_close_event = false;
    
    public XML_Export(DB_controller db_controller, ObstaclePosition obstaclePosition){
        this.db_controller = db_controller;
        this.obstaclePosition = obstaclePosition;
        export();
    }

    private void export(){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document document = docBuilder.newDocument();
            Element rootElement = document.createElement("class");
            document.appendChild(rootElement);


            // Single threaded
            force_close_event  = false;

            SelectAirportPopup popup = new SelectAirportPopup();
            for (Airport airport : popup.display(db_controller)) {
                    buildAirportElement(airport, document, rootElement);
                }

                if(force_close_event) {
                    force_close_event = false;
                    return;
                }

            SelectObstaclePopup popup2 = new SelectObstaclePopup();
            Obstacle[] obstacles = popup2.display(db_controller);


                if(force_close_event){
                    force_close_event = false;
                    return;
                }


                if(SelectObstaclePopup.export_obstacle_position == true) {
                        if (obstaclePosition == null) {
                            Logger.Log("Skipping export Obstacle Position Dialog - Obstacle Position is null.");
                            Alert no_obstacle_set = new Alert(Alert.AlertType.ERROR, "Failed to export obstacle position. \nNo obstacle position is set!", ButtonType.CLOSE);
                            no_obstacle_set.showAndWait();
                        } else {
                                Logger.Log("Exporting obstacle position to XML.");
                                exportObstaclePosition(document, rootElement);
                        }
                    } else {
                        Logger.Log("Not exporting obstacle position [" + SelectObstaclePopup.export_obstacle_position + "].");
                    }


                for(Obstacle obstacle : obstacles){
                    buildObstacleElement(obstacle, document, rootElement);
                }

                FileChooser fd = new FileChooser();
                fd.setInitialDirectory(new File(System.getProperty("user.home")));
                fd.setTitle("Select a save location");
                fd.setInitialFileName("exported_airports.xml");
                fd.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XML", "*.xml")
                );

                File file = fd.showSaveDialog(null);


                if(file == null){
                    Logger.Log(Logger.Level.WARNING, "User closed file chooser window before selection. Exiting export procedure.");
                    return;
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "YES");
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);
                Logger.Log("Saving XML file [File=" + file.getAbsolutePath() + "].");
                PopupNotification.display("Success", "XML file exported to file " + file.getName());

                return;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void buildObstacleElement(Obstacle obstacle, Document document, Element rootElement){
        Element obstacle_element = document.createElement("Obstacle");
        obstacle_element.setAttribute("obstacle_name", obstacle.getName());
        rootElement.appendChild(obstacle_element);

        Element height_element = document.createElement("height");
        height_element.appendChild(document.createTextNode(String.valueOf(obstacle.getHeight())));
        obstacle_element.appendChild(height_element);

        Logger.Log("Adding Obstacle Node [" + obstacle.toString() + "]");
    }


    private void buildAirportElement(Airport airport, Document document, Element rootElement){

        // Root airport element + name attribute
        Element airport_element = document.createElement("Airport");
        airport_element.setAttribute("airport_name", airport.getAirport_name());
        rootElement.appendChild(airport_element);


        // Airport id element

        Element airport_id = document.createElement("airport_id");
        airport_id.appendChild(document.createTextNode(airport.getAirport_id()));
        airport_element.appendChild(airport_id);
        Logger.Log("Adding Airport [" + airport.toString() + "] to XML.");

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
                    Logger.Log("Adding Virtual Runway [" + vr.toString() + "] to XML.");

                }
                airport_element.appendChild(runway_Element);
            }
    }


    private void exportObstaclePosition(Document document, Element rootElement){
        if(obstaclePosition == null){
            Logger.Log(Logger.Level.WARNING, "Obstacle position is null, skipping.");
        }


        Element op_element = document.createElement("ObstaclePosition");
        rootElement.appendChild(op_element);

        Element width  = document.createElement("width");
        width.appendChild(document.createTextNode(String.valueOf(obstaclePosition.getWidth())));
        op_element.appendChild(width);

        Element distanceFromCL  = document.createElement("DistanceFromCL");
        distanceFromCL.appendChild(document.createTextNode(String.valueOf(obstaclePosition.getDistFromCL())));
        op_element.appendChild(distanceFromCL);

        Element  distLeftTSH = document.createElement("DistanceLeftTSH");
        distLeftTSH.appendChild(document.createTextNode(String.valueOf(obstaclePosition.getDistLeftTSH())));
        op_element.appendChild(distLeftTSH);

        Element  distRightTSH = document.createElement("DistanceRightTSH");
        distRightTSH.appendChild(document.createTextNode(String.valueOf(obstaclePosition.getDistRightTSH())));
        op_element.appendChild(distRightTSH);


        Element  runwaySide = document.createElement("RunwaySide");
        runwaySide.appendChild(document.createTextNode(String.valueOf(obstaclePosition.getRunwaySide())));
        op_element.appendChild(runwaySide);

        Logger.Log("Writing obstacle position [" + obstaclePosition.toString() + "] to XML.");
        Logger.Log("Finished building obstacle position XML.");
    }
    

}
