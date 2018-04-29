package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class StaticElements extends RunwayView {

    private GraphicsContext gc;

    public StaticElements(VirtualRunway runway, ObstaclePosition obstaclePosition, boolean rotateView) {
        super(runway, obstaclePosition, rotateView);

    }

    protected void draw() {
        double width = getWidth();
        double height = getHeight();
        gc = getGraphicsContext2D();
        gc.setFill(Color.web("ddd"));
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.setFont(Font.font("Consolas", 16));
        scaledStrokeLine(60, 290, 560, 290);
        scaledStrokeLine(60, 292, 60, 288);
        scaledStrokeLine(560, 292, 560, 288);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("500m", scale_x(60), scale_y(285));

        drawTakeOffLandingDirection();
    }

    // TODO: Bad. Must implement method but doesn't have anything to do with obstacles.
    public void drawObstacle(){}
}
