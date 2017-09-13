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

                String[] ratioStrings = lines[firstRow + 15].split("\t");
                int countOfRatios = Integer.valueOf(ratioStrings[1]);
                taskSquid25.ratioNames = new ArrayList<>();
                for (int i = 0; i < countOfRatios; i++) {
                    taskSquid25.ratioNames.add(ratioStrings[i + 2]);
                }

                String[] equations = lines[firstRow + 26].split("\t");
                int countOfEquations = Integer.valueOf(equations[1]);

                String[] equationNames = lines[firstRow + 27].split("\t");

                String[] switchST = lines[firstRow + 28].split("\t");

                String[] switchSA = lines[firstRow + 29].split("\t");

                String[] switchSC = lines[firstRow + 30].split("\t");

                taskSquid25.task25Equations = new ArrayList<>();
                for (int i = 0; i < countOfEquations; i++) {
                    if (prepareSquid25ExcelStringForSquid3(equations[i + 2]).length() > 0) {
                        taskSquid25.task25Equations.add(new TaskSquid25Equation(
                                prepareSquid25ExcelStringForSquid3(equations[i + 2]),
                                prepareSquid25ExcelStringForSquid3(equationNames[i + 2]),
                                Boolean.parseBoolean(switchST[i + 2]),
                                Boolean.parseBoolean(switchSA[i + 2]),
                                Boolean.parseBoolean(switchSC[i + 2])));
                    }
                }

            }
        } catch (IOException iOException) {
        }

        return taskSquid25;
    }

    private static String prepareSquid25ExcelStringForSquid3(String excelString) {
        String retVal = "";

        retVal = excelString.replace("|", "");
        retVal = retVal.replace("[\"Total 204 cts/sec\"]", "totalCps([\"204\"])");
        retVal = retVal.replace("[\"Bkrd cts/sec\"]", "totalCps([\"BKG\"])");
        retVal = retVal.replace("(Ma)", "");

        // regex for robreg with four arguments = robreg.*\)
        Pattern robregPattern = Pattern.compile("^(.*)robreg.*\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = robregPattern.matcher(retVal);
        if (matcher.matches()) {
            String[] robregParts = matcher.group().split(",");
            retVal = retVal.replace(matcher.group(), robregParts[0] + "," + robregParts[1] + ")");
        }

        if (excelString.startsWith("[")) {
            // do not accept field names as being equations
            retVal = "";
        }

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

}
