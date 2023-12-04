/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.shrimp.*;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.tasks.taskDesign.TaskDesign;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GEOCHRON;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltinExpressionsCountCorrection204.*;

/**
 * @author James F. Bowring
 */
public interface TaskInterface {

    ExpressionTreeInterface findNamedExpression(String ratioName);

    Expression generateExpressionFromRawExcelStyleText(String name, String originalExpressionText, boolean eqnSwitchNU, boolean referenceMaterialValue, boolean parameterValue);

    void updateRefMatCalibConstWMeanExpressions(boolean squidAllowsAutoExclusionOfSpots) throws SquidException;

    void buildSquidSpeciesModelList() throws SquidException;

    /**
     * @param xstream
     */
    void customizeXstream(XStream xstream);

    /**
     *
     */
    void evaluateTaskExpressions();

    /**
     * @return the authorName
     */
    String getAuthorName();

    /**
     * @param authorName the authorName to set
     */
    void setAuthorName(String authorName);

    /**
     * @return the dateRevised
     */
    long getDateRevised();

    /**
     * @param dateRevised the dateRevised to set
     */
    void setDateRevised(long dateRevised);

    /**
     * @return the description
     */
    String getDescription();

    /**
     * @param description the description to set
     */
    void setDescription(String description);

    /**
     * @return the labName
     */
    String getLabName();

    /**
     * @param labName the labName to set
     */
    void setLabName(String labName);

    /**
     * @return the name
     */
    String getName();

    /**
     * @param name the name to set
     */
    void setName(String name);

    /**
     * @return the provenance
     */
    String getProvenance();

    /**
     * @param provenance the provenance to set
     */
    void setProvenance(String provenance);

    /**
     * @return the ratioNames
     */
    List<String> getRatioNames();

    /**
     * @param ratioNames the ratioNames to set
     */
    void setRatioNames(List<String> ratioNames);

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    Map<Integer, MassStationDetail> getMapOfIndexToMassStationDetails() throws SquidException;

    /**
     * @return the nominalMasses
     */
    List<String> getNominalMasses();

    /**
     * @param nominalMasses the nominalMasses to set
     */
    void setNominalMasses(List<String> nominalMasses);

    /**
     * @param prawnFile the prawnFile to set
     */
    void setPrawnFile(ShrimpDataFileInterface prawnFile);

    SquidSpeciesModel lookUpSpeciesByName(String isotopeName);

    /**
     * @return the squidRatiosModelList
     */
    List<SquidRatiosModel> getSquidRatiosModelList();

    /**
     * @return the squidSpeciesModelList
     */
    List<SquidSpeciesModel> getSquidSpeciesModelList();

    /**
     * @return the tableOfSelectedRatiosByMassStationIndex
     */
    boolean[][] getTableOfSelectedRatiosByMassStationIndex();

    /**
     * @return the taskExpressionsEvaluationsPerSpotSet
     */
    Map<String, SpotSummaryDetails> getTaskExpressionsEvaluationsPerSpotSet();

    /**
     * @param taskExpressionsEvaluationsPerSpotSet the
     *                                             taskExpressionsEvaluationsPerSpotSet to set
     */
    void setTaskExpressionsEvaluationsPerSpotSet(Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet);

    /**
     * @return the type
     */
    TaskTypeEnum getTaskType();

    /**
     * @param type the type to set
     */
    void setTaskType(TaskTypeEnum type);

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    List<MassStationDetail> makeListOfMassStationDetails();

    String printTaskAudit();

    String printTaskSummary();

    int selectBackgroundSpeciesReturnPreviousIndex(SquidSpeciesModel ssm);

    void removeExpression(Expression expression, boolean reprocessExpressions) throws SquidException;

    void restoreRemovedExpressions() throws SquidException;

    void addExpression(Expression exp, boolean reprocessExpressions) throws SquidException;

    ParametersModel getReferenceMaterialModel();

