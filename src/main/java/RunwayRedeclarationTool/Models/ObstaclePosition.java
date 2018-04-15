package RunwayRedeclarationTool.Models;

// Holds values about the dimension and positioning of the runway obstruction
public class ObstaclePosition {
    private Obstacle obstacle;
    private int distLeftTSH, distRightTSH, width, distFromCL;
    private RunwaySide runwaySide; // needed for display, but not for calculations

    public ObstaclePosition(Obstacle obstacle, int distLeftTSH, int distRightTSH, int width, int distFromCL, RunwaySide runwaySide) {
        this.obstacle = obstacle;
        this.distLeftTSH = distLeftTSH;
        this.distRightTSH = distRightTSH;
        this.width = width;
        this.distFromCL = distFromCL;
        this.runwaySide = runwaySide;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public int getDistLeftTSH() {
        return distLeftTSH;
    }

    public int getDistRightTSH() {
        return distRightTSH;
    }

    public int getWidth(){return width;}

    public int getDistFromCL() {
        return distFromCL;
    }

    public RunwaySide getRunwaySide() {
        return runwaySide;
    }

    @Override
    public String toString() {
        return "ObstaclePosition{" +
                "name='" + obstacle.getName() + '\'' +
                ", height=" + obstacle.getHeight() +
                ", distLeftTSH=" + distLeftTSH +
                ", distRightTSH=" + distRightTSH +
                ", width=" + width +
                ", distFromCL=" + distFromCL +
                ", runwaySide=" + runwaySide +
                '}';
    }
}
