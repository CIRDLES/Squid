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

import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TaskDesignGeochronBlank extends TaskDesign {

    public TaskDesignGeochronBlank() throws SquidException {
        super();

        this.nominalMasses = new ArrayList<>(Collections.emptyList());
        this.ratioNames = new ArrayList<>(Collections.emptyList());
        this.name = "New empty Geochron task";
        this.taskType = Squid3Constants.TaskTypeEnum.GEOCHRON;
        indexOfBackgroundSpecies = -1;

        this.useSBM = true;
        this.userLinFits = false;

        this.selectedIndexIsotope = Squid3Constants.IndexIsoptopesEnum.PB_204;
        this.squidAllowsAutoExclusionOfSpots = true;

        this.extPErrU = 0.75;
        this.extPErrTh = 0.75;
        this.delimiterForUnknownNames = Squid3Constants.SampleNameDelimitersEnum.HYPHEN.getName().trim();
        this.parentNuclide = "238";
        this.directAltPD = false;

        this.specialSquidFourExpressionsMap = new TreeMap<>();
        this.specialSquidFourExpressionsMap.put(UNCOR206PB238U_CALIB_CONST, UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(UNCOR208PB232TH_CALIB_CONST, UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(TH_U_EXP_DEFAULT, TH_U_EXP_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(PARENT_ELEMENT_CONC_CONST, PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION);
        this.commonPbModel = SquidLabData.getExistingSquidLabData().getCommonPbDefault();

        // Default to blank
        this.nominalMasses = new ArrayList<>(Collections.singletonList(DEFAULT_BACKGROUND_MASS_LABEL));
        this.ratioNames = new ArrayList<>(Collections.emptyList());

        buildShrimpSpeciesNodeMap();
    }
}