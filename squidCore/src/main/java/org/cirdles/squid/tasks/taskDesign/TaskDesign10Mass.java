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
public class TaskDesign10Mass extends TaskDesign {

    public TaskDesign10Mass() {

        this.nominalMasses = new ArrayList<>(Arrays.asList(new String[]{
                //            DEFAULT_BACKGROUND_MASS_LABEL, "196", "238", "248", "254", "270"}));
                "204.1", "196", "238", "248", "254", "270"}));

        this.ratioNames = new ArrayList<>(Arrays.asList(new String[]{
                "238/196", "206/238", "208/248", "254/238", "248/254", "206/254", "206/270", "270/254"}));

        this.name = "New 10 mass task";

        indexOfBackgroundSpecies = 2;

        this.specialSquidFourExpressionsMap.put(UNCOR206PB238U_CALIB_CONST, "[\"206/238\"]/[\"254/238\"]^2");
        this.specialSquidFourExpressionsMap.put(UNCOR208PB232TH_CALIB_CONST, UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(TH_U_EXP_DEFAULT, "(0.03446*[\"254/238\"]+0.868)*[\"248/254\"]");
        this.specialSquidFourExpressionsMap.put(PARENT_ELEMENT_CONC_CONST, "[\"238/196\"]/[\"254/238\"]^0.66");
    }
}
