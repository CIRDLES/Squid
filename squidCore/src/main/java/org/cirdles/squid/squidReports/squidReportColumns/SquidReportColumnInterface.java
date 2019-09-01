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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public interface SquidReportColumnInterface {

    public void initReportColumn(TaskInterface task);

    public String cellEntryForSpot(ShrimpFractionExpressionInterface spot);

    /**
     * @param expTree the expTree to set
     */
    public void setExpTree(ExpressionTreeInterface expTree);

    /**
     * @return the expressionName
     */
    public String getExpressionName();

    /**
     * @return the columnHeaders
     */
    public String[] getColumnHeaders();

    /**
     * @return the footnoteSpec
     */
    public String getFootnoteSpec();

    public boolean hasVisibleUncertaintyColumn();

    /**
     * @return the uncertaintyColumn
     */
    public SquidReportColumnInterface getUncertaintyColumn();

    /**
     * @return the uncertaintyDirective
     */
    public String getUncertaintyDirective();

    /**
     * @param uncertaintyDirective the uncertaintyDirective to set
     */
    public void setUncertaintyDirective(String uncertaintyDirective);

    /**
     * @return the amUncertaintyColumn
     */
    public boolean isAmUncertaintyColumn();

    /**
     * @param amUncertaintyColumn the amUncertaintyColumn to set
     */
    public void setAmUncertaintyColumn(boolean amUncertaintyColumn);

    /**
     * @return the units
     */
    public String getUnits();

    /**
     * @return the visible
     */
    public boolean isVisible();

    /**
     * @param columnHeaders the columnHeaders to set
     */
    public void setColumnHeaders(String[] columnHeaders);

    /**
     * @param countOfSignificantDigits the countOfSignificantDigits to set
     */
    public void setCountOfSignificantDigits(int countOfSignificantDigits);

    /**
     * @param footnoteSpec the footnoteSpec to set
     */
    public void setFootnoteSpec(String footnoteSpec);

    /**
     * @param units the units to set
     */
    public void setUnits(String units);

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible);

    /**
     * @param amIsotopicRatio the amIsotopicRatio to set
     */
    public void setAmIsotopicRatio(boolean amIsotopicRatio);

    static String formatBigDecimalForPublicationSigDigMode(
            BigDecimal number,
            int uncertaintySigDigits) {

        if ((number.compareTo(BigDecimal.ZERO) == 0)//
                || // jan 2011 to trap for absurdly small uncertainties
                (number.abs().doubleValue() < Math.pow(10, -1 * 15))) {
            return "0";
        } else {
            return number.round(new MathContext(//
                    uncertaintySigDigits, RoundingMode.HALF_UP)).toPlainString();
        }
    }

}
