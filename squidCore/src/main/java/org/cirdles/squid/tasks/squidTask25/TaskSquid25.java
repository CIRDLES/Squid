/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import org.cirdles.squid.tasks.expressions.parsing.ShuntingYard;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.AV_PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM206PB_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM208PB_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_235U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;

/**
 *
 * @author James F. Bowring
 */
public class TaskSquid25 implements Serializable {

    private static final long serialVersionUID = -2805382700088270719L;

    private String squidVersion;
    private String squidTaskFileName;
    private TaskTypeEnum taskType;
    private String taskName;
    private String taskDescription;
    private String labName;
    private String authorName;
    private List<String> nominalMasses;
    private List<String> ratioNames;
    private String backgroundMass;
    private String parentNuclide;
    private boolean directAltPD;
    private String primaryParentElement;
    private List<TaskSquid25Equation> task25Equations;
    private List<String> constantNames;
    private List<String> constantValues;

    private static TaskSquid25 taskSquid25;

    public static TaskSquid25 importSquidTaskFile(File squidTaskFile) {

        taskSquid25 = null;

        try {
            InputStream inp = new FileInputStream(squidTaskFile);
            HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
            ExcelExtractor extractor = new org.apache.poi.hssf.extractor.ExcelExtractor(wb);

            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);
            String text = extractor.getText();

            String[] lines = text.split("\n");
            boolean isSquid2_20 = false;
            if (lines[0].startsWith("Created by SQUID")) {
                taskSquid25 = new TaskSquid25();
                taskSquid25.constantNames = new ArrayList<>();
                taskSquid25.constantValues = new ArrayList<>();

                taskSquid25.squidVersion = lines[0].split("\t")[1];
                // July 2018 detect if version 2.20
                isSquid2_20 = (taskSquid25.squidVersion.startsWith("2.20"));

                int firstRow = Integer.parseInt(lines[1].split("\t")[1]) - 1;

                taskSquid25.squidTaskFileName = lines[firstRow].split("\t")[1];
                taskSquid25.taskType = TaskTypeEnum.valueOf(lines[firstRow + 1].split("\t")[1].toUpperCase());
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

                // July 2018
                // decrement first row to handle missing line for Hidden Masses in Squid 2.20
                if (isSquid2_20) {
                    firstRow--;
                }

                String[] ratioStrings = lines[firstRow + 15].split("\t");
                if (isSquid2_20 && ratioStrings[0].toUpperCase().startsWith("HIDDEN")) {
                    // special case of 2.2 where this row exists
                    firstRow++;
                    ratioStrings = lines[firstRow + 15].split("\t");
                }
                int countOfRatios = Integer.valueOf(ratioStrings[1]);
                taskSquid25.ratioNames = new ArrayList<>();
                for (int i = 0; i < countOfRatios; i++) {
                    taskSquid25.ratioNames.add(ratioStrings[i + 2]);
                }

                String[] backgroundStrings = lines[firstRow + 19].split("\t");
                taskSquid25.backgroundMass = backgroundStrings[1];

                String[] parentNuclideStrings = lines[firstRow + 20].split("\t");
                taskSquid25.parentNuclide = parentNuclideStrings[1];

                String[] directAltPDStrings = lines[firstRow + 21].split("\t");
                taskSquid25.directAltPD = Boolean.valueOf(directAltPDStrings[1]);

                taskSquid25.task25Equations = new ArrayList<>();

                // determine where uranium or thorium is primary or secondary
                String primaryUThEqnName = UNCOR206PB238U_CALIB_CONST;
                String primaryUThEqnOtherName = UNCOR208PB232TH_CALIB_CONST;

                String[] primaryUThPbEqn = lines[firstRow + 22].split("\t");
                if (primaryUThPbEqn.length > 1) {

                    if (taskSquid25.parentNuclide.contains("232")) {
                        primaryUThEqnName = UNCOR208PB232TH_CALIB_CONST;
                        primaryUThEqnOtherName = UNCOR206PB238U_CALIB_CONST;
                    }

                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(primaryUThPbEqn[1]),
                            primaryUThEqnName,
                            true,
                            true,
                            false,
                            true,
                            true, false));
                }

