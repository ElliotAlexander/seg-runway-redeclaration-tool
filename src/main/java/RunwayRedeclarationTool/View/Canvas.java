package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.RunwayParameters;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Canvas extends javafx.scene.canvas.Canvas{
    protected VirtualRunway runway;
    protected boolean leftRunway;
    protected int TORA, ASDA, TODA, LDA;

    protected GraphicsContext gc;

    public Canvas (VirtualRunway runway) {
        this.runway = runway;

        RunwayParameters p = runway.getOrigParams();
        this.TORA = p.getTORA();
        this.ASDA = p.getASDA();
        this.TODA = p.getTODA();
        this.LDA = p.getLDA();

        if (Integer.parseInt(runway.getDesignator().substring(0, 2)) <= 18) {   // left virtual runway
            leftRunway = true;
        } else {
            leftRunway = false;
        }

        gc = getGraphicsContext2D();
    }

    protected void drawDesignators(int leftSpace, int y) {
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
            gc.fillText(designator1, scale_x(TORA / 7 + leftSpace), scale_y(y));
            gc.fillText(designator2, scale_x(TORA * 6 / 7 + leftSpace), scale_y(y));
        } else {
            gc.fillText(designator2, scale_x(TORA / 7 + leftSpace), scale_y(y));
            gc.fillText(designator1, scale_x(TORA * 6 / 7 + leftSpace), scale_y(y));
        }
    }

    protected void drawTakeOffLandingDirection() {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(scale_y(0.5));
        gc.setFont(Font.font("Consolas", 14));

        scaledStrokeLine(60, 20, 560, 20);
        if (!leftRunway) {
            scaledStrokeLine(60, 20, 100, 16);
            scaledStrokeLine(60, 20, 100, 24);
        } else {
            scaledStrokeLine(560, 20, 520, 16);
            scaledStrokeLine(560, 20, 520, 24);
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Take-off/landing direction", scale_x(60), scale_y(10));
    }

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

    protected void drawMeasuringLine(int x, int length, int y, String text) {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(scale_y(0.5));
        gc.setFont(Font.font("Consolas", 16));
        gc.setTextAlign(TextAlignment.CENTER);

        scaledStrokeLine(x, y, x + length, y);
        scaledStrokeLine(x, y + 2, x, y - 2);
        scaledStrokeLine(x + length, y + 2, x + length, y - 2);
        gc.fillText(text, scale_x(x + length / 2), scale_y(y - 5));
    }

    protected double scale_x(double length) {
        return length / (TODA + 60) * getWidth();
    }

    protected double scale_y(double length) {
        return length / 300 * getHeight();
    }

    protected void scaledStrokeLine(double x1, double y1, double x2, double y2) {
        gc.strokeLine(scale_x(x1), scale_y(y1), scale_x(x2), scale_y(y2));
    }

    protected void scaledFillRect(double x, double y, double w, double h) {
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
