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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.bind.JAXBException;
import org.cirdles.commons.util.ResourceExtractor;
import static org.cirdles.squid.constants.Squid3Constants.DEFAULT_PRAWNFILE_NAME;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.core.PrawnFileHandler;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.tasks.TaskInterface;
import static org.cirdles.squid.utilities.FileUtilities.recursiveDelete;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import org.xml.sax.SAXException;

/**
 * Created by johnzeringue on 7/27/16. Adapted by James Bowring Dec 2018.
 */
public class PrawnFileHandlerService {

    private final PrawnFileHandler prawnFileHandler;
    private final CalamariReportsEngine reportsEngine;
    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(PrawnFileHandlerService.class);
    private static final String PRAWN_FILE_RESOURCE_Z6266_TASK_PERM1
            = "/org/cirdles/squid/tasks/squidTask25/SquidTask_Z6266 = 11pk Perm1.SB.xls";

    public PrawnFileHandlerService() {
        prawnFileHandler = new PrawnFileHandler();
        reportsEngine = prawnFileHandler.getReportsEngine();
    }

    public Path generateReports(
            String myFileName,
            InputStream prawnFile,
            boolean useSBM,
            boolean userLinFits,
            String refMatFilter) throws IOException, JAXBException, SAXException {

        String fileName = myFileName;
        if (myFileName == null) {
            fileName = DEFAULT_PRAWNFILE_NAME;
        }

        Path uploadDirectory = Files.createTempDirectory("upload");
        Path prawnFilePath = uploadDirectory.resolve("prawn-file.xml");
        Files.copy(prawnFile, prawnFilePath);

        Path calamarirReportsFolderAlias = Files.createTempDirectory("reports-destination");
        File reportsDestinationFile = calamarirReportsFolderAlias.toFile();

        reportsEngine.setFolderToWriteCalamariReports(reportsDestinationFile);

        // this gives reportengine the name of the Prawnfile for use in report names
        prawnFileHandler.initReportsEngineWithCurrentPrawnFileName(fileName);

        CalamariFileUtilities.initSampleParametersModels();

        SquidProject squidProject = new SquidProject();

        Path reportsZip = null;
        try {
            PrawnFile prawnFileData = prawnFileHandler.unmarshallPrawnFileXML(prawnFilePath.toString(), true);
            squidProject.setPrawnFile(prawnFileData);

            // hard-wired for now
            squidProject.getTask().setCommonPbModel(CommonPbModel.getDefaultModel("GA Common Lead 2018", "1.0"));
            squidProject.getTask().setPhysicalConstantsModel(PhysicalConstantsModel.getDefaultModel("GA Physical Constants Model Squid 2", "1.0"));
            File squidTaskFile = RESOURCE_EXTRACTOR
                    .extractResourceAsFile(PRAWN_FILE_RESOURCE_Z6266_TASK_PERM1);
            
            
            squidProject.createTaskFromImportedSquid25Task(squidTaskFile);
            squidProject.setDelimiterForUnknownNames("-");
            
            TaskInterface task = squidProject.getTask();
            task.setFilterForRefMatSpotNames(refMatFilter);
            task.setFilterForConcRefMatSpotNames("6266");
            task.applyTaskIsotopeLabelsToMassStations();

            try {
                reportsEngine.produceReports(
                        task.getShrimpFractions(),
                        (ShrimpFraction) task.getUnknownSpots().get(0),
                        task.getReferenceMaterialSpots().size() > 0
                        ? (ShrimpFraction) task.getReferenceMaterialSpots().get(0) : (ShrimpFraction) task.getUnknownSpots().get(0),
                        true, false);
            } catch (IOException iOException) {
            }

            Files.delete(prawnFilePath);

            Path reportsFolder = Paths.get(reportsEngine.getFolderToWriteCalamariReports().getPath());

            reportsZip = ZipUtility.recursivelyZip(reportsFolder);
            recursiveDelete(reportsFolder);
        } catch (IOException | JAXBException | SAXException | SquidException iOException) {
        }

        return reportsZip;
    }
}
