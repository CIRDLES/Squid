/*
 * ValueModel.java
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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
package org.cirdles.squid.shrimp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.ConcurrentMap;

/**
 * A
 * <code>ValueModel</code> object represents scientifically measured quantities
 * and their errors. It also provides additional methods for manipulating and
 * publishing these values.
 *
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/XStream.html>com.thoughtworks.xstream.XStream</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/ConversionException.html>com.thoughtworks.xstream.converters.ConversionException</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/xml/DomDriver.html>com.thoughtworks.xstream.io.xml.DomDriver</a>
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class ValueModel implements
        Serializable,
        Comparable<ValueModel>{

    // Instance variables
    /**
     * name of <code>ValueModel</code>, such as ratio name.
     */
    protected String name;
    /**
     * numerical value of <code>ValueModel</code>
     */
    protected BigDecimal value;
    /**
     * type of uncertainty stored in <code>ValueModel</code>; ABS or PCT
     */
    protected String uncertaintyType;
    /**
     * one Sigma of <code>ValueModel</code>
     */
    protected BigDecimal oneSigma;

    /**
     * creates a new instance of <code>ValueModel</code> with <code>name</code>,
     * <code>value</code>, <code>uncertainty type</code>, and
     * <code>one sigma</code> fields initialized to "NONE", 0, "NONE", and 0
     * respectively.
     */
    public ValueModel() {
        this.name = "NONE";
        this.value = BigDecimal.ZERO;
        this.uncertaintyType = "NONE";
        this.oneSigma = BigDecimal.ZERO;
    }

    /**
     * creates a new instance of <code>ValueModel</code> with a specified
     * <code>name</code>, a default <code>value</code> and
     * <code>one sigma</code>, and <code>uncertainty type</code> initialized to
     * "NONE".
     *
     * @param name name of the ratio that this <code>ValueModel</code>
     * represents
     */
    public ValueModel(String name) {
        this();
        this.name = name.trim();
    }

    /**
     * creates a new instance of <code>ValueModel</code> with a specified
     * <code>name</code> and <code>uncertainty type</code> and a default
     * <code>value</code> and <code>one sigma</code>
     *
     * @param name name of the ratio that this <code>ValueModel</code>
     * represents
     * @param uncertaintyType type of uncertainty; ABS or PCT
     */
    public ValueModel(String name, String uncertaintyType) {
        this(name);
        String temp = uncertaintyType.trim();
        if (!temp.equalsIgnoreCase("ABS") && !temp.equalsIgnoreCase("PCT")) {
            temp = "NONE";
        }
        this.uncertaintyType = temp;
    }

    /**
     * creates a new instance of <code>ValueModel</code> with a specified
     * <code>name</code>, <code>value</code>, <code>uncertainty type</code>, and
     * <code>one sigma</code>
     *
     * @param name name of the ratio that this <code>ValueModel</code>
     * represents
     * @param value numerical value of ratio
     * @param uncertaintyType type of uncertainty; ABS or PCT
     * @param oneSigma value of one standard deviation
     */
    public ValueModel(
            String name, BigDecimal value, String uncertaintyType, BigDecimal oneSigma) {

        this(name, uncertaintyType);
        this.value = value;
        this.oneSigma = oneSigma.abs();
    }

    /**
     * returns a deep copy of a <code>ValueModel</code>; a new      <code>ValueModel
     * </code> whose fields are equal to those of this <code>ValueModel</code>
     *
     * @pre this <code>ValueModel</code> exists @post a new
     * <code>ValueModel</code> with identical data to this      <code>
     *          ValueModel</code> is returned
     *
     * @return <code>ValueModel</code> - a new <code>ValueModel</code> whose
     * fields match those of this <code>ValueModel</code>
     */
    public ValueModel copy() {
        ValueModel retval =//
                new ValueModel(
                        getName(),
                        getValue(),
                        getUncertaintyType(),
                        getOneSigma());
        return retval;
    }

    /**
     *
     * @param inputValueModels
     * @param parDerivTerms
     */
    public void calculateValue(
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms) {
    }

    /**
     * sets the <code>value</code>, <code>uncertainty type</code>, and
     * <code>one sigma</code> of this <code>ValueModel</code> to those of the
     * argument <code>valueModel</code>
     *
     * @pre argument <code>valueModel</code> is a valid <code>ValueModel</code>
     * @post this <code>ValueModel</code>'s <code>value</code>,      <code>uncertainty
     *          type</code>, and <code>one sigma</code> are set to the argument
     * <code>valueModel</code>'s respective values
     *
     * @param valueModel <code>ValueModel</code> whose fields will be copied
     */
    public void copyValuesFrom(ValueModel valueModel) {
        this.setValue(valueModel.getValue());
        this.setUncertaintyType(valueModel.getUncertaintyType());
        this.setOneSigma(valueModel.getOneSigma());
    }

    /**
     * compares this <code>ValueModel</code> with the argument      <code>valueModel
     * </code> by <code>name</code>. Returns an <code>integer</code> based on
     * comparison
     *
     * @pre <code>valueModel</code> exists @post an <code>integer</code> is
     * returned that relates whether the given <code>ValueModel</code> is
     * lexicographically less than, equal to, or greater than this
     * <code>ValueModel</code>
     *
     * @param valueModel the <code>ValueModel</code> to be compared to this one
     * @return int - 0 if the argument <code>ValueModel</code> is equal to this
     * <code>ValueModel</code>, less than 0 if it is less than this      <code>
     *          ValueModel</code>, and greater than 0 if it is greater than this
     * <code>ValueModel</code>
     * @throws java.lang.ClassCastException a ClassCastException
     */
    @Override
    public int compareTo(ValueModel valueModel) throws ClassCastException {
        String argName = valueModel.getName();
        return this.getName().trim().compareToIgnoreCase(argName.trim());
    }

    /**
     * checks for lexicographic equivalence between this <code>ValueModel</code>
     * and the argument <code>valueModel</code>. Returns <code>false</code> if
     * the <code>object</code> given is not a <code>ValueModel</code>
     *
     * @pre <code>valueModel</code> exists @post a <code>boolean</code> is
     * returned - <code>true</code> if argument is equal to this
     * <code>ValueModel</code>, else <code>false</code>
     *
     * @param valueModel the <code>Object</code> to be compared to this
     * <code>ValueModel</code>
     * @return boolean - <code>true</code> if the argument
     * <code>valueModel</code> is this <code>ValueModel</code> or is
     * lexicographically equivalent, else <code>false</code>
     */
    @Override
    public boolean equals(Object valueModel) {
        //check for self-comparison
        if (this == valueModel) {
            return true;
        }
        if (!(valueModel instanceof ValueModel)) {
            return false;
        }

        ValueModel myValueModel = (ValueModel) valueModel;
        return (this.getName().trim().compareToIgnoreCase(myValueModel.getName().trim()) == 0);
    }

    /**
     * returns 0 as the hashcode for this <code>ValueModel</code>. Implemented
     * to meet equivalency requirements as documented by
     * <code>java.lang.Object</code>
     *
     * @pre <code>ValueModel</code> exists @post hashcode of 0 is returned for
     * this <code>ValueModel</code>
     *
     * @return int - 0
     */
    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    @Override
    public int hashCode() {
        return 0;
    }

    // Field Accessors

    /**
     * gets the value of the <code>name</code> field
     *
     * @pre this <code>ValueModel</code> exists @post <code>name</code> of this
     * <code>ValueModel</code> is returned
     *
     * @return <code>String</code> - <code>name</code> of this
     * <code>ValueModel</code>
     */
    public String getName() {
        return name;
    }

    /**
     * sets the value of the <code>name</code> field
     *
     * @pre argument <code>name</code> is a valid <code>String</code> @post
     * <code>name</code> of this <code>ValueModel</code> is set to argument
     * <code>name</code>
     *
     * @param name value to which this <code>ValueModel</code>'s
     * <code>name</code> is set
     */
    public void setName(String name) {
        this.name = name.trim();
    }

    /**
     * gets the value of the <code>value</code> field
     *
     * @pre this <code>ValueModel</code> exists @post <code>value</code> of this
     * <code>ValueModel</code> is returned
     *
     * @return <code>BigDecimal</code> - <code>value</code> of this
     * <code>ValueModel</code>
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * sets the value of the <code>value</code> field
     *
     * @pre argument <code>value</code> is a valid <code>BigDecimal</code> @post
     * <code>value</code> of this <code>ValueModel</code> is set to argument
     * <code>value</code>
     *
     * @param value value to which this <code>ValueModel</code>'s
     * <code>value</code> is set
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     *
     * @param value
     */
    public void setValue(double value) {
        if (Double.isFinite(value)) {
            this.value = new BigDecimal(value);
        } else {
            this.value = BigDecimal.ZERO;
        }
    }

    /**
     * gets the value of the <code>uncertainty type</code> field
     *
     * @pre this <code>ValueModel</code> exists @post
     * <code>uncertainty type</code> of this <code>ValueModel</code> is returned
     *
     * @return <code>String</code> - <code>uncertainty type</code> of this
     * <code>ValueModel</code>
     */
    public String getUncertaintyType() {
        return uncertaintyType;
    }

    /**
     * sets the value of the <code>uncertainty type</code> field
     *
     * @pre argument <code>uncertaintyType</code> is a valid <code>String</code>
     * @post <code>uncertainty type</code> of this <code>ValueModel</code> is
     * set to argument <code>uncertaintyType</code>
     *
     * @param uncertaintyType value to which this <code>ValueModel</code>'s
     * <code>uncertainty type</code> is set
     */
    public void setUncertaintyType(String uncertaintyType) {
        String temp = uncertaintyType.trim();
        if (!temp.equalsIgnoreCase("ABS") && !temp.equalsIgnoreCase("PCT")) {
            temp = "NONE";
        }
        this.uncertaintyType = temp;
    }

    /**
     *
     */
    public void setUncertaintyTypeABS() {
        this.uncertaintyType = "ABS";
    }

    /**
     *
     */
    public void setUncertaintyTypePCT() {
        this.uncertaintyType = "PCT";
    }

    /**
     * gets the value of the <code>one sigma</code> field
     *
     * @pre this <code>ValueModel</code> exists @post <code>one sigma</code> of
     * this <code>ValueModel</code> is returned
     *
     * @return <code>BigDecimal</code> - <code>one sigma</code> of this
     * <code>ValueModel</code>
     */
    public BigDecimal getOneSigma() {
        return oneSigma;
    }

    /**
     * sets the value of the <code>one sigma</code> field
     *
     * @pre argument <code>oneSigma</code> is a valid <code>BigDecimal</code>
     * @post <code>one sigma</code> of this <code>ValueModel</code> is set to
     * argument <code>oneSigma</code>
     *
     * @param oneSigma value to which this <code>ValueModel</code>'s
     * <code>one sigma</code> is set
     */
    public void setOneSigma(BigDecimal oneSigma) {
        this.oneSigma = oneSigma.abs();
    }

    /**
     *
     * @param oneSigma
     */
    public void setOneSigma(double oneSigma) {
        this.oneSigma = new BigDecimal(oneSigma).abs();
    }

    /**
     * gets the value of the <code>one sigma</code> field as ABS uncertainty and
     * if needed converts PCT uncertainty type to ABS by 100 *
     * variableOneSigmaAbs / variable.
     *
     * @pre this <code>ValueModel</code> exists @post <code>one sigma</code> of
     * this <code>ValueModel</code> when using ABS <code>uncertainty type</code>
     * is returned
     *
     * @return <code>BigDecimal</code> - <code>one sigma</code> value of this
     * <code>ValueModel</code> using ABS <code>uncertainty type</code>
     */
    public BigDecimal getOneSigmaAbs() {
        if (getUncertaintyType().equalsIgnoreCase("PCT")) {
            return oneSigma.multiply(value).movePointLeft(2);
        } else {
            return oneSigma;
        }
    }

    /**
     *
     * @param valueModel
     * @param oneSigmaAbs
     * @return
     */
    public static BigDecimal convertOneSigmaAbsToPctIfRequired(//
            ValueModel valueModel, BigDecimal oneSigmaAbs) {
        if (valueModel.getUncertaintyType().equalsIgnoreCase("PCT")) {
            return oneSigmaAbs.divide(valueModel.getValue(),MathContext.DECIMAL32)//
                    .movePointRight(2);
        } else {
            return oneSigmaAbs;
        }
    }

    /**
     *
     * @param valueModel
     * @param oneSigmaPct
     * @return
     */
    public static BigDecimal convertOneSigmaPctToAbsIfRequired(ValueModel valueModel, BigDecimal oneSigmaPct) {
        if (valueModel.getUncertaintyType().equalsIgnoreCase("ABS")) {
            return oneSigmaPct.multiply(valueModel.getValue()).movePointLeft(2);
        } else {
            return oneSigmaPct;
        }
    }

    /**
     * gets the value of the <code>one sigma</code> field as PCT value
     *
     * @pre this <code>ValueModel</code> exists @post <code>one sigma</code> of
     * this <code>ValueModel</code> when using PCT <code>uncertainty type</code>
     * is returned
     *
     * @return <code>BigDecimal</code> - <code>one sigma</code> value of this
     * <code>ValueModel</code> using PCT <code>uncertainty type</code>
     */
    public BigDecimal getOneSigmaPct() {
        if (getUncertaintyType().equalsIgnoreCase("PCT")) {
            return oneSigma;
        } else if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return oneSigma.divide(value, MathContext.DECIMAL32).movePointRight(2);
        }
    }

    /**
     * gets the value of two sigma as ABS value
     *
     * @pre this <code>ValueModel</code> exists @post two sigma of this
     * <code>ValueModel</code> using ABS <code>uncertainty type</code> is
     * returned
     *
     * @return <code>BigDecimal</code> - two sigma value of this      <code>ValueModel
     * </code> using ABS <code>uncertainty type</code>
     */
    public BigDecimal getTwoSigmaAbs() {
        return new BigDecimal("2.0").multiply(getOneSigmaAbs());
    }

    /**
     *
     * @return
     */
    public BigDecimal getTwoSigmaPct() {
        return new BigDecimal("2.0").multiply(getOneSigmaPct());
    }

}
