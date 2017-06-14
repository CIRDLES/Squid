/*
 * Copyright 2006-2017 CIRDLES.org.
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
import org.cirdles.squid.tasks.TaskExpressionEvaluatedPerSpotPerScanModelInterface;

/**
 *
 * @author James F. Bowring
 */
public interface ShrimpFractionExpressionInterface {

    /**
     *
     * @param speciesName
     * @return
     */
    public int getIndexOfSpeciesByName(IsotopeNames speciesName);

    /**
     * @return the referenceMaterial
     */
    public boolean isReferenceMaterial();

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

    /**
     * @param taskExpressionsEvaluated the taskExpressionsEvaluated to set
     */
    public void setTaskExpressionsForScansEvaluated(List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsEvaluated);

    /**
     * @return the taskExpressionsForScansEvaluated
     */
    public List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> getTaskExpressionsForScansEvaluated();

    /**
     * @param fieldName
     * @return the taskExpressionsEvaluationsPerSpot
     */
    public double[][] getTaskExpressionsEvaluationsPerSpotByField(String fieldName);

    /**
     * @return the taskExpressionsEvaluationsPerSpot
     */
    public Map<String, double[][]> getTaskExpressionsEvaluationsPerSpot();

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

    /**
     * @return the isotopicRatios
     */
    public Map<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> getIsotopicRatios();
}
