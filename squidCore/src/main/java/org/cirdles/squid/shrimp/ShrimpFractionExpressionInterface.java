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
    public boolean isReferenceMaterial();

    /**
     * @return
     */
    public boolean isConcentrationReferenceMaterial();

    /**
     * @return the pkInterp
     */
    public double[][] getReducedPkHt();

    /**
     * @return the pkInterpFerr
     */
    public double[][] getReducedPkHtFerr();

    /**
     * @return
     */
    public String getFractionID();

    /**
     * @return the spotNumber
     */
    public int getSpotNumber();

    /**
     * @return the timeStampSec
     */
    public double[][] getTimeStampSec();

    /**
     * @return the userLinFits
     */
    public boolean isUserLinFits();

    // getters used by reflection - change names carefully

    /**
     * @return the pkInterpScanArray
     */
    public double[] getPkInterpScanArray();

    /**
     * @param pkInterpScanArray the pkInterpScanArray to set
     */
    public void setPkInterpScanArray(double[] pkInterpScanArray);

    /**
     * @return the totalCps
     */
    public double[] getTotalCps();

    /**
     * @param totalCps the totalCps to set
     */
    public void setTotalCps(double[] totalCps);

    public double[] getNscansTimesCountTimeSec();

    /**
     * @param taskExpressionsEvaluated the taskExpressionsEvaluated to set
     */
    public void setTaskExpressionsForScansEvaluated(List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsEvaluated);

    /**
     * @return the taskExpressionsForScansEvaluated
     */
    public List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> getTaskExpressionsForScansEvaluated();

    /**
     * @return the taskExpressionsEvaluationsPerSpot
     */
    public Map<ExpressionTreeInterface, double[][]> getTaskExpressionsEvaluationsPerSpot();

    /**
     * Used by Reflection in
     * org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions
     *
     * @param fieldName
     * @return
     */
    public double[][] getTaskExpressionsEvaluationsPerSpotByField(String fieldName);

    public Map<ExpressionTreeInterface, String> getTaskExpressionsMetaDataPerSpot();

    /**
     * Used by reflection in expression evaluations by VariableNode, for example
     *
     * @param name
     * @return double [1][2] containing ratio value and 1-sigma abs uncertainty
     */
    public double[][] getIsotopicRatioValuesByStringName(String name);

    public double[][] getOriginalIsotopicRatioValuesByStringName(String name);

    // May 2017 for temp backward comp with ET_Redux

    /**
     * @return the nameOfMount
     */
    public String getNameOfMount();

    public long getDateTimeMillisecondsLong();

    /**
     * @return the dateTimeMilliseconds
     */
    public String getDateTimeMilliseconds();

    public String getDateTime();

    /**
     * @return the totalCounts
     */
    public double[][] getTotalCounts();

    /**
     * @return the totalCountsOneSigmaAbs
     */
    public double[][] getTotalCountsOneSigmaAbs();

    /**
     * @return the namesOfSpecies
     */
    public String[] getNamesOfSpecies();

    /**
     * @return the netPkCps
     */
    public double[][] getNetPkCps();

    /**
     * @return the pkFerr
     */
    public double[][] getPkFerr();

    public double getHours();

    public int getSpotIndex();

    /**
     * @return the primaryBeam
     */
    public double getPrimaryBeam();

    /**
     * @return the stageX
     */
    public int getStageX();

    /**
     * @return the stageY
     */
    public int getStageY();

    /**
     * @return the stageZ
     */
    public int getStageZ();

    /**
     * @return the qt1Y
     */
    public int getQt1Y();

    /**
     * @return the qt1Z
     */
    public int getQt1Z();

    /**
     * @return the selected
     */
    public boolean isSelected();

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected);

    /**
     * @return the com_64
     */
    public double getCom_206Pb204Pb() throws SquidException;

    /**
     * @param com_64 the com_64 to set
     */
    public void setCom_206Pb204Pb(double com_64) throws SquidException;

    /**
     * @return the com_207Pb206Pb
     */
    public double getCom_207Pb206Pb() throws SquidException;

    /**
     * @param com_207Pb206Pb the com_207Pb206Pb to set
     */
    public void setCom_207Pb206Pb(double com_207Pb206Pb) throws SquidException;

    /**
     * @return the com_208Pb206Pb
     */
    public double getCom_208Pb206Pb() throws SquidException;

    /**
     * @param com_208Pb206Pb the com_208Pb206Pb to set
     */
    public void setCom_208Pb206Pb(double com_208Pb206Pb) throws SquidException;

    /**
     * @return the com_206Pb208Pb
     */
    public double getCom_206Pb208Pb() throws SquidException;

    /**
     * @param com_206Pb208Pb the com_206Pb208Pb to set
     */
    public void setCom_206Pb208Pb(double com_206Pb208Pb) throws SquidException;

    /**
     * @return the com_207Pb204Pb
     */
    public double getCom_207Pb204Pb() throws SquidException;

    /**
     * @param com_207Pb204Pb the com_207Pb204Pb to set
     */
    public void setCom_207Pb204Pb(double com_207Pb204Pb) throws SquidException;

    /**
     * @return the com_208Pb204Pb
     */
    public double getCom_208Pb204Pb() throws SquidException;

    /**
     * @param com_208Pb204Pb the com_208Pb204Pb to set
     */
    public void setCom_208Pb204Pb(double com_208Pb204Pb) throws SquidException;

    public ParametersModel getCommonLeadModel() throws SquidException;

    public void setCommonLeadModel(ParametersModel commonLeadModel) throws SquidException;

    /**
     * @return the commonLeadSpecsForSpot
     */
    public CommonLeadSpecsForSpot getCommonLeadSpecsForSpot() throws SquidException;

    /**
     * @return the overcountCorrectionIsotope
     */
    public Squid3Constants.IndexIsoptopesEnum getOvercountCorrectionIsotope() throws SquidException;

    /**
     * @param overcountCorrectionIsotope the overcountCorrectionIsotope to set
     */
    public void setOvercountCorrectionIsotope(Squid3Constants.IndexIsoptopesEnum overcountCorrectionIsotope);

    public String getOverCtCorr();

    public String getCommonPbCorrMetaData();

    public String getComPbCorrSKTargetAge();

    public String getSelectedAgeExpressionName() throws SquidException;

    /**
     * Used in custom reports per issue # 701.
     * @return
     * @throws SquidException
     */
    public String getComPbSelectedAgeType() throws SquidException;
}