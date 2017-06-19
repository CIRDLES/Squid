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

import java.io.IOException;
import java.net.URL;
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
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
import org.xml.sax.SAXException;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class SquidUIController implements Initializable {

    public static SquidProject squidProject;

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
    private MenuItem newSquidProjectByMergeMenuItem;

    @FXML
    private ImageView squidImageView;

    private static Pane projectManagerUI;
    private static Pane sessionAuditUI;
    private static Pane massesAuditUI;
    private static Pane spotManagerUI;
    private static Pane ratioManagerUI;
    private static Pane expressionManagerUI;
    @FXML
    private Menu managePrawnFileMenu;

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
        manageAnalysisMenu.setDisable(true);
        manageReportsMenu.setDisable(true);

        // Squid project menu items
        newSquidProjectMenuItem.setDisable(false);
        newSquidProjectByMergeMenuItem.setDisable(false);
        openSquidProjectMenuItem.setDisable(false);
        openRecentSquidProjectMenuItem.setDisable(true);
        saveSquidProjectMenuItem.setDisable(true);
        saveAsSquidProjectMenuItem.setDisable(true);
        projectManagerMenuItem.setDisable(true);
        closeSquidProjectMenuItem.setDisable(true);

        CalamariFileUtilities.initExamplePrawnFiles();
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

            saveAsSquidProjectMenuItem.setDisable(false);
            closeSquidProjectMenuItem.setDisable(false);
            projectManagerMenuItem.setDisable(false);

            managePrawnFileMenu.setDisable(false);
            manageRatiosMenu.setDisable(false);
            manageExpressionsMenu.setDisable(false);

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
        mainPane.getChildren().remove(ratioManagerUI);
        mainPane.getChildren().remove(expressionManagerUI);

        saveAsSquidProjectMenuItem.setDisable(true);
        closeSquidProjectMenuItem.setDisable(true);
        projectManagerMenuItem.setDisable(true);

        manageExpressionsMenu.setDisable(true);
        manageRatiosMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
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
            if (FileHandler.selectPrawnFile(squidProject, primaryStageWindow)) {
                launchProjectManager();
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
    private void newSquidProjectByMergeAction(ActionEvent event) {
        prepareForNewProject();

        SquidMessageDialog.showInfoDialog(
                "To merge two Prawn XML files, be sure they are in the same folder, \n\tand then in the next dialog, choose both files."
                + "\n\nNotes: \n\t1) Joining will be done by comparing the timestamps of the first run in \n\t    each file to determine the order of join."
                + "\n\n\t2) The joined file will be written to disk and then read back in as a \n\t    check.  The name of the new file"
                + " will appear in the project manager's \n\t    text box for the Prawn XML file name.",
                primaryStageWindow);

        try {
            if (FileHandler.selectForMergeTwoPrawnFiles(squidProject, primaryStageWindow)) {
                launchProjectManager();
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
                FileHandler.saveProjectFile(squidProject, SquidUI.primaryStageWindow);
            } catch (IOException ex) {
            }
        }
    }

    @FXML
    private void openSquidProjectMenuItemAction(ActionEvent event) {
        removeAllManagers();

        try {
            String projectFileName = FileHandler.selectProjectFile(SquidUI.primaryStageWindow);
            if (!"".equals(projectFileName)) {
                squidProject = (SquidProject) SquidSerializer.getSerializedObjectFromFile(projectFileName);
                if (squidProject != null) {
                    squidProject.setPrawnFileHandler(new PrawnFileHandler());
                    squidProject.updatePrawnFileHandlerWithFileLocation();
                    launchProjectManager();
                }
            }
        } catch (IOException iOException) {
        }
    }

    @FXML
    private void closeSquidProjectMenuItemClose(ActionEvent event) {
        removeAllManagers();
    }

    @FXML
    private void saveSquidProjectMenuItemAction(ActionEvent event) {
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

    private void launchRatioManager() {
        try {
            ratioManagerUI = FXMLLoader.load(getClass().getResource("RatioManager.fxml"));
            ratioManagerUI.setId("RatioManager");
            VBox.setVgrow(ratioManagerUI, Priority.ALWAYS);
            HBox.setHgrow(ratioManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(ratioManagerUI);
            ratioManagerUI.setVisible(false);

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
            System.out.println(">>>>   " + iOException.getMessage());
        }
    }

    @FXML
    private void projectManagerMenuItemAction(ActionEvent event) {
        showUI(projectManagerUI);
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
        mainPane.getChildren().remove(ratioManagerUI);
        launchRatioManager();
        showUI(ratioManagerUI);
    }

    @FXML
    private void selectRatiosMenuItemAction(ActionEvent event) {
    }

}
