package RunwayRedeclarationTool.View;

import RunwayRedeclarationTool.Models.ObstaclePosition;
import RunwayRedeclarationTool.Models.VirtualRunway;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * For drawing elements on the top-down view that do not change when the runway is rotated.
 */
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

        drawMapScale();
        drawTakeOffLandingDirection();
        drawNorthArrow();
    }

    /**
     * Draw an arrow on the top-down view to show the direction of North.
     */
    private void drawNorthArrow(){
        Image compass  = new Image(this.getClass().getClassLoader().getResourceAsStream("compass.png"));
//        double designator = Integer.parseInt(runway.getDesignator().substring(0, 2));
//        double bearing;
//
//        if (designator < 18) {
//            bearing = designator * 10;
//        } else {
//            bearing = (36 - designator) * 10;
//        }
//
//        Rotate rotate = new Rotate();
//        rotate.pivotXProperty().setValue(50);
//        rotate.pivotYProperty().setValue(50);
//
//        if(rotateView){
//            gc.drawImage(compass, scale_x(0), scale_y(0));
//        } else {
//            gc.rotate(40);
//            gc.drawImage(compass, scale_x(0), scale_y(0), 30, 30);
//            gc.rotate(-40);
//        }
    }

    // TODO: Bad. Must implement method but doesn't have anything to do with obstacles.
    public void drawObstacle(){}
}
