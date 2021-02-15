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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static org.cirdles.squid.constants.Squid3Constants.DEMO_SQUID_PROJECTS_FOLDER;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.ParametersModelComparator;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
import org.cirdles.squid.projects.Squid3ProjectBasicAPI;
import org.cirdles.squid.projects.Squid3ProjectParametersAPI;
import org.cirdles.squid.projects.Squid3ProjectReportingAPI;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import org.cirdles.squid.utilities.fileUtilities.ProjectFileUtilities;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;

/**
 * Provides specialized class to implement API for Squid3Ink Virtual Squid3.
 *
 * @author bowring
 */
public class Squid3Ink implements Squid3API {

    private static final SquidLabData squidLabData;
    private static final SquidPersistentState squidPersistentState;

    static {
        CalamariFileUtilities.initSampleParametersModels();
        squidLabData = SquidLabData.getExistingSquidLabData();
        squidLabData.testVersionAndUpdate();

        squidPersistentState
                = SquidPersistentState.getExistingPersistentState();
    }

    private Squid3ProjectBasicAPI squid3Project;

    @Override
    public Squid3ProjectBasicAPI getSquid3Project() {
        return squid3Project;
    }

    private Squid3Ink() {
        CalamariFileUtilities.initExamplePrawnFiles();
        CalamariFileUtilities.initDemoSquidProjectFiles();
        CalamariFileUtilities.loadShrimpFileSchema();
        CalamariFileUtilities.loadJavadoc();
        CalamariFileUtilities.initXSLTML();
        CalamariFileUtilities.initSquidTaskLibraryFiles();
    }

    public static Squid3API spillSquid3Ink() {
        return new Squid3Ink();
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

    @Override
    public void generateAllSquid3ProjectReports() throws IOException {
        ((Squid3ProjectReportingAPI) squid3Project).generateAllReports();
    }

    @Override
    public void saveCurrentSquid3Project() throws IOException, SquidException {
        if (squid3Project != null) {
            ProjectFileUtilities.serializeSquidProject(
                    (SquidProject) squid3Project,
                    squidPersistentState.getMRUProjectFile().getCanonicalPath());
        }
    }

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

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    public static void main(String[] args) throws IOException, SquidException {
        Squid3API squid3Ink = Squid3Ink.spillSquid3Ink();
        squid3Ink.openDemonstrationSquid3Project();

        squid3Ink.generateAllSquid3ProjectReports();
        System.out.println(squid3Ink.getSquid3Project().getProjectName()
                + "   " + squid3Ink.getSquid3Project().getPrawnFileHandler().getReportsEngine().makeReportFolderStructure());

        squid3Ink.saveAsSquid3Project(new File("XXXXXX.squid"));
        squid3Ink.generateAllSquid3ProjectReports();
        System.out.println(squid3Ink.getSquid3Project().getProjectName()
                + "   " + squid3Ink.getSquid3Project().getPrawnFileHandler().getReportsEngine().makeReportFolderStructure());

        System.out.println(squid3Ink.retrieveSquid3ProjectListMRU());
    }
}
