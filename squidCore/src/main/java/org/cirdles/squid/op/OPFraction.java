package org.cirdles.squid.op;

import static org.cirdles.squid.utilities.conversionUtilities.CloningUtilities.clone2dArray;

public class OPFraction {
    //in 2d arrays the columns are scans and the rows are measurements

    double[] countTimeSec; //countTimeSec
    double[][] timeStampSec; //timeStampSec
    double[][] totalCounts; //totalCounts
    int sbmZeroCPS; //sbmZeroCps
    double[][] totalSBM; //totalCountsSBM
    private String name; //fractionID
    private long dateTimeMilliseconds;
    private int sets;
    private int scans;
    private int measurements; //number of species, no variable

    public OPFraction() {
        name = "";
        countTimeSec = new double[0];
        timeStampSec = new double[0][0];
        totalCounts = new double[0][0];
        totalSBM = new double[0][0];
    }

    public double[][] getTotalSBM() {
        return clone2dArray(totalSBM);
    }

    public void setTotalSBM(double[][] totalSBM) {
        this.totalSBM = clone2dArray(totalSBM);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateTimeMilliseconds() {
        return dateTimeMilliseconds;
    }

    public void setDateTimeMilliseconds(long dateTimeMilliseconds) {
        this.dateTimeMilliseconds = dateTimeMilliseconds;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getScans() {
        return scans;
    }

    public void setScans(int scans) {
        this.scans = scans;
    }

    public int getMeasurements() {
        return measurements;
    }

    public void setMeasurements(int measurements) {
        this.measurements = measurements;
    }

    public double[] getCountTimeSec() {
        return countTimeSec.clone();
    }

    public void setCountTimeSec(double[] countTimeSec) {
        this.countTimeSec = countTimeSec.clone();
    }

    public double[][] getTimeStampSec() {
        return clone2dArray(timeStampSec);
    }

    public void setTimeStampSec(double[][] timeStampSec) {
        this.timeStampSec = clone2dArray(timeStampSec);
    }

    public double[][] getTotalCounts() {
        return clone2dArray(totalCounts);
    }

    public void setTotalCounts(double[][] totalCounts) {
        this.totalCounts = clone2dArray(totalCounts);
    }

    public int getSbmZeroCPS() {
        return sbmZeroCPS;
    }

    public void setSbmZeroCPS(int sbmZeroCPS) {
        this.sbmZeroCPS = sbmZeroCPS;
    }
}