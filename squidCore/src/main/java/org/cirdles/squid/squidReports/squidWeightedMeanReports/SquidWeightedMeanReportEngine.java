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
package org.cirdles.squid.squidReports.squidWeightedMeanReports;

import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;

/**
 * per @NicoleRayner Items I would like to see incorporated as headers into the
 * WM stats file: Sample Name * Value Name WM Value * 2s Err * 95% conf Err * n
 * (number of analyses included in mean)
 *
 * Included analysis ID (this is maybe not realistic but if this could
 * concatenate a list of the analysis names (e.g.1242-2.1, 1242-4.1, 1242-6.1)
 * used to calculate the WM as a string that would be useful. I know it would be
 * long and unwieldy but would permit other users to see exactly what analyses
 * were used in the calculation).
 *
 * MSWD * PoF * Min. Prob. (minimum prob of fit filter used)
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SquidWeightedMeanReportEngine {

    private SquidWeightedMeanReportEngine() {
    }

    public static String makeWeightedMeanReportAsCSV(SpotSummaryDetails spotSummaryDetails) {
        StringBuilder report = new StringBuilder();
        
        int countOfIncluded = 0;
        StringBuilder spots = new StringBuilder();
        for (int i = 0; i < spotSummaryDetails.getRejectedIndices().length; i++) {
            if (!spotSummaryDetails.getRejectedIndices()[i]){
                countOfIncluded++;
                spots.append(spotSummaryDetails.getSelectedSpots().get(i).getFractionID()).append(";");               
            }
        }
        
        boolean isAnAge = spotSummaryDetails.getSelectedExpressionName().contains("Age");
        report.append(spotSummaryDetails.getExpressionTree().getUnknownsGroupSampleName()).append(", ");
        report.append(spotSummaryDetails.getExpressionTree().getName().split("_WM_")[0]).append(", ");
        report.append(spotSummaryDetails.getValues()[0][0] / (isAnAge ? 1e6 : 1.0)).append(", ");
        report.append(spotSummaryDetails.getValues()[0][1] / (isAnAge ? 1e6 : 1.0) * 2.0).append(", ");
        report.append(spotSummaryDetails.getValues()[0][3] / (isAnAge ? 1e6 : 1.0)).append(", ");        
        report.append(countOfIncluded).append(", ");
        report.append(spotSummaryDetails.getRejectedIndices().length).append(", ");
        report.append(spotSummaryDetails.getValues()[0][4]).append(", ");
        report.append(spotSummaryDetails.getValues()[0][5]).append(", ");
        report.append(spotSummaryDetails.getMinProbabilityWM()).append(", ");
        report.append(spots);

        return report.toString();
    }

    public static String makeWeightedMeanReportHeaderAsCSV() {
        StringBuilder header = new StringBuilder();
        header.append("SampleName, WeightedMeanName, WeightedMean, 2sigma abs unct, 95% conf, n, N (total), MSWD, Prob of Fit, Min Prob, Spots");

        return header.toString();
    }

}
