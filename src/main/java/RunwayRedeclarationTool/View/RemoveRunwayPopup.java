package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.Airport;
import RunwayRedeclarationTool.Models.Runway;
import RunwayRedeclarationTool.Models.VirtualRunway;
import RunwayRedeclarationTool.Models.db.DB_controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class RemoveRunwayPopup {

    private static boolean force_closed = false;

    public Runway[] display(Runway[] input) {
        final Stage window = new Stage();
        force_closed = false;

        // Window setup
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Select Runway to Remove");
        BorderPane grid = new BorderPane();
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 700, 400);
        window.setScene(scene);


        // Top Panel
        Label topLabel = new Label("Select one or more Runways to remove");
        grid.setTop(topLabel);


        // Center Panel - Table setup
        TableView table = new TableView();
        table.setEditable(true);
        TableColumn runwayDesignator = new TableColumn("Designator");
        table.getColumns().addAll(runwayDesignator);

        final ObservableList<Runway> data = FXCollections.observableArrayList(input);

        runwayDesignator.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Runway, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue call(TableColumn.CellDataFeatures<Runway, String> p) {
                    ObservableValue<String> obs = new SimpleStringProperty(p.getValue().toString());
                    return obs;
                }
            }
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


        ArrayList<Runway> runways = new ArrayList<>();


        confirmButtom.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                List<Runway> showing = table.getSelectionModel().getSelectedItems();
                for(Runway a : showing){
                    runways.add(a);
                }
                window.close();
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
        Runway[] return_array = force_closed ? null : runways.toArray(new Runway[runways.size()]);
        return return_array;
    }

}
