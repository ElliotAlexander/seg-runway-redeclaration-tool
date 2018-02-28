package RunwayRedeclarationTool.Models;

enum RunwaySide { LEFT, RIGHT }

// Holds values about the dimension and positioning of the runway obstruction
public class Obstacle {
    private String name;
    private int height, distFromTSH, distFromCL;
    private RunwaySide runwaySide; // needed for display, but not for calculations

    public Obstacle(String name, int height, int distFromTSH, int distFromCL, RunwaySide runwaySide) {
        this.name = name;
        this.height = height;
        this.distFromTSH = distFromTSH;
        this.distFromCL = distFromCL;
        this.runwaySide = runwaySide;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int getDistFromTSH() {
        return distFromTSH;
    }

    public int getDistFromCL() {
        return distFromCL;
    }

    public RunwaySide getRunwaySide() {
        return runwaySide;
    }
}
