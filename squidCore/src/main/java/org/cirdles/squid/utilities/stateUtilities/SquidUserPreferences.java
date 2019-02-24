/*
 * SquidUserPreferences.java
 *
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.utilities.stateUtilities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
public class SquidUserPreferences implements Serializable {

    // NOTE: java.utils.Properties should be considered for use here JFB
    // Jan 2019 - considered and rejected as too simplistic
    // Class variables
    private static final long serialVersionUID = -936841271782482788L;

    // instance variables
    private String geochronUserName;
    private String geochronPassWord;

    private TaskTypeEnum taskType;
    private String authorName;
    private String labName;
    private boolean useSBM;
    private boolean userLinFits;
    private Squid3Constants.IndexIsoptopesEnum selectedIndexIsotope;

    private boolean squidAllowsAutoExclusionOfSpots;

    private double extPErr;

    private String delimiterForUnknownNames;
    private String parentNuclide;
    private boolean directAltPD;

    private Map<String, String> specialSquidFourExpressionsMap;
    private List<String> nominalMasses;
    private final List<String> requiredNominalMasses;
    private List<String> ratioNames;
    private final List<String> requiredRatioNames;
    private Map<String, ShrimpSpeciesNode> shrimpSpeciesNodeMap;

    private Map<String, ExpressionTreeInterface> namedExpressionsMap;

    /**
     * Creates a new instance of ReduxPreferences
     */
    public SquidUserPreferences() {

        this.geochronUserName = "username";
        this.geochronPassWord = "longpassword";

        this.taskType = TaskTypeEnum.GEOCHRON;
        this.authorName = "";
        this.labName = "";

        this.useSBM = true;
        this.userLinFits = false;

        this.selectedIndexIsotope = IndexIsoptopesEnum.PB_204;

        this.squidAllowsAutoExclusionOfSpots = true;

        this.extPErr = 0.75;

        this.delimiterForUnknownNames = Squid3Constants.SampleNameDelimetersEnum.HYPHEN.getName().trim();

        this.parentNuclide = "238";
        this.directAltPD = false;

        this.specialSquidFourExpressionsMap = new TreeMap<>();
        this.specialSquidFourExpressionsMap.put(UNCOR206PB238U_CALIB_CONST, UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(UNCOR208PB232TH_CALIB_CONST, UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(TH_U_EXP_DEFAULT, TH_U_EXP_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(PARENT_ELEMENT_CONC_CONST, PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION);

        // Default to 11 - mass
        this.nominalMasses = Arrays.asList(new String[]{"BKG", "190", "195.8", "195.9", "238", "248", "254"});
        this.requiredNominalMasses = Arrays.asList(new String[]{"204", "206", "207", "208"});
        this.ratioNames = Arrays.asList(new String[]{"190/195.8", "195.9/195.8", "238/195.8", "248/195.8"});
        this.requiredRatioNames = Arrays.asList(new String[]{
            "204/206", "207/206", "208/206", "206/238", "254/238", "208/248", "206/254", "248/254"});

        buildShrimpSpeciesNodeMap();
        buildNamedExpressionsMap();

    }

    private void buildShrimpSpeciesNodeMap() {
        shrimpSpeciesNodeMap = new TreeMap<>();
        for (int i = 0; i < requiredNominalMasses.size(); i++) {
            shrimpSpeciesNodeMap.put(
                    requiredNominalMasses.get(i),
                    ShrimpSpeciesNode.buildShrimpSpeciesNode(new SquidSpeciesModel(requiredNominalMasses.get(i)), "getPkInterpScanArray"));
        }
        for (int i = 0; i < nominalMasses.size(); i++) {
            shrimpSpeciesNodeMap.put(
                    nominalMasses.get(i),
                    ShrimpSpeciesNode.buildShrimpSpeciesNode(new SquidSpeciesModel(nominalMasses.get(i)), "getPkInterpScanArray"));
        }
    }

    public void buildNamedExpressionsMap() {
        namedExpressionsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < requiredRatioNames.size(); i++) {
            String[] numDem = requiredRatioNames.get(i).split("/");
            ExpressionTreeInterface ratio = new ExpressionTree(
                    requiredRatioNames.get(i),
                    shrimpSpeciesNodeMap.get(numDem[0]),
                    shrimpSpeciesNodeMap.get(numDem[1]),
                    Operation.divide());
            ratio.setSquidSwitchSAUnknownCalculation(true);
            ratio.setSquidSwitchSTReferenceMaterialCalculation(true);
            namedExpressionsMap.put(requiredRatioNames.get(i), ratio);
        }
        for (int i = 0; i < ratioNames.size(); i++) {
            String[] numDem = ratioNames.get(i).split("/");
            ExpressionTreeInterface ratio = new ExpressionTree(
                    ratioNames.get(i),
                    shrimpSpeciesNodeMap.get(numDem[0]),
                    shrimpSpeciesNodeMap.get(numDem[1]),
                    Operation.divide());
            ratio.setSquidSwitchSAUnknownCalculation(true);
            ratio.setSquidSwitchSTReferenceMaterialCalculation(true);
            namedExpressionsMap.put(ratioNames.get(i), ratio);
        }
    }

    /**
     * @return the geochronUserName
     */
    public String getGeochronUserName() {
        if (geochronUserName == null) {
            geochronUserName = "username";
        }
        return geochronUserName;
    }

    /**
     * @param geochronUserName the geochronUserName to set
     */
    public void setGeochronUserName(String geochronUserName) {
        this.geochronUserName = geochronUserName;
    }

    /**
     * @return the geochronPassWord
     */
    public String getGeochronPassWord() {
        if (geochronPassWord == null) {
            geochronPassWord = "longpassword";
        }
        return geochronPassWord;
    }

    /**
     * @param geochronPassWord the geochronPassWord to set
     */
    public void setGeochronPassWord(String geochronPassWord) {
        this.geochronPassWord = geochronPassWord;
    }

    /**
     *
     * @return
     */
    public TaskTypeEnum getTaskType() {
        if (taskType == null) {
            taskType = TaskTypeEnum.GEOCHRON;
        }
        return taskType;
    }

    /**
     *
     * @param taskType
     */
    public void setTaskType(TaskTypeEnum taskType) {
        this.taskType = taskType;
    }

    /**
     * @return the authorName
     */
    public String getAuthorName() {
        if (authorName == null) {
            authorName = "";
        }
        return authorName;
    }

    /**
     * @param authorName the authorName to set
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * @return the labName
     */
    public String getLabName() {
        if (labName == null) {
            labName = "";
        }
        return labName;
    }

    /**
     * @param labName the labName to set
     */
    public void setLabName(String labName) {
        this.labName = labName;
    }

    /**
     * @return the useSBM
     */
    public boolean isUseSBM() {
        return useSBM;
    }

    /**
     * @param useSBM the useSBM to set
     */
    public void setUseSBM(boolean useSBM) {
        this.useSBM = useSBM;
    }

    /**
     * @return the userLinFits
     */
    public boolean isUserLinFits() {
        return userLinFits;
    }

    /**
     * @param userLinFits the userLinFits to set
     */
    public void setUserLinFits(boolean userLinFits) {
        this.userLinFits = userLinFits;
    }

    /**
     * @return the selectedIndexIsotope
     */
    public Squid3Constants.IndexIsoptopesEnum getSelectedIndexIsotope() {
        if (selectedIndexIsotope == null) {
            selectedIndexIsotope = IndexIsoptopesEnum.PB_204;
        }
        return selectedIndexIsotope;
    }

    /**
     * @param selectedIndexIsotope the selectedIndexIsotope to set
     */
    public void setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum selectedIndexIsotope) {
        this.selectedIndexIsotope = selectedIndexIsotope;
    }

    /**
     * @return the squidAllowsAutoExclusionOfSpots
     */
    public boolean isSquidAllowsAutoExclusionOfSpots() {
        return squidAllowsAutoExclusionOfSpots;
    }

    /**
     * @param squidAllowsAutoExclusionOfSpots the
     * squidAllowsAutoExclusionOfSpots to set
     */
    public void setSquidAllowsAutoExclusionOfSpots(boolean squidAllowsAutoExclusionOfSpots) {
        this.squidAllowsAutoExclusionOfSpots = squidAllowsAutoExclusionOfSpots;
    }

    /**
     * @return the extPErr
     */
    public double getExtPErr() {
        return extPErr;
    }

    /**
     * @param extPErr the extPErr to set
     */
    public void setExtPErr(double extPErr) {
        this.extPErr = extPErr;
    }

    /**
     * @return the delimiterForUnknownNames
     */
    public String getDelimiterForUnknownNames() {
        return delimiterForUnknownNames;
    }

    /**
     * @param delimiterForUnknownNames the delimiterForUnknownNames to set
     */
    public void setDelimiterForUnknownNames(String delimiterForUnknownNames) {
        this.delimiterForUnknownNames = delimiterForUnknownNames;
    }

    /**
     * @return the parentNuclide
     */
    public String getParentNuclide() {
        return parentNuclide;
    }

    /**
     * @param parentNuclide the parentNuclide to set
     */
    public void setParentNuclide(String parentNuclide) {
        this.parentNuclide = parentNuclide;
    }

    /**
     * @return the directAltPD
     */
    public boolean isDirectAltPD() {
        return directAltPD;
    }

    /**
     * @param directAltPD the directAltPD to set
     */
    public void setDirectAltPD(boolean directAltPD) {
        this.directAltPD = directAltPD;
    }

    /**
     * @return the specialSquidFourExpressionsMap
     */
    public Map<String, String> getSpecialSquidFourExpressionsMap() {
        return specialSquidFourExpressionsMap;
    }

    /**
     * @param specialSquidFourExpressionsMap the specialSquidFourExpressionsMap
     * to set
     */
    public void setSpecialSquidFourExpressionsMap(Map<String, String> specialSquidFourExpressionsMap) {
        this.specialSquidFourExpressionsMap = specialSquidFourExpressionsMap;
    }

    /**
     * @return the nominalMasses
     */
    public List<String> getNominalMasses() {
        return nominalMasses;
    }

    /**
     * @param nominalMasses the nominalMasses to set
     */
    public void setNominalMasses(List<String> nominalMasses) {
        this.nominalMasses = nominalMasses;
    }

    /**
     * @return the ratioNames
     */
    public List<String> getRatioNames() {
        return ratioNames;
    }

    /**
     * @param ratioNames the ratioNames to set
     */
    public void setRatioNames(List<String> ratioNames) {
        this.ratioNames = ratioNames;
    }

    /**
     * @return the requiredNominalMasses
     */
    public List<String> getRequiredNominalMasses() {
        return requiredNominalMasses;
    }

    /**
     * @return the requiredRatioNames
     */
    public List<String> getRequiredRatioNames() {
        return requiredRatioNames;
    }

    /**
     * @return the namedExpressionsMap
     */
    public Map<String, ExpressionTreeInterface> getNamedExpressionsMap() {
        return namedExpressionsMap;
    }

}
