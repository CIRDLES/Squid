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
package org.cirdles.squid.tasks.evaluationEngines;

import java.io.Serializable;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.ludwig.squid25.Utilities;

/**
 *
 * @author James F. Bowring
 */
public class TaskExpressionEvaluatedPerSpotPerScanModel implements TaskExpressionEvaluatedPerSpotPerScanModelInterface, Serializable {

    private static final long serialVersionUID = 2530366687586794333L;

    private ExpressionTreeInterface expression;
    private double[] ratEqVal;
    private double[] ratEqTime;
    private double[] ratEqErr;
    private double ratioVal;
    private double ratioFractErr;

    private TaskExpressionEvaluatedPerSpotPerScanModel() {
    }

    /**
     * Structure to store results of Squid Switch NU expression evaluation using
     * ratios of interest and per SImon Bodorkos, rounded to 12 sig digits to
     * comply with VBA comparisons.
     *
     * @param expression
     * @param ratEqVal
     * @param ratEqTime
     * @param ratEqErr
     * @param ratioVal
     * @param ratioFractErr
     */
    public TaskExpressionEvaluatedPerSpotPerScanModel(
            ExpressionTreeInterface expression, double[] ratEqVal, double[] ratEqTime, double[] ratEqErr, double ratioVal, double ratioFractErr) {

        // April 2017 Rounding per Bodorkos
        int sigDigs = 12;

        this.expression = expression;
//        this.ratEqVal = Utilities.roundedToSize(ratEqVal.clone(), sigDigs);
        this.ratEqVal = ratEqVal.clone();
        this.ratEqTime = ratEqTime.clone();
        this.ratEqErr = ratEqErr.clone();

        this.ratioVal = Utilities.roundedToSize(ratioVal, sigDigs);
        this.ratioFractErr = Utilities.roundedToSize(ratioFractErr, sigDigs);
    }

    /**
     * @return the expression
     */
    @Override
    public ExpressionTreeInterface getExpression() {
        return expression;
    }

    /**
     * @return the ratEqVal
     */
    @Override
    public double[] getRatEqVal() {
        return ratEqVal.clone();
    }

    /**
     * @return the ratEqTime
     */
    @Override
    public double[] getRatEqTime() {
        return ratEqTime.clone();
    }

    /**
     * @return the ratEqErr
     */
    @Override
    public double[] getRatEqErr() {
        return ratEqErr.clone();
    }

    /**
     * @return the ratioVal
     */
    @Override
    public double getRatioVal() {
        return ratioVal;
    }

    /**
     * @return the ratioFractErr
     */
    @Override
    public double getRatioFractErr() {
        return ratioFractErr;
    }

}
