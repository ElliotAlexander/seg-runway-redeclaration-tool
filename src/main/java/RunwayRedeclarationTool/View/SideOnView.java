package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.scene.paint.Color;

public class SideOnView extends RunwayView {

    public SideOnView(VirtualRunway runway, ObstaclePosition obstaclePosition) {
        super(runway, obstaclePosition);
    }

    /**
     * Draw a side-on view of the runway.
     */
    protected void draw() {
        double width = getWidth();
        double height = getHeight();

        // Fill canvas with grey
        gc.setFill(Color.web("ddd"));
        gc.fillRect(0, 0, width, height);

        // Draw runway surface
        gc.setFill(Color.web("333"));


        scaledFillRect(leftSpace, 149, TORA, 2);

        drawDesignators(170, Color.BLACK);
        drawStopway(100);
        drawClearway(110);

        drawDisplacedThreshold(100);
        drawMapScale();
        drawTakeOffLandingDirection();
    }

    /**
     * Draw an obstacle on the runway.
     */
    public void drawObstacle() {
        try {
            draw();

            int obstacleLength = runway.getOrigParams().getTORA() - obstaclePosition.getDistRightTSH() - obstaclePosition.getDistLeftTSH();

            gc.setFill(Color.RED);
            gc.setGlobalAlpha(0.5);
            scaledFillRect(obstaclePosition.getDistLeftTSH() + leftSpace, 149 - obstaclePosition.getObstacle().getHeight(), obstacleLength, obstaclePosition.getObstacle().getHeight());
            gc.setGlobalAlpha(1.0);


            // TODO Add a button for this
            drawBrokenDownDistances(obstacleLength, 200);

        } catch (NullPointerException e) {
            // TODO: this is bad practice. Make sure that there is an object when this method is called
        } catch (AttributeNotAssignedException e) {
            e.printStackTrace();
            System.err.println("Recalculated parameters have not been assigned!");
        }

    }

}