    void setReferenceMaterialModel(ParametersModel refMat);

    ParametersModel getPhysicalConstantsModel();

    void setPhysicalConstantsModel(ParametersModel physConst);

    ParametersModel getCommonPbModel();

    void setCommonPbModel(ParametersModel model);

    ParametersModel getConcentrationReferenceMaterialModel();

    void setConcentrationReferenceMaterialModel(ParametersModel refMat);

    /**
     * @param forceReprocess the value of forceReprocess
     */
    void setupSquidSessionSpecsAndReduceAndReport(boolean forceReprocess) throws SquidException;

    void updateTableOfSelectedRatiosByMassStationIndex(int row, int col, boolean selected);

    /**
     * @return the NAMED_EXPRESSIONS_MAP
     */
    Map<String, ExpressionTreeInterface> getNamedExpressionsMap();

    /**
     * @return the squidSessionModel
     */
    SquidSessionModel getSquidSessionModel();

    /**
     * @return the taskExpressionsOrdered
     */
    List<Expression> getTaskExpressionsOrdered();

    /**
     * @param taskExpressionsOrdered
     */
    void setTaskExpressionsOrdered(List<Expression> taskExpressionsOrdered);

    List<Expression> getCustomTaskExpressions();

    /**
     * @param expression Name of the expression to test
     * @return True if the expression exists, false if not
     */
    boolean expressionExists(Expression expression);

    /**
     * @param changed the changed to set
     */
    void setChanged(boolean changed);

    /**
     * @return the shrimpFractions
     */
    List<ShrimpFractionExpressionInterface> getShrimpFractions();

    List<ShrimpFractionExpressionInterface> processRunFractions(ShrimpDataFileInterface prawnFile, SquidSessionModel squidSessionSpecs) throws SquidException;

    /**
     * @return the referenceMaterialSpots
     */
    List<ShrimpFractionExpressionInterface> getReferenceMaterialSpots();

    /**
     * @return the unknownSpots
     */
    List<ShrimpFractionExpressionInterface> getUnknownSpots();

    /**
     * @return the concentrationReferenceMaterialSpots
     */
    List<ShrimpFractionExpressionInterface> getConcentrationReferenceMaterialSpots();

    /**
     * @param filterForRefMatSpotNames the filterForRefMatSpotNames to set
     */
    void setFilterForRefMatSpotNames(String filterForRefMatSpotNames);

    /**
     * @param filterForConcRefMatSpotNames
     */
    void setFilterForConcRefMatSpotNames(String filterForConcRefMatSpotNames);

    /**
     * @param filtersForUnknownNames the filtersForUnknownNames to set
     */
    void setFiltersForUnknownNames(Map<String, Integer> filtersForUnknownNames);

    /**
     * @return the useSBM
     */
    boolean isUseSBM();

    /**
     * @param useSBM the useSBM to set
     */
    void setUseSBM(boolean useSBM);

    /**
     * @return the userLinFits
     */
    boolean isUserLinFits();

    /**
     * @param userLinFits the userLinFits to set
     */
    void setUserLinFits(boolean userLinFits);

    /**
     * @return the indexOfBackgroundSpecies
     */
    int getIndexOfBackgroundSpecies();

    /**
     * @param indexOfBackgroundSpecies the indexOfBackgroundSpecies to set
     */
    void setIndexOfBackgroundSpecies(int indexOfBackgroundSpecies);

    void updateAllExpressions(boolean reprocessExpressions) throws SquidException;

    /**
     * @param reportsEngine the reportsEngine to set
     */
    void setReportsEngine(CalamariReportsEngine reportsEngine);

    /**
     * The original Calamari Reports
     *
     * @return
     */
    File producePerScanReportsToFiles() throws IOException;

    void updateRatioNames(String[] ratioNames) throws SquidException;

    void updateAffectedExpressions(Expression sourceExpression, boolean reprocessExpressions) throws SquidException;

