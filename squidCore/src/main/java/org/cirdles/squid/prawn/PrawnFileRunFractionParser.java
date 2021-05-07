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
package org.cirdles.squid.prawn;

import org.cirdles.ludwig.squid25.SquidMathUtils;
import org.cirdles.ludwig.squid25.Utilities;
import org.cirdles.squid.algorithms.PoissonLimitsCountLessThanEqual100;
import org.cirdles.squid.algorithms.weightedMeans.WtdLinCorrResults;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_ERROR_VALUE;
import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_TINY_VALUE;
import static org.cirdles.squid.algorithms.weightedMeans.WeightedMeanCalculators.wtdLinCorr;
import static org.cirdles.squid.utilities.conversionUtilities.RoundingUtilities.squid3RoundedToSize;

/**
 * Parses run fractions from Prawn files into
 * {@link org.cirdles.squid.shrimp.ShrimpFraction}s.
 */
public class PrawnFileRunFractionParser {

    private String fractionID;
    private long dateTimeMilliseconds;
    private double[][] totalCounts;
    private double[][] totalCountsOneSigmaAbs;
    private double[][] totalCountsSBM;
    private int[][] rawPeakData;
    private int[][] rawSBMData;
    private int nSpecies;
    private int nScans;
    private int peakMeasurementsCount;
    private int deadTimeNanoseconds;
    private int sbmZeroCps;
    private List<PrawnFile.Run.RunTable.Entry> runTableEntries;
    private List<PrawnFile.Run.Set.Scan> scans;
    private double[] countTimeSec;
    private String[] namesOfSpecies;
    private double[][] timeStampSec;
    private double[][] trimMass;
    private double[][] pkNetCps;
    private double[][] sbmCps;
    private double[][] pkFerr;
    private double[] totalCps;
    private SortedSet<SquidRatiosModel> isotopicRatiosII;

    private double[][] reducedPkHt;
    private double[][] reducedPkHtFerr;

    private int stageX;
    private int stageY;
    private int stageZ;
    private int qt1Y;
    private int qt1Z;
    private double primaryBeam;

    /**
     *
     */
    public PrawnFileRunFractionParser() {
        this.dateTimeMilliseconds = 0l;
    }

    /**
     * @param runFraction
     * @param useSBM
     * @param userLinFits
     * @param indexOfBackgroundSpecies
     * @param referenceMaterialLetter
     * @return the org.cirdles.squid.shrimp.ShrimpFraction
     */
    public ShrimpFraction processRunFraction(PrawnFile.Run runFraction, boolean useSBM, boolean userLinFits, int indexOfBackgroundSpecies, String referenceMaterialLetter) {
        SquidSessionModel squidSessionSpecs = new SquidSessionModel(
                null, null, useSBM, userLinFits, indexOfBackgroundSpecies, referenceMaterialLetter, "", new HashMap<>());

        return processRunFraction(runFraction, squidSessionSpecs);
    }

    /**
     * @param runFraction
     * @param squidSessionSpecs
     * @return
     */
    public ShrimpFraction processRunFraction(Run runFraction, SquidSessionModel squidSessionSpecs) {

        boolean useSBM = squidSessionSpecs.isUseSBM();
        boolean userLinFits = squidSessionSpecs.isUserLinFits();
        int indexOfBackgroundSpecies = squidSessionSpecs.getIndexOfBackgroundSpecies();
        String referenceMaterialNameFilter = squidSessionSpecs.getReferenceMaterialNameFilter();
        String concentrationReferenceMaterialNameFilter = squidSessionSpecs.getConcentrationReferenceMaterialNameFilter();

        ShrimpFraction shrimpFraction = null;
        prepareRunFractionMetaData(runFraction, squidSessionSpecs);
        if (nScans > 1) {
            parseRunFractionData();
            int countOfNonPositiveSBMCounts = 0;
            try {
                calculateTotalPerSpeciesCPS(indexOfBackgroundSpecies);
                calculateIsotopicRatios(useSBM, userLinFits);
                countOfNonPositiveSBMCounts = calculateInterpolatedPeakHeights();
            } catch (Exception e) {
            }

            shrimpFraction = new ShrimpFraction(fractionID, isotopicRatiosII);
            shrimpFraction.setDateTimeMilliseconds(dateTimeMilliseconds);
            shrimpFraction.setDeadTimeNanoseconds(deadTimeNanoseconds);
            shrimpFraction.setSbmZeroCps(sbmZeroCps);
            shrimpFraction.setStageX(stageX);
            shrimpFraction.setStageY(stageY);
            shrimpFraction.setStageZ(stageZ);
            shrimpFraction.setQt1Y(qt1Y);
            shrimpFraction.setQt1Z(qt1Z);
            shrimpFraction.setPrimaryBeam(primaryBeam);

            shrimpFraction.setCountTimeSec(countTimeSec);
            shrimpFraction.setNamesOfSpecies(namesOfSpecies);
            shrimpFraction.setPeakMeasurementsCount(peakMeasurementsCount);
            shrimpFraction.setTotalCounts(totalCounts);
            shrimpFraction.setTotalCountsOneSigmaAbs(totalCountsOneSigmaAbs);
            shrimpFraction.setTotalCountsSBM(totalCountsSBM);
            shrimpFraction.setTimeStampSec(timeStampSec);
            shrimpFraction.setTrimMass(trimMass);
            shrimpFraction.setRawPeakData(rawPeakData);
            shrimpFraction.setRawSBMData(rawSBMData);
            if (totalCps != null) {
                shrimpFraction.setTotalCps(totalCps);
            }
            shrimpFraction.setNetPkCps(pkNetCps);
            shrimpFraction.setPkFerr(pkFerr);
            shrimpFraction.setUseSBM(useSBM);
            shrimpFraction.setUserLinFits(userLinFits);
            if (reducedPkHt != null) {
                shrimpFraction.setReducedPkHt(reducedPkHt);
                shrimpFraction.setReducedPkHtFerr(reducedPkHtFerr);
            }

            shrimpFraction.setCountOfNonPositiveSBMCounts(countOfNonPositiveSBMCounts);

            // determine reference material status
            if (referenceMaterialNameFilter.length() > 0) {
                if (fractionID.toUpperCase(Locale.US).startsWith(referenceMaterialNameFilter.toUpperCase(Locale.ENGLISH))) {
                    shrimpFraction.setReferenceMaterial(true);
                }
            }

            // determine concentration reference material status
            if (concentrationReferenceMaterialNameFilter.length() > 0) {
                if (fractionID.toUpperCase(Locale.US).startsWith(concentrationReferenceMaterialNameFilter.toUpperCase(Locale.ENGLISH))) {
                    shrimpFraction.setConcentrationReferenceMaterial(true);
                }
            }

        }
        return shrimpFraction;
    }

