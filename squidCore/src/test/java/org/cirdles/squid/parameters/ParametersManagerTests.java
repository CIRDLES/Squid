package org.cirdles.squid.parameters;

import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertTrue;

public class ParametersManagerTests {

    @Test
    public void testReferenceMaterialValueEntry() {
        BigDecimal num = new BigDecimal(Double.MAX_VALUE);

        ParametersModel mod = new ReferenceMaterialModel();
        Task task = new Task();
        task.setReferenceMaterialModel(mod);
        mod.getValues()[4].setValue(num);
        task.updateParametersFromModels();
        Expression exp = task.getExpressionByName(BuiltInExpressionsDataDictionary.REF_238U235U);

        assertTrue(exp != null && getDoubleValueOfExpressionWithOneConstantNodeChild(exp) == num.doubleValue());
    }

    @Test
    public void testCommonPbValueEntry() {
        BigDecimal num = new BigDecimal(Double.MAX_VALUE);

        ParametersModel mod = new CommonPbModel();
        Task task = new Task();
        task.setCommonPbModel(mod);
        mod.getValues()[4].setValue(num);
        task.updateParametersFromModels();
        Expression exp = task.getExpressionByName(BuiltInExpressionsDataDictionary.DEFCOM_86);

        assertTrue(exp != null && getDoubleValueOfExpressionWithOneConstantNodeChild(exp) == num.doubleValue());
    }

    @Test
    public void testPhysicalConstantsModelValueEntry() {
        BigDecimal num = new BigDecimal(Double.MAX_VALUE);

        ParametersModel mod = new PhysicalConstantsModel();
        Task task = new Task();
        task.setPhysicalConstantsModel(mod);
        mod.getValues()[4].setValue(num);
        task.updateParametersFromModels();
        Expression exp = task.getExpressionByName(BuiltInExpressionsDataDictionary.LAMBDA234);

        assertTrue(exp != null && getDoubleValueOfExpressionWithOneConstantNodeChild(exp) == num.doubleValue());
    }

    //consider moving this method elsewhere such as to Expression.class
    public static double getDoubleValueOfExpressionWithOneConstantNodeChild(Expression exp) {
        ExpressionTreeBuilderInterface builderInterface = (ExpressionTreeBuilderInterface) exp.getExpressionTree();
        ConstantNode node = (ConstantNode) builderInterface.getChildrenET().get(0);
        return (double) node.getValue();
    }
}
