package RunwayRedeclarationTool.Models;

// Singleton class that creates Runways
public class RunwayFactory {
    public static final RunwayFactory instance = new RunwayFactory();
    private RunwayFactory () {}

    public static RunwayFactory getInstance () {
        return instance;
    }

//    public Runway getRunway(String designator, int TORA, int TODA, int ASDA, int LDA, int displacedThreshold) {
//        // DO WE NEED TO PERFORM ANY CHECKS ON INPUT?
//        RunwayParameters params = new RunwayParameters(TORA, TODA, ASDA, LDA, displacedThreshold);
//    }
}
