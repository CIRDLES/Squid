/*
 * Copyright 2020 James F. Bowring and CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.tasks.taskUtilities;

import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class OvercountCorrection {
    
    public static void correctionNone(TaskInterface task) {
        task.setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes.NONE);
        ((Task)task).updateAllUnknownSpotsWithOriginal204_206();
        SquidProject.setProjectChanged(true);
        ((Task)task).evaluateUnknownsWithChangedParameters(task.getUnknownSpots());
    }
    
    public static void correction207(TaskInterface task) {
        task.setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes.FR_207);
        ((Task)task).updateAllUnknownSpotsWithOverCountCorrectedBy204_206_207();
        SquidProject.setProjectChanged(true);
        ((Task)task).evaluateUnknownsWithChangedParameters(task.getUnknownSpots());
    }

    public static void correction208(TaskInterface task) {
        task.setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes.FR_208);
        ((Task)task).updateAllUnknownSpotsWithOverCountCorrectedBy204_206_208();
        SquidProject.setProjectChanged(true);
        ((Task)task).evaluateUnknownsWithChangedParameters(task.getUnknownSpots());
    }
}
