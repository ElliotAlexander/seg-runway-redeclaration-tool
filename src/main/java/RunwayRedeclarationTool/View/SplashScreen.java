package RunwayRedeclarationTool.View;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;


public class SplashScreen extends Preloader {

    Stage preloaderStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;
        preloaderStage.initStyle(StageStyle.UNDECORATED);
        BorderPane flowPane = new BorderPane();
        Scene scene = new Scene(flowPane, 1276, 607);
        preloaderStage.setScene(scene);
        Image image = new Image("splashscreen.png");
        ImageView iv2 = new ImageView();
        iv2.setImage(image);
        flowPane.setCenter(iv2);
        preloaderStage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            if (preloaderStage.isShowing()) {
                //fade out, hide stage at the end of animation
                FadeTransition ft = new FadeTransition(
                        Duration.millis(1000), preloaderStage.getScene().getRoot());
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                final Stage s = preloaderStage;
                EventHandler<ActionEvent> eh = new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        s.hide();
                    }
                };
                ft.setOnFinished(eh);
                ft.play();
            } else {
                preloaderStage.hide();
            }
        }
    }
}
