/*
 * Copyright 2017 CIRDLES.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.projects;

import org.cirdles.squid.shrimp.MassStationDetail;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.JAXBException;
import org.cirdles.squid.core.PrawnFileHandler;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.TaskSquid25;
import org.cirdles.squid.tasks.expressions.ExpressionTree;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeWithRatiosInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.storedTasks.SquidBodorkosTask1;
import org.xml.sax.SAXException;
import org.cirdles.squid.utilities.SquidPrefixTree;
import org.cirdles.squid.utilities.fileUtilities.PrawnFileUtilities;

/**
 *
 * @author bowring
 */
public class SquidProject implements Serializable {

    private static final long serialVersionUID = 7099919411562934142L;

    private transient SquidPrefixTree prefixTree;
    // cannot be serialized because of JavaFX private final SimpleStringProperty fields
    private transient Map<Integer, MassStationDetail> mapOfIndexToMassStationDetails;

    private PrawnFileHandler prawnFileHandler = new PrawnFileHandler();
    private String projectName;
    private String analystName;
    private String projectNotes;
    private File prawnXMLFile;
    private PrawnFile prawnFile;
    private String filterForRefMatSpotNames;
    private List<Run> shrimpRunsRefMat;
    private double sessionDurationHours;
    private SquidSessionModel squidSessionModel;
    private List<SquidSpeciesModel> squidSpeciesModelList;
    private List<SquidRatiosModel> squidRatiosModelList;
    private boolean[][] tableOfSelectedRatiosByMassStationIndex;
    private TaskSquid25 taskSquid25;

    public SquidProject() {
        this.prawnFileHandler = new PrawnFileHandler();
        this.projectName = "NO_NAME";
        this.prawnXMLFile = new File("");
        this.prawnFile = null;

        this.filterForRefMatSpotNames = "";

        this.shrimpRunsRefMat = new ArrayList<>();
        this.sessionDurationHours = 0.0;

        squidSpeciesModelList = new ArrayList<>();
        squidRatiosModelList = new ArrayList<>();
        tableOfSelectedRatiosByMassStationIndex = new boolean[0][];

        taskSquid25 = null;

    }

