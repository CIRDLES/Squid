/*
 * Copyright 2021 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid;

import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.ParametersModelComparator;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.projects.Squid3ProjectBasicAPI;
import org.cirdles.squid.projects.Squid3ProjectParametersAPI;
import org.cirdles.squid.projects.Squid3ProjectReportingAPI;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.taskDesign.TaskDesign;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import org.cirdles.squid.utilities.fileUtilities.ProjectFileUtilities;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.cirdles.squid.constants.Squid3Constants.DEMO_SQUID_PROJECTS_FOLDER;
import static org.cirdles.squid.constants.Squid3Constants.REF_238U235U_DEFAULT;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GEOCHRON;
import static org.cirdles.squid.parameters.util.RadDates.*;
import static org.cirdles.squid.parameters.util.ReferenceMaterialEnum.r238_235s;
import static org.cirdles.squid.utilities.fileUtilities.ZipUtility.extractZippedFile;

/**
 * Provides specialized class to implement API for Squid3Ink Virtual Squid3.
 *
 * @author bowring
 */
public class Squid3Ink implements Squid3API {

    private static SquidLabData squidLabData;
    private static SquidPersistentState squidPersistentState;
    private Squid3ProjectBasicAPI squid3Project;

    private Squid3Ink(String squidUserHomeDirectory) throws SquidException {
        System.setProperty("user.home", squidUserHomeDirectory);
        CalamariFileUtilities.initSampleParametersModels();
        squidLabData = SquidLabData.getExistingSquidLabData();
        squidLabData.testVersionAndUpdate();
        squidPersistentState
                = SquidPersistentState.getExistingPersistentState();
        CalamariFileUtilities.initExamplePrawnFiles();
        CalamariFileUtilities.initDemoSquidProjectFiles();
        CalamariFileUtilities.loadShrimpFileSchema();
        CalamariFileUtilities.loadJavadoc();
        CalamariFileUtilities.initXSLTML();
        CalamariFileUtilities.initSquidTaskLibraryFiles();
    }

    public static SquidLabData getSquidLabData() {
        return squidLabData;
    }

    public static SquidPersistentState getSquidPersistentState() {
        return squidPersistentState;
    }

