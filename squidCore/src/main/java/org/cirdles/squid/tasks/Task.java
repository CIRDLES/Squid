
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import org.cirdles.squid.Squid;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.ConcentrationTypeEnum;
import static org.cirdles.squid.constants.Squid3Constants.ConcentrationTypeEnum.THORIUM;
import static org.cirdles.squid.constants.Squid3Constants.ConcentrationTypeEnum.URANIUM;
import org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum;
import org.cirdles.squid.constants.Squid3Constants.OvercountCorrectionTypes;
import static org.cirdles.squid.constants.Squid3Constants.SpotTypes.UNKNOWN;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GENERAL;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GEOCHRON;
import static org.cirdles.squid.constants.Squid3Constants.XML_HEADER_FOR_SQUIDTASK_FILES_USING_LOCAL_SCHEMA;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.ParametersModelComparator;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFileRunFractionParser;
import org.cirdles.squid.projects.SquidProject;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_COMMON_LEAD_MODEL;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_STACEY_KRAMER;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpDataFileInterface;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;
import org.cirdles.squid.tasks.evaluationEngines.ExpressionEvaluator;
import org.cirdles.squid.tasks.evaluationEngines.TaskExpressionEvaluatedPerSpotPerScanModelInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.AV_PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;

import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWithRatiosInterface;
import org.cirdles.squid.tasks.expressions.functions.WtdMeanACalc;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.utilities.IntuitiveStringComparator;
import org.cirdles.squid.utilities.fileUtilities.PrawnFileUtilities;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.tasks.taskDesign.TaskDesign;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFAULT_BACKGROUND_MASS_LABEL;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.MIN_206PB238U_EXT_1SIGMA_ERR_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.MIN_208PB232TH_EXT_1SIGMA_ERR_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNTS_PERSEC_4_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNT_4_6_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REQUIRED_NOMINAL_MASSES;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REQUIRED_RATIO_NAMES;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_CONCEN_PPM_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_206_238;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_206_238_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_208_232;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_208_232_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.U_CONCEN_PPM_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generateExperimentalExpressions;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generateOverCountExpressions;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generatePerSpotProportionsOfCommonPb;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generatePlaceholderExpressions;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.generatePpmUandPpmTh;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.overCountMeans;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.samRadiogenicCols;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.stdRadiogenicCols;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.updateCommonLeadParameterValuesFromModel;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.updateConcReferenceMaterialValuesFromModel;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.updatePhysicalConstantsParameterValuesFromModel;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.updateReferenceMaterialValuesFromModel;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SampleAgeTypesEnum;
import static org.cirdles.squid.tasks.expressions.constants.ConstantNode.MISSING_EXPRESSION_STRING;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;
import static org.cirdles.squid.utilities.conversionUtilities.CloningUtilities.clone2dArray;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_STACEY_KRAMER_BY_GROUP;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTable;
import static org.cirdles.squid.squidReports.squidReportTables.SquidReportTable.NAME_OF_WEIGHTEDMEAN_PLOT_SORT_REPORT;
import org.cirdles.squid.tasks.expressions.ExpressionSpec;
import static org.cirdles.squid.tasks.expressions.ExpressionSpec.specifyExpression;
import org.cirdles.squid.tasks.expressions.ExpressionSpecInterface;
import org.cirdles.squid.tasks.expressions.ExpressionSpecXMLConverter;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_TH_CONC_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_U_CONC_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory.buildExpression;
import org.cirdles.squid.tasks.expressions.functions.SqWtdAv;

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
    protected String taskSquidVersion;
    protected TaskTypeEnum taskType;
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
    private Map<String, ExpressionTreeInterface> namedSpotLookupFieldsMap;

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

    protected boolean useCalculatedAv_ParentElement_ConcenConst;

    protected IndexIsoptopesEnum selectedIndexIsotope;

    // next 7 fields used to track user's choice of displayed options in mass audits
    protected List<MassStationDetail> massMinuends;
    protected List<MassStationDetail> massSubtrahends;
    protected boolean showTimeNormalized;
    protected boolean showPrimaryBeam;
    protected boolean showQt1y;
    protected boolean showQt1z;
    protected boolean showSpotLabels;

    protected boolean squidAllowsAutoExclusionOfSpots;

    // MIN_206PB238U_EXT_1SIGMA_ERR_PCT
    protected double extPErrU;
    // MIN_208PB232TH_EXT_1SIGMA_ERR_PCT
    protected double extPErrTh;

    protected ParametersModel physicalConstantsModel;
    protected ParametersModel referenceMaterialModel;
    protected ParametersModel commonPbModel;
    protected ParametersModel concentrationReferenceMaterialModel;

    protected boolean physicalConstantsModelChanged;
    protected boolean referenceMaterialModelChanged;
    protected boolean commonPbModelChanged;
    protected boolean concentrationReferenceMaterialModelChanged;

    protected Map<String, String> specialSquidFourExpressionsMap;

    protected String delimiterForUnknownNames;

    protected ConcentrationTypeEnum concentrationTypeEnum;

    protected Map<String, List<String>> providesExpressionsGraph;
    protected Map<String, List<String>> requiresExpressionsGraph;
    protected List<String> missingExpressionsByName;

    protected boolean roundingForSquid3;

    protected List<SquidReportTableInterface> squidReportTablesRefMat;
    protected List<SquidReportTableInterface> squidReportTablesUnknown;
    protected SquidReportTableInterface selectedRefMatReportModel;
    protected SquidReportTableInterface selectedUnknownReportModel;

    protected OvercountCorrectionTypes overcountCorrectionType;

    // Sept 2020 to support stickiness of choices for the plot any 2 feature of interpretations
    protected String xAxisExpressionName;
    protected String yAxisExpressionName;

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
        TaskDesign taskDesign = SquidPersistentState.getExistingPersistentState().getTaskDesign();
        this.prawnFile = prawnFile;
        this.reportsEngine = reportsEngine;

        SquidProject squidProject = null;
        if (reportsEngine != null) {
            squidProject = reportsEngine.getSquidProject();
        }

        this.name = name;
        this.taskSquidVersion = Squid.VERSION;
        this.taskType = taskDesign.getTaskType();
        this.description = "";
        this.authorName = taskDesign.getAuthorName();
        this.labName = taskDesign.getLabName();
        this.provenance = "";
        this.dateRevised = 0l;
        this.filterForRefMatSpotNames = "";
        this.filterForConcRefMatSpotNames = "";
        this.filtersForUnknownNames = new HashMap<>();

        this.indexOfBackgroundSpecies = -1;
        this.indexOfTaskBackgroundMass = -1;
        this.parentNuclide = taskDesign.getParentNuclide();//  "238";
        this.directAltPD = taskDesign.isDirectAltPD();//  false;

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
        this.namedSpotLookupFieldsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        this.shrimpFractions = new ArrayList<>();
        this.referenceMaterialSpots = new ArrayList<>();
        this.concentrationReferenceMaterialSpots = new ArrayList<>();
        this.unknownSpots = new ArrayList<>();

        this.changed = true;
        SquidProject.setProjectChanged(true);

        this.useCalculatedAv_ParentElement_ConcenConst = false;

        this.massMinuends = new ArrayList<>();
        this.massSubtrahends = new ArrayList<>();
        this.showTimeNormalized = true; // in honor of Nicole Rayner's normal work flow
        this.showSpotLabels = false;

        this.showPrimaryBeam = false;
        this.showQt1y = false;
        this.showQt1z = false;

        this.prawnChanged = false;

        this.specialSquidFourExpressionsMap = taskDesign.getSpecialSquidFourExpressionsMap();
        if (squidProject != null) {
            this.useSBM = squidProject.isUseSBM();
            this.userLinFits = squidProject.isUserLinFits();
            this.selectedIndexIsotope = squidProject.getSelectedIndexIsotope();
            this.squidAllowsAutoExclusionOfSpots = squidProject.isSquidAllowsAutoExclusionOfSpots();
            this.extPErrU = squidProject.getExtPErrU();
            this.extPErrTh = squidProject.getExtPErrTh();
            this.physicalConstantsModel = squidProject.getPhysicalConstantsModel();
            this.commonPbModel = squidProject.getCommonPbModel();
            this.delimiterForUnknownNames = squidProject.getDelimiterForUnknownNames();
        } else {
            this.useSBM = taskDesign.isUseSBM();
            this.userLinFits = taskDesign.isUserLinFits();
            this.selectedIndexIsotope = taskDesign.getSelectedIndexIsotope();
            this.squidAllowsAutoExclusionOfSpots = taskDesign.isSquidAllowsAutoExclusionOfSpots();
            this.extPErrU = taskDesign.getExtPErrU();
            this.extPErrTh = taskDesign.getExtPErrTh();
            this.physicalConstantsModel = taskDesign.getPhysicalConstantsModel();
            this.commonPbModel = taskDesign.getCommonPbModel();
            this.delimiterForUnknownNames = taskDesign.getDelimiterForUnknownNames();
        }

        this.referenceMaterialModel = new ReferenceMaterialModel();
        this.concentrationReferenceMaterialModel = new ReferenceMaterialModel();

        this.physicalConstantsModelChanged = false;
        this.referenceMaterialModelChanged = false;
        this.commonPbModelChanged = false;
        this.concentrationReferenceMaterialModelChanged = false;

        this.concentrationTypeEnum = URANIUM;

        this.providesExpressionsGraph = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.requiresExpressionsGraph = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.missingExpressionsByName = new ArrayList<>();

        this.roundingForSquid3 = false;

        this.squidReportTablesRefMat = new ArrayList<>();
        this.squidReportTablesUnknown = new ArrayList<>();
        this.selectedRefMatReportModel = null;
        this.selectedUnknownReportModel = null;

        this.overcountCorrectionType = OvercountCorrectionTypes.NONE;

        generateConstants();
        generateParameters();
        generateSpotLookupFields();
    }

    public boolean synchronizeTaskVersion() {
        boolean retVal = false;
        if (taskSquidVersion == null) {
            taskSquidVersion = "0.0.0";
        }
        if (taskSquidVersion.compareTo(Squid.VERSION) != 0) {
            taskSquidVersion = Squid.VERSION;
            updateTask();
            retVal = true;
        }

        return retVal;
    }

    public void saveTaskToXML(File taskFileXML) {
        serializeXMLObject(taskFileXML.getAbsolutePath());
    }

    public List<ParametersModel> verifySquidLabDataParameters() {
        List<ParametersModel> retVal = new ArrayList<>(3);

        ParametersModel refMat = getReferenceMaterialModel();
        ParametersModel refMatConc = getConcentrationReferenceMaterialModel();
        ParametersModel physConst = getPhysicalConstantsModel();
        ParametersModel commonPbMod = getCommonPbModel();

        SquidLabData squidLabData = SquidLabData.getExistingSquidLabData();

        if (physConst == null) {
            setPhysicalConstantsModel(squidLabData.getPhysConstDefault());
        } else if (!squidLabData.getPhysicalConstantsModels().contains(physConst)) {
            squidLabData.addPhysicalConstantsModel(physConst);
            retVal.add(physConst);
            squidLabData.getPhysicalConstantsModels().sort(new ParametersModelComparator());
        }

        if (refMat == null) {
            setReferenceMaterialModel(new ReferenceMaterialModel());
        } else if (!squidLabData.getReferenceMaterials().contains(refMat)) {
            squidLabData.addReferenceMaterial(refMat);
            retVal.add(refMat);
            squidLabData.getReferenceMaterials().sort(new ParametersModelComparator());
        }

        if (refMatConc == null) {
            setConcentrationReferenceMaterialModel(new ReferenceMaterialModel());
        } else if (!squidLabData.getReferenceMaterials().contains(refMatConc)) {
            squidLabData.addReferenceMaterial(refMatConc);
            retVal.add(refMatConc);
            squidLabData.getReferenceMaterials().sort(new ParametersModelComparator());
        }

        if (commonPbMod == null) {
            setCommonPbModel(squidLabData.getCommonPbDefault());
        } else if (!squidLabData.getCommonPbModels().contains(commonPbMod)) {
            squidLabData.addcommonPbModel(commonPbMod);
            retVal.add(commonPbMod);
            squidLabData.getCommonPbModels().sort(new ParametersModelComparator());
        }

        squidLabData.storeState();

        return retVal;
    }

    /**
     *
     * @param taskDesign the value of taskDesign
     * @param taskSkeleton the value of taskSkeleton
     */
    @Override
    public void updateTaskFromTaskDesign(TaskDesign taskDesign, boolean taskSkeleton) {

        Method[] gettersAndSetters = taskDesign.getClass().getMethods();

        for (int i = 0; i < gettersAndSetters.length; i++) {
            String methodName = gettersAndSetters[i].getName();

            try {
                if (methodName.startsWith("get") && !methodName.contains("Class")) {
                    this.getClass().getMethod(
                            methodName.replaceFirst("get", "set"),
                            gettersAndSetters[i].getReturnType()).invoke(this, gettersAndSetters[i].invoke(taskDesign, new Object[0]));
                } else if (methodName.startsWith("is")) {
                    this.getClass().getMethod(
                            methodName.replaceFirst("is", "set"),
                            gettersAndSetters[i].getReturnType()).invoke(this, gettersAndSetters[i].invoke(taskDesign, new Object[0]));
                }
            } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                System.out.println(">>>  " + methodName + "     " + e.getMessage());
            }
        }

        // need to remove stored expression results on fractions to clear the decks
        shrimpFractions.forEach((spot) -> {
            spot.getTaskExpressionsForScansEvaluated().clear();
            spot.getTaskExpressionsEvaluationsPerSpot().clear();
        });

        boolean amGeochronMode = (taskType.equals(TaskTypeEnum.GEOCHRON));
        List<String> allMasses = new ArrayList<>();
        if (amGeochronMode) {
            allMasses.addAll(REQUIRED_NOMINAL_MASSES);
        }
        allMasses.addAll(nominalMasses);
        nominalMasses = allMasses;
        Collections.sort(nominalMasses);

        nominalMasses.remove(DEFAULT_BACKGROUND_MASS_LABEL);
        indexOfTaskBackgroundMass = indexOfBackgroundSpecies;

        List<String> allRatios = new ArrayList<>();
        if (amGeochronMode) {
            allRatios.addAll(REQUIRED_RATIO_NAMES);
        }
        allRatios.addAll(ratioNames);
        ratioNames = allRatios;
        Collections.sort(ratioNames);

        setChanged(true);
        if (!taskSkeleton) {
            squidSpeciesModelList.clear();
            buildSquidSpeciesModelList();
            generateConstants();
            generateParameters();
            generateSpotLookupFields();
        }

        for (Expression customExp : taskDesign.getCustomTaskExpressions()) {
            taskExpressionsOrdered.add(customExp);
        }

        if (!taskSkeleton) {
            initializeTaskAndReduceData(false);
        }
    }

    /**
     *
     * @param taskDesign the value of taskDesign
     * @param includeCustomExp the value of includeCustomExp
     */
    @Override
    public void updateTaskDesignFromTask(TaskDesign taskDesign, boolean includeCustomExp) {

        Method[] gettersAndSetters = taskDesign.getClass().getMethods();

        for (int i = 0; i < gettersAndSetters.length; i++) {
            String methodName = gettersAndSetters[i].getName();
            try {
                if (methodName.startsWith("get") && !methodName.contains("Class")) {
                    Method methSetPref = taskDesign.getClass().getMethod(
                            methodName.replaceFirst("get", "set"),
                            gettersAndSetters[i].getReturnType());
                    Method methGetTask = this.getClass().getMethod(
                            methodName);
                    methSetPref.invoke(taskDesign, methGetTask.invoke(this, new Object[0]));
                } else if (methodName.startsWith("is")) {
                    Method methSetPref = taskDesign.getClass().getMethod(
                            methodName.replaceFirst("is", "set"),
                            gettersAndSetters[i].getReturnType());
                    Method methGetTask = this.getClass().getMethod(
                            methodName);
                    methSetPref.invoke(taskDesign, methGetTask.invoke(this, new Object[0]));
                }
            } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                System.out.println(">>>  " + methodName + "     " + e.getMessage());
            }
        }

        // housekeeping
        List<String> myNominalMasses = new ArrayList<>();
        myNominalMasses.addAll(nominalMasses);
        myNominalMasses.removeAll(REQUIRED_NOMINAL_MASSES);
        taskDesign.setNominalMasses(myNominalMasses);

        List<String> myRatioNames = new ArrayList<>();
        myRatioNames.addAll(ratioNames);
        myRatioNames.removeAll(REQUIRED_RATIO_NAMES);
        taskDesign.setRatioNames(myRatioNames);

        if (includeCustomExp) {
            taskDesign.setCustomTaskExpressions(getCustomTaskExpressions());
        } else {
            taskDesign.setCustomTaskExpressions(new ArrayList<>());
        }

    }

    /**
     *
     * @param autoGenerateNominalMasses the value of autoGenerateNominalMasses
     */
    public void initializeTaskAndReduceData(boolean autoGenerateNominalMasses) {

        // four passes needed for percolating results
        updateAllExpressions(true);
        setChanged(true);
        setupSquidSessionSpecsAndReduceAndReport(false);

        // autogenerate task basics for type General = Ratio mode
        if (autoGenerateNominalMasses && taskType.equals(GENERAL)) {
            List<String> nominalMasses = new ArrayList<>();
            for (SquidSpeciesModel ssm : getSquidSpeciesModelList()) {
                // no background assumed
                String proposedNominalMassName
                        = new BigDecimal(getMapOfIndexToMassStationDetails()
                                .get(ssm.getMassStationIndex())
                                .getIsotopeAMU()).setScale(1, RoundingMode.HALF_UP).toPlainString();
                nominalMasses.add(proposedNominalMassName);
            }
            setNominalMasses(nominalMasses);

            initializeSquidSpeciesModelsRatioMode(true, false, true, false);
        }
    }

    private void generateConstants() {
        Map<String, ExpressionTreeInterface> constants = BuiltInExpressionsFactory.generateConstants();
        this.namedConstantsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        namedConstantsMap.putAll(constants);
    }

    private void generateParameters() {
        Map<String, ExpressionTreeInterface> parameters = BuiltInExpressionsFactory.generateParameters();
        this.namedParametersMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        namedParametersMap.putAll(parameters);
    }

    private void generateSpotLookupFields() {
        Map<String, ExpressionTreeInterface> spotLookupFields = BuiltInExpressionsFactory.generateSpotLookupFields();
        this.namedSpotLookupFieldsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        namedSpotLookupFieldsMap.putAll(spotLookupFields);
    }

    @Override
    public void generateBuiltInExpressions() {

        SortedSet<Expression> generateReferenceMaterialValues = updateReferenceMaterialValuesFromModel((ReferenceMaterialModel) referenceMaterialModel);
        taskExpressionsOrdered.addAll(generateReferenceMaterialValues);

        SortedSet<Expression> generateConcReferenceMaterialValues = updateConcReferenceMaterialValuesFromModel((ReferenceMaterialModel) concentrationReferenceMaterialModel);
        taskExpressionsOrdered.addAll(generateConcReferenceMaterialValues);

        SortedSet<Expression> generateCommonLeadParameterValues = updateCommonLeadParameterValuesFromModel(commonPbModel);
        taskExpressionsOrdered.addAll(generateCommonLeadParameterValues);

        SortedSet<Expression> generatePhysicalConstantsValues = updatePhysicalConstantsParameterValuesFromModel((PhysicalConstantsModel) physicalConstantsModel);
        taskExpressionsOrdered.addAll(generatePhysicalConstantsValues);

        if (taskType.equals(GEOCHRON)) {
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
            // March 2019
            if (specialSquidFourExpressionsMap.get(PARENT_ELEMENT_CONC_CONST).matches("(.*)(232|248|264)(.*)")) {
                concentrationTypeEnum = THORIUM;
            } else {
                concentrationTypeEnum = URANIUM;
            }
            SortedSet<Expression> perSpotConcentrations = generatePpmUandPpmTh(parentNuclide, isDirectAltPD(), concentrationTypeEnum);
            taskExpressionsOrdered.addAll(perSpotConcentrations);

            SortedSet<Expression> overCountMeansRefMaterials = overCountMeans();
            taskExpressionsOrdered.addAll(overCountMeansRefMaterials);

            SortedSet<Expression> stdRadiogenicCols = stdRadiogenicCols(parentNuclide, isDirectAltPD());
            taskExpressionsOrdered.addAll(stdRadiogenicCols);

            SortedSet<Expression> samRadiogenicCols = samRadiogenicCols(parentNuclide, isDirectAltPD());
            taskExpressionsOrdered.addAll(samRadiogenicCols);
        }
        // uses complex comparator in expression
        Collections.sort(taskExpressionsOrdered);
    }

    @Override
    public void generateMapOfUnknownsBySampleNames() {
        Comparator<String> intuitiveStringComparator = new IntuitiveStringComparator<>();
        mapOfUnknownsBySampleNames = new TreeMap<>(intuitiveStringComparator);
        // walk chosen sample names (excluding reference materials) and get list of spots belonging to each
        // first put full set
        mapOfUnknownsBySampleNames.put(Squid3Constants.SpotTypes.UNKNOWN.getPlotType(), unknownSpots);
        for (String sampleName : filtersForUnknownNames.keySet()) {
            List<ShrimpFractionExpressionInterface> filteredList
                    = unknownSpots.stream()
                            .filter(spot -> spot.getFractionID().toUpperCase(Locale.ENGLISH).startsWith(sampleName.toUpperCase(Locale.ENGLISH)))
                            .collect(Collectors.toList());
            if (filteredList.size() > 0) {
                mapOfUnknownsBySampleNames.put(sampleName, filteredList);
                // provide 1-based time index within unknown group
                for (int i = 0; i < filteredList.size(); i++) {
                    ((ShrimpFraction) filteredList.get(i)).setSpotIndex(i + 1);
                }
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
        StringBuilder summary = new StringBuilder();

        summary.append(" ")
                .append("Data Source File provides ")
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
                .append(" Masses matching Species found in Data file:");

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
        if (taskExpressionsEvaluationsPerSpotSet.get(AV_PARENT_ELEMENT_CONC_CONST) != null) {
            meanConcValue = String.valueOf(taskExpressionsEvaluationsPerSpotSet.get(AV_PARENT_ELEMENT_CONC_CONST).getValues()[0][0]);
        }
        summary.append("\n ")
                .append(String.valueOf(concentrationReferenceMaterialSpots.size()))
                .append(" Concentration Reference Material Spots extracted by filter: \"")
                .append(filterForConcRefMatSpotNames)
                .append("\".\n\t\t  Mean Concentration of Primary Parent Element ")
                .append(concentrationTypeEnum.getName())
                .append(" = ")
                .append(meanConcValue)
                .append(" ppm.");

        summary.append("\n ")
                .append(String.valueOf(unknownSpots.size()))
                .append(" Unknown Spots");

        if (mapOfUnknownsBySampleNames.size() <= 1) {
            summary.append(". Individual samples not yet identified - see Data Menu");
        } else {
            summary.append(", organized into these samples:");
        }

        for (String sampleName : mapOfUnknownsBySampleNames.keySet()) {
            if (sampleName.compareTo(UNKNOWN.getPlotType()) != 0) {
                summary.append("\n\t")
                        .append("\"")
                        .append(sampleName)
                        .append("\"")
                        .append("\t with ")
                        .append(mapOfUnknownsBySampleNames.get(sampleName).size())
                        .append(" spot").append(mapOfUnknownsBySampleNames.get(sampleName).size() > 1 ? "s" : "");
            }
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
        summary.append("\n\t UnHealthy / Mismatched targets: ");
        summary.append((String) ((taskExpressionsOrdered.size() - count) > 0
                ? String.valueOf(taskExpressionsOrdered.size() - count) : "None")).append(" excluded.");

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

    public void prepareParametersAndRatios() {
        updateParametersFromModels();

        buildSquidSpeciesModelList();

        prepareRatios();
    }

    public void prepareRatios() {

        populateTableOfSelectedRatiosFromRatiosList();

        buildSquidRatiosModelListFromMassStationDetails();
    }

    /**
     *
     * @param forceReprocess the value of forceReprocess
     */
    @Override
    public void setupSquidSessionSpecsAndReduceAndReport(boolean forceReprocess) {

        if (changed) {

            prepareParametersAndRatios();

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

            if (requiresChanges || prawnChanged || forceReprocess) {
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
                    updateAllSpotsWithCurrentCommonPbModel();
                } catch (Exception e) {
                }
            }

            generateMapOfUnknownsBySampleNames();

            processAndSortExpressions();

            evaluateTaskExpressions();

            buildExpressionDependencyGraphs();

            reportsEngine.clearReports();

            prawnChanged = false;
            changed = false;
        }
    }

    /**
     *
     * @param refreshCommonLeadModel the value of refreshCommonLeadModel
     * @param refreshPhysicalConstantsModel the value of
     * refreshPhysicalConstantsModel
     * @param refreshReferenceMaterialsModel the value of
     * refreshReferenceMaterialsModel
     */
    @Override
    public void refreshParametersFromModels(
            boolean refreshCommonLeadModel,
            boolean refreshPhysicalConstantsModel,
            boolean refreshReferenceMaterialsModel) {

        List<ParametersModel> models = null;

        if (refreshCommonLeadModel) {
            models = SquidLabData.getExistingSquidLabData().getCommonPbModels();
            commonPbModel = findModelByName(models, commonPbModel);
            commonPbModelChanged = true;
        }

        if (refreshPhysicalConstantsModel) {
            models = SquidLabData.getExistingSquidLabData().getPhysicalConstantsModels();
            physicalConstantsModel = findModelByName(models, physicalConstantsModel);
            physicalConstantsModelChanged = true;
        }

        if (refreshReferenceMaterialsModel) {
            models = SquidLabData.getExistingSquidLabData().getReferenceMaterials();
            referenceMaterialModel = findModelByName(models, referenceMaterialModel);
            referenceMaterialModelChanged = true;

            concentrationReferenceMaterialModel = findModelByName(models, concentrationReferenceMaterialModel);
            concentrationReferenceMaterialModelChanged = true;
        }

        updateParametersFromModels();
    }

    private ParametersModel findModelByName(List<ParametersModel> models, ParametersModel model) {
        ParametersModel retVal = model;
        for (ParametersModel pm : models) {
            if (pm.equals(model)) {
                retVal = pm;
                break;
            }
        }

        return retVal;
    }

    @Override
    public void updateAllSpotsWithCurrentCommonPbModel() {
        for (ShrimpFractionExpressionInterface spot : shrimpFractions) {
            spot.setCommonLeadModel(commonPbModel);
        }
    }

    public void updateAllUnknownSpotsWithOriginal204_206() {
        for (ShrimpFractionExpressionInterface spot : unknownSpots) {
            SquidRatiosModel ratio204_206 = ((ShrimpFraction) spot).getRatioByName("204/206");
            ratio204_206.restoreRatioValueAndUnct();
        }
    }

    public void updateAllUnknownSpotsWithOverCountCorrectedBy204_206_207() {
        ExpressionTreeInterface countCorrectionExpression204From207 = namedExpressionsMap.get("CountCorrectionExpression204From207");
        for (ShrimpFractionExpressionInterface spot : unknownSpots) {
            SquidRatiosModel ratio204_206 = ((ShrimpFraction) spot).getRatioByName("204/206");
            double[][] r204_206_207 = spot.getTaskExpressionsEvaluationsPerSpot().get(countCorrectionExpression204From207);
            ratio204_206.setRatioValUsed(r204_206_207[0][0]);
            ratio204_206.setRatioFractErrUsed(r204_206_207[0][1]);
        }
    }

    public void updateAllUnknownSpotsWithOverCountCorrectedBy204_206_208() {
        ExpressionTreeInterface countCorrectionExpression204From208 = namedExpressionsMap.get("CountCorrectionExpression204From208");
        for (ShrimpFractionExpressionInterface spot : unknownSpots) {
            SquidRatiosModel ratio204_206 = ((ShrimpFraction) spot).getRatioByName("204/206");
            double[][] r204_206_207 = spot.getTaskExpressionsEvaluationsPerSpot().get(countCorrectionExpression204From208);
            ratio204_206.setRatioValUsed(r204_206_207[0][0]);
            ratio204_206.setRatioFractErrUsed(r204_206_207[0][1]);
        }
    }

    public void updateParametersFromModels() {
        boolean doUpdateAll
                = commonPbModelChanged || physicalConstantsModelChanged || referenceMaterialModelChanged || concentrationReferenceMaterialModelChanged;

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
            // Sept 2019: assign values to individual spots
            updateAllSpotsWithCurrentCommonPbModel();

            commonPbModelChanged = false;
        }

        if (physicalConstantsModelChanged) {
            SortedSet<Expression> updatedPhysicalConstantsExpressions
                    = BuiltInExpressionsFactory.updatePhysicalConstantsParameterValuesFromModel((PhysicalConstantsModel) physicalConstantsModel);
            Iterator<Expression> updatedPhysicalConstantsExpressionsIterator = updatedPhysicalConstantsExpressions.iterator();
            while (updatedPhysicalConstantsExpressionsIterator.hasNext()) {
                Expression exp = updatedPhysicalConstantsExpressionsIterator.next();
                removeExpression(exp, false);
                addExpression(exp, false);
                updateAffectedExpressions(exp, false);
            }
            physicalConstantsModelChanged = false;
        }

        if (referenceMaterialModelChanged) {
            SortedSet<Expression> updatedReferenceMaterialExpressions
                    = BuiltInExpressionsFactory.updateReferenceMaterialValuesFromModel((ReferenceMaterialModel) referenceMaterialModel);
            Iterator<Expression> updatedReferenceMaterialExpressionsIterator = updatedReferenceMaterialExpressions.iterator();
            while (updatedReferenceMaterialExpressionsIterator.hasNext()) {
                Expression exp = updatedReferenceMaterialExpressionsIterator.next();
                removeExpression(exp, false);
                addExpression(exp, false);
            }
            referenceMaterialModelChanged = false;
        }

        if (concentrationReferenceMaterialModelChanged) {
            // sept 2020 per nicole rayner, if no concentration model, 
            // or either value is zero, kill the expressions with zeroes
            Expression expRemove = getExpressionByName(REF_U_CONC_PPM);
            removeExpression(expRemove, false);
            expRemove = getExpressionByName(REF_TH_CONC_PPM);
            removeExpression(expRemove, false);

            SortedSet<Expression> updatedConcReferenceMaterialExpressions
                    = BuiltInExpressionsFactory.updateConcReferenceMaterialValuesFromModel((ReferenceMaterialModel) concentrationReferenceMaterialModel);
            Iterator<Expression> updatedConcReferenceMaterialExpressionsIterator = updatedConcReferenceMaterialExpressions.iterator();
            while (updatedConcReferenceMaterialExpressionsIterator.hasNext()) {
                Expression exp = updatedConcReferenceMaterialExpressionsIterator.next();
                addExpression(exp, false);
            }
            concentrationReferenceMaterialModelChanged = false;
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
        setupSquidSessionSpecsAndReduceAndReport(false);
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
    public File producePerScanReportsToFiles() {
        File retVal = null;
        if (unknownSpots.size() > 0) {
            try {
                retVal = reportsEngine.produceReports(shrimpFractions,
                        (ShrimpFraction) unknownSpots.get(0),
                        referenceMaterialSpots.size() > 0
                        ? (ShrimpFraction) referenceMaterialSpots.get(0) : (ShrimpFraction) unknownSpots.get(0),
                        true, false);
            } catch (IOException iOException) {
            }
        }
        return retVal;
    }

    private void reorderExpressions() {
        // cannot depend on comparator to do deep compares
        for (Expression listedExp : taskExpressionsOrdered) {
            // handle selected isotope-specific expressions
            // TODO: Better logic - selfaware expressionTree or polymorphism
            String IsotopeCorrectionPrefixString = selectedIndexIsotope.getIsotopeCorrectionPrefixString();
            if (directAltPD) {
                if (listedExp.getName().compareToIgnoreCase(TH_U_EXP_RM) == 0) {
                    listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + TH_U_EXP_RM);
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }
                if (listedExp.getName().compareToIgnoreCase(TH_U_EXP) == 0) {
                    listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + TH_U_EXP);
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSAUnknownCalculation(true);
                }
                if (listedExp.getName().compareToIgnoreCase(U_CONCEN_PPM_RM) == 0) {
                    listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + U_CONCEN_PPM_RM);
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }
                if (listedExp.getName().compareToIgnoreCase(TH_CONCEN_PPM_RM) == 0) {
                    listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + TH_CONCEN_PPM_RM);
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }
                if (listedExp.getName().compareToIgnoreCase(OVER_COUNT_4_6_8) == 0) {
                    listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + OVER_COUNT_4_6_8);
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }
                if (listedExp.getName().compareToIgnoreCase(OVER_COUNTS_PERSEC_4_8) == 0) {
                    listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + OVER_COUNTS_PERSEC_4_8);
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }
                if (listedExp.getName().compareToIgnoreCase(CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT) == 0) {
                    listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT);
                    listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                    listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                }
            }
            if (listedExp.getName().compareToIgnoreCase(TOTAL_206_238_RM) == 0) {
                listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + TOTAL_206_238_RM);
                listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
            }
            if (listedExp.getName().compareToIgnoreCase(TOTAL_206_238) == 0) {
                listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + TOTAL_206_238);
                listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                listedExp.getExpressionTree().setSquidSwitchSAUnknownCalculation(true);
            }
            if (listedExp.getName().compareToIgnoreCase(TOTAL_208_232_RM) == 0) {
                listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + TOTAL_208_232_RM);
                listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
                listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
            }
            if (listedExp.getName().compareToIgnoreCase(TOTAL_208_232) == 0) {
                listedExp.setExcelExpressionString(IsotopeCorrectionPrefixString + TOTAL_208_232);
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
            taskExpressionsOrdered.add(listedExp);
        }
    }

    /**
     *
     */
    @Override
    public void processAndSortExpressions() {
        assembleNamedExpressionsMap();
        reorderExpressions();
        buildExpressions();
    }

    /**
     *
     * @param sourceExpression
     * @param reprocessExpressions
     */
    @Override
    public void updateAffectedExpressions(Expression sourceExpression, boolean reprocessExpressions) {
        for (Expression listedExp : taskExpressionsOrdered) {
            if (listedExp.getExpressionTree().usesAnotherExpression(sourceExpression.getExpressionTree())) {
                updateSingleExpression(listedExp);
            }
        }

        setChanged(true);
        if (reprocessExpressions) {
            setupSquidSessionSpecsAndReduceAndReport(false);
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
            setupSquidSessionSpecsAndReduceAndReport(false);
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
            listedExp.getExpressionTree().setUnknownsGroupSampleName(original.getUnknownsGroupSampleName());
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
            if (namedExpressionsMap.containsKey(expression.getName())) {
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
                setChanged(reprocessExpressions);
                if (reprocessExpressions) {
                    setupSquidSessionSpecsAndReduceAndReport(false);
                }
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
        setChanged(reprocessExpressions);
        if (reprocessExpressions) {
            setupSquidSessionSpecsAndReduceAndReport(false);
        } else {
            namedExpressionsMap.put(exp.getName(), exp.getExpressionTree());
            buildExpressionDependencyGraphs();
            evaluateTaskExpression(exp.getExpressionTree());
            List<String> evaluated = new ArrayList<>();
            evaluated.add(exp.getName());
            evaluateDependentExpressions(evaluated, exp.getName());
        }
    }

    private void evaluateDependentExpressions(List<String> evaluated, String expressionName) {
        List<String> providedTo = providesExpressionsGraph.get(expressionName);
        if (providedTo != null) {
            for (String providedToName : providedTo) {
                if (!evaluated.contains(providedToName)) {
                    evaluated.add(providedToName);
                    evaluateTaskExpression(getExpressionByName(providedToName).getExpressionTree());
                    evaluateDependentExpressions(evaluated, providedToName);
                }
            }
        }
    }

    private void createMapOfIndexToMassStationDetails() {
        if (prawnFile != null) {
            mapOfIndexToMassStationDetails
                    = PrawnFileUtilities.createMapOfIndexToMassStationDetails(indexOfBackgroundSpecies, prawnFile.getRun());
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

    /**
     * Builds a custom expression from a specification and adds it to task
     *
     * @param expressionSpec
     */
    public void makeCustomExpression(ExpressionSpecInterface expressionSpec) {
        Expression expression
                = generateExpressionFromRawExcelStyleText(
                        expressionSpec.getExpressionName(),
                        expressionSpec.getExcelExpressionString(),
                        expressionSpec.isSquidSwitchNU(),
                        false, false);

        expression.setNotes(expressionSpec.getNotes());

        ExpressionTreeInterface expressionTree = expression.getExpressionTree();
        expressionTree.setSquidSwitchSTReferenceMaterialCalculation(expressionSpec.isSquidSwitchSTReferenceMaterialCalculation());
        expressionTree.setSquidSwitchSAUnknownCalculation(expressionSpec.isSquidSwitchSAUnknownCalculation());
        expressionTree.setSquidSwitchConcentrationReferenceMaterialCalculation(expressionSpec.isSquidSwitchConcentrationReferenceMaterialCalculation());

        expressionTree.setSquidSwitchSCSummaryCalculation(expressionSpec.isSquidSwitchSCSummaryCalculation());
        expressionTree.setSquidSpecialUPbThExpression(expressionSpec.isSquidSpecialUPbThExpression());

        taskExpressionsOrdered.add(expression);
    }

    @Override
    public void updateRefMatCalibConstWMeanExpressions(boolean squidAllowsAutoExclusionOfSpots) {

        updateRefMatCalibConstWMeanExpression(PB4COR206_238CALIB_CONST_WM, squidAllowsAutoExclusionOfSpots);
        updateRefMatCalibConstWMeanExpression(PB7COR206_238CALIB_CONST_WM, squidAllowsAutoExclusionOfSpots);
        updateRefMatCalibConstWMeanExpression(PB8COR206_238CALIB_CONST_WM, squidAllowsAutoExclusionOfSpots);
        updateRefMatCalibConstWMeanExpression(PB4COR208_232CALIB_CONST_WM, squidAllowsAutoExclusionOfSpots);
        updateRefMatCalibConstWMeanExpression(PB7COR208_232CALIB_CONST_WM, squidAllowsAutoExclusionOfSpots);

        this.squidAllowsAutoExclusionOfSpots = squidAllowsAutoExclusionOfSpots;
        changed = true;
        setupSquidSessionSpecsAndReduceAndReport(false);
    }

    private void updateRefMatCalibConstWMeanExpression(String wmWxpressionName, boolean squidAllowsAutoExclusionOfSpots) {
        Expression wmExp = getExpressionByName(wmWxpressionName);
        if (wmExp != null) {
            wmExp.setExcelExpressionString(wmExp.getExcelExpressionString()
                    .replaceFirst("\\s*,\\s*(TRUE|FALSE)\\s*,\\s*",
                            "," + String.valueOf(!squidAllowsAutoExclusionOfSpots).toUpperCase(Locale.ENGLISH) + ","));
            completeUpdateRefMatCalibConstWMeanExpressions(wmExp);
        }
    }

    private void completeUpdateRefMatCalibConstWMeanExpressions(Expression listedExp) {
        listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);
        listedExp.getExpressionTree().setSquidSpecialUPbThExpression(true);
        listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
        listedExp.getExpressionTree().setSquidSwitchSCSummaryCalculation(true);

        // Aug 2018 change logic to clear the spots list now that task manager has checkbox for auto reject
        SpotSummaryDetails spotSummaryDetails = taskExpressionsEvaluationsPerSpotSet.get(listedExp.getName());
        if (spotSummaryDetails != null) {
            spotSummaryDetails.rejectNone();
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
                            massStationDetail.updateTaskIsotopeLabelForBackground(nominalMasses.get(index));
                        } else {
                            try {
                                massStationDetail.setTaskIsotopeLabel(nominalMasses.get(index));
                            } catch (Exception e) {
                                //TODO: more robust handling for tasks without enough masses
                            }
                        }
                        index++;
                    }

                    massStationDetail.setIsBackground(ssm.getIsBackground());
                    if (ssm.getIsBackground()) {
                        indexOfBackgroundSpecies = massStationDetail.getMassStationIndex();
                        indexOfTaskBackgroundMass = indexOfBackgroundSpecies;
                        ssm.setNumeratorRole(false);
                        ssm.setDenominatorRole(false);
                    }

                    massStationDetail.setuThBearingName(ssm.getuThBearingName());

                    massStationDetail.setViewedAsGraph(ssm.isViewedAsGraph());

                    massStationDetail.setNumeratorRole(ssm.isNumeratorRole());
                    massStationDetail.setDenominatorRole(ssm.isDenominatorRole());
                }
            } else {
                buildSquidSpeciesModelListFromMassStationDetails();
                // TODO: July 2018 not a priority          alignTaskMassStationsWithPrawnFile();
            }
        }
    }

    @Override
    public void applyTaskIsotopeLabelsToMassStationsAndUpdateTask() {
        applyTaskIsotopeLabelsToMassStations();
        updateTask();
    }

    private void updateTask() {
        changed = true;

        // Sept 2020 - need to comb out bad ratios accidentally introduced
        List<String> badRatios = new ArrayList<>();
        for (String ratio : ratioNames) {
            if (ratio.toUpperCase(Locale.ENGLISH).contains("BKG")) {
                badRatios.add(ratio);
            }
        }
        for (String ratio : badRatios) {
            ratioNames.remove(ratio);
        }

        applyDirectives();

        //ensure metadata fields for spots up to date to power sorting at Interpretations menu
        try {
            ExpressionTreeInterface hoursExp = namedSpotLookupFieldsMap.get("Hours");
            evaluateExpressionForSpotSet(hoursExp, shrimpFractions);
            ExpressionTreeInterface spotIndexExp = namedSpotLookupFieldsMap.get("SpotIndex");
            evaluateExpressionForSpotSet(spotIndexExp, shrimpFractions);
        } catch (SquidException squidException) {
            System.out.println("FIXME");
        }
    }

    @Override
    public void applyTaskIsotopeLabelsToMassStations() {
        int index = 0;
        for (SquidSpeciesModel ssm : squidSpeciesModelList) {
            MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(ssm.getMassStationIndex());
            if ((ssm.getMassStationIndex() == indexOfTaskBackgroundMass) && (indexOfTaskBackgroundMass != indexOfBackgroundSpecies)) {
                // changing mass station background to match task background
                massStationDetail.setIsotopeLabel(DEFAULT_BACKGROUND_MASS_LABEL);
                selectBackgroundSpeciesReturnPreviousIndex(ssm);
                indexOfBackgroundSpecies = indexOfTaskBackgroundMass;
            } else {
                ssm.setIsotopeName(nominalMasses.get(index));
                massStationDetail.setIsotopeLabel(ssm.getIsotopeName());
            }
            index++;
        }
    }

    public void applyTaskIsotopeRatioRolesToSquidSpeciesModels(MassStationDetail massStationDetail) {
        for (SquidSpeciesModel ssm : squidSpeciesModelList) {
            if (ssm.getMassStationIndex() == massStationDetail.getMassStationIndex()) {
                ssm.setNumeratorRole(massStationDetail.isNumeratorRole());
                ssm.setDenominatorRole(massStationDetail.isDenominatorRole());
                break;
            }
        }
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
        setupSquidSessionSpecsAndReduceAndReport(false);
        updateAllExpressions(true);
    }

    private void buildSquidSpeciesModelListFromMassStationDetails() {
        squidSpeciesModelList = new ArrayList<>();
        for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
            SquidSpeciesModel spm
                    = new SquidSpeciesModel(
                            entry.getKey(),
                            entry.getValue().getMassStationLabel(),
                            entry.getValue().getIsotopeLabel(),
                            entry.getValue().getElementLabel(),
                            entry.getValue().getIsBackground(),
                            entry.getValue().getuThBearingName(),
                            entry.getValue().isViewedAsGraph());

            squidSpeciesModelList.add(spm);
        }
        if (tableOfSelectedRatiosByMassStationIndex.length == 0) {
            tableOfSelectedRatiosByMassStationIndex = new boolean[squidSpeciesModelList.size()][squidSpeciesModelList.size()];
        }
    }

    // temporarily de-activated while workflow specs finished
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
                mapOfIndexToMassStationDetails.get(retVal).setIsBackground(false);
                break;
            }
        }

        if (ssm != null) {
            ssm.setIsBackground(true);
            ssm.setNumeratorRole(false);
            ssm.setDenominatorRole(false);
            MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(ssm.getMassStationIndex());
            massStationDetail.setIsBackground(true);
            massStationDetail.setNumeratorRole(false);
            massStationDetail.setDenominatorRole(false);
        }

        return retVal;
    }

    public void buildSquidRatiosModelListFromMassStationDetails() {
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

        generateSpotLookupFields();
        for (Map.Entry<String, ExpressionTreeInterface> entry : namedSpotLookupFieldsMap.entrySet()) {
            namedExpressionsMap.put(entry.getKey(), entry.getValue());
        }

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
        ExpressionTreeInterface ratioExpressionTree = null;
        if ((findNumerator(ratioName) != null) && (findDenominator(ratioName) != null)) {
            ratioExpressionTree
                    = new ExpressionTree(
                            ratioName,
                            ShrimpSpeciesNode.buildShrimpSpeciesNode(findNumerator(ratioName), "getPkInterpScanArray"),
                            ShrimpSpeciesNode.buildShrimpSpeciesNode(findDenominator(ratioName), "getPkInterpScanArray"),
                            Operation.divide());

            ((ExpressionTreeWithRatiosInterface) ratioExpressionTree).getRatiosOfInterest().add(ratioName);
            ratioExpressionTree.setSquidSwitchSTReferenceMaterialCalculation(true);
            ratioExpressionTree.setSquidSwitchSAUnknownCalculation(true);
        }
        return ratioExpressionTree;
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

        xstream.registerConverter(new ExpressionSpecXMLConverter());
        xstream.alias("ExpressionSpec", ExpressionSpec.class);

        xstream.registerConverter(new TaskXMLConverter());
        xstream.alias("Task", Task.class);
        xstream.alias("Task", this.getClass());

        // Note: http://cristian.sulea.net/blog.php?p=2014-11-12-xstream-object-references
        xstream.setMode(XStream.NO_REFERENCES);
    }

    @Override
    public String customizeXML(String xml) {
        String xmlR = xml;
        
        xmlR = xmlR.replaceFirst("<Task>",
                XML_HEADER_FOR_SQUIDTASK_FILES_USING_LOCAL_SCHEMA);
        
        return xmlR;
    }

    @Override
    public List<ShrimpFractionExpressionInterface> processRunFractions(ShrimpDataFileInterface prawnFile, SquidSessionModel squidSessionSpecs) {
        shrimpFractions = new ArrayList<>();
        int countOfShrimpFractionsWithInvalidSBMcounts = 0;

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
                if (shrimpFraction.getCountOfNonPositiveSBMCounts() > 0) {
                    countOfShrimpFractionsWithInvalidSBMcounts++;
                }
                shrimpFractions.add(shrimpFraction);
            }
        }

        // June 2019 per issue #340
        if (useSBM && (countOfShrimpFractionsWithInvalidSBMcounts > 0)) {
            SquidMessageDialog.showWarningDialog(
                    "Squid3 detected that " + countOfShrimpFractionsWithInvalidSBMcounts + " spots contain invalid SBM counts. \n\n"
                    + "Squid3 recommends switching SBM normalisation OFF until you diagnose the problem.",
                    null);
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
        int refMatSpotIndex = 1;
        for (ShrimpFractionExpressionInterface spot : shrimpFractions) {
            // spots that are concentrationReferenceMaterialModel will also be in one of the other two buckets
            if (spot.isConcentrationReferenceMaterial()) {
                concentrationReferenceMaterialSpots.add(spot);
            }

            if (spot.isReferenceMaterial()) {
                referenceMaterialSpots.add(spot);
                // assign 1-based spotIndex for sorting at WM visualization
                ((ShrimpFraction) spot).setSpotIndex(refMatSpotIndex);
                refMatSpotIndex++;

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
            evaluateTaskExpression(expression.getExpressionTree());
        }
    }

    public void setUnknownGroupCommonLeadMethod(List<ShrimpFractionExpressionInterface> spotsForExpression, int methodFlag) {
        for (ShrimpFractionExpressionInterface spot : spotsForExpression) {
            spot.getCommonLeadSpecsForSpot().setMethodSelected(methodFlag);
        }
    }

    public void setUnknownGroupCommonLeadModel(List<ShrimpFractionExpressionInterface> spotsForExpression, ParametersModel commonLeadModel) {
        for (ShrimpFractionExpressionInterface spot : spotsForExpression) {
            spot.setCommonLeadModel(commonLeadModel);
        }
    }

    public void setUnknownGroupSelectedAge(List<ShrimpFractionExpressionInterface> spotsForExpression, SampleAgeTypesEnum sampleAgeType) {
        for (ShrimpFractionExpressionInterface spot : spotsForExpression) {
            spot.getCommonLeadSpecsForSpot().setSampleAgeType(sampleAgeType);
        }
    }

    public void setUnknownGroupSelectedAge(List<ShrimpFractionExpressionInterface> spotsForExpression, String sampleAgeName) {
        SampleAgeTypesEnum sampleAgeType = null;
        for (SampleAgeTypesEnum sat : SampleAgeTypesEnum.values()) {
            if (sat.getExpressionName().compareTo(sampleAgeName) == 0) {
                sampleAgeType = sat;
                break;
            }
        }
        setUnknownGroupSelectedAge(spotsForExpression, sampleAgeType);
    }

    public void setUnknownGroupAgeSK(List<ShrimpFractionExpressionInterface> spotsForExpression, double sampleAgeSK) {
        for (ShrimpFractionExpressionInterface spot : spotsForExpression) {
            spot.getCommonLeadSpecsForSpot().setSampleAgeSK(sampleAgeSK);
        }
    }

    /**
     * Update Common Lead calculations
     *
     * @param spotsForExpression
     */
    public void evaluateUnknownsWithChangedParameters(List<ShrimpFractionExpressionInterface> spotsForExpression) {
        // first iterate the spots and perform individual evaluations
        for (ShrimpFractionExpressionInterface spot : spotsForExpression) {
            switch (spot.getCommonLeadSpecsForSpot().getMethodSelected()) {
                case METHOD_STACEY_KRAMER:
                    // per Bodorkos Oct 2019 first restore original age values
                    spot.getCommonLeadSpecsForSpot().updateCommonLeadRatiosFromModel();
                    evaluateExpressionsForSampleSpot(spot);
                    ExpressionTreeInterface selectedAgeExpression
                            = namedExpressionsMap.get(spot.getSelectedAgeExpressionName());
                    // run SK 3 times per Ludwig
                    for (int i = 0; i < 3; i++) {
                        spot.getCommonLeadSpecsForSpot().updateCommonLeadRatiosFromSK(
                                spot.getTaskExpressionsEvaluationsPerSpot().get(selectedAgeExpression)[0][0]);
                        // update
                        evaluateExpressionsForSampleSpot(spot);
                    }
                    break;
                case METHOD_COMMON_LEAD_MODEL:
                    spot.getCommonLeadSpecsForSpot().updateCommonLeadRatiosFromModel();
                    evaluateExpressionsForSampleSpot(spot);
                    break;
                // todo
                case METHOD_STACEY_KRAMER_BY_GROUP:
                    spot.getCommonLeadSpecsForSpot().updateCommonLeadRatiosFromAgeSK();
                    // update
                    evaluateExpressionsForSampleSpot(spot);
                    break;
                default:
                    break;
            }
        }
    }

    public SpotSummaryDetails evaluateSelectedAgeWeightedMeanForUnknownGroup(
            String groupName,
            List<ShrimpFractionExpressionInterface> spotsForExpression) {
        // use stored selected age expression name from first fraction as each in the sample has the chosen current name
        String selectedAgeExpressionName = spotsForExpression.get(0).getSelectedAgeExpressionName();

        return evaluateSelectedExpressionWeightedMeanForUnknownGroup(selectedAgeExpressionName, groupName, spotsForExpression);
    }

    public SpotSummaryDetails evaluateSelectedExpressionWeightedMeanForUnknownGroup(
            String expressionName,
            String sampleName,
            List<ShrimpFractionExpressionInterface> spotsForExpression) {

        Expression expressionWM = constructCustomWMExpression(expressionName, sampleName);

        SpotSummaryDetails spotSummaryDetails = null;
        try {
            evaluateExpressionForSpotSet(expressionWM.getExpressionTree(), spotsForExpression);
            spotSummaryDetails
                    = taskExpressionsEvaluationsPerSpotSet.get(expressionWM.getExpressionTree().getName());
        } catch (SquidException squidException) {
        }

        return spotSummaryDetails;
    }

    private Expression constructCustomWMExpression(String expressionName, String sampleName) {
        // calculate weighted mean of selected expressionName without auto-rejection
        Expression expressionWM = buildExpression(expressionName + "_WM_" + sampleName,
                "WtdAv([\"" + expressionName + "\"])", false, true, true);
//                "WtdMeanACalc([\"" + expressionName + "\"],[%\"" + expressionName + "\"],TRUE,FALSE)", false, true, true);
        expressionWM.setNotes("Expression generated from the Samples Weighted Mean screen under Interpretations.");

        expressionWM.getExpressionTree().setUnknownsGroupSampleName(sampleName);
        expressionWM.getExpressionTree().setSquidSpecialUPbThExpression(false);

        updateSingleExpression(expressionWM);

        return expressionWM;
    }

    public void includeCustomWMExpressionByName(String expressionName) {
        Expression exp = getExpressionByName(expressionName);
        if (exp == null) {
            String[] wmExpNameArray = expressionName.split("_WM_");
            exp = constructCustomWMExpression(wmExpNameArray[0], wmExpNameArray[1]);
        }

        if (!namedExpressionsMap.containsKey(exp.getName())) {
            removeExpression(exp, false);
            addExpression(exp, false);
        }
    }

    /**
     *
     * @param spot the value of spot
     */
    private void evaluateExpressionsForSampleSpot(ShrimpFractionExpressionInterface spot) {
        for (Expression expression : taskExpressionsOrdered) {
            ExpressionTreeInterface expressionTree = expression.getExpressionTree();
            if (expressionTree.amHealthy()) {
                if (((ExpressionTree) expressionTree).isSquidSwitchSAUnknownCalculation()) {
                    // now evaluate expressionTree
                    try {
                        evaluateExpressionForSpot(expressionTree, spot);
                    } catch (SquidException | ArrayIndexOutOfBoundsException squidException) {
                    }
                }
            }
        }
    }

    /**
     *
     * @param expressionTree the value of expressionTree2
     */
    public void evaluateTaskExpression(ExpressionTreeInterface expressionTree) {

        if (expressionTree.amHealthy()) {// && expressionTree.isRootExpressionTree()) {
            // determine subset of spots to be evaluated - default = all
            List<ShrimpFractionExpressionInterface> spotsForExpression = shrimpFractions;

            if (((ExpressionTree) expressionTree).isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                spotsForExpression = concentrationReferenceMaterialSpots;
            } else if (!((ExpressionTree) expressionTree).isSquidSwitchSTReferenceMaterialCalculation()) {
                // lookup set of unknowns
                String unknownGroupSampleName = ((ExpressionTree) expressionTree).getUnknownsGroupSampleName();
                spotsForExpression = mapOfUnknownsBySampleNames.get(unknownGroupSampleName);

            } else if (!((ExpressionTree) expressionTree).isSquidSwitchSAUnknownCalculation()) {
                spotsForExpression = referenceMaterialSpots;
            }

            if (spotsForExpression == null) {
                spotsForExpression = new ArrayList<>();
            }
            // now evaluate expressionTree
            try {
                evaluateExpressionForSpotSet(expressionTree, spotsForExpression);
            } catch (SquidException | ArrayIndexOutOfBoundsException squidException) {

            }
        }
    }

    public void evaluateExpressionForSpotSet(
            ExpressionTreeInterface expressionTree,
            List<ShrimpFractionExpressionInterface> spotsForExpression) throws SquidException {

        // determine taskType of expressionTree
        // Summary expression test
        // June 2019 added null test for operation that exists when function name is unknown
        if (((ExpressionTree) expressionTree).isSquidSwitchSCSummaryCalculation()
                && ((ExpressionTree) expressionTree).getOperation() != null) {
            List<ShrimpFractionExpressionInterface> spotsUsedForCalculation = new ArrayList<>();
            double[][] values;

            // May 2018 new logic to support user rejecting some fractions in summary calculations - Weighted Mean for now
            SpotSummaryDetails spotSummaryDetails;
            if (taskExpressionsEvaluationsPerSpotSet.containsKey(expressionTree.getName())) {
                spotSummaryDetails = taskExpressionsEvaluationsPerSpotSet.get(expressionTree.getName());
            } else {
                spotSummaryDetails = new SpotSummaryDetails(expressionTree);
                taskExpressionsEvaluationsPerSpotSet.put(expressionTree.getName(), spotSummaryDetails);
            }

            // if the spotsForExpression are the same, then preserve list of indices of rejected
            // otherwise reset the list of indices to empty
            if (!spotSummaryDetails.getSelectedSpots().equals(spotsForExpression)) {
                spotSummaryDetails.setRejectedIndices(new boolean[spotsForExpression.size()]);
                spotSummaryDetails.setSelectedSpots(spotsForExpression);
            }

            if (((expressionTree instanceof ConstantNode) || ((ExpressionTree) expressionTree).getOperation().isScalarResult())
                    && !expressionTree.isRootExpressionTree()) {
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

            // Sep 2020 switched to SqWtdAv for Samples with no auto reject
            if (((ExpressionTree) expressionTree).getOperation() instanceof SqWtdAv) {
                // we use the user's stored rejections and do not do autorejection
                spotsUsedForCalculation = spotSummaryDetails.retrieveActiveSpots();
                noReject = true;
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
            ExpressionTreeInterface expressionTree,
            ShrimpFractionExpressionInterface spot) throws SquidException {

        if (((ExpressionTree) expressionTree).hasRatiosOfInterest()) {
            // case of Squid switch "NU"
            ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
            TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEvaluatedPerSpotPerScanModel
                    = expressionEvaluator.evaluateTaskExpressionsPerSpotPerScan(this, expressionTree, spot);

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
            if (taskExpressionsPerSpot.containsKey(expressionTree)) {
                taskExpressionsPerSpot.remove(expressionTree);
            }
            spot.getTaskExpressionsEvaluationsPerSpot().put(expressionTree, value);

        } else {
            List<ShrimpFractionExpressionInterface> singleSpot = new ArrayList<>();
            singleSpot.add(spot);
            try {
                double[][] value = convertObjectArrayToDoubles(expressionTree.eval(singleSpot, this));
                spot.getTaskExpressionsEvaluationsPerSpot().put(expressionTree, value);
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
                entry.getValue().setIsotopeLabel(DEFAULT_BACKGROUND_MASS_LABEL);
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
//        setupSquidSessionSpecsAndReduceAndReport(false);
        prepareRatios();
//        updateAllExpressions(true);
    }

    @Override
    public void updateTableOfSelectedRatiosByRowOrCol(int row, int col, boolean selected) {
        if (row == -1) {
            if (mapOfIndexToMassStationDetails.get(col).isDenominatorRole()) {
                String den = mapOfIndexToMassStationDetails.get(col).getIsotopeLabel();
                for (int i = 0; i < mapOfIndexToMassStationDetails.size(); i++) {
                    if (mapOfIndexToMassStationDetails.get(i).isNumeratorRole()) {
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
            }
        }

        if (col == -1) {
            if (mapOfIndexToMassStationDetails.get(row).isNumeratorRole()) {
                String num = mapOfIndexToMassStationDetails.get(row).getIsotopeLabel();
                for (int i = 0; i < mapOfIndexToMassStationDetails.size(); i++) {
                    if (mapOfIndexToMassStationDetails.get(i).isDenominatorRole()) {
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
            }
        }

        setChanged(true);
//        setupSquidSessionSpecsAndReduceAndReport(false);
        prepareRatios();
//        updateAllExpressions(true);
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

    /**
     * Performs update to set all numerator and denominator flags to true if all
     * are false for backward compatibility.
     */
    public void updateSquidSpeciesModelsGeochronMode() {
        if (taskType.equals(TaskTypeEnum.GEOCHRON)) {
            boolean detectTrueNumerator = false;
            boolean detectTrueDenominator = false;
            for (SquidSpeciesModel ssm : squidSpeciesModelList) {
                detectTrueNumerator = detectTrueNumerator || ssm.isNumeratorRole();
                detectTrueDenominator = detectTrueDenominator || ssm.isDenominatorRole();
            }

            if (!(detectTrueNumerator && detectTrueDenominator)) {
                for (SquidSpeciesModel ssm : squidSpeciesModelList) {
                    if (!ssm.getIsBackground()) {
                        ssm.setNumeratorRole(true);
                        ssm.setDenominatorRole(true);

                        MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(ssm.getMassStationIndex());
                        massStationDetail.setNumeratorRole(true);
                        massStationDetail.setDenominatorRole(true);
                    } else {
                        ssm.setNumeratorRole(false);
                        ssm.setDenominatorRole(false);

                        MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(ssm.getMassStationIndex());
                        massStationDetail.setNumeratorRole(false);
                        massStationDetail.setDenominatorRole(false);
                    }
                }
            }
        }
    }

    /**
     *
     * @param numerator the value of numerator
     * @param numValue the value of numValue
     * @param denominator the value of denominator
     * @param denValue the value of denValue
     */
    public void initializeSquidSpeciesModelsRatioMode(boolean numerator, boolean numValue, boolean denominator, boolean denValue) {
        for (SquidSpeciesModel ssm : squidSpeciesModelList) {
            MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(ssm.getMassStationIndex());
            if (!ssm.getIsBackground()) {
                if (numerator) {
                    ssm.setNumeratorRole(numValue);
                    massStationDetail.setNumeratorRole(numValue);
                }
                if (denominator) {
                    ssm.setDenominatorRole(denValue);
                    massStationDetail.setDenominatorRole(denValue);
                }
            } else {
                // background
                ssm.setNumeratorRole(false);
                massStationDetail.setNumeratorRole(false);
                ssm.setDenominatorRole(false);
                massStationDetail.setDenominatorRole(false);
            }
        }
    }

    public void buildExpressionDependencyGraphs() {
        // prep graphs
        requiresExpressionsGraph = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        providesExpressionsGraph = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        missingExpressionsByName = new ArrayList<>();

        for (Expression exp : taskExpressionsOrdered) {

            List<String> calledExpressions = buildExpressionRequiresGraph(exp);
            requiresExpressionsGraph.put(exp.getName(), calledExpressions);

            // work in reverse to build providesExpressionsGraph
            List<String> callingExpressions;
            for (String called : calledExpressions) {
                if (providesExpressionsGraph.containsKey(called)) {
                    callingExpressions = providesExpressionsGraph.get(called);
                } else {
                    callingExpressions = new ArrayList<>();
                    providesExpressionsGraph.put(called, callingExpressions);
                }
                if (!callingExpressions.contains(exp.getName())) {
                    callingExpressions.add(exp.getName());
                }
            }

        }
    }

    private List<String> buildExpressionRequiresGraph(Expression exp) {
        ExpressionTreeInterface expTree = exp.getExpressionTree();
        List<String> calledExpressions;
        if (requiresExpressionsGraph.containsKey(exp.getName())) {
            calledExpressions = requiresExpressionsGraph.get(exp.getName());
        } else {
            calledExpressions = new ArrayList<>();
        }

        // walk the tree looking for named expressions
        walkExpressionTreeRequires(expTree, calledExpressions);

        return calledExpressions;
    }

    private void walkExpressionTreeRequires(ExpressionTreeInterface expTree, List<String> calledExpressions) {
        List<ExpressionTreeInterface> children = ((ExpressionTreeBuilderInterface) expTree).getChildrenET();
        for (ExpressionTreeInterface child : children) {
            String calledName = child.getName();
            if (namedExpressionsMap.containsKey(calledName)
                    && !(child instanceof ShrimpSpeciesNode)
                    && (child.getName().compareTo("FALSE") != 0)
                    && (child.getName().compareTo("TRUE") != 0)) {
                if (!calledExpressions.contains(calledName)) {
                    calledExpressions.add(calledName);
                }
            }

            // see ExpressionParser.java - this is where missing expressions go
            if (calledName.startsWith(MISSING_EXPRESSION_STRING)) {
                String missingExpressionName = (String) ((ConstantNode) child).getValue();
                if (!missingExpressionsByName.contains(missingExpressionName.toUpperCase(Locale.ENGLISH))) {
                    missingExpressionsByName.add(missingExpressionName.toUpperCase(Locale.ENGLISH));
                }
            }
            walkExpressionTreeRequires(child, calledExpressions);
        }
    }

    @Override
    public String printExpressionRequiresGraph(Expression exp) {
        if ((requiresExpressionsGraph == null) || (providesExpressionsGraph == null)) {
            buildExpressionDependencyGraphs();
        }

        List<Boolean> showEdge = new ArrayList<>();
        showEdge.add(true);
        return "Graph of required expressions - NEEDED BY: \n\n"
                + exp.getName() + "\n"
                + printDependencyGraph(requiresExpressionsGraph.get(exp.getName()), 1, showEdge, requiresExpressionsGraph, "|-> ");
    }

    @Override
    public String printExpressionProvidesGraph(Expression exp) {
        if ((requiresExpressionsGraph == null) || (providesExpressionsGraph == null)) {
            buildExpressionDependencyGraphs();
        }

        List<Boolean> showEdge = new ArrayList<>();
        showEdge.add(true);
        return "Graph of provided expressions - NEEDING: \n\n"
                + exp.getName() + "\n"
                + printDependencyGraph(providesExpressionsGraph.get(exp.getName()), 1, showEdge, providesExpressionsGraph, "|<- ");
    }

    /**
     *
     * @param calledExpressions the value of calledExpressions
     * @param depth the value of depth
     * @param showEdge the value of showEdge
     * @param graph the value of graph
     * @param direction the value of direction
     */
    private String printDependencyGraph(
            List<String> calledExpressions, int depth, List<Boolean> showEdge, Map<String, List<String>> graph, String direction) {

        StringBuilder calls = new StringBuilder();
        if (calledExpressions != null) {
            for (String call : calledExpressions) {
                for (int i = 0; i < depth; i++) {
                    calls.append("     ");
                    calls.append((showEdge.get(i)) ? (i == (depth - 1)) ? direction : "|" : " ");
                    if (showEdge.get(i) && (i == (depth - 1))) {
                        showEdge.set(i, call.compareTo(calledExpressions.get(calledExpressions.size() - 1)) != 0);
                    }
                }

                calls.append(call).append("\n");
                if (graph.get(call) != null) {
                    showEdge.add(true);
                    calls.append(printDependencyGraph(graph.get(call), depth + 1, showEdge, graph, direction));
                }
            }
        }
        showEdge.set(depth - 1, true);
        return calls.toString();
    }

    /**
     *
     * @param updateDefaultReports the value of updateDefaultReports
     * @return
     */
    public SquidReportTableInterface initTaskDefaultSquidReportTables(boolean updateDefaultReports) {
        if (updateDefaultReports || squidReportTablesRefMat == null) {
            this.squidReportTablesRefMat = new ArrayList<>();
        }
        if (updateDefaultReports || squidReportTablesUnknown == null) {
            this.squidReportTablesUnknown = new ArrayList<>();
        }

        if (squidReportTablesRefMat.isEmpty()) {
            squidReportTablesRefMat.add(SquidReportTable.createDefaultSquidReportTableRefMat(this));
            selectedRefMatReportModel = squidReportTablesRefMat.get(0);
        }

        if (squidReportTablesUnknown.isEmpty()) {
            squidReportTablesUnknown.add(SquidReportTable.createDefaultSquidReportTableUnknown(this));
            selectedUnknownReportModel = squidReportTablesUnknown.get(0);
        }

        return initSquidWeightedMeanPlotSortTable();
    }

    private SquidReportTableInterface initSquidWeightedMeanPlotSortTable() {
        SquidReportTableInterface squidWeightedMeanPlotSortTable = null;

        boolean containsFilterReport = false;
        for (SquidReportTableInterface table : squidReportTablesUnknown) {
            if (table.getReportTableName().matches(NAME_OF_WEIGHTEDMEAN_PLOT_SORT_REPORT)) {
                squidWeightedMeanPlotSortTable = table;
                containsFilterReport = true;
                break;
            }
        }

        if (containsFilterReport
                && ((SquidReportTable) squidWeightedMeanPlotSortTable).getVersion() < SquidReportTable.WEIGHTEDMEAN_PLOT_SORT_TABLE_VERSION) {
            squidReportTablesUnknown.remove(squidWeightedMeanPlotSortTable);
            containsFilterReport = false;
        }

        if (!containsFilterReport) {
            squidWeightedMeanPlotSortTable
                    = SquidReportTable.createDefaultSquidReportTableUnknownSquidFilter(this, SquidReportTable.WEIGHTEDMEAN_PLOT_SORT_TABLE_VERSION);
            squidWeightedMeanPlotSortTable.setIsDefault(false);
            squidReportTablesUnknown.add(squidWeightedMeanPlotSortTable);
        }

        // handle special case where raw ratios is populated on the fly per task
        SquidReportCategoryInterface rawRatiosCategory = squidWeightedMeanPlotSortTable.getReportCategories().get(2);
        LinkedList<SquidReportColumnInterface> categoryColumns = new LinkedList<>();
        for (String ratioName : ratioNames) {
            SquidReportColumnInterface column = SquidReportColumn.createSquidReportColumn(ratioName);
            categoryColumns.add(column);
        }
        rawRatiosCategory.setCategoryColumns(categoryColumns);

        return squidWeightedMeanPlotSortTable;
    }

    @Override
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
     * @return the taskType
     */
    @Override
    public TaskTypeEnum getTaskType() {
        return taskType;
    }

    /**
     * @param taskType the taskType to set
     */
    @Override
    public void setTaskType(TaskTypeEnum taskType) {
        this.taskType = taskType;
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
    public int getIndexOfTaskBackgroundMass() {
        return indexOfTaskBackgroundMass;
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

    public String findNominalMassOfTaskBackgroundMass() {
        return nominalMasses.get(indexOfTaskBackgroundMass);
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
     * @return the namedSpotLookupFieldsMap
     */
    @Override
    public Map<String, ExpressionTreeInterface> getNamedSpotLookupFieldsMap() {
        return namedSpotLookupFieldsMap;
    }

    /**
     * @return the taskExpressionsOrdered
     */
    @Override
    public List<Expression> getTaskExpressionsOrdered() {
        return taskExpressionsOrdered;
    }

    public List<ExpressionSpecInterface> getTaskCustomExpressionsOrdered() {
        List<ExpressionSpecInterface> taskCustomExpressionsOrdered = new ArrayList<>();
        for (Expression exp : taskExpressionsOrdered) {
            if (exp.isCustom()) {
                taskCustomExpressionsOrdered.add(specifyExpression(exp));
            }
        }

        return taskCustomExpressionsOrdered;
    }

    @Override
    public List<Expression> getCustomTaskExpressions() {
        List<Expression> customTaskExpressions = new ArrayList<>();
        for (Expression expression : taskExpressionsOrdered) {
            if (expression.isCustom()) {
                customTaskExpressions.add(expression);
            }
        }
        return customTaskExpressions;
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
     * @return the changed
     */
    public boolean isChanged() {
        return changed;
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
     * @return the useCalculatedAv_ParentElement_ConcenConst
     */
    @Override
    public boolean isUseCalculatedAv_ParentElement_ConcenConst() {
        return useCalculatedAv_ParentElement_ConcenConst;
    }

    /**
     * @param useCalculatedAv_ParentElement_ConcenConst the
     * useCalculatedAv_ParentElement_ConcenConst to set
     */
    @Override
    public void setUseCalculatedAv_ParentElement_ConcenConst(boolean useCalculatedAv_ParentElement_ConcenConst) {
        this.useCalculatedAv_ParentElement_ConcenConst = useCalculatedAv_ParentElement_ConcenConst;
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

    /**
     *
     * @return showSpotLabels
     */
    @Override
    public boolean isShowSpotLabels() {
        return showSpotLabels;
    }

    /**
     *
     * @param showSpotLabels
     */
    @Override
    public void setShowSpotLabels(boolean showSpotLabels) {
        this.showSpotLabels = showSpotLabels;
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
        // safety feature
        mapOfUnknownsBySampleNames.put(Squid3Constants.SpotTypes.UNKNOWN.getPlotType(), unknownSpots);
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
     * @param squidAllowsAutoExclusionOfSpots the
     * squidAllowsAutoExclusionOfSpots to set
     */
    @Override
    public void setSquidAllowsAutoExclusionOfSpots(boolean squidAllowsAutoExclusionOfSpots) {
        this.squidAllowsAutoExclusionOfSpots = squidAllowsAutoExclusionOfSpots;
    }

    /**
     * @return the squidAllowsAutoExclusionOfSpots
     */
    @Override
    public boolean isSquidAllowsAutoExclusionOfSpots() {
        return squidAllowsAutoExclusionOfSpots;
    }

    /**
     * @param extPErrU the extPErrU to set
     */
    @Override
    public void setExtPErrU(double extPErrU) {
        this.extPErrU = extPErrU;
        if (namedParametersMap.containsKey(MIN_206PB238U_EXT_1SIGMA_ERR_PCT)) {
            ExpressionTreeInterface constant = namedParametersMap.get(MIN_206PB238U_EXT_1SIGMA_ERR_PCT);
            ((ConstantNode) constant).setValue(extPErrU);
        }
        this.changed = true;
    }

    /**
     * @return the extPErrU
     */
    @Override
    public double getExtPErrU() {
        return extPErrU;
    }

    /**
     * @return the extPErrTh
     */
    @Override
    public double getExtPErrTh() {
        return extPErrTh;
    }

    /**
     * @param extPErrTh the extPErrTh to set
     */
    @Override
    public void setExtPErrTh(double extPErrTh) {
        this.extPErrTh = extPErrTh;
        if (namedParametersMap.containsKey(MIN_208PB232TH_EXT_1SIGMA_ERR_PCT)) {
            ExpressionTreeInterface constant = namedParametersMap.get(MIN_208PB232TH_EXT_1SIGMA_ERR_PCT);
            ((ConstantNode) constant).setValue(extPErrTh);
        }
        this.changed = true;
    }

    @Override
    public void setReferenceMaterialModel(ParametersModel refMat) {
        if (refMat instanceof ReferenceMaterialModel) {
            referenceMaterialModelChanged = referenceMaterialModel == null || !referenceMaterialModel.equals(refMat);
            referenceMaterialModel = (ReferenceMaterialModel) refMat;
        }
    }

    @Override
    public ParametersModel getReferenceMaterialModel() {
        return referenceMaterialModel;
    }

    @Override
    public void setConcentrationReferenceMaterialModel(ParametersModel refMat) {
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

    /**
     * @return the specialSquidFourExpressionsMap
     */
    @Override
    public Map<String, String> getSpecialSquidFourExpressionsMap() {
        return specialSquidFourExpressionsMap;
    }

    /**
     * @param specialSquidFourExpressionsMap the specialSquidFourExpressionsMap
     * to set
     */
    @Override
    public void setSpecialSquidFourExpressionsMap(Map<String, String> specialSquidFourExpressionsMap) {
        this.specialSquidFourExpressionsMap = specialSquidFourExpressionsMap;
    }

    /**
     * @return the delimiterForUnknownNames
     */
    @Override
    public String getDelimiterForUnknownNames() {
        return delimiterForUnknownNames;
    }

    /**
     * @param delimiterForUnknownNames the delimiterForUnknownNames to set
     */
    @Override
    public void setDelimiterForUnknownNames(String delimiterForUnknownNames) {
        this.delimiterForUnknownNames = delimiterForUnknownNames;
    }

    /**
     * @return the providesExpressionsGraph
     */
    public Map<String, List<String>> getProvidesExpressionsGraph() {
        if ((providesExpressionsGraph == null) || providesExpressionsGraph.isEmpty()) {
            buildExpressionDependencyGraphs();
        }
        return providesExpressionsGraph;
    }

    /**
     * @return the requiresExpressionsGraph
     */
    public Map<String, List<String>> getRequiresExpressionsGraph() {
        if ((requiresExpressionsGraph == null) || requiresExpressionsGraph.isEmpty()) {
            buildExpressionDependencyGraphs();
        }
        return requiresExpressionsGraph;
    }

    /**
     * @return the missingExpressionsByName
     */
    public List<String> getMissingExpressionsByName() {
        if (missingExpressionsByName == null) {
            missingExpressionsByName = new ArrayList<>();
        }
        return missingExpressionsByName;
    }

    /**
     * @return the roundingForSquid3
     */
    public boolean isRoundingForSquid3() {
        return roundingForSquid3;
    }

    /**
     * @param roundingForSquid3 the roundingForSquid3 to set
     */
    public void setRoundingForSquid3(boolean roundingForSquid3) {
        this.roundingForSquid3 = roundingForSquid3;
    }

    /**
     * @return the squidReportTablesRefMat
     */
    public List<SquidReportTableInterface> getSquidReportTablesRefMat() {
        return squidReportTablesRefMat;
    }

    /**
     * @param squidReportTablesRefMat the squidReportTablesRefMat to set
     */
    public void setSquidReportTablesRefMat(List<SquidReportTableInterface> squidReportTablesRefMat) {
        this.squidReportTablesRefMat = squidReportTablesRefMat;
    }

    /**
     * @return the squidReportTablesUnknown
     */
    public List<SquidReportTableInterface> getSquidReportTablesUnknown() {
        //TODO: backwards fix can be removed after July 1 2020
        SquidReportTableInterface oldReport = null;
        for (SquidReportTableInterface srt : squidReportTablesUnknown) {
            if (srt.getReportTableName().contains("Squid Filter Report")) {
                oldReport = srt;
                break;
            }
        }

        if (oldReport != null) {
            squidReportTablesUnknown.remove(oldReport);
        }

        return squidReportTablesUnknown;
    }

    /**
     * @param squidReportTablesUnknown the squidReportTablesUnknown to set
     */
    @Override
    public void setSquidReportTablesUnknown(List<SquidReportTableInterface> squidReportTablesUnknown) {
        this.squidReportTablesUnknown = squidReportTablesUnknown;
    }

    /**
     * @return the selectedRefMatReportModel
     */
    public SquidReportTableInterface getSelectedRefMatReportModel() {
        return selectedRefMatReportModel;
    }

    /**
     * @param selectedRefMatReportModel the selectedRefMatReportModel to set
     */
    public void setSelectedRefMatReportModel(SquidReportTableInterface selectedRefMatReportModel) {
        this.selectedRefMatReportModel = selectedRefMatReportModel;
    }

    /**
     * @return the selectedUnknownReportModel
     */
    public SquidReportTableInterface getSelectedUnknownReportModel() {
        return selectedUnknownReportModel;
    }

    /**
     * @param selectedUnknownReportModel the selectedUnknownReportModel to set
     */
    public void setSelectedUnknownReportModel(SquidReportTableInterface selectedUnknownReportModel) {
        this.selectedUnknownReportModel = selectedUnknownReportModel;
    }

    /**
     * @return the overcountCorrectionType
     */
    @Override
    public OvercountCorrectionTypes getOvercountCorrectionType() {
        if (overcountCorrectionType == null) {
            overcountCorrectionType = OvercountCorrectionTypes.NONE;
        }
        return overcountCorrectionType;
    }

    /**
     * @param overcountCorrectionType the overcountCorrectionType to set
     */
    @Override
    public void setOvercountCorrectionType(OvercountCorrectionTypes overcountCorrectionType) {
        this.overcountCorrectionType = overcountCorrectionType;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, taskSquidVersion, taskType, description, authorName, labName,
                provenance, dateRevised, useSBM, userLinFits, indexOfBackgroundSpecies,
                indexOfTaskBackgroundMass, parentNuclide, directAltPD, filterForRefMatSpotNames,
                filterForConcRefMatSpotNames, filtersForUnknownNames, nominalMasses, ratioNames,
                mapOfIndexToMassStationDetails, squidSessionModel, squidSpeciesModelList,
                squidRatiosModelList, taskExpressionsOrdered, taskExpressionsRemoved,
                namedExpressionsMap, namedOvercountExpressionsMap, namedConstantsMap, namedParametersMap, namedSpotLookupFieldsMap, shrimpFractions,
                referenceMaterialSpots, concentrationReferenceMaterialSpots, unknownSpots,
                mapOfUnknownsBySampleNames, prawnChanged, taskExpressionsEvaluationsPerSpotSet,
                prawnFile, reportsEngine, changed, useCalculatedAv_ParentElement_ConcenConst,
                selectedIndexIsotope, massMinuends, massSubtrahends, showTimeNormalized,
                showPrimaryBeam, showQt1y, showQt1z, squidAllowsAutoExclusionOfSpots,
                extPErrU, extPErrTh, physicalConstantsModel, referenceMaterialModel, commonPbModel,
                concentrationReferenceMaterialModel, physicalConstantsModelChanged,
                referenceMaterialModelChanged, commonPbModelChanged, concentrationReferenceMaterialModelChanged,
                specialSquidFourExpressionsMap, delimiterForUnknownNames, concentrationTypeEnum,
                providesExpressionsGraph, requiresExpressionsGraph, missingExpressionsByName,
                roundingForSquid3, squidReportTablesRefMat, squidReportTablesUnknown, overcountCorrectionType);
//                ((ShrimpFraction) this.referenceMaterialSpots.get(1)));
        result = 31 * result + Arrays.hashCode(tableOfSelectedRatiosByMassStationIndex);
        System.out.println(result);
        return result;
    }

    public static void main(String[] args) {
        SquidProject sp = new SquidProject(GEOCHRON);
        TaskInterface task = sp.getTask();
        task.setNominalMasses(REQUIRED_NOMINAL_MASSES);
        task.setRatioNames(REQUIRED_RATIO_NAMES);

        ((XMLSerializerInterface) task).serializeXMLObject("TASK.xml");

    }

    /**
     * @return the xAxisExpressionName
     */
    public String getxAxisExpressionName() {
        if (xAxisExpressionName == null) {
            xAxisExpressionName = "204/206";
        }
        return xAxisExpressionName;
    }

    /**
     * @param xAxisExpressionName the xAxisExpressionName to set
     */
    public void setxAxisExpressionName(String xAxisExpressionName) {
        this.xAxisExpressionName = xAxisExpressionName;
    }

    /**
     * @return the yAxisExpressionName
     */
    public String getyAxisExpressionName() {
        if (yAxisExpressionName == null) {
            yAxisExpressionName = "204/206";
        }
        return yAxisExpressionName;
    }

    /**
     * @param yAxisExpressionName the yAxisExpressionName to set
     */
    public void setyAxisExpressionName(String yAxisExpressionName) {
        this.yAxisExpressionName = yAxisExpressionName;
    }
}
