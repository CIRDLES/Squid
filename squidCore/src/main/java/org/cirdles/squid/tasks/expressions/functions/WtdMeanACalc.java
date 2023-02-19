/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.functions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

import java.util.ArrayList;
import java.util.List;

import static org.cirdles.ludwig.isoplot3.Means.weightedAverage;
import static org.cirdles.ludwig.squid25.Resistant.fdNmad;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class WtdMeanACalc extends Function {

    private static final long serialVersionUID = -501759218931897288L;

    /**
     * Evaluates, for the Standard Reference Material, the weighted mean (and
     * associated parameters) of a set of common-Pb corrected calibration
     * constant values. This mean is the value to which all spot-by-spot
     * calibration constants determined for the unknowns will be calibrated, and
     * all spot-by-spot, 'directly-calculated daughter/parent dates' (i.e.
     * 206Pb/238U and/or 208Pb/232Th as appropriate) are calculated from there,
     * because SHRIMP (and secondary ion mass spectrometry in general) is an
     * indirect dating technique. Thus this is a critical step.
     * <p>
     * We modify Ludwig's VBA version so that the input is any set of values and
     * oneSigmaPercentUncertainties but plan for it to be called separately for
     * each of the 5 flavors of calibration constants, named here:
     * 4-corr206/238, 7-corr206/238, 8-corr206/238, 4-corr208/232,
     * 7-corr208/232.
     * <p>
     * In a break with Squid3 architecture, in order to preserve the results of
     * Squid25, the uncertainties input to this function are 1-sigma percent.
     * After the large rejection stage, the uncertainties are converted to
     * absolute for the balance of the calculations and 1-sigma absolute values
     * are returned per Squid3 architecture.
     * <p>
     * Note: the following values displayed in Squid25 can be calculated from
     * the outputs of this function.
     * <p>
     * 1sigma error of mean (%) = exterr68 / mean * 100
     * <p>
     * 95%-conf. err. of mean(%) = exterr95 / mean * 100
     * <p>
     * if externalFlag = 1.0, otherwise zero 1s external spot-to-spot error =
     * 1-sigmaAbs / mean * 100
     * <p>
     * <p>
     * Returns double[3][], where the indices listed in [1] and [2] are
     * zero-based rather than the Squid25 1-based values.
     *
     * @see https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Sub-WtdMeanAcalc
     */
    public WtdMeanACalc() {
        name = "WtdMeanACalc";
        argumentCount = 4;
        precedence = 4;
        rowCount = 3;
        colCount = 7;
        summaryCalc = true;
        labelsForOutputValues = new String[][]{
                {"mean", "1-sigmaAbs", "exterr68", "exterr95", "MSWD", "probability", "externalFlag"},
                {"LargeRej Indices"},
                {"WmeanRej Indices"}};
        labelsForInputValues = new String[]{"numbers", "oneSigmaPercentUncertainties", "noUPbConstAutoReject", "pbCanDriftCorr"};
        definition = "Evaluates, for the Standard Reference Material, the weighted mean (and\n"
                + DEF_TAB + "associated parameters) of a set of common-Pb corrected calibration\n"
                + DEF_TAB + "constant values. This mean is the value to which all spot-by-spot\n"
                + DEF_TAB + "calibration constants determined for the unknowns will be calibrated, and\n"
                + DEF_TAB + "all spot-by-spot, 'directly-calculated daughter/parent dates' (i.e.\n"
                + DEF_TAB + "206Pb/238U and/or 208Pb/232Th as appropriate) are calculated from there,\n"
                + DEF_TAB + "because SHRIMP (and secondary ion mass spectrometry in general) is an\n"
                + DEF_TAB + "indirect dating technique. Thus this is a critical step.\n"
                + DEF_TAB + "\n"
                + DEF_TAB + "We modify Ludwig's VBA version so that the input is any set of values and\n"
                + DEF_TAB + "oneSigmaPercentUncertainties but plan for it to be called separately for\n"
                + DEF_TAB + "each of the 5 flavors of calibration constants, named here:\n"
                + DEF_TAB + "4-corr206/238, 7-corr206/238, 8-corr206/238, 4-corr208/232,\n"
                + DEF_TAB + "7-corr208/232.\n"
                + DEF_TAB + "\n"
                + DEF_TAB + "In a break with Squid3 architecture, in order to preserve the results of\n"
                + DEF_TAB + "Squid25, the uncertainties input to this function are 1-sigma percent.\n"
                + DEF_TAB + "After the large rejection stage, the uncertainties are converted to\n"
                + DEF_TAB + "absolute for the balance of the calculations and 1-sigma absolute values\n"
                + DEF_TAB + "are returned per Squid3 architecture.\n"
                + DEF_TAB + "\n"
                + DEF_TAB + "Note: the following values displayed in Squid25 can be calculated from\n"
                + DEF_TAB + "the outputs of this function.\n"
                + DEF_TAB + "\n"
                + DEF_TAB + "1sigma%errorOfMean = err68 / mean * 100\n"
                + DEF_TAB + "\n"
                + DEF_TAB + "95%-conf. err. of mean(%) = err95 / mean * 100\n"
                + DEF_TAB + "\n"
                + DEF_TAB + "if externalFlag = 1.0, otherwise zero 1s external spot-to-spot error =\n"
                + DEF_TAB + "1-sigmaAbs / mean * 100\n"
                + DEF_TAB + "\n"
                + DEF_TAB + "Returns double[3][], where the indices listed in [1] and [2] are\n"
                + DEF_TAB + "zero-based rather than the Squid25 1-based values.\n";
    }

    /**
     * Evaluates, for the Standard Reference Material, the weighted mean (and
     * associated parameters) of a set of common-Pb corrected calibration
     * constant values. This mean is the value to which all spot-by-spot
     * calibration constants determined for the unknowns will be calibrated, and
     * all spot-by-spot, 'directly-calculated daughter/parent dates' (i.e.
     * 206Pb/238U and/or 208Pb/232Th as appropriate) are calculated from there,
     * because SHRIMP (and secondary ion mass spectrometry in general) is an
     * indirect dating technique. Thus this is a critical step.
     * <p>
     * We modify Ludwig's VBA version so that the input is any set of values and
     * oneSigmaPercentUncertainties but plan for it to be called for both
     * flavors of calibration constants, 8/32 and 6/38 named here:
     * UncorrPb/Uconst and UncorrPb/Thconst.
     * <p>
     * In keeping with Squid3 architecture, noramlly all
     * oneSigmaPercentUncertainties are input and output as one-sigma absolute;
     * however, here we input oneSigmaPercentUncertainties as 1-sigma percents
     * because of an assumption in the mean error calculations made by Ludwig.
     *
     * @param values
     * @param oneSigmaPctUnct
     * @param noUPbConstAutoReject
     * @param pbCanDriftCorr
     * @return double[3][] where [0] contains results of
     * Ludwig.Isoplot3.Means.weightedAverage and [1] contains indices of large
     * rejection process and [2] contains indices of weightedAverage rejection
     * process.
     */
    public static double[][] wtdMeanAcalc(double[] values, double[] oneSigmaPctUnct, boolean noUPbConstAutoReject, boolean pbCanDriftCorr) {
        double[][] retVal;

        int countOfValues = values.length;

        boolean noReject = (noUPbConstAutoReject && !pbCanDriftCorr);

        // create a double array - selectionArray - that works as a boolean array with 1s for active values and 0s for Large errrejected
        double[] selectionArray = new double[countOfValues];
        // create two index arrays to return rejected indices
        double[] largeErrRejIndexArray;
        double[] wmErrRejIndexArray;

        // first get fdNmad of uncertainty column
        double medianEr = org.cirdles.ludwig.squid25.Utilities.median(oneSigmaPctUnct);
        double nMadd = fdNmad(oneSigmaPctUnct)[0];

        List<Double> largeErrRejIndexList = new ArrayList<>();
        int largeErRegN = 0;

        // perform rejections if allowed
        if (!noReject) {
            for (int i = 0; i < countOfValues; i++) {
                if ((StrictMath.abs(oneSigmaPctUnct[i] - medianEr) > 10.0 * nMadd) || (oneSigmaPctUnct[i] == 0)) {
                    largeErRegN++;
                    largeErrRejIndexList.add((double) i);
                }
            }
        } // test noReject

        if (pbCanDriftCorr) {
            // [out-of-scope stuff]
            retVal = new double[][]{{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, {}, {}};
        } else {
            /**
             * Here we implement the functionality of wtdAv. see
             * https://github.com/CIRDLES/LudwigLibrary/blob/master/vbaCode/isoplot3Basic/Pub.bas
             * called in Ludwig as: WtdAv Adatrange, TRUE, TRUE , 1, (NOT
             * NoReject), TRUE, 1; as values, percentOut, percentIn,
             * sigmaLevelIn, canReject, constantExtError, sigmaLevelOut.
             * However, we implement wtdAv assuming WtdAv Adarange, FALSE, TRUE,
             * 1, (NOT noUPbConstAutoRejectWe), TRUE, 1. elect to keep
             * ConstExtErr true and report all calculated outputs of
             * WeightedAverage.
             */

            // convert to abs oneSigmaPercentUncertainties
            double[] oneSigmaAbsUnct = oneSigmaPctUnct.clone();
            for (int i = 0; i < values.length; i++) {
                oneSigmaAbsUnct[i] = (values[i] * oneSigmaPctUnct[i]) / 100.0;
            }

            // first shrink data set by removing large rejections
            double[] valuesKeep = new double[countOfValues - largeErRegN];
            double[] oneSigmaAbsUnctKeep = new double[countOfValues - largeErRegN];

            int indexCounter = 0;
            for (int i = 0; i < countOfValues; i++) {
                if (!largeErrRejIndexList.contains((double) i)) {
                    valuesKeep[indexCounter] = values[i];
                    oneSigmaAbsUnctKeep[indexCounter] = oneSigmaAbsUnct[i];
                    selectionArray[i] = 1;
                    indexCounter++;
                } else {
                    selectionArray[i] = 0;
                }
            }

            // calculate weightedAverage
            double[][] ww = weightedAverage(valuesKeep, oneSigmaAbsUnctKeep, !noReject, false);

            // detect any selectionArray value rejected by weightedAverage
            // walk returned values array where rejects are 0 and
            // determine the original index in selectionArray and record it
            List<Double> wmErrRejIndexList = new ArrayList<>();
            double[] retVals = ww[1];
            for (int rejIndex = 0; rejIndex < retVals.length; rejIndex++) {
                if (retVals[rejIndex] == 0.0) {
                    // find the rejIndex-th plus 1 "1" in bitarray and flip
                    int counter = -1; // to align with zero-based indexing
                    for (int i = 0; i < selectionArray.length; i++) {
                        if (selectionArray[i] == 1) {
                            counter++;
                            if (counter == rejIndex) {
                                wmErrRejIndexList.add((double) i);
                            }
                        }
                    }
                }
            }

            // prepare return arrays of rejected indices
            largeErrRejIndexArray = largeErrRejIndexList.stream().mapToDouble(Double::doubleValue).toArray();
            wmErrRejIndexArray = wmErrRejIndexList.stream().mapToDouble(Double::doubleValue).toArray();

            retVal = new double[][]{ww[0], largeErrRejIndexArray, wmErrRejIndexArray};
        }

        return retVal;

    }

    /**
     * Requires that child 0 and 1 are VariableNodes that evaluate to double
     * arrays each with one column and a row for each member of shrimpFractions
     * and that child 2, 3 are ConstantNodes that evaluate to true or false
     * denoting uncertaintiesInPercent, noUPbConstAutoReject.
     *
     * @param childrenET      list containing child 0, child 1, child 2, child 3,
     *                        child 4
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[3][n] where [0] is array of intMean, intSigmaMean,
     * MSWD, probability, intErr68, intMeanErr95, extMean, extSigma,
     * extMeanErr68, extMeanErr95}, [1] is array of large rejection indices, and
     * [2] is array of weighted mean additional rejected indices.
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            Object[][] values = childrenET.get(0).eval(shrimpFractions, task);
            double[] variableValues = transposeColumnVectorOfDoubles(values, 0);

            // it is required that uncertainties supplied to this method are in percent
            Object[][] oneSigmaPercentUncertainties = childrenET.get(1).eval(shrimpFractions, task);
            double[] oneSigmaPercentUncertaintyValues = transposeColumnVectorOfDoubles(oneSigmaPercentUncertainties, 0);

            Object noUPbConstAutoRejectO = childrenET.get(2).eval(shrimpFractions, task)[0][0];
            boolean noUPbConstAutoReject = (boolean) noUPbConstAutoRejectO;

            Object pbCanDriftCorrO = childrenET.get(3).eval(shrimpFractions, task)[0][0];
            boolean pbCanDriftCorr = (boolean) pbCanDriftCorrO;

            double[][] weightedAverage = wtdMeanAcalc(variableValues, oneSigmaPercentUncertaintyValues, noUPbConstAutoReject, pbCanDriftCorr);
            retVal = new Object[][]{convertArrayToObjects(weightedAverage[0]), convertArrayToObjects(weightedAverage[1]), convertArrayToObjects(weightedAverage[2])};
        } catch (ArithmeticException | NullPointerException e) {
            retVal = new Object[][]{{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, {}, {}};
        }

        return retVal;
    }

    /**
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        String retVal
                = "<mrow>"
                + "<mi>" + name + "</mi>"
                + "<mfenced>";

        retVal += buildChildrenToMathML(childrenET);

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}