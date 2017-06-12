/*
 * Copyright 2006-2016 CIRDLES.org.
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
package org.cirdles.squid.algorithms;


        import java.math.BigDecimal;
        import java.math.MathContext;
        import java.math.RoundingMode;

/**
 * This is an implementation of operations not included in BigDecimal. Currently
 * only square root has been needed, so only it has been made.
 *
 * Last edited 1/31/2017 Griffin Hiers
 *
 * @author James F. Bowring
 */
public class BigDecimalCustomAlgorithms {
    /**
     * Returns the square root of a BigDecimal object. Uses the Babylonian method. Uses a precision of 34 digits (the
     * same as MathContext.DECIMAL128). Iterates until further iterations of the algorithm don't update the estimate any
     * further, i.e. iterates until the best guess for the given precision is achieved.
     *
     * @param S The number of which the square root is returned
     * @return The square root of S
     * @throws NumberFormatException if S is negative
     */
    public static BigDecimal bigDecimalSqrtBabylonian(BigDecimal S) throws NumberFormatException {
        return bigDecimalSqrtBabylonian(S, 34);
    }

    /**
     * Returns the square root of a BigDecimal object. Uses the Babylonian method. Uses a precision given by a parameter
     * Iterates until further iterations of the algorithm don't update the estimate any further, i.e. iterates until the
     * best guess for the given precision is achieved.
     *
     * @param S The number of which the square root is returned
     * @param precision The number of digits the return value will be. Calculating less digits is faster.
     * @return The square root of S
     * @throws NumberFormatException if S is negative
     */
    public static BigDecimal bigDecimalSqrtBabylonian(BigDecimal S, int precision) throws NumberFormatException {

        //MathContext with the specified precision
        MathContext precisionMC = new MathContext(precision, RoundingMode.HALF_EVEN);

        //obtain an initial guess for the root
        BigDecimal guess = obtainInitialGuess(S);

        //check to see if the initial guess is already exact
        if (S.compareTo(guess.pow(2)) != 0) {

            //iterate until the amount between guesses is zero, meaning more precision would be needed to store the next estimation
            BigDecimal theError = BigDecimal.ONE;
            while (theError.compareTo(new BigDecimal(0)) != 0) {
                BigDecimal nextGuess = BigDecimal.ZERO;
                try {
                    nextGuess = guess.add(S.divide(guess, precisionMC)).divide(new BigDecimal(2.0), precisionMC);
                } catch (java.lang.ArithmeticException e) {
                    // I can't think of any number for S that would cause this to be run -Griffin
                    System.out.println(e.getMessage());
                    // sets these to -1 so that -1 is returned. Should an exception be thrown instead?
                    guess = new BigDecimal("-1");
                    nextGuess = new BigDecimal("-1");
                }
                theError = guess.subtract(nextGuess, precisionMC).abs();
                guess = nextGuess;
            }
        }
        return guess;
    }

    /**
     * Obtains an initial guess for the square root of a BigDecimal number. If the number is too small/too large to be
     * represented as a double, 10^(n/2), where n is the number of digits left of the decimal place of S, is used.
     * Otherwise, the square root of the double value is used.
     * @param S
     * @return A number that, hopefully, is close to the square root of S
     */
    private static BigDecimal obtainInitialGuess(BigDecimal S) {
        BigDecimal guess;
        //compareTo() is used instead of equals() because equals() requires the numbers to have the same scale
        if (S.compareTo(BigDecimal.ZERO)==0) {
            guess = BigDecimal.ZERO;
        }
        //ensure that we don't sqrt an infinite and that guess won't be zero
        else if (Double.isInfinite(S.doubleValue())||S.doubleValue() == 0.0) {
            /*Really rough estimate for huge/tiny numbers. If S.doubleValue() returned zero at this point, that means S
            is very small.*/
            int digitsLeftOfDecimalPlace = S.precision() - S.scale();
            guess = BigDecimal.ONE.scaleByPowerOfTen(digitsLeftOfDecimalPlace/2);
        } else {
            guess = new BigDecimal(StrictMath.sqrt(S.doubleValue()));
        }
        return guess;
    }
}