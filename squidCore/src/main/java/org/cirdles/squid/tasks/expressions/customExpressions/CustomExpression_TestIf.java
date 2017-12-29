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
package org.cirdles.squid.tasks.expressions.customExpressions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("ExpressionTree")
public class CustomExpression_TestIf {

    /**
     * Squid Excel format is if(and(Expoc9511>=1.5,Expoc9511<=2.5),Expoc9511,2)
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("TestIf");

    static {
        
        ExpressionTreeInterface LTExpression = new ExpressionTree("LT");
        ((ExpressionTreeBuilderInterface) LTExpression)
                .addChild(0, new VariableNodeForSummary(CustomExpression_Expo.EXPRESSION.getName()));
        ((ExpressionTreeBuilderInterface) LTExpression).addChild(new ConstantNode("2.5", 1.5));
        ((ExpressionTreeBuilderInterface) LTExpression).setOperation(Operation.lessThan());
        
        
        ExpressionTreeInterface AndExpression = new ExpressionTree("And");
        ((ExpressionTreeBuilderInterface) AndExpression).addChild(0, LTExpression);
        ((ExpressionTreeBuilderInterface) AndExpression).addChild(new ConstantNode("true", true));
        ((ExpressionTreeBuilderInterface) AndExpression).setOperation(Function.and());

        ((ExpressionTreeBuilderInterface) EXPRESSION)
                .addChild(0, AndExpression);
        ((ExpressionTreeBuilderInterface) EXPRESSION)
                .addChild(new VariableNodeForSummary(CustomExpression_Expo.EXPRESSION.getName()));
        ((ExpressionTreeBuilderInterface) EXPRESSION)
                .addChild(new ConstantNode("99", 99));

        ((ExpressionTreeBuilderInterface) EXPRESSION).setOperation(Function.sqIf());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSCSummaryCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSTReferenceMaterialCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSAUnknownCalculation(false);
        ((ExpressionTree) EXPRESSION).setSquidSwitchConcentrationReferenceMaterialCalculation(false);
    }
}
