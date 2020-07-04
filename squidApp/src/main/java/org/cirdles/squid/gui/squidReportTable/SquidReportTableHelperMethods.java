package org.cirdles.squid.gui.squidReportTable;

import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTable;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;

import java.util.List;

import static org.cirdles.squid.gui.SquidUIController.squidProject;

public class SquidReportTableHelperMethods {
    public static String[][] processReportTextArray(SquidReportTableLauncher.ReportTableTab tab, SquidReportTableInterface squidReportTable, String unknownSpot) {
        String[][] textArray = {};

        switch (tab) {
            case refMatCustom:
                if (squidReportTable == null) {
                    squidReportTable = SquidReportTable.createDefaultSquidReportTableRefMat(squidProject.getTask());
                }
                textArray = squidReportTable.reportSpotsInCustomTable(squidReportTable, squidProject.getTask(), squidProject.getTask().getReferenceMaterialSpots());
                break;
            case unknownCustom:
                if (squidReportTable == null) {
                    squidReportTable = SquidReportTable.createDefaultSquidReportTableUnknown(squidProject.getTask());
                }
                List<ShrimpFractionExpressionInterface> spots = squidProject.makeListOfUnknownsBySampleName();
                filterShrimpFractionsBySpot(spots, unknownSpot);
                textArray = squidReportTable.reportSpotsInCustomTable(squidReportTable, squidProject.getTask(), spots);
                break;

            default:
        }

        return textArray;
    }

    private static void filterShrimpFractionsBySpot(List<ShrimpFractionExpressionInterface> spots, String unknownSpot) {
        if (unknownSpot.compareToIgnoreCase("UNKNOWNS") != 0) {
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
