package org.cirdles.squid.op;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OPFileRunFractionParser {
    public static List<OPFraction> parseOPFile(File file) throws IOException {
        List<OPFraction> opList = new ArrayList<>();
        Scanner scanner = null;
        InputStreamReader fis =null;
        try {
            fis = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            scanner = new Scanner(fis);

            while (scanner.hasNextLine()) {

                //checks if the end of the file was reached or gets to the next run
                String name = scanner.nextLine();
                while (scanner.hasNextLine() && name.trim().isEmpty()) {
                    name = scanner.nextLine().trim();
                }
                if (!name.isEmpty()) {

                    OPFraction op = new OPFraction();
                    op.setName(name);

                    //time and date
                    String timeDate = scanner.nextLine();
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

                    double[][] totalSBM = new double[op.getMeasurements()][op.getScans()];
                    for (int i = 0; i < op.getMeasurements(); i++) {
                        String[] totalSBMRowString = scanner.nextLine().split("\\s+");
                        if (totalSBMRowString.length == op.getScans()) {
                            double[] totalSBMRow = new double[totalSBMRowString.length];
                            for (int j = 0; j < totalSBMRowString.length; j++) {
                                totalSBMRow[j] = Double.parseDouble(totalSBMRowString[j]);
                            }
                            totalSBM[i] = totalSBMRow;
                        } else {
                            throw new Exception("incorrect input");
                        }
                    }
                    op.setTotalSBM(totalSBM);

                    opList.add(op);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                fis.close();
            }
            if (scanner != null){
                scanner.close();
            }
        }

        return opList;
    }
}