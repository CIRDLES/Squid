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

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import static org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory.createReportCategory;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM206PB_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM206PB_PCT_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM208PB_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM208PB_PCT_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CONCEN_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CONCEN_208PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ERR_CORREL;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ERR_CORREL_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR207_206AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR207_206AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR_DISCORDANCE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR207_206AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR207_206AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R204PB_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_206PB_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_235U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_235U_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208PB206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208PB206PB_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208PB_232TH;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R238U_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_206_238_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_208_232_RM;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.operations.Value;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SquidReportTable implements Serializable, SquidReportTableInterface {
    // private static final long serialVersionUID = 5227409808812622714L;

    public final static int HEADER_ROW_COUNT = 7;
    public final static int DEFAULT_COUNT_OF_SIGNIFICANT_DIGITS = 15;

    // Fields
    private String reportTableName;
    private LinkedList<SquidReportCategoryInterface> reportCategories;

    private SquidReportTable() {
    }

    private SquidReportTable(String reportTableName, LinkedList<SquidReportCategoryInterface> reportCategories) {
        this.reportTableName = reportTableName;
        this.reportCategories = reportCategories;
    }

    public static SquidReportTable createEmptySquidReportTable(String reportTableName) {
        return new SquidReportTable(reportTableName, new LinkedList<>());
    }

    public static SquidReportTable createDefaultSquidReportTableRefMat(TaskInterface task) {
        String reportTableName = "Default Squid3 Report Table for Reference Materials";
        LinkedList<SquidReportCategoryInterface> reportCategories = createDefaultReportCategoriesRefMat(task);

        return new SquidReportTable(reportTableName, reportCategories);
    }

    public static SquidReportTable createDefaultSquidReportTableUnknown(TaskInterface task) {
        String reportTableName = "Default Squid3 Report Table for Unknowns";
        LinkedList<SquidReportCategoryInterface> reportCategories = createDefaultReportCategoriesUnknown(task);

        return new SquidReportTable(reportTableName, reportCategories);
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
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + COM206PB_PCT));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + COM208PB_PCT));
        pb204Corr_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(PB4CORR + R208PB206PB));
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

//        SquidReportCategoryInterface custom_RM = createReportCategory("Custom Expressions Ref Mat");
//        List<Expression> customExpressionsRM = task.getCustomTaskExpressions();
//        for (Expression exp : customExpressionsRM) {
//            ExpressionTreeInterface expTree = exp.getExpressionTree();
//            if ((!expTree.isSquidSwitchSCSummaryCalculation())
//                    && (expTree.isSquidSwitchSTReferenceMaterialCalculation())) {
//                custom_RM.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(expTree.getName()));
//            }    
//        }
//        reportCategoriesRefMat.add(custom_RM);
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
//        reportCategoriesUnknown.add(corrIndependentData);

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
            if ((!expTree.isSquidSwitchSCSummaryCalculation())
                    && !(expTree instanceof ConstantNode)
                    && !(((ExpressionTreeBuilderInterface) expTree).getOperation() instanceof Value)
                    && (expTree.isSquidSwitchSAUnknownCalculation())) {
                custom.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(expTree.getName()));
            }
        }
        reportCategoriesUnknown.add(custom);

        return reportCategoriesUnknown;
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
                String displayNameNoSpaces = entry.getDisplayNameNoSpaces().substring(0, Math.min(20, entry.getDisplayNameNoSpaces().length()));
                rawNuclideRatios.getCategoryColumns().add(SquidReportColumn.createSquidReportColumn(displayNameNoSpaces));
            }
        }

        return rawNuclideRatios;
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

}
