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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class ReportSerializerToCSV {

    public static void writeCSVReport(boolean rawOutput, File csvFile, String[][] reportFractions) {

        int firstColumnShown = 2;
        PrintWriter outputWriter = null;

        if (rawOutput) {
            try {
                outputWriter = new PrintWriter(new FileWriter(csvFile));
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
                outputWriter = new PrintWriter(new FileWriter(csvFile));

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

                    // check whether fraction is included
                    if (reportFractions[row][0].equalsIgnoreCase("TRUE")) {
                        // for each aliquot
                        if (!reportFractions[row][1].equalsIgnoreCase(saveAliquotName)) {
                            saveAliquotName = reportFractions[row][1];
                            outputWriter.write(reportFractions[row][1] + "\n");
                        }

                        // fraction data
                        for (int c = firstColumnShown; c < reportFractions[0].length; c++) {
                            outputWriter.write(reportFractions[row][c] + ",");
                        }

                        outputWriter.write("\n");

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

            } catch (IOException iOException) {
            }
        }

        outputWriter.flush();
        outputWriter.close();

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
