package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.RunwaySide;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TopDownView extends RunwayView {
    protected boolean rotateView;

    public TopDownView(VirtualRunway runway, ObstaclePosition obstaclePosition, boolean rotateView) {
        super(runway, obstaclePosition);
        this.rotateView = rotateView;
    }

    /**
     * Draw a top-down view of the runway.
     */
    protected void draw() {
        double width = getWidth();
        double height = getHeight();
        GraphicsContext gc = getGraphicsContext2D();

        // Clear the canvas
        gc.clearRect(0, 0, width, height);

        if (rotateView) {
            this.setRotate(bearing - 90);
            this.setScaleX(0.0089*bearing+0.2);
        }

        drawClearedAndGradedArea(gc, leftSpace);

        // Draw runway surface
        gc.setFill(Color.web("333"));
        scaledFillRect(leftSpace, 125, TORA, 50);


        drawThresholdMarkers(gc);
        drawCentreLine(gc);
        drawDesignators(150, Color.WHITE);
        drawStopway(80);
        drawClearway(90);

        drawDisplacedThreshold(80);

    }

    /**
     * Draw the cleared and graded areas around the runway.
     *
     * @param gc     GraphicsContext for drawing.
     * @param offset the value to horizontally shift the drawing.
     */
    private void drawClearedAndGradedArea(GraphicsContext gc, int offset) {
        // Cleared and graded areas
        gc.setFill(Color.web("ccc"));
        gc.fillPolygon(
                new double[]{
                        scale_x(offset - 60),
                        scale_x(150 + offset),
                        scale_x(300 + offset),
                        scale_x(TORA + offset - 300),
                        scale_x(TORA + offset - 150),
                        scale_x(TORA + offset + 60),
                        scale_x(TORA + offset + 60),
                        scale_x(TORA + offset - 150),
                        scale_x(TORA + offset - 300),
                        scale_x(300 + offset),
                        scale_x(150 + offset),
                        scale_x(offset - 60)},
                new double[]{
                        scale_y(75),
                        scale_y(75),
                        scale_y(45),
                        scale_y(45),
                        scale_y(75),
                        scale_y(75),
                        scale_y(225),
                        scale_y(225),
                        scale_y(255),
                        scale_y(255),
                        scale_y(225),
                        scale_y(225)},
            12);
    }

    /**
     * Draw the threshold markers on the runway.
     *
     * @param gc GraphicsContext for drawing.
     */
    private void drawThresholdMarkers(GraphicsContext gc) {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(scale_y(2));

        for (int i = 130; i < 175; i += 5) {
            scaledStrokeLine(TORA / 30 + leftSpace, i, TORA * 3 / 30 + leftSpace, i);
            scaledStrokeLine(TORA * 27 / 30 + leftSpace, i, TORA * 29 / 30 + leftSpace, i);
        }
    }

    /**
     * Draw the runway centreline.
     *
     * @param gc GraphicsContext for drawing.
     */
    private void drawCentreLine(GraphicsContext gc) {
        gc.setLineWidth(scale_y(1));
        gc.setLineDashes(30);
        scaledStrokeLine(TORA / 6 + leftSpace, 150, TORA * 5 / 6 + leftSpace, 150);
        gc.setLineDashes(0);
    }

    /**
     * Draw the obstacle on the view.
     */
    public void drawObstacle() {
        try {
            draw();

            int obstacle_x = obstaclePosition.getDistLeftTSH() + leftSpace;
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

            drawBrokenDownDistances(obstacleLength, 210, 110);


            gc.setFill(Color.RED);
            gc.setGlobalAlpha(0.5);
            scaledFillRect(obstacle_x, obstacle_y - obstaclePosition.getWidth() / 2, TORA - obstaclePosition.getDistRightTSH() - obstaclePosition.getDistLeftTSH(), obstaclePosition.getWidth());
            gc.setGlobalAlpha(1.0);

            gc.setFill(Color.BLACK);
            scaledStrokeRect(obstacle_x, obstacle_y - obstaclePosition.getWidth() / 2, TORA - obstaclePosition.getDistRightTSH() - obstaclePosition.getDistLeftTSH(), obstaclePosition.getWidth());

        } catch (NullPointerException e) {
        }
    }
}