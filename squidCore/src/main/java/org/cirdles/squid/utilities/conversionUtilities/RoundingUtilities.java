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
package org.cirdles.squid.utilities.conversionUtilities;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class RoundingUtilities {

//    public static boolean USE_SIG_FIG_15 = true; // starting v1.5.8 sept 202 per bodorkos false;

    public static double squid3RoundedToSize(double value, int sigFigs) {
//        return org.cirdles.ludwig.squid25.Utilities.roundedToSize(value, USE_SIG_FIG_15 ? 15 : sigFigs);
        return org.cirdles.ludwig.squid25.Utilities.roundedToSize(value, 15);
    }
}