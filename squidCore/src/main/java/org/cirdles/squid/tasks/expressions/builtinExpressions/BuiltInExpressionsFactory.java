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
package org.cirdles.squid.tasks.expressions.builtinExpressions;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_MEAN_PPM_PARENT_NAME;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_PPM_PARENT_EQN_NAME;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_PPM_PARENT_EQN_NAME_TH;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_PPM_PARENT_EQN_NAME_U;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_TH_U_EQN_NAME;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class BuiltInExpressionsFactory {

    /**
     * TODO: these guys are hard coded for now, but need to be calculated from
     * reference materials, physical constants, etc.
     *
     * @return
     */
    public static Map<String, ExpressionTreeInterface> generateConstants() {
        Map<String, ExpressionTreeInterface> constants = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        ExpressionTreeInterface r238_235s = new ConstantNode("r238_235s", 137.88);
        constants.put(r238_235s.getName(), r238_235s);

        return constants;
    }

    /**
     * TODO: these guys are hard coded for now, but need to be calculated from
     * reference materials, physical constants, etc.
     *
     * @return
     */
    public static Map<String, ExpressionTreeInterface> generateParameters() {
        Map<String, ExpressionTreeInterface> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        ExpressionTreeInterface stdUPbRatio = new ConstantNode("StdUPbRatio", 0.0906025999827849);
        parameters.put(stdUPbRatio.getName(), stdUPbRatio);

        ExpressionTreeInterface stdPpmU = new ConstantNode("Std_ppmU", 903);
        parameters.put(stdPpmU.getName(), stdPpmU);

        ExpressionTreeInterface std_76 = new ConstantNode("Std_76", 0.0587838486664528);
        parameters.put(std_76.getName(), std_76);

        ExpressionTreeInterface stdThPbRatio = new ConstantNode("StdThPbRatio", 0.0280476031222372);
        parameters.put(stdThPbRatio.getName(), stdThPbRatio);

        ExpressionTreeInterface stdRad86fact = new ConstantNode("StdRad86fact", 0.309567309630921);
        parameters.put(stdRad86fact.getName(), stdRad86fact);

        ExpressionTreeInterface sComm_74 = new ConstantNode("sComm_74", 15.5773361);
        parameters.put(sComm_74.getName(), sComm_74);

        ExpressionTreeInterface sComm_64 = new ConstantNode("sComm_64", 17.821);
        parameters.put(sComm_64.getName(), sComm_64);

        ExpressionTreeInterface sComm_84 = new ConstantNode("sComm_84", 37.5933995);
        parameters.put(sComm_84.getName(), sComm_84);

        return parameters;
    }

    public static SortedSet<Expression> generatePpmUandPpmTh(String primaryParentElement) {
        SortedSet<Expression> concentrationExpressionsOrdered = new TreeSet<>();

        // TODO: promote this and tie to physical constants model
        String uConstant = "((238/232) * r238_235s / (r238_235s - 1.0))";

        String ppmEqnName = SQUID_PPM_PARENT_EQN_NAME_U;
        String ppmEquation = "[\"" + SQUID_PPM_PARENT_EQN_NAME + "\"] / [\"" + SQUID_MEAN_PPM_PARENT_NAME + "\"] * Std_ppmU";

        String ppmOtherEqnName = SQUID_PPM_PARENT_EQN_NAME_TH;
        String ppmOtherEqn = "[\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"] * [\"" + SQUID_TH_U_EQN_NAME + "\"] / " + uConstant;

        if (primaryParentElement.contains("232")) {
            ppmEqnName = SQUID_PPM_PARENT_EQN_NAME_TH;

            ppmOtherEqnName = SQUID_PPM_PARENT_EQN_NAME_U;
            ppmOtherEqn = "[\"" + SQUID_PPM_PARENT_EQN_NAME_TH + "\"] / [\"" + SQUID_TH_U_EQN_NAME + "\"] * " + uConstant;
        }

        Expression expressionPpmU = buildExpression(
                ppmEqnName,
                ppmEquation, true, true);
        concentrationExpressionsOrdered.add(expressionPpmU);

        Expression expressionPpmTh = buildExpression(
                ppmOtherEqnName,
                ppmOtherEqn, true, true);
        concentrationExpressionsOrdered.add(expressionPpmTh);
        // Ludwig Q3 - additional equations to calculate 232Th/238U
        /* NEED SIMON'S HELP
            Else --i.e. if piNumDauPar = 2
            --i.e. Switch.DirectAltPD = TRUE and therefore Secondary  
            --U-Th/Pb expression (EqNum = -2) is NOT NULL  

            If pbStd = FALSE --i.e. sample analysis  
            Tot68_82_fromA plSpotOutputRw  
            --Produces calculated 232Th/238U, total 206/238, and total  
            --208/232 from calib. constants A(206/238) and A(208/232)  
            --subroutine documented separately  
            End If  

            ThUfromA1A2 pbStd, plSpotOutputRw, TRUE --following from Ludwig:  
            --NOTE: must recalc later because WtdMeanA1/2 range doesn't yet exist
            --subroutine documented separately  

            If pbStd = FALSE --i.e. sample analysis  
            SecondaryParentPpmFromThU pbStd, plSpotOutputRw  
            --subroutine documented separately   
            End If  
      
  End If
        
        
         */
        // feb 2018
        // logic for Squid 2.50 Sub: Tot68_82_FromA (see comment immediately above)
        /*
           similar user-specified control (which would live on the same screen as the controls
           for SBM-normalisation and SpotAvg vs LinReg) called 'Preferred index isotope', with 
           options 204Pb, 207Pb, 208Pb (strictly, the availability of 208Pb on this list should 
           hinge on whether the selected Task is Perm1-type or not; 
         */
        // we need SHRIMP: Sub WtdMeanAcalc 
        return concentrationExpressionsOrdered;
    }

    /**
     * This subroutine evaluates, for the Standard, the weighted mean (and
     * associated parameters) of each relevant set of common-Pb corrected
     * calibration constant values. This mean is the value to which all
     * spot-by-spot calibration constants determined for the unknowns will be
     * calibrated, and all spot-by-spot, 'directly-calculated daughter/parent
     * dates' (i.e. 206Pb/238U and/or 208Pb/232Th as appropriate) are calculated
     * from there, because SHRIMP (and secondary ion mass spectrometry in
     * general) is an indirect dating technique. Thus this is a critical step.
     */
    public static void wtdMeanAcalc() {

        // will do for both flavors of calibration contants, 8/32 and 6/38 but start with 208/232
        // these are found here: UncorrPb/Uconst and UncorrPb/Thconst
        /**
         * If (pbU = TRUE AND NumDauPar = 1) OR (pbTh = TRUE AND NumDauPar = 2)
         * --we are dealing specifically with a 206Pb/238U calib. constant:
         * Lambda = pscLm8 --i.e. 238U decay constant in units of "Ma^-1" Ele =
         * "U" Else --we are dealing specifically with a 208Pb/232Th calib.
         * constant: Lambda = pscLm2 --i.e. 232Th decay constant in units of
         * "Ma^-1" Ele = "Th" End If
         */
        
        // first get fdNmad ofr uncertainty column for the calibration constant
        
        
    }

    /**
     * TODO: These should probably be segregated to the end of the expression
     * list and not be sorted each time?
     *
     * Ludwig Q4 part 1
     *
     * @return
     */
    public static SortedSet<Expression> generateOverCountExpressions() {
        SortedSet<Expression> overCountExpressionsOrdered = new TreeSet<>();

        Expression expressionOverCount4_6_7 = buildExpression(
                "204/206 (fr. 207)",
                "([\"207/206\"] - Std_76 ) / ( sComm_74  - (Std_76 * sComm_64)) ", true, false);
        overCountExpressionsOrdered.add(expressionOverCount4_6_7);

        Expression expressionOverCount4_6_7U = buildExpression(
                "204/206 (fr. 207) %err",
                "ABS( [%\"207/206\"] * [\"207/206\"] / ([\"207/206\"] - Std_76) )", true, false);
        overCountExpressionsOrdered.add(expressionOverCount4_6_7U);

        Expression expressionOverCount4_6_8 = buildExpression(
                "204/206 (fr. 208)",
                "( [\"208/206\"] - StdRad86fact * [\"232Th/238U\"] ) / (sComm_84 - StdRad86fact * [\"232Th/238U\"] * sComm_64 )", true, false);
        overCountExpressionsOrdered.add(expressionOverCount4_6_8);

        Expression expressionOverCountPerSec4_7 = buildExpression(
                "204 overcts/sec (fr. 207)",
                "TotalCps([\"204\"]) - TotalCps([\"BKG\"]) - [\"204/206 (fr. 207)\"] * ( TotalCps([\"206\"]) - TotalCps([\"BKG\"]))", true, false);
        overCountExpressionsOrdered.add(expressionOverCountPerSec4_7);

        Expression expressionOverCountPerSec4_8 = buildExpression(
                "204 overcts/sec (fr. 208)",
                "TotalCps([\"204\"]) - TotalCps([\"BKG\"]) - [\"204/206 (fr. 208)\"] * ( TotalCps([\"206\"]) - TotalCps([\"BKG\"]))", true, false);
        overCountExpressionsOrdered.add(expressionOverCountPerSec4_8);

        Expression expressionOverCount7CorrCalib = buildExpression(
                "7-corr primary calib const. delta%",
                "100 * ( (1 - sComm_64 * [\"204/206\"]) / (1 - sComm_64 * [\"204/206 (fr. 207)\"]) - 1 )", true, false);
        overCountExpressionsOrdered.add(expressionOverCount7CorrCalib);

        Expression expressionOverCount8CorrCalib = buildExpression(
                "8-corr primary calib const. delta%",
                "100 * ( (1 - sComm_64 * [\"204/206\"]) / (1 - sComm_64 * [\"204/206 (fr. 208)\"]) - 1 ) ", true, false);
        overCountExpressionsOrdered.add(expressionOverCount8CorrCalib);

        return overCountExpressionsOrdered;
    }

    /**
     * TODO: These should probably be segregated to the end of the expression
     * list and not be sorted each time?
     *
     * @return
     */
    public static SortedSet<Expression> generateCorrectionsOfCalibrationConstants() {
        SortedSet<Expression> correctionsOfCalibrationConstants = new TreeSet<>();

        Expression expression4corr208Pb232Thcalibrconst = buildExpression(
                "4-corr208Pb/232Thcalibr.const",
                "(1 - [\"204/206\"] / [\"208/206\"] * sComm_84) * [\"UncorrPb/Thconst\"]", true, true);
        correctionsOfCalibrationConstants.add(expression4corr208Pb232Thcalibrconst);

        Expression expression4corr206Pb238Ucalibrconst = buildExpression(
                "4-corr206Pb/238Ucalibr.const",
                "(1 - [\"204/206\"] * sComm_64) * [\"UncorrPb/Uconst\"]", true, true);
        correctionsOfCalibrationConstants.add(expression4corr206Pb238Ucalibrconst);

        Expression expression7corr208Pb232Thcalibrconst = buildExpression(
                "7-corr208Pb/232Thcalibr.const",
                "(1 - [\"204/206 (fr. 207)\"] / [\"208/206\"] * sComm_84) * [\"UncorrPb/Thconst\"]", true, true);
        correctionsOfCalibrationConstants.add(expression7corr208Pb232Thcalibrconst);

        Expression expression7corr206Pb238Ucalibrconst = buildExpression(
                "7-corr206Pb/238Ucalibr.const",
                "(1 - [\"204/206 (fr. 207)\"] * sComm_64) * [\"UncorrPb/Uconst\"]", true, true);
        correctionsOfCalibrationConstants.add(expression7corr206Pb238Ucalibrconst);

        Expression expression8corr206Pb238Ucalibrconst = buildExpression(
                "8-corr206Pb/238Ucalibr.const",
                "(1 - [\"204/206 (fr. 208)\"] * sComm_84) * [\"UncorrPb/Uconst\"]", true, true);
        correctionsOfCalibrationConstants.add(expression8corr206Pb238Ucalibrconst);

        //--Now calculate common Pb-corrected calibration constant ERRORS: 
        Expression expression4corr208Pb232Thcalibrconsterr = buildExpression(
                "4-corr208Pb/232Thcalibr.const %err",
                "sqrt(( sComm_84 / ( [\"208/206\"] / [\"204/206\"] - sComm_84 ) )^2 * [%\"204/206\"]^2 )", true, true);
        correctionsOfCalibrationConstants.add(expression4corr208Pb232Thcalibrconsterr);

        Expression expression4corr206Pb238Ucalibrconsterr = buildExpression(
                "4-corr206Pb/238Ucalibr.const %err",
                "sqrt([%\"UncorrPb/Uconst\"]^2 + \n"
                + "( sComm_64 / ( 1 / [\"204/206\"] - sComm_64 ) )^2 * [%\"204/206\"]^2 )", true, true);
        correctionsOfCalibrationConstants.add(expression4corr206Pb238Ucalibrconsterr);

        Expression expression7corr208Pb232Thcalibrconsterr = buildExpression(
                "7-corr208Pb/232Thcalibr.const %err",
                "sqrt([%\"UncorrPb/Thconst\"]^2 +  \n"
                + "( sComm_84 / ( [\"208/206\"] / [\"204/206 (fr. 207)\"] - sComm_84 ) )^2 * \n"
                + "( [%\"208/206\"]^2 + [\"204/206 (fr. 207) %err\"]^2 ))", true, true);
        correctionsOfCalibrationConstants.add(expression7corr208Pb232Thcalibrconsterr);

        Expression expression7corr206Pb238Ucalibrconsterr = buildExpression(
                "7-corr206Pb/238Ucalibr.const %err",
                "sqrt([%\"UncorrPb/Uconst\"]^2 +\n"
                + "( sComm_64 / (1 / [\"204/206 (fr. 207)\"] - sComm_64 ) )^2 * \n"
                + "[\"204/206 (fr. 207) %err\"]^2)", true, true);
        correctionsOfCalibrationConstants.add(expression7corr206Pb238Ucalibrconsterr);

        // TODO: FIX term1 = Simon
        String term1 = "[%\"UncorrPb/Uconst\"]^2 +  \n"
                + "( sComm_64 * [\"UncorrPb/Uconst\"] * [\"204/206 (fr. 208)\"] / [\"8-corr206Pb/238Ucalibr.const\"] )^2 ";
        String term3 = "( [\"208/206\"] * [%\"208/206\"] / \n"
                + "( [\"208/206\"] - StdRad86fact * [\"232Th/238U\"] ) )^2 ";
        String term4 = "1 / ( [\"208/206\"] - StdRad86fact * [\"232Th/238U\"] ) +  \n"
                + "sComm_64 / ( sComm_84 - sComm_64 * StdRad86fact * [\"232Th/238U\"] )";
        String term6 = "((" + term4 + ")* StdRad86fact * [\"232Th/238U\"] * [%\"232Th/238U\"] )^2";

        Expression expression8corr206Pb238Ucalibrconsterr = buildExpression(
                "8-corr206Pb/238Ucalibr.const %err",
                "sqrt( (" + term1 + ") * ((" + term3 + ") + (" + term6 + ")) )", true, true);
        correctionsOfCalibrationConstants.add(expression8corr206Pb238Ucalibrconsterr);

        // TODO: Logic needed to decide which of these goes into "calib.const. %err"
        return correctionsOfCalibrationConstants;
    }

    /**
     * TODO: These should probably be segregated to the end of the expression
     * list and not be sorted each time?
     *
     * Squid2.5 Framework: Part 4
     *
     * @return
     */
    public static SortedSet<Expression> generatePerSpotProportionsOfCommonPb() {
        SortedSet<Expression> perSpotPbCorrectionsOrdered = new TreeSet<>();

        Expression expression7Corr46 = buildExpression(
                "7-corr204Pb/206Pb",
                "Pb46cor7( [\"207/206\"], sComm_64, sComm_74, [\"207corr206Pb/238UAge\"] )", false, true);
        perSpotPbCorrectionsOrdered.add(expression7Corr46);

        Expression expression8Corr46 = buildExpression(
                "8-corr204Pb/206Pb",
                "Pb46cor8( [\"208/206\"], [\"232Th/238U\"], sComm_64, sComm_84, [\"208corr206Pb/238UAge\"] )", false, true);
        perSpotPbCorrectionsOrdered.add(expression8Corr46);

        // for ref materials
        Expression expression4corCom206 = buildExpression(
                "4-corr%com206",
                "100 * sComm_64 * [\"204/206\"]", true, true);
        perSpotPbCorrectionsOrdered.add(expression4corCom206);

        Expression expression7corCom206 = buildExpression(
                "7-corr%com206",
                "100 * sComm_64 * [\"204/206 (fr. 207)\"]", true, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom206);

        Expression expression8corCom206 = buildExpression(
                "8-corr%com206",
                "100 * sComm_64 * [\"204/206 (fr. 208)\"]", true, false);
        perSpotPbCorrectionsOrdered.add(expression8corCom206);

        Expression expression4corCom208 = buildExpression(
                "4-corr%com208",
                "100 * sComm_84 / [\"208/206\"] * [\"204/206\"]", true, true);
        perSpotPbCorrectionsOrdered.add(expression4corCom208);

        Expression expression7corCom208 = buildExpression(
                "7-corr%com208",
                "100 * sComm_84 / [\"208/206\"] * [\"204/206 (fr. 207)\"]", true, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom208);

        // for samples
        Expression expression7corCom206S = buildExpression(
                "7-corr%com206S",
                "100 * sComm_64 * [\"7-corr204Pb/206Pb\"]", false, true);
        perSpotPbCorrectionsOrdered.add(expression7corCom206S);

        Expression expression8corCom206S = buildExpression(
                "8-corr%com206S",
                "100 * sComm_64 * [\"8-corr204Pb/206Pb\"]", false, true);
        perSpotPbCorrectionsOrdered.add(expression8corCom206S);

        // The next step is to calculate all the applicable radiogenic 208Pb/206Pb values. 
        Expression expression4corr208Pb206Pb = buildExpression(
                "4-corr208Pb*/206Pb*",
                "( [\"208/206\"] / [\"204/206\"] - sComm_84 ) / ( 1 / [\"204/206\"] - sComm_64)", true, true);
        perSpotPbCorrectionsOrdered.add(expression4corr208Pb206Pb);

        // ref material version
        Expression expression4corr208Pb206Pberr = buildExpression(
                "4-corr208Pb*/206Pb* %err",
                "100 * sqrt( ( ([%\"208/206\"] / 100 * [\"208/206\"])^2 +\n"
                + "(StdRad86fact * sComm_64 - sComm_84)^2 * ([%\"204/206\"] / 100 * [\"204/206\"])^2 )  \n"
                + " / (1 - sComm_64 * [\"204/206\"])   ) / abs( StdRad86fact ) ", true, false);
        perSpotPbCorrectionsOrdered.add(expression4corr208Pb206Pberr);

        // sample version
        Expression expression4corr208Pb206PbSerr = buildExpression(
                "4-corr208Pb*/206Pb*S %err",
                "100 * sqrt( ( ([%\"208/206\"] / 100 * [\"208/206\"])^2 +\n"
                + "([\"4-corr208Pb*/206Pb*\"] * sComm_64 - sComm_84)^2 * ([%\"204/206\"] / 100 * [\"204/206\"])^2 )  \n"
                + " / (1 - sComm_64 * [\"204/206\"])  ) / abs( [\"4-corr208Pb*/206Pb*\"] )  ", false, true);
        perSpotPbCorrectionsOrdered.add(expression4corr208Pb206PbSerr);

        // ref material version
        Expression expression7corr208Pb206Pb = buildExpression(
                "7-corr208Pb*/206Pb*",
                "( [\"208/206\"] / [\"204/206 (fr. 207)\"] - sComm_84 ) / \n"
                + "( 1 / [\"204/206 (fr. 207)\"] - sComm_64) ", true, false);
        perSpotPbCorrectionsOrdered.add(expression7corr208Pb206Pb);

        // sample version
        Expression expression7corr208Pb206PbS = buildExpression(
                "7-corr208Pb*/206Pb*S",
                "( [\"208/206\"] / [\"7-corr204Pb/206Pb\"] - sComm_84 ) / \n"
                + "( 1 / [\"7-corr204Pb/206Pb\"] - sComm_64)  ", false, true);
        perSpotPbCorrectionsOrdered.add(expression7corr208Pb206PbS);

        return perSpotPbCorrectionsOrdered;
    }

    private static Expression buildExpression(String name, String excelExpression, boolean isRefMatCalc, boolean isSampleCalc) {
        Expression expression = new Expression(name, excelExpression);
        expression.setSquidSwitchNU(false);

        ExpressionTreeInterface expressionTree = expression.getExpressionTree();
        expressionTree.setSquidSwitchSTReferenceMaterialCalculation(isRefMatCalc);
        expressionTree.setSquidSwitchSAUnknownCalculation(isSampleCalc);

        expressionTree.setSquidSwitchConcentrationReferenceMaterialCalculation(false);
        expressionTree.setSquidSwitchSCSummaryCalculation(false);
        expressionTree.setSquidSpecialUPbThExpression(true);
        expressionTree.setRootExpressionTree(true);
        System.out.println(">>>>>   " + expressionTree.getName());

        return expression;
    }

}
