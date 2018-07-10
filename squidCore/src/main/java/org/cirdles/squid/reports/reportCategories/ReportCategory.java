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

import javafx.scene.paint.Color;
import org.cirdles.squid.reports.reportColumns.ReportColumn;
import org.cirdles.squid.reports.reportColumns.ReportColumnInterface;



/**
 *
 * @author James F. Bowring
 */
public class ReportCategory implements org.cirdles.squid.reports.reportCategories.ReportCategoryInterface {

    private static final long serialVersionUID = 5227409808812622714L;

    // Fields
    private String displayName;
    private int positionIndex;
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
     */
    public ReportCategory(
            String displayName, String[][] reportCategorySpecs, boolean isVisible) {

        this.displayName = displayName;
        this.positionIndex = 0;
        categoryColumns = new ReportColumn[reportCategorySpecs.length];
        for (int i = 0; i < categoryColumns.length; i++) {
            categoryColumns[i] = SetupReportColumn(i, reportCategorySpecs);
        }

        this.categoryColor = Color.WHITE;
        this.visible = isVisible;
        this.legacyData = false;

    }

    private ReportColumnInterface SetupReportColumn(int index, String[][] specs) {
        String displayName1 = specs[index][0];
        ReportColumnInterface retVal = new ReportColumn(//
                specs[index][0].contains("delta") ? specs[index][0].replace("delta", "\u03B4") : specs[index][0], //displayName1, //specs[index][0], // displayname1
                specs[index][1].contains("delta") ? specs[index][1].replace("delta", "\u03B4") : specs[index][1], // displayname2
                specs[index][2].contains("delta") ? specs[index][2].replace("delta", "\u03B4") : specs[index][2], // displayname3
                specs[index][3].contains("delta") ? specs[index][3].replace("delta", "\u03B4") : specs[index][3], // displayname4
                index, // positionIndex
                specs[index][4], // units
                specs[index][5], // retrieveMethodName
                specs[index][6], // retrieveMethodParameterName
                specs[index][7], // uncertaintyType
                specs[index][8], // footnoteSpec
                Boolean.valueOf(specs[index][9]), // visible
                false); // amUncertainty

        retVal.setDisplayedWithArbitraryDigitCount(Boolean.valueOf(specs[index][10]));
        retVal.setCountOfSignificantDigits(Integer.parseInt(specs[index][11]));
        retVal.setAlternateDisplayName(specs[index][13]);
        retVal.setNeedsPb(Boolean.valueOf(specs[index][14]));
        retVal.setNeedsU(Boolean.valueOf(specs[index][15]));
        retVal.setLegacyData(isLegacyData());

        // check for need to create uncertainty column
        ReportColumnInterface uncertaintyCol = null;

        if (!specs[index][7].equals("")) {
            uncertaintyCol = new ReportColumn(//
                    "",
                    "",
                    specs[index][7].equalsIgnoreCase("PCT") ? "" : "\u00B12\u03C3",
                    specs[index][7].equalsIgnoreCase("PCT") ? "\u00B12\u03C3 %" : "abs",
                    //"third",
                    index,
                    specs[index][4],
                    specs[index][5],
                    specs[index][6],
                    specs[index][7],
                    "",
                    Boolean.valueOf(specs[index][12]),// show uncertainty
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
