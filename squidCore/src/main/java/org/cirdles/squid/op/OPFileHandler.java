package org.cirdles.squid.op;

import org.cirdles.squid.Squid;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.RunParameterNames;
import org.cirdles.squid.prawn.RunTableEntryParameterNames;
import org.cirdles.squid.prawn.SetParameterNames;
import org.cirdles.squid.shrimp.ShrimpDataFileInterface;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFAULT_BACKGROUND_MASS_LABEL;

public class OPFileHandler {

    private static final String[] massStationLabelsNINE
            = new String[]{"196Zr2O", "204Pb", DEFAULT_BACKGROUND_MASS_LABEL, "206Pb", "207Pb", "208Pb", "238U", "248ThO", "254UO"};

    private static final String[] massStationAMUsNINE
            = new String[]{"196", "204", "204.1", "206", "207", "208", "238", "248", "254"};

    private static final String[] massStationLabelsTEN
            = new String[]{"196Zr2O", "204Pb", DEFAULT_BACKGROUND_MASS_LABEL, "206Pb", "207Pb", "208Pb", "238U", "248ThO", "254UO", "270UO2"};

    private static final String[] massStationAMUsTEN
            = new String[]{"196", "204", "204.1", "206", "207", "208", "238", "248", "254", "270"};

    private static final String[] massStationLabelsELEVEN
            = new String[]{"YbO", "Zr2O", "HfO", "Pb204", DEFAULT_BACKGROUND_MASS_LABEL, "206Pb", "207Pb", "208Pb", "238U", "248ThO", "254UO"};

    private static final String[] massStationAMUsELEVEN
            = new String[]{"190", "195.8", "195.9", "204", "204.1", "206", "207", "208", "238", "248", "254"};

    public ShrimpDataFileInterface convertOPFileToPrawnFile(File opFile) throws IOException {
        List<OPFraction> opFractions = OPFileRunFractionParser.parseOPFile(opFile);
        return convertOPFilesToPrawnFile(opFractions);
    }

