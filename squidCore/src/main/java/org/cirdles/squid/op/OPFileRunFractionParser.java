package org.cirdles.squid.op;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class OPFileRunFractionParser {
    public static List<OPFile> parseOPFile(File file) {
        List<OPFile> opList = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(file);
            Scanner scanner = new Scanner(fis);

            while (scanner.hasNextLine()) {

                //checks if the end of the file was reached or gets to the next run
                String name = scanner.nextLine();
                while (scanner.hasNextLine() && name.trim().isEmpty()) {
                    name = scanner.nextLine().trim();
                }
                if (!name.isEmpty()) {

                    OPFile op = new OPFile();
                    op.setName(name);

                    //time and date
                    String timeDate = scanner.nextLine();
                    /*int indexOfFirstSpaceInTimeDate = timeDate.indexOf(' ');
                    String timeString = timeDate.substring(0, indexOfFirstSpaceInTimeDate);
                    String date = timeDate.substring(indexOfFirstSpaceInTimeDate);
                    Time time = Time.valueOf(timeString));
                    String[] dates = date.split("/");
                    int day = Integer.parseInt(dates[0].trim());
                    int month = Integer.parseInt(dates[1].trim());
                    int year = Integer.parseInt(dates[2].trim());*/
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    op.setDateTimeMilliseconds(dateFormat.parse(timeDate).getTime());

                    op.setSets(Integer.parseInt(scanner.nextLine().trim()));
                    op.setScans(Integer.parseInt(scanner.nextLine().trim()));
                    op.setMeasurements(Integer.parseInt(scanner.nextLine().trim()));

                    String[] countTimeSecString = scanner.nextLine().split("\\s+");
                    if (countTimeSecString.length == op.getMeasurements()) {
                        double[] countTimeSec = new double[countTimeSecString.length];
                        for (int i = 0; i < countTimeSec.length; i++) {
                            countTimeSec[i] = Double.parseDouble(countTimeSecString[i]);
                        }
                        op.setCountTimeSec(countTimeSec);
                    } else {
                        throw new Exception("incorrect input");
                    }

                    double[][] timeStampSec = new double[op.getMeasurements()][op.getScans()];
                    for (int i = 0; i < op.getMeasurements(); i++) {
                        String[] timeStampSecRowString = scanner.nextLine().split("\\s+");
                        if (timeStampSecRowString.length == op.getScans()) {
                            double[] timeStampSecRow = new double[timeStampSecRowString.length];
                            for (int j = 0; j < op.getScans(); j++) {
                                timeStampSecRow[j] = Double.parseDouble(timeStampSecRowString[j]);
                            }
                            timeStampSec[i] = timeStampSecRow;
                        } else {
                            throw new Exception("incorrect input");
                        }
                    }
                    op.setTimeStampSec(timeStampSec);

                    double[][] totalCounts = new double[op.getMeasurements()][op.getScans()];
                    for (int i = 0; i < op.getMeasurements(); i++) {
                        String[] totalCountsRowString = scanner.nextLine().split("\\s+");
                        if (totalCountsRowString.length == op.getScans()) {
                            double[] totalCountsRow = new double[totalCountsRowString.length];
                            for (int j = 0; j < op.getScans(); j++) {
                                totalCountsRow[j] = Double.parseDouble(totalCountsRowString[j]);
                            }
                            totalCounts[i] = totalCountsRow;
                        } else {
                            throw new Exception("incorrect input");
                        }
                    }
                    op.setTotalCounts(totalCounts);

                    op.setSbmZeroCPS(Integer.parseInt(scanner.nextLine()));

                    int[][] totalSBM = new int[op.getMeasurements()][op.getScans()];
                    for (int i = 0; i < op.getMeasurements(); i++) {
                        String[] totalSBMRowString = scanner.nextLine().split("\\s+");
                        if (totalSBMRowString.length == op.getScans()) {
                            int[] totalSBMRow = new int[totalSBMRowString.length];
                            for (int j = 0; j < totalSBMRowString.length; j++) {
                                totalSBMRow[j] = Integer.parseInt(totalSBMRowString[j]);
                            }
                            totalSBM[i] = totalSBMRow;
                        } else {
                            throw new Exception("incorrect input");
                        }
                    }

                    opList.add(op);
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return opList;
    }

    public static void main(String[] args) {

    }

}
