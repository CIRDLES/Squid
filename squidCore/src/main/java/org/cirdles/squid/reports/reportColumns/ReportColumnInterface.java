/*
 * Copyright 2015 CIRDLES.
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
package org.cirdles.squid.reports.reportColumns;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.reports.reportSpecifications.ReportSpecificationsUPbSamples;
import org.cirdles.squid.reports.reportViews.ReportListItemI;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;

/**
 *
 * @author bowring
 */
public interface ReportColumnInterface extends Comparable<ReportColumnInterface>, ReportListItemI, Serializable {

    /**
     *
     * @param reportColumn
     * @return
     * @throws ClassCastException
     */
    @Override
    public default int compareTo(ReportColumnInterface reportColumn)
            throws ClassCastException {
        String reportColumnFullName
                = ((ReportColumnInterface) reportColumn).getDisplayName();
        return this.getDisplayName().trim().//
                compareToIgnoreCase(reportColumnFullName.trim());
    }

    /**
     *
     * @return
     */
    @Override
    public default String getDisplayName() {
        if (getAlternateDisplayName().equals("")) {
            return getDisplayName1() + getDisplayName2() + getDisplayName3() + getDisplayName4();
        } else {
            return getAlternateDisplayName();
        }
    }

    /**
     *
     */
    @Override
    public default void ToggleIsVisible() {
        setVisible(!isVisible());
    }

    /**
     *
     * @param reportColumn
     * @return
     */
    @Override
    boolean equals(Object reportColumn);

    /**
     * @return the alternateDisplayName
     */
    String getAlternateDisplayName();

    /**
     *
     * @return
     */
    int getCountOfSignificantDigits();

    /**
     *
     * @return
     */
    String getDisplayName1();

    /**
     *
     * @return
     */
    String getDisplayName2();

    /**
     * @return the displayName3
     */
    String getDisplayName3();

    /**
     * @return the displayName3
     */
    String getDisplayName4();

    /**
     *
     * @return
     */
    String getFootnoteSpec();

    /**
     *
     * @return
     */
    int getPositionIndex();

    /**
     *
     * @return
     */
    String getRetrieveMethodName();

    /**
     *
     * @return
     */
    String getRetrieveVariableName();

    /**
     *
     * @return
     */
    ReportColumnInterface getUncertaintyColumn();

    /**
     *
     * @return
     */
    String getUncertaintyType();

    /**
     *
     * @return
     */
    String getUnits();

    /**
     *
     * @return
     */
    public default String getUnitsFoxXML() {
        String retVal = ReportSpecificationsUPbSamples.unicodeConversionsToXML.get(getUnits());
        if (retVal == null) {
            retVal = getUnits();
        }
        return retVal;
    }

    boolean hasUncertaintyColumn();

    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    /**
     *
     * @return
     */
    int hashCode();

    /**
     * @return the amUncertaintyColumn
     */
    boolean isAmUncertaintyColumn();

    /**
     *
     * @return
     */
    boolean isDisplayedWithArbitraryDigitCount();

    /**
     * @return the legacyData
     */
    boolean isLegacyData();

    /**
     * @return the needsPb
     */
    boolean isNeedsPb();

    /**
     * @return the needsU
     */
    boolean isNeedsU();

    /**
     *
     * @return
     */
    @Override
    boolean isVisible();

    /**
     * @param alternateDisplayName the alternateDisplayName to set
     */
    void setAlternateDisplayName(String alternateDisplayName);

    /**
     *
     * @param countOfSignificantDigits
     */
    void setCountOfSignificantDigits(int countOfSignificantDigits);

    /**
     *
     * @param displayName1
     */
    void setDisplayName1(String displayName1);

    /**
     *
     * @param displayName2
     */
    void setDisplayName2(String displayName2);

    /**
     * @param displayName3 the displayName3 to set
     */
    void setDisplayName3(String displayName3);

    /**
     *
     * @param displayedWithArbitraryDigitCount
     */
    void setDisplayedWithArbitraryDigitCount(boolean displayedWithArbitraryDigitCount);

