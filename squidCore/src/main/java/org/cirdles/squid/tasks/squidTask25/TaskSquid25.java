/*
 * Copyright 2017 CIRDLES.org.
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
package org.cirdles.squid.tasks.squidTask25;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.extractor.ExcelExtractor;

/**
 *
 * @author James F. Bowring
 */
public class TaskSquid25 implements Serializable {

    private static final long serialVersionUID = -2805382700088270719L;

    private String squidVersion;
    private String squidTaskFileName;
    private String taskType;
    private String taskName;
    private String taskDescription;
    private String labName;
    private String authorName;
    private List<String> nominalMasses;
    private List<String> ratioNames;
    private List<TaskSquid25Equation> task25Equations;

    public static TaskSquid25 importSquidTaskFile(File squidTaskFile) {

        TaskSquid25 taskSquid25 = null;

        try {
            InputStream inp = new FileInputStream(squidTaskFile);
            HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
            ExcelExtractor extractor = new org.apache.poi.hssf.extractor.ExcelExtractor(wb);

            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);
            String text = extractor.getText();

            String[] lines = text.split("\n");
            if (lines[0].startsWith("Created by SQUID")) {
                taskSquid25 = new TaskSquid25();

                taskSquid25.squidVersion = lines[0].split("\t")[1];

                int firstRow = Integer.parseInt(lines[1].split("\t")[1]) - 1;

                taskSquid25.squidTaskFileName = lines[firstRow].split("\t")[1];
                taskSquid25.taskType = lines[firstRow + 1].split("\t")[1];
                taskSquid25.taskName = lines[firstRow + 2].split("\t")[1];
                taskSquid25.taskDescription = lines[firstRow + 3].split("\t")[1];

                taskSquid25.authorName = "";
                taskSquid25.labName = lines[firstRow + 7].split("\t")[1];

                String[] nominalMasses = lines[firstRow + 13].split("\t");
                int countOfMasses = Integer.valueOf(nominalMasses[1]);
                taskSquid25.nominalMasses = new ArrayList<>();
                for (int i = 0; i < countOfMasses; i++) {
                    taskSquid25.nominalMasses.add(nominalMasses[i + 2]);
                }

                String[] ratioStrings = lines[firstRow + 15].split("\t");
                int countOfRatios = Integer.valueOf(ratioStrings[1]);
                taskSquid25.ratioNames = new ArrayList<>();
                for (int i = 0; i < countOfRatios; i++) {
                    taskSquid25.ratioNames.add(ratioStrings[i + 2]);
                }

                taskSquid25.task25Equations = new ArrayList<>();

                String[] primaryUThPbEqn = lines[firstRow + 22].split("\t");
                if (primaryUThPbEqn.length > 1) {
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(primaryUThPbEqn[1]),
                            prepareSquid25ExcelEquationNameForSquid3(primaryUThPbEqn[0]),
                            true,
                            true,
                            false,
                            true));
                }

