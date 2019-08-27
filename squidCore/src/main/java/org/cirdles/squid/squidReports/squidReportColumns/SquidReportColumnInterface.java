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

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public interface SquidReportColumnInterface {

    String cellEntryForSpot(ShrimpFractionExpressionInterface spot);

    /**
     * @return the columnHeaders
     */
    String[] getColumnHeaders();

    /**
     * @return the footnoteSpec
     */
    String getFootnoteSpec();

    /**
     * @return the uncertaintyColumn
     */
    SquidReportColumnInterface getUncertaintyColumn();

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
    String getUnits();

    /**
     * @return the visible
     */
    boolean isVisible();

    /**
     * @param columnHeaders the columnHeaders to set
     */
    void setColumnHeaders(String[] columnHeaders);

    /**
     * @param countOfSignificantDigits the countOfSignificantDigits to set
     */
    void setCountOfSignificantDigits(int countOfSignificantDigits);

    /**
     * @param footnoteSpec the footnoteSpec to set
     */
    void setFootnoteSpec(String footnoteSpec);

    /**
     * @param units the units to set
     */
    void setUnits(String units);

    /**
     * @param visible the visible to set
     */
    void setVisible(boolean visible);

}
