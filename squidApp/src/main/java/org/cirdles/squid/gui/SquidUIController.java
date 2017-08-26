
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
import java.util.ArrayList;
import java.util.List;
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
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.gui.utilities.BrowserControl;
import static org.cirdles.squid.gui.utilities.BrowserControl.urlEncode;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
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
    private Menu openRecentSquidProjectMenuItem;
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
    private static Pane expressionManagerUI;
    private static Pane analysisManagerUI;
    @FXML
    private MenuItem newSquid3TaskMenuItem;
    @FXML
    private MenuItem importSquid25TaskMenuItem;
    @FXML
    private MenuItem exportSquid3TaskMenuItem;
    @FXML
    private MenuItem selectSquid3TaskFromLibraryMenuItem;

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

        manageExpressionsMenu.setDisable(true);
        managePrawnFileMenu.setDisable(true);
        manageRatiosMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageAnalysisMenu.setDisable(false);
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
        newSquid3TaskMenuItem.setDisable(true);
        selectSquid3TaskFromLibraryMenuItem.setDisable(true);
        importSquid25TaskMenuItem.setDisable(true);
        exportSquid3TaskMenuItem.setDisable(true);

        CalamariFileUtilities.initExamplePrawnFiles();
        CalamariFileUtilities.loadShrimpPrawnFileSchema();
    }

    private void buildProjectMenuMRU() {
        openRecentSquidProjectMenuItem.setDisable(false);

        openRecentSquidProjectMenuItem.getItems().clear();
        ArrayList<String> mruProjectList = squidPersistentState.getMRUProjectList();
        for (String projectFileName : mruProjectList) {
            MenuItem menuItem = new MenuItem(projectFileName);
            menuItem.setOnAction((ActionEvent t) -> {
                try {
                    openProject(menuItem.getText());
                } catch (IOException iOException) {
                    squidPersistentState.cleanProjectListMRU();
                    // remove self safe?
                    openRecentSquidProjectMenuItem.getItems().remove(menuItem);
                }
            });
            openRecentSquidProjectMenuItem.getItems().add(menuItem);
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
            manageTasksMenu.setDisable(false);
            manageRatiosMenu.setDisable(false);
            manageExpressionsMenu.setDisable(false);
//            manageAnalysisMenu.setDisable(false);

            // log prawnFileFolderMRU
            squidPersistentState.setMRUPrawnFileFolderPath(squidProject.getPrawnFileHandler().getCurrentPrawnFileLocationFolder());

        } catch (IOException | RuntimeException iOException) {
            System.out.println(">>>>   " + iOException.getMessage());
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
        mainPane.getChildren().remove(expressionManagerUI);
        mainPane.getChildren().remove(analysisManagerUI);

        saveSquidProjectMenuItem.setDisable(true);
        saveAsSquidProjectMenuItem.setDisable(true);
        closeSquidProjectMenuItem.setDisable(true);
        projectManagerMenuItem.setDisable(true);

        manageExpressionsMenu.setDisable(true);
        managePrawnFileMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageRatiosMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageAnalysisMenu.setDisable(false);
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
            File prawnXMLFileNew = FileHandler.selectPrawnFile(squidPersistentState.getMRUPrawnFileFolderPath(), primaryStageWindow);
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
            List<File> prawnXMLFilesNew = FileHandler.selectForJoinTwoPrawnFiles(squidPersistentState.getMRUPrawnFileFolderPath(), primaryStageWindow);
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
            SpotManagerController.saveProjectData();
            try {
                File projectFile = FileHandler.saveProjectFile(squidProject, SquidUI.primaryStageWindow);
                saveSquidProjectMenuItem.setDisable(false);
                squidPersistentState.updateProjectListMRU(projectFile);
                buildProjectMenuMRU();

            } catch (IOException ex) {
            }
        }
    }

    @FXML
    private void openSquidProjectMenuItemAction(ActionEvent event) {
        removeAllManagers();

        try {
            String projectFileName = FileHandler.selectProjectFile(squidPersistentState.getMRUProjectFolderPath(), SquidUI.primaryStageWindow);
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
            SpotManagerController.saveProjectData();
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

    private void launchExpressionManager() {

        try {
            expressionManagerUI = FXMLLoader.load(getClass().getResource("ExpressionManager.fxml"));
            expressionManagerUI.setId("ExpressionManager");
            VBox.setVgrow(expressionManagerUI, Priority.ALWAYS);
            HBox.setHgrow(expressionManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(expressionManagerUI);
            expressionManagerUI.setVisible(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("ExpressionManager >>>>   " + iOException.getMessage());
        }
    }

    private void launchAnalysisManager() {
        try {
            analysisManagerUI = FXMLLoader.load(getClass().getResource("AnalysisManager.fxml"));
            analysisManagerUI.setId("AnalysisManager");
            VBox.setVgrow(analysisManagerUI, Priority.ALWAYS);
            HBox.setHgrow(analysisManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(analysisManagerUI);
            analysisManagerUI.setVisible(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println("AnalysisManager >>>>   " + iOException.getMessage());
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
    private void exploreExpressionsMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(expressionManagerUI);
        launchExpressionManager();
        showUI(expressionManagerUI);
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
        squidProject.createMapOfIndexToMassStationDetails();
        launchMassesAudit();
        showUI(massesAuditUI);
    }

    @FXML
    private void specifyIsotopesMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(isotopesManagerUI);
        squidProject.createMapOfIndexToMassStationDetails();
        launchIsotopesManager();
        showUI(isotopesManagerUI);
    }

    @FXML
    private void selectRatiosMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(ratiosManagerUI);
        launchRatiosManager();
        showUI(ratiosManagerUI);
    }

    @FXML
    private void reduceDataMenuItemAction(ActionEvent event) {
        mainPane.getChildren().remove(analysisManagerUI);
        launchAnalysisManager();
        showUI(analysisManagerUI);
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
    }

    @FXML
    private void importSquid25TaskMenuItemAction(ActionEvent event) {
        try {
            File squidTaskFile = FileHandler.selectSquid25TaskFile(squidProject, primaryStageWindow);
            if (squidTaskFile != null) {
                squidProject.setupTaskSquid25File(squidTaskFile);
                SquidUIController.squidProject.extractTask25Ratios();
                launchTaskManager();
            }
        } catch (IOException | JAXBException | SAXException iOException) {
        }
    }

    @FXML
    private void exportSquid3TaskMenuItemAction(ActionEvent event) {
    }

    @FXML
    private void selectSquid3TaskFromLibraryMenuItemAction(ActionEvent event) {
    }

}
