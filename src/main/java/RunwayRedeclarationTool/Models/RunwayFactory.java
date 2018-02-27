package RunwayRedeclarationTool.Models;

// Singleton class that creates Runways
public class RunwayFactory {
    public static final RunwayFactory instance = new RunwayFactory();

    private RunwayFactory() {

    }
}
