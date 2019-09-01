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

import java.io.Serializable;
import java.util.LinkedList;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SquidReportCategory implements Serializable, SquidReportCategoryInterface {
    // private static final long serialVersionUID = 5227409808812622714L;

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

}
