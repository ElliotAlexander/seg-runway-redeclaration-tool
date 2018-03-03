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

        // To which threshold is the object closer?
        if (o.getDistLeftTSH() < o.getDistRightTSH()) {
            r.leftRunway.setRecalcParams(takeoffAwayLandOver(o, leftParams, o.getDistLeftTSH()));
            r.rightRunway.setRecalcParams(takeoffTowardsLandTowards(o, rightParams, o.getDistRightTSH()));
        } else {
            r.leftRunway.setRecalcParams(takeoffTowardsLandTowards(o, leftParams, o.getDistLeftTSH()));
            r.rightRunway.setRecalcParams(takeoffAwayLandOver(o, rightParams, o.getDistRightTSH()));
        }
    }

    private RunwayParameters takeoffAwayLandOver (Obstacle o, RunwayParameters params, int distFromTSH) {
        int stopway = params.ASDA - params.TORA;
        int clearway = params.TODA - params.TORA;
        int displacedTSH = params.TORA - params.LDA;

        // take off
        int rTORA = params.TORA - distFromTSH - displacedTSH - visual_strip_end - RESA;
        int rASDA = rTORA + stopway;
        int rTODA = rTORA + clearway;

        // land
        int slopeCalc = o.getHeight() * 50;
        if (slopeCalc < RESA) {
            slopeCalc = RESA;  // minimum distance from obstacle: RESA + strip end
        }
        int rLDA = params.LDA - distFromTSH - slopeCalc - visual_strip_end;

        return new RunwayParameters(rTORA, rTODA, rASDA, rLDA);
    }

    private RunwayParameters takeoffTowardsLandTowards(Obstacle o, RunwayParameters params, int distFromTSH) {
        int displacedTSH = params.TORA - params.LDA;

        // take off
        int slopeCalc = o.getHeight() * 50;
        if (slopeCalc < RESA) {
            slopeCalc = RESA;
        }
        int rTORA = distFromTSH + displacedTSH - slopeCalc - visual_strip_end;

        // land
        int rLDA = distFromTSH - RESA - visual_strip_end;

        return new RunwayParameters(rTORA, rTORA, rTORA, rLDA);
    }
}
