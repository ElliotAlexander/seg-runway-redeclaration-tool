package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.RunwaySide;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class TopDownView extends Canvas {
    private VirtualRunway runway;
    private ObstaclePosition obstaclePosition;

    private boolean leftRunway;
    private int leftSpace = 60;     // either 60 if leftRunway, or clearway if it's a right virtual runway
    private int TORA, ASDA, TODA, LDA;

    public TopDownView(VirtualRunway runway, ObstaclePosition obstaclePosition) {
        this.runway = runway;
        this.obstaclePosition = obstaclePosition;

        this.TORA = runway.getOrigParams().getTORA();
        this.ASDA = runway.getOrigParams().getASDA();
        this.TODA = runway.getOrigParams().getTODA();
        this.LDA = runway.getOrigParams().getLDA();

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
        drawDesignators(gc);
        drawMapScale(gc);
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

    private void drawDesignators(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
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
            gc.fillText(designator1, scale_x(TORA / 7 + leftSpace), scale_y(150));
            gc.fillText(designator2, scale_x(TORA * 6 / 7 + leftSpace), scale_y(150));
        } else {
            gc.fillText(designator2, scale_x(TORA / 7 + leftSpace), scale_y(150));
            gc.fillText(designator1, scale_x(TORA * 6 / 7 + leftSpace), scale_y(150));
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
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(scale_y(0.5));
        gc.setFont(Font.font("Consolas", 14));
        gc.setTextAlign(TextAlignment.CENTER);

        int stopway = ASDA - TORA;
        int clearway = TODA - TORA;

        if (stopway != 0) {
            if (leftRunway) {
                scaledStrokeLine(TORA + 60, 200, ASDA + 60, 200);
                scaledStrokeLine(TORA + 60, 198, TORA + 60, 202);
                scaledStrokeLine(ASDA + 60, 198, ASDA + 60, 202);
                gc.fillText("stopway", scale_x(TORA + 60 + stopway / 2), scale_y(205));
            } else {
                scaledStrokeLine(clearway - stopway, 200, clearway, 200);
                scaledStrokeLine(clearway - stopway, 198, clearway - stopway, 202);
                scaledStrokeLine(clearway, 198, clearway, 202);
                gc.fillText("stopway", scale_x(clearway - stopway / 2), scale_y(205));
            }

            // Assumption: clearway >= stopway (Heathrow slides)
            if (clearway != stopway) {
                if (leftRunway) {
                    scaledStrokeLine(TORA + 60, 210, TODA + 60, 210);
                    scaledStrokeLine(TORA + 60, 208, TORA + 60, 212);
                    scaledStrokeLine(TODA + 60, 208, TODA + 60, 212);
                    gc.fillText("clearway", scale_x(TORA + 60 + clearway / 2), scale_y(215));
                } else {
                    scaledStrokeLine(0, 210, clearway, 210);
                    scaledStrokeLine(0, 208, 0, 212);
                    scaledStrokeLine(clearway, 208, clearway, 212);
                    gc.fillText("clearway", scale_x(clearway / 2), scale_y(215));
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

    private double scale_x(double length) {
        return length / (TODA + 60) * getWidth();
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