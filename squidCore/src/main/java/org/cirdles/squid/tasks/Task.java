/*
 * Copyright 2006-2017 CIRDLES.org.
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
package org.cirdles.squid.tasks;

import com.google.common.primitives.Doubles;
import com.thoughtworks.xstream.XStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_ERROR_VALUE;
import org.cirdles.squid.algorithms.weightedMeans.WtdLinCorrResults;
import static org.cirdles.squid.algorithms.weightedMeans.WeightedMeanCalculators.wtdLinCorr;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.expressions.ExpressionTree;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeWithRatiosInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeXMLConverter;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.constants.ConstantNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.functions.Ln;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.operations.Add;
import org.cirdles.squid.tasks.expressions.operations.Divide;
import org.cirdles.squid.tasks.expressions.operations.Multiply;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.expressions.operations.OperationXMLConverter;
import org.cirdles.squid.tasks.expressions.operations.Pow;
import org.cirdles.squid.tasks.expressions.operations.Subtract;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;
import static org.cirdles.squid.tasks.expressions.ExpressionTreeInterface.convertObjectArrayToDoubles;
import org.cirdles.squid.tasks.expressions.functions.FunctionXMLConverter;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;
import org.cirdles.squid.tasks.expressions.variables.VariableXMLConverter;

/**
 *
 * @author James F. Bowring
 */
public class Task implements TaskInterface, XMLSerializerInterface {

    /**
     *
     */
    protected String name;

    /**
     *
     */
    protected List<ExpressionTreeInterface> taskExpressionsOrdered;

    /**
     *
     */
    protected Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet;

    protected transient SquidProject squidProject;

    /**
     *
     */
    public Task() {
        this("NoName", null);
    }

    /**
     *
     * @param name
     * @param squidProject
     */
    public Task(String name, SquidProject squidProject) {
        this.name = name;
        this.squidProject = squidProject;
        this.taskExpressionsOrdered = new ArrayList<>();
        this.taskExpressionsEvaluationsPerSpotSet = new TreeMap<>();
    }

    /**
     *
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
        xstream.alias("ShrimpSpeciesNode", ShrimpSpeciesNode.class);

        xstream.registerConverter(new ConstantNodeXMLConverter());
        xstream.alias("ConstantNode", ConstantNode.class);

        xstream.registerConverter(new VariableXMLConverter());
        xstream.alias("VariableNodeForSummary", VariableNodeForSummary.class);
        xstream.alias("VariableNodeForPerSpotTaskExpressions", VariableNodeForPerSpotTaskExpressions.class);
        xstream.alias("VariableNodeForIsotopicRatios", VariableNodeForIsotopicRatios.class);

        xstream.registerConverter(new OperationXMLConverter());
        xstream.registerConverter(new FunctionXMLConverter());

        xstream.registerConverter(new ExpressionTreeXMLConverter());
        xstream.alias("ExpressionTree", ExpressionTree.class);

        xstream.registerConverter(new TaskXMLConverter());
        xstream.alias("Task", Task.class);
        xstream.alias("Task", this.getClass());

        // Note: http://cristian.sulea.net/blog.php?p=2014-11-12-xstream-object-references
        xstream.setMode(XStream.NO_REFERENCES);

    }

    /**
     *
     * @param shrimpFractions
     */
    @Override
    public void evaluateTaskExpressions(List<ShrimpFractionExpressionInterface> shrimpFractions) {

        taskExpressionsEvaluationsPerSpotSet = new TreeMap<>();

        // setup spots
        shrimpFractions.forEach((spot) -> {
            List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsForScansEvaluated = new ArrayList<>();
            spot.setTaskExpressionsForScansEvaluated(taskExpressionsForScansEvaluated);
        });

        // subdivide spots
        List<ShrimpFractionExpressionInterface> referenceMaterialSpots = new ArrayList<>();
        List<ShrimpFractionExpressionInterface> unknownSpots = new ArrayList<>();
        shrimpFractions.forEach((spot) -> {
            if (spot.isReferenceMaterial()) {
                referenceMaterialSpots.add(spot);
            } else {
                unknownSpots.add(spot);
            }
        });

        for (ExpressionTreeInterface expression : taskExpressionsOrdered) {
            // determine subset of spots to be evaluated - default = all
            List<ShrimpFractionExpressionInterface> spotsForExpression = shrimpFractions;
            if (!((ExpressionTree) expression).isSquidSwitchSTReferenceMaterialCalculation()) {
                spotsForExpression = unknownSpots;
            }
            if (!((ExpressionTree) expression).isSquidSwitchSAUnknownCalculation()) {
                spotsForExpression = referenceMaterialSpots;
            }

            try {
                evaluateExpressionForSpotSet(expression, spotsForExpression);
            } catch (SquidException squidException) {
                // TODO - log and report failure of expression
//                JOptionPane.showMessageDialog(null,
//                        "Expression failed: " + expression.getName() + " because: " + squidException.getMessage());
            }
        }
    }

