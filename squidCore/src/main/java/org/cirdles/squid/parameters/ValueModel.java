/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 *
 * @author ryanb
 */
public class ValueModel implements Comparable<ValueModel>, Serializable {

    private String name;
    private String uncertaintyType;
    private String reference;
    private BigDecimal value;
    private BigDecimal oneSigma;

    public ValueModel() {
        this.name = "";
        uncertaintyType = "";
        reference = "";
        value = null;
        oneSigma = null;
    }

    public ValueModel(String name) {
        this.name = name;
        uncertaintyType = "";
        reference = "";
        value = null;
        oneSigma = null;
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

    public boolean equals(Object o) {
        boolean retVal = o instanceof ValueModel;
        if (retVal && ((ValueModel) o).getName().compareTo(name) != 0) {
            retVal = false;
        }
        return retVal;
    }

    public BigDecimal getOneSigmaABS() {
        BigDecimal retVal;
        if (uncertaintyType.toUpperCase().equals("PCT")) {
            retVal = oneSigma.multiply(value, new MathContext(15, RoundingMode.HALF_UP)).movePointLeft(2);
        } else {
            retVal = oneSigma;
        }
        return retVal;
    }

    public BigDecimal getOneSigmaPCT() {
        BigDecimal retVal;
        if (uncertaintyType.toUpperCase().equals("PCT")) {
            retVal = oneSigma;
        } else if (value.equals(BigDecimal.ZERO)) {
            retVal = BigDecimal.ZERO;
        } else {
            retVal = oneSigma.divide(value, new MathContext(15, RoundingMode.HALF_UP)).movePointRight(2);
        }
        return retVal;
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
}