    private ShrimpDataFileInterface convertOPFilesToPrawnFile(List<OPFraction> opFractions) {
        ShrimpDataFileInterface prawnFile = new PrawnFile();

        prawnFile.setSoftwareVersion("Squid3 v" + Squid.VERSION + "-processed OP File");

        for (OPFraction opFraction : opFractions) {
            PrawnFile.Run run = new PrawnFile.Run();

            // run
            // run parameters
            PrawnFile.Run.Par title = new PrawnFile.Run.Par();
            title.setName(RunParameterNames.TITLE);
            title.setValue(opFraction.getName());
            run.getPar().add(title);

            PrawnFile.Run.Par sets = new PrawnFile.Run.Par();
            sets.setName(RunParameterNames.SETS);
            sets.setValue("" + opFraction.getSets());
            run.getPar().add(sets);

            PrawnFile.Run.Par measurements = new PrawnFile.Run.Par();
            measurements.setName(RunParameterNames.MEASUREMENTS);
            measurements.setValue("" + opFraction.getMeasurements());
            run.getPar().add(measurements);

            PrawnFile.Run.Par scans = new PrawnFile.Run.Par();
            scans.setName(RunParameterNames.SCANS);
            scans.setValue("" + opFraction.getScans());
            run.getPar().add(scans);

            PrawnFile.Run.Par deadTimeNS = new PrawnFile.Run.Par();
            deadTimeNS.setName(RunParameterNames.DEAD_TIME_NS);
            deadTimeNS.setValue("0");
            run.getPar().add(deadTimeNS); // placeholder in Par list

            PrawnFile.Run.Par sbmZeroCps = new PrawnFile.Run.Par();
            sbmZeroCps.setName(RunParameterNames.SBM_ZERO_CPS);
            sbmZeroCps.setValue("" + opFraction.getSbmZeroCPS());
            run.getPar().add(sbmZeroCps);

            // need placeholders
            /*
            <par name="autocentering" value="yes" />
            <par name="qt1y_mode" value="qt1y_scan" /><!-- can be "focus_stage" "qt1y_scan" "constant" -->
            <par name="deflect_beam_between_peaks" value="yes" />
            <par name="autocenter_method" value="50_percent_pk_height" /><!-- can be "polynomial_fit" "50_percent_pk_height" "sliding_integral" -->
            <par name="stage_x" value="4927" />
            <par name="stage_y" value="14867" />
            <par name="stage_z" value="7415" />
             */
            run.getPar().add(deadTimeNS); // placeholder in Par list
            run.getPar().add(deadTimeNS); // placeholder in Par list
            run.getPar().add(deadTimeNS); // placeholder in Par list
            run.getPar().add(deadTimeNS); // placeholder in Par list
            run.getPar().add(deadTimeNS); // placeholder in Par list
            run.getPar().add(deadTimeNS); // placeholder in Par list
            run.getPar().add(deadTimeNS); // placeholder in Par list

            // run.runtable
            // run.runtable entries
            // run.runtable entry parameters
            run.setRunTable(new PrawnFile.Run.RunTable());

            for (int i = 0; i < opFraction.getCountTimeSec().length; i++) {
                PrawnFile.Run.RunTable.Entry entry = new PrawnFile.Run.RunTable.Entry();

                // the first two are faked for now
                PrawnFile.Run.RunTable.Entry.Par label = new PrawnFile.Run.RunTable.Entry.Par();
                label.setName(RunTableEntryParameterNames.LABEL);

                int measCount = opFraction.getMeasurements();
                label.setValue((measCount == 9) ? massStationLabelsNINE[i] : (measCount == 10) ? massStationLabelsTEN[i] : massStationLabelsELEVEN[i]);
                entry.getPar().add(label);

                PrawnFile.Run.RunTable.Entry.Par amu = new PrawnFile.Run.RunTable.Entry.Par();
                amu.setName(RunTableEntryParameterNames.AMU);

                amu.setValue((measCount == 9) ? massStationAMUsNINE[i] : (measCount == 10) ? massStationAMUsTEN[i] : massStationAMUsELEVEN[i]);
                entry.getPar().add(amu);

                // need placeholder parameters for 
                /*
                <par name="trim_amu" value="0.000" />
                <par name="autocenter_offset_amu" value="0.000000" /><!-- 0=do not use -->
                 */
                PrawnFile.Run.RunTable.Entry.Par placeHolderPar = new PrawnFile.Run.RunTable.Entry.Par();
                entry.getPar().add(placeHolderPar);
                entry.getPar().add(placeHolderPar);

                PrawnFile.Run.RunTable.Entry.Par countTimeSec = new PrawnFile.Run.RunTable.Entry.Par();
                countTimeSec.setName(RunTableEntryParameterNames.COUNT_TIME_SEC);
                countTimeSec.setValue("" + opFraction.getCountTimeSec()[i]);
                entry.getPar().add(countTimeSec);

                // need placeholder parameters for 
                /*
                <par name="delay_sec" value="5.000" />
                <par name="collector_focus" value="18.000" />
                 */
                entry.getPar().add(placeHolderPar);
                entry.getPar().add(placeHolderPar);

                PrawnFile.Run.RunTable.Entry.Par centeringTimeSec = new PrawnFile.Run.RunTable.Entry.Par();
                centeringTimeSec.setName(RunTableEntryParameterNames.CENTERING_TIME_SEC);
                centeringTimeSec.setValue("0");
                entry.getPar().add(centeringTimeSec);

                run.getRunTable().getEntry().add(entry);
            }

            // run.set            
            // run.set parameters
            // run.set.scan
            // run.set.scan.measurement
            // run.set.scan.measurement.parameters
            // run.set.scan.measurement.data
            run.setSet(new PrawnFile.Run.Set());

            PrawnFile.Run.Set.Par date = new PrawnFile.Run.Set.Par();
            date.setName(SetParameterNames.DATE);
            date.setValue(new Date(opFraction.getDateTimeMilliseconds()).toString());
            run.getSet().getPar().add(date);

            PrawnFile.Run.Set.Par time = new PrawnFile.Run.Set.Par();
            time.setName(SetParameterNames.TIME);
            time.setValue(new Time(opFraction.getDateTimeMilliseconds()).toString());
            run.getSet().getPar().add(time);

            for (int j = 0; j < opFraction.getScans(); j++) {
                PrawnFile.Run.Set.Scan scan = new PrawnFile.Run.Set.Scan();
                for (int i = 0; i < opFraction.getMeasurements(); i++) {
                    PrawnFile.Run.Set.Scan.Measurement measurement = new PrawnFile.Run.Set.Scan.Measurement();

                    // need placeholder
                    /*
                    <par name="detectors" value="12" />
                     */
                    PrawnFile.Run.Set.Scan.Measurement.Par placeHolderPar = new PrawnFile.Run.Set.Scan.Measurement.Par();
                    measurement.getPar().add(placeHolderPar);

                    PrawnFile.Run.Set.Scan.Measurement.Par trimMass = new PrawnFile.Run.Set.Scan.Measurement.Par();
                    trimMass.setName("trim_mass");
                    trimMass.setValue("0");
                    measurement.getPar().add(trimMass);

                    PrawnFile.Run.Set.Scan.Measurement.Par timeStampSec = new PrawnFile.Run.Set.Scan.Measurement.Par();
                    timeStampSec.setName("time_stamp_sec");
                    timeStampSec.setValue("" + opFraction.getTimeStampSec()[i][j]);
                    measurement.getPar().add(timeStampSec);

                    PrawnFile.Run.Set.Scan.Measurement.Data totalCounts = new PrawnFile.Run.Set.Scan.Measurement.Data();
                    // TODO: make robust
                    if (opFraction.getMeasurements() == 10) {
                        totalCounts.setName(massStationLabelsTEN[i]);
                    } else {
                        totalCounts.setName(massStationLabelsELEVEN[i]);
                    }
                    // place negative total in the measurements slot as a signal flag "OP FILE" to PrawnFileRunParser
                    totalCounts.setValue("-" + opFraction.getTotalCounts()[i][j]);
                    measurement.getData().add(totalCounts);

                    PrawnFile.Run.Set.Scan.Measurement.Data totalSBM = new PrawnFile.Run.Set.Scan.Measurement.Data();
                    totalSBM.setName("SBM");
                    totalSBM.setValue("" + opFraction.getTotalSBM()[i][j]);
                    measurement.getData().add(totalSBM);

                    scan.getMeasurement().add(measurement);
                }
                run.getSet().getScan().add(scan);
            }

            prawnFile.getRun().add(run);
        }

        prawnFile.setRuns((short) opFractions.size());

        return prawnFile;
    }
}