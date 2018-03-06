package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.Runway;
import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;

public class NewRunwayWindow extends Application {



    @FXML
    private TextField lda_textfield, asda_textfield, toda_textfield, tora_textfield, designation_textfield;
    private final String FXML_FILE = "NewRunwayWindow.fxml";

    public Runway NewRunwayWindow(){
        launch();
        return null;
    }

    // Temp static main for testing
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(FXML_FILE));
        primaryStage.setTitle("Add a runway");
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();

    }

    // FXML is weird bro
    @FXML
    private void handleClearButton(){
        designation_textfield.setText("");
        asda_textfield.setText("");
        tora_textfield.setText("");
        toda_textfield.setText("");
        lda_textfield.setText("");
    }



    // TODO can runway parameters be doubles? I'm okay with it but needs to be raised.
    @FXML
    private void handleSubmitButton() {
        Integer tora, toda, asda, lda;
        String designator;
        try {
            designator =  designation_textfield.getText();
            tora = Integer.parseInt(tora_textfield.getText());
            toda = Integer.parseInt(toda_textfield.getText());
            asda = Integer.parseInt(asda_textfield.getText());
            lda =  Integer.parseInt(lda_textfield.getText());
            RunwayParameters runwayParameters = new RunwayParameters(tora, toda, asda, lda);
            VirtualRunway new_v_runway = new VirtualRunway(designator, runwayParameters);
        } catch(NumberFormatException e ){
            JOptionPane.showMessageDialog(null, "Error parsing runway inputs!");
        }
    }

}
