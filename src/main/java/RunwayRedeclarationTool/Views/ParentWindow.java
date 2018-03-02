package RunwayRedeclarationTool.Views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ParentWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("ParentWindow.fxml"));
        primaryStage.setTitle("Runway Re-declaration Tool");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}