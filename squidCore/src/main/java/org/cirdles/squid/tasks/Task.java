
/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks;

import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFileRunFractionParser;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpDataFileInterface;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.evaluationEngines.ExpressionEvaluator;
import org.cirdles.squid.tasks.evaluationEngines.TaskExpressionEvaluatedPerSpotPerScanModelInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNTS_PERSEC_4_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNT_4_6_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_CALIB_CONST_AGE_206_238_BASENAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_CALIB_CONST_AGE_208_232_BASENAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_MEAN_PPM_PARENT_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PPM_PARENT_EQN_NAME_TH;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TH_U_EQN_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TH_U_EQN_NAME_S;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_206_238_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_206_238_NAME_S;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_208_232_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_208_232_NAME_S;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generateExperimentalExpressions;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generateOverCountExpressions;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generatePerSpotProportionsOfCommonPb;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generatePlaceholderExpressions;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generatePpmUandPpmTh;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generateReferenceMaterialValues;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.overCountMeans;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.samRadiogenicCols;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.stdRadiogenicCols;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.updateCommonLeadParameterValuesFromModel;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.updatePhysicalConstantsParameterValuesFromModel;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import static org.cirdles.squid.tasks.expressions.constants.ConstantNode.MISSING_EXPRESSION_STRING;
import org.cirdles.squid.tasks.expressions.constants.ConstantNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;

