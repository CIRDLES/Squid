/*
 * TaskDesign.java
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
package org.cirdles.squid.tasks.taskDesign;

import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.utilities.IntuitiveStringComparator;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;

import java.io.Serializable;
import java.util.*;

import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_STACEY_KRAMER;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;

/**
 * @author James F. Bowring
 */
public class TaskDesign implements Serializable {

    // Class variables
    protected static final long serialVersionUID = -936841271782482788L;

    // instance variables
    protected String name;
    protected String description;
    protected String provenance;

    protected TaskTypeEnum taskType;
    protected String authorName;
    protected String labName;
    protected boolean useSBM;
    protected boolean userLinFits;
    protected Squid3Constants.IndexIsoptopesEnum selectedIndexIsotope;

    protected boolean squidAllowsAutoExclusionOfSpots;

    protected double extPErrU;
    protected double extPErrTh;

    protected String delimiterForUnknownNames;
    protected String parentNuclide;
    protected boolean directAltPD;
    protected Map<String, String> specialSquidFourExpressionsMap;
    protected List<String> nominalMasses;
    protected List<String> ratioNames;
    protected int indexOfBackgroundSpecies;
    protected Map<String, ShrimpSpeciesNode> shrimpSpeciesNodeMap;
    protected ParametersModel physicalConstantsModel;
    protected ParametersModel commonPbModel;
    // part of savable defaults
    protected String analystName;
    // issue #714
    // methods: 1 = commonLeadModel, 0 = StaceyKramer, 2 = StaceyKramer per group (asterisk - uses sampleSKAge)
    protected int commonLeadForUnknownsMethodSelected;
    private List<Expression> customTaskExpressions;

