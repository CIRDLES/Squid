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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.cirdles.squid.tasks.evaluationEngines.TaskExpressionEvaluatedPerSpotPerScanModelInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.utilities.conversionUtilities.CloningUtilities.clone2dArray;

/**
 *
 * @author James F. Bowring
 */
public class ShrimpFraction implements Serializable, ShrimpFractionExpressionInterface {

    private static final long serialVersionUID = -8414997835056044184L;

    private String fractionID;
    private int spotNumber;
    private String nameOfMount;
    private long dateTimeMilliseconds;
    private double hours;
    private int deadTimeNanoseconds;
    private int sbmZeroCps;
    private int stageX;
    private int stageY;
    private int stageZ;
    private int qt1Y;
    private int qt1Z;
    private double primaryBeam;
    private double[] countTimeSec;
    private String[] namesOfSpecies;
    private int peakMeasurementsCount;
    private SortedSet<SquidRatiosModel> isotopicRatiosII;
    private int[][] rawPeakData;
    private int[][] rawSBMData;
    private double[][] totalCounts;
    private double[][] totalCountsOneSigmaAbs;
    private double[][] totalCountsSBM;
    private double[][] timeStampSec;
    private double[][] trimMass;
    private double[] totalCps;
    private double[][] netPkCps;
    private double[][] pkFerr;
    private boolean referenceMaterial;
    private boolean concentrationReferenceMaterial;
    private boolean useSBM;
    private boolean userLinFits;

    private double[][] reducedPkHt;
    private double[][] reducedPkHtFerr;

    private double[] pkInterpScanArray;

    private List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsForScansEvaluated;
    private Map<ExpressionTreeInterface, double[][]> taskExpressionsEvaluationsPerSpot;

    private boolean selected;
    
    private int countOfNonPositiveSBMCounts;

    /**
     *
     */
    public ShrimpFraction() {
        fractionID = "NONE";
        spotNumber = -1;
        nameOfMount = "NONE";
        dateTimeMilliseconds = 0l;
        hours = 0.0;
        deadTimeNanoseconds = 0;
        sbmZeroCps = 0;
        countTimeSec = new double[0];
        namesOfSpecies = new String[0];
        peakMeasurementsCount = 0;
        isotopicRatiosII = new TreeSet<>();
        rawPeakData = new int[0][0];
        rawSBMData = new int[0][0];
        totalCounts = new double[0][0];
        totalCountsSBM = new double[0][0];
        timeStampSec = new double[0][0];
        trimMass = new double[0][0];
        totalCps = new double[0];
        netPkCps = new double[0][0];
        pkFerr = new double[0][0];
        referenceMaterial = false;
        concentrationReferenceMaterial = false;

        reducedPkHt = new double[0][0];
        reducedPkHtFerr = new double[0][0];

        pkInterpScanArray = new double[0];

        taskExpressionsForScansEvaluated = new ArrayList<>();

        taskExpressionsEvaluationsPerSpot = new HashMap<>();

        this.selected = true;

    }

    /**
     *
     * @param fractionID
     * @param isotopicRatios
     * @param isotopicRatiosII
     */
    public ShrimpFraction(String fractionID, SortedSet<SquidRatiosModel> isotopicRatiosII) {
        this();
        this.fractionID = fractionID;
        this.isotopicRatiosII = isotopicRatiosII;
    }

    public void calculateSpotHours(long baseTimeOfFirstRefMatForCalcHoursField) {
        long deltaTime = dateTimeMilliseconds - baseTimeOfFirstRefMatForCalcHoursField;

        BigDecimal deltaTimeBD = new BigDecimal(String.valueOf(deltaTime));

        BigDecimal hrs = deltaTimeBD.divide(new BigDecimal("3600000"), MathContext.DECIMAL32).setScale(3, RoundingMode.HALF_UP);

        hours = hrs.doubleValue();
    }

    /**
     * This method is needed by expression processing and referred to by its
     * String name.
     *
     * @return double elapsed time in hours (hh.###) since timestamp of first
     * reference material
     */
    @Override
    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    /**
     * @return the fractionID
     */
    @Override
    public String getFractionID() {
        return fractionID;
    }

