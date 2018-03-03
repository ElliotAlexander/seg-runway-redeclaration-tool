package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Exceptions.NoRedeclarationNeededException;

// Singleton that takes in Obstacle and Runway and sets the recalculated runway parameters in the two virtual runways
public class Calculator {
    public static final Calculator instance = new Calculator();

    private static final int visual_strip_end = 60;
    private static final int visual_strip_width = 75;
    private static final int RESA = 240;

    private Calculator() {}

    public static Calculator getInstance () {
        return instance;
    }

    public void calculate (Obstacle o, Runway r) throws NoRedeclarationNeededException {
        // The two virtual runways:
        RunwayParameters leftParams = r.leftRunway.getOrigParams();
        RunwayParameters rightParams = r.rightRunway.getOrigParams();

        // Check redeclaration is needed
        if (o.getDistLeftTSH() < -visual_strip_end ||
                o.getDistRightTSH() < -visual_strip_end ||
                o.getDistFromCL() > visual_strip_width) {
            throw new NoRedeclarationNeededException("Obstacle is outside visual strip.");
        }

        decideSideOfRunway(o, r);
    }

    public void decideSideOfRunway (Obstacle o, Runway r) {
        if (o.getDistLeftTSH() < o.getDistRightTSH()) {
            takeoffAwayLandOver(o, r.leftRunway, o.getDistLeftTSH());
            takeoffTowardsLandTowards(o, r.rightRunway, o.getDistRightTSH());
        } else {
            takeoffAwayLandOver(o, r.rightRunway, o.getDistRightTSH());
            takeoffTowardsLandTowards(o, r.leftRunway, o.getDistLeftTSH());
        }
    }

    // Requires the smaller distance from threshold
    private void takeoffAwayLandOver (Obstacle o, VirtualRunway vRunway, int distFromTSH) {
        // Setting up all needed values
        RunwayParameters params = vRunway.getOrigParams();
        int stopway = params.ASDA - params.TORA;
        int clearway = params.TODA - params.TORA;
        int displacedTSH = params.TORA - params.LDA;
        StringBuffer brkdwn = new StringBuffer(300);

        // Take off distances
        int rTORA = params.TORA - distFromTSH - displacedTSH - visual_strip_end - RESA;
        int rTODA = rTORA + clearway;
        int rASDA = rTORA + stopway;

        // And their breakdown
        brkdwn.append("TORA = original TORA - distance from threshold - displaced threshold - blast protection\n")
                .append("     = ").append(params.TORA).append(" - ").append(distFromTSH).append(" - ")
                .append(displacedTSH).append(visual_strip_end + RESA).append(" = ").append(rTORA).append("\n");
        brkdwn.append("TODA = recalculated TORA + clearway\n").append("     = ").append(params.TORA).append(" + ")
                .append(clearway).append(" = ").append(rTODA).append("\n");
        brkdwn.append("ASDA = recalculated TORA + stopway\n").append("     = ").append(params.TORA).append(" + ")
                .append(stopway).append(" = ").append(rASDA).append("\n");

        // Landing distance
        int slopeCalc = o.getHeight() * 50;
        boolean largeSlope = true;
        if (slopeCalc < RESA) {
            slopeCalc = RESA;  // minimum distance from obstacle: RESA + strip end
            largeSlope = false;
        }
        int rLDA = params.LDA - distFromTSH - slopeCalc - visual_strip_end;

        // And its breakdown
        if (largeSlope) {
            brkdwn.append("LDA  = original LDA - distance from threshold - slope calculation - strip end\n")
                    .append("     = ").append(params.LDA).append(" - ").append(distFromTSH).append(" - ")
                    .append(o.getHeight()).append("*50 - ").append(visual_strip_end).append(" = ").append(rLDA);
        } else {
            brkdwn.append("LDA  = original LDA - distance from threshold - blast protection\n").append("     = ")
                    .append(params.LDA).append(" - ").append(distFromTSH).append(" - ").append(visual_strip_end + RESA)
                    .append(" = ").append(rLDA);
        }

        vRunway.setRecalcParams(new RunwayParameters(rTORA, rTODA, rASDA, rLDA));
        vRunway.setRecalcBreakdown(brkdwn.toString());
    }

    // Requires the greater distance from threshold
    private void takeoffTowardsLandTowards(Obstacle o, VirtualRunway vRunway, int distFromTSH) {
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
    }
}
