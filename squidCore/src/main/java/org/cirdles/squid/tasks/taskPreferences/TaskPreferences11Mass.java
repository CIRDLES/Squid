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
package org.cirdles.squid.tasks.taskPreferences;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TaskPreferences11Mass extends SquidTaskPreferences {

    public TaskPreferences11Mass() { 

        this.nominalMasses = new ArrayList<>(Arrays.asList(new String[]{"190", "195.8", "195.9", "238", "248", "254"}));

        this.ratioNames = new ArrayList<>(Arrays.asList(new String[]{
            "190/195.8", "195.9/195.8", "238/195.8", "248/195.8", "206/238", "254/238", "208/248", "206/254", "248/254"}));
        
        indexOfBackgroundSpecies = 4;
    }
}
