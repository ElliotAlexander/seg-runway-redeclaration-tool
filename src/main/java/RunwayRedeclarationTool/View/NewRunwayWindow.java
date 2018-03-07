package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Controllers.MainWindowController;
import RunwayRedeclarationTool.Models.Runway;
import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.VirtualRunway;
import RunwayRedeclarationTool.Models.db.DB_controller;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;

public class NewRunwayWindow extends Application {


    private MainWindowController mwc;


    public void add_mwc(MainWindowController mwc){
        this.mwc = mwc;
    }

    @FXML
    private TextField lda_textfield_1, lda_textfield_2, asda_textfield_1, asda_textfield_2, toda_textfield_1, toda_textfield_2, tora_textfield_1, tora_textfield_2, designation_textfield_1, designation_textfield_2;

    @FXML
    private Button submitButton;

    private final String FXML_FILE = "NewRunwayWindow.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(FXML_FILE));
        primaryStage.setTitle("Add a runway");
        primaryStage.setScene(new Scene(root, 825, 450));
        primaryStage.show();
    }

    // FXML is weird bro
    @FXML
    private void handleClearButton(){
        designation_textfield_1.setText("");
        asda_textfield_1.setText("");
        tora_textfield_1.setText("");
        toda_textfield_1.setText("");
        lda_textfield_1.setText("");

        designation_textfield_2.setText("");
        asda_textfield_2.setText("");
        tora_textfield_2.setText("");
        toda_textfield_2.setText("");
        lda_textfield_2.setText("");

    }

    @FXML
    private void handleSubmitButton() {
        Integer tora_1, toda_1, asda_1, lda_1;
        String designator_1;


        Integer tora_2, toda_2, asda_2, lda_2;
        String designator_2;
        try {
            designator_1 =  designation_textfield_1.getText();
            tora_1 = Integer.parseInt(tora_textfield_1.getText());
            toda_1 = Integer.parseInt(toda_textfield_1.getText());
            asda_1 = Integer.parseInt(asda_textfield_1.getText());
            lda_1 =  Integer.parseInt(lda_textfield_1.getText());
            VirtualRunway VR_1 = new VirtualRunway(designator_1, new RunwayParameters( tora_1, toda_1, asda_1, lda_1));

            designator_2 =  designation_textfield_2.getText();
            tora_2 = Integer.parseInt(tora_textfield_2.getText());
            toda_2 = Integer.parseInt(toda_textfield_2.getText());
            asda_2 = Integer.parseInt(asda_textfield_2.getText());
            lda_2 =  Integer.parseInt(lda_textfield_2.getText());
            VirtualRunway VR_2 = new VirtualRunway(designator_2, new RunwayParameters( tora_2, toda_2, asda_2, lda_2));



            Runway final_runway = new Runway(VR_1, VR_2);

            DB_controller.instance.add_Runway(final_runway, "EGLL");


            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();

        } catch(NumberFormatException e ){
            JOptionPane.showMessageDialog(null, "Error parsing runway inputs!");
            return;
        }
    }

}
