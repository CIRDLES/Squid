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
                            "", "true", "true", "2", "", "", "false", "false"
                        };
            }
            categoryColumns = new ReportColumn[generatedReportCategorySpecs.length];
            for (int i = 0; i < categoryColumns.length; i++) {
                categoryColumns[i] = SetupReportColumn(i, generatedReportCategorySpecs[i]);
            }
        } else if (reportCategorySpecs[0][6].compareToIgnoreCase("<RATIOS_ARRAY>") == 0) {
            // special case of generation
            Iterator<SquidRatiosModel> squidRatiosIterator = ((ShrimpFraction) task.getReferenceMaterialSpots().get(0)).getIsotopicRatiosII().iterator();
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
                        "", "true", "true", "4", "true", "", "false", "false"
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
                    = ((ShrimpFraction) task.getReferenceMaterialSpots().get(0)).getTaskExpressionsForScansEvaluated();
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
                        "", "true", "true", "4", "true", "", "false", "false"
                    };

                    generatedReportCategorySpecsList.add(columnSpec);
                }

            }
            categoryColumns = new ReportColumn[generatedReportCategorySpecsList.size()];
            for (int i = 0; i < categoryColumns.length; i++) {
                categoryColumns[i] = SetupReportColumn(i, generatedReportCategorySpecsList.get(i));
            }
        } else {
            categoryColumns = new ReportColumn[reportCategorySpecs.length];
            for (int i = 0; i < categoryColumns.length; i++) {
                categoryColumns[i] = SetupReportColumn(i, reportCategorySpecs[i]);
            }
        }

        this.categoryColor = Color.WHITE;
        this.visible = isVisible;
        this.legacyData = false;

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