    private void prepareRunFractionMetaData(PrawnFile.Run runFraction, SquidSessionModel squidSessionSpecs) {
        fractionID = runFraction.getPar().get(0).getValue();
        nSpecies = Integer.parseInt(runFraction.getPar().get(2).getValue());
        nScans = Integer.parseInt(runFraction.getPar().get(3).getValue());
        deadTimeNanoseconds = Integer.parseInt(runFraction.getPar().get(4).getValue());
        sbmZeroCps = Integer.parseInt(runFraction.getPar().get(5).getValue());
        if (runFraction.getPar().size() > 12) {
            stageX = Integer.parseInt(runFraction.getPar().get(10).getValue());
            stageY = Integer.parseInt(runFraction.getPar().get(11).getValue());
            stageZ = Integer.parseInt(runFraction.getPar().get(12).getValue());
        } else {
            stageX = 0;
            stageY = 0;
            stageZ = 0;
        }

        runTableEntries = runFraction.getRunTable().getEntry();
        scans = runFraction.getSet().getScan();
        String[] firstIntegrations = runFraction.getSet().getScan().get(0).getMeasurement().get(0).getData().get(0).getValue().split(",");
        peakMeasurementsCount = firstIntegrations.length;

        String dateTime = runFraction.getSet().getPar().get(0).getValue()
                + " " + runFraction.getSet().getPar().get(1).getValue()
                + (Integer.parseInt(runFraction.getSet().getPar().get(1).getValue().substring(0, 2)) < 12 ? " AM" : " PM");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
        try {
            dateTimeMilliseconds = dateFormat.parse(dateTime).getTime();
        } catch (ParseException parseException) {
        }

        // need to search entries due to variation in files
        for (int i = 0; i < runFraction.getSet().getPar().size(); i++) {
            try {
                if (runFraction.getSet().getPar().get(i).getName().compareTo(SetParameterNames.QT_1_Y) == 0) {
                    qt1Y = Integer.parseInt(runFraction.getSet().getPar().get(i).getValue());
                }
                if (runFraction.getSet().getPar().get(i).getName().compareTo(SetParameterNames.QT_1_Z) == 0) {
                    qt1Z = Integer.parseInt(runFraction.getSet().getPar().get(i).getValue());
                }
                if (runFraction.getSet().getPar().get(i).getName().compareTo(SetParameterNames.PBM) == 0) {
                    primaryBeam = Double.parseDouble(runFraction.getSet().getPar().get(i).getValue().replace("nA", ""));
                }
            } catch (NumberFormatException numberFormatException) {
                // keep going
            }
        }

        namesOfSpecies = new String[nSpecies];
        if (squidSessionSpecs.getSquidSpeciesModelList().isEmpty()) {
            // back compatible
            for (int i = 0; i < nSpecies; i++) {
                namesOfSpecies[i] = runTableEntries.get(i).getPar().get(0).getValue();
            }
        } else {
            namesOfSpecies = squidSessionSpecs.getSquidSpeciesMassStationNames();
        }

        countTimeSec = new double[nSpecies];
        for (int i = 0; i < nSpecies; i++) {
            countTimeSec[i] = Double.parseDouble(runTableEntries.get(i).getPar().get(4).getValue());
        }

        timeStampSec = new double[nScans][nSpecies];
        trimMass = new double[nScans][nSpecies];
        pkNetCps = new double[nScans][nSpecies];
        sbmCps = new double[nScans][nSpecies];
        pkFerr = new double[nScans][nSpecies];

        isotopicRatiosII = squidSessionSpecs.produceRatiosCopySortedSet();
    }

