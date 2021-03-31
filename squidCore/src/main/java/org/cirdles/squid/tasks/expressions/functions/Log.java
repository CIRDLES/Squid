/*
 * Copyright 2020 James F. Bowring and CIRDLES.org.
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

import java.util.List;

import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Log extends Function {

    private static final long serialVersionUID = 4646312522597920358L;

//    private void readObject(ObjectInputStream stream) throws IOException,
//            ClassNotFoundException {
//        stream.defaultReadObject();
//
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(
//                Class.forName(Log.class.getCanonicalName()));
//        long theSUID = myObject.getSerialVersionUID();
//
//        System.out.println("Customized De-serialization of Log "
//                + theSUID);
//    }

    /**
     *
     */
    public Log() {
        name = "log";
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"LogBase10"}};
        labelsForInputValues = new String[]{"number"};
        definition = "Returns the base 10 logarithm of a number.  To calculate the log of n with base b, use log(n)/log(b) or ln(n)/ln(b).";
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
            retVal = StrictMath.log10(convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0])[0]);
        } catch (SquidException se) {
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
                + "<mi>log</mi>"
                + "<mfenced>";

        retVal += buildChildrenToMathML(childrenET);

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}