    private void evaluateExpressionForSpotSet(
            ExpressionTreeInterface expression,
            List<ShrimpFractionExpressionInterface> spotsForExpression) throws SquidException {
        // determine type of expression
        if (((ExpressionTree) expression).isSquidSwitchSCSummaryCalculation()) {
            double[][] value = convertObjectArrayToDoubles(expression.eval(spotsForExpression, this));
            taskExpressionsEvaluationsPerSpotSet.put(expression.getName(), new SpotSummaryDetails(value, spotsForExpression));
        } else {
            // perform expression on each spot
            for (ShrimpFractionExpressionInterface spot : spotsForExpression) {
                evaluateExpressionForSpot(expression, spot);
            }
        }
    }

    private void evaluateExpressionForSpot(
            ExpressionTreeInterface expression,
            ShrimpFractionExpressionInterface spot) throws SquidException {

        if (((ExpressionTree) expression).hasRatiosOfInterest()) {
            // case of Squid switch "NU"
            TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEvaluatedPerSpotPerScanModel
                    = evaluateTaskExpressionsPerSpotPerScan(expression, spot);
            // save scan-specific results
            spot.getTaskExpressionsForScansEvaluated().add(taskExpressionEvaluatedPerSpotPerScanModel);
            // save spot-specific results
            double[][] value = new double[][]{{taskExpressionEvaluatedPerSpotPerScanModel.getRatioVal(),
                taskExpressionEvaluatedPerSpotPerScanModel.getRatioFractErr()}};
            spot.getTaskExpressionsEvaluationsPerSpot().put(expression.getName(), value);
        } else {
            List<ShrimpFractionExpressionInterface> singleSpot = new ArrayList<>();
            singleSpot.add(spot);
            double[][] value = convertObjectArrayToDoubles(expression.eval(singleSpot, this));
            spot.getTaskExpressionsEvaluationsPerSpot().put(expression.getName(), value);
        }
    }

