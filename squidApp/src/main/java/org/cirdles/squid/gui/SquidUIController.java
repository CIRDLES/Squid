
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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.StageStyle;
import org.cirdles.squid.Squid;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController;
import org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.PlotTypes;
import org.cirdles.squid.gui.dialogs.SquidMessageDialog;
import org.cirdles.squid.gui.expressions.ExpressionBuilderController;
import org.cirdles.squid.gui.parameters.ParametersLauncher;
import org.cirdles.squid.gui.squidReportTable.SquidReportTableLauncher;
import org.cirdles.squid.gui.utilities.BrowserControl;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.parameters.ParametersModelComparator;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.StaceyKramerCommonLeadModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import org.cirdles.squid.utilities.fileUtilities.FileNameFixer;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.cirdles.squid.constants.Squid3Constants.*;
import static org.cirdles.squid.constants.Squid3Constants.TaskEditTypeEnum.EDIT_CURRENT;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GENERAL;
import static org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum.GEOCHRON;
import static org.cirdles.squid.core.CalamariReportsEngine.CalamariReportFlavors.MEAN_RATIOS_PER_SPOT_UNKNOWNS;
import static org.cirdles.squid.gui.SquidUI.*;
import static org.cirdles.squid.gui.utilities.BrowserControl.urlEncode;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_238U235U_RM_MODEL_NAME;
import static org.cirdles.squid.utilities.fileUtilities.FileValidator.validateXML;
import static org.cirdles.squid.utilities.fileUtilities.ZipUtility.extractZippedFile;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class SquidUIController implements Initializable {

    public static File fileToLoad;
    public static SquidLabData squidLabData = null;
    public static SquidPersistentState squidPersistentState = null;
    public static SquidProject squidProject;
    public static Node taskFolderBrowserUI;
    public static Node plotUI;
    public static VBox countCorrectionsUI;
    public static VBox commonLeadAssignmentUI;
    public static VBox weightedMeansUI;
    public static String projectFileName;
    public static ParametersLauncher parametersLauncher;
    public static SquidReportTableLauncher squidReportTableLauncher;
    public static HighlightMainMenu menuHighlighter;
    public static int squidProjectOriginalHash;
    private static GridPane projectManagerUI;
    private static VBox sessionAuditUI;
    private static ScrollPane massesAuditUI;
    private static HBox spotManagerUI;
    private static GridPane taskManagerUI;
    private static GridPane taskEditorUI;
    private static VBox isotopesManagerUI;
    private static ScrollPane countsAuditManager;
    private static SplitPane expressionBuilderUI;
    private static AnchorPane squidReportSettingsUI;
    private static Pane reductionManagerUI;
    private static Pane reducedDataReportManagerUI;

    private static int enjoyImageRotationIndex = 0;

    static {
        try {
            squidPersistentState = SquidPersistentState.getExistingPersistentState();
        } catch (SquidException e) {
            e.printStackTrace();
        }
    }

    static {
        CalamariFileUtilities.initSampleParametersModels();
        try {
            squidLabData = SquidLabData.getExistingSquidLabData();
            squidLabData.testVersionAndUpdate();
        } catch (SquidException squidException) {
        }
    }

    public boolean runSaveMenuDisableCheck;
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
    @FXML
    private Label squidVersionLabel;
    @FXML
    private Label versionBuildDate;

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

    private void loadSquidFile(File fileToLoad) {
        try {
            if (fileToLoad.getName().toLowerCase(Locale.ROOT).endsWith(".squid")) {
                openProject(fileToLoad.getAbsolutePath());
            } else if (fileToLoad.getName().toLowerCase(Locale.ROOT).endsWith(".xml")) {
                processPrawnXMLFile(fileToLoad);
            } else if (fileToLoad.getName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
                Path prawnFilePathParent = fileToLoad.toPath().getParent();
                Path prawnFilePath = extractZippedFile(fileToLoad, prawnFilePathParent.toFile());
                File prawnSourceFileNew = prawnFilePath.toFile();
                processPrawnXMLFile(prawnSourceFileNew);
            }
        } catch (IOException | SquidException | JAXBException | SAXException e) {
            e.printStackTrace();
        }
    }


    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // June 2022 implement drag n drop of files
        // Tuned for issue #745
        mainPane.setOnDragOver(event -> {
            ObservableList<Node> children = mainPane.getChildren();
            boolean proceed = true;
            for (Node child : children) {
                if (child.idProperty().toString().contains("ExpressionBuilder")
                        || child.idProperty().toString().contains("SquidReportSettings")
                        || child.idProperty().toString().contains("MassesAudit")) {
                    proceed = false;
                    break;
                }
            }
            if (proceed) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        });
        mainPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (event.getDragboard().hasFiles()) {
                File fileToLoad = db.getFiles().get(0);
                loadSquidFile(fileToLoad);
            }
        });

        squidVersionLabel.setText("v" + Squid.VERSION);
        versionBuildDate.setText(Squid.RELEASE_DATE);

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
        CalamariFileUtilities.loadShrimpFileSchema();
        CalamariFileUtilities.loadJavadoc();
        CalamariFileUtilities.initXSLTML();
        CalamariFileUtilities.initSquidTaskLibraryFiles();

        parametersLauncher = new ParametersLauncher(primaryStage);
        squidReportTableLauncher = new SquidReportTableLauncher(primaryStage);

        // check for file from command line
        if ((fileToLoad != null) && fileToLoad.exists()) {
            loadSquidFile(fileToLoad);
        }
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
                } catch (IOException | SquidException iOException) {
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
                } catch (IOException | SquidException iOException) {
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

                try {
                    if (!loadExpressionFromXMLFile(new File(menuItem.getText()))) {
                        squidPersistentState.removeExpressionFileNameFromMRU(menuItem.getText());
                        squidPersistentState.cleanExpressionListMRU();
                        openRecentExpressionFileMenu.getItems().remove(menuItem);
                    }
                } catch (SquidException squidException) {
                    SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
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
        mainPane.getChildren().remove(countsAuditManager);

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
     * @param projectType the value of projectType
     */
    private void prepareForNewProject(TaskTypeEnum projectType) throws SquidException {
        confirmSaveOnProjectClose();
        removeAllManagers();

        squidProject = new SquidProject(projectType);

        // this updates output folder for reports to current version
        CalamariFileUtilities.initCalamariReportsFolder(squidProject.getPrawnFileHandler(), new File(projectFileName).getParentFile());

        runSaveMenuDisableCheck = false;
    }

    @FXML
    private void newSquidProjectFromOPFileAction() {
        try {
            openOPFile(FileHandler.selectOPFile(primaryStageWindow));
        } catch (IOException | SquidException iOException) {
            String message = iOException.getMessage();
            if (message == null) {
                message = iOException.getCause().getMessage();
            }

            SquidMessageDialog.showWarningDialog(
                    "Squid3 encountered an error while trying to open the selected file:\n\n"
                            + message,
                    primaryStageWindow);
        }
    }

    private void openOPFile(String path) throws IOException, SquidException {
        openOPFile(new File(path));
    }

    private void openOPFile(File file) throws IOException, SquidException {
        prepareForNewProject(GEOCHRON);
        if (file != null) {
            if (squidProject.setupPrawnOPFile(file)) {
                squidProject.autoDivideSamples();
                //Needs own MRU squidPersistentState.updatePrawnFileListMRU(prawnSourceFile);
                SquidUI.updateStageTitle("");
                launchProjectManager();
                saveSquidProjectMenuItem.setDisable(true);
                customizeDataMenu();
                squidPersistentState.updateOPFileListMRU(file);
                buildOPFileMenuMRU();
                squidPersistentState.setMRUProjectFolderPath(file.getParent());
                saveAsSquidProject();
            } else {
                SquidMessageDialog.showWarningDialog(
                        "Squid3 encountered an error while trying to open the selected data file.",
                        primaryStageWindow);
            }
        } else {
            squidProject.getTask().setChanged(false);
            SquidProject.setProjectChanged(false);
        }
    }

    @FXML
    private void newSquidProjectFromZippedPrawnAction() {
        try {
            prepareForNewProject(GEOCHRON);
            File prawnZippedSourceFile = FileHandler.selectZippedPrawnXMLFile(primaryStageWindow);
            if (prawnZippedSourceFile != null) {

                Path prawnFilePathParent = prawnZippedSourceFile.toPath().getParent();
                Path prawnFilePath = extractZippedFile(prawnZippedSourceFile, prawnFilePathParent.toFile());

                File prawnSourceFileNew = prawnFilePath.toFile();

                if (squidProject.setupPrawnXMLFile(prawnSourceFileNew)) {
                    squidProject.autoDivideSamples();
                    squidPersistentState.updatePrawnFileListMRU(prawnSourceFileNew);
                    SquidUI.updateStageTitle("");
                    launchProjectManager();
                    saveSquidProjectMenuItem.setDisable(true);
                    customizeDataMenu();
                    squidPersistentState.setMRUProjectFolderPath(prawnZippedSourceFile.getParent());
                    saveAsSquidProject();
                } else {
                    SquidMessageDialog.showWarningDialog(
                            "Squid3 encountered an error while trying to open the selected data file.",
                            primaryStageWindow);
                }
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
                    "Squid3 encountered an error while trying to open the selected file:\n\n"
                            + message,
                    primaryStageWindow);
        }
    }

    @FXML
    private void newSquidProjectAction() {
        try {
//            prepareForNewProject(GEOCHRON);
            File prawnSourceFile = FileHandler.selectPrawnXMLFile(primaryStageWindow);
            if (prawnSourceFile != null) {
                processPrawnXMLFile(prawnSourceFile);
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
                    "Squid3 encountered an error while trying to open the selected file:\n\n"
                            + message,
                    primaryStageWindow);
        }
    }

    private void processPrawnXMLFile(File prawnSourceFile) throws SquidException, JAXBException, IOException, SAXException {
        prepareForNewProject(GEOCHRON);
        if (squidProject.setupPrawnXMLFile(prawnSourceFile)) {
            squidProject.autoDivideSamples();
            squidPersistentState.updatePrawnFileListMRU(prawnSourceFile);
            SquidUI.updateStageTitle("");
            launchProjectManager();
            saveSquidProjectMenuItem.setDisable(true);
            customizeDataMenu();
            squidPersistentState.setMRUProjectFolderPath(prawnSourceFile.getParent());
            saveAsSquidProject();
        } else {
            SquidMessageDialog.showWarningDialog(
                    "Squid3 encountered an error while trying to open the selected data file.",
                    primaryStageWindow);
        }
    }

    @FXML
    private void newSquidRatioProjectAction() {
        try {
            prepareForNewProject(GENERAL);
            File prawnSourceFile = FileHandler.selectPrawnXMLFile(primaryStageWindow);
            if (prawnSourceFile != null) {
                if (squidProject.setupPrawnXMLFile(prawnSourceFile)) {
                    squidProject.autoDivideSamples();
                    squidPersistentState.updatePrawnFileListMRU(prawnSourceFile);

                    // auto generate task data for nominal masses
                    squidProject.initializeTaskAndReduceData(true);

                    SquidUI.updateStageTitle("");
                    launchProjectManager();
                    saveSquidProjectMenuItem.setDisable(true);
                    customizeDataMenu();
                    squidPersistentState.setMRUProjectFolderPath(prawnSourceFile.getParent());
                    saveAsSquidProject();
                } else {
                    SquidMessageDialog.showWarningDialog(
                            "Squid3 encountered an error while trying to open the selected data file.",
                            primaryStageWindow);
                }
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
                    "Squid3 encountered an error while trying to open the selected file:\n\n"
                            + message,
                    primaryStageWindow);
        }
    }

    @FXML
    private void newSquidProjectByJoinAction() {
        try {
            prepareForNewProject(GEOCHRON);

            SquidMessageDialog.showInfoDialog(
                    "To join two Prawn XML files, be sure they are in the same folder, \n\tand then in the next dialog, choose both files."
                            + "\n\nNotes: \n\t1) Joining will be done by comparing the timestamps of the first run in \n\t    each file to determine the order of join."
                            + "\n\n\t2) The joined file will be written to disk and then read back in as a \n\t    check.  The name of the new file"
                            + " will appear in the project manager's \n\t    text box for the Prawn XML file name.",
                    primaryStageWindow);

            List<File> prawnXMLFilesNew = FileHandler.selectForJoinTwoPrawnXMLFiles(primaryStageWindow);
            if (prawnXMLFilesNew.size() == 2) {
                if (squidProject.setupPrawnXMLFileByJoin(prawnXMLFilesNew)) {
                    squidProject.autoDivideSamples();
                    squidPersistentState.updatePrawnFileListMRU(prawnXMLFilesNew.get(0));
                    SquidUI.updateStageTitle("");
                    launchProjectManager();
                    saveSquidProjectMenuItem.setDisable(true);
                    customizeDataMenu();
                    squidPersistentState.setMRUProjectFolderPath(prawnXMLFilesNew.get(0).getParent());
                    saveAsSquidProject();
                } else {
                    SquidMessageDialog.showWarningDialog(
                            "Squid3 encountered an error while trying to open and join the selected files.",
                            primaryStageWindow);
                }
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
                    "Squid3 encountered an error while trying to open and join the selected files:\n\n"
                            + message,
                    primaryStageWindow);
        }
    }

    @FXML
    private void saveAsSquidProjectMenuItemAction() {
        if (squidProject != null) {
            saveAsSquidProject();
        }
    }

    private void saveAsSquidProject() {
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

    @FXML
    private void openSquidProjectMenuItemAction() {
        confirmSaveOnProjectClose();
        removeAllManagers();

        try {
            projectFileName = FileHandler.selectProjectFile(SquidUI.primaryStageWindow);
            openProject(projectFileName);
        } catch (IOException | SquidException iOException) {
        }
    }

    private void openProject(String aProjectFileName) throws IOException, SquidException {
        if (!"".equals(aProjectFileName)) {
            projectFileName = aProjectFileName;
            confirmSaveOnProjectClose();
            squidProject = (SquidProject) SquidSerializer.getSerializedObjectFromFile(projectFileName, true);

            if (squidProject != null) {
                synchronizeTaskLabDataAndSquidVersion();

                // fixes #624 by correcting out of synch tasks
                squidProject.getTask().setSelectedIndexIsotope(squidProject.getSelectedIndexIsotope());
                squidProject.getTask().setChanged(true);
                squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

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
    private void closeSquidProjectMenuItemClose() {
        confirmSaveOnProjectClose();
        removeAllManagers();
        SquidUI.updateStageTitle("");
        menuHighlighter.deHighlight();
    }

    @FXML
    private void saveSquidProjectMenuItemAction() {
        if (squidProject != null) {
            try {
                ProjectFileUtilities.serializeSquidProject(squidProject, squidPersistentState.getMRUProjectFile().getCanonicalPath());
                squidProjectOriginalHash = squidProject.hashCode();
            } catch (IOException | SquidException ex) {
                SquidMessageDialog.showWarningDialog(ex.getMessage(), null);
            }
        }
    }

    private void confirmSaveOnProjectClose() {
        if (SquidProject.isProjectChanged()) {

            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Do you want to save Squid3 Project changes?",
                    ButtonType.YES,
                    ButtonType.NO
            );
            alert.setX(SquidUI.primaryStageWindow.getX() + (primaryStageWindow.getWidth() - 200) / 2);
            alert.setY(SquidUI.primaryStageWindow.getY() + (primaryStageWindow.getHeight() - 150) / 2);
            alert.showAndWait().ifPresent((t) -> {
                if (t.equals(ButtonType.YES)) {
                    File projectFile = null;
                    try {
                        projectFile = FileHandler.saveProjectFile(squidProject, primaryStageWindow);
                    } catch (IOException iOException) {
                        SquidMessageDialog.showWarningDialog("Squid3 cannot access the target file.\n"
                                        + ((projectFile != null) ? projectFile.getAbsolutePath() : ""),
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
    private void quitAction() {
        try {
            SquidPersistentState.getExistingPersistentState().updateSquidPersistentState();
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
        confirmSaveOnProjectClose();
        try {
            ExpressionBuilderController.EXPRESSION_NOTES_STAGE.close();
        } catch (Exception e) {
        }
        Platform.exit();
    }

    @FXML
    void sustainableVideoAction() {
        BrowserControl.showURI("https://www.youtube.com/watch?v=mC5eNrMjfk4");
    }

    @FXML
    private void onlineHelpAction() {
        BrowserControl.showURI("https://github.com/CIRDLES/Squid#readme"); //"http://cirdles.org/projects/squid/#Development");
    }

    @FXML
    private void aboutSquidAction() {
        squidAboutWindow.loadAboutWindow();
    }

    @FXML
    private void contributeIssueOnGitHubAction() {
        String version = "Squid3 Version: " + Squid.VERSION;
        String javaVersion = "Java Version: " + System.getProperties().getProperty("java.version");
        String javaFXVersion = "JavaFX Version: " + System.getProperties().getProperty("javafx.runtime.version");
        String operatingSystem = "OS: " + System.getProperties().getProperty("os.name") + " " + System.getProperties().getProperty("os.version");

        String issueBody = urlEncode(version + "\n") +
                urlEncode(javaVersion + "\n") +
                urlEncode(javaFXVersion + "\n") +
                urlEncode(operatingSystem + "\n") +
                urlEncode("\nIssue details:\n");

        BrowserControl.showURI("https://github.com/CIRDLES/Squid/issues/new?body=" + issueBody);
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

    public void launchMassesAudit() throws SquidException {
        if (massesAuditUI != null) {
            mainPane.getChildren().remove(massesAuditUI);
            massesAuditUI.getSkin().dispose();
        }
        squidProject.getTask().buildSquidSpeciesModelList();
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
            ((Task) squidProject.getTask()).resetMassStationGraphViews();
        }
        showUI(massesAuditUI);
    }

    private void launchSpotManager() {
        try {
            // force use of delimiter to filter samples
            sessionAuditUI = FXMLLoader.load(getClass().getResource("SessionAudit.fxml"));
            sessionAuditUI.setId("SessionAudit");

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

    public void launchTaskViewer() throws SquidException {
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
     * @param editType the value of editType
     */
    public void launchTaskEditor(TaskEditTypeEnum editType) throws SquidException {
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

    private void launchTaskFolderBrowser() throws SquidException {
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

    private void launchExpressionBuilder() throws SquidException {

        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
        squidProject.getTask().updateAllExpressions(true);

        // present warning if needed
        if (squidProject.isTypeGeochron() && !squidProject.projectIsHealthyGeochronMode()) {
            SquidMessageDialog.showInfoDialog("Please be sure to Manage Isotopes (press red button) to initialize expressions\n"
                            + " and confirm the ratios in ParentElement_ConcenConst = \n"
                            + "\t\t" + squidProject.getTask().getExpressionByName(PARENT_ELEMENT_CONC_CONST).getExcelExpressionString(),
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

    private void launchReducedDataReportManager() throws SquidException {
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
    private void projectManagerMenuItemAction() {
        launchProjectManager();
    }

    private void showUI(Node myManager) throws SquidException {
        SquidPersistentState.getExistingPersistentState().updateSquidPersistentState();

        for (Node manager : mainPane.getChildren()) {
            manager.setVisible(false);
        }

        // logo
        mainPane.getChildren().get(0).setVisible(true);

        myManager.setVisible(true);
    }

    @FXML
    private void auditSessionMenuItemAction() throws SquidException {
        mainPane.getChildren().remove(sessionAuditUI);
        launchSessionAudit();
        showUI(sessionAuditUI);
    }

    @FXML
    private void manageSpotsMenuItemAction() throws SquidException {
        mainPane.getChildren().remove(spotManagerUI);
        launchSpotManager();
        showUI(spotManagerUI);
    }

    @FXML
    private void auditMassesMenuItemAction() throws SquidException {
        launchMassesAudit();
    }

    @FXML
    private void specifyIsotopesMenuItemAction() throws SquidException {
        mainPane.getChildren().remove(isotopesManagerUI);
        squidProject.getTask().buildSquidSpeciesModelList();
        launchIsotopesManager();
        showUI(isotopesManagerUI);
    }

    @FXML
    private void savePrawnFileCopyMenuItemAction() {
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
    private void loadExpressionFromXMLFileMenuItemAction() {
        try {
            File expressionFileXML = FileHandler.selectExpressionXMLFile(primaryStageWindow);
            loadExpressionFromXMLFile(expressionFileXML);

        } catch (IOException | JAXBException | SAXException | SquidException iOException) {
        }
    }

    private boolean loadExpressionFromXMLFile(File expressionFileXML) throws SquidException {
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
                    alert.setX(primaryStageWindow.getX() + (primaryStageWindow.getWidth() - 200) / 2);
                    alert.setY(primaryStageWindow.getY() + (primaryStageWindow.getHeight() - 150) / 2);
                    alert.showAndWait().ifPresent((t) -> {
                        if (t.equals(replace)) {
                            try {
                                addExpressionToTask(exp);
                            } catch (SquidException squidException) {
                                SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
                            }
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
                            dialog.setX(primaryStageWindow.getX() + (primaryStageWindow.getWidth() - 200) / 2);
                            dialog.setY(primaryStageWindow.getY() + (primaryStageWindow.getHeight() - 150) / 2);
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                exp.setName(result.get());
                                try {
                                    addExpressionToTask(exp);
                                } catch (SquidException squidException) {
                                    SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
                                }
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

    private void addExpressionToTask(Expression exp) throws SquidException {
        squidProject.getTask().removeExpression(exp, true);
        squidProject.getTask().addExpression(exp, true);

        ExpressionBuilderController.expressionToHighlightOnInit = exp;
        buildExpressionMenuMRU();
        launchExpressionBuilder();
        showUI(expressionBuilderUI);
    }

    @FXML
    private void showWithinSpotRatiosReferenceMatMenutItemAction() throws SquidException {
        calamariReportFlavor = CalamariReportsEngine.CalamariReportFlavors.WITHIN_SPOT_RATIOS_REFERENCEMAT;
        launchReducedDataReportManager();
    }

    @FXML
    private void showWithinSpotRatiosUnknownsMenutItemAction() throws SquidException {
        calamariReportFlavor = CalamariReportsEngine.CalamariReportFlavors.WITHIN_SPOT_RATIOS_UNKNOWNS;
        launchReducedDataReportManager();
    }

    @FXML
    private void showMeanRatiosReferenceMatMenutItemAction() throws SquidException {
        calamariReportFlavor = CalamariReportsEngine.CalamariReportFlavors.MEAN_RATIOS_PER_SPOT_REFERENCEMAT;
        launchReducedDataReportManager();
    }

    @FXML
    private void showMeanRatiosUnknownMenutItemAction() throws SquidException {
        calamariReportFlavor = MEAN_RATIOS_PER_SPOT_UNKNOWNS;
        launchReducedDataReportManager();
    }

    @FXML
    private void reportsMenuSelectedAction(Event event) throws SquidException {
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
    }

    @FXML
    private void visitCIRDLESAction() {
        BrowserControl.showURI("https://CIRDLES.org");
    }

    @FXML
    private void showSquid3GithubRepo() {
        BrowserControl.showURI("https://github.com/CIRDLES/Squid");
    }

    @FXML
    private void showSquid3DevNotes() {
        BrowserControl.showURI("https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Intro");
    }

    @FXML
    private void showTopsoilGithubRepo() {
        BrowserControl.showURI("https://github.com/CIRDLES/Topsoil");
    }

    @FXML
    private void showLudwigLibraryGithubRepo() {
        BrowserControl.showURI("https://github.com/CIRDLES/LudwigLibrary");
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
    private void expressionBuilderMenuItemAction() throws SquidException {
        mainPane.getChildren().remove(expressionBuilderUI);
        launchExpressionBuilder();
        showUI(expressionBuilderUI);
    }

    @FXML
    private void ludwigLibraryJavaDocAction() {
        BrowserControl.showURI(LUDWIGLIBRARY_JAVADOC_FOLDER + File.separator + "index.html");
    }

    @FXML
    private void videoTutorialsMenuItemAction() {
        BrowserControl.showURI("https://www.youtube.com/playlist?list=PLfF8bcNRe2WTWx2IuDaHW_XpLh36bWkUc");
    }

    @FXML
    private void producePerScanReportsAction() {
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
    private void referenceMaterialsReportTableAction() throws IOException, SquidException {
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
    private void unknownsReportTableAction() throws IOException, SquidException {
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
    private void unknownsBySampleReportTableAction() throws IOException, SquidException {
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
    private void referenceMaterialSummaryReportOnAction() throws IOException {
        if (squidProject.hasReportsFolder()) {
            File summaryFile
                    = squidProject.getPrawnFileHandler().getReportsEngine().writeSummaryReportsForReferenceMaterials();
            SquidMessageDialog.showSavedAsDialog(summaryFile, primaryStageWindow);
        } else {
            showReportsWarning();
        }
    }

    @FXML
    private void unknownsSummaryReportOnAction() throws IOException {
        if (squidProject.hasReportsFolder()) {
            File summaryFile = squidProject.getPrawnFileHandler().getReportsEngine().writeSummaryReportsForUnknowns();
            SquidMessageDialog.showSavedAsDialog(summaryFile, primaryStageWindow);
        } else {
            showReportsWarning();
        }
    }

    @FXML
    private void produceTaskSummaryReportAction() throws IOException {
        if (squidProject.hasReportsFolder()) {
            File taskAuditFile = squidProject.getPrawnFileHandler().getReportsEngine().writeTaskAudit();
            SquidMessageDialog.showSavedAsDialog(taskAuditFile, primaryStageWindow);
        } else {
            showReportsWarning();
        }
    }

    @FXML
    public void produceProjectAuditReportAction() throws IOException {
        if (squidProject.hasReportsFolder()) {
            File projectAuditFile = squidProject.getPrawnFileHandler().getReportsEngine().writeProjectAudit();
            SquidMessageDialog.showSavedAsDialog(projectAuditFile, primaryStageWindow);
        } else {
            showReportsWarning();
        }
    }

    @FXML
    public void generateAllReportsAction() throws IOException, SquidException {
        if (squidProject.getTask().getRatioNames().isEmpty()) {
            SquidMessageDialog.showInfoDialog("Please be sure to Manage Isotopes and Ratios to initialize expressions.\n",
                    primaryStageWindow);
        } else {
            if (squidProject.hasReportsFolder()) {

                Path reportFolderPath = squidProject.generateAllReports();

                SquidMessageDialog.showSavedAsDialog(reportFolderPath.toFile(), primaryStageWindow);
            } else {
                showReportsWarning();
            }
        }
    }

    private void showReportsWarning() {
        SquidMessageDialog.showWarningDialog("The Squid3 Project must be saved before reports can be written out.", primaryStageWindow);
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

    public void launchCountCorrections() throws SquidException {
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

    public void launchCommonLeadAssignment() throws SquidException {
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

    private void launchConcordiaAndWeightedMeanPlots() throws SquidException {
        mainPane.getChildren().remove(plotUI);
        squidProject.getTask().buildSquidSpeciesModelList();
        squidProject.getTask().updateAllExpressions(true);
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
    private void referenceMaterialConcordiaAction() {
        PlotsController.fractionTypeSelected = SpotTypes.REFERENCE_MATERIAL;
        PlotsController.plotTypeSelected = PlotTypes.CONCORDIA;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        try {
            launchConcordiaAndWeightedMeanPlots();
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void referenceMaterialCalibrationConstAction() {
        PlotsController.fractionTypeSelected = SpotTypes.REFERENCE_MATERIAL;
        PlotsController.plotTypeSelected = PlotTypes.CALIBRATION_CONSTANT;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        try {
            launchConcordiaAndWeightedMeanPlots();
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void referenceMaterialWMAction() {
        PlotsController.fractionTypeSelected = SpotTypes.REFERENCE_MATERIAL;
        PlotsController.plotTypeSelected = PlotTypes.WEIGHTED_MEAN_RM;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        try {
            launchConcordiaAndWeightedMeanPlots();
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void unknownConcordiaAction() {
        PlotsController.fractionTypeSelected = SpotTypes.UNKNOWN;
        PlotsController.plotTypeSelected = PlotsController.PlotTypes.CONCORDIA;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        try {
            launchConcordiaAndWeightedMeanPlots();
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void weightedMeansAction() {
        PlotsController.fractionTypeSelected = SpotTypes.UNKNOWN;
        PlotsController.plotTypeSelected = PlotTypes.WEIGHTED_MEAN_SAMPLE;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        try {
            launchConcordiaAndWeightedMeanPlots();
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void plotAnyTwoExpressionsAction() {
        PlotsController.fractionTypeSelected = SpotTypes.REFERENCE_MATERIAL;
        PlotsController.plotTypeSelected = PlotTypes.ANY_TWO;
        PlotsController.currentlyPlottedSampleTreeNode = null;
        try {
            launchConcordiaAndWeightedMeanPlots();
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void openParametersManagerPhysConst() {
        parametersLauncher.launchParametersManager(ParametersLauncher.ParametersTab.physConst);
    }

    @FXML
    private void openParametersManagerRefMat() {
        parametersLauncher.launchParametersManager(ParametersLauncher.ParametersTab.refMat);
    }

    @FXML
    private void openParametersManagerCommonPbModels() {
        parametersLauncher.launchParametersManager(ParametersLauncher.ParametersTab.commonPb);
    }

    private void openDefaultSquidLabDataModels() {
        parametersLauncher.launchParametersManager(ParametersLauncher.ParametersTab.defaultModels);
    }

    @FXML
    public void importCustomExpressionsOnAction() throws SquidException {
        File folder = FileHandler.getCustomExpressionFolder(primaryStageWindow);
        if (folder != null && folder.exists()) {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            List<File> validatedFiles = new ArrayList<>();
            try {
                final Schema schema = sf.newSchema(new File(Squid3Constants.URL_STRING_FOR_SQUIDTASK_EXPRESSION_XML_SCHEMA_LOCAL));
                File[] files = folder.listFiles(f -> f.getName().toLowerCase().endsWith(".xml"));
                for (File file : files) {
                    try {
                        validateXML(file, schema);
                        validatedFiles.add(file);
                    } catch (SAXException | IOException e) {
                        // Need something for the user here?
                    }
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
                for (int i = 0; i < validatedFiles.size(); i++) {
                    try {
                        final Expression exp = (Expression) (new Expression()).readXMLObject(validatedFiles.get(i).getAbsolutePath(), false);

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
                                        dialog.setX(primaryStageWindow.getX() + (primaryStageWindow.getWidth() - 200) / 2);
                                        dialog.setY(primaryStageWindow.getY() + (primaryStageWindow.getHeight() - 150) / 2);
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
                        System.out.println(validatedFiles.get(i).getName() + " custom expression not added");
                    }
                }

            } catch (SAXException e) {
                String message = e.getMessage();
                SquidMessageDialog.showWarningDialog(
                        "Squid3 encountered an error while trying to open the folder of custom expressions\n\n"
                                + message,
                        primaryStageWindow);
            }

            try {
                squidProject.getTask().setChanged(true);
                //two passes needed
                squidProject.getTask().updateAllExpressions(true);
                squidProject.getTask().updateAllExpressions(true);

                squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
            } catch (SquidException squidException) {
                SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
            }


        } else {
            System.out.println("custom expressions folder does not exist");
        }

        buildExpressionMenuMRU();

        try {
            launchExpressionBuilder();
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }

        showUI(expressionBuilderUI);
    }

    @FXML
    public void exportCustomExpressionsOnAction() throws SquidException {
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
    private void choosePrawnFileMenuItemAction() {
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
                    "Squid3 encountered an error while trying to open the selected Prawn File:\n\n"
                            + message,
                    primaryStageWindow);
        }
    }

    private void synchronizeTaskLabDataAndSquidVersion() throws SquidException {
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
                // dec 2021 for issue #674
                task.setExtPErrU(squidProject.getExtPErrU());
                task.setExtPErrTh(squidProject.getExtPErrTh());

                // next two lines make sure 15-digit rounding is used by reprocessing data
                task.setChanged(true);

                task.setupSquidSessionSpecsAndReduceAndReport(true);

                // prime the models - this staceykramer code is for fixing issue #714 backwards compatible
                StaceyKramerCommonLeadModel.updatePhysicalConstants(squidProject.getTask().getPhysicalConstantsModel());
                StaceyKramerCommonLeadModel.updateU_Ratio(
                        squidProject.getTask().getReferenceMaterialModel().getDatumByName(REF_238U235U_RM_MODEL_NAME).getValue().doubleValue());
                ((Task) task).evaluateUnknownsWithChangedParameters(task.getUnknownSpots());
                // Issue #714
                ((Task) task).resetReferenceMaterialCommonLeadMethod();

                ((Task) task).initTaskDefaultSquidReportTables(true);

                ProjectFileUtilities.serializeSquidProject(squidProject, projectFileName);

                SquidMessageDialog.showInfoDialog(
                        "The project file has been updated for this version of Squid3.\n",
                        primaryStageWindow);
            }
        }
    }

    @FXML
    private void enjoySquidMenuItemAction() {
        List<String> enjoyImageRotationList = new ArrayList<>();
        enjoyImageRotationList.add("https://www.popsci.com/uploads/2019/06/24/E33YQCRIFLE3TWYBFO5J5ASLL4.png");
        enjoyImageRotationList.add("https://www.marinespecies.org/carms/photogallery.php?album=2003&pic=34970");

        BrowserControl.showURI(enjoyImageRotationList.get(enjoyImageRotationIndex % enjoyImageRotationList.size()));
        enjoyImageRotationIndex++;
    }

    @FXML
    public void reportLayoutManagerOnAction() throws SquidException {
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
    private void assignCommonLeadRatiosAction() throws SquidException {
        launchCommonLeadAssignment();
    }

    @FXML
    private void citeSquidAction() {
        BrowserControl.showURI("https://doi.org/10.11636/133870");
    }

    @FXML
    private void openDemoSquiProjectAction() {
        try {
            openProject(DEMO_SQUID_PROJECTS_FOLDER.getAbsolutePath() + File.separator + "SQUID3_demo_file.squid");
        } catch (IOException | SquidException ignored) {
        }
    }

    // Task actions ************************************************************
    @FXML
    private void browseSquidTasksLibrary() throws SquidException {
        TaskFolderBrowserController.tasksBrowserTarget = SQUID_TASK_LIBRARY_FOLDER;
        TaskFolderBrowserController.tasksBrowserType = ".xml";
        launchTaskFolderBrowser();
    }

    @FXML
    private void browseTaskFolderTaskMenuItemAction() throws SquidException {
        File tasksBrowserTarget = FileHandler.selectSquid3TasksFolderForBrowsing(primaryStageWindow);
        if (tasksBrowserTarget != null) {
            TaskFolderBrowserController.tasksBrowserTarget = tasksBrowserTarget;
            TaskFolderBrowserController.tasksBrowserType = ".xml";
            launchTaskFolderBrowser();
        }
    }

    @FXML
    private void browseSquid25TaskFolderMenuItemAction() throws SquidException {
        File tasksBrowserTarget = FileHandler.selectSquid25TasksFolderForBrowsing(primaryStageWindow);
        if (tasksBrowserTarget != null) {
            TaskFolderBrowserController.tasksBrowserTarget = tasksBrowserTarget;
            TaskFolderBrowserController.tasksBrowserType = ".xls";
            launchTaskFolderBrowser();
        }
    }

    @FXML
    private void viewTaskMenuItemAction() throws SquidException {
        launchTaskViewer();
    }

    @FXML
    private void editTaskMenuItemAction() throws SquidException {
        launchTaskEditor(EDIT_CURRENT);
    }

    @FXML
    private void editEmptyTaskAction() throws SquidException {
        launchTaskEditor(TaskEditTypeEnum.EDIT_EMPTY);
    }

    @FXML
    private void editCopyCurrentTaskAction() throws SquidException {
        launchTaskEditor(TaskEditTypeEnum.EDIT_COPY_CURRENT);
    }

    @FXML
    private void editCopyCurrentTaskNoExpAction() throws SquidException {
        launchTaskEditor(TaskEditTypeEnum.EDIT_COPY_CURRENT_NO_EXP);
    }

    @FXML
    private void editExistingTaskMenuItemAction() throws SquidException {
        launchTaskEditor(TaskEditTypeEnum.EDIT_EXISTING_TASK);
    }

    @FXML
    private void videoTutorialsGoogleDriveMenuItemAction() {
        BrowserControl.showURI("https://drive.google.com/drive/folders/1PnGhJENKeN6lLJyruc8mGewiUp1DAeCX?usp=sharing");
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