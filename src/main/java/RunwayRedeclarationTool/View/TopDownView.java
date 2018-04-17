package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.RunwaySide;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TopDownView extends RunwayView {

    public TopDownView(VirtualRunway runway, ObstaclePosition obstaclePosition) {
        super(runway, obstaclePosition);
    }

    protected void draw() {
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
        if (leftRunway) {
            scaledFillRect(60, 125, TORA, 50);
        } else {
            scaledFillRect(leftSpace, 125, TORA, 50);
        }


        drawThresholdMarkers(gc);
        drawCentreLine(gc);
        drawDesignators(150);
        drawMapScale();
        drawTakeOffLandingDirection();
        //drawScaleMarkings(gc);
        drawStopway(200);
        drawClearway(210);

        drawDisplacedThreshold(250);

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

    private void drawScaleMarkings(GraphicsContext gc) {
        // TORA
        scaledStrokeLine(60, 195, runway.getOrigParams().getTORA() + 60, 195);
        scaledStrokeLine(60, 193, 60, 197);
        scaledStrokeLine(TORA + 60, 193, TORA + 60, 197);
        gc.fillText("TORA: " + TORA + "m", scale_x(60), scale_y(191));
    }

    public void drawObstacle() {
        try {
            draw();

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



            int obstacleLength = runway.getOrigParams().getTORA() - obstaclePosition.getDistRightTSH() - obstaclePosition.getDistLeftTSH();
            try {
                drawBrokenDownDistances(obstacleLength);
            } catch (AttributeNotAssignedException e) {
                e.printStackTrace();
            }

            gc.setFill(Color.RED);
            gc.setGlobalAlpha(0.5);
            scaledFillRect(obstacle_x, obstacle_y - obstaclePosition.getWidth() / 2, TORA - obstaclePosition.getDistRightTSH() - obstaclePosition.getDistLeftTSH(), obstaclePosition.getWidth());
            gc.setGlobalAlpha(1.0);
        } catch (NullPointerException e) {
        }
    }
}