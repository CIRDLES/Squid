/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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

import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.prawn.PrawnFile.Run.RunTable.Entry;
import org.cirdles.squid.prawn.PrawnFile.Run.Set.Scan;
import org.cirdles.squid.prawn.PrawnFile.Run.Set.Scan.Measurement;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.UThBearingEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author James F. Bowring
 */
public final class PrawnFileUtilities {

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

    /**
     * Builds sorted map based on index of mass station to MassStationDetail
     * from first run table and assumes all run tables have identical labels
     * across all runs.
     *
     * @param indexOfBackgroundSpecies the value of indexOfBackgroundSpecies
     * @param runs
     * @return the
     * java.util.Map<java.lang.Integer,org.cirdles.squid.shrimp.MassStationDetail>
     */
    public static Map<Integer, MassStationDetail> createMapOfIndexToMassStationDetails(int indexOfBackgroundSpecies, List<Run> runs) throws SquidException {
        Map<Integer, MassStationDetail> mapOfIndexToMassStationDetails = new TreeMap<>();

        if (!runs.isEmpty()) {
            // prepare map from first run table
            List<Entry> entries = runs.get(0).getRunTable().getEntry();
            int index = 0;
            for (Entry entry : entries) {
                String massStationLabel = entry.getPar().get(0).getValue();
                double atomicMassUnit = Double.parseDouble(entry.getPar().get(1).getValue());
                double centeringTimeSec = Double.parseDouble(entry.getPar().get(7).getValue());
                String isotopeLabel = new BigDecimal(atomicMassUnit).setScale(5, RoundingMode.HALF_UP).toPlainString();

                // upated July 2020
                Pattern pattern = Pattern.compile("^(\\d\\d)?(\\d)?(\\D+\\d?\\D?)*(\\d\\d\\d)?$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(massStationLabel);
                String elementLabel = massStationLabel;
                if (matcher.matches()) {
                    elementLabel = matcher.group(3);
                }

                boolean isBackground = (index == indexOfBackgroundSpecies);

                String uThBearingName = UThBearingEnum.N.getName();
                if (elementLabel.matches("(.*)(238|254|270|U)(.*)")) {
                    uThBearingName = UThBearingEnum.U.getName();
                } else if (elementLabel.matches("(.*)(232|248|264|Th)(.*)")) {
                    uThBearingName = UThBearingEnum.T.getName();
                }

                MassStationDetail massStationDetail
                        = new MassStationDetail(index, massStationLabel, centeringTimeSec, isotopeLabel, elementLabel, isBackground, uThBearingName);

                mapOfIndexToMassStationDetails.put(index, massStationDetail);
                index++;
            }

            // collect mass variations by time for all runs
            boolean detectedMassStationCountAnomaly = false;
            int runIndex = 1;
            for (Run run : runs) {
                long runStartTime = timeInMillisecondsOfRun(run);
                List<Scan> scans = run.getSet().getScan();
                for (Scan scan : scans) {
                    List<Measurement> measurements = scan.getMeasurement();
                    index = 0;
                    // assume measurements are in same order and length as  runtable mass station
                    if (measurements.size() != entries.size()) {
                        // inform user
                        detectedMassStationCountAnomaly = true;
                    }
                    for (Measurement measurement : measurements) {
                        try {
                            double trimMass = Double.parseDouble(measurement.getPar().get(1).getValue());
                            double timeStampSec = Double.parseDouble(measurement.getPar().get(2).getValue());
                            long measurementTime = runStartTime + (long) timeStampSec * 1000l;

                            MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(index);
                            massStationDetail.getMeasuredTrimMasses().add(trimMass);
                            massStationDetail.getTimesOfMeasuredTrimMasses().add((double) measurementTime);
                            massStationDetail.getIndicesOfScansAtMeasurementTimes().add((int) scan.getNumber());
                            massStationDetail.getIndicesOfRunsAtMeasurementTimes().add(runIndex);

                            index++;
                        } catch (NumberFormatException | NullPointerException numberFormatException) {
                            // likely not a uniform number of mass stations but we proceed anyway for now
                            // inform user
                            detectedMassStationCountAnomaly = true;
                        }
                    }
                }
                runIndex++;
            }

            if (detectedMassStationCountAnomaly) {
                throw new SquidException("Squid3 has detected that there are different mass station counts among the spot analyses.\n"
                        + "Please edit your data file to fix this issue.");
//                SquidMessageDialog.showWarningDialog(
//                        "Squid3 has detected that there are different mass station counts among the spot analyses.\n"
//                                + "Please edit your data file to fix this issue.",
//                        null);
            }
        }
        return mapOfIndexToMassStationDetails;
    }
}