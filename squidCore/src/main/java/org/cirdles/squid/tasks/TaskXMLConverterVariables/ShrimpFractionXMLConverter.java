/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.tasks.TaskXMLConverterVariables;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.SquidRatiosModel;

/**
 *
 * @author ryanb
 */
public class ShrimpFractionXMLConverter implements Converter {

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        ShrimpFraction fraction = (ShrimpFraction) o;

        writer.startNode("fractioinID");
        writer.setValue(fraction.getFractionID());
        writer.endNode();

        writer.startNode("spotNumber");
        writer.setValue(Integer.toString(fraction.getSpotNumber()));
        writer.endNode();

        writer.startNode("nameOfMount");
        writer.setValue(fraction.getNameOfMount());
        writer.endNode();

        writer.startNode("dateTimeMilliseconds");
        writer.setValue(Long.toString(fraction.getDateTimeMilliseconds()));
        writer.endNode();

        writer.startNode("hours");
        writer.setValue(Double.toString(fraction.getHours()));
        writer.endNode();

        writer.startNode("deadTimeNanoseconds");
        writer.setValue(Integer.toString(fraction.getDeadTimeNanoseconds()));
        writer.endNode();

        writer.startNode("sbmZeroCps");
        writer.setValue(Integer.toString(fraction.getSbmZeroCps()));
        writer.endNode();

        writer.startNode("stageX");
        writer.setValue(Integer.toString(fraction.getStageX()));
        writer.endNode();

        writer.startNode("stageY");
        writer.setValue(Integer.toString(fraction.getStageY()));
        writer.endNode();

        writer.startNode("stageZ");
        writer.setValue(Integer.toString(fraction.getStageZ()));
        writer.endNode();

        writer.startNode("qtlY");
        writer.setValue(Integer.toString(fraction.getQtlY()));
        writer.endNode();

        writer.startNode("qtlZ");
        writer.setValue(Integer.toString(fraction.getQtlZ()));
        writer.endNode();

        writer.startNode("primaryBeam");
        writer.setValue(Double.toString(fraction.getPrimaryBeam()));
        writer.endNode();

        writer.startNode("countTimeSecs");
        double[] countTimeSec = fraction.getCountTimeSec();
        for (int i = 0; i < countTimeSec.length; i++) {
            writer.startNode("countTimeSec");
            writer.setValue(Double.toString(countTimeSec[i]));
            writer.endNode();
        }
        writer.endNode();

