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
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public enum SampleAgeTypesEnum implements Serializable {

    PB4COR206_238AGE("4cor_206Pb238U_Age"),
    PB4COR208_232AGE("4cor_208Pb232Th_Age"),
    PB4COR207_206AGE("4cor_207Pb206Pb_Age"),
    PB7COR206_238AGE("7cor_206Pb238U_Age"),
    PB7COR208_232AGE("7cor_208Pb232Th_Age"),
    PB8COR206_238AGE("8cor_206Pb238U_Age"),
    PB8COR207_206AGE("8cor_207Pb206Pb_Age");

    private final String expressionName;

    private SampleAgeTypesEnum(String expressionName) {
        this.expressionName = expressionName;
    }

    /**
     * @return the expressionName
     */
    public String getExpressionName() {
        return expressionName;
    }

    public static String[] getNames() {
        String[] retVal = new String[7];
        int index = 0;
        for (SampleAgeTypesEnum sat : SampleAgeTypesEnum.values()) {
            retVal[index++] = sat.getExpressionName();
        }

        return retVal;
    }

    @Override
    public String toString() {
        return expressionName;
    }
    
    
}