    /**
     * Returns 2D Double array of converted raw data Based on Simon Bodorkos
     * email 4.Feb.2016 interpretation of Squid code Corrected per Phil Main
     * 9.Feb.2016 email
     *
     * @return
     */
    private void parseRunFractionData() {

        totalCounts = new double[nScans][nSpecies];
        totalCountsOneSigmaAbs = new double[nScans][nSpecies];
        totalCountsSBM = new double[nScans][nSpecies];
        rawPeakData = new int[nScans][nSpecies * peakMeasurementsCount];
        rawSBMData = new int[nScans][nSpecies * peakMeasurementsCount];

        for (int scanNum = 0; scanNum < nScans; scanNum++) {
            // there is one measurement per mass per scanNum
            List<PrawnFile.Run.Set.Scan.Measurement> measurements = scans.get(scanNum).getMeasurement();
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
                // record the time_stamp_sec
                timeStampSec[scanNum][speciesMeasurementIndex]
                        = Double.parseDouble(measurements.get(speciesMeasurementIndex).getPar().get(2).getValue());
                // record the trim_mass
                trimMass[scanNum][speciesMeasurementIndex]
                        = Double.parseDouble(measurements.get(speciesMeasurementIndex).getPar().get(1).getValue());

                // Bodorkos via Ludwig call for trimTime here, but it is an easy on-the-fly calcuation:
                // ***TrimTime[j, k] = (StartTime + time_stamp_sec[j, k]) / 3600***
                // handle peakMeasurements measurements
                String[] peakMeasurementsRaw = measurements.get(speciesMeasurementIndex).getData().get(0).getValue().split(",");
                // Jan 2019 Handling OP files means that the total counts and SBM are present as single negative integers               

                double median;
                double totalCountsPeak;
                double totalCountsSigma;

                if (Double.parseDouble(peakMeasurementsRaw[0]) < 0) {
                    // we are processing OP file per Bodorkos email Oct 11 2018
                    totalCountsPeak = StrictMath.abs(Double.parseDouble(peakMeasurementsRaw[0]));
                    totalCountsSigma = StrictMath.sqrt(totalCountsPeak);

                } else {
                    double[] peakMeasurements = new double[peakMeasurementsCount];
                    for (int i = 0; i < peakMeasurementsCount; i++) {
                        peakMeasurements[i] = Double.parseDouble(peakMeasurementsRaw[i]);
                        rawPeakData[scanNum][speciesMeasurementIndex + speciesMeasurementIndex * (peakMeasurementsCount - 1) + i] = (int) peakMeasurements[i];
                    }
                    median = Utilities.median(peakMeasurements);

                    if (median > 100.0) {
                        double[] peakTukeysMeanAndUnct = SquidMathUtils.tukeysBiweight(peakMeasurements, 9.0);

                        // BV is variable used by Ludwig for Tukey Mean fo peak measurements
                        double bV = peakTukeysMeanAndUnct[0];
                        double bVcps = bV * peakMeasurementsCount / countTimeSec[speciesMeasurementIndex];
                        double bVcpsDeadTime = bVcps / (1.0 - bVcps * deadTimeNanoseconds / 1E9);

                        totalCountsPeak = bVcpsDeadTime * countTimeSec[speciesMeasurementIndex];

                        double countsSigmaCandidate = StrictMath.max(peakTukeysMeanAndUnct[1], StrictMath.sqrt(bV));
                        totalCountsSigma = countsSigmaCandidate / StrictMath.sqrt(peakMeasurementsCount) * bVcps * countTimeSec[speciesMeasurementIndex] / bV;

                    } else if (median >= 0.0) {

                        // remove the one element with first occurrence of largest residual if any.
                        int maxResidualIndex = PoissonLimitsCountLessThanEqual100.determineIndexOfValueWithLargestResidual(median, peakMeasurements);
                        double sumX = 0.0;
                        double sumXsquared = 0.0;
                        for (int i = 0; i < peakMeasurementsCount; i++) {
                            if (i != maxResidualIndex) {
                                sumX += peakMeasurements[i];
                                sumXsquared += peakMeasurements[i] * peakMeasurements[i];
                            }
                        }

                        int countIncludedIntegrations = (maxResidualIndex == -1) ? peakMeasurementsCount : peakMeasurementsCount - 1;
                        double peakMeanCounts = sumX / countIncludedIntegrations;
                        double poissonSigma = StrictMath.sqrt(peakMeanCounts);
                        double sigmaPeakCounts = StrictMath.sqrt((sumXsquared - (sumX * sumX / countIncludedIntegrations)) / (countIncludedIntegrations - 1));

                        double peakCountsPerSecond = peakMeanCounts * peakMeasurementsCount / countTimeSec[speciesMeasurementIndex];
                        double peakCountsPerSecondDeadTime = peakCountsPerSecond / (1.0 - peakCountsPerSecond * deadTimeNanoseconds / 1E9);

                        totalCountsPeak = peakCountsPerSecondDeadTime * countTimeSec[speciesMeasurementIndex];

                        totalCountsSigma = 0.0;
                        if (peakMeanCounts > 0.0) {
                            totalCountsSigma
                                    = StrictMath.max(sigmaPeakCounts, poissonSigma) / StrictMath.sqrt(countIncludedIntegrations) * peakCountsPerSecond * countTimeSec[speciesMeasurementIndex] / peakMeanCounts;
                        }

                    } else {
                        // set flag as this should be impossible for count data
                        totalCountsPeak = -1.0;
                        totalCountsSigma = -1.0;
                    }

                } // end of decision regular Prawn vs OP

                totalCounts[scanNum][speciesMeasurementIndex] = totalCountsPeak;
                totalCountsOneSigmaAbs[scanNum][speciesMeasurementIndex] = totalCountsSigma;

                // handle SBM measurements
                String[] sbmMeasurementsRaw = measurements.get(speciesMeasurementIndex).getData().get(1).getValue().split(",");
                // Jan 2019 Handling OP files means that the total counts and SBM are present as single negative integers
                double totalCountsSpeciesSBM;

                if (Double.parseDouble(sbmMeasurementsRaw[0]) < 0) {
                    // we are processing OP file per Bodorkos email Oct 11 2018
                    totalCountsSpeciesSBM = StrictMath.abs(Double.parseDouble(sbmMeasurementsRaw[0]));
                } else {

                    int sbmMeasurementsCount = sbmMeasurementsRaw.length;
                    double[] sbm = new double[sbmMeasurementsCount];
                    for (int i = 0; i < sbmMeasurementsCount; i++) {
                        sbm[i] = Double.parseDouble(sbmMeasurementsRaw[i]);
                        rawSBMData[scanNum][speciesMeasurementIndex + speciesMeasurementIndex * (sbmMeasurementsCount - 1) + i] = (int) sbm[i];
                    }
                    double[] sbmTukeysMeanAndUnct = SquidMathUtils.tukeysBiweight(sbm, 6.0);
                    totalCountsSpeciesSBM = sbmMeasurementsCount * sbmTukeysMeanAndUnct[0];
                } // end of decision regular Prawn vs OP

                totalCountsSBM[scanNum][speciesMeasurementIndex] = totalCountsSpeciesSBM;
            }
        }
    }

    private void calculateTotalPerSpeciesCPS(int indexOfBackgroundSpecies) {
        // Calculate Total CPS per Species = Step 2 of Development for SHRIMP
        // (see wiki: https://github.com/CIRDLES/ET_Redux/wiki/Development-for-SHRIMP:-Step-2)

        double[][] pkCps = new double[nScans][nSpecies];
        double[] backgroundCpsArray = new double[nScans];

        double sumBackgroundCps = 0.0;
        for (int scanNum = 0; scanNum < nScans; scanNum++) {
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
                // calculate PeakCps
                pkCps[scanNum][speciesMeasurementIndex] = totalCounts[scanNum][speciesMeasurementIndex] / countTimeSec[speciesMeasurementIndex];
                // calculate corrected (by sbmZeroCps) SBMCps
                sbmCps[scanNum][speciesMeasurementIndex] = (totalCountsSBM[scanNum][speciesMeasurementIndex] / countTimeSec[speciesMeasurementIndex]) - sbmZeroCps;

                if (speciesMeasurementIndex == indexOfBackgroundSpecies) {
                    backgroundCpsArray[scanNum] = pkCps[scanNum][speciesMeasurementIndex];
                    sumBackgroundCps += pkCps[scanNum][speciesMeasurementIndex];
                }
            }
        }

        // determine backgroundCps if background species exists
        double backgroundCps = 0.0;
        if (indexOfBackgroundSpecies >= 0) {
            backgroundCps = sumBackgroundCps / nScans;

            if (backgroundCps >= 10.0) {
                // recalculate
                backgroundCps = SquidMathUtils.tukeysBiweight(backgroundCpsArray, 9.0)[0];
            }
        }

        // background correct the peaks with fractional error and calculate total cps for peaks
        double[] sumOfCorrectedPeaks = new double[nSpecies];
        for (int scanNum = 0; scanNum < nScans; scanNum++) {
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
                if (speciesMeasurementIndex != indexOfBackgroundSpecies) {

                    // July 2020 bug fix for no background case: indexOfBackgroundSpecies = -1
                    int operativeIndexOfBackgroundSpecies = (indexOfBackgroundSpecies < 0) ? speciesMeasurementIndex : indexOfBackgroundSpecies;
                    // correct PeakCps to NetPkCps
                    pkNetCps[scanNum][speciesMeasurementIndex] = pkCps[scanNum][speciesMeasurementIndex] - backgroundCps;
                    sumOfCorrectedPeaks[speciesMeasurementIndex] += pkNetCps[scanNum][speciesMeasurementIndex];
                    // calculate fractional error
                    double absNetPeakCps = pkNetCps[scanNum][speciesMeasurementIndex];
                    if (absNetPeakCps > 1.0e-6) {
                        double calcVariance
                                = totalCounts[scanNum][speciesMeasurementIndex]//
                                + (StrictMath.abs(backgroundCps) * StrictMath.pow(countTimeSec[speciesMeasurementIndex] / countTimeSec[operativeIndexOfBackgroundSpecies], 2));
                        pkFerr[scanNum][speciesMeasurementIndex]
                                = StrictMath.sqrt(calcVariance) / absNetPeakCps / countTimeSec[speciesMeasurementIndex];
                    } else {
                        pkFerr[scanNum][speciesMeasurementIndex] = 1.0;
                    }
                }
            }
        }

        totalCps = new double[nSpecies];
        for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
            // calculate total cps
            // this has the effect of setting totalCps[backgroundIndex] to backgroundCps
            //modified April 2017 to round to 12 sig digits using half-up            
            double originalTotalCPS = (sumOfCorrectedPeaks[speciesMeasurementIndex] / nScans) + backgroundCps;
            totalCps[speciesMeasurementIndex] = squid3RoundedToSize(originalTotalCPS, 12);
        }
    }

    /**
     * @param useSBM      the value of useSBM
     * @param userLinFits the value of userLinFits
     */
    private void calculateIsotopicRatios(boolean useSBM, boolean userLinFits) {
        // Step 3 of Development for SHRIMP
        // (see wiki: https://github.com/CIRDLES/ET_Redux/wiki/Development-for-SHRIMP:-Step-3)
        // walk the ratios
        // April 2017 per Bodorkos
        int sigFigs = 12;

        isotopicRatiosII.forEach((isotopicRatioModel) -> {
            int nDod = nScans - 1;
            int NUM = isotopicRatioModel.getNumerator().getMassStationIndex();
            int DEN = isotopicRatioModel.getDenominator().getMassStationIndex();

            // test that ratio is legal
            if ((DEN < nSpecies) && (NUM < nSpecies)) {
                isotopicRatioModel.setActive(true);
                int aOrd = (DEN > NUM) ? NUM : DEN;
                int bOrd = (DEN > NUM) ? DEN : NUM;

                double totCtsNUM = 0.0;
                double totCtsDEN = 0.0;

                for (int j = 0; j < nScans; j++) {
                    totCtsNUM += pkNetCps[j][NUM] * countTimeSec[NUM];
                    totCtsDEN += pkNetCps[j][DEN] * countTimeSec[DEN];
                }

                double ratioVal;
                double ratioFractErr;
                double[] ratioInterpTime;
                double[] interpRatVal;
                double[] ratValFerr;
                double[] ratValSig;
                double[][] sigRho;
                boolean[] zerPkCt;

                List<Double> ratEqTime = new ArrayList<>();
                List<Double> ratEqVal = new ArrayList<>();
                List<Double> ratEqErr = new ArrayList<>();

                if ((totCtsNUM < 32) || (totCtsDEN < 32) || (nDod == 0)) {
                    ratioFractErr = 1.0;
                    if (totCtsNUM == 0.0) {
                        ratioVal = SQUID_TINY_VALUE;
                    } else if (totCtsDEN == 0.0) {
                        ratioVal = 1e16;
                    } else {
                        ratioVal = (totCtsNUM / countTimeSec[NUM]) / (totCtsDEN / countTimeSec[DEN]);
                        ratioFractErr = StrictMath.sqrt((1.0 / StrictMath.abs(totCtsNUM)) + (1.0 / StrictMath.abs(totCtsDEN)));
                    }

                    ratioInterpTime = new double[]{//
                            0.5 * (StrictMath.min(timeStampSec[0][NUM], timeStampSec[0][DEN]) + StrictMath.max(timeStampSec[nScans - 1][NUM], timeStampSec[nScans - 1][DEN]))
                    };

                    isotopicRatioModel.setRatioVal(squid3RoundedToSize(ratioVal, sigFigs));
                    isotopicRatioModel.setRatioFractErr(squid3RoundedToSize(ratioFractErr, sigFigs));

                    ratEqTime.add(ratioInterpTime[0]);
                    ratEqVal.add(ratioVal);
                    ratEqErr.add(StrictMath.abs(ratioFractErr * ratioVal));

                    // flush out for reports to handle empty entries
                    for (int i = 0; i < (nDod - 1); i++) {
                        ratEqTime.add(0.0);
                        ratEqVal.add(0.0);
                        ratEqErr.add(0.0);
                    }

                } else {
                    // main treatment using double interpolation following Dodson (1978): http://dx.doi.org/10.1088/0022-3735/11/4/004)
                    double[] pkF = new double[nDod];
                    double sumPkF = 0.0;
                    for (int j = 0; j < nDod; j++) {
                        pkF[j] = (timeStampSec[j][bOrd] - timeStampSec[j][aOrd]) / (timeStampSec[j + 1][aOrd] - timeStampSec[j][aOrd]);
                        sumPkF += pkF[j];
                    }

                    double avPkF = sumPkF / nDod;
                    double f1 = (1.0 - avPkF) / 2.0;
                    double f2 = (1.0 + avPkF) / 2.0;
                    double rhoIJ;// = (1.0 - avPkF * avPkF) / (1.0 + avPkF * avPkF) / 2.0;

                    ratioInterpTime = new double[nDod];
                    interpRatVal = new double[nDod];
                    ratValFerr = new double[nDod];
                    ratValSig = new double[nDod];
                    sigRho = new double[nDod][nDod];
                    zerPkCt = new boolean[nScans];

                    int rct = -1;

                    for (int sNum = 0; sNum < nDod; sNum++) {
                        boolean continueWithScanProcessing = true;
                        int sn1 = sNum + 1;
                        double totT = timeStampSec[sNum][aOrd] + timeStampSec[sNum][bOrd]
                                + timeStampSec[sn1][aOrd] + timeStampSec[sn1][bOrd];
                        double meanT = totT / 4.0;
                        ratioInterpTime[sNum] = meanT;

                        zerPkCt[sNum] = false;
                        zerPkCt[sn1] = false;

                        double[] aPkCts = new double[2];
                        double[] bPkCts = new double[2];
                        for (int numDenom = 0; numDenom < 2; numDenom++) {
                            if (continueWithScanProcessing) {
                                int k = sNum + numDenom;
                                double aNetCPS = pkNetCps[k][aOrd];
                                double bNetCPS = pkNetCps[k][bOrd];

                                if ((aNetCPS == SQUID_ERROR_VALUE) || (bNetCPS == SQUID_ERROR_VALUE)) {
                                    zerPkCt[k] = true;
                                    continueWithScanProcessing = false;
                                }

                                if (continueWithScanProcessing) {
                                    aPkCts[numDenom] = aNetCPS * countTimeSec[aOrd];
                                    bPkCts[numDenom] = bNetCPS * countTimeSec[bOrd];

                                    if (useSBM) {
                                        if ((sbmCps[k][aOrd] <= 0.0) || (sbmCps[k][aOrd] == SQUID_ERROR_VALUE)
                                                || (sbmCps[k][bOrd] <= 0.0) || (sbmCps[k][aOrd] == SQUID_ERROR_VALUE)) {
                                            zerPkCt[k] = true;
                                            continueWithScanProcessing = false;
                                        }
                                    }
                                }
                            } // test continueWithScanProcessing
                        } // iteration through numDenom

                        if (continueWithScanProcessing) {
                            for (int k = 0; k < 2; k++) {
                                int numDenom = (k == 0) ? 1 : 0;

                                double a = aPkCts[k];
                                double b = aPkCts[numDenom];
                                if ((a <= 0) && (b > 16)) {
                                    zerPkCt[sNum + k] = true;
                                }

                                a = bPkCts[k];
                                b = bPkCts[numDenom];
                                if ((a <= 0) && (b > 16)) {
                                    zerPkCt[sNum + k] = true;
                                }
                            } // k iteration

                            // test whether to continue
                            if (!zerPkCt[sNum] && !zerPkCt[sn1]) {
                                double aPk1 = pkNetCps[sNum][aOrd];
                                double bPk1 = pkNetCps[sNum][bOrd];
                                double aPk2 = pkNetCps[sn1][aOrd];
                                double bPk2 = pkNetCps[sn1][bOrd];

                                if (useSBM) {
                                    aPk1 /= sbmCps[sNum][aOrd];
                                    bPk1 /= sbmCps[sNum][bOrd];
                                    aPk2 /= sbmCps[sn1][aOrd];
                                    bPk2 /= sbmCps[sn1][bOrd];
                                }

                                double scanDeltaT = timeStampSec[sn1][aOrd] - timeStampSec[sNum][aOrd];
                                double bTfract = timeStampSec[sNum][bOrd] - timeStampSec[sNum][aOrd];
                                pkF[sNum] = bTfract / scanDeltaT;
                                double ff1 = (1.0 - pkF[sNum]) / 2.0;
                                double ff2 = (1.0 + pkF[sNum]) / 2.0;
                                double aInterp = (ff1 * aPk1) + (ff2 * aPk2);
                                double bInterp = (ff2 * bPk1) + (ff1 * bPk2);

                                double rNum = (NUM < DEN) ? aInterp : bInterp;
                                double rDen = (NUM < DEN) ? bInterp : aInterp;

                                if (rDen != 0.0) {
                                    rct++;
                                    interpRatVal[rct] = rNum / rDen;
                                    double a1PkSig = pkFerr[sNum][aOrd] * aPk1;
                                    double a2PkSig = pkFerr[sn1][aOrd] * aPk2;
                                    double b1PkSig = pkFerr[sNum][bOrd] * bPk1;
                                    double b2PkSig = pkFerr[sn1][bOrd] * bPk2;

                                    if (useSBM) {
                                        a1PkSig = StrictMath.sqrt(a1PkSig * a1PkSig
                                                + (aPk1 * aPk1 / sbmCps[sNum][aOrd] / countTimeSec[aOrd]));
                                        a2PkSig = StrictMath.sqrt(a2PkSig * a2PkSig
                                                + (aPk2 * aPk2 / sbmCps[sn1][aOrd] / countTimeSec[aOrd]));
                                        b1PkSig = StrictMath.sqrt(b1PkSig * b1PkSig
                                                + (bPk1 * bPk1 / sbmCps[sNum][bOrd] / countTimeSec[bOrd]));
                                        b2PkSig = StrictMath.sqrt(b2PkSig * b2PkSig
                                                + (bPk2 * bPk2 / sbmCps[sn1][bOrd] / countTimeSec[bOrd]));
                                    }

                                    if ((aInterp == 0.0) || (bInterp == 0.0)) {
                                        ratValFerr[rct] = 1.0;
                                        ratValSig[rct] = SQUID_TINY_VALUE;
                                        sigRho[rct][rct] = SQUID_TINY_VALUE;
                                    } else {
                                        double term1 = ((f1 * a1PkSig) * (f1 * a1PkSig) + (f2 * a2PkSig) * (f2 * a2PkSig));
                                        double term2 = ((f2 * b1PkSig) * (f2 * b1PkSig) + (f1 * b2PkSig) * (f1 * b2PkSig));
                                        double ratValFvar = (term1 / (aInterp * aInterp)) + (term2 / (bInterp * bInterp));
                                        double ratValVar = ratValFvar * (interpRatVal[rct] * interpRatVal[rct]);
                                        ratValFerr[rct] = StrictMath.sqrt(ratValFvar);
                                        ratValSig[rct] = StrictMath.max(1E-10, StrictMath.sqrt(ratValVar));
                                        sigRho[rct][rct] = ratValSig[rct];

                                        if (rct > 0) {
                                            rhoIJ = (zerPkCt[sNum - 1]) ? 0.0 : (1 - pkF[sNum] * pkF[sNum]) / (1 + pkF[sNum] * pkF[sNum]) / 2.0;

                                            sigRho[rct][rct - 1] = rhoIJ;
                                            sigRho[rct - 1][rct] = rhoIJ;
                                        }
                                    } // test aInterp andbInterp
                                } // test rDen

                            } // test !zerPkCt[sNum] && !zerPkCt[sn1]

                        } // continueWithScanProcessing is true

                    } // iteration through nDod using sNum (see "NextScanNum" in pseudocode)
                    switch (rct) {
                        case -1:
                            ratioVal = SQUID_ERROR_VALUE;
                            ratioFractErr = SQUID_ERROR_VALUE;

                            ratEqTime.add(ratioInterpTime[0]);
                            ratEqVal.add(ratioVal);
                            ratEqErr.add(ratioFractErr);

                            isotopicRatioModel.setRatioVal(squid3RoundedToSize(ratioVal, sigFigs));
                            isotopicRatioModel.setRatioFractErr(squid3RoundedToSize(ratioFractErr, sigFigs));

                            break;
                        case 0:
                            ratioVal = interpRatVal[0];
                            if (ratioVal == 0.0) {
                                ratioVal = SQUID_TINY_VALUE;
                                ratioFractErr = 1.0;
                            } else {
                                ratioFractErr = StrictMath.abs(ratValFerr[0]);// this is abs not percent July 2019 added abs call
                            }

                            ratEqTime.add(ratioInterpTime[0]);
                            ratEqVal.add(ratioVal);
                            ratEqErr.add(ratioFractErr);

                            isotopicRatioModel.setRatioVal(squid3RoundedToSize(ratioVal, sigFigs));
                            isotopicRatioModel.setRatioFractErr(squid3RoundedToSize(ratioFractErr, sigFigs));

                            break;
                        default:
                            for (int j = 0; j < (rct + 1); j++) {
                                ratEqTime.add(ratioInterpTime[j]);
                                ratEqVal.add(interpRatVal[j]);
                                ratEqErr.add(StrictMath.abs(ratValFerr[j] * interpRatVal[j]));
                            }

                            // step 4 **************************************************************************
                            WtdLinCorrResults wtdLinCorrResults;
                            double ratioMean;
                            double ratioMeanSig;

                            if (userLinFits && rct > 3) {
                                wtdLinCorrResults = wtdLinCorr(interpRatVal, sigRho, ratioInterpTime);

                                double midTime = (timeStampSec[nScans - 1][nSpecies - 1] + timeStampSec[0][0]) / 2.0;
                                ratioMean = (wtdLinCorrResults.getSlope() * midTime) + wtdLinCorrResults.getIntercept();
                                ratioMeanSig = StrictMath.sqrt((midTime * wtdLinCorrResults.getSigmaSlope() * midTime * wtdLinCorrResults.getSigmaSlope())//
                                        + wtdLinCorrResults.getSigmaIntercept() * wtdLinCorrResults.getSigmaIntercept() //
                                        + 2.0 * midTime * wtdLinCorrResults.getCovSlopeInter());

                            } else {
                                wtdLinCorrResults = wtdLinCorr(interpRatVal, sigRho, new double[0]);
                                ratioMean = wtdLinCorrResults.getIntercept();
                                ratioMeanSig = wtdLinCorrResults.getSigmaIntercept();
                            }

                            if (wtdLinCorrResults.isBad()) {
                                isotopicRatioModel.setRatioVal(SQUID_ERROR_VALUE);
                                isotopicRatioModel.setRatioFractErr(SQUID_ERROR_VALUE);
                            } else if (wtdLinCorrResults.getIntercept() == 0.0) {
                                isotopicRatioModel.setRatioVal(SQUID_TINY_VALUE);
                                isotopicRatioModel.setRatioFractErr(1.0);
                            } else {
                                isotopicRatioModel.setRatioVal(squid3RoundedToSize(ratioMean, sigFigs));
                                isotopicRatioModel.setRatioFractErr(squid3RoundedToSize(StrictMath.max(SQUID_TINY_VALUE,
                                        ratioMeanSig) / StrictMath.abs(ratioMean), sigFigs));
                            }

                            isotopicRatioModel.setMinIndex(wtdLinCorrResults.getMinIndex());

                            break;
                    }

                } // end decision on which ratio procedure to use

                // store values for reports
                isotopicRatioModel.setRatEqTime(ratEqTime);
                isotopicRatioModel.setRatEqVal(ratEqVal);
                isotopicRatioModel.setRatEqErr(ratEqErr);
            } else {
                isotopicRatioModel.setActive(false);
            }// check for number of species present
        }); // end iteration through isotopicRatios

    }// end calculateIsotopicRatios

    private int calculateInterpolatedPeakHeights() {
        // June 2019 per issue #340 :https://github.com/CIRDLES/Squid/issues/340   
        // return count of invalid SBM counts
        int countOfNonPositiveSBMCounts = 0;

        // extracted from https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Sub-EqnInterp
        // design decision to pre-compute all to store with shrimpFraction and enable on-the-fly tasks
        boolean singleScan = (nScans == 1);
//        int sIndx = singleScan ? 1 : nScans - 1;
        reducedPkHt = new double[nScans][nSpecies];
        reducedPkHtFerr = new double[nScans][nSpecies];

        // The next step is to convert SBM peak-heights, calculate uncertainties, 
        //and assign 'working' peak-heights ("ReducedPkHt") values to both SBM-normalised 
        //data and data not normalised to SBM.
        for (int scanNum = 0; scanNum < nScans; scanNum++) {
            for (int speciesIndex = 0; speciesIndex < nSpecies; speciesIndex++) {
                int pkOrder = speciesIndex;
                double scanPkCts;

                double netPkCps = pkNetCps[scanNum][pkOrder];
                if (netPkCps <= SQUID_ERROR_VALUE) {
                    reducedPkHt[scanNum][pkOrder] = SQUID_ERROR_VALUE;
                } else {
                    scanPkCts = netPkCps * countTimeSec[pkOrder];
//                    If ScanPkCts <= 0 And ScanPkCts > 16 --verbatim, seems nonsensical
//                    ReducedPkHt[ScanNum, PkOrder] = _[SQUID error-value]_
//                    Exit For
//                    End If
                    double pkFractErr = pkFerr[scanNum][pkOrder];

                    boolean sbmNorm = (sbmCps[scanNum][pkOrder] > 0.0);
                    if (sbmNorm) {
                        try {
                            scanPkCts /= sbmCps[scanNum][pkOrder];
                        } catch (Exception e) {
                            scanPkCts = SQUID_ERROR_VALUE;
                        }

                        pkFractErr = StrictMath.sqrt(pkFractErr * pkFractErr
                                + 1.0 / sbmCps[scanNum][pkOrder] / countTimeSec[pkOrder]);
                        if (!Double.isFinite(pkFractErr)) {
                            pkFractErr = SQUID_ERROR_VALUE;
                        }
                    } else {
                        countOfNonPositiveSBMCounts++;
                    }

                    // in all cases
                    try {
                        reducedPkHt[scanNum][pkOrder] = scanPkCts / countTimeSec[pkOrder];
                    } catch (Exception e) {
                        reducedPkHt[scanNum][pkOrder] = SQUID_ERROR_VALUE;
                    }
                    reducedPkHtFerr[scanNum][pkOrder] = pkFractErr;
                }
            } // end of scans loop
            // the remainder of the math is done on a per-expression basis
        }
        return countOfNonPositiveSBMCounts;
    }

}