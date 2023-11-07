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
package org.cirdles.squid.tasks.expressions.expressionTrees;

import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;

import java.util.List;

/**
 * @author James F. Bowring
 */
public interface ExpressionTreeBuilderInterface {

    /**
     * @return the operation
     */
    OperationOrFunctionInterface getOperation();

    /**
     * @param operation the operation to set
     */
    void setOperation(OperationOrFunctionInterface operation);

    /**
     * @return the leftET
     */
    ExpressionTreeInterface getLeftET();

    /**
     * @return the rightET
     */
    ExpressionTreeInterface getRightET();

    /**
     * @param childET
     */
    void addChild(ExpressionTreeInterface childET);

    /**
     * @param index
     * @param childET
     */
    void addChild(int index, ExpressionTreeInterface childET);

    /**
     * @return
     */
    int getCountOfChildren();

    /**
     * @return
     */
    int getOperationPrecedence();

    /**
     * @return the childrenET
     */
    List<ExpressionTreeInterface> getChildrenET();

    String auditOperationArgumentCount();

    String auditTargetCompatibility();

}