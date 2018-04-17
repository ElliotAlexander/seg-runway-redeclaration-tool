package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SideOnView extends RunwayView {

    public SideOnView(VirtualRunway runway, ObstaclePosition obstaclePosition) {
        super(runway, obstaclePosition);

        widthProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
                draw();
                drawObstacle();
            }
        });
        heightProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
                draw();
                drawObstacle();
            }
        });
        draw();
    }

    private void draw() {
        double width = getWidth();
        double height = getHeight();

        // Fill canvas with grey
        gc.setFill(Color.web("ddd"));
        gc.fillRect(0, 0, width, height);

        // Draw runway surface
        gc.setFill(Color.web("333"));
        if (!leftRunway) {
            leftSpace = Math.max(60, TODA-TORA);
        }

        scaledFillRect(leftSpace, 149, TORA, 2);

        drawDesignators(leftSpace, 170);
        drawStopwayClearway(gc);
        drawMapScale();
        drawTakeOffLandingDirection();
    }

    private void drawStopwayClearway(GraphicsContext gc) {
        int stopway = ASDA - TORA;
        int clearway = TODA - TORA;

        if (stopway != 0) {
            if (leftRunway) {
                drawMeasuringLine(TORA + 60, stopway, 180, "stopway");
            } else {
                drawMeasuringLine(clearway - stopway, stopway, 180, "stopway");
            }

            // Assumption: clearway >= stopway (Heathrow slides)
            if (clearway != stopway) {
                if (leftRunway) {
                    drawMeasuringLine(TORA + 60, clearway, 190, "clearway");
                } else {
                    drawMeasuringLine(0, clearway, 190, "clearway");
                }
            }
        }
    }

    public void drawObstacle() {
        try {
            draw();

            int obstacleLength = runway.getOrigParams().getTORA() - obstaclePosition.getDistRightTSH() - obstaclePosition.getDistLeftTSH();

            gc.setFill(Color.RED);
            gc.setGlobalAlpha(0.5);
            scaledFillRect(obstaclePosition.getDistLeftTSH() + 60, 149 - obstaclePosition.getObstacle().getHeight(), obstacleLength, obstaclePosition.getObstacle().getHeight());
            gc.setGlobalAlpha(1.0);


            // TODO Add a button for this
            drawBrokenDownDistances(obstacleLength);

        } catch (NullPointerException e) {
            // TODO: this is bad practice. Make sure that there is an object when this method is called
        } catch (AttributeNotAssignedException e) {
            e.printStackTrace();
            System.err.println("Recalculated parameters have not been assigned!");
        }

    }

}