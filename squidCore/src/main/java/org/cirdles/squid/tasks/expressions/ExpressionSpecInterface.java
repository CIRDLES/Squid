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
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public interface ExpressionSpecInterface {

    /**
     * @return the excelExpressionString
     */
    String getExcelExpressionString();

    /**
     * @return the expressionName
     */
    String getExpressionName();

    /**
     * @return the squidSpecialUPbThExpression
     */
    boolean isSquidSpecialUPbThExpression();

    /**
     * @return the squidSwitchConcentrationReferenceMaterialCalculation
     */
    boolean isSquidSwitchConcentrationReferenceMaterialCalculation();

    /**
     * @return the squidSwitchNU
     */
    boolean isSquidSwitchNU();

    /**
     * @return the squidSwitchSAUnknownCalculation
     */
    boolean isSquidSwitchSAUnknownCalculation();

    /**
     * @return the squidSwitchSCSummaryCalculation
     */
    boolean isSquidSwitchSCSummaryCalculation();

    /**
     * @return the squidSwitchSTReferenceMaterialCalculation
     */
    boolean isSquidSwitchSTReferenceMaterialCalculation();
    
    /**
     * @return the notes
     */
    public String getNotes();
    
}