    /**
     *
     * @param footnoteSpec
     */
    void setFootnoteSpec(String footnoteSpec);

    /**
     * @param legacyData the legacyData to set
     */
    void setLegacyData(boolean legacyData);

    /**
     * @param needsPb the needsPb to set
     */
    void setNeedsPb(boolean needsPb);

    /**
     * @param needsU the needsU to set
     */
    void setNeedsU(boolean needsU);

    /**
     *
     * @param positionIndex
     */
    @Override
    void setPositionIndex(int positionIndex);

    /**
     *
     * @param retrieveMethodName
     */
    void setRetrieveMethodName(String retrieveMethodName);

    /**
     *
     * @param retrieveVariableName
     */
    void setRetrieveVariableName(String retrieveVariableName);

    /**
     *
     * @param uncertaintyColumn
     */
    void setUncertaintyColumn(ReportColumnInterface uncertaintyColumn);

    /**
     *
     * @param uncertaintyType
     */
    void setUncertaintyType(String uncertaintyType);

    /**
     *
     * @param units
     */
    void setUnits(String units);

    /**
     *
     * @param xmlCode
     */
    void setUnitsFromXML(String xmlCode);

    /**
     *
     * @param visible
     */
    @Override
    void setVisible(boolean visible);

    /**
     *
     * @param numericString
     * @return
     */
    public static String FormatNumericStringAlignDecimalPoint(String numericString) {
        // precondition: can fit within 123456789.0123456789
        int countOfLeadingDigits = 9;
        int totalStringLength = 25;
        String twentySpaces = "                         ";

        String retVal = "---";

        int indexOfPoint = numericString.indexOf(".");
        if (indexOfPoint == -1) {
            indexOfPoint = numericString.length();
        }

        // nov 2014
        if (numericString.length() > totalStringLength) {
            //retVal = "       Too Large";
            // Jan 2015 refinement
            retVal = numericString.substring(0, totalStringLength - 1);
        } else {
            // pad left
            retVal = twentySpaces.substring(0, Math.abs(countOfLeadingDigits - indexOfPoint)) + numericString;
        }

        // pad right
        try {
            retVal += twentySpaces.substring(0, totalStringLength - retVal.length());
        } catch (Exception e) {
            // Jan 2015 refinement
            retVal = retVal.substring(0, totalStringLength - 1);
            //System.out.println("RETVAL " + retVal);
        }
        return retVal;
    }

