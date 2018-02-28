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
}
