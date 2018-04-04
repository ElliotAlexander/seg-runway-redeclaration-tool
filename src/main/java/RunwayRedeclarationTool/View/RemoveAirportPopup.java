package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.Airport;
import RunwayRedeclarationTool.Models.Runway;
import RunwayRedeclarationTool.Models.db.DB_controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;


public class RemoveAirportPopup {



    public static Airport[] display(DB_controller controller) {
        final Stage window = new Stage();


        // Window setup
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Remove an Airport");
        BorderPane grid = new BorderPane();
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 700, 400);
        window.setScene(scene);


        // Top Panel
        Label topLabel = new Label("Select Airports to remove:");
        grid.setTop(topLabel);


        // Center Panel - Table setup
        TableView table = new TableView();
        table.setEditable(true);
        TableColumn airportName = new TableColumn("Name");
        TableColumn airportID = new TableColumn("Identifier");
        table.getColumns().addAll(airportName, airportID);

        final ObservableList<Airport> data = FXCollections.observableArrayList(controller.get_airports());

        airportName.setCellValueFactory(
                new PropertyValueFactory<Airport,String>("airport_name")
        );

        airportID.setCellValueFactory(
                new PropertyValueFactory<Airport,String>("airport_id")
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



        confirmButtom.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Airport> airports = table.getSelectionModel().getSelectedItems();
                for(Airport airport : airports){
                    controller.remove_Airport(airport);
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
        return null;
    }
}
