package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Exceptions.NoRedeclarationNeededException;
import RunwayRedeclarationTool.Logger.Logger;

// Singleton that takes in ObstaclePosition and Runway and sets the recalculated runway parameters in the two virtual runways
public class Calculator {
    public static final Calculator instance = new Calculator();

    private static final int visual_strip_end = 60;
    private static final int visual_strip_width = 75;
    private static final int RESA = 240;

    private Calculator() {}

    public static Calculator getInstance () {
        return instance;
    }

    public void calculate (ObstaclePosition o, Runway r) throws NoRedeclarationNeededException {
        // The two virtual runways:
        RunwayParameters leftParams = r.leftRunway.getOrigParams();
        RunwayParameters rightParams = r.rightRunway.getOrigParams();

        // Check redeclaration is needed
        if (o.getDistLeftTSH() < -visual_strip_end ||
                o.getDistRightTSH() < -visual_strip_end ||
                o.getDistFromCL() > visual_strip_width) {
            throw new NoRedeclarationNeededException("ObstaclePosition is outside visual strip.");
        }

        decideSideOfRunway(o, r);
    }

    public void decideSideOfRunway (ObstaclePosition o, Runway r) {
        if (o.getDistLeftTSH() < o.getDistRightTSH()) {
            takeoffAwayLandOver(o.getObstacle(), r.leftRunway, o.getDistLeftTSH());
            takeoffTowardsLandTowards(o.getObstacle(), r.rightRunway, o.getDistRightTSH());
        } else {
            takeoffAwayLandOver(o.getObstacle(), r.rightRunway, o.getDistRightTSH());
            takeoffTowardsLandTowards(o.getObstacle(), r.leftRunway, o.getDistLeftTSH());
        }
    }

    // Requires the smaller distance from threshold
    private void takeoffAwayLandOver (Obstacle o, VirtualRunway vRunway, int distFromTSH) {
        // Setting up all needed values
        RunwayParameters params = vRunway.getOrigParams();
        int stopway = params.ASDA - params.TORA;
        int clearway = params.TODA - params.TORA;
        int displacedTSH = 0;

        if (distFromTSH < 0) { // Object is off the runway
            displacedTSH = params.TORA - params.LDA;
        }

        // Take off distances
        int rTORA = params.TORA - distFromTSH - displacedTSH - visual_strip_end - RESA;
        int rTODA = rTORA + clearway;
        int rASDA = rTORA + stopway;

        // Landing distance
        int slopeCalc = o.getHeight() * 50;
        if (slopeCalc < RESA) {
            slopeCalc = RESA;  // minimum distance from obstacle: RESA + strip end
        }
        int rLDA = params.LDA - distFromTSH - slopeCalc - visual_strip_end;

        RunwayParameters recalcParams = new RunwayParameters(rTORA, rTODA, rASDA, rLDA);
        recalcParams.setSlopeCalculation(slopeCalc);
        vRunway.setRecalcParams(recalcParams);
        Logger.Log("Calculated parameters for " + vRunway.getDesignator() + " taking off away and landing over.");
        takeoffAwayLandOverBreakdown(vRunway, distFromTSH, o.getHeight());
    }

    private void takeoffAwayLandOverBreakdown(VirtualRunway vRunway, int distFromTSH, int obHeight) {
        // Setting up all needed values
        RunwayParameters oParams = vRunway.getOrigParams();
        RunwayParameters rParams = null;
        try {
            rParams = vRunway.getRecalcParams();
        } catch (AttributeNotAssignedException e) {
            e.printStackTrace();    //Shouldn't happen since we're setting them just before this method is called. Everything is private
        }

        int stopway = oParams.ASDA - oParams.TORA;
        int clearway = oParams.TODA - oParams.TORA;
        String brkdwn;

        if (distFromTSH < 0) {
            int displacedTSH = oParams.TORA - oParams.LDA;

            brkdwn = vRunway.getDesignator() + " (Take off away, landing over):\n" +
                    "TORA = original TORA - distance from threshold - displaced threshold - RESA - strip end\n" +
                    "     = " + oParams.TORA + " - " + distFromTSH + " - " + displacedTSH + " - " + RESA + " - " +
                    visual_strip_end + " = " + rParams.TORA + "\n";
        } else {
            brkdwn = vRunway.getDesignator() + " (Take off away, landing over):\n" +
                    "TORA = original TORA - distance from threshold - RESA - strip end\n" +
                    "     = " + oParams.TORA + " - " + distFromTSH + " - " + RESA + " - " + visual_strip_end +
                    " = " + rParams.TORA + "\n";
        }
        brkdwn += "TODA = recalculated TORA + clearway\n" + "     = " + oParams.TORA + " + " + clearway + " = " +
            rParams.TODA + "\n" +
            "ASDA = recalculated TORA + stopway\n" + "     = " + oParams.TORA + " + " + stopway + " = " +
            rParams.ASDA + "\n" +
            "LDA  = original LDA - distance from threshold - slope calculation - strip end\n" + "     = " +
            oParams.LDA + " - " + distFromTSH + " - " + obHeight + "*50 - " + visual_strip_end + " = " + rParams.LDA;

        Logger.Log("Added breakdown calculations for " + vRunway.getDesignator() + " taking off away and landing over.");
        vRunway.setRecalcBreakdown(brkdwn);
    }

    // Requires the greater distance from threshold
    private void takeoffTowardsLandTowards(Obstacle o, VirtualRunway vRunway, int distFromTSH) {
        // Setting up all needed values
        RunwayParameters params = vRunway.getOrigParams();
        int displacedTSH = 0;
        if (distFromTSH < 0) {      // Object is off the runway
            displacedTSH = params.TORA - params.LDA;
        }

        // Take off distances
        int slopeCalc = o.getHeight() * 50;
        if (slopeCalc < RESA) {
            slopeCalc = RESA;
        }
        int rTORA = distFromTSH + displacedTSH - slopeCalc - visual_strip_end;

        // Landing distances
        int rLDA = distFromTSH - RESA - visual_strip_end;

        RunwayParameters recalcParams = new RunwayParameters(rTORA, rTORA, rTORA, rLDA);
        recalcParams.setSlopeCalculation(slopeCalc);
        vRunway.setRecalcParams(recalcParams);
        Logger.Log("Calculated parameters for " + vRunway.getDesignator() + " taking off towards and landing towards.");
        takeoffTowardsLandTowardsBreakdown(vRunway, distFromTSH, o.getHeight());
    }

    private void takeoffTowardsLandTowardsBreakdown(VirtualRunway vRunway, int distFromTSH, int obHeight) {
        // Setting up all needed values
        RunwayParameters oParams = vRunway.getOrigParams();
        RunwayParameters rParams = null;
        try {
            rParams = vRunway.getRecalcParams();
        } catch (AttributeNotAssignedException e) {
            e.printStackTrace();    //Shouldn't happen since we're setting them just before this method is called. Everything is private
        }

        String brkdwn;

        if (distFromTSH < 0) {
            int displacedTSH = oParams.TORA - oParams.LDA;

            brkdwn = vRunway.getDesignator() + " (Take off towards, landing towards):\n" +
                    "TORA = distance from threshold + displaced threshold - slope calculation - strip end\n" +
                    "     = " + distFromTSH + " + " + displacedTSH + " - " + obHeight + "*50 - " + visual_strip_end +
                    " = " + rParams.TORA + "\n";
        } else {
            brkdwn = vRunway.getDesignator() + " (Take off towards, landing towards):\n" +
                    "TORA = distance from threshold - slope calculation - strip end\n" +
                    "     = " + distFromTSH + " - " + obHeight + "*50 - " + visual_strip_end +
                    " = " + rParams.TORA + "\n";
        }

        brkdwn += "TODA = recalculated TORA\n" + "     = " + rParams.TODA + "\n" +
            "ASDA = recalculated TORA\n" + "     = " + rParams.ASDA + "\n" +
            "LDA  = distance from threshold - RESA - strip end\n" + "     = " + distFromTSH + " - " + RESA +
            " - " + visual_strip_end + " = " + rParams.LDA;

        Logger.Log("Added breakdown calculations for " + vRunway.getDesignator() + " taking off towards and landing towards.");
        vRunway.setRecalcBreakdown(brkdwn);
    }
}