    void applyTaskIsotopeLabelsToMassStationsAndUpdateTask() throws SquidException;

    void applyTaskIsotopeLabelsToMassStations();

    void populateTableOfSelectedRatiosFromRatiosList();

    void updateTableOfSelectedRatiosByRowOrCol(int row, int col, boolean selected);

    /**
     * @return the namedConstantsMap
     */
    Map<String, ExpressionTreeInterface> getNamedConstantsMap();

    /**
     * @return namedParametersMap
     */
    Map<String, ExpressionTreeInterface> getNamedParametersMap();

    void setIndexOfTaskBackgroundMass(int indexOfTask25BackgroundMass);

    void applyMassStationLabelsToTask() throws SquidException;

    /**
     * @return
     */
    String getParentNuclide();

    /**
     * @param parentNuclide
     */
    void setParentNuclide(String parentNuclide);

    String getPrimaryDaughterParentRatio();

    String getSecondaryDaughterParentCalculation();

    String getIndexIsotope();

    /**
     *
     */
    default void applyDirectives(boolean customizeTaskExpressions) throws SquidException {
        TaskDesign taskDesign = SquidPersistentState.getExistingPersistentState().getTaskDesign();

        // need to remove stored expression results on fractions to clear the decks   
        getShrimpFractions().forEach((spot) -> {
            spot.getTaskExpressionsForScansEvaluated().clear();
            spot.getTaskExpressionsEvaluationsPerSpot().clear();
            spot.getTaskExpressionsMetaDataPerSpot().clear();
        });

        // clear task expressions
        // issue #729 this next line was also removing memory of rejected spots
        // expressions in this map are checked at evaluateExpressionForSpotSet
        // the solution is to put in a flag for updating old files that would specify
        // which expressions to rewrite as part of an update and then restore the rejections.

        if (customizeTaskExpressions) {
            // handle individual summary expressions and their existing rejection flags
        } else {
            setTaskExpressionsEvaluationsPerSpotSet(new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
        }

        List<Expression> customExpressions = getCustomTaskExpressions();
        // special temporary case Sep 2019
        customExpressions.remove(buildCountCorrectionExpressionFrom207());
        customExpressions.remove(buildCountCorrectionExpressionFrom208());
        customExpressions.remove(buildCountCorrectionCustomExpression());

        getTaskExpressionsOrdered().clear();

        Expression parentPPM = null;
        if (getTaskType().equals(GEOCHRON)) {
            // write the magic 4 expressions plus parent mean exp
            String parentPPM_Expression = getSpecialSquidFourExpressionsMap().get(PARENT_ELEMENT_CONC_CONST);
            if ((parentPPM_Expression == null) || (parentPPM_Expression.length()) == 0) {
                parentPPM_Expression = taskDesign.getSpecialSquidFourExpressionsMap().get(PARENT_ELEMENT_CONC_CONST);
            }
            if ((parentPPM_Expression == null) || (parentPPM_Expression.length()) == 0) {
                parentPPM_Expression = BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION;
            }
            parentPPM = BuiltInExpressionsFactory.buildExpression(
                    PARENT_ELEMENT_CONC_CONST, parentPPM_Expression, true, true, false);
            parentPPM.setSquidSwitchNU(true);
            getSpecialSquidFourExpressionsMap().put(PARENT_ELEMENT_CONC_CONST, parentPPM_Expression);

            String uThU_Expression = getSpecialSquidFourExpressionsMap().get(UNCOR206PB238U_CALIB_CONST);
            if ((uThU_Expression == null) || (uThU_Expression.length()) == 0) {
                uThU_Expression = taskDesign.getSpecialSquidFourExpressionsMap().get(UNCOR206PB238U_CALIB_CONST);
            }
            if ((uThU_Expression == null) || (uThU_Expression.length()) == 0) {
                uThU_Expression = UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION;
            }
            Expression uThU = BuiltInExpressionsFactory.buildExpression(
                    UNCOR206PB238U_CALIB_CONST, uThU_Expression, true, true, false);
            uThU.setSquidSwitchNU(true);
            getSpecialSquidFourExpressionsMap().put(UNCOR206PB238U_CALIB_CONST, uThU_Expression);

            String uThTh_Expression = getSpecialSquidFourExpressionsMap().get(UNCOR208PB232TH_CALIB_CONST);
            if ((uThTh_Expression == null) || (uThTh_Expression.length()) == 0) {
                uThTh_Expression = taskDesign.getSpecialSquidFourExpressionsMap().get(UNCOR208PB232TH_CALIB_CONST);
            }
            if ((uThTh_Expression == null) || (uThTh_Expression.length()) == 0) {
                uThTh_Expression = UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION;
            }
            Expression uThTh = BuiltInExpressionsFactory.buildExpression(
                    UNCOR208PB232TH_CALIB_CONST, uThTh_Expression, true, true, false);
            uThTh.setSquidSwitchNU(true);
            getSpecialSquidFourExpressionsMap().put(UNCOR208PB232TH_CALIB_CONST, uThTh_Expression);

            if (isPbU()) {
                getTaskExpressionsOrdered().add(uThU);
                if (isDirectAltPD()) {
                    getTaskExpressionsOrdered().add(uThTh);
                }
            } else {
                getTaskExpressionsOrdered().add(uThTh);
                if (isDirectAltPD()) {
                    getTaskExpressionsOrdered().add(uThU);
                }
            }

            if (!isDirectAltPD()) {
                String thU_DEFAULT_Expression = getSpecialSquidFourExpressionsMap().get(TH_U_EXP_DEFAULT);
                if ((thU_DEFAULT_Expression == null) || (thU_DEFAULT_Expression.length()) == 0) {
                    thU_DEFAULT_Expression = taskDesign.getSpecialSquidFourExpressionsMap().get(TH_U_EXP_DEFAULT);
                }
                if ((thU_DEFAULT_Expression == null) || (thU_DEFAULT_Expression.length()) == 0) {
                    thU_DEFAULT_Expression = TH_U_EXP_DEFAULT_EXPRESSION;
                }
                Expression thU_RM = BuiltInExpressionsFactory.buildExpression(TH_U_EXP_RM, thU_DEFAULT_Expression, true, false, false);
                thU_RM.setSquidSwitchNU(true);
                getTaskExpressionsOrdered().add(thU_RM);

                Expression thU = BuiltInExpressionsFactory.buildExpression(TH_U_EXP, thU_DEFAULT_Expression, false, true, false);
                thU.setSquidSwitchNU(true);
                getTaskExpressionsOrdered().add(thU);
            }

        }
        generateBuiltInExpressions();

        if (getTaskType().equals(GEOCHRON) && (parentPPM != null)) {
            getTaskExpressionsOrdered().add(parentPPM);
        }

        getTaskExpressionsOrdered().addAll(customExpressions);

        setChanged(true);

        updateRefMatCalibConstWMeanExpressions(isSquidAllowsAutoExclusionOfSpots());

        updateAllExpressions(true);
        processAndSortExpressions();
        updateAllExpressions(true);
        setupSquidSessionSpecsAndReduceAndReport(false);
        // prepares for second pass when needed
        setChanged(true);
    }

    void evaluateExpressionForSpotSet(
            ExpressionTreeInterface expressionTree,
            List<ShrimpFractionExpressionInterface> spotsForExpression) throws SquidException;

    void processAndSortExpressions();

    default boolean isPbU() {
        return (getParentNuclide().contains("238"));
    }

    /**
     * @return the directAltPD
     */
    boolean isDirectAltPD();

    /**
     * @param directAltPD the directAltPD to set
     */
    void setDirectAltPD(boolean directAltPD);

    /**
     * @return the useCalculatedAvParentElementConcenConst
     */
    boolean isUseCalculatedAvParentElementConcenConst();

    /**
     * @param useCalculatedAv_ParentElement_ConcenConst
     */
    void setUseCalculatedAvParentElementConcenConst(boolean useCalculatedAv_ParentElement_ConcenConst);

    /**
     * @return the selectedIndexIsotope
     */
    Squid3Constants.IndexIsoptopesEnum getSelectedIndexIsotope();

    /**
     * @param selectedIndexIsotope the selectedIndexIsotope to set
     */
    void setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum selectedIndexIsotope);

