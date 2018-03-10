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
import java.util.ArrayList;
import java.util.List;
import static org.cirdles.ludwig.isoplot3.Means.weightedAverage;
import static org.cirdles.ludwig.squid25.Resistant.fdNmad;
import static org.cirdles.ludwig.squid25.Utilities.median;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;

/**
 *
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
     *
     * We modify Ludwig's VBA version so that the input is any set of values and
     * uncertainties but plan for it to be called for both flavors of
     * calibration constants, 8/32 and 6/38 named here: UncorrPb/Uconst and
     * UncorrPb/Thconst.
     *
     * In keeping with Squid3 architecture, all uncertainties are input and
     * output as one-sigma absolute.
     *
     * @see https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Sub-WtdMeanAcalc
     */
    public WtdMeanACalc() {
        name = "WtdMeanACalc";
        argumentCount = 5;
        precedence = 4;
        rowCount = 3;
        colCount = 11;
        labelsForOutputValues = new String[][]{
            {"intMean", "intSigmaMean", "MSWD", "probability", "intErr68",
                "intMeanErr95", "extMean", "extSigma", "extMeanErr68", "extMeanErr95", "WMrejects"},
            {"LargeRej Indices"},
            {"WmeanRej Indices"}};
    }

    /**
     * Requires that child 0 and 1 are VariableNodes that evaluate to double
     * arrays each with one column and a row for each member of shrimpFractions
     * and that child 2, 3, 4 are ConstantNodes that evaluate to true or false
     * denoting uncertaintiesInPercent, noUPbConstAutoReject, and pbCanDriftCorr
     *
     * @param childrenET list containing child 0, child 1, child 2, child 3,
     * child 4
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][11] array of intMean, intSigmaMean, MSWD,
     * probability, intErr68, intMeanErr95, extMean, extSigma, extMeanErr68,
     * extMeanErr95, rejectsCount}
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            Object[][] values = childrenET.get(0).eval(shrimpFractions, task);
            double[] variableValues = transposeColumnVectorOfDoubles(values, 0);

            Object[][] uncertainties = childrenET.get(1).eval(shrimpFractions, task);
            double[] uncertaintyValues = transposeColumnVectorOfDoubles(uncertainties, 0);

            Object uncertaintiesInPercent0 = childrenET.get(2).eval(shrimpFractions, task)[0][0];
            boolean uncertaintiesInPercent = (boolean) uncertaintiesInPercent0;

            Object noUPbConstAutoRejectO = childrenET.get(3).eval(shrimpFractions, task)[0][0];
            boolean noUPbConstAutoReject = (boolean) noUPbConstAutoRejectO;

            Object pbCanDriftCorrO = childrenET.get(4).eval(shrimpFractions, task)[0][0];
            boolean pbCanDriftCorr = (boolean) pbCanDriftCorrO;

            double[] absUnct = uncertaintyValues.clone();
            if (uncertaintiesInPercent) {
                for (int i = 0; i < values.length; i++) {
                    absUnct[i] = (uncertaintyValues[i] * variableValues[i]) / 100.0;
                }
            }

            double[][] weightedAverage = wtdMeanAcalc(variableValues, absUnct, noUPbConstAutoReject, pbCanDriftCorr);
            retVal = new Object[][]{convertArrayToObjects(weightedAverage[0]), convertArrayToObjects(weightedAverage[1]), convertArrayToObjects(weightedAverage[2])};
        } catch (ArithmeticException | NullPointerException e) {
            retVal = new Object[][]{{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}};
        }

        return retVal;
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        String retVal
                = "<mrow>"
                + "<mi>" + name + "</mi>"
                + "<mfenced>";

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
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
     *
     * We modify Ludwig's VBA version so that the input is any set of values and
     * uncertainties but plan for it to be called for both flavors of
     * calibration constants, 8/32 and 6/38 named here: UncorrPb/Uconst and
     * UncorrPb/Thconst.
     *
     * In keeping with Squid3 architecture, all uncertainties are input and
     * output as one-sigma absolute.
     *
     * @param values
     * @param oneSigmaAbsUnct
     * @param noUPbConstAutoReject
     * @param pbCanDriftCorr
     * @return double[3][] where [0] contains results of
     * Ludwig.Isoplot3.Means.weightedAverage and [1] contains indices of large
     * rejection process and [2] contains indices of weightedAverage rejection
     * process.
     */
    public static double[][] wtdMeanAcalc(double[] values, double[] oneSigmaAbsUnct, boolean noUPbConstAutoReject, boolean pbCanDriftCorr) {
        int countOfValues = values.length;

        boolean noReject = (noUPbConstAutoReject && !pbCanDriftCorr);

        double[][] ww = new double[2][1];

        // create a double array - selectionArray - that works as a boolean array with 1s for active values and 0s for Large errrejected
        double[] selectionArray = new double[countOfValues];
        // create two index arrays to return rejected indices
        double[] largeErrRejIndexArray = new double[0];
        double[] wmErrRejIndexArray = new double[0];

        // first get fdNmad of uncertainty column 
        double medianEr = median(oneSigmaAbsUnct);
        double nMadd = fdNmad(oneSigmaAbsUnct)[0];

        List<Double> largeErrRejIndexList = new ArrayList<>();
        int largeErRegN = 0;

        // perform rejections if allowed
        if (!noReject) {
            for (int i = 0; i < countOfValues; i++) {
                if ((Math.abs(oneSigmaAbsUnct[i] - medianEr) > 10.0 * nMadd) || (oneSigmaAbsUnct[i] == 0)) {
                    largeErRegN++;
                    largeErrRejIndexList.add((double) i);
                }
            }
        } // test noReject

        if (pbCanDriftCorr) {
            // [out-of-scope stuff]  
        } else {
            /**
             * Here we implement the functionality of wtdAv. see
             * https://github.com/CIRDLES/LudwigLibrary/blob/master/vbaCode/isoplot3Basic/Pub.bas
             * called as: WtdAv Adatrange, TRUE, TRUE , 1, (NOT NoReject), TRUE,
             * 1 We elect to keep ConstExtErr true and report all calculated
             * outputs of WeightedAverage.
             */

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
            ww = weightedAverage(valuesKeep, oneSigmaAbsUnctKeep, !noReject, false);

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
        }

        double[][] retVal = new double[][]{ww[0], largeErrRejIndexArray, wmErrRejIndexArray};

        return retVal;

    }

}
