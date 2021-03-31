package org.cirdles.squid.gui.squidReportTable;

import java.util.HashMap;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTable;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;

import java.util.List;
import java.util.Map;
import org.cirdles.squid.constants.Squid3Constants;

import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.tasks.Task;

public class SquidReportTableHelperMethods {

    public static String[][] processReportTextArray(SquidReportTableLauncher.ReportTableTab tab, SquidReportTableInterface squidReportTable, String unknownSpot) {
        String[][] textArray = {};

        Map<String, List<ShrimpFractionExpressionInterface>> reportSamplesMap = new HashMap<>();
        switch (tab) {
            case refMatCustom:
                if (squidReportTable == null) {
                    squidReportTable = SquidReportTable.createDefaultSquidReportTableRefMat(squidProject.getTask());
                }

                reportSamplesMap.put(((Task) squidProject.getTask()).getFilterForRefMatSpotNames(), squidProject.getTask().getReferenceMaterialSpots());
                textArray = squidReportTable.reportSpotsInCustomTable(squidReportTable, squidProject.getTask(), reportSamplesMap, true);
                break;
            case unknownCustom:
                if (squidReportTable == null) {
                    squidReportTable = SquidReportTable.createDefaultSquidReportTableUnknown(squidProject.getTask());
                }

                if (Squid3Constants.SpotTypes.UNKNOWN.getSpotTypeName().compareToIgnoreCase(unknownSpot) == 0) {
                    for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : squidProject.getTask().getMapOfUnknownsBySampleNames().entrySet()) {
                        if (entry.getKey().compareToIgnoreCase(Squid3Constants.SpotTypes.UNKNOWN.getSpotTypeName()) != 0) {
                            reportSamplesMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    reportSamplesMap.put(unknownSpot, squidProject.getTask().getMapOfUnknownsBySampleNames().get(unknownSpot));
                }
                textArray = squidReportTable.reportSpotsInCustomTable(squidReportTable, squidProject.getTask(), reportSamplesMap, true);
                break;

            default:
        }

        return textArray;
    }

    private static void filterShrimpFractionsBySpot(List<ShrimpFractionExpressionInterface> spots, String unknownSpot) {
        if (unknownSpot.compareToIgnoreCase(Squid3Constants.SpotTypes.UNKNOWN.getSpotTypeName()) != 0) {
            for (int i = 0; i < spots.size(); i++) {
                ShrimpFractionExpressionInterface spot = spots.get(i);
                if (!spot.getFractionID().startsWith(unknownSpot)) {
                    spots.remove(i);
                    i--;
                }
            }
        }
    }
}
