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
package org.cirdles.squid.tasks.taskUtilities;

import java.util.Arrays;
import java.util.List;
import org.cirdles.ludwig.squid25.Utilities;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;

/**
 * Provides statistical operations on groups of spots.
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SpotGroupProcessor {

    /**
     * Modifies spotSummaryDetailsWM by providng a coherent subgroup of selected
     * spots
     *
     * @param task
     * @param spotSummaryDetailsWM contains the expression for the weighted mean
     * and the set of spots it acts on
     * @param minCoherentProbability
     * @see
     * https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Sub-FindCoherentGroup
     */
    public static void findCoherentGroupOfSpotsForWeightedMean(
            TaskInterface task,
            SpotSummaryDetails spotSummaryDetailsWM,
            double minCoherentProbability) {

        // make all spots selected then perform coherency standard
        // we don't care here if too few are chosen even though Ludwig's code does
        // TODO: currently our WM math requires 2 spots minimum - may consider returning simple avg for 1 or 2 spots?
        spotSummaryDetailsWM.rejectNone();
        
        int countOfAcceptedSpots = 0;
        for (int i = 0; i < spotSummaryDetailsWM.getRejectedIndices().length; i++) {
            // reject spot if value is less than or equal to the 1-sigma abs unct > 
            spotSummaryDetailsWM.getRejectedIndices()[i] = (spotSummaryDetailsWM.getValues()[0][0] <= Math.abs(spotSummaryDetailsWM.getValues()[0][1]));
            countOfAcceptedSpots += spotSummaryDetailsWM.getRejectedIndices()[i] ? 0 : 1;
        }
        
        ExpressionTreeInterface expressionTree = spotSummaryDetailsWM.getExpressionTree();
        String selectedExpressionName = spotSummaryDetailsWM.getSelectedExpressionName();
        List<ShrimpFractionExpressionInterface> spotsUsedForCalculation;
        
        double[][] values = new double[3][7];
        boolean doContinue = true;
        while (doContinue && (countOfAcceptedSpots > 2)) {
            
            spotsUsedForCalculation = spotSummaryDetailsWM.retrieveActiveSpots();
            
            try {
                values = convertObjectArrayToDoubles(expressionTree.eval(spotSummaryDetailsWM.retrieveActiveSpots(), task));

                // check probability
                if (values[0][5] < minCoherentProbability) {
                    // try to reject a spot
                    double[] valuesOfSelectedSpots = new double[spotsUsedForCalculation.size()];

                    // accumulate values from active spots
                    int indexOfAccumulator = 0;
                    int indexOfSpots = 0;
                    for (ShrimpFractionExpressionInterface spot : spotSummaryDetailsWM.getSelectedSpots()) {
                        if (!spotSummaryDetailsWM.getRejectedIndices()[indexOfSpots]) {
                            double[] valueAndUnctOfSelectedSpot;
                            if (selectedExpressionName.contains("/")) {
                                // case of raw ratios
                                valueAndUnctOfSelectedSpot = Arrays.stream(spot
                                        .getIsotopicRatioValuesByStringName(selectedExpressionName)).toArray(double[][]::new)[0];
                            } else {
                                valueAndUnctOfSelectedSpot = spot.getTaskExpressionsEvaluationsPerSpotByField(
                                        selectedExpressionName)[0];
                            }
                            valuesOfSelectedSpots[indexOfAccumulator] = valueAndUnctOfSelectedSpot[0];
                            indexOfAccumulator++;
                        }
                        indexOfSpots++;
                    }

                    // seek next spot to reject
                    double median = Utilities.median(valuesOfSelectedSpots);
                    double currentMaxResidual = 0;
                    int indexToReject = -1;
                    indexOfSpots = 0;
                    for (ShrimpFractionExpressionInterface spot : spotSummaryDetailsWM.getSelectedSpots()) {
                        // if spot not rejected, check its residual
                        if (!spotSummaryDetailsWM.getRejectedIndices()[indexOfSpots]) {
                            double[] valueAndUnctOfSelectedSpot;
                            if (selectedExpressionName.contains("/")) {
                                // case of raw ratios
                                valueAndUnctOfSelectedSpot = Arrays.stream(spot
                                        .getIsotopicRatioValuesByStringName(selectedExpressionName)).toArray(double[][]::new)[0];
                            } else {
                                valueAndUnctOfSelectedSpot = spot.getTaskExpressionsEvaluationsPerSpotByField(
                                        selectedExpressionName)[0];
                            }
                            double valueOfSelectedSpot = valueAndUnctOfSelectedSpot[0];
                            double oneSigmaAbsUnctOfSelectedSpot = valueAndUnctOfSelectedSpot[1];
                            double wtdResidual = Math.abs((valueOfSelectedSpot - median) / oneSigmaAbsUnctOfSelectedSpot);
                            if (wtdResidual > currentMaxResidual) {
                                currentMaxResidual = wtdResidual;
                                indexToReject = indexOfSpots;
                            }
                        }
                        indexOfSpots++;
                    }
                    
                    if (indexToReject > -1) {
                        // reject and repeat
                        spotSummaryDetailsWM.setIndexOfRejectedIndices(indexToReject, true);
                        countOfAcceptedSpots--;
                    } else {
                        // we are done since we did not reject any
                        doContinue = false;
                    }
                } else {
                    // passed the probability test
                    doContinue = false;
                }
            } catch (SquidException squidException) {
                // TODO: throw and provide feedback
                doContinue = false;
            }
        } // end of while
        // store current weighted mean
        try {
            values = convertObjectArrayToDoubles(expressionTree.eval(spotSummaryDetailsWM.retrieveActiveSpots(), task));
        } catch (SquidException squidException) {
        }
        spotSummaryDetailsWM.setValues(values);
    }
}
