package org.cirdles.squid.op;

import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.RunParameterNames;
import org.cirdles.squid.prawn.RunTableEntryParameterNames;
import org.cirdles.squid.prawn.SetParameterNames;
import org.cirdles.squid.shrimp.ShrimpFraction;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class OPFileHandler {


    public static PrawnFile convertOPFileToPrawnFile(File file) {
        List<OPFile> ops = OPFileRunFractionParser.parseOPFile(file);
        PrawnFile prawnFile = convertOPFilesToPrawnFile(ops);
        return prawnFile;
    }

    public static PrawnFile convertOPFilesToPrawnFile(List<OPFile> ops) {
        PrawnFile prawnFile = new PrawnFile();
        for (OPFile op : ops) {
            PrawnFile.Run run = new PrawnFile.Run();
            run.setSet(new PrawnFile.Run.Set());
            run.setRunTable(new PrawnFile.Run.RunTable());

            PrawnFile.Run.Par measurements = new PrawnFile.Run.Par();
            measurements.setName(RunParameterNames.MEASUREMENTS);
            measurements.setValue("" + op.getMeasurements());
            run.getPar().add(measurements);

            PrawnFile.Run.Par scans = new PrawnFile.Run.Par();
            scans.setName(RunParameterNames.SCANS);
            scans.setValue("" + op.getScans());
            run.getPar().add(scans);

            PrawnFile.Run.Par sets = new PrawnFile.Run.Par();
            sets.setName(RunParameterNames.SETS);
            sets.setValue("" + op.getSets());
            run.getPar().add(sets);

            PrawnFile.Run.Par sbmZeroCps = new PrawnFile.Run.Par();
            sbmZeroCps.setName(RunParameterNames.SBM_ZERO_CPS);
            sbmZeroCps.setValue("" + op.getSbmZeroCPS());
            run.getPar().add(sbmZeroCps);

            PrawnFile.Run.Set.Par date = new PrawnFile.Run.Set.Par();
            date.setName(SetParameterNames.DATE);
            date.setValue(new Date(op.getDateTimeMilliseconds()).toString());
            run.getSet().getPar().add(date);

            PrawnFile.Run.Set.Par time = new PrawnFile.Run.Set.Par();
            time.setName(SetParameterNames.TIME);
            time.setValue(new Time(op.getDateTimeMilliseconds()).toString());
            run.getSet().getPar().add(time);

            for (int i = 0; i < op.getCountTimeSec().length; i++) {
                PrawnFile.Run.RunTable.Entry entry = new PrawnFile.Run.RunTable.Entry();
                PrawnFile.Run.RunTable.Entry.Par countTimeSec = new PrawnFile.Run.RunTable.Entry.Par();
                countTimeSec.setName(RunTableEntryParameterNames.COUNT_TIME_SEC); //not sure if this is right, didn't find count_time_sec
                countTimeSec.setValue("" + op.getCountTimeSec()[i]);
                entry.getPar().add(countTimeSec);

                run.getRunTable().getEntry().add(entry);
            }

            for (int j = 0; j < op.getScans(); j++) {
                PrawnFile.Run.Set.Scan scan = new PrawnFile.Run.Set.Scan();
                for (int i = 0; i < op.getMeasurements(); i++) {
                    PrawnFile.Run.Set.Scan.Measurement measurement = new PrawnFile.Run.Set.Scan.Measurement();

                    PrawnFile.Run.Set.Scan.Measurement.Par timeStampSec = new PrawnFile.Run.Set.Scan.Measurement.Par();
                    timeStampSec.setName("time_stamp_sec");
                    timeStampSec.setValue("" + op.getTimeStampSec()[i][j]);
                    measurement.getPar().add(timeStampSec);

                    PrawnFile.Run.Set.Scan.Measurement.Par totalCounts = new PrawnFile.Run.Set.Scan.Measurement.Par();
                    totalCounts.setName("196Zr2O");
                    totalCounts.setValue("" + op.getTotalCounts()[i][j]);
                    measurement.getPar().add(totalCounts);

                    PrawnFile.Run.Set.Scan.Measurement.Par totalSBM = new PrawnFile.Run.Set.Scan.Measurement.Par();
                    totalSBM.setName("SBM");
                    totalSBM.setValue("" + op.getTotalSBM()[i][j]);
                    measurement.getPar().add(totalSBM);

                    scan.getMeasurement().add(measurement);
                }
                run.getSet().getScan().add(scan);
            }

            prawnFile.getRun().add(run);
        }

        return prawnFile;
    }

    public static List<ShrimpFraction> convertOPFileToShrimpFractions(File file) {
        List<OPFile> ops = OPFileRunFractionParser.parseOPFile(file);
        List<ShrimpFraction> shrimps = convertOPFilesToShrimpFractions(ops);
        return shrimps;
    }

    public static List<ShrimpFraction> convertOPFilesToShrimpFractions(List<OPFile> ops) {
        List<ShrimpFraction> shrimps = new ArrayList<>(ops.size());
        for (int i = 0; i < ops.size(); i++) {
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
