package RunwayRedeclarationTool.Models;

// Holds TORA, TODA, ASDA, LDA and whatever else needed
public class RunwayParameters {
    protected int TORA, TODA, ASDA, LDA;
    protected int slopeCalculation = 0;

    public RunwayParameters (int TORA, int TODA, int ASDA, int LDA) {
        this.TORA = TORA;
        this.TODA = TODA;
        this.ASDA = ASDA;
        this.LDA = LDA;
    }

    public void setSlopeCalculation(int slopeCalculation) {
        this.slopeCalculation = slopeCalculation;
    }

    public int getSlopeCalculation() {
        return slopeCalculation;
    }

    public int getTORA() {
        return TORA;
    }

    public int getTODA() {
        return TODA;
    }

    public int getASDA() {
        return ASDA;
    }

    public int getLDA() {
        return LDA;
    }
}
