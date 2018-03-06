package RunwayRedeclarationTool.Models;

public class Obstacle {
    private String name;
    private int height;

    public Obstacle(String name, int height) {
        this.name = name;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return name + " (" + height + "m)";
    }
}