    /**
     * see https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Sub-EqnInterp
     *
     * @param expression
     * @param shrimpFraction
     */
    private TaskExpressionEvaluatedPerSpotPerScanModelInterface
            evaluateTaskExpressionsPerSpotPerScan(
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
                if (squidProject.findNumerator(ratiosOfInterest.get(i)) != null) {
                    isotopeIndices[2 * i] = squidProject.findNumerator(ratiosOfInterest.get(i)).getMassStationIndex();
                } else {
                    isotopeIndices[2 * i] = -1;
                }
                if (squidProject.findDenominator(ratiosOfInterest.get(i)) != null) {
                    isotopeIndices[2 * i + 1] = squidProject.findDenominator(ratiosOfInterest.get(i)).getMassStationIndex();
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

                        if (redPk1Ht == SQUID_ERROR_VALUE || redPk2Ht == SQUID_ERROR_VALUE) {
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
                                pkInterpFerr[scanNum][isotopeIndices[i]] = Math.sqrt((fractLessInterpTime * pkF1) * (fractLessInterpTime * pkF1)
                                        + (fractInterpTime * pkF2) * (fractInterpTime * pkF2));
                            }
                        }
                    }
                }

                // The next step is to evaluate the equation 'FormulaEval', 
                // documented separately, and approximate the uncertainties:
                shrimpFraction.setPkInterpScanArray(pkInterp[scanNum]);

                double eqValTmp = convertObjectArrayToDoubles(expression.eval(singleSpot, this))[0][0];

                double eqFerr;

                if (eqValTmp != 0.0) {
                    // numerical pertubation procedure
                    // EqPkUndupeOrd is here a List of the unique Isotopes in order of acquisition in the expression
                    Set<SquidSpeciesModel> eqPkUndupeOrd = squidProject.extractUniqueSpeciesNumbers(((ExpressionTreeWithRatiosInterface) expression).getRatiosOfInterest());
                    Iterator<SquidSpeciesModel> species = eqPkUndupeOrd.iterator();

                    double fVar = 0.0;
                    while (species.hasNext()) {
                        SquidSpeciesModel specie = species.next();
                        int unDupPkOrd = specie.getMassStationIndex();

                        // clone pkInterp[scanNum] for use in pertubation
                        double[] perturbed = pkInterp[scanNum].clone();
                        perturbed[unDupPkOrd] *= 1.0001;
                        shrimpFraction.setPkInterpScanArray(perturbed);

                        double pertVal = convertObjectArrayToDoubles(expression.eval(singleSpot, this))[0][0];

                        double fDelt = (pertVal - eqValTmp) / eqValTmp; // improvement suggested by Bodorkos
                        double tA = pkInterpFerr[scanNum][unDupPkOrd];
                        double tB = 1.0001 - 1.0;// --note that Excel 16-bit floating binary gives 9.9999999999989E-05    
                        double tC = fDelt * (tA / tB); // Bodorkos rescaled tc and td April 2017    fDelt * fDelt;
                        double tD = tC * tC;         // Bodorkos rescaled tc and td April 2017(tA / tB) * (tA / tB) * tC;
                        fVar += tD;// --fractional internal variance

                    } // end of visiting each isotope and perturbing equation

                    eqFerr = Math.sqrt(fVar);

                    // now that expression and its error are calculated
                    if (eqFerr != 0.0) {
                        eqValList.add(eqValTmp);
                        absErrList.add(Math.abs(eqFerr * eqValTmp));
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
                meanEqSig = Math.sqrt((midTime * sigmaSlope * midTime * sigmaSlope)//
                        + sigmaIntercept * sigmaIntercept //
                        + 2.0 * midTime * wtdLinCorrResults.getCovSlopeInter());

            } else {
                wtdLinCorrResults = wtdLinCorr(eqVal, sigRho, new double[0]);
                meanEq = wtdLinCorrResults.getIntercept();
                meanEqSig = wtdLinCorrResults.getSigmaIntercept();
            }

            double eqValFerr;
            if (meanEq == 0.0) {
                eqValFerr = 1.0;
            } else {
                eqValFerr = Math.abs(meanEqSig / meanEq);
            }

            // for consistency with Bodorkos documentation
            double[] ratEqVal = eqVal.clone();
            double[] ratEqTime = eqTime.clone();
            double[] ratEqErr = new double[eqVal.length];
            for (int i = 0; i < ratEqErr.length; i++) {
                ratEqErr[i] = Math.abs(eqVal[i] * fractErr[i]);
            }

            // April 20178 rounding of ratEqVal, meanEq, and eqValFerr occurs within this constructor
            taskExpressionEvaluatedPerSpotPerScanModel
                    = new TaskExpressionEvaluatedPerSpotPerScanModel(
                            expression, ratEqVal, ratEqTime, ratEqErr, meanEq, eqValFerr);
        }

        return taskExpressionEvaluatedPerSpotPerScanModel;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the taskExpressionsOrdered
     */
    @Override
    public List<ExpressionTreeInterface> getTaskExpressionsOrdered() {
        return taskExpressionsOrdered;
    }

    /**
     * @param taskExpressionsOrdered the taskExpressionsOrdered to set
     */
    public void setTaskExpressionsOrdered(List<ExpressionTreeInterface> taskExpressionsOrdered) {
        this.taskExpressionsOrdered = taskExpressionsOrdered;
    }

    /**
     * @return the taskExpressionsEvaluationsPerSpotSet
     */
    public Map<String, SpotSummaryDetails> getTaskExpressionsEvaluationsPerSpotSet() {
        return taskExpressionsEvaluationsPerSpotSet;
    }

    /**
     * @param taskExpressionsEvaluationsPerSpotSet the
     * taskExpressionsEvaluationsPerSpotSet to set
     */
    public void setTaskExpressionsEvaluationsPerSpotSet(Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet) {
        this.taskExpressionsEvaluationsPerSpotSet = taskExpressionsEvaluationsPerSpotSet;
    }
}
