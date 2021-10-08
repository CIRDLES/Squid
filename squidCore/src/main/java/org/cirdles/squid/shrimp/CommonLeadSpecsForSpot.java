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
package org.cirdles.squid.shrimp;

import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.StaceyKramerCommonLeadModel;
import org.cirdles.squid.tasks.expressions.builtinExpressions.ReferenceMaterialAgeTypesEnum;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SampleAgeTypesEnum;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class CommonLeadSpecsForSpot implements Serializable {

    private static final long serialVersionUID = -3180471908770010857L;

    public static final int METHOD_COMMON_LEAD_MODEL = 0;
    public static final int METHOD_STACEY_KRAMER = 1;
    public static final int METHOD_STACEY_KRAMER_BY_GROUP = 2;

    private double com_206Pb204Pb;
    private double com_207Pb206Pb;
    private double com_208Pb206Pb;
    private double com_206Pb208Pb;
    private double com_207Pb204Pb;
    private double com_208Pb204Pb;

    // methods: 0 = commonLeadModel, 1 = StaceyKramer, 2 = StaceyKramer per group
    private int methodSelected;

    private SampleAgeTypesEnum sampleAgeType;
    private ReferenceMaterialAgeTypesEnum refMatAgeType;
    private double sampleAgeSK;

    private ParametersModel commonLeadModel;

    public CommonLeadSpecsForSpot() throws SquidException {
        this.com_206Pb204Pb = 0.0;
        this.com_207Pb206Pb = 0.0;
        this.com_208Pb206Pb = 0.0;
        this.com_206Pb208Pb = 0.0;
        this.com_207Pb204Pb = 0.0;
        this.com_208Pb204Pb = 0.0;

        this.methodSelected = METHOD_COMMON_LEAD_MODEL;

        this.sampleAgeType = SampleAgeTypesEnum.PB4COR206_238AGE;
        this.refMatAgeType = ReferenceMaterialAgeTypesEnum.PB4COR206_238AGE_RM;
        this.sampleAgeSK = 0.0;

        this.commonLeadModel = SquidLabData.getExistingSquidLabData().getCommonPbDefault();
    }

    public String correctionMetaData() {
        StringBuilder metaData = new StringBuilder();

        switch (methodSelected) {
            case METHOD_COMMON_LEAD_MODEL:
                metaData.append(commonLeadModel.getModelNameWithVersion());
                break;
            case METHOD_STACEY_KRAMER:
                metaData.append("SK");
                break;
            case METHOD_STACEY_KRAMER_BY_GROUP:
                metaData.append("SK @ " + (new BigDecimal(sampleAgeSK)).movePointLeft(6).setScale(0, RoundingMode.HALF_UP) + " Ma");
                break;
        }

        return metaData.toString();
    }

    /**
     * @return the com_206Pb204Pb
     */
    public double getCom_206Pb204Pb() {
        return com_206Pb204Pb;
    }

    /**
     * @param com_206Pb204Pb the com_206Pb204Pb to set
     */
    public void setCom_206Pb204Pb(double com_206Pb204Pb) {
        this.com_206Pb204Pb = com_206Pb204Pb;
    }

    /**
     * @return the com_207Pb206Pb
     */
    public double getCom_207Pb206Pb() {
        return com_207Pb206Pb;
    }

    /**
     * @param com_207Pb206Pb the com_207Pb206Pb to set
     */
    public void setCom_207Pb206Pb(double com_207Pb206Pb) {
        this.com_207Pb206Pb = com_207Pb206Pb;
    }

    /**
     * @return the com_208Pb206Pb
     */
    public double getCom_208Pb206Pb() {
        return com_208Pb206Pb;
    }

    /**
     * @param com_208Pb206Pb the com_208Pb206Pb to set
     */
    public void setCom_208Pb206Pb(double com_208Pb206Pb) {
        this.com_208Pb206Pb = com_208Pb206Pb;
    }

    /**
     * @return the com_206Pb208Pb
     */
    public double getCom_206Pb208Pb() {
        return com_206Pb208Pb;
    }

    /**
     * @param com_206Pb208Pb the com_206Pb208Pb to set
     */
    public void setCom_206Pb208Pb(double com_206Pb208Pb) {
        this.com_206Pb208Pb = com_206Pb208Pb;
    }

    /**
     * @return the com_207Pb204Pb
     */
    public double getCom_207Pb204Pb() {
        return com_207Pb204Pb;
    }

    /**
     * @param com_207Pb204Pb the com_207Pb204Pb to set
     */
    public void setCom_207Pb204Pb(double com_207Pb204Pb) {
        this.com_207Pb204Pb = com_207Pb204Pb;
    }

    /**
     * @return the com_208Pb204Pb
     */
    public double getCom_208Pb204Pb() {
        return com_208Pb204Pb;
    }

    /**
     * @param com_208Pb204Pb the com_208Pb204Pb to set
     */
    public void setCom_208Pb204Pb(double com_208Pb204Pb) {
        this.com_208Pb204Pb = com_208Pb204Pb;
    }

    /**
     * @return the methodSelected
     */
    public int getMethodSelected() {
        return methodSelected;
    }

    /**
     * @param methodSelected the methodSelected to set
     */
    public void setMethodSelected(int methodSelected) {
        this.methodSelected = methodSelected;
    }

    /**
     * @return the sampleAgeType
     */
    public SampleAgeTypesEnum getSampleAgeType() {
        return sampleAgeType;
    }

    /**
     * @param sampleAgeType the sampleAgeType to set
     */
    public void setSampleAgeType(SampleAgeTypesEnum sampleAgeType) {
        this.sampleAgeType = sampleAgeType;
    }

    public ReferenceMaterialAgeTypesEnum getRefMatAgeType() {
        if (refMatAgeType == null) {
            refMatAgeType = ReferenceMaterialAgeTypesEnum.PB4COR206_238AGE_RM;
        }
        return refMatAgeType;
    }

    public void setRefMatAgeType(ReferenceMaterialAgeTypesEnum refMatAgeType) {
        this.refMatAgeType = refMatAgeType;
    }

    /**
     * @return the sampleAgeSK
     */
    public double getSampleAgeSK() {
        return sampleAgeSK;
    }

    /**
     * @param sampleAgeSK the sampleAgeSK to set
     */
    public void setSampleAgeSK(double sampleAgeSK) {
        this.sampleAgeSK = sampleAgeSK;
    }

    /**
     * @return the commonLeadModel
     */
    public ParametersModel getCommonLeadModel() {
        return commonLeadModel;
    }

    /**
     * @param commonPbModel
     */
    public void setCommonLeadModel(ParametersModel commonPbModel) {
        this.commonLeadModel = commonPbModel;
        if (methodSelected == METHOD_COMMON_LEAD_MODEL) {
            updateCommonLeadRatiosFromModel();
        }
    }

    public void updateCommonLeadRatiosFromModel() {
        com_206Pb204Pb = commonLeadModel.getDatumByName(R206_204B).getValue().doubleValue();
        com_207Pb204Pb = commonLeadModel.getDatumByName(R207_204B).getValue().doubleValue();
        com_208Pb204Pb = commonLeadModel.getDatumByName(R208_204B).getValue().doubleValue();

        com_207Pb206Pb = commonLeadModel.getDatumByName(R207_206B).getValue().doubleValue();
        com_208Pb206Pb = commonLeadModel.getDatumByName(R208_206B).getValue().doubleValue();
        com_206Pb208Pb = 1.0 / commonLeadModel.getDatumByName(R208_206B).getValue().doubleValue();
    }

    /**
     * @param targetAge the value of targetAge
     */
    public void updateCommonLeadRatiosFromSK(double targetAge) {
        // Output is a 3-element vector of model [206Pb/204Pb, 207Pb/204Pb,
        // 208Pb/204Pb] corresponding to TargetAge, as per Stacey & Kramers (1975).
        double[] staceyKramerSingleStagePbR
                = StaceyKramerCommonLeadModel.staceyKramerSingleStagePbR(targetAge);

        com_206Pb204Pb = staceyKramerSingleStagePbR[0];
        com_207Pb204Pb = staceyKramerSingleStagePbR[1];
        com_208Pb204Pb = staceyKramerSingleStagePbR[2];

        com_207Pb206Pb = com_207Pb204Pb / com_206Pb204Pb;
        com_208Pb206Pb = com_208Pb204Pb / com_206Pb204Pb;
        com_206Pb208Pb = 1.0 / com_208Pb206Pb;
    }

    public void updateCommonLeadRatiosFromAgeSK() {
        updateCommonLeadRatiosFromSK(sampleAgeSK);
    }

}