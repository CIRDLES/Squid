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
package org.cirdles.squid.shrimp;

import java.io.Serializable;

/**
 *
 * @author James F. Bowring
 */
public class SquidRatiosSpecs implements Serializable {

    private static final long serialVersionUID = -2944080263487487243L;

    private String ratioName;
    private SquidSpeciesSpecs numerator;
    private SquidSpeciesSpecs denominator;

    public SquidRatiosSpecs(String ratioName, SquidSpeciesSpecs numerator, SquidSpeciesSpecs denominator) {
        this.ratioName = ratioName;
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * @return the ratioName
     */
    public String getRatioName() {
        return ratioName;
    }

    /**
     * @param ratioName the ratioName to set
     */
    public void setRatioName(String ratioName) {
        this.ratioName = ratioName;
    }

    /**
     * @return the numerator
     */
    public SquidSpeciesSpecs getNumerator() {
        return numerator;
    }

    /**
     * @param numerator the numerator to set
     */
    public void setNumerator(SquidSpeciesSpecs numerator) {
        this.numerator = numerator;
    }

    /**
     * @return the denominator
     */
    public SquidSpeciesSpecs getDenominator() {
        return denominator;
    }

    /**
     * @param denominator the denominator to set
     */
    public void setDenominator(SquidSpeciesSpecs denominator) {
        this.denominator = denominator;
    }

}
