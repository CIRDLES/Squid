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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import static org.cirdles.squid.constants.Squid3Constants.SpotTypes;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import static org.cirdles.squid.squidReports.squidReportTables.SquidReportTable.HEADER_ROW_COUNT;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public interface SquidReportTableInterface extends XMLSerializerInterface {

    public default String[][] reportSpotsInCustomTable(
            SquidReportTableInterface squidReportTable,
            TaskInterface task,
            List<ShrimpFractionExpressionInterface> spots) {

        String[][] retVal = new String[spots.size() + HEADER_ROW_COUNT][];

        // process columns
        List<SquidReportColumnInterface> columns = new ArrayList<>();
        LinkedList<SquidReportCategoryInterface> squidReportCategories = squidReportTable.getReportCategories();
        Iterator<SquidReportCategoryInterface> categoryIterator = squidReportCategories.iterator();
        while (categoryIterator.hasNext()) {
            SquidReportCategoryInterface cat = categoryIterator.next();
            LinkedList<SquidReportColumnInterface> squidReportColumns = cat.getCategoryColumns();

            Iterator<SquidReportColumnInterface> columnIterator = squidReportColumns.iterator();
            boolean firstCol = true;
            while (columnIterator.hasNext()) {
                SquidReportColumnInterface column = columnIterator.next();
                column.initReportColumn(task);

                column.getColumnHeaders()[0] = firstCol ? cat.getDisplayName() : "";
                firstCol = false;

                columns.add(column);

                if (column.hasVisibleUncertaintyColumn()) {
                    SquidReportColumnInterface uncertaintyColumn = column.getUncertaintyColumn();
                    columns.add(uncertaintyColumn);
                }
            }
        }

        // column 0 will contain true for included fractions and false for rejected fractions
        // column 1 will contain aliquot name
        // column 2 for spot name
        // column last - 1 for spot name
        // last column will flag whether fraction is filtered = true or false
        int countOfAllColumns = columns.size() + 5;

        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = new String[countOfAllColumns];
            for (int j = 0; j < retVal[i].length; j++) {
                retVal[i][j] = "";
            }
        }

        // headers
        int columnIndex = 3;
        for (SquidReportColumnInterface col : columns) {
            String[] headers = col.getColumnHeaders();
            for (int i = 0; i < headers.length; i++) {
                retVal[i][columnIndex] = headers[i];
            }
            columnIndex++;
        }

        retVal[0][0] = Integer.toString(HEADER_ROW_COUNT);
        int fractionRowCount = HEADER_ROW_COUNT;

        for (ShrimpFractionExpressionInterface spot : spots) {
            retVal[fractionRowCount][0] = "true";//included
            retVal[fractionRowCount][2] = spot.getFractionID();
            // detect aliquot
            if (spot.getSpotNumber() < 0) {
                retVal[fractionRowCount][1] = spot.getFractionID();
            } else {

                retVal[fractionRowCount][countOfAllColumns - 2] = spot.getFractionID();

                retVal[fractionRowCount][countOfAllColumns - 1] = "false";

                columnIndex = 3;
                for (SquidReportColumnInterface col : columns) {
                    retVal[fractionRowCount][columnIndex] = col.cellEntryForSpot(spot);
                    columnIndex++;
                }
            }
            fractionRowCount++;
        }

        return retVal;
    }

    /**
     * @return the reportTableName
     */
    public String getReportTableName();

    /**
     * @param reportTableName the reportTableName to set
     */
    public void setReportTableName(String reportTableName);
    
    public boolean amWeightedMeanPlotAndSortReport();
    
    public void formatWeightedMeanPlotAndSortReport();

    /**
     * @return the reportCategories
     */
    public LinkedList<SquidReportCategoryInterface> getReportCategories();

    /**
     * @param reportCategories the reportCategories to set
     */
    public void setReportCategories(LinkedList<SquidReportCategoryInterface> reportCategories);
    
    /**
     * @return the reportSpotTarget
     */
    public SpotTypes getReportSpotTarget();
    
    /**
     * @param reportSpotTarget the reportSpotTarget to set
     */
    public void setReportSpotTarget(SpotTypes reportSpotTarget);

    public void setIsDefault(boolean isDefault);

    public boolean isDefault();

    public boolean equals(Object ob);

    /**
     * @return the version
     */
    public int getVersion();

    /**
     * @param version the version to set
     */
    public void setVersion(int version);

}