    Expression getExpressionByName(String name);

    void generateBuiltInExpressions();

    void produceSummaryReportsForGUI();

    /**
     * @return the massMinuends
     */
    List<MassStationDetail> getMassMinuends();

    /**
     * @param massMinuends the massMinuends to set
     */
    void setMassMinuends(List<MassStationDetail> massMinuends);

    /**
     * @return the massSubtrahends
     */
    List<MassStationDetail> getMassSubtrahends();

    /**
     * @param massSubtrahends the massSubtrahends to set
     */
    void setMassSubtrahends(List<MassStationDetail> massSubtrahends);

    /**
     * @return the showTimeNormalized
     */
    boolean isShowTimeNormalized();

    /**
     * @param showTimeNormalized the showTimeNormalized to set
     */
    void setShowTimeNormalized(boolean showTimeNormalized);

    /**
     * @return the showPrimaryBeam
     */
    boolean isShowPrimaryBeam();

    /**
     * @param showPrimaryBeam the showPrimaryBeam to set
     */
    void setShowPrimaryBeam(boolean showPrimaryBeam);

    /**
     * @return the showQt1y
     */
    boolean isShowQt1y();

    /**
     * @param aShowQt1y the showQt1y to set
     */
    void setShowQt1y(boolean aShowQt1y);

