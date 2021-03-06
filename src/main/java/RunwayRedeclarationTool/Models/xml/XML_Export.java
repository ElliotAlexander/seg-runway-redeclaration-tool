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


    public XML_Export(DB_controller db_controller, ObstaclePosition obstaclePosition) {
        this.db_controller = db_controller;
        this.obstaclePosition = obstaclePosition;
        Logger.Log("Starting XML Exporter...");
        export();
    }

    private void export() {
        try {

            // build the XML document.
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Build the root element
            Document document = docBuilder.newDocument();
            Element rootElement = document.createElement("class");
            document.appendChild(rootElement);

            Logger.Log("Building root element was successful. Displaying Airport/Obstacle Popups.");


            // Present the user windows to select what Airports / Obstacles to export.
            displayAirportPopupWindow(document, rootElement);
            displayObstaclePopupWindow(document, rootElement);


            // Present the user a window to choose where they save their XML file.
            File file = displayFileChooserWindow();
            Logger.Log("File Chooser window closed. Configuring transformer...");


            // Configure XML transformer.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Set encoding and Indentation depth.
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "YES");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            // User / console feedback.
            Logger.Log("Saving XML file [File=" + file.getAbsolutePath() + "].");
            PopupNotification.display("Success - XML exported", "Exported to file " + file.getName());
            return;

        } catch (ParserConfigurationException e) {
            Logger.Log("There was a problem configuring the parser! Exiting");
            PopupNotification.error("Parser Error!", "There was a problem configuring the parser - check the logfile for more details.");
            e.printStackTrace();
        } catch (TransformerException e) {
            Logger.Log("There was a problem configuring the transformer. Exiting.");
            PopupNotification.error("Transformer configuration error", "There was a transformer error when exporting to XML - check the logfile for more details.");
            e.printStackTrace();
        }
    }

    private void buildObstacleElement(Obstacle obstacle, Document document, Element rootElement) {

        // Create a root obstacle element
        Element obstacle_element = document.createElement("Obstacle");

        // Set Attribute, i.e. <Obstacle name="HelloWorld"></Obstacle>
        obstacle_element.setAttribute("obstacle_name", obstacle.getName());
        rootElement.appendChild(obstacle_element);

        Element height_element = document.createElement("height");
        height_element.appendChild(document.createTextNode(String.valueOf(obstacle.getHeight())));
        obstacle_element.appendChild(height_element);

        Logger.Log("Adding Obstacle Node [" + obstacle.toString() + "]");
    }

    private void buildAirportElement(Airport airport, Document document, Element rootElement) {

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

        for (Runway runway : db_controller.get_runways(airport.getAirport_id())) {
            Element runway_Element = document.createElement("Runway");
            for (VirtualRunway vr : new VirtualRunway[]{runway.leftRunway, runway.rightRunway}) {
                Element vr_node = document.createElement("vr");
                vr_node.setAttribute("designator", vr.getDesignator());


                Element TORA = document.createElement("TORA");
                TORA.appendChild(document.createTextNode(String.valueOf(vr.getOrigParams().getTORA())));
                vr_node.appendChild(TORA);

                Element TODA = document.createElement("TODA");
                TODA.appendChild(document.createTextNode(String.valueOf(vr.getOrigParams().getTODA())));
                vr_node.appendChild(TODA);

                Element ASDA = document.createElement("ASDA");
                ASDA.appendChild(document.createTextNode(String.valueOf(vr.getOrigParams().getASDA())));
                vr_node.appendChild(ASDA);

                Element LDA = document.createElement("LDA");
                LDA.appendChild(document.createTextNode(String.valueOf(vr.getOrigParams().getLDA())));
                vr_node.appendChild(LDA);


                runway_Element.appendChild(vr_node);
                Logger.Log("Adding Virtual Runway [" + vr.toString() + "] to XML.");

            }
            airport_element.appendChild(runway_Element);
        }
    }

    private void exportObstaclePosition(Document document, Element rootElement) {
        if (obstaclePosition == null) {
            Logger.Log(Logger.Level.WARNING, "Obstacle position is null, skipping.");
        }


        Element op_element = document.createElement("ObstaclePosition");
        rootElement.appendChild(op_element);

        Element width = document.createElement("width");
        width.appendChild(document.createTextNode(String.valueOf(obstaclePosition.getWidth())));
        op_element.appendChild(width);

        Element distanceFromCL = document.createElement("DistanceFromCL");
        distanceFromCL.appendChild(document.createTextNode(String.valueOf(obstaclePosition.getDistFromCL())));
        op_element.appendChild(distanceFromCL);

        Element distLeftTSH = document.createElement("DistanceLeftTSH");
        distLeftTSH.appendChild(document.createTextNode(String.valueOf(obstaclePosition.getDistLeftTSH())));
        op_element.appendChild(distLeftTSH);

        Element distRightTSH = document.createElement("DistanceRightTSH");
        distRightTSH.appendChild(document.createTextNode(String.valueOf(obstaclePosition.getDistRightTSH())));
        op_element.appendChild(distRightTSH);


        Element runwaySide = document.createElement("RunwaySide");
        runwaySide.appendChild(document.createTextNode(String.valueOf(obstaclePosition.getRunwaySide())));
        op_element.appendChild(runwaySide);

        Logger.Log("Writing obstacle position [" + obstaclePosition.toString() + "] to XML.");
        Logger.Log("Finished building obstacle position XML.");
    }

    private void displayObstaclePopupWindow(Document document, Element rootElement) {
        SelectObstaclePopup popup2 = new SelectObstaclePopup();
        Obstacle[] obstacles = popup2.display(db_controller);

        // Check if the user cancelled the export process from within the select window.
        // Else export all the obstacles they selected.
        if (obstacles != null) {
            for (Obstacle obstacle : obstacles) {
                buildObstacleElement(obstacle, document, rootElement);
            }
        } else {
            Logger.Log("Export process cancelled by the user inside ObstacleSelectWindow - exiting export process.");
            PopupNotification.error("Export process cancelled", "The XML exporting process has been cancelled.");
            return;
        }

        // Check if the export Obstacle Position checkboxw as checked inside the Obstacle Popup window.
        if (SelectObstaclePopup.export_obstacle_position == true) {

            // If obstacle position is not set - inform the user, and skip.
            if (obstaclePosition == null) {
                Logger.Log("Skipping export Obstacle Position Dialog - Obstacle Position is null.");
                PopupNotification.error("Obstacle Position not set!", "Skipping exporting obstacle position\n.No obstacle position has been set.");
            } else {

                // Else - export the obstacle position. The user recieves no feedback here (they'e already checked the box inside ObstaclePopup).
                Logger.Log("Exporting obstacle position to XML.");
                exportObstaclePosition(document, rootElement);
            }
        } else {
            Logger.Log("Not exporting obstacle position [" + SelectObstaclePopup.export_obstacle_position + "].");
        }

    }

    private void displayAirportPopupWindow(Document document, Element rootElement) {

        SelectAirportPopup popup = new SelectAirportPopup();
        Airport[] airports = popup.display(db_controller);
        if (airports != null) {
            for (Airport airport :  airports) {
                buildAirportElement(airport, document, rootElement);
            }
        } else {
            Logger.Log("Export process cancelled by the user inside AirportSelectWindow - exiting export process.");
            PopupNotification.error("Export process cancelled", "The XML exporting process has been cancelled.");
            return;
        }

    }

    private File displayFileChooserWindow(){
        // Configure file chooser for the user to define where they want to display the file.
        FileChooser fd = new FileChooser();
        fd.setInitialDirectory(new File(System.getProperty("user.home")));
        fd.setTitle("Select a save location");
        fd.setInitialFileName("exported_airports.xml");
        fd.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml")
        );

        File file = fd.showSaveDialog(null);


        if (file == null) {
            Logger.Log(Logger.Level.WARNING, "User closed file chooser window before selection. Exiting export procedure.");
            return null;
        } else {
            return file;
        }
    }


}
