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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.core.PrawnFileHandler;
import org.cirdles.calamari.prawn.PrawnFile;
import org.cirdles.calamari.prawn.PrawnFile.Run;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.exceptions.SquidException;
import org.xml.sax.SAXException;
import org.cirdles.squid.utilities.SquidPrefixTree;
import org.cirdles.squid.utilities.SquidSerializer;

/**
 *
 * @author bowring
 */
public class SquidProject implements Serializable {

    private static final long serialVersionUID = 7099919411562934142L;

    private transient PrawnFileHandler prawnFileHandler;
    private String projectName;
    private String analystName;
    private File prawnXMLFile;
    private PrawnFile prawnFile;
    private String filterForRefMatSpotNames;
    private List<Run> shrimpRunsRefMat;
    private double sessionDurationHours;

    public SquidProject() {
        this.prawnFileHandler = new PrawnFileHandler();
        this.projectName = "NO_NAME";
        this.prawnXMLFile = new File("");
        this.prawnFile = null;

        this.filterForRefMatSpotNames = "";

        this.shrimpRunsRefMat = new ArrayList<>();
        this.sessionDurationHours = 0.0;
    }

    public void serializeSquidProject(String projectFileName) {
        try {
            SquidSerializer.SerializeObjectToFile(this, projectFileName);
        } catch (SquidException ex) {
            SquidMessageDialog.showWarningDialog(ex.getMessage(), null);
        }
    }

    public static String selectProjectFile(Window ownerWindow)
            throws IOException {
        String retVal = "";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Project '.squid' file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid Project files", "*.squid"));
        fileChooser.setInitialDirectory(null);

        File projectFileNew = fileChooser.showOpenDialog(ownerWindow);

        if (projectFileNew != null) {
            retVal = projectFileNew.getCanonicalPath();
        }

        return retVal;
    }

    public boolean saveProjectFile(Window ownerWindow)
            throws IOException {

        boolean retVal = false;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project '.squid' file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid Project files", "*.squid"));
        fileChooser.setInitialDirectory(null);
        fileChooser.setInitialFileName(projectName.toUpperCase(Locale.US) + ".squid");

        File projectFileNew = fileChooser.showSaveDialog(ownerWindow);

        if (projectFileNew != null) {
            retVal = true;
            serializeSquidProject(projectFileNew.getCanonicalPath());
        }

        return retVal;
    }

    public boolean selectPrawnFile(Window ownerWindow)
            throws IOException, JAXBException, SAXException {
        boolean retVal = false;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Prawn XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        fileChooser.setInitialDirectory(prawnFileHandler.currentPrawnFileLocationFolder());

        File prawnXMLFileNew = fileChooser.showOpenDialog(ownerWindow);

        if (prawnXMLFileNew != null) {
            retVal = true;
            prawnXMLFile = prawnXMLFileNew;
            updatePrawnFileHandlerWithFileLocation();
            prawnFile = prawnFileHandler.unmarshallCurrentPrawnFileXML();
        }

        return retVal;
    }

    public boolean selectAndMergeTwoPrawnFile(Window ownerWindow)
            throws IOException, JAXBException, SAXException {
        boolean retVal = false;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Two Prawn XML files");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        fileChooser.setInitialDirectory(prawnFileHandler.currentPrawnFileLocationFolder());

        List<File> prawnXMLFilesNew = fileChooser.showOpenMultipleDialog(ownerWindow);

        if (prawnXMLFilesNew != null) {
            if (prawnXMLFilesNew.size() == 2) {

                PrawnFile prawnFile1 = prawnFileHandler.unmarshallPrawnFileXML(prawnXMLFilesNew.get(0).getCanonicalPath());
                PrawnFile prawnFile2 = prawnFileHandler.unmarshallPrawnFileXML(prawnXMLFilesNew.get(1).getCanonicalPath());

                long start1 = timeInMillisecondsOfRun(prawnFile1.getRun().get(0));
                long start2 = timeInMillisecondsOfRun(prawnFile2.getRun().get(0));

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
                serializePrawnData(prawnFileHandler.getCurrentPrawnFileLocation());
                prawnFile = prawnFileHandler.unmarshallCurrentPrawnFileXML();

                retVal = true;
            }
        }

        return retVal;
    }

