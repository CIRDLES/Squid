/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
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
    public Timeout timeout = Timeout.seconds(120);

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
        
        PrawnFile prawnFileData = prawnFileHandler.unmarshallPrawnFileXML(prawnFile.getAbsolutePath());
        
        List<SquidSpeciesModel> squidSpeciesModelList = new ArrayList<>();
        squidSpeciesModelList.add(new SquidSpeciesModel(0, "196Zr2O", "196", "Zr2O", false, "No"));
        squidSpeciesModelList.add(new SquidSpeciesModel(1, "204Pb", "204", "Pb", false, "No"));
        squidSpeciesModelList.add(new SquidSpeciesModel(2, "Bkgnd", "Bkgnd", "Bkgnd", true, "No"));
        squidSpeciesModelList.add(new SquidSpeciesModel(3, "206Pb", "206", "Pb", false, "No"));
        squidSpeciesModelList.add(new SquidSpeciesModel(4, "207Pb", "207", "Pb", false, "No"));
        squidSpeciesModelList.add(new SquidSpeciesModel(5, "208Pb", "208", "Pb", false, "No"));
        squidSpeciesModelList.add(new SquidSpeciesModel(6, "238U", "238", "U", false, "No"));
        squidSpeciesModelList.add(new SquidSpeciesModel(7, "248ThO", "248", "ThO", false, "No"));
        squidSpeciesModelList.add(new SquidSpeciesModel(8, "254UO", "254", "UO", false, "No"));
        squidSpeciesModelList.add(new SquidSpeciesModel(9, "270UO2", "270", "UO2", false, "No"));

        List<SquidRatiosModel> squidRatiosModelList = new ArrayList<>();
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(1), squidSpeciesModelList.get(3),0));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(4), squidSpeciesModelList.get(3),1));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(5), squidSpeciesModelList.get(3),2));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(6), squidSpeciesModelList.get(0),3));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(3), squidSpeciesModelList.get(6),4));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(8), squidSpeciesModelList.get(6),5));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(7), squidSpeciesModelList.get(8),6));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(3), squidSpeciesModelList.get(9),7));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(9), squidSpeciesModelList.get(8),8));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(3), squidSpeciesModelList.get(8),9));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(6), squidSpeciesModelList.get(3),10));

        TaskInterface task = new Task();
        SquidSessionModel squidSessionModel = new SquidSessionModel(squidSpeciesModelList, squidRatiosModelList, true, false, 2, "T", "");
        List<ShrimpFractionExpressionInterface> shrimpFractions = task.processRunFractions(prawnFileData, squidSessionModel);
        
        try {
            prawnFileHandler.getReportsEngine().produceReports(shrimpFractions, true, false);
        } catch (IOException iOException) {
        }             
        
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
