/*
 * Age208_232r.java
 *
 * Created on Dec 12, 2008
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
package org.cirdles.squid.parameters.valueModels;

import org.cirdles.squid.parameters.util.RadDates;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentMap;

/**
 * @author James F. Bowring
 */
public class Age208_232r extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 7155492664021403471L;
    private final static String NAME = RadDates.age208_232r.getName();
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private transient ValueModel r208_232r;
    private transient ValueModel lambda232;

    /**
     * Creates a new instance of Age208_232r
     */
    public Age208_232r() {
        super(NAME, UNCT_TYPE);
    }

    /**
     * @param inputValueModels
     * @param parDerivTerms
     */
    public void calculateValue(
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms) {

//        setValue( BigDecimal.ZERO );
//        setValueTree( ExpTreeII.ZERO );

        // April 2012 from Noah
        // Age208_232r = 1/lambda232 * log( r208_232r + 1 )
        r208_232r = inputValueModels[0];
        lambda232 = inputValueModels[1];


        try {
            setValue(//
                    new BigDecimal(//
                            //
                            //
                            //
                            (1.0 / lambda232.getValue().doubleValue())//
                                    * StrictMath.log1p(r208_232r.getValue().doubleValue()), new MathContext(15, RoundingMode.HALF_UP)));
        } catch (Exception e) {
            setValue(BigDecimal.ZERO);
        }

//        try {
//            setValueTree(//
//                    ExpTreeII.ONE.//
//                    divide( lambda232.getValueTree() ).//
//                    multiply( r208_232r.getValueTree().add( ExpTreeII.ONE ).log() ) );
//        } catch (Exception e) {
//            setValue( BigDecimal.ZERO );
//        }


        if (parDerivTerms != null) {
            // oct 2014 to handle common lead
            String partialDerivativeNameAge__Ratio = "dA" + name.substring(1) + "__dR" + r208_232r.getName().substring(1);//dAge208_232r__dR208_232r            
            try {
                BigDecimal dAge208_232r__dR208_232r = BigDecimal.ONE.//
                        divide(lambda232.getValue().//
                        multiply(BigDecimal.ONE.//
                        add(r208_232r.getValue())), new MathContext(15, RoundingMode.HALF_UP));
                parDerivTerms.put(partialDerivativeNameAge__Ratio, dAge208_232r__dR208_232r);
            } catch (Exception e) {
                parDerivTerms.put(partialDerivativeNameAge__Ratio, BigDecimal.ZERO);
            }

            // added march 2013 for LAICPMS
            // oct 2014 to handle common lead
            String partialDerivativeNameAge__Lambda = "dA" + name.substring(1) + "__dLambda232";//dAge208_232r__dLambda232
            try {
                BigDecimal dAge208_232r__dLambda232 = BigDecimal.valueOf(StrictMath.log1p(r208_232r.getValue().doubleValue())).//
                        divide(lambda232.getValue().pow(2), new MathContext(15, RoundingMode.HALF_UP)).negate();
                parDerivTerms.put(partialDerivativeNameAge__Lambda, dAge208_232r__dLambda232);
            } catch (Exception e) {
                parDerivTerms.put(partialDerivativeNameAge__Lambda, BigDecimal.ZERO);
            }

        }
    }
}