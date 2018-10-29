package org.cirdles.squid.op;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

public class OPFile {
    //in 2d arrays the columns are scans and the rows are measurements

    private String name;
    private Date date;
    private Time time;
    private int sets;
    private int scans;
    private int measurements;
    double[] countTimeSec;
    int[][] timeStampSec;
    int[][] totalCounts;
    int sbmZeroCPS;
    int[][] totalSBM;

    public OPFile() {
        this.name = name;
        this.date = new Date();
        this.time = Time.valueOf(LocalTime.now());
        countTimeSec = new double[0];
        timeStampSec = new int[0][0];
        totalCounts = new int[0][0];
        totalSBM = new int[0][0];
    }

    public int[][] getTotalSBM() {
        return totalSBM;
    }

    public void setTotalSBM(int[][] totalSBM) {
        this.totalSBM = totalSBM;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
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

    public int[][] getTimeStampSec() {
        return timeStampSec;
    }

    public void setTimeStampSec(int[][] timeStampSec) {
        this.timeStampSec = timeStampSec;
    }

    public int[][] getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(int[][] totalCounts) {
        this.totalCounts = totalCounts;
    }

    public int getSbmZeroCPS() {
        return sbmZeroCPS;
    }

    public void setSbmZeroCPS(int sbmZeroCPS) {
        this.sbmZeroCPS = sbmZeroCPS;
    }
}
