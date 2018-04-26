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

    /*
        Must be set before setting an instance of this class to the redeclared distances of a virtual runway.
        It will be used to draw the broken down distances on the display and the TOCS/ALS slope.
     */
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