    public void updatePrawnFileHandlerWithFileLocation()
            throws IOException {
        prawnFileHandler.setCurrentPrawnFileLocation(prawnXMLFile.getCanonicalPath());
    }

    public boolean savePrawnFile(Window ownerWindow)
            throws IOException, JAXBException, SAXException {

        preProcessPrawnSession();

        boolean retVal = false;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Prawn XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        fileChooser.setInitialDirectory(prawnFileHandler.currentPrawnFileLocationFolder());
        fileChooser.setInitialFileName(prawnXMLFile.getName().toUpperCase(Locale.US).replace(".XML", "-REV.xml"));

        File prawnXMLFileNew = fileChooser.showSaveDialog(ownerWindow);

        if (prawnXMLFileNew != null) {
            retVal = true;
            prawnXMLFile = prawnXMLFileNew;
            prawnFileHandler.setCurrentPrawnFileLocation(prawnXMLFile.getCanonicalPath());
            serializePrawnData(prawnFileHandler.getCurrentPrawnFileLocation());
        }

        return retVal;
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

    public String getPrawnFileShrimpSoftwareVersionName() {
        return prawnFile.getSoftwareVersion();
    }

    public List<Run> getPrawnFileRuns() {
        return prawnFile.getRun();
    }

    public void preProcessPrawnSession() {

        List<Run> runs = prawnFile.getRun();
        Map<String, Integer> spotNameCountMap = new HashMap<>();
        for (int i = 0; i < runs.size(); i++) {
            String spotName = runs.get(i).getPar().get(0).getValue();
            if (spotNameCountMap.containsKey(spotName)) {
                int count = spotNameCountMap.get(spotName);
                count++;
                spotNameCountMap.put(spotName, count);
                runs.get(i).getPar().get(0).setValue(spotName + "-DUP-" + count);
            } else {
                spotNameCountMap.put(spotName, 0);
            }
        }

        // determine time in hours for session
        long startFirst = timeInMillisecondsOfRun(runs.get(0));
        long startLast = timeInMillisecondsOfRun(runs.get(runs.size() - 1));
        long sessionDuration = startLast - startFirst;

        sessionDurationHours = (double) sessionDuration / 1000 / 60 / 60;

    }

    private long timeInMillisecondsOfRun(Run run) {
        String startDateTime = run.getSet().getPar().get(0).getValue()
                + " " + run.getSet().getPar().get(1).getValue()
                + (Integer.parseInt(run.getSet().getPar().get(1).getValue().substring(0, 2)) < 12 ? " AM" : " PM");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
        long milliseconds = 0l;
        try {
            milliseconds = dateFormat.parse(startDateTime).getTime();
        } catch (ParseException parseException) {
        }

        return milliseconds;
    }

    public void removeRunFromPrawnFile(Run run) {
        prawnFile.getRun().remove(run);

        // save new count
        prawnFile.setRuns((short) prawnFile.getRun().size());
    }

    public SquidPrefixTree generatePrefixTreeFromSpotNames() {
        SquidPrefixTree prefixTree = new SquidPrefixTree();

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
        List<Run> runsCopy;

        if (useOriginalData) {
            PrawnFile prawnFileOriginal = null;
            try {
                prawnFileOriginal = deserializePrawnData();
            } catch (IOException | JAXBException | SAXException iOException) {
            }
            runsCopy = new CopyOnWriteArrayList<>(prawnFileOriginal.getRun());
        } else {
            runsCopy = new CopyOnWriteArrayList<>(runs);
        }

        List<Run> first = runsCopy.subList(0, indexOfRun);
        List<Run> second = runsCopy.subList(indexOfRun, runs.size());

        // keep first
        runs.clear();
        runs.addAll(first);
        prawnFile.setRuns((short) runs.size());
        try {
            prawnFileHandler.writeRawDataFileAsXML(prawnFile, retVal[0]);
        } catch (JAXBException jAXBException) {
        }

        runs.clear();
        runs.addAll(second);
        prawnFile.setRuns((short) runs.size());
        try {
            prawnFileHandler.writeRawDataFileAsXML(prawnFile, retVal[1]);
        } catch (JAXBException jAXBException) {
        }

        // restore list
        runs.addAll(first);
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

}
