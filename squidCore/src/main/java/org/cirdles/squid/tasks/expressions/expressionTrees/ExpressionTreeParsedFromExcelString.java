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
package org.cirdles.squid.tasks.expressions.expressionTrees;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("ExpressionTree")
public class ExpressionTreeParsedFromExcelString extends ExpressionTree implements BuiltInExpressionInterface {

    private static final long serialVersionUID = -1526502328130247004L;

    /**
     *
     * @param operation
     */
    public ExpressionTreeParsedFromExcelString(OperationOrFunctionInterface operation) {
        super(operation);
    }

    @Override
    public void buildExpression() {
        this.ratiosOfInterest = getAllRatiosOfInterest();

        //other switches set outside of this operation
        setRootExpressionTree(true);
    }
}
