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
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.parameters.parameterModels.pbBlankICModels.PbBlankICModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

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
     * @param shrimpFractions
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
    public void setPrawnFile(PrawnFile prawnFile);

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
    String getType();

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    List<MassStationDetail> makeListOfMassStationDetails();

    public String printTaskAudit();

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

    public void removeExpression(Expression expression);

    public void restoreRemovedExpressions();

    public void addExpression(Expression exp);
    
    public void setReferenceMaterial(ReferenceMaterial refMat);
    public ReferenceMaterial getReferenceMaterial();
    public void setPhysicalConstantsModel(PhysicalConstantsModel physConst);
    public PhysicalConstantsModel getPhysicalConstantsModel();
    public void setCommonPbModel(PbBlankICModel model);
    public PbBlankICModel getCommonPbModel();

    /**
     * @param type the type to set
     */
    public void setType(String type);

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

    /**
     *
     * @param expression Name of the expression to test
     * @return True if the expression exists, false if not
     */
    public boolean expressionExists(Expression expression);

    /**
     * @param taskExpressionTreesOrdered the taskExpressionTreesOrdered to set
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

    public List<ShrimpFractionExpressionInterface> processRunFractions(PrawnFile prawnFile, SquidSessionModel squidSessionSpecs);

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
     * @param filterForRefMatSpotNames the filterForRefMatSpotNames to set
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

    public void updateAllExpressions();

    /**
     * @param reportsEngine the reportsEngine to set
     */
    public void setReportsEngine(CalamariReportsEngine reportsEngine);

    /**
     * The original Calamari Reports
     */
    public void produceSanityReportsToFiles();

    public void updateRatioNames(String[] ratioNames);

    public void updateAffectedExpressions(Expression sourceExpression);

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
     * @return the useCalculated_pdMeanParentEleA
     */
    public boolean isUseCalculated_pdMeanParentEleA();

    /**
     * @param useCalculated_pdMeanParentEleA the useCalculated_pdMeanParentEleA
     * to set
     */
    public void setUseCalculated_pdMeanParentEleA(boolean useCalculated_pdMeanParentEleA);

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
     * @param extPErr the extPErr to set
     */
    public void setExtPErr(double extPErr);
      /**
     * @return the extPErr
     */
    public double getExtPErr() ;
}
