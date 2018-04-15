package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class SideOnView extends Canvas {
    private VirtualRunway runway;
    private ObstaclePosition obstaclePosition;

    private int TORA, ASDA, TODA, LDA;
    private int leftSpace = 60;

    public SideOnView(VirtualRunway runway, ObstaclePosition obstaclePosition) {
        this.runway = runway;
        this.obstaclePosition = obstaclePosition;

        this.TORA = runway.getOrigParams().getTORA();
        this.ASDA = runway.getOrigParams().getASDA();
        this.TODA = runway.getOrigParams().getTODA();
        this.LDA = runway.getOrigParams().getLDA();

        widthProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
                draw();
                drawObstacle(obstaclePosition);
            }
        });
        heightProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
                draw();
                drawObstacle(obstaclePosition);
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
            leftSpace = Math.max(60, TODA-TORA);
        }

        scaledFillRect(leftSpace, 149, TORA, 2);

        drawDesignators(gc);
        drawMapScale(gc);
    }

    private void drawDesignators(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Consolas", 24));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        String num = runway.getDesignator().substring(0, 2);
        String designator1 = num + "\n" + runway.getDesignator().substring(2, runway.getDesignator().length());

        int oppositeNum = Integer.parseInt(num) + 18;
        if (oppositeNum > 36) {
            oppositeNum -= 36;
        }
        String designator2 = String.format("%02d", oppositeNum);

        String letter = runway.getDesignator().substring(2, runway.getDesignator().length());
        if (letter.equals("L")) {
            designator2 += "\nR";
        } else if (letter.equals("R")) {
            designator2 += "\nL";
        } else {
            designator2 += "\n" + letter;
        }

        if (Integer.parseInt(runway.getDesignator().substring(0, 2)) <= 18) {
            gc.fillText(designator1, scale_x(TORA / 7 + leftSpace), scale_y(170));
            gc.fillText(designator2, scale_x(TORA * 6 / 7 + leftSpace), scale_y(170));
        } else {
            gc.fillText(designator2, scale_x(TORA / 7 + leftSpace), scale_y(170));
            gc.fillText(designator1, scale_x(TORA * 6 / 7 + leftSpace), scale_y(170));
        }
    }

    private void drawMapScale(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(scale_y(0.5));
        gc.setFont(Font.font("Consolas", 16));
        scaledStrokeLine(60, 290, 560, 290);
        scaledStrokeLine(60, 292, 60, 288);
        scaledStrokeLine(560, 292, 560, 288);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("500m", scale_x(60), scale_y(285));
    }

    public void drawObstacle(ObstaclePosition obstaclePosition) {
        try {
            draw();
            double width = getWidth();

            GraphicsContext gc = getGraphicsContext2D();

            int obstacleLength = runway.getOrigParams().getTORA() - obstaclePosition.getDistRightTSH() - obstaclePosition.getDistLeftTSH();

            gc.setFill(Color.RED);
            gc.setGlobalAlpha(0.5);
            scaledFillRect(obstaclePosition.getDistLeftTSH() + 60, 149 - obstaclePosition.getObstacle().getHeight(), obstacleLength, obstaclePosition.getObstacle().getHeight());
            gc.setGlobalAlpha(1.0);

            drawMeasuringLine(gc, 60, 200, obstaclePosition.getDistLeftTSH() + 60, 200, Integer.toString(obstaclePosition.getDistLeftTSH()) + "m");
            drawMeasuringLine(gc, obstaclePosition.getDistLeftTSH() + 60, 200, obstaclePosition.getDistLeftTSH() + 60 + obstacleLength, 200, "Obstacle");
            drawMeasuringLine(gc, obstaclePosition.getDistLeftTSH() + 60 + obstacleLength, 200, obstaclePosition.getDistLeftTSH() + 60 + obstacleLength + obstaclePosition.getDistRightTSH(), 200, Integer.toString(obstaclePosition.getDistRightTSH()) + "m");


        } catch (NullPointerException e) {
        }

    }

    private void drawMeasuringLine(GraphicsContext gc, double x1, double y1, double x2, double y2, String text) {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(scale_y(0.5));
        gc.setFont(Font.font("Consolas", 16));
        scaledStrokeLine(x1, y1, x2, y2);
        scaledStrokeLine(x1, y1 + 2, x1, y2 - 2);
        scaledStrokeLine(x2, y1 + 2, x2, y2 - 2);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(text, scale_x((x1 + x2) / 2), scale_y(y1 - 5));
    }

    private double scale_x(double length) {
        return length / (runway.getOrigParams().getTORA() + 120) * getWidth();
    }

    private double scale_y(double length) {
        return length / 300 * getHeight();
    }

    private void scaledStrokeLine(double x1, double y1, double x2, double y2) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.strokeLine(scale_x(x1), scale_y(y1), scale_x(x2), scale_y(y2));
    }

    private void scaledFillRect(double x, double y, double w, double h) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.fillRect(scale_x(x), scale_y(y), scale_x(w), scale_y(h));
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
}