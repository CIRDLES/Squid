package org.cirdles.squid.gui.dateInterpretations;

import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.Task;

import static org.cirdles.squid.gui.SquidUIController.squidProject;

public class Correction {

    public static void correctionNoneAction() {
        squidProject.getTask().setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes.NONE);
        ((Task) squidProject.getTask()).updateAllUnknownSpotsWithOriginal204_206();
        SquidProject.setProjectChanged(true);
        ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(squidProject.getTask().getUnknownSpots());
    }

    public static void correction207Action() {
        squidProject.getTask().setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes.FR_207);
        ((Task) squidProject.getTask()).updateAllUnknownSpotsWithOverCountCorrectedBy204_206_207();
        SquidProject.setProjectChanged(true);
        ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(squidProject.getTask().getUnknownSpots());
    }

    public static void correction208Action() {
        squidProject.getTask().setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes.FR_208);
        ((Task) squidProject.getTask()).updateAllUnknownSpotsWithOverCountCorrectedBy204_206_208();
        SquidProject.setProjectChanged(true);
        ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(squidProject.getTask().getUnknownSpots());
    }
}
