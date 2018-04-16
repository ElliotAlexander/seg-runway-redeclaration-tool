package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SideOnView extends RunwayRedeclarationTool.View.Canvas {
    private ObstaclePosition obstaclePosition;

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

            drawBrokenDownDistances(obstacleLength);

        } catch (NullPointerException e) {
            // TODO: this is bad practice. Make sure that there is an object when this method is called
        } catch (AttributeNotAssignedException e) {
            e.printStackTrace();
            System.err.println("Recalculated parameters have not been assigned!");
        }

    }

    public void drawBrokenDownDistances(int oLength) throws AttributeNotAssignedException {
        RunwayParameters params = runway.getRecalcParams();

        int rTORA = params.getTORA();
        int rLDA = params.getLDA();
        // maybe use distFromRightTSH() instead of recalculated values?
        int slopecalc = params.getSlopeCalculation();
        int displacedThs = TORA - LDA; //original values



        if (obstaclePosition.getDistLeftTSH() < obstaclePosition.getDistRightTSH()) {
            drawMeasuringLine(60, obstaclePosition.getDistLeftTSH(), 200, Integer.toString(obstaclePosition.getDistLeftTSH()) + "m");
            int endObstacle = 60 + obstaclePosition.getDistLeftTSH() + oLength;
            drawMeasuringLine(obstaclePosition.getDistLeftTSH() + 60, oLength, 200, "Obstacle");

            if (leftRunway) {
                // __|=|__->______
                drawMeasuringLine(endObstacle, 300, 200, "blast protection");
                drawMeasuringLine(endObstacle + 300, rTORA, 200, "TORA " + rTORA + "m");

                drawMeasuringLine(endObstacle, slopecalc, 210, "slope offset");
                drawMeasuringLine(endObstacle + slopecalc, rLDA, 210, "LDA " + rLDA + "m");
            } else {
                // __|=|__<-______
                drawMeasuringLine(endObstacle, slopecalc, 200, "slope offset");
                drawMeasuringLine(endObstacle + slopecalc, rTORA, 200, "TORA " + rTORA + "m");

                drawMeasuringLine(endObstacle, 300, 210, "RESA + strip end");
                drawMeasuringLine(endObstacle + 300, rLDA, 210, "LDA " + rLDA + "m");
            }
        } else {
            int startObstacle = leftSpace + obstaclePosition.getDistLeftTSH();

            if (leftRunway) {
                // ______->__|=|__
                drawMeasuringLine(60, rTORA, 200, "TORA " + rTORA + "m");
                drawMeasuringLine(60 + rTORA, slopecalc, 200, "slope offset");

                if (displacedThs > 0) {
                    drawMeasuringLine(60, displacedThs, 210, "displaced TSH");
                }
                drawMeasuringLine(60 + displacedThs, rLDA, 210, "LDA " + rLDA + "m");
                drawMeasuringLine(60 + displacedThs + rLDA, 300, 210, "strip end + RESA");
            } else {
                // ______<-__|=|__
                drawMeasuringLine(leftSpace, rTORA, 200, "TORA " + rTORA + "m");
                drawMeasuringLine(leftSpace + rTORA, 300, 200, "blast protection");

                drawMeasuringLine(leftSpace, rLDA, 210, "LDA " + rLDA + "m");
                drawMeasuringLine(leftSpace + rLDA, slopecalc, 210, "slope offset");
            }

            drawMeasuringLine(startObstacle, oLength, 200, "Obstacle");
            drawMeasuringLine(startObstacle + oLength, obstaclePosition.getDistRightTSH(), 200, obstaclePosition.getDistRightTSH() + "m");
        }

//
//
//
//            drawMeasuringLine(gc, obstaclePosition.getDistLeftTSH() + 60 + oLength, 200, obstaclePosition.getDistLeftTSH() + 60 + oLength + obstaclePosition.getDistRightTSH(), 200, Integer.toString(obstaclePosition.getDistRightTSH()) + "m");

    }
}