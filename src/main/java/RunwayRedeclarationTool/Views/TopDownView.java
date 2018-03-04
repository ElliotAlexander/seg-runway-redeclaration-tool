package RunwayRedeclarationTool.Views;

import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TopDownView extends Canvas {
    public VirtualRunway runway;

    public TopDownView(VirtualRunway runway) {
        this.runway = runway;
        draw();

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
    }

    private double scale_x(double length){
        return length / (runway.getOrigParams().getTORA() + 120) * getWidth();
    }

    private double scale_y(double length){
        return length / 300 * getHeight();
    }

    public void draw() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();

        // draw the background
        gc.setFill(Color.web("cccccc"));
        gc.fillRect(0, 0, width, height);

        // draw the cleared and graded area
        gc.setFill(Color.web("aaaaaa"));
        gc.fillPolygon(new double[]{0, scale_x(210), scale_x(360), scale_x(runway.getOrigParams().getTORA()-240), scale_x(runway.getOrigParams().getTORA()-90), scale_x(runway.getOrigParams().getTORA()+120), scale_x(runway.getOrigParams().getTORA()+120),  scale_x(runway.getOrigParams().getTORA()-90), scale_x(runway.getOrigParams().getTORA()-240), scale_x(360), scale_x(210), 0},
                new double[]{scale_y(75), scale_y(75), scale_y(45), scale_y(45), scale_y(75), scale_y(75), scale_y(225), scale_y(225), scale_y(255), scale_y(255), scale_y(225), scale_y(225)}, 12);

        // draw the runway
        gc.setFill(Color.WHITE);
        gc.fillRect(scale_x(40), scale_y(124), scale_x(runway.getOrigParams().getTORA()+40), scale_y(52));
        gc.setFill(Color.web("333333"));
        gc.fillRect(scale_x(60), scale_y(125), scale_x(runway.getOrigParams().getTORA()), scale_y(50));

        // draw the threshold markers
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(scale_y(2));
        for(int i = 130; i<175; i+=5){
            gc.strokeLine(scale_x(120), scale_y(i), scale_x(520), scale_y(i));
            gc.strokeLine(scale_x(runway.getOrigParams().getTORA()), scale_y(i), scale_x(runway.getOrigParams().getTORA()-400), scale_y(i));
        }

        // draw C/L
        gc.setLineWidth(scale_y(1));
        gc.setLineDashes(15);
        gc.strokeLine(scale_x(850), scale_y(150), scale_x(runway.getOrigParams().getTORA()-730), scale_y(150));
        gc.setLineDashes(0);

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Consolas", 24));
        gc.fillText(runway.getDesignator(), scale_x(100), scale_y(20));

        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(scale_y(0.5));
        gc.setFont(Font.font("Consolas", 16));
        gc.strokeLine(scale_x(100), scale_y(280), scale_x(600), scale_y(280));
        gc.strokeLine(scale_x(100), scale_y(278), scale_x(100), scale_y(282));
        gc.strokeLine(scale_x(600), scale_y(278), scale_x(600), scale_y(282));
        gc.fillText("500m", scale_x(100), scale_y(276));

        gc.strokeLine(scale_x(60), scale_y(195), scale_x(runway.getOrigParams().getTORA()+60), scale_y(195));
        gc.strokeLine(scale_x(60), scale_y(193), scale_x(60), scale_y(197));
        gc.strokeLine(scale_x(runway.getOrigParams().getTORA()+60), scale_y(193), scale_x(runway.getOrigParams().getTORA()+60), scale_y(197));
        gc.fillText("TORA: " + runway.getOrigParams().getTORA() + "m", scale_x(60), scale_y(191));

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