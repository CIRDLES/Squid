package org.cirdles.squid.gui.dataViews.SpeciesGraphs;

import org.cirdles.squid.prawn.PrawnFile;

import java.util.List;

public interface SpeciesGraphInterface {
    void setIndexOfSecondSelectedSpotForMultiSelect(int index);

    void setIndexOfSelectedSpot(int index);

    /**
     * @return the indicesOfRunsAtMeasurementTimes
     */
    List<Integer> getIndicesOfRunsAtMeasurementTimes();

    void setIndicesOfRunsAtMeasurementTimes(List<Integer> indicesOfRunsAtMeasurementTimes);

    /**
     * @return the measuredTrimMasses
     */
    List<Double> getMeasuredTrimMasses();

    void setMeasuredTrimMasses(List<Double> measuredTrimMasses);

    /**
     * @return the timesOfMeasuredTrimMasses
     */
    List<Double> getTimesOfMeasuredTrimMasses();

    void setTimesOfMeasuredTrimMasses(List<Double> timesOfMeasuredTrimMasses);

    void setPrawnFileRuns(List<PrawnFile.Run> prawnFileRuns);

    /**
     * @return the indicesOfScansAtMeasurementTimes
     */
    List<Integer> getIndicesOfScansAtMeasurementTimes();

    void setIndicesOfScansAtMeasurementTimes(List<Integer> indicesOfScansAtMeasurementTimes);

    public List<PrawnFile.Run> getPrawnFileRuns();
}