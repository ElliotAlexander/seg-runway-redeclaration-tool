package RunwayRedeclarationTool.View;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AboutPopup {

    public static void display() {
        final Stage window = new Stage();
        window.initModality(Modality.NONE);
        window.setTitle("About our project");

        Text t1 = new Text("Runway Re-declaration Tool\n");
        Text t2 = new Text("A project by\nKeshav Kaushal\nAdelaida Creosteanu\nElliot Alexander\nChauncey Hertzog Fraser\n\n");
        Text t3 = new Text("Commercial airports are busy places. Ideally runways will be fully open at all times, but this is " +
                "not always possible. When there is an obstruction (such as a broken down aircraft or surface " +
                "damage) on the runway, it may need to be closed. However it may still be possible to keep the " +
                "runway open, albeit with reduced distances available for landing and taking off. " +
                "All runways have a published set of parameters. \n\nWhen an obstacle is present on the runway, " +
                "these parameters must be recalculated and a commercial decision made whether to continue " +
                "operations on the runway. If (limited) operations are to continue, the published data about the " +
                "runway must be recalculated and republished. The final decision about whether to land/take off " +
                "rests with the pilot.");
        t1.setFont(Font.font("Helvetica", FontWeight.BOLD, 30));
        t2.setFont(Font.font("Helvetica", 20));
        t2.setTextAlignment(TextAlignment.CENTER);
        t3.setFont(Font.font("Helvetica", 16));

        TextFlow flow = new TextFlow(t1, t2, t3);
        flow.setPadding(new Insets(25));
        Scene scene = new Scene(flow, 700, 500);
        window.setScene(scene);
        window.showAndWait();

    }

}