    /**
     * @param fractionID the fractionID to set
     */
    public void setFractionID(String fractionID) {
        this.fractionID = fractionID;
    }

    /**
     * @return the spotNumber
     */
    public int getSpotNumber() {
        return spotNumber;
    }

    /**
     * @param spotNumber the spotNumber to set
     */
    public void setSpotNumber(int spotNumber) {
        this.spotNumber = spotNumber;
    }

    /**
     * @return the nameOfMount
     */
    @Override
    public String getNameOfMount() {
        return nameOfMount;
    }

    /**
     * @param nameOfMount the nameOfMount to set
     */
    public void setNameOfMount(String nameOfMount) {
        this.nameOfMount = nameOfMount;
    }

    /**
     * @return the dateTimeMilliseconds
     */
    @Override
    public long getDateTimeMilliseconds() {
        return dateTimeMilliseconds;
    }

    /**
     * @param dateTimeMilliseconds the dateTimeMilliseconds to set
     */
    public void setDateTimeMilliseconds(long dateTimeMilliseconds) {
        this.dateTimeMilliseconds = dateTimeMilliseconds;
    }

    /**
     * @return the deadTimeNanoseconds
     */
    public int getDeadTimeNanoseconds() {
        return deadTimeNanoseconds;
    }

    /**
     * @param aDeadTimeNanoseconds the deadTimeNanoseconds to set
     */
    public void setDeadTimeNanoseconds(int aDeadTimeNanoseconds) {
        deadTimeNanoseconds = aDeadTimeNanoseconds;
    }

    /**
     * @return the sbmZeroCps
     */
    public int getSbmZeroCps() {
        return sbmZeroCps;
    }

    /**
     * @param sbmZeroCps the sbmZeroCps to set
     */
    public void setSbmZeroCps(int sbmZeroCps) {
        this.sbmZeroCps = sbmZeroCps;
    }

    /**
     * @return the stageX
     */
    @Override
    public int getStageX() {
        return stageX;
    }

    /**
     * @param stageX the stageX to set
     */
    public void setStageX(int stageX) {
        this.stageX = stageX;
    }

    /**
     * @return the stageY
     */
    @Override
    public int getStageY() {
        return stageY;
    }

    /**
     * @param stageY the stageY to set
     */
    public void setStageY(int stageY) {
        this.stageY = stageY;
    }

    /**
     * @return the stageZ
     */
    @Override
    public int getStageZ() {
        return stageZ;
    }

    /**
     * @param stageZ the stageZ to set
     */
    public void setStageZ(int stageZ) {
        this.stageZ = stageZ;
    }

    /**
     * @return the qt1Y
     */
    public int getQt1Y() {
        return qt1Y;
    }

    /**
     * @param qt1Y the qt1Y to set
     */
    public void setQt1Y(int qt1Y) {
        this.qt1Y = qt1Y;
    }

    /**
     * @return the qt1Z
     */
    @Override
    public int getQt1Z() {
        return qt1Z;
    }

    /**
     * @param qt1Z the qt1Z to set
     */
    public void setQt1Z(int qt1Z) {
        this.qt1Z = qt1Z;
    }

    /**
     * @return the primaryBeam
     */
    @Override
    public double getPrimaryBeam() {
        return primaryBeam;
    }

    /**
     * @param primaryBeam the primaryBeam to set
     */
    public void setPrimaryBeam(double primaryBeam) {
        this.primaryBeam = primaryBeam;
    }

    /**
     * @return the countTimeSec
     */
    public double[] getCountTimeSec() {
        return countTimeSec.clone();
    }

    /**
     * @param countTimeSec the countTimeSec to set
     */
    public void setCountTimeSec(double[] countTimeSec) {
        this.countTimeSec = countTimeSec.clone();
    }

    /**
     * @return the namesOfSpecies
     */
    @Override
    public String[] getNamesOfSpecies() {
        return namesOfSpecies.clone();
    }

    /**
     * @param namesOfSpecies the namesOfSpecies to set
     */
    public void setNamesOfSpecies(String[] namesOfSpecies) {
        this.namesOfSpecies = namesOfSpecies.clone();
    }

    /**
     * @return the peakMeasurementsCount
     */
    public int getPeakMeasurementsCount() {
        return peakMeasurementsCount;
    }

