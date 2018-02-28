package RunwayRedeclarationTool.Models;

// Singleton that takes in Obstacle and Runway and returns an updated set of RunwayParameters
public class Calculator {
    public static final Calculator instance = new Calculator();
    private Calculator() {}

    public Calculator getInstance () {
        return instance;
    }

    public RunwayParameters calculate (Obstacle obstacle, VirtualRunway runway) {
        // TODO
        return new RunwayParameters(0,0,0,0);
    }
}
