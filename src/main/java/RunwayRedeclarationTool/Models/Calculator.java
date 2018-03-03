package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Exceptions.AttributeNotAssignedException;
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

    // TODO: change blast protection to RESA + strip end
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
        StringBuffer brkdwn = new StringBuffer(300);

        brkdwn.append("TORA = original TORA - distance from threshold - displaced threshold - blast protection\n")
                .append("     = ").append(oParams.TORA).append(" - ").append(distFromTSH).append(" - ")
                .append(displacedTSH).append(visual_strip_end + RESA).append(" = ").append(rParams.TORA).append("\n");
        brkdwn.append("TODA = recalculated TORA + clearway\n").append("     = ").append(oParams.TORA).append(" + ")
                .append(clearway).append(" = ").append(rParams.TODA).append("\n");
        brkdwn.append("ASDA = recalculated TORA + stopway\n").append("     = ").append(oParams.TORA).append(" + ")
                .append(stopway).append(" = ").append(rParams.ASDA).append("\n");
        brkdwn.append("LDA  = original LDA - distance from threshold - slope calculation - strip end\n")
                .append("     = ").append(oParams.LDA).append(" - ").append(distFromTSH).append(" - ")
                .append(obHeight).append("*50 - ").append(visual_strip_end).append(" = ").append(rParams.LDA);

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
//        int stopway = oParams.ASDA - oParams.TORA;
//        int clearway = oParams.TODA - oParams.TORA;
        int displacedTSH = oParams.TORA - oParams.LDA;
        StringBuffer brkdwn = new StringBuffer(300);

        brkdwn.append("TORA = distance from threshold + displaced threshold - slope calculation - strip end\n")
                .append(distFromTSH).append(" + ").append(displacedTSH).append(" - ").append(obHeight).append("*50 - ")
                .append(visual_strip_end).append(" = ").append(rParams.TORA).append("\n");
        brkdwn.append("TODA = recalculated TORA\n").append("     = ").append(rParams.TODA).append("\n");
        brkdwn.append("ASDA = recalculated TORA\n").append("     = ").append(rParams.ASDA).append("\n");
        brkdwn.append("LDA  = distance from threshold - blast protection\n").append("     = ").append(distFromTSH)
                .append(" - ").append(visual_strip_end + RESA).append(" = ").append(rParams.LDA);

        vRunway.setRecalcBreakdown(brkdwn.toString());
    }
}
