/*
 * ReportCategory.java
 *
 * Created on September 9, 2008, 3:05 PM
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.reports.reportCategories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.paint.Color;
import org.cirdles.squid.reports.reportColumns.ReportColumn;
import org.cirdles.squid.reports.reportColumns.ReportColumnInterface;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.evaluationEngines.TaskExpressionEvaluatedPerSpotPerScanModelInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNTS_PERSEC_4_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNT_4_6_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PPM_PARENT_EQN_NAME_TH;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TH_U_EQN_NAME;

/**
 *
 * @author James F. Bowring
 */
public class ReportCategory implements org.cirdles.squid.reports.reportCategories.ReportCategoryInterface {

    // private static final long serialVersionUID = 5227409808812622714L;
    // Fields
    private String displayName;
    private int positionIndex;
    private TaskInterface task;
    private ReportColumnInterface[] categoryColumns;
    private Color categoryColor;
    private boolean visible;
    private boolean legacyData;

    /**
     * Creates a new instance of ReportCategory
     */
    public ReportCategory() {
    }

    /**
     * Creates a new instance of ReportCategory
     *
     * @param displayName
     * @param reportCategorySpecs
     * @param isVisible
     * @param task the value of task
     */
    public ReportCategory(
            String displayName, String[][] reportCategorySpecs, boolean isVisible, TaskInterface task) {

        this.displayName = displayName;
        this.positionIndex = 0;

        if (reportCategorySpecs[0][6].compareToIgnoreCase("<SPECIES_ARRAY>") == 0) {
            // special case of generation
            String[] isotopeLabels = new String[task.getSquidSpeciesModelList().size()];
            task.getMapOfIndexToMassStationDetails().get(1).getIsotopeLabel();
            for (int i = 0; i < isotopeLabels.length; i++) {
                isotopeLabels[i] = task.getMapOfIndexToMassStationDetails().get(i).getIsotopeLabel();
            }

            String[][] generatedReportCategorySpecs = new String[isotopeLabels.length][];
            for (int i = 0; i < isotopeLabels.length; i++) {
                // Report column order =
                //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
                //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
                //     needsLead, needsUranium
                generatedReportCategorySpecs[i]
                        = new String[]{
                            "total",
                            isotopeLabels[i],
                            "cts",
                            "/sec",
                            "",
                            reportCategorySpecs[0][5],
                            "<INDEX>" + i,
                            "",
                            "", "true", "true", "15", "", "", "false", "false"
                        };
            }
            categoryColumns = new ReportColumn[generatedReportCategorySpecs.length];
            for (int i = 0; i < categoryColumns.length; i++) {
                categoryColumns[i] = SetupReportColumn(i, generatedReportCategorySpecs[i]);
            }
        } else if (reportCategorySpecs[0][6].compareToIgnoreCase("<RATIOS_ARRAY>") == 0) {
            // special case of generation
            Iterator<SquidRatiosModel> squidRatiosIterator = ((ShrimpFraction) task.getUnknownSpots().get(0)).getIsotopicRatiosII().iterator();
            List<String[]> generatedReportCategorySpecsList = new ArrayList<>();
            while (squidRatiosIterator.hasNext()) {
                SquidRatiosModel entry = squidRatiosIterator.next();
                if (entry.isActive()) {
                    // Report column order =
                    //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
                    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
                    //     needsLead, needsUranium
                    String displayNameNoSpaces = entry.getDisplayNameNoSpaces().substring(0, Math.min(20, entry.getDisplayNameNoSpaces().length()));
                    String[] columnSpec = new String[]{
                        "",
                        "",
                        displayNameNoSpaces.split("/")[0],
                        "/" + displayNameNoSpaces.split("/")[1],
                        "",
                        reportCategorySpecs[0][5],
                        displayNameNoSpaces,
                        "PCT",
                        "", "true", "false", "15", "true", "", "false", "false"
                    };

                    generatedReportCategorySpecsList.add(columnSpec);
                }
            }
            categoryColumns = new ReportColumn[generatedReportCategorySpecsList.size()];
            for (int i = 0; i < categoryColumns.length; i++) {
                categoryColumns[i] = SetupReportColumn(i, generatedReportCategorySpecsList.get(i));
            }
        } else if (reportCategorySpecs[0][6].compareToIgnoreCase("<RM_EXPRESSIONS_ARRAY>") == 0) {
            // special case of generation
            List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsEvaluated
                    = ((ShrimpFraction) task.getUnknownSpots().get(0)).getTaskExpressionsForScansEvaluated();
            List<String[]> generatedReportCategorySpecsList = new ArrayList<>();
            for (TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEval : taskExpressionsEvaluated) {

                if ((!taskExpressionEval.getExpression().isSquidSpecialUPbThExpression())
                        && (taskExpressionEval.getExpression().isSquidSwitchSTReferenceMaterialCalculation())) {
                    // Report column order =
                    //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
                    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
                    //     needsLead, needsUranium
                    String expressionName = taskExpressionEval.getExpression().getName().substring(0, Math.min(20, taskExpressionEval.getExpression().getName().length()));
                    String[] columnSpec = new String[]{
                        "",
                        "",
                        "",
                        expressionName,
                        "",
                        reportCategorySpecs[0][5],
                        expressionName,
                        "PCT",
                        "", "true", "false", "15", "true", "", "false", "false"
                    };

                    generatedReportCategorySpecsList.add(columnSpec);
                }

            }
            categoryColumns = new ReportColumn[generatedReportCategorySpecsList.size()];
            for (int i = 0; i < categoryColumns.length; i++) {
                categoryColumns[i] = SetupReportColumn(i, generatedReportCategorySpecsList.get(i));
            }
        } else {

            List<ReportColumnInterface> categoryColumnList = new ArrayList<>();
            int colIndex = 0;
            boolean has232 = task.getParentNuclide().contains("232");
            boolean isDirect = task.isDirectAltPD();

            for (int specIndex = 0; specIndex < reportCategorySpecs.length; specIndex++) {

                if (reportCategorySpecs[specIndex][6].compareToIgnoreCase("<SQUID_TH_U_EQN_NAME>") == 0) {
                    List<ReportColumnInterface> categoryColumnListSQUID_TH_U_EQN_NAME
                            = produceColumnSQUID_TH_U_EQN_NAME(colIndex, isDirect, has232);
                    categoryColumnList.addAll(categoryColumnListSQUID_TH_U_EQN_NAME);
                    colIndex = colIndex + categoryColumnListSQUID_TH_U_EQN_NAME.size();
                } else if (reportCategorySpecs[specIndex][6].compareToIgnoreCase("<SQUID_PPM_PARENT_EQN_NAME_TH>") == 0) {
                    List<ReportColumnInterface> categoryColumnListSQUID_PPM_PARENT_EQN_NAME_TH
                            = produceColumnSQUID_PPM_PARENT_EQN_NAME_TH(colIndex, isDirect, has232);
                    categoryColumnList.addAll(categoryColumnListSQUID_PPM_PARENT_EQN_NAME_TH);
                    colIndex = colIndex + categoryColumnListSQUID_PPM_PARENT_EQN_NAME_TH.size();
                } else if (reportCategorySpecs[specIndex][6].compareToIgnoreCase("<OVER_COUNT_4_6_8>") == 0) {
                    List<ReportColumnInterface> categoryColumnListOVER_COUNT_4_6_8
                            = produceColumnOVER_COUNT_4_6_8(colIndex, isDirect, has232);
                    categoryColumnList.addAll(categoryColumnListOVER_COUNT_4_6_8);
                    colIndex = colIndex + categoryColumnListOVER_COUNT_4_6_8.size();
                } else if (reportCategorySpecs[specIndex][6].compareToIgnoreCase("<OVER_COUNTS_PERSEC_4_8>") == 0) {
                    List<ReportColumnInterface> categoryColumnListOVER_COUNTS_PERSEC_4_8
                            = produceColumnOVER_COUNTS_PERSEC_4_8(colIndex, isDirect, has232);
                    categoryColumnList.addAll(categoryColumnListOVER_COUNTS_PERSEC_4_8);
                    colIndex = colIndex + categoryColumnListOVER_COUNTS_PERSEC_4_8.size();
                } else if (reportCategorySpecs[specIndex][6].compareToIgnoreCase("<CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA>") == 0) {
                    List<ReportColumnInterface> categoryColumnListCORR_8_PRIMARY_CALIB_CONST_PCT_DELTA
                            = produceColumnCORR_8_PRIMARY_CALIB_CONST_PCT_DELTA(colIndex, isDirect, has232);
                    categoryColumnList.addAll(categoryColumnListCORR_8_PRIMARY_CALIB_CONST_PCT_DELTA);
                    colIndex = colIndex + categoryColumnListCORR_8_PRIMARY_CALIB_CONST_PCT_DELTA.size();
                } else {
                    categoryColumnList.add(SetupReportColumn(colIndex, reportCategorySpecs[specIndex]));
                    colIndex++;
                }
            }
            categoryColumns = new ReportColumn[categoryColumnList.size()];
            for (int i = 0; i < categoryColumnList.size(); i++) {
                categoryColumns[i] = categoryColumnList.get(i);
            }

        }

        this.categoryColor = Color.WHITE;
        this.visible = isVisible;
        this.legacyData = false;

    }

