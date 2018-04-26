package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.Airport;
import RunwayRedeclarationTool.Models.Obstacle;
import RunwayRedeclarationTool.Models.db.DB_controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class RemoveObstaclePopup {

    public static Obstacle[] display(DB_controller controller) {
        final Stage window = new Stage();


        // Window setup
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Select an obstacle to remove");
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
        Button confirmButtom = new Button("Remove");
        Button cancelButton = new Button("Cancel");
        FlowPane layout = new FlowPane();
        layout.getChildren().add(confirmButtom);
        layout.setMargin(confirmButtom, new Insets(15, 15, 15, 0));
        layout.getChildren().add(cancelButton);
        layout.setMargin(cancelButton, new Insets(15, 0, 15, 0));
        grid.setBottom(layout);

        ArrayList<Obstacle> obstacles = new ArrayList<>();

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


        window.setScene(scene);
        window.showAndWait();
        return obstacles.toArray(new Obstacle[obstacles.size()]);
    }

}
