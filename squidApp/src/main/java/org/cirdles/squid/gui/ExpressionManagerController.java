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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.cirdles.ludwig.squid25.Utilities;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWriterMathML;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class ExpressionManagerController implements Initializable {

    private final Image HEALTHY = new Image("org/cirdles/squid/gui/images/icon_checkmark.png");
    private final Image UNHEALTHY = new Image("org/cirdles/squid/gui/images/wrongx_icon.png");

    private Expression currentExpression;
    private ExpressionTreeInterface originalExpressionTree;
    private WebEngine webEngine;

    @FXML
    private AnchorPane scrolledAnchorPane;
    @FXML
    private AnchorPane expressionsAnchorPane;
    @FXML
    private ListView<Expression> expressionsListView;
    @FXML
    private Pane expressionDetailsPane;
    @FXML
    private Label expressionListHeaderLabel;
    @FXML
    private TextField expressionNameTextField;
    @FXML
    private TextArea expressionAuditTextArea;

    @FXML
    private TextArea expressionExcelTextArea;
    @FXML
    private CheckBox refMatSwitchCheckBox;
    @FXML
    private CheckBox unknownsSwitchCheckBox;
    @FXML
    private WebView expressionWebView;
    @FXML
    private TextArea rmPeekTextArea;
    @FXML
    private TextArea unPeekTextArea;
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button auditButton;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        expressionsAnchorPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        expressionsAnchorPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        // update 
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport();

        initializeExpressionsListView();

        webEngine = expressionWebView.getEngine();
    }

    private void initializeExpressionsListView() {
        expressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        expressionListHeaderLabel.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        expressionListHeaderLabel.setText(
                String.format("%1$-" + 5 + "s", " ")
                + String.format("%1$-" + 3 + "s", "RI")
                + String.format("%1$-" + 3 + "s", "SC")
                + String.format("%1$-" + 3 + "s", "RM")
                + String.format("%1$-" + 3 + "s", "UN")
                + String.format("%1$-" + 3 + "s", "SQ")
                + String.format("%1$-" + 16 + "s", "Sorted in Execution Order"));
        Tooltip tooltip = new Tooltip();
        tooltip.setText("RI = Ratios of Interest; SC = Summary; RM = Reference Materials; UN = Unknowns; SQ = Special Squid UPbTh");
        expressionListHeaderLabel.setTooltip(tooltip);

        expressionsListView.setCellFactory(param -> new ListCell<Expression>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(Expression expression, boolean empty) {
                super.updateItem(expression, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (expression.amHealthy()) {
                        imageView.setImage(HEALTHY);
                    } else {
                        imageView.setImage(UNHEALTHY);
                    }

                    imageView.setFitHeight(12);
                    imageView.setFitWidth(12);
                    setText(expression.buildSignatureString());
                    setGraphic(imageView);
                }
            }

        });

        expressionsListView.setContextMenu(createExpressionsListViewContextMenu());
        expressionsListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Expression>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Expression> exp) {
                ObservableList<Expression> selected = expressionsListView.getSelectionModel().getSelectedItems();
                toggleEditMode(false);
                populateExpressionDetails(selected.get(0));
            }
        });

        populateExpressionsListView();
    }

    private Expression parseAndAuditCurrentExcelExpression() {
        Expression exp = squidProject.getTask().generateExpressionFromRawExcelStyleText(
                expressionNameTextField.getText().length() == 0 ? "Anonymous" : expressionNameTextField.getText(),
                expressionExcelTextArea.getText().trim().replace("\n", ""));

        ExpressionTreeInterface expTree = exp.getExpressionTree();
        // to detect ratios of interest
        if (expTree instanceof BuiltInExpressionInterface) {
            ((BuiltInExpressionInterface) expTree).buildExpression(squidProject.getTask());
        }

        if (originalExpressionTree != null) {
            expTree.setSquidSwitchSAUnknownCalculation(originalExpressionTree.isSquidSwitchSAUnknownCalculation());
            expTree.setSquidSwitchSTReferenceMaterialCalculation(originalExpressionTree.isSquidSwitchSTReferenceMaterialCalculation());
            expTree.setSquidSwitchSCSummaryCalculation(originalExpressionTree.isSquidSwitchSCSummaryCalculation());
        }

        expressionAuditTextArea.setText(exp.produceExpressionTreeAudit());

        webEngine.loadContent(ExpressionTreeWriterMathML.toStringBuilderMathML(exp.getExpressionTree()).toString());

        return exp;
    }

    private void populatePeeks(Expression exp) {

        if ((exp == null) || (!exp.amHealthy())) {
            rmPeekTextArea.setText("No expression.");
            unPeekTextArea.setText("No expression.");
        } else {

            TaskInterface task = squidProject.getTask();

            List<ShrimpFractionExpressionInterface> refMatSpots = task.getReferenceMaterialSpots();
            List<ShrimpFractionExpressionInterface> unSpots = task.getUnknownSpots();

            if (originalExpressionTree.isSquidSwitchSCSummaryCalculation()) {
                SpotSummaryDetails spotSummary = task.getTaskExpressionsEvaluationsPerSpotSet().get(originalExpressionTree.getName());

                if (task.getTaskExpressionsEvaluationsPerSpotSet().get(originalExpressionTree.getName()) != null) {
                    if (originalExpressionTree.isSquidSwitchSTReferenceMaterialCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            rmPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            rmPeekTextArea.setText("No Reference Materials");
                        }
                    } else {
                        rmPeekTextArea.setText("No Summary");
                    }

                    if (originalExpressionTree.isSquidSwitchSAUnknownCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            unPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            unPeekTextArea.setText("No Unknowns");
                        }
                    } else {
                        unPeekTextArea.setText("No Summary");
                    }
                }

            } else {
                if (originalExpressionTree.isSquidSwitchSTReferenceMaterialCalculation()) {
                    if (refMatSpots.size() > 0) {
                        rmPeekTextArea.setText(peekDetailsPerSpot(refMatSpots));
                    } else {
                        rmPeekTextArea.setText("No Reference Materials");
                    }
                } else if (!originalExpressionTree.isSquidSwitchSTReferenceMaterialCalculation()) {
                    rmPeekTextArea.setText("Reference Materials not processed.");
                }
                if (originalExpressionTree.isSquidSwitchSAUnknownCalculation()) {
                    if (unSpots.size() > 0) {
                        unPeekTextArea.setText(peekDetailsPerSpot(unSpots));
                    } else {
                        rmPeekTextArea.setText("No Unknowns");
                    }
                } else if (!originalExpressionTree.isSquidSwitchSAUnknownCalculation()) {
                    unPeekTextArea.setText("Unknowns not processed.");
                }
            }
        }
    }

    private String peekDetailsPerSummary(SpotSummaryDetails spotSummary) {
        String[][] labels = spotSummary.getOperation().getLabelsForOutputValues();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < labels[0].length; i++) {
            sb.append("\t");
            sb.append(String.format("%1$-" + 13 + "s", labels[0][i]));
            sb.append(": ");
            sb.append(Utilities.roundedToSize(spotSummary.getValues()[0][i], 12));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String peekDetailsPerSpot(List<ShrimpFractionExpressionInterface> spots) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%1$-" + 15 + "s", "Spot name"));
        String[][] resultLabels = ((ExpressionTree) originalExpressionTree).getOperation().getLabelsForOutputValues();
        for (int i = 0; i < resultLabels[0].length; i++) {
            try {
                sb.append(String.format("%1$-" + 20 + "s", resultLabels[0][i], 12));
            } catch (Exception e) {
            }
        }
        if (((ExpressionTree) originalExpressionTree).hasRatiosOfInterest()) {
            sb.append(String.format("%1$-" + 20 + "s", "1-sigma ABS", 12));
        }
        sb.append("\n");

        for (ShrimpFractionExpressionInterface spot : spots) {
            if (spot.getTaskExpressionsEvaluationsPerSpot().get(originalExpressionTree) != null) {
                sb.append(String.format("%1$-" + 15 + "s", spot.getFractionID()));
                double[][] results
                        = spot.getTaskExpressionsEvaluationsPerSpot().get(originalExpressionTree);
                for (int i = 0; i < results[0].length; i++) {
                    try {
                        sb.append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(results[0][i], 12)));
                    } catch (Exception e) {
                    }
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void populateExpressionDetails(Expression expression) {
        if (expression != null) {

            currentExpression = expression;

            originalExpressionTree = currentExpression.getExpressionTree();

            expressionNameTextField.setText(currentExpression.getName());
            expressionExcelTextArea.setText(currentExpression.getExcelExpressionString());

            refMatSwitchCheckBox.setSelected(((ExpressionTree) currentExpression.getExpressionTree()).isSquidSwitchSTReferenceMaterialCalculation());
            unknownsSwitchCheckBox.setSelected(((ExpressionTree) currentExpression.getExpressionTree()).isSquidSwitchSAUnknownCalculation());

            populatePeeks(parseAndAuditCurrentExcelExpression());
        }
    }

    private void vacateExpressionDetails() {
        expressionNameTextField.setText("");
        expressionExcelTextArea.setText("");
        expressionAuditTextArea.setText("Audit:");
    }

    private void populateExpressionsListView() {
        List<Expression> namedExpressions = squidProject.getTask().getTaskExpressionsOrdered();
        ObservableList<Expression> items
                = FXCollections.observableArrayList(namedExpressions);
        expressionsListView.setItems(items);
    }

    private ContextMenu createExpressionsListViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem = new MenuItem("Remove expression.");
        menuItem.setOnAction((evt) -> {
            Expression selectedExpression = expressionsListView.getSelectionModel().getSelectedItem();
            if (selectedExpression != null) {
                squidProject.getTask().removeExpression(selectedExpression);
                populateExpressionsListView();
//                expressionsListView.getItems().remove(selectedExpression);
//                expressionsListView.refresh();
            }
        });
        contextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Restore removed expressions.");
        menuItem.setOnAction((evt) -> {
            squidProject.getTask().restoreRemovedExpressions();
            populateExpressionsListView();
        });
        contextMenu.getItems().add(menuItem);

        menuItem = new SeparatorMenuItem();
        contextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Export expression as XML document.");
        menuItem.setOnAction((evt) -> {
            Expression selectedExpression = expressionsListView.getSelectionModel().getSelectedItem();
            if (selectedExpression != null) {
                try {
                    File expressionFileXML = FileHandler.saveExpressionFileXML(selectedExpression, SquidUI.primaryStageWindow);
                } catch (IOException iOException) {
                }
            }
        });
        contextMenu.getItems().add(menuItem);

        return contextMenu;
    }

    @FXML
    private void newButtonAction(ActionEvent event) {
    }

    @FXML
    private void editButtonAction(ActionEvent event) {
        rmPeekTextArea.setText("");
        unPeekTextArea.setText("");

        toggleEditMode(true);
    }

    @FXML
    private void saveButtonAction(ActionEvent event) {
        if (currentExpression != null) {
            Expression exp = parseAndAuditCurrentExcelExpression();

            ExpressionTreeInterface expTree = exp.getExpressionTree();

            ((ExpressionTree) expTree).setSquidSwitchSTReferenceMaterialCalculation(refMatSwitchCheckBox.selectedProperty().getValue());
            ((ExpressionTree) expTree).setSquidSwitchSAUnknownCalculation(unknownsSwitchCheckBox.selectedProperty().getValue());

            ((ExpressionTree) expTree).setSquidSwitchSCSummaryCalculation(((ExpressionTree) originalExpressionTree).isSquidSwitchSCSummaryCalculation());
            ((ExpressionTree) expTree).setSquidSpecialUPbThExpression(((ExpressionTree) originalExpressionTree).isSquidSpecialUPbThExpression());
            ((ExpressionTree) expTree).setRootExpressionTree(((ExpressionTree) originalExpressionTree).isRootExpressionTree());

            currentExpression.setName(expressionNameTextField.getText().trim());
            currentExpression.setExpressionTree(expTree);
            currentExpression.setExcelExpressionString(expressionExcelTextArea.getText().trim().replace("\n", ""));

            squidProject.getTask().updateAffectedExpressions(2, currentExpression);
            squidProject.getTask().updateAllExpressions(1);
            squidProject.getTask().setChanged(true);
            squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport();

            // reveal new ordering etc
            populateExpressionsListView();
            expressionsListView.refresh();

            populatePeeks(exp);
        } else {
            rmPeekTextArea.setText("No Expression due to parsing error.");
            unPeekTextArea.setText("No Expression due to parsing error.");
        }

        toggleEditMode(false);
    }

    private void toggleEditMode(boolean editMode) {
        expressionNameTextField.setEditable(editMode);
        expressionExcelTextArea.setEditable(editMode);
        refMatSwitchCheckBox.setDisable(!editMode);
        unknownsSwitchCheckBox.setDisable(!editMode);

        editButton.setDisable(editMode);
        saveButton.setDisable(!editMode);
        cancelButton.setDisable(!editMode);

        if (editMode) {
            rmPeekTextArea.setText("No values calculated during edit of expression");
            unPeekTextArea.setText("No values calculated during edit of expression");
        }
    }

    @FXML
    private void cancelButtonAction(ActionEvent event) {
        cancelEdit();
    }

    private void cancelEdit() {
        toggleEditMode(false);
        populateExpressionDetails(currentExpression);
    }

    @FXML
    private void auditButtonAction(ActionEvent event) {
        parseAndAuditCurrentExcelExpression();
        rmPeekTextArea.setText("Audit mode - no evaluation.");
        unPeekTextArea.setText("Audit mode - no evaluation.");
    }

    @FXML
    private void refMatSwitchCheckBoxOnAction(ActionEvent event) {
    }

    @FXML
    private void unknownsSwitchCheckBoxOnAction(ActionEvent event) {
    }

}
