/*
 * Copyright 2018 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.utilities.csvSerialization;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class ReportSerializerToCSV {

    private ReportSerializerToCSV() {
    }

    public static void writeCSVReport(boolean rawOutput, File csvFile, String[][] reportFractions, boolean showSample) {

        int firstColumnShown = showSample ? 1 : 2;
        PrintWriter outputWriter;

        if (rawOutput) {
            try {
                outputWriter = new PrintWriter(csvFile, "UTF-8");
                for (int row = 0; row < reportFractions.length; row++) {
                    for (int c = 0; c < reportFractions[0].length; c++) {
                        outputWriter.write(reportFractions[row][c] + ", ");
                    }
                    outputWriter.write("\n");
                }

            } catch (IOException iOException) {
            }
        } else {
            int firstDataRow = Integer.parseInt(reportFractions[0][0]);

            try {
                outputWriter = new PrintWriter(csvFile, "UTF-8");

                // category titles 
                String catName = "Fraction";
                String savedCatName = catName;
                // starts after Fraction column = 2
                for (int c = firstColumnShown; c < reportFractions[0].length; c++) {
                    catName = reportFractions[0][c].trim();
                    if (!catName.equalsIgnoreCase(savedCatName)) {
                        outputWriter.write(catName + ",");
                        savedCatName = catName;
                    } else {
                        outputWriter.write(",");
                    }
                }

                outputWriter.write("\n");
                // column titles
                // March 2021 per issue #593
                if (showSample) {
                    reportFractions[4][1] = "Sample";
                    reportFractions[4][2] = "Spot  ";
                    reportFractions[4][reportFractions[0].length - 2] = "Spot  ";
                }
                for (int row = 1; row < 5; row++) {
                    for (int c = firstColumnShown; c < reportFractions[0].length; c++) {

                        // footnote
                        if (row == 4) {
                            outputWriter.write(replaceUnicodes(reportFractions[row][c]) + " " + reportFractions[6][c] + ",");
                        } else {
                            outputWriter.write(replaceUnicodes(reportFractions[row][c]) + ",");
                        }
                    }
                    outputWriter.write("\n");

                }

                String saveAliquotName = "";

                for (int row = firstDataRow - 1; row < reportFractions.length; row++) {
                    // test for data
                    if (reportFractions[row][3].length() > 0) {
                        // check whether fraction is included
                        if (reportFractions[row][0].equalsIgnoreCase("TRUE")) {
                            // fraction data
                            for (int c = firstColumnShown; c < reportFractions[0].length - 1; c++) {
                                outputWriter.write(reportFractions[row][c] + ",");
                            }

                            outputWriter.write("\n");
                        }
                    }
                }

                // write out footnotes
                outputWriter.write("\n");
                outputWriter.write("\n");

                for (int i = 0; i < reportFractions[6].length; i++) {
                    if (!reportFractions[6][i].equals("")) {
                        // strip out footnote letter
                        String[] footNote = reportFractions[6][i].split("&");
                        String footNoteLine
                                = //
                                " " //
                                        + footNote[0] //
                                        + "  " //
                                        + footNote[1] + "\n";
                        outputWriter.write(replaceUnicodes(footNoteLine));
                    }
                }

                outputWriter.write("\n");
                outputWriter.write("\n");

                outputWriter.flush();
                outputWriter.close();

            } catch (IOException iOException) {
            }
        }

    }

    public static void writeSquid3CustomCSVReport(File csvFile, String[][] reportFractions) {
        reportFractions[4][2] = "Fractions";
        reportFractions[4][reportFractions[4].length - 2] = "Fractions";
        for (int i = 0; i < reportFractions.length; i++) {
            String[] row = reportFractions[i];
            reportFractions[i] = Arrays.copyOfRange(row, 2, row.length - 1);
        }

        for (int j = 2; j < reportFractions[4].length - 1; j++) {
            reportFractions[4][j] = replaceUnicodes(reportFractions[4][j]);
        }

        writeCSVReport(true, csvFile, reportFractions, true);
    }

    private static String replaceUnicodes(String text) {
        String retVal = text;

        retVal = retVal.replace("\u00B1", "+/-");
        retVal = retVal.replace("\u03C3", "sigma");
        retVal = retVal.replace("\u03c1", "rho");
        retVal = retVal.replace("\u03BB", "lambda");
        retVal = retVal.replace(",", " and ");

        return retVal;
    }
}