    private List<ReportColumnInterface> produceColumnSQUID_TH_U_EQN_NAME(int myColIndex, boolean isDirect, boolean has232) {
        List<ReportColumnInterface> categoryColumnList = new ArrayList<>();
        int colIndex = myColIndex;

        String[] columnSpec = new String[]{
            "",
            "204corr",
            "232Th",
            "/238U",
            "",
            "getTaskExpressionsEvaluationsPerSpotByField",
            SQUID_TH_U_EQN_NAME,
            "PCT",
            "", "true", "false", "20", "true", "232/238 ratio", "false", "false"};
        // perm1 and 3
        if (!isDirect) {
            columnSpec[1] = "204corr";
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            columnSpec[1] = "207corr";
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            // perm 1 only
            if (!has232) {
                columnSpec[1] = "208corr";
                categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
                colIndex++;
            }
        } else {
            // perm2 and 4
            columnSpec[1] = "204corr";
            columnSpec[6] = "4-corr " + SQUID_TH_U_EQN_NAME;
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            columnSpec[1] = "207corr";
            columnSpec[6] = "7-corr " + SQUID_TH_U_EQN_NAME;
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;
        }
        return categoryColumnList;
    }

    private List<ReportColumnInterface> produceColumnSQUID_PPM_PARENT_EQN_NAME_TH(int myColIndex, boolean isDirect, boolean has232) {
        List<ReportColumnInterface> categoryColumnList = new ArrayList<>();
        int colIndex = myColIndex;

        String[] columnSpec = new String[]{
            "",
            "204corr",
            "Th",
            "(ppm)",
            "",
            "getTaskExpressionsEvaluationsPerSpotByField",
            SQUID_PPM_PARENT_EQN_NAME_TH,
            "",
            "", "true", "false", "20", "", "concentration of Th", "false", "false"};
        // perm1 and 3
        if (!isDirect) {
            columnSpec[1] = "204corr";
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            columnSpec[1] = "207corr";
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            // perm 1 only
            if (!has232) {
                columnSpec[1] = "208corr";
                categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
                colIndex++;
            }
        } else {
            // perm2 and 4
            columnSpec[1] = "204corr";
            columnSpec[6] = "4-corr " + SQUID_PPM_PARENT_EQN_NAME_TH;
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            columnSpec[1] = "207corr";
            columnSpec[6] = "7-corr " + SQUID_PPM_PARENT_EQN_NAME_TH;
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;
        }
        return categoryColumnList;
    }

