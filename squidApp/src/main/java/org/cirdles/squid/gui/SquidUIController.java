
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
package org.cirdles.squid.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javax.xml.bind.JAXBException;
import org.cirdles.squid.core.PrawnFileHandler;
import org.cirdles.squid.Squid;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.gui.utilities.BrowserControl;
import static org.cirdles.squid.gui.utilities.BrowserControl.urlEncode;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.utilities.fileUtilities.ProjectFileUtilities;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
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
    private Menu manageAnalysisMenu;
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
    private Pane mainPane;
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

    private static Pane projectManagerUI;

    private static Pane sessionAuditUI;
    private static Pane massesAuditUI;
    private static Pane spotManagerUI;

    private static Pane taskManagerUI;

    private static Pane isotopesManagerUI;
    private static Pane ratiosManagerUI;

    private static Pane expressionExplorerUI;
    private static Pane expressionManagerUI;

    private static Pane reductionManagerUI;
    private static Pane reducedUnknownsReportManagerUI;

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
    private Menu dataReductionMenu;
    @FXML
    private Menu openRecentExpressionFileMenu;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // center Logo
        squidImageView.layoutXProperty().bind(primaryStageWindow.getScene().widthProperty()
                .divide(2).subtract(squidImageView.getFitWidth() / 2));
        squidImageView.layoutYProperty().bind(primaryStageWindow.getScene().heightProperty()
                .divide(2).subtract(squidImageView.getFitHeight() / 2).subtract(60));

        managePrawnFileMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageRatiosMenu.setDisable(true);
        dataReductionMenu.setDisable(true);
        manageExpressionsMenu.setDisable(true);
        manageAnalysisMenu.setDisable(true);
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
                squidProject.loadAndInitializeTask(squidProject.getTaskLibrary().get(menuItem.getText()));
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
            }
            );
            openRecentExpressionFileMenu.getItems()
                    .add(menuItem);
        }
    }

    private void launchProjectManager() {

        try {
            removeAllManagers();

            projectManagerUI = FXMLLoader.load(getClass().getResource("ProjectManager.fxml"));
            projectManagerUI.setId("ProjectManager");
            VBox.setVgrow(projectManagerUI, Priority.ALWAYS);
            HBox.setHgrow(projectManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(projectManagerUI);
            projectManagerUI.setVisible(true);

            saveSquidProjectMenuItem.setDisable(true);
            saveAsSquidProjectMenuItem.setDisable(false);
            closeSquidProjectMenuItem.setDisable(false);
            projectManagerMenuItem.setDisable(false);

            managePrawnFileMenu.setDisable(false);
            buildTaskLibraryMenu();
            manageTasksMenu.setDisable(false);
            manageRatiosMenu.setDisable(true);
            manageExpressionsMenu.setDisable(true);
            dataReductionMenu.setDisable(true);
            manageAnalysisMenu.setDisable(true);

            // log prawnFileFolderMRU
            squidPersistentState.setMRUPrawnFileFolderPath(squidProject.getPrawnFileHandler().getCurrentPrawnFileLocationFolder());

        } catch (IOException | RuntimeException iOException) {
            System.out.println("ProjectManager >>>>   " + iOException.getMessage());
        }

    }

    private void removeAllManagers() {
        // prevent stacking of project panes
        mainPane.getChildren().remove(projectManagerUI);

        mainPane.getChildren().remove(sessionAuditUI);
        mainPane.getChildren().remove(massesAuditUI);
        mainPane.getChildren().remove(spotManagerUI);

        mainPane.getChildren().remove(taskManagerUI);

        mainPane.getChildren().remove(isotopesManagerUI);
        mainPane.getChildren().remove(ratiosManagerUI);

        mainPane.getChildren().remove(expressionExplorerUI);
        mainPane.getChildren().remove(expressionManagerUI);

        mainPane.getChildren().remove(reductionManagerUI);
        mainPane.getChildren().remove(reducedUnknownsReportManagerUI);

        saveSquidProjectMenuItem.setDisable(true);
        saveAsSquidProjectMenuItem.setDisable(true);
        closeSquidProjectMenuItem.setDisable(true);
        projectManagerMenuItem.setDisable(true);

        manageExpressionsMenu.setDisable(true);
        managePrawnFileMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageRatiosMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        dataReductionMenu.setDisable(true);
        manageAnalysisMenu.setDisable(true);
        manageReportsMenu.setDisable(true);

        // logo
        mainPane.getChildren().get(0).setVisible(true);

    }

    private void prepareForNewProject() {
        removeAllManagers();

        squidProject = new SquidProject();

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
        } catch (IOException | JAXBException | SAXException anException) {
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
        } catch (IOException | JAXBException | SAXException anException) {
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
                squidProject.setPrawnFileHandler(new PrawnFileHandler());
                squidProject.updatePrawnFileHandlerWithFileLocation();
                squidPersistentState.updateProjectListMRU(new File(projectFileName));
                buildProjectMenuMRU();
                launchProjectManager();
                saveSquidProjectMenuItem.setDisable(false);
            } else {
                saveSquidProjectMenuItem.setDisable(true);
                throw new IOException();
            }
        }
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
            VBox.setVgrow(sessionAuditUI, Priority.ALWAYS);
            HBox.setHgrow(sessionAuditUI, Priority.ALWAYS);
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
            VBox.setVgrow(massesAuditUI, Priority.ALWAYS);
            HBox.setHgrow(massesAuditUI, Priority.ALWAYS);
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
            VBox.setVgrow(spotManagerUI, Priority.ALWAYS);
            HBox.setHgrow(spotManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(spotManagerUI);
            spotManagerUI.setVisible(false);
        } catch (IOException | RuntimeException iOException) {
            System.out.println("SpotManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchTaskManager() {
        try {
            taskManagerUI = FXMLLoader.load(getClass().getResource("TaskManager.fxml"));
            taskManagerUI.setId("TaskManager");
            VBox.setVgrow(taskManagerUI, Priority.ALWAYS);
            HBox.setHgrow(taskManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(taskManagerUI);
            showUI(taskManagerUI);
            manageRatiosMenu.setDisable(false);
            manageExpressionsMenu.setDisable(false);
            dataReductionMenu.setDisable(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("taskManagerUI >>>>   " + iOException.getMessage());
        }
    }

    private void launchIsotopesManager() {
        try {
            isotopesManagerUI = FXMLLoader.load(getClass().getResource("IsotopesManager.fxml"));
            isotopesManagerUI.setId("IsotopesManager");
            VBox.setVgrow(isotopesManagerUI, Priority.ALWAYS);
            HBox.setHgrow(isotopesManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(isotopesManagerUI);
            isotopesManagerUI.setVisible(false);
        } catch (IOException | RuntimeException iOException) {
            System.out.println("IsotopesManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchRatiosManager() {
        try {
            ratiosManagerUI = FXMLLoader.load(getClass().getResource("RatiosManager.fxml"));
            ratiosManagerUI.setId("RatiosManager");
            VBox.setVgrow(ratiosManagerUI, Priority.ALWAYS);
            HBox.setHgrow(ratiosManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(ratiosManagerUI);
            ratiosManagerUI.setVisible(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("RatioManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchExpressionExplorer() {

        try {
            expressionExplorerUI = FXMLLoader.load(getClass().getResource("ExpressionExplorer.fxml"));
            expressionExplorerUI.setId("ExpressionExplorer");
            VBox.setVgrow(expressionExplorerUI, Priority.ALWAYS);
            HBox.setHgrow(expressionExplorerUI, Priority.ALWAYS);
            mainPane.getChildren().add(expressionExplorerUI);
            expressionExplorerUI.setVisible(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("ExpressionExplorer >>>>   " + iOException.getMessage());
        }
    }

    private void launchExpressionManager() {
        mainPane.getChildren().remove(expressionManagerUI);

        try {
            expressionManagerUI = FXMLLoader.load(getClass().getResource("ExpressionManager.fxml"));
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
            VBox.setVgrow(reductionManagerUI, Priority.ALWAYS);
            HBox.setHgrow(reductionManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(reductionManagerUI);
            reductionManagerUI.setVisible(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("ReductionManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchReducedUnknownsReportManager() {
        try {
            reducedUnknownsReportManagerUI = FXMLLoader.load(getClass().getResource("dataReduction/ReducedUnknownsReport.fxml"));
            reducedUnknownsReportManagerUI.setId("ReducedUnknownsReport");
            VBox.setVgrow(reducedUnknownsReportManagerUI, Priority.ALWAYS);
            HBox.setHgrow(reducedUnknownsReportManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(reducedUnknownsReportManagerUI);
            reducedUnknownsReportManagerUI.setVisible(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("ReducedUnknownsReport >>>>   " + iOException.getMessage());
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

    private void exploreExpressionsMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(expressionExplorerUI);
        launchExpressionExplorer();
        showUI(expressionExplorerUI);
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
        mainPane.getChildren().remove(ratiosManagerUI);
        squidProject.getTask().buildSquidSpeciesModelList();
        launchRatiosManager();
        showUI(ratiosManagerUI);
    }

    @FXML
    private void reduceDataMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(reductionManagerUI);
        launchReductionManager();
        showUI(reductionManagerUI);
    }

    @FXML
    private void manageTaskMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(taskManagerUI);
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
        } catch (IOException | JAXBException | SAXException iOException) {
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
    private void showReferenceMaterialDataMenutItemAction(ActionEvent event) {
    }

    @FXML
    private void showUnknownDataMenuItemAction(ActionEvent event) {
        launchReducedUnknownsReportManager();
        showUI(reducedUnknownsReportManagerUI);
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
                retVal = true;
                squidProject.getTask().addExpression(exp);
                squidPersistentState.updateExpressionListMRU(expressionFileXML);
                buildExpressionMenuMRU();
                launchExpressionManager();
            }
        }
        return retVal;
    }

}
