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
package org.cirdles.squid.tasks.squidTask25;

import org.cirdles.squid.tasks.expressions.ExpressionSpecInterface;

import java.io.Serializable;

/**
 * @author James F. Bowring
 */
public class TaskSquid25Equation implements ExpressionSpecInterface, Serializable {

    private final String excelExpressionString;
    private final String expressionName;
    private final boolean squidSwitchSTReferenceMaterialCalculation;
    private final boolean squidSwitchSAUnknownCalculation;
    private final boolean squidSwitchConcentrationReferenceMaterialCalculation;
    private final boolean squidSwitchSCSummaryCalculation;
    private final boolean squidSwitchNU;
    private final boolean squidSpecialUPbThExpression;
    private final String notes;

    public TaskSquid25Equation(
            String excelEquationString,
            String equationName,
            boolean eqnSwitchST,
            boolean eqnSwitchSA,
            boolean eqnSwitchSC,
            boolean eqnSwitchNU,
            String notes) {

        this.excelExpressionString = excelEquationString;
        this.expressionName = equationName;
        this.squidSwitchSTReferenceMaterialCalculation = eqnSwitchST;
        this.squidSwitchSAUnknownCalculation = eqnSwitchSA;
        this.squidSwitchSCSummaryCalculation = eqnSwitchSC;
        this.squidSwitchNU = eqnSwitchNU;
        this.squidSpecialUPbThExpression = false;
        this.squidSwitchConcentrationReferenceMaterialCalculation = false;
        this.notes = notes;
    }

    /**
     * @return the excelExpressionString
     */
    public String getExcelExpressionString() {
        return excelExpressionString;
    }

    /**
     * @return the expressionName
     */
    public String getExpressionName() {
        return expressionName;
    }

    /**
     * @return the squidSwitchSTReferenceMaterialCalculation
     */
    public boolean isSquidSwitchSTReferenceMaterialCalculation() {
        return squidSwitchSTReferenceMaterialCalculation;
    }

    /**
     * @return the squidSwitchSAUnknownCalculation
     */
    public boolean isSquidSwitchSAUnknownCalculation() {
        return squidSwitchSAUnknownCalculation;
    }

    /**
     * @return the squidSwitchNU
     */
    public boolean isSquidSwitchNU() {
        return squidSwitchNU;
    }

    /**
     * @return the squidSwitchSCSummaryCalculation
     */
    public boolean isSquidSwitchSCSummaryCalculation() {
        return squidSwitchSCSummaryCalculation;
    }

    /**
     * @return
     */
    public boolean isSquidSpecialUPbThExpression() {
        return squidSpecialUPbThExpression;
    }

    /**
     * @return
     */
    public boolean isSquidSwitchConcentrationReferenceMaterialCalculation() {
        return squidSwitchConcentrationReferenceMaterialCalculation;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

}