    /**
     *
     * @param fraction
     * @param isNumeric
     * @return
     */
    public default String[] getReportRecordByColumnSpec(ShrimpFractionExpressionInterface fraction, boolean isNumeric) {
        // returns an entry for the value and one for the uncertainty if it exists
        // there are two possible modes : sigfig and arbitrary
        // if sigfig, the string contains only the sig digits forced to length
        // 20 with the decimal point at position 9 (0-based index) with space fillers
        // if arbitrary, the string is the bigdecimal number with 15 places after the
        // decimal
        // these "raw" strings will be post-processed by the report engine
        String[] retVal = new String[2];

        // default for value column
        retVal[0] = "NOT FOUND";
        // default for uncertainty column
        retVal[1] = "";

        if (!getRetrieveMethodName().equals("")) {
            // get fraction field by using reflection
            //String retrieveVariableName = getRetrieveVariableName();
            try {
                Class<?> fractionClass
                        = Class.forName(ShrimpFractionExpressionInterface.class.getCanonicalName());

                // this is the case of fractionID, the only string returned
                if (getRetrieveVariableName().length() == 0) {
                    try {
                        Method meth
                                = fractionClass.getMethod(//
                                        getRetrieveMethodName(),
                                        new Class[0]);

                        Object o = meth.invoke(fraction, new Object[0]);

                        retVal[0] = o.toString();
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        System.err.println(e);
                    }
                } else if (getRetrieveVariableName().compareToIgnoreCase("<DATE>") == 0) {
                    try {
                        Method meth
                                = fractionClass.getMethod(//
                                        getRetrieveMethodName(),
                                        new Class[0]);

                        long milliseconds = (long) meth.invoke(fraction, new Object[0]);

                        retVal[0] = CalamariReportsEngine.getFormattedDate(milliseconds);
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        System.err.println(e);
                    }
                } else if (getRetrieveVariableName().compareToIgnoreCase("<INT>") == 0) {
                    try {
                        Method meth
                                = fractionClass.getMethod(//
                                        getRetrieveMethodName(),
                                        new Class[0]);

                        int intValue = (int) meth.invoke(fraction, new Object[0]);

                        retVal[0] = String.valueOf(intValue);
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        System.err.println(e);
                    }
                } else if (getRetrieveVariableName().compareToIgnoreCase("<DOUBLE>") == 0) {
                    try {
                        Method meth
                                = fractionClass.getMethod(//
                                        getRetrieveMethodName(),
                                        new Class[0]);

                        double doubleValue = (double) meth.invoke(fraction, new Object[0]);

                        if (isNumeric) {
                            retVal[0] = String.valueOf(doubleValue);
                        } else if (isDisplayedWithArbitraryDigitCount()) {
                            retVal[0] = formatBigDecimalForPublicationArbitraryMode(//
                                    new BigDecimal(doubleValue),
                                    getCountOfSignificantDigits());
                        }
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        System.err.println(e);
                    }
                } else if (getRetrieveVariableName().startsWith("<INDEX>")) {
                    int index = Integer.parseInt(getRetrieveVariableName().split(">")[1]);
                    try {
                        Method meth
                                = fractionClass.getMethod(//
                                        getRetrieveMethodName(),
                                        new Class[0]);

                        double doubleValue = ((double[]) meth.invoke(fraction, new Object[0]))[index];

                        if (isNumeric) {
                            retVal[0] = String.valueOf(doubleValue);
                        } else if (isDisplayedWithArbitraryDigitCount()) {
                            retVal[0] = formatBigDecimalForPublicationArbitraryMode(//
                                    new BigDecimal(doubleValue),
                                    getCountOfSignificantDigits());
                        }
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        System.err.println(e);
                    }
                } else {
                    try {
                        Method meth = fractionClass.getMethod(//
                                getRetrieveMethodName(),
                                new Class[]{String.class});

                        double[] vm = ((double[][]) meth.invoke(fraction, new Object[]{getRetrieveVariableName()}))[0].clone();

                        if (isNumeric) {
                            retVal[0] = getValueInUnits(vm[0], getUnits()).toPlainString().trim();
                        } else if (isDisplayedWithArbitraryDigitCount()) {
                            retVal[0] = formatBigDecimalForPublicationArbitraryMode(//
                                    getValueInUnits(vm[0], getUnits()),
                                    getCountOfSignificantDigits());
                        } else {
                            // value is in sigfig mode = two flavors
                            // if there is no uncertainty column, then show the value with
                            // normal sigfig formatting
                            // if there is an uncertainty column and it is in arbitrary mode, then
                            // also show value with normal sigfig formatting

                            retVal[0] = formatBigDecimalForPublicationSigDigMode(
                                    new BigDecimal(vm[0]).movePointRight(Squid3Constants.getUnitConversionMoveCount(getUnits())),//
                                    getCountOfSignificantDigits());

                            // however, if uncertainty column is in sigfig mode, then
                            // use special algorithm to format value per the digits of
                            // the formatted uncertainty column
                            if (getUncertaintyColumn() != null) {
                                // added July 2017 to disable uncert column effect if it is not visible (making it behave as if arbitrary)
                                if (getUncertaintyColumn().isVisible() && !getUncertaintyColumn().isDisplayedWithArbitraryDigitCount()) {
                                    // uncertainty column is in sigfig mode
                                    retVal[0] = formatValueFromOneSigmaForPublicationSigDigMode(//
                                            vm[0], vm[1],
                                            getUncertaintyType(), Squid3Constants.getUnitConversionMoveCount(getUnits()),//
                                            getUncertaintyColumn().getCountOfSignificantDigits());
                                }
                            }
//                            // in either case, we have a sigfig mode for the value
//                            retVal[0] = FormatNumericStringAlignDecimalPoint(retVal[0]);
                        }
                        // in nonnumeric case, we need to format string
                        if (!isNumeric) {
                            retVal[0] = ReportColumnInterface.FormatNumericStringAlignDecimalPoint(retVal[0]);
                        }

                        // report 1-sigma uncertainty
                        if (getUncertaintyColumn() != null) {
                            if (getUncertaintyColumn().isVisible()) {
                                // check for reporting mode

                                if ((vm[0] > 0.0) && (vm[0] < 10e-20)) {
                                    // may 2013 for tiny numbers due to below detection
                                    retVal[1] = " bd "; // below detection

                                } else if (vm[0] == 0.0) {//oct 2014
                                    retVal[1] = " - ";

                                } else if (isNumeric) {
                                    retVal[1]
                                            = getOneSigma(vm[0], vm[1], getUncertaintyType(), getUnits()).toPlainString().trim();
                                } else if (getUncertaintyColumn().isDisplayedWithArbitraryDigitCount()) {
                                    retVal[1]
                                            = formatBigDecimalForPublicationArbitraryMode(//
                                                    getOneSigma(vm[0], vm[1], getUncertaintyType(), getUnits()),
                                                    getUncertaintyColumn().getCountOfSignificantDigits());
                                } else {
                                    retVal[1] = formatOneSigmaForPublicationSigDigMode(//
                                            vm[0],
                                            vm[1],
                                            getUncertaintyType(),
                                            Squid3Constants.getUnitConversionMoveCount(getUnits()),
                                            getUncertaintyColumn().getCountOfSignificantDigits());
                                }
                                retVal[1] = ReportColumnInterface.FormatNumericStringAlignDecimalPoint(retVal[1]);
                                // }
                            }
                        }

                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        System.err.println("problem formatting " + getRetrieveVariableName() + " for " + fraction.getFractionID() + " >> " + e);
                    }
                }

            } catch (ClassNotFoundException classNotFoundException) {
            }
        }

        return retVal;
    }