    /**
     * @return the showQt1z
     */
    boolean isShowQt1z();

    /**
     * @param aShowQt1z the showQt1z to set
     */
    void setShowQt1z(boolean aShowQt1z);

    boolean expressionIsNuSwitched(String expressionName);

    /**
     * @return showSpotLabels
     */
    boolean isShowSpotLabels();

    /**
     * @param showSpotLabels
     */
    void setShowSpotLabels(boolean showSpotLabels);

    /**
     * @return the mapOfUnknownsBySampleNames
     */
    Map<String, List<ShrimpFractionExpressionInterface>> getMapOfUnknownsBySampleNames();

    /**
     * @param prawnChanged the prawnChanged to set
     */
    void setPrawnChanged(boolean prawnChanged);

    /**
     * @return the squidAllowsAutoExclusionOfSpots
     */
    boolean isSquidAllowsAutoExclusionOfSpots();

    /**
     * @param squidAllowsAutoExclusionOfSpots the
     *                                        squidAllowsAutoExclusionOfSpots to set
     */
    void setSquidAllowsAutoExclusionOfSpots(boolean squidAllowsAutoExclusionOfSpots);

    /**
     * @return the extPErr
     */
    double getExtPErrU();

    /**
     * @param extPErr the extPErr to set
     */
    void setExtPErrU(double extPErr);

    /**
     * @return the extPErrTh
     */
    double getExtPErrTh();

    /**
     * @param extPErrTh the extPErrTh to set
     */
    void setExtPErrTh(double extPErrTh);

    Map<String, ExpressionTreeInterface> getNamedSpotLookupFieldsMap();

    Map<String, ExpressionTreeInterface> getNamedSpotMetaDataFieldsMap();

    /**
     * @return the specialSquidFourExpressionsMap
     */
    Map<String, String> getSpecialSquidFourExpressionsMap();

    /**
     * @param specialSquidFourExpressionsMap the specialSquidFourExpressionsMap
     *                                       to set
     */
    void setSpecialSquidFourExpressionsMap(Map<String, String> specialSquidFourExpressionsMap);

