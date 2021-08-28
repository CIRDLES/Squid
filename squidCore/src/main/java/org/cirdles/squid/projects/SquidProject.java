/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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

import org.cirdles.squid.Squid;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.SpotTypes;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import org.cirdles.squid.core.PrawnXMLFileHandler;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.op.OPFileHandler;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.reports.reportSettings.ReportSettings;
import org.cirdles.squid.reports.reportSettings.ReportSettingsInterface;
import org.cirdles.squid.shrimp.ShrimpDataFileInterface;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.ExpressionSpecInterface;
import org.cirdles.squid.tasks.squidTask25.TaskSquid25;
import org.cirdles.squid.tasks.squidTask25.TaskSquid25Equation;
import org.cirdles.squid.tasks.taskDesign.TaskDesign;
import org.cirdles.squid.utilities.IntuitiveStringComparator;
import org.cirdles.squid.utilities.fileUtilities.PrawnFileUtilities;
import org.cirdles.squid.utilities.squidPrefixTree.SquidPrefixTree;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.cirdles.squid.constants.Squid3Constants.DUPLICATE_STRING;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GEOCHRON;
import static org.cirdles.squid.tasks.expressions.ExpressionSpec.specifyConstantExpression;

/**
 * @author bowring
 */
public final class SquidProject implements Squid3ProjectBasicAPI, Squid3ProjectReportingAPI, Squid3ProjectParametersAPI {

    private static final long serialVersionUID = 7099919411562934142L;
    public static transient boolean sampleNamingNotStandard = false;
    private static boolean projectChanged;
    private transient SquidPrefixTree prefixTree;
    private PrawnXMLFileHandler prawnFileHandler;
    private String projectName;
    private String analystName;
    private String projectNotes;
    private File prawnSourceFile;
    private ShrimpDataFileInterface prawnFile;
    private String filterForRefMatSpotNames;
    private String filterForConcRefMatSpotNames;
    private double sessionDurationHours;
    private TaskInterface task;
    private Map<String, Integer> filtersForUnknownNames;
    private String delimiterForUnknownNames;
    private ParametersModel referenceMaterialModel;
    private ParametersModel concentrationReferenceMaterialModel;
    //Spring 2020 adding parameters to project from task ***********************
    private ParametersModel physicalConstantsModel;
    private ParametersModel commonPbModel;
    private boolean squidAllowsAutoExclusionOfSpots;
    // MIN_206PB238U_EXT_1SIGMA_ERR_PCT
    private double extPErrU;
    // MIN_208PB232TH_EXT_1SIGMA_ERR_PCT
    private double extPErrTh;
    private Squid3Constants.IndexIsoptopesEnum selectedIndexIsotope;
    private boolean useSBM;
    private boolean userLinFits;
    private TaskTypeEnum projectType;
    // jan 2021 issue #547
    private List<Run> removedRuns;

    /**
     * @param projectType the value of projectType
     */
    public SquidProject(TaskTypeEnum projectType) {
        this.projectType = projectType;
        this.prawnFileHandler = new PrawnXMLFileHandler(this);
        this.projectName = "NO_NAME";
        this.prawnSourceFile = new File("");
        this.prawnFile = null;

        this.filterForRefMatSpotNames = "";
        this.filterForConcRefMatSpotNames = "";

        this.sessionDurationHours = 0.0;

        SquidProject.projectChanged = false;

        this.referenceMaterialModel = new ReferenceMaterialModel();
        this.concentrationReferenceMaterialModel = new ReferenceMaterialModel();

        // new fields from migration to project of parameters spring 2020
        TaskDesign taskDesignDefault = SquidPersistentState.getExistingPersistentState().getTaskDesign();
        this.physicalConstantsModel = taskDesignDefault.getPhysicalConstantsModel();
        this.commonPbModel = taskDesignDefault.getCommonPbModel();
        this.squidAllowsAutoExclusionOfSpots = taskDesignDefault.isSquidAllowsAutoExclusionOfSpots();
        this.extPErrU = taskDesignDefault.getExtPErrU();
        this.extPErrTh = taskDesignDefault.getExtPErrTh();
        this.selectedIndexIsotope = taskDesignDefault.getSelectedIndexIsotope();
        this.useSBM = taskDesignDefault.isUseSBM();
        this.userLinFits = taskDesignDefault.isUserLinFits();
        this.analystName = taskDesignDefault.getAnalystName();

        this.task = new Task("New Task", prawnFileHandler.getNewReportsEngine());
        this.task.setTaskType(projectType);
        this.task.setReferenceMaterialModel(this.referenceMaterialModel);
        this.task.setConcentrationReferenceMaterialModel(this.concentrationReferenceMaterialModel);
        this.task.setPhysicalConstantsModel(physicalConstantsModel);
        this.task.setCommonPbModel(commonPbModel);

        this.filtersForUnknownNames = new HashMap<>();
        this.delimiterForUnknownNames
                = SquidPersistentState.getExistingPersistentState().getTaskDesign().getDelimiterForUnknownNames();

        this.removedRuns = new ArrayList<>();

    }

    /**
     * @param projectName the value of projectName
     * @param projectType the value of projectType
     */
    public SquidProject(String projectName, TaskTypeEnum projectType) {
        this(projectType);
        this.projectName = projectName;
    }

    public static String generateDateTimeMillisecondsStringForRun(Run run) {
        return run.getSet().getPar().get(0).getValue()
                + " " + run.getSet().getPar().get(1).getValue()
                + (Integer.parseInt(run.getSet().getPar().get(1).getValue().substring(0, 2)) < 12 ? " AM" : " PM");
    }

    /**
     * @return the projectChanged
     */
    public static boolean isProjectChanged() {
        return projectChanged;
    }

    /**
     * @param aProjectChanged the projectChanged to set
     */
    public static void setProjectChanged(boolean aProjectChanged) {
        projectChanged = aProjectChanged;
    }

    @Override
    public List<Run> getRemovedRuns() {
        if (removedRuns == null) {
            removedRuns = new ArrayList<>();
        }
        return removedRuns;
    }

