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

                int firstRow = Integer.valueOf(lines[1].split("\t")[1]) - 1;

                taskSquid25.squidTaskFileName = lines[firstRow].split("\t")[1];
                taskSquid25.taskType = lines[firstRow + 1].split("\t")[1];
                taskSquid25.taskName = lines[firstRow + 2].split("\t")[1];
                taskSquid25.taskDescription = lines[firstRow + 3].split("\t")[1];
                              
                taskSquid25.authorName = "";
                taskSquid25.labName = lines[firstRow + 7].split("\t")[1];

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
                            false));
                }

                String[] secondaryUThPbEqn = lines[firstRow + 23].split("\t");
                if (secondaryUThPbEqn.length > 1) {
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(secondaryUThPbEqn[1]),
                            prepareSquid25ExcelEquationNameForSquid3(secondaryUThPbEqn[0]),
                            true,
                            true,
                            false));
                }

                String[] ThUEqn = lines[firstRow + 24].split("\t");
                if (ThUEqn.length > 1) {
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(ThUEqn[1]),
                            prepareSquid25ExcelEquationNameForSquid3(ThUEqn[0]),
                            true,
                            true,
                            false));
                }

                String[] ppmParentEqn = lines[firstRow + 25].split("\t");
                if (ppmParentEqn.length > 1) {
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(ppmParentEqn[1]),
                            prepareSquid25ExcelEquationNameForSquid3(ppmParentEqn[0]),
                            true,
                            true,
                            false));
                }

                String[] equations = lines[firstRow + 26].split("\t");
                int countOfEquations = Integer.valueOf(equations[1]);

                String[] equationNames = lines[firstRow + 27].split("\t");

                String[] switchST = lines[firstRow + 28].split("\t");

                String[] switchSA = lines[firstRow + 29].split("\t");

                String[] switchSC = lines[firstRow + 30].split("\t");

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
                                Boolean.parseBoolean(switchSC[i + 2])));
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
            retVal = retVal.replace(matcher.group(), robregParts[0] + "," + robregParts[1] + (robregParts.length > 2 ? ")" : ""));
        }

        // regex for robreg with four arguments = agePb76.*\)
        squid25FunctionPattern = Pattern.compile("^(.*)[a,A]gePb76.*\\)", Pattern.CASE_INSENSITIVE);
        matcher = squid25FunctionPattern.matcher(retVal);
        if (matcher.matches()) {
            String[] agePb76Parts = matcher.group().split(",");
            if (agePb76Parts.length > 1) {
                retVal = retVal.replace(matcher.group(), agePb76Parts[0] + "," + agePb76Parts[1] + (agePb76Parts.length > 2 ? ")" : ""));
            }
        }

        if (excelString.startsWith("[")) {
            // do not accept field names as being equations
            //retVal = "";
        } else if (!excelString.contains("(") && !excelString.contains("[")) {
            // do not accept constants as being equations - this results from the conflation in Squid2.5 between equations and outputs
            retVal = "";
        }

        return retVal;
    }

    private static String prepareSquid25ExcelEquationNameForSquid3(String excelString) {
        String retVal = "";

        retVal = excelString.replace("|", "");
        retVal = retVal.replace("(Ma)", "");

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
