package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.RunwaySide;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class TopDownView extends RunwayRedeclarationTool.View.Canvas {
    private ObstaclePosition obstaclePosition;

    private boolean leftRunway;
    private int leftSpace = 60;     // either 60 if leftRunway, or clearway if it's a right virtual runway

    public TopDownView(VirtualRunway runway, ObstaclePosition obstaclePosition) {
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

        if(leftRunway){
            drawClearedAndGradedArea(gc, 0);
        } else{
            drawClearedAndGradedArea(gc, TORA-TODA+60);
        }

        // Draw runway surface
        gc.setFill(Color.web("333"));
        if (Integer.parseInt(runway.getDesignator().substring(0, 2)) <= 18) {   // left virtual runway
            leftRunway = true;
            scaledFillRect(60, 125, TORA, 50);

        } else {    // right virtual runway
            leftRunway = false;
            int clearway = TODA - TORA;
            leftSpace = Math.max(60, clearway);
            scaledFillRect(leftSpace, 125, TORA, 50);
        }

        drawThresholdMarkers(gc);
        drawCentreLine(gc);
        drawDesignators(leftSpace, 150);
        drawMapScale();
        drawTakeOffLandingDirection(gc);
        //drawScaleMarkings(gc);
        drawStopwayClearway(gc);
    }

    private void drawClearedAndGradedArea(GraphicsContext gc, int offset) {
        // Cleared and graded areas
        gc.setFill(Color.web("ccc"));
        gc.fillPolygon(
            new double[]{0 - scale_x(offset), scale_x(210 - offset), scale_x(360 - offset), scale_x(TORA - 240 - offset), scale_x(TORA - 90 - offset), scale_x(TORA + 120 - offset), scale_x(TORA + 120 - offset), scale_x(TORA - 90 - offset), scale_x(TORA - 240 - offset), scale_x(360 - offset), scale_x(210 - offset), 0 - scale_x(offset)},
            new double[]{scale_y(75), scale_y(75), scale_y(45), scale_y(45), scale_y(75), scale_y(75), scale_y(225), scale_y(225), scale_y(255), scale_y(255), scale_y(225), scale_y(225)},
            12);
    }

    private void drawThresholdMarkers(GraphicsContext gc) {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(scale_y(2));

        for (int i = 130; i < 175; i += 5) {
            scaledStrokeLine(TORA / 30 + leftSpace, i, TORA * 3 / 30 + leftSpace, i);
            scaledStrokeLine(TORA * 27 / 30 + leftSpace, i, TORA * 29 / 30 + leftSpace, i);
        }
    }

    private void drawCentreLine(GraphicsContext gc) {
        gc.setLineWidth(scale_y(1));
        gc.setLineDashes(30);
        scaledStrokeLine(TORA/6 + leftSpace, 150, TORA*5/6 +leftSpace, 150);
        gc.setLineDashes(0);
    }

    private void drawTakeOffLandingDirection(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(scale_y(0.5));
        gc.setFont(Font.font("Consolas", 14));
        scaledStrokeLine(60, 20, 560, 20);
        if(!leftRunway){
            scaledStrokeLine(60, 20, 100, 16);
            scaledStrokeLine(60, 20, 100, 24);
        } else {
            scaledStrokeLine(560, 20, 520, 16);
            scaledStrokeLine(560, 20, 520, 24);
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Take-off/landing direction", scale_x(60), scale_y(10));
    }

    private void drawScaleMarkings(GraphicsContext gc) {
        // TORA
        scaledStrokeLine(60, 195, runway.getOrigParams().getTORA() + 60, 195);
        scaledStrokeLine(60, 193, 60, 197);
        scaledStrokeLine(TORA + 60, 193, TORA + 60, 197);
        gc.fillText("TORA: " + TORA + "m", scale_x(60), scale_y(191));
    }

    private void drawStopwayClearway(GraphicsContext gc) {
        int stopway = ASDA - TORA;
        int clearway = TODA - TORA;

        if (stopway != 0) {
            if (leftRunway) {
                drawMeasuringLine(TORA+60, ASDA + 60, 200, "stopway");
            } else {
                drawMeasuringLine(clearway - stopway, clearway, 200, "stopway");
            }

            // Assumption: clearway >= stopway (Heathrow slides)
            if (clearway != stopway) {
                if (leftRunway) {
                    drawMeasuringLine(TORA+60, TODA+60+clearway, 210, "clearway");
                } else {
                    drawMeasuringLine(0, clearway, 210, "clearway");
                }
            }
        }
    }

    public void drawObstacle() {
        try {
            draw();
            double width = getWidth();
            double height = getHeight();

            GraphicsContext gc = getGraphicsContext2D();

            int obstacle_x = obstaclePosition.getDistLeftTSH() + 60;
            int obstacle_y;

            if (Integer.parseInt(runway.getDesignator().substring(0, 2)) > 18) {
                if (obstaclePosition.getRunwaySide() == RunwaySide.LEFT) {
                    obstacle_y = 150 + obstaclePosition.getDistFromCL();
                } else {
                    obstacle_y = 150 - obstaclePosition.getDistFromCL();
                }
            } else {
                if (obstaclePosition.getRunwaySide() == RunwaySide.LEFT) {
                    obstacle_y = 150 - obstaclePosition.getDistFromCL();
                } else {
                    obstacle_y = 150 + obstaclePosition.getDistFromCL();
                }
            }

            gc.setFill(Color.RED);
            gc.setGlobalAlpha(0.5);
            scaledFillRect(obstacle_x, obstacle_y - obstaclePosition.getWidth() / 2, TORA - obstaclePosition.getDistRightTSH() - obstaclePosition.getDistLeftTSH(), obstaclePosition.getWidth());
            gc.setGlobalAlpha(1.0);
        } catch (NullPointerException e) {
        }
    }
}