    /**
     * @param peakMeasurementsCount the peakMeasurementsCount to set
     */
    public void setPeakMeasurementsCount(int peakMeasurementsCount) {
        this.peakMeasurementsCount = peakMeasurementsCount;
    }

    /**
     * @return the isotopicRatiosII
     */
    public SortedSet<SquidRatiosModel> getIsotopicRatiosII() {
        return isotopicRatiosII;
    }

    /**
     * @return the rawPeakData
     */
    public int[][] getRawPeakData() {
        return clone2dArray(rawPeakData);
    }

    /**
     * @param rawPeakData the rawPeakData to set
     */
    public void setRawPeakData(int[][] rawPeakData) {
        this.rawPeakData = clone2dArray(rawPeakData);
    }

    /**
     * @return the rawSBMData
     */
    public int[][] getRawSBMData() {
        return clone2dArray(rawSBMData);
    }

    /**
     * @param rawSBMData the rawSBMData to set
     */
    public void setRawSBMData(int[][] rawSBMData) {
        this.rawSBMData = clone2dArray(rawSBMData);
    }

    /**
     * @return the totalCounts
     */
    @Override
    public double[][] getTotalCounts() {
        return clone2dArray(totalCounts);
    }

    /**
     * @param totalCounts the totalCounts to set
     */
    public void setTotalCounts(double[][] totalCounts) {
        this.totalCounts = clone2dArray(totalCounts);
    }

    /**
     * @return the totalCountsOneSigmaAbs
     */
    @Override
    public double[][] getTotalCountsOneSigmaAbs() {
        return clone2dArray(totalCountsOneSigmaAbs);
    }

    /**
     * @param totalCountsOneSigmaAbs the totalCountsOneSigmaAbs to set
     */
    public void setTotalCountsOneSigmaAbs(double[][] totalCountsOneSigmaAbs) {
        this.totalCountsOneSigmaAbs = clone2dArray(totalCountsOneSigmaAbs);
    }

    /**
     * @return the totalCountsSBM
     */
    public double[][] getTotalCountsSBM() {
        return clone2dArray(totalCountsSBM);
    }

    /**
     * @param totalCountsSBM the totalCountsSBM to set
     */
    public void setTotalCountsSBM(double[][] totalCountsSBM) {
        this.totalCountsSBM = clone2dArray(totalCountsSBM);
    }

    /**
     * @return the timeStampSec
     */
    @Override
    public double[][] getTimeStampSec() {
        return clone2dArray(timeStampSec);
    }

    /**
     * @param timeStampSec the timeStampSec to set
     */
    public void setTimeStampSec(double[][] timeStampSec) {
        this.timeStampSec = clone2dArray(timeStampSec);
    }

    /**
     * @return the trimMass
     */
    public double[][] getTrimMass() {
        return clone2dArray(trimMass);
    }

    /**
     * @param trimMass the trimMass to set
     */
    public void setTrimMass(double[][] trimMass) {
        this.trimMass = clone2dArray(trimMass);
    }

    /**
     * @return the totalCps
     */
    @Override
    public double[] getTotalCps() {
        return totalCps.clone();
    }

    /**
     * @param totalCps the totalCps to set
     */
    @Override
    public void setTotalCps(double[] totalCps) {
        this.totalCps = totalCps.clone();
    }

    /**
     * @return the netPkCps
     */
    @Override
    public double[][] getNetPkCps() {
        return clone2dArray(netPkCps);
    }

    /**
     * @param aNetPkCps the netPkCps to set
     */
    public void setNetPkCps(double[][] aNetPkCps) {
        netPkCps = clone2dArray(aNetPkCps);
    }

    /**
     * @return the pkFerr
     */
    @Override
    public double[][] getPkFerr() {
        return clone2dArray(pkFerr);
    }

    /**
     * @param aPkFerr the pkFerr to set
     */
    public void setPkFerr(double[][] aPkFerr) {
        pkFerr = clone2dArray(aPkFerr);
    }

    /**
     * @return the referenceMaterial
     */
    @Override
    public boolean isReferenceMaterial() {
        return referenceMaterial;
    }

