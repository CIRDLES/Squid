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
package org.cirdles.squid.tasks.evaluationEngines;

import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 * @author James F. Bowring
 */
public interface TaskExpressionEvaluatedPerSpotPerScanModelInterface {

    /**
     * @return the expression
     */
    public ExpressionTreeInterface getExpression();

    /**
     * @return the ratEqVal
     */
    public double[] getRatEqVal();

    /**
     * @return the ratEqTime
     */
    public double[] getRatEqTime();

    /**
     * @return the ratEqErr
     */
    public double[] getRatEqErr();

    /**
     * @return the ratioVal
     */
    public double getRatioVal();

    /**
     * @return the ratioFractErr
     */
    public double getRatioFractErr();

}