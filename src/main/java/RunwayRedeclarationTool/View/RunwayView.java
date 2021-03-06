package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public abstract class RunwayView extends javafx.scene.canvas.Canvas {
    protected VirtualRunway runway;
    protected int TORA, ASDA, TODA, LDA;

    protected boolean leftRunway;
    protected int leftSpace;
    protected int padding;

    protected double bearing;

    protected ObstaclePosition obstaclePosition;

    protected GraphicsContext gc;

    /**
     * Parent class of top-down and side-on view classes.
     *
     * @param runway           the runway to draw.
     * @param obstaclePosition the position of the obstacle.
     */
    public RunwayView(VirtualRunway runway, ObstaclePosition obstaclePosition) {
        this.runway = runway;
        this.obstaclePosition = obstaclePosition;

        if(runway == null){
            return;
        }
        RunwayParameters p = runway.getOrigParams();
        this.TORA = p.getTORA();
        this.ASDA = p.getASDA();
        this.TODA = p.getTODA();
        this.LDA = p.getLDA();

        padding = TORA / 10;

        if (Integer.parseInt(runway.getDesignator().substring(0, 2)) <= 18) {   // left virtual runway
            leftRunway = true;
            leftSpace = 0;
        } else {
            leftRunway = false;
            leftSpace = Math.max(TODA, ASDA) - TORA;    // the largest out of clearway and stopway
        }


        double designator = Integer.parseInt(runway.getDesignator().substring(0, 2));

        if (designator <= 18) {
            bearing = designator * 10;
        } else {
            bearing = (designator - 18) * 10;
        }

        gc = getGraphicsContext2D();

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

    /**
     * Draw a measuring line to display the stopway.
     *
     * @param y the height at which to draw the measuring line.
     */
    protected void drawStopway(int y) {
        int stopway = ASDA - TORA;

        if (stopway != 0) {
            if (leftRunway) {
                drawMeasuringLine(TORA, stopway, y, "Stopway");
            } else {
                drawMeasuringLine(leftSpace - stopway, stopway, y, "Stopway");
            }
        }
    }

    /**
     * Draw a measuring line to display the clearway.
     *
     * @param y the height at which to draw the measuring line.
     */
    protected void drawClearway(int y) {
        int clearway = TODA - TORA;

        if (clearway != 0) {
            if (leftRunway) {
                drawMeasuringLine(TORA, clearway, y, "Clearway");
            } else {
                drawMeasuringLine(leftSpace - clearway, clearway, y, "Clearway");
            }
        }
    }

    /**
     * Draw the runway designators on both ends of the runway.
     *
     * @param y         the height at which to draw the designators.
     * @param textColor the colour to draw the designators in.
     */
    protected void drawDesignators(int y, Color textColor) {
        gc.setFill(textColor);
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
            gc.fillText(designator1, scale_x(TORA / 7 + leftSpace), scale_y(y));
            gc.fillText(designator2, scale_x(TORA * 6 / 7 + leftSpace), scale_y(y));
        } else {
            gc.fillText(designator2, scale_x(TORA / 7 + leftSpace), scale_y(y));
            gc.fillText(designator1, scale_x(TORA * 6 / 7 + leftSpace), scale_y(y));
        }
    }

    /**
     * Display an indication of the take-off and landing direction.
     */
    protected void drawTakeOffLandingDirection() {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(scale_y(0.5));
        gc.setFont(Font.font("Consolas", 14));
        gc.setTextAlign(TextAlignment.RIGHT);
        if (!leftRunway) {
            gc.fillText("Take-off/landing direction: \uD83E\uDC78", getWidth() - scale_x(60), scale_y(290));
        } else {
            gc.fillText("Take-off/landing direction: \uD83E\uDC7A", getWidth() - scale_x(60), scale_y(290));
        }
    }

    /**
     * Draw a breakdown of the distances relating to the object.
     *
     * @param oLength the length of the obstacle.
     */
    protected void drawBrokenDownDistances(int oLength, int yDistances, int yObstacle) {

        if(obstaclePosition == null){
            Logger.Log(Logger.Level.WARNING, "Obstacle Position is not yet set, and this method has been called in error");
            Logger.Log(Logger.Level.WARNING, "Skipping drawBrokenDownDistances. ");
            return;
        }

        RunwayParameters recalcParams;

        try {
             recalcParams = runway.getRecalcParams();
        } catch(AttributeNotAssignedException e){
            return;
        }

        int rTORA = recalcParams.getTORA();
        int rLDA = recalcParams.getLDA();

        int slopecalc = recalcParams.getSlopeCalculation();
        int displacedThs = TORA - LDA; //original values

        if (obstaclePosition.getDistLeftTSH() < obstaclePosition.getDistRightTSH()) {
            if (obstaclePosition.getDistLeftTSH() > 0) {
                drawMeasuringLine(leftSpace, obstaclePosition.getDistLeftTSH(), yObstacle, Integer.toString(obstaclePosition.getDistLeftTSH()) + "m");
            }
            drawMeasuringLine(obstaclePosition.getDistLeftTSH() + leftSpace, oLength, yObstacle, "Obstacle");
            int endObstacle = leftSpace + obstaclePosition.getDistLeftTSH() + oLength;

            if (leftRunway) {
                // __|=|__->______
                drawMeasuringLine(endObstacle, 300, yDistances, "blast protection");
                drawTORA_TODA_ASDA(endObstacle + 300, yDistances, recalcParams);

                // length = slopecalc + 60 where 60 is the strip end
                drawMeasuringLine(endObstacle, slopecalc + 60, yDistances + 10, "slope + strip end");
                drawMeasuringLine(endObstacle + slopecalc + 60, rLDA, yDistances + 10, "LDA " + rLDA + "m");
            } else {
                // __|=|__<-______
                drawMeasuringLine(endObstacle, slopecalc + 60, yDistances, "slope + strip end");
                drawTORA_TODA_ASDA(endObstacle + slopecalc + 60, yDistances, recalcParams);

                drawMeasuringLine(endObstacle, 300, yDistances + 10, "RESA + strip end");
                drawMeasuringLine(endObstacle + 300, rLDA, yDistances + 10, "LDA " + rLDA + "m");
            }
        } else {
            int startObstacle = leftSpace + obstaclePosition.getDistLeftTSH();

            if (leftRunway) {
                // ______->__|=|__
                drawTORA_TODA_ASDA(0, yDistances, recalcParams);
                drawMeasuringLine(rTORA, slopecalc + 60, yDistances, "strip end + slope");

                drawMeasuringLine(displacedThs, rLDA, yDistances + 10, "LDA " + rLDA + "m");
                drawMeasuringLine(displacedThs + rLDA, 300, yDistances + 10, "strip end + RESA");
            } else {
                // ______<-__|=|__
                drawTORA_TODA_ASDA(leftSpace, yDistances, recalcParams);
                drawMeasuringLine(leftSpace + rTORA, 300, yDistances, "blast protection");

                drawMeasuringLine(leftSpace, rLDA, yDistances + 10, "LDA " + rLDA + "m");
                drawMeasuringLine(leftSpace + rLDA, slopecalc + 60, yDistances + 10, "strip end + slope");
            }

            drawMeasuringLine(startObstacle, oLength, yObstacle, "Obstacle");
            if (obstaclePosition.getDistRightTSH() > 0) {
                drawMeasuringLine(startObstacle + oLength, obstaclePosition.getDistRightTSH(), yObstacle, obstaclePosition.getDistRightTSH() + "m");
            }
        }
    }

    /**
     * Draw the TORA, TODA and ASDA of the runway.
     *
     * @param x
     * @param recalcParams
     */
    private void drawTORA_TODA_ASDA(int x, int y, RunwayParameters recalcParams) {
        int rTORA = recalcParams.getTORA();
        int rTODA = recalcParams.getTODA();
        int rASDA = recalcParams.getASDA();

        drawMeasuringLine(x, rTORA, y, "TORA " + rTORA + "m");

        if (rTODA != rTORA) {
            drawMeasuringLine(x, rTODA, y - 10, "TODA " + rTODA + "m");
        } else {
            gc.fillText("TODA " + rTODA + "m", scale_x(x + rTODA / 2), scale_y(y - 10));
        }
        if (rASDA != rTORA && rASDA != rTODA) {
            drawMeasuringLine(x, rASDA, y - 20, "ASDA " + rASDA + "m");
        } else {
            gc.fillText("ASDA " + rASDA + "m", scale_x(x + rASDA / 2), scale_y(y - 20));
        }
    }

    /**
     * Draw the diaplaced threshold marker.
     *
     * @param y the height at which to draw the marker.
     */
    protected void drawDisplacedThreshold(int y) {
        int displacedTsh = TORA - LDA;

        if (displacedTsh > 0) {
            if (leftRunway) {
                drawMeasuringLine(0, displacedTsh, y, "Displaced TSH");
            } else {
                drawMeasuringLine(leftSpace + LDA, displacedTsh, y, "Displaced TSH");
            }
        }
    }

    /**
     * Draw a scale on the views.
     */
    protected void drawMapScale() {
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

    /**
     * Draw a horizontal measuring line on the view.
     *
     * @param x      the start position of the line
     * @param length the length of the line.
     * @param y      the height at which to draw the line.
     * @param text   the text to label the line with.
     */
    protected void drawMeasuringLine(int x, int length, int y, String text) {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(scale_y(0.5));
        gc.setFont(Font.font("Consolas", 14));
        gc.setTextAlign(TextAlignment.CENTER);

        scaledStrokeLine(x, y, x + length, y);
        scaledStrokeLine(x, y + 2, x, y - 2);
        scaledStrokeLine(x + length, y + 2, x + length, y - 2);
        gc.fillText(text, scale_x(x + length / 2), scale_y(y - 5));
    }

    /**
     * Draw a scaled line on the view.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    protected void scaledStrokeLine(double x1, double y1, double x2, double y2) {
        gc.strokeLine(scale_x(x1), scale_y(y1), scale_x(x2), scale_y(y2));

    }

    /**
     * Draw a scaled filled rectangle on the view.
     *
     * @param x
     * @param y
     * @param w
     * @param h
     */
    protected void scaledFillRect(double x, double y, double w, double h) {
        gc.fillRect(scale_x(x), scale_y(y), scale_x(w - padding), scale_y(h));
    }

    /**
     * Draw a scaled outline of a rectangle on the view.
     *
     * @param x
     * @param y
     * @param w
     * @param h
     */
    protected void scaledStrokeRect(double x, double y, double w, double h) {
        gc.strokeRect(scale_x(x), scale_y(y), scale_x(w - padding), scale_y(h));
    }

    /**
     * Scale distances in metres to percentage width on the views.
     *
     * @param length the length to scale.
     * @return the scaled length.
     */
    protected double scale_x(double length) {
        double maxWidth = Math.max(TODA, ASDA) + 2 * padding;
        return (length + padding) * getWidth() / maxWidth;
    }

    /**
     * Scale distances in metres to percentage height on the views.
     *
     * @param length the length to scale.
     * @return the scaled length.
     */
    protected double scale_y(double length) {
        return length * getHeight() / 300;
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

    protected abstract void draw();

    public abstract void drawObstacle();


    public VirtualRunway getVirtualRunway(){
        return runway;
    }
}