    /**
     * @return the projectType
     */
    @Override
    public TaskTypeEnum getProjectType() {
        if (projectType == null) {
            projectType = GEOCHRON;
        }
        return projectType;
    }

    @Override
    public boolean isTypeGeochron() {
        return (projectType.equals(GEOCHRON));
    }

    /**
     * @param autoGenerateNominalMasses the value of autoGenerateNominalMasses
     */
    public void initializeTaskAndReduceData(boolean autoGenerateNominalMasses) {
        if (task != null) {
            task.setPrawnFile(prawnFile);
            task.setReportsEngine(prawnFileHandler.getReportsEngine());
            task.setFilterForRefMatSpotNames(filterForRefMatSpotNames);
            task.setFilterForConcRefMatSpotNames(filterForConcRefMatSpotNames);
            task.setFiltersForUnknownNames(filtersForUnknownNames);

            ((Task) task).initializeTaskAndReduceData(autoGenerateNominalMasses);
        }
    }

    public void createNewTask() {
        this.task = new Task(
                "New Task", prawnFile, prawnFileHandler.getNewReportsEngine());

        this.task.setDelimiterForUnknownNames(delimiterForUnknownNames);
        this.task.setFilterForConcRefMatSpotNames(filterForConcRefMatSpotNames);
        this.task.setFilterForRefMatSpotNames(filterForRefMatSpotNames);
        this.task.setFiltersForUnknownNames(filtersForUnknownNames);
        this.task.setCommonPbModel(commonPbModel);
        this.task.setPhysicalConstantsModel(physicalConstantsModel);
        this.task.setSelectedIndexIsotope(selectedIndexIsotope);
        this.task.setSquidAllowsAutoExclusionOfSpots(squidAllowsAutoExclusionOfSpots);
        this.task.setExtPErrU(extPErrU);
        this.task.setExtPErrTh(extPErrTh);

        this.task.setReferenceMaterialModel(referenceMaterialModel);
        this.task.setConcentrationReferenceMaterialModel(concentrationReferenceMaterialModel);
        this.task.setChanged(true);
        this.task.applyDirectives();
        initializeTaskAndReduceData(false);
    }

    public TaskInterface makeTaskFromSquid25Task(TaskSquid25 taskSquid25) {
        TaskInterface taskFromSquid25 = new Task(taskSquid25.getTaskName());

        taskFromSquid25.setTaskType(taskSquid25.getTaskType());
        taskFromSquid25.setDescription(taskSquid25.getTaskDescription());
        taskFromSquid25.setProvenance(taskSquid25.getSquidTaskFileName());
        taskFromSquid25.setAuthorName(taskSquid25.getAuthorName());
        taskFromSquid25.setLabName(taskSquid25.getLabName());
        taskFromSquid25.setNominalMasses(taskSquid25.getNominalMasses());
        taskFromSquid25.setRatioNames(taskSquid25.getRatioNames());
        taskFromSquid25.setFilterForRefMatSpotNames(filterForRefMatSpotNames);
        taskFromSquid25.setFilterForConcRefMatSpotNames(filterForConcRefMatSpotNames);
        taskFromSquid25.setFiltersForUnknownNames(filtersForUnknownNames);
        taskFromSquid25.setParentNuclide(taskSquid25.getParentNuclide());
        taskFromSquid25.setDirectAltPD(taskSquid25.isDirectAltPD());

        taskFromSquid25.setExtPErrTh(extPErrTh);
        taskFromSquid25.setExtPErrU(extPErrU);
        taskFromSquid25.setSelectedIndexIsotope(selectedIndexIsotope);
        taskFromSquid25.setSquidAllowsAutoExclusionOfSpots(squidAllowsAutoExclusionOfSpots);
        taskFromSquid25.setUseSBM(useSBM);
        taskFromSquid25.setUserLinFits(userLinFits);

        taskFromSquid25.setReferenceMaterialModel(referenceMaterialModel);
        taskFromSquid25.setConcentrationReferenceMaterialModel(concentrationReferenceMaterialModel);

        // determine index of background mass as specified in task
        for (int i = 0; i < taskSquid25.getNominalMasses().size(); i++) {
            if (taskSquid25.getNominalMasses().get(i).compareToIgnoreCase(taskSquid25.getBackgroundMass()) == 0) {
                taskFromSquid25.setIndexOfTaskBackgroundMass(i);
                taskFromSquid25.setIndexOfBackgroundSpecies(i);
                break;
            }
        }

        List<TaskSquid25Equation> task25Equations = taskSquid25.getTask25Equations();
        for (TaskSquid25Equation task25Eqn : task25Equations) {
            ((Task) taskFromSquid25).makeCustomExpression(task25Eqn);
        }

        List<String> constantNames = taskSquid25.getConstantNames();
        List<String> constantValues = taskSquid25.getConstantValues();
        for (int i = 0; i < constantNames.size(); i++) {

            // March 2019 moved imported constants to be custom expressions
            ExpressionSpecInterface constantSpec = specifyConstantExpression(
                    constantNames.get(i),
                    constantValues.get(i),
                    "Custom constant imported from Squid2 task " + taskSquid25.getTaskName() + " ."
            );
            ((Task) taskFromSquid25).makeCustomExpression(constantSpec);
        }

        taskFromSquid25.setSpecialSquidFourExpressionsMap(taskSquid25.getSpecialSquidFourExpressionsMap());

        return taskFromSquid25;
    }

