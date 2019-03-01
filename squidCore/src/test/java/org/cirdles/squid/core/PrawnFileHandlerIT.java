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
import java.util.HashMap;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.reports.reportSettings.ReportSettings;
import org.cirdles.squid.reports.reportSettings.ReportSettingsInterface;
import org.cirdles.squid.shrimp.ShrimpDataFileInterface;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION;
import org.cirdles.squid.utilities.csvSerialization.ReportSerializerToCSV;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import static org.cirdles.squid.utilities.stateUtilities.SquidLabData.SQUID2_DEFAULT_PHYSICAL_CONSTANTS_MODEL_V1;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

/**
 *
 * @author bowring
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrawnFileHandlerIT {

    private static final String PRAWN_FILE_RESOURCE
            = "/org/cirdles/squid/prawn/100142_G6147_10111109.43.xml";

    private static final String PRAWN_FILE_RESOURCE_Z6266
            = "/org/cirdles/squid/prawn/836_1_2016_Nov_28_09.50.xml";

    private static final String PRAWN_FILE_RESOURCE_Z6266_TASK_PERM1
            = "/org/cirdles/squid/tasks/squidTask25/SquidTask_Z6266 = 11pk Perm1.SB.xls";
    
    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(PrawnFileHandlerIT.class);

    private static File reportsFolder;
    private static File prawnFileZ6266;
    private static SquidProject squidProjectZ6266;
    private static ShrimpDataFileInterface prawnFileDataZ6266;
    private static File squidTaskFileZ6266_TASK_PERM1;

    /**
     *
     */
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();
    @ClassRule
    public static TemporaryFolder temporaryFolderPerm1 = new TemporaryFolder();

    /**
     *
     */
    @Rule
    public Timeout timeout = Timeout.seconds(120);

    private static PrawnXMLFileHandler prawnFileHandler;

    /**
     *
     */
    @BeforeClass // provides to run setup once
    public static void setUp() throws Exception {
        prawnFileHandler = (new SquidProject()).getPrawnFileHandler();
        reportsFolder = temporaryFolder.getRoot();

        prawnFileZ6266 = RESOURCE_EXTRACTOR
                .extractResourceAsFile(PRAWN_FILE_RESOURCE_Z6266);

        CalamariFileUtilities.initSampleParametersModels();

        squidProjectZ6266 = new SquidProject();

        prawnFileDataZ6266 = prawnFileHandler.unmarshallPrawnFileXML(prawnFileZ6266.getAbsolutePath(), true);

        squidTaskFileZ6266_TASK_PERM1 = RESOURCE_EXTRACTOR
                .extractResourceAsFile(PRAWN_FILE_RESOURCE_Z6266_TASK_PERM1);

        squidProjectZ6266.setPrawnFile(prawnFileDataZ6266);
        squidProjectZ6266.createTaskFromImportedSquid25Task(squidTaskFileZ6266_TASK_PERM1);

        squidProjectZ6266.setDelimiterForUnknownNames("-");
        squidProjectZ6266.getTask().setFilterForRefMatSpotNames("6266");
        squidProjectZ6266.getTask().setFilterForConcRefMatSpotNames("6266");

        // overcome user preferences
        squidProjectZ6266.getTask().setTaskType(Squid3Constants.TaskTypeEnum.GEOCHRON);
        squidProjectZ6266.getTask().setUseSBM(true);
        squidProjectZ6266.getTask().setUserLinFits(false);
        squidProjectZ6266.getTask().setSquidAllowsAutoExclusionOfSpots(true);

        squidProjectZ6266.getTask().setExtPErrU(0.75);
        squidProjectZ6266.getTask().setExtPErrTh(0.75);
        squidProjectZ6266.getTask().setPhysicalConstantsModel(PhysicalConstantsModel.getDefaultModel(SQUID2_DEFAULT_PHYSICAL_CONSTANTS_MODEL_V1, "1.0"));
        squidProjectZ6266.getTask().setCommonPbModel(CommonPbModel.getDefaultModel("GA Common Lead 2018", "1.0"));
        squidProjectZ6266.getTask().setReferenceMaterial(ReferenceMaterialModel.getDefaultModel("GA Accepted BR266", "1.0"));
        squidProjectZ6266.getTask().setConcentrationReferenceMaterial(ReferenceMaterialModel.getDefaultModel("GA Accepted BR266", "1.0"));

        squidProjectZ6266.getTask().applyTaskIsotopeLabelsToMassStations();
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void writesReportsFromPrawnFile() throws Exception {
        prawnFileHandler.getReportsEngine().setFolderToWriteCalamariReports(reportsFolder);
        File prawnFile = RESOURCE_EXTRACTOR.extractResourceAsFile(PRAWN_FILE_RESOURCE);
        prawnFileHandler.initReportsEngineWithCurrentPrawnFileName(PRAWN_FILE_RESOURCE);

        ShrimpDataFileInterface prawnFileData
                = prawnFileHandler.unmarshallPrawnFileXML(prawnFile.getAbsolutePath(), true);

        List<SquidSpeciesModel> squidSpeciesModelList = new ArrayList<>();
        squidSpeciesModelList.add(new SquidSpeciesModel(0, "196Zr2O", "196", "Zr2O", false, "No", false));
        squidSpeciesModelList.add(new SquidSpeciesModel(1, "204Pb", "204", "Pb", false, "No", false));
        squidSpeciesModelList.add(new SquidSpeciesModel(2, "Bkgnd", "Bkgnd", "Bkgnd", true, "No", false));
        squidSpeciesModelList.add(new SquidSpeciesModel(3, "206Pb", "206", "Pb", false, "No", false));
        squidSpeciesModelList.add(new SquidSpeciesModel(4, "207Pb", "207", "Pb", false, "No", false));
        squidSpeciesModelList.add(new SquidSpeciesModel(5, "208Pb", "208", "Pb", false, "No", false));
        squidSpeciesModelList.add(new SquidSpeciesModel(6, "238U", "238", "U", false, "No", false));
        squidSpeciesModelList.add(new SquidSpeciesModel(7, "248ThO", "248", "ThO", false, "No", false));
        squidSpeciesModelList.add(new SquidSpeciesModel(8, "254UO", "254", "UO", false, "No", false));
        squidSpeciesModelList.add(new SquidSpeciesModel(9, "270UO2", "270", "UO2", false, "No", false));

        List<SquidRatiosModel> squidRatiosModelList = new ArrayList<>();
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(1), squidSpeciesModelList.get(3), 0));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(4), squidSpeciesModelList.get(3), 1));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(5), squidSpeciesModelList.get(3), 2));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(6), squidSpeciesModelList.get(0), 3));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(3), squidSpeciesModelList.get(6), 4));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(8), squidSpeciesModelList.get(6), 5));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(7), squidSpeciesModelList.get(8), 6));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(3), squidSpeciesModelList.get(9), 7));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(9), squidSpeciesModelList.get(8), 8));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(3), squidSpeciesModelList.get(8), 9));
        squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(6), squidSpeciesModelList.get(3), 10));

        TaskInterface task = new Task();
        // overcome user preferences
        task.setTaskType(Squid3Constants.TaskTypeEnum.GEOCHRON);
        task.setUseSBM(true);
        task.setUserLinFits(false);
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        task.setSquidAllowsAutoExclusionOfSpots(true);
        task.setExtPErrU(0.75);
        task.setPhysicalConstantsModel(PhysicalConstantsModel.getDefaultModel(SQUID2_DEFAULT_PHYSICAL_CONSTANTS_MODEL_V1, "1.0"));
        task.setCommonPbModel(CommonPbModel.getDefaultModel("GA Common Lead 2018", "1.0"));
        task.setReferenceMaterial(ReferenceMaterialModel.getDefaultModel("GA Accepted BR266", "1.0"));
        task.setConcentrationReferenceMaterial(ReferenceMaterialModel.getDefaultModel("GA Accepted BR266", "1.0"));

        SquidSessionModel squidSessionModel = new SquidSessionModel(squidSpeciesModelList, squidRatiosModelList, true, false, 2, "T", "", new HashMap<>());
        List<ShrimpFractionExpressionInterface> shrimpFractions = task.processRunFractions(prawnFileData, squidSessionModel);

        try {
            prawnFileHandler.getReportsEngine().produceReports(shrimpFractions, true, false);
        } catch (IOException iOException) {
        }

        // Dec 2018 represents temp / PROJECT / TASK / PRAWN /
        // NOTE: this works on MACOS because it executes before creation of .dstore files
        File targetFolder = reportsFolder.listFiles((File current, String name) -> new File(current, name).isDirectory())[0];
        assertThat(targetFolder.listFiles()[0].listFiles()[0].listFiles()[0].listFiles()).hasSize(6); // 6 reports

        // reportsFolder has produced reports
        for (File report : targetFolder.listFiles()[0].listFiles()[0].listFiles()[0].listFiles()) {
            File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(report.getName());
            assertThat(report).hasSameContentAs(expectedReport);
        }
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tA_testingOutputForZ6266Perm1_4corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm1 with 4cor unknowns.");
        squidProjectZ6266.getTask().setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        // force defaults for testing
        squidProjectZ6266.getTask().getSpecialSquidFourExpressionsMap()
                .put(PARENT_ELEMENT_CONC_CONST, "[\"238/195.8\"]/[\"254/238\"]^0.66");
        squidProjectZ6266.getTask().getSpecialSquidFourExpressionsMap()
                .put(UNCOR206PB238U_CALIB_CONST, UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION);
        squidProjectZ6266.getTask().getSpecialSquidFourExpressionsMap()
                .put(UNCOR208PB232TH_CALIB_CONST, UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION);
        squidProjectZ6266.getTask().getSpecialSquidFourExpressionsMap()
                .put(TH_U_EXP_DEFAULT, TH_U_EXP_DEFAULT_EXPRESSION);
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm1_4Corr_Unknowns.csv");
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tB_testingOutputForZ6266Perm1_7corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm1 with 7cor unknowns.");
        // change selected index isotope
        squidProjectZ6266.getTask().setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm1_7Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tC_testingOutputForZ6266Perm1_8corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm1 with 8cor unknowns.");
        // change selected index isotope
        squidProjectZ6266.getTask().setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_208);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm1_8Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tD_testingOutputForZ6266Perm1_4corrRM() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm1 with 4cor reference materials.");
        // change selected index isotope
        squidProjectZ6266.getTask().setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm1_RefMat.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", true, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getReferenceMaterialSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tE_testingOutputForZ6266Perm2_4corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm2 with 4cor unknowns.");
        // change to directALT
        squidProjectZ6266.getTask().setDirectAltPD(true);
        // keep selected index isotope
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().applyDirectives();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm2_4Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tF_testingOutputForZ6266Perm2_7corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm2 with 7cor unknowns.");
        // change selected index isotope
        squidProjectZ6266.getTask().setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm2_7Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tG_testingOutputForZ6266Perm2_4corrRM() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm2 with 4cor reference materials.");
        // change selected index isotope
        squidProjectZ6266.getTask().setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm2_RefMat.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", true, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getReferenceMaterialSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tH_testingOutputForZ6266Perm4_4corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm4 with 4cor unknowns.");
        // change to 232
        squidProjectZ6266.getTask().setParentNuclide("232");
        // keep selected index isotope
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().applyDirectives();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm4_4Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tI_testingOutputForZ6266Perm4_7corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm4 with 7cor unknowns.");
        // change selected index isotope
        squidProjectZ6266.getTask().setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm4_7Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tJ_testingOutputForZ6266Perm4_4corrRM() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm4 with 4cor reference materials.");
        // change selected index isotope
        squidProjectZ6266.getTask().setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm4_RefMat.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", true, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getReferenceMaterialSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tK_testingOutputForZ6266Perm3_4corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm3 with 4cor unknowns.");
        // change to indirectALT
        squidProjectZ6266.getTask().setDirectAltPD(false);
        // keep selected index isotope
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().applyDirectives();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm3_4Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tL_testingOutputForZ6266Perm3_7corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm3 with 7cor unknowns.");
        // change selected index isotope
        squidProjectZ6266.getTask().setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm3_7Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void tM_testingOutputForZ6266Perm3_4corrRM() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm2 with 4cor reference materials.");
        // change selected index isotope
        squidProjectZ6266.getTask().setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport();
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm3_RefMat.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", true, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getReferenceMaterialSpots(), true);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameContentAs(expectedReport);
    }
}
