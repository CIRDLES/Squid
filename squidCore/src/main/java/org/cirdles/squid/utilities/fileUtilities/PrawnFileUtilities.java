/*
 * Copyright 2017 CIRDLES.org.
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
package org.cirdles.squid.utilities.fileUtilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.cirdles.calamari.prawn.PrawnFile;

/**
 *
 * @author James F. Bowring
 */
public final class PrawnFileUtilities {

    public static long timeInMillisecondsOfRun(PrawnFile.Run run) {
        String startDateTime = run.getSet().getPar().get(0).getValue()
                + " " + run.getSet().getPar().get(1).getValue()
                + (Integer.parseInt(run.getSet().getPar().get(1).getValue().substring(0, 2)) < 12 ? " AM" : " PM");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
        long milliseconds = 0l;
        try {
            milliseconds = dateFormat.parse(startDateTime).getTime();
        } catch (ParseException parseException) {
        }

        return milliseconds;
    }
    
}
