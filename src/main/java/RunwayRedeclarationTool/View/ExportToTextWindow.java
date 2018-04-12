package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Logger.Logger;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class ExportToTextWindow {

    public static void display(String export_string){


        Logger.Log("Opening export as text window.");
        final Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Export to text");

        BorderPane grid = new BorderPane();
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 700, 400);
        window.setScene(scene);


        TextArea textarea = new TextArea();
        textarea.setText(export_string);
        grid.setCenter(textarea);
        textarea.setWrapText(true);



        FlowPane pane = new FlowPane();

        // I wish windows had toast notifications yk
        Label resultsText = new Label("");
        resultsText.setVisible(false);

        Button saveButton = new Button("Save as");
        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showSaveDialog(window);
                Logger.Log("Loaded File [" + file.getName() + "]");

                FileWriter fileWriter = null;
                try {
                    Logger.Log("Opening File Writer Object.");
                    fileWriter = new FileWriter(file);
                    fileWriter.write(textarea.getText());
                    fileWriter.close();
                    Logger.Log("Finished writing to file.");
                    resultsText.setText("Saved!");
                    resultsText.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button copyButton = new Button("Copy");
        copyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                StringSelection string = new StringSelection(textarea.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(string, string);
                Logger.Log("Adding string to clipboard [ " + textarea.getText() + " ]");
                resultsText.setText("Copied!");
                resultsText.setVisible(true);
            }
        });


        Button closeButton = new Button("Close");
        closeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Logger.Log("Closing ExportToTextWindow");
                    window.close();
            }
        });




        pane.getChildren().addAll(copyButton, saveButton, closeButton, resultsText);
        pane.setMargin(copyButton, new Insets(15, 0, 15, 0));
        pane.setMargin(saveButton, new Insets(15, 0, 15, 15));
        pane.setMargin(closeButton, new Insets(15, 0, 15, 15));
        pane.setMargin(resultsText, new Insets(15, 0, 15, 40));



        grid.setBottom(pane);

        window.setScene(scene);
        window.showAndWait();

    }

}
