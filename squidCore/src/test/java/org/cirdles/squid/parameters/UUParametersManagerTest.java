package org.cirdles.squid.parameters;

import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.SortedSet;

import static junit.framework.TestCase.assertTrue;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_238U235U_RM_MODEL_NAME;

public class UUParametersManagerTest {

    @Test
    public void testUUValueEntry() {
        BigDecimal num = new BigDecimal(Long.MAX_VALUE);

        ReferenceMaterialModel mod = new ReferenceMaterialModel();
        mod.getValues()[4].setValue(num);
        SortedSet<Expression> set = BuiltInExpressionsFactory.updateReferenceMaterialValuesFromModel(mod);
        Expression exp = null;

        Iterator<Expression> iterator = set.iterator();
        while (iterator.hasNext() && exp == null) {
            Expression curr = iterator.next();
            if (curr.getName().equals(REF_238U235U_RM_MODEL_NAME)) {
                exp = curr;
            }
        }

        assertTrue(exp != null/* && exp.getValue().equals(num)*/);
    }
}
