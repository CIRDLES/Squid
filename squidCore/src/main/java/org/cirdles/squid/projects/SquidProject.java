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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.JAXBException;
import org.cirdles.squid.core.PrawnFileHandler;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.squidTask25.TaskSquid25;
import org.cirdles.squid.tasks.builtinTasks.Squid3ExampleTask1;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.squidTask25.TaskSquid25Equation;
import org.xml.sax.SAXException;
import org.cirdles.squid.utilities.SquidPrefixTree;
import org.cirdles.squid.utilities.fileUtilities.PrawnFileUtilities;

/**
 *
 * @author bowring
 */
public final class SquidProject implements Serializable {

    private static final long serialVersionUID = 7099919411562934142L;

    private transient SquidPrefixTree prefixTree;

    private PrawnFileHandler prawnFileHandler;
    private String projectName;
    private String analystName;
    private String projectNotes;
    private File prawnXMLFile;
    private PrawnFile prawnFile;
    private String filterForRefMatSpotNames;
    private double sessionDurationHours;
    private TaskInterface task;

    public SquidProject() {
        this.prawnFileHandler = new PrawnFileHandler();
        this.projectName = "NO_NAME";
        this.prawnXMLFile = new File("");
        this.prawnFile = null;

        this.filterForRefMatSpotNames = "";

        this.sessionDurationHours = 0.0;

        this.task = new Task();
    }

    public Map< String, TaskInterface> getTaskLibrary() {
        Map< String, TaskInterface> builtInTasks = new HashMap<>();

        TaskInterface builtInTask = new Squid3ExampleTask1();
        builtInTasks.put(builtInTask.getName(), builtInTask);

        return builtInTasks;
    }

    public void loadAndInitializeTask(TaskInterface task) {
        this.task = task;
        initializeTaskAndReduceData();
    }

    public void initializeTaskAndReduceData() {
        if (task != null) {
            task.setPrawnFile(prawnFile);
            task.setReportsEngine(prawnFileHandler.getReportsEngine());
            task.setFilterForRefMatSpotNames(filterForRefMatSpotNames);
            task.setupSquidSessionSpecsAndReduceAndReport();
        }
    }

    public void createNewTask() {
        this.task = new Task(
                "New Task", prawnFile, filterForRefMatSpotNames, prawnFileHandler.getNewReportsEngine());
        this.task.setChanged(true);
        initializeTaskAndReduceData();
    }

    public void createTaskFromImportedSquid25Task(File squidTaskFile) {

        TaskSquid25 taskSquid25 = TaskSquid25.importSquidTaskFile(squidTaskFile);

        this.task = new Task(
                taskSquid25.getTaskName(), prawnFile, filterForRefMatSpotNames, prawnFileHandler.getNewReportsEngine());
        this.task.setType(taskSquid25.getTaskType());
        this.task.setDescription(taskSquid25.getTaskDescription());
        this.task.setProvenance(taskSquid25.getSquidTaskFileName());
        this.task.setAuthorName(taskSquid25.getAuthorName());
        this.task.setLabName(taskSquid25.getLabName());
        this.task.setRatioNames(taskSquid25.getRatioNames());
        this.task.setFilterForRefMatSpotNames(filterForRefMatSpotNames);

        // first pass
        this.task.setupSquidSessionSpecsAndReduceAndReport();

        List<TaskSquid25Equation> task25Equations = taskSquid25.getTask25Equations();
        for (TaskSquid25Equation task25Eqn : task25Equations) {
            Expression expression = this.task.generateExpressionFromRawExcelStyleText(
                    task25Eqn.getEquationName(), task25Eqn.getExcelEquationString());

            ExpressionTreeInterface expressionTree = expression.getExpressionTree();
            System.out.println(">>>>>   " + expressionTree.getName());
            expressionTree.setSquidSwitchSTReferenceMaterialCalculation(task25Eqn.isEqnSwitchST());
            expressionTree.setSquidSwitchSAUnknownCalculation(task25Eqn.isEqnSwitchSA());
            expressionTree.setSquidSwitchSCSummaryCalculation(task25Eqn.isEqnSwitchSC());

            this.task.getTaskExpressionsOrdered().add(expression);
        }

        this.task.setChanged(true);
        this.task.updateExpressions(2);
        initializeTaskAndReduceData();

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
     * @return the sessionDurationHours
     */
    public double getSessionDurationHours() {
        return sessionDurationHours;
    }

    /**
     * @return the prefixTree
     */
    public SquidPrefixTree getPrefixTree() {
        return prefixTree;
    }

    /**
     * @return the task
     */
    public TaskInterface getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(TaskInterface task) {
        this.task = task;
    }

}
