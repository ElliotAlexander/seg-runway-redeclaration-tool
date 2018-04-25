package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Exceptions.NoRedeclarationNeededException;
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
        VirtualRunway runway09R = new VirtualRunway("09R", new RunwayParameters(3658, 3658, 3658, 3350));
        VirtualRunway runway27L = new VirtualRunway("27L", new RunwayParameters(3658, 3658, 3658, 3658));
        runways.add(new Runway(runway09R, runway27L));

        //Gatwick
        VirtualRunway runway08L = new VirtualRunway("08L", new RunwayParameters(2565, 3040, 2565, 2243));
        VirtualRunway runway26R = new VirtualRunway("26R", new RunwayParameters(2565, 2703, 2565, 2148));
        runways.add(new Runway(runway08L, runway26R));
    }

    @Test
    public void scenario1() {
        ObstaclePosition o = new ObstaclePosition(new Obstacle("scenario1", 25), 2853, 500, 10, 20, RunwaySide.RIGHT);

        try {
            Runway r = runways.get(0); // Runway 09R/27L
            calculator.calculate(o, r);

            // 09R
            RunwayParameters params = r.leftRunway.getRecalcParams();
            assertEquals(params.TORA, 1851);
            assertEquals(params.TORA, 1851);
            assertEquals(params.TORA, 1851);
            assertEquals(params.LDA, 2553);
            System.out.println(r.leftRunway.getRecalcBreakdown() + "\n");

            // 27L
            params = r.rightRunway.getRecalcParams();
            System.out.println(r.rightRunway.getRecalcBreakdown() + "\n");
            assertEquals(params.TORA, 2861);
            assertEquals(params.TODA, 2861);
            assertEquals(params.ASDA, 2861);
            assertEquals(params.LDA, 1851);

        } catch (NoRedeclarationNeededException e) {
            fail("Calculator throws exception in the wrong situation.");
        } catch (AttributeNotAssignedException e) {
            fail("Calculator failed to assign redeclared distances and breakdown to runways.");
        }
    }

    @Test
    public void scenario2() {
        ObstaclePosition o = new ObstaclePosition(new Obstacle("scenario2", 15), 150, 3203, 10, 60, RunwaySide.RIGHT);

        try {
            Runway r = runways.get(0); // Runway 09R/27L
            calculator.calculate(o, r);

            // 09R
            RunwayParameters params = r.leftRunway.getRecalcParams();
            System.out.println(r.leftRunway.getRecalcBreakdown() + "\n");
            assertEquals(params.TORA, 2903);
            assertEquals(params.TORA, 2903);
            assertEquals(params.TORA, 2903);
            assertEquals(params.LDA, 2393);

            // 27L
            params = r.rightRunway.getRecalcParams();
            System.out.println(r.rightRunway.getRecalcBreakdown() + "\n");
            assertEquals(params.TORA, 2393);
            assertEquals(params.TODA,2393);
            assertEquals(params.ASDA,2393);
            assertEquals(params.LDA,2903);

        } catch (NoRedeclarationNeededException e) {
            fail("Calculator throws exception in the wrong situation.");
        } catch (AttributeNotAssignedException e) {
            fail("Calculator failed to assign redeclared distances and breakdown to runways.");
        }
    }

    @Test
    public void scenario3() {
        ObstaclePosition o = new ObstaclePosition(new Obstacle("scenario4", 20), 1606, 198, 10, 20, RunwaySide.RIGHT);

        try {
            Runway r = runways.get(1); // Runway 08L/26R
            calculator.calculate(o, r);

            // 09L
            RunwayParameters params = r.leftRunway.getRecalcParams();
            System.out.println(r.leftRunway.getRecalcBreakdown() + "\n");
            assertEquals(params.TORA, 868);
            assertEquals(params.TORA, 868);
            assertEquals(params.TORA, 868);
            assertEquals(params.LDA, 1306);

            // 27R
            params = r.rightRunway.getRecalcParams();
            System.out.println(r.rightRunway.getRecalcBreakdown() + "\n");
            assertEquals(params.TORA, 1628);
            assertEquals(params.TODA, 1766);
            assertEquals(params.ASDA, 1628);
            assertEquals(params.LDA, 868);

        } catch (NoRedeclarationNeededException e) {
            fail("Calculator throws exception in the wrong situation.");
        } catch (AttributeNotAssignedException e) {
            fail("Calculator failed to assign redeclared distances and breakdown to runways.");
        }
    }

    @Test
    public void outsideInstrumentStrip() {
        ArrayList<ObstaclePosition> obstaclePositions = new ArrayList<>();
        obstaclePositions.add(new ObstaclePosition(new Obstacle("outside", 5), -70, 3730, 10,1, RunwaySide.LEFT));
        obstaclePositions.add(new ObstaclePosition(new Obstacle("outside", 5), 3730, -100, 10,1, RunwaySide.LEFT));
        obstaclePositions.add(new ObstaclePosition(new Obstacle("outside", 5), 50, 3600, 10,76, RunwaySide.LEFT));

        for (Runway r : runways) {
            for (ObstaclePosition o : obstaclePositions) {
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