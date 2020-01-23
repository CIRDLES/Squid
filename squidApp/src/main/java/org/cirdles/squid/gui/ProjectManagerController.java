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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;
import org.cirdles.squid.utilities.squidPrefixTree.SquidPrefixTree;

/**
 * FXML Controller class
 *
 * @see
 * <a href="https://courses.bekwam.net/public_tutorials/bkcourse_filterlistapp.html" target="_blank">Bekwam.net</a>
 * @author James F. Bowring
 */
public class ProjectManagerController implements Initializable {

    @FXML
    private TextField orignalPrawnFileName;
    @FXML
    private Label softwareVersionLabel;
    @FXML
    private Label summaryStatsLabel;
    @FXML
    private Label totalAnalysisTimeLabel;
    @FXML
    private TextField projectNameText;
    @FXML
    private TextField analystNameText;
    @FXML
    private TextArea projectNotesText;
    @FXML
    private Label loginCommentLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private GridPane projectManagerGridPane;
    @FXML
    private Label softwareVersionLabel1;
    @FXML
    private TextField squidFileNameText;


    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        orignalPrawnFileName.setEditable(false);

        setupListeners();

        // detect if project opened from menu by deserialization
        if (squidProject.prawnFileExists()) {
            setUpPrawnFile();
        }
        
        titleLabel.setStyle(STYLE_MANAGER_TITLE);
    }

    private void setupListeners() {
        projectNameText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                squidProject.setProjectName(newValue);
            }
        });

        analystNameText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                squidProject.setAnalystName(newValue);
            }
        });

        projectNotesText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                squidProject.setProjectNotes(newValue);
            }
        });
    }

    private void setUpPrawnFile() {
        // first prepare the session
        squidProject.preProcessPrawnSession();

        projectNameText.setText(squidProject.getProjectName());
        analystNameText.setText(squidProject.getAnalystName());
        projectNotesText.setText(squidProject.getProjectNotes());

        orignalPrawnFileName.setText(squidProject.getPrawnSourceFilePath());
        
        squidFileNameText.setText(SquidUIController.projectFileName);

        softwareVersionLabel.setText(
                "Version: "
                + squidProject.getPrawnFileShrimpSoftwareVersionName());
        
        loginCommentLabel.setText(
                "Login Comment: "
                + squidProject.getPrawnFileLoginComment());

        extractSummaryStatsFromPrawnFile();
    }

    private void extractSummaryStatsFromPrawnFile() {

        SquidPrefixTree spotPrefixTree = squidProject.generatePrefixTreeFromSpotNames();

        String summaryStatsString = spotPrefixTree.buildSummaryDataString();

        // format into rows for summary tab
        summaryStatsLabel.setText("Summary:\n\t" + summaryStatsString.replaceAll(";", "\n\t"));

        totalAnalysisTimeLabel.setText("Total time in hours = " + (int) squidProject.getSessionDurationHours());
    }

}
