package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.scene.paint.Color;

public class SideOnView extends RunwayView {

    public SideOnView(VirtualRunway runway, ObstaclePosition obstaclePosition, boolean rotateView) {
        super(runway, obstaclePosition, rotateView);
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
            int oHeight = obstaclePosition.getObstacle().getHeight();

            gc.setFill(Color.RED);
            gc.setGlobalAlpha(0.5);
            scaledFillRect(obstaclePosition.getDistLeftTSH() + leftSpace, 149 - oHeight, obstacleLength, oHeight);
            gc.setGlobalAlpha(1.0);

            gc.setFill(Color.BLACK);
            gc.strokeRect(scale_x(obstaclePosition.getDistLeftTSH() + leftSpace), scale_y(149 - oHeight), scale_x(obstacleLength), scale_y(oHeight));

            int slopecalc = runway.getRecalcParams().getSlopeCalculation();

            drawSlope(obstacleLength, slopecalc);

            // TODO Add a button for this
            drawBrokenDownDistances(obstacleLength, 200);

        } catch (NullPointerException e) {
            // TODO: this is bad practice. Make sure that there is an object when this method is called
        } catch (AttributeNotAssignedException e) {
            e.printStackTrace();
            System.err.println("Recalculated parameters have not been assigned!");
        }

    }

    /**
     * Draw the TOCS/ALS slope that the plane needs to ascend/descend to safely fly over the obstacle.
     */
    private void drawSlope(int oLength, int slopecalc) {
        gc.setFill(Color.BLUE);
        gc.setGlobalAlpha(0.5);

        int oHeight = obstaclePosition.getObstacle().getHeight();

        if (obstaclePosition.getDistLeftTSH() < obstaclePosition.getDistRightTSH()) {
            if (leftRunway) {
                // __|=|__->______
                int endObstacle = leftSpace + obstaclePosition.getDistLeftTSH() + oLength;

                gc.fillPolygon(new double[]{scale_x(endObstacle), scale_x(endObstacle), scale_x(endObstacle + slopecalc)}, new double[]{scale_y(149 - oHeight), scale_y(149), scale_y(149)}, 3);
            }
        } else {
            if (!leftRunway) {
                // ______<-__|=|__
                int startObstacle = leftSpace + obstaclePosition.getDistLeftTSH();

                gc.fillPolygon(new double[]{scale_x(startObstacle - slopecalc), scale_x(startObstacle), scale_x(startObstacle)}, new double[]{scale_y(149), scale_y(149), scale_y(149 - oHeight)}, 3);
            }
        }

        gc.setGlobalAlpha(1.0);
    }

}