    public static Squid3API spillSquid3Ink(String squidUserHomeDirectory) throws SquidException {
        return new Squid3Ink(squidUserHomeDirectory);
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws org.cirdles.squid.exceptions.SquidException
     * @throws javax.xml.bind.JAXBException
     * @throws org.xml.sax.SAXException
     */
    public static void main(String[] args) throws IOException, SquidException, JAXBException, SAXException {
        Squid3API squid3Ink = Squid3Ink.spillSquid3Ink("/Users/TEST");
        //squid3Ink.openDemonstrationSquid3Project();

        File file = new File("/Users/bowring/Google Drive/_SQUID3/OP_Files/180050_GA6392_18081912.13.op");
        squid3Ink.newSquid3GeochronProjectFromDataFileOP(file.toPath());

//        squid3Ink.generateAllSquid3ProjectReports();
//        System.out.println(squid3Ink.getSquid3Project().getProjectName()
//                + "\n" + squid3Ink.getSquid3Project().getPrawnFileHandler().getReportsEngine().makeReportFolderStructure());
//        try {
//            System.out.println(squid3Ink.generateReferenceMaterialSummaryExpressionsReport().toString());
//            System.out.println(squid3Ink.generatePerScanReports().toString());
//        } catch (IOException iOException) {
//        }

        System.out.println(squid3Ink.retrieveSquid3ProjectListMRU());

        for (int i = 0; i < squid3Ink.getArrayOfSampleNames().length; i++) {
            System.out.println(squid3Ink.getArrayOfSampleNames()[i]);
        }

//        for (int i = 0; i < squid3Ink.getArrayOfSpotSummariesFromSample("6266").length; i++) {
//            System.out.println(squid3Ink.getArrayOfSpotSummariesFromSample("6266")[i][0]);
//        }
//
//        squid3Ink.updateSpotName("Temora-1.1", "jimmy-1.122");
//        for (int i = 0; i < squid3Ink.getArrayOfSpotSummariesFromSample("TEMORA").length; i++) {
//            System.out.println("  " + squid3Ink.getArrayOfSpotSummariesFromSample("Temora")[i][0]);
//        }
//        System.out.println();
//
//        List<String> spotNames = new ArrayList<>();
//        spotNames.add("Temora-2.1");
//        spotNames.add("Temora-5.1");
//        squid3Ink.removeSpotsFromDataFile(spotNames);
//        for (int i = 0; i < squid3Ink.getArrayOfSpotSummariesFromSample("TEMORA").length; i++) {
//            System.out.println("  " + squid3Ink.getArrayOfSpotSummariesFromSample("Temora")[i][0]);
//        }
//        System.out.println();
//        squid3Ink.restoreAllSpotsToDataFile();
//        for (int i = 0; i < squid3Ink.getArrayOfSpotSummariesFromSample("TEMORA").length; i++) {
//            System.out.println("  " + squid3Ink.getArrayOfSpotSummariesFromSample("Temora")[i][0]);
//        }
    }

    @Override
    public Squid3ProjectBasicAPI getSquid3Project() {
        return squid3Project;
    }

    /**
     * @param prawnXMLFileSourcePath
     * @throws IOException
     * @throws JAXBException
     * @throws SAXException
     * @throws SquidException
     */
    @Override
    public void newSquid3GeochronProjectFromPrawnXML(Path prawnXMLFileSourcePath)
            throws IOException, JAXBException, SAXException, SquidException {

        File prawnSourceFile = prawnXMLFileSourcePath.toFile();
        if (prawnSourceFile != null) {
            squid3Project = new SquidProject(GEOCHRON);

            // this updates output folder for reports to current version
            CalamariFileUtilities.initCalamariReportsFolder(squid3Project.getPrawnFileHandler(),
                    prawnXMLFileSourcePath.toFile().getParentFile());

            if (((SquidProject) squid3Project).setupPrawnXMLFile(prawnSourceFile)) {
                ((SquidProject) squid3Project).autoDivideSamples();
                squidPersistentState.updatePrawnFileListMRU(prawnSourceFile);
            } else {
                squid3Project.getTask().setChanged(false);
                SquidProject.setProjectChanged(false);
                throw new SquidException(
                        "Squid3 encountered an error while trying to open the selected data file.");
            }
        }
    }

    /**
     * @param prawnXMLFileSourcePath
     * @throws IOException
     * @throws JAXBException
     * @throws SAXException
     * @throws SquidException
     */
    @Override
    public void newSquid3GeochronProjectFromZippedPrawnXML(Path prawnXMLFileSourcePath)
            throws IOException, JAXBException, SAXException, SquidException {

        File prawnZippedSourceFile = prawnXMLFileSourcePath.toFile();
        if (prawnZippedSourceFile != null) {
            squid3Project = new SquidProject(GEOCHRON);

            File prawnSourceFileNew = extractZippedFile(
                    prawnZippedSourceFile,
                    prawnXMLFileSourcePath.getParent().toFile()).toFile();

            // this updates output folder for reports to current version
            CalamariFileUtilities.initCalamariReportsFolder(squid3Project.getPrawnFileHandler(),
                    prawnXMLFileSourcePath.toFile().getParentFile());

            if (((SquidProject) squid3Project).setupPrawnXMLFile(prawnSourceFileNew)) {
                ((SquidProject) squid3Project).autoDivideSamples();
                squidPersistentState.updatePrawnFileListMRU(prawnSourceFileNew);
            } else {
                squid3Project.getTask().setChanged(false);
                SquidProject.setProjectChanged(false);
                throw new SquidException(
                        "Squid3 encountered an error while trying to open the selected data file.");
            }
        }
    }

    /**
     * @param opFileSourcePath
     * @throws IOException
     * @throws SquidException
     */
    public void newSquid3GeochronProjectFromDataFileOP(Path dataFileOPSourcePath)
            throws IOException, SquidException {
        File opSourceFile = dataFileOPSourcePath.toFile();

        if (opSourceFile != null) {
            squid3Project = new SquidProject(GEOCHRON);
            if (squid3Project.setupPrawnOPFile(opSourceFile)) {
                ((SquidProject) squid3Project).autoDivideSamples();
                squidPersistentState.updateOPFileListMRU(opSourceFile);
            } else {
                squid3Project.getTask().setChanged(false);
                SquidProject.setProjectChanged(false);
                throw new SquidException(
                        "Squid3 encountered an error while trying to open the selected data file.");
            }
        } else {
            squid3Project.getTask().setChanged(false);
            SquidProject.setProjectChanged(false);
            throw new SquidException(
                    "Squid3 encountered an error while trying to open the selected data file.");
        }
    }

    /**
     * Loads existing Squid3 project file.
     *
     * @param projectFilePath
     * @return
     */
    @Override
    public void openSquid3Project(Path projectFilePath) throws SquidException {
        squid3Project
                = (SquidProject) SquidSerializer.getSerializedObjectFromFile(projectFilePath.toString(), true);
        if (squid3Project != null && squid3Project.getTask() != null) {
            TaskInterface task = squid3Project.getTask();

            SquidProject.setProjectChanged(((Task) task).synchronizeTaskVersion());

            (((Task) task).verifySquidLabDataParameters()).forEach(model -> {
                if (model instanceof PhysicalConstantsModel) {
                    squidLabData.addPhysicalConstantsModel(model);
                    squidLabData.getPhysicalConstantsModels().sort(new ParametersModelComparator());
                } else if (model instanceof CommonPbModel) {
                    squidLabData.addcommonPbModel(model);
                    squidLabData.getCommonPbModels().sort(new ParametersModelComparator());
                } else if (model instanceof ReferenceMaterialModel) {
                    squidLabData.addReferenceMaterial(model);
                    squidLabData.getReferenceMaterials().sort(new ParametersModelComparator());
                }
            });

            ((Squid3ProjectParametersAPI) squid3Project).setReferenceMaterialModel(
                    task.getReferenceMaterialModel());
            ((Squid3ProjectParametersAPI) squid3Project).setConcentrationReferenceMaterialModel(
                    task.getConcentrationReferenceMaterialModel());

            if (SquidProject.isProjectChanged()) {
                // next two lines make sure 15-digit rounding is used by reprocessing data
                task.setChanged(true);
                task.setupSquidSessionSpecsAndReduceAndReport(true);

                ((Task) task).initTaskDefaultSquidReportTables(true);
            }

            ((Task) task).buildExpressionDependencyGraphs();
            ((Task) task).updateSquidSpeciesModelsGeochronMode();

            squidPersistentState.updateProjectListMRU(new File(projectFilePath.toString()));

            try {
                squid3Project.getPrawnFileHandler().getReportsEngine()
                        .setFolderToWriteCalamariReports(projectFilePath.toFile().getParentFile());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                squid3Project.getPrawnFileHandler().initReportsEngineWithCurrentPrawnFileName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     */
    @Override
    public List<String> retrieveSquid3ProjectListMRU() {
        return squidPersistentState.getMRUProjectList();
    }

    /**
     * Loads demonstration Squid3 project.
     *
     * @return SquidProject
     */
    @Override
    public void openDemonstrationSquid3Project() throws IOException, SquidException {
        File localDemoFile = new File(DEMO_SQUID_PROJECTS_FOLDER.getAbsolutePath()
                + File.separator + "SQUID3_demo_file.squid");
        openSquid3Project(localDemoFile.toPath());
    }

    /**
     * @throws IOException
     * @throws SquidException
     */
    @Override
    public void saveCurrentSquid3Project() throws IOException, SquidException {
        if (squid3Project != null) {
            ProjectFileUtilities.serializeSquidProject(
                    (SquidProject) squid3Project,
                    squidPersistentState.getMRUProjectFile().getCanonicalPath());
        }
    }

    /**
     * @param squid3ProjectFileTarget
     * @throws IOException
     * @throws SquidException
     */
    @Override
    public void saveAsSquid3Project(File squid3ProjectFileTarget) throws IOException, SquidException {
        if (squid3Project != null) {
            if (squid3ProjectFileTarget != null) {
                SquidProject.setProjectChanged(false);

                // capture squid project file name from file for project itself
                squid3Project.setProjectName(
                        squid3ProjectFileTarget.getName()
                                .substring(0,
                                        squid3ProjectFileTarget.getName().lastIndexOf(".")));

                ProjectFileUtilities.serializeSquidProject(
                        (SquidProject) squid3Project,
                        squid3ProjectFileTarget.getCanonicalPath());

                squidPersistentState.updateProjectListMRU(squid3ProjectFileTarget);
            }
        }
    }


    // project UI management

    @Override
    public void setUseSBM(boolean doUse) throws SquidException {
        squid3Project.setUseSBM(doUse);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setUseSBM(doUse);
        task.setChanged(true);
        task.setupSquidSessionSpecsAndReduceAndReport(true);
    }

    @Override
    public void setUseLinearRegression(boolean doUse) throws SquidException {
        squid3Project.setUserLinFits(doUse);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setUserLinFits(doUse);
        task.setChanged(true);
        task.setupSquidSessionSpecsAndReduceAndReport(true);
    }

    @Override
    public void setPreferredIndexIsotope(Squid3Constants.IndexIsoptopesEnum isotope) throws SquidException {
        squid3Project.setSelectedIndexIsotope(isotope);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setChanged(true);
    }

    @Override
    public void setAutoExcludeSpots(boolean doAutoExclude) throws SquidException {
        squid3Project.setSquidAllowsAutoExclusionOfSpots(doAutoExclude);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.updateRefMatCalibConstWMeanExpressions(doAutoExclude);
    }

    @Override
    public void setMinimumExternalSigma206238(double minExternalSigma) {
        squid3Project.setExtPErrU(minExternalSigma);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setExtPErrU(minExternalSigma);
    }

    @Override
    public void setMinimumExternalSigma208232(double minExternalSigma) {
        squid3Project.setExtPErrTh(minExternalSigma);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setExtPErrTh(minExternalSigma);
    }

    @Override
    public void setDefaultCommonPbModel(ParametersModel commonPbModel) throws SquidException {
        ((Squid3ProjectParametersAPI) squid3Project).setCommonPbModel(commonPbModel);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setChanged(true);
        if (task.getReferenceMaterialSpots().size() > 0) {
            task.setupSquidSessionSpecsAndReduceAndReport(false);
        }
    }

    @Override
    public void setDefaultPhysicalConstantsModel(ParametersModel physicalConstantsModel) throws SquidException {
        ((Squid3ProjectParametersAPI) squid3Project).setPhysicalConstantsModel(physicalConstantsModel);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setChanged(true);
        if (task.getReferenceMaterialSpots().size() > 0) {
            task.setupSquidSessionSpecsAndReduceAndReport(false);
        }
    }

    @Override
    public void setDefaultParametersFromCurrentChoices() throws SquidException {
        TaskDesign taskDesign = squidPersistentState.getTaskDesign();
        taskDesign.setUseSBM(squid3Project.isUseSBM());
        taskDesign.setUserLinFits(squid3Project.isUserLinFits());
        taskDesign.setSelectedIndexIsotope(squid3Project.getSelectedIndexIsotope());
        taskDesign.setSquidAllowsAutoExclusionOfSpots(squid3Project.isSquidAllowsAutoExclusionOfSpots());
        taskDesign.setExtPErrU(squid3Project.getExtPErrU());
        taskDesign.setExtPErrTh(squid3Project.getExtPErrTh());
        taskDesign.setPhysicalConstantsModel(squid3Project.getPhysicalConstantsModel());
        squidLabData.setPhysConstDefault(squid3Project.getPhysicalConstantsModel());
        taskDesign.setCommonPbModel(squid3Project.getCommonPbModel());
        squidLabData.setCommonPbDefault(squid3Project.getCommonPbModel());
        taskDesign.setAnalystName(squid3Project.getAnalystName());

        squidPersistentState.updateSquidPersistentState();
    }

    @Override
    public void refreshModelsAction() throws SquidException {
        TaskInterface task = squid3Project.getTask();
        task.refreshParametersFromModels(squid3Project.isTypeGeochron(), true, false);
    }

    // Sample UI management ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Override
    public String[] getArrayOfSampleNames() {
        String[] samples = squid3Project.getFiltersForUnknownNames().keySet().toArray(new String[0]);
        List<String> samplesList = new ArrayList<String>(Arrays.asList(samples));
        samplesList.add(0, "All Samples");
        return samplesList.toArray(new String[0]);
    }

    @Override
    public String[][] getArrayOfSpotSummariesFromSample(String sampleName) {
        List<PrawnFile.Run> spots = squid3Project.getPrawnFileRuns();
        List<PrawnFile.Run> selectedSpots = new ArrayList<>();
        if (sampleName.toUpperCase(Locale.ROOT).startsWith("ALL SAMPLES")) {
            selectedSpots = spots;
        } else {
            for (PrawnFile.Run spot : spots) {
                if (spot.getPar().get(0).getValue().toUpperCase(Locale.ROOT).trim().startsWith(sampleName.toUpperCase(Locale.ROOT))) {
                    selectedSpots.add(spot);
                }
            }
        }
        String[][] spotSummaries = new String[selectedSpots.size()][5];
        int i = 0;
        for (PrawnFile.Run spot : selectedSpots) {
            spotSummaries[i][0] = String.format("%1$-" + 20 + "s", spot.getPar().get(0).getValue()); // name
            spotSummaries[i][1] = String.format("%1$-" + 12 + "s", spot.getSet().getPar().get(0).getValue()); //date
            spotSummaries[i][2] = String.format("%1$-" + 12 + "s", spot.getSet().getPar().get(1).getValue()); //time
            spotSummaries[i][3] = String.format("%1$-" + 6 + "s", spot.getPar().get(2).getValue()); //peaks
            spotSummaries[i][4] = String.format("%1$-" + 6 + "s", spot.getPar().get(3).getValue()); //scans
            i++;
        }
        return spotSummaries;
    }

    @Override
    public String getReferenceMaterialSampleName() {
        return squid3Project.getFilterForRefMatSpotNames();
    }

    @Override
    public void setReferenceMaterialSampleName(String refMatSampleName) {
        squid3Project.updateFilterForRefMatSpotNames(refMatSampleName);
    }

    @Override
    public String getConcReferenceMaterialSampleName() {
        return squid3Project.getFilterForConcRefMatSpotNames();
    }

    @Override
    public void setConcReferenceMaterialSampleName(String concRefMatSampleName) {
        squid3Project.updateFilterForConcRefMatSpotNames(concRefMatSampleName);
    }

    @Override
    public void updateSpotName(String oldSpotName, String spotName) {
        List<PrawnFile.Run> spots = squid3Project.getPrawnFileRuns();
        for (PrawnFile.Run spot : spots) {
            if (spot.getPar().get(0).getValue().compareToIgnoreCase(oldSpotName) == 0) {
                spot.getPar().get(0).setValue(spotName.trim());
                squid3Project.processPrawnSessionForDuplicateSpotNames();
                squid3Project.getTask().setChanged(true);
                squid3Project.getTask().setPrawnChanged(true);
                SquidProject.setProjectChanged(true);
                break;
            }
        }
    }

    @Override
    public void updateRefMatModelChoice(ParametersModel refMatModel) throws SquidException {
        ((Squid3ProjectParametersAPI) squid3Project).setReferenceMaterialModel(refMatModel);
        squid3Project.getTask().setChanged(true);
        squid3Project.getTask().refreshParametersFromModels(false, false, true);
    }

    @Override
    public void updateConcRefMatModelChoice(ParametersModel concRefMatModel) throws SquidException {
        ((Squid3ProjectParametersAPI) squid3Project).setConcentrationReferenceMaterialModel(concRefMatModel);
        squid3Project.getTask().setChanged(true);
        squid3Project.getTask().refreshParametersFromModels(false, false, true);
    }

    /**
     * Produces 2 element array where [0] is three flags separated by semicolons with one for each of
     * dates 206_238; 207_206; 208_232 where flags are 0 = no change; 1 = change; F = bad Model.
     * If "F" is first flag, then produce message: "This reference material model is missing meaningful age data.
     * Please choose another model." if any flag is "1", produce message: "This reference material model is missing
     * key age(s), so Squid3 is temporarily substituting values (shown in red) and refreshing as follows:"
     * At this point append element [1], which reports the results of the audit and any temporary
     * changes made to the model to make it useful.
     *
     * @param curRefMatModel
     * @return
     */
    @Override
    public String[] produceAuditOfRefMatModel(ReferenceMaterialModel curRefMatModel) {
        return curRefMatModel.auditAndTempUpdateRefMatModel().split("Audit:");
    }

    @Override
    public String get206_238DateMa(ReferenceMaterialModel curRefMatModel) {
        return curRefMatModel.getDateByName(
                age206_238r.getName()).getValue().movePointLeft(6).setScale(3, RoundingMode.HALF_UP).toString();
    }

    @Override
    public String get207_206DateMa(ReferenceMaterialModel curRefMatModel) {
        return curRefMatModel.getDateByName(
                age207_206r.getName()).getValue().movePointLeft(6).setScale(3, RoundingMode.HALF_UP).toString();
    }

    @Override
    public String get208_232DateMa(ReferenceMaterialModel curRefMatModel) {
        return curRefMatModel.getDateByName(
                age208_232r.getName()).getValue().movePointLeft(6).setScale(3, RoundingMode.HALF_UP).toString();
    }

    @Override
    public String get238_235Abundance(ReferenceMaterialModel curRefMatModel) {
        String abundance = curRefMatModel.getDatumByName(r238_235s.getName())
                .getValue().setScale(3, RoundingMode.HALF_UP).toString();
        if (curRefMatModel.getDatumByName(r238_235s.getName()).getValue().compareTo(BigDecimal.ZERO) == 0) {
            abundance = REF_238U235U_DEFAULT + "";
        }
        return abundance;
    }

    @Override
    public String getU_ppm(ReferenceMaterialModel curConcRefMatModel) {
        return curConcRefMatModel.getConcentrationByName("concU")
                .getValue().setScale(3, RoundingMode.HALF_UP).toString();
    }

    @Override
    public String getTh_ppm(ReferenceMaterialModel curConcRefMatModel) {
        return curConcRefMatModel.getConcentrationByName("concTh")
                .getValue().setScale(3, RoundingMode.HALF_UP).toString();
    }

    /**
     * Squid3 maintains a list of removed spots so that they can be recovered at anytime
     *
     * @param spotNames
     */
    @Override
    public void removeSpotsFromDataFile(List<String> spotNames) throws SquidException {
        squid3Project.removeSpotsFromDataFile(spotNames);
        squid3Project.generatePrefixTreeFromSpotNames();
        SquidProject.setProjectChanged(true);
    }

    @Override
    public List<String> getRemovedSpotsByName() {
        return squid3Project.retrieveRemovedSpotsByName();
    }

    @Override
    public void restoreSpotToDataFile(String spotName) throws SquidException {
        squid3Project.restoreSpotToDataFile(spotName);
        squid3Project.generatePrefixTreeFromSpotNames();
        SquidProject.setProjectChanged(true);
    }

    @Override
    public void restoreAllSpotsToDataFile() throws SquidException {
        squid3Project.restoreAllRunsToPrawnFile();
        squid3Project.generatePrefixTreeFromSpotNames();
        SquidProject.setProjectChanged(true);
    }

    // REPORTS +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    /**
     * @return @throws IOException
     */
    @Override
    public Path generateReferenceMaterialSummaryExpressionsReport() throws IOException {
        return ((Squid3ProjectReportingAPI) squid3Project).generateReferenceMaterialSummaryExpressionsReport();
    }

    /**
     * @return @throws IOException
     */
    @Override
    public Path generateUnknownsSummaryExpressionsReport() throws IOException {
        return ((Squid3ProjectReportingAPI) squid3Project).generateUnknownsSummaryExpressionsReport();
    }

    /**
     * @return @throws IOException
     */
    @Override
    public Path generateTaskSummaryReport() throws IOException {
        return ((Squid3ProjectReportingAPI) squid3Project).generateTaskSummaryReport();
    }

    /**
     * @return @throws IOException
     */
    @Override
    public Path generateProjectAuditReport() throws IOException {
        return ((Squid3ProjectReportingAPI) squid3Project).generateProjectAuditReport();
    }

    /**
     * @return @throws IOException
     */
    @Override
    public Path generateAllSquid3ProjectReports() throws IOException, SquidException {
        return ((Squid3ProjectReportingAPI) squid3Project).generateAllReports();
    }

    /**
     * @return
     * @throws IOException
     */
    @Override
    public Path generatePerScanReports() throws IOException {
        return ((Squid3ProjectReportingAPI) squid3Project).generatePerScanReports();
    }
}