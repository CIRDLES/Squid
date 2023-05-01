/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.squidReports.squidReportCategories;

import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnXMLConverter;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary;
import org.cirdles.squid.tasks.expressions.builtinExpressions.ReferenceMaterialAgeTypesEnum;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SampleAgeTypesEnum;
import org.cirdles.squid.utilities.IntuitiveStringComparator;

import java.io.Serializable;
import java.util.*;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */

public class SquidReportCategory implements Serializable, SquidReportCategoryInterface {

    public static final List<SquidReportCategory> defaultSampleWMSortingCategories = new ArrayList<>();
    public static final List<SquidReportCategory> defaultRefMatWMSortingCategories = new ArrayList<>();
    public static final List<SquidReportCategory> defaultRefMatCalibrationConstantSortingCategories = new ArrayList<>();
    private static final long serialVersionUID = 8741573410884399160L;

    static {

        SquidReportCategory time = createReportCategory("Time");
        LinkedList<SquidReportColumnInterface> categoryColumns = new LinkedList<>();
        SquidReportColumnInterface column = SquidReportColumn.createSquidReportColumn("SpotIndex");
        categoryColumns.add(column);
        column = SquidReportColumn.createSquidReportColumn("Hours");
        categoryColumns.add(column);
        time.setCategoryColumns(categoryColumns);
        defaultSampleWMSortingCategories.add(time);
        defaultRefMatWMSortingCategories.add(time);
        defaultRefMatCalibrationConstantSortingCategories.add(time);

        SquidReportCategory ages = createReportCategory("Ages");
        categoryColumns = new LinkedList<>();
        for (SampleAgeTypesEnum sampleAgeType : SampleAgeTypesEnum.values()) {
            column = SquidReportColumn.createSquidReportColumn(sampleAgeType.getExpressionName());
            categoryColumns.add(column);
        }
        ages.setCategoryColumns(categoryColumns);
        defaultSampleWMSortingCategories.add(ages);

        SquidReportCategory agesRM = createReportCategory("Ages");
        categoryColumns = new LinkedList<>();
        for (ReferenceMaterialAgeTypesEnum referenceMaterialAgeType : ReferenceMaterialAgeTypesEnum.values()) {
            column = SquidReportColumn.createSquidReportColumn(referenceMaterialAgeType.getExpressionName());
            categoryColumns.add(column);
        }
        agesRM.setCategoryColumns(categoryColumns);
        defaultRefMatWMSortingCategories.add(agesRM);

        // raw ratios will be populated on the fly from task with the exception of the required ratios
        SquidReportCategory rawRatios = createReportCategory("Raw Ratios");
        categoryColumns = new LinkedList<>();
        for (String ratioName : BuiltInExpressionsDataDictionary.REQUIRED_RATIO_NAMES) {
            column = SquidReportColumn.createSquidReportColumn(ratioName);
            categoryColumns.add(column);
        }
        rawRatios.setCategoryColumns(categoryColumns);
        defaultSampleWMSortingCategories.add(rawRatios);
        defaultRefMatWMSortingCategories.add(rawRatios);
        defaultRefMatCalibrationConstantSortingCategories.add(rawRatios);

        SquidReportCategory correctedRatios = createReportCategory("Corr. Ratios");
        categoryColumns = new LinkedList<>();
        for (String ratioName : BuiltInExpressionsDataDictionary.CORRECTED_RATIOS_EXPRESSION_NAMES) {
            column = SquidReportColumn.createSquidReportColumn(ratioName);
            categoryColumns.add(column);
        }
        correctedRatios.setCategoryColumns(categoryColumns);
        defaultSampleWMSortingCategories.add(correctedRatios);

        correctedRatios = createReportCategory("Corr. Ratios");
        categoryColumns = new LinkedList<>();
        for (String ratioName : BuiltInExpressionsDataDictionary.CORRECTED_RATIOS_EXPRESSION_NAMES) {
            column = SquidReportColumn.createSquidReportColumn(ratioName + "_RM");
            categoryColumns.add(column);
        }
        correctedRatios.setCategoryColumns(categoryColumns);
        defaultRefMatWMSortingCategories.add(correctedRatios);

    }

    // Fields
    private String displayName;

    private LinkedList<SquidReportColumnInterface> categoryColumns;

    private boolean visible;

    private SquidReportCategory() {
        this("", null, false);
    }

    private SquidReportCategory(String displayName) {
        this(displayName, null, false);
    }

    private SquidReportCategory(String displayName, LinkedList<SquidReportColumnInterface> categoryColumns, boolean visible) {
        this.displayName = displayName;
        this.categoryColumns = categoryColumns;
        this.visible = visible;
    }

    public static SquidReportCategory createReportCategory(String displayName) {
        LinkedList<SquidReportColumnInterface> categoryColumns = new LinkedList<>();

        return new SquidReportCategory(displayName, categoryColumns, true);
    }

    /**
     * @return the displayName
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the categoryColumns
     */
    @Override
    public LinkedList<SquidReportColumnInterface> getCategoryColumns() {
        return categoryColumns;
    }

    /**
     * @param categoryColumns the categoryColumns to set
     */
    @Override
    public void setCategoryColumns(LinkedList<SquidReportColumnInterface> categoryColumns) {
        this.categoryColumns = categoryColumns;
    }

    @Override
    public LinkedList<SquidReportColumnInterface> getCategoryColumnsSorted() {
        @SuppressWarnings("unchecked") LinkedList<SquidReportColumnInterface> sortedColumns
                = (LinkedList<SquidReportColumnInterface>) categoryColumns.clone();

        if (this.displayName.compareToIgnoreCase("Time") != 0) {
            Collections.sort(sortedColumns, (SquidReportColumnInterface col1, SquidReportColumnInterface col2) -> {
                IntuitiveStringComparator<String> intuitiveStringComparator
                        = new IntuitiveStringComparator<>();
                return intuitiveStringComparator.compare(
                        col1.getExpressionName().toLowerCase(),
                        col2.getExpressionName().toLowerCase());
            });
        }

        return sortedColumns;
    }

    /**
     * @return the visible
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new SquidReportCategoryXMLConverter());
        xstream.alias("SquidReportCategory", SquidReportCategory.class);

        xstream.registerConverter(new SquidReportColumnXMLConverter());
        xstream.alias("SquidReportColumn", SquidReportColumn.class);
    }

    public SquidReportCategory clone() {
        SquidReportCategoryInterface cat = new SquidReportCategory(displayName);
        LinkedList<SquidReportColumnInterface> cols = new LinkedList<>();
        for (SquidReportColumnInterface col : categoryColumns) {
            cols.add(col.clone());
        }
        cat.setCategoryColumns(cols);
        cat.setVisible(visible);
        return (SquidReportCategory) cat;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SquidReportCategory that = (SquidReportCategory) o;
        return visible == that.visible && displayName.equals(that.displayName) && categoryColumns.equals(that.categoryColumns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, categoryColumns, visible);
    }
}