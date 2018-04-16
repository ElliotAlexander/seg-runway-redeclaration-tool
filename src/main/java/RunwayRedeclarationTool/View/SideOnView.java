package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class SideOnView extends RunwayRedeclarationTool.View.Canvas {
    private ObstaclePosition obstaclePosition;

    private boolean leftRunway = true;
    private int leftSpace = 60;

    public SideOnView(VirtualRunway runway, ObstaclePosition obstaclePosition) {
        super(runway);

        this.obstaclePosition = obstaclePosition;

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
        GraphicsContext gc = getGraphicsContext2D();

        // Fill canvas with grey
        gc.setFill(Color.web("ddd"));
        gc.fillRect(0, 0, width, height);

        // Draw runway surface
        gc.setFill(Color.web("333"));
        if (Integer.parseInt(runway.getDesignator().substring(0, 2)) > 18) {
            leftRunway = false;
            leftSpace = Math.max(60, TODA-TORA);
        }

        scaledFillRect(leftSpace, 149, TORA, 2);

        drawDesignators(leftSpace, 170);
        drawStopwayClearway(gc);
        drawMapScale();
    }

    private void drawStopwayClearway(GraphicsContext gc) {
        int stopway = ASDA - TORA;
        int clearway = TODA - TORA;

        if (stopway != 0) {
            if (leftRunway) {
                drawMeasuringLine(TORA + 60, TORA + 60 + stopway, 180,"stopway");
            } else {
                drawMeasuringLine(clearway - stopway, clearway, 180, "stopway");
            }

            // Assumption: clearway >= stopway (Heathrow slides)
            if (clearway != stopway) {
                if (leftRunway) {
                    drawMeasuringLine(TORA + 60,TORA+60 +clearway, 190, "clearway");
                } else {
                    drawMeasuringLine(0, clearway, 190, "clearway");
                }
            }
        }
    }

    public void drawObstacle() {
        try {
            draw();
            double width = getWidth();

            GraphicsContext gc = getGraphicsContext2D();

            int obstacleLength = runway.getOrigParams().getTORA() - obstaclePosition.getDistRightTSH() - obstaclePosition.getDistLeftTSH();

            gc.setFill(Color.RED);
            gc.setGlobalAlpha(0.5);
            scaledFillRect(obstaclePosition.getDistLeftTSH() + 60, 149 - obstaclePosition.getObstacle().getHeight(), obstacleLength, obstaclePosition.getObstacle().getHeight());
            gc.setGlobalAlpha(1.0);

            drawBrokenDownDistances(obstacleLength);

        } catch (NullPointerException e) {
        }

    }

    public void drawBrokenDownDistances(int oLength) {
        GraphicsContext gc = getGraphicsContext2D();
        int slopecalc = 0;

        try {
            slopecalc = runway.getRecalcParams().getSlopeCalculation();
        } catch (AttributeNotAssignedException e) {
            // Shouldn't happen
            System.err.println("Recalculated parameters not assigned!");;
        }

        if (obstaclePosition.getDistLeftTSH() < obstaclePosition.getDistRightTSH()) {
            if (leftRunway) {
                // __|=|__->______


            } else {
                // __|=|__<-______
            }
        } else {
            if (leftRunway) {
                // ______->__|=|__
            } else {
                // ______<-__|=|__
            }
        }

//            drawMeasuringLine(gc, 60, 200, obstaclePosition.getDistLeftTSH() + 60, 200, Integer.toString(obstaclePosition.getDistLeftTSH()) + "m");
//            drawMeasuringLine(gc, obstaclePosition.getDistLeftTSH() + 60, 200, obstaclePosition.getDistLeftTSH() + 60 + oLength, 200, "Obstacle");
//            drawMeasuringLine(gc, obstaclePosition.getDistLeftTSH() + 60 + oLength, 200, obstaclePosition.getDistLeftTSH() + 60 + oLength + slopecalc, 200, "slope");
//            drawMeasuringLine(gc, obstaclePosition.getDistLeftTSH() + 60 + oLength, 200, obstaclePosition.getDistLeftTSH() + 60 + oLength + obstaclePosition.getDistRightTSH(), 200, Integer.toString(obstaclePosition.getDistRightTSH()) + "m");

    }
}