    private void buildSquidSpeciesModelListFromMassStationDetails() {
        squidSpeciesModelList = new ArrayList<>();
        for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
            SquidSpeciesModel spm = new SquidSpeciesModel(
                    entry.getKey(), entry.getValue().getMassStationLabel(), entry.getValue().getIsotopeLabel(), entry.getValue().getElementLabel(), entry.getValue().getIsBackground());

            squidSpeciesModelList.add(spm);
        }
        if (tableOfSelectedRatiosByMassStationIndex.length == 0) {
            tableOfSelectedRatiosByMassStationIndex = new boolean[squidSpeciesModelList.size()][squidSpeciesModelList.size()];
        }
    }

    public void buildSquidRatiossModelListFromMassStationDetails() {
        squidRatiosModelList = new ArrayList<>();

        SquidRatiosModel.knownSquidRatiosModels.clear();

        for (int row = 0; row < tableOfSelectedRatiosByMassStationIndex.length; row++) {
            for (int col = 0; col < tableOfSelectedRatiosByMassStationIndex[0].length; col++) {
                if ((tableOfSelectedRatiosByMassStationIndex[row][col])
                        && (!squidSpeciesModelList.get(row).getIsBackground())
                        && (!squidSpeciesModelList.get(col).getIsBackground())) {
                    squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(row), squidSpeciesModelList.get(col), 0));
                }
            }
        }

    }

    public void createMapOfIndexToMassStationDetails() {
        mapOfIndexToMassStationDetails = PrawnFileUtilities.createMapOfIndexToMassStationDetails(prawnFile.getRun());

        // update these if squidSpeciesModelList exists
        if (squidSpeciesModelList.size() > 0) {
            for (SquidSpeciesModel ssm : squidSpeciesModelList) {
                MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(ssm.getMassStationIndex());
                // only these two fields change
                massStationDetail.setIsotopeLabel(ssm.getIsotopeName());
                massStationDetail.setIsBackground(ssm.getIsBackground());
            }
        } else {
            buildSquidSpeciesModelListFromMassStationDetails();
        }
    }

    public void setupSquidSessionSpecs() {
        createMapOfIndexToMassStationDetails();

        squidRatiosModelList = new ArrayList<>();
        try {
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(1), squidSpeciesModelList.get(3), 0));
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(4), squidSpeciesModelList.get(3), 1));
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(5), squidSpeciesModelList.get(3), 2));
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(6), squidSpeciesModelList.get(0), 3));
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(3), squidSpeciesModelList.get(6), 4));
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(6), squidSpeciesModelList.get(3), 10));
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(8), squidSpeciesModelList.get(6), 5));
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(3), squidSpeciesModelList.get(8), 9));
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(7), squidSpeciesModelList.get(8), 6));
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(3), squidSpeciesModelList.get(9), 7));
            squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(9), squidSpeciesModelList.get(8), 8));

        } catch (Exception e) {
        }
        squidSessionModel = new SquidSessionModel(squidSpeciesModelList, squidRatiosModelList, true, false, "T");
    }

    public int selectBackgroundSpeciesReturnPreviousIndex(SquidSpeciesModel ssm) {
        // there is at most one
        int retVal = -1;

        for (SquidSpeciesModel squidSpeciesModel : squidSpeciesModelList) {
            if (squidSpeciesModel.getIsBackground()) {
                squidSpeciesModel.setIsBackground(false);
                retVal = squidSpeciesModel.getMassStationIndex();
                break;
            }
        }

        if (ssm != null) {
            ssm.setIsBackground(true);
        }

        return retVal;
    }

    public ExpressionTreeInterface buildRatioExpression(String ratioName) {
        // format of ratioName is "nnn/mmm"
        ExpressionTreeInterface ratioExpression = null;
        if ((findNumerator(ratioName) != null) & (findDenominator(ratioName) != null)) {
            ratioExpression
                    = new ExpressionTree(
                            ratioName,
                            new ShrimpSpeciesNode(findNumerator(ratioName), "getPkInterpScanArray"),
                            new ShrimpSpeciesNode(findDenominator(ratioName), "getPkInterpScanArray"),
                            Operation.divide());

            ((ExpressionTreeWithRatiosInterface) ratioExpression).getRatiosOfInterest().add(ratioName);
        }
        return ratioExpression;
    }

    public SquidSpeciesModel findNumerator(String ratioName) {
        String[] parts = ratioName.split("/");
        return lookUpSpeciesByName(parts[0]);
    }

    public SquidSpeciesModel findDenominator(String ratioName) {
        String[] parts = ratioName.split("/");
        return lookUpSpeciesByName(parts[1]);
    }

    public SquidSpeciesModel lookUpSpeciesByName(String isotopeName) {
        SquidSpeciesModel retVal = null;

        for (SquidSpeciesModel squidSpeciesModel : squidSpeciesModelList) {
            if (squidSpeciesModel.getIsotopeName().compareToIgnoreCase(isotopeName) == 0) {
                retVal = squidSpeciesModel;
                break;
            }
        }

        return retVal;
    }

    /**
     *
     * @return
     */
    public Set<SquidSpeciesModel> extractUniqueSpeciesNumbers(List<String> ratiosOfInterest) {
        // assume acquisition order is atomic weight order
        Set<SquidSpeciesModel> eqPkUndupeOrd = new TreeSet<>();
        for (int i = 0; i < ratiosOfInterest.size(); i++) {
            eqPkUndupeOrd.add(findNumerator(ratiosOfInterest.get(i)));
            eqPkUndupeOrd.add(findDenominator(ratiosOfInterest.get(i)));
        }
        return eqPkUndupeOrd;
    }

    public void testRunOfSessionModel() {
        List<ShrimpFractionExpressionInterface> shrimpFractions = prawnFileHandler.processRunFractions(prawnFile, squidSessionModel);

        TaskInterface squidBodorkosTask1 = new SquidBodorkosTask1(this);
        squidBodorkosTask1.evaluateTaskExpressions(shrimpFractions);

        try {
            prawnFileHandler.getReportsEngine().produceReports(shrimpFractions);
        } catch (IOException iOException) {
        }
    }

    public void setupTaskSquid25File(File squidTaskFile) {

        taskSquid25 = TaskSquid25.importSquidTaskFile(squidTaskFile);

    }

    public void setupPrawnFile(File prawnXMLFileNew)
            throws IOException, JAXBException, SAXException {

        prawnXMLFile = prawnXMLFileNew;
        updatePrawnFileHandlerWithFileLocation();
        prawnFile = prawnFileHandler.unmarshallCurrentPrawnFileXML();
    }

    public void setupPrawnFileByJoin(List<File> prawnXMLFilesNew)
            throws IOException, JAXBException, SAXException {

        if (prawnXMLFilesNew.size() == 2) {
            PrawnFile prawnFile1 = prawnFileHandler.unmarshallPrawnFileXML(prawnXMLFilesNew.get(0).getCanonicalPath());
            PrawnFile prawnFile2 = prawnFileHandler.unmarshallPrawnFileXML(prawnXMLFilesNew.get(1).getCanonicalPath());

            long start1 = PrawnFileUtilities.timeInMillisecondsOfRun(prawnFile1.getRun().get(0));
            long start2 = PrawnFileUtilities.timeInMillisecondsOfRun(prawnFile2.getRun().get(0));

            if (start1 > start2) {
                prawnFile2.getRun().addAll(prawnFile1.getRun());
                prawnFile2.setRuns((short) prawnFile2.getRun().size());
                prawnXMLFile = new File(
                        prawnXMLFilesNew.get(1).getName().replace(".xml", "").replace(".XML", "")
                        + "-JOIN-"
                        + prawnXMLFilesNew.get(0).getName().replace(".xml", "").replace(".XML", "") + ".xml");
                prawnFile = prawnFile2;
            } else {
                prawnFile1.getRun().addAll(prawnFile2.getRun());
                prawnFile1.setRuns((short) prawnFile1.getRun().size());
                prawnXMLFile = new File(
                        prawnXMLFilesNew.get(0).getName().replace(".xml", "").replace(".XML", "")
                        + "-JOIN-"
                        + prawnXMLFilesNew.get(1).getName().replace(".xml", "").replace(".XML", "") + ".xml");
                prawnFile = prawnFile1;
            }

            updatePrawnFileHandlerWithFileLocation();
            // write and read merged file to confirm conforms to schema
            serializePrawnData(prawnFileHandler.getCurrentPrawnFileLocation());
            prawnFile = prawnFileHandler.unmarshallCurrentPrawnFileXML();
        } else {
            throw new IOException("Two files not present");
        }
    }

    public void updatePrawnFileHandlerWithFileLocation()
            throws IOException {
        prawnFileHandler.setCurrentPrawnFileLocation(prawnXMLFile.getCanonicalPath());
    }

    public void savePrawnFile(File prawnXMLFileNew)
            throws IOException, JAXBException, SAXException {

        preProcessPrawnSession();

        prawnXMLFile = prawnXMLFileNew;
        prawnFileHandler.setCurrentPrawnFileLocation(prawnXMLFile.getCanonicalPath());
        serializePrawnData(prawnFileHandler.getCurrentPrawnFileLocation());
    }

    public boolean prawnFileExists() {
        return prawnFile != null;
    }

    public PrawnFile deserializePrawnData()
            throws IOException, JAXBException, SAXException {
        return prawnFileHandler.unmarshallCurrentPrawnFileXML();
    }

    private void serializePrawnData(String fileName)
            throws IOException, JAXBException, SAXException {
        prawnFileHandler.writeRawDataFileAsXML(prawnFile, fileName);
    }

    public String getPrawnXMLFileName() {
        return prawnXMLFile.getName();
    }
    
    public String getPrawnXMLFilePath() {
        return prawnXMLFile.getAbsolutePath();
    }

    public String getPrawnFileShrimpSoftwareVersionName() {
        return prawnFile.getSoftwareVersion();
    }

    public String getPrawnFileLoginComment() {
        return prawnFile.getLoginComment();
    }

    public List<Run> getPrawnFileRuns() {
        return prawnFile.getRun();
    }

    public void processPrawnSessionForDuplicateSpotNames() {
        List<Run> runs = prawnFile.getRun();
        Map<String, Integer> spotNameCountMap = new HashMap<>();
        for (int i = 0; i < runs.size(); i++) {
            String spotName = runs.get(i).getPar().get(0).getValue();
            // remove existing duplicate label in case editing occurred
            int indexDUP = spotName.indexOf("-DUP");
            if (indexDUP > 0) {
                runs.get(i).getPar().get(0).setValue(spotName.substring(0, spotName.indexOf("-DUP")));
                spotName = runs.get(i).getPar().get(0).getValue();
            }
            if (spotNameCountMap.containsKey(spotName)) {
                int count = spotNameCountMap.get(spotName);
                count++;
                spotNameCountMap.put(spotName, count);
                runs.get(i).getPar().get(0).setValue(spotName + "-DUP-" + count);
            } else {
                spotNameCountMap.put(spotName, 0);
            }
        }
    }

    public void preProcessPrawnSession() {

        processPrawnSessionForDuplicateSpotNames();

        // determine time in hours for session
        List<Run> runs = prawnFile.getRun();
        long startFirst = PrawnFileUtilities.timeInMillisecondsOfRun(runs.get(0));
        long startLast = PrawnFileUtilities.timeInMillisecondsOfRun(runs.get(runs.size() - 1));
        long sessionDuration = startLast - startFirst;

        sessionDurationHours = (double) sessionDuration / 1000 / 60 / 60;

    }

    public void removeRunFromPrawnFile(Run run) {
        prawnFile.getRun().remove(run);

        // save new count
        prawnFile.setRuns((short) prawnFile.getRun().size());
    }

    public SquidPrefixTree generatePrefixTreeFromSpotNames() {
        prefixTree = new SquidPrefixTree();

        for (int i = 0; i < prawnFile.getRun().size(); i++) {
            SquidPrefixTree leafParent = prefixTree.insert(prawnFile.getRun().get(i).getPar().get(0).getValue());
            leafParent.getChildren().get(0).setCountOfSpecies(Integer.parseInt(prawnFile.getRun().get(i).getPar().get(2).getValue()));
            leafParent.getChildren().get(0).setCountOfScans(Integer.parseInt(prawnFile.getRun().get(i).getPar().get(3).getValue()));
        }

        prefixTree.prepareStatistics();

        return prefixTree;
    }

    /**
     * Splits the current PrawnFile into two sets of runs based on the index of
     * the run supplied and writes out two prawn xml files in the same folder as
     * the original.
     *
     * @param run
     * @param useOriginalData, when true, the original unedited file is used,
     * otherwise the edited file is used.
     * @return String [2] containing the file names of the two Prawn XML files
     * written as a result of the split.
     */
    public String[] splitPrawnFileAtRun(Run run, boolean useOriginalData) {
        String[] retVal = new String[2];
        retVal[0] = prawnFileHandler.getCurrentPrawnFileLocation().replace(".xml", "-PART-A.xml").replace(".XML", "-PART-A.xml");
        retVal[1] = prawnFileHandler.getCurrentPrawnFileLocation().replace(".xml", "-PART-B.xml").replace(".XML", "-PART-B.xml");

        // get index from original prawnFile
        int indexOfRun = prawnFile.getRun().indexOf(run);

        List<Run> runs = prawnFile.getRun();
        List<Run> runsCopy = new ArrayList<>();

        if (useOriginalData) {
            PrawnFile prawnFileOriginal = null;
            try {
                prawnFileOriginal = deserializePrawnData();
                runsCopy = new CopyOnWriteArrayList<>(prawnFileOriginal.getRun());
            } catch (IOException | JAXBException | SAXException iOException) {
            }
        } else {
            runsCopy = new CopyOnWriteArrayList<>(runs);
        }

        List<Run> first = runsCopy.subList(0, indexOfRun);
        List<Run> second = runsCopy.subList(indexOfRun, runs.size());

        // keep first
        runs.clear();
        // preserve order
        for (Run runF : first) {
            runs.add(runF);
        }

        prawnFile.setRuns((short) runs.size());
        try {
            prawnFileHandler.writeRawDataFileAsXML(prawnFile, retVal[0]);
        } catch (JAXBException jAXBException) {
        }

        runs.clear();
        // preserve order
        for (Run runS : second) {
            runs.add(runS);
        }
        prawnFile.setRuns((short) runs.size());
        try {
            prawnFileHandler.writeRawDataFileAsXML(prawnFile, retVal[1]);
        } catch (JAXBException jAXBException) {
        }

        // restore list
        runs.clear();
        // preserve order
        for (Run runF : first) {
            runs.add(runF);
        }
        for (Run runS : second) {
            runs.add(runS);
        }
        prawnFile.setRuns((short) runs.size());

        return retVal;
    }

    /**
     * @return the prawnFileHandler
     */
    public PrawnFileHandler getPrawnFileHandler() {
        return prawnFileHandler;
    }

    /**
     * @param prawnFileHandler the prawnFileHandler to set
     */
    public void setPrawnFileHandler(PrawnFileHandler prawnFileHandler) {
        this.prawnFileHandler = prawnFileHandler;
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the analystName
     */
    public String getAnalystName() {
        return analystName;
    }

    /**
     * @param analystName the analystName to set
     */
    public void setAnalystName(String analystName) {
        this.analystName = analystName;
    }

    /**
     * @return the projectNotes
     */
    public String getProjectNotes() {
        return projectNotes;
    }

    /**
     * @param projectNotes the projectNotes to set
     */
    public void setProjectNotes(String projectNotes) {
        this.projectNotes = projectNotes;
    }

    /**
     * @return the filterForRefMatSpotNames
     */
    public String getFilterForRefMatSpotNames() {
        return filterForRefMatSpotNames;
    }

    /**
     * @param filterForRefMatSpotNames the filterForRefMatSpotNames to set
     */
    public void setFilterForRefMatSpotNames(String filterForRefMatSpotNames) {
        this.filterForRefMatSpotNames = filterForRefMatSpotNames;
    }

    /**
     * @return shrimpRunsRefMat - list of reference material runs
     */
    public List<Run> getShrimpRunsRefMat() {
        return shrimpRunsRefMat;
    }

    /**
     * @param shrimpRunsRefMat the shrimpRunsRefMat to set
     */
    public void setShrimpRunsRefMat(List<Run> shrimpRunsRefMat) {
        this.shrimpRunsRefMat = shrimpRunsRefMat;
    }

    /**
     * @return the sessionDurationHours
     */
    public double getSessionDurationHours() {
        return sessionDurationHours;
    }

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    public Map<Integer, MassStationDetail> getMapOfIndexToMassStationDetails() {
        return mapOfIndexToMassStationDetails;
    }

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    public List<MassStationDetail> makeListOfMassStationDetails() {
        List<MassStationDetail> listOfassStationDetails = new ArrayList<>();
        for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
            listOfassStationDetails.add(entry.getValue());
        }
        return listOfassStationDetails;
    }

    /**
     * @return the prefixTree
     */
    public SquidPrefixTree getPrefixTree() {
        return prefixTree;
    }

    /**
     * @return the squidSessionModel
     */
    public SquidSessionModel getSquidSessionModel() {
        return squidSessionModel;
    }

    /**
     * @param squidSessionModel the squidSessionModel to set
     */
    public void setSquidSessionModel(SquidSessionModel squidSessionModel) {
        this.squidSessionModel = squidSessionModel;
    }

    /**
     * @return the squidSpeciesModelList
     */
    public List<SquidSpeciesModel> getSquidSpeciesModelList() {
        return squidSpeciesModelList;
    }

    /**
     * @return the squidRatiosModelList
     */
    public List<SquidRatiosModel> getSquidRatiosModelList() {
        return squidRatiosModelList;
    }

    /**
     * @return the tableOfSelectedRatiosByMassStationIndex
     */
    public boolean[][] getTableOfSelectedRatiosByMassStationIndex() {
        return tableOfSelectedRatiosByMassStationIndex;
    }

    public void resetTableOfSelectedRatiosByMassStationIndex() {
        tableOfSelectedRatiosByMassStationIndex = new boolean[squidSpeciesModelList.size()][squidSpeciesModelList.size()];
    }

    /**
     * @return the taskSquid25
     */
    public TaskSquid25 getTaskSquid25() {
        return taskSquid25;
    }
}
