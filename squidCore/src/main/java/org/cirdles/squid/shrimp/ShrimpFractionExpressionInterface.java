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

import java.util.List;
import java.util.Map;
import org.cirdles.squid.tasks.evaluationEngines.TaskExpressionEvaluatedPerSpotPerScanModelInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public interface ShrimpFractionExpressionInterface {

    /**
     * @return the referenceMaterial
     */
    public boolean isReferenceMaterial();

    /**
     *
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
     *
     * @return
     */
    public String getFractionID();

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

    /**
     * Used by reflection in expression evaluations by VariableNode, for example
     *
     * @param name
     * @return double [1][2] containing ratio value and 1-sigma abs uncertainty
     */
    public double[][] getIsotopicRatioValuesByStringName(String name);

    // May 2017 for temp backward comp with ET_Redux
    /**
     * @return the nameOfMount
     */
    public String getNameOfMount();

    /**
     * @return the dateTimeMilliseconds
     */
    public long getDateTimeMilliseconds();
    
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

}
