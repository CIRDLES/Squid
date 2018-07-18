/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import static java.nio.file.StandardOpenOption.APPEND;
import java.text.SimpleDateFormat;
import static java.util.Arrays.asList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static org.cirdles.squid.constants.Squid3Constants.DEFAULT_PRAWNFILE_NAME;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.evaluationEngines.TaskExpressionEvaluatedPerSpotPerScanModelInterface;
import org.cirdles.ludwig.squid25.Utilities;
import org.cirdles.squid.Squid;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 * Calamari's reports engine.
 */
public class CalamariReportsEngine implements Serializable {

    private static final long serialVersionUID = 9086141392949762545L;

    private String folderToWriteCalamariReportsPath;
    private String reportParameterValues;
    private String reportNamePrefix;

    private File folderToWriteCalamariReports;
    private String nameOfPrawnXMLFile;

    private File ionIntegrations_PerScan;
    private File sBMIntegrations_PerScan;
    private File totalCounts_IonsAndSBM_PerScan;
    private File nuclideCPS_PerSpot;
    private File withinSpotRatios_PerScanMinus1;
    private File meanRatios_PerSpot;

    private StringBuilder refMatFractionsNuclideCPS_PerSpot;
    private StringBuilder unknownFractionsNuclideCPS_PerSpot;

    private StringBuilder headerWithinSpotRatios_PerScanMinus1;
    private StringBuilder refMatWithinSpotRatios_PerScanMinus1;
    private StringBuilder unknownWithinSpotRatios_PerScanMinus1;

    private StringBuilder headerMeanRatios_PerSpot_Unknowns;
    private StringBuilder headerMeanRatios_PerSpot_RefMat;
    private StringBuilder refMatMeanRatios_PerSpot;
    private StringBuilder unknownMeanRatios_PerSpot;

    private boolean doWriteReportFiles;

    /**
     *
     */
    public CalamariReportsEngine() {
        this.folderToWriteCalamariReportsPath = "";
        this.reportParameterValues = "";
        this.reportNamePrefix = "";

        this.folderToWriteCalamariReports = Squid.DEFAULT_SQUID3_REPORTS_FOLDER;
        this.nameOfPrawnXMLFile = "";

        this.refMatFractionsNuclideCPS_PerSpot = new StringBuilder();
        this.unknownFractionsNuclideCPS_PerSpot = new StringBuilder();

        this.headerWithinSpotRatios_PerScanMinus1 = new StringBuilder();
        this.refMatWithinSpotRatios_PerScanMinus1 = new StringBuilder();
        this.unknownWithinSpotRatios_PerScanMinus1 = new StringBuilder();

        this.headerMeanRatios_PerSpot_Unknowns = new StringBuilder();
        this.headerMeanRatios_PerSpot_RefMat = new StringBuilder();
        this.refMatMeanRatios_PerSpot = new StringBuilder();
        this.unknownMeanRatios_PerSpot = new StringBuilder();

        this.doWriteReportFiles = true;
    }

    public void clearReports() {
        refMatFractionsNuclideCPS_PerSpot = new StringBuilder();
        unknownFractionsNuclideCPS_PerSpot = new StringBuilder();

        headerWithinSpotRatios_PerScanMinus1 = new StringBuilder();
        refMatWithinSpotRatios_PerScanMinus1 = new StringBuilder();
        unknownWithinSpotRatios_PerScanMinus1 = new StringBuilder();

        headerMeanRatios_PerSpot_Unknowns = new StringBuilder();
        headerMeanRatios_PerSpot_RefMat = new StringBuilder();
        refMatMeanRatios_PerSpot = new StringBuilder();
        unknownMeanRatios_PerSpot = new StringBuilder();
    }

    /**
     * ReportsEngine to test results
     *
     * @param shrimpFractions the value of shrimpFractions
     * @param doWriteReportFiles
     * @param summaryOnly
     * @throws java.io.IOException
     */
    public void produceReports(List<ShrimpFractionExpressionInterface> shrimpFractions, boolean doWriteReportFiles, boolean summaryOnly) throws IOException {
        if (shrimpFractions.size() > 0) {
            // gather general info for all runs  from first fraction unknown and ref material
            ShrimpFraction shrimpFractionUnknown = (ShrimpFraction) shrimpFractions.get(0);

            produceReports(shrimpFractions, shrimpFractionUnknown, shrimpFractionUnknown, doWriteReportFiles, summaryOnly);
        }
    }

