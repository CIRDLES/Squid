
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
package org.cirdles.squid.gui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.StageStyle;
import org.cirdles.squid.Squid;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.SpotTypes;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController;
import org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.PlotTypes;
import org.cirdles.squid.gui.expressions.ExpressionBuilderController;
import org.cirdles.squid.gui.parameters.ParametersLauncher;
import org.cirdles.squid.gui.squidReportTable.SquidReportTableLauncher;
import org.cirdles.squid.gui.utilities.BrowserControl;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.parameters.ParametersModelComparator;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import org.cirdles.squid.utilities.fileUtilities.FileNameFixer;
import org.cirdles.squid.utilities.fileUtilities.FileValidator;
import org.cirdles.squid.utilities.fileUtilities.ProjectFileUtilities;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import static org.cirdles.squid.constants.Squid3Constants.DEMO_SQUID_PROJECTS_FOLDER;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GENERAL;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GEOCHRON;

import static org.cirdles.squid.constants.Squid3Constants.getDEFAULT_RATIOS_LIST_FOR_10_SPECIES;
import static org.cirdles.squid.core.CalamariReportsEngine.CalamariReportFlavors.MEAN_RATIOS_PER_SPOT_UNKNOWNS;
import static org.cirdles.squid.gui.SquidUI.primaryStage;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.utilities.BrowserControl.urlEncode;
import org.cirdles.squid.prawn.PrawnFile;
import static org.cirdles.squid.utilities.fileUtilities.ZipUtility.extractZippedFile;
import static org.cirdles.squid.constants.Squid3Constants.LUDWIGLIBRARY_JAVADOC_FOLDER;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_TASK_LIBRARY_FOLDER;
import org.cirdles.squid.constants.Squid3Constants.TaskEditTypeEnum;
import static org.cirdles.squid.constants.Squid3Constants.TaskEditTypeEnum.EDIT_CURRENT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class SquidUIController implements Initializable {

    public static final SquidLabData squidLabData;

    static {
        CalamariFileUtilities.initSampleParametersModels();
        squidLabData = SquidLabData.getExistingSquidLabData();
        squidLabData.testVersionAndUpdate();
    }

    public static SquidProject squidProject;
    public static final SquidPersistentState squidPersistentState = SquidPersistentState.getExistingPersistentState();

    public static List<PrawnFile.Run> selectedRunsForRestore = new ArrayList<>();

    private static Map<String, String> taskLibraryDescriptions = new HashMap<>();

    @FXML
    private ImageView squidImageView;

    private static GridPane projectManagerUI;

    private static VBox sessionAuditUI;
    private static ScrollPane massesAuditUI;
    private static HBox spotManagerUI;

    private static GridPane taskManagerUI;
    private static GridPane taskEditorUI;
    public static Node taskFolderBrowserUI;

    private static VBox isotopesManagerUI;
    private static ScrollPane ratiosManagerUI;

    private static SplitPane expressionBuilderUI;

    private static AnchorPane squidReportSettingsUI;

    private static Pane reductionManagerUI;
    private static Pane reducedDataReportManagerUI;
    public static Node plotUI;
    public static VBox countCorrectionsUI;
    public static VBox commonLeadAssignmentUI;
    public static VBox weightedMeansUI;

    public static String projectFileName;

    @FXML
    private Label chinese;
    @FXML
    private Label japanese;
    @FXML
    private Label korean;
    @FXML
    private MenuItem choosePrawnFileMenuItem;
    @FXML
    private MenuItem auditRawDataMenuItem;
    @FXML
    private Label polish;
    @FXML
    private Label portuguese;
    @FXML
    private Label russian;
    @FXML
    private Label spanish;

    @FXML
    private SeparatorMenuItem dataSeparatorMenuItem;
    @FXML
    private Menu manageInterpretationsMenu;

    public static ParametersLauncher parametersLauncher;
    public static SquidReportTableLauncher squidReportTableLauncher;
    public static HighlightMainMenu menuHighlighter;

    public static int squidProjectOriginalHash;
    public boolean runSaveMenuDisableCheck;

    @FXML
    private Menu projectMenu;
    @FXML
    private Menu openRecentOPFileMenu;
    @FXML
    private MenuItem newSquidProjectFromOPFileMenuItem;
    @FXML
    private Menu manageExpressionsMenu;

    @FXML
    private MenuItem newSquidProjectMenuItem;
    @FXML
    private MenuItem openSquidProjectMenuItem;
    @FXML
    private MenuItem saveAsSquidProjectMenuItem;
    @FXML
    private MenuItem closeSquidProjectMenuItem;
    @FXML
    private Menu manageRatiosMenu;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Menu manageReportsMenu;
    @FXML
    private MenuItem projectManagerMenuItem;
    @FXML
    private MenuItem saveSquidProjectMenuItem;
    @FXML
    private MenuItem newSquidProjectByJoinMenuItem;
    @FXML
    private Menu managePrawnFileMenu;
    @FXML
    private MenuItem savePrawnFileCopyMenuItem;

    @FXML
    private Menu openRecentSquidProjectMenu;

    @FXML
    private Menu openRecentExpressionFileMenu;
    private Menu squidLabDataMenu;
    @FXML
    private Menu commonPbMenu;
    @FXML
    private MenuItem newSquidProjectFromZippedPrawnMenuItem;
    @FXML
    private MenuItem newSquidRatioProjectMenuItem;

    @FXML
    private MenuItem refMatConcordiaMenuItem;
    @FXML
    private MenuItem unknownConcordiaMenuItem;

    // Task Menus ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @FXML
    private Menu manageTasksMenu;
    @FXML
    private MenuItem browseTaskFolderTaskMenuItem;
    @FXML
    private MenuItem browseTaskFolderTaskMenuItem1;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // center Logo
        mainPane.heightProperty().addListener((ov, oldValue, newValue) -> {
            AnchorPane.setTopAnchor(squidImageView, newValue.doubleValue() / 2.0 - squidImageView.getFitHeight() / 2.0);
        });
        mainPane.widthProperty().addListener((ov, oldValue, newValue) -> {
            AnchorPane.setLeftAnchor(squidImageView, newValue.doubleValue() / 2.0 - squidImageView.getFitWidth() / 2.0);
        });

        initSaveMenuItemDisabling();

        menuHighlighter = new HighlightMainMenu();

        PlotsController.plotTypeSelected = PlotTypes.CONCORDIA;

        managePrawnFileMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageRatiosMenu.setDisable(true);
        manageExpressionsMenu.setDisable(true);
        commonPbMenu.setDisable(true);
        manageReportsMenu.setDisable(true);
        manageInterpretationsMenu.setDisable(true);

        // Squid project menu items
        newSquidProjectMenuItem.setDisable(false);
        newSquidProjectFromZippedPrawnMenuItem.setDisable(false);
        newSquidRatioProjectMenuItem.setDisable(false);
        newSquidProjectByJoinMenuItem.setDisable(false);
        openSquidProjectMenuItem.setDisable(false);
        buildProjectMenuMRU();
        buildOPFileMenuMRU();
        saveSquidProjectMenuItem.setDisable(true);
        saveAsSquidProjectMenuItem.setDisable(true);
        projectManagerMenuItem.setDisable(true);
        closeSquidProjectMenuItem.setDisable(true);

        // Prawn File Menu Items
        savePrawnFileCopyMenuItem.setDisable(false);

        // Expression menu
        buildExpressionMenuMRU();

        CalamariFileUtilities.initExamplePrawnFiles();
        CalamariFileUtilities.initDemoSquidProjectFiles();
        CalamariFileUtilities.loadShrimpPrawnFileSchema();
        CalamariFileUtilities.loadJavadoc();
        CalamariFileUtilities.initXSLTML();
        CalamariFileUtilities.initSquidTaskLibraryFiles();

        parametersLauncher = new ParametersLauncher(primaryStage);
        squidReportTableLauncher = new SquidReportTableLauncher(primaryStage);
    }

    private void initSaveMenuItemDisabling() {
        projectMenu.setOnShown(value -> {
            Thread thread = new Thread(() -> {
                if (squidProject != null && runSaveMenuDisableCheck) {
                    Runnable saveSquidProjectMenuItemAlterRunnable = null;
                    if (squidProjectOriginalHash == squidProject.hashCode()) {
                        if (!saveSquidProjectMenuItem.isDisable()) {
                            saveSquidProjectMenuItemAlterRunnable = () -> {
                                saveSquidProjectMenuItem.setDisable(true);
                            };
                        }
                    } else if (saveSquidProjectMenuItem.isDisable()) {
                        saveSquidProjectMenuItemAlterRunnable = () -> {
                            saveSquidProjectMenuItem.setDisable(false);
                        };
                    }
                    if (saveSquidProjectMenuItemAlterRunnable != null) {
                        Platform.runLater(saveSquidProjectMenuItemAlterRunnable);
                    }
                }
            });
            thread.start();
        });
    }

    private void buildProjectMenuMRU() {
        openRecentSquidProjectMenu.setDisable(false);

        openRecentSquidProjectMenu.getItems().clear();
        List<String> mruProjectList = squidPersistentState.getMRUProjectList();
        for (String aProjectFileName : mruProjectList) {
            MenuItem menuItem = new MenuItem(aProjectFileName);
            menuItem.setOnAction((ActionEvent t) -> {
                try {
                    openProject(menuItem.getText());
                } catch (IOException iOException) {
                    squidPersistentState.removeProjectFileNameFromMRU(menuItem.getText());
                    squidPersistentState.cleanProjectListMRU();
                    openRecentSquidProjectMenu.getItems().remove(menuItem);
                }
            });
            openRecentSquidProjectMenu.getItems().add(menuItem);
        }
    }

    private void buildOPFileMenuMRU() {
        openRecentOPFileMenu.setDisable(false);

        openRecentOPFileMenu.getItems().clear();
        List<String> mruOPFileList = squidPersistentState.getMRUOPFileList();
        for (String opFileName : mruOPFileList) {
            MenuItem menuItem = new MenuItem(opFileName);
            menuItem.setOnAction((ActionEvent t) -> {
                try {
                    openOPFile(menuItem.getText());
                } catch (IOException iOException) {
                    squidPersistentState.removeOPFileNameFromMRU(menuItem.getText());
                    squidPersistentState.cleanOPFileListMRU();
                    openRecentOPFileMenu.getItems().remove(menuItem);
                }
            });
            openRecentOPFileMenu.getItems().add(menuItem);
        }
    }

    private void buildExpressionMenuMRU() {

        openRecentExpressionFileMenu.getItems().clear();
        List<String> mruExpressionList = squidPersistentState.getMRUExpressionList();
        for (String expressionFileName : mruExpressionList) {
            MenuItem menuItem = new MenuItem(expressionFileName);
            menuItem.setOnAction((ActionEvent t) -> {

                if (!loadExpressionFromXMLFile(new File(menuItem.getText()))) {
                    squidPersistentState.removeExpressionFileNameFromMRU(menuItem.getText());
                    squidPersistentState.cleanExpressionListMRU();
                    openRecentExpressionFileMenu.getItems().remove(menuItem);
                }
            });
            openRecentExpressionFileMenu.getItems().add(menuItem);
        }
    }

    private void launchProjectManager() {

        try {
            removeAllManagers();

            projectManagerUI = FXMLLoader.load(getClass().getResource("ProjectManager.fxml"));
            projectManagerUI.setId("ProjectManager");

            AnchorPane.setLeftAnchor(projectManagerUI, 0.0);
            AnchorPane.setRightAnchor(projectManagerUI, 0.0);
            AnchorPane.setTopAnchor(projectManagerUI, 0.0);
            AnchorPane.setBottomAnchor(projectManagerUI, 0.0);

            mainPane.getChildren().add(projectManagerUI);
            projectManagerUI.setVisible(true);

            saveSquidProjectMenuItem.setDisable(squidProject.getTask().getNominalMasses().isEmpty());
            saveAsSquidProjectMenuItem.setDisable(false);
            closeSquidProjectMenuItem.setDisable(false);
            projectManagerMenuItem.setDisable(false);

            managePrawnFileMenu.setDisable(false);
            manageTasksMenu.setDisable(false);
            manageRatiosMenu.setDisable(squidProject.getTask().getNominalMasses().isEmpty());
            manageExpressionsMenu.setDisable(squidProject.getTask().getNominalMasses().isEmpty());
            commonPbMenu.setDisable(!squidProject.isTypeGeochron() || squidProject.getTask().getNominalMasses().isEmpty());
            manageReportsMenu.setDisable(squidProject.getTask().getNominalMasses().isEmpty());
            manageInterpretationsMenu.setDisable(!squidProject.isTypeGeochron() || squidProject.getTask().getNominalMasses().isEmpty());

            //Interpretations Menu
            refMatConcordiaMenuItem.setVisible(squidProject.isTypeGeochron());
            unknownConcordiaMenuItem.setVisible(squidProject.isTypeGeochron());

            menuHighlighter.highlight(projectMenu);
        } catch (IOException | RuntimeException iOException) {
            //System.out.println("ProjectManager >>>>   " + iOException.getMessage());
        }

    }

    private void removeAllManagers() {
        for (Node manager : mainPane.getChildren()) {
            manager.setVisible(false);
        }

        // prevent stacking of project panes
        mainPane.getChildren().remove(projectManagerUI);

        mainPane.getChildren().remove(sessionAuditUI);
        mainPane.getChildren().remove(massesAuditUI);
        mainPane.getChildren().remove(spotManagerUI);

        mainPane.getChildren().remove(taskManagerUI);

        mainPane.getChildren().remove(isotopesManagerUI);
        mainPane.getChildren().remove(ratiosManagerUI);

        mainPane.getChildren().remove(expressionBuilderUI);
        mainPane.getChildren().remove(squidReportSettingsUI);

        mainPane.getChildren().remove(reductionManagerUI);
        mainPane.getChildren().remove(reducedDataReportManagerUI);
        mainPane.getChildren().remove(plotUI);
        mainPane.getChildren().remove(countCorrectionsUI);
        mainPane.getChildren().remove(commonLeadAssignmentUI);
        mainPane.getChildren().remove(weightedMeansUI);

        mainPane.getChildren().remove(taskEditorUI);
        mainPane.getChildren().remove(taskFolderBrowserUI);

        saveSquidProjectMenuItem.setDisable(true);
        saveAsSquidProjectMenuItem.setDisable(true);
        closeSquidProjectMenuItem.setDisable(true);
        projectManagerMenuItem.setDisable(true);

        manageExpressionsMenu.setDisable(true);
        commonPbMenu.setDisable(true);
        managePrawnFileMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageRatiosMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageReportsMenu.setDisable(true);
        manageInterpretationsMenu.setDisable(true);

        // logo
        mainPane.getChildren().get(0).setVisible(true);

    }

    /**
     *
     * @param projectType the value of projectType
     */
    private void prepareForNewProject(TaskTypeEnum projectType) {
        confirmSaveOnProjectClose();
        removeAllManagers();

        squidProject = new SquidProject(projectType);

        // this updates output folder for reports to current version
        CalamariFileUtilities.initCalamariReportsFolder(squidProject.getPrawnFileHandler(), new File(projectFileName).getParentFile());

        runSaveMenuDisableCheck = false;
    }

    @FXML
    private void newSquidProjectFromOPFileAction(ActionEvent actionEvent) {
        try {
            openOPFile(FileHandler.selectOPFile(primaryStageWindow));
        } catch (IOException iOException) {
            String message = iOException.getMessage();
            if (message == null) {
                message = iOException.getCause().getMessage();
            }

            SquidMessageDialog.showWarningDialog(
                    "Squid encountered an error while trying to open the selected file:\n\n"
                    + message,
                    primaryStageWindow);
        }
    }

    private void openOPFile(String path) throws IOException {
        openOPFile(new File(path));
    }

    private void openOPFile(File file) throws IOException {
        prepareForNewProject(GEOCHRON);
        if (file != null) {
            squidProject.setupPrawnOPFile(file);
            squidProject.autoDivideSamples();
            //Needs own MRU squidPersistentState.updatePrawnFileListMRU(prawnSourceFile);
            SquidUI.updateStageTitle("");
            launchProjectManager();
            saveSquidProjectMenuItem.setDisable(true);
            customizeDataMenu();
            squidPersistentState.updateOPFileListMRU(file);
            buildOPFileMenuMRU();
        } else {
            squidProject.getTask().setChanged(false);
            SquidProject.setProjectChanged(false);
        }
    }

    @FXML
    private void newSquidProjectFromZippedPrawnAction(ActionEvent event) {
        prepareForNewProject(GEOCHRON);

        try {
            File prawnZippedSourceFile = FileHandler.selectZippedPrawnXMLFile(primaryStageWindow);
            if (prawnZippedSourceFile != null) {

                Path prawnFilePathParent = prawnZippedSourceFile.toPath().getParent();
                Path prawnFilePath = extractZippedFile(prawnZippedSourceFile, prawnFilePathParent.toFile());

                File prawnSourceFileNew = prawnFilePath.toFile();

                squidProject.setupPrawnXMLFile(prawnSourceFileNew);
                squidProject.autoDivideSamples();
                squidPersistentState.updatePrawnFileListMRU(prawnSourceFileNew);
                SquidUI.updateStageTitle("");
                launchProjectManager();
                saveSquidProjectMenuItem.setDisable(true);
                customizeDataMenu();
            } else {
                squidProject.getTask().setChanged(false);
                SquidProject.setProjectChanged(false);
            }
        } catch (IOException | JAXBException | SAXException | SquidException anException) {
            String message = anException.getMessage();
            if (message == null) {
                message = anException.getCause().getMessage();
            }

            SquidMessageDialog.showWarningDialog(
                    "Squid encountered an error while trying to open the selected file:\n\n"
                    + message,
                    primaryStageWindow);
        }
    }

    @FXML
    private void newSquidProjectAction(ActionEvent event) {
        prepareForNewProject(GEOCHRON);

        try {
            File prawnSourceFile = FileHandler.selectPrawnXMLFile(primaryStageWindow);
            if (prawnSourceFile != null) {
                squidProject.setupPrawnXMLFile(prawnSourceFile);
                squidProject.autoDivideSamples();
                squidPersistentState.updatePrawnFileListMRU(prawnSourceFile);
                SquidUI.updateStageTitle("");
                launchProjectManager();
                saveSquidProjectMenuItem.setDisable(true);
                customizeDataMenu();
            } else {
                squidProject.getTask().setChanged(false);
                SquidProject.setProjectChanged(false);
            }

        } catch (IOException | JAXBException | SAXException | SquidException anException) {
            String message = anException.getMessage();
            if (message == null) {
                message = anException.getCause().getMessage();
            }

            SquidMessageDialog.showWarningDialog(
                    "Squid encountered an error while trying to open the selected file:\n\n"
                    + message,
                    primaryStageWindow);
        }
    }

    @FXML
    private void newSquidRatioProjectAction(ActionEvent event) {
        prepareForNewProject(GENERAL);

        try {
            File prawnSourceFile = FileHandler.selectPrawnXMLFile(primaryStageWindow);
            if (prawnSourceFile != null) {
                squidProject.setupPrawnXMLFile(prawnSourceFile);
                squidProject.autoDivideSamples();
                squidPersistentState.updatePrawnFileListMRU(prawnSourceFile);

                // auto generate task data for nominal masses
                squidProject.initializeTaskAndReduceData(true);

                SquidUI.updateStageTitle("");
                launchProjectManager();
                saveSquidProjectMenuItem.setDisable(true);
                customizeDataMenu();
            } else {
                squidProject.getTask().setChanged(false);
                SquidProject.setProjectChanged(false);
            }
        } catch (IOException | JAXBException | SAXException | SquidException anException) {
            String message = anException.getMessage();
            if (message == null) {
                message = anException.getCause().getMessage();
            }

            SquidMessageDialog.showWarningDialog(
                    "Squid encountered an error while trying to open the selected file:\n\n"
                    + message,
                    primaryStageWindow);
        }
    }

    @FXML
    private void newSquidProjectByJoinAction(ActionEvent event) {
        prepareForNewProject(GEOCHRON);

        SquidMessageDialog.showInfoDialog(
                "To join two Prawn XML files, be sure they are in the same folder, \n\tand then in the next dialog, choose both files."
                + "\n\nNotes: \n\t1) Joining will be done by comparing the timestamps of the first run in \n\t    each file to determine the order of join."
                + "\n\n\t2) The joined file will be written to disk and then read back in as a \n\t    check.  The name of the new file"
                + " will appear in the project manager's \n\t    text box for the Prawn XML file name.",
                primaryStageWindow);

        try {
            List<File> prawnXMLFilesNew = FileHandler.selectForJoinTwoPrawnXMLFiles(primaryStageWindow);
            if (prawnXMLFilesNew.size() == 2) {
                squidProject.setupPrawnXMLFileByJoin(prawnXMLFilesNew);
                launchProjectManager();
                saveSquidProjectMenuItem.setDisable(true);
                customizeDataMenu();
            } else {
                squidProject.getTask().setChanged(false);
                SquidProject.setProjectChanged(false);
            }
        } catch (IOException | JAXBException | SAXException | SquidException anException) {
            String message = anException.getMessage();
            if (message == null) {
                message = anException.getCause().getMessage();
            }

            SquidMessageDialog.showWarningDialog(
                    "Squid encountered an error while trying to open and join the selected files:\n\n"
                    + message,
                    primaryStageWindow);
        }
    }

    @FXML
    private void saveAsSquidProjectMenuItemAction(ActionEvent event) {
        if (squidProject != null) {
            try {
                File projectFile = FileHandler.saveProjectFile(squidProject, SquidUI.primaryStageWindow);
                if (projectFile != null) {
                    saveSquidProjectMenuItem.setDisable(false);
                    squidPersistentState.updateProjectListMRU(projectFile);
                    SquidUI.updateStageTitle(projectFile.getAbsolutePath());
                    buildProjectMenuMRU();
                    launchProjectManager();
                    runSaveMenuDisableCheck = true;
                    squidProjectOriginalHash = squidProject.hashCode();
                }

            } catch (IOException ex) {
                saveSquidProjectMenuItem.setDisable(false);
            }
        }
    }

    @FXML
    private void openSquidProjectMenuItemAction(ActionEvent event) {
        confirmSaveOnProjectClose();
        removeAllManagers();

        try {
            projectFileName = FileHandler.selectProjectFile(SquidUI.primaryStageWindow);
            openProject(projectFileName);
        } catch (IOException iOException) {
        }
    }

    private void openProject(String aProjectFileName) throws IOException {
        if (!"".equals(aProjectFileName)) {
            projectFileName = aProjectFileName;
            confirmSaveOnProjectClose();
            squidProject = (SquidProject) SquidSerializer.getSerializedObjectFromFile(projectFileName, true);

            if (squidProject != null) {
                synchronizeTaskLabDataAndSquidVersion();

                ((Task) squidProject.getTask()).buildExpressionDependencyGraphs();
                ((Task) squidProject.getTask()).updateSquidSpeciesModelsGeochronMode();

                squidPersistentState.updateProjectListMRU(new File(projectFileName));
                SquidUI.updateStageTitle(projectFileName);
                buildProjectMenuMRU();
                launchProjectManager();
                saveSquidProjectMenuItem.setDisable(false);

                customizeDataMenu();

                // this updates output folder for reports to current version
                CalamariFileUtilities.initCalamariReportsFolder(squidProject.getPrawnFileHandler(), new File(projectFileName).getParentFile());

                squidProjectOriginalHash = squidProject.hashCode();
                runSaveMenuDisableCheck = true;
            } else {
                saveSquidProjectMenuItem.setDisable(true);
                SquidUI.updateStageTitle("");
                throw new IOException();
            }
        }
    }

    private void customizeDataMenu() {
        boolean opSourceFile = squidProject.getPrawnSourceFileName().toUpperCase(Locale.ENGLISH).endsWith(".OP");

        auditRawDataMenuItem.setVisible(!opSourceFile);
        savePrawnFileCopyMenuItem.setVisible(!opSourceFile);
        choosePrawnFileMenuItem.setVisible(!opSourceFile);
        dataSeparatorMenuItem.setVisible(!opSourceFile);
    }

    @FXML
    private void closeSquidProjectMenuItemClose(ActionEvent event) {
        confirmSaveOnProjectClose();
        removeAllManagers();
        SquidUI.updateStageTitle("");
        menuHighlighter.deHighlight();
    }

    @FXML
    private void saveSquidProjectMenuItemAction(ActionEvent event) {
        if (squidProject != null) {
            try {
                ProjectFileUtilities.serializeSquidProject(squidProject, squidPersistentState.getMRUProjectFile().getCanonicalPath());
                squidProjectOriginalHash = squidProject.hashCode();
            } catch (IOException iOException) {
            }
        }
    }

    private void confirmSaveOnProjectClose() {
        if (SquidProject.isProjectChanged()) {

            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Do you want to save Squid Project changes?",
                    ButtonType.YES,
                    ButtonType.NO
            );
            alert.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
            alert.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
            alert.showAndWait().ifPresent((t) -> {
                if (t.equals(ButtonType.YES)) {
                    try {
                        File projectFile = FileHandler.saveProjectFile(squidProject, SquidUI.primaryStageWindow);
                    } catch (IOException iOException) {
                        SquidMessageDialog.showWarningDialog("Squid3 cannot access the target file.\n",
                                null);
                    }
                }
            });
            SquidProject.setProjectChanged(false);
            launchProjectManager();
            squidProjectOriginalHash = squidProject.hashCode();
        }
    }

    @FXML
    private void quitAction(ActionEvent event) {
        SquidPersistentState.getExistingPersistentState().updateSquidPersistentState();
        confirmSaveOnProjectClose();
        try {
            ExpressionBuilderController.EXPRESSION_NOTES_STAGE.close();
        } catch (Exception e) {
        }
        Platform.exit();
    }

    @FXML
    private void onlineHelpAction(ActionEvent event) {
        BrowserControl.showURI("http://cirdles.org/projects/squid/#Development");
    }

    @FXML
    private void aboutSquidAction(ActionEvent event) {
        SquidUI.squidAboutWindow.loadAboutWindow();
    }

    @FXML
    private void contributeIssueOnGitHubAction(ActionEvent event) {
        String version = "Squid3 Version: " + Squid.VERSION;
        String javaVersion = "Java Version: " + System.getProperties().getProperty("java.version");
        String operatingSystem = "OS: " + System.getProperties().getProperty("os.name") + " " + System.getProperties().getProperty("os.version");

        StringBuilder issueBody = new StringBuilder();
        issueBody.append(urlEncode(version + "\n"));
        issueBody.append(urlEncode(javaVersion + "\n"));
        issueBody.append(urlEncode(operatingSystem + "\n"));
        issueBody.append(urlEncode("\n\nIssue details:\n"));

        BrowserControl.showURI("https://github.com/CIRDLES/Squid/issues/new?body=" + issueBody.toString());
    }

    private void launchSessionAudit() {
        try {
            sessionAuditUI = FXMLLoader.load(getClass().getResource("SessionAudit.fxml"));
            sessionAuditUI.setId("SessionAudit");

            AnchorPane.setLeftAnchor(sessionAuditUI, 0.0);
            AnchorPane.setRightAnchor(sessionAuditUI, 0.0);
            AnchorPane.setTopAnchor(sessionAuditUI, 0.0);
            AnchorPane.setBottomAnchor(sessionAuditUI, 0.0);

            mainPane.getChildren().add(sessionAuditUI);
            sessionAuditUI.setVisible(false);

            menuHighlighter.highlight(managePrawnFileMenu);
        } catch (IOException | RuntimeException iOException) {
            //System.out.println("SessionAudit >>>>   " + iOException.getMessage());
        }
    }

    private void launchMassesAudit() {
        try {
            massesAuditUI = FXMLLoader.load(getClass().getResource("MassesAudit.fxml"));
            massesAuditUI.setId("MassesAudit");

            AnchorPane.setLeftAnchor(massesAuditUI, 0.0);
            AnchorPane.setRightAnchor(massesAuditUI, 0.0);
            AnchorPane.setTopAnchor(massesAuditUI, 0.0);
            AnchorPane.setBottomAnchor(massesAuditUI, 0.0);

            mainPane.getChildren().add(massesAuditUI);
            massesAuditUI.setVisible(false);

            menuHighlighter.highlight(managePrawnFileMenu);
        } catch (IOException | RuntimeException iOException) {
            //System.out.println("MassesAudit >>>>   " + iOException.getMessage());
        }
    }

    private void launchSpotManager() {
        try {
            spotManagerUI = FXMLLoader.load(getClass().getResource("SpotManager.fxml"));
            spotManagerUI.setId("SpotManager");

            AnchorPane.setLeftAnchor(spotManagerUI, 0.0);
            AnchorPane.setRightAnchor(spotManagerUI, 0.0);
            AnchorPane.setTopAnchor(spotManagerUI, 0.0);
            AnchorPane.setBottomAnchor(spotManagerUI, 0.0);

            mainPane.getChildren().add(spotManagerUI);
            spotManagerUI.setVisible(false);

            menuHighlighter.highlight(managePrawnFileMenu);
        } catch (IOException | RuntimeException iOException) {
            //System.out.println("SpotManager >>>>   " + iOException.getMessage());
        }
    }

    private void ManageRefMatWarning() {
        // present warning if needed
        if (squidProject.isTypeGeochron() && squidProject.getTask().getReferenceMaterialSpots().isEmpty()) {
            SquidMessageDialog.showInfoDialog("Please be sure to Manage Reference Materials and "
                    + "Sample names using the Data menu.\n",
                    primaryStageWindow);
        }
    }

    public void launchTaskViewer() {
        ManageRefMatWarning();

        mainPane.getChildren().remove(taskManagerUI);
        try {
            taskManagerUI = FXMLLoader.load(getClass().getResource("TaskManager.fxml"));
            taskManagerUI.setId("TaskManager");

            AnchorPane.setLeftAnchor(taskManagerUI, 0.0);
            AnchorPane.setRightAnchor(taskManagerUI, 0.0);
            AnchorPane.setTopAnchor(taskManagerUI, 0.0);
            AnchorPane.setBottomAnchor(taskManagerUI, 0.0);

            mainPane.getChildren().add(taskManagerUI);
            showUI(taskManagerUI);
            manageRatiosMenu.setDisable(squidProject.getTask().getNominalMasses().isEmpty());
            manageExpressionsMenu.setDisable(squidProject.getTask().getNominalMasses().isEmpty());
            commonPbMenu.setDisable(!squidProject.isTypeGeochron() || squidProject.getTask().getNominalMasses().isEmpty());
            manageReportsMenu.setDisable(squidProject.getTask().getNominalMasses().isEmpty());
            manageInterpretationsMenu.setDisable(!squidProject.isTypeGeochron() || squidProject.getTask().getNominalMasses().isEmpty());

            menuHighlighter.highlight(manageTasksMenu);
        } catch (IOException | RuntimeException iOException) {
            //System.out.println("TaskManager >>>>   " + iOException.getMessage());
        }
    }

    /**
     *
     * @param editType the value of editType
     */
    public void launchTaskEditor(TaskEditTypeEnum editType) {
        mainPane.getChildren().remove(taskEditorUI);
        try {
            TaskEditorController.editType = editType;
            taskEditorUI = FXMLLoader.load(getClass().getResource("TaskEditor.fxml"));
            taskEditorUI.setId("TaskEditor");

            AnchorPane.setLeftAnchor(taskEditorUI, 0.0);
            AnchorPane.setRightAnchor(taskEditorUI, 0.0);
            AnchorPane.setTopAnchor(taskEditorUI, 0.0);
            AnchorPane.setBottomAnchor(taskEditorUI, 0.0);

            mainPane.getChildren().add(taskEditorUI);
            showUI(taskEditorUI);

            menuHighlighter.highlight(manageTasksMenu);
        } catch (IOException | RuntimeException iOException) {
            //System.out.println("TaskEditor >>>>   " + iOException.getMessage());
        }
    }

    private void launchTaskFolderBrowser() {
        mainPane.getChildren().remove(taskFolderBrowserUI);
        try {
            taskFolderBrowserUI = FXMLLoader.load(getClass().getResource("TaskFolderBrowser.fxml"));
            taskFolderBrowserUI.setId("TaskFolderBrowser");

            AnchorPane.setLeftAnchor(taskFolderBrowserUI, 0.0);
            AnchorPane.setRightAnchor(taskFolderBrowserUI, 0.0);
            AnchorPane.setTopAnchor(taskFolderBrowserUI, 0.0);
            AnchorPane.setBottomAnchor(taskFolderBrowserUI, 0.0);

            mainPane.getChildren().add(taskFolderBrowserUI);
            showUI(taskFolderBrowserUI);

            menuHighlighter.highlight(manageTasksMenu);
        } catch (IOException | RuntimeException iOException) {
            //System.out.println("taskFolderBrowserUI >>>>   " + iOException.getMessage());
        }
    }

    private void launchIsotopesManager() {
        try {
            isotopesManagerUI = FXMLLoader.load(getClass().getResource("IsotopesManager.fxml"));
            isotopesManagerUI.setId("IsotopesManager");

            AnchorPane.setLeftAnchor(isotopesManagerUI, 0.0);
            AnchorPane.setRightAnchor(isotopesManagerUI, 0.0);
            AnchorPane.setTopAnchor(isotopesManagerUI, 0.0);
            AnchorPane.setBottomAnchor(isotopesManagerUI, 0.0);

            mainPane.getChildren().add(isotopesManagerUI);
            isotopesManagerUI.setVisible(false);

            menuHighlighter.highlight(manageRatiosMenu);
        } catch (IOException | RuntimeException iOException) {
            //System.out.println("IsotopesManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchRatiosManager() {
        try {
            mainPane.getChildren().remove(ratiosManagerUI);
            // critical for populating table
            squidProject.getTask().buildSquidSpeciesModelList();

            ratiosManagerUI = FXMLLoader.load(getClass().getResource("RatiosManager.fxml"));
            ratiosManagerUI.setId("RatiosManager");

            AnchorPane.setLeftAnchor(ratiosManagerUI, 0.0);
            AnchorPane.setRightAnchor(ratiosManagerUI, 0.0);
            AnchorPane.setTopAnchor(ratiosManagerUI, 0.0);
            AnchorPane.setBottomAnchor(ratiosManagerUI, 0.0);

            mainPane.getChildren().add(ratiosManagerUI);
            ratiosManagerUI.setVisible(false);

            showUI(ratiosManagerUI);

            menuHighlighter.highlight(manageRatiosMenu);
        } catch (IOException | RuntimeException iOException) {
            //System.out.println("RatioManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchExpressionBuilder() {

        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
        squidProject.getTask().updateAllExpressions(true);

        // present warning if needed
        if (squidProject.isTypeGeochron() && !squidProject.projectIsHealthyGeochronMode()) {
            SquidMessageDialog.showInfoDialog("Please be sure to Manage Isotopes (press red button) to initialize expressions\n"
                    + " and confirm the ratios in ParentElement_ConcenConst = \n"
                    + "\t\t" + ((Expression) squidProject.getTask().getExpressionByName(PARENT_ELEMENT_CONC_CONST)).getExcelExpressionString(),
                    primaryStageWindow);
        }

        try {
            expressionBuilderUI = FXMLLoader.load(getClass().getResource("expressions/ExpressionBuilder.fxml"));
            expressionBuilderUI.setId("ExpressionBuilder");

            AnchorPane.setLeftAnchor(expressionBuilderUI, 0.0);
            AnchorPane.setRightAnchor(expressionBuilderUI, 0.0);
            AnchorPane.setTopAnchor(expressionBuilderUI, 0.0);
            AnchorPane.setBottomAnchor(expressionBuilderUI, 0.0);

            mainPane.getChildren().add(expressionBuilderUI);
            expressionBuilderUI.setVisible(false);

            menuHighlighter.highlight(manageExpressionsMenu);
        } catch (IOException ex) {
            Logger.getLogger(SquidUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void launchReducedDataReportManager() {
        try {
            reducedDataReportManagerUI = FXMLLoader.load(getClass().getResource("dataReductionReports/ReducedDataReportManager.fxml"));
            reducedDataReportManagerUI.setId("reducedDataReportManagerUI");

            AnchorPane.setLeftAnchor(reducedDataReportManagerUI, 0.0);
            AnchorPane.setRightAnchor(reducedDataReportManagerUI, 0.0);
            AnchorPane.setTopAnchor(reducedDataReportManagerUI, 0.0);
            AnchorPane.setBottomAnchor(reducedDataReportManagerUI, 0.0);

            mainPane.getChildren().add(reducedDataReportManagerUI);
            reducedDataReportManagerUI.setVisible(false);

            showUI(reducedDataReportManagerUI);

            menuHighlighter.highlight(manageReportsMenu);
        } catch (IOException | RuntimeException iOException) {
            //System.out.println("reducedDataReportManagerUI >>>>   " + iOException.getMessage());
        }
    }

    @FXML
    private void projectManagerMenuItemAction(ActionEvent event) {
        launchProjectManager();
    }

    private void showUI(Node myManager) {
        SquidPersistentState.getExistingPersistentState().updateSquidPersistentState();

        for (Node manager : mainPane.getChildren()) {
            manager.setVisible(false);
        }

        // logo
        mainPane.getChildren().get(0).setVisible(true);

        myManager.setVisible(true);
    }

    @FXML
    private void auditSessionMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(sessionAuditUI);
        launchSessionAudit();
        showUI(sessionAuditUI);
    }

    @FXML
    private void manageSpotsMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(spotManagerUI);
        launchSpotManager();
        showUI(spotManagerUI);
    }

    @FXML
    private void auditMassesMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(massesAuditUI);
        squidProject.getTask().buildSquidSpeciesModelList();
        launchMassesAudit();
        showUI(massesAuditUI);
    }

    @FXML
    private void specifyIsotopesMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(isotopesManagerUI);
        squidProject.getTask().buildSquidSpeciesModelList();
        launchIsotopesManager();
        showUI(isotopesManagerUI);
    }

    @FXML
    private void selectRatiosMenuItemAction(ActionEvent event) {
        launchRatiosManager();
    }

    @FXML
    private void savePrawnFileCopyMenuItemAction(ActionEvent event) {
        try {
            File prawnXMLFileNew = FileHandler.savePrawnXMLFile(squidProject, primaryStageWindow);
            if (prawnXMLFileNew != null) {
                squidProject.setupPrawnXMLFile(prawnXMLFileNew);
                launchProjectManager();
            }
        } catch (IOException | JAXBException | SAXException | SquidException iOException) {
        }
    }

    @FXML
    private void loadExpressionFromXMLFileMenuItemAction(ActionEvent event) {
        try {
            File expressionFileXML = FileHandler.selectExpressionXMLFile(primaryStageWindow);
            loadExpressionFromXMLFile(expressionFileXML);

        } catch (IOException | JAXBException | SAXException iOException) {
        }
    }

    private boolean loadExpressionFromXMLFile(File expressionFileXML) {
        boolean retVal = false;
        if (expressionFileXML != null) {
            Expression exp = (Expression) (new Expression()).readXMLObject(expressionFileXML.getAbsolutePath(), false);
            if (exp != null) {
                squidPersistentState.updateExpressionListMRU(expressionFileXML);
                retVal = true;
                if (squidProject.getTask().expressionExists(exp)) {
                    ButtonType replace = new ButtonType("Replace");
                    ButtonType rename = new ButtonType("Rename");
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                            "An expression already exists with this name. What do you want to do?",
                            replace,
                            rename,
                            ButtonType.CANCEL
                    );
                    alert.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
                    alert.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
                    alert.showAndWait().ifPresent((t) -> {
                        if (t.equals(replace)) {
                            addExpressionToTask(exp);
                        } else if (t.equals(rename)) {
                            TextInputDialog dialog = new TextInputDialog(exp.getName());
                            dialog.setTitle("Rename");
                            dialog.setHeaderText("Rename " + exp.getName());
                            dialog.setContentText("Enter the new name:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    okBtn.setDisable(squidProject.getTask().expressionExists(exp) || newValue.isEmpty());
                                });
                            }
                            dialog.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
                            dialog.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                exp.setName(result.get());
                                addExpressionToTask(exp);
                            }
                        }
                    });
                } else {
                    addExpressionToTask(exp);
                }
            }
        }
        return retVal;
    }

    private void addExpressionToTask(Expression exp) {
        squidProject.getTask().removeExpression(exp, true);
        squidProject.getTask().addExpression(exp, true);

        ExpressionBuilderController.expressionToHighlightOnInit = exp;
        buildExpressionMenuMRU();
        launchExpressionBuilder();
        showUI(expressionBuilderUI);
    }

    @FXML
    private void defaultEmptyRatioSetAction(ActionEvent event) {
        squidProject.getTask().updateRatioNames(new String[]{});
        launchRatiosManager();
    }

    @FXML
    private void default10SpeciesRatioSetAction(ActionEvent event) {
        squidProject.getTask().updateRatioNames(getDEFAULT_RATIOS_LIST_FOR_10_SPECIES());
        launchRatiosManager();
    }

    @FXML
    private void showWithinSpotRatiosReferenceMatMenutItemAction(ActionEvent event) {
        SquidUI.calamariReportFlavor = CalamariReportsEngine.CalamariReportFlavors.WITHIN_SPOT_RATIOS_REFERENCEMAT;
        launchReducedDataReportManager();
    }

    @FXML
    private void showWithinSpotRatiosUnknownsMenutItemAction(ActionEvent event) {
        SquidUI.calamariReportFlavor = CalamariReportsEngine.CalamariReportFlavors.WITHIN_SPOT_RATIOS_UNKNOWNS;
        launchReducedDataReportManager();
    }

    @FXML
    private void showMeanRatiosReferenceMatMenutItemAction(ActionEvent event) {
        SquidUI.calamariReportFlavor = CalamariReportsEngine.CalamariReportFlavors.MEAN_RATIOS_PER_SPOT_REFERENCEMAT;
        launchReducedDataReportManager();
    }

    @FXML
    private void showMeanRatiosUnknownMenutItemAction(ActionEvent event) {
        SquidUI.calamariReportFlavor = MEAN_RATIOS_PER_SPOT_UNKNOWNS;
        launchReducedDataReportManager();
    }

    @FXML
    private void reportsMenuSelectedAction(Event event) {
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
    }

    @FXML
    private void visitCIRDLESAction(ActionEvent event) {
        BrowserControl.showURI("https://CIRDLES.org");
    }

    @FXML
    private void showSquid3GithubRepo(ActionEvent event) {
        BrowserControl.showURI("https://github.com/CIRDLES/Squid");
    }

    @FXML
    private void showSquid3DevNotes(ActionEvent event) {
        BrowserControl.showURI("https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Intro");
    }

    @FXML
    private void showTopsoilGithubRepo(ActionEvent event) {
        BrowserControl.showURI("https://github.com/CIRDLES/Topsoil");
    }

    private void launchInterpretations() {
        try {
            plotUI = FXMLLoader.load(getClass().getResource("dateInterpretations/plots/plotControllers/Plots.fxml"));
            plotUI.setId("PlotUI");

            AnchorPane.setLeftAnchor(plotUI, 0.0);
            AnchorPane.setRightAnchor(plotUI, 0.0);
            AnchorPane.setTopAnchor(plotUI, 0.0);
            AnchorPane.setBottomAnchor(plotUI, 0.0);

            mainPane.getChildren().add(plotUI);
            plotUI.setVisible(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("PlotUI >>>>   " + iOException.getMessage());
        }
    }

    @FXML
    private void expressionBuilderMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(expressionBuilderUI);
        launchExpressionBuilder();
        showUI(expressionBuilderUI);
    }

    private void ludwigLibraryJavaDocAction(ActionEvent event) {
        BrowserControl.showURI(LUDWIGLIBRARY_JAVADOC_FOLDER + File.separator + "index.html");
    }

    @FXML
    private void videoTutorialsMenuItemAction(ActionEvent event) {
        BrowserControl.showURI("https://www.youtube.com/channel/UCC6iRpem2LkdozahaIphXTg/playlists");
    }

    @FXML
    private void producePerScanReportsAction(ActionEvent event) {
        if (squidProject.getTask().getRatioNames().isEmpty()) {
            SquidMessageDialog.showInfoDialog("Please be sure to Manage Isotopes and Ratios to initialize expressions.\n",
                    primaryStageWindow);
        } else {
            if (squidProject.hasReportsFolder()) {
                SquidMessageDialog.showSavedAsDialog(squidProject.getTask().producePerScanReportsToFiles(), primaryStageWindow);
            } else {
                showReportsWarning();
            }
        }
    }

    @FXML
    private void referenceMaterialsReportTableAction(ActionEvent event) throws IOException {
        if (squidProject.hasReportsFolder()) {
            File reportTableFile = squidProject.produceReferenceMaterialPerSquid25CSV(true);
            if (reportTableFile != null) {
                SquidMessageDialog.showSavedAsDialog(reportTableFile, primaryStageWindow);
            } else {
                showManageRefMatWarning();
            }
        } else {
            showReportsWarning();
        }
    }

    @FXML
    private void unknownsReportTableAction(ActionEvent event) throws IOException {
        if (squidProject.hasReportsFolder()) {
            File reportTableFile = squidProject.produceUnknownsPerSquid25CSV(true);
            if (reportTableFile != null) {
                SquidMessageDialog.showSavedAsDialog(reportTableFile, primaryStageWindow);
            } else {
                SquidMessageDialog.showInfoDialog(
                        "There are no Unknowns chosen.\n\n",
                        primaryStageWindow);
            }
        } else {
            showReportsWarning();
        }
    }

    @FXML
    private void unknownsBySampleReportTableAction(ActionEvent event) throws IOException {
        if (squidProject.hasReportsFolder()) {
            File reportTableFile = squidProject.produceUnknownsBySampleForETReduxCSV(true);
            if (reportTableFile != null) {
                SquidMessageDialog.showSavedAsDialog(reportTableFile, primaryStageWindow);
            } else {
                SquidMessageDialog.showInfoDialog(
                        "There are no Unknowns chosen.\n\n",
                        primaryStageWindow);
            }
        } else {
            showReportsWarning();
        }
    }

    @FXML
    private void referenceMaterialSummaryReportOnAction(ActionEvent actionEvent) throws IOException {
        if (squidProject.hasReportsFolder()) {
            File summaryFile
                    = squidProject.getPrawnFileHandler().getReportsEngine().writeSummaryReportsForReferenceMaterials();
            SquidMessageDialog.showSavedAsDialog(summaryFile, primaryStageWindow);
        } else {
            showReportsWarning();
        }
    }

    @FXML
    private void unknownsSummaryReportOnAction(ActionEvent actionEvent) throws IOException {
        if (squidProject.hasReportsFolder()) {
            File summaryFile = squidProject.getPrawnFileHandler().getReportsEngine().writeSummaryReportsForUnknowns();
            SquidMessageDialog.showSavedAsDialog(summaryFile, primaryStageWindow);
        } else {
            showReportsWarning();
        }
    }

    @FXML
    private void produceTaskSummaryReportAction(ActionEvent actionEvent) throws IOException {
        if (squidProject.hasReportsFolder()) {
            File taskAuditFile = squidProject.getPrawnFileHandler().getReportsEngine().writeTaskAudit();
            SquidMessageDialog.showSavedAsDialog(taskAuditFile, primaryStageWindow);
        } else {
            showReportsWarning();
        }
    }

    @FXML
    public void produceProjectAuditReportAction(ActionEvent actionEvent) throws IOException {
        if (squidProject.hasReportsFolder()) {
            File projectAuditFile = squidProject.getPrawnFileHandler().getReportsEngine().writeProjectAudit();
            SquidMessageDialog.showSavedAsDialog(projectAuditFile, primaryStageWindow);
        } else {
            showReportsWarning();
        }
    }

    @FXML
    public void generateAllReportsAction(ActionEvent actionEvent) throws IOException {
        if (squidProject.getTask().getRatioNames().isEmpty()) {
            SquidMessageDialog.showInfoDialog("Please be sure to Manage Isotopes and Ratios to initialize expressions.\n",
                    primaryStageWindow);
        } else {
            if (squidProject.hasReportsFolder()) {
                File projectAuditFile = squidProject.getPrawnFileHandler().getReportsEngine().writeProjectAudit();
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
                SquidMessageDialog.showSavedAsDialog(projectAuditFile.getParentFile(), primaryStageWindow);
            } else {
                showReportsWarning();
            }
        }
    }

    private void showReportsWarning() {
        SquidMessageDialog.showWarningDialog("The Squid Project must be saved before reports can be written out.", primaryStageWindow);
    }

    private void showManageIsotopesWarning() {
        SquidMessageDialog.showInfoDialog(
                "Please use the 'Isotopes & Ratios' menu to manage isotopes so reduction can proceed.\n\n",
                primaryStageWindow);
    }

    private void showManageRefMatWarning() {
        SquidMessageDialog.showInfoDialog(
                "There are no Reference Material spots chosen.\n\n",
                primaryStageWindow);
    }

    private void showManageRefMatModelWarning() {
        SquidMessageDialog.showInfoDialog(
                "There is no Reference Material Model chosen.\n\n",
                primaryStageWindow);
    }

    public void launchCountCorrections() {
        mainPane.getChildren().remove(countCorrectionsUI);
        squidProject.getTask().buildSquidSpeciesModelList();
        // if ratios list not populated or no ref mat chosen show warning
        if (squidProject.getTask().getSquidRatiosModelList().isEmpty()) {
            showManageIsotopesWarning();
        } else if (squidProject.getTask().getReferenceMaterialSpots().isEmpty()) {
            showManageRefMatWarning();
        } else if (!((ReferenceMaterialModel) squidProject.getTask().getReferenceMaterialModel()).hasAtLeastOneNonZeroApparentDate()) {
            showManageRefMatModelWarning();
        } else {
            try {
                countCorrectionsUI = FXMLLoader.load(getClass().getResource("dateInterpretations/countCorrections/CountCorrections.fxml"));
                countCorrectionsUI.setId("Count Corrections");

                AnchorPane.setLeftAnchor(countCorrectionsUI, 0.0);
                AnchorPane.setRightAnchor(countCorrectionsUI, 0.0);
                AnchorPane.setTopAnchor(countCorrectionsUI, 0.0);
                AnchorPane.setBottomAnchor(countCorrectionsUI, 0.0);

                mainPane.getChildren().add(countCorrectionsUI);
                countCorrectionsUI.setVisible(false);
            } catch (IOException | RuntimeException iOException) {
                System.out.println("countCorrectionsUI >>>>   " + iOException.getMessage());
            }
            menuHighlighter.highlight(commonPbMenu);

            showUI(countCorrectionsUI);
        }
    }

    public void launchCommonLeadAssignment() {
        mainPane.getChildren().remove(commonLeadAssignmentUI);
        squidProject.getTask().buildSquidSpeciesModelList();
        // if ratios list not populated or no ref mat chosen show warning
        if (squidProject.getTask().getSquidRatiosModelList().isEmpty()) {
            showManageIsotopesWarning();
        } else if (squidProject.getTask().getReferenceMaterialSpots().isEmpty()) {
            showManageRefMatWarning();
        } else if (!((ReferenceMaterialModel) squidProject.getTask().getReferenceMaterialModel()).hasAtLeastOneNonZeroApparentDate()) {
            showManageRefMatModelWarning();
        } else {
            try {
                commonLeadAssignmentUI = FXMLLoader.load(getClass().getResource("dateInterpretations/commonLeadAssignment/CommonLeadAssignment.fxml"));
                commonLeadAssignmentUI.setId("Common Lead Assignment");

                AnchorPane.setLeftAnchor(commonLeadAssignmentUI, 0.0);
                AnchorPane.setRightAnchor(commonLeadAssignmentUI, 0.0);
                AnchorPane.setTopAnchor(commonLeadAssignmentUI, 0.0);
                AnchorPane.setBottomAnchor(commonLeadAssignmentUI, 0.0);

                mainPane.getChildren().add(commonLeadAssignmentUI);
                commonLeadAssignmentUI.setVisible(false);
            } catch (IOException | RuntimeException iOException) {
                System.out.println("commonLeadAssignmentUI >>>>   " + iOException.getMessage());
            }
            menuHighlighter.highlight(commonPbMenu);

            showUI(commonLeadAssignmentUI);
        }
    }

    private void launchConcordiaAndWeightedMeanPlots() {
        mainPane.getChildren().remove(plotUI);
        squidProject.getTask().buildSquidSpeciesModelList();
        if (confirmReduction()) {
            PlotsController.currentlyPlottedSampleTreeNode = null;
            launchInterpretations();
            
            showUI(plotUI);
            menuHighlighter.highlight(manageInterpretationsMenu);
        }
    }

    private boolean confirmReduction() {
        boolean retVal = false;
        // if ratios list not populated or no ref mat chosen show warning
        if (squidProject.getTask().getSquidRatiosModelList().isEmpty()) {
            showManageIsotopesWarning();
        } else if ((squidProject.isTypeGeochron()) && squidProject.getTask().getReferenceMaterialSpots().isEmpty()) {
            showManageRefMatWarning();
        } else if ((squidProject.isTypeGeochron()) && !((ReferenceMaterialModel) squidProject.getTask().getReferenceMaterialModel()).hasAtLeastOneNonZeroApparentDate()) {
            showManageRefMatModelWarning();
        } else {
            retVal = true;
        }
        return retVal;
    }

    @FXML
    private void referenceMaterialConcordiaAction(ActionEvent event) {
        PlotsController.fractionTypeSelected = SpotTypes.REFERENCE_MATERIAL;
        PlotsController.plotTypeSelected = PlotTypes.CONCORDIA;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        launchConcordiaAndWeightedMeanPlots();
    }

    @FXML
    private void referenceMaterialWMAction(ActionEvent event) {
        PlotsController.fractionTypeSelected = SpotTypes.REFERENCE_MATERIAL;
        PlotsController.plotTypeSelected = PlotTypes.WEIGHTED_MEAN;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        launchConcordiaAndWeightedMeanPlots();
    }

    @FXML
    private void unknownConcordiaAction(ActionEvent event) {
        PlotsController.fractionTypeSelected = SpotTypes.UNKNOWN;
        PlotsController.plotTypeSelected = PlotsController.PlotTypes.CONCORDIA;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        launchConcordiaAndWeightedMeanPlots();
    }

    @FXML
    private void weightedMeansAction(ActionEvent event) {
        PlotsController.fractionTypeSelected = SpotTypes.UNKNOWN;
        PlotsController.plotTypeSelected = PlotTypes.WEIGHTED_MEAN_SAMPLE;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        launchConcordiaAndWeightedMeanPlots();
    }

    @FXML
    private void plotAnyTwoExpressionsAction(ActionEvent event) {
        PlotsController.fractionTypeSelected = SpotTypes.REFERENCE_MATERIAL;
        PlotsController.plotTypeSelected = PlotTypes.ANY_TWO;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        launchConcordiaAndWeightedMeanPlots();
    }

    @FXML
    private void openParametersManagerPhysConst(ActionEvent event) {
        parametersLauncher.launchParametersManager(ParametersLauncher.ParametersTab.physConst);
    }

    @FXML
    private void openParametersManagerRefMat(ActionEvent event) {
        parametersLauncher.launchParametersManager(ParametersLauncher.ParametersTab.refMat);
    }

    @FXML
    private void openParametersManagerCommonPbModels(ActionEvent event) {
        parametersLauncher.launchParametersManager(ParametersLauncher.ParametersTab.commonPb);
    }

    private void openDefaultSquidLabDataModels() {
        parametersLauncher.launchParametersManager(ParametersLauncher.ParametersTab.defaultModels);
    }

    @FXML
    public void importCustomExpressionsOnAction(ActionEvent actionEvent) {
        File folder = FileHandler.getCustomExpressionFolder(primaryStageWindow);
        if (folder != null && folder.exists()) {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            File[] files;
            try {
                final Schema schema = sf.newSchema(new File(Squid3Constants.URL_STRING_FOR_SQUIDTASK_EXPRESSION_XML_SCHEMA_LOCAL));
                files = folder.listFiles(f -> f.getName().toLowerCase().endsWith(".xml")
                        && FileValidator.validateFileIsXMLSerializedEntity(f, schema));
            } catch (SAXException e) {
                files = folder.listFiles(f -> f.getName().toLowerCase().endsWith(".xml"));
            }

            final List<Expression> expressions = squidProject.getTask().getTaskExpressionsOrdered();

            ButtonType replaceAll = new ButtonType("Replace All");
            ButtonType replaceNone = new ButtonType("Replace None");
            ButtonType replace = new ButtonType("Replace");
            ButtonType dontReplace = new ButtonType("Don't Replace");
            ButtonType rename = new ButtonType("Rename");
            Alert alert = new Alert(Alert.AlertType.WARNING, "", replaceAll, replaceNone, replace, dontReplace, rename);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.initOwner(primaryStage.getScene().getWindow());
            alert.setX(primaryStage.getX() + (primaryStage.getWidth() - alert.getWidth()) / 2);
            alert.setY(primaryStage.getY() + (primaryStage.getHeight() - alert.getHeight()) / 2);
            for (int i = 0; i < files.length; i++) {
                try {
                    final Expression exp = (Expression) (new Expression()).readXMLObject(files[i].getAbsolutePath(), false);

                    if (expressions.contains(exp)) {
                        if (alert.getResult() != null && alert.getResult().equals(replaceAll)) {
                            expressions.remove(exp);
                            expressions.add(exp);
                        } else if (alert.getResult() == null || !alert.getResult().equals(replaceNone)) {
                            alert.setContentText(exp.getName() + " exists");
                            alert.showAndWait().ifPresent((t) -> {
                                if (t.equals(replace) || t.equals(replaceAll)) {
                                    expressions.remove(exp);
                                    expressions.add(exp);
                                } else if (t.equals(rename)) {
                                    TextInputDialog dialog = new TextInputDialog(exp.getName());
                                    dialog.setTitle("Rename");
                                    dialog.setHeaderText("Rename " + exp.getName());
                                    dialog.setContentText("Enter the new name:");
                                    Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                                    TextField newName = null;
                                    for (Node n : dialog.getDialogPane().getChildren()) {
                                        if (n instanceof TextField) {
                                            newName = (TextField) n;
                                        }
                                    }
                                    if (okBtn != null && newName != null) {
                                        newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                            okBtn.setDisable(squidProject.getTask().expressionExists(exp) || newValue.isEmpty());
                                        });
                                    }
                                    dialog.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
                                    dialog.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
                                    Optional<String> result = dialog.showAndWait();
                                    if (result.isPresent()) {
                                        exp.setName(result.get());
                                        expressions.add(exp);
                                    }
                                }
                            });
                        }
                    } else {
                        expressions.add(exp);
                    }
                } catch (Exception e) {
                    System.out.println(files[i].getName() + " custom expression not added");
                }
            }

            squidProject.getTask().setChanged(true);
            //two passes needed
            squidProject.getTask().updateAllExpressions(true);
            squidProject.getTask().updateAllExpressions(true);

            squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
        } else {
            System.out.println("custom expressions folder does not exist");
        }

        buildExpressionMenuMRU();

        launchExpressionBuilder();

        showUI(expressionBuilderUI);
    }

    @FXML
    public void exportCustomExpressionsOnAction(ActionEvent actionEvent) {
        File folder = FileHandler.setCustomExpressionFolder(primaryStageWindow);
        if (folder != null) {
            folder.mkdirs();
            for (Expression expression : squidProject.getTask().getCustomTaskExpressions()) {
                try {
                    expression.serializeXMLObject(folder.getAbsolutePath() + File.separator
                            + FileNameFixer.fixFileName(expression.getName()) + ".xml");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            System.out.println("custom expression folder not created");
        }
    }

    @FXML
    private void choosePrawnFileMenuItemAction(ActionEvent event) {
        try {
            File prawnXMLFileNew = FileHandler.selectPrawnXMLFile(primaryStageWindow);
            if (prawnXMLFileNew != null) {
                squidProject.setupPrawnXMLFile(prawnXMLFileNew);
                squidProject.updateFilterForRefMatSpotNames("");
                squidProject.updateFilterForConcRefMatSpotNames("");
                squidProject.updateFiltersForUnknownNames(new HashMap<>());
                squidProject.getTask().setChanged(true);
                squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
                squidPersistentState.updatePrawnFileListMRU(prawnXMLFileNew);
                squidProject.autoDivideSamples();
                squidProject.setProjectName("NEW PROJECT");
                launchProjectManager();
                saveSquidProjectMenuItem.setDisable(true);
            }
        } catch (IOException | JAXBException | SAXException | SquidException anException) {
            String message = anException.getMessage();
            if (message == null) {
                message = anException.getCause().getMessage();
            }

            SquidMessageDialog.showWarningDialog(
                    "Squid encountered an error while trying to open the selected Prawn File:\n\n"
                    + message,
                    primaryStageWindow);
        }
    }

    private void synchronizeTaskLabDataAndSquidVersion() {
        if (squidProject != null && squidProject.getTask() != null) {
            TaskInterface task = squidProject.getTask();

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
            squidProject.setReferenceMaterialModel(task.getReferenceMaterialModel());
            squidProject.setConcentrationReferenceMaterialModel(task.getConcentrationReferenceMaterialModel());

            if (SquidProject.isProjectChanged()) {
                // next two lines make sure 15-digit rounding is used by reprocessing data
                task.setChanged(true);
                task.setupSquidSessionSpecsAndReduceAndReport(true);
                
                ((Task) task).initTaskDefaultSquidReportTables(true);

                SquidMessageDialog.showInfoDialog(
                        "The task has been updated for this version of Squid3.\n"
                        + "Please save Project.",
                        primaryStageWindow);
            }
        }
    }

    public static void createCopyToClipboardContextMenu(TextArea textArea) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyAll = new MenuItem("Copy all");
        copyAll.setOnAction((evt) -> {
            content.putString(textArea.getText());
            clipboard.setContent(content);
        });
        MenuItem copyAsCsv = new MenuItem("Copy all as CSV");
        copyAsCsv.setOnAction((evt) -> {
            String csv = textArea.getText().replaceAll("\\s*\\R", "\n");
            String csv2 = csv.replaceAll("\\s+[^\\S\\r\\n]", ", ");
            content.putString(csv2);
            clipboard.setContent(content);
        });
        contextMenu.getItems().addAll(copyAll, copyAsCsv);

        textArea.setContextMenu(contextMenu);
    }

    @FXML
    private void enjoySquidMenuItemAction(ActionEvent event) {
        BrowserControl.showURI("https://www.popsci.com/resizer/BHnnigECLPVEb2Ypab_mQTar8dk=/795x474/arc-anglerfish-arc2-prod-bonnier.s3.amazonaws.com/public/E33YQCRIFLE3TWYBFO5J5ASLL4.png");
    }

    @FXML
    public void reportLayoutManagerOnAction(ActionEvent actionEvent) {
        mainPane.getChildren().remove(squidReportSettingsUI);
        launchReportLayoutManager();
        showUI(squidReportSettingsUI);
    }

    private void launchReportLayoutManager() {
        try {
            squidReportSettingsUI = FXMLLoader.load(getClass().getResource("squidReportTable/SquidReportSettings.fxml"));
            squidReportSettingsUI.setId("SquidReportSettings");

            AnchorPane.setLeftAnchor(squidReportSettingsUI, 0.0);
            AnchorPane.setRightAnchor(squidReportSettingsUI, 0.0);
            AnchorPane.setTopAnchor(squidReportSettingsUI, 0.0);
            AnchorPane.setBottomAnchor(squidReportSettingsUI, 0.0);

            mainPane.getChildren().add(squidReportSettingsUI);
            squidReportSettingsUI.setVisible(false);

            menuHighlighter.highlight(manageReportsMenu);

        } catch (IOException ex) {
            Logger.getLogger(SquidUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void assignCommonLeadRatiosAction(ActionEvent event) {
        launchCommonLeadAssignment();
    }

    @FXML
    private void citeSquidAction(ActionEvent event) {
        BrowserControl.showURI("https://doi.org/10.11636/133870");
    }

    @FXML
    private void openDemoSquiProjectAction(ActionEvent event) {
        try {
            openProject(DEMO_SQUID_PROJECTS_FOLDER.getAbsolutePath() + File.separator + "SQUID3_demo_file.squid");
        } catch (IOException iOException) {
        }
    }

    // Task actions ************************************************************
    @FXML
    private void browseSquidTasksLibrary(ActionEvent event) {
        if (SQUID_TASK_LIBRARY_FOLDER != null) {
            TaskFolderBrowserController.tasksBrowserTarget = SQUID_TASK_LIBRARY_FOLDER;
            TaskFolderBrowserController.tasksBrowserType = ".xml";
            launchTaskFolderBrowser();
        }
    }

    @FXML
    private void browseTaskFolderTaskMenuItemAction(ActionEvent event) {
        File tasksBrowserTarget = FileHandler.selectSquid3TasksFolderForBrowsing(primaryStageWindow);
        if (tasksBrowserTarget != null) {
            TaskFolderBrowserController.tasksBrowserTarget = tasksBrowserTarget;
            TaskFolderBrowserController.tasksBrowserType = ".xml";
            launchTaskFolderBrowser();
        }
    }

    @FXML
    private void browseSquid25TaskFolderMenuItemAction(ActionEvent event) {
        File tasksBrowserTarget = FileHandler.selectSquid25TasksFolderForBrowsing(primaryStageWindow);
        if (tasksBrowserTarget != null) {
            TaskFolderBrowserController.tasksBrowserTarget = tasksBrowserTarget;
            TaskFolderBrowserController.tasksBrowserType = ".xls";
            launchTaskFolderBrowser();
        }
    }

    @FXML
    private void viewTaskMenuItemAction(ActionEvent event) {
        launchTaskViewer();
    }

    @FXML
    private void editTaskMenuItemAction(ActionEvent event) {
        launchTaskEditor(EDIT_CURRENT);
    }

    @FXML
    private void editEmptyTaskAction(ActionEvent event) {
        launchTaskEditor(TaskEditTypeEnum.EDIT_EMPTY);
    }

    @FXML
    private void editCopyCurrentTaskAction(ActionEvent event) {
        launchTaskEditor(TaskEditTypeEnum.EDIT_COPY_CURRENT);
    }

    @FXML
    private void editCopyCurrentTaskNoExpAction(ActionEvent event) {
        launchTaskEditor(TaskEditTypeEnum.EDIT_COPY_CURRENT_NO_EXP);
    }

    @FXML
    private void editExistingTaskMenuItemAction(ActionEvent event) {
        launchTaskEditor(TaskEditTypeEnum.EDIT_EXISTING_TASK);
    }

    private class HighlightMainMenu {

        private Menu highlightedMenu;

        public HighlightMainMenu() {
            this(null);
        }

        public HighlightMainMenu(Menu highlightedMenu) {
            this.highlightedMenu = highlightedMenu;

        }

        public void highlight(Menu item) {
            deHighlight();

            if (item != null) {
                item.getStyleClass().add("highlightedMenu");
            }
            highlightedMenu = item;
        }

        public void deHighlight() {
            deHighlight(highlightedMenu);
        }

        public void deHighlight(Menu item) {
            if (item != null) {
                ObservableList<String> styles = item.getStyleClass();
                for (int i = 0; i < styles.size(); i++) {
                    String currStyle = styles.get(i);
                    if (currStyle.compareTo("highlightedMenu") == 0) {
                        styles.remove(i);
                        break;
                    }
                }
            }
        }
    }
}
