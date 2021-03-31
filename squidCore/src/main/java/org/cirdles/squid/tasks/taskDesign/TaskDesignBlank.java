/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.taskDesign;

import java.util.ArrayList;
import java.util.Arrays;

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TaskDesignBlank extends TaskDesign {

    public TaskDesignBlank() {

        this.nominalMasses = new ArrayList<>(Arrays.asList(new String[]{}));

        this.ratioNames = new ArrayList<>(Arrays.asList(new String[]{}));

        this.name = "New empty Geochron task";

        indexOfBackgroundSpecies = -1;

        this.specialSquidFourExpressionsMap.put(UNCOR206PB238U_CALIB_CONST, "");
        this.specialSquidFourExpressionsMap.put(UNCOR208PB232TH_CALIB_CONST, "");
        this.specialSquidFourExpressionsMap.put(TH_U_EXP_DEFAULT, "");
        this.specialSquidFourExpressionsMap.put(PARENT_ELEMENT_CONC_CONST, "");
    }
}