    /**
     * Creates a new instance of TaskDesign
     */
    public TaskDesign() throws SquidException {
        this.name = "";
        this.description = "";
        this.provenance = "";

        this.taskType = TaskTypeEnum.GEOCHRON;
        this.authorName = "";
        this.labName = "";

        this.useSBM = true;
        this.userLinFits = false;

        this.selectedIndexIsotope = IndexIsoptopesEnum.PB_204;

        this.squidAllowsAutoExclusionOfSpots = true;

        this.extPErrU = 0.75;
        this.extPErrTh = 0.75;

        this.delimiterForUnknownNames = Squid3Constants.SampleNameDelimitersEnum.HYPHEN.getName().trim();

        this.parentNuclide = "238";
        this.directAltPD = false;

        this.specialSquidFourExpressionsMap = new TreeMap<>();
        this.specialSquidFourExpressionsMap.put(UNCOR206PB238U_CALIB_CONST, UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(UNCOR208PB232TH_CALIB_CONST, UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(TH_U_EXP_DEFAULT, TH_U_EXP_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(PARENT_ELEMENT_CONC_CONST, PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION);

        this.physicalConstantsModel = SquidLabData.getExistingSquidLabData().getPhysConstDefault();
        this.commonPbModel = SquidLabData.getExistingSquidLabData().getCommonPbDefault();

        // Default to blank
        this.nominalMasses = new ArrayList<>(Collections.singletonList(DEFAULT_BACKGROUND_MASS_LABEL));

        this.ratioNames = new ArrayList<>(Collections.emptyList());

        indexOfBackgroundSpecies = 5;

        this.analystName = "";

        this.customTaskExpressions = new ArrayList<>();

        this.commonLeadForUnknownsMethodSelected = METHOD_STACEY_KRAMER;

        buildShrimpSpeciesNodeMap();
    }

    public boolean isPbU() {
        return (parentNuclide.contains("238"));
    }

    private void buildShrimpSpeciesNodeMap() {
        shrimpSpeciesNodeMap = new TreeMap<>();
        for (int i = 0; i < REQUIRED_NOMINAL_MASSES.size(); i++) {
            shrimpSpeciesNodeMap.put(
                    REQUIRED_NOMINAL_MASSES.get(i),
                    ShrimpSpeciesNode.buildShrimpSpeciesNode(new SquidSpeciesModel(REQUIRED_NOMINAL_MASSES.get(i)), "getPkInterpScanArray"));
        }
        for (int i = 0; i < nominalMasses.size(); i++) {
            shrimpSpeciesNodeMap.put(
                    nominalMasses.get(i),
                    ShrimpSpeciesNode.buildShrimpSpeciesNode(new SquidSpeciesModel(nominalMasses.get(i)), "getPkInterpScanArray"));
        }
    }

    public Map<String, ExpressionTreeInterface> buildNamedExpressionsMap() {
        Map<String, ExpressionTreeInterface> namedExpressionsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < REQUIRED_RATIO_NAMES.size(); i++) {
            String[] numDem = REQUIRED_RATIO_NAMES.get(i).split("/");
            ExpressionTreeInterface ratio = new ExpressionTree(
                    REQUIRED_RATIO_NAMES.get(i),
                    shrimpSpeciesNodeMap.get(numDem[0]),
                    shrimpSpeciesNodeMap.get(numDem[1]),
                    Operation.divide());
            ratio.setSquidSwitchSAUnknownCalculation(true);
            ratio.setSquidSwitchSTReferenceMaterialCalculation(true);
            namedExpressionsMap.put(REQUIRED_RATIO_NAMES.get(i), ratio);
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
        for (Expression exp : customTaskExpressions) {
            namedExpressionsMap.put(exp.getName(), exp.getExpressionTree());
        }
        return namedExpressionsMap;
    }

    /**
     * @return
     */
    public TaskTypeEnum getTaskType() {
        if (taskType == null) {
            taskType = TaskTypeEnum.GEOCHRON;
        }
        return taskType;
    }

    /**
     * @param taskType
     */
    public void setTaskType(TaskTypeEnum taskType) {
        this.taskType = taskType;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the provenance
     */
    public String getProvenance() {
        return provenance;
    }

    /**
     * @param provenance the provenance to set
     */
    public void setProvenance(String provenance) {
        this.provenance = provenance;
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
     *                                        squidAllowsAutoExclusionOfSpots to set
     */
    public void setSquidAllowsAutoExclusionOfSpots(boolean squidAllowsAutoExclusionOfSpots) {
        this.squidAllowsAutoExclusionOfSpots = squidAllowsAutoExclusionOfSpots;
    }

    /**
     * @return the extPErrU
     */
    public double getExtPErrU() {
        return extPErrU;
    }

    /**
     * @param extPErrU the extPErrU to set
     */
    public void setExtPErrU(double extPErrU) {
        this.extPErrU = extPErrU;
    }

    /**
     * @return the extPErrTh
     */
    public double getExtPErrTh() {
        return extPErrTh;
    }

    /**
     * @param extPErrTh the extPErrTh to set
     */
    public void setExtPErrTh(double extPErrTh) {
        this.extPErrTh = extPErrTh;
    }

    /**
     * @return the delimiterForUnknownNames
     */
    public String getDelimiterForUnknownNames() {
        return delimiterForUnknownNames;
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
     *                                       to set
     */
    public void setSpecialSquidFourExpressionsMap(Map<String, String> specialSquidFourExpressionsMap) {
        this.specialSquidFourExpressionsMap = specialSquidFourExpressionsMap;
    }

    /**
     * @return the nominalMasses
     */
    public List<String> getNominalMasses() {
        Collections.sort(nominalMasses, new IntuitiveStringComparator<>());

        List<String> retVal = new ArrayList<>();
        retVal.addAll(nominalMasses);
        return retVal;
    }

    /**
     * @param nominalMasses the nominalMasses to set
     */
    public void setNominalMasses(List<String> nominalMasses) {
        this.nominalMasses = nominalMasses;
    }

    public void removeNominalMass(String nominalMass) {
        this.nominalMasses.remove(nominalMass);
        // check if present in any ratios and zap them too
        List<String> removals = new ArrayList<>();
        for (String ratio : ratioNames) {
            String[] numDen = ratio.split("/");
            if ((numDen[0].compareTo(nominalMass) == 0) || (numDen[1].compareTo(nominalMass) == 0)) {
                removals.add(ratio);
            }
        }
        ratioNames.removeAll(removals);
    }

    public void addNominalMass(String nominalMass) {
        if (!nominalMasses.contains(nominalMass)) {
            this.nominalMasses.add(nominalMass);
        }
    }

    /**
     * @return the ratioNames
     */
    public List<String> getRatioNames() {
        Collections.sort(ratioNames, new IntuitiveStringComparator<>());
        List<String> retVal = new ArrayList<>();
        retVal.addAll(ratioNames);
        return retVal;
    }

    /**
     * @param ratioNames the ratioNames to set
     */
    public void setRatioNames(List<String> ratioNames) {
        this.ratioNames = ratioNames;
    }

    public void removeRatioName(String ratioName) {
        this.ratioNames.remove(ratioName);
    }

    public void addRatioName(String ratioName) {
        if (!ratioNames.contains(ratioName)) {
            this.ratioNames.add(ratioName);
            Collections.sort(ratioNames, new IntuitiveStringComparator<>());
        }
    }

    /**
     * @return the indexOfBackgroundSpecies
     */
    public int getIndexOfBackgroundSpecies() {
        return indexOfBackgroundSpecies;
    }

    /**
     * @param indexOfBackgroundSpecies the indexOfBackgroundSpecies to set
     */
    public void setIndexOfBackgroundSpecies(int indexOfBackgroundSpecies) {
        this.indexOfBackgroundSpecies = indexOfBackgroundSpecies;
    }

    /**
     * @return the physicalConstantsModel
     */
    public ParametersModel getPhysicalConstantsModel() {
        return physicalConstantsModel;
    }

    /**
     * @param physicalConstantsModel the physicalConstantsModel to set
     */
    public void setPhysicalConstantsModel(ParametersModel physicalConstantsModel) {
        this.physicalConstantsModel = physicalConstantsModel;
    }

    /**
     * @return the commonPbModel
     */
    public ParametersModel getCommonPbModel() {
        return commonPbModel;
    }

    /**
     * @param commonPbModel the commonPbModel to set
     */
    public void setCommonPbModel(ParametersModel commonPbModel) {
        this.commonPbModel = commonPbModel;
    }

    /**
     * @return the analystName
     */
    public String getAnalystName() {
        if (analystName == null) {
            analystName = "";
        }
        return analystName;
    }

    /**
     * @param analystName the analystName to set
     */
    public void setAnalystName(String analystName) {
        this.analystName = analystName;
    }

    /**
     * @return the customTaskExpressions
     */
    public List<Expression> getCustomTaskExpressions() {
        if (customTaskExpressions == null) {
            customTaskExpressions = new ArrayList<>();
        }
        return customTaskExpressions;
    }

    /**
     * @param customTaskExpressions the customTaskExpressions to set
     */
    public void setCustomTaskExpressions(List<Expression> customTaskExpressions) {
        this.customTaskExpressions = customTaskExpressions;
    }

    public int getCommonLeadForUnknownsMethodSelected() {
        return commonLeadForUnknownsMethodSelected;
    }

    public void setCommonLeadForUnknownsMethodSelected(int commonLeadForUnknownsMethodSelected) {
        this.commonLeadForUnknownsMethodSelected = commonLeadForUnknownsMethodSelected;
    }
}