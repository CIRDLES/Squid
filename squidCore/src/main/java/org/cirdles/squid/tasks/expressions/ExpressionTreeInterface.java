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
package org.cirdles.squid.tasks.expressions;

import java.util.List;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;

/**
 *
 * @author James F. Bowring
 */
public interface ExpressionTreeInterface {

    /**
     *
     * @param shrimpFractions the value of shrimpFraction
     * @param task
     * @return the double[][]
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException;

    /**
     *
     * @return
     */
    public String getName();

    /**
     * @return the parentET
     */
    public ExpressionTreeInterface getParentET();

    /**
     * @param parentET the parentET to set
     */
    public void setParentET(ExpressionTreeInterface parentET);

    /**
     * @return the rootExpressionTree
     */
    public boolean isRootExpressionTree();

    /**
     *
     * @return
     */
    public String toStringMathML();

    /**
     *
     * @return
     */
    public boolean isTypeFunction();

    /**
     *
     * @return
     */
    public boolean isTypeFunctionOrOperation();

    /**
     *
     * @return
     */
    public int argumentCount();

    /**
     *
     * @param objects
     * @return
     */
    public static double[] convertObjectArrayToDoubles(Object[] objects) throws SquidException {
        if (objects == null) {
            throw new SquidException("Failed to retrieve data.");
        }
        double[] retVal = new double[objects.length];
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof Integer) {
                retVal[i] = (double) ((Integer) objects[i]);
            } else {
                retVal[i] = (double) objects[i];
            }
        }
        return retVal;
    }

    /**
     *
     * @param objects
     * @return
     */
    public static double[][] convertObjectArrayToDoubles(Object[][] objects) throws SquidException {
        if (objects == null) {
            throw new SquidException("Failed to retrieve data.");
        }
        double[][] retVal = new double[objects.length][];
        for (int i = 0; i < objects.length; i++) {
            retVal[i] = convertObjectArrayToDoubles(objects[i]);
        }
        return retVal;
    }

    /**
     *
     * @param types
     * @return
     */
    public static Object[] convertArrayToObjects(double[] types) throws SquidException {
        if (types == null) {
            throw new SquidException("Failed to retrieve data.");
        }
        Object[] retVal = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            retVal[i] = (Object) types[i];
        }

        return retVal;
    }

    /**
     *
     * @param types
     * @return
     */
    public static Object[][] convertArrayToObjects(double[][] types) throws SquidException {
        if (types == null) {
            throw new SquidException("Failed to retrieve data.");
        }
        Object[][] retVal = new Object[types.length][];
        for (int i = 0; i < types.length; i++) {
            retVal[i] = convertArrayToObjects(types[i]);
        }
        return retVal;
    }

    /**
     *
     * @param objects
     * @return
     */
    public static boolean[] convertObjectArrayToBooleans(Object[] objects) throws SquidException {
        if (objects == null) {
            throw new SquidException("Failed to retrieve data.");
        }
        boolean[] retVal = new boolean[objects.length];
        for (int i = 0; i < objects.length; i++) {
            retVal[i] = (boolean) objects[i];
        }
        return retVal;
    }

}
