package org.cirdles.squid.gui.squidReportTable;

import org.cirdles.squid.utilities.IntuitiveStringComparator;

import java.util.Comparator;

public class RowComparator implements Comparator<String> {
    private static final IntuitiveStringComparator<String> stringComparator = new IntuitiveStringComparator<>();

    public RowComparator() {
    }

    @Override
    public int compare(String s1, String s2) {
        int retVal;

        try {
            double n1 = Double.parseDouble(s1);
            double n2 = Double.parseDouble(s2);

            if (n1 == n2) {
                retVal = 0;
            } else if (n1 > n2) {
                retVal = 1;
            } else {
                retVal = -1;
            }
        } catch (Exception e) {
            retVal = stringComparator.compare(s1, s2);
        }

        return retVal;
    }
}