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
import org.apache.commons.math3.distribution.TDistribution;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

import java.util.List;

import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class TInv extends Function {

    private static final long serialVersionUID = 1408341634276755722L;

    /**
     *
     */
    public TInv() {
        name = "TInv";
        argumentCount = 2;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"TInv"}};
        labelsForInputValues = new String[]{"probability 0<p<1", "degrees of freedom >= 1"};
        definition = "Returns the value x for which TDIST(x, df, 2) returns p.";
    }

    /**
     * @param childrenET      the value of childrenET
     * @param shrimpFractions the value of shrimpFraction
     * @param task
     * @return the double[][]
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {

        double retVal;
        try {
            double probability = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0])[0];
            double degreesOfFreedom = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0])[0];
            TDistribution td = new TDistribution(degreesOfFreedom);

            retVal = td.inverseCumulativeProbability(1.0 - (probability / 2.0));
        } catch (Exception e) {
            retVal = 0.0;
        }

        return new Object[][]{{retVal}};
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