        String[] nameOfSpecies = fraction.getNamesOfSpecies();
        writer.startNode("namesofSpecies");
        for (int i = 0; i < nameOfSpecies.length; i++) {
            writer.startNode("nameOfSpecie");
            writer.setValue(nameOfSpecies[i]);
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("peakMeasurementsCount");
        writer.setValue(Integer.toString(fraction.getPeakMeasurementsCount()));
        writer.endNode();

        writer.startNode("isotopicRatiosII");
        context.convertAnother(fraction.getIsotopicRatiosII());
        writer.endNode();

        writer.startNode("rawPeakData");
        int[][] rawPeakData = fraction.getRawPeakData();
        for (int i = 0; i < rawPeakData.length; i++) {
            writer.startNode("rawPeakDataRow");
            for (int j = 0; j < rawPeakData[0].length; j++) {
                writer.startNode("rawPeakDataValue");
                writer.setValue(Integer.toString(rawPeakData[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("rawSBMData");
        int[][] rawSBMData = fraction.getRawSBMData();
        for (int i = 0; i < rawSBMData.length; i++) {
            writer.startNode("rawSBMDataRow");
            for (int j = 0; j < rawSBMData[0].length; j++) {
                writer.startNode("rawSBMDataValue");
                writer.setValue(Integer.toString(rawSBMData[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("totalCounts");
        double[][] totalCounts = fraction.getTotalCounts();
        for (int i = 0; i < totalCounts.length; i++) {
            writer.startNode("totalCountsRow");
            for (int j = 0; j < totalCounts[0].length; j++) {
                writer.startNode("totalCountsValue");
                writer.setValue(Double.toString(totalCounts[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("totalCountsOneSigmaAbs");
        double[][] totalCountsOneSigmaAbs = fraction.getTotalCountsOneSigmaAbs();
        for (int i = 0; i < totalCountsOneSigmaAbs.length; i++) {
            writer.startNode("totalCountsOneSigmaAbsRow");
            for (int j = 0; j < totalCountsOneSigmaAbs[0].length; j++) {
                writer.startNode("totalCountsOneSigmaAbsValue");
                writer.setValue(Double.toString(totalCountsOneSigmaAbs[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("totalCountsSBM");
        double[][] totalCountsSBM = fraction.getTotalCountsSBM();
        for (int i = 0; i < totalCountsSBM.length; i++) {
            writer.startNode("totalCountsSBMRow");
            for (int j = 0; j < totalCountsSBM[0].length; j++) {
                writer.startNode("totalCountsSBMValue");
                writer.setValue(Double.toString(totalCountsSBM[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("timeStampSecs");
        double[][] timeStampSecs = fraction.getTimeStampSec();
        for (int i = 0; i < timeStampSecs.length; i++) {
            writer.startNode("timeStampSecsRow");
            for (int j = 0; j < timeStampSecs[0].length; j++) {
                writer.startNode("timeStampSec");
                writer.setValue(Double.toString(timeStampSecs[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("trimMasses");
        double[][] trimMass = fraction.getTrimMass();
        for (int i = 0; i < trimMass.length; i++) {
            writer.startNode("trimMassesRow");
            for (int j = 0; j < trimMass[0].length; j++) {
                writer.startNode("trimMass");
                writer.setValue(Double.toString(trimMass[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("totalCps");
        double[] totalCps = fraction.getTotalCps();
        for (int i = 0; i < totalCps.length; i++) {
            writer.startNode("totalCpsValue");
            writer.setValue(Double.toString(totalCps[i]));
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("netPkCps");
        double[][] netPkCps = fraction.getNetPkCps();
        for (int i = 0; i < netPkCps.length; i++) {
            writer.startNode("netPkCps");
            for (int j = 0; j < netPkCps[0].length; j++) {
                writer.startNode("netPkCpsValue");
                writer.setValue(Double.toString(netPkCps[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("pkFerr");
        double[][] pkFerr = fraction.getPkFerr();
        for (int i = 0; i < pkFerr.length; i++) {
            writer.startNode("pkFerr");
            for (int j = 0; j < pkFerr[0].length; j++) {
                writer.startNode("pkFerrValue");
                writer.setValue(Double.toString(pkFerr[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("referenceMaterial");
        writer.setValue(Boolean.toString(fraction.isReferenceMaterial()));
        writer.endNode();

        writer.startNode("concentrationReferenceMaterial");
        writer.setValue(Boolean.toString(fraction.isConcentrationReferenceMaterial()));
        writer.endNode();

        writer.startNode("useSBM");
        writer.setValue(Boolean.toString(fraction.isUseSBM()));
        writer.endNode();

        writer.startNode("userLinFits");
        writer.setValue(Boolean.toString(fraction.isUserLinFits()));
        writer.endNode();

        writer.startNode("reducedPkHt");
        double[][] reducedPkHt = fraction.getReducedPkHt();
        for (int i = 0; i < reducedPkHt.length; i++) {
            writer.startNode("reducedPkHtRow");
            for (int j = 0; j < reducedPkHt[0].length; j++) {
                writer.startNode("reducedPkHtValue");
                writer.setValue(Double.toString(reducedPkHt[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("reducedPkHtFerr");
        double[][] reducedPkHtFerr = fraction.getReducedPkHtFerr();
        for (int i = 0; i < reducedPkHtFerr.length; i++) {
            writer.startNode("reducedPkHtFerrRow");
            for (int j = 0; j < reducedPkHtFerr[0].length; j++) {
                writer.startNode("reducedPkHtFerrValue");
                writer.setValue(Double.toString(reducedPkHtFerr[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("pkInterpScanArray");
        double[] pkInterpScanArray = fraction.getPkInterpScanArray();
        for (int i = 0; i < pkInterpScanArray.length; i++) {
            writer.startNode("pkInterpScanArray");
            writer.setValue(Double.toString(pkInterpScanArray[i]));
            writer.endNode();
        }
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ShrimpFraction fraction = new ShrimpFraction();

        reader.moveDown();
        fraction.setFractionID(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        fraction.setSpotNumber(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setNameOfMount(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        fraction.setDateTimeMilliseconds(Long.parseLong(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setHours(Double.parseDouble(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setDeadTimeNanoseconds(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setSbmZeroCps(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setStageX(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setStageY(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setQtlY(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setQtlZ(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setPrimaryBeam(Double.parseDouble(reader.getValue()));
        reader.moveUp();

        reader.moveDown();

        List<Double> countTimeSec = new LinkedList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            countTimeSec.add(Double.parseDouble(reader.getValue()));
            reader.moveUp();
        }
        reader.moveUp();
        double[] countTimeSecs = new double[countTimeSec.size()];
        for (int i = 0; i < countTimeSecs.length; i++) {
            countTimeSecs[i] = countTimeSec.get(i);
        }
        fraction.setCountTimeSec(countTimeSecs);

        List<String> namesOfSpeciesList = new LinkedList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            namesOfSpeciesList.add(reader.getValue());
            reader.moveUp();
        }
        reader.moveUp();
        String[] namesOfSpecies = new String[namesOfSpeciesList.size()];
        for (int i = 0; i < namesOfSpecies.length; i++) {
            namesOfSpecies[i] = namesOfSpeciesList.get(i);
        }
        fraction.setNamesOfSpecies(namesOfSpecies);

        reader.moveDown();
        fraction.setPeakMeasurementsCount(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        SortedSet<SquidRatiosModel> isotopicRatiosII = new TreeSet<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Object isotopicRatio = new Object();
            isotopicRatiosII.add((SquidRatiosModel) context.convertAnother((SquidRatiosModel) isotopicRatio, SquidRatiosModel.class));
            reader.moveUp();
        }
        reader.moveUp();
        fraction.setIsotopicRatiosII(isotopicRatiosII);

        List<List<Integer>> rawPeakDataList = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Integer> rawDataList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                rawDataList.add(Integer.parseInt(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            rawPeakDataList.add(rawDataList);
        }
        reader.moveUp();
        int[][] rawPeakData = new int[rawPeakDataList.size()][rawPeakDataList.get(0).size()];
        for (int i = 0; i < rawPeakData.length; i++) {
            for (int j = 0; j < rawPeakData[0].length; j++) {
                rawPeakData[i][j] = rawPeakDataList.get(i).get(j);
            }
        }
        fraction.setRawPeakData(rawPeakData);

        List<List<Integer>> rawSBMDataList = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Integer> rawSBMList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                rawSBMList.add(Integer.parseInt(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            rawSBMDataList.add(rawSBMList);
        }
        reader.moveUp();
        int[][] rawSBMData = new int[rawSBMDataList.size()][rawSBMDataList.get(0).size()];
        for (int i = 0; i < rawSBMData.length; i++) {
            for (int j = 0; j < rawSBMData[0].length; j++) {
                rawSBMData[i][j] = rawSBMDataList.get(i).get(j);
            }
        }
        fraction.setRawSBMData(rawSBMData);

        List<List<Double>> totalCountsListOfLists = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Double> totalCountsList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                totalCountsList.add(Double.parseDouble(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            totalCountsListOfLists.add(totalCountsList);
        }
        reader.moveUp();
        double[][] totalCounts = new double[totalCountsListOfLists.size()][totalCountsListOfLists.get(0).size()];
        for (int i = 0; i < totalCounts.length; i++) {
            for (int j = 0; j < totalCounts[0].length; j++) {
                totalCounts[i][j] = totalCountsListOfLists.get(i).get(j);
            }
        }
        fraction.setTotalCounts(totalCounts);

        List<List<Double>> totalCountsOneSigmaAbsListOfLists = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Double> totalCountsList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                totalCountsList.add(Double.parseDouble(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            totalCountsOneSigmaAbsListOfLists.add(totalCountsList);
        }
        reader.moveUp();
        double[][] totalCountsOneSigmaAbs = new double[totalCountsOneSigmaAbsListOfLists.size()][totalCountsOneSigmaAbsListOfLists.get(0).size()];
        for (int i = 0; i < totalCountsOneSigmaAbs.length; i++) {
            for (int j = 0; j < totalCountsOneSigmaAbs[0].length; j++) {
                totalCountsOneSigmaAbs[i][j] = totalCountsOneSigmaAbsListOfLists.get(i).get(j);
            }
        }
        fraction.setTotalCountsOneSigmaAbs(totalCountsOneSigmaAbs);

        List<List<Double>> totalCountsSBMListOfLists = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Double> totalCountsSBMList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                totalCountsSBMList.add(Double.parseDouble(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            totalCountsSBMListOfLists.add(totalCountsSBMList);
        }
        reader.moveUp();
        double[][] totalCountsSBM = new double[totalCountsSBMListOfLists.size()][totalCountsSBMListOfLists.get(0).size()];
        for (int i = 0; i < totalCountsSBM.length; i++) {
            for (int j = 0; j < totalCountsSBM[0].length; j++) {
                totalCountsSBM[i][j] = totalCountsSBMListOfLists.get(i).get(j);
            }
        }
        fraction.setTotalCountsSBM(totalCountsSBM);

        List<List<Double>> timeStampSecLOL = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Double> timeStampSecList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                timeStampSecList.add(Double.parseDouble(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            timeStampSecLOL.add(timeStampSecList);
        }
        reader.moveUp();
        double[][] timeStampSec = new double[timeStampSecLOL.size()][timeStampSecLOL.get(0).size()];
        for (int i = 0; i < timeStampSec.length; i++) {
            for (int j = 0; j < timeStampSec[0].length; j++) {
                timeStampSec[i][j] = timeStampSecLOL.get(i).get(j);
            }
        }
        fraction.setTimeStampSec(timeStampSec);

        List<List<Double>> trimMassLOL = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Double> trimMassList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                trimMassList.add(Double.parseDouble(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            trimMassLOL.add(trimMassList);
        }
        reader.moveUp();
        double[][] trimMass = new double[trimMassLOL.size()][trimMassLOL.get(0).size()];
        for (int i = 0; i < trimMass.length; i++) {
            for (int j = 0; j < trimMass[0].length; j++) {
                trimMass[i][j] = trimMassLOL.get(i).get(j);
            }
        }
        fraction.setTrimMass(trimMass);

        List<Double> totalCpsList = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            totalCpsList.add(Double.parseDouble(reader.getValue()));
            reader.moveUp();
        }
        reader.moveUp();
        double[] totalCps = new double[totalCpsList.size()];
        for (int i = 0; i < totalCps.length; i++) {
            totalCps[i] = totalCpsList.get(i);
        }
        fraction.setTotalCps(totalCps);

        List<List<Double>> netPkCpsLOL = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Double> netPkCpsList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                netPkCpsList.add(Double.parseDouble(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            netPkCpsLOL.add(netPkCpsList);
        }
        reader.moveUp();
        double[][] netPkCps = new double[netPkCpsLOL.size()][netPkCpsLOL.get(0).size()];
        for (int i = 0; i < netPkCps.length; i++) {
            for (int j = 0; j < netPkCps[0].length; j++) {
                netPkCps[i][j] = netPkCpsLOL.get(i).get(j);
            }
        }
        fraction.setNetPkCps(netPkCps);

        List<List<Double>> pkFerrLOL = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Double> pkFerrList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                pkFerrList.add(Double.parseDouble(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            pkFerrLOL.add(pkFerrList);
        }
        reader.moveUp();
        double[][] pkFerr = new double[pkFerrLOL.size()][pkFerrLOL.get(0).size()];
        for (int i = 0; i < pkFerr.length; i++) {
            for (int j = 0; j < pkFerr[0].length; j++) {
                pkFerr[i][j] = pkFerrLOL.get(i).get(j);
            }
        }
        fraction.setPkFerr(pkFerr);

        reader.moveDown();
        fraction.setReferenceMaterial(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setConcentrationReferenceMaterial(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setUseSBM(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        fraction.setUserLinFits(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        List<List<Double>> reducedPkHtLOL = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Double> reducedPkHtList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                reducedPkHtList.add(Double.parseDouble(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            reducedPkHtLOL.add(reducedPkHtList);
        }
        reader.moveUp();
        double[][] reducedPkHt = new double[reducedPkHtLOL.size()][reducedPkHtLOL.get(0).size()];
        for (int i = 0; i < reducedPkHt.length; i++) {
            for (int j = 0; j < reducedPkHt[0].length; j++) {
                reducedPkHt[i][j] = reducedPkHtLOL.get(i).get(j);
            }
        }
        fraction.setReducedPkHt(reducedPkHt);

        List<List<Double>> reducedPkHtFerrLOL = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Double> reducedPkHtFerrList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                reducedPkHtFerrList.add(Double.parseDouble(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            reducedPkHtFerrLOL.add(reducedPkHtFerrList);
        }
        reader.moveUp();
        double[][] reducedPkHtFerr = new double[reducedPkHtFerrLOL.size()][reducedPkHtFerrLOL.get(0).size()];
        for (int i = 0; i < reducedPkHtFerr.length; i++) {
            for (int j = 0; j < reducedPkHtFerr[0].length; j++) {
                reducedPkHtFerr[i][j] = reducedPkHtFerrLOL.get(i).get(j);
            }
        }
        fraction.setReducedPkHtFerr(reducedPkHtFerr);

        List<Double> pkInterpScanList = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            pkInterpScanList.add(Double.parseDouble(reader.getValue()));
            reader.moveUp();
        }
        reader.moveUp();
        double[] pkInterpScanArray = new double[pkInterpScanList.size()];
        for (int i = 0; i < pkInterpScanArray.length; i++) {
            pkInterpScanArray[i] = pkInterpScanList.get(i);
        }
        fraction.setPkInterpScanArray(pkInterpScanArray);

        return fraction;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(ShrimpFraction.class);
    }

}
