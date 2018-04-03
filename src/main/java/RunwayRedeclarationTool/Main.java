package RunwayRedeclarationTool;

import RunwayRedeclarationTool.Models.config.Config_Manager;
import RunwayRedeclarationTool.Models.config.Configuration;
import RunwayRedeclarationTool.Controllers.MainWindowController;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.config.PredefinedObstacles;
import RunwayRedeclarationTool.Models.db.DB_controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application{


    private Configuration config;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Config_Manager configManager = new Config_Manager();

        // Note that this represents a file OUTSIDE of the classpath
        this.config = configManager.load_config();
        Logger.Log("Finished loading config file.");
        new Logger(config);

        DB_controller dbc = new DB_controller(config);
        new PredefinedObstacles().addDefaults(dbc, config);

        primaryStage.setTitle("Runway Re-declaration Tool");
        Image icon  = new Image(this.getClass().getClassLoader().getResourceAsStream("icon.png"));
        primaryStage.getIcons().add(icon);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainWindow.fxml"));
        loader.setControllerFactory( c -> {
            return new MainWindowController(config, dbc);
        });

        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}