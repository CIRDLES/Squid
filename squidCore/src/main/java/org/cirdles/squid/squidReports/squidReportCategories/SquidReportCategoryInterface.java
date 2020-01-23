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

import static java.nio.file.Paths.get;
import java.util.LinkedList;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public interface SquidReportCategoryInterface extends XMLSerializerInterface {

    /**
     * @return the displayName
     */
    public String getDisplayName();

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName);

    /**
     * @return the categoryColumns
     */
    public LinkedList<SquidReportColumnInterface> getCategoryColumns();

    /**
     * @param categoryColumns the categoryColumns to set
     */
    public void setCategoryColumns(LinkedList<SquidReportColumnInterface> categoryColumns);

    /**
     * @return the visible
     */
    public boolean isVisible();

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible);

    public SquidReportCategory clone();

    public default SquidReportColumnInterface findColumnByName(String columnName) {
        SquidReportColumnInterface retVal = null;
        for (SquidReportColumnInterface src : getCategoryColumns()) {
            if (src.getExpressionName().compareToIgnoreCase(columnName) == 0) {
                retVal = src;
                break;
            }
        }

        return retVal;
    }
}
