package RunwayRedeclarationTool.Views;

import RunwayRedeclarationTool.Models.Runway;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TopDownView extends Canvas {
    public Runway runway;

    public TopDownView(Runway runway) {
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

    private double rescale_x(double length){
        //return length/5120*getWidth();
        return length / (runway.leftRunway.getOrigParams().getTORA() + 120) * getWidth();
    }

    private double rescale_y(double length){
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
        gc.fillPolygon(new double[]{0, rescale_x(210), rescale_x(360), rescale_x(runway.leftRunway.getOrigParams().getTORA()-240), rescale_x(runway.leftRunway.getOrigParams().getTORA()-90), rescale_x(runway.leftRunway.getOrigParams().getTORA()+120), rescale_x(runway.leftRunway.getOrigParams().getTORA()+120),  rescale_x(runway.leftRunway.getOrigParams().getTORA()-90), rescale_x(runway.leftRunway.getOrigParams().getTORA()-240), rescale_x(360), rescale_x(210), 0},
                new double[]{rescale_y(75), rescale_y(75), rescale_y(45), rescale_y(45), rescale_y(75), rescale_y(75), rescale_y(225), rescale_y(225), rescale_y(255), rescale_y(255), rescale_y(225), rescale_y(225)}, 12);

        // draw the runway
        gc.setFill(Color.web("333333"));
        gc.fillRect(rescale_x(60), rescale_y(125),rescale_x(runway.leftRunway.getOrigParams().getTORA()), rescale_y(50));

        // draw the threshold markers
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(rescale_y(2));
        for(int i = 130; i<175; i+=5){
            gc.strokeLine(rescale_x(120), rescale_y(i),rescale_x(520), rescale_y(i));
            gc.strokeLine(rescale_x(runway.leftRunway.getOrigParams().getTORA()), rescale_y(i),rescale_x(runway.leftRunway.getOrigParams().getTORA()-400), rescale_y(i));
        }

        // draw C/L
        gc.setLineWidth(rescale_y(1));
        gc.setLineDashes(15);
        gc.strokeLine(rescale_x(850), rescale_y(150), rescale_x(runway.leftRunway.getOrigParams().getTORA()-730), rescale_y(150));
        gc.setLineDashes(0);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", 24));
        gc.fillText(runway.toString(), rescale_x(100), rescale_y(20));

        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(rescale_y(0.5));
        gc.setFont(Font.font("Consolas", 16));
        gc.strokeLine(rescale_x(100), rescale_y(280), rescale_x(600), rescale_y(280));
        gc.strokeLine(rescale_x(100), rescale_y(278), rescale_x(100), rescale_y(282));
        gc.strokeLine(rescale_x(600), rescale_y(278), rescale_x(600), rescale_y(282));
        gc.fillText("500m", rescale_x(100), rescale_y(276));

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