    private List<ReportColumnInterface> produceColumnOVER_COUNT_4_6_8(int myColIndex, boolean isDirect, boolean has232) {
        List<ReportColumnInterface> categoryColumnList = new ArrayList<>();
        int colIndex = myColIndex;

        String[] columnSpec = new String[]{
            "204corr", "204", "/206", "(fr. 208)",
            "",
            "getTaskExpressionsEvaluationsPerSpotByField",
            OVER_COUNT_4_6_8,
            "",
            "", "true", "false", "20", "", "", "false", "false"};
        // perm1 and 3
        if (!isDirect) {
            columnSpec[0] = "204corr";
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            columnSpec[0] = "207corr";
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            // perm 1 only
            if (!has232) {
                columnSpec[0] = "208corr";
                categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
                colIndex++;
            }
        } else {
            // perm2 and 4
            columnSpec[0] = "204corr";
            columnSpec[6] = "4-corr " + OVER_COUNT_4_6_8;
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            columnSpec[0] = "207corr";
            columnSpec[6] = "7-corr " + OVER_COUNT_4_6_8;
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;
        }
        return categoryColumnList;
    }

    private List<ReportColumnInterface> produceColumnOVER_COUNTS_PERSEC_4_8(int myColIndex, boolean isDirect, boolean has232) {
        List<ReportColumnInterface> categoryColumnList = new ArrayList<>();
        int colIndex = myColIndex;

        String[] columnSpec = new String[]{
            "204corr", "204", "overcts/sec", "(fr. 208)",
            "",
            "getTaskExpressionsEvaluationsPerSpotByField",
            OVER_COUNTS_PERSEC_4_8,
            "",
            "", "true", "false", "20", "", "", "false", "false"};
        // perm1 and 3
        if (!isDirect) {
            columnSpec[0] = "204corr";
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            columnSpec[0] = "207corr";
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            // perm 1 only
            if (!has232) {
                columnSpec[0] = "208corr";
                categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
                colIndex++;
            }
        } else {
            // perm2 and 4
            columnSpec[0] = "204corr";
            columnSpec[6] = "4-corr " + OVER_COUNTS_PERSEC_4_8;
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            columnSpec[0] = "207corr";
            columnSpec[6] = "7-corr " + OVER_COUNTS_PERSEC_4_8;
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;
        }
        return categoryColumnList;
    }

