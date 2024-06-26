/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.builtinExpressions;

import java.io.Serializable;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public enum ReferenceMaterialAgeTypesEnum implements Serializable {

    PB4COR206_238AGE_RM("4cor_206Pb238U_Age_RM"),
    PB4COR208_232AGE_RM("4cor_208Pb232Th_Age_RM"),
    PB4COR207_206AGE_RM("4cor_207Pb206Pb_Age_RM"),
    PB7COR206_238AGE_RM("7cor_206Pb238U_Age_RM"),
    PB7COR208_232AGE_RM("7cor_208Pb232Th_Age_RM"),
    PB8COR206_238AGE_RM("8cor_206Pb238U_Age_RM"),
    PB8COR207_206AGE_RM("8cor_207Pb206Pb_Age_RM");

    private final String expressionName;

    ReferenceMaterialAgeTypesEnum(String expressionName) {
        this.expressionName = expressionName;
    }

    public static boolean isReservedName(String nameString) {
        boolean retVal = false;

        for (String name : getNames()) {
            if (nameString.startsWith(name)) {
                retVal = true;
                break;
            }
        }
        return retVal;
    }

    public static String[] getNames() {
        String[] retVal = new String[7];
        int index = 0;
        for (ReferenceMaterialAgeTypesEnum sat : ReferenceMaterialAgeTypesEnum.values()) {
            retVal[index++] = sat.getExpressionName();
        }

        return retVal;
    }

    /**
     * @return the expressionName
     */
    public String getExpressionName() {
        return expressionName;
    }

    @Override
    public String toString() {
        return expressionName;
    }


}