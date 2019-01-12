package org.cirdles.squid.op;

public class OPFraction {
    //in 2d arrays the columns are scans and the rows are measurements

    private String name; //fractionID
    private long dateTimeMilliseconds;
    private int sets;
    private int scans;
    private int measurements; //number of species, no variable
    double[] countTimeSec; //countTimeSec
    double[][] timeStampSec; //timeStampSec
    double[][] totalCounts; //totalCounts
    int sbmZeroCPS; //sbmZeroCps
    double[][] totalSBM; //totalCountsSBM

    public OPFraction() {
        name = "";
        countTimeSec = new double[0];
        timeStampSec = new double[0][0];
        totalCounts = new double[0][0];
        totalSBM = new double[0][0];
    }

    public double[][] getTotalSBM() {
        return totalSBM;
    }

    public void setTotalSBM(double[][] totalSBM) {
        this.totalSBM = totalSBM;
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
        return countTimeSec;
    }

    public void setCountTimeSec(double[] countTimeSec) {
        this.countTimeSec = countTimeSec;
    }

    public double[][] getTimeStampSec() {
        return timeStampSec;
    }

    public void setTimeStampSec(double[][] timeStampSec) {
        this.timeStampSec = timeStampSec;
    }

    public double[][] getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(double[][] totalCounts) {
        this.totalCounts = totalCounts;
    }

    public int getSbmZeroCPS() {
        return sbmZeroCPS;
    }

    public void setSbmZeroCPS(int sbmZeroCPS) {
        this.sbmZeroCPS = sbmZeroCPS;
    }
}
