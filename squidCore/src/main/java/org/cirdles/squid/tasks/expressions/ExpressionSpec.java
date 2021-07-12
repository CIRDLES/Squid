/*
 * Copyright 2020 James F. Bowring and CIRDLES.org.
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

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class ExpressionSpec implements ExpressionSpecInterface {

    private String expressionName;
    private String excelExpressionString;
    private boolean squidSwitchNU;
    private boolean squidSwitchSCSummaryCalculation;
    private boolean squidSwitchSTReferenceMaterialCalculation;
    private boolean squidSwitchSAUnknownCalculation;
    private boolean squidSpecialUPbThExpression;
    private boolean squidSwitchConcentrationReferenceMaterialCalculation;
    private String notes;

    /**
     * Used for XML unmarshalling
     */
    public ExpressionSpec() {
    }

    public static ExpressionSpecInterface specifyExpression(Expression expression) {
        ExpressionSpec expressionSpec = new ExpressionSpec();
        expressionSpec.expressionName = expression.getName();
        expressionSpec.excelExpressionString = expression.getExcelExpressionString();
        expressionSpec.squidSwitchNU = expression.isSquidSwitchNU();
        expressionSpec.squidSwitchSCSummaryCalculation
                = expression.getExpressionTree().isSquidSwitchSCSummaryCalculation();
        expressionSpec.squidSwitchSTReferenceMaterialCalculation
                = expression.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation();
        expressionSpec.squidSwitchSAUnknownCalculation
                = expression.getExpressionTree().isSquidSwitchSAUnknownCalculation();
        expressionSpec.squidSpecialUPbThExpression
                = expression.getExpressionTree().isSquidSpecialUPbThExpression();
        expressionSpec.squidSwitchConcentrationReferenceMaterialCalculation
                = expression.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation();
        expressionSpec.notes
                = expression.getNotes();

        return expressionSpec;
    }

    public static ExpressionSpecInterface specifyConstantExpression(
            String constantName, String constantValue, String notes) {
        ExpressionSpec expressionSpec = new ExpressionSpec();
        expressionSpec.expressionName = constantName;
        expressionSpec.excelExpressionString = constantValue;
        expressionSpec.squidSwitchNU = false;
        expressionSpec.squidSwitchSCSummaryCalculation = true;
        expressionSpec.squidSwitchSTReferenceMaterialCalculation = true;
        expressionSpec.squidSwitchSAUnknownCalculation = true;
        expressionSpec.squidSpecialUPbThExpression = false;
        expressionSpec.squidSwitchConcentrationReferenceMaterialCalculation = false;
        expressionSpec.notes = notes;

        return expressionSpec;
    }

    /**
     * @return the expressionName
     */
    public String getExpressionName() {
        return expressionName;
    }

    /**
     * @param expressionName the expressionName to set
     */
    public void setExpressionName(String expressionName) {
        this.expressionName = expressionName;
    }

    /**
     * @return the excelExpressionString
     */
    public String getExcelExpressionString() {
        return excelExpressionString;
    }

    /**
     * @param excelExpressionString the excelExpressionString to set
     */
    public void setExcelExpressionString(String excelExpressionString) {
        this.excelExpressionString = excelExpressionString;
    }

    /**
     * @return the squidSwitchNU
     */
    public boolean isSquidSwitchNU() {
        return squidSwitchNU;
    }

    /**
     * @param squidSwitchNU the squidSwitchNU to set
     */
    public void setSquidSwitchNU(boolean squidSwitchNU) {
        this.squidSwitchNU = squidSwitchNU;
    }

    /**
     * @return the squidSwitchSCSummaryCalculation
     */
    public boolean isSquidSwitchSCSummaryCalculation() {
        return squidSwitchSCSummaryCalculation;
    }

    /**
     * @param squidSwitchSCSummaryCalculation the squidSwitchSCSummaryCalculation to set
     */
    public void setSquidSwitchSCSummaryCalculation(boolean squidSwitchSCSummaryCalculation) {
        this.squidSwitchSCSummaryCalculation = squidSwitchSCSummaryCalculation;
    }

    /**
     * @return the squidSwitchSTReferenceMaterialCalculation
     */
    public boolean isSquidSwitchSTReferenceMaterialCalculation() {
        return squidSwitchSTReferenceMaterialCalculation;
    }

    /**
     * @param squidSwitchSTReferenceMaterialCalculation the squidSwitchSTReferenceMaterialCalculation to set
     */
    public void setSquidSwitchSTReferenceMaterialCalculation(boolean squidSwitchSTReferenceMaterialCalculation) {
        this.squidSwitchSTReferenceMaterialCalculation = squidSwitchSTReferenceMaterialCalculation;
    }

    /**
     * @return the squidSwitchSAUnknownCalculation
     */
    public boolean isSquidSwitchSAUnknownCalculation() {
        return squidSwitchSAUnknownCalculation;
    }

    /**
     * @param squidSwitchSAUnknownCalculation the squidSwitchSAUnknownCalculation to set
     */
    public void setSquidSwitchSAUnknownCalculation(boolean squidSwitchSAUnknownCalculation) {
        this.squidSwitchSAUnknownCalculation = squidSwitchSAUnknownCalculation;
    }

    /**
     * @return the squidSpecialUPbThExpression
     */
    public boolean isSquidSpecialUPbThExpression() {
        return squidSpecialUPbThExpression;
    }

    /**
     * @param squidSpecialUPbThExpression the squidSpecialUPbThExpression to set
     */
    public void setSquidSpecialUPbThExpression(boolean squidSpecialUPbThExpression) {
        this.squidSpecialUPbThExpression = squidSpecialUPbThExpression;
    }

    /**
     * @return the squidSwitchConcentrationReferenceMaterialCalculation
     */
    public boolean isSquidSwitchConcentrationReferenceMaterialCalculation() {
        return squidSwitchConcentrationReferenceMaterialCalculation;
    }

    /**
     * @param squidSwitchConcentrationReferenceMaterialCalculation the squidSwitchConcentrationReferenceMaterialCalculation to set
     */
    public void setSquidSwitchConcentrationReferenceMaterialCalculation(boolean squidSwitchConcentrationReferenceMaterialCalculation) {
        this.squidSwitchConcentrationReferenceMaterialCalculation = squidSwitchConcentrationReferenceMaterialCalculation;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }


}