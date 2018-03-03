package RunwayRedeclarationTool.Models;

// Holds TORA, TODA, ASDA, LDA and whatever else needed
public class RunwayParameters {
    protected int TORA, TODA, ASDA, LDA;

    public RunwayParameters (int TORA, int TODA, int ASDA, int LDA) {
        this.TORA = TORA;
        this.TODA = TODA;
        this.ASDA = ASDA;
        this.LDA = LDA;
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