                String[] secondaryUThPbEqn = lines[firstRow + 23].split("\t");
                if (secondaryUThPbEqn.length > 1) {
                    // this is the case of DirectAltPd = TRUE
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(secondaryUThPbEqn[1]),
                            primaryUThEqnOtherName,
                            true,
                            true,
                            false,
                            true,
                            true, false));
                }

                String[] ThUEqn = lines[firstRow + 24].split("\t");
                if (ThUEqn.length > 1) {
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(ThUEqn[1]),
                            TH_U_EXP_RM,
                            true,
                            false,
                            false,
                            true,
                            true, false));
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(ThUEqn[1]),
                            TH_U_EXP,
                            false,
                            true,
                            false,
                            true,
                            true, false));
                }

                String[] ppmParentEqn = lines[firstRow + 25].split("\t");
                if (ppmParentEqn.length > 1) {
                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            prepareSquid25ExcelEquationStringForSquid3(ppmParentEqn[1]),
                            PARENT_ELEMENT_CONC_CONST,
                            true,
                            true,
                            false,
                            true,
                            true, false));

                    taskSquid25.task25Equations.add(new TaskSquid25Equation(
                            "CalculateMeanConcStd([\"" + PARENT_ELEMENT_CONC_CONST + "\"])",
                            AV_PARENT_ELEMENT_CONC_CONST,
                            false,
                            false,
                            true,
                            false,
                            true, true));

                }

                String[] equations = lines[firstRow + 26].split("\t");
                int countOfEquations = Integer.valueOf(equations[1]);

                String[] equationNames = lines[firstRow + 27].split("\t");

                // these sqitches split into an array of length equations mius 1 in Squid2.20 due to missing count entry
                String[] switchST = lines[firstRow + 28].split("\t");

                String[] switchSA = lines[firstRow + 29].split("\t");

                String[] switchSC = lines[firstRow + 30].split("\t");

                String[] switchNU = lines[firstRow + 32].split("\t");

                // run constants first so can detect in equations
                String[] constantNamesSource = lines[firstRow + 40].split("\t");
                String[] constantValuesSource = lines[firstRow + 41].split("\t");

                int countOfConstants = 0;
                if (constantNamesSource.length > 1) {
                    countOfConstants = Integer.valueOf(constantNamesSource[1]);
                }

                for (int i = 0; i < countOfConstants; i++) {
                    taskSquid25.constantNames.add(constantNamesSource[i + 2].replaceFirst("_", ""));
                    taskSquid25.constantValues.add(constantValuesSource[i + 2]);
                }

                for (int i = 0; i < countOfEquations; i++) {
                    // handle backwards logic of Squid25 where both ST, SA false, means both True
                    boolean switchRM = Boolean.parseBoolean(switchST[i + (isSquid2_20 ? 1 : 2)]);
                    boolean switchUN = Boolean.parseBoolean(switchSA[i + (isSquid2_20 ? 1 : 2)]);
                    if (!switchRM && !switchUN) {
                        switchRM = true;
                        switchUN = true;
                    }

                    if (prepareSquid25ExcelEquationStringForSquid3(equations[i + 2]).length() > 0) {
                        String excelExpression = prepareSquid25ExcelEquationStringForSquid3(equations[i + 2]);
                        //detect if name contains "Age" or "abs" - undo change to % for errors
                        if ((equationNames[i + 2].toUpperCase().contains("ABS")) || (equationNames[i + 2].toUpperCase().contains("AGE"))) {
                            if (excelExpression.startsWith("[%\"")) {
                                excelExpression = excelExpression.replaceFirst("\\[%\"", "\\[±\"");
                            }
                        }
                        taskSquid25.task25Equations.add(new TaskSquid25Equation(
                                excelExpression,
                                prepareSquid25ExcelEquationNameForSquid3(equationNames[i + 2]),
                                switchRM,
                                switchUN,
                                Boolean.parseBoolean(switchSC[i + (isSquid2_20 ? 1 : 2)]),
                                Boolean.parseBoolean(switchNU[i + (isSquid2_20 ? 1 : 2)]),
                                false, false));
                    }
                }