import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWithRatiosInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeXMLConverter;
import org.cirdles.squid.tasks.expressions.functions.FunctionXMLConverter;
import org.cirdles.squid.tasks.expressions.functions.WtdMeanACalc;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.expressions.operations.OperationXMLConverter;
import static org.cirdles.squid.tasks.expressions.spots.SpotFieldNode.buildSpotNode;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummaryXMLConverter;
import org.cirdles.squid.utilities.IntuitiveStringComparator;
import static org.cirdles.squid.utilities.conversionUtilities.CloningUtilities.clone2dArray;
import org.cirdles.squid.utilities.fileUtilities.PrawnFileUtilities;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class Task implements TaskInterface, Serializable, XMLSerializerInterface {

    private static final long serialVersionUID = 6522574920235718028L;

    private static final PrawnFileRunFractionParser PRAWN_FILE_RUN_FRACTION_PARSER
            = new PrawnFileRunFractionParser();

    /**
     *
     */
    protected String name;
    protected TaskTypeEnum type;
    protected String description;
    protected String authorName;
    protected String labName;
    protected String provenance;
    protected long dateRevised;
    protected boolean useSBM;
    protected boolean userLinFits;
    // comes from prawn file mass station
    protected int indexOfBackgroundSpecies;
    // comes from task
    protected int indexOfTaskBackgroundMass;
    protected String parentNuclide;
    protected boolean directAltPD;
    protected String filterForRefMatSpotNames;
    protected String filterForConcRefMatSpotNames;
    protected Map<String, Integer> filtersForUnknownNames;

    protected List<String> nominalMasses;
    protected List<String> ratioNames;
    protected Map<Integer, MassStationDetail> mapOfIndexToMassStationDetails;
    protected SquidSessionModel squidSessionModel;
    protected List<SquidSpeciesModel> squidSpeciesModelList;
    protected List<SquidRatiosModel> squidRatiosModelList;
    protected boolean[][] tableOfSelectedRatiosByMassStationIndex;

    /**
     *
     */
    protected List<Expression> taskExpressionsOrdered;
    protected SortedSet<Expression> taskExpressionsRemoved;
    protected Map<String, ExpressionTreeInterface> namedExpressionsMap;
    protected Map<String, ExpressionTreeInterface> namedOvercountExpressionsMap;
    protected Map<String, ExpressionTreeInterface> namedConstantsMap;
    protected Map<String, ExpressionTreeInterface> namedParametersMap;

    protected List<ShrimpFractionExpressionInterface> shrimpFractions;
    protected List<ShrimpFractionExpressionInterface> referenceMaterialSpots;
    protected List<ShrimpFractionExpressionInterface> concentrationReferenceMaterialSpots;
    protected List<ShrimpFractionExpressionInterface> unknownSpots;
    protected Map<String, List<ShrimpFractionExpressionInterface>> mapOfUnknownsBySampleNames;

    protected boolean prawnChanged;

    /**
     *
     */
    protected Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet;

    protected ShrimpDataFileInterface prawnFile;
    protected CalamariReportsEngine reportsEngine;

    protected boolean changed;

    protected boolean useCalculated_pdMeanParentEleA;

    protected IndexIsoptopesEnum selectedIndexIsotope;

    // next 3 fields used to track user's choice of displayed deltas
    protected List<MassStationDetail> massMinuends;
    protected List<MassStationDetail> massSubtrahends;
    protected boolean showTimeNormalized;
    protected boolean showPrimaryBeam;
    protected boolean showQt1y;
    protected boolean showQt1z;

    protected boolean squidAllowsAutoExclusionOfSpots;

    protected double extPErr;

    protected ParametersModel physicalConstantsModel;
    protected ParametersModel referenceMaterialModel;
    protected ParametersModel commonPbModel;
    protected ParametersModel concentrationReferenceMaterialModel;

    protected boolean physicalConstantsModelChanged;
    protected boolean referenceMaterialModelChanged;
    protected boolean commonPbModelChanged;
    protected boolean concentrationReferenceMaterialModelChanged;

    public Task() {
        this("New Task", null, null);
    }

    public Task(String name) {
        this(name, null, null);
    }

    public Task(String name, CalamariReportsEngine reportsEngine) {
        this(name, null, reportsEngine);
    }

    /**
     *
     * @param name
     * @param prawnFile
     * @param reportsEngine
     */
    public Task(String name, ShrimpDataFileInterface prawnFile, CalamariReportsEngine reportsEngine) {
        this.name = name;
        this.type = SquidPersistentState.getExistingPersistentState().getSquidUserPreferences().getTaskType();
        this.description = "";
        this.authorName = "";
        this.labName = "";
        this.provenance = "";
        this.dateRevised = 0l;
        this.filterForRefMatSpotNames = "";
        this.filterForConcRefMatSpotNames = "";
        this.filtersForUnknownNames = new HashMap<>();

        this.useSBM = true;
        this.userLinFits = false;
        this.indexOfBackgroundSpecies = -1;
        this.indexOfTaskBackgroundMass = -1;
        this.parentNuclide = "";
        this.directAltPD = false;

        this.nominalMasses = new ArrayList<>();
        this.ratioNames = new ArrayList<>();
        this.squidSessionModel = null;
        this.squidSpeciesModelList = new ArrayList<>();
        this.squidRatiosModelList = new ArrayList<>();
        this.tableOfSelectedRatiosByMassStationIndex = new boolean[0][];

        this.taskExpressionsOrdered = new ArrayList<>();
        this.taskExpressionsRemoved = new TreeSet<>();
        this.namedExpressionsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.namedOvercountExpressionsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.namedConstantsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.namedParametersMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.taskExpressionsEvaluationsPerSpotSet = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        this.shrimpFractions = new ArrayList<>();
        this.referenceMaterialSpots = new ArrayList<>();
        this.concentrationReferenceMaterialSpots = new ArrayList<>();
        this.unknownSpots = new ArrayList<>();

        this.prawnFile = prawnFile;
        this.reportsEngine = reportsEngine;

        this.changed = true;
        SquidProject.setProjectChanged(true);

        this.useCalculated_pdMeanParentEleA = false;
        this.selectedIndexIsotope = IndexIsoptopesEnum.PB_204;

        this.massMinuends = new ArrayList<>();
        this.massSubtrahends = new ArrayList<>();
        this.showTimeNormalized = true; // in honor of Nicole Rayner's normal work flow

        this.showPrimaryBeam = false;
        this.showQt1y = false;
        this.showQt1z = false;

        this.prawnChanged = false;

        this.squidAllowsAutoExclusionOfSpots = true;

        this.extPErr = 0.75;

        this.physicalConstantsModel = SquidLabData.getExistingSquidLabData().getPhysConstDefault();
        this.referenceMaterialModel = SquidLabData.getExistingSquidLabData().getRefMatDefault();
        this.concentrationReferenceMaterialModel = SquidLabData.getExistingSquidLabData().getRefMatDefault();
        this.commonPbModel = SquidLabData.getExistingSquidLabData().getCommonPbDefault();

        this.physicalConstantsModelChanged = false;
        this.referenceMaterialModelChanged = false;
        this.commonPbModelChanged = false;
        this.concentrationReferenceMaterialModelChanged = false;

        generateConstants();
        generateParameters();
    }

    private void generateConstants() {
        Map<String, ExpressionTreeInterface> constants = BuiltInExpressionsFactory.generateConstants();
        namedConstantsMap.putAll(constants);
    }

    private void generateParameters() {
        Map<String, ExpressionTreeInterface> parameters = BuiltInExpressionsFactory.generateParameters();
        namedParametersMap.putAll(parameters);
    }

    @Override
    public void generateBuiltInExpressions() {

        SortedSet<Expression> generateReferenceMaterialValues = generateReferenceMaterialValues();
        taskExpressionsOrdered.addAll(generateReferenceMaterialValues);

        SortedSet<Expression> generateParameterValues = updateCommonLeadParameterValuesFromModel(commonPbModel);
        taskExpressionsOrdered.addAll(generateParameterValues);

        SortedSet<Expression> generatePhysicalConstantsValues = updatePhysicalConstantsParameterValuesFromModel(physicalConstantsModel);
        taskExpressionsOrdered.addAll(generatePhysicalConstantsValues);

        SortedSet<Expression> generatePlaceholderExpressions = generatePlaceholderExpressions(parentNuclide, isDirectAltPD());
        taskExpressionsOrdered.addAll(generatePlaceholderExpressions);

        SortedSet<Expression> overCountExpressionsOrdered = generateOverCountExpressions(isDirectAltPD());
        taskExpressionsOrdered.addAll(overCountExpressionsOrdered);

        SortedSet<Expression> experimentalExpressions = generateExperimentalExpressions();
        taskExpressionsOrdered.addAll(experimentalExpressions);

        // Squid2.5 Framework: Part 4 up to means
        SortedSet<Expression> perSpotProportionsOfCommonPb = generatePerSpotProportionsOfCommonPb();
        taskExpressionsOrdered.addAll(perSpotProportionsOfCommonPb);

        //Squid2.5 Framework: Part 4 means
        SortedSet<Expression> perSpotConcentrations = generatePpmUandPpmTh(parentNuclide, isDirectAltPD());
        taskExpressionsOrdered.addAll(perSpotConcentrations);

        SortedSet<Expression> overCountMeansRefMaterials = overCountMeans();
        taskExpressionsOrdered.addAll(overCountMeansRefMaterials);

        SortedSet<Expression> stdRadiogenicCols = stdRadiogenicCols(parentNuclide, isDirectAltPD());
        taskExpressionsOrdered.addAll(stdRadiogenicCols);

        SortedSet<Expression> samRadiogenicCols = samRadiogenicCols(parentNuclide, isDirectAltPD());
        taskExpressionsOrdered.addAll(samRadiogenicCols);

        Collections.sort(taskExpressionsOrdered);
    }

    private void generateMapOfUnknownsBySampleNames() {
        Comparator<String> intuitiveStringComparator = new IntuitiveStringComparator<>();
        mapOfUnknownsBySampleNames = new TreeMap<>(intuitiveStringComparator);
        // walk chosen sample names (excluding reference materials) and get list of spots belonging to each
        for (String sampleName : filtersForUnknownNames.keySet()) {
            List<ShrimpFractionExpressionInterface> filteredList
                    = unknownSpots.stream()
                            .filter(spot -> spot.getFractionID().startsWith(sampleName))
                            .collect(Collectors.toList());
            if (filteredList.size() > 0) {
                mapOfUnknownsBySampleNames.put(sampleName, filteredList);
            }
        }
    }

    @Override
    public String printTaskSummary() {
        StringBuilder summary = new StringBuilder();

        summary.append("TASK SUMMARY REPORT\n")
                .append("Task Name: ")
                .append(name)
                .append("\n")
                .append("Task Description: ")
                .append(description)
                .append("\n")
                .append("Task Provenance: ")
                .append(provenance)
                .append("\n")
                .append("\tNormalise Ion Counts for SBM?: ")
                .append(useSBM)
                .append("\n")
                .append("\tRatio Calculation Method: ")
                .append(userLinFits ? "Linear" : "Spot Average")
                .append("\n")
                .append("\tPreferred index isotope: ")
                .append(selectedIndexIsotope.getIsotope())
                .append("\n")
                .append("\tAuto-reject spots in RefMat weighted means: ")
                .append(squidAllowsAutoExclusionOfSpots)
                .append("\n")
                .append("\tReference Material Model: ")
                .append("not available")
                .append("\n")
                .append("\tConcentration Reference Material Model: ")
                .append("not available")
                .append("\n")
                .append("\tCommon Pb model: ")
                .append(commonPbModel.getModelNameWithVersion())
                .append("\n")
                .append("\tPhysical Constants model: ")
                .append(physicalConstantsModel.getModelNameWithVersion())
                .append("\n");

        summary.append("\n");

        summary.append(printTaskAudit());

        return summary.toString();
    }

    @Override
    public String printTaskAudit() {
        // backward compatible 
        // TODO: Remove after 2/1/2018
        if (concentrationReferenceMaterialSpots == null) {
            concentrationReferenceMaterialSpots = new ArrayList<>();
        }
        if (filterForConcRefMatSpotNames == null) {
            filterForConcRefMatSpotNames = "";
        }

        StringBuilder summary = new StringBuilder();

        summary.append(" ")
                .append("Prawn Source File provides ")
                .append(String.valueOf(squidSpeciesModelList.size()))
                .append(" Species:");

        summary.append("\n      ");
        for (int i = 0; i < squidSpeciesModelList.size(); i++) {
            String comma = (i == (squidSpeciesModelList.size() - 1) ? "" : ", ");
            summary.append(String.format("%1$-" + 7 + "s", squidSpeciesModelList.get(i).getPrawnFileIsotopeName() + comma));
        }

        summary.append("\n\n ")
                .append("Task File specifies ")
                .append(String.valueOf(nominalMasses.size()))
                .append(" Masses matching Species found in Prawn file:");

        summary.append("\n      ");
        for (int i = 0; i < nominalMasses.size(); i++) {
            String comma = (i == (nominalMasses.size() - 1) ? "" : ", ");
            summary.append(String.format("%1$-" + 7 + "s", nominalMasses.get(i) + comma));
        }

        Collections.sort(ratioNames);
        int countOfRatios = ratioNames.size();
        summary.append("\n\n Task Ratios: ");
        summary.append((String) (countOfRatios > 0 ? String.valueOf(countOfRatios) : "None")).append(" specified using available masses.");
        summary.append("\n  ");
        for (int i = 0; i < countOfRatios; i++) {
            summary.append(String.format("%1$-" + 6 + "s", ratioNames.get(i).split("/")[0]));
        }
        summary.append("\n  ");
        for (int i = 0; i < countOfRatios; i++) {
            summary.append(String.format("%1$-" + 6 + "s", "----"));
        }
        summary.append("\n  ");
        for (int i = 0; i < countOfRatios; i++) {
            summary.append(String.format("%1$-" + 6 + "s", ratioNames.get(i).split("/")[1]));
        }

        summary.append("\n\n ")
                .append(String.valueOf(referenceMaterialSpots.size()))
                .append(" Reference Material Spots extracted by filter: \"")
                .append(filterForRefMatSpotNames)
                .append("\".");

        String meanConcValue = "Not Calculated";
        if (taskExpressionsEvaluationsPerSpotSet.get(SQUID_MEAN_PPM_PARENT_NAME) != null) {
            meanConcValue = String.valueOf(taskExpressionsEvaluationsPerSpotSet.get(SQUID_MEAN_PPM_PARENT_NAME).getValues()[0][0]);
        }
        summary.append("\n ")
                .append(String.valueOf(concentrationReferenceMaterialSpots.size()))
                .append(" Concentration Reference Material Spots extracted by filter: \"")
                .append(filterForConcRefMatSpotNames)
                .append("\".\n\t\t  Mean Concentration of Primary Parent Element ")
                .append(parentNuclide)
                .append(" = ")
                .append(meanConcValue);

        summary.append("\n ")
                .append(String.valueOf(unknownSpots.size()))
                .append(" Unknown Spots");

        if (mapOfUnknownsBySampleNames.isEmpty()) {
            summary.append(". Individual samples not yet identified - see PrawnFile Menu");
        } else {
            summary.append(", organized into these samples:");
        }

        for (String sampleName : mapOfUnknownsBySampleNames.keySet()) {
            summary.append("\n\t")
                    .append("\"")
                    .append(sampleName)
                    .append("\"")
                    .append("\t with ")
                    .append(mapOfUnknownsBySampleNames.get(sampleName).size())
                    .append(" spot" + (mapOfUnknownsBySampleNames.get(sampleName).size() > 1 ? "s" : ""));
        }

        int count = 0;
        for (Expression exp : taskExpressionsOrdered) {
            if (exp.amHealthy()) {
                count++;
            }
        }
        summary.append("\n\n Task Expressions: ");
        summary.append("\n\t Healthy: ");
        summary.append((String) (count > 0 ? String.valueOf(count) : "None")).append(" included.");
        summary.append("\n\t UnHealthy: ");
        summary.append((String) ((taskExpressionsOrdered.size() - count) > 0
                ? String.valueOf(taskExpressionsOrdered.size() - count) : "None")).append(" excluded.");

        summary.append("\n\n Task Constants (imported with task or hard-coded): \n");
        if (namedConstantsMap.size() > 0) {
            for (Map.Entry<String, ExpressionTreeInterface> entry : namedConstantsMap.entrySet()) {
                summary.append("\t").append(entry.getKey()).append(" = ").append(((ConstantNode) entry.getValue()).getValue()).append("\n");
            }
        } else {
            summary.append(" No constants supplied.");
        }

        summary.append("\n Task Parameters (currently hard-coded, except ExtPErr, which is set above.): \n");
        if (namedParametersMap.size() > 0) {
            for (Map.Entry<String, ExpressionTreeInterface> entry : namedParametersMap.entrySet()) {
                summary.append("\t").append(entry.getKey()).append(" = ").append((double) ((ConstantNode) entry.getValue()).getValue()).append("\n");
            }
        } else {
            summary.append(" No Parameters supplied.");
        }

        return summary.toString();
    }

    @Override
    public Expression generateExpressionFromRawExcelStyleText(
            String name,
            String originalExpressionText,
            boolean eqnSwitchNU,
            boolean referenceMaterialValue,
            boolean parameterValue) {
        Expression exp = new Expression(name, originalExpressionText);
        exp.setSquidSwitchNU(eqnSwitchNU);
        exp.setReferenceMaterialValue(referenceMaterialValue);
        exp.setParameterValue(parameterValue);

        exp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);

        return exp;
    }

    @Override
    public void setupSquidSessionSpecsAndReduceAndReport() {

        if (changed) {

            updateParametersFromModels();

            buildSquidSpeciesModelList();

            populateTableOfSelectedRatiosFromRatiosList();

            buildSquidRatiosModelListFromMassStationDetails();

            boolean requiresChanges = true;
            if (squidSessionModel != null) {
                requiresChanges = squidSessionModel.updateFields(
                        squidSpeciesModelList,
                        squidRatiosModelList,
                        useSBM,
                        userLinFits,
                        indexOfBackgroundSpecies,
                        filterForRefMatSpotNames,
                        filterForConcRefMatSpotNames,
                        filtersForUnknownNames);
            }

            if (requiresChanges || prawnChanged) {
                squidSessionModel
                        = new SquidSessionModel(
                                squidSpeciesModelList,
                                squidRatiosModelList,
                                useSBM,
                                userLinFits,
                                indexOfBackgroundSpecies,
                                filterForRefMatSpotNames,
                                filterForConcRefMatSpotNames,
                                filtersForUnknownNames);

                try {
                    shrimpFractions = processRunFractions(prawnFile, squidSessionModel);
                } catch (Exception e) {
                }
            }

            generateMapOfUnknownsBySampleNames();

            processAndSortExpressions();

            evaluateTaskExpressions();

            reportsEngine.clearReports();

            prawnChanged = false;
            changed = false;
        }
    }

    private void updateParametersFromModels() {
        boolean doUpdateAll
                = commonPbModelChanged || physicalConstantsModelChanged;// temp || referenceMaterialModelChanged || concentrationReferenceMaterialModelChanged;

        if (commonPbModelChanged) {
            SortedSet<Expression> updatedCommonPbExpressions
                    = BuiltInExpressionsFactory.updateCommonLeadParameterValuesFromModel(commonPbModel);
            Iterator<Expression> updatedCommonPbIterator = updatedCommonPbExpressions.iterator();
            while (updatedCommonPbIterator.hasNext()) {
                Expression exp = updatedCommonPbIterator.next();
                removeExpression(exp, false);
                addExpression(exp, false);
                updateAffectedExpressions(exp, false);
            }
            commonPbModelChanged = false;
        }

        if (physicalConstantsModelChanged) {
            SortedSet<Expression> updatedPhysicalConstantsExpressions
                    = BuiltInExpressionsFactory.updatePhysicalConstantsParameterValuesFromModel(physicalConstantsModel);
            Iterator<Expression> updatedPhysicalConstantsExpressionsIterator = updatedPhysicalConstantsExpressions.iterator();
            while (updatedPhysicalConstantsExpressionsIterator.hasNext()) {
                Expression exp = updatedPhysicalConstantsExpressionsIterator.next();
                removeExpression(exp, false);
                addExpression(exp, false);
                updateAffectedExpressions(exp, false);
            }
            physicalConstantsModelChanged = false;
        }

        if (doUpdateAll) {
            updateAllExpressions(false);
        }
    }

    /**
     * This method provides a skeleton of the ShrimpFractions to give additional
     * info for mass audit graphs.
     */
    public void setupSquidSessionSkeleton() {

        buildSquidSpeciesModelList();

        squidSessionModel = new SquidSessionModel(
                squidSpeciesModelList,
                squidRatiosModelList,
                true,
                false,
                indexOfBackgroundSpecies,
                filterForRefMatSpotNames,
                filterForConcRefMatSpotNames,
                filtersForUnknownNames);

        try {
            shrimpFractions = processRunFractions(prawnFile, squidSessionModel);
        } catch (Exception e) {
        }

    }

    @Override
    public void updateRatioNames(String[] ratioNames) {
        this.ratioNames.clear();
        for (String rn : ratioNames) {
            this.ratioNames.add(rn);
        }

        populateTableOfSelectedRatiosFromRatiosList();

        setChanged(true);
        setupSquidSessionSpecsAndReduceAndReport();
        updateAllExpressions(true);
    }

    @Override
    public void produceSummaryReportsForGUI() {
        if (unknownSpots.size() > 0) {
            try {
                reportsEngine.produceReports(shrimpFractions,
                        (ShrimpFraction) unknownSpots.get(0),
                        referenceMaterialSpots.size() > 0
                        ? (ShrimpFraction) referenceMaterialSpots.get(0) : (ShrimpFraction) unknownSpots.get(0),
                        false, true);
            } catch (IOException iOException) {
            }
        }
    }

    /**
     * The original Calamari Reports
     */
    @Override
    public void produceSanityReportsToFiles() {
        if (unknownSpots.size() > 0) {
            try {
                reportsEngine.produceReports(shrimpFractions,
                        (ShrimpFraction) unknownSpots.get(0),
                        referenceMaterialSpots.size() > 0
                        ? (ShrimpFraction) referenceMaterialSpots.get(0) : (ShrimpFraction) unknownSpots.get(0),
                        true, false);
            } catch (IOException iOException) {
            }
        }
    }

    private void reorderExpressions() {
        // cannot depend on comparator to do deep compares
        for (Expression listedExp : taskExpressionsOrdered) {
            // handle selected isotope-specific expressions
            // TODO: Better logic - selfaware expressionTree or polymorphism
            if (directAltPD) {
                if (listedExp.getName().compareToIgnoreCase(SQUID_TH_U_EQN_NAME) == 0) {
                    listedExp.setExcelExpressionString("[\"" + selectedIndexIsotope.getIsotopeCorrectionPrefixString() + SQUID_TH_U_EQN_NAME + "\"]");
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }
                if (listedExp.getName().compareToIgnoreCase(SQUID_TH_U_EQN_NAME_S) == 0) {
                    listedExp.setExcelExpressionString("[\"" + selectedIndexIsotope.getIsotopeCorrectionPrefixString() + SQUID_TH_U_EQN_NAME_S + "\"]");
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSAUnknownCalculation(true);
                }
                if (listedExp.getName().compareToIgnoreCase(SQUID_PPM_PARENT_EQN_NAME_TH) == 0) {
                    listedExp.setExcelExpressionString("[\"" + selectedIndexIsotope.getIsotopeCorrectionPrefixString() + SQUID_PPM_PARENT_EQN_NAME_TH + "\"]");
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }
                if (listedExp.getName().compareToIgnoreCase(OVER_COUNT_4_6_8) == 0) {
                    listedExp.setExcelExpressionString("[\"" + selectedIndexIsotope.getIsotopeCorrectionPrefixString() + OVER_COUNT_4_6_8 + "\"]");
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }

                if (listedExp.getName().compareToIgnoreCase(OVER_COUNTS_PERSEC_4_8) == 0) {
                    listedExp.setExcelExpressionString("[\"" + selectedIndexIsotope.getIsotopeCorrectionPrefixString() + OVER_COUNTS_PERSEC_4_8 + "\"]");
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }
                if (listedExp.getName().compareToIgnoreCase(CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA) == 0) {
                    listedExp.setExcelExpressionString("[\"" + selectedIndexIsotope.getIsotopeCorrectionPrefixString() + CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA + "\"]");
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }

            }
            if (listedExp.getName().compareToIgnoreCase(SQUID_TOTAL_206_238_NAME) == 0) {
                listedExp.setExcelExpressionString("[\"" + selectedIndexIsotope.getIsotopeCorrectionPrefixString() + SQUID_TOTAL_206_238_NAME + "\"]");
                listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
            }
            if (listedExp.getName().compareToIgnoreCase(SQUID_TOTAL_206_238_NAME_S) == 0) {
                listedExp.setExcelExpressionString("[\"" + selectedIndexIsotope.getIsotopeCorrectionPrefixString() + SQUID_TOTAL_206_238_NAME_S + "\"]");
                listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                listedExp.getExpressionTree().setSquidSwitchSAUnknownCalculation(true);
            }
            if (listedExp.getName().compareToIgnoreCase(SQUID_TOTAL_208_232_NAME) == 0) {
                listedExp.setExcelExpressionString("[\"" + selectedIndexIsotope.getIsotopeCorrectionPrefixString() + SQUID_TOTAL_208_232_NAME + "\"]");
                listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
            }
            if (listedExp.getName().compareToIgnoreCase(SQUID_TOTAL_208_232_NAME_S) == 0) {
                listedExp.setExcelExpressionString("[\"" + selectedIndexIsotope.getIsotopeCorrectionPrefixString() + SQUID_TOTAL_208_232_NAME_S + "\"]");
                listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                listedExp.getExpressionTree().setSquidSwitchSAUnknownCalculation(true);
            }
        }
        try {
            Collections.sort(taskExpressionsOrdered);
        } catch (Exception e) {
            //System.out.println("V I O L A T I O N ??");
        }
        Expression[] expArray = taskExpressionsOrdered.toArray(new Expression[taskExpressionsOrdered.size()]);
        Expression saved;
        for (int i = 0; i < expArray.length - 1; i++) {
            for (int j = i + 1; j < expArray.length; j++) {
                if (expArray[i].getExpressionTree().usesAnotherExpression(expArray[j].getExpressionTree())) {
                    saved = expArray[j];
                    for (int n = j; n > i; n--) {
                        expArray[n] = expArray[n - 1];
                    }
                    expArray[i] = saved;
                }
            }
        }

        // second pass to remove side effects
        for (int i = 0; i < expArray.length - 1; i++) {
            for (int j = i + 1; j < expArray.length; j++) {
                if (expArray[i].getExpressionTree().usesAnotherExpression(expArray[j].getExpressionTree())) {
                    saved = expArray[j];
                    for (int n = j; n > i; n--) {
                        expArray[n] = expArray[n - 1];
                    }
                    expArray[i] = saved;
                }
            }
        }

        taskExpressionsOrdered.clear();
        for (Expression listedExp : expArray) {
            //System.out.println(listedExp.getName());
            taskExpressionsOrdered.add(listedExp);
        }
        //System.out.println("<>><<><>>>>>>>>>>>>>>>>\n\n");
    }

    public void processAndSortExpressions() {

        reorderExpressions();

        assembleNamedExpressionsMap();

        buildExpressions();
    }

    @Override
    /**
     *
     */
    public void updateAffectedExpressions(Expression sourceExpression, boolean reprocessExpressions) {
        for (Expression listedExp : taskExpressionsOrdered) {
            if (listedExp.getExpressionTree().usesAnotherExpression(sourceExpression.getExpressionTree())) {
                updateSingleExpression(listedExp);
            }
        }

        setChanged(true);
        if (reprocessExpressions) {
            setupSquidSessionSpecsAndReduceAndReport();
        }
    }

    @Override
    /**
     * Updates expressions by parsing to detect new health or new sickness
     *
     */
    public void updateAllExpressions(boolean reprocessExpressions) {
        for (Expression listedExp : taskExpressionsOrdered) {
            updateSingleExpression(listedExp);
        }

        setChanged(true);
        if (reprocessExpressions) {
            setupSquidSessionSpecsAndReduceAndReport();
        }
    }

    // reparses and restores flags
    private void updateSingleExpression(Expression listedExp) {
        ExpressionTreeInterface original = listedExp.getExpressionTree();
        listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);

        if (listedExp.isSquidSwitchNU()) {
            if (listedExp.getExpressionTree() instanceof BuiltInExpressionInterface) {
                ((BuiltInExpressionInterface) listedExp.getExpressionTree()).buildExpression();
            }
        }

        if (original != null) {
            listedExp.getExpressionTree().setSquidSwitchSAUnknownCalculation(original.isSquidSwitchSAUnknownCalculation());
            listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(original.isSquidSwitchSTReferenceMaterialCalculation());
            listedExp.getExpressionTree().setSquidSwitchConcentrationReferenceMaterialCalculation(original.isSquidSwitchConcentrationReferenceMaterialCalculation());

            listedExp.getExpressionTree().setSquidSwitchSCSummaryCalculation(original.isSquidSwitchSCSummaryCalculation());
            listedExp.getExpressionTree().setSquidSpecialUPbThExpression(original.isSquidSpecialUPbThExpression());
            listedExp.getExpressionTree().setRootExpressionTree(original.isRootExpressionTree());
        }
    }

    @Override
    public void removeExpression(Expression expression, boolean reprocessExpressions) {
        if (expression != null) {
            // having issues with remove, so handling by hand
            // it appears java has a bug since even when comparator and equals have correct result
            List<Expression> taskBasket = new ArrayList<>();
            for (Expression exp : taskExpressionsOrdered) {
                if (!exp.equals(expression)) {
                    taskBasket.add(exp);
                }
            }
            taskExpressionsOrdered = taskBasket;
            taskExpressionsRemoved.add(expression);

            updateAffectedExpressions(expression, reprocessExpressions);
            updateAllExpressions(reprocessExpressions);
            setChanged(true);
            if (reprocessExpressions) {
                setupSquidSessionSpecsAndReduceAndReport();
            }
        }
    }

    @Override
    public void restoreRemovedExpressions() {
        for (Expression exp : taskExpressionsRemoved) {
            addExpression(exp, true);
        }
        taskExpressionsRemoved.clear();
    }

    @Override
    public void addExpression(Expression exp, boolean reprocessExpressions) {
        taskExpressionsOrdered.add(exp);

        updateAffectedExpressions(exp, reprocessExpressions);
        updateAllExpressions(reprocessExpressions);
        setChanged(true);
        if (reprocessExpressions) {
            setupSquidSessionSpecsAndReduceAndReport();
        }
    }

    private void createMapOfIndexToMassStationDetails() {
        if (prawnFile != null) {
            mapOfIndexToMassStationDetails = PrawnFileUtilities.createMapOfIndexToMassStationDetails(prawnFile.getRun());
        }
    }

    private void buildExpressions() {
        for (Expression exp : taskExpressionsOrdered) {
            if (exp.isSquidSwitchNU()) {
                if (exp.getExpressionTree() instanceof BuiltInExpressionInterface) {
                    ((BuiltInExpressionInterface) exp.getExpressionTree()).buildExpression();
                }
            }
        }
    }

    @Override
    public String listBuiltInExpressions() {
        StringBuilder expressionList = new StringBuilder();

        // order by ConcRefMat then RU then R then U
        List<Expression> taskExpressionsOrderedByTarget = new ArrayList<>();
        taskExpressionsOrderedByTarget.addAll(taskExpressionsOrdered);
        Collections.sort(taskExpressionsOrderedByTarget, ((o1, o2) -> {
            // ConcRefMat
            if (o1.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()
                    && !o2.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                return -1;
                // ConcRefMat
            } else if (!o1.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()
                    && o2.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                return 1;
                //RU
            } else if (o1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                    && o1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                    && o2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                    && o2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                // RU
            } else if (o1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                    && o1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                    && (!o2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                    || !o2.getExpressionTree().isSquidSwitchSAUnknownCalculation())) {
                return -1;
                // R
            } else if (o1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                    && !o1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                    && o2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                    && !o2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                // R
            } else if (o1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                    && !o1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                    && !o2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                    && o2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                return -1;
                // U
            } else if (!o1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                    && o1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                    && !o2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                    && o2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            } else {
                return 1;
            }

        }));

        expressionList.append("Built-in Expressions Sorted by Target Sample Type\n");

        for (int i = 0; i < taskExpressionsOrderedByTarget.size(); i++) {
            ExpressionTreeInterface expTree = taskExpressionsOrderedByTarget.get(i).getExpressionTree();
            if (expTree.isSquidSpecialUPbThExpression()) {
                if (expTree.isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                    expressionList.append("C");
                } else {
                    expressionList.append(" ");
                }
                if (expTree.isSquidSwitchSTReferenceMaterialCalculation()) {
                    expressionList.append("R");
                } else {
                    expressionList.append(" ");
                }
                if (expTree.isSquidSwitchSAUnknownCalculation()) {
                    expressionList.append("U");
                } else {
                    expressionList.append(" ");
                }

                expressionList.append("\t").append(expTree.getName()).append("\n");
            }
        }

        return expressionList.toString();
    }

    @Override
    public void updateRefMatCalibConstWMeanExpressions(boolean squidAllowsAutoExclusionOfSpots) {
        String xCorr206238Name = "-corr " + SQUID_CALIB_CONST_AGE_206_238_BASENAME + "calibr.const WM";
        String xCorr208232Name = "-corr " + SQUID_CALIB_CONST_AGE_208_232_BASENAME + "calibr.const WM";
        for (Expression listedExp : taskExpressionsOrdered) {
            if (listedExp.getName().compareToIgnoreCase("4" + xCorr206238Name) == 0) {
                listedExp.setExcelExpressionString("WtdMeanACalc( [\"4-corr 206Pb/238Ucalibr.const\"], "
                        + "[%\"4-corr 206Pb/238Ucalibr.const\"], "
                        + !squidAllowsAutoExclusionOfSpots + ", "
                        + "FALSE )");
                completeUpdateRefMatCalibConstWMeanExpressions(listedExp);
            } else if (listedExp.getName().compareToIgnoreCase("7" + xCorr206238Name) == 0) {
                listedExp.setExcelExpressionString("WtdMeanACalc( [\"7-corr 206Pb/238Ucalibr.const\"], "
                        + "[%\"7-corr 206Pb/238Ucalibr.const\"], "
                        + !squidAllowsAutoExclusionOfSpots + ", "
                        + "FALSE )");
                completeUpdateRefMatCalibConstWMeanExpressions(listedExp);
            } else if (listedExp.getName().compareToIgnoreCase("8" + xCorr206238Name) == 0) {
                listedExp.setExcelExpressionString("WtdMeanACalc( [\"8-corr 206Pb/238Ucalibr.const\"], "
                        + "[%\"8-corr 206Pb/238Ucalibr.const\"], "
                        + !squidAllowsAutoExclusionOfSpots + ", "
                        + "FALSE )");
                completeUpdateRefMatCalibConstWMeanExpressions(listedExp);
            } else if (listedExp.getName().compareToIgnoreCase("4" + xCorr208232Name) == 0) {
                listedExp.setExcelExpressionString("WtdMeanACalc( [\"4-corr 208Pb/232Thcalibr.const\"], "
                        + "[%\"4-corr 208Pb/232Thcalibr.const\"], "
                        + !squidAllowsAutoExclusionOfSpots + ", "
                        + "FALSE )");
                completeUpdateRefMatCalibConstWMeanExpressions(listedExp);
            } else if (listedExp.getName().compareToIgnoreCase("7" + xCorr208232Name) == 0) {
                listedExp.setExcelExpressionString("WtdMeanACalc( [\"7-corr 208Pb/232Thcalibr.const\"], "
                        + "[%\"7-corr 208Pb/232Thcalibr.const\"], "
                        + !squidAllowsAutoExclusionOfSpots + ", "
                        + "FALSE )");
                completeUpdateRefMatCalibConstWMeanExpressions(listedExp);
            }
        }
        this.squidAllowsAutoExclusionOfSpots = squidAllowsAutoExclusionOfSpots;
        changed = true;
        setupSquidSessionSpecsAndReduceAndReport();
    }

    private void completeUpdateRefMatCalibConstWMeanExpressions(Expression listedExp) {
        listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
        listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
        listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
        listedExp.getExpressionTree().setSquidSwitchSCSummaryCalculation(true);

        // Aug 2018 change logic to clear the spots list now that task manager has checkbox for auto reject
        SpotSummaryDetails spotSummaryDetails = taskExpressionsEvaluationsPerSpotSet.get(listedExp.getName());
        if (spotSummaryDetails != null) {
            spotSummaryDetails.resetRejectedIndices();
        }
    }

    @Override
    public void buildSquidSpeciesModelList() {
        createMapOfIndexToMassStationDetails();
        if (squidSpeciesModelList.isEmpty()) {
            buildSquidSpeciesModelListFromMassStationDetails();
        }
        if (mapOfIndexToMassStationDetails != null) {
            // update these if squidSpeciesModelList exists
            if (squidSpeciesModelList.size() > 0) {
                int index = 0;
                for (SquidSpeciesModel ssm : squidSpeciesModelList) {
                    MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(ssm.getMassStationIndex());
                    // only these fields change
                    massStationDetail.setIsotopeLabel(ssm.getIsotopeName());
                    if (nominalMasses.size() > 0) {
                        if (indexOfTaskBackgroundMass == index) {
                            massStationDetail.setTaskIsotopeLabel(SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL);
                        } else {
                            massStationDetail.setTaskIsotopeLabel(nominalMasses.get(index));
                        }
                        index++;
                    }

                    massStationDetail.setIsBackground(ssm.getIsBackground());
                    if (ssm.getIsBackground()) {
                        indexOfBackgroundSpecies = massStationDetail.getMassStationIndex();
                    }

                    massStationDetail.setuThBearingName(ssm.getuThBearingName());

                    massStationDetail.setViewedAsGraph(ssm.isViewedAsGraph());
                }
            } else {
                buildSquidSpeciesModelListFromMassStationDetails();
                // TODO: July 2018 not a priority          alignTaskMassStationsWithPrawnFile();
            }
        }
    }

    @Override
    public void applyTaskIsotopeLabelsToMassStations() {
        int index = 0;
        for (SquidSpeciesModel ssm : squidSpeciesModelList) {
            MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(ssm.getMassStationIndex());
            if ((ssm.getMassStationIndex() == indexOfTaskBackgroundMass) && (indexOfTaskBackgroundMass != indexOfBackgroundSpecies)) {
                // changing mass station background to match task background
                massStationDetail.setIsotopeLabel(SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL);
                selectBackgroundSpeciesReturnPreviousIndex(ssm);
                indexOfBackgroundSpecies = indexOfTaskBackgroundMass;
            } else {
                ssm.setIsotopeName(nominalMasses.get(index));
                massStationDetail.setIsotopeLabel(ssm.getIsotopeName());
            }
            index++;
        }

        changed = true;
        SquidProject.setProjectChanged(true);
        updateAllExpressions(true);
        processAndSortExpressions();

        updateAllExpressions(true);
        setupSquidSessionSpecsAndReduceAndReport();
    }

    @Override
    public void applyMassStationLabelsToTask() {
        // identify isotopelabels to change
        List<Integer> changelings = new ArrayList<>();
        for (int i = 0; i < nominalMasses.size(); i++) {
            if (nominalMasses.get(i).compareToIgnoreCase(mapOfIndexToMassStationDetails.get(i).getIsotopeLabel()) != 0) {
                changelings.add(i);
            }
        }

        for (Expression exp : taskExpressionsOrdered) {
            String excelString = exp.getExcelExpressionString();
            for (int i = 0; i < changelings.size(); i++) {
                int index = changelings.get(i);
                if (excelString.contains(nominalMasses.get(index))) {
                    //System.out.println("CHANGE " + exp.getName() + " >> " + excelString + " >> " + mapOfIndexToMassStationDetails.get(index).getIsotopeLabel());
                    excelString = excelString.replaceAll(nominalMasses.get(index), mapOfIndexToMassStationDetails.get(index).getIsotopeLabel());
                    for (int r = 0; r < ratioNames.size(); r++) {
                        if (ratioNames.get(r).contains(nominalMasses.get(index))) {
                            ratioNames.set(r, ratioNames.get(r).replaceAll(nominalMasses.get(index), mapOfIndexToMassStationDetails.get(index).getIsotopeLabel()));
                        }
                    }
                }
            }
            exp.setExcelExpressionString(excelString);
        }

        // update nominalMasses
        for (int i = 0; i < changelings.size(); i++) {
            nominalMasses.set(changelings.get(i), mapOfIndexToMassStationDetails.get(changelings.get(i)).getIsotopeLabel());
        }

        indexOfTaskBackgroundMass = indexOfBackgroundSpecies;

        changed = true;
        SquidProject.setProjectChanged(true);
        setupSquidSessionSpecsAndReduceAndReport();
        updateAllExpressions(true);
    }

    private void buildSquidSpeciesModelListFromMassStationDetails() {
        squidSpeciesModelList = new ArrayList<>();
        for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
            SquidSpeciesModel spm = new SquidSpeciesModel(
                    entry.getKey(), entry.getValue().getMassStationLabel(), entry.getValue().getIsotopeLabel(),
                    entry.getValue().getElementLabel(), entry.getValue().getIsBackground(), entry.getValue().getuThBearingName(),
                    entry.getValue().isViewedAsGraph());

            squidSpeciesModelList.add(spm);
        }
        if (tableOfSelectedRatiosByMassStationIndex.length == 0) {
            tableOfSelectedRatiosByMassStationIndex = new boolean[squidSpeciesModelList.size()][squidSpeciesModelList.size()];
        }
    }

    private void alignTaskMassStationsWithPrawnFile() {
        List<String> matchedNominalMasses = new ArrayList<>();
        boolean[] recordedMatches = new boolean[mapOfIndexToMassStationDetails.size()];
        if (mapOfIndexToMassStationDetails.size() < nominalMasses.size()) {

            for (int i = 0; i < nominalMasses.size(); i++) {//            String taskIsotopeLabel : nominalMasses) {
                String taskIsotopeLabel = nominalMasses.get(i);
                boolean matched = false;
                int intTaskIsotopeLabel = new BigDecimal(taskIsotopeLabel).add(new BigDecimal(0.5)).intValue();
                for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
                    if (!matched) {
                        int intPrawnIsoptopeLabel = new BigDecimal(entry.getValue().getIsotopeLabel()).setScale(0, RoundingMode.HALF_EVEN).intValue();//               Integer.parseInt(entry.getValue().getIsotopeLabel());
                        int recordedEntryKey = (int) entry.getKey();
                        if ((intTaskIsotopeLabel == intPrawnIsoptopeLabel) && !recordedMatches[recordedEntryKey]) {
                            matchedNominalMasses.add(taskIsotopeLabel);
                            recordedMatches[entry.getKey()] = true;
                            matched = true;
                        }
                    }
                }
                if (!matched) {
                    if (i < indexOfTaskBackgroundMass) {
                        indexOfTaskBackgroundMass--;
                    }
                }
            }

            // assume all matching is done
            nominalMasses = matchedNominalMasses;
            updateRatioNamesFromNominalMasses();

        }
    }

    @Override
    public int selectBackgroundSpeciesReturnPreviousIndex(SquidSpeciesModel ssm) {
        // there is at most one background for now
        int retVal = -1;

        for (SquidSpeciesModel squidSpeciesModel : squidSpeciesModelList) {
            if (squidSpeciesModel.getIsBackground()) {
                squidSpeciesModel.setIsBackground(false);
                retVal = squidSpeciesModel.getMassStationIndex();
                break;
            }
        }

        if (ssm != null) {
            ssm.setIsBackground(true);
        }

        return retVal;
    }

    private void buildSquidRatiosModelListFromMassStationDetails() {
        squidRatiosModelList = new ArrayList<>();

        // TODO revise use of this in comparator of squidSpeciesModel
        int reportingOrderIndex = 0;
        for (int row = 0; row < tableOfSelectedRatiosByMassStationIndex.length; row++) {
            for (int col = 0; col < tableOfSelectedRatiosByMassStationIndex[0].length; col++) {
                if ((tableOfSelectedRatiosByMassStationIndex[row][col])
                        && (!squidSpeciesModelList.get(row).getIsBackground())
                        && (!squidSpeciesModelList.get(col).getIsBackground())) {
                    squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(row), squidSpeciesModelList.get(col), reportingOrderIndex));
                    reportingOrderIndex++;
                }
            }
        }
    }

    private void assembleNamedExpressionsMap() {
        namedExpressionsMap.clear();

        for (Map.Entry<String, ExpressionTreeInterface> entry : namedConstantsMap.entrySet()) {
            namedExpressionsMap.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, ExpressionTreeInterface> entry : namedParametersMap.entrySet()) {
            namedExpressionsMap.put(entry.getKey(), entry.getValue());
        }

        // TODO: make a SpotFieldNode factory
        ExpressionTreeInterface expHours = buildSpotNode("getHours");
        namedExpressionsMap.put(expHours.getName(), expHours);

        for (SquidSpeciesModel spm : squidSpeciesModelList) {
            ShrimpSpeciesNode shrimpSpeciesNode = ShrimpSpeciesNode.buildShrimpSpeciesNode(spm);
            namedExpressionsMap.put(spm.getIsotopeName(), shrimpSpeciesNode);
        }

        for (SquidRatiosModel srm : squidRatiosModelList) {
            namedExpressionsMap.put(srm.getRatioName(), buildRatioExpression(srm.getRatioName()));
        }

        for (Expression exp : taskExpressionsOrdered) {
            namedExpressionsMap.put(exp.getName(), exp.getExpressionTree());
        }
    }

    private ExpressionTreeInterface buildRatioExpression(String ratioName) {
        // format of ratioName is "nnn/mmm"
        ExpressionTreeInterface ratioExpression = null;
        if ((findNumerator(ratioName) != null) && (findDenominator(ratioName) != null)) {
            ratioExpression
                    = new ExpressionTree(
                            ratioName,
                            ShrimpSpeciesNode.buildShrimpSpeciesNode(findNumerator(ratioName), "getPkInterpScanArray"),
                            ShrimpSpeciesNode.buildShrimpSpeciesNode(findDenominator(ratioName), "getPkInterpScanArray"),
                            Operation.divide());

            ((ExpressionTreeWithRatiosInterface) ratioExpression).getRatiosOfInterest().add(ratioName);
        }
        return ratioExpression;
    }

    @Override
    public ExpressionTreeInterface findNamedExpression(String ratioName) {
        ExpressionTreeInterface foundRatioExp = namedExpressionsMap.get(ratioName);
        if (foundRatioExp == null) {
            foundRatioExp = new ConstantNode(MISSING_EXPRESSION_STRING, ratioName);
        } else if (!foundRatioExp.amHealthy()) {
            foundRatioExp = new ConstantNode(MISSING_EXPRESSION_STRING, ratioName);
        }
        return foundRatioExp;
    }

    public SquidSpeciesModel findNumerator(String ratioName) {
        String[] parts = ratioName.split("/");
        return lookUpSpeciesByName(parts[0]);
    }

    public SquidSpeciesModel findDenominator(String ratioName) {
        String[] parts = ratioName.split("/");
        return lookUpSpeciesByName(parts[1]);
    }

    @Override
    public SquidSpeciesModel lookUpSpeciesByName(String isotopeName) {
        SquidSpeciesModel retVal = null;

        for (SquidSpeciesModel squidSpeciesModel : squidSpeciesModelList) {
            if (squidSpeciesModel.getIsotopeName().compareToIgnoreCase(isotopeName) == 0) {
                retVal = squidSpeciesModel;
                break;
            }
        }

        return retVal;
    }

    /**
     *
     * @param ratiosOfInterest
     * @return
     */
    public Set<SquidSpeciesModel> extractUniqueSpeciesNumbers(List<String> ratiosOfInterest) {
        // assume acquisition order is atomic weight order
        Set<SquidSpeciesModel> eqPkUndupeOrd = new TreeSet<>();
        for (int i = 0; i < ratiosOfInterest.size(); i++) {
            eqPkUndupeOrd.add(findNumerator(ratiosOfInterest.get(i)));
            eqPkUndupeOrd.add(findDenominator(ratiosOfInterest.get(i)));
        }
        return eqPkUndupeOrd;
    }

    /**
     *
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
        xstream.alias("ShrimpSpeciesNode", ShrimpSpeciesNode.class);

        xstream.registerConverter(new ConstantNodeXMLConverter());
        xstream.alias("ConstantNode", ConstantNode.class);

        xstream.registerConverter(new VariableNodeForSummaryXMLConverter());
        xstream.alias("VariableNodeForSummary", VariableNodeForSummary.class);
        xstream.alias("VariableNodeForPerSpotTaskExpressions", VariableNodeForPerSpotTaskExpressions.class);
        xstream.alias("VariableNodeForIsotopicRatios", VariableNodeForIsotopicRatios.class);

        xstream.registerConverter(new OperationXMLConverter());
        xstream.registerConverter(new FunctionXMLConverter());

        xstream.registerConverter(new ExpressionTreeXMLConverter());
        xstream.alias("ExpressionTree", ExpressionTree.class);

        xstream.registerConverter(new TaskXMLConverter());
        xstream.alias("Task", Task.class);
        xstream.alias("Task", this.getClass());

        // Note: http://cristian.sulea.net/blog.php?p=2014-11-12-xstream-object-references
        xstream.setMode(XStream.NO_REFERENCES);

    }

    @Override
    public List<ShrimpFractionExpressionInterface> processRunFractions(ShrimpDataFileInterface prawnFile, SquidSessionModel squidSessionSpecs) {
        shrimpFractions = new ArrayList<>();

        ShrimpFraction shrimpFraction = null;
        for (int f = 0; f < prawnFile.extractCountOfRuns(); f++) {
            PrawnFile.Run runFraction = ((PrawnFile) prawnFile).getRun().get(f);
            shrimpFraction
                    = PRAWN_FILE_RUN_FRACTION_PARSER.processRunFraction(runFraction, squidSessionSpecs);

            if (shrimpFraction != null) {
                shrimpFraction.setSpotNumber(f + 1);
                String nameOfMount = ((PrawnFile) prawnFile).getMount();
                if (nameOfMount == null) {
                    nameOfMount = "No-Mount-Name";
                }
                shrimpFraction.setNameOfMount(nameOfMount);
                shrimpFractions.add(shrimpFraction);
            }
        }
        // prepare for task expressions to be evaluated
        // setup spots
        shrimpFractions.forEach(
                (spot) -> {
                    List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsForScansEvaluated = new ArrayList<>();
                    spot.setTaskExpressionsForScansEvaluated(taskExpressionsForScansEvaluated);
                }
        );

        // subdivide spots and calculate hours
        referenceMaterialSpots = new ArrayList<>();
        concentrationReferenceMaterialSpots = new ArrayList<>();
        unknownSpots = new ArrayList<>();
        boolean firstReferenceMaterial = true;
        long baseTimeOfFirstRefMatForCalcHoursField = 0l;
        for (ShrimpFractionExpressionInterface spot : shrimpFractions) {
            // spots that are concentrationReferenceMaterialModel will also be in one of the other two buckets
            if (spot.isConcentrationReferenceMaterial()) {
                concentrationReferenceMaterialSpots.add(spot);
            }
            if (spot.isReferenceMaterial()) {
                referenceMaterialSpots.add(spot);
                if (firstReferenceMaterial) {
                    baseTimeOfFirstRefMatForCalcHoursField = spot.getDateTimeMilliseconds();
                    firstReferenceMaterial = false;
                }
            } else {
                unknownSpots.add(spot);
            }
        }

        for (ShrimpFractionExpressionInterface spot : shrimpFractions) {
            ((ShrimpFraction) spot).calculateSpotHours(baseTimeOfFirstRefMatForCalcHoursField);
        }

        generateMapOfUnknownsBySampleNames();

        return shrimpFractions;
    }

    /**
     *
     */
    @Override
    public void evaluateTaskExpressions() {

        // prep spots
        shrimpFractions.forEach((spot) -> {
            spot.getTaskExpressionsForScansEvaluated().clear();
        });

        for (Expression expression : taskExpressionsOrdered) {
            ExpressionTreeInterface expressionTree = expression.getExpressionTree();

            if (expressionTree.amHealthy()) {// && expressionTree.isRootExpressionTree()) {
                // determine subset of spots to be evaluated - default = all
                List<ShrimpFractionExpressionInterface> spotsForExpression = shrimpFractions;

                if (((ExpressionTree) expressionTree).isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                    spotsForExpression = concentrationReferenceMaterialSpots;
                } else if (!((ExpressionTree) expressionTree).isSquidSwitchSTReferenceMaterialCalculation()) {
                    spotsForExpression = unknownSpots;
                } else if (!((ExpressionTree) expressionTree).isSquidSwitchSAUnknownCalculation()) {
                    spotsForExpression = referenceMaterialSpots;
                }

                // now evaluate expressionTree
                try {
                    evaluateExpressionForSpotSet(expression, spotsForExpression);
                } catch (SquidException | ArrayIndexOutOfBoundsException squidException) {
                    //System.out.println("Out of bounds at evaluateTaskExpressions");
                }
            }
        }
    }

    /**
     *
     * @param expressionTree
     * @param spotsForExpression
     * @throws SquidException
     */
    private void evaluateExpressionForSpotSet(
            Expression expression,
            List<ShrimpFractionExpressionInterface> spotsForExpression) throws SquidException {

        ExpressionTreeInterface expressionTree = expression.getExpressionTree();

        // determine type of expressionTree
        // Summary expression test
        if (((ExpressionTree) expressionTree).isSquidSwitchSCSummaryCalculation()) {
            List<ShrimpFractionExpressionInterface> spotsUsedForCalculation = new ArrayList<>();
            double[][] values;

            // May 2018 new logic to support user rejecting some fractions in summary calculations - Weighted Mean for now
            SpotSummaryDetails spotSummaryDetails;
            if (taskExpressionsEvaluationsPerSpotSet.containsKey(expression.getName())) {
                spotSummaryDetails = taskExpressionsEvaluationsPerSpotSet.get(expression.getName());
            } else {
                spotSummaryDetails = new SpotSummaryDetails(expressionTree);
                taskExpressionsEvaluationsPerSpotSet.put(expression.getName(), spotSummaryDetails);
            }

            // if the spotsForExpression are the same, then preserve list of indices of rejected
            // otherwise reset the list of indices to empty
            if (!spotSummaryDetails.getSelectedSpots().equals(spotsForExpression)) {
                spotSummaryDetails.setRejectedIndices(new boolean[spotsForExpression.size()]);
                spotSummaryDetails.setSelectedSpots(spotsForExpression);
            }

            if ((expressionTree instanceof ConstantNode) || ((ExpressionTree) expressionTree).getOperation().isScalarResult()) {
                // create list of one spot, since we only need to look up value once
                if (spotsForExpression.size() > 0) {
                    spotsUsedForCalculation.add(spotsForExpression.get(0));
                }
            } else {
                // default is all
                spotsUsedForCalculation.addAll(spotsForExpression);
            }

            // update spotSummaryDetails
            spotSummaryDetails.setExpressionTree(expressionTree);

            // special case for WeightedMean in which auto rejection is possible
            boolean noReject = false;
            if (((ExpressionTree) expressionTree).getOperation() instanceof WtdMeanACalc) {
                // discover what the flags say - repeats logic of WtdMeanACalc()
                // TODO: move logic to single location
                List<ExpressionTreeInterface> childrenET = ((ExpressionTree) expressionTree).getChildrenET();

                Object noUPbConstAutoRejectO = childrenET.get(2).eval(shrimpFractions, this)[0][0];
                boolean noUPbConstAutoReject = (boolean) noUPbConstAutoRejectO;

                Object pbCanDriftCorrO = childrenET.get(3).eval(shrimpFractions, this)[0][0];
                boolean pbCanDriftCorr = (boolean) pbCanDriftCorrO;

                noReject = (noUPbConstAutoReject && !pbCanDriftCorr);

                if (noReject) {
                    // we use the user's stored rejections and do not do autorejection
                    // note on first pass after changing noreject, the rejected spots from auto reject are used 
                    // because they are still logged in rejectedIndices
                    spotsUsedForCalculation = spotSummaryDetails.retrieveActiveSpots();
                }
            }

            values = convertObjectArrayToDoubles(expressionTree.eval(spotsUsedForCalculation, this));

            // in the case of auto-rejection, mark the rejected spots after the fact
            if ((((ExpressionTree) expressionTree).getOperation() instanceof WtdMeanACalc) && !noReject) {
                // we save off auto-rejected spots for display purposes
                boolean[] rejectedIndices = new boolean[spotsUsedForCalculation.size()];
                for (int i = 0; i < values[1].length; i++) {
                    rejectedIndices[(int) values[1][i]] = true;
                }
                for (int i = 0; i < values[2].length; i++) {
                    rejectedIndices[(int) values[2][i]] = true;
                }

                spotSummaryDetails.setRejectedIndices(rejectedIndices);
            }

            spotSummaryDetails.setManualRejectionEnabled(noReject);
            spotSummaryDetails.setValues(values);

        } else {
            // perform expressionTree on each spot
            for (ShrimpFractionExpressionInterface spot : spotsForExpression) {
                evaluateExpressionForSpot(expressionTree, spot);
            }
        }
    }

    private void evaluateExpressionForSpot(
            ExpressionTreeInterface expression,
            ShrimpFractionExpressionInterface spot) throws SquidException {

        if (((ExpressionTree) expression).hasRatiosOfInterest()) {
            // case of Squid switch "NU"
            ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
            TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEvaluatedPerSpotPerScanModel
                    = expressionEvaluator.evaluateTaskExpressionsPerSpotPerScan(this, expression, spot);

            // save scan-specific results
            List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsForScansEvaluated = spot.getTaskExpressionsForScansEvaluated();
            if (taskExpressionsForScansEvaluated.contains(taskExpressionEvaluatedPerSpotPerScanModel)) {
                taskExpressionsForScansEvaluated.remove(taskExpressionEvaluatedPerSpotPerScanModel);
            }
            spot.getTaskExpressionsForScansEvaluated().add(taskExpressionEvaluatedPerSpotPerScanModel);

            // save spot-specific results
            double[][] value = new double[][]{{taskExpressionEvaluatedPerSpotPerScanModel.getRatioVal(),
                taskExpressionEvaluatedPerSpotPerScanModel.getRatioFractErr()}};

            Map<ExpressionTreeInterface, double[][]> taskExpressionsPerSpot = spot.getTaskExpressionsEvaluationsPerSpot();
            if (taskExpressionsPerSpot.containsKey(expression)) {
                taskExpressionsPerSpot.remove(expression);
            }
            spot.getTaskExpressionsEvaluationsPerSpot().put(expression, value);

        } else {
            List<ShrimpFractionExpressionInterface> singleSpot = new ArrayList<>();
            singleSpot.add(spot);
            try {
                double[][] value = convertObjectArrayToDoubles(expression.eval(singleSpot, this));
                spot.getTaskExpressionsEvaluationsPerSpot().put(expression, value);
            } catch (SquidException squidException) {
//                //System.out.println(squidException.getMessage() + " while processing " + expressionTree.getName());
            }
        }
    }

    /**
     * @return the listOfMassStationDetails
     */
    @Override
    public List<MassStationDetail> makeListOfMassStationDetails() {
        List<MassStationDetail> listOfMassStationDetails = new ArrayList<>();
        for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
            listOfMassStationDetails.add(entry.getValue());
            if (entry.getValue().getIsBackground()) {
                entry.getValue().setIsotopeLabel(SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL);
                indexOfBackgroundSpecies = entry.getKey();
            }
        }
        return listOfMassStationDetails;
    }

    @Override
    public void populateTableOfSelectedRatiosFromRatiosList() {
        resetTableOfSelectedRatiosByMassStationIndex();

        for (int i = 0; i < ratioNames.size(); i++) {
            String[] ratio = ratioNames.get(i).split("/");
            if ((lookUpSpeciesByName(ratio[0]) != null)
                    && lookUpSpeciesByName(ratio[1]) != null) {
                int num = lookUpSpeciesByName(ratio[0]).getMassStationIndex();
                int den = lookUpSpeciesByName(ratio[1]).getMassStationIndex();
                tableOfSelectedRatiosByMassStationIndex[num][den] = true;
            }
        }
    }

    @Override
    public void updateTableOfSelectedRatiosByMassStationIndex(int row, int col, boolean selected) {
        tableOfSelectedRatiosByMassStationIndex[row][col] = selected;
        String num = mapOfIndexToMassStationDetails.get(row).getIsotopeLabel();
        String den = mapOfIndexToMassStationDetails.get(col).getIsotopeLabel();
        String ratioName = num + "/" + den;
        if (ratioNames.contains(ratioName)) {
            if (!selected) {
                ratioNames.remove(ratioName);
            }
        } else {
            if (selected) {
                ratioNames.add(ratioName);
            }
        }

        setChanged(true);
        setupSquidSessionSpecsAndReduceAndReport();
        updateAllExpressions(true);
    }

    @Override
    public void updateTableOfSelectedRatiosByRowOrCol(int row, int col, boolean selected) {
        if (row == -1) {
            String den = mapOfIndexToMassStationDetails.get(col).getIsotopeLabel();
            for (int i = 0; i < mapOfIndexToMassStationDetails.size(); i++) {
                String num = mapOfIndexToMassStationDetails.get(i).getIsotopeLabel();
                if (num.compareTo(den) != 0) {
                    String ratioName = num + "/" + den;
                    if (selected) {
                        if (!ratioNames.contains(ratioName)) {
                            ratioNames.add(ratioName);
                        }
                    } else {
                        ratioNames.remove(ratioName);
                    }
                }
            }
        }

        if (col == -1) {
            String num = mapOfIndexToMassStationDetails.get(row).getIsotopeLabel();
            for (int i = 0; i < mapOfIndexToMassStationDetails.size(); i++) {
                String den = mapOfIndexToMassStationDetails.get(i).getIsotopeLabel();
                if (num.compareTo(den) != 0) {
                    String ratioName = num + "/" + den;
                    if (selected) {
                        if (!ratioNames.contains(ratioName)) {
                            ratioNames.add(ratioName);
                        }
                    } else {
                        ratioNames.remove(ratioName);
                    }
                }
            }
        }

        setChanged(true);
        setupSquidSessionSpecsAndReduceAndReport();
        updateAllExpressions(true);
    }

    private void updateRatioNamesFromNominalMasses() {
        List<String> revisedRatioNames = new ArrayList<>();
        for (String ratioName : ratioNames) {
            String[] ratioNameParts = ratioName.split("/");
            if ((nominalMasses.contains(ratioNameParts[0])) && (nominalMasses.contains(ratioNameParts[1]))) {
                revisedRatioNames.add(ratioName);
            }
        }

        ratioNames = revisedRatioNames;
    }

    public Expression getExpressionByName(String name) {
        Expression exp = null;
        for (Expression expression : taskExpressionsOrdered) {
            if (expression.getName().compareToIgnoreCase(name) == 0) {
                exp = expression;
                break;
            }
        }
        return exp;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    @Override
    public TaskTypeEnum getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    @Override
    public void setType(TaskTypeEnum type) {
        this.type = type;
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the authorName
     */
    @Override
    public String getAuthorName() {
        return authorName;
    }

    /**
     * @param authorName the authorName to set
     */
    @Override
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * @return the labName
     */
    @Override
    public String getLabName() {
        return labName;
    }

    /**
     * @param labName the labName to set
     */
    @Override
    public void setLabName(String labName) {
        this.labName = labName;
    }

    /**
     * @return the dateRevised
     */
    @Override
    public long getDateRevised() {
        return dateRevised;
    }

    /**
     * @param dateRevised the dateRevised to set
     */
    @Override
    public void setDateRevised(long dateRevised) {
        this.dateRevised = dateRevised;
    }

    /**
     * @param filterForRefMatSpotNames the filterForRefMatSpotNames to set
     */
    @Override
    public void setFilterForRefMatSpotNames(String filterForRefMatSpotNames) {
        this.filterForRefMatSpotNames = filterForRefMatSpotNames;
    }

    /**
     * @return the filterForRefMatSpotNames
     */
    public String getFilterForRefMatSpotNames() {
        return filterForRefMatSpotNames;
    }

    @Override
    /**
     *
     */
    public void setFilterForConcRefMatSpotNames(String filterForConcRefMatSpotNames) {
        this.filterForConcRefMatSpotNames = filterForConcRefMatSpotNames;
    }

    /**
     * @param filtersForUnknownNames the filtersForUnknownNames to set
     */
    @Override
    public void setFiltersForUnknownNames(Map<String, Integer> filtersForUnknownNames) {
        this.filtersForUnknownNames = filtersForUnknownNames;
    }

    /**
     * @return the useSBM
     */
    @Override
    public boolean isUseSBM() {
        return useSBM;
    }

    /**
     * @param useSBM the useSBM to set
     */
    @Override
    public void setUseSBM(boolean useSBM) {
        this.useSBM = useSBM;
    }

    /**
     * @return the userLinFits
     */
    @Override
    public boolean isUserLinFits() {
        return userLinFits;
    }

    /**
     * @param userLinFits the userLinFits to set
     */
    @Override
    public void setUserLinFits(boolean userLinFits) {
        this.userLinFits = userLinFits;
    }

    /**
     * @return the indexOfBackgroundSpecies
     */
    @Override
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
     *
     * @param indexOfTaskBackgroundMass
     */
    @Override
    public void setIndexOfTaskBackgroundMass(int indexOfTaskBackgroundMass) {
        this.indexOfTaskBackgroundMass = indexOfTaskBackgroundMass;
    }

    /**
     *
     * @return
     */
    @Override
    public String getParentNuclide() {
        return parentNuclide;
    }

    /**
     *
     * @param parentNuclide
     */
    @Override
    public void setParentNuclide(String parentNuclide) {
        this.parentNuclide = parentNuclide;
    }

    /**
     * @return the directAltPD
     */
    @Override
    public boolean isDirectAltPD() {
        return directAltPD;
    }

    /**
     * @param directAltPD the directAltPD to set
     */
    @Override
    public void setDirectAltPD(boolean directAltPD) {
        this.directAltPD = directAltPD;
    }

    /**
     * @param taskExpressionsOrdered the taskExpressionTreesOrdered to set
     */
    @Override
    public void setTaskExpressionsOrdered(List<Expression> taskExpressionsOrdered) {
        this.taskExpressionsOrdered = taskExpressionsOrdered;
    }

    /**
     * @return the taskExpressionsEvaluationsPerSpotSet
     */
    @Override
    public Map<String, SpotSummaryDetails> getTaskExpressionsEvaluationsPerSpotSet() {
        return taskExpressionsEvaluationsPerSpotSet;
    }

    /**
     * @param taskExpressionsEvaluationsPerSpotSet the
     * taskExpressionsEvaluationsPerSpotSet to set
     */
    @Override
    public void setTaskExpressionsEvaluationsPerSpotSet(Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet) {
        this.taskExpressionsEvaluationsPerSpotSet = taskExpressionsEvaluationsPerSpotSet;
    }

    /**
     * @return the provenance
     */
    @Override
    public String getProvenance() {
        return provenance;
    }

    /**
     * @param provenance the provenance to set
     */
    @Override
    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    /**
     * @return the nominalMasses
     */
    @Override
    public List<String> getNominalMasses() {
        return nominalMasses;
    }

    /**
     * @param nominalMasses the nominalMasses to set
     */
    @Override
    public void setNominalMasses(List<String> nominalMasses) {
        this.nominalMasses = nominalMasses;
    }

    /**
     * @return the ratioNames
     */
    @Override
    public List<String> getRatioNames() {
        return ratioNames;
    }

    /**
     * @param ratioNames the ratioNames to set
     */
    @Override
    public void setRatioNames(List<String> ratioNames) {
        this.ratioNames = ratioNames;
    }

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    @Override
    public Map<Integer, MassStationDetail> getMapOfIndexToMassStationDetails() {
        if (mapOfIndexToMassStationDetails == null) {
            createMapOfIndexToMassStationDetails();
        }
        return mapOfIndexToMassStationDetails;
    }

    /**
     * @return the squidSessionModel
     */
    @Override
    public SquidSessionModel getSquidSessionModel() {
        return squidSessionModel;
    }

    /**
     * @param squidSessionModel the squidSessionModel to set
     */
    public void setSquidSessionModel(SquidSessionModel squidSessionModel) {
        this.squidSessionModel = squidSessionModel;
    }

    /**
     * @return the squidSpeciesModelList
     */
    @Override
    public List<SquidSpeciesModel> getSquidSpeciesModelList() {
        return squidSpeciesModelList;
    }

    /**
     * @return the squidRatiosModelList
     */
    @Override
    public List<SquidRatiosModel> getSquidRatiosModelList() {
        return squidRatiosModelList;
    }

    /**
     * @return the tableOfSelectedRatiosByMassStationIndex
     */
    @Override
    public boolean[][] getTableOfSelectedRatiosByMassStationIndex() {
        return clone2dArray(tableOfSelectedRatiosByMassStationIndex);
    }

    private void resetTableOfSelectedRatiosByMassStationIndex() {
        tableOfSelectedRatiosByMassStationIndex = new boolean[squidSpeciesModelList.size()][squidSpeciesModelList.size()];
    }

    /**
     * @param prawnFile the prawnFile to set
     */
    @Override
    public void setPrawnFile(ShrimpDataFileInterface prawnFile) {
        this.prawnFile = prawnFile;
    }

    /**
     * @return the namedExpressionsMap
     */
    @Override
    public Map<String, ExpressionTreeInterface> getNamedExpressionsMap() {
        return namedExpressionsMap;
    }

    /**
     * @return the namedConstantsMap
     */
    @Override
    public Map<String, ExpressionTreeInterface> getNamedConstantsMap() {
        if (namedConstantsMap == null) {
            this.namedConstantsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
        return namedConstantsMap;
    }

    @Override
    public Map<String, ExpressionTreeInterface> getNamedParametersMap() {
        if (namedParametersMap == null) {
            this.namedParametersMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
        return namedParametersMap;
    }

    /**
     * @return the taskExpressionsOrdered
     */
    @Override
    public List<Expression> getTaskExpressionsOrdered() {
        return taskExpressionsOrdered;
    }

    /**
     * @param changed the changed to set
     */
    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
        SquidProject.setProjectChanged(true);
    }

    /**
     * @return the shrimpFractions
     */
    @Override
    public List<ShrimpFractionExpressionInterface> getShrimpFractions() {
        return shrimpFractions;
    }

    /**
     * @return the referenceMaterialSpots
     */
    @Override
    public List<ShrimpFractionExpressionInterface> getReferenceMaterialSpots() {
        return referenceMaterialSpots;
    }

    /**
     * @return the unknownSpots
     */
    @Override
    public List<ShrimpFractionExpressionInterface> getUnknownSpots() {
        return unknownSpots;
    }

    /**
     * @return the concentrationReferenceMaterialSpots
     */
    @Override
    public List<ShrimpFractionExpressionInterface> getConcentrationReferenceMaterialSpots() {
        return concentrationReferenceMaterialSpots;
    }

    /**
     * @param reportsEngine the reportsEngine to set
     */
    @Override
    public void setReportsEngine(CalamariReportsEngine reportsEngine) {
        this.reportsEngine = reportsEngine;
    }

    /**
     * @return the useCalculated_pdMeanParentEleA
     */
    @Override
    public boolean isUseCalculated_pdMeanParentEleA() {
        return useCalculated_pdMeanParentEleA;
    }

    /**
     * @param useCalculated_pdMeanParentEleA the useCalculated_pdMeanParentEleA
     * to set
     */
    @Override
    public void setUseCalculated_pdMeanParentEleA(boolean useCalculated_pdMeanParentEleA) {
        this.useCalculated_pdMeanParentEleA = useCalculated_pdMeanParentEleA;
    }

    /**
     * @return the selectedIndexIsotope
     */
    @Override
    public IndexIsoptopesEnum getSelectedIndexIsotope() {
        return selectedIndexIsotope;
    }

    /**
     * @param selectedIndexIsotope the selectedIndexIsotope to set
     */
    @Override
    public void setSelectedIndexIsotope(IndexIsoptopesEnum selectedIndexIsotope) {
        this.selectedIndexIsotope = selectedIndexIsotope;
    }

    @Override
    public boolean expressionExists(Expression expression) {
        return taskExpressionsOrdered.contains(expression);
    }

    /**
     * @return the massMinuends
     */
    @Override
    public List<MassStationDetail> getMassMinuends() {
        if (massMinuends == null) {
            massMinuends = new ArrayList<>();
        }
        return massMinuends;
    }

    /**
     * @param massMinuends the massMinuends to set
     */
    @Override
    public void setMassMinuends(List<MassStationDetail> massMinuends) {
        this.massMinuends = massMinuends;
    }

    /**
     * @return the massSubtrahends
     */
    @Override
    public List<MassStationDetail> getMassSubtrahends() {
        if (massSubtrahends == null) {
            massSubtrahends = new ArrayList<>();
        }
        return massSubtrahends;
    }

    /**
     * @param massSubtrahends the massSubtrahends to set
     */
    @Override
    public void setMassSubtrahends(List<MassStationDetail> massSubtrahends) {
        this.massSubtrahends = massSubtrahends;
    }

    /**
     * @return the showTimeNormalized
     */
    @Override
    public boolean isShowTimeNormalized() {
        return showTimeNormalized;
    }

    /**
     * @param showTimeNormalized the showTimeNormalized to set
     */
    @Override
    public void setShowTimeNormalized(boolean showTimeNormalized) {
        this.showTimeNormalized = showTimeNormalized;
    }

    /**
     * @return the showPrimaryBeam
     */
    @Override
    public boolean isShowPrimaryBeam() {
        return showPrimaryBeam;
    }

    /**
     * @param showPrimaryBeam the showPrimaryBeam to set
     */
    @Override
    public void setShowPrimaryBeam(boolean showPrimaryBeam) {
        this.showPrimaryBeam = showPrimaryBeam;
    }

    /**
     * @return the showQt1y
     */
    @Override
    public boolean isShowQt1y() {
        return showQt1y;
    }

    /**
     * @param aShowQt1y the showQt1y to set
     */
    @Override
    public void setShowQt1y(boolean aShowQt1y) {
        showQt1y = aShowQt1y;
    }

    /**
     * @return the showQt1z
     */
    @Override
    public boolean isShowQt1z() {
        return showQt1z;
    }

    /**
     * @param aShowQt1z the showQt1z to set
     */
    @Override
    public void setShowQt1z(boolean aShowQt1z) {
        showQt1z = aShowQt1z;
    }

    @Override
    public boolean expressionIsNuSwitched(String expressionName) {
        boolean retVal = false;
        for (Expression exp : taskExpressionsOrdered) {
            if ((exp.getName().compareToIgnoreCase(expressionName) == 0)
                    && (exp.isSquidSwitchNU())) {
                retVal = true;
                break;
            }
        }
        return retVal;
    }

    /**
     * @return the mapOfUnknownsBySampleNames
     */
    @Override
    public Map<String, List<ShrimpFractionExpressionInterface>> getMapOfUnknownsBySampleNames() {
        if (mapOfUnknownsBySampleNames == null) {
            mapOfUnknownsBySampleNames = new TreeMap<>();
        }
        return mapOfUnknownsBySampleNames;
    }

    /**
     * @param prawnChanged the prawnChanged to set
     */
    @Override
    public void setPrawnChanged(boolean prawnChanged) {
        this.prawnChanged = prawnChanged;
    }

    /**
     * @return the squidAllowsAutoExclusionOfSpots
     */
    @Override
    public boolean isSquidAllowsAutoExclusionOfSpots() {
        return squidAllowsAutoExclusionOfSpots;
    }

    /**
     * @param extPErr the extPErr to set
     */
    @Override
    public void setExtPErr(double extPErr) {
        this.extPErr = extPErr;
        if (namedParametersMap.containsKey(SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR)) {
            ExpressionTreeInterface constant = namedParametersMap.get(SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR);
            ((ConstantNode) constant).setValue(extPErr);
        }
        this.changed = true;
    }

    /**
     * @return the extPErr
     */
    @Override
    public double getExtPErr() {
        return extPErr;
    }

    @Override
    public void setReferenceMaterial(ParametersModel refMat) {
        if (refMat instanceof ReferenceMaterialModel) {
            referenceMaterialModelChanged = !referenceMaterialModel.equals(refMat);
            referenceMaterialModel = (ReferenceMaterialModel) refMat;
        }
    }

    @Override
    public ParametersModel getReferenceMaterialModel() {
        return referenceMaterialModel;
    }

    @Override
    public void setConcentrationReferenceMaterial(ParametersModel refMat) {
        if (refMat instanceof ReferenceMaterialModel) {
            concentrationReferenceMaterialModelChanged = !concentrationReferenceMaterialModel.equals(refMat);
            concentrationReferenceMaterialModel = (ReferenceMaterialModel) refMat;
        }
    }

    @Override
    public ParametersModel getConcentrationReferenceMaterialModel() {
        return concentrationReferenceMaterialModel;
    }

    @Override
    public void setPhysicalConstantsModel(ParametersModel physConst) {
        if (physConst instanceof PhysicalConstantsModel) {
            physicalConstantsModelChanged = !physicalConstantsModel.equals(physConst);
            physicalConstantsModel = (PhysicalConstantsModel) physConst;
        }
    }

    @Override
    public ParametersModel getPhysicalConstantsModel() {
        return physicalConstantsModel;
    }

    @Override
    public void setCommonPbModel(ParametersModel commonPbModel) {
        if (commonPbModel instanceof CommonPbModel) {
            commonPbModelChanged = !this.commonPbModel.equals(commonPbModel);
            this.commonPbModel = (CommonPbModel) commonPbModel;
        }
    }

    @Override
    public ParametersModel getCommonPbModel() {
        return commonPbModel;
    }

}
