package RunwayRedeclarationTool.Controllers;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Exceptions.NoRedeclarationNeededException;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.*;
import RunwayRedeclarationTool.Models.config.Configuration;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.Models.xml.XML_File_Loader;
import RunwayRedeclarationTool.View.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private FlowPane topDownViewContainer, sideOnViewContainer;
    @FXML
    private ComboBox<Airport> airportComboBox;
    @FXML
    private ComboBox<Runway> runwayComboBox;
    @FXML
    private ComboBox<VirtualRunway> virtualRunwayComboBox;
    @FXML
    private ComboBox<Obstacle> obstructionComboBox;
    @FXML
    private ComboBox<RunwaySide> runwaySideComboBox;
    @FXML
    private TextFlow declaredDistances, calculationsBreakdown;
    @FXML
    private Button calculateButton;
    @FXML
    private TextField distanceFromTHRLeft, distanceFromTHRRight, distanceFromCL;

    private final DB_controller controller;
    private final Configuration config;

    private TopDownView topDownView;
    private SideOnView sideOnView;

    public MainWindowController(Configuration config, DB_controller controller) {
        this.config = config;
        this.controller = controller;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        runwaySideComboBox.getItems().addAll(RunwaySide.LEFT, RunwaySide.RIGHT);

        if (controller.get_airports().length > 0) {
            Logger.Log("Loaded airports: c" + controller.get_airports().toString());
            refresh_combobox();
            drawRunway();
        }
    }


    //  --- FIXED - See below
    // TODO - Theres a bug with the FXML combobox onaction calls - When selecting an airport, onaction for
    // the runway combo box is also called, throwing null pointers and hanging the program
    // Not quite sure how to fix this
    public void drawRunway() {
        topDownViewContainer.getChildren().clear();
        sideOnViewContainer.getChildren().clear();
        declaredDistances.getChildren().clear();

        Runway runway = runwayComboBox.getValue();
        VirtualRunway virtualRunway = virtualRunwayComboBox.getValue();

        try {
            if (runway == null) {
                runway = runwayComboBox.getItems().get(0);
                // Default to the left virtual runway
                virtualRunway = runway.leftRunway;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // There is no runway yet
            return;
        }

        declaredDistances.getChildren().clear();
        declaredDistances.getChildren().add(new Text("Runway " + runway.leftRunway.getDesignator() + ":\nTORA: " + runway.leftRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.leftRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.leftRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.leftRunway.getOrigParams().getLDA() + "m\n\n"));
        declaredDistances.getChildren().add(new Text("Runway " + runway.rightRunway.getDesignator() + ":\nTORA: " + runway.rightRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.rightRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.rightRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.rightRunway.getOrigParams().getLDA() + "m\n"));

        topDownView = new TopDownView(virtualRunway);
        topDownView.widthProperty().bind(topDownViewContainer.widthProperty());
        topDownView.heightProperty().bind(topDownViewContainer.heightProperty());
        topDownViewContainer.getChildren().add(topDownView);

        sideOnView = new SideOnView(virtualRunway);
        sideOnView.widthProperty().bind(sideOnViewContainer.widthProperty());
        sideOnView.heightProperty().bind(sideOnViewContainer.heightProperty());
        sideOnViewContainer.getChildren().add(sideOnView);
    }

    @FXML
    public void updateRunways() {
        Airport airport = airportComboBox.getValue();
        if (airport == null || controller.get_runways(airport.getAirport_id()).length==0) {
            return;
        }
        Logger.Log("Switching to airport: " + airport.getAirport_id());
        ArrayList<Runway> runways = new ArrayList<>();
        Collections.addAll(runways, controller.get_runways(airport.getAirport_id()));
        ObservableList<Runway> observableList = FXCollections.observableList(runways);
        runwayComboBox.setItems(observableList);
    }

    @FXML
    public void updateVirtualRunways() {
        Runway runway = runwayComboBox.getValue();
        if(runway == null) { return; }
        Logger.Log("Switching to runway: " + runway.toString());
        ArrayList<VirtualRunway> virtualRunways = new ArrayList<>();
        Collections.addAll(virtualRunways, runway.leftRunway, runway.rightRunway);
        ObservableList<VirtualRunway> observableList = FXCollections.observableList(virtualRunways);
        virtualRunwayComboBox.setItems(observableList);
    }

    @FXML
    public void recalculateDistances() {

        Logger.Log("Attempting to recalculate distances.");
        Calculator calculator = Calculator.getInstance();
        try {

            // TODO - When calculate is clicked with empty values, nothing is shown to the user. Catch NumberFormatExcpetion and throw a popup
            // Can copy code from NewRunwayWindow probs.
            Runway runway = runwayComboBox.getValue();
            Obstacle obstacle = obstructionComboBox.getValue();
            ObstaclePosition obstaclePosition = new ObstaclePosition(obstacle, Integer.parseInt(distanceFromTHRLeft.getText()), Integer.parseInt(distanceFromTHRRight.getText()), Integer.parseInt(distanceFromCL.getText()), runwaySideComboBox.getValue());

            declaredDistances.getChildren().clear();
            //declaredDistances.getChildren().add(new Text(obstaclePosition.toString()));
            declaredDistances.getChildren().add(new Text("Original distances:\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.leftRunway.getDesignator() + ":\nTORA: " + runway.leftRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.leftRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.leftRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.leftRunway.getOrigParams().getLDA() + "m\n\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.rightRunway.getDesignator() + ":\nTORA: " + runway.rightRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.rightRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.rightRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.rightRunway.getOrigParams().getLDA() + "m\n\n"));

            calculator.calculate(obstaclePosition, runway);

            declaredDistances.getChildren().add(new Text("Recalculated distances:\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.leftRunway.getDesignator() + ":\nTORA: " + runway.leftRunway.getRecalcParams().getTORA() + "m\nTODA: " + runway.leftRunway.getRecalcParams().getTODA() + "m\nASDA: " + runway.leftRunway.getRecalcParams().getASDA() + "m\nLDA:  " + runway.leftRunway.getRecalcParams().getLDA() + "m\n\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.rightRunway.getDesignator() + ":\nTORA: " + runway.rightRunway.getRecalcParams().getTORA() + "m\nTODA: " + runway.rightRunway.getRecalcParams().getTODA() + "m\nASDA: " + runway.rightRunway.getRecalcParams().getASDA() + "m\nLDA:  " + runway.rightRunway.getRecalcParams().getLDA() + "m\n"));

            calculationsBreakdown.getChildren().clear();
            calculationsBreakdown.getChildren().add(new Text(runway.leftRunway.getRecalcBreakdown() + "\n\n"));
            calculationsBreakdown.getChildren().add(new Text(runway.rightRunway.getRecalcBreakdown()));

            topDownView.drawObstacle(obstaclePosition);

        } catch (NoRedeclarationNeededException e) {
            Logger.Log(Logger.Level.ERROR, e.getStackTrace().toString());
            declaredDistances.getChildren().add(new Text("\n\n" + e.getMessage()));
        } catch (AttributeNotAssignedException e) {
            //TODO
        }
    }

    @FXML
    public void handleNewAirport() {
        try {
            Airport newAirport = NewAirportPopup.display();
            // This stops select an airport being added to the combobox.
            if (newAirport == null) {
                return;
            }
            airportComboBox.getItems().add(newAirport);
            airportComboBox.setValue(newAirport);
            controller.add_airport(newAirport);
        } catch (NullPointerException e) {
        }
    }

    @FXML
    public void handleNewRunway() {
        try {
            Airport currentAirport = airportComboBox.getValue();
            if (currentAirport == null) {
                JOptionPane.showMessageDialog(null, "You need to add or import an airport prior to adding a runway.");
                return;
            }
            Runway newRunway = NewRunwayPopup.display("Add a new runway to " + currentAirport.toString());
            // This stops select a runway being added to the combo box.
            if (newRunway == null) {
                return;
            }
            runwayComboBox.getItems().addAll(newRunway);
            runwayComboBox.setValue(newRunway);
            controller.add_Runway(newRunway, currentAirport.getAirport_id());
        } catch (NullPointerException e) {
        }

    }

    @FXML
    public void handleNewObstacle() {
        try {
            Obstacle newObstacle;
            newObstacle = NewObstaclePopup.display("Add a New Obstacle");
            // This stops 'select an obstacle' being added as an option on the combo box.
            if (newObstacle == null) {
                return;
            }
            obstructionComboBox.getItems().add(newObstacle);
            obstructionComboBox.setValue(newObstacle);
            controller.add_obstacle(newObstacle);
        } catch (NullPointerException e) {
        }
    }


    @FXML
    public void handleImportFile() {
        new XML_File_Loader(controller).load_file();
        refresh_combobox();
    }


    @FXML
    void handleImportFolder() {
        new XML_File_Loader(controller).load_directory();
        refresh_combobox();
    }

    @FXML
    public void handleRemoveAirport() {
        RemoveAirportPopup.display(controller);
        refresh_combobox();
    }

    private void refresh_combobox() {
        airportComboBox.getItems().clear();
        airportComboBox.getItems().addAll(controller.get_airports());
        if (airportComboBox.getItems().size() > 0) {
            airportComboBox.setValue(airportComboBox.getItems().get(0));
            if(controller.get_runways(airportComboBox.getValue().getAirport_id()).length > 0){
                runwayComboBox.getItems().clear();
                runwayComboBox.getItems().addAll(controller.get_runways());
                if (runwayComboBox.getItems().size() > 0) {
                    runwayComboBox.setValue(runwayComboBox.getItems().get(0));
                    updateVirtualRunways();
                }
            }
        }

        obstructionComboBox.getItems().clear();
        obstructionComboBox.getItems().addAll(controller.get_obstacles());
        if (obstructionComboBox.getItems().size() > 0) {
            obstructionComboBox.setValue(obstructionComboBox.getItems().get(0));
        }

        // Make sure that only the correct runways are shown for the selected airport.
        if (airportComboBox.getItems().size() > 0) {
            updateRunways();
        }
    }


}