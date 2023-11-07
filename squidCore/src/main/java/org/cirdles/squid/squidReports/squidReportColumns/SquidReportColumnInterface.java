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

import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */

public interface SquidReportColumnInterface extends XMLSerializerInterface {

    static String formatBigDecimalForPublicationSigDigMode(
            BigDecimal number,
            int uncertaintySigDigits) {

        if ((number.compareTo(BigDecimal.ZERO) == 0)//
                || // jan 2011 to trap for absurdly small uncertainties
                (number.abs().doubleValue() < StrictMath.pow(10, -1 * 15))) {
            return "0";
        } else {
            return number.round(new MathContext(//
                    uncertaintySigDigits, RoundingMode.HALF_UP)).toPlainString();
        }
    }

    void initReportColumn(TaskInterface task);

    String cellEntryForSpot(ShrimpFractionExpressionInterface spot);

    /**
     * @param expTree the expTree to set
     */
    void setExpTree(ExpressionTreeInterface expTree);

    /**
     * @return the expressionName
     */
    String getExpressionName();

    /**
     * @return the columnHeaders
     */
    String[] getColumnHeaders();

    /**
     * @param columnHeaders the columnHeaders to set
     */
    void setColumnHeaders(String[] columnHeaders);

    /**
     * @return the footnoteSpec
     */
    String getFootnoteSpec();

    /**
     * @param footnoteSpec the footnoteSpec to set
     */
    void setFootnoteSpec(String footnoteSpec);

    boolean hasVisibleUncertaintyColumn();

    /**
     * @return the uncertaintyColumn
     */
    SquidReportColumnInterface getUncertaintyColumn();

    /**
     * @param uncertaintyColumn the uncertaintyColumn to set
     */
    void setUncertaintyColumn(SquidReportColumnInterface uncertaintyColumn);

    /**
     * @return the uncertaintyDirective
     */
    String getUncertaintyDirective();

    /**
     * @param uncertaintyDirective the uncertaintyDirective to set
     */
    void setUncertaintyDirective(String uncertaintyDirective);

    /**
     * @return the amUncertaintyColumn
     */
    boolean isAmUncertaintyColumn();

    /**
     * @param amUncertaintyColumn the amUncertaintyColumn to set
     */
    void setAmUncertaintyColumn(boolean amUncertaintyColumn);

    /**
     * @return the units
     */
    String getUnits();

    /**
     * @param units the units to set
     */
    void setUnits(String units);

    /**
     * @return the visible
     */
    boolean isVisible();

    /**
     * @param visible the visible to set
     */
    void setVisible(boolean visible);

    /**
     * @param amIsotopicRatio the amIsotopicRatio to set
     */
    void setAmIsotopicRatio(boolean amIsotopicRatio);

    /**
     * @return the countOfSignificantDigits
     */
    int getCountOfSignificantDigits();

    /**
     * @param countOfSignificantDigits the countOfSignificantDigits to set
     */
    void setCountOfSignificantDigits(int countOfSignificantDigits);

    SquidReportColumn clone();

}