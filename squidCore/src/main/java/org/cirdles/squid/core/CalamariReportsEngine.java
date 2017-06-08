/*
 * Copyright 2016 CIRDLES
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
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import static java.nio.file.StandardOpenOption.APPEND;
import java.text.SimpleDateFormat;
import static java.util.Arrays.asList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import static org.cirdles.squid.constants.Squid3Constants.DEFAULT_PRAWNFILE_NAME;
import org.cirdles.squid.shrimp.IsotopeRatioModelSHRIMP;
import org.cirdles.squid.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskExpressionEvaluatedPerSpotPerScanModelInterface;
import org.cirdles.ludwig.squid25.Utilities;

/**
 * Calamari's reports engine.
 */
public class CalamariReportsEngine {

    private transient String folderToWriteCalamariReportsPath;
    private transient String reportParameterValues;
    private transient String reportNamePrefix;

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
    private StringBuilder refMatWithinSpotRatios_PerScanMinus1;
    private StringBuilder unknownWithinSpotRatios_PerScanMinus1;
    private StringBuilder refMatMeanRatios_PerSpot;
    private StringBuilder unknownMeanRatios_PerSpot;

    /**
     *
     */
    public CalamariReportsEngine() {
        folderToWriteCalamariReports = new File(System.getProperty("user.dir"));
        nameOfPrawnXMLFile = "";
    }

