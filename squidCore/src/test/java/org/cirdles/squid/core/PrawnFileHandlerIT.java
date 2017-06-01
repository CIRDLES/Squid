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

package org.cirdles.squid.core;

import java.io.File;
import static org.assertj.core.api.Assertions.assertThat;
import org.cirdles.commons.util.ResourceExtractor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

/**
 *
 * @author bowring
 */
public class PrawnFileHandlerIT {

    private static final String PRAWN_FILE_RESOURCE
            = "/org/cirdles/squid/prawn/100142_G6147_10111109.43.xml";

    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(PrawnFileHandlerIT.class);

    /**
     *
     */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     *
     */
    @Rule
    public Timeout timeout = Timeout.seconds(1200);

    private PrawnFileHandler prawnFileHandler;

    /**
     *
     */
    @Before
    public void setUp() {
        prawnFileHandler = new PrawnFileHandler();
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void writesReportsFromPrawnFile() throws Exception {
        File reportsFolder = temporaryFolder.getRoot();
        
        prawnFileHandler.getReportsEngine().setFolderToWriteCalamariReports(reportsFolder);

        File prawnFile = RESOURCE_EXTRACTOR
                .extractResourceAsFile(PRAWN_FILE_RESOURCE);

        prawnFileHandler.initReportsEngineWithCurrentPrawnFileName(PRAWN_FILE_RESOURCE);
        prawnFileHandler.writeReportsFromPrawnFile(prawnFile.getAbsolutePath(), // prawnFileLocation
                true,   // useSBM
                false,  // userLinFits
                "T");   // first letter of reference material                 
        
        assertThat(reportsFolder.listFiles()).hasSize(1); //Temp Calamari Reports Folder
        assertThat(reportsFolder.listFiles()[0].listFiles()).hasSize(1); //Reports folder with name of this Prawn File
        assertThat(reportsFolder.listFiles()[0].listFiles()[0]).isDirectory(); // the currently written folder of reports
        assertThat(reportsFolder.listFiles()[0].listFiles()[0].listFiles()).hasSize(6); // 6 reports

        for (File report : reportsFolder.listFiles()[0].listFiles()[0].listFiles()) {
            File expectedReport = RESOURCE_EXTRACTOR
                    .extractResourceAsFile(report.getName());

            assertThat(report).hasSameContentAs(expectedReport);
        }
    }

}
