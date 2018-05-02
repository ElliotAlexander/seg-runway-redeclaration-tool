package RunwayRedeclarationTool.Controllers;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Exceptions.NoRedeclarationNeededException;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.*;
import RunwayRedeclarationTool.Models.config.Configuration;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.Models.xml.XML_Export;
import RunwayRedeclarationTool.View.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * This is the controller that handles user events and updates the views.
 */
public class MainWindowController implements Initializable {

    private final Configuration config;
    @FXML
    private FlowPane topDownViewContainer, sideOnViewContainer;
    @FXML
    private ComboBox<Airport> airportComboBox;
    @FXML
    private ComboBox<Runway> runwayComboBox;
    @FXML
    private ComboBox<VirtualRunway> virtualRunwayComboBox;
    @FXML
    private ComboBox<Obstacle> obstacleComboBox;
    @FXML
    private ComboBox<RunwaySide> runwaySideComboBox;
    @FXML
    private TextFlow declaredDistances, calculationsBreakdown;
    @FXML
    private CheckBox rotateViewCheckbox;
    @FXML
    private TextField distanceFromTHRLeft, distanceFromTHRRight, distanceFromCL, obstacleWidth;
    private TopDownView topDownView;
    private SideOnView sideOnView;
    private ObstaclePosition obstaclePosition;

    private final DB_controller controller;
    private final PopupController popupController;
    private final IOController ioController;


    public MainWindowController(Configuration config, DB_controller controller) {
        this.config = config;
        this.controller = controller;
        this.popupController = new PopupController();
        this.ioController = new IOController(this, controller);
    }

