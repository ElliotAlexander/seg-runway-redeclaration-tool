package RunwayRedeclarationTool.Models;

// Holds values about the dimension and positioning of the runway obstruction
public class Obstacle {
    private String name;
    private int height, distLeftTSH, distRightTSH, distFromCL;
    private RunwaySide runwaySide; // needed for display, but not for calculations

    public Obstacle(String name, int height, int distLeftTSH, int distRightTSH, int distFromCL, RunwaySide runwaySide) {
        this.name = name;
        this.height = height;
        this.distLeftTSH = distLeftTSH;
        this.distRightTSH = distRightTSH;
        this.distFromCL = distFromCL;
        this.runwaySide = runwaySide;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int getDistLeftTSH() {
        return distLeftTSH;
    }

    public int getDistRightTSH() {
        return distRightTSH;
    }

    public int getDistFromCL() {
        return distFromCL;
    }

    public RunwaySide getRunwaySide() {
        return runwaySide;
    }

    @Override
    public String toString() {
        return "Obstacle{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", distLeftTSH=" + distLeftTSH +
                ", distRightTSH=" + distRightTSH +
                ", distFromCL=" + distFromCL +
                ", runwaySide=" + runwaySide +
                '}';
    }
}
