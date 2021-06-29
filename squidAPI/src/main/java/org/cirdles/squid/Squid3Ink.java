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
import java.nio.file.Path;
import java.util.List;

import static org.cirdles.squid.constants.Squid3Constants.DEMO_SQUID_PROJECTS_FOLDER;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GEOCHRON;
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

    private Squid3Ink(String squidUserHomeDirectory) {
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

    public static Squid3API spillSquid3Ink(String squidUserHomeDirectory) {
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

        squid3Ink.openDemonstrationSquid3Project();
//        squid3Ink.newSquid3GeochronProjectFromPrawnXML(
//                (new File("Squid3_Resources/ExamplePrawnXMLFiles/836_1_2016_Nov_28_09.50.xml")).toPath());
//        squid3Ink.newSquid3GeochronProjectFromZippedPrawnXML(
//                (new File("zippy/836_1_2016_Nov_28_09.50.xml.zip")).toPath());
//
        squid3Ink.generateAllSquid3ProjectReports();
        System.out.println(squid3Ink.getSquid3Project().getProjectName()
                + "\n" + squid3Ink.getSquid3Project().getPrawnFileHandler().getReportsEngine().makeReportFolderStructure());
        try {
            System.out.println(squid3Ink.generateReferenceMaterialSummaryExpressionsReport().toString());
            System.out.println(squid3Ink.generatePerScanReports().toString());
        } catch (IOException iOException) {
        }
//        squid3Ink.saveAsSquid3Project(new File("XXXXXX.squid"));
//        squid3Ink.generateAllSquid3ProjectReports();
//        System.out.println(squid3Ink.getSquid3Project().getProjectName()
//                + "   " + squid3Ink.getSquid3Project().getPrawnFileHandler().getReportsEngine().makeReportFolderStructure());

        System.out.println(squid3Ink.retrieveSquid3ProjectListMRU());
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
     * Loads existing Squid3 project file.
     *
     * @param projectFilePath
     * @return
     */
    @Override
    public void openSquid3Project(Path projectFilePath) {
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

            // this updates output folder for reports to current version
            CalamariFileUtilities.initCalamariReportsFolder(squid3Project.getPrawnFileHandler(),
                    projectFilePath.toFile().getParentFile());
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
    public void openDemonstrationSquid3Project() throws IOException {
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
    public void setUseSBM(boolean doUse) {
        squid3Project.setUseSBM(doUse);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setUseSBM(doUse);
        task.setChanged(true);
        task.setupSquidSessionSpecsAndReduceAndReport(true);
    }

    @Override
    public void setUseLinearRegression(boolean doUse) {
        squid3Project.setUserLinFits(doUse);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setUserLinFits(doUse);
        task.setChanged(true);
        task.setupSquidSessionSpecsAndReduceAndReport(true);
    }

    @Override
    public void setPreferredIndexIsotope(Squid3Constants.IndexIsoptopesEnum isotope) {
        squid3Project.setSelectedIndexIsotope(isotope);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setChanged(true);
    }

    @Override
    public void setAutoExcludeSpots(boolean doAutoExclude) {
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
    public void setDefaultCommonPbModel(ParametersModel commonPbModel) {
        ((Squid3ProjectParametersAPI) squid3Project).setCommonPbModel(commonPbModel);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setChanged(true);
        if (task.getReferenceMaterialSpots().size() > 0) {
            task.setupSquidSessionSpecsAndReduceAndReport(false);
        }
    }

    @Override
    public void setDefaultPhysicalConstantsModel(ParametersModel physicalConstantsModel) {
        ((Squid3ProjectParametersAPI) squid3Project).setPhysicalConstantsModel(physicalConstantsModel);
        SquidProject.setProjectChanged(true);
        TaskInterface task = squid3Project.getTask();
        task.setChanged(true);
        if (task.getReferenceMaterialSpots().size() > 0) {
            task.setupSquidSessionSpecsAndReduceAndReport(false);
        }
    }

    @Override
    public void setDefaultParametersFromCurrentChoices() {
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
    public void refreshModelsAction() {
        TaskInterface task = squid3Project.getTask();
        task.refreshParametersFromModels(squid3Project.isTypeGeochron(), true, false);
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
    public Path generateAllSquid3ProjectReports() throws IOException {
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