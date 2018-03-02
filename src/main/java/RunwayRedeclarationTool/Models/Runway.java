package RunwayRedeclarationTool.Models;

// Models a physical runway and holds two VirtualRunways (for the two orientations)
public class Runway {
    public VirtualRunway leftRunway;
    public VirtualRunway rightRunway;

    public Runway(VirtualRunway leftRunway, VirtualRunway rightRunway) {
        this.leftRunway = leftRunway;
        this.rightRunway = rightRunway;
    }

    public String toString() {
        return "Runway " + leftRunway.getDesignator() + "/" + rightRunway.getDesignator();
    }
}
