/*
 * Lambdas.java
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.parameters.util;

/**
 * Decay Constants Names
 *
 * @author James F. Bowring
 */
public enum Lambdas {
    LAMBDA_226("lambda226", "lambda 226"),
    LAMBDA_230("lambda230", "lambda 230"),
    /**
     *
     */
    LAMBDA_231("lambda231", "lambda 231"),
    /**
     *
     */
    LAMBDA_232("lambda232", "lambda 232"),
    /**
     *
     */
    LAMBDA_234("lambda234", "lambda 234"),
    /**
     *
     */
    LAMBDA_235("lambda235", "lambda 235"),
    /**
     *
     */
    LAMBDA_238("lambda238", "lambda 238");
    private String name;
    private String displayName;

    private Lambdas(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public static String[] getNames() {
        String[] retVal = new String[Lambdas.values().length];
        for (int i = 0; i < Lambdas.values().length; i++) {
            retVal[i] = Lambdas.values()[i].getName();
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     *
     * @param checkString
     * @return
     */
    public static boolean contains(String checkString) {
        boolean retVal = true;
        try {
            Lambdas.valueOf(checkString);
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
