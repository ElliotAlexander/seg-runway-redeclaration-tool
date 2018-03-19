package RunwayRedeclarationTool;

import RunwayRedeclarationTool.Config.ConfigManager;
import RunwayRedeclarationTool.Config.Configuration;
import RunwayRedeclarationTool.Controllers.MainWindowController;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.db.DB_controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application{


    public static final String Config_File_Name = "config.txt";
    // Note that all fields inside config are final.
    private Configuration config;

    @Override
    public void start(Stage primaryStage) throws Exception {

        ConfigManager configManager = new ConfigManager();

        // Note that this represents a file OUTSIDE of the classpath
        this.config = configManager.load_config(new File(Config_File_Name));
        Logger.Log("Finished loading config file.");

        new Logger(config);
        DB_controller dbc = new DB_controller(config);


        primaryStage.setTitle("Runway Re-declaration Tool");

        // This is better explained here
        // https://www.reddit.com/r/javahelp/comments/4pnbuk/javafx_constructor_parameters_for_controller/
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