    public void replaceCurrentTaskWithImportedSquid25Task(File squidTaskFile) {

        TaskSquid25 taskSquid25 = TaskSquid25.importSquidTaskFile(squidTaskFile);

        // if Task is short of nominal masses, add them
        int prawnSpeciesCount = Integer.parseInt(prawnFile.getRun().get(0).getPar().get(2).getValue());
        if (prawnSpeciesCount != taskSquid25.getNominalMasses().size()) {
            for (int i = 0; i < (prawnSpeciesCount - taskSquid25.getNominalMasses().size()); i++) {
                taskSquid25.getNominalMasses().add("DUMMY" + (i + 1));
            }
        }

        // need to remove stored expression results on fractions to clear the decks
        task.getShrimpFractions().forEach((spot) -> {
            spot.getTaskExpressionsForScansEvaluated().clear();
            spot.getTaskExpressionsEvaluationsPerSpot().clear();
            spot.getTaskExpressionsMetaDataPerSpot().clear();
        });

        task = makeTaskFromSquid25Task(taskSquid25);

        task.setPrawnFile(prawnFile);
        task.setReportsEngine(prawnFileHandler.getNewReportsEngine());

        // first pass
        task.setChanged(true);
        task.setupSquidSessionSpecsAndReduceAndReport(false);
        this.task.applyDirectives();

        initializeTaskAndReduceData(false);
    }

    public boolean setupPrawnOPFile(File opFileNew)
            throws IOException {

        boolean retVal = false;
        prawnSourceFile = opFileNew;
        updatePrawnFileHandlerWithFileLocation();

        OPFileHandler opFileHandler = new OPFileHandler();
        prawnFile = opFileHandler.convertOPFileToPrawnFile(opFileNew);
        removedRuns = new ArrayList<>();

        if (prawnFileExists()) {
            retVal = true;
            task.setPrawnFile(prawnFile);
            ((Task) task).setupSquidSessionSkeleton();
        }

        return retVal;
    }

    public boolean setupPrawnXMLFile(File prawnXMLFileNew)
            throws IOException, JAXBException, SAXException, SquidException {

        boolean retVal = false;
        prawnSourceFile = prawnXMLFileNew;
        updatePrawnFileHandlerWithFileLocation();

        // determine whether legacy or modern Prawn XML file
        // if Legacy, then it has to be read in and translated to modern by unmarshall
        prawnFile = prawnFileHandler.unmarshallCurrentPrawnFileXML();
        removedRuns = new ArrayList<>();

        if (prawnFileExists()) {
            retVal = true;
            task.setPrawnFile(prawnFile);
            ((Task) task).setupSquidSessionSkeleton();
        }

        return retVal;
    }

    public boolean setupPrawnXMLFileByJoin(List<File> prawnXMLFilesNew)
            throws IOException, JAXBException, SAXException, SquidException {

        if (prawnXMLFilesNew.size() == 2) {
            ShrimpDataFileInterface prawnFile1 = prawnFileHandler.unmarshallPrawnFileXML(prawnXMLFilesNew.get(0).getCanonicalPath(), false);
            ShrimpDataFileInterface prawnFile2 = prawnFileHandler.unmarshallPrawnFileXML(prawnXMLFilesNew.get(1).getCanonicalPath(), false);

            if ((prawnFile1 != null) && (prawnFile2 != null)) {
                long start1 = PrawnFileUtilities.timeInMillisecondsOfRun(prawnFile1.getRun().get(0));
                long start2 = PrawnFileUtilities.timeInMillisecondsOfRun(prawnFile2.getRun().get(0));

                if (start1 > start2) {
                    prawnFile2.getRun().addAll(prawnFile1.getRun());
                    prawnFile2.setRuns((short) prawnFile2.getRun().size());
                    prawnSourceFile = new File(
                            prawnXMLFilesNew.get(1).getName().replace(".xml", "").replace(".XML", "")
                                    + "-JOIN-"
                                    + prawnXMLFilesNew.get(0).getName().replace(".xml", "").replace(".XML", "") + ".xml");
                    prawnFile = prawnFile2;
                } else {
                    prawnFile1.getRun().addAll(prawnFile2.getRun());
                    prawnFile1.setRuns((short) prawnFile1.getRun().size());
                    prawnSourceFile = new File(
                            prawnXMLFilesNew.get(0).getName().replace(".xml", "").replace(".XML", "")
                                    + "-JOIN-"
                                    + prawnXMLFilesNew.get(1).getName().replace(".xml", "").replace(".XML", "") + ".xml");
                    prawnFile = prawnFile1;
                }

                updatePrawnFileHandlerWithFileLocation();
                // write and read merged file to confirm conforms to schema
                serializePrawnData(prawnFileHandler.getCurrentPrawnSourceFileLocation());
                prawnFile = prawnFileHandler.unmarshallCurrentPrawnFileXML();
                removedRuns = new ArrayList<>();

                if (prawnFileExists()) {
                    task.setPrawnFile(prawnFile);
                    ((Task) task).setupSquidSessionSkeleton();
                }
            } else {
                prawnFile = null;
            }
        } else {
            throw new IOException("Two files not present");
        }

        return prawnFileExists();
    }

    public void updatePrawnFileHandlerWithFileLocation()
            throws IOException {
        prawnFileHandler.setCurrentPrawnSourceFileLocation(prawnSourceFile.getCanonicalPath());
    }

    public void savePrawnXMLFile(File prawnXMLFileNew)
            throws IOException, JAXBException, SAXException {

        preProcessPrawnSession();

        prawnSourceFile = prawnXMLFileNew;
        prawnFileHandler.setCurrentPrawnSourceFileLocation(prawnSourceFile.getCanonicalPath());
        serializePrawnData(prawnFileHandler.getCurrentPrawnSourceFileLocation());
    }

    @Override
    public boolean prawnFileExists() {
        return prawnFile != null;
    }

    public ShrimpDataFileInterface deserializePrawnData()
            throws IOException, JAXBException, SAXException, SquidException {
        return prawnFileHandler.unmarshallCurrentPrawnFileXML();
    }

    private void serializePrawnData(String fileName)
            throws IOException, JAXBException {
        prawnFileHandler.writeRawDataFileAsXML(prawnFile, fileName);
    }

    /**
     * First guess using default delimiter
     */
    public void autoDivideSamples() {
        this.filtersForUnknownNames = new HashMap<>();
        String firstFractionID = prawnFile.getRun().get(0).getPar().get(0).getValue();
        Matcher matcher
                = Pattern.compile("[^a-zA-Z0-9]+").matcher(firstFractionID);
        if (matcher.find()) {
            int s = matcher.start();
            delimiterForUnknownNames = firstFractionID.substring(s, s + 1);
        }
        divideSamples();
        if (sampleNamingNotStandard) {
            sampleNamingNotStandard = false;
            delimiterForUnknownNames = "1";
            divideSamples();
        }
    }