    /**
     * Called to initialize a controller after its root element has been completely processed.
     *
     * @param location  used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        runwaySideComboBox.getItems().addAll(RunwaySide.LEFT, RunwaySide.RIGHT, RunwaySide.CENTER);

        distanceFromTHRLeft.textProperty().addListener(new IntegerOnlyTextListener(distanceFromTHRLeft));
        distanceFromTHRRight.textProperty().addListener(new IntegerOnlyTextListener(distanceFromTHRRight));
        distanceFromCL.textProperty().addListener(new IntegerOnlyTextListener(distanceFromCL));
        obstacleWidth.textProperty().addListener(new IntegerOnlyTextListener(obstacleWidth));

        if (controller.get_airports().length > 0) {
            refresh_airports();
            drawRunway();
        }

        if (controller.get_obstacles().length > 0) {
            refresh_obstacles();
        }
    }

    @FXML
    public void handleVirtualRunwayComboBox() {
        drawRunway();
    }

    /**
     * Add top-down and side-on views of the selected runway to the canvas
     */
    public void drawRunway() {
        topDownViewContainer.getChildren().clear();
        sideOnViewContainer.getChildren().clear();

        Runway runway = runwayComboBox.getSelectionModel().getSelectedItem();
        VirtualRunway virtualRunway = virtualRunwayComboBox.getSelectionModel().getSelectedItem();

        try {
            if (runway == null) {
                if (runwayComboBox.getItems().size() > 0) {
                    runway = runwayComboBox.getItems().get(0);
                    // Default to the left virtual runway
                    virtualRunway = runway.leftRunway;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // There is no runway yet
            return;
        }

        if (virtualRunway == null) {
            return;
        }

        Pane pane = new Pane();

        // Add a compass to the top-down view
        Canvas canvas = new Canvas(60, 60);
        Image compass = new Image(this.getClass().getClassLoader().getResourceAsStream("compass.png"));

        String designator_String = virtualRunway.getDesignator();
        Integer designator = Integer.parseInt(designator_String.replaceAll("[^\\d.]", ""));

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Calculate bearing of runway
        double bearing;
        if (designator <= 18) {
            bearing = designator * 10;
        } else {
            bearing = (designator - 18) * 10;
        }

        // Rotate compass accordingly
        if (rotateViewCheckbox.isSelected()) {
            canvas.setRotate(0);
        } else {
            canvas.setRotate(90 - bearing);
        }
        gc.drawImage(compass, 0, 0, 60, 60);

        // Draw static elements: measuring line, take-off direction, compass
        StaticElements staticElements = new StaticElements(virtualRunway, obstaclePosition, rotateViewCheckbox.isSelected());
        staticElements.widthProperty().bind(topDownViewContainer.widthProperty());
        staticElements.heightProperty().bind(topDownViewContainer.heightProperty());

        topDownView = new TopDownView(virtualRunway, obstaclePosition, rotateViewCheckbox.isSelected());
        topDownView.widthProperty().bind(topDownViewContainer.widthProperty());
        topDownView.heightProperty().bind(topDownViewContainer.heightProperty());

        // Add everything to top down view tab
        pane.getChildren().addAll(staticElements, topDownView, canvas);
        topDownViewContainer.getChildren().add(pane);

        // Draw side on view
        sideOnView = new SideOnView(virtualRunway, obstaclePosition);
        sideOnView.widthProperty().bind(sideOnViewContainer.widthProperty());
        sideOnView.heightProperty().bind(sideOnViewContainer.heightProperty());
        sideOnViewContainer.getChildren().add(sideOnView);

        topDownView.drawObstacle();
        sideOnView.drawObstacle();

        popupController.redrawAll(sideOnView, topDownView);
    }


    @FXML
    public void popoutSideView() {
        popupController.newPopup(sideOnView);
    }

    @FXML
    public void popoutTopView() {
        popupController.newPopup(topDownView);
    }

    /**
     * Clear the current object and additional markings.
     */
    @FXML
    public void clearFields() {
        distanceFromTHRLeft.clear();
        distanceFromTHRRight.clear();
        distanceFromCL.clear();
        obstacleWidth.clear();
        this.obstaclePosition = null;
        drawRunway();
    }

    /**
     * Export the current top-down view as an image.
     */
    @FXML
    public void handleTopDownImageExport() {
        ioController.exportImage(topDownView);
    }

    /**
     * Export the current side-on view as an image.
     */
    @FXML
    public void handleSideOnImageExport() {
        ioController.exportImage(sideOnView);
    }

    /**
     * Export all runway and obstacle information as text.
     */
    @FXML
    public void handleExportAsText() {
        ioController.exportAsText(declaredDistances.getChildren(), calculationsBreakdown.getChildren(), obstacleComboBox.getSelectionModel().getSelectedItem());
    }

    /**
     * Recalculate and display the declared distances given the obstacle on the runway.
     */
    @FXML
    public void recalculateDistances() {

        Logger.Log("Attempting to recalculate distances.");
        Calculator calculator = Calculator.getInstance();
        try {
            Runway runway = runwayComboBox.getValue();
            Obstacle obstacle = obstacleComboBox.getValue();

            int distFromCL;
            if (runwaySideComboBox.getValue() == RunwaySide.CENTER) {
                distFromCL = 0;
            } else {
                distFromCL = Integer.parseInt(distanceFromCL.getText());
            }
            obstaclePosition = new ObstaclePosition(obstacle, Integer.parseInt(distanceFromTHRLeft.getText()), Integer.parseInt(distanceFromTHRRight.getText()), Integer.parseInt(obstacleWidth.getText()), distFromCL, runwaySideComboBox.getValue());

            declaredDistances.getChildren().clear();
            declaredDistances.getChildren().add(new Text("Original distances:\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.leftRunway.getDesignator() + ":\nTORA: " + runway.leftRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.leftRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.leftRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.leftRunway.getOrigParams().getLDA() + "m\n\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.rightRunway.getDesignator() + ":\nTORA: " + runway.rightRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.rightRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.rightRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.rightRunway.getOrigParams().getLDA() + "m\n\n"));

            calculator.calculate(obstaclePosition, runway);

            declaredDistances.getChildren().add(new Text("Recalculated distances:\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.leftRunway.getDesignator() + ":\nTORA: " + runway.leftRunway.getRecalcParams().getTORA() + "m\nTODA: " + runway.leftRunway.getRecalcParams().getTODA() + "m\nASDA: " + runway.leftRunway.getRecalcParams().getASDA() + "m\nLDA:  " + runway.leftRunway.getRecalcParams().getLDA() + "m\n\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.rightRunway.getDesignator() + ":\nTORA: " + runway.rightRunway.getRecalcParams().getTORA() + "m\nTODA: " + runway.rightRunway.getRecalcParams().getTODA() + "m\nASDA: " + runway.rightRunway.getRecalcParams().getASDA() + "m\nLDA:  " + runway.rightRunway.getRecalcParams().getLDA() + "m\n"));

            calculationsBreakdown.getChildren().clear();
            calculationsBreakdown.getChildren().add(new Text(runway.leftRunway.getRecalcBreakdown() + "\n\n"));
            calculationsBreakdown.getChildren().add(new Text(runway.rightRunway.getRecalcBreakdown()));

            drawRunway();
            PopupNotification.display("Obstacle added", "Distances recalculated and display updated");

        } catch (NoRedeclarationNeededException e) {
            Logger.Log(Logger.Level.ERROR, e.getStackTrace().toString());
            declaredDistances.getChildren().add(new Text("\n\n" + e.getMessage()));
        } catch (AttributeNotAssignedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error recalculating runway parameters.\nEnsure that the Obstacle position fields are correct.", ButtonType.CLOSE);
            alert.setTitle("Calculation failed");
            alert.showAndWait();
            return;
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to parse values in Obstacle Position boxes.\n,Please ensure the values are valid.", ButtonType.CLOSE);
            alert.setTitle("Failed to parse values");
            alert.showAndWait();
        }
    }

    /**
     * Add a new airport to the database.
     */
    @FXML
    public void handleNewAirport() {
        try {
            NewAirportPopup popup = new NewAirportPopup();
            Airport newAirport = popup.display();
            // This stops select an airport being added to the combobox.
            if (newAirport == null) {
                Logger.Log("New airport popup returned null");
                return;
            }
            airportComboBox.getItems().add(newAirport);
            airportComboBox.setValue(newAirport);
            controller.add_airport(newAirport);
            PopupNotification.display("Success - Airport added.", "Airport " + newAirport.toString() + " added successfully.");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Add a new runway to the database.
     */
    @FXML
    public void handleNewRunway() {

        Airport currentAirport = airportComboBox.getValue();
        if (currentAirport == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You need to add or import an airport prior to adding a runway.", ButtonType.CLOSE);
            alert.setTitle("Failed to parse values");
            alert.showAndWait();
            return;
        }
        NewRunwayPopup popup = new NewRunwayPopup();
        Runway newRunway = popup.display("Add a new runway to " + currentAirport.toString());
        // This stops select a runway being added to the combo box.
        if (newRunway == null) {
            return;
        }
        runwayComboBox.getItems().addAll(newRunway);
        runwayComboBox.setValue(newRunway);
        controller.add_Runway(newRunway, currentAirport.getAirport_id());
        PopupNotification.display("Success - Runway added", "Successfully added Runway " + newRunway.toString());

    }

    /**
     * Add a new obstacle to the database.
     */
    @FXML
    public void handleNewObstacle() {
        Obstacle newObstacle;
        NewObstaclePopup popup = new NewObstaclePopup();
        newObstacle = popup.display("Add a New Obstacle");
        // This stops 'select an obstacle' being added as an option on the combo box.
        if (newObstacle == null) {
            return;
        }
        obstacleComboBox.getItems().add(newObstacle);
        obstacleComboBox.setValue(newObstacle);
        controller.add_obstacle(newObstacle);
        PopupNotification.display("Success - Obstacle added", "Successfully added obstacle " + newObstacle.toString());
    }

    /**
     * Load an XML file.
     */
    @FXML
    public void handleImportFile() {
        ioController.importXMLFile();
        refresh_airports();
        refresh_obstacles();
    }

    /**
     * Load a directory.
     */
    @FXML
    void handleImportFolder() {
        ioController.importXMLFolder();
        refresh_airports();
        refresh_obstacles();
    }

    /**
     * Remove an airport from the database.
     */
    @FXML
    public void handleRemoveAirport() {
        int remove_count = 0;
        RemoveAirportPopup popup = new RemoveAirportPopup();
        Airport[] airports = popup.display(controller);
        if (airports != null) {
            for (Airport a : airports) {
                for (Runway runway : controller.get_runways(a.getAirport_id())) {
                    Logger.Log("Removing " + runway.toString() + " attached to Airport " + a.toString() + ".");
                    controller.remove_Runway(runway);
                }
                Logger.Log("Removed Airport " + a.toString());
                controller.remove_Airport(a);
                remove_count++;
            }
            refresh_airports();
            PopupNotification.display("Success - Airports removed", "Successfully removed " + remove_count + " airports.");

        } else {
            Logger.Log("Remove Airports Popup force closed by user.");
        }
    }


    /**
     * This method is called when a runway is removed.
     */
    @FXML
    public void handleRemoveRunway() {
        SelectAirportPopup airportPopup = new SelectAirportPopup();
        Airport[] airports = airportPopup.display(controller);

        if (airports == null) {
            Logger.Log("User cancelled Runway remove operation. Exiting remove process.");
            PopupNotification.display("Cancelled removing runway", "");
            return;
        }
        if (airports.length > 1) {
            Logger.Log("Multiple airports selected, taking only the first airport.");
            PopupNotification.error("Please select a single airport!", "Runways can only be removed from one airport at once.");
            return;
        }

        RemoveRunwayPopup popup = new RemoveRunwayPopup();
        Runway[] runways = popup.display(controller.get_runways(airports[0].getAirport_id()));

        if (runways == null) {
            Logger.Log("User cancelled Runway remove operation. Exiting remove process.");
            PopupNotification.display("Cancelled removing runway", "");
            return;
        }

        for (Runway runway : runways) {
            controller.remove_Runway(runway);
            Logger.Log("Removing " + runway.toString() + " from airport " + airports[0].toString());
            PopupNotification.display("Removed Runway " + runway.toString(), "");
        }
    }

    /**
     * Refresh the combo boxes to reflect changes to .
     */
    private void refresh_airports() {
        airportComboBox.getItems().clear();
        Airport[] airports = controller.get_airports();
        if (airports.length > 0) {
            airportComboBox.getItems().addAll(airports);
            airportComboBox.setValue(airports[0]);
            refresh_runways();
        } else {
            airportComboBox.getItems().clear();
        }
    }

    @FXML
    public void handleObstacleComboBox() {
        if (obstacleComboBox.getValue() != null) {
            PopupNotification.display("Switched to obstacle: " + obstacleComboBox.getValue().getName(), "");
        }
    }

    @FXML
    private void refresh_runways() {
        if (airportComboBox.getItems().size() > 0) {
            Runway[] runways = controller.get_runways(airportComboBox.getValue().getAirport_id());
            if (runways.length > 0) {
                runwayComboBox.getItems().clear();
                runwayComboBox.getItems().addAll(runways);
                runwayComboBox.setValue(runwayComboBox.getItems().get(0));
                PopupNotification.display("Switched to " + runwayComboBox.getItems().get(0).toString(), "");
                try {
                    if (obstaclePosition != null) {
                        recalculateDistances();
                    }
                } catch (Exception e) {
                    Logger.Log(Logger.Level.ERROR, "NOPE");
                }
                refresh_virtual_runways();
            }
        } else {
            runwayComboBox.getItems().clear();
        }
    }

    private void refresh_obstacles() {
        obstacleComboBox.getItems().clear();
        obstacleComboBox.getItems().addAll(controller.get_obstacles());
        if (obstacleComboBox.getItems().size() > 0) {
            obstacleComboBox.setValue(obstacleComboBox.getItems().get(0));
        } else {
            obstacleComboBox.getItems().clear();
        }
    }

    @FXML
    private void refresh_virtual_runways() {
        Runway runway = runwayComboBox.getValue();
        if (runway == null) {
            return;
        }
        ArrayList<VirtualRunway> virtualRunways = new ArrayList<>();
        Collections.addAll(virtualRunways, runway.leftRunway, runway.rightRunway);
        ObservableList<VirtualRunway> observableList = FXCollections.observableList(virtualRunways);
        virtualRunwayComboBox.setItems(observableList);
        virtualRunwayComboBox.setValue(virtualRunwayComboBox.getItems().get(0));
        updateDeclaredDistancesTextfield();
    }

    public void updateDeclaredDistancesTextfield() {
        try {
            Runway runway = runwayComboBox.getValue();
            declaredDistances.getChildren().clear();
            declaredDistances.getChildren().add(new Text("Runway " + runway.leftRunway.getDesignator() + ":\nTORA: " + runway.leftRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.leftRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.leftRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.leftRunway.getOrigParams().getLDA() + "m\n\n"));
            declaredDistances.getChildren().add(new Text("Runway " + runway.rightRunway.getDesignator() + ":\nTORA: " + runway.rightRunway.getOrigParams().getTORA() + "m\nTODA: " + runway.rightRunway.getOrigParams().getTODA() + "m\nASDA: " + runway.rightRunway.getOrigParams().getASDA() + "m\nLDA:  " + runway.rightRunway.getOrigParams().getLDA() + "m\n"));
        } catch (NullPointerException e) {
            // The runway is not set.
        }
    }

    /**
     * Prevents user from entering a value for distance to C/L if they specify the object is in the center.
     */
    @FXML
    private void runwaySideComboBoxHandler() {
        if (runwaySideComboBox.getValue() == RunwaySide.CENTER) {
            distanceFromCL.setText("0");
            distanceFromCL.setEditable(false);
        } else {
            distanceFromCL.clear();
            distanceFromCL.setEditable(true);
        }
    }

    /**
     * Show the About popup display.
     */
    @FXML
    private void showAbout() {
        AboutPopup.display();
    }

    @FXML
    public void handleExportXML() {
        new XML_Export(controller, obstaclePosition);
    }

    @FXML
    public void handleRemoveObstacle() {
        int removed_count = 0;
        RemoveObstaclePopup popup = new RemoveObstaclePopup();
        for (Obstacle o : popup.display(controller)) {
            controller.remove_obstacle(o);
            removed_count++;
        }
        refresh_obstacles();
        PopupNotification.display("Success - Obstacles removed.", "Successfully removed " + removed_count + " obstacles.");
    }

    public ObstaclePosition getObstaclePosition() {
        return obstaclePosition;
    }

    public void setObstaclePosition(ObstaclePosition o) {
        obstaclePosition = o;
        Logger.Log(Logger.Level.INFO, "Updated Obstacle Position to [" + obstaclePosition.toString() + "]");
        distanceFromCL.setText(String.valueOf(obstaclePosition.getDistFromCL()));
        distanceFromTHRRight.setText(String.valueOf(obstaclePosition.getDistRightTSH()));
        distanceFromTHRLeft.setText(String.valueOf(obstaclePosition.getDistLeftTSH()));
        obstacleWidth.setText(String.valueOf(obstaclePosition.getWidth()));
        recalculateDistances();
    }

    public Obstacle getObstacle() {
        if (obstacleComboBox.getSelectionModel().getSelectedItem() != null) {
            return obstacleComboBox.getSelectionModel().getSelectedItem();
        } else if (obstacleComboBox.getItems().size() > 0) {
            obstacleComboBox.getItems().get(0);
        } else {
            Logger.Log("No obstacles have been added! Returning a dummy obstacle.");
            return new Obstacle("Dummy Obstacle", 50);
        }
        return null;
    }

    @FXML
    public void handleOpenLogFile(){
        ioController.openLogFile(config);
    }

    @FXML
    public void handleOpenLogDirectory(){
        ioController.openLogDirectory(config);
    }

}