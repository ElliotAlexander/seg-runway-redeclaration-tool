package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewObstaclePopup {

    static ObstaclePosition obstacle;

    public static ObstaclePosition display(String title){
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

        Label designator = new Label("Obstacle name:");
        grid.add(designator, 0, 1);

        final TextField designatorField = new TextField();
        grid.add(designatorField, 1, 1);

        Label heightLabel = new Label("Obstacle Height:");
        grid.add(heightLabel, 0, 2);

        final TextField heightField = new TextField();
        grid.add(heightField, 1, 2);

        Label distLeftTSHLabel = new Label("Distance Left of Threshold:");
        grid.add(distLeftTSHLabel, 0, 2);

        final TextField distLeftTSHField = new TextField();
        grid.add(distLeftTSHField, 1, 2);

        Label distRightTSHLabel = new Label("Distance Right of Threshold:");
        grid.add(distRightTSHLabel, 0, 3);

        final TextField distRightTSHField = new TextField();
        grid.add(distRightTSHField, 1, 3);

        Label distFromCLLabel = new Label("Distance from Centreline:");
        grid.add(distFromCLLabel, 0, 4);

        final TextField distFromCLField = new TextField();
        grid.add(distFromCLField, 1, 4);

        Label runwaySideLabel = new Label("Runway Side:");
        grid.add(runwaySideLabel, 0, 5);

        final TextField runwaySideField = new TextField();
        grid.add(runwaySideField, 1, 5);


        Button button = new Button("Submit");

        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                RunwaySide new_runwaySide = runwaySideField.getText()=="L" ? RunwaySide.LEFT : RunwaySide.RIGHT;

                // TODO catch Number Format here.
                Obstacle new_obstacle = new Obstacle(designatorField.getText(), Integer.parseInt(heightField.getText()));
                obstacle = new ObstaclePosition(new_obstacle,
                        Integer.parseInt(distLeftTSHField.getText()),
                        Integer.parseInt(distRightTSHField.getText()),
                        Integer.parseInt(distFromCLField.getText()),
                        new_runwaySide);
                window.close();
            }
        });
        grid.add(button, 0, 6);

        window.setScene(scene);
        window.showAndWait();
        return obstacle;
    }

}
