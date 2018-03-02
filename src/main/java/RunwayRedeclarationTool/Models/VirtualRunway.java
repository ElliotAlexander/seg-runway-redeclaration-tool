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

    public RunwayParameters getRecalcParams() throws NoDistancesAssignedException {
        if (recalcParams == null) {
            throw new NoDistancesAssignedException();
        }
        return recalcParams;
    }

    public void setRecalcParams(RunwayParameters recalcParams) {
        this.recalcParams = recalcParams;
    }
}