    /**
     *
     * @param shrimpFractions
     * @param shrimpFractionUnknown
     * @param shrimpFractionRefMat
     * @param doWriteReportFiles
     * @param summaryOnly
     * @throws IOException
     */
    public void produceReports(
            List<ShrimpFractionExpressionInterface> shrimpFractions,
            ShrimpFraction shrimpFractionUnknown, ShrimpFraction shrimpFractionRefMat,
            boolean doWriteReportFiles, boolean summaryOnly) throws IOException {

        this.doWriteReportFiles = doWriteReportFiles;

        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyyMMdd-HHmmss");

        reportParameterValues
                = "_" + (shrimpFractionUnknown.isUseSBM() ? "SBM" : "NOSBM")
                + "_" + (shrimpFractionUnknown.isUserLinFits() ? "LINREG" : "SPOTAV");

        if (nameOfPrawnXMLFile.length() >= DEFAULT_PRAWNFILE_NAME.length()) {
            reportNamePrefix = nameOfPrawnXMLFile.substring(0, DEFAULT_PRAWNFILE_NAME.length()) + "_" + reportParameterValues + "_";
        } else {
            reportNamePrefix = nameOfPrawnXMLFile + "_" + reportParameterValues + "_";
        }

        if (doWriteReportFiles) {
            folderToWriteCalamariReportsPath
                    = folderToWriteCalamariReports.getCanonicalPath()
                    + File.separator + nameOfPrawnXMLFile
                    + File.separator + sdfTime.format(new Date())
                    + reportParameterValues
                    + File.separator;
            File reportsFolder = new File(folderToWriteCalamariReportsPath);
            if (!reportsFolder.mkdirs()) {
                throw new IOException("Failed to delete reports folder '" + folderToWriteCalamariReportsPath + "'");
            }
        }

        prepSpeciesReportFiles(shrimpFractionUnknown);
        prepRatiosReportFiles(shrimpFractionUnknown, shrimpFractionRefMat);

        for (int f = 0; f < shrimpFractions.size(); f++) {
            ShrimpFraction shrimpFraction = (ShrimpFraction) shrimpFractions.get(f);
            if (summaryOnly) {
                reportWithinSpotRatiosAtInterpolatedTimes(shrimpFraction);
                reportMeanRatiosPerSpot(shrimpFraction);
            } else {
                reportTotalIonCountsAtMass(shrimpFraction);
                reportTotalSBMCountsAtMass(shrimpFraction);
                reportTotalCountsAtTimeStampAndTrimMass(shrimpFraction);
                reportTotalCountsPerSecondPerSpeciesPerAnalysis(shrimpFraction);
                reportWithinSpotRatiosAtInterpolatedTimes(shrimpFraction);
                reportMeanRatiosPerSpot(shrimpFraction);
            }

        } // end of fractions loop

        finishSpeciesReportFiles();
        finishRatiosReportFiles();

    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step "0a" – Total ion
     * counts at mass We’ve touched on this one once before, informally. It is a
     * direct extract from the XML, with one row per scan, and one column per
     * ‘integration-value’. For the demo XML, the array will have 684 rows of
     * data (114 analyses x 6 scans), and 115 columns (5 for row identifiers,
     * then for each of the 10 measured species, 11 columns comprising
     * count_time_sec and the integer values of the 10 integrations).
     * <p>
     * It needs five ‘left-hand’ columns to allow the rows to be identified and
     * sorted: Title = analysis-specific text-string read from XML Date =
     * analysis-specific date read from XML, to be expressed as YYYY-MM-DD
     * HH24:MI:SS Scan = integer, starting at 1 within each analysis Type =
     * "standard" or "unknown"; analyses with prefix "T." to be labelled
     * "standard", all others "unknown" Dead_time_ns = analysis-specific integer
     * read from XML
     * <p>
     * These are to be followed by 11 columns for each species (i.e. 110 columns
     * for the demo XML): [entry-label].count_time_sec = analysis-specific
     * integer read from XML [entry-label].1 = integer value corresponding to
     * the first of 10 ‘integrations’ within tags "<data name = [entry-label]>
     * </data>" for the specified combination of analysis, scan and species
     * [entry-label].2 = integer value corresponding to the second of 10
     * ‘integrations’ within tags "<data name = [entry-label]> </data>" for the
     * specified combination of analysis, scan and species … [entry-label].10 =
     * integer value corresponding to the tenth of 10 ‘integrations’ within tags
     * "<data name = [entry-label]> </data>" for the specified combination of
     * analysis, scan and species
     * <p>
     * Sorting: Primary criterion = Date, secondary criterion = Scan
     *
     * @param shrimpFraction
     * @param countOfSpecies
     */
    private void reportTotalIonCountsAtMass(ShrimpFraction shrimpFraction) throws IOException {

        int countOfPeaks = shrimpFraction.getPeakMeasurementsCount();
        int[][] rawPeakData = shrimpFraction.getRawPeakData();

        for (int scanNum = 0; scanNum < rawPeakData.length; scanNum++) {
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(scanNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown").append(", ");
            dataLine.append(String.valueOf(shrimpFraction.getDeadTimeNanoseconds()));

            double[] countTimeSec = shrimpFraction.getCountTimeSec();
            for (int i = 0; i < rawPeakData[scanNum].length; i++) {
                if ((i % countOfPeaks) == 0) {
                    dataLine.append(", ").append(String.valueOf(countTimeSec[i / countOfPeaks]));
                }
                dataLine.append(", ").append(rawPeakData[scanNum][i]);
            }

            if (doWriteReportFiles) {
                Files.write(ionIntegrations_PerScan.toPath(), asList(dataLine), APPEND);
            }
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step "0b" – Total SBM
     * counts at mass As for step "0a" in all respects , except that in the
     * fifth ‘left-hand’ column, dead_time_ns should be discarded and replaced
     * by SBM_zero_cps = analysis-specific integer read from XML
     * <p>
     * And the 11 columns for each species are: [entry-label].count_time_sec =
     * analysis-specific integer read from XML [entry-label].SBM.1 = integer
     * value corresponding to the first of 10 ‘integrations’ within tags "<data
     * name = SBM > </data>" for the specified combination of analysis, scan and
     * species [entry-label].SBM.2 = integer value corresponding to the second
     * of 10 ‘integrations’ within tags "<data name = SBM > </data>" for the
     * specified combination of analysis, scan and species …
     * [entry-label].SBM.10 = integer value corresponding to the tenth of 10
     * ‘integrations’ within tags "<data name = SBM> </data>" for the specified
     * combination of analysis, scan and species
     * <p>
     * Sorting: Primary criterion = Date (ascending), secondary criterion = Scan
     * (ascending)
     *
     * @param shrimpFraction
     * @param countOfSpecies
     */
    private void reportTotalSBMCountsAtMass(ShrimpFraction shrimpFraction) throws IOException {

        int countOfPeaks = shrimpFraction.getPeakMeasurementsCount();
        int[][] rawSBMData = shrimpFraction.getRawSBMData();
        double[] countTimeSec = shrimpFraction.getCountTimeSec();

        for (int scanNum = 0; scanNum < rawSBMData.length; scanNum++) {
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(scanNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown").append(", ");
            dataLine.append(String.valueOf(shrimpFraction.getSbmZeroCps()));

            for (int i = 0; i < rawSBMData[scanNum].length; i++) {
                if ((i % countOfPeaks) == 0) {
                    dataLine.append(", ").append(String.valueOf(countTimeSec[i / countOfPeaks]));
                }
                dataLine.append(", ").append(rawSBMData[scanNum][i]);
            }

            if (doWriteReportFiles) {
                Files.write(sBMIntegrations_PerScan.toPath(), asList(dataLine), APPEND);
            }
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step 1 – Total counts
     * at time-stamp and trim-mass This is intended to replicate the current
     * Step 1 sanity-check, with one row per scan, and one column per key
     * attribute of "total counts at peak". For the demo XML, the array will
     * have 684 rows of data (114 analyses x 6 scans), and 54 columns (4 for row
     * identifiers, then for each of the 10 measured species, 5 columns as
     * specified below).
     * <p>
     * It needs four ‘left-hand’ columns to allow the rows to be identified and
     * sorted: Title = analysis-specific text-string read from XML Date =
     * analysis-specific date read from XML, to be expressed as YYYY-MM-DD
     * HH24:MI:SS Scan = integer, starting at 1 within each analysis Type =
     * "standard" or "unknown"; analyses with prefix "T." to be labelled
     * "standard", all others "unknown"
     * <p>
     * These are to be followed by 5 columns for each species (i.e. 50 columns
     * for the demo XML): [entry-label].Time = integer "time_stamp_sec" read
     * from XML for the specified combination of analysis, scan and species
     * [entry-label].TotalCounts = calculated decimal value for "total counts at
     * mass" from Step 1, for the specified combination of analysis, scan and
     * species [entry-label].1SigmaAbs = calculated decimal value for "+/-1sigma
     * at mass" from Step 1, for the specified combination of analysis, scan and
     * species [entry-label].TotalSBM = calculated decimal value for "total SBM
     * counts" from Step 1, for the specified combination of analysis, scan and
     * species [entry-label].TrimMass = decimal "trim_mass_amu" read from XML
     * for the specified combination of analysis, scan and species
     * <p>
     * Sorting: Primary criterion = Date (ascending), secondary criterion = Scan
     * (ascending)
     *
     * @param shrimpFraction
     */
    private void reportTotalCountsAtTimeStampAndTrimMass(ShrimpFraction shrimpFraction) throws IOException {

        double[][] timeStampSec = shrimpFraction.getTimeStampSec();
        double[][] totalCounts = shrimpFraction.getTotalCounts();
        double[][] totalCountsOneSigmaAbs = shrimpFraction.getTotalCountsOneSigmaAbs();
        double[][] totalCountsSBM = shrimpFraction.getTotalCountsSBM();
        double[][] trimMass = shrimpFraction.getTrimMass();

        for (int scanNum = 0; scanNum < timeStampSec.length; scanNum++) {
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(scanNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

            for (int i = 0; i < timeStampSec[scanNum].length; i++) {
                dataLine.append(", ").append(timeStampSec[scanNum][i]);
                dataLine.append(", ").append(Utilities.roundedToSize(totalCounts[scanNum][i], 15));
                dataLine.append(", ").append(Utilities.roundedToSize(totalCountsOneSigmaAbs[scanNum][i], 15));
                dataLine.append(", ").append(Utilities.roundedToSize(totalCountsSBM[scanNum][i], 15));
                dataLine.append(", ").append(trimMass[scanNum][i]);
            }

            if (doWriteReportFiles) {
                Files.write(totalCounts_IonsAndSBM_PerScan.toPath(), asList(dataLine), APPEND);
            }
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step 2 – Total
     * counts-per-second, per species, per analysis This is intended to
     * replicate the current Step 2 sanity-check, with one row per *analysis*,
     * and one column per species. For the demo XML, the array will have 114
     * rows of data (one per analysis), and 13 columns (3 for row identifiers,
     * then one for each of the 10 measured species).
     * <p>
     * It needs three ‘left-hand’ columns to allow the rows to be identified and
     * sorted: Title = analysis-specific text-string read from XML Date =
     * analysis-specific date read from XML, to be expressed as YYYY-MM-DD
     * HH24:MI:SS Type = "standard" or "unknown"; analyses with prefix "T." to
     * be labelled "standard", all others "unknown"
     * <p>
     * These are to be followed by 1 column for each species (i.e. 10 columns
     * for the demo XML): [entry-label].TotalCps = calculated decimal value for
     * "total counts per second" from Step 2, for the specified combination of
     * analysis and species
     * <p>
     * Sorting: Primary criterion = Type (ascending; "standard" before unknown,
     * so alphabetical would do), secondary criterion = Date (ascending)
     *
     * @param shrimpFraction the value of shrimpFraction
     */
    private void reportTotalCountsPerSecondPerSpeciesPerAnalysis(ShrimpFraction shrimpFraction) {

        // need to sort by reference material vs unknown
        StringBuilder dataLine = new StringBuilder();
        dataLine.append(shrimpFraction.getFractionID()).append(", ");
        dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
        dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

        double[] totalCps = shrimpFraction.getTotalCps();

        for (int i = 0; i < totalCps.length; i++) {
            //rounding was done to saved values per Bodorkos April 2017
            dataLine.append(", ").append(totalCps[i]);
        }

        dataLine.append("\n");
        if (shrimpFraction.isReferenceMaterial()) {
            refMatFractionsNuclideCPS_PerSpot.append(dataLine);
        } else {
            unknownFractionsNuclideCPS_PerSpot.append(dataLine);
        }

    }

    /**
     * Produces Squid_03 report
     *
     * @param shrimpFraction
     */
    private void reportWithinSpotRatiosAtInterpolatedTimes(ShrimpFraction shrimpFraction) {

        int nDodCount = shrimpFraction.getIsotopicRatiosII().iterator().next().getRatEqTime().size();

        for (int nDodNum = 0; nDodNum < nDodCount; nDodNum++) {
            StringBuilder dataLine = new StringBuilder();
            if (doWriteReportFiles) {
                dataLine.append(shrimpFraction.getFractionID()).append(", ");
                dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
                dataLine.append(String.valueOf(nDodNum + 1)).append(", ");
                dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");
            } else {
                // format for GUI
                dataLine
                        .append(String.format("%1$-" + 20 + "s", shrimpFraction.getFractionID()))
                        .append(String.format("%1$-" + 20 + "s", getFormattedDate(shrimpFraction.getDateTimeMilliseconds())))
                        .append(String.format("%1$-" + 10 + "s", String.valueOf(nDodNum + 1)))
                        .append(String.format("%1$-" + 15 + "s", shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown"));
            }

            Iterator<SquidRatiosModel> squidRatiosIterator = shrimpFraction.getIsotopicRatiosII().iterator();
            while (squidRatiosIterator.hasNext()) {
                SquidRatiosModel isotopeRatioModel = squidRatiosIterator.next();
                if (isotopeRatioModel.isActive()) {
                    if (doWriteReportFiles) {
                        // July 2016 case of less than nDodCount = rare
                        if (nDodNum < isotopeRatioModel.getRatEqTime().size()) {
                            dataLine.append(", ").append(Utilities.roundedToSize(isotopeRatioModel.getRatEqTime().get(nDodNum), 15));
                            dataLine.append(", ").append(Utilities.roundedToSize(isotopeRatioModel.getRatEqVal().get(nDodNum), 15));
                            dataLine.append(", ").append(Utilities.roundedToSize(isotopeRatioModel.getRatEqErr().get(nDodNum), 15));
                        } else {
                            dataLine.append(", ").append("n/a");
                            dataLine.append(", ").append("n/a");
                            dataLine.append(", ").append("n/a");
                        }
                    } else {
                        if (nDodNum < isotopeRatioModel.getRatEqTime().size()) {
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(isotopeRatioModel.getRatEqTime().get(nDodNum), 15)));
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(isotopeRatioModel.getRatEqVal().get(nDodNum), 15)));
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(isotopeRatioModel.getRatEqErr().get(nDodNum), 15)));
                        } else {
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", "n/a"));
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", "n/a"));
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", "n/a"));
                        }
                    }
                }
            }
            if (!doWriteReportFiles) {
                // Handle any task expressions
                List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsEvaluated = shrimpFraction.getTaskExpressionsForScansEvaluated();
                for (TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEval : taskExpressionsEvaluated) {
                    if (doWriteReportFiles) {
                        if (nDodNum < taskExpressionEval.getRatEqTime().length) {
                            dataLine.append(", ").append(String.valueOf(taskExpressionEval.getRatEqTime()[nDodNum]));
                            dataLine.append(", ").append(Utilities.roundedToSize(taskExpressionEval.getRatEqVal()[nDodNum], 15));
                            dataLine.append(", ").append(Utilities.roundedToSize(taskExpressionEval.getRatEqErr()[nDodNum], 15));
                        } else {
                            dataLine.append(", ").append("n/a");
                            dataLine.append(", ").append("n/a");
                            dataLine.append(", ").append("n/a");
                        }
                    } else {
                        if (nDodNum < taskExpressionEval.getRatEqTime().length) {
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", String.valueOf(taskExpressionEval.getRatEqTime()[nDodNum])));
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(taskExpressionEval.getRatEqVal()[nDodNum], 15)));
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(taskExpressionEval.getRatEqErr()[nDodNum], 15)));
                        } else {
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", "n/a"));
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", "n/a"));
                            dataLine.append(", ").append(String.format("%1$-" + 20 + "s", "n/a"));
                        }

                    }
                }
            }

            dataLine.append("\n");
            if (shrimpFraction.isReferenceMaterial()) {
                refMatWithinSpotRatios_PerScanMinus1.append(dataLine);
            } else {
                unknownWithinSpotRatios_PerScanMinus1.append(dataLine);
            }
        }

        // place blank line between spots for in app view
        if (!doWriteReportFiles) {
            if (shrimpFraction.isReferenceMaterial()) {
                refMatWithinSpotRatios_PerScanMinus1.append("\n");
            } else {
                unknownWithinSpotRatios_PerScanMinus1.append("\n");
            }
        }
    }

    /**
     * Produces Squid_04 report
     *
     * @param shrimpFraction
     */
    private void reportMeanRatiosPerSpot(ShrimpFraction shrimpFraction) {

        // need to sort by reference material vs unknown
        StringBuilder dataLine = new StringBuilder();
        if (doWriteReportFiles) {
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");
        } else {
            // format for GUI
            dataLine
                    .append(String.format("%1$-" + 20 + "s", shrimpFraction.getFractionID()))
                    .append(String.format("%1$-" + 20 + "s", getFormattedDate(shrimpFraction.getDateTimeMilliseconds())))
                    .append(String.format("%1$-" + 15 + "s", shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown"));
        }

        Iterator<SquidRatiosModel> squidRatiosIterator = shrimpFraction.getIsotopicRatiosII().iterator();
        while (squidRatiosIterator.hasNext()) {
            SquidRatiosModel isotopeRatioModel = squidRatiosIterator.next();
            if (isotopeRatioModel.isActive()) {
                // April 2017 rounding was performed on calculated numbers
                if (doWriteReportFiles) {
                    dataLine.append(", ").append(String.valueOf(isotopeRatioModel.getMinIndex()));
                    dataLine.append(", ").append(Utilities.roundedToSize(isotopeRatioModel.getRatioVal(), 12));
                    dataLine.append(", ").append(Utilities.roundedToSize(isotopeRatioModel.getRatioFractErrAsOneSigmaPercent(), 12));
                } else {
                    dataLine.append(", ").append(String.format("%1$-" + 12 + "s", String.valueOf(isotopeRatioModel.getMinIndex())));
                    dataLine.append(", ").append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(isotopeRatioModel.getRatioVal(), 12)));
                    dataLine.append(", ").append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(isotopeRatioModel.getRatioFractErrAsOneSigmaPercent(), 12)));
                }
            }
        }

        if (!doWriteReportFiles) {
            // these expressions are spot-specific and INCLUDE those = Squid Switch "NU" calculated from ratios-of-interest
            Map<ExpressionTreeInterface, double[][]> spotExpressions = shrimpFraction.getTaskExpressionsEvaluationsPerSpot();
            for (Map.Entry<ExpressionTreeInterface, double[][]> entry : spotExpressions.entrySet()) {
                double[] expressionResults = entry.getValue()[0];
                if (doWriteReportFiles) {
                    dataLine.append(", ").append(Utilities.roundedToSize(expressionResults[0], 12));
                    if (expressionResults.length > 1) {
                        dataLine.append(", ").append(Utilities.roundedToSize(
                                calculatePercentUncertainty(expressionResults[0], expressionResults[1]), 12));
                    }
                } else {
                    dataLine.append(", ").append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(expressionResults[0], 12)));
                    if (expressionResults.length > 1) {
                        dataLine.append(", ").append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(
                                calculatePercentUncertainty(expressionResults[0], expressionResults[1]), 12)));
                    }
                }
            }
        }
        dataLine.append("\n");

        if (shrimpFraction.isReferenceMaterial()) {
            refMatMeanRatios_PerSpot.append(dataLine);
        } else {
            unknownMeanRatios_PerSpot.append(dataLine);
        }
    }

    private double calculatePercentUncertainty(double ratioVal, double ratioFractErr) {
        return 100.0 * ((ratioVal == 0) ? 1.0 : Math.abs(ratioFractErr / ratioVal));
    }

    private void prepSpeciesReportFiles(ShrimpFraction shrimpFraction) throws IOException {
        String[] namesOfSpecies = shrimpFraction.getNamesOfSpecies();
        int countOfIntegrations = shrimpFraction.getPeakMeasurementsCount();

        if (doWriteReportFiles) {
            ionIntegrations_PerScan = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "Check_01_IonIntegrations_PerScan.csv");
        }
        StringBuilder header = new StringBuilder();
        header.append("Title, Date, Scan, Type, Dead_time_ns");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".count_time_sec");
            for (int i = 0; i < countOfIntegrations; i++) {
                header.append(", ").append(nameOfSpecies).append(".").append(String.valueOf(i + 1));
            }
        }
        header.append("\n");

        if (doWriteReportFiles) {
            Files.write(ionIntegrations_PerScan.toPath(), header.toString().getBytes(UTF_8));
        }

        if (doWriteReportFiles) {
            sBMIntegrations_PerScan = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "Check_02_SBMIntegrations_PerScan.csv");
        }

        header = new StringBuilder();
        header.append("Title, Date, Scan, Type, SBM_zero_cps");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".count_time_sec");
            for (int i = 0; i < countOfIntegrations; i++) {
                header.append(", ").append(nameOfSpecies).append(".SBM.").append(String.valueOf(i + 1));
            }
        }
        header.append("\n");

        if (doWriteReportFiles) {
            Files.write(sBMIntegrations_PerScan.toPath(), header.toString().getBytes(UTF_8));
        }

        if (doWriteReportFiles) {
            totalCounts_IonsAndSBM_PerScan = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "SQUID_01_TotalCounts_IonsAndSBM_PerScan.csv");
        }
        header = new StringBuilder();
        header.append("Title, Date, Scan, Type");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".Time");
            header.append(", ").append(nameOfSpecies).append(".TotalCounts");
            header.append(", ").append(nameOfSpecies).append(".1SigmaAbs");
            header.append(", ").append(nameOfSpecies).append(".TotalSBM");
            header.append(", ").append(nameOfSpecies).append(".TrimMass");
        }
        header.append("\n");

        if (doWriteReportFiles) {
            Files.write(totalCounts_IonsAndSBM_PerScan.toPath(), header.toString().getBytes(UTF_8));
        }

        if (doWriteReportFiles) {
            nuclideCPS_PerSpot = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "SQUID_02_NuclideCPS_PerSpot.csv");
        }
        header = new StringBuilder();
        header.append("Title, Date, Type");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".TotalCps");
        }
        header.append("\n");

        if (doWriteReportFiles) {
            Files.write(nuclideCPS_PerSpot.toPath(), header.toString().getBytes(UTF_8));
        }

        refMatFractionsNuclideCPS_PerSpot = new StringBuilder();
        unknownFractionsNuclideCPS_PerSpot = new StringBuilder();
    }

    private void prepRatiosReportFiles(ShrimpFraction shrimpFractionUnknown, ShrimpFraction shrimpFractionRefMat) throws IOException {
        // report Squid_03 headers
        if (doWriteReportFiles) {
            withinSpotRatios_PerScanMinus1 = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "SQUID_03_WithinSpotRatios_PerScanMinus1.csv");
        }

        headerWithinSpotRatios_PerScanMinus1 = new StringBuilder();
        StringBuilder headerWithinSpotRatios_PerScanMinus1_2 = new StringBuilder();

        if (doWriteReportFiles) {
            headerWithinSpotRatios_PerScanMinus1.append("Title, Date, Ndod, Type");
        } else {
            // format for GUI
            headerWithinSpotRatios_PerScanMinus1
                    .append(String.format("%1$-" + 20 + "s", "Title"))
                    .append(String.format("%1$-" + 20 + "s", "Date"))
                    .append(String.format("%1$-" + 10 + "s", "Ndod"))
                    .append(String.format("%1$-" + 15 + "s", "Type"));
            headerWithinSpotRatios_PerScanMinus1_2
                    .append(String.format("%1$-" + 20 + "s", "     "))
                    .append(String.format("%1$-" + 20 + "s", "     "))
                    .append(String.format("%1$-" + 10 + "s", "     "))
                    .append(String.format("%1$-" + 15 + "s", "     "));
        }

        Iterator<SquidRatiosModel> squidRatiosIterator = shrimpFractionUnknown.getIsotopicRatiosII().iterator();
        while (squidRatiosIterator.hasNext()) {
            SquidRatiosModel entry = squidRatiosIterator.next();
            if (entry.isActive()) {
                String displayNameNoSpaces = entry.getDisplayNameNoSpaces().substring(0, Math.min(20, entry.getDisplayNameNoSpaces().length()));
                if (doWriteReportFiles) {
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(displayNameNoSpaces).append(".InterpTime");
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(displayNameNoSpaces).append(".Value");
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(displayNameNoSpaces).append(".1SigmaAbs");
                } else {
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(String.format("%1$-" + 20 + "s", displayNameNoSpaces));
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(String.format("%1$-" + 20 + "s", displayNameNoSpaces));
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(String.format("%1$-" + 20 + "s", displayNameNoSpaces));
                    headerWithinSpotRatios_PerScanMinus1_2.append(", ").append(String.format("%1$-" + 20 + "s", ".InterpTime"));
                    headerWithinSpotRatios_PerScanMinus1_2.append(", ").append(String.format("%1$-" + 20 + "s", ".Value"));
                    headerWithinSpotRatios_PerScanMinus1_2.append(", ").append(String.format("%1$-" + 20 + "s", ".1SigmaAbs"));
                }
            }
        }

        if (doWriteReportFiles) {
            headerWithinSpotRatios_PerScanMinus1.append("\n");
            Files.write(withinSpotRatios_PerScanMinus1.toPath(), headerWithinSpotRatios_PerScanMinus1.toString().getBytes(UTF_8));
        } else {
            // prepare headers for any task expressions
            List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsEvaluated = shrimpFractionUnknown.getTaskExpressionsForScansEvaluated();
            for (TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEval : taskExpressionsEvaluated) {
                String expressionName = taskExpressionEval.getExpression().getName().substring(0, Math.min(20, taskExpressionEval.getExpression().getName().length()));
                if (doWriteReportFiles) {
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(expressionName).append(".Time");
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(expressionName).append(".Value");
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(expressionName).append(".1SigmaAbs");
                } else {
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(String.format("%1$-" + 20 + "s", expressionName));
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(String.format("%1$-" + 20 + "s", expressionName));
                    headerWithinSpotRatios_PerScanMinus1.append(", ").append(String.format("%1$-" + 20 + "s", expressionName));
                    headerWithinSpotRatios_PerScanMinus1_2.append(", ").append(String.format("%1$-" + 20 + "s", ".Time"));
                    headerWithinSpotRatios_PerScanMinus1_2.append(", ").append(String.format("%1$-" + 20 + "s", ".Value"));
                    headerWithinSpotRatios_PerScanMinus1_2.append(", ").append(String.format("%1$-" + 20 + "s", ".1SigmaAbs"));
                }
            }

        }
        headerWithinSpotRatios_PerScanMinus1.append("\n");
        headerWithinSpotRatios_PerScanMinus1_2.append("\n");

        headerWithinSpotRatios_PerScanMinus1.append(headerWithinSpotRatios_PerScanMinus1_2);

        refMatWithinSpotRatios_PerScanMinus1 = new StringBuilder();
        unknownWithinSpotRatios_PerScanMinus1 = new StringBuilder();

        // report squid_04 headers *********************************************
        if (doWriteReportFiles) {
            meanRatios_PerSpot = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "SQUID_04_MeanRatios_PerSpot.csv");
        }
        headerMeanRatios_PerSpot_Unknowns = new StringBuilder();
        StringBuilder headerMeanRatios_PerSpot_Unknowns2 = new StringBuilder();
        headerMeanRatios_PerSpot_RefMat = new StringBuilder();
        StringBuilder headerMeanRatios_PerSpot_RefMat2 = new StringBuilder();
        if (doWriteReportFiles) {
            headerMeanRatios_PerSpot_Unknowns.append("Title, Date, Type");
        } else {
            // format for GUI
            headerMeanRatios_PerSpot_Unknowns
                    .append(String.format("%1$-" + 20 + "s", "Title"))
                    .append(String.format("%1$-" + 20 + "s", "Date"))
                    .append(String.format("%1$-" + 15 + "s", "Type"));
            headerMeanRatios_PerSpot_Unknowns2
                    .append(String.format("%1$-" + 20 + "s", "     "))
                    .append(String.format("%1$-" + 20 + "s", "     "))
                    .append(String.format("%1$-" + 15 + "s", "     "));
        }

        squidRatiosIterator = shrimpFractionUnknown.getIsotopicRatiosII().iterator();
        while (squidRatiosIterator.hasNext()) {
            SquidRatiosModel entry = squidRatiosIterator.next();
            if (entry.isActive()) {
                String displayNameNoSpaces = entry.getDisplayNameNoSpaces().substring(0, Math.min(20, entry.getDisplayNameNoSpaces().length()));
                if (doWriteReportFiles) {
                    headerMeanRatios_PerSpot_Unknowns.append(", ").append(displayNameNoSpaces).append(".MinIndex");
                    headerMeanRatios_PerSpot_Unknowns.append(", ").append(displayNameNoSpaces).append(".Value");
                    headerMeanRatios_PerSpot_Unknowns.append(", ").append(displayNameNoSpaces).append(".1SigmaPct");
                } else {
                    headerMeanRatios_PerSpot_Unknowns.append(", ").append(String.format("%1$-" + 12 + "s", displayNameNoSpaces));
                    headerMeanRatios_PerSpot_Unknowns.append(", ").append(String.format("%1$-" + 20 + "s", displayNameNoSpaces));
                    headerMeanRatios_PerSpot_Unknowns.append(", ").append(String.format("%1$-" + 20 + "s", displayNameNoSpaces));
                    headerMeanRatios_PerSpot_Unknowns2.append(", ").append(String.format("%1$-" + 12 + "s", ".MinIndex"));
                    headerMeanRatios_PerSpot_Unknowns2.append(", ").append(String.format("%1$-" + 20 + "s", ".Value"));
                    headerMeanRatios_PerSpot_Unknowns2.append(", ").append(String.format("%1$-" + 20 + "s", ".1SigmaPct"));
                }
            }
        }

        if (doWriteReportFiles) {
            headerMeanRatios_PerSpot_Unknowns.append("\n");
            Files.write(meanRatios_PerSpot.toPath(), headerMeanRatios_PerSpot_Unknowns.toString().getBytes(UTF_8));
        } else {
            // these expressions are spot-specific with no ratios-of-interest
            // we create two flavors of header - one for unknowns and one for reference materials when not writing file
            // todo : more robust solution
            // currently, this duplicates these outputs since they are stored in the per scan results too
            headerMeanRatios_PerSpot_RefMat.append(headerMeanRatios_PerSpot_Unknowns.toString());
            headerMeanRatios_PerSpot_RefMat2.append(headerMeanRatios_PerSpot_Unknowns2.toString());

            // for unknowns
            Map<ExpressionTreeInterface, double[][]> spotExpressions = shrimpFractionUnknown.getTaskExpressionsEvaluationsPerSpot();
            for (Map.Entry<ExpressionTreeInterface, double[][]> entry : spotExpressions.entrySet()) {
                String expressionName = entry.getKey().getName().substring(0, Math.min(20, entry.getKey().getName().length()));
                if (doWriteReportFiles) {
                    headerMeanRatios_PerSpot_Unknowns.append(", ").append(expressionName).append(".Value");
                    if (((ExpressionTree) entry.getKey()).hasRatiosOfInterest()) {
                        headerMeanRatios_PerSpot_Unknowns.append(", ").append(expressionName).append(".1SigmaPct");
                    }
                } else {
                    headerMeanRatios_PerSpot_Unknowns.append(", ").append(String.format("%1$-" + 20 + "s", expressionName));
                    if (((ExpressionTree) entry.getKey()).hasRatiosOfInterest()) {
                        headerMeanRatios_PerSpot_Unknowns.append(", ").append(String.format("%1$-" + 20 + "s", expressionName));
                    }
                    headerMeanRatios_PerSpot_Unknowns2.append(", ").append(String.format("%1$-" + 20 + "s", ".Value"));
                    if (((ExpressionTree) entry.getKey()).hasRatiosOfInterest()) {
                        headerMeanRatios_PerSpot_Unknowns2.append(", ").append(String.format("%1$-" + 20 + "s", ".1SigmaPct"));
                    }
                }
            }

            headerMeanRatios_PerSpot_Unknowns.append("\n");
            headerMeanRatios_PerSpot_Unknowns2.append("\n");

            headerMeanRatios_PerSpot_Unknowns.append(headerMeanRatios_PerSpot_Unknowns2);

            // for reference materials ********************************************************************************
            spotExpressions = shrimpFractionRefMat.getTaskExpressionsEvaluationsPerSpot();
            for (Map.Entry<ExpressionTreeInterface, double[][]> entry : spotExpressions.entrySet()) {
                String expressionName = entry.getKey().getName().substring(0, Math.min(20, entry.getKey().getName().length()));
                double[] expressionResults = entry.getValue()[0];
                if (doWriteReportFiles) {
                    headerMeanRatios_PerSpot_RefMat.append(", ").append(expressionName).append(".Value");
                    if (expressionResults.length > 1) {//   ((ExpressionTree) entry.getKey()).hasRatiosOfInterest()) {
                        headerMeanRatios_PerSpot_RefMat.append(", ").append(expressionName).append(".1SigmaPct");
                    }
                } else {
                    headerMeanRatios_PerSpot_RefMat.append(", ").append(String.format("%1$-" + 20 + "s", expressionName));
                    if (expressionResults.length > 1) {//   ((ExpressionTree) entry.getKey()).hasRatiosOfInterest()) {
                        headerMeanRatios_PerSpot_RefMat.append(", ").append(String.format("%1$-" + 20 + "s", expressionName));
                    }
                    headerMeanRatios_PerSpot_RefMat2.append(", ").append(String.format("%1$-" + 20 + "s", ".Value"));
                    if (expressionResults.length > 1) {//   ((ExpressionTree) entry.getKey()).hasRatiosOfInterest()) {
                        headerMeanRatios_PerSpot_RefMat2.append(", ").append(String.format("%1$-" + 20 + "s", ".1SigmaPct"));
                    }
                }
            }

        }
        headerMeanRatios_PerSpot_RefMat.append("\n");
        headerMeanRatios_PerSpot_RefMat2.append("\n");

        if (doWriteReportFiles) {
            // do nothing Files.write(meanRatios_PerSpot.toPath(), headerMeanRatios_PerSpot_Unknowns.toString().getBytes(UTF_8));
        } else {
            headerMeanRatios_PerSpot_RefMat.append(headerMeanRatios_PerSpot_RefMat2);
        }

        refMatMeanRatios_PerSpot = new StringBuilder();
        unknownMeanRatios_PerSpot = new StringBuilder();

    }

    private void finishSpeciesReportFiles() throws IOException {
        if (doWriteReportFiles) {
            Files.write(nuclideCPS_PerSpot.toPath(), refMatFractionsNuclideCPS_PerSpot.toString().getBytes(UTF_8), APPEND);
            Files.write(nuclideCPS_PerSpot.toPath(), unknownFractionsNuclideCPS_PerSpot.toString().getBytes(UTF_8), APPEND);
        }
    }

    private void finishRatiosReportFiles() throws IOException {
        if (doWriteReportFiles) {
            Files.write(withinSpotRatios_PerScanMinus1.toPath(), refMatWithinSpotRatios_PerScanMinus1.toString().getBytes(UTF_8), APPEND);
            Files.write(withinSpotRatios_PerScanMinus1.toPath(), unknownWithinSpotRatios_PerScanMinus1.toString().getBytes(UTF_8), APPEND);

            Files.write(meanRatios_PerSpot.toPath(), refMatMeanRatios_PerSpot.toString().getBytes(UTF_8), APPEND);
            Files.write(meanRatios_PerSpot.toPath(), unknownMeanRatios_PerSpot.toString().getBytes(UTF_8), APPEND);
        }
    }

    public static String getFormattedDate(long milliseconds) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        return dateFormat.format(calendar.getTime());
    }

    public enum CalamariReportFlavors {
        MEAN_RATIOS_PER_SPOT_UNKNOWNS,
        MEAN_RATIOS_PER_SPOT_REFERENCEMAT,
        WITHIN_SPOT_RATIOS_UNKNOWNS,
        WITHIN_SPOT_RATIOS_REFERENCEMAT
    }

    public String produceCalamariReportByFlavor(CalamariReportFlavors flavor) {
        StringBuilder report = new StringBuilder();

        switch (flavor) {
            case MEAN_RATIOS_PER_SPOT_UNKNOWNS:
                report.append(headerMeanRatios_PerSpot_Unknowns);
                report.append(unknownMeanRatios_PerSpot);
                break;

            case MEAN_RATIOS_PER_SPOT_REFERENCEMAT:
                report.append(headerMeanRatios_PerSpot_RefMat);
                report.append(refMatMeanRatios_PerSpot);
                break;

            case WITHIN_SPOT_RATIOS_UNKNOWNS:
                report.append(headerWithinSpotRatios_PerScanMinus1);
                report.append(unknownWithinSpotRatios_PerScanMinus1);
                break;

            case WITHIN_SPOT_RATIOS_REFERENCEMAT:
                report.append(headerWithinSpotRatios_PerScanMinus1);
                report.append(refMatWithinSpotRatios_PerScanMinus1);
                break;

            default:
            // throw exception
        }

        return report.toString();
    }

    /**
     * @param aFolderToWriteCalamariReports the folderToWriteCalamariReports to
     * set
     */
    public void setFolderToWriteCalamariReports(File aFolderToWriteCalamariReports) {
        folderToWriteCalamariReports = aFolderToWriteCalamariReports;
    }

    /**
     * @return the folderToWriteCalamariReports
     */
    public File getFolderToWriteCalamariReports() {
        return folderToWriteCalamariReports;
    }

    /**
     * @param nameOfPrawnXMLFile the nameOfPrawnXMLFile to set
     */
    public void setNameOfPrawnXMLFile(String nameOfPrawnXMLFile) {
        this.nameOfPrawnXMLFile = nameOfPrawnXMLFile;
    }

    /**
     * @return the headerMeanRatios_PerSpot_Unknowns
     */
    public StringBuilder getHeaderMeanRatios_PerSpot_Unknowns() {
        return headerMeanRatios_PerSpot_Unknowns;
    }

    /**
     * @return the refMatMeanRatios_PerSpot
     */
    public StringBuilder getRefMatMeanRatios_PerSpot() {
        return refMatMeanRatios_PerSpot;
    }

    /**
     * @return the unknownMeanRatios_PerSpot
     */
    public StringBuilder getUnknownMeanRatios_PerSpot() {
        return unknownMeanRatios_PerSpot;
    }

}
