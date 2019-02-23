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

import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import com.thoughtworks.xstream.XStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.shrimp.ShrimpDataFileInterface;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.AV_PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.utilities.stateUtilities.SquidUserPreferences;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT;

/**
 *
 * @author James F. Bowring
 */
public interface TaskInterface {

    public ExpressionTreeInterface findNamedExpression(String ratioName);

    public Expression generateExpressionFromRawExcelStyleText(String name, String originalExpressionText, boolean eqnSwitchNU, boolean referenceMaterialValue, boolean parameterValue);

    public void updateRefMatCalibConstWMeanExpressions(boolean squidAllowsAutoExclusionOfSpots);

    public void buildSquidSpeciesModelList();

    /**
     *
     * @param xstream
     */
    public void customizeXstream(XStream xstream);

    /**
     *
     */
    public void evaluateTaskExpressions();

    /**
     * @return the authorName
     */
    public String getAuthorName();

    /**
     * @return the dateRevised
     */
    public long getDateRevised();

    /**
     * @return the description
     */
    public String getDescription();

    /**
     * @return the labName
     */
    public String getLabName();

    /**
     * @return the name
     */
    public String getName();

    /**
     * @return the provenance
     */
    public String getProvenance();

    /**
     * @return the ratioNames
     */
    public List<String> getRatioNames();

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    public Map<Integer, MassStationDetail> getMapOfIndexToMassStationDetails();

    /**
     * @return the nominalMasses
     */
    public List<String> getNominalMasses();

    /**
     * @param prawnFile the prawnFile to set
     */
    public void setPrawnFile(ShrimpDataFileInterface prawnFile);

    public SquidSpeciesModel lookUpSpeciesByName(String isotopeName);

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
     * @return the type
     */
    TaskTypeEnum getType();

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    List<MassStationDetail> makeListOfMassStationDetails();

    public String printTaskAudit();

    public String printTaskSummary();

    public int selectBackgroundSpeciesReturnPreviousIndex(SquidSpeciesModel ssm);

    /**
     * @param authorName the authorName to set
     */
    public void setAuthorName(String authorName);

    /**
     * @param dateRevised the dateRevised to set
     */
    public void setDateRevised(long dateRevised);

    /**
     * @param description the description to set
     */
    public void setDescription(String description);

    /**
     * @param labName the labName to set
     */
    public void setLabName(String labName);

    /**
     * @param name the name to set
     */
    public void setName(String name);

    /**
     * @param provenance the provenance to set
     */
    public void setProvenance(String provenance);

    /**
     * @param nominalMasses the nominalMasses to set
     */
    public void setNominalMasses(List<String> nominalMasses);

    /**
     * @param ratioNames the ratioNames to set
     */
    public void setRatioNames(List<String> ratioNames);

    /**
     * @param taskExpressionsEvaluationsPerSpotSet the
     * taskExpressionsEvaluationsPerSpotSet to set
     */
    public void setTaskExpressionsEvaluationsPerSpotSet(Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet);

    public void removeExpression(Expression expression, boolean reprocessExpressions);

    public void restoreRemovedExpressions();

    public void addExpression(Expression exp, boolean reprocessExpressions);

    public void setReferenceMaterial(ParametersModel refMat);

    public ParametersModel getReferenceMaterialModel();

    public void setPhysicalConstantsModel(ParametersModel physConst);

    public ParametersModel getPhysicalConstantsModel();

    public void setCommonPbModel(ParametersModel model);

    public ParametersModel getCommonPbModel();

    public void setConcentrationReferenceMaterial(ParametersModel refMat);

    public ParametersModel getConcentrationReferenceMaterialModel();

    /**
     * @param type the type to set
     */
    public void setType(TaskTypeEnum type);

    public void setupSquidSessionSpecsAndReduceAndReport();

    public void updateTableOfSelectedRatiosByMassStationIndex(int row, int col, boolean selected);

    /**
     * @return the NAMED_EXPRESSIONS_MAP
     */
    public Map<String, ExpressionTreeInterface> getNamedExpressionsMap();

