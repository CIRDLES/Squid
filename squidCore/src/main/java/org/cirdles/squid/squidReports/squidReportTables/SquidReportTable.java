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
import java.util.LinkedList;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SquidReportTable implements Serializable, SquidReportTableInterface {
    // private static final long serialVersionUID = 5227409808812622714L;

    public final static int HEADER_ROW_COUNT = 6;
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

    public static SquidReportTable createDefaultSquidReportTable() {
        String reportTableName = "Default Squid3 Report Table";
        LinkedList<SquidReportCategoryInterface> reportCategories = new LinkedList<>();

        reportCategories.add(SquidReportCategory.createDefaultReportCategory());

        return new SquidReportTable(reportTableName, reportCategories);
    }

    /**
     * @return the reportTableName
     */
    public String getReportTableName() {
        return reportTableName;
    }

    /**
     * @param reportTableName the reportTableName to set
     */
    public void setReportTableName(String reportTableName) {
        this.reportTableName = reportTableName;
    }

    /**
     * @return the reportCategories
     */
    public LinkedList<SquidReportCategoryInterface> getReportCategories() {
        return reportCategories;
    }

    /**
     * @param reportCategories the reportCategories to set
     */
    public void setReportCategories(LinkedList<SquidReportCategoryInterface> reportCategories) {
        this.reportCategories = reportCategories;
    }

}
