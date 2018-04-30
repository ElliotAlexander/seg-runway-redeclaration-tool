package RunwayRedeclarationTool.View;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class CanvasPopup {


    private Node pane;
    private Stage window;

    private final String title;

    public CanvasPopup(String title){
        this.title = title;
    }

    public void display(Image image){

        window = new Stage();
        window.initModality(Modality.WINDOW_MODAL);
        window.setTitle("Side on View");
        BorderPane root = new BorderPane(pane);
        Scene scene = new Scene(root, image.getWidth(), image.getHeight());

        window.setScene(scene);
        redraw(image);
        window.show();

    }


    public void redraw(Image image){
        if(image == null){
            return;
        }

        pane = new ImageView(image);
        BorderPane bp = new BorderPane(pane);
        bp.prefWidthProperty().bind(window.widthProperty());
        bp.prefHeightProperty().bind(window.heightProperty());
        Scene scene = new Scene(bp);

        window.setScene(scene);

    }


}