    /**
     * @return the squidSessionModel
     */
    public SquidSessionModel getSquidSessionModel();

    /**
     * @return the taskExpressionsOrdered
     */
    public List<Expression> getTaskExpressionsOrdered();

    public List<Expression> getCustomTaskExpressions();

    /**
     *
     * @param expression Name of the expression to test
     * @return True if the expression exists, false if not
     */
    public boolean expressionExists(Expression expression);

    /**
     * @param taskExpressionsOrdered
     */
    public void setTaskExpressionsOrdered(List<Expression> taskExpressionsOrdered);

    /**
     * @param changed the changed to set
     */
    public void setChanged(boolean changed);

    /**
     * @return the shrimpFractions
     */
    public List<ShrimpFractionExpressionInterface> getShrimpFractions();

    public List<ShrimpFractionExpressionInterface> processRunFractions(ShrimpDataFileInterface prawnFile, SquidSessionModel squidSessionSpecs);

    /**
     * @return the referenceMaterialSpots
     */
    public List<ShrimpFractionExpressionInterface> getReferenceMaterialSpots();

    /**
     * @return the unknownSpots
     */
    public List<ShrimpFractionExpressionInterface> getUnknownSpots();

    /**
     * @return the concentrationReferenceMaterialSpots
     */
    public List<ShrimpFractionExpressionInterface> getConcentrationReferenceMaterialSpots();

    /**
     * @param filterForRefMatSpotNames the filterForRefMatSpotNames to set
     */
    public void setFilterForRefMatSpotNames(String filterForRefMatSpotNames);

    /**
     * @param filterForConcRefMatSpotNames
     */
    public void setFilterForConcRefMatSpotNames(String filterForConcRefMatSpotNames);

    /**
     * @param filtersForUnknownNames the filtersForUnknownNames to set
     */
    public void setFiltersForUnknownNames(Map<String, Integer> filtersForUnknownNames);

    /**
     * @return the useSBM
     */
    public boolean isUseSBM();

    /**
     * @param useSBM the useSBM to set
     */
    public void setUseSBM(boolean useSBM);

    /**
     * @return the userLinFits
     */
    public boolean isUserLinFits();

    /**
     * @param userLinFits the userLinFits to set
     */
    public void setUserLinFits(boolean userLinFits);

    /**
     * @return the indexOfBackgroundSpecies
     */
    public int getIndexOfBackgroundSpecies();

    /**
     * @param indexOfBackgroundSpecies the indexOfBackgroundSpecies to set
     */
    public void setIndexOfBackgroundSpecies(int indexOfBackgroundSpecies);

    public void updateAllExpressions(boolean reprocessExpressions);

    /**
     * @param reportsEngine the reportsEngine to set
     */
    public void setReportsEngine(CalamariReportsEngine reportsEngine);

    /**
     * The original Calamari Reports
     */
    public void produceSanityReportsToFiles();

    public void updateRatioNames(String[] ratioNames);

    public void updateAffectedExpressions(Expression sourceExpression, boolean reprocessExpressions);

    public void applyTaskIsotopeLabelsToMassStations();

    public void populateTableOfSelectedRatiosFromRatiosList();

    public void updateTableOfSelectedRatiosByRowOrCol(int row, int col, boolean selected);

    /**
     * @return the namedConstantsMap
     */
    public Map<String, ExpressionTreeInterface> getNamedConstantsMap();

    /**
     *
     * @return namedParametersMap
     */
    public Map<String, ExpressionTreeInterface> getNamedParametersMap();

    public void setIndexOfTaskBackgroundMass(int indexOfTask25BackgroundMass);

    public void applyMassStationLabelsToTask();

    /**
     *
     * @return
     */
    public String getParentNuclide();

    /**
     *
     * @param parentNuclide
     */
    public void setParentNuclide(String parentNuclide);

