package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.Airport;
import RunwayRedeclarationTool.Models.Obstacle;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.Models.xml.XML_Export;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;

public class SelectObstaclePopup {

    public static boolean export_obstacle_position = true;

    public static Obstacle[] display(DB_controller controller) {
        final Stage window = new Stage();


        // Window setup
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Select an obstacle to export");
        BorderPane grid = new BorderPane();
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 700, 400);
        window.setScene(scene);


        // Top Panel
        Label topLabel = new Label("Select one or more Obstacles from the table");
        grid.setTop(topLabel);


        // Center Panel - Table setup
        TableView table = new TableView();
        table.setEditable(true);
        TableColumn obstacleName = new TableColumn("Name");
        TableColumn obstacleHeight = new TableColumn("Height");
        table.getColumns().addAll(obstacleName, obstacleHeight);

        final ObservableList<Obstacle> data = FXCollections.observableArrayList(controller.get_obstacles());

        obstacleName.setCellValueFactory(
                new PropertyValueFactory<Airport,String>("name")
        );

        obstacleHeight.setCellValueFactory(
                new PropertyValueFactory<Airport,String>("height")
        );

        table.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );


        table.setItems(data);
        grid.setCenter(table);

        // Bottom Panel
        Button confirmButtom = new Button("Select");
        Button cancelButton = new Button("Cancel");
        FlowPane layout = new FlowPane();
        layout.getChildren().add(confirmButtom);
        layout.setMargin(confirmButtom, new Insets(15, 15, 15, 0));
        layout.getChildren().add(cancelButton);
        layout.setMargin(cancelButton, new Insets(15, 0, 15, 0));
        grid.setBottom(layout);

        CheckBox checkbox = new CheckBox();
        checkbox.setText("Export obstacle position");
        checkbox.setSelected(true);
        layout.getChildren().add(checkbox);
        layout.setMargin(checkbox, new Insets(15, 15, 15, 15));

        export_obstacle_position = true;

        ArrayList<Obstacle> obstacles = new ArrayList<>();

        checkbox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                export_obstacle_position = checkbox.isSelected();
                Logger.Log("Setting export obstacle position to " + checkbox.isSelected());
            }
        });

        confirmButtom.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                List<Obstacle> showing = table.getSelectionModel().getSelectedItems();
                for(Obstacle obstacle : showing){
                    obstacles.add(obstacle);
                }
                window.close();
            }
        });

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                window.close();
            }
        });

        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                Logger.Log("Window closed from SelectObstaclePopup. Exiting Export process.");
                XML_Export.force_close_event = true;
            }
        });


        window.setScene(scene);
        window.showAndWait();
        return obstacles.toArray(new Obstacle[obstacles.size()]);
    }
}
