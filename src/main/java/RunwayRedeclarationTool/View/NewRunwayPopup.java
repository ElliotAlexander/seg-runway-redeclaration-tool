package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.Runway;
import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.VirtualRunway;
import RunwayRedeclarationTool.Models.db.DB_controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class NewRunwayPopup {
    static Runway runway;

    public static Runway display(String title){
        final Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 700, 400);
        window.setScene(scene);

        Label designator = new Label("Runway designator 1:");
        grid.add(designator, 0, 1);

        final TextField designatorField = new TextField();
        grid.add(designatorField, 1, 1);

        Label toraLabel = new Label("TORA:");
        grid.add(toraLabel, 0, 2);

        final TextField tora = new TextField();
        grid.add(tora, 1, 2);

        Label todaLabel = new Label("TODA:");
        grid.add(todaLabel, 0, 3);

        final TextField toda = new TextField();
        grid.add(toda, 1, 3);

        Label asdalabel = new Label("ASDA:");
        grid.add(asdalabel, 0, 4);

        final TextField asda = new TextField();
        grid.add(asda, 1, 4);

        Label ldalabel = new Label("LDA:");
        grid.add(ldalabel, 0, 5);

        final TextField lda = new TextField();
        grid.add(lda, 1, 5);


        Label designator2 = new Label("Runway designator 2:");
        grid.add(designator2, 2, 1);

        final TextField designatorField2 = new TextField();
        grid.add(designatorField2, 3, 1);

        Label toralabel2 = new Label("TORA:");
        grid.add(toralabel2, 2, 2);

        final TextField tora2 = new TextField();
        grid.add(tora2, 3, 2);

        Label todalabel2 = new Label("TODA:");
        grid.add(todalabel2, 2, 3);

        final TextField toda2 = new TextField();
        grid.add(toda2, 3, 3);

        Label asdalabel2 = new Label("ASDA:");
        grid.add(asdalabel2, 2, 4);

        final TextField asda2 = new TextField();
        grid.add(asda2, 3, 4);

        Label ldalabel2 = new Label("LDA:");
        grid.add(ldalabel2, 2, 5);

        final TextField lda2 = new TextField();
        grid.add(lda2, 3, 5);

        Button button = new Button("Submit");
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                VirtualRunway leftRunway = new VirtualRunway(
                        designatorField.getText(),
                        new RunwayParameters(
                        Integer.parseInt(tora.getText()),
                        Integer.parseInt(toda.getText()),
                        Integer.parseInt(asda.getText()),
                        Integer.parseInt(lda.getText())
                ));
                VirtualRunway rightRunway = new VirtualRunway(
                        designatorField.getText(),
                        new RunwayParameters(
                                Integer.parseInt(tora2.getText()),
                                Integer.parseInt(toda2.getText()),
                                Integer.parseInt(asda2.getText()),
                                Integer.parseInt(lda2.getText())
                        ));
                runway = new Runway(leftRunway, rightRunway);
                window.close();
            }
        });
        grid.add(button, 0, 6);

        window.setScene(scene);
        window.showAndWait();
        return runway;
    }
}
