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
package org.cirdles.squid.utilities.fileUtilities;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.Squid;
import org.cirdles.squid.core.PrawnXMLFileHandler;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.ExpressionPublisher;
import org.cirdles.squid.utilities.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.cirdles.squid.constants.Squid3Constants.*;
import static org.cirdles.squid.utilities.FileUtilities.unpackZipFile;

/**
 * @author bowring
 */
public class CalamariFileUtilities {

    private static File exampleFolder;
    private static File physicalConstantsFolder;
    private static File referenceMaterialsFolder;
    private static File commonPbModelsFolder;
    public static File XSLTMLFolder;

    public static List<String> taskLibraryFileNamesList;

    // June 2021 reworked
    static {
        try {
            NAME_OF_SQUID_RESOURCES_FOLDER.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SQUID_PARAMETER_MODELS_FOLDER.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Provides a clean copy of two example Prawn XML files every time Squid
     * runs.
     */
    public static void initExamplePrawnFiles() {
        ResourceExtractor prawnFileResourceExtractor
                = new ResourceExtractor(PrawnFile.class);

        Path listOfPrawnFiles = prawnFileResourceExtractor.extractResourceAsPath("listOfPrawnFiles.txt");
        if (listOfPrawnFiles != null) {
            exampleFolder = new File(NAME_OF_SQUID_RESOURCES_FOLDER.getAbsolutePath() + File.separator + "ExamplePrawnXMLFiles");
            try {
                if (exampleFolder.exists()) {
                    FileUtilities.recursiveDelete(exampleFolder.toPath());
                }
                if (exampleFolder.mkdir()) {
                    List<String> fileNames = Files.readAllLines(listOfPrawnFiles, ISO_8859_1);
                    for (int i = 0; i < fileNames.size(); i++) {
                        // test for empty string
                        if (fileNames.get(i).trim().length() > 0) {
                            File prawnFileResource = prawnFileResourceExtractor.extractResourceAsFile(fileNames.get(i));
                            File prawnFile = new File(exampleFolder.getCanonicalPath() + File.separator + fileNames.get(i));

                            if (prawnFileResource.renameTo(prawnFile)) {
                            } else {
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
            }

        }
    }

    /**
     * Provides a clean copy of demo Squid project file(s) every time Squid
     * runs.
     */
    public static void initDemoSquidProjectFiles() {
        ResourceExtractor prawnFileResourceExtractor
                = new ResourceExtractor(SquidProject.class);

        Path listOfSquidProjectFiles = prawnFileResourceExtractor.extractResourceAsPath("listOfSquidProjectFiles.txt");
        if (listOfSquidProjectFiles != null) {
            try {
                if (DEMO_SQUID_PROJECTS_FOLDER.exists()) {
                    FileUtilities.recursiveDelete(DEMO_SQUID_PROJECTS_FOLDER.toPath());
                }
                if (DEMO_SQUID_PROJECTS_FOLDER.mkdir()) {
                    List<String> fileNames = Files.readAllLines(listOfSquidProjectFiles, ISO_8859_1);
                    for (int i = 0; i < fileNames.size(); i++) {
                        // test for empty string
                        if (fileNames.get(i).trim().length() > 0) {
                            File prawnFileResource = prawnFileResourceExtractor.extractResourceAsFile(fileNames.get(i));
                            File prawnFile = new File(DEMO_SQUID_PROJECTS_FOLDER.getCanonicalPath() + File.separator + fileNames.get(i));

                            if (prawnFileResource.renameTo(prawnFile)) {
                            } else {
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
            }
        }
    }

    /**
     * Provides a clean copy of Squid Task Library file(s) every time Squid
     * runs.
     */
    public static void initSquidTaskLibraryFiles() {
        ResourceExtractor taskFileResourceExtractor
                = new ResourceExtractor(Task.class);

        Path listOfTaskXMLFiles = taskFileResourceExtractor.extractResourceAsPath("listOfTaskXMLFiles.txt");
        if (listOfTaskXMLFiles != null) {
            try {
                if (SQUID_TASK_LIBRARY_FOLDER.exists()) {
                    FileUtilities.recursiveDelete(SQUID_TASK_LIBRARY_FOLDER.toPath());
                }
                if (SQUID_TASK_LIBRARY_FOLDER.mkdir()) {
                    taskLibraryFileNamesList = Files.readAllLines(listOfTaskXMLFiles, ISO_8859_1);
                    for (int i = 0; i < taskLibraryFileNamesList.size(); i++) {
                        // test for empty string
                        if (taskLibraryFileNamesList.get(i).trim().length() > 0) {
                            File taskXMLFileResource = taskFileResourceExtractor.extractResourceAsFile(taskLibraryFileNamesList.get(i));
                            File taskFile = new File(SQUID_TASK_LIBRARY_FOLDER.getCanonicalPath() + File.separator + taskLibraryFileNamesList.get(i));

                            if (taskXMLFileResource.renameTo(taskFile)) {
                            } else {
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
            }
        }
    }

    public static void initXSLTML() {
        ResourceExtractor extractor = new ResourceExtractor(ExpressionPublisher.class);

        Path listOfXSLTMLFiles = extractor.extractResourceAsPath("listOfXSLTMLFiles.txt");
        if (listOfXSLTMLFiles != null) {
            XSLTMLFolder = new File(NAME_OF_SQUID_RESOURCES_FOLDER.getAbsolutePath() + File.separator + "XSLTML");
            try {
                if (XSLTMLFolder.exists()) {
                    FileUtilities.recursiveDelete(XSLTMLFolder.toPath());
                }
                if (XSLTMLFolder.mkdir()) {
                    List<String> fileNames = Files.readAllLines(listOfXSLTMLFiles, ISO_8859_1);
                    for (int i = 0; i < fileNames.size(); i++) {
                        // test for empty string
                        if (fileNames.get(i).trim().length() > 0) {
                            File resource = extractor.extractResourceAsFile(fileNames.get(i));
                            File file = new File(XSLTMLFolder.getCanonicalPath() + File.separator + fileNames.get(i));

                            if (resource.renameTo(file)) {
                                System.out.println("XSLTML File added: " + fileNames.get(i));
                            } else {
                                System.out.println("XSLTML File failed to add: " + fileNames.get(i));
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public static void initSampleParametersModels() {

        ResourceExtractor physConstResourceExtractor = new ResourceExtractor(PhysicalConstantsModel.class);

        Path listOfPhysicalConstants = physConstResourceExtractor.extractResourceAsPath("listOfSquidPhysicalConstantsModels.txt");
        if (listOfPhysicalConstants != null) {
            physicalConstantsFolder = new File(SQUID_PARAMETER_MODELS_FOLDER.getAbsolutePath() + File.separator + "SquidPhysicalConstantsModels");
            try {
                if (physicalConstantsFolder.exists()) {
                    FileUtilities.recursiveDelete(physicalConstantsFolder.toPath());
                }
                if (physicalConstantsFolder.mkdir()) {
                    List<String> fileNames = Files.readAllLines(listOfPhysicalConstants, ISO_8859_1);
                    for (int i = 0; i < fileNames.size(); i++) {
                        // test for empty string
                        if (fileNames.get(i).trim().length() > 0) {
                            File physConstResource = physConstResourceExtractor.extractResourceAsFile(fileNames.get(i));
                            File physConstFile = new File(physicalConstantsFolder.getCanonicalPath() + File.separator + fileNames.get(i));

                            if (physConstResource.renameTo(physConstFile)) {
                                System.out.println("PhysicalConstantsModelFile added: " + fileNames.get(i));
                            } else {
                                System.out.println("PhysicalConstantsModelFile failed to add: " + fileNames.get(i));
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }

        }

        ResourceExtractor refMatResourceExtractor = new ResourceExtractor(ReferenceMaterialModel.class);

        Path listOfReferenceMaterials = refMatResourceExtractor.extractResourceAsPath("listOfSquidReferenceMaterials.txt");
        if (listOfReferenceMaterials != null) {
            referenceMaterialsFolder = new File(SQUID_PARAMETER_MODELS_FOLDER.getAbsolutePath() + File.separator + "SquidReferenceMaterialModels");
            try {
                if (referenceMaterialsFolder.exists()) {
                    FileUtilities.recursiveDelete(referenceMaterialsFolder.toPath());
                }
                if (referenceMaterialsFolder.mkdir()) {
                    List<String> fileNames = Files.readAllLines(listOfReferenceMaterials, ISO_8859_1);
                    for (int i = 0; i < fileNames.size(); i++) {
                        // test for empty string
                        if (fileNames.get(i).trim().length() > 0) {
                            File refMatResource = refMatResourceExtractor.extractResourceAsFile(fileNames.get(i));
                            File refMatFile = new File(referenceMaterialsFolder.getCanonicalPath() + File.separator + fileNames.get(i));

                            if (refMatResource.renameTo(refMatFile)) {
                                System.out.println("ReferenceMaterialFile added: " + fileNames.get(i));
                            } else {
                                System.out.println("ReferenceMaterialFile failed to add: " + fileNames.get(i));
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }

        }

        ResourceExtractor commonPbResourceExtractor = new ResourceExtractor(CommonPbModel.class);

        Path listOfCommonPbModels = commonPbResourceExtractor.extractResourceAsPath("listOfSquidCommonPbModels.txt");
        if (listOfCommonPbModels != null) {
            commonPbModelsFolder = new File(SQUID_PARAMETER_MODELS_FOLDER.getAbsolutePath() + File.separator + "SquidCommonPbModels");
            try {
                if (commonPbModelsFolder.exists()) {
                    FileUtilities.recursiveDelete(commonPbModelsFolder.toPath());
                }
                if (commonPbModelsFolder.mkdir()) {
                    List<String> fileNames = Files.readAllLines(listOfCommonPbModels, ISO_8859_1);
                    for (int i = 0; i < fileNames.size(); i++) {
                        // test for empty string
                        if (fileNames.get(i).trim().length() > 0) {
                            File commonPbResource = commonPbResourceExtractor.extractResourceAsFile(fileNames.get(i));
                            File commonPbFile = new File(commonPbModelsFolder.getCanonicalPath() + File.separator + fileNames.get(i));

                            if (commonPbResource.renameTo(commonPbFile)) {
                                System.out.println("Common Pb Model added: " + fileNames.get(i));
                            } else {
                                System.out.println("Common Pb Model failed to add: " + fileNames.get(i));
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }

        }
    }

    /**
     * Loads PrawnFile schema locally for use when Internet connection not
     * available.
     */
    public static void loadShrimpFileSchema() {
        ResourceExtractor prawnFileResourceExtractor
                = new ResourceExtractor(Squid.class);
        try {
            if (SCHEMA_FOLDER.exists()) {
                FileUtilities.recursiveDelete(SCHEMA_FOLDER.toPath());
            }
            if (SCHEMA_FOLDER.mkdir()) {
                File shrimpPrawnFileSchemaResource = prawnFileResourceExtractor.extractResourceAsFile("schema/SHRIMP_PRAWN.xsd");
                File shrimpPrawnFileSchema = new File(SCHEMA_FOLDER.getCanonicalPath() + File.separator + "SHRIMP_PRAWN.xsd");

                if (shrimpPrawnFileSchemaResource.renameTo(shrimpPrawnFileSchema)) {
                    //System.out.println("SHRIMP_PRAWN.xsd added.");
                } else {
                    //System.out.println("Failed to add SHRIMP_PRAWN.xsd.");
                }

                File shrimpPrawnLegacyFileSchemaResource = prawnFileResourceExtractor.extractResourceAsFile("schema/SHRIMP_PRAWN_LEGACY.xsd");
                File shrimpPrawnLegacyFileSchema = new File(SCHEMA_FOLDER.getCanonicalPath() + File.separator + "SHRIMP_PRAWN_LEGACY.xsd");

                if (shrimpPrawnLegacyFileSchemaResource.renameTo(shrimpPrawnLegacyFileSchema)) {
                    //System.out.println("SHRIMP_PRAWN.xsd added.");
                } else {
                    //System.out.println("Failed to add SHRIMP_PRAWN.xsd.");
                }

                File shrimpExpressionSchemaResource = prawnFileResourceExtractor.extractResourceAsFile("schema/SquidTask_ExpressionXMLSchema.xsd");
                File shrimpExpressionSchema = new File(SCHEMA_FOLDER.getCanonicalPath() + File.separator + "SquidTask_ExpressionXMLSchema.xsd");

                if (shrimpExpressionSchemaResource.renameTo(shrimpExpressionSchema)) {
                    //System.out.println("SquidTask_ExpressionXMLSchema.xsd added.");
                } else {
                    //System.out.println("Failed to add SquidTask_ExpressionXMLSchema.xsd.");
                }


                File squidTaskSchemaResource = prawnFileResourceExtractor.extractResourceAsFile("schema/SquidTask_XMLSchema.xsd");
                File squidTaskSchema = new File(SCHEMA_FOLDER.getCanonicalPath() + File.separator + "SquidTask_XMLSchema.xsd");

                if (squidTaskSchemaResource.renameTo(squidTaskSchema)) {
                    //System.out.println("SquidTask_XMLSchema.xsd added.");
                } else {
                    //System.out.println("Failed to add SquidTask_XMLSchema.xsd.");
                }

                File squidReportTableSchemaResource = prawnFileResourceExtractor.extractResourceAsFile("schema/SquidReportTable.xsd");
                File squidReportTableSchema = new File(SCHEMA_FOLDER.getCanonicalPath() + File.separator + "SquidReportTable.xsd");

                if (squidReportTableSchemaResource.renameTo(squidReportTableSchema)) {
                    //System.out.println("SquidReportTable.xsd added.");
                } else {
                    //System.out.println("Failed to add SquidReportTable.xsd.");

                }
            }
        } catch (IOException iOException) {
        }
    }

    public static void loadJavadoc() {

        try {
            if (LUDWIGLIBRARY_JAVADOC_FOLDER.exists()) {
                FileUtilities.recursiveDelete(LUDWIGLIBRARY_JAVADOC_FOLDER.toPath());
            }
            if (LUDWIGLIBRARY_JAVADOC_FOLDER.mkdir()) {
                File ludwigLibraryJavadocResource = Squid.SQUID_RESOURCE_EXTRACTOR.extractResourceAsFile("javadoc/LudwigLibrary-javadoc.jar");
                File ludwigLibraryJavadoc = new File(LUDWIGLIBRARY_JAVADOC_FOLDER.getAbsolutePath() + File.separator + "LudwigLibraryJavadoc.jar");

                if (ludwigLibraryJavadocResource.renameTo(ludwigLibraryJavadoc)) {
                    try {
                        unpackZipFile(ludwigLibraryJavadoc, LUDWIGLIBRARY_JAVADOC_FOLDER);
                    } catch (IOException iOException) {
                    }
                } else {
                    //System.out.println("Failed to add LudwigLibraryJavadoc.jar.");
                }
            } else {
                //System.out.println("Failed to make LudwigLibraryJavadoc folder.");
            }
        } catch (IOException iOException) {
        }
    }

    /**
     * @param prawnFileHandler the value of prawnFileHandler
     * @param folder           the value of folder
     */
    public static void initCalamariReportsFolder(PrawnXMLFileHandler prawnFileHandler, File folder) {
        prawnFileHandler.getReportsEngine()
                .setFolderToWriteCalamariReports(folder);

        prawnFileHandler.initReportsEngineWithCurrentPrawnFileName();
    }
}