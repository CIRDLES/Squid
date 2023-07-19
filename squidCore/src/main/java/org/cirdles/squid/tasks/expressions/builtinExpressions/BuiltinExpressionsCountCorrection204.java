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
package org.cirdles.squid.tasks.expressions.builtinExpressions;

import org.cirdles.squid.tasks.expressions.Expression;

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_204_OVR_CTS_FROM_207;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_204_OVR_CTS_FROM_208;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.buildExpression;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class BuiltinExpressionsCountCorrection204 {

    /**
     * @return
     * @see https://github.com/CIRDLES/ET_Redux/wiki/SQ2.50-Procedural-Framework:-Part-7a-(204-overcounts)
     */
    public static Expression buildCountCorrectionExpressionFrom207() {

        String term1
                = "ABS(TotalCps([\"204\"])-TotalCps([\"BKG\"]))/"
                + "(TotalCpsTime([\"204\"]))";

        String term2
                = "[\"" + BIWT_204_OVR_CTS_FROM_207 + "\"][2]^2";

        String term3
                = "(TotalCps([\"204\"])-TotalCps([\"BKG\"])-"
                + BIWT_204_OVR_CTS_FROM_207 + ")^2/"
                + "(TotalCpsTime([\"206\"])*"
                + "ABS(TotalCps([\"206\"])-TotalCps([\"BKG\"])))";

        String term4
                = "ABS(TotalCps([\"204\"])-TotalCps([\"BKG\"])-"
                + BIWT_204_OVR_CTS_FROM_207 + ")";

        String term5
                = "100*SQRT(" + term1 + "+" + term2 + "+" + term3 + ")/" + term4;

        Expression countCorrectionExpression204From207 = buildExpression(
                "SWAPCountCorrectionExpression204From207",
                "ValueModel("
                        + "(TotalCps([\"204\"])-TotalCps([\"BKG\"])-" + BIWT_204_OVR_CTS_FROM_207 + ")/"
                        + "(TotalCps([\"206\"])-TotalCps([\"BKG\"]))" + ","
                        + term5 + ","
                        + "false)", false, true, false);

        countCorrectionExpression204From207.getExpressionTree().setSquidSpecialUPbThExpression(true);

        return countCorrectionExpression204From207;
    }

    /**
     * @return
     * @see https://github.com/CIRDLES/ET_Redux/wiki/SQ2.50-Procedural-Framework:-Part-7a-(204-overcounts)
     */
    public static Expression buildCountCorrectionExpressionFrom208() {

        String term1
                = "ABS(TotalCps([\"204\"])-TotalCps([\"BKG\"]))/"
                + "(TotalCpsTime([\"204\"]))";

        String term2
                = "[\"" + BIWT_204_OVR_CTS_FROM_208 + "\"][2]^2";

        String term3
                = "(TotalCps([\"204\"])-TotalCps([\"BKG\"])-"
                + BIWT_204_OVR_CTS_FROM_208 + ")^2/"
                + "(TotalCpsTime([\"206\"])*"
                + "ABS(TotalCps([\"206\"])-TotalCps([\"BKG\"])))";

        String term4
                = "ABS(TotalCps([\"204\"])-TotalCps([\"BKG\"])-"
                + BIWT_204_OVR_CTS_FROM_208 + ")";

        String term5
                = "100*SQRT(" + term1 + "+" + term2 + "+" + term3 + ")/" + term4;

        Expression countCorrectionExpression204From208 = buildExpression(
                "SWAPCountCorrectionExpression204From208",
                "ValueModel("
                        + "(TotalCps([\"204\"])-TotalCps([\"BKG\"])-" + BIWT_204_OVR_CTS_FROM_208 + ")/"
                        + "(TotalCps([\"206\"])-TotalCps([\"BKG\"]))" + ","
                        + term5 + ","
                        + "false)", false, true, false);

        countCorrectionExpression204From208.getExpressionTree().setSquidSpecialUPbThExpression(true);

        return countCorrectionExpression204From208;
    }

    public static Expression buildCountCorrectionCustomExpression() {

        Expression countCorrectionCustom = buildExpression(
                "SWAPCustomCorrection204",
                "ValueModel(Orig([\"204/206\"]),Orig([±\"204/206\"]),true)", false, true, false);

        countCorrectionCustom.getExpressionTree().setSquidSpecialUPbThExpression(false);
        countCorrectionCustom.setNotes(
                "Edit this expression to calculate a custom corrected value "
                        + "for 204/206 as per Squid2.5 'column swapping' and invoke it from the Common Pb menu "
                        + "window for overcounts of 204.  \n\n"
                        + "REQUIRED: Use the expression name 'SWAPCustomCorrection204' and define a ValueModel.  \n"
                        + "The default definition is: 'ValueModel(Orig([\"204/206\"]),Orig([±\"204/206\"]),true)'. \n\n"
                        + " NOTE:  Use the 'Orig' function to retrieve the unmutable value of a ratio.  Otherwise, "
                        + "a circular logic obtains.");

        return countCorrectionCustom;
    }
}