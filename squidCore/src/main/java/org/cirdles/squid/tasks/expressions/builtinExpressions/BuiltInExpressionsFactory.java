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
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class BuiltInExpressionsFactory {
    
    public static Map<String, ExpressionTreeInterface> generateParameterConstants(){
        Map<String, ExpressionTreeInterface> parameterConstants = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        
        ExpressionTreeInterface constantStd_76 = new ConstantNode("Std_76", 0.0587838486664528);
        parameterConstants.put(constantStd_76.getName(), constantStd_76);
        
        ExpressionTreeInterface constantsComm_74 = new ConstantNode("sComm_74", 15.5773361);
        parameterConstants.put(constantsComm_74.getName(), constantsComm_74);
        
        ExpressionTreeInterface constantsComm_64 = new ConstantNode("sComm_64", 17.821);
        parameterConstants.put(constantsComm_64.getName(), constantsComm_64);
        
        return parameterConstants;
    }

    public static SortedSet<Expression> generateOverCountExpressions() {
        SortedSet<Expression> overCountExpressionsOrdered = new TreeSet<>();

        Expression expressionStd_76 = buildExpression(
                "204/206 (fr. 207)", 
                "([\"207/206\"] - Std_76 ) / ( sComm_74  - (Std_76 * sComm_64)) ");
        overCountExpressionsOrdered.add(expressionStd_76);
        
        Expression expressionStd_76U = buildExpression(
                "204/206 (fr. 207) %err", 
                "ABS( [%\"207/206\"] * [\"207/206\"] / ([\"207/206\"] - Std_76) )");
        overCountExpressionsOrdered.add(expressionStd_76U);
        

        return overCountExpressionsOrdered;
    }

    private static Expression buildExpression(String name, String excelExpression) {
        Expression expression = new Expression(name, excelExpression);
        expression.setSquidSwitchNU(false);

        ExpressionTreeInterface expressionTree = expression.getExpressionTree();
        expressionTree.setSquidSwitchSTReferenceMaterialCalculation(true);
        expressionTree.setSquidSwitchSAUnknownCalculation(false);
        expressionTree.setSquidSwitchSCSummaryCalculation(false);
        expressionTree.setSquidSpecialUPbThExpression(true);
        expressionTree.setRootExpressionTree(true);
        System.out.println(">>>>>   " + expressionTree.getName());

        return expression;
    }

}