    public void divideSamples() {
        boolean delimiterIsNumber = Squid3Constants.SampleNameDelimitersEnum.getByName(delimiterForUnknownNames.trim()).isNumber();

        // May 2021 - previously this used fractions, but it is the runs that get
        // updated when the names are edited and the fractions are redone later
        List<Run> copyOfRuns = new ArrayList<>(prawnFile.getRun());
        Comparator<String> intuitiveString = new IntuitiveStringComparator<>();
        Collections.sort(copyOfRuns, (Run pt1, Run pt2)
                -> (intuitiveString.compare(pt1.getPar().get(0).getValue(), pt2.getPar().get(0).getValue())));

        filtersForUnknownNames = new HashMap<>();

        for (int i = 0; i < copyOfRuns.size(); i++) {
            String fractionID = copyOfRuns.get(i).getPar().get(0).getValue();

            // determine flavor
            int delimiterIndex;
            if (delimiterIsNumber) {
                delimiterIndex = Integer.parseInt(delimiterForUnknownNames.trim());
            } else {
                delimiterIndex = fractionID.indexOf(delimiterForUnknownNames.trim());
            }
            // we have poor practice of missing delimiter or too few characters
            sampleNamingNotStandard = sampleNamingNotStandard || ((delimiterIndex < 0) || (fractionID.length() < (delimiterIndex)));
            String sampleName = ((delimiterIndex < 0) || (fractionID.length() < (delimiterIndex)))
                    ? fractionID : fractionID
                    .substring(0, (delimiterIndex <= fractionID.length() ? delimiterIndex : fractionID.length()))
                    .toUpperCase(Locale.ENGLISH);
            if (filtersForUnknownNames.containsKey(sampleName)) {
                filtersForUnknownNames.put(sampleName, filtersForUnknownNames.get(sampleName) + 1);
            } else {
                filtersForUnknownNames.put(sampleName, 1);
            }
        }

        task.setDelimiterForUnknownNames(delimiterForUnknownNames);
        task.setFiltersForUnknownNames(filtersForUnknownNames);
        task.generateMapOfUnknownsBySampleNames();
    }

    // reports
    public File produceReferenceMaterialPerSquid25CSV(boolean numberStyleIsNumeric)
            throws IOException {
        File reportTableFile = null;
        if (task.getReferenceMaterialSpots().size() > 0) {
            ReportSettingsInterface reportSettings = new ReportSettings("RefMat", true, task);
            String[][] report = reportSettings.reportFractionsByNumberStyle(task.getReferenceMaterialSpots(), numberStyleIsNumeric);
            reportTableFile = prawnFileHandler.getReportsEngine().writeReportTableFiles(
                    report, projectName + "_RefMatReportTablePerSquid25.csv");
        }
        return reportTableFile;
    }

    public File produceSelectedReferenceMaterialReportCSV()
            throws IOException {
        File reportTableFile = null;
        if (task.getReferenceMaterialSpots().size() > 0) {
            SquidReportTableInterface reportSettings = task.getSelectedRefMatReportModel();
            Map<String, List<ShrimpFractionExpressionInterface>> reportSamplesMap = new HashMap<>();
            reportSamplesMap.put(((Task) task).getFilterForRefMatSpotNames(), task.getReferenceMaterialSpots());
            String[][] report = reportSettings.reportSpotsInCustomTable(reportSettings, task, reportSamplesMap, true);
            reportTableFile = prawnFileHandler.getReportsEngine().writeReportTableFiles(
                    report, (projectName + "_" + reportSettings.getReportTableName()).replaceAll("\\s+", "_") + ".csv");
        }
        return reportTableFile;
    }

    public File produceUnknownsPerSquid25CSV(boolean numberStyleIsNumeric)
            throws IOException {
        File reportTableFile = null;
        if (task.getUnknownSpots().size() > 0) {
            ReportSettingsInterface reportSettings = new ReportSettings("Unknowns", false, task);
            String[][] report = reportSettings.reportFractionsByNumberStyle(task.getUnknownSpots(), numberStyleIsNumeric);
            reportTableFile = prawnFileHandler.getReportsEngine().writeReportTableFiles(
                    report, projectName + "_UnknownsReportTablePerSquid25.csv");
        }
        return reportTableFile;
    }

    public File produceUnknownsBySampleForETReduxCSV(boolean numberStyleIsNumeric)
            throws IOException {
        File reportTableFile = null;

        List<ShrimpFractionExpressionInterface> spotsBySampleNames = makeListOfUnknownsBySampleName();
        if (spotsBySampleNames.size() > 0) {
            ReportSettingsInterface reportSettings = new ReportSettings("UnknownsBySample", false, task);
            String[][] report = reportSettings.reportFractionsByNumberStyle(spotsBySampleNames, numberStyleIsNumeric);
            reportTableFile = prawnFileHandler.getReportsEngine().writeReportTableFiles(
                    report, projectName + "_UnknownsBySampleReportTableForET_ReduxAndTopsoil.csv");
        }
        return reportTableFile;
    }

    public void produceSelectedUnknownsReportCSV()
            throws IOException {
        produceTargetedSelectedUnknownsReportCSV(SpotTypes.UNKNOWN.getSpotTypeName());
    }