    /**
     * ReportsEngine to test results
     *
     * @param shrimpFractions the value of shrimpFractions
     * @throws java.io.IOException
     */
    protected void produceReports(List<ShrimpFractionExpressionInterface> shrimpFractions) throws IOException {

        if (shrimpFractions.size() > 0) {
            // gather general info for all runs  from first fraction
            ShrimpFraction firstShrimpFraction = (ShrimpFraction) shrimpFractions.get(0);

            SimpleDateFormat sdfTime = new SimpleDateFormat("yyyyMMdd-HHmmss");

            reportParameterValues
                    = "_" + (firstShrimpFraction.isUseSBM() ? "SBM" : "NOSBM")
                    + "_" + (firstShrimpFraction.isUserLinFits() ? "LINREG" : "SPOTAV");

            if (nameOfPrawnXMLFile.length() >= DEFAULT_PRAWNFILE_NAME.length()) {
                reportNamePrefix = nameOfPrawnXMLFile.substring(0, DEFAULT_PRAWNFILE_NAME.length()) + "_" + reportParameterValues + "_";
            } else {
                reportNamePrefix = nameOfPrawnXMLFile + "_" + reportParameterValues + "_";
            }

            folderToWriteCalamariReportsPath
                    = folderToWriteCalamariReports.getCanonicalPath()
                    + File.separator + nameOfPrawnXMLFile
                    + File.separator + sdfTime.format(new Date())
                    + reportParameterValues
                    + File.separator;
            File reportsFolder = new File(folderToWriteCalamariReportsPath);
            reportsFolder.mkdirs();

            prepSpeciesReportFiles(firstShrimpFraction);
            prepRatiosReportFiles(firstShrimpFraction);

            for (int f = 0; f < shrimpFractions.size(); f++) {
                ShrimpFraction shrimpFraction = (ShrimpFraction) shrimpFractions.get(f);
                shrimpFraction.setSpotNumber(f + 1);
                reportTotalIonCountsAtMass(shrimpFraction);
                reportTotalSBMCountsAtMass(shrimpFraction);
                reportTotalCountsAtTimeStampAndTrimMass(shrimpFraction);
                reportTotalCountsPerSecondPerSpeciesPerAnalysis(shrimpFraction);
                reportWithinSpotRatiosAtInterpolatedTimes(shrimpFraction);
                reportMeanRatiosPerSpot(shrimpFraction);

            } // end of fractions loop

            finishSpeciesReportFiles();
            finishRatiosReportFiles();
        }
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

            Files.write(ionIntegrations_PerScan.toPath(), asList(dataLine), APPEND);
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

            Files.write(sBMIntegrations_PerScan.toPath(), asList(dataLine), APPEND);
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

            Files.write(totalCounts_IonsAndSBM_PerScan.toPath(), asList(dataLine), APPEND);
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

        int nDodCount = shrimpFraction.getIsotopicRatios().entrySet().iterator().next().getValue().getRatEqTime().size();

        for (int nDodNum = 0; nDodNum < nDodCount; nDodNum++) {
            // need to sort by reference material vs unknown
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(nDodNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

            for (Map.Entry<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> entry : shrimpFraction.getIsotopicRatios().entrySet()) {
                IsotopeRatioModelSHRIMP isotopeRatioModel = entry.getValue();
                if (isotopeRatioModel.isActive()) {
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
                }
            }

            // Handle any task expressions
            List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsEvaluated = shrimpFraction.getTaskExpressionsForScansEvaluated();
            for (TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEval : taskExpressionsEvaluated) {
                if (nDodNum < taskExpressionEval.getRatEqTime().length) {
                    dataLine.append(", ").append(String.valueOf(taskExpressionEval.getRatEqTime()[nDodNum]));
                    dataLine.append(", ").append(Utilities.roundedToSize(taskExpressionEval.getRatEqVal()[nDodNum], 15));
                    dataLine.append(", ").append(Utilities.roundedToSize(taskExpressionEval.getRatEqErr()[nDodNum], 15));
                } else {
                    dataLine.append(", ").append("n/a");
                    dataLine.append(", ").append("n/a");
                    dataLine.append(", ").append("n/a");
                }
            }

            dataLine.append("\n");
            if (shrimpFraction.isReferenceMaterial()) {
                refMatWithinSpotRatios_PerScanMinus1.append(dataLine);
            } else {
                unknownWithinSpotRatios_PerScanMinus1.append(dataLine);
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
        dataLine.append(shrimpFraction.getFractionID()).append(", ");
        dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
        dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

        for (Map.Entry<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> entry : shrimpFraction.getIsotopicRatios().entrySet()) {
            IsotopeRatioModelSHRIMP isotopeRatioModel = entry.getValue();
            if (isotopeRatioModel.isActive()) {
                // April 2017 rounding was performed on calculated numbers
                dataLine.append(", ").append(String.valueOf(isotopeRatioModel.getMinIndex()));
                dataLine.append(", ").append(Utilities.roundedToSize(isotopeRatioModel.getRatioVal(), 12));
                dataLine.append(", ").append(Utilities.roundedToSize(isotopeRatioModel.getRatioFractErrAsOneSigmaPercent(), 12));
            }
        }

        // Handle any task expressions that we calculated per scan with a summary value per spot = Squid Switch "NU"
        List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsEvaluated = shrimpFraction.getTaskExpressionsForScansEvaluated();
        for (TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEval : taskExpressionsEvaluated) {
            dataLine.append(", ").append(Utilities.roundedToSize(taskExpressionEval.getRatioVal(), 12));
            dataLine.append(", ").append(Utilities.roundedToSize(taskExpressionEval.getRatioFractErr() * 100.0, 12));
        }

//        System.out.println("\n" + shrimpFraction.getFractionID() + "********************");
//        for (Map.Entry<String, double[][]> entry : shrimpFraction.getTaskExpressionsEvaluationsPerSpot().entrySet()) {
//            String expressionName = entry.getKey();
//            double[] expressionResults = entry.getValue()[0];
//
//            System.out.print(expressionName + "\t");
//            for (int i = 0; i < expressionResults.length; i++) {
//                System.out.print("\t" + rounded(expressionResults[i]));
//            }
//            System.out.println();
//        }
        dataLine.append("\n");
        if (shrimpFraction.isReferenceMaterial()) {
            refMatMeanRatios_PerSpot.append(dataLine);
        } else {
            unknownMeanRatios_PerSpot.append(dataLine);
        }
    }

    private void prepSpeciesReportFiles(ShrimpFraction shrimpFraction) throws IOException {
        String[] namesOfSpecies = shrimpFraction.getNamesOfSpecies();
        int countOfIntegrations = shrimpFraction.getPeakMeasurementsCount();

        ionIntegrations_PerScan = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "Check_01_IonIntegrations_PerScan.csv");
        StringBuilder header = new StringBuilder();
        header.append("Title, Date, Scan, Type, Dead_time_ns");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".count_time_sec");
            for (int i = 0; i < countOfIntegrations; i++) {
                header.append(", ").append(nameOfSpecies).append(".").append(String.valueOf(i + 1));
            }
        }
        header.append("\n");

        Files.write(ionIntegrations_PerScan.toPath(), header.toString().getBytes(UTF_8));

        sBMIntegrations_PerScan = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "Check_02_SBMIntegrations_PerScan.csv");
        header = new StringBuilder();
        header.append("Title, Date, Scan, Type, SBM_zero_cps");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".count_time_sec");
            for (int i = 0; i < countOfIntegrations; i++) {
                header.append(", ").append(nameOfSpecies).append(".SBM.").append(String.valueOf(i + 1));
            }
        }
        header.append("\n");

        Files.write(sBMIntegrations_PerScan.toPath(), header.toString().getBytes(UTF_8));

        totalCounts_IonsAndSBM_PerScan = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "SQUID_01_TotalCounts_IonsAndSBM_PerScan.csv");
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

        Files.write(totalCounts_IonsAndSBM_PerScan.toPath(), header.toString().getBytes(UTF_8));

        nuclideCPS_PerSpot = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "SQUID_02_NuclideCPS_PerSpot.csv");
        header = new StringBuilder();
        header.append("Title, Date, Type");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".TotalCps");
        }
        header.append("\n");

        Files.write(nuclideCPS_PerSpot.toPath(), header.toString().getBytes(UTF_8));

        refMatFractionsNuclideCPS_PerSpot = new StringBuilder();
        unknownFractionsNuclideCPS_PerSpot = new StringBuilder();
    }

    private void prepRatiosReportFiles(ShrimpFraction shrimpFraction) throws IOException {
        withinSpotRatios_PerScanMinus1 = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "SQUID_03_WithinSpotRatios_PerScanMinus1.csv");
        StringBuilder header = new StringBuilder();
        header.append("Title, Date, Ndod, Type");

        for (Map.Entry<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> entry : shrimpFraction.getIsotopicRatios().entrySet()) {
            if (entry.getValue().isActive()) {
                header.append(", ").append(entry.getKey().getDisplayNameNoSpaces()).append(".InterpTime");
                header.append(", ").append(entry.getKey().getDisplayNameNoSpaces()).append(".Value");
                header.append(", ").append(entry.getKey().getDisplayNameNoSpaces()).append(".1SigmaAbs");
            }
        }

        // prepare headers for any task expressions
        List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsEvaluated = shrimpFraction.getTaskExpressionsForScansEvaluated();
        for (TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEval : taskExpressionsEvaluated) {
            String expressionName = taskExpressionEval.getExpression().getName();
            header.append(", ").append(expressionName).append(".Time");
            header.append(", ").append(expressionName).append(".Value");
            header.append(", ").append(expressionName).append(".1SigmaAbs");
        }

        header.append("\n");

        Files.write(withinSpotRatios_PerScanMinus1.toPath(), header.toString().getBytes(UTF_8));

        refMatWithinSpotRatios_PerScanMinus1 = new StringBuilder();
        unknownWithinSpotRatios_PerScanMinus1 = new StringBuilder();

        meanRatios_PerSpot = new File(folderToWriteCalamariReportsPath + reportNamePrefix + "SQUID_04_MeanRatios_PerSpot.csv");
        header = new StringBuilder();
        header.append("Title, Date, Type");

        for (Map.Entry<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> entry : shrimpFraction.getIsotopicRatios().entrySet()) {
            if (entry.getValue().isActive()) {
                header.append(", ").append(entry.getKey().getDisplayNameNoSpaces()).append(".MinIndex");
                header.append(", ").append(entry.getKey().getDisplayNameNoSpaces()).append(".Value");
                header.append(", ").append(entry.getKey().getDisplayNameNoSpaces()).append(".1SigmaPct");
            }
        }

        // prepare headers for any task expressions
        for (TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEval : taskExpressionsEvaluated) {
            String expressionName = taskExpressionEval.getExpression().getName();
            header.append(", ").append(expressionName).append(".Value");
            header.append(", ").append(expressionName).append(".1SigmaPct");
        }

        header.append("\n");

        Files.write(meanRatios_PerSpot.toPath(), header.toString().getBytes(UTF_8));

        refMatMeanRatios_PerSpot = new StringBuilder();
        unknownMeanRatios_PerSpot = new StringBuilder();

    }

    private void finishSpeciesReportFiles() throws IOException {
        Files.write(nuclideCPS_PerSpot.toPath(), refMatFractionsNuclideCPS_PerSpot.toString().getBytes(UTF_8), APPEND);
        Files.write(nuclideCPS_PerSpot.toPath(), unknownFractionsNuclideCPS_PerSpot.toString().getBytes(UTF_8), APPEND);
    }

    private void finishRatiosReportFiles() throws IOException {
        Files.write(withinSpotRatios_PerScanMinus1.toPath(), refMatWithinSpotRatios_PerScanMinus1.toString().getBytes(UTF_8), APPEND);
        Files.write(withinSpotRatios_PerScanMinus1.toPath(), unknownWithinSpotRatios_PerScanMinus1.toString().getBytes(UTF_8), APPEND);

        Files.write(meanRatios_PerSpot.toPath(), refMatMeanRatios_PerSpot.toString().getBytes(UTF_8), APPEND);
        Files.write(meanRatios_PerSpot.toPath(), unknownMeanRatios_PerSpot.toString().getBytes(UTF_8), APPEND);
    }

    private String getFormattedDate(long milliseconds) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        return dateFormat.format(calendar.getTime());
    }

    /**
     * @return the folderToWriteCalamariReports
     */
    public File getFolderToWriteCalamariReports() {
        return folderToWriteCalamariReports;
    }

    /**
     * @param aFolderToWriteCalamariReports the folderToWriteCalamariReports to
     * set
     */
    public void setFolderToWriteCalamariReports(File aFolderToWriteCalamariReports) {
        folderToWriteCalamariReports = aFolderToWriteCalamariReports;
    }

    /**
     * @param nameOfPrawnXMLFile the nameOfPrawnXMLFile to set
     */
    public void setNameOfPrawnXMLFile(String nameOfPrawnXMLFile) {
        this.nameOfPrawnXMLFile = nameOfPrawnXMLFile;
    }

    /**
     * @return the folderToWriteCalamariReportsPath
     */
    public String getFolderToWriteCalamariReportsPath() {
        return folderToWriteCalamariReportsPath;
    }

}