    private List<ReportColumnInterface> produceColumnCORR_8_PRIMARY_CALIB_CONST_PCT_DELTA(int myColIndex, boolean isDirect, boolean has232) {
        List<ReportColumnInterface> categoryColumnList = new ArrayList<>();
        int colIndex = myColIndex;

        String[] columnSpec = new String[]{
            "204corr", "8-corr", "206Pb/238U", "const delta%",
            "",
            "getTaskExpressionsEvaluationsPerSpotByField",
            CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA,
            "",
            "", "true", "false", "20", "", "", "false", "false"};
        // perm1 and 3
        if (!isDirect) {
            columnSpec[0] = "204corr";
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            columnSpec[0] = "207corr";
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            // perm 1 only
            if (!has232) {
                columnSpec[0] = "208corr";
                categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
                colIndex++;
            }
        } else {
            // perm2 and 4
            columnSpec[0] = "204corr";
            columnSpec[6] = "4-corr " + CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA;
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;

            columnSpec[0] = "207corr";
            columnSpec[6] = "7-corr " + CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA;
            categoryColumnList.add(SetupReportColumn(colIndex, columnSpec));
            colIndex++;
        }
        return categoryColumnList;
    }

    private ReportColumnInterface SetupReportColumn(int index, String[] specs) {

        ReportColumnInterface retVal = new ReportColumn(//
                specs[0], // displayname1
                specs[1], // displayname2
                specs[2], // displayname3
                specs[3].contains("delta") ? specs[3].replace("delta", "\u1E9F") : specs[3], // displayname4
                index, // positionIndex
                specs[4], // units
                specs[5], // retrieveMethodName
                specs[6], // retrieveMethodParameterName
                specs[7], // uncertaintyType
                specs[8], // footnoteSpec
                Boolean.valueOf(specs[9]), // visible
                false); // amUncertainty

        retVal.setDisplayedWithArbitraryDigitCount(Boolean.valueOf(specs[10]));
        retVal.setCountOfSignificantDigits(Integer.parseInt(specs[11]));
        retVal.setAlternateDisplayName(specs[13]);
        retVal.setNeedsPb(Boolean.valueOf(specs[14]));
        retVal.setNeedsU(Boolean.valueOf(specs[15]));
        retVal.setLegacyData(isLegacyData());

        // check for need to create uncertainty column
        ReportColumnInterface uncertaintyCol = null;

        if (!specs[7].equals("")) {
            uncertaintyCol = new ReportColumn(//
                    "",
                    "",
                    specs[7].equalsIgnoreCase("PCT") ? "" : "\u00B11\u03C3",
                    specs[7].equalsIgnoreCase("PCT") ? "\u00B11\u03C3 %" : "abs",
                    //"third",
                    index,
                    specs[4],
                    specs[5],
                    specs[6],
                    specs[7],
                    "",
                    Boolean.valueOf(specs[12]),// show uncertainty
                    true); // amUncertainty

            uncertaintyCol.setAlternateDisplayName("");
        }

        retVal.setUncertaintyColumn(uncertaintyCol);

        return retVal;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportColumnInterface[] getCategoryColumns() {
        return categoryColumns;
    }

    /**
     *
     * @param categoryColumns
     */
    @Override
    public void setCategoryColumns(ReportColumnInterface[] categoryColumns) {
        this.categoryColumns = categoryColumns;
    }

    /**
     *
     * @return
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     *
     * @param displayName
     */
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @return
     */
    @Override
    public int getPositionIndex() {
        return positionIndex;
    }

    /**
     *
     * @param positionIndex
     */
    @Override
    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    /**
     *
     * @return
     */
    @Override
    public Color getCategoryColor() {
        return categoryColor;
    }

    /**
     *
     * @param categoryColor
     */
    @Override
    public void setCategoryColor(Color categoryColor) {
        this.categoryColor = categoryColor;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     *
     */
    @Override
    public void ToggleIsVisible() {
        this.setVisible(!isVisible());
    }

    /**
     * @return the legacyData
     */
    @Override
    public boolean isLegacyData() {
        return legacyData;
    }

    /**
     * @param legacyData the legacyData to set
     */
    @Override
    public void setLegacyData(boolean legacyData) {
        this.legacyData = legacyData;
        for (ReportColumnInterface categoryColumn : categoryColumns) {
            categoryColumn.setLegacyData(legacyData);
        }
    }
}
