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
package org.cirdles.squid.squidReports.squidReportColumns;

import java.io.Serializable;
import java.util.Arrays;
import static org.cirdles.squid.constants.Squid3Constants.ABS_UNCERTAINTY_DIRECTIVE;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import static org.cirdles.squid.squidReports.squidReportTables.SquidReportTable.DEFAULT_COUNT_OF_SIGNIFICANT_DIGITS;
import static org.cirdles.squid.squidReports.squidReportTables.SquidReportTable.HEADER_ROW_COUNT;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Divide;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;
import static org.cirdles.squid.utilities.conversionUtilities.RoundingUtilities.squid3RoundedToSize;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SquidReportColumn implements Serializable, SquidReportColumnInterface {
    //private static final long serialVersionUID = 1474850196549001090L;

    // source of spot-specific data for this column
    private ExpressionTreeInterface expTree;
    private boolean amIsotopicRatio;
    // provides for multi-row column headers - 6 rows
    private String[] columnHeaders;

    // used to calculate shiftPointRightCount = Squid3Constants.getUnitConversionMoveCount(units)
    private String units;

    // optional uncertainty column
    private SquidReportColumnInterface uncertaintyColumn;
    private boolean amUncertaintyColumn;
    // 1 sigma abs or pct if uncertainty column
    private String uncertaintyDirective;

    private int countOfSignificantDigits;
    private boolean visible;
    private String footnoteSpec;

    private SquidReportColumn() {
        this(null, false, new String[HEADER_ROW_COUNT], "", null, false, "", 0, false, "");
    }

    private SquidReportColumn(ExpressionTreeInterface expTree, boolean amIsotopicRatio) {
        this(expTree, amIsotopicRatio, new String[HEADER_ROW_COUNT], "", null, false, "", 0, false, "");
    }

    private SquidReportColumn(
            ExpressionTreeInterface expTree,
            boolean amIsotopicRatio,
            String[] columnHeaders,
            String units,
            SquidReportColumnInterface uncertaintyColumn,
            boolean amUncertaintyColumn,
            String uncertaintyDirective,
            int countOfSignificantDigits,
            boolean visible,
            String footnoteSpec) {

        this.expTree = expTree;
        this.amIsotopicRatio = amIsotopicRatio;
        this.columnHeaders = columnHeaders;
        this.units = units;
        this.uncertaintyColumn = uncertaintyColumn;
        this.amUncertaintyColumn = amUncertaintyColumn;
        this.uncertaintyDirective = uncertaintyDirective;
        this.countOfSignificantDigits = countOfSignificantDigits;
        this.visible = visible;
        this.footnoteSpec = footnoteSpec;
    }

    public static SquidReportColumn createDefaultReportColumn(){
        // TODO decide best default
        return new SquidReportColumn();
    }
    
    public static SquidReportColumn createReportColumn(ExpressionTreeInterface expTree) {
        // extract properties of expTree
        boolean amIsotopicRatio = false;
        if (((ExpressionTree) expTree).getLeftET() instanceof ShrimpSpeciesNode) {
            // Check for isotopic ratios
            amIsotopicRatio = (((ExpressionTree) expTree).getOperation() instanceof Divide);
        }

        // propose column headers
        String[] columnHeaders = new String[HEADER_ROW_COUNT];
        columnHeaders[HEADER_ROW_COUNT - 1] = expTree.getName();

        // propose units
        String units = "";

        // propose uncertainty column and type if detected in expTree
        // if expTree has uncertaintyDirective, then this column is itself an uncertainty column as defined
        // by the expression; otherwise the uncertainty column can have a directive for how it should be rendered
        String uncertaintyDirective = ((ExpressionTree) expTree).getUncertaintyDirective();
        if (((ExpressionTree) expTree).getOperation() != null) {
            if ((((ExpressionTree) expTree).getOperation().getName().compareToIgnoreCase("Value") == 0)) {
                if (((ExpressionTree) expTree).getChildrenET().get(0) instanceof VariableNodeForSummary) {
                    uncertaintyDirective
                            = ((VariableNodeForSummary) ((ExpressionTree) expTree).getChildrenET().get(0)).getUncertaintyDirective();
                }
            }
        }

        SquidReportColumnInterface uncertaintyColumn = null;
        if (uncertaintyDirective.length() == 0) {
            uncertaintyColumn = new SquidReportColumn(expTree, amIsotopicRatio);
            String[] unctColumnHeaders = new String[HEADER_ROW_COUNT];
            unctColumnHeaders[HEADER_ROW_COUNT - 1] = "unct";
            uncertaintyColumn.setColumnHeaders(unctColumnHeaders);

            uncertaintyColumn.setAmUncertaintyColumn(true);
            uncertaintyColumn.setVisible(true);
            uncertaintyColumn.setCountOfSignificantDigits(DEFAULT_COUNT_OF_SIGNIFICANT_DIGITS);
            uncertaintyColumn.setUncertaintyDirective(ABS_UNCERTAINTY_DIRECTIVE);
        }

        boolean amUncertaintyColumn = false;

        int countOfSignificantDigits = DEFAULT_COUNT_OF_SIGNIFICANT_DIGITS;

        boolean visible = true;

        // propose footnote from notes field of expTree
        String footnoteSpec = "";

        return new SquidReportColumn(
                expTree,
                amIsotopicRatio,
                columnHeaders,
                units,
                uncertaintyColumn,
                amUncertaintyColumn,
                uncertaintyDirective,
                countOfSignificantDigits,
                visible,
                footnoteSpec);
    }

    @Override
    public String cellEntryForSpot(ShrimpFractionExpressionInterface spot) {
        double[][] results;
        // TODO: check uncertainty directive

        if (amIsotopicRatio) {
            results = Arrays.stream(spot.getIsotopicRatioValuesByStringName(expTree.getName())).toArray(double[][]::new);
        } else {
            results = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);
        }

        String retVal;
        if (amUncertaintyColumn) {
            retVal = String.format("%1$-23s", squid3RoundedToSize(results[0][1], countOfSignificantDigits));
        } else {
            retVal = String.format("%1$-23s", squid3RoundedToSize(results[0][0], countOfSignificantDigits));
        }

        return retVal;
    }

    /**
     * @return the columnHeaders
     */
    @Override
    public String[] getColumnHeaders() {
        return columnHeaders;
    }

    /**
     * @param columnHeaders the columnHeaders to set
     */
    @Override
    public void setColumnHeaders(String[] columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    /**
     * @return the units
     */
    @Override
    public String getUnits() {
        return units;
    }

    /**
     * @param units the units to set
     */
    @Override
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * @return the uncertaintyColumn
     */
    @Override
    public SquidReportColumnInterface getUncertaintyColumn() {
        return uncertaintyColumn;
    }

    /**
     * @return the uncertaintyDirective
     */
    @Override
    public String getUncertaintyDirective() {
        return uncertaintyDirective;
    }

    /**
     * @param uncertaintyDirective the uncertaintyDirective to set
     */
    @Override
    public void setUncertaintyDirective(String uncertaintyDirective) {
        this.uncertaintyDirective = uncertaintyDirective;
    }

    /**
     * @return the amUncertaintyColumn
     */
    @Override
    public boolean isAmUncertaintyColumn() {
        return amUncertaintyColumn;
    }

    /**
     * @param amUncertaintyColumn the amUncertaintyColumn to set
     */
    @Override
    public void setAmUncertaintyColumn(boolean amUncertaintyColumn) {
        this.amUncertaintyColumn = amUncertaintyColumn;
    }

    /**
     * @param countOfSignificantDigits the countOfSignificantDigits to set
     */
    @Override
    public void setCountOfSignificantDigits(int countOfSignificantDigits) {
        this.countOfSignificantDigits = countOfSignificantDigits;
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

    /**
     * @return the footnoteSpec
     */
    @Override
    public String getFootnoteSpec() {
        return footnoteSpec;
    }

    /**
     * @param footnoteSpec the footnoteSpec to set
     */
    @Override
    public void setFootnoteSpec(String footnoteSpec) {
        this.footnoteSpec = footnoteSpec;
    }

}
