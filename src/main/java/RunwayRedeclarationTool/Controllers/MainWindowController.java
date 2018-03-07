package RunwayRedeclarationTool.Controllers;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Exceptions.NoRedeclarationNeededException;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.*;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.View.NewRunwayPopup;
import RunwayRedeclarationTool.View.NewRunwayWindow;
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
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    FlowPane leftTopDownViewContainer, rightTopDownViewContainer;
    @FXML
    Tab leftRunwayTab, rightRunwayTab;
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

    VirtualRunway runway09R = new VirtualRunway("09R", new RunwayParameters(3660, 3660, 3660, 3353));
    VirtualRunway runway27L = new VirtualRunway("27L", new RunwayParameters(3660, 3660, 3660, 3660));
    Runway runway1 = new Runway(runway09R, runway27L);

    VirtualRunway runway09L = new VirtualRunway("09L", new RunwayParameters(3902, 3902, 3902, 3595));
    VirtualRunway runway27R = new VirtualRunway("27R", new RunwayParameters(3884, 3962, 3884, 3884));
    Runway runway2 = new Runway(runway09L, runway27R);

    Obstacle obstacle = new Obstacle("Demo obstacle", 12);

    public void initialize(URL url, ResourceBundle bundle) {
//        runwayComboBox.getItems().addAll(runway1, runway2);
        DB_controller db_controller = DB_controller.instance;
        runwayComboBox.getItems().addAll(db_controller.get_runways());

        obstructionComboBox.getItems().add(obstacle);
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
    public void recalculateDistances() {

        Calculator calculator = Calculator.getInstance();
        try {

            // TODO - When calculate is clicked with empty values, nothing is shown to the user. Catch NumberFormatExcpetion and throw a popup
            // Can copy code from NewRunwayWindow probs.
            Runway runway = runwayComboBox.getValue();
            Obstacle obstacle = obstructionComboBox.getValue();
            ObstaclePosition obstaclePosition = new ObstaclePosition(obstacle, Integer.parseInt(distanceFromTHRLeft.getText()), Integer.parseInt(distanceFromTHRRight.getText()), Integer.parseInt(distanceFromCL.getText()), runwaySideComboBox.getValue());

            declaredDistances.getChildren().clear();
            declaredDistances.getChildren().add(new Text(obstaclePosition.toString()));
            declaredDistances.getChildren().add(new Text("\n\nOriginal distances:\n"));
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
    public void handleNewRunway() {
        Runway newRunway;
        newRunway = NewRunwayPopup.display("Add a New Runway to <Airport>");
        runwayComboBox.getItems().addAll(newRunway);
        runwayComboBox.setValue(newRunway);

        // TODO Move away from arbitrary airports.
        DB_controller.instance.add_Runway(newRunway, "LGW");
        drawRunway();
    }


}