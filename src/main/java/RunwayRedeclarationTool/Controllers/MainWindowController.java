package RunwayRedeclarationTool.Controllers;

import RunwayRedeclarationTool.Models.Calculator;
import RunwayRedeclarationTool.Models.Runway;
import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.VirtualRunway;
import RunwayRedeclarationTool.Views.TopDownView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private Calculator calculator;

    @FXML
    ComboBox<Runway> runwayComboBox;

    @FXML
    FlowPane topDownViewParent = new FlowPane();

    @FXML
    Tab test;

    public void initialize(URL url, ResourceBundle bundle){
        calculator = Calculator.getInstance();

        VirtualRunway runway09R = new VirtualRunway("09R", new RunwayParameters(3660, 3660, 3660, 3353));
        VirtualRunway runway27L = new VirtualRunway("27L", new RunwayParameters(3660,3660,3660,3660));
        Runway runway1 = new Runway(runway09R, runway27L);

        VirtualRunway runway09L = new VirtualRunway("09L", new RunwayParameters(3902,3902,3902,3595));
        VirtualRunway runway27R = new VirtualRunway("27R", new RunwayParameters(3884,3962, 3884,3884));
        Runway runway2 = new Runway(runway09L, runway27R);

        // populate dropdown list
        runwayComboBox.getItems().addAll(runway1, runway2);
    }

    @FXML
    public void showRunway(){
        TopDownView view = new TopDownView();
        view.widthProperty().bind(topDownViewParent.widthProperty());
        topDownViewParent.getChildren().add(view);
    }
}