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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.core.PrawnFileHandler;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.fileManagement.CalamariFileManager;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.gui.utilities.BrowserControl;
import static org.cirdles.squid.gui.utilities.BrowserControl.urlEncode;
import org.cirdles.squid.utilities.SquidSerializer;
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

    private Pane projectManagerUI;
    @FXML
    private MenuItem saveSquidProjectMenuItem;
    @FXML
    private MenuItem newSquidProjectByMergeMenuItem;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        manageExpressionsMenu.setDisable(true);
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

        CalamariFileManager.initExamplePrawnFiles();
    }

    private void launchProjectManager() {

        try {
            // prevent stacking of project panes
            mainPane.getChildren().remove(projectManagerUI);

            projectManagerUI = FXMLLoader.load(getClass().getResource("ProjectManager.fxml"));
            projectManagerUI.setId("ProjectManager");
            VBox.setVgrow(projectManagerUI, Priority.ALWAYS);
            HBox.setHgrow(projectManagerUI, Priority.ALWAYS);
            mainPane.getChildren().add(projectManagerUI);
            projectManagerUI.setVisible(true);

            saveAsSquidProjectMenuItem.setDisable(false);
            closeSquidProjectMenuItem.setDisable(false);

        } catch (IOException | RuntimeException iOException) {
            System.out.println(">>>>   " + iOException.getMessage());
        }

    }

    @FXML
    private void newSquidProjectAction(ActionEvent event) {
        squidProject = new SquidProject();

        CalamariFileManager.initProjectFiles(squidProject.getPrawnFileHandler(), "1.4.5");

        try {
            if (squidProject.selectPrawnFile(primaryStageWindow)) {
                launchProjectManager();
            }
        } catch (IOException | JAXBException | SAXException anException) {
            SquidMessageDialog.showWarningDialog("Squid encountered an error while trying to open the selected file(s).");
        }
    }

    @FXML
    private void newSquidProjectByMergeAction(ActionEvent event) {
        SquidMessageDialog.showInfoDialog("Coming soon!");
    }

    @FXML
    private void saveAsSquidProjectMenuItemAction(ActionEvent event) {
        if (squidProject != null) {
            ProjectManagerController.saveProjectData();
            try {
                squidProject.saveProjectFile(SquidUI.primaryStageWindow);
            } catch (IOException ex) {
            }
        }
    }

    @FXML
    private void openSquidProjectMenuItemAction(ActionEvent event) {
        try {
            String projectFileName = SquidProject.selectProjectFile(SquidUI.primaryStageWindow);
            if (!"".equals(projectFileName)) {
                squidProject = (SquidProject) SquidSerializer.GetSerializedObjectFromFile(projectFileName);
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
        mainPane.getChildren().remove(projectManagerUI);

        saveAsSquidProjectMenuItem.setDisable(true);
        closeSquidProjectMenuItem.setDisable(true);
        projectManagerMenuItem.setDisable(true);

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
        BrowserControl.showURI("https://github.com/CIRDLES/Squid/wiki");
    }

    @FXML
    private void aboutSquidAction(ActionEvent event) {
        SquidUI.squidAboutWindow.loadAboutWindow();
    }

    @FXML
    private void contributeIssueOnGitHubAction(ActionEvent event) {
        String version = "Squid3 Version: " + SquidUI.VERSION;
        String javaVersion = "Java Version: " + System.getProperties().getProperty("java.version");
        String operatingSystem = "OS: " + System.getProperties().getProperty("os.name") + " " + System.getProperties().getProperty("os.version");

        StringBuilder issueBody = new StringBuilder();
        issueBody.append(urlEncode(version + "\n"));
        issueBody.append(urlEncode(javaVersion + "\n"));
        issueBody.append(urlEncode(operatingSystem + "\n"));
        issueBody.append(urlEncode("\n\nIssue details:\n"));

        BrowserControl.showURI("https://github.com/CIRDLES/Squid/issues/new?body=" + issueBody.toString());
    }

}
