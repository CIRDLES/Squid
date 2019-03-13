package org.cirdles.squid.parameters;

import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertTrue;

public class UUParametersManagerTest {

    @Test
    public void testUUValueEntry() {
        BigDecimal num = new BigDecimal(Double.MAX_VALUE);

        ReferenceMaterialModel mod = new ReferenceMaterialModel();
        Task task = new Task();
        task.setReferenceMaterialModel(mod);
        mod.getValues()[4].setValue(num);
        task.updateParametersFromModels();
        Expression exp = task.getExpressionByName("Ref_238U235U");

        assertTrue(exp != null && ((double) ((ConstantNode) ((ExpressionTreeBuilderInterface) exp.getExpressionTree()).getChildrenET().get(0)).getValue()) == num.doubleValue());
    }
}
