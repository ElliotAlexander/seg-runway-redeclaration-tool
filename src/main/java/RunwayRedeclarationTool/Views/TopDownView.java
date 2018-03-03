package RunwayRedeclarationTool.Views;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class TopDownView extends Canvas {

    public TopDownView() {
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

    private double rescalex(double length){
        return length/5120*getWidth();
    }

    private double rescaley(double length){
        return length/300*getHeight();
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
        gc.fillPolygon(new double[]{0, rescalex(210), rescalex(360), rescalex(4760), rescalex(4910), rescalex(5120), rescalex(5120),  rescalex(4910), rescalex(4760), rescalex(360), rescalex(210), 0},
                new double[]{rescaley(75), rescaley(75), rescaley(45), rescaley(45), rescaley(75), rescaley(75), rescaley(225), rescaley(225), rescaley(255), rescaley(255), rescaley(225), rescaley(225)}, 12);

        // draw the runway
        gc.setFill(Color.web("333333"));
        gc.fillRect(rescalex(60), rescaley(125),rescalex(5000), rescaley(50));

        // draw the threshold markers
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(rescaley(2));
        for(int i = 130; i<175; i+=5){
            gc.strokeLine(rescalex(120), rescaley(i),rescalex(500), rescaley(i));
            gc.strokeLine(rescalex(5000), rescaley(i),rescalex(4620), rescaley(i));
        }

        gc.setLineWidth(rescaley(1));
        gc.setLineDashes(40);
        gc.strokeLine(rescalex(850), rescaley(150), rescalex(4270),rescaley(150));
        gc.setLineDashes(0);

        gc.setFill(Color.web("ffffff"));
        gc.setFont(Font.font(null, FontWeight.BOLD, rescalex(150)));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("09", rescalex(650), rescaley(138));
        gc.fillText("R", rescalex(650), rescaley(162));
        gc.fillText("27", rescalex(4470), rescaley(138));
        gc.fillText("L", rescalex(4470), rescaley(162));
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