    // these staitic methods stolen from ET_Redux valueModel for now
    static BigDecimal getValueInUnits(double value, String units) {
        int shiftPointRightCount = 0;

        try {
            shiftPointRightCount = Squid3Constants.getUnitConversionMoveCount(units);
        } catch (Exception e) {
        }

        return new BigDecimal(value).movePointRight(shiftPointRightCount);
    }

    static String formatBigDecimalForPublicationArbitraryMode(
            BigDecimal number,
            int roundingDigits) {
        // if roundingDigits > -1, then return that many places to the right of decimal
        if (roundingDigits > -1) {
            return number.setScale(roundingDigits, RoundingMode.HALF_UP).toPlainString();
        } else {
            return "fix me";
        }
    }

    static String formatValueFromOneSigmaForPublicationSigDigMode(
            double value,
            double oneSigmaAbs,
            String uncertaintyType,
            int movePointRightCount,
            int uncertaintySigDigits) {

        String temp = formatValueAndOneSigmaForPublicationSigDigMode(//
                value, oneSigmaAbs,
                uncertaintyType, movePointRightCount, uncertaintySigDigits);

        String[] retVal = temp.split("\u00B1");

        return retVal[0].trim();
    }

    static String formatValueAndOneSigmaForPublicationSigDigMode(
            double value,
            double oneSigmaAbs,
            String uncertaintyType,
            int movePointRightCount,
            int uncertaintySigDigits) {

        // sept 2009 modified to force use of ABS uncertainty for sigfig counting
        // first determine the shape of significant digits of ABS uncertainty
        String oneSigmaUnct
                = formatOneSigmaForPublicationSigDigMode(//
                        value, oneSigmaAbs,
                        "ABS", movePointRightCount, uncertaintySigDigits);

        // then generate the ouput string based on uncertainty type
        String oneSigmaUnctOutput
                = formatOneSigmaForPublicationSigDigMode(//
                        value, oneSigmaAbs,
                        uncertaintyType, movePointRightCount, uncertaintySigDigits);

        // determine location of decimal point in uncertainty
        int countOfDigitsAfterDecPointInError = calculateCountOfDigitsAfterDecPoint(oneSigmaUnct);

        // create string from value
        String valueString
                = new BigDecimal(value).movePointRight(movePointRightCount).toPlainString();

        // revised feb 2008 to do rounding !!
        valueString = new BigDecimal(valueString).//
                setScale(countOfDigitsAfterDecPointInError, RoundingMode.HALF_UP).toPlainString();

        //strip trailing decimal point if any
        if (valueString.endsWith(".")) {
            valueString = valueString.substring(0, valueString.length() - 1);
            // now check if trailing zeroes in uncertainty and zero those in value
            if (oneSigmaUnct.length() > uncertaintySigDigits) {
                // the extra length is zeroes and these need to be transferred to value
                int zeroesCount = oneSigmaUnct.length() - uncertaintySigDigits;
                try {
                    valueString
                            = valueString.substring(0, valueString.length() - zeroesCount)//
                            + "000000000000".substring(0, zeroesCount);
                } catch (Exception e) {
                }
            }
        }

        return valueString + " \u00B1 " + oneSigmaUnctOutput;
    }

