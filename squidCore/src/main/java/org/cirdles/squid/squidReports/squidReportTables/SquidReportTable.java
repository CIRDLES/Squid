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
package org.cirdles.squid.squidReports.squidReportTables;

import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.constants.Squid3Constants.SpotTypes;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryXMLConverter;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnXMLConverter;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.operations.Value;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.cirdles.squid.constants.Squid3Constants.XML_HEADER_FOR_SQUIDREPORTTABLE_FILES_USING_LOCAL_SCHEMA;
import static org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory.createReportCategory;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SquidReportTable implements Serializable, SquidReportTableInterface, Comparable<SquidReportTableInterface> {

    public final static int HEADER_ROW_COUNT = 7;
    public final static int DEFAULT_COUNT_OF_SIGNIFICANT_DIGITS = 15;
    private static final long serialVersionUID = 1685572683987304408L;
    public static int WEIGHTEDMEAN_PLOT_SORT_TABLE_VERSION = 6;
    public static String NAME_OF_WEIGHTEDMEAN_PLOT_SORT_REPORT = "Weighted Mean Plot and Sort Report";
    public static int RM_WEIGHTEDMEAN_PLOT_SORT_TABLE_VERSION = 6;
    public static String RM_NAME_OF_WEIGHTEDMEAN_PLOT_SORT_REPORT = "RM Weighted Mean Plot and Sort Report";
    // Fields
    private String reportTableName;

    private LinkedList<SquidReportCategoryInterface> reportCategories;

    // Backwards compatibility with report tables prior to ...
    private SpotTypes reportSpotTarget;

    private boolean isBuiltInSquidDefault;

    private boolean isLabDataDefault;

    private int version;

    private SquidReportTable() {
    }

    private SquidReportTable(String reportTableName, LinkedList<SquidReportCategoryInterface> reportCategories, boolean isDefault) {
        this(reportTableName, reportCategories, isDefault, 0);
    }

    private SquidReportTable(String reportTableName, LinkedList<SquidReportCategoryInterface> reportCategories, boolean isDefault, int version) {
        this.reportTableName = reportTableName;
        this.reportCategories = reportCategories;
        this.isBuiltInSquidDefault = isDefault;
        this.isLabDataDefault = false;
        this.version = version;
    }

    public static SquidReportTable createEmptySquidReportTable(String reportTableName) {
        SquidReportTable table = new SquidReportTable(reportTableName, new LinkedList<>(), false);
        table.setReportSpotTarget(SpotTypes.NONE);
        return table;
    }

    public static SquidReportTable createDefaultSquidReportTableRefMat(TaskInterface task) {
        String reportTableName = "Builtin Report Table for Reference Materials";
        LinkedList<SquidReportCategoryInterface> reportCategories = createDefaultReportCategoriesRefMat(task);
        SquidReportTable table = new SquidReportTable(reportTableName, reportCategories, true);
        table.setReportSpotTarget(SpotTypes.REFERENCE_MATERIAL);
        return table;
    }

    public static SquidReportTable createDefaultSquidReportTableUnknown(TaskInterface task) {
        String reportTableName = "Builtin Report Table for Unknowns";
        LinkedList<SquidReportCategoryInterface> reportCategories = createDefaultReportCategoriesUnknown(task);
        SquidReportTable table = new SquidReportTable(reportTableName, reportCategories, true);
        table.setReportSpotTarget(SpotTypes.UNKNOWN);
        return table;
    }

    public static SquidReportTable createDefaultSquidReportTableUnknownSquidFilter(TaskInterface task, int version) {
        LinkedList<SquidReportCategoryInterface> reportCategories = createDefaultSampleWMSortingCategoriesSquidFilter(task);
        SquidReportTable table = new SquidReportTable(NAME_OF_WEIGHTEDMEAN_PLOT_SORT_REPORT, reportCategories, false, version);
        table.setReportSpotTarget(SpotTypes.UNKNOWN);
        return table;
    }

    public static SquidReportTable createDefaultSquidReportTableRMSquidFilter(TaskInterface task, int version) {
        LinkedList<SquidReportCategoryInterface> reportCategories = createDefaultRMWMSortingCategoriesSquidFilter(task);
        SquidReportTable table = new SquidReportTable(RM_NAME_OF_WEIGHTEDMEAN_PLOT_SORT_REPORT, reportCategories, false, version);
        table.setReportSpotTarget(SpotTypes.REFERENCE_MATERIAL);
        return table;
    }

    public static LinkedList<SquidReportCategoryInterface> createDefaultReportCategoriesRefMat(TaskInterface task) {
        LinkedList<SquidReportCategoryInterface> reportCategoriesRefMat = new LinkedList<>();

        SquidReportCategoryInterface spotFundamentals = createDefaultSpotFundamentalsCategory();
        reportCategoriesRefMat.add(spotFundamentals);

        SquidReportCategoryInterface countsPerSecond = createDefaultCountsPerSecondCategory(task);
        reportCategoriesRefMat.add(countsPerSecond);

        SquidReportCategoryInterface rawNuclideRatios = createDefaultRawNuclideRatiosCategory(task);
        reportCategoriesRefMat.add(rawNuclideRatios);

        SquidReportCategoryInterface corrIndependentBuiltIn = createReportCategory("Correction-Independent Built-In");
        // TODO
//        reportCategoriesRefMat.add(corrIndependentBuiltIn);

        SquidReportCategoryInterface pb204Corr_RM = createReportCategory("204Pb Corrected");
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4COR206_238CALIB_CONST));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4COR206_238AGE_RM, "Ma"));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4COR208_232CALIB_CONST));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4COR208_232AGE_RM));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + TOTAL_206_238_RM));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + TOTAL_208_232_RM));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + COM206PB_PCT_RM));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + COM208PB_PCT_RM));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R208PB206PB_RM));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4COR207_206AGE_RM, "Ma"));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R207PB_206PB_RM));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R207PB_235U_RM));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R206PB_238U_RM));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + ERR_CORREL_RM));
        reportCategoriesRefMat.add(pb204Corr_RM);

        SquidReportCategoryInterface pb207Corr_RM = createReportCategory("207Pb Corrected");
        pb207Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7COR206_238CALIB_CONST));
        pb207Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7COR206_238AGE_RM, "Ma"));
        pb207Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7COR208_232CALIB_CONST));
        pb207Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7COR208_232AGE_RM, "Ma"));
        pb207Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + TOTAL_206_238_RM));
        pb207Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + TOTAL_208_232_RM));
        pb207Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + COM206PB_PCT_RM));
        pb207Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + COM208PB_PCT_RM));
        pb207Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + R208PB206PB_RM));
        pb207Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + R206PB_238U_RM));
        reportCategoriesRefMat.add(pb207Corr_RM);

        SquidReportCategoryInterface pb208Corr_RM = createReportCategory("208Pb Corrected");
        pb208Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8COR206_238CALIB_CONST));
        pb208Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8COR206_238AGE_RM, "Ma"));
        pb208Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + TOTAL_206_238_RM));
        pb208Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + COM206PB_PCT_RM));
        pb208Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8COR207_206AGE_RM, "Ma"));
        pb208Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + R207PB_206PB_RM));
        pb208Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + R207PB_235U_RM));
        pb208Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + R206PB_238U_RM));
        pb208Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + ERR_CORREL_RM));
        reportCategoriesRefMat.add(pb208Corr_RM);

        return reportCategoriesRefMat;
    }

    public static LinkedList<SquidReportCategoryInterface> createDefaultReportCategoriesUnknown(TaskInterface task) {
        LinkedList<SquidReportCategoryInterface> reportCategoriesUnknown = new LinkedList<>();

        SquidReportCategoryInterface spotFundamentals = createDefaultSpotFundamentalsCategory();
        reportCategoriesUnknown.add(spotFundamentals);

        SquidReportCategoryInterface countsPerSecond = createDefaultCountsPerSecondCategory(task);
        reportCategoriesUnknown.add(countsPerSecond);

        SquidReportCategoryInterface rawNuclideRatios = createDefaultRawNuclideRatiosCategory(task);
        reportCategoriesUnknown.add(rawNuclideRatios);

        SquidReportCategoryInterface corrIndependentData = createReportCategory("Correction-Independent Data");
        // TODO
//        reportCategoriesUnknownSquidFilter.add(corrIndependentData);

        SquidReportCategoryInterface pb204Corr = createReportCategory("204Pb Corrected");
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + COM206PB_PCT));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + COM208PB_PCT));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R208PB206PB));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + CONCEN_206PB));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + CONCEN_208PB));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4COR206_238AGE, "Ma"));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4COR207_206AGE, "Ma"));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4COR208_232AGE, "Ma"));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4COR_DISCORDANCE));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R208PB_232TH));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R238U_206PB));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R207PB_206PB));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R207PB_235U));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R206PB_238U));
        pb204Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + ERR_CORREL));
        reportCategoriesUnknown.add(pb204Corr);

        SquidReportCategoryInterface pb207Corr = createReportCategory("207Pb Corrected");
        pb207Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + R204PB_206PB));
        pb207Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + COM206PB_PCT));
        pb207Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + COM208PB_PCT));
        pb207Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + R208PB206PB));
        pb207Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + CONCEN_206PB));
        pb207Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + CONCEN_208PB));
        pb207Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7COR206_238AGE, "Ma"));
        pb207Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7COR208_232AGE, "Ma"));
        pb207Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + R206PB_238U));
        pb207Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB7CORR + R208PB_232TH));
        reportCategoriesUnknown.add(pb207Corr);

        SquidReportCategoryInterface pb208Corr = createReportCategory("208Pb Corrected");
        pb208Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + R204PB_206PB));
        pb208Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + COM206PB_PCT));
        pb208Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + CONCEN_206PB));
        pb208Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8COR206_238AGE, "Ma"));
        pb208Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8COR207_206AGE, "Ma"));
        pb208Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + R238U_206PB));
        pb208Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + R207PB_206PB));
        pb208Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + R207PB_235U));
        pb208Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + R206PB_238U));
        pb208Corr.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB8CORR + ERR_CORREL));
        reportCategoriesUnknown.add(pb208Corr);

        SquidReportCategoryInterface custom = createReportCategory("Custom Expressions");
        List<Expression> customExpressions = task.getCustomTaskExpressions();
        for (Expression exp : customExpressions) {
            ExpressionTreeInterface expTree = exp.getExpressionTree();
            if (expTree.amHealthy()
                    && (!expTree.isSquidSwitchSCSummaryCalculation())
                    && !(expTree instanceof ConstantNode)
                    && !(((ExpressionTreeBuilderInterface) expTree).getOperation() instanceof Value)
                    && (expTree.isSquidSwitchSAUnknownCalculation())) {
                custom.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(expTree.getName()));
            }
        }
        reportCategoriesUnknown.add(custom);

        SquidReportCategoryInterface metaData = createReportCategory("Meta Data");
        metaData.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn("CommonPbCorrMetaData"));
        metaData.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn("OverCtCorr"));
        reportCategoriesUnknown.add(metaData);

        return reportCategoriesUnknown;
    }

    private static LinkedList<SquidReportCategoryInterface> createDefaultSampleWMSortingCategoriesSquidFilter(TaskInterface task) {
        LinkedList<SquidReportCategoryInterface> reportCategoriesUnknownSquidFilter = new LinkedList<>();
        for (SquidReportCategory src : SquidReportCategory.defaultSampleWMSortingCategories) {
            reportCategoriesUnknownSquidFilter.add(src);
        }
        return reportCategoriesUnknownSquidFilter;
    }

    private static LinkedList<SquidReportCategoryInterface> createDefaultRMWMSortingCategoriesSquidFilter(TaskInterface task) {
        LinkedList<SquidReportCategoryInterface> reportCategoriesRMSquidFilter = new LinkedList<>();
        for (SquidReportCategory src : SquidReportCategory.defaultRefMatWMSortingCategories) {
            reportCategoriesRMSquidFilter.add(src);
        }
        return reportCategoriesRMSquidFilter;
    }

    private static SquidReportCategoryInterface createDefaultSpotFundamentalsCategory() {
        SquidReportCategoryInterface spotFundamentals = createReportCategory("Spot Fundamentals");
        spotFundamentals.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn("DateTimeMilliseconds"));
        spotFundamentals.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn("Hours", 5));
        spotFundamentals.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn("StageX", 5));
        spotFundamentals.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn("StageY", 5));
        spotFundamentals.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn("StageZ", 5));
        spotFundamentals.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn("Qt1Y", 5));
        spotFundamentals.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn("Qt1Z", 5));
        spotFundamentals.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn("PrimaryBeam", 5));

        return spotFundamentals;
    }

    private static SquidReportCategoryInterface createDefaultCountsPerSecondCategory(TaskInterface task) {
        SquidReportCategoryInterface countsPerSecond = createReportCategory("CPS");
        // special case of generation
        String[] isotopeLabels = new String[task.getSquidSpeciesModelList().size()];
        for (int i = 0; i < isotopeLabels.length; i++) {
            isotopeLabels[i] = task.getMapOfIndexToMassStationDetails().get(i).getIsotopeLabel();
            countsPerSecond.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(isotopeLabels[i]));
        }

        return countsPerSecond;
    }

    private static SquidReportCategoryInterface createDefaultRawNuclideRatiosCategory(TaskInterface task) {
        SquidReportCategoryInterface rawNuclideRatios = createReportCategory("Raw Nuclide Ratios");
        // special case of generation
        Iterator<SquidRatiosModel> squidRatiosIterator = ((ShrimpFraction) task.getUnknownSpots().get(0)).getIsotopicRatiosII().iterator();
        while (squidRatiosIterator.hasNext()) {
            SquidRatiosModel entry = squidRatiosIterator.next();
            if (entry.isActive()) {
                String displayNameNoSpaces = entry.getDisplayNameNoSpaces().substring(0, StrictMath.min(20, entry.getDisplayNameNoSpaces().length()));
                rawNuclideRatios.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(displayNameNoSpaces));
            }
        }

        return rawNuclideRatios;
    }

    @Override
    public SquidReportTableInterface copy() {
        LinkedList<SquidReportCategoryInterface> cats = new LinkedList<>();
        reportCategories.forEach(cat -> cats.add(cat.clone()));
        SquidReportTableInterface table = new SquidReportTable(reportTableName, cats, isBuiltInSquidDefault, version);
        table.setIsLabDataDefault(isLabDataDefault);
        table.setReportSpotTarget(reportSpotTarget);
        return table;
    }

    @Override
    public int compareTo(SquidReportTableInterface srt)
            throws ClassCastException {
        String tableName = srt.getReportTableName();
        return this.getReportTableName().trim().
                compareToIgnoreCase(tableName.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SquidReportTableInterface)) {
            return false;
        }
        SquidReportTableInterface that = (SquidReportTable) o;
        return reportTableName.compareTo(that.getReportTableName()) == 0;
    }

    @Override
    public int hashCode() {
        return reportTableName.hashCode();
    }

    @Override
    public boolean amWeightedMeanPlotAndSortReport() {
        return (reportTableName.compareTo(NAME_OF_WEIGHTEDMEAN_PLOT_SORT_REPORT) == 0)
                ||
                (reportTableName.compareTo(RM_NAME_OF_WEIGHTEDMEAN_PLOT_SORT_REPORT) == 0);
    }

    @Override
    public void formatWeightedMeanPlotAndSortReport() {
        // force Time and Ages categories to top of categories list
        SquidReportCategoryInterface timeCat = null;
        SquidReportCategoryInterface agesCat = null;
        for (SquidReportCategoryInterface cat : reportCategories) {
            if (cat.getDisplayName().compareToIgnoreCase("Time") == 0) {
                timeCat = cat;
            }
            if (cat.getDisplayName().compareToIgnoreCase("Ages") == 0) {
                agesCat = cat;
            }
        }

        if ((timeCat != null) && (agesCat != null)) {
            reportCategories.remove(timeCat);
            reportCategories.remove(agesCat);

            reportCategories.add(0, agesCat);
            reportCategories.add(0, timeCat);
        }
    }

    /**
     * @return the reportTableName
     */
    @Override
    public String getReportTableName() {
        return reportTableName;
    }

    /**
     * @param reportTableName the reportTableName to set
     */
    @Override
    public void setReportTableName(String reportTableName) {
        this.reportTableName = reportTableName;
    }

    /**
     * @return the reportCategories
     */
    @Override
    public LinkedList<SquidReportCategoryInterface> getReportCategories() {
        return reportCategories;
    }

    /**
     * @param reportCategories the reportCategories to set
     */
    @Override
    public void setReportCategories(LinkedList<SquidReportCategoryInterface> reportCategories) {
        this.reportCategories = reportCategories;
    }

    /**
     * @return the reportSpotTarget
     */
    @Override
    public SpotTypes getReportSpotTarget() {
        return this.reportSpotTarget;
    }

    /**
     * @param reportSpotTarget the reportSpotTarget to set
     */
    @Override
    public void setReportSpotTarget(SpotTypes reportSpotTarget) {
        this.reportSpotTarget = reportSpotTarget;
    }

    @Override
    public void setIsBuiltInSquidDefault(boolean isBuiltInSquidDefault) {
        this.isBuiltInSquidDefault = isBuiltInSquidDefault;
    }

    @Override
    public boolean isDefault() {
        return isBuiltInSquidDefault;
    }

    /**
     * @return the isLabDataDefault
     */
    @Override
    public boolean isIsLabDataDefault() {
        return isLabDataDefault;
    }

    /**
     * @param isLabDataDefault the isLabDataDefault to set
     */
    @Override
    public void setIsLabDataDefault(boolean isLabDataDefault) {
        this.isLabDataDefault = isLabDataDefault;
    }

    @Override
    public String customizeXML(String xml) {
        String xmlR = xml;
        xmlR = xmlR.replaceFirst("<SquidReportTable>",
                XML_HEADER_FOR_SQUIDREPORTTABLE_FILES_USING_LOCAL_SCHEMA);
        return xmlR;
    }

    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new SquidReportTableXMLConverter());
        xstream.alias("SquidReportTable", SquidReportTable.class);

        xstream.registerConverter(new SquidReportCategoryXMLConverter());
        xstream.alias("SquidReportCategory", SquidReportCategory.class);

        xstream.registerConverter(new SquidReportColumnXMLConverter());
        xstream.alias("SquidReportColumn", SquidReportColumn.class);
    }

    @Override
    public SquidReportTable clone() {
        SquidReportTable table = createEmptySquidReportTable(reportTableName);

        LinkedList<SquidReportCategoryInterface> cats = new LinkedList<>();
        for (SquidReportCategoryInterface cat : reportCategories) {
            cats.add(cat.clone());
        }
        table.setReportCategories(cats);
        table.setIsBuiltInSquidDefault(isBuiltInSquidDefault);
        table.setIsLabDataDefault(isLabDataDefault);

        return table;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }
}