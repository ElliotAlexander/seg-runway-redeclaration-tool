package RunwayRedeclarationTool.Models;

// Holds TORA, TODA, ASDA, LDA and whatever else needed
public class RunwayParameters {
    protected int TORA, TODA, ASDA, LDA, displacedThreshold;

    public RunwayParameters (int TORA, int TODA, int ASDA, int LDA, int displacedThreshold) {
        this.TORA = TORA;
        this.TODA = TODA;
        this.ASDA = ASDA;
        this.LDA = LDA;
        this.displacedThreshold = displacedThreshold;
    }
}
