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

import java.io.Serializable;

/**
 *
 * @author James F. Bowring
 */
public class TaskSquid25Equation implements Serializable {

    private final String excelEquationString;
    private final String equationName;
    private final boolean eqnSwitchST;
    private final boolean eqnSwitchSA;
    private final boolean eqnSwitchConcST;
    private final boolean eqnSwitchSC;
    private final boolean eqnSwitchNU;
    private final boolean eqnSwitchSpecialBuiltin;

    public TaskSquid25Equation(
            String excelEquationString,
            String equationName,
            boolean eqnSwitchST,
            boolean eqnSwitchSA,
            boolean eqnSwitchSC,
            boolean eqnSwitchNU,
            boolean eqnSwitchSpecialBuiltin,
            boolean eqnSwitchConcST) {

        this.excelEquationString = excelEquationString;
        this.equationName = equationName;
        this.eqnSwitchST = eqnSwitchST;
        this.eqnSwitchSA = eqnSwitchSA;
        this.eqnSwitchSC = eqnSwitchSC;
        this.eqnSwitchNU = eqnSwitchNU;
        this.eqnSwitchSpecialBuiltin = eqnSwitchSpecialBuiltin;
        this.eqnSwitchConcST = eqnSwitchConcST;
    }

    /**
     * @return the excelEquationString
     */
    public String getExcelEquationString() {
        return excelEquationString;
    }

    /**
     * @return the equationName
     */
    public String getEquationName() {
        return equationName;
    }

    /**
     * @return the eqnSwitchST
     */
    public boolean isEqnSwitchST() {
        return eqnSwitchST;
    }

    /**
     * @return the eqnSwitchSA
     */
    public boolean isEqnSwitchSA() {
        return eqnSwitchSA;
    }

    /**
     * @return the eqnSwitchNU
     */
    public boolean isEqnSwitchNU() {
        return eqnSwitchNU;
    }

    /**
     * @return the eqnSwitchSC
     */
    public boolean isEqnSwitchSC() {
        return eqnSwitchSC;
    }

    /**
     *
     * @return
     */
    public boolean isEqnSwitchSpecialBuiltin() {
        return eqnSwitchSpecialBuiltin;
    }

    /**
     * 
     * @return 
     */
    public boolean isEqnSwitchConcST() {
        return eqnSwitchConcST;
    }

}
