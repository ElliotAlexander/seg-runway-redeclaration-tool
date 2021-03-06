package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;

// Holds original and recalculated values of a runway (there's one for both orientations of a physical runway)
public class VirtualRunway {
    private String designator;
    private RunwayParameters origParams;
    private RunwayParameters recalcParams = null;
    private String recalcBreakdown = null;

    public VirtualRunway(String designator, RunwayParameters parameters) {
        this.designator = designator;
        this.origParams = parameters;
    }

    public String toString(){
        return "Runway " + designator;
    }

    public String getDesignator() {
        return designator;
    }

    public RunwayParameters getOrigParams() {
        return origParams;
    }

    public RunwayParameters getRecalcParams() throws AttributeNotAssignedException {
        if (recalcParams == null) {
            throw new AttributeNotAssignedException();
        }
        return recalcParams;
    }

    public void setRecalcParams(RunwayParameters recalcParams, int slopecalc) {
        recalcParams.setSlopeCalculation(slopecalc);
        this.recalcParams = recalcParams;
    }

    public String getRecalcBreakdown() throws AttributeNotAssignedException {
        if (recalcBreakdown == null) {
            throw new AttributeNotAssignedException();
        }
        return recalcBreakdown;
    }

    public void setRecalcBreakdown(String recalcBreakdown) {
        this.recalcBreakdown = recalcBreakdown;
    }
}
