package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.Airport;
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
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;

public class SelectAirportPopup {

    private static boolean force_closed = false;

    public Airport[] display(DB_controller controller) {
        final Stage window = new Stage();
        force_closed = false;

        // Window setup
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Select Airport to Remove");
        BorderPane grid = new BorderPane();
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 700, 400);
        window.setScene(scene);


        // Top Panel
        Label topLabel = new Label("Select one or more Airports from the table.");
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


        ArrayList<Airport> airports = new ArrayList<>();


            confirmButtom.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    List<Airport> showing = table.getSelectionModel().getSelectedItems();
                    for(Airport a : showing){
                        airports.add(a);
                    }
                    window.close();
                    force_closed = true;

                }});


        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                window.close();
                force_closed = true;
            }
        });


        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                     public void handle(WindowEvent we) {
                                            force_closed = true;
                                            window.close();
                                         }
                                 });

        window.setScene(scene);
        window.showAndWait();

        // This allows us to tell in XML_Export whether the user closed the window via the X button/Cancel button (i.e. cancel the export process), or just
        // didn't select any values.
        Airport[] return_array = force_closed ? null : new Airport[airports.size()];
        return return_array;
    }
}
