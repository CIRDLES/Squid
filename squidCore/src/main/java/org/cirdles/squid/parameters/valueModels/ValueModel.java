/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.valueModels;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ryanb
 */
public class ValueModel implements Comparable<ValueModel>, Serializable {

    protected String name;
    protected String uncertaintyType;
    protected String reference;
    protected BigDecimal value;
    protected BigDecimal oneSigma;

    public ValueModel() {
        this.name = "";
        uncertaintyType = "ABS";
        reference = "";
        value = BigDecimal.ZERO;
        oneSigma = BigDecimal.ZERO;
    }

    public ValueModel(String name) {
        this.name = name;
        uncertaintyType = "ABS";
        reference = "";
        value = BigDecimal.ZERO;
        oneSigma = BigDecimal.ZERO;
    }

    public ValueModel(String name, String uncertaintyType) {
        this.name = name;
        this.uncertaintyType = uncertaintyType;
        reference = "";
        value = BigDecimal.ZERO;
        oneSigma = BigDecimal.ZERO;
    }

    public ValueModel(String name, String uncertaintyType, BigDecimal value,
                      BigDecimal oneSigma) {
        this.name = name;
        this.uncertaintyType = uncertaintyType;
        this.value = value;
        this.oneSigma = oneSigma;
        this.reference = "";
    }

    public ValueModel(String name, String uncertaintyType, String reference,
                      BigDecimal value, BigDecimal oneSigma) {
        this.name = name;
        this.uncertaintyType = uncertaintyType;
        this.reference = reference;
        this.value = value;
        this.oneSigma = oneSigma;
    }

    @Override
    public int compareTo(ValueModel o) {
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        boolean retVal = o instanceof ValueModel;
        if (retVal && ((ValueModel) o).getName().compareTo(name) != 0) {
            retVal = false;
        }
        return retVal;
    }

    public BigDecimal getOneSigmaABS() {
        BigDecimal retVal;
        if (uncertaintyType.toUpperCase(Locale.ENGLISH).equals("PCT")) {
            retVal = oneSigma.multiply(value, new MathContext(15, RoundingMode.HALF_UP)).movePointLeft(2);
        } else {
            retVal = oneSigma;
        }
        return retVal;
    }

    public BigDecimal getOneSigmaPCT() {
        BigDecimal retVal;
        if (uncertaintyType.toUpperCase(Locale.ENGLISH).equals("PCT")) {
            retVal = oneSigma;
        } else if (value.doubleValue() == 0.0) {
            retVal = BigDecimal.ZERO;
        } else {
            retVal = oneSigma.divide(value, new MathContext(15, RoundingMode.HALF_UP)).movePointRight(2);
        }
        return retVal;
    }

    public boolean hasPositiveValue() {
        return getValue().compareTo(BigDecimal.ZERO) > 0;
    }

    public String formatValueAndTwoSigmaForPublicationSigDigMode(
            String uncertaintyType,
            int movePointRightCount,
            int uncertaintySigDigits) {

        // sept 2009 modified to force use of ABS uncertainty for sigfig counting
        // first determine the shape of significant digits of ABS uncertainty
        String twoSigmaUnct
                = //
                formatTwoSigmaForPublicationSigDigMode(//
                        "ABS", movePointRightCount, uncertaintySigDigits);

        // then generate the ouput string based on uncertainty type
        String twoSigmaUnctOutput
                = //
                formatTwoSigmaForPublicationSigDigMode(//
                        uncertaintyType, movePointRightCount, uncertaintySigDigits);

        // determine location of decimal point in uncertainty
        int countOfDigitsAfterDecPointInError = calculateCountOfDigitsAfterDecPoint(twoSigmaUnct);

        // create string from value
        String valueString
                = //
                getValue().movePointRight(movePointRightCount).toPlainString();

        // revised feb 2008 to do rounding !!
        valueString = new BigDecimal(valueString).//
                setScale(countOfDigitsAfterDecPointInError, RoundingMode.HALF_UP).toPlainString();

        //strip trailing decimal point if any
        if (valueString.endsWith(".")) {
            valueString = valueString.substring(0, valueString.length() - 1);
            // now check if trailing zeroes in uncertainty and zero those in value
            if (twoSigmaUnct.length() > uncertaintySigDigits) {
                // the extra length is zeroes and these need to be transferred to value
                int zeroesCount = twoSigmaUnct.length() - uncertaintySigDigits;
                try {
                    valueString
                            = valueString.substring(0, valueString.length() - zeroesCount)//
                            + "000000000000".substring(0, zeroesCount);
                } catch (Exception e) {
                }
            }
        }

        return valueString + " \u00B1 " + twoSigmaUnctOutput;
    }

    protected int calculateCountOfDigitsAfterDecPoint(String twoSigError) {
        // determine location of decimal point in uncertainty
        int countOfDigitsAfterDecPointInError;
        int indexOfDecPoint = twoSigError.indexOf(".");
        if (indexOfDecPoint < 0) {
            countOfDigitsAfterDecPointInError = 0;
        } else {
            countOfDigitsAfterDecPointInError
                    =//
                    twoSigError.length() - (indexOfDecPoint + 1);
        }

        return countOfDigitsAfterDecPointInError;

    }

    public String formatTwoSigmaForPublicationSigDigMode(
            String uncertaintyType,
            int movePointRightCount,
            int uncertaintySigDigits) {

        if (uncertaintyType.equalsIgnoreCase("PCT")) {
            return formatBigDecimalForPublicationSigDigMode(//
                    getTwoSigmaPct().movePointRight(movePointRightCount),//
                    uncertaintySigDigits);
        } else {
            return formatBigDecimalForPublicationSigDigMode(//
                    getTwoSigmaAbs().movePointRight(movePointRightCount),//
                    uncertaintySigDigits);
        }
    }

    public static String formatBigDecimalForPublicationSigDigMode(
            BigDecimal number,
            int uncertaintySigDigits) {

        if ((number.compareTo(BigDecimal.ZERO) == 0)//
                || // jan 2011 to trap for absurdly small uncertainties
                // july 2011 added abs to handle negative values in tripoli alphas
                (number.abs().doubleValue() < StrictMath.pow(10, -1 * 15))) {
            return "0";
        } else {
            return number.round(new MathContext(//
                    uncertaintySigDigits, RoundingMode.HALF_UP)).toPlainString();
        }
    }

    public void calculateValue(
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms) {
    }

    public BigDecimal getTwoSigmaAbs() {
        return new BigDecimal("2.0").multiply(getOneSigmaABS());
    }

    public BigDecimal getTwoSigmaPct() {
        return new BigDecimal("2.0").multiply(getOneSigmaPCT());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUncertaintyType() {
        return uncertaintyType;
    }

    public void setUncertaintyType(String uncertaintyType) {
        this.uncertaintyType = uncertaintyType;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getOneSigma() {
        return oneSigma;
    }

    public void setOneSigma(BigDecimal oneSigma) {
        this.oneSigma = oneSigma;
    }

    public boolean isPositive() {
        return getOneSigma().compareTo(BigDecimal.ZERO) > 0;
    }
}