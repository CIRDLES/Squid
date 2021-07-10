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
package org.cirdles.squid.tasks.evaluationEngines;

import com.google.common.primitives.Doubles;
import org.cirdles.squid.algorithms.weightedMeans.WtdLinCorrResults;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWithRatiosInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_EPSILON;
import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_ERROR_VALUE;
import static org.cirdles.squid.algorithms.weightedMeans.WeightedMeanCalculators.wtdLinCorr;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;

/**
 * @author James F. Bowring
 */
public class ExpressionEvaluator {

    public ExpressionEvaluator() {
    }

    /**
     * see https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Sub-EqnInterp
     *
     * @param task
     * @param expression
     * @param shrimpFraction
     * @return
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    public TaskExpressionEvaluatedPerSpotPerScanModelInterface
    evaluateTaskExpressionsPerSpotPerScan(
            Task task,
            ExpressionTreeInterface expression,
            ShrimpFractionExpressionInterface shrimpFraction) throws SquidException {

        TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEvaluatedPerSpotPerScanModel = null;
        if (shrimpFraction != null) {

            // construct argument list of one spot
            List<ShrimpFractionExpressionInterface> singleSpot = new ArrayList<>();
            singleSpot.add(shrimpFraction);

            // first have to build pkInterp etc per expression and then evaluate by scan
            List<String> ratiosOfInterest = ((ExpressionTreeWithRatiosInterface) expression).getRatiosOfInterest();

            int[] isotopeIndices = new int[ratiosOfInterest.size() * 2];
            for (int i = 0; i < ratiosOfInterest.size(); i++) {
                if (task.findNumerator(ratiosOfInterest.get(i)) != null) {
                    isotopeIndices[2 * i] = task.findNumerator(ratiosOfInterest.get(i)).getMassStationIndex();
                } else {
                    isotopeIndices[2 * i] = -1;
                }
                if (task.findDenominator(ratiosOfInterest.get(i)) != null) {
                    isotopeIndices[2 * i + 1] = task.findDenominator(ratiosOfInterest.get(i)).getMassStationIndex();
                } else {
                    isotopeIndices[2 * i + 1] = -1;
                }
            }

            //TODO June 2017 temp hack until expression checking is in place
            for (int i = 0; i < isotopeIndices.length; i++) {
                if (isotopeIndices[i] == -1) {
                    throw new SquidException("Missing Isotope");
                }
            }

            int sIndx = shrimpFraction.getReducedPkHt().length - 1;
            double[][] pkInterp = new double[sIndx][shrimpFraction.getReducedPkHt()[0].length];
            double[][] pkInterpFerr = new double[sIndx][shrimpFraction.getReducedPkHt()[0].length];
            boolean singleScan = (sIndx == 1);
            double interpTime = 0.0;

            List<Double> eqValList = new ArrayList<>();
            List<Double> fractErrList = new ArrayList<>();
            List<Double> absErrList = new ArrayList<>();
            List<Double> eqTimeList = new ArrayList<>();

            for (int scanNum = 0; scanNum < sIndx; scanNum++) {
                boolean doProceed = true;
                if (!singleScan) {
                    double interpTimeSpan = 0.0;
                    for (int i = 0; i < isotopeIndices.length; i++) {
                        interpTimeSpan
                                += shrimpFraction.getTimeStampSec()[scanNum][isotopeIndices[i]]
                                + shrimpFraction.getTimeStampSec()[scanNum + 1][isotopeIndices[i]];
                    }
                    interpTime = interpTimeSpan / isotopeIndices.length / 2.0;
                } // end check singleScan

                for (int i = 0; i < isotopeIndices.length; i++) {
                    double fractInterpTime = 0.0;
                    double fractLessInterpTime = 0.0;
                    double redPk2Ht = 0.0;

                    if (!singleScan) {
                        // default value
                        pkInterp[scanNum][isotopeIndices[i]] = SQUID_ERROR_VALUE;
                        double pkTdelt
                                = shrimpFraction.getTimeStampSec()[scanNum + 1][isotopeIndices[i]]
                                - shrimpFraction.getTimeStampSec()[scanNum][isotopeIndices[i]];

                        doProceed = (pkTdelt > 0.0);

                        if (doProceed) {
                            fractInterpTime = (interpTime - shrimpFraction.getTimeStampSec()[scanNum][isotopeIndices[i]]) / pkTdelt;
                            fractLessInterpTime = 1.0 - fractInterpTime;
                            redPk2Ht = shrimpFraction.getReducedPkHt()[scanNum + 1][isotopeIndices[i]];
                        }
                    } // end check singleScan
                    if (doProceed) {
                        double redPk1Ht = shrimpFraction.getReducedPkHt()[scanNum][isotopeIndices[i]];

                        if (StrictMath.abs(redPk1Ht - SQUID_ERROR_VALUE) < SQUID_EPSILON || StrictMath.abs(redPk2Ht - SQUID_ERROR_VALUE) < SQUID_EPSILON) {
                            doProceed = false;
                        }

                        if (doProceed) {
                            double pkF1 = shrimpFraction.getReducedPkHtFerr()[scanNum][isotopeIndices[i]];

                            if (singleScan) {
                                pkInterp[scanNum][isotopeIndices[i]] = redPk1Ht;
                                pkInterpFerr[scanNum][isotopeIndices[i]] = pkF1;
                            } else {
                                pkInterp[scanNum][isotopeIndices[i]] = (fractLessInterpTime * redPk1Ht) + (fractInterpTime * redPk2Ht);
                                double pkF2 = shrimpFraction.getReducedPkHtFerr()[scanNum + 1][isotopeIndices[i]];
                                pkInterpFerr[scanNum][isotopeIndices[i]] = StrictMath.sqrt((fractLessInterpTime * pkF1) * (fractLessInterpTime * pkF1)
                                        + (fractInterpTime * pkF2) * (fractInterpTime * pkF2));
                            }
                        }
                    }
                }

                // The next step is to evaluate the equation 'FormulaEval', 
                // documented separately, and approximate the uncertainties:
                shrimpFraction.setPkInterpScanArray(pkInterp[scanNum]);

                double eqValTmp = convertObjectArrayToDoubles(expression.eval(singleSpot, task))[0][0];

                double eqFerr;

                if (eqValTmp != 0.0) {
                    // numerical pertubation procedure
                    // EqPkUndupeOrd is here a List of the unique Isotopes in order of acquisition in the expression
                    Set<SquidSpeciesModel> eqPkUndupeOrd = task.extractUniqueSpeciesNumbers(((ExpressionTreeWithRatiosInterface) expression).getRatiosOfInterest());
                    Iterator<SquidSpeciesModel> species = eqPkUndupeOrd.iterator();

                    double fVar = 0.0;
                    while (species.hasNext()) {
                        SquidSpeciesModel specie = species.next();
                        int unDupPkOrd = specie.getMassStationIndex();

                        // clone pkInterp[scanNum] for use in pertubation
                        double[] perturbed = pkInterp[scanNum].clone();
                        perturbed[unDupPkOrd] *= 1.0001;
                        shrimpFraction.setPkInterpScanArray(perturbed);

                        double pertVal = convertObjectArrayToDoubles(expression.eval(singleSpot, task))[0][0];

                        double fDelt = (pertVal - eqValTmp) / eqValTmp; // improvement suggested by Bodorkos
                        double tA = pkInterpFerr[scanNum][unDupPkOrd];
                        double tB = 1.0001 - 1.0;// --note that Excel 16-bit floating binary gives 9.9999999999989E-05    
                        double tC = fDelt * (tA / tB); // Bodorkos rescaled tc and td April 2017    fDelt * fDelt;
                        double tD = tC * tC;         // Bodorkos rescaled tc and td April 2017(tA / tB) * (tA / tB) * tC;
                        fVar += tD;// --fractional internal variance

                    } // end of visiting each isotope and perturbing equation

                    eqFerr = StrictMath.sqrt(fVar);

                    // now that expression and its error are calculated
                    if (eqFerr != 0.0) {
                        eqValList.add(eqValTmp);
                        absErrList.add(StrictMath.abs(eqFerr * eqValTmp));
                        fractErrList.add(eqFerr);
                        double totRatTime = 0.0;
                        int numPksInclDupes = 0;

                        // reset iterator
                        species = eqPkUndupeOrd.iterator();
                        while (species.hasNext()) {
                            int unDupPkOrd = species.next().getMassStationIndex();

                            totRatTime += shrimpFraction.getTimeStampSec()[scanNum][unDupPkOrd];
                            numPksInclDupes++;

                            totRatTime += shrimpFraction.getTimeStampSec()[scanNum + 1][unDupPkOrd];
                            numPksInclDupes++;
                        }
                        eqTimeList.add(totRatTime / numPksInclDupes);
                    }
                } // end test of eqValTmp != 0.0 VBA calls this a bailout and has no logic

            } // end scanNum loop

            // The final step is to assemble outputs EqTime, EqVal and AbsErr, and
            // to define SigRho as input for the use of subroutine WtdLinCorr and its sub-subroutines: 
            // convert to arrays
            double[] eqVal = Doubles.toArray(eqValList);
            double[] absErr = Doubles.toArray(absErrList);
            double[] fractErr = Doubles.toArray(fractErrList);
            double[] eqTime = Doubles.toArray(eqTimeList);
            double[][] sigRho = new double[eqVal.length][eqVal.length];

            for (int i = 0; i < sigRho.length; i++) {
                sigRho[i][i] = absErr[i];
                if (i > 0) {
                    sigRho[i][i - 1] = 0.25;
                    sigRho[i - 1][i] = 0.25;
                }
            }

            WtdLinCorrResults wtdLinCorrResults;
            double meanEq;
            double meanEqSig;

            if (shrimpFraction.isUserLinFits() && eqVal.length > 3) {
                wtdLinCorrResults = wtdLinCorr(eqVal, sigRho, eqTime);

                double midTime
                        = (shrimpFraction.getTimeStampSec()[sIndx][shrimpFraction.getReducedPkHt()[0].length - 1]
                        + shrimpFraction.getTimeStampSec()[0][0]) / 2.0;
                double slope = wtdLinCorrResults.getSlope();
                double sigmaSlope = wtdLinCorrResults.getSigmaSlope();
                double sigmaIntercept = wtdLinCorrResults.getSigmaIntercept();

                meanEq = (slope * midTime) + wtdLinCorrResults.getIntercept();
                meanEqSig = StrictMath.sqrt((midTime * sigmaSlope * midTime * sigmaSlope)//
                        + sigmaIntercept * sigmaIntercept //
                        + 2.0 * midTime * wtdLinCorrResults.getCovSlopeInter());

            } else {
                wtdLinCorrResults = wtdLinCorr(eqVal, sigRho, new double[0]);
                meanEq = wtdLinCorrResults.getIntercept();
                meanEqSig = wtdLinCorrResults.getSigmaIntercept();
            }

            // this calculates the percent uncertainty without the 100 multiplier
            // sept 2017 we are going to store the 1 sigma unct of meanEqSig instead
//            double eqValFerr;
//            if (meanEq == 0.0) {
//                eqValFerr = 1.0;
//            } else {
//                eqValFerr = StrictMath.abs(meanEqSig / meanEq);
//            }

            // for consistency with Bodorkos documentation
            double[] ratEqVal = eqVal.clone();
            double[] ratEqTime = eqTime.clone();
            double[] ratEqErr = new double[eqVal.length];
            for (int i = 0; i < ratEqErr.length; i++) {
                // calculates 1 sigma abs unct
                ratEqErr[i] = StrictMath.abs(eqVal[i] * fractErr[i]);
            }

//            // April 2017 rounding of ratEqVal, meanEq, and eqValFerr occurs within this constructor
//            taskExpressionEvaluatedPerSpotPerScanModel
//                    = new TaskExpressionEvaluatedPerSpotPerScanModel(
//                            expression, ratEqVal, ratEqTime, ratEqErr, meanEq, eqValFerr);
//            
            // Sept 2017 rounding of ratEqVal, meanEq, and meanEqSig occurs within this constructor
            taskExpressionEvaluatedPerSpotPerScanModel
                    = new TaskExpressionEvaluatedPerSpotPerScanModel(
                    expression, ratEqVal, ratEqTime, ratEqErr, meanEq, meanEqSig);
        }

        return taskExpressionEvaluatedPerSpotPerScanModel;
    }

}