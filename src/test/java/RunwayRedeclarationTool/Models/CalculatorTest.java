package RunwayRedeclarationTool.Models;

import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class CalculatorTest {
    private Calculator calculator;
    private ArrayList<Runway> runways = new ArrayList<Runway>();

    @org.junit.Before
    public void setUp() throws Exception {
        calculator = Calculator.getInstance();

        //Heathrow
        VirtualRunway runway09R = new VirtualRunway("09R", new RunwayParameters(3660, 3660, 3660, 3353));
        VirtualRunway runway27L = new VirtualRunway("27L", new RunwayParameters(3660,3660,3660,3660));
        runways.add(new Runway(runway09R, runway27L));

        VirtualRunway runway09L = new VirtualRunway("09L", new RunwayParameters(3902,3902,3902,3595));
        VirtualRunway runway27R = new VirtualRunway("27R", new RunwayParameters(3884,3962, 3884,3884));
        runways.add(new Runway(runway09L, runway27R));
    }

    @Test
    public void scenario1() {
        Obstacle o = new Obstacle("scenario1", 12, -50, 3646, 0, RunwaySide.LEFT);

        try {
            Runway r = runways.get(1); // Runway 09L/27R
            calculator.calculate(o, r);

            // 09L
            RunwayParameters params = r.leftRunway.getRecalcParams();
            assertEquals(params.TORA, 3345);
            assertEquals(params.TORA, 3345);
            assertEquals(params.TORA, 3345);
            assertEquals(params.LDA, 2985);

            // 27R
            params = r.rightRunway.getRecalcParams();
            assertEquals(params.TORA, 2986);
            assertEquals(params.TODA,2986);
            assertEquals(params.ASDA,2986);
            assertEquals(params.LDA,3346);

        } catch (NoRedeclarationNeededException e) {
            fail("Calculator throws exception in the wrong situation.");
        } catch (NoDistancesAssignedException e) {
            fail("Calculator failed to assign redeclared distances to runways.");
        }
    }

    @Test
    public void scenario2() {
        Obstacle o = new Obstacle("scenario2", 25, 2853, 500, 20, RunwaySide.RIGHT);

        try {
            Runway r = runways.get(0); // Runway 09R/27L
            calculator.calculate(o, r);

            // 09R
            RunwayParameters params = r.leftRunway.getRecalcParams();
            assertEquals(params.TORA, 1850);
            assertEquals(params.TORA, 1850);
            assertEquals(params.TORA, 1850);
            assertEquals(params.LDA, 2553);

            // 27L
            params = r.rightRunway.getRecalcParams();
            assertEquals(params.TORA, 2860);
            assertEquals(params.TODA,2860);
            assertEquals(params.ASDA,2860);
            assertEquals(params.LDA,1850);

        } catch (NoRedeclarationNeededException e) {
            fail("Calculator throws exception in the wrong situation.");
        } catch (NoDistancesAssignedException e) {
            fail("Calculator failed to assign redeclared distances to runways.");
        }
    }

    @Test
    public void outsideInstrumentStrip() {
        ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
        obstacles.add(new Obstacle("outside", 5, -70, 3730, 1, RunwaySide.LEFT));
        obstacles.add(new Obstacle("outside", 5, 3730, -100, 1, RunwaySide.LEFT));
        obstacles.add(new Obstacle("outside", 5, 50, 3600, 76, RunwaySide.LEFT));

        for (Runway r : runways) {
            for (Obstacle o : obstacles) {
                try {
                    calculator.calculate(o, r);
                    fail("Expected exception for " + r.toString() + " and " + o.toString());
                } catch (NoRedeclarationNeededException e) {
                    assertTrue("Exception successfully thrown: " + e.getMessage(), true);
                }
            }
        }
    }
}