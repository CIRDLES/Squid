package org.cirdles.squid.prawnLegacy;

import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.RunParameterNames;
import org.cirdles.squid.prawn.RunTableEntryParameterNames;
import org.cirdles.squid.prawn.SetParameterNames;

import java.util.List;
import org.cirdles.squid.Squid;
import org.cirdles.squid.shrimp.ShrimpDataFileInterface;
import org.cirdles.squid.shrimp.ShrimpDataLegacyFileInterface;

public class PrawnLegacyFileHandler {

    public static ShrimpDataFileInterface convertPrawnLegacyFileToPrawnFile(ShrimpDataLegacyFileInterface prawnLegacyFile) {
        ShrimpDataFileInterface prawnFile = new PrawnFile();

        prawnFile.setSoftwareVersion("Squid3 v" + Squid.VERSION + " - translated from SHRIMP II v2 SW");

        prawnFile.setRuns((short) prawnLegacyFile.getRun().size());
        
        for (PrawnLegacyFile.Run legacyRun : prawnLegacyFile.getRun()) {
            PrawnFile.Run run = new PrawnFile.Run();

            // run
            // run parameters
            PrawnFile.Run.Par title = new PrawnFile.Run.Par();
            title.setName(RunParameterNames.TITLE);
            title.setValue(legacyRun.getTitle());
            run.getPar().add(title);

            PrawnFile.Run.Par sets = new PrawnFile.Run.Par();
            sets.setName(RunParameterNames.SETS);
            sets.setValue("" + legacyRun.getSets());
            run.getPar().add(sets);

            PrawnFile.Run.Par measurements = new PrawnFile.Run.Par();
            measurements.setName(RunParameterNames.MEASUREMENTS);
            measurements.setValue("" + legacyRun.getPeaks());
            run.getPar().add(measurements);

            PrawnFile.Run.Par scans = new PrawnFile.Run.Par();
            scans.setName(RunParameterNames.SCANS);
            scans.setValue("" + legacyRun.getScans());
            run.getPar().add(scans);

            PrawnFile.Run.Par deadTimeNS = new PrawnFile.Run.Par();
            deadTimeNS.setName(RunParameterNames.DEAD_TIME_NS);
            deadTimeNS.setValue("" + legacyRun.getDeadTimeNs());
            run.getPar().add(deadTimeNS);

            PrawnFile.Run.Par sbmZeroCps = new PrawnFile.Run.Par();
            sbmZeroCps.setName(RunParameterNames.SBM_ZERO_CPS);
            sbmZeroCps.setValue("" + legacyRun.getSbmZeroCps());
            run.getPar().add(sbmZeroCps);

            PrawnFile.Run.Par autocentering = new PrawnFile.Run.Par();
            autocentering.setName(RunParameterNames.AUTOCENTERING);
            autocentering.setValue("" + legacyRun.getAutocentering());
            run.getPar().add(autocentering);

            PrawnFile.Run.Par qt1y_mode = new PrawnFile.Run.Par();
            qt1y_mode.setName(RunParameterNames.QT_1_Y_MODE);
            qt1y_mode.setValue("" + legacyRun.getQt1YMode());
            run.getPar().add(qt1y_mode);

            PrawnFile.Run.Par deflect_beam_between_peaks = new PrawnFile.Run.Par();
            deflect_beam_between_peaks.setName(RunParameterNames.DEFLECT_BEAM_BETWEEN_PEAKS);
            deflect_beam_between_peaks.setValue("" + legacyRun.getDeflectBeamBetweenPeaks());
            run.getPar().add(deflect_beam_between_peaks);

            PrawnFile.Run.Par autocenter_method = new PrawnFile.Run.Par();
            autocenter_method.setName(RunParameterNames.AUTOCENTER_METHOD);
            autocenter_method.setValue("" + legacyRun.getAutocenterMethod());
            run.getPar().add(autocenter_method);

            // need placeholders for stage x,y,z
            PrawnFile.Run.Par stage_x = new PrawnFile.Run.Par();
            stage_x.setName(RunParameterNames.STAGE_X);
            stage_x.setValue("0");
            run.getPar().add(stage_x);

            PrawnFile.Run.Par stage_y = new PrawnFile.Run.Par();
            stage_y.setName(RunParameterNames.STAGE_Y);
            stage_y.setValue("0");
            run.getPar().add(stage_y);

            PrawnFile.Run.Par stage_z = new PrawnFile.Run.Par();
            stage_z.setName(RunParameterNames.STAGE_Z);
            stage_z.setValue("0");
            run.getPar().add(stage_z);

            // run.runtable
            // run.runtable entries
            // run.runtable entry parameters
            run.setRunTable(new PrawnFile.Run.RunTable());

            for (int i = 0; i < legacyRun.getRunTable().getName().size(); i++) {
                PrawnFile.Run.RunTable.Entry entry = new PrawnFile.Run.RunTable.Entry();
                PrawnLegacyFile.Run.RunTable legacyRunTable = legacyRun.getRunTable();

                PrawnFile.Run.RunTable.Entry.Par label = new PrawnFile.Run.RunTable.Entry.Par();
                label.setName(RunTableEntryParameterNames.LABEL);
                label.setValue("" + legacyRunTable.getName().get(i));
                entry.getPar().add(label);

                PrawnFile.Run.RunTable.Entry.Par amu = new PrawnFile.Run.RunTable.Entry.Par();
                amu.setName(RunTableEntryParameterNames.AMU);
                amu.setValue("" + legacyRunTable.getTrimMass().get(i));
                entry.getPar().add(amu);

                PrawnFile.Run.RunTable.Entry.Par trim_amu = new PrawnFile.Run.RunTable.Entry.Par();
                trim_amu.setName(RunTableEntryParameterNames.TRIM_AMU);
                trim_amu.setValue("0.0");
                entry.getPar().add(trim_amu);

                PrawnFile.Run.RunTable.Entry.Par autocenter_offset_amu = new PrawnFile.Run.RunTable.Entry.Par();
                autocenter_offset_amu.setName(RunTableEntryParameterNames.AUTOCENTER_OFFSET_AMU);
                autocenter_offset_amu.setValue("0.0");
                entry.getPar().add(autocenter_offset_amu);

                PrawnFile.Run.RunTable.Entry.Par countTimeSec = new PrawnFile.Run.RunTable.Entry.Par();
                countTimeSec.setName(RunTableEntryParameterNames.COUNT_TIME_SEC);
                countTimeSec.setValue("" + legacyRunTable.getCountTimeSec().get(i));
                entry.getPar().add(countTimeSec);

                PrawnFile.Run.RunTable.Entry.Par delay_sec = new PrawnFile.Run.RunTable.Entry.Par();
                delay_sec.setName(RunTableEntryParameterNames.DELAY_SEC);
                delay_sec.setValue("" + legacyRunTable.getDelaySec().get(i));
                entry.getPar().add(delay_sec);

                PrawnFile.Run.RunTable.Entry.Par collector_focus = new PrawnFile.Run.RunTable.Entry.Par();
                collector_focus.setName(RunTableEntryParameterNames.COLLECTOR_FOCUS);
                collector_focus.setValue("" + legacyRunTable.getCollectorFocus().get(i));
                entry.getPar().add(collector_focus);

                PrawnFile.Run.RunTable.Entry.Par centeringTimeSec = new PrawnFile.Run.RunTable.Entry.Par();
                centeringTimeSec.setName(RunTableEntryParameterNames.CENTERING_TIME_SEC);
                centeringTimeSec.setValue("" + legacyRunTable.getCenteringTimeSec().get(i));
                entry.getPar().add(centeringTimeSec);

                PrawnFile.Run.RunTable.Entry.Par centering_frequency = new PrawnFile.Run.RunTable.Entry.Par();
                centering_frequency.setName(RunTableEntryParameterNames.CENTERING_FREQUENCY);
                centering_frequency.setValue("" + legacyRunTable.getCenteringFrequency().get(i));
                entry.getPar().add(centering_frequency);

                PrawnFile.Run.RunTable.Entry.Par detector_selection = new PrawnFile.Run.RunTable.Entry.Par();
                detector_selection.setName(RunTableEntryParameterNames.DETECTOR_SELECTION);
                detector_selection.setValue("0.0");
                entry.getPar().add(detector_selection);

                PrawnFile.Run.RunTable.Entry.Par mc_lm_pos = new PrawnFile.Run.RunTable.Entry.Par();
                mc_lm_pos.setName(RunTableEntryParameterNames.MC_LM_POS);
                mc_lm_pos.setValue("-1.000");
                entry.getPar().add(mc_lm_pos);

                PrawnFile.Run.RunTable.Entry.Par mc_hm_pos = new PrawnFile.Run.RunTable.Entry.Par();
                mc_hm_pos.setName(RunTableEntryParameterNames.MC_HM_POS);
                mc_hm_pos.setValue("-1.000");
                entry.getPar().add(mc_hm_pos);

                PrawnFile.Run.RunTable.Entry.Par sc_reference = new PrawnFile.Run.RunTable.Entry.Par();
                sc_reference.setName(RunTableEntryParameterNames.SC_REFERENCE);
                sc_reference.setValue("" + legacyRunTable.getReference().get(i));
                entry.getPar().add(sc_reference);

                PrawnFile.Run.RunTable.Entry.Par sc_detector = new PrawnFile.Run.RunTable.Entry.Par();
                sc_detector.setName(RunTableEntryParameterNames.SC_DETECTOR);
                sc_detector.setValue("" + legacyRunTable.getDetector().get(i));
                entry.getPar().add(sc_detector);

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
            date.setValue(legacyRun.getSet().getDate());
            run.getSet().getPar().add(date);

            PrawnFile.Run.Set.Par time = new PrawnFile.Run.Set.Par();
            time.setName(SetParameterNames.TIME);
            time.setValue(legacyRun.getSet().getTime());
            run.getSet().getPar().add(time);

            PrawnFile.Run.Set.Par qt1y = new PrawnFile.Run.Set.Par();
            qt1y.setName(SetParameterNames.QT_1_Y);
            qt1y.setValue("" + legacyRun.getSet().getQt1Y());
            run.getSet().getPar().add(qt1y);

            PrawnFile.Run.Set.Par qt1y_volts = new PrawnFile.Run.Set.Par();
            qt1y_volts.setName(SetParameterNames.QT_1_Y_VOLTS);
            qt1y_volts.setValue("0.0");
            run.getSet().getPar().add(qt1y_volts);

            PrawnFile.Run.Set.Par qt1z = new PrawnFile.Run.Set.Par();
            qt1z.setName(SetParameterNames.QT_1_Z);
            qt1z.setValue("0");
            run.getSet().getPar().add(qt1z);

            PrawnFile.Run.Set.Par pbm = new PrawnFile.Run.Set.Par();
            pbm.setName(SetParameterNames.PBM);
            pbm.setValue("" + legacyRun.getSet().getPbm());
            run.getSet().getPar().add(pbm);

            // the issue is that the legacy prawn file lists all the scans for a peak together
            // while the newer prawn file lists the scans for every peak together
            // so set up the Prawn data structure to populate
            List<PrawnFile.Run.Set.Scan> scanSet = run.getSet().getScan();
            scanSet.clear();
            for (int i = 0; i < legacyRun.getScans(); i++) {
                PrawnFile.Run.Set.Scan scan = new PrawnFile.Run.Set.Scan();
                scan.setNumber((short) (i + 1));
                scanSet.add(scan);
            }

            for (PrawnLegacyFile.Run.Set.Data.Peak legacyPeak : legacyRun.getSet().getData().getPeak()) {
                for (int i = 0; i < legacyPeak.getScan().size(); i++) {
                    PrawnFile.Run.Set.Scan.Measurement measurement
                            = makeMeasurement(legacyPeak.getScan().get(i), legacyPeak.getPeakName());
                    scanSet.get(i).getMeasurement().add(measurement);
                }
            }

            prawnFile.getRun().add(run);
        }

        return prawnFile;
    }

    private static PrawnFile.Run.Set.Scan.Measurement makeMeasurement(PrawnLegacyFile.Run.Set.Data.Peak.Scan legacyScan, String peakName) {
        PrawnFile.Run.Set.Scan.Measurement measurement = new PrawnFile.Run.Set.Scan.Measurement();

        PrawnFile.Run.Set.Scan.Measurement.Par detectors = new PrawnFile.Run.Set.Scan.Measurement.Par();
        detectors.setName("detectors");
        detectors.setValue("0");
        measurement.getPar().add(detectors);

        PrawnFile.Run.Set.Scan.Measurement.Par trimMass = new PrawnFile.Run.Set.Scan.Measurement.Par();
        trimMass.setName("trim_mass");
        trimMass.setValue("" + legacyScan.getTrimMass());
        measurement.getPar().add(trimMass);

        PrawnFile.Run.Set.Scan.Measurement.Par time_stamp_sec = new PrawnFile.Run.Set.Scan.Measurement.Par();
        time_stamp_sec.setName("time_stamp_sec");
        time_stamp_sec.setValue("" + legacyScan.getTimeStampSec());
        measurement.getPar().add(time_stamp_sec);

        PrawnFile.Run.Set.Scan.Measurement.Par autocentering_result = new PrawnFile.Run.Set.Scan.Measurement.Par();
        autocentering_result.setName("autocentering_result");
        autocentering_result.setValue("ok");
        measurement.getPar().add(autocentering_result);

        PrawnFile.Run.Set.Scan.Measurement.Par autocentering_detector = new PrawnFile.Run.Set.Scan.Measurement.Par();
        autocentering_detector.setName("autocentering_detector");
        autocentering_detector.setValue("1");
        measurement.getPar().add(autocentering_detector);

        PrawnFile.Run.Set.Scan.Measurement.Data ionCount = new PrawnFile.Run.Set.Scan.Measurement.Data();
        ionCount.setName(peakName);
        String ionCounts = "";
        for (int i = 0; i < legacyScan.getIonCount().size(); i++) {
            ionCounts += legacyScan.getIonCount().get(i);
            if (i < (legacyScan.getIonCount().size() - 1)) {
                ionCounts += ",";
            }
        }
        ionCount.setValue(ionCounts);
        measurement.getData().add(ionCount);

        PrawnFile.Run.Set.Scan.Measurement.Data SBM = new PrawnFile.Run.Set.Scan.Measurement.Data();
        SBM.setName("SBM");
        String SBMCounts = "";
        for (int i = 0; i < legacyScan.getNormCount().size(); i++) {
            SBMCounts += legacyScan.getNormCount().get(i);
            if (i < (legacyScan.getNormCount().size() - 1)) {
                SBMCounts += ",";
            }
        }
        SBM.setValue(SBMCounts);
        measurement.getData().add(SBM);

        return measurement;
    }
}