    /**
     * @param referenceMaterial the referenceMaterial to set
     */
    public void setReferenceMaterial(boolean referenceMaterial) {
        this.referenceMaterial = referenceMaterial;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isConcentrationReferenceMaterial() {
        return concentrationReferenceMaterial;
    }

    /**
     *
     * @param concentrationReferenceMaterial
     */
    public void setConcentrationReferenceMaterial(boolean concentrationReferenceMaterial) {
        this.concentrationReferenceMaterial = concentrationReferenceMaterial;
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
    @Override
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
     * @return the reducedPkHt
     */
    @Override
    public double[][] getReducedPkHt() {
        return clone2dArray(reducedPkHt);
    }

    /**
     * @param reducedPkHt the reducedPkHt to set
     */
    public void setReducedPkHt(double[][] reducedPkHt) {
        this.reducedPkHt = clone2dArray(reducedPkHt);
    }

    /**
     * @return the reducedPkHtFerr
     */
    @Override
    public double[][] getReducedPkHtFerr() {
        return clone2dArray(reducedPkHtFerr);
    }

    /**
     * @param reducedPkHtFerr the reducedPkHtFerr to set
     */
    public void setReducedPkHtFerr(double[][] reducedPkHtFerr) {
        this.reducedPkHtFerr = clone2dArray(reducedPkHtFerr);
    }

    /**
     * @return the pkInterpScanArray
     */
    @Override
    public double[] getPkInterpScanArray() {
        return pkInterpScanArray.clone();
    }

    /**
     * @param pkInterpScanArray the pkInterpScanArray to set
     */
    @Override
    public void setPkInterpScanArray(double[] pkInterpScanArray) {
        this.pkInterpScanArray = pkInterpScanArray.clone();
    }

    /**
     * @return the taskExpressionsForScansEvaluated
     */
    @Override
    public List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> getTaskExpressionsForScansEvaluated() {
        return taskExpressionsForScansEvaluated;
    }

    /**
     * @param taskExpressionsForScansEvaluated the
     * taskExpressionsForScansEvaluated to set
     */
    @Override
    public void setTaskExpressionsForScansEvaluated(List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsForScansEvaluated) {
        this.taskExpressionsForScansEvaluated = taskExpressionsForScansEvaluated;
    }

    /**
     * @return the taskExpressionsEvaluationsPerSpot
     */
    @Override
    public Map<ExpressionTreeInterface, double[][]> getTaskExpressionsEvaluationsPerSpot() {
        return taskExpressionsEvaluationsPerSpot;
    }

    /**
     * Used by Reflection in
     * org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions
     *
     * @param fieldName
     * @return
     */
    @Override
    public double[][] getTaskExpressionsEvaluationsPerSpotByField(String fieldName) {
        double[][] values = new double[][]{{0.0, 0.0}};

        for (Map.Entry<ExpressionTreeInterface, double[][]> entry : taskExpressionsEvaluationsPerSpot.entrySet()) {
            if (entry.getKey().getName().compareTo(fieldName) == 0) {
                values = entry.getValue();
                break;
            }
        }

        return values;
    }

    /**
     * Used by reflection in expression evaluations by VariableNode, for example
     *
     * @param name
     * @return double [1][2] containing ratio value and 1-sigma abs uncertainty
     */
    @Override
    public double[][] getIsotopicRatioValuesByStringName(String name) {
        double[][] ratioAndUnct = new double[][]{{0.0, 0.0}};

        SquidRatiosModel ratio = SquidRatiosModel.findSquidRatiosModelByName(isotopicRatiosII, name);
        if (ratio != null) {
            ratioAndUnct = new double[][]{{ratio.getRatioVal(), ratio.getRatioFractErr()}};
        }

        return ratioAndUnct;
    }

    /**
     * @return the selectedProperty
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selectedProperty to set
     */
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the countOfNonPositiveSBMCounts
     */
    public int getCountOfNonPositiveSBMCounts() {
        return countOfNonPositiveSBMCounts;
    }

    /**
     * @param countOfNonPositiveSBMCounts the countOfNonPositiveSBMCounts to set
     */
    public void setCountOfNonPositiveSBMCounts(int countOfNonPositiveSBMCounts) {
        this.countOfNonPositiveSBMCounts = countOfNonPositiveSBMCounts;
    }
}