    /**
     * @param taskDesign   the value of taskDesign
     * @param taskSkeleton the value of taskSkeleton
     */
    void updateTaskFromTaskDesign(TaskDesign taskDesign, boolean taskSkeleton) throws SquidException;

    /**
     * @param taskDesign       the value of taskDesign
     * @param includeCustomExp the value of includeCustomExp
     */
    void updateTaskDesignFromTask(TaskDesign taskDesign, boolean includeCustomExp);

    /**
     * @return the delimiterForUnknownNames
     */
    String getDelimiterForUnknownNames();

    /**
     * @param delimiterForUnknownNames the delimiterForUnknownNames to set
     */
    void setDelimiterForUnknownNames(String delimiterForUnknownNames);

    String printExpressionRequiresGraph(Expression exp);

    String printExpressionProvidesGraph(Expression exp);

    void generateMapOfUnknownsBySampleNames();

    /**
     * @param refreshCommonLeadModel         the value of refreshCommonLeadModel
     * @param refreshPhysicalConstantsModel  the value of
     *                                       refreshPhysicalConstantsModel
     * @param refreshReferenceMaterialsModel the value of
     *                                       refreshReferenceMaterialsModel
     */
    void refreshParametersFromModels(boolean refreshCommonLeadModel, boolean refreshPhysicalConstantsModel, boolean refreshReferenceMaterialsModel) throws SquidException;

    /**
     * @return the missingExpressionsByName
     */
    List<String> getMissingExpressionsByName();

    /**
     * @return the roundingForSquid3
     */
    boolean isRoundingForSquid3();

    /**
     * @param roundingForSquid3 the roundingForSquid3 to set
     */
    void setRoundingForSquid3(boolean roundingForSquid3);

    List<SquidReportTableInterface> getSquidReportTablesRefMat();

    void setSquidReportTablesRefMat(List<SquidReportTableInterface> squidReportTablesRefMat);

    List<SquidReportTableInterface> getSquidReportTablesUnknown();

    void setSquidReportTablesUnknown(List<SquidReportTableInterface> squidReportTablesUnknown);

    /**
     * @return the selectedRefMatReportModel
     */
    SquidReportTableInterface getSelectedRefMatReportModel() throws SquidException;

    /**
     * @param selectedRefMatReportModel the selectedRefMatReportModel to set
     */
    void setSelectedRefMatReportModel(SquidReportTableInterface selectedRefMatReportModel);

    /**
     * @return the selectedUnknownReportModel
     */
    SquidReportTableInterface getSelectedUnknownReportModel() throws SquidException;

    /**
     * @param selectedUnknownReportModel the selectedUnknownReportModel to set
     */
    void setSelectedUnknownReportModel(SquidReportTableInterface selectedUnknownReportModel);

    /**
     * @return the overcountCorrectionType
     */
    Squid3Constants.OvercountCorrectionTypes getOvercountCorrectionType();

    /**
     * @param overcountCorrectionType the overcountCorrectionType to set
     */
    void setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes overcountCorrectionType);

    void updateAllSpotsWithCurrentCommonPbModel() throws SquidException;

    /**
     * @param excelExpression
     * @return boolean whether this expression contains a named ratio and thus
     * could use NU handling
     */
    default boolean expressionTreeIsCandidateForSwitchNU(String excelExpression) {
        boolean retVal = false;

        // june 2022 added WM condition
        if (!excelExpression.toUpperCase(Locale.ROOT).contains("WTDAV")) {
            for (String ratioName : getRatioNames()) {
                if (excelExpression.contains(ratioName)) {
                    // check to make sure excelExpression is not just a ratio
                    retVal = (excelExpression.trim().compareTo("[\"" + ratioName + "\"]") != 0);
                    break;
                }
            }
        }

        return retVal;
    }
}