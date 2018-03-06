package RunwayRedeclarationTool.Controllers;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Exceptions.NoRedeclarationNeededException;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.*;
import RunwayRedeclarationTool.View.NewRunwayWindow;
import RunwayRedeclarationTool.View.TopDownView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML FlowPane topDownViewContainer;
    @FXML ComboBox<Runway> runwayComboBox;
    @FXML ComboBox<Obstacle> obstructionComboBox;
    @FXML TextFlow declaredDistances, calculationsBreakdown;
    @FXML Button calculateButton;

    VirtualRunway runway09R = new VirtualRunway("09R", new RunwayParameters(3660, 3660, 3660, 3353));
    VirtualRunway runway27L = new VirtualRunway("27L", new RunwayParameters(3660, 3660, 3660, 3660));
    Runway runway1 = new Runway(runway09R, runway27L);

    VirtualRunway runway09L = new VirtualRunway("09L", new RunwayParameters(3902, 3902, 3902, 3595));
    VirtualRunway runway27R = new VirtualRunway("27R", new RunwayParameters(3884, 3962, 3884, 3884));
    Runway runway2 = new Runway(runway09L, runway27R);

    Obstacle testObstacle = new Obstacle("Test", 12, -50, 3646, 0, RunwaySide.LEFT);

    public void initialize(URL url, ResourceBundle bundle) {
        runwayComboBox.getItems().addAll(runway1, runway2);
        obstructionComboBox.getItems().add(testObstacle);
    }

    @FXML
    public void drawRunway() {
        topDownViewContainer.getChildren().clear();
        declaredDistances.getChildren().clear();

        Runway runway = runwayComboBox.getValue();
        declaredDistances.getChildren().clear();
        declaredDistances.getChildren().add(new Text("Runway " + runway.leftRunway.getDesignator() + ":\nTORA: " + runway.leftRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.leftRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.leftRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.leftRunway.getOrigParams().getLDA() + "m\n\n"));
        declaredDistances.getChildren().add(new Text("Runway " + runway.rightRunway.getDesignator() + ":\nTORA: " + runway.rightRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.rightRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.rightRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.rightRunway.getOrigParams().getLDA() + "m\n"));

        TopDownView view = new TopDownView(runway.leftRunway); //TODO: Which runway to draw?
        view.widthProperty().bind(topDownViewContainer.widthProperty());
        view.heightProperty().bind(topDownViewContainer.heightProperty());

        topDownViewContainer.getChildren().add(view);
    }

    @FXML
    public void recalculateDistances(){

        Calculator calculator = Calculator.getInstance();
        try{
            Runway runway = runwayComboBox.getValue();
            Obstacle obstacle = obstructionComboBox.getValue();
            calculator.calculate(obstacle, runway);

            declaredDistances.getChildren().clear();
            declaredDistances.getChildren().add(new Text("Runway " + runway.leftRunway.getDesignator() + ":\nTORA: " + runway.leftRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.leftRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.leftRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.leftRunway.getOrigParams().getLDA() + "m\n\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.rightRunway.getDesignator() + ":\nTORA: " + runway.rightRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.rightRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.rightRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.rightRunway.getOrigParams().getLDA() + "m\n\n"));
            declaredDistances.getChildren().add(new Text("Recalculated distances:\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.leftRunway.getDesignator() + ":\nTORA: " + runway.leftRunway.getRecalcParams().getTORA() + "m\nTODA: " + runway.leftRunway.getRecalcParams().getTODA() + "m\nASDA: " + runway.leftRunway.getRecalcParams().getASDA() + "m\nLDA:  " + runway.leftRunway.getRecalcParams().getLDA() + "m\n\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.rightRunway.getDesignator() + ":\nTORA: " + runway.rightRunway.getRecalcParams().getTORA() + "m\nTODA: " + runway.rightRunway.getRecalcParams().getTODA() + "m\nASDA: " + runway.rightRunway.getRecalcParams().getASDA() + "m\nLDA:  " + runway.rightRunway.getRecalcParams().getLDA() + "m\n"));

            calculationsBreakdown.getChildren().clear();
            calculationsBreakdown.getChildren().add(new Text(runway.leftRunway.getRecalcBreakdown() + "\n\n"));
            calculationsBreakdown.getChildren().add(new Text(runway.rightRunway.getRecalcBreakdown()));
        } catch (NoRedeclarationNeededException e){
            System.out.println(e.getMessage());
        } catch (AttributeNotAssignedException e){
            //TODO
        }
    }


    @FXML
    public void handleNewRunway(){
        NewRunwayWindow runway = new NewRunwayWindow();
        try {
            runway.add_mwc(this);
            runway.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add_Runway(Runway r){
        runwayComboBox.getItems().add(r);
        Logger.Log("Adding new runway " + r.toString() + " to main window controller.");
    }

}