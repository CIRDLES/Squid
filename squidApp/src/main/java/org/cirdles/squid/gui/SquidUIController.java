
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

import org.cirdles.squid.gui.topsoil.TopsoilPlotWetherill;
import org.cirdles.squid.gui.topsoil.TopsoilWindow;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javax.xml.bind.JAXBException;
import org.cirdles.squid.Squid;
import static org.cirdles.squid.constants.Squid3Constants.getDEFAULT_RATIOS_LIST_FOR_10_SPECIES;
import org.cirdles.squid.core.CalamariReportsEngine;
import static org.cirdles.squid.core.CalamariReportsEngine.CalamariReportFlavors.MEAN_RATIOS_PER_SPOT_UNKNOWNS;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.gui.expressions.ExpressionBuilderController;
import org.cirdles.squid.gui.topsoil.AbstractTopsoilPlot;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.gui.utilities.BrowserControl;
import static org.cirdles.squid.gui.utilities.BrowserControl.urlEncode;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import static org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities.DEFAULT_LUDWIGLIBRARY_JAVADOC_FOLDER;
import org.cirdles.squid.utilities.fileUtilities.ProjectFileUtilities;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.TITLE;
import org.xml.sax.SAXException;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class SquidUIController implements Initializable {

    public static SquidProject squidProject;
    public static final SquidPersistentState squidPersistentState = SquidPersistentState.getExistingPersistentState();

    @FXML
    private Menu manageExpressionsMenu;
    @FXML
    private Menu manageTasksMenu;
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
    private ImageView squidImageView;

    private static VBox projectManagerUI;

    private static VBox sessionAuditUI;
    private static ScrollPane massesAuditUI;
    private static HBox spotManagerUI;

    private static GridPane taskManagerUI;

    private static VBox isotopesManagerUI;
    private static ScrollPane ratiosManagerUI;

    private static SplitPane expressionBuilderUI;
    private static Pane expressionManagerUI;

    private static Pane reductionManagerUI;
    private static Pane reducedDataReportManagerUI;
    private static Pane topsoilPlotUI;

    @FXML
    private MenuItem newSquid3TaskMenuItem;
    @FXML
    private MenuItem importSquid25TaskMenuItem;
    @FXML
    private MenuItem exportSquid3TaskMenuItem;
    @FXML
    private MenuItem importSquid3TaskMenuItem;
    @FXML
    private Menu openRecentSquidProjectMenu;
    @FXML
    private Menu selectSquid3TaskFromLibraryMenu;
    @FXML
    private Menu openRecentExpressionFileMenu;

    private TopsoilWindow[] topsoilWindows;

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

        managePrawnFileMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageRatiosMenu.setDisable(true);
        manageExpressionsMenu.setDisable(true);
        manageReportsMenu.setDisable(true);

        // Squid project menu items
        newSquidProjectMenuItem.setDisable(false);
        newSquidProjectByJoinMenuItem.setDisable(false);
        openSquidProjectMenuItem.setDisable(false);
        buildProjectMenuMRU();
        saveSquidProjectMenuItem.setDisable(true);
        saveAsSquidProjectMenuItem.setDisable(true);
        projectManagerMenuItem.setDisable(true);
        closeSquidProjectMenuItem.setDisable(true);

        // Prawn File Menu Items
        savePrawnFileCopyMenuItem.setDisable(false);

        //Task menu
        newSquid3TaskMenuItem.setDisable(false);
        selectSquid3TaskFromLibraryMenu.setDisable(false);
        importSquid25TaskMenuItem.setDisable(false);
        importSquid3TaskMenuItem.setDisable(true);
        exportSquid3TaskMenuItem.setDisable(true);

        // Expression menu
        buildExpressionMenuMRU();

        CalamariFileUtilities.initExamplePrawnFiles();
        CalamariFileUtilities.loadShrimpPrawnFileSchema();
        CalamariFileUtilities.loadJavadoc();
    }

    private void buildProjectMenuMRU() {
        openRecentSquidProjectMenu.setDisable(false);

        openRecentSquidProjectMenu.getItems().clear();
        List<String> mruProjectList = squidPersistentState.getMRUProjectList();
        for (String projectFileName : mruProjectList) {
            MenuItem menuItem = new MenuItem(projectFileName);
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

    private void buildTaskLibraryMenu() {
        selectSquid3TaskFromLibraryMenu.setDisable(false);

        selectSquid3TaskFromLibraryMenu.getItems().clear();
        Map< String, TaskInterface> taskLibrary = squidProject.getTaskLibrary();
        for (Map.Entry< String, TaskInterface> entry : taskLibrary.entrySet()) {
            MenuItem menuItem = new MenuItem(entry.getKey());
            menuItem.setOnAction((ActionEvent t) -> {
                // get a new library
                squidProject.loadAndInitializeLibraryTask(squidProject.getTaskLibrary().get(menuItem.getText()));
                launchTaskManager();
            });
            selectSquid3TaskFromLibraryMenu.getItems().add(menuItem);
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

            saveSquidProjectMenuItem.setDisable(squidProject.getTask().getRatioNames().isEmpty());
            saveAsSquidProjectMenuItem.setDisable(false);
            closeSquidProjectMenuItem.setDisable(false);
            projectManagerMenuItem.setDisable(false);

            managePrawnFileMenu.setDisable(false);
            buildTaskLibraryMenu();
            manageTasksMenu.setDisable(false);
            manageRatiosMenu.setDisable(squidProject.getTask().getRatioNames().isEmpty());
            manageExpressionsMenu.setDisable(squidProject.getTask().getRatioNames().isEmpty());
            manageReportsMenu.setDisable(squidProject.getTask().getRatioNames().isEmpty());

            // log prawnFileFolderMRU
            squidPersistentState.setMRUPrawnFileFolderPath(squidProject.getPrawnFileHandler().getCurrentPrawnFileLocationFolder());

        } catch (IOException | RuntimeException iOException) {
            System.out.println("ProjectManager >>>>   " + iOException.getMessage());
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
        mainPane.getChildren().remove(expressionManagerUI);

        mainPane.getChildren().remove(reductionManagerUI);
        mainPane.getChildren().remove(reducedDataReportManagerUI);
        mainPane.getChildren().remove(topsoilPlotUI);

        saveSquidProjectMenuItem.setDisable(true);
        saveAsSquidProjectMenuItem.setDisable(true);
        closeSquidProjectMenuItem.setDisable(true);
        projectManagerMenuItem.setDisable(true);

        manageExpressionsMenu.setDisable(true);
        managePrawnFileMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageRatiosMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageReportsMenu.setDisable(true);

        // logo
        mainPane.getChildren().get(0).setVisible(true);

    }

    private void prepareForNewProject() {
        removeAllManagers();

        squidProject = new SquidProject();

        // this updates output folder for reports to current version
        CalamariFileUtilities.initCalamariReportsFolder(squidProject.getPrawnFileHandler());

    }

    @FXML
    private void newSquidProjectAction(ActionEvent event) {
        prepareForNewProject();

        try {
            File prawnXMLFileNew = FileHandler.selectPrawnFile(primaryStageWindow);
            if (prawnXMLFileNew != null) {
                squidProject.setupPrawnFile(prawnXMLFileNew);
                launchProjectManager();
                saveSquidProjectMenuItem.setDisable(true);
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
        prepareForNewProject();

        SquidMessageDialog.showInfoDialog(
                "To join two Prawn XML files, be sure they are in the same folder, \n\tand then in the next dialog, choose both files."
                + "\n\nNotes: \n\t1) Joining will be done by comparing the timestamps of the first run in \n\t    each file to determine the order of join."
                + "\n\n\t2) The joined file will be written to disk and then read back in as a \n\t    check.  The name of the new file"
                + " will appear in the project manager's \n\t    text box for the Prawn XML file name.",
                primaryStageWindow);

        try {
            List<File> prawnXMLFilesNew = FileHandler.selectForJoinTwoPrawnFiles(primaryStageWindow);
            if (prawnXMLFilesNew.size() == 2) {
                squidProject.setupPrawnFileByJoin(prawnXMLFilesNew);
                launchProjectManager();
                saveSquidProjectMenuItem.setDisable(true);
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
                    buildProjectMenuMRU();
                }

            } catch (IOException ex) {
                saveSquidProjectMenuItem.setDisable(false);
            }
        }
    }

    @FXML
    private void openSquidProjectMenuItemAction(ActionEvent event) {
        removeAllManagers();

        try {
            String projectFileName = FileHandler.selectProjectFile(SquidUI.primaryStageWindow);
            openProject(projectFileName);
        } catch (IOException iOException) {
        }
    }

    private void openProject(String projectFileName) throws IOException {
        if (!"".equals(projectFileName)) {
            squidProject = (SquidProject) SquidSerializer.getSerializedObjectFromFile(projectFileName, true);
            if (squidProject != null) {
                squidPersistentState.updateProjectListMRU(new File(projectFileName));
                buildProjectMenuMRU();
                launchProjectManager();
                launchTaskManager();
                saveSquidProjectMenuItem.setDisable(false);
            } else {
                saveSquidProjectMenuItem.setDisable(true);
                throw new IOException();
            }
        }

        // this updates output folder for reports to current version
        CalamariFileUtilities.initCalamariReportsFolder(squidProject.getPrawnFileHandler());

    }

    @FXML
    private void closeSquidProjectMenuItemClose(ActionEvent event) {
        removeAllManagers();
    }

    @FXML
    private void saveSquidProjectMenuItemAction(ActionEvent event) {
        if (squidProject != null) {
            try {
                ProjectFileUtilities.serializeSquidProject(squidProject, squidPersistentState.getMRUProjectFile().getCanonicalPath());
            } catch (IOException iOException) {
            }
        }
    }

    @FXML
    private void quitAction(ActionEvent event) {
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
        } catch (IOException | RuntimeException iOException) {
            System.out.println("SessionAudit >>>>   " + iOException.getMessage());
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
        } catch (IOException | RuntimeException iOException) {
            System.out.println("MassesAudit >>>>   " + iOException.getMessage());
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
        } catch (IOException | RuntimeException iOException) {
            System.out.println("SpotManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchTaskManager() {
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
            manageRatiosMenu.setDisable(false);
            manageExpressionsMenu.setDisable(false);
            manageReportsMenu.setDisable(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("IsotopesManager >>>>   " + iOException.getMessage());
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
        } catch (IOException | RuntimeException iOException) {
            System.out.println("IsotopesManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchRatiosManager() {
        try {
            mainPane.getChildren().remove(ratiosManagerUI);
            // critical for puplating table
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

        } catch (IOException | RuntimeException iOException) {
            System.out.println("RatioManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchExpressionBuilder() {

        try {
            expressionBuilderUI = FXMLLoader.load(getClass().getResource("expressions/ExpressionBuilder.fxml"));
            expressionBuilderUI.setId("ExpressionBuilder");

            AnchorPane.setLeftAnchor(expressionBuilderUI, 0.0);
            AnchorPane.setRightAnchor(expressionBuilderUI, 0.0);
            AnchorPane.setTopAnchor(expressionBuilderUI, 0.0);
            AnchorPane.setBottomAnchor(expressionBuilderUI, 0.0);

            mainPane.getChildren().add(expressionBuilderUI);
            expressionBuilderUI.setVisible(false);

        } catch (IOException ex) {
            Logger.getLogger(SquidUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void launchExpressionManager() {
        mainPane.getChildren().remove(expressionManagerUI);

        try {
            expressionManagerUI = FXMLLoader.load(getClass().getResource("expressions/ExpressionManager.fxml"));
            expressionManagerUI.setId("ExpressionManager");
            VBox.setVgrow(expressionManagerUI, Priority.ALWAYS);
            HBox.setHgrow(expressionManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(expressionManagerUI);
            expressionManagerUI.setVisible(false);

            showUI(expressionManagerUI);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("expressionManagerUI >>>>   " + iOException.getMessage());
        }
    }

    private void launchReductionManager() {
        try {
            reductionManagerUI = FXMLLoader.load(getClass().getResource("dataReduction/ReductionManager.fxml"));
            reductionManagerUI.setId("ReductionManager");

            AnchorPane.setLeftAnchor(reductionManagerUI, 0.0);
            AnchorPane.setRightAnchor(reductionManagerUI, 0.0);
            AnchorPane.setTopAnchor(reductionManagerUI, 0.0);
            AnchorPane.setBottomAnchor(reductionManagerUI, 0.0);

            mainPane.getChildren().add(reductionManagerUI);
            reductionManagerUI.setVisible(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("ReductionManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchReducedDataReportManager() {
        try {
            reducedDataReportManagerUI = FXMLLoader.load(getClass().getResource("dataReduction/reducedDataReportManager.fxml"));
            reducedDataReportManagerUI.setId("reducedDataReportManagerUI");

            AnchorPane.setLeftAnchor(reducedDataReportManagerUI, 0.0);
            AnchorPane.setRightAnchor(reducedDataReportManagerUI, 0.0);
            AnchorPane.setTopAnchor(reducedDataReportManagerUI, 0.0);
            AnchorPane.setBottomAnchor(reducedDataReportManagerUI, 0.0);

            mainPane.getChildren().add(reducedDataReportManagerUI);
            reducedDataReportManagerUI.setVisible(false);

            showUI(reducedDataReportManagerUI);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("reducedDataReportManagerUI >>>>   " + iOException.getMessage());
        }
    }

    @FXML
    private void projectManagerMenuItemAction(ActionEvent event) {
        launchProjectManager();
    }

    private void showUI(Node myManager) {
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

    private void reduceDataMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(reductionManagerUI);
        launchReductionManager();
        showUI(reductionManagerUI);
    }

    @FXML
    private void manageTaskMenuItemAction(ActionEvent event) {
//        mainPane.getChildren().remove(taskManagerUI);
        launchTaskManager();
    }

    @FXML
    private void savePrawnFileCopyMenuItemAction(ActionEvent event) {
        try {
            File prawnXMLFileNew = FileHandler.savePrawnFile(squidProject, primaryStageWindow);
            if (prawnXMLFileNew != null) {
                squidProject.setupPrawnFile(prawnXMLFileNew);
                launchProjectManager();
            }
        } catch (IOException | JAXBException | SAXException | SquidException iOException) {
        }
    }

    @FXML
    private void newSquid3TaskMenuItemAction(ActionEvent event) {
        squidProject.createNewTask();
        launchTaskManager();
    }

    @FXML
    private void importSquid25TaskMenuItemAction(ActionEvent event) {
        try {
            File squidTaskFile = FileHandler.selectSquid25TaskFile(squidProject, primaryStageWindow);
            if (squidTaskFile != null) {
                squidProject.createTaskFromImportedSquid25Task(squidTaskFile);
                launchTaskManager();
            }
        } catch (SquidException | IOException | JAXBException | SAXException iOException) {
        }
    }

    @FXML
    private void exportSquid3TaskMenuItemAction(ActionEvent event) {
    }

    @FXML
    private void importSquid3TaskMenuItemAction(ActionEvent event) {
    }

    @FXML
    private void manageExpressionsMenuItemAction(ActionEvent event) {
        launchExpressionManager();
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
        squidProject.getTask().removeExpression(exp);
        squidProject.getTask().addExpression(exp);
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
    private void produceSanityCheckReportsAction(ActionEvent event) {
        squidProject.getTask().produceSanityReportsToFiles();
    }

    @FXML
    private void reportsMenuSelectedAction(Event event) {
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport();
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

    @FXML
    private void topsoilAction(ActionEvent event) {
        mainPane.getChildren().remove(topsoilPlotUI);

        AbstractTopsoilPlot topsoilPlot = new TopsoilPlotWetherill("Example Wetherill using CM2 data");

        topsoilPlotUI = topsoilPlot.initializePlotPane();
        topsoilPlotUI.setId("topsoilPlotUI");
        VBox.setVgrow(topsoilPlotUI, Priority.ALWAYS);
        HBox.setHgrow(topsoilPlotUI, Priority.ALWAYS);
        mainPane.getChildren().add(topsoilPlotUI);
        topsoilPlotUI.setVisible(false);
        showUI(topsoilPlotUI);
    }

    @FXML
    private void topsoilAction2(ActionEvent event) {
        if (topsoilWindows != null) {
            for (int i = 0; i < 6; i++) {
                topsoilWindows[i].close();
            }
        }
        topsoilWindows = new TopsoilWindow[6];
        for (int i = 0; i < 6; i++) {
            AbstractTopsoilPlot topsoilPlot = new TopsoilPlotWetherill("Squid Test Plot #" + i);
            topsoilWindows[i] = new TopsoilWindow(topsoilPlot);
            topsoilWindows[i].loadTopsoilWindow(i * 40, 100);
        }

        topsoilWindows[3].getTopsoilPlot().getPlot().getProperties().put(TITLE, "Testing Handle");

    }

    @FXML
    private void expressionBuilderMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(expressionBuilderUI);
        launchExpressionBuilder();
        showUI(expressionBuilderUI);
    }

    @FXML
    private void ludwigLibraryJavaDocAction(ActionEvent event) {
        BrowserControl.showURI(DEFAULT_LUDWIGLIBRARY_JAVADOC_FOLDER + File.separator + "index.html");
    }

}
