package RunwayRedeclarationTool.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class NewRunwayWindow extends Application {

    // Temp static main for testing
    public static void main(String[] args) {
        launch(args);
    }


    private final String FXML_FILE = "NewRunwayWindow.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(FXML_FILE));
        primaryStage.setTitle("Add a runway");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

    }

}
