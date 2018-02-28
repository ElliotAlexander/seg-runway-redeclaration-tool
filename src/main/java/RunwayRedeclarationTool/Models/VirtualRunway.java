package RunwayRedeclarationTool.Models;

// Holds original and recalculated values of a runway (there's one for both orientations of a physical runway)
public class VirtualRunway {
    private String designator;
    private RunwayParameters origParams;
    private RunwayParameters recalcParams = null;

    public VirtualRunway(String designator, RunwayParameters parameters) {
        this.designator = designator;
        this.origParams = parameters;
    }

    public String getDesignator() {
        return designator;
    }

    public RunwayParameters getOrigParams() {
        return origParams;
    }

    public RunwayParameters getRecalcParams() throws Exception {
        if (recalcParams == null) {
            throw new Exception("Recalculated parameters were not calculated or assigned!");
        }
        return recalcParams;
    }
}
