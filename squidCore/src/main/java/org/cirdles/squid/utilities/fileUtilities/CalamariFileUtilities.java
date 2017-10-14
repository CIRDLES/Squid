/*
 * Copyright 2017 cirdles.org.
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

import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.cirdles.squid.core.PrawnFileHandler;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.utilities.FileUtilities;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.Squid;

/**
 *
 * @author bowring
 */
public class CalamariFileUtilities {

    private static File exampleFolder;
    private static File schemaFolder;

    /**
     * Provides a clean copy of two example Prawn XML files every time Squid
     * runs.
     */
    public static void initExamplePrawnFiles() {
        ResourceExtractor prawnFileResourceExtractor
                = new ResourceExtractor(PrawnFile.class);

        Path listOfPrawnFiles = prawnFileResourceExtractor.extractResourceAsPath("listOfPrawnFiles.txt");
        if (listOfPrawnFiles != null) {
            exampleFolder = new File("ExamplePrawnXMLFiles");
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
                                System.out.println("PrawnFile added: " + fileNames.get(i));
                            } else {
                                System.out.println("PrawnFile failed to add: " + fileNames.get(i));
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
            }

        }
    }

    /**
     * Loads PrawnFIle schema locally for use when Internet connection not available.
     */
    public static void loadShrimpPrawnFileSchema() {
        ResourceExtractor prawnFileResourceExtractor
                = new ResourceExtractor(Squid.class);

        schemaFolder = new File("Schema");
        try {
            if (schemaFolder.exists()) {
                FileUtilities.recursiveDelete(schemaFolder.toPath());
            }
            if (schemaFolder.mkdir()) {
                File shrimpPrawnFileSchemaResource = prawnFileResourceExtractor.extractResourceAsFile("schema/SHRIMP_PRAWN.xsd");
                File shrimpPrawnFileSchema = new File(schemaFolder.getCanonicalPath() + File.separator + "SHRIMP_PRAWN.xsd");

                if (shrimpPrawnFileSchemaResource.renameTo(shrimpPrawnFileSchema)) {
                    System.out.println("SHRIMP_PRAWN.xsd added.");
                } else {
                    System.out.println("Failed to add SHRIMP_PRAWN.xsd.");
                }
                
                File shrimpExpressionSchemaResource = prawnFileResourceExtractor.extractResourceAsFile("schema/SquidTask_ExpressionXMLSchema.xsd");
                File shrimpExpressionSchema = new File(schemaFolder.getCanonicalPath() + File.separator + "SquidTask_ExpressionXMLSchema.xsd");

                if (shrimpExpressionSchemaResource.renameTo(shrimpExpressionSchema)) {
                    System.out.println("SquidTask_ExpressionXMLSchema.xsd added.");
                } else {
                    System.out.println("Failed to add SquidTask_ExpressionXMLSchema.xsd.");
                }
            }
        } catch (IOException iOException) {
        }
    }

    public static void initCalamariReportsFolder(PrawnFileHandler prawnFileHandler) {
        prawnFileHandler.getReportsEngine()
                .setFolderToWriteCalamariReports(Squid.DEFAULT_SQUID3_REPORTS_FOLDER);
        
        prawnFileHandler.initReportsEngineWithCurrentPrawnFileName();
    }
}