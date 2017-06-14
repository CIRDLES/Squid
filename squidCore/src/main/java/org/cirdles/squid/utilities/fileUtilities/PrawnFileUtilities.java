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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.prawn.PrawnFile.Run.RunTable.Entry;
import org.cirdles.squid.prawn.PrawnFile.Run.Set.Scan;
import org.cirdles.squid.prawn.PrawnFile.Run.Set.Scan.Measurement;
import org.cirdles.squid.shrimp.IsotopeNames;

/**
 *
 * @author James F. Bowring
 */
public final class PrawnFileUtilities {

    private static Map<String, IsotopeNames> prawnMassNameMap = new HashMap<>();

    static {
        // provide a map from prawn names to isotope names
        for (IsotopeNames isotope : IsotopeNames.values()) {
            prawnMassNameMap.put(isotope.getPrawnName().toUpperCase(Locale.US), isotope);
        }
    }

    public static long timeInMillisecondsOfRun(Run run) {
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

    public static Map<String, List<List<Double>>> extractMassTimeSeries(List<Run> runs) {
        Map<String, List<List<Double>>> massTimeSeries;
        massTimeSeries = new TreeMap<>(
                new Comparator<String>() {
            @Override
            public int compare(String prawnMassName1, String prawnMassName2) {
                // workaround until prawn names list is stable
                IsotopeNames prawnMassIN1 = prawnMassNameMap.get(prawnMassName1.toUpperCase(Locale.US));
                double atomicMass1 = 0;
                if (prawnMassIN1 != null){
                    atomicMass1 = prawnMassIN1.getAtomicMass();
                } else{
                    atomicMass1 = (double)(prawnMassName1.toLowerCase(Locale.US).contains("k") ? 0 :   Double.parseDouble(prawnMassName1.replaceAll("\\D+","")));
                }
                
                IsotopeNames prawnMassIN2 = prawnMassNameMap.get(prawnMassName2.toUpperCase(Locale.US));
                double atomicMass2 = 0;
                if (prawnMassIN2 != null){
                    atomicMass2 = prawnMassIN2.getAtomicMass();
                } else {
                    atomicMass2 = (double)(prawnMassName2.toLowerCase(Locale.US).contains("k") ? 0 :   Double.parseDouble(prawnMassName2.replaceAll("\\D+","")));
                }
                
                return Double.compare(atomicMass1,atomicMass2);
            }
        });

        // prepare map from first run table
        List<Entry> entries = runs.get(0).getRunTable().getEntry();
        for (Entry entry : entries) {
            List<List<Double>> data = new ArrayList<>();
            // element 0 = amu, 1 = timestamp
            data.add(new ArrayList<>());
            data.add(new ArrayList<>());

            String massLabel = entry.getPar().get(0).getValue();

            massTimeSeries.put(massLabel, data);
        }

        for (Run run : runs) {
            long runStartTime = timeInMillisecondsOfRun(run);
            List<Scan> scans = run.getSet().getScan();
            for (Scan scan : scans) {
                List<Measurement> measurements = scan.getMeasurement();
                for (Measurement measurement : measurements) {
                    double trimMass = Double.parseDouble(measurement.getPar().get(1).getValue());
                    double timeStampSec = Double.parseDouble(measurement.getPar().get(2).getValue());
                    long measurementTime = runStartTime + (long) timeStampSec * 1000l;

                    String massLabel = measurement.getData().get(0).getName();

                    massTimeSeries.get(massLabel).get(0).add(trimMass);
                    massTimeSeries.get(massLabel).get(1).add((double) measurementTime);
                }
            }
        }

        return massTimeSeries;
    }

}
