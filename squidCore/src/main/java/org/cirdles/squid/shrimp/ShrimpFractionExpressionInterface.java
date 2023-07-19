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
package org.cirdles.squid.shrimp;

import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.tasks.evaluationEngines.TaskExpressionEvaluatedPerSpotPerScanModelInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

import java.util.List;
import java.util.Map;

/**
 * @author James F. Bowring
 */
public interface ShrimpFractionExpressionInterface {

    /**
     * @return the referenceMaterial
     */
    boolean isReferenceMaterial();

    /**
     * @return
     */
    boolean isConcentrationReferenceMaterial();

    /**
     * @return the pkInterp
     */
    double[][] getReducedPkHt();

    /**
     * @return the pkInterpFerr
     */
    double[][] getReducedPkHtFerr();

    /**
     * @return
     */
    String getFractionID();

    /**
     * @return the spotNumber
     */
    int getSpotNumber();

    /**
     * @return the timeStampSec
     */
    double[][] getTimeStampSec();

    /**
     * @return the userLinFits
     */
    boolean isUserLinFits();

    // getters used by reflection - change names carefully

    /**
     * @return the pkInterpScanArray
     */
    double[] getPkInterpScanArray();

    /**
     * @param pkInterpScanArray the pkInterpScanArray to set
     */
    void setPkInterpScanArray(double[] pkInterpScanArray);

    /**
     * @return the totalCps
     */
    double[] getTotalCps();

    /**
     * @param totalCps the totalCps to set
     */
    void setTotalCps(double[] totalCps);

    double[] getNscansTimesCountTimeSec();

    /**
     * @return the taskExpressionsForScansEvaluated
     */
    List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> getTaskExpressionsForScansEvaluated();

    /**
     * @param taskExpressionsEvaluated the taskExpressionsEvaluated to set
     */
    void setTaskExpressionsForScansEvaluated(List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsEvaluated);

    /**
     * @return the taskExpressionsEvaluationsPerSpot
     */
    Map<ExpressionTreeInterface, double[][]> getTaskExpressionsEvaluationsPerSpot();

    /**
     * Used by Reflection in
     * org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions
     *
     * @param fieldName
     * @return
     */
    double[][] getTaskExpressionsEvaluationsPerSpotByField(String fieldName);

    Map<ExpressionTreeInterface, String> getTaskExpressionsMetaDataPerSpot();

    /**
     * Used by reflection in expression evaluations by VariableNode, for example
     *
     * @param name
     * @return double [1][2] containing ratio value and 1-sigma abs uncertainty
     */
    double[][] getIsotopicRatioValuesByStringName(String name);

    double[][] getOriginalIsotopicRatioValuesByStringName(String name);

    // May 2017 for temp backward comp with ET_Redux

    /**
     * @return the nameOfMount
     */
    String getNameOfMount();

    long getDateTimeMillisecondsLong();

    /**
     * @return the dateTimeMilliseconds
     */
    String getDateTimeMilliseconds();

    String getDateTime();

    /**
     * @return the totalCounts
     */
    double[][] getTotalCounts();

    /**
     * @return the totalCountsOneSigmaAbs
     */
    double[][] getTotalCountsOneSigmaAbs();

    /**
     * @return the namesOfSpecies
     */
    String[] getNamesOfSpecies();

    /**
     * @return the netPkCps
     */
    double[][] getNetPkCps();

    /**
     * @return the pkFerr
     */
    double[][] getPkFerr();

    double getHours();

    int getSpotIndex();

    /**
     * @return the primaryBeam
     */
    double getPrimaryBeam();

    /**
     * @return the stageX
     */
    int getStageX();

    /**
     * @return the stageY
     */
    int getStageY();

    /**
     * @return the stageZ
     */
    int getStageZ();

    /**
     * @return the qt1Y
     */
    int getQt1Y();

    /**
     * @return the qt1Z
     */
    int getQt1Z();

    /**
     * @return the selected
     */
    boolean isSelected();

    /**
     * @param selected the selected to set
     */
    void setSelected(boolean selected);

    /**
     * @return the com_64
     */
    double getCom_206Pb204Pb() throws SquidException;

    /**
     * @param com_64 the com_64 to set
     */
    void setCom_206Pb204Pb(double com_64) throws SquidException;

    /**
     * @return the com_207Pb206Pb
     */
    double getCom_207Pb206Pb() throws SquidException;

    /**
     * @param com_207Pb206Pb the com_207Pb206Pb to set
     */
    void setCom_207Pb206Pb(double com_207Pb206Pb) throws SquidException;

    /**
     * @return the com_208Pb206Pb
     */
    double getCom_208Pb206Pb() throws SquidException;

    /**
     * @param com_208Pb206Pb the com_208Pb206Pb to set
     */
    void setCom_208Pb206Pb(double com_208Pb206Pb) throws SquidException;

    /**
     * @return the com_206Pb208Pb
     */
    double getCom_206Pb208Pb() throws SquidException;

    /**
     * @param com_206Pb208Pb the com_206Pb208Pb to set
     */
    void setCom_206Pb208Pb(double com_206Pb208Pb) throws SquidException;

    /**
     * @return the com_207Pb204Pb
     */
    double getCom_207Pb204Pb() throws SquidException;

    /**
     * @param com_207Pb204Pb the com_207Pb204Pb to set
     */
    void setCom_207Pb204Pb(double com_207Pb204Pb) throws SquidException;

    /**
     * @return the com_208Pb204Pb
     */
    double getCom_208Pb204Pb() throws SquidException;

    /**
     * @param com_208Pb204Pb the com_208Pb204Pb to set
     */
    void setCom_208Pb204Pb(double com_208Pb204Pb) throws SquidException;

    ParametersModel getCommonLeadModel() throws SquidException;

    void setCommonLeadModel(ParametersModel commonLeadModel) throws SquidException;

    /**
     * @return the commonLeadSpecsForSpot
     */
    CommonLeadSpecsForSpot getCommonLeadSpecsForSpot() throws SquidException;

    /**
     * @param overcountCorrectionIsotope the overcountCorrectionIsotope to set
     */
    void setOvercountCorrectionIsotope(Squid3Constants.IndexIsoptopesEnum overcountCorrectionIsotope);

    String getOverCtCorr();

    String getCommonPbCorrMetaData();

    String getComPbCorrSKTargetAge();

    String getSelectedAgeExpressionName() throws SquidException;

    /**
     * Used in custom reports per issue # 701.
     *
     * @return
     * @throws SquidException
     */
    String getComPbSelectedAgeType() throws SquidException;
}