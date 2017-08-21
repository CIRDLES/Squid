/*
 * Copyright 2017 CIRDLES.org.
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
import javafx.scene.layout.Pane;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.utilities.SquidPrefixTree;

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
    private Pane mainProjectManagerPane;
    @FXML
    private TextArea projectNotesText;
    @FXML
    private Label loginCommentLabel;



    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        mainProjectManagerPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        mainProjectManagerPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        orignalPrawnFileName.setEditable(false);

        setupListeners();

        // detect if project opened from menu by deserialization
        if (squidProject.prawnFileExists()) {
            setUpPrawnFile();
        }
    }

    private void setupListeners() {
        projectNameText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                squidProject.setProjectName(newValue);
                SquidUI.updateStageTitle("Squid 3.0 pre-release" + "  [Project: " + newValue + "]");
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

        orignalPrawnFileName.setText(squidProject.getPrawnXMLFileName());

        softwareVersionLabel.setText(
                "Software Version: "
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
        summaryStatsLabel.setText("Session summary:\n\t" + summaryStatsString.replaceAll(";", "\n\t"));

        totalAnalysisTimeLabel.setText("Total session time in hours = " + (int) squidProject.getSessionDurationHours());
        
        squidProject.setupSquidSessionSpecs();
    }

}
