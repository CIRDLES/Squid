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

import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;

import java.net.URL;
import java.util.ResourceBundle;

import static org.cirdles.squid.gui.SquidUI.HEALTHY_EXPRESSION_STYLE;
import static org.cirdles.squid.gui.SquidUI.UNHEALTHY_EXPRESSION_STYLE;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import static org.cirdles.squid.tasks.expressions.Expression.makeExpressionForAudit;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class TaskManagerController implements Initializable {

    private TaskInterface task;

    @FXML
    private TextField provenanceTextField;
    @FXML
    private GridPane taskManagerGridPane;
    @FXML
    private TextField taskNameTextField;
    @FXML
    private TextField authorsNameTextField;
    @FXML
    private TextField labNameTextField;
    @FXML
    private TextField taskDescriptionTextField;
    @FXML
    private TextArea taskAuditTextArea;
    @FXML
    private Label titleLabel;
    @FXML
    private ToggleGroup primaryAgeToggleGroup;
    @FXML
    private ToggleGroup dirctALTtoggleGroup;
    @FXML
    private Label uncorrConstPbUlabel;
    @FXML
    private Label uncorrConstPbThlabel;
    @FXML
    private Label uncorrConstPbUExpressionLabel;
    @FXML
    private Label uncorrConstPbThExpressionLabel;
    @FXML
    private Label th232U238Label;
    @FXML
    private Label th232U238ExpressionLabel;
    @FXML
    private Label parentConcExpressionLabel;
    @FXML
    private Label parentConcLabel;
    @FXML
    private GridPane directivesGridPane;
    @FXML
    private Label projectModeLabel;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (squidProject.getTask() != null) {
            task = squidProject.getTask();
            // Sept 2020 v1.5.8 this is true USE_SIG_FIG_15 = task.isRoundingForSquid3();
            task.setupSquidSessionSpecsAndReduceAndReport(false);
            projectModeLabel.setText(squidProject.getProjectType().getProjectName() + " Mode");
            populateTaskFields();
            setupListeners();
        } else {
            taskAuditTextArea.setText("No Task information available");
        }

        titleLabel.setStyle(STYLE_MANAGER_TITLE);
    }

    private void populateTaskFields() {

        taskNameTextField.setText(task.getName());
        taskDescriptionTextField.setText(task.getDescription());
        authorsNameTextField.setText(task.getAuthorName());
        labNameTextField.setText(task.getLabName());
        provenanceTextField.setText(task.getProvenance());

        taskAuditTextArea.setText(task.printTaskAudit());

        if (!squidProject.isTypeGeochron()) {
            int indexChildDirectives = taskManagerGridPane.getChildren().indexOf(directivesGridPane);
            taskManagerGridPane.getChildren().get(indexChildDirectives).setVisible(false);
        }

        populateDirectives();
    }

    @FXML
    private void editCurrentTaskAction(ActionEvent event) {
        MenuItem menuItemTaskEditorHidden = ((MenuBar) SquidUI.primaryStage.getScene()
                .getRoot().getChildrenUnmodifiable().get(0)).getMenus().get(2).getItems().get(1);
        menuItemTaskEditorHidden.fire();
    }

    @FXML
    private void saveCurrentTaskAction(ActionEvent event) {
        try {
            FileHandler.saveTaskFileXML(squidProject.getTask(), SquidUI.primaryStageWindow);
        } catch (IOException iOException) {
        }
        
        // refresh view
        MenuItem menuItemTaskViewer = ((MenuBar) SquidUI.primaryStage.getScene()
                .getRoot().getChildrenUnmodifiable().get(0)).getMenus().get(2).getItems().get(0);
        menuItemTaskViewer.fire();
    }

    class MyConverter extends StringConverter<Double> {

        @Override
        public String toString(Double object) {
            return object + "";
        }

        @Override
        public Double fromString(String string) {
            return Double.parseDouble(string);
        }

    }

    /**
     * @param expressionName
     * @param expressionString
     * @return
     */
    private Expression makeExpression(String expressionName, final String expressionString) {
        return makeExpressionForAudit(expressionName, expressionString, task.getNamedExpressionsMap());
    }

    private void updateDirectiveButtons() {
        ((RadioButton) taskManagerGridPane.lookup("#232")).setDisable(task.getSelectedIndexIsotope().compareTo(Squid3Constants.IndexIsoptopesEnum.PB_208) == 0);
        ((RadioButton) taskManagerGridPane.lookup("#direct")).setDisable(task.getSelectedIndexIsotope().compareTo(Squid3Constants.IndexIsoptopesEnum.PB_208) == 0);
    }

    private void populateDirectives() {
        updateDirectiveButtons();

        // Directives fields
        try {
            ((RadioButton) taskManagerGridPane.lookup("#" + task.getParentNuclide())).setSelected(true);
        } catch (Exception e) {
        }
        ((RadioButton) taskManagerGridPane.lookup("#" + (String) (task.isDirectAltPD() ? "direct" : "indirect"))).setSelected(true);

        boolean uPicked = ((RadioButton) taskManagerGridPane.lookup("#238")).isSelected();
        boolean directPicked = ((RadioButton) taskManagerGridPane.lookup("#direct")).isSelected();

        boolean perm1 = uPicked && !directPicked;
        boolean perm2 = uPicked && directPicked;
        boolean perm3 = !uPicked && !directPicked;
        boolean perm4 = !uPicked && directPicked;

        uncorrConstPbUlabel.setText(UNCOR206PB238U_CALIB_CONST + ":");
        Expression UTh_U = task.getExpressionByName(UNCOR206PB238U_CALIB_CONST);
        String UTh_U_ExpressionString = (UTh_U == null) ? UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION : UTh_U.getExcelExpressionString();
        uncorrConstPbUExpressionLabel.setText((perm1 || perm2 || perm4) ? UTh_U_ExpressionString : "Not Used");

        uncorrConstPbUExpressionLabel.setStyle(uncorrConstPbUExpressionLabel.getStyle()
                + (makeExpression(UNCOR206PB238U_CALIB_CONST, UTh_U_ExpressionString).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE));

        uncorrConstPbThlabel.setText(UNCOR208PB232TH_CALIB_CONST + ":");
        Expression UTh_Th = task.getExpressionByName(UNCOR208PB232TH_CALIB_CONST);
        String UTh_Th_ExpressionString = (UTh_Th == null) ? UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION : UTh_Th.getExcelExpressionString();
        uncorrConstPbThExpressionLabel.setText((perm2 || perm3 || perm4) ? UTh_Th_ExpressionString : "Not Used");

        uncorrConstPbThExpressionLabel.setStyle(uncorrConstPbThExpressionLabel.getStyle()
                + (makeExpression(UNCOR208PB232TH_CALIB_CONST, UTh_Th_ExpressionString).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE));

        th232U238Label.setText(TH_U_EXP_RM + ":");
        Expression thU = task.getExpressionByName(TH_U_EXP_DEFAULT);
        String thU_ExpressionString = (thU == null) ? TH_U_EXP_DEFAULT_EXPRESSION : thU.getExcelExpressionString();
        th232U238ExpressionLabel.setText((perm1 || perm3) ? thU_ExpressionString : "Not Used");

        th232U238ExpressionLabel.setStyle(th232U238ExpressionLabel.getStyle()
                + (makeExpression(TH_U_EXP_DEFAULT, thU_ExpressionString).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE));

        parentConcLabel.setText(PARENT_ELEMENT_CONC_CONST + ":");
        Expression parentPPM = task.getExpressionByName(PARENT_ELEMENT_CONC_CONST);
        String parentPPM_ExpressionString = (parentPPM == null) ? PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION : parentPPM.getExcelExpressionString();
        parentConcExpressionLabel.setText(parentPPM_ExpressionString);
        parentConcExpressionLabel.setStyle(parentConcExpressionLabel.getStyle()
                + (makeExpression(PARENT_ELEMENT_CONC_CONST, parentPPM_ExpressionString).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE));
    }

    private void setupListeners() {
        taskNameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                task.setName(newValue);
            }
        });

        taskDescriptionTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                task.setDescription(newValue);
            }
        });

        authorsNameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                task.setAuthorName(newValue);
            }
        });

        labNameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                task.setLabName(newValue);
            }
        });

        provenanceTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                task.setProvenance(newValue);
            }
        });
    }

    @FXML
    private void toggleParentNuclideAction(ActionEvent event) {
        task.setParentNuclide(((RadioButton) event.getSource()).getId());
        task.applyDirectives();
        populateDirectives();
        taskAuditTextArea.setText(task.printTaskAudit());
    }

    @FXML
    private void toggleDirectAltAction(ActionEvent event) {
        task.setDirectAltPD(((RadioButton) event.getSource()).getId().compareToIgnoreCase("DIRECT") == 0);
        task.applyDirectives();
        populateDirectives();
        taskAuditTextArea.setText(task.printTaskAudit());
    }
}
