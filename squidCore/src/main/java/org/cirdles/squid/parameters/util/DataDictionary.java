/*
 * DataDictionary.java
 *
 * Created on April 23, 2007, 7:06 AM
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.parameters.util;

import java.util.HashMap;
import java.util.Map;

public final class DataDictionary {

    public final static String[][] AtomicMolarMasses = new String[][]{
        {"gmol204", "203.973028"},
        {"gmol205", "204.9737"},
        {"gmol206", "205.974449"},
        {"gmol207", "206.975880"},
        {"gmol208", "207.976636"},
        {"gmol230", "230.033128"},
        {"gmol232", "232.038051"},
        {"gmol235", "235.043922"},
        {"gmol238", "238.050785"}
    };

    public final static String[][] MeasuredConstants = new String[][]{
        {Lambdas.lambda226.getName(), "0.0004335", "0.0000011", "Holden 1990"},
        {Lambdas.lambda230.getName(), "0.00000912516", "0.15244", "Cheng et al. 2000"},
        {Lambdas.lambda231.getName(), "0.0000211887", "0.33578", "Robert et al. 1969"},
        {Lambdas.lambda232.getName(), "0.0000000000493343", "0.042769", "Holden 1990"},
        {Lambdas.lambda234.getName(), "0.0000028262", "0.00000000285", "Cheng et al. 2000"},
        {Lambdas.lambda235.getName(), "0.00000000098485", "0.068031", "Jaffey et al. 1971"},
        {Lambdas.lambda238.getName(), "0.000000000155125", "0.053505", "Jaffey et al. 1971"}
    };

    public final static String[] earthTimePbBlankICRatioNames = new String[]{
        "r206_204b",
        "r207_204b",
        "r208_204b"
    };
    
}