                String[] secondaryUThPbEqn = lines[firstRow + 23].split("\t");
                if (secondaryUThPbEqn.length > 1) {
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(secondaryUThPbEqn[1]),
                            prepareSquid25ExcelEquationNameForSquid3(secondaryUThPbEqn[0]),
                            true,
                            true,
                            false,
                            true));
                }

                String[] ThUEqn = lines[firstRow + 24].split("\t");
                if (ThUEqn.length > 1) {
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(ThUEqn[1]),
                            prepareSquid25ExcelEquationNameForSquid3(ThUEqn[0]),
                            true,
                            true,
                            false,
                            true));
                }

                String[] ppmParentEqn = lines[firstRow + 25].split("\t");
                if (ppmParentEqn.length > 1) {
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(ppmParentEqn[1]),
                            prepareSquid25ExcelEquationNameForSquid3(ppmParentEqn[0]),
                            true,
                            true,
                            false,
                            true));
                }

                String[] equations = lines[firstRow + 26].split("\t");
                int countOfEquations = Integer.valueOf(equations[1]);

                String[] equationNames = lines[firstRow + 27].split("\t");

                String[] switchST = lines[firstRow + 28].split("\t");

                String[] switchSA = lines[firstRow + 29].split("\t");

                String[] switchSC = lines[firstRow + 30].split("\t");

                String[] switchNU = lines[firstRow + 32].split("\t");

                for (int i = 0; i < countOfEquations; i++) {
                    // handle backwards logic of Squid25 where both ST, SA false, means both True
                    boolean switchRM = Boolean.parseBoolean(switchST[i + 2]);
                    boolean switchUN = Boolean.parseBoolean(switchSA[i + 2]);
                    if (!switchRM && !switchUN) {
                        switchRM = true;
                        switchUN = true;
                    }

                    if (prepareSquid25ExcelEquationStringForSquid3(equations[i + 2]).length() > 0) {
                        taskSquid25.task25Equations.add(new TaskSquid25Equation(
                                prepareSquid25ExcelEquationStringForSquid3(equations[i + 2]),
                                prepareSquid25ExcelEquationNameForSquid3(equationNames[i + 2]),
                                switchRM,
                                switchUN,
                                Boolean.parseBoolean(switchSC[i + 2]),
                                Boolean.parseBoolean(switchNU[i + 2])));
                    }
                }

            }
        } catch (IOException iOException) {
        }

        return taskSquid25;
    }

    private static String prepareSquid25ExcelEquationStringForSquid3(String excelString) {
        String retVal = "";

        retVal = excelString.replace("|", "");
        retVal = retVal.replace("[\"Total 204 cts/sec\"]", "totalCps([\"204\"])");
        retVal = retVal.replace("[\"Total 206cts/sec\"]", "totalCps([\"206\"])");
        retVal = retVal.replace("[\"Bkrd cts/sec\"]", "totalCps([\"BKG\"])");
        retVal = retVal.replace("[\"Bkrdcts/sec\"]", "totalCps([\"BKG\"])");
        retVal = retVal.replace("9511", "95");
        retVal = retVal.replace("(Ma)", "");

        // regex for robreg with four arguments = robreg.*\)
        Pattern squid25FunctionPattern = Pattern.compile("^(.*)[r,R]obreg.*\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = squid25FunctionPattern.matcher(retVal);
        if (matcher.matches()) {
            String[] robregParts = matcher.group().split(",");
            if (robregParts.length > 1) {
                retVal = retVal.replace(matcher.group(), robregParts[0] + "," + robregParts[1] + (robregParts.length > 2 ? ")" : ""));
            }
        }

        // regex for robreg with four arguments = robreg.*\)
        squid25FunctionPattern = Pattern.compile("^(.*)[a,A]gePb76.*\\)", Pattern.CASE_INSENSITIVE);
        matcher = squid25FunctionPattern.matcher(retVal);
        if (matcher.matches()) {
            String[] agePb76Parts = matcher.group().split(",");
            if (agePb76Parts.length > 1) {
                retVal = retVal.replace(matcher.group(), agePb76Parts[0] + ")");
            }
        }

        // regex for sqWtdAv with four arguments = sqWtdAv.*\)
        squid25FunctionPattern = Pattern.compile("^(.*)[s,S]qWtdAv.*\\)", Pattern.CASE_INSENSITIVE);
        matcher = squid25FunctionPattern.matcher(retVal);
        if (matcher.matches()) {
            String[] sqWtdAvParts = matcher.group().split(",");
            if (sqWtdAvParts.length > 1) {
                retVal = retVal.replace(matcher.group(), sqWtdAvParts[0] + ")");
            }
        }

        // regex for sqBiweight with five arguments = sqBiweight.*\)
        squid25FunctionPattern = Pattern.compile("^(.*)[s,S]qBiweight.*\\)", Pattern.CASE_INSENSITIVE);
        matcher = squid25FunctionPattern.matcher(retVal);
        if (matcher.matches()) {
            String[] sqBiweightParts = matcher.group().split(",");
            if (sqBiweightParts.length > 1) {
                retVal = retVal.replace(matcher.group(), sqBiweightParts[0] + "," + sqBiweightParts[1] + (sqBiweightParts.length > 2 ? ")" : ""));
            }
        }

        boolean handledConcordiaTW = false;
        // regex for concordiaTW with up to seven arguments = concordiaTW.*\) x, sigma, y sigma , 3 optional
        squid25FunctionPattern = Pattern.compile("^(.*)[c,C]oncordiaTW.*\\)", Pattern.CASE_INSENSITIVE);
        matcher = squid25FunctionPattern.matcher(retVal);
        if (matcher.matches()) {
            String[] concordiaTWParts = matcher.group().split(",");
            retVal = retVal.replace(matcher.group(), concordiaTWParts[0] + "," + concordiaTWParts[2] + (concordiaTWParts.length > 3 ? ")" : ""));
            handledConcordiaTW = true;
        }

        if (!handledConcordiaTW) {
            // regex for concordia with up to eight arguments = concordia.*\) x, sigma, y, sigma , rho,  3 optional
            squid25FunctionPattern = Pattern.compile("^(.*)[c,C]oncordia.*\\)", Pattern.CASE_INSENSITIVE);
            matcher = squid25FunctionPattern.matcher(retVal);
            if (matcher.matches()) {
                String[] concordiaParts = matcher.group().split(",");
                retVal = retVal.replace(matcher.group(), concordiaParts[0] + "," + concordiaParts[2] + "," + concordiaParts[4] + (concordiaParts.length > 5 ? ")" : ""));
            }
        }

        // remove leading mulitpliers meant for output tables
        retVal = retVal.replace("1000*", "");
        retVal = retVal.replace("100*", "");

        // do not accept constants as being equations - this results from the conflation in Squid2.5 between equations and outputs
        if (!excelString.contains("(") && !excelString.contains("[")) {
            retVal = "";
        }

        // do not include calls to error functions of Age as in AgeErPb76 etc
        if (excelString.toUpperCase(Locale.US).contains("AGEER")) {
            retVal = "";
        }

        return retVal;
    }

    private static String prepareSquid25ExcelEquationNameForSquid3(String excelString) {
        String retVal = "";

        retVal = excelString.replace("|", "");
        retVal = retVal.replace("(Ma)", "");
        retVal = retVal.replace("/U", "U");
        // remove leading mulitpliers meant for output tables
        retVal = retVal.replace("1000*", "");
        retVal = retVal.replace("100*", "");
        // remove spaces
        retVal = retVal.replace(" ", "");

        return retVal;
    }

    /**
     * @return the squidVersion
     */
    public String getSquidVersion() {
        return squidVersion;
    }

    /**
     * @return the squidTaskFileName
     */
    public String getSquidTaskFileName() {
        return squidTaskFileName;
    }

    /**
     * @return the taskType
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @return the taskDescription
     */
    public String getTaskDescription() {
        return taskDescription;
    }

    /**
     * @return the nominalMasses
     */
    public List<String> getNominalMasses() {
        return nominalMasses;
    }

    /**
     * @return the ratioNames
     */
    public List<String> getRatioNames() {
        return ratioNames;
    }

    /**
     * @return the task25Equations
     */
    public List<TaskSquid25Equation> getTask25Equations() {
        return task25Equations;
    }

    /**
     * @return the labName
     */
    public String getLabName() {
        return labName;
    }

    /**
     * @return the authorName
     */
    public String getAuthorName() {
        return authorName;
    }

}
