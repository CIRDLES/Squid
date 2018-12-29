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
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
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
import static org.cirdles.squid.utilities.FileUtilities.recursiveDelete;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import org.xml.sax.SAXException;

/**
 * Created by johnzeringue on 7/27/16. Adapted by James Bowring Dec 2018.
 */
public class PrawnFileHandlerService {

    private static final Map<String, String> ZIP_FILE_ENV;

    static {
        Map<String, String> zipFileEnv = new HashMap<>();
        zipFileEnv.put("create", "true");

        ZIP_FILE_ENV = Collections.unmodifiableMap(zipFileEnv);
    }

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

    private Path zip(Path target) throws IOException {
        Path zipFilePath = target.resolveSibling("reports.zip");

        try (FileSystem zipFileFileSystem = FileSystems.newFileSystem(
                URI.create("jar:file:" + zipFilePath), ZIP_FILE_ENV)) {

            Files.list(target).forEach(new Consumer<Path>() {
                @Override
                public void accept(Path entry) {
                    try {
                        Files.copy(entry, zipFileFileSystem.getPath("/" + entry.getFileName()));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }

        return zipFilePath;
    }

    // saved for future use
//    private void unZip(Path zippedFilePath, Path target) throws IOException {
//        Map<String, String> zip_properties = new HashMap<>();
//        zip_properties.put("create", "false");
//
//        URI zip_disk = URI.create("jar:file:" + zippedFilePath);
//
//        try (FileSystem zipFileSystem = FileSystems.newFileSystem(zip_disk, zip_properties)) {
//            final Path root = zipFileSystem.getPath("/");
//
//            //walk the zip file tree and copy files to the destination
//            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
//                @Override
//                public FileVisitResult visitFile(Path file,
//                        BasicFileAttributes attrs) throws IOException {
//                    final Path destFile = Paths.get(target.toString(),
//                            file.toString());
//                    //System.out.printf("Extracting file %s to %s\n", file, destFile);
//                    Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
//                    return FileVisitResult.CONTINUE;
//                }
//            });
//        }
//    }
    public Path generateReports(
            String myFileName,
            InputStream prawnFile,
            boolean useSBM,
            boolean userLinFits,
            String firstLetterRM) throws IOException, JAXBException, SAXException {

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
            
            squidProject.getTask().setCommonPbModel(CommonPbModel.getDefaultModel("GA Common Lead 2018", "1.0"));
            squidProject.getTask().setPhysicalConstantsModel(PhysicalConstantsModel.getDefaultModel("GA Physical Constants Model Squid 2", "1.0"));
            
            File squidTaskFile = RESOURCE_EXTRACTOR
                    .extractResourceAsFile(PRAWN_FILE_RESOURCE_Z6266_TASK_PERM1);
            squidProject.createTaskFromImportedSquid25Task(squidTaskFile);
            
            squidProject.setDelimiterForUnknownNames("-");
            squidProject.getTask().setFilterForRefMatSpotNames("6266");
            squidProject.getTask().setFilterForConcRefMatSpotNames("6266");
            
            squidProject.getTask().applyTaskIsotopeLabelsToMassStations();
            
            try {
                reportsEngine.produceReports(
                        squidProject.getTask().getShrimpFractions(),
                        (ShrimpFraction) squidProject.getTask().getUnknownSpots().get(0),
                        squidProject.getTask().getReferenceMaterialSpots().size() > 0
                        ? (ShrimpFraction) squidProject.getTask().getReferenceMaterialSpots().get(0) : (ShrimpFraction) squidProject.getTask().getUnknownSpots().get(0),
                        true, false);
            } catch (IOException iOException) {
            }
            
//            prawnFileHandler.writeReportsFromPrawnFile(prawnFilePath.toString(),
//                    useSBM,
//                    userLinFits,
//                    firstLetterRM);
            
            Files.delete(prawnFilePath);
            
           // Path reportsFolder = Paths.get(reportsEngine.getFolderToWriteCalamariReports().getPath()).toAbsolutePath();
            Path reportsFolder = Paths.get((new File(reportsEngine.getFolderToWriteCalamariReportsPath())).getParent());
            
            Path reports = Files.list(reportsFolder)
                    .findFirst().orElseThrow(() -> new IllegalStateException());
            
            reportsZip = zip(reports);
            recursiveDelete(reports);
        } catch (IOException | JAXBException | SAXException | SquidException iOException) {
        }

        return reportsZip;
    }

}
