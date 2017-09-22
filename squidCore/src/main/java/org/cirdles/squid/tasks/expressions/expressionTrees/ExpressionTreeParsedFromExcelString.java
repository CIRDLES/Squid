/*
 * Copyright 2017 CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.expressionTrees;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.squid.tasks.expressions.parsing.ExpressionParser;

/**
 *
 * @author James F. Bowring
 */
public class ExpressionTreeParsedFromExcelString extends ExpressionTree implements BuiltInExpressionInterface {

    private static final long serialVersionUID = -1526502328130247004L;

    private List<String> parsedRPNreversedExcelString;

    public ExpressionTreeParsedFromExcelString(String name) {
        super(name);
        parsedRPNreversedExcelString = new ArrayList<>();
    }

    /**
     *
     * @param operation
     */
    public ExpressionTreeParsedFromExcelString(OperationOrFunctionInterface operation) {
        super(operation);
        parsedRPNreversedExcelString = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.parsedRPNreversedExcelString);
        return hash;
    }

    
    @Override
    public void buildExpression(TaskInterface task) {

        ExpressionParser parser = new ExpressionParser(task.getNamedExpressionsMap());
        ExpressionTreeInterface parsedExp = parser.buildTree(parsedRPNreversedExcelString);

        // copy to this children, operation, and calculate ratiosOfInterest
        this.childrenET = ((ExpressionTree) parsedExp).getChildrenET();
        this.operation = ((ExpressionTree) parsedExp).getOperation();
              
        this.ratiosOfInterest = getAllRatiosOfInterest();
             

        //other switches set outside of this operation
        setRootExpressionTree(true);
    }

    /**
     * @param parsedRPNreversedExcelString the parsedRPNreversedExcelString to
     * set
     */
    public void setParsedRPNreversedExcelString(List<String> parsedRPNreversedExcelString) {
        this.parsedRPNreversedExcelString = parsedRPNreversedExcelString;
    }

}
