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

import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Divide;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import static org.cirdles.squid.constants.Squid3Constants.ABS_UNCERTAINTY_DIRECTIVE;
import static org.cirdles.squid.constants.Squid3Constants.PCT_UNCERTAINTY_DIRECTIVE;
import static org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface.formatBigDecimalForPublicationSigDigMode;
import static org.cirdles.squid.squidReports.squidReportTables.SquidReportTable.DEFAULT_COUNT_OF_SIGNIFICANT_DIGITS;
import static org.cirdles.squid.squidReports.squidReportTables.SquidReportTable.HEADER_ROW_COUNT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R204PB_206PB;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SquidReportColumn implements Serializable, SquidReportColumnInterface {

    private static final long serialVersionUID = -4256285353332428810L;

    // source of spot-specific data for this column
    private transient ExpressionTreeInterface expTree;
    private transient boolean amIsotopicRatio;
    // provides for multi-row column headers
    private transient String[] columnHeaders;

    private String expressionName;
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
        this("", "", 0, false, "");
    }

    private SquidReportColumn(String expressionName) {
        this(expressionName, "", 0, false, "");
    }

    private SquidReportColumn(
            String expressionName,
            String units,
            int countOfSignificantDigits,
            boolean visible,
            String footnoteSpec) {

        this.expTree = null;
        this.expressionName = expressionName;
        this.units = units;

        this.uncertaintyColumn = null;
        this.amUncertaintyColumn = false;
        this.uncertaintyDirective = "";

        this.countOfSignificantDigits = countOfSignificantDigits;
        this.visible = visible;
        this.footnoteSpec = footnoteSpec;
    }

    public static SquidReportColumn createSquidReportColumn(String expressionName) {
        return new SquidReportColumn(expressionName);
    }

    public static SquidReportColumn createSquidReportColumn(String expressionName, String units) {
        SquidReportColumn squidReportColumn = new SquidReportColumn(expressionName);
        squidReportColumn.setUnits(units);
        return squidReportColumn;
    }

    public static SquidReportColumn createSquidReportColumn(String expressionName, int countOfSignificantDigits) {
        SquidReportColumn squidReportColumn = new SquidReportColumn(expressionName);
        squidReportColumn.setCountOfSignificantDigits(countOfSignificantDigits);
        return squidReportColumn;
    }

    @Override
    public void initReportColumn(TaskInterface task) {
        // extract properties of expTree
        expTree = task.findNamedExpression(expressionName);
        if (expTree instanceof SpotFieldNode) {
            ((Task) task).evaluateTaskExpression(expTree);
        } else if (expTree instanceof ShrimpSpeciesNode) {
            // default view of species nodes
            ((ShrimpSpeciesNode) expTree).setMethodNameForShrimpFraction("getTotalCps");
            ((Task) task).evaluateTaskExpression(expTree);
            expressionName = "total_" + expressionName + "_cts_/sec";
        }

        amIsotopicRatio = false;
        if (((ExpressionTree) expTree).getLeftET() instanceof ShrimpSpeciesNode) {
            // Check for isotopic ratios
            amIsotopicRatio = (((ExpressionTree) expTree).getOperation() instanceof Divide);
        }

        // propose column headers by splitting on underscores in name
        // row 0 is reserved for category displayname
        // row 1 is reserved for column displayName1
        // row 2 is reserved for column displayName2
        // row 3 is reserved for column displayName3
        // row 4 is reserved for column displayName4
        // row 5 is reserved for units spec
        // row 6 is reserved for column footnotes as reference letters
        // row 7 is reserved for storage of actual footnotes in the correct order
        // fraction data starts at col 2, row FRACTION_DATA_START_ROW
        //
        // filename is stored in 0,1
        columnHeaders = new String[HEADER_ROW_COUNT];
        for (int i = 0; i < HEADER_ROW_COUNT; i++) {
            columnHeaders[i] = "";
        }
        String[] headers = expressionName.split("_");
        int headerRowCount = headers.length;
        int headerRow = 4;
        for (int i = headerRowCount; i > 0; i--) {
            columnHeaders[headerRow] = headers[i - 1];
            headerRow--;
        }
        //columnHeaders[HEADER_ROW_COUNT - 1] = expTree.getName();

        columnHeaders[5] = units;
        if (columnHeaders[5].length() > 0) {
            columnHeaders[4] += "(" + units + ")";
        }

        // propose uncertainty column and type if detected in expTree
        // if expTree has uncertaintyDirective, then this column is itself an uncertainty column as defined
        // by the expression; otherwise the uncertainty column can have a directive for how it should be rendered
        uncertaintyDirective = ((ExpressionTree) expTree).getUncertaintyDirective();
        if (((ExpressionTree) expTree).getOperation() != null) {
            if ((((ExpressionTree) expTree).getOperation().getName().compareToIgnoreCase("Value") == 0)) {
                if (((ExpressionTree) expTree).getChildrenET().get(0) instanceof VariableNodeForSummary) {
                    uncertaintyDirective
                            = ((VariableNodeForSummary) ((ExpressionTree) expTree).getChildrenET().get(0)).getUncertaintyDirective();
                }
            }
        }

        uncertaintyColumn = null;
        if ((uncertaintyDirective.length() == 0)
                && (!expressionName.toUpperCase().contains("PCT"))
                && (!expressionName.toUpperCase().contains("ERR"))
                && (!expressionName.toUpperCase().contains("CONCEN"))
                && (!expressionName.toUpperCase().contains("DISC"))
                && (!expressionName.toUpperCase().contains("PPM"))
                && (!expressionName.toUpperCase().contains("CORR"))
                && (!expressionName.contains(R204PB_206PB))
                && !(expTree instanceof SpotFieldNode)
                && !(expTree instanceof ShrimpSpeciesNode)) {
            uncertaintyColumn = createSquidReportColumn(expressionName, units);
            uncertaintyColumn.setExpTree(expTree);

            String[] unctColumnHeaders = new String[HEADER_ROW_COUNT];
            for (int i = 0; i < HEADER_ROW_COUNT; i++) {
                unctColumnHeaders[i] = "";
            }

            if (expressionName.toUpperCase().contains("AGE")) {
                uncertaintyColumn.setUncertaintyDirective(ABS_UNCERTAINTY_DIRECTIVE);
                unctColumnHeaders[4] = "±1σ abs";
            } else {
                uncertaintyColumn.setUncertaintyDirective(PCT_UNCERTAINTY_DIRECTIVE);
                unctColumnHeaders[4] = "±1σ %";
            }

            unctColumnHeaders[5] = units;
            uncertaintyColumn.setColumnHeaders(unctColumnHeaders);

            uncertaintyColumn.setAmUncertaintyColumn(true);

            uncertaintyColumn.setVisible(true);
            uncertaintyColumn.setCountOfSignificantDigits(DEFAULT_COUNT_OF_SIGNIFICANT_DIGITS);
            uncertaintyColumn.setAmIsotopicRatio(amIsotopicRatio);
        }

        amUncertaintyColumn = false;

        countOfSignificantDigits = (countOfSignificantDigits == 0) ? DEFAULT_COUNT_OF_SIGNIFICANT_DIGITS : countOfSignificantDigits;

        boolean visible = true;

        // propose footnote from notes field of expTree
        footnoteSpec = "";
    }

    @Override
    public boolean hasVisibleUncertaintyColumn() {
        return (uncertaintyColumn != null && uncertaintyColumn.isVisible());
    }

    @Override
    public String cellEntryForSpot(ShrimpFractionExpressionInterface spot) {
        String retVal = "not init";
        if (expTree != null) {
            double[][] results = new double[][]{{0, 0}, {0, 0}};
            // TODO: check uncertainty directive

            boolean success = true;
            if (amIsotopicRatio) {
                try {
                    results = Arrays.stream(spot.getIsotopicRatioValuesByStringName(expTree.getName())).toArray(double[][]::new);
                } catch (Exception e) {
                    success = false;
                }
            } else {
                try {
                    results = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);
                } catch (Exception e) {
                    success = false;
                }
            }

            if (success) {
                if (amUncertaintyColumn) {
                    double uncertainty;
                    try {
                        if (uncertaintyDirective.compareToIgnoreCase(PCT_UNCERTAINTY_DIRECTIVE) == 0) {
                            uncertainty = results[0][1] / results[0][0] * 100.0;
                            retVal = formatBigDecimalForPublicationSigDigMode(
                                    new BigDecimal(uncertainty),
                                    countOfSignificantDigits);
                        } else {
                            uncertainty = results[0][1];
                            retVal = formatBigDecimalForPublicationSigDigMode(
                                    new BigDecimal(uncertainty).movePointRight(Squid3Constants.getUnitConversionMoveCount(units)),
                                    countOfSignificantDigits);
                        }
                    } catch (Exception e) {
                        // no uncertainty - let user know they should hide
                        retVal = String.format("%1$-23s", "n/a");
                    }
                } else {
                    // first handle special cases
                    if (expressionName.toUpperCase().contains("DATETIMEMILLISECONDS")) {
                        retVal = spot.getDateTime();
                    } else {
                        if (Double.isFinite(results[0][0])){
                        retVal = formatBigDecimalForPublicationSigDigMode(
                                new BigDecimal(results[0][0]).movePointRight(Squid3Constants.getUnitConversionMoveCount(units)),
                                countOfSignificantDigits);
                        } else {
                            retVal = "NaN";
                        }
                    }
                }
            } else {
                retVal = "not found";
            }
        }
        return retVal;
    }

    /**
     * @param expTree the expTree to set
     */
    public void setExpTree(ExpressionTreeInterface expTree) {
        this.expTree = expTree;
    }

    /**
     * @return the expressionName
     */
    public String getExpressionName() {
        return expressionName;
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
     * @return the countOfSignificantDigits
     */
    @Override
    public int getCountOfSignificantDigits() {
        return countOfSignificantDigits;
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

    /**
     * @param amIsotopicRatio the amIsotopicRatio to set
     */
    public void setAmIsotopicRatio(boolean amIsotopicRatio) {
        this.amIsotopicRatio = amIsotopicRatio;
    }

    /**
     * @param uncertaintyColumn the uncertaintyColumn to set
     */
    @Override
    public void setUncertaintyColumn(SquidReportColumnInterface uncertaintyColumn) {
        this.uncertaintyColumn = uncertaintyColumn;
    }

    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new SquidReportColumnXMLConverter());
        xstream.alias("SquidReportColumn", SquidReportColumn.class);
    }

    public SquidReportColumn clone() {
        SquidReportColumnInterface col = createSquidReportColumn(expressionName);
        col.setUnits(units);
        col.setUncertaintyColumn(uncertaintyColumn);
        col.setAmUncertaintyColumn(amUncertaintyColumn);
        col.setUncertaintyDirective(uncertaintyDirective);
        col.setCountOfSignificantDigits(countOfSignificantDigits);
        col.setVisible(visible);
        col.setFootnoteSpec(footnoteSpec);
        return (SquidReportColumn) col;
    }

    @Override
    public String toString() {
        return expressionName;
    }


    @Override
    public int hashCode() {
        int result = Objects.hash(expTree, amIsotopicRatio, getExpressionName(), getUnits(), getUncertaintyColumn(), isAmUncertaintyColumn(), getUncertaintyDirective(), getCountOfSignificantDigits(), isVisible(), getFootnoteSpec());
        result = 31 * result + Arrays.hashCode(getColumnHeaders());
        return result;

    }
}
