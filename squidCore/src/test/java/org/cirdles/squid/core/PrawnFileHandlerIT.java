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

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.reports.reportSettings.ReportSettings;
import org.cirdles.squid.reports.reportSettings.ReportSettingsInterface;
import org.cirdles.squid.shrimp.CommonLeadSpecsForSpot;
import org.cirdles.squid.shrimp.ShrimpDataFileInterface;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.taskDesign.TaskDesign11Mass;
import org.cirdles.squid.utilities.csvSerialization.ReportSerializerToCSV;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GEOCHRON;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;
import static org.cirdles.squid.utilities.stateUtilities.SquidLabData.SQUID2_DEFAULT_PHYSICAL_CONSTANTS_MODEL_V1;

/**
 * @author bowring
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrawnFileHandlerIT {

    private static final String PRAWN_FILE_RESOURCE_Z6266
            = "/org/cirdles/squid/prawn/836_1_2016_Nov_28_09.50.xml";

    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(PrawnFileHandlerIT.class);
    /**
     *
     */
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();
    @ClassRule
    public static TemporaryFolder temporaryFolderPerm1 = new TemporaryFolder();
    private static File reportsFolder;
    private static File prawnFileZ6266;
    private static SquidProject squidProjectZ6266;
    private static ShrimpDataFileInterface prawnFileDataZ6266;
    private static PrawnXMLFileHandler prawnFileHandler;
    /**
     *
     */
    @Rule
    public Timeout timeout = Timeout.seconds(120);

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass // provides to run setup once
    public static void setUp() throws Exception {
        // april 2022 to preserve tests due to change in issue #698
        CommonLeadSpecsForSpot.DEFAULT_METHOD = CommonLeadSpecsForSpot.METHOD_COMMON_LEAD_MODEL;

        prawnFileHandler = (new SquidProject(GEOCHRON)).getPrawnFileHandler();
        reportsFolder = temporaryFolder.getRoot();

        prawnFileZ6266 = RESOURCE_EXTRACTOR
                .extractResourceAsFile(PRAWN_FILE_RESOURCE_Z6266);

        CalamariFileUtilities.initSampleParametersModels();

        squidProjectZ6266 = new SquidProject(GEOCHRON);

        prawnFileDataZ6266 = prawnFileHandler.unmarshallPrawnFileXML(prawnFileZ6266.getAbsolutePath(), true);

        squidProjectZ6266.setPrawnFile(prawnFileDataZ6266);

        // March 2019 remove dependency on Squid25 task for testing of built-ins
        SquidProject.setProjectChanged(true);
        TaskInterface task = new Task("test", prawnFileDataZ6266, prawnFileHandler.getReportsEngine());

        task.updateTaskFromTaskDesign(new TaskDesign11Mass(), false);

        squidProjectZ6266.setTask(task);

        squidProjectZ6266.setDelimiterForUnknownNames("-");
        squidProjectZ6266.getTask().setFilterForRefMatSpotNames("6266");
        squidProjectZ6266.getTask().setFilterForConcRefMatSpotNames("6266");

        squidProjectZ6266.getTask().setTaskType(Squid3Constants.TaskTypeEnum.GEOCHRON);
        squidProjectZ6266.getTask().setUseSBM(true);
        squidProjectZ6266.setUseSBM(true);
        squidProjectZ6266.getTask().setUserLinFits(false);
        squidProjectZ6266.setUserLinFits(false);
        squidProjectZ6266.getTask().setSquidAllowsAutoExclusionOfSpots(true);
        squidProjectZ6266.setSquidAllowsAutoExclusionOfSpots(true);

        squidProjectZ6266.getTask().setExtPErrU(0.75);
        squidProjectZ6266.setExtPErrU(0.75);
        squidProjectZ6266.getTask().setExtPErrTh(0.75);
        squidProjectZ6266.setExtPErrTh(0.75);
        squidProjectZ6266.getTask().setPhysicalConstantsModel(PhysicalConstantsModel.getDefaultModel(SQUID2_DEFAULT_PHYSICAL_CONSTANTS_MODEL_V1, "1.0"));
        squidProjectZ6266.setPhysicalConstantsModel(PhysicalConstantsModel.getDefaultModel(SQUID2_DEFAULT_PHYSICAL_CONSTANTS_MODEL_V1, "1.0"));
        squidProjectZ6266.getTask().setCommonPbModel(CommonPbModel.getDefaultModel("Stacey-Kramers@559.0Ma (z6266)", "1.0"));
        squidProjectZ6266.setCommonPbModel(CommonPbModel.getDefaultModel("Stacey-Kramers@559.0Ma (z6266)", "1.0"));

        // modified sept 202 to accommodate old tests and new models
        ResourceExtractor extractor = new ResourceExtractor(ReferenceMaterialModel.class);
        File testingModelFile = extractor.extractResourceAsFile("GA Accepted BR266 v.1.0.xml");
        ReferenceMaterialModel testingModel = new ReferenceMaterialModel();
        testingModel = (ReferenceMaterialModel) testingModel.readXMLObject(testingModelFile.getAbsolutePath(), false);
        squidProjectZ6266.getTask().setReferenceMaterialModel(testingModel);
        squidProjectZ6266.setReferenceMaterialModel(testingModel);
        squidProjectZ6266.getTask().setConcentrationReferenceMaterialModel(testingModel);
        squidProjectZ6266.setConcentrationReferenceMaterialModel(testingModel);

        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);

        // force defaults for testing of builtins
        squidProjectZ6266.getTask().getSpecialSquidFourExpressionsMap()
                .put(PARENT_ELEMENT_CONC_CONST, "[\"238/195.8\"]/[\"254/238\"]^0.66");
        squidProjectZ6266.getTask().getSpecialSquidFourExpressionsMap()
                .put(UNCOR206PB238U_CALIB_CONST, UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION);
        squidProjectZ6266.getTask().getSpecialSquidFourExpressionsMap()
                .put(UNCOR208PB232TH_CALIB_CONST, UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION);
        squidProjectZ6266.getTask().getSpecialSquidFourExpressionsMap()
                .put(TH_U_EXP_DEFAULT, TH_U_EXP_DEFAULT_EXPRESSION);
        squidProjectZ6266.getTask().setSquidAllowsAutoExclusionOfSpots(true);

        squidProjectZ6266.getTask().applyTaskIsotopeLabelsToMassStations();
    }

    /**
     * @throws Exception
     */
    @Test
    public void writesReportsFromPrawnFile() throws Exception {
        prawnFileHandler.getReportsEngine().setFolderToWriteCalamariReports(reportsFolder);

        CalamariReportsEngine reportsEngine = prawnFileHandler.getReportsEngine();

        try {
            reportsEngine.produceReports(squidProjectZ6266.getTask().getShrimpFractions(), true, false);
        } catch (IOException iOException) {
        }

        // Dec 2018 represents temp / PROJECT / TASK / PRAWN /
        // JUL 2020 represents temp / PROJECT / PRAWN / 6 reports plus 1 pdf file
        // NOTE: this works on MACOS because it executes before creation of .dstore files
        File targetFolder = reportsFolder.listFiles((File current, String name) -> new File(current, name).isDirectory())[0];
        assertThat(targetFolder.listFiles()[0].listFiles()[0].listFiles()).hasSize(7); // 6 reports plus 1 pdf

        // reportsFolder has produced reports
        for (File report : targetFolder.listFiles()[0].listFiles()[0].listFiles()) {
            // ignore pdf files
            if (report.getAbsolutePath().endsWith(".csv")) {
                File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(report.getName());
                //assertThat(report).hasSameContentAs(expectedReport);
                assertThat(report).usingCharset(StandardCharsets.UTF_8).hasSameTextualContentAs(expectedReport, StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void tA_testingOutputForZ6266Perm1_4corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm1 with 4cor unknowns.");

        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().applyDirectives();
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm1_4Corr_Unknowns.csv");
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
     * @throws Exception
     */
    @Test
    public void tB_testingOutputForZ6266Perm1_7corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm1 with 7cor unknowns.");
        // change selected index isotope
        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm1_7Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        // assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
     * @throws Exception
     */
    @Test
    public void tC_testingOutputForZ6266Perm1_8corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm1 with 8cor unknowns.");
        // change selected index isotope
        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_208);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm1_8Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
     * @throws Exception
     */
    @Test
    public void tD_testingOutputForZ6266Perm1_4corrRM() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm1 with 4cor reference materials.");
        // change selected index isotope
        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm1_RefMat.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", true, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getReferenceMaterialSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
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
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
     * @throws Exception
     */
    @Test
    public void tF_testingOutputForZ6266Perm2_7corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm2 with 7cor unknowns.");
        // change selected index isotope
        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm2_7Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
     * @throws Exception
     */
    @Test
    public void tG_testingOutputForZ6266Perm2_4corrRM() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm2 with 4cor reference materials.");
        // change selected index isotope
        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm2_RefMat.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", true, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getReferenceMaterialSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
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
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
     * @throws Exception
     */
    @Test
    public void tI_testingOutputForZ6266Perm4_7corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm4 with 7cor unknowns.");
        // change selected index isotope
        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm4_7Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
     * @throws Exception
     */
    @Test
    public void tJ_testingOutputForZ6266Perm4_4corrRM() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm4 with 4cor reference materials.");
        // change selected index isotope
        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm4_RefMat.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", true, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getReferenceMaterialSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
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
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
     * @throws Exception
     */
    @Test
    public void tL_testingOutputForZ6266Perm3_7corr() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm3 with 7cor unknowns.");
        // change selected index isotope
        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm3_7Corr_Unknowns.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getUnknownSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }

    /**
     * @throws Exception
     */
    @Test
    public void tM_testingOutputForZ6266Perm3_4corrRM() throws Exception {
        System.out.println("Testing 836_1_2016_Nov_28_09_TaskPerm3 with 4cor reference materials.");
        // change selected index isotope
        squidProjectZ6266.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        squidProjectZ6266.getTask().setChanged(true);
        squidProjectZ6266.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        File reportTableFile = new File(reportsFolder + File.separator + "836_1_2016_Nov_28_09_TaskPerm3_RefMat.csv");
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", true, squidProjectZ6266.getTask());
        String[][] report = reportSettings.reportFractionsByNumberStyle(squidProjectZ6266.getTask().getReferenceMaterialSpots(), true, null);
        ReportSerializerToCSV.writeCSVReport(false, reportTableFile, report, false);
        File expectedReport = RESOURCE_EXTRACTOR.extractResourceAsFile(reportTableFile.getName());
        assertThat(reportTableFile).hasSameTextualContentAs(expectedReport);
    }
}