    public default void applyDirectives() {
        // todo change dictionary to preferences
        SquidUserPreferences squidUserPreferences = SquidPersistentState.getExistingPersistentState().getSquidUserPreferences();
        // save the magic 4 expressions
        String uThU_Expression;
        if (getExpressionByName(UNCOR206PB238U_CALIB_CONST) != null) {
            uThU_Expression = getExpressionByName(UNCOR206PB238U_CALIB_CONST).getExcelExpressionString();
        } else {
            uThU_Expression = BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION;
        }

        String uThTh_Expression;
        if (getExpressionByName(UNCOR208PB232TH_CALIB_CONST) != null) {
            uThTh_Expression = getExpressionByName(UNCOR208PB232TH_CALIB_CONST).getExcelExpressionString();
        } else {
            uThTh_Expression = BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION;
        }

        String parentPPM_Expression;
        if (getExpressionByName(PARENT_ELEMENT_CONC_CONST) != null) {
            parentPPM_Expression = getExpressionByName(PARENT_ELEMENT_CONC_CONST).getExcelExpressionString();
        } else {
            parentPPM_Expression = BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION;
        }

        String parentPPMmean_Expression;
        if (getExpressionByName(AV_PARENT_ELEMENT_CONC_CONST) != null) {
            parentPPMmean_Expression = getExpressionByName(AV_PARENT_ELEMENT_CONC_CONST).getExcelExpressionString();
        } else {
            parentPPMmean_Expression = BuiltInExpressionsDataDictionary.AV_PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION;
        }
        
        Expression originalTHU = getExpressionByName(TH_U_EXP_DEFAULT);

        // need to remove stored expression results on fractions to clear the decks   
        getShrimpFractions().forEach((spot) -> {
            spot.getTaskExpressionsForScansEvaluated().clear();
            spot.getTaskExpressionsEvaluationsPerSpot().clear();
        });
        // clear task expressions
        setTaskExpressionsEvaluationsPerSpotSet(new TreeMap<>(String.CASE_INSENSITIVE_ORDER));

        List<Expression> customExpressions = getCustomTaskExpressions();

        getTaskExpressionsOrdered().clear();

        // write the magic 4 expressions
        // TODO: expressions need to come from preferences and/or models
        Expression parentPPM = BuiltInExpressionsFactory.buildExpression(
                PARENT_ELEMENT_CONC_CONST, parentPPM_Expression, true, true, false);
        parentPPM.setSquidSwitchNU(true);

        Expression parentPPMmean = BuiltInExpressionsFactory.buildExpression(
                AV_PARENT_ELEMENT_CONC_CONST, parentPPMmean_Expression, true, true, true);
        parentPPMmean.setSquidSwitchNU(false);
        parentPPMmean.getExpressionTree().setSquidSwitchConcentrationReferenceMaterialCalculation(true);

        Expression uThU = BuiltInExpressionsFactory.buildExpression(
                UNCOR206PB238U_CALIB_CONST, uThU_Expression, true, true, false);
        uThU.setSquidSwitchNU(true);

        Expression uThTh = BuiltInExpressionsFactory.buildExpression(
                UNCOR208PB232TH_CALIB_CONST, uThTh_Expression, true, true, false);
        uThTh.setSquidSwitchNU(true);

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
            String thU_DEFAULT_Expression;
            if (originalTHU != null) {
                thU_DEFAULT_Expression = originalTHU.getExcelExpressionString();
            } else {
                thU_DEFAULT_Expression = BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT_EXPRESSION;
            }

            Expression thU_RM = BuiltInExpressionsFactory.buildExpression(TH_U_EXP_RM, thU_DEFAULT_Expression, true, false, false);
            thU_RM.setSquidSwitchNU(true);
            getTaskExpressionsOrdered().add(thU_RM);

            Expression thU = BuiltInExpressionsFactory.buildExpression(TH_U_EXP, thU_DEFAULT_Expression, false, true, false);
            thU.setSquidSwitchNU(true);
            getTaskExpressionsOrdered().add(thU);
        }

        generateBuiltInExpressions();