    public File produceTargetedSelectedUnknownsReportCSV(String nameOfTargetSample)
            throws IOException {
        File reportTableFile = null;
        if (task.getUnknownSpots().size() > 0) {
            SquidReportTableInterface reportSettings = task.getSelectedUnknownReportModel();
            Map<String, List<ShrimpFractionExpressionInterface>> reportSamplesMap = new HashMap<>();
            if (SpotTypes.UNKNOWN.getSpotTypeName().compareToIgnoreCase(nameOfTargetSample) == 0) {
                for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : task.getMapOfUnknownsBySampleNames().entrySet()) {
                    if (entry.getKey().compareToIgnoreCase(SpotTypes.UNKNOWN.getSpotTypeName()) != 0) {
                        reportSamplesMap.put(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                reportSamplesMap.put(nameOfTargetSample, task.getMapOfUnknownsBySampleNames().get(nameOfTargetSample));
            }
            String[][] report = reportSettings.reportSpotsInCustomTable(reportSettings, task, reportSamplesMap, true);
            reportTableFile = prawnFileHandler.getReportsEngine().writeReportTableFiles(
                    report,
                    (projectName
                            + "_"
                            + reportSettings.getReportTableName()).replaceAll("\\s+", "_")
                            + "_" + ((nameOfTargetSample.equalsIgnoreCase("Unknowns")) ? "ALL" : nameOfTargetSample) + ".csv");
        }
        return reportTableFile;
    }

    public File produceUnknownsWeightedMeanSortingFieldsCSV()
            throws IOException {
        File reportTableFile = null;
        if (task.getUnknownSpots().size() > 0) {
            SquidReportTableInterface reportSettings = task.getSquidReportTablesUnknown().get(1);
            Map<String, List<ShrimpFractionExpressionInterface>> reportSamplesMap = new HashMap<>();
            for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : task.getMapOfUnknownsBySampleNames().entrySet()) {
                if (entry.getKey().compareToIgnoreCase(SpotTypes.UNKNOWN.getSpotTypeName()) != 0) {
                    reportSamplesMap.put(entry.getKey(), entry.getValue());
                }
            }
            String[][] report = reportSettings.reportSpotsInCustomTable(reportSettings, task, reportSamplesMap, true);
            reportTableFile = prawnFileHandler.getReportsEngine().writeReportTableFiles(
                    report, projectName.replaceAll("\\s+", "_") + "_UnknownsWeightedMeansSortingFields.csv");
        }
        return reportTableFile;
    }

    public String printProjectAudit() {
        StringBuilder sb = new StringBuilder();

        sb.append("Project Audit produced by Squid3 v").append(Squid.VERSION).append(" on ").append(LocalDate.now()).append("\n");
        sb.append("Project Name: ").append(projectName).append("\n");
        sb.append("Analyst Name: ").append(analystName).append("\n");
        sb.append("Data File: ").append(prawnFileHandler.getCurrentPrawnSourceFileLocation()).append("\n");
        sb.append("Software: ").append(getPrawnFileShrimpSoftwareVersionName()).append("\n\n");
        sb.append("Session\n");
        sb.append("\tLogin Comment: ").append(getPrawnFileLoginComment()).append("\n");
        sb.append("\tSummary\n");
        sb.append("\t\t").append(generatePrefixTreeFromSpotNames().buildSummaryDataString().replaceAll(";", "\n\t\t")).append("\n");
        sb.append("\tTotal Time in Hours: ").append((int) sessionDurationHours).append("\n\n");
        sb.append("Project Notes:\n").append(projectNotes).append("\n");

        //parameters
        if (task != null) {
            sb.append("\nTask Name: ").append(task.getName());
            sb.append("\nTask Description: ").append(task.getDescription());
            sb.append("\nTask Author: ").append(task.getAuthorName());
            sb.append("\nTask Lab: ").append(task.getLabName());
            sb.append("\nTask Provenance: ").append(task.getProvenance());
            sb.append("\n\nParameters:\n");
            sb.append("\tIon Counts Normalized for SBM: ").append(task.isUseSBM()).append("\n");
            sb.append("\tRatio Calculation Method: ").append((task.isUserLinFits() ? "Linear Regression to Burn Mid-Time" : "Spot Average (time-invariant)")).append("\n");
            sb.append("\tPreferred Index Isotope: ").append(task.getSelectedIndexIsotope().getName()).append("\n");
            sb.append("\tWeighted Means of RefMat:\n");
            sb.append("\t\tAllow Squid3 to Auto Reject Spots: ").append(task.isSquidAllowsAutoExclusionOfSpots()).append("\n");
            sb.append("\t\tMinimum external 1sigma % err for 206Pb/238U: ").append(task.getExtPErrU()).append("\n");
            sb.append("\t\tMinimum external 1sigma % err for 208Pb/232Th: ").append(task.getExtPErrTh()).append("\n");
            sb.append("\tParameter Models:\n");
            sb.append("\t\tDef Comm Pb: ").append(task.getCommonPbModel().getModelNameWithVersion()).append("\n");
            sb.append("\t\tPhys Const: ").append(task.getPhysicalConstantsModel().getModelNameWithVersion()).append("\n");
            sb.append("\t\tRef Mat: ").append(task.getReferenceMaterialModel().getModelNameWithVersion()).append("\n");
            sb.append("\t\tConc Ref Mat: ").append(task.getConcentrationReferenceMaterialModel().getModelNameWithVersion()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Helper method to prepare for reports by sample
     *
     * @return List<ShrimpFractionExpressionInterface>
     */
    public List<ShrimpFractionExpressionInterface> makeListOfUnknownsBySampleName() {
        Map<String, List<ShrimpFractionExpressionInterface>> mapOfUnknownsBySampleNames = task.getMapOfUnknownsBySampleNames();
        List<ShrimpFractionExpressionInterface> listOfUnknownsBySample = new ArrayList<>();

        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfUnknownsBySampleNames.entrySet()) {
            if (entry.getKey().compareToIgnoreCase(SpotTypes.UNKNOWN.getSpotTypeName()) != 0) {
                ShrimpFractionExpressionInterface dummyForSample = new ShrimpFraction(entry.getKey(), new TreeSet<>());
                listOfUnknownsBySample.add(dummyForSample);
                listOfUnknownsBySample.addAll(entry.getValue());
            }
        }

        return listOfUnknownsBySample;
    }

    public boolean projectIsHealthyGeochronMode() {
        return isTypeGeochron() && task.getExpressionByName("ParentElement_ConcenConst").amHealthy();
    }

    @Override
    public String getPrawnSourceFileName() {
        return prawnSourceFile.getName();
    }

    /**
     * @param prawnFile the prawnFile to set
     */
    public void setPrawnFile(ShrimpDataFileInterface prawnFile) {
        this.prawnFile = prawnFile;
    }

    @Override
    public String getPrawnSourceFilePath() {
        return prawnSourceFile.getAbsolutePath();
    }

    @Override
    public String getPrawnFileShrimpSoftwareVersionName() {
        return prawnFile.getSoftwareVersion();
    }

    @Override
    public String getPrawnFileLoginComment() {
        return ((PrawnFile) prawnFile).getLoginComment();
    }

    @Override
    public List<Run> getPrawnFileRuns() {
        return new ArrayList<>(prawnFile.getRun());
    }

    public void processPrawnSessionForDuplicateSpotNames() {
        List<Run> runs = prawnFile.getRun();
        Map<String, Integer> spotNameCountMap = new HashMap<>();
        for (int i = 0; i < runs.size(); i++) {
            String spotName = runs.get(i).getPar().get(0).getValue().trim();
            String spotNameKey = spotName.toUpperCase(Locale.ENGLISH);
            // remove existing duplicate label in case editing occurred
            int indexDUP = spotName.indexOf("-DUP");
            if (indexDUP > 0) {
                runs.get(i).getPar().get(0).setValue(spotName.substring(0, spotName.indexOf("-DUP")));
                spotName = runs.get(i).getPar().get(0).getValue();
                spotNameKey = spotName.toUpperCase(Locale.ENGLISH);
            }
            if (spotNameCountMap.containsKey(spotNameKey)) {
                int count = spotNameCountMap.get(spotNameKey);
                count++;
                spotNameCountMap.put(spotNameKey, count);
                runs.get(i).getPar().get(0).setValue(spotName + DUPLICATE_STRING + count);
            } else {
                spotNameCountMap.put(spotNameKey, 0);
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

    public void removeRunsFromPrawnFile(List<Run> runs) {

        prawnFile.getRun().removeAll(runs);
        removedRuns.addAll(runs);

        // save new count
        prawnFile.setRuns((short) prawnFile.getRun().size());

        // update fractions
        ((Task) task).setupSquidSessionSkeleton();
    }

    public void restoreRunToPrawnFile(Run run) {
        prawnFile.getRun().add(run);
        removedRuns.remove(run);

        updatePrawnRunsSorting();
        // save new count
        prawnFile.setRuns((short) prawnFile.getRun().size());

        // update fractions
        ((Task) task).setupSquidSessionSkeleton();
    }

    public void restoreAllRunsToPrawnFile() {

        prawnFile.getRun().addAll(removedRuns);
        removedRuns.clear();

        updatePrawnRunsSorting();

        // save new count
        prawnFile.setRuns((short) prawnFile.getRun().size());

        // update fractions
        ((Task) task).setupSquidSessionSkeleton();
    }

    private void updatePrawnRunsSorting() {
        Collections.sort(prawnFile.getRun(), new Comparator<Run>() {
            @Override
            public int compare(Run run1, Run run2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
                String run1DateTime = generateDateTimeMillisecondsStringForRun(run1);
                String run2DateTime = generateDateTimeMillisecondsStringForRun(run2);

                long run1DateTimeMilliseconds = 0l;
                long run2DateTimeMilliseconds = 0l;
                try {
                    run1DateTimeMilliseconds = dateFormat.parse(run1DateTime).getTime();
                    run2DateTimeMilliseconds = dateFormat.parse(run2DateTime).getTime();
                } catch (ParseException parseException) {
                }

                return Long.compare(run1DateTimeMilliseconds, run2DateTimeMilliseconds);
            }
        });
    }

    public SquidPrefixTree generatePrefixTreeFromSpotNames() {
        prefixTree = new SquidPrefixTree();

        List<Run> copyOfRuns = new ArrayList<>(prawnFile.getRun());
        Comparator<String> intuitiveString = new IntuitiveStringComparator<>();
        Collections.sort(copyOfRuns, (Run pt1, Run pt2)
                -> (intuitiveString.compare(pt1.getPar().get(0).getValue(), pt2.getPar().get(0).getValue())));

        for (int i = 0; i < copyOfRuns.size(); i++) {
            SquidPrefixTree leafParent = prefixTree.insert(copyOfRuns.get(i).getPar().get(0).getValue());
            leafParent.getChildren().get(0).setCountOfSpecies(Integer.parseInt(copyOfRuns.get(i).getPar().get(2).getValue()));
            leafParent.getChildren().get(0).setCountOfScans(Integer.parseInt(copyOfRuns.get(i).getPar().get(3).getValue()));
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
     *                         otherwise the edited file is used.
     * @return String [2] containing the file names of the two Prawn XML files
     * written as a result of the split.
     */
    public String[] splitPrawnFileAtRun(Run run, boolean useOriginalData)
            throws SquidException {
        String[] retVal = new String[2];
        retVal[0] = prawnFileHandler.getCurrentPrawnSourceFileLocation().replace(".xml", "-PART-A.xml").replace(".XML", "-PART-A.xml");
        retVal[1] = prawnFileHandler.getCurrentPrawnSourceFileLocation().replace(".xml", "-PART-B.xml").replace(".XML", "-PART-B.xml");

        // get index from original prawnFile
        int indexOfRun = prawnFile.getRun().indexOf(run);

        List<Run> runs = prawnFile.getRun();
        List<Run> runsCopy;

        if (useOriginalData) {
            ShrimpDataFileInterface prawnFileOriginal = null;
            try {
                prawnFileOriginal = deserializePrawnData();
                runsCopy = new CopyOnWriteArrayList<>(prawnFileOriginal.getRun());
            } catch (IOException | JAXBException | SAXException | SquidException iOException) {
                throw new SquidException(iOException.getMessage());
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
        } catch (IOException | JAXBException Exception) {
        }

        runs.clear();
        // preserve order
        for (Run runS : second) {
            runs.add(runS);
        }
        prawnFile.setRuns((short) runs.size());
        try {
            prawnFileHandler.writeRawDataFileAsXML(prawnFile, retVal[1]);
        } catch (IOException | JAXBException Exception) {
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

    @Override
    public Path generateAllReports() throws IOException {

        if (prawnFileExists()) {
            // these are raw data reports
            getTask().producePerScanReportsToFiles();
        }

        if (generateReportsValid()) {
            prawnFileHandler.getReportsEngine().writeProjectAudit();
            prawnFileHandler.getReportsEngine().writeTaskAudit();

            prawnFileHandler.getReportsEngine().writeSummaryReportsForReferenceMaterials();
            produceReferenceMaterialPerSquid25CSV(true);
            produceSelectedReferenceMaterialReportCSV();

            prawnFileHandler.getReportsEngine().writeSummaryReportsForUnknowns();
            produceUnknownsPerSquid25CSV(true);
            produceUnknownsBySampleForETReduxCSV(true);
            produceSelectedUnknownsReportCSV();
            produceUnknownsWeightedMeanSortingFieldsCSV();
        }

        return (new File(prawnFileHandler.getReportsEngine().makeReportFolderStructure())).toPath();
    }

    @Override
    public boolean hasReportsFolder() {
        return prawnFileHandler.getReportsEngine().getFolderToWriteCalamariReports() != null;
    }

    /**
     * @return the prawnFileHandler
     */
    @Override
    public PrawnXMLFileHandler getPrawnFileHandler() {
        return prawnFileHandler;
    }

    /**
     * @param prawnFileHandler the prawnFileHandler to set
     */
    public void setPrawnFileHandler(PrawnXMLFileHandler prawnFileHandler) {
        this.prawnFileHandler = prawnFileHandler;
    }

    /**
     * @return the projectName
     */
    @Override
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the analystName
     */
    @Override
    public String getAnalystName() {
        return analystName;
    }

    /**
     * @param analystName the analystName to set
     */
    @Override
    public void setAnalystName(String analystName) {
        this.analystName = analystName;
    }

    /**
     * @return the projectNotes
     */
    @Override
    public String getProjectNotes() {
        if (projectNotes == null) {
            projectNotes = "";
        }
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
    @Override
    public String getFilterForRefMatSpotNames() {
        if (filterForConcRefMatSpotNames == null) {
            filterForConcRefMatSpotNames = "";
        }
        return filterForRefMatSpotNames;
    }

    @Override
    public void updateFilterForRefMatSpotNames(String filterForRefMatSpotNames) {
        if (filtersForUnknownNames.containsKey(filterForRefMatSpotNames)) {
            this.filterForRefMatSpotNames = filterForRefMatSpotNames;
        }
        if (filterForRefMatSpotNames.length() == 0) {
            setReferenceMaterialModel(new ReferenceMaterialModel());
        }
        if (task != null) {
            task.setFilterForRefMatSpotNames(filterForRefMatSpotNames);
        }
    }

    @Override
    public String getFilterForConcRefMatSpotNames() {
        return filterForConcRefMatSpotNames;
    }

    @Override
    public void updateFilterForConcRefMatSpotNames(String filterForConcRefMatSpotNames) {
        if (filtersForUnknownNames.containsKey(filterForConcRefMatSpotNames)) {
            this.filterForConcRefMatSpotNames = filterForConcRefMatSpotNames;
        }
        if (filterForConcRefMatSpotNames.length() == 0) {
            setConcentrationReferenceMaterialModel(new ReferenceMaterialModel());
        }
        if (task != null) {
            task.setFilterForConcRefMatSpotNames(filterForConcRefMatSpotNames);
        }
    }

    @Override
    public Map<String, Integer> getFiltersForUnknownNames() {
        if (filtersForUnknownNames == null) {
            filtersForUnknownNames = new HashMap<>();
        }
        return new HashMap<>(filtersForUnknownNames);
    }

    @Override
    public void updateFiltersForUnknownNames(Map<String, Integer> filtersForUnknownNames) {
        this.filtersForUnknownNames = filtersForUnknownNames;
        if (task != null) {
            task.setFiltersForUnknownNames(filtersForUnknownNames);
        }
    }

    /**
     * @return the sessionDurationHours
     */
    @Override
    public double getSessionDurationHours() {
        return sessionDurationHours;
    }

    /**
     * @return the prefixTree
     */
    @Override
    public SquidPrefixTree getPrefixTree() {
        return prefixTree;
    }

    /**
     * @return the task
     */
    @Override
    public TaskInterface getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(TaskInterface task) {
        this.task = task;
    }

    /**
     * @return the delimiterForUnknownNames
     */
    @Override
    public String getDelimiterForUnknownNames() {
        if (delimiterForUnknownNames == null) {
            delimiterForUnknownNames = Squid3Constants.SampleNameDelimitersEnum.HYPHEN.getName();
        }
        return delimiterForUnknownNames;
    }

    /**
     * @param delimiterForUnknownNames the delimiterForUnknownNames to set
     */
    public void setDelimiterForUnknownNames(String delimiterForUnknownNames) {
        this.delimiterForUnknownNames = delimiterForUnknownNames;
    }

    /**
     * @return the referenceMaterialModel
     */
    @Override
    public ParametersModel getReferenceMaterialModel() {
        if (referenceMaterialModel == null) {
            this.referenceMaterialModel = new ReferenceMaterialModel();
        }
        return referenceMaterialModel;
    }

    @Override
    public void setReferenceMaterialModel(ParametersModel referenceMaterialModel) {
        if (task != null) {
            task.setReferenceMaterialModel(referenceMaterialModel);
        }
        this.referenceMaterialModel = referenceMaterialModel;
    }

    /**
     * @return the concentrationReferenceMaterialModel
     */
    @Override
    public ParametersModel getConcentrationReferenceMaterialModel() {
        if (concentrationReferenceMaterialModel == null) {
            this.concentrationReferenceMaterialModel = new ReferenceMaterialModel();
        }
        return concentrationReferenceMaterialModel;
    }

    @Override
    public void setConcentrationReferenceMaterialModel(ParametersModel concentrationReferenceMaterialModel) {
        if (task != null) {
            task.setConcentrationReferenceMaterialModel(concentrationReferenceMaterialModel);
        }
        this.concentrationReferenceMaterialModel = concentrationReferenceMaterialModel;
    }

    /**
     * @return the physicalConstantsModel
     */
    @Override
    public ParametersModel getPhysicalConstantsModel() {
        if (physicalConstantsModel == null) {
            physicalConstantsModel = task.getPhysicalConstantsModel();
            //backwards compatible
            if (task != null) {
                physicalConstantsModel = task.getPhysicalConstantsModel();
            } else {
                physicalConstantsModel
                        = SquidPersistentState.getExistingPersistentState().getTaskDesign().getPhysicalConstantsModel();
            }
        }
        return physicalConstantsModel;
    }

    /**
     * @param physicalConstantsModel the physicalConstantsModel to set
     */
    @Override
    public void setPhysicalConstantsModel(ParametersModel physicalConstantsModel) {
        if (task != null) {
            task.setPhysicalConstantsModel(physicalConstantsModel);
        }
        this.physicalConstantsModel = physicalConstantsModel;
    }

    /**
     * @return the commonPbModel
     */
    @Override
    public ParametersModel getCommonPbModel() {
        if (commonPbModel == null) {
            //backwards compatible
            if (task != null) {
                commonPbModel = task.getCommonPbModel();
            } else {
                commonPbModel
                        = SquidPersistentState.getExistingPersistentState().getTaskDesign().getCommonPbModel();
            }
        }
        return commonPbModel;
    }

    /**
     * @param commonPbModel the commonPbModel to set
     */
    @Override
    public void setCommonPbModel(ParametersModel commonPbModel) {
        if (task != null) {
            task.setCommonPbModel(commonPbModel);
        }
        this.commonPbModel = commonPbModel;
    }

    /**
     * @return the squidAllowsAutoExclusionOfSpots
     */
    @Override
    public boolean isSquidAllowsAutoExclusionOfSpots() {
        //backwards compatible
        if (task != null) {
            squidAllowsAutoExclusionOfSpots = task.isSquidAllowsAutoExclusionOfSpots();
        }
        return squidAllowsAutoExclusionOfSpots;
    }

    /**
     * @param squidAllowsAutoExclusionOfSpots the
     *                                        squidAllowsAutoExclusionOfSpots to set
     */
    @Override
    public void setSquidAllowsAutoExclusionOfSpots(boolean squidAllowsAutoExclusionOfSpots) {
        if (task != null) {
            task.setSquidAllowsAutoExclusionOfSpots(squidAllowsAutoExclusionOfSpots);
        }
        this.squidAllowsAutoExclusionOfSpots = squidAllowsAutoExclusionOfSpots;
    }

    /**
     * @return the extPErrU
     */
    @Override
    public double getExtPErrU() {
        //backwards compatible
        if (task != null) {
            extPErrU = task.getExtPErrU();
        }
        return extPErrU;
    }

    /**
     * @param extPErrU the extPErrU to set
     */
    @Override
    public void setExtPErrU(double extPErrU) {
        if (task != null) {
            task.setExtPErrU(extPErrU);
        }
        this.extPErrU = extPErrU;
    }

    /**
     * @return the extPErrTh
     */
    @Override
    public double getExtPErrTh() {
        //backwards compatible
        if (task != null) {
            extPErrTh = task.getExtPErrTh();
        }
        return extPErrTh;
    }

    /**
     * @param extPErrTh the extPErrTh to set
     */
    public void setExtPErrTh(double extPErrTh) {
        if (task != null) {
            task.setExtPErrTh(extPErrTh);
        }
        this.extPErrTh = extPErrTh;
    }

    /**
     * @return the selectedIndexIsotope
     */
    @Override
    public Squid3Constants.IndexIsoptopesEnum getSelectedIndexIsotope() {
        if (selectedIndexIsotope == null) {
            selectedIndexIsotope = Squid3Constants.IndexIsoptopesEnum.PB_204;
        }
        return selectedIndexIsotope;
    }

    /**
     * @param selectedIndexIsotope the selectedIndexIsotope to set
     */
    public void setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum selectedIndexIsotope) {
        if (task != null) {
            task.setSelectedIndexIsotope(selectedIndexIsotope);
        }
        this.selectedIndexIsotope = selectedIndexIsotope;
    }

    /**
     * @return the useSBM
     */
    @Override
    public boolean isUseSBM() {
        //backwards compatible
        if (task != null) {
            useSBM = task.isUseSBM();
        }
        return useSBM;
    }

    /**
     * @param useSBM the useSBM to set
     */
    public void setUseSBM(boolean useSBM) {
        if (task != null) {
            task.setUseSBM(useSBM);
        }
        this.useSBM = useSBM;
    }

    /**
     * @return the userLinFits
     */
    @Override
    public boolean isUserLinFits() {
        //backwards compatible
        if (task != null) {
            userLinFits = task.isUserLinFits();
        }
        return userLinFits;
    }

    /**
     * @param userLinFits the userLinFits to set
     */
    public void setUserLinFits(boolean userLinFits) {
        if (task != null) {
            task.setUserLinFits(userLinFits);
        }
        this.userLinFits = userLinFits;
    }

    @Override
    public int hashCode() {
        /*return Objects.hash(getPrefixTree(), getPrawnFileHandler(), getProjectName(), getAnalystName(), getProjectNotes(),
                prawnSourceFile, prawnFile, getFilterForRefMatSpotNames(), getFilterForConcRefMatSpotNames(),
                getSessionDurationHours(), getTask(), getFiltersForUnknownNames(), getDelimiterForUnknownNames(),
                getReferenceMaterialModel(), getConcentrationReferenceMaterialModel());*/
        int result = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            result = Arrays.hashCode(bos.toByteArray());
        } catch (IOException e) {
        }
        return result;// + 31 * Objects.hash(task);
        //return HashCodeBuilder.reflectionHashCode(17, 31, this, false, Object.class);
    }
}