    static String formatOneSigmaForPublicationSigDigMode(
            double value,
            double oneSigmaAbs,
            String uncertaintyType,
            int movePointRightCount,
            int uncertaintySigDigits) {

        if (uncertaintyType.equalsIgnoreCase("PCT")) {
            return formatBigDecimalForPublicationSigDigMode(//
                    new BigDecimal(oneSigmaAbs / value * 100.0).movePointRight(movePointRightCount),//
                    uncertaintySigDigits);
        } else {
            return formatBigDecimalForPublicationSigDigMode(//
                    new BigDecimal(oneSigmaAbs * 1.0).movePointRight(movePointRightCount),//
                    uncertaintySigDigits);
        }
    }

    static String formatBigDecimalForPublicationSigDigMode(
            BigDecimal number,
            int uncertaintySigDigits) {

        if ((number.compareTo(BigDecimal.ZERO) == 0)//
                || // jan 2011 to trap for absurdly small uncertainties
                (number.abs().doubleValue() < Math.pow(10, -1 * 15))) {
            return "0";
        } else {
            return number.round(new MathContext(//
                    uncertaintySigDigits, RoundingMode.HALF_UP)).toPlainString();
        }
    }

    static BigDecimal getOneSigma(double value, double oneSigmaAbs, String uncertaintyType, String units) {
        int shiftPointRightCount = 0;

        try {
            shiftPointRightCount = Squid3Constants.getUnitConversionMoveCount(units);
        } catch (Exception e) {
        }

        if (uncertaintyType.equalsIgnoreCase("PCT")) {
            return getOneSigmaPct(value, oneSigmaAbs).movePointRight(shiftPointRightCount);
        } else {
            return getOneSigmaAbs(oneSigmaAbs).movePointRight(shiftPointRightCount);
        }
    }

    static BigDecimal getOneSigmaAbs(double oneSigmaAbs) {
        return new BigDecimal(oneSigmaAbs);
    }

    /**
     *
     * @return
     */
    static BigDecimal getOneSigmaPct(double value, double oneSigmaAbs) {
        return new BigDecimal(oneSigmaAbs / value * 100.0);
    }

    static int calculateCountOfDigitsAfterDecPoint(String oneSigError) {
        // determine location of decimal point in uncertainty
        int countOfDigitsAfterDecPointInError;
        int indexOfDecPoint = oneSigError.indexOf(".");
        if (indexOfDecPoint < 0) {
            countOfDigitsAfterDecPointInError = 0;
        } else {
            countOfDigitsAfterDecPointInError
                    = oneSigError.length() - (indexOfDecPoint + 1);
        }

        return countOfDigitsAfterDecPointInError;

    }
}
