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
package org.cirdles.squid.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.FilenameUtils;
import org.cirdles.squid.constants.Squid3Constants;
import static org.cirdles.squid.constants.Squid3Constants.DEFAULT_PRAWNFILE_NAME;
import org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GEOCHRON;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.core.PrawnXMLFileHandler;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.shrimp.ShrimpDataFileInterface;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.utilities.FileUtilities;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import org.cirdles.squid.utilities.fileUtilities.ZipUtility;
import static org.cirdles.squid.utilities.fileUtilities.ZipUtility.extractZippedFile;
import static org.cirdles.squid.utilities.stateUtilities.SquidLabData.SQUID2_DEFAULT_PHYSICAL_CONSTANTS_MODEL_V1;
import org.xml.sax.SAXException;

/**
 * Created by johnzeringue on 7/27/16. Adapted by James Bowring Dec 2018.
 */
public class SquidReportingService {

    private PrawnXMLFileHandler prawnFileHandler;
    private CalamariReportsEngine reportsEngine;

    public SquidReportingService() {
    }

    public Path generateReports(
            String myProjectName,
            String myFileName,
            InputStream prawnFile,
            InputStream taskFile,
            boolean useSBM,
            boolean userLinFits,
            String refMatFilter,
            String concRefMatFilter,
            String preferredIndexIsotopeName)
            throws IOException, JAXBException, SAXException {

        IndexIsoptopesEnum preferredIndexIsotope = IndexIsoptopesEnum.valueOf(preferredIndexIsotopeName);

        // Posix attributes added to support web service on Linux - ignoring windows for now
        Set<PosixFilePermission> perms = EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, GROUP_READ);

        // detect if prawnfile is zipped
        boolean prawnIsZip = false;
        String fileName = "";
        if (myFileName == null) {
            fileName = DEFAULT_PRAWNFILE_NAME;
        } else if (myFileName.toLowerCase().endsWith(".zip")) {
            fileName = FilenameUtils.removeExtension(myFileName);
            prawnIsZip = true;
        } else {
            fileName = myFileName;
        }

        SquidProject squidProject = new SquidProject(myProjectName, GEOCHRON);
        prawnFileHandler = squidProject.getPrawnFileHandler();

        CalamariFileUtilities.initSampleParametersModels();

        Path reportsZip = null;
        Path reportsFolder = null;
        try {
            Path uploadDirectory = Files.createTempDirectory("upload");
            Path uploadDirectory2 = Files.createTempDirectory("upload2");

            Path prawnFilePath;
            Path taskFilePath;
            if (prawnIsZip) {
                Path prawnFilePathZip = uploadDirectory.resolve("prawn-file.zip");
                Files.copy(prawnFile, prawnFilePathZip);
                prawnFilePath = extractZippedFile(prawnFilePathZip.toFile(), uploadDirectory.toFile());
            } else {
                prawnFilePath = uploadDirectory.resolve("prawn-file.xml");
                Files.copy(prawnFile, prawnFilePath);
            }

            taskFilePath = uploadDirectory2.resolve("task-file.xls");
            Files.copy(taskFile, taskFilePath);
            File squidTaskFile = taskFilePath.toFile();

            ShrimpDataFileInterface prawnFileData = prawnFileHandler.unmarshallPrawnFileXML(prawnFilePath.toString(), true);
            squidProject.setPrawnFile(prawnFileData);
            squidProject.setDelimiterForUnknownNames("-");

            squidProject.updateFilterForRefMatSpotNames(refMatFilter);
            squidProject.updateFilterForRefMatSpotNames(concRefMatFilter);
            squidProject.replaceCurrentTaskWithImportedSquid25Task(squidTaskFile);

            TaskInterface task = squidProject.getTask();

            // hard-wired for now
            task.setTaskType(Squid3Constants.TaskTypeEnum.GEOCHRON);
            task.setCommonPbModel(CommonPbModel.getDefaultModel("GA Common Lead 2018", "1.0"));
            task.setPhysicalConstantsModel(PhysicalConstantsModel.getDefaultModel(SQUID2_DEFAULT_PHYSICAL_CONSTANTS_MODEL_V1, "1.0"));
            task.setReferenceMaterialModel(ReferenceMaterialModel.getDefaultModel("GA Accepted BR266", "1.0"));
            task.setConcentrationReferenceMaterialModel(ReferenceMaterialModel.getDefaultModel("GA Accepted BR266", "1.0"));

            task.setExtPErrU(0.75);
            task.setExtPErrTh(0.75);
            task.setUseSBM(useSBM);
            task.setUserLinFits(userLinFits);
            task.setSelectedIndexIsotope(preferredIndexIsotope);
            task.setSquidAllowsAutoExclusionOfSpots(true);

            // process task           
            task.applyTaskIsotopeLabelsToMassStationsAndUpdateTask();

            Path calamariReportsFolderAliasParent = Files.createTempDirectory("reports-destination");
            Path calamariReportsFolderAlias = calamariReportsFolderAliasParent.resolve("Squid3ReportsFromWebService");
            File reportsDestinationFile = calamariReportsFolderAlias.toFile();

            reportsEngine = prawnFileHandler.getReportsEngine();
            prawnFileHandler.initReportsEngineWithCurrentPrawnFileName(fileName);
            reportsEngine.setFolderToWriteCalamariReports(reportsDestinationFile);

            ((Task) task).initTaskDefaultSquidReportTables(true);

            if (squidProject.hasReportsFolder()) {
                squidProject.getPrawnFileHandler().getReportsEngine().writeProjectAudit();
                squidProject.getPrawnFileHandler().getReportsEngine().writeTaskAudit();

                squidProject.getPrawnFileHandler().getReportsEngine().writeSummaryReportsForReferenceMaterials();
                squidProject.produceReferenceMaterialPerSquid25CSV(true);
                squidProject.produceSelectedReferenceMaterialReportCSV();

                squidProject.getPrawnFileHandler().getReportsEngine().writeSummaryReportsForUnknowns();
                squidProject.produceUnknownsPerSquid25CSV(true);
                squidProject.produceUnknownsBySampleForETReduxCSV(true);
                squidProject.produceSelectedUnknownsReportCSV();
                squidProject.produceUnknownsWeightedMeanSortingFieldsCSV();

                squidProject.getTask().producePerScanReportsToFiles();
            }

            Files.delete(prawnFilePath);

            reportsFolder = Paths.get(reportsEngine.getFolderToWriteCalamariReports().getParentFile().getPath());

        } catch (IOException | JAXBException | SAXException | SquidException iOException) {

            Path config = Files.createTempFile("SquidWebServiceMessage", "txt", PosixFilePermissions.asFileAttribute(perms));
            try (BufferedWriter writer = Files.newBufferedWriter(config, StandardCharsets.UTF_8)) {
                writer.write("Squid Reporting web service was not able to process supplied files.");
                writer.newLine();
                writer.write(iOException.getMessage());
                writer.newLine();
            }
            File message = config.toFile();

            Path messageDirectory = Files.createTempDirectory("message");
            Path messageFilePath = messageDirectory.resolve("Squid Web Service Message.txt");
            Files.copy(message.toPath(), messageFilePath);

            reportsFolder = messageFilePath.getParent();
        }

        reportsZip = ZipUtility.recursivelyZip(reportsFolder);
        FileUtilities.recursiveDelete(reportsFolder);

        return reportsZip;
    }
}
