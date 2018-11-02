package org.cirdles.squid.op;

import org.cirdles.squid.shrimp.ShrimpFraction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OPFileHandler {

    public static List<ShrimpFraction> convertOPFileToShrimpFractions(File file) {
        List<OPFile> ops = OPFileRunFractionParser.parseOPFile(file);
        List<ShrimpFraction> shrimps = convertOPFilesToShrimpFractions(ops);
        return shrimps;
    }

    public static List<ShrimpFraction> convertOPFilesToShrimpFractions(List<OPFile> ops) {
        List<ShrimpFraction> shrimps = new ArrayList<>(ops.size());
        for (int i = 0; i < shrimps.size(); i++) {
            ShrimpFraction shrimp = convertOPFileToShrimpFraction(ops.get(i));
            shrimps.add(shrimp);
        }
        return shrimps;
    }

    public static ShrimpFraction convertOPFileToShrimpFraction(OPFile op) {
        ShrimpFraction shrimp = new ShrimpFraction();
        shrimp.setFractionID(op.getName());
        shrimp.setDateTimeMilliseconds(op.getDateTimeMilliseconds());
        shrimp.setCountTimeSec(op.getCountTimeSec());
        shrimp.setTimeStampSec(op.getTimeStampSec());
        shrimp.setTotalCounts(op.getTotalCounts());
        shrimp.setSbmZeroCps(op.getSbmZeroCPS());
        shrimp.setTotalCountsSBM(op.getTotalSBM());
        return shrimp;
    }
}
