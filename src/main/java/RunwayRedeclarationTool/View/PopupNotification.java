package RunwayRedeclarationTool.View;

import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.TrayNotification;

public class PopupNotification {

    public static void display(String title, String message){

        TrayNotification tray = new TrayNotification();

        Image pic = new Image("/popup.png");
        tray.setTitle(title);
        tray.setMessage(message);
        tray.setRectangleFill(Paint.valueOf("#2A9A84"));
        tray.setImage(pic);
        tray.setAnimationType(AnimationType.POPUP);
        tray.showAndDismiss(Duration.seconds(2));


    }

}
