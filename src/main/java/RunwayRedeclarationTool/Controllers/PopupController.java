package RunwayRedeclarationTool.Controllers;

import RunwayRedeclarationTool.View.CanvasPopup;
import RunwayRedeclarationTool.View.RunwayView;
import RunwayRedeclarationTool.View.SideOnView;
import RunwayRedeclarationTool.View.TopDownView;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class PopupController {


    private ArrayList<CanvasPopup> side_popups;
    private ArrayList<CanvasPopup> top_popups;

    public PopupController(){
        this.side_popups = new ArrayList<>();
        this.top_popups = new ArrayList<>();
    }



    public void newPopup(RunwayView runwayView){

        CanvasPopup popup;

        // We store runways in a seperate array - this allows us to refresh them independently and display popups of both
        // the side on view and top down view at the same time.
        if(runwayView instanceof SideOnView){
            popup = new CanvasPopup("Side on view - " + runwayView.getVirtualRunway().toString());
            side_popups.add(popup);
        } else {
            popup = new CanvasPopup("Top down view - " + runwayView.getVirtualRunway().toString());
            top_popups.add(popup);
        }


        // Each window represents an imageview of the Main Top/Side down view canvas.
        // Evreytime the main canvas updates, so do the images.
        // This seems to scale very well.
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image snapshot = runwayView.snapshot(params, null);

        popup.display(snapshot);

    }


    /**
     * Rerender the imageviews for all existing top and side view windows.
     * @param side
     * @param top
     */
    public void redrawAll(SideOnView side, TopDownView top){


        // Re render images for both side and top down view.
        // we only render each image once.
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image side_snapshot = side.snapshot(params, null);
        Image top_snapshot = top.snapshot(params, null);

        for(CanvasPopup side_popup : side_popups){
            side_popup.redraw(side_snapshot);
        }

        for(CanvasPopup top_popup : top_popups){
            top_popup.redraw(top_snapshot);
        }
    }
}
