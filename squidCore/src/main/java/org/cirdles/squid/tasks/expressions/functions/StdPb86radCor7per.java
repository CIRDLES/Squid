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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.List;
import static org.cirdles.squid.constants.Squid3Constants.sComm0_64;
import static org.cirdles.squid.constants.Squid3Constants.sComm0_74;
import static org.cirdles.squid.constants.Squid3Constants.sComm0_84;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class StdPb86radCor7per extends Function {

    //private static final long serialVersionUID = 6770836871819373387L;
    private void readObject(
            ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ObjectStreamClass myObject = ObjectStreamClass.lookup(Class.forName(StdPb86radCor7per.class.getCanonicalName()));
        long theSUID = myObject.getSerialVersionUID();
        System.out.println("Customized De-serialization of StdPb86radCor7per " + theSUID);
    }

    /**
     * Provides the functionality of Squid's agePb76 by calling pbPbAge and
     * returning "Age" and "AgeErr" and encoding the labels for each cell of the
     * values array produced by eval.
     *
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/Pub.bas
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/UPb.bas
     */
    public StdPb86radCor7per() {

        name = "stdPb86radCor7per";
        argumentCount = 3;
        precedence = 10;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"stdPb86radCor7per"}};
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one column representing the 208/206 IsotopicRatio and a row for each
     * member of shrimpFractions. Requires that child 1 is a VariableNode that
     * evaluates to a double array with one column representing 207/206 and a
     * row for each member of shrimpFractions. Likewise, child 2 is pb46cor7.
     *
     * @param childrenET list containing child 0 - 2
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][1] array of pb46cor8
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
//double pb86tot, double pb86totPer, double pb76tot,
//            double pb76totPer, double radPb86cor7, double pb46cor7, double stdRadPb76,
//            double alpha0, double beta0, double gamma0
        Object[][] retVal;
        try {
            double[] pb208_206RatioAndUnct = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] pb207_206RatioAndUnct = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);
            double[] pb46cor7 = convertObjectArrayToDoubles(childrenET.get(2).eval(shrimpFractions, task)[0]);
            
            // convert uncertainties to percents for function call
            double  pb208_206Unct = pb208_206RatioAndUnct[1] / pb208_206RatioAndUnct[0] * 100.0;
            double  pb207_206Unct = pb207_206RatioAndUnct[1] / pb207_206RatioAndUnct[0] * 100.0;
//todo promote some more constants
            double[] stdPb86radCor7per = org.cirdles.ludwig.squid25.PbUTh_2.stdPb86radCor7per(
                    pb208_206RatioAndUnct[0], pb208_206Unct, pb207_206RatioAndUnct[0], pb207_206Unct, 1, pb46cor7[0], 1, sComm0_64, sComm0_74, sComm0_84);
            
            retVal = new Object[][]{{stdPb86radCor7per[0]}};
        } catch (ArithmeticException | IndexOutOfBoundsException | NullPointerException e) {
            retVal = new Object[][]{{0.0}};
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
                + "<mi>StdPb86radCor7per</mi>"
                + "<mfenced>";

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}
