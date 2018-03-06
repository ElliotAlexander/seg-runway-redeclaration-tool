package RunwayRedeclarationTool.View;

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

    public TopDownView(VirtualRunway runway) {
        this.runway = runway;
        widthProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
                draw();
            }
        });
        heightProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
                draw();
            }
        });
        draw();
    }

    private double scale_x(double length){
        return length / (runway.getOrigParams().getTORA() + 120) * getWidth();
    }
    private double scale_y(double length){
        return length / 300 * getHeight();
    }

    private void draw() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(Color.web("DDDDDD"));
        gc.fillRect(0, 0, width, height);

        // Cleared and graded areas
        gc.setFill(Color.web("AAAAAA"));
        gc.fillPolygon(
                new double[]{0, scale_x(210), scale_x(360), scale_x(runway.getOrigParams().getTORA()-240), scale_x(runway.getOrigParams().getTORA()-90), scale_x(runway.getOrigParams().getTORA()+120), scale_x(runway.getOrigParams().getTORA()+120),  scale_x(runway.getOrigParams().getTORA()-90), scale_x(runway.getOrigParams().getTORA()-240), scale_x(360), scale_x(210), 0},
                new double[]{scale_y(75), scale_y(75), scale_y(45), scale_y(45), scale_y(75), scale_y(75), scale_y(225), scale_y(225), scale_y(255), scale_y(255), scale_y(225), scale_y(225)}, 12);

        // Runway and outline
        gc.setFill(Color.WHITE);
        scaledFillRect(40, 124, runway.getOrigParams().getTORA()+40, 52);

        gc.setFill(Color.web("333333"));
        scaledFillRect(60, 125, runway.getOrigParams().getTORA(), 50);

        // Threshold markers
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(scale_y(2));
        for(int i = 130; i<175; i+=5){
            scaledStrokeLine(120, i, 520, i);
            scaledStrokeLine(runway.getOrigParams().getTORA(), i, runway.getOrigParams().getTORA()-400, i);
        }

        // C/L
        gc.setLineWidth(scale_y(1));
        gc.setLineDashes(15);
        scaledStrokeLine(850, 150, runway.getOrigParams().getTORA()-730, 150);
        gc.setLineDashes(0);

        drawDesignators(gc);

        // Map scale
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(scale_y(0.5));
        gc.setFont(Font.font("Consolas", 16));
        scaledStrokeLine(60, 290, 560, 290);
        scaledStrokeLine(60, 292, 60, 288);
        scaledStrokeLine(560, 292, 560, 288);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("500m", scale_x(60), scale_y(285));

//        // TORA
//        scaledStrokeLine(60, 195, runway.getOrigParams().getTORA()+60, 195);
//        scaledStrokeLine(60, 193, 60, 197);
//        scaledStrokeLine(runway.getOrigParams().getTORA()+60, 193, runway.getOrigParams().getTORA()+60, 197);
//        gc.fillText("TORA: " + runway.getOrigParams().getTORA() + "m", scale_x(60), scale_y(191));

    }

    private void scaledStrokeLine(double x1, double y1, double x2, double y2){
        GraphicsContext gc = getGraphicsContext2D();
        gc.strokeLine(scale_x(x1), scale_y(y1), scale_x(x2), scale_y(y2));
    }

    private void scaledFillRect(double x, double y, double w, double h){
        GraphicsContext gc = getGraphicsContext2D();
        gc.fillRect(scale_x(x), scale_y(y), scale_x(w), scale_y(h));
    }

    private void drawDesignators(GraphicsContext gc){
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", 24));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        String designator1 = runway.getDesignator().substring(0,2) + "\n" + runway.getDesignator().substring(2,runway.getDesignator().length());
        String designator2 = String.format("%02d", 36 - Integer.parseInt(runway.getDesignator().substring(0,2)));

        if (runway.getDesignator().substring(2,runway.getDesignator().length()).equals("L")){
            designator2 += "\nR";
        } else if (runway.getDesignator().substring(2,runway.getDesignator().length()).equals("R")) {
            designator2 += "\nL";
        } else {
            designator2 += "\n" + runway.getDesignator().substring(2,runway.getDesignator().length());
        }

        if(Integer.parseInt(runway.getDesignator().substring(0,2)) < 36 - Integer.parseInt(runway.getDesignator().substring(0,2))){
            gc.fillText(designator1, scale_x(685), scale_y(150));
            gc.fillText(designator2, scale_x(runway.getOrigParams().getTORA() - 565), scale_y(150));
        } else {
            gc.fillText(designator2, scale_x(685), scale_y(150));
            gc.fillText(designator1, scale_x(runway.getOrigParams().getTORA() - 565), scale_y(150));
        }
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