        getTaskExpressionsOrdered().add(parentPPM);
        getTaskExpressionsOrdered().add(parentPPMmean);
        getTaskExpressionsOrdered().add(originalTHU);

        getTaskExpressionsOrdered().addAll(customExpressions);

        setChanged(true);

        updateAllExpressions(true);
        processAndSortExpressions();
        updateAllExpressions(true);
        setupSquidSessionSpecsAndReduceAndReport();
        // prepares for second pass when needed
        setChanged(true);
    }

    public void processAndSortExpressions();

    public default boolean isPbU() {
        return (getParentNuclide().contains("238"));
    }

    /**
     * @return the directAltPD
     */
    public boolean isDirectAltPD();

    /**
     * @param directAltPD the directAltPD to set
     */
    public void setDirectAltPD(boolean directAltPD);

    /**
     * @return the useCalculatedAv_ParentElement_ConcenConst
     */
    public boolean isUseCalculatedAv_ParentElement_ConcenConst();

    /**
     * @param useCalculatedAv_ParentElement_ConcenConst
     */
    public void setUseCalculatedAv_ParentElement_ConcenConst(boolean useCalculatedAv_ParentElement_ConcenConst);

    /**
     * @return the selectedIndexIsotope
     */
    public Squid3Constants.IndexIsoptopesEnum getSelectedIndexIsotope();

    /**
     * @param selectedIndexIsotope the selectedIndexIsotope to set
     */
    public void setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum selectedIndexIsotope);

    public Expression getExpressionByName(String name);

    public void generateBuiltInExpressions();

    public void produceSummaryReportsForGUI();

    /**
     * @return the massMinuends
     */
    public List<MassStationDetail> getMassMinuends();

    /**
     * @param massMinuends the massMinuends to set
     */
    public void setMassMinuends(List<MassStationDetail> massMinuends);

    /**
     * @return the massSubtrahends
     */
    public List<MassStationDetail> getMassSubtrahends();

    /**
     * @param massSubtrahends the massSubtrahends to set
     */
    public void setMassSubtrahends(List<MassStationDetail> massSubtrahends);

    /**
     * @return the showTimeNormalized
     */
    public boolean isShowTimeNormalized();

    /**
     * @param showTimeNormalized the showTimeNormalized to set
     */
    public void setShowTimeNormalized(boolean showTimeNormalized);

    /**
     * @return the showPrimaryBeam
     */
    public boolean isShowPrimaryBeam();

    /**
     * @param showPrimaryBeam the showPrimaryBeam to set
     */
    public void setShowPrimaryBeam(boolean showPrimaryBeam);

    /**
     * @return the showQt1y
     */
    public boolean isShowQt1y();

    /**
     * @param aShowQt1y the showQt1y to set
     */
    public void setShowQt1y(boolean aShowQt1y);

    /**
     * @return the showQt1z
     */
    public boolean isShowQt1z();

    /**
     * @param aShowQt1z the showQt1z to set
     */
    public void setShowQt1z(boolean aShowQt1z);

    public boolean expressionIsNuSwitched(String expressionName);

    /**
     * @return the mapOfUnknownsBySampleNames
     */
    public Map<String, List<ShrimpFractionExpressionInterface>> getMapOfUnknownsBySampleNames();

    /**
     * @param prawnChanged the prawnChanged to set
     */
    public void setPrawnChanged(boolean prawnChanged);

    /**
     * @return the squidAllowsAutoExclusionOfSpots
     */
    public boolean isSquidAllowsAutoExclusionOfSpots();

    /**
     * @param squidAllowsAutoExclusionOfSpots the
     * squidAllowsAutoExclusionOfSpots to set
     */
    public void setSquidAllowsAutoExclusionOfSpots(boolean squidAllowsAutoExclusionOfSpots);

    /**
     * @param extPErr the extPErr to set
     */
    public void setExtPErr(double extPErr);

    /**
     * @return the extPErr
     */
    public double getExtPErr();

    public String listBuiltInExpressions();

    public Map<String, ExpressionTreeInterface> getNamedSpotLookupFieldsMap();
}
