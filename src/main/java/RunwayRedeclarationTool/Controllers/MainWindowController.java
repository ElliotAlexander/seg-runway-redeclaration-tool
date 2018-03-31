package RunwayRedeclarationTool.Controllers;

import RunwayRedeclarationTool.Models.config.Configuration;
import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Exceptions.NoRedeclarationNeededException;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.*;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.Models.xml.XML_File_Loader;
import RunwayRedeclarationTool.View.NewAirportPopup;
import RunwayRedeclarationTool.View.NewObstaclePopup;
import RunwayRedeclarationTool.View.NewRunwayPopup;
import RunwayRedeclarationTool.View.TopDownView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    FlowPane leftTopDownViewContainer, rightTopDownViewContainer;
    @FXML
    Tab leftRunwayTab, rightRunwayTab;
    @FXML
    ComboBox<Airport> airportComboBox;
    @FXML
    ComboBox<Runway> runwayComboBox;
    @FXML
    ComboBox<Obstacle> obstructionComboBox;
    @FXML
    ComboBox<RunwaySide> runwaySideComboBox;
    @FXML
    TextFlow declaredDistances, calculationsBreakdown;
    @FXML
    Button calculateButton;
    @FXML
    TextField distanceFromTHRLeft, distanceFromTHRRight, distanceFromCL;

    private final DB_controller controller;
    private final Configuration config;

    public MainWindowController(Configuration config, DB_controller controller){
        this.config = config;
        this.controller = controller;
    }

    public void initialize(URL url, ResourceBundle bundle) {
        refresh_combobox();
        runwaySideComboBox.getItems().addAll(RunwaySide.LEFT, RunwaySide.RIGHT);
    }

    @FXML
    public void drawRunway() {
        leftTopDownViewContainer.getChildren().clear();
        rightTopDownViewContainer.getChildren().clear();
        declaredDistances.getChildren().clear();

        Runway runway = runwayComboBox.getValue();
        declaredDistances.getChildren().clear();
        declaredDistances.getChildren().add(new Text("Runway " + runway.leftRunway.getDesignator() + ":\nTORA: " + runway.leftRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.leftRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.leftRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.leftRunway.getOrigParams().getLDA() + "m\n\n"));
        declaredDistances.getChildren().add(new Text("Runway " + runway.rightRunway.getDesignator() + ":\nTORA: " + runway.rightRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.rightRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.rightRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.rightRunway.getOrigParams().getLDA() + "m\n"));

        TopDownView leftView = new TopDownView(runway.leftRunway);
        leftView.widthProperty().bind(leftTopDownViewContainer.widthProperty());
        leftView.heightProperty().bind(leftTopDownViewContainer.heightProperty());
        leftTopDownViewContainer.getChildren().add(leftView);

        TopDownView rightView = new TopDownView(runway.rightRunway);
        rightView.widthProperty().bind(rightTopDownViewContainer.widthProperty());
        rightView.heightProperty().bind(rightTopDownViewContainer.heightProperty());
        rightTopDownViewContainer.getChildren().add(rightView);

        leftRunwayTab.setText("Runway " + runway.leftRunway.getDesignator());
        rightRunwayTab.setText("Runway " + runway.rightRunway.getDesignator());

    }

    @FXML
    public void updateRunways(){
        Airport airport = airportComboBox.getValue();
        runwayComboBox.getItems().clear();
        Logger.Log("Using airport ID " + airport.getAirport_id());
        for(Runway r : controller.get_runways(airport.getAirport_id())){
            Logger.Log("Runway returned  = " + r.toString());
            runwayComboBox.getItems().add(r);
        }
    }

    @FXML
    public void recalculateDistances() {

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
        } catch (NoRedeclarationNeededException e) {
            System.out.println(e.getMessage());
            declaredDistances.getChildren().add(new Text("\n\n" + e.getMessage()));
        } catch (AttributeNotAssignedException e) {
            //TODO
        }
    }

    @FXML
    public void handleNewAirport() {
        try {
            Airport newAirport = NewAirportPopup.display();
            airportComboBox.getItems().add(newAirport);
            airportComboBox.setValue(newAirport);

            controller.add_airport(newAirport);
        } catch (NullPointerException e){}
    }

    @FXML
    public void handleNewRunway() {
        try {
            Airport currentAirport = airportComboBox.getValue();
            Runway newRunway = NewRunwayPopup.display("Add a new runway to " + currentAirport.toString());
            runwayComboBox.getItems().addAll(newRunway);
            runwayComboBox.setValue(newRunway);

            controller.add_Runway(newRunway, currentAirport.getAirport_id());
            drawRunway();
        } catch (NullPointerException e){}

    }

    @FXML
    public void handleNewObstacle() {
        try {
            Obstacle newObstacle;
            newObstacle = NewObstaclePopup.display("Add a New Obstacle");
            controller.add_obstacle(newObstacle);
            refresh_combobox();
        } catch (NullPointerException e){}
    }


    @FXML
    public void handleImportFile(){
        new XML_File_Loader(controller).load_file();
        refresh_combobox();
    }


    @FXML void handleImportFolder() {
        new XML_File_Loader(controller).load_directory();
        refresh_combobox();
    }


    private void refresh_combobox(){
        airportComboBox.getItems().clear();
        airportComboBox.getItems().addAll(controller.get_airports());

        runwayComboBox.getItems().clear();
        runwayComboBox.getItems().addAll(controller.get_runways());

        obstructionComboBox.getItems().clear();
        obstructionComboBox.getItems().addAll(controller.get_obstacles());

    }
}