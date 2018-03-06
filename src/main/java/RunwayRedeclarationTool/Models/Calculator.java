package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
import RunwayRedeclarationTool.Exceptions.NoRedeclarationNeededException;

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
            takeoffAwayLandOver(o, r.leftRunway, o.getDistLeftTSH());
            takeoffTowardsLandTowards(o, r.rightRunway, o.getDistRightTSH());
        } else {
            takeoffAwayLandOver(o, r.rightRunway, o.getDistRightTSH());
            takeoffTowardsLandTowards(o, r.leftRunway, o.getDistLeftTSH());
        }
    }

    // Requires the smaller distance from threshold
    private void takeoffAwayLandOver (ObstaclePosition o, VirtualRunway vRunway, int distFromTSH) {
        // Setting up all needed values
        RunwayParameters params = vRunway.getOrigParams();
        int stopway = params.ASDA - params.TORA;
        int clearway = params.TODA - params.TORA;
        int displacedTSH = params.TORA - params.LDA;

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

        vRunway.setRecalcParams(new RunwayParameters(rTORA, rTODA, rASDA, rLDA));
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
        int displacedTSH = oParams.TORA - oParams.LDA;

        String brkdwn = vRunway.getDesignator() + " (Take off away, landing over):\n" +
                "TORA = original TORA - distance from threshold - displaced threshold - RESA - strip end\n" +
                "     = " + oParams.TORA + " - " + distFromTSH + " - " + displacedTSH + " - " + RESA + " - " +
                visual_strip_end + " = " + rParams.TORA + "\n" +
                "TODA = recalculated TORA + clearway\n" + "     = " + oParams.TORA + " + " + clearway + " = " +
                rParams.TODA + "\n" +
                "ASDA = recalculated TORA + stopway\n" + "     = " + oParams.TORA + " + " + stopway + " = " +
                rParams.ASDA + "\n" +
                "LDA  = original LDA - distance from threshold - slope calculation - strip end\n" + "     = " +
                oParams.LDA + " - " + distFromTSH + " - " + obHeight + "*50 - " + visual_strip_end + " = " + rParams.LDA;

        vRunway.setRecalcBreakdown(brkdwn);
    }

    // Requires the greater distance from threshold
    private void takeoffTowardsLandTowards(ObstaclePosition o, VirtualRunway vRunway, int distFromTSH) {
        // Setting up all needed values
        RunwayParameters params = vRunway.getOrigParams();
        int displacedTSH = params.TORA - params.LDA;

        // Take off distances
        int slopeCalc = o.getHeight() * 50;
        if (slopeCalc < RESA) {
            slopeCalc = RESA;
        }
        int rTORA = distFromTSH + displacedTSH - slopeCalc - visual_strip_end;

        // Landing distances
        int rLDA = distFromTSH - RESA - visual_strip_end;

        vRunway.setRecalcParams(new RunwayParameters(rTORA, rTORA, rTORA, rLDA));
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

        int displacedTSH = oParams.TORA - oParams.LDA;

        String brkdwn = vRunway.getDesignator() + " (Take off towards, landing towards):\n" +
                "TORA = distance from threshold + displaced threshold - slope calculation - strip end\n" +
                "     = " + distFromTSH + " + " + displacedTSH + " - " + obHeight + "*50 - " + visual_strip_end +
                " = " + rParams.TORA + "\n" +
                "TODA = recalculated TORA\n" + "     = " + rParams.TODA + "\n" +
                "ASDA = recalculated TORA\n" + "     = " + rParams.ASDA + "\n" +
                "LDA  = distance from threshold - RESA - strip end\n" + "     = " + distFromTSH + " - " + RESA +
                " - " + visual_strip_end + " = " + rParams.LDA;

        vRunway.setRecalcBreakdown(brkdwn);
    }
}
