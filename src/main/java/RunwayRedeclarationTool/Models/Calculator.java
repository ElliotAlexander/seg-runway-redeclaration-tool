package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Exceptions.NoRedeclarationNeededException;
import RunwayRedeclarationTool.Logger.Logger;

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
        // Check redeclaration is needed
        if (o.getDistLeftTSH() < -visual_strip_end ||
                o.getDistRightTSH() < -visual_strip_end ||
                o.getDistFromCL() > visual_strip_width) {
            throw new NoRedeclarationNeededException("ObstaclePosition is outside visual strip.");
        }

        decideRedeclarationCase(o, r);
    }

    private void decideRedeclarationCase(ObstaclePosition o, Runway r) {
        int rightDisplaced = r.rightRunway.getOrigParams().getTORA() - r.rightRunway.getOrigParams().getLDA();
        int leftDisplaced = r.leftRunway.getOrigParams().getTORA() - r.leftRunway.getOrigParams().getLDA();

        int oLength = r.rightRunway.getOrigParams().getLDA() - o.getDistLeftTSH() - o.getDistRightTSH() - leftDisplaced;
        int oHeight = o.getObstacle().getHeight();

        if (o.getDistLeftTSH() < o.getDistRightTSH()) {
            // __|=|__->______
            takeoffAwayLandOver(oLength, oHeight, o.getDistLeftTSH(), r.leftRunway);

            // __|=|__<-______
            takeoffTowardsLandTowards(rightDisplaced, oHeight, o.getDistRightTSH(), r.rightRunway);
        } else {
            // ______<-__|=|__
            takeoffAwayLandOver(oLength, oHeight, o.getDistRightTSH(), r.rightRunway);

            // ______->__|=|__
            takeoffTowardsLandTowards(leftDisplaced, oHeight, o.getDistLeftTSH(), r.leftRunway);
        }
    }

    private void takeoffAwayLandOver(int oLength, int oHeight, int smallDistTSH, VirtualRunway r) {
        // Setting up all needed values
        RunwayParameters params = r.getOrigParams();
        int stopway = params.ASDA - params.TORA;
        int clearway = params.TODA - params.TORA;
        int displacedTSH = params.TORA - params.LDA;

        // Take off distances
        int rTORA = params.TORA - smallDistTSH - displacedTSH - oLength - RESA - visual_strip_end;
        int rTODA = rTORA + clearway;
        int rASDA = rTORA + stopway;

        // Landing distance
        int slopeCalc = oHeight * 50;
        if (slopeCalc < RESA) {
            slopeCalc = RESA;  // minimum distance from obstacle: RESA + strip end
        }
        int rLDA = params.LDA - smallDistTSH - oLength - slopeCalc - visual_strip_end;

        String brkdwn = r.getDesignator() + " (Take off away, landing over):\n" +
                "TORA = original TORA - distance from threshold - displaced threshold - obstacle length - RESA - strip end\n" +
                "     = " + params.TORA + " - " + smallDistTSH + " - " + displacedTSH + " - " + oLength + " - " +
                RESA + " - " + visual_strip_end + " = " + rTORA + "\n" +
                "TODA = recalculated TORA + clearway\n" + "     = " + params.TORA + " + " + clearway + " = " +
                rTODA + "\n" +
                "ASDA = recalculated TORA + stopway\n" + "     = " + params.TORA + " + " + stopway + " = " +
                rASDA + "\n" +
                "LDA  = original LDA - distance from threshold - obstacle length - slope calculation - strip end\n" + "     = " +
                params.LDA + " - " + smallDistTSH + " - " + oLength + " - " + oHeight + "*50 - " + visual_strip_end + " = " + rLDA;

        r.setRecalcParams(new RunwayParameters(rTORA, rTODA, rASDA, rLDA), slopeCalc);
        Logger.Log("Calculated parameters for " + r.getDesignator() + " taking off towards and landing towards.");
        r.setRecalcBreakdown(brkdwn);
        Logger.Log("Added breakdown calculations for " + r.getDesignator() + " taking off towards and landing towards.");
    }

    private void takeoffTowardsLandTowards(int displacedTSH, int oHeight, int largeDistTSH, VirtualRunway r) {
        // Take off distances
        int slopeCalc = oHeight * 50;
        if (slopeCalc < RESA) {
            slopeCalc = RESA;   // minimum distance from obstacle: RESA + strip end
        }
        int rTORA = largeDistTSH + displacedTSH - slopeCalc - visual_strip_end;

        // Landing distances
        int rLDA = largeDistTSH - RESA - visual_strip_end;

        String brkdwn = r.getDesignator() + " (Take off towards, landing towards):\n" +
                "TORA = distance from threshold + displaced threshold - slope calculation - strip end\n" +
                "     = " + largeDistTSH + " + " + displacedTSH + " - " + oHeight + "*50 - "
                + visual_strip_end + " = " + rTORA + "\n" +
                "TODA = recalculated TORA\n" + "     = " + rTORA + "\n" +
                "ASDA = recalculated TORA\n" + "     = " + rTORA + "\n" +
                "LDA  = distance from threshold - RESA - strip end\n" + "     = " + largeDistTSH + " - " + RESA +
                " - " + visual_strip_end + " = " + rLDA;

        r.setRecalcParams(new RunwayParameters(rTORA, rTORA, rTORA, rLDA), slopeCalc);
        r.setRecalcBreakdown(brkdwn);
        Logger.Log("Calculated parameters and added breakdown for " + r.getDesignator() + " taking off towards and landing towards.");
    }
}