//                String[] constantNamesSource = lines[firstRow + 40].split("\t");
//                String[] constantValuesSource = lines[firstRow + 41].split("\t");
//
//                int countOfConstants = 0;
//                if (constantNamesSource.length > 1) {
//                    countOfConstants = Integer.valueOf(constantNamesSource[1]);
//                }
//                taskSquid25.constantNames = new ArrayList<>();
//                taskSquid25.constantValues = new ArrayList<>();
//                for (int i = 0; i < countOfConstants; i++) {
//                    taskSquid25.constantNames.add(constantNamesSource[i + 2].replaceFirst("_", ""));
//                    taskSquid25.constantValues.add(constantValuesSource[i + 2]);
//                }
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
        // assume most calls to uncertainty are for percent
        retVal = retVal.replace("[±\"", "[%\"");

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

        // remove "<" and ">" from constants
        squid25FunctionPattern = Pattern.compile("<(.[^>]*)>", Pattern.CASE_INSENSITIVE);
        matcher = squid25FunctionPattern.matcher(retVal);
        if (matcher.find()) {
            String constant = matcher.group();
            constant = constant.substring(1, constant.length() - 1);
            retVal = retVal.replace(matcher.group(), constant);
        }

        // remove "/" and " " from expressions that contain letters only
        squid25FunctionPattern = Pattern.compile("\\[\\\"\\D*\\\"\\]", Pattern.CASE_INSENSITIVE);
        matcher = squid25FunctionPattern.matcher(retVal);
        if (matcher.find()) {
            String name = matcher.group();
            name = name.replaceAll("/", "").replaceAll(" ", "");
            retVal = retVal.replace(matcher.group(), name);
        }

        // remove leading mulitpliers meant for output tables
        retVal = retVal.replace("1000*", "");
        retVal = retVal.replace("100*", "");

        // do not accept non-numeric constants as being equations - this results from the conflation in Squid2.5 between equations and outputs
        // unless already noted as constant
        if (!taskSquid25.constantNames.contains(retVal)) {
            if (!excelString.contains("(") && !excelString.contains("[")) {
                if (!ShuntingYard.isNumber(excelString)) {
                    retVal = "";
                }
            }
        }

        // do not include calls to error functions of Age as in AgeErPb76 etc
        if (excelString.toUpperCase(Locale.US).contains("AGEER")) {
            retVal = "";
        }

        // misc edits
        if (retVal.matches("^.*(?i)corr\\S.*")) {
            if (!retVal.contains("corr.")) {
                retVal = retVal.replace("corr", "cor_");
                if (retVal.contains("204cor")) {
                    retVal = retVal.replace("204cor", "4cor");
                }

                if (retVal.contains("207cor")) {
                    retVal = retVal.replace("207cor", "7cor");
                }

                if (retVal.contains("208cor")) {
                    retVal = retVal.replace("208cor", "8cor");
                }

                if (retVal.matches("^.*\\S(?i)age.*")) {
                    retVal = retVal.replace("Age", "_Age");
                    retVal = retVal.replace("age", "_Age");
                }
            } else {
                if (retVal.matches("^.*\\S(?i)age.*")) {
                    retVal = retVal.replace("Age", " Age");
                    retVal = retVal.replace("age", " Age");
                }
            }
            retVal = retVal.replace("corr 2", "cor_2");
            retVal = retVal.replace("corr2", "cor_2");
        }

        if (retVal.contains("\"4-cor")) {
            retVal = retVal.replace("\"4-cor", "\"4cor");
        }
        if (retVal.contains("\"7-corr")) {
            retVal = retVal.replace("\"7-cor", "\"7cor");
        }
        if (retVal.contains("\"8-corr")) {
            retVal = retVal.replace("\"8-cor", "\"8cor");
        }

        if (retVal.contains("%com206")) {
            retVal = retVal.replace("%com206", COM206PB_PCT);
        }
        if (retVal.contains("%com208")) {
            retVal = retVal.replace("%com208", COM208PB_PCT);
        }

        if (retVal.contains("r_206*/238")) {
            retVal = retVal.replace("r_206*/238", "r_" + R206PB_238U);
        }

        if (retVal.contains("r_207*/206*")) {
            retVal = retVal.replace("r_207*/206*", "r_" + R207PB_206PB);
        }

        if (retVal.contains("r_207*/235")) {
            retVal = retVal.replace("r_207*/235", "r_" + R207PB_235U);
        }

        if (retVal.contains("r_207Pb/206Pb")) {
            retVal = retVal.replace("r_207Pb/206Pb", "r_" + R207PB_206PB);
        }

        if (retVal.contains("r_206Pb/238U")) {
            retVal = retVal.replace("r_206Pb/238U", "r_" + R206PB_238U);
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

        // expression customizeradds this space
        retVal = retVal.replace("Age", " Age");

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
    public TaskTypeEnum getTaskType() {
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
     * @return the backgroundMass
     */
    public String getBackgroundMass() {
        return backgroundMass;
    }

    /**
     *
     * @return
     */
    public String getParentNuclide() {
        return parentNuclide;
    }

    /**
     * @return the directAltPD
     */
    public boolean isDirectAltPD() {
        return directAltPD;
    }

    /**
     *
     * @return
     */
    public String getPrimaryParentElement() {
        return primaryParentElement;
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

    /**
     * @return the constantNames
     */
    public List<String> getConstantNames() {
        return constantNames;
    }

    /**
     * @return the constantValues
     */
    public List<String> getConstantValues() {
        return constantValues;
    }

}
