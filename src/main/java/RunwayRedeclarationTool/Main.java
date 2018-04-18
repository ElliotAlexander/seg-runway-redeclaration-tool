package RunwayRedeclarationTool;

import RunwayRedeclarationTool.Exceptions.ConfigurationFileNotFound;
import RunwayRedeclarationTool.Models.config.Config_Manager;
import RunwayRedeclarationTool.Models.config.Configuration;
import RunwayRedeclarationTool.Controllers.MainWindowController;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.config.PredefinedObstacles;
import RunwayRedeclarationTool.Models.db.DB_controller;
import RunwayRedeclarationTool.View.SplashScreen;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{


    private Configuration config;
    private DB_controller controller;

    @Override
    public void init() throws ClassNotFoundException {
        Config_Manager configManager = new Config_Manager();

        // Note that this represents a file OUTSIDE of the classpath
        try {
            this.config = configManager.load_config();
        } catch (ConfigurationFileNotFound configurationFileNotFound) {
            configurationFileNotFound.printStackTrace();
        }
        Logger.Log("Finished loading config file.");
        new Logger(config);

        this.controller = new DB_controller(config);
        new PredefinedObstacles().addDefaults(controller, config);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Runway Re-declaration Tool");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(960);
        Image icon  = new Image(this.getClass().getClassLoader().getResourceAsStream("icon.png"));
        primaryStage.getIcons().add(icon);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainWindow.fxml"));
        loader.setControllerFactory( c -> {
            return new MainWindowController(config, controller);
        });

        Parent root = loader.load();
        Scene primaryscene = new Scene(root);
        primaryStage.setScene(primaryscene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, SplashScreen.class, args);
        //launch(args);


    }
}