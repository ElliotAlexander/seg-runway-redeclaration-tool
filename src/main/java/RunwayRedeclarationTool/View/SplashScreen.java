package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Logger.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;


public class SplashScreen extends Preloader {

    Stage preloaderStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;
        preloaderStage.initStyle(StageStyle.UNDECORATED);
        BorderPane flowPane = new BorderPane();
        Scene scene = new Scene(flowPane, 500, 500);
        preloaderStage.setScene(scene);
        Image image = new Image("splashscreen.png");
        ImageView iv2 = new ImageView();
        iv2.setImage(image);
        iv2.fitWidthProperty().bind(preloaderStage.widthProperty());
        iv2.fitHeightProperty().bind(preloaderStage.heightProperty());
        flowPane.setCenter(iv2);

        preloaderStage.show();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        preloaderStage.setX((screenBounds.getWidth() - preloaderStage.getWidth()) / 2);
        preloaderStage.setY((screenBounds.getHeight() - preloaderStage.getHeight()) / 2);
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if(stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START){
            Logger.Log("Hiding Splashscreen");
            preloaderStage.hide();
        }
    }
}
