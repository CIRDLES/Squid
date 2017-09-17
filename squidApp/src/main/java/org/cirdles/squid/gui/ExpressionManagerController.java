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
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
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
import org.cirdles.squid.exceptions.SquidException;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
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

        // update expressions
        squidProject.getTask().setupSquidSessionSpecs();

        initializeExpressionsListView();
        
        squidProject.getTask().evaluateTaskExpressions();

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

        expressionsListView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Expression>() {
            public void changed(ObservableValue<? extends Expression> ov,
                    Expression old_val, Expression new_val) {
                if (new_val != null) {
                    populateExpressionDetails(new_val);
                } else {
                    vacateExpressionDetails();
                }
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

        expTree.setSquidSwitchSAUnknownCalculation(originalExpressionTree.isSquidSwitchSAUnknownCalculation());
        expTree.setSquidSwitchSTReferenceMaterialCalculation(originalExpressionTree.isSquidSwitchSTReferenceMaterialCalculation());
        expTree.setSquidSwitchSCSummaryCalculation(originalExpressionTree.isSquidSwitchSCSummaryCalculation());

        expressionAuditTextArea.setText(exp.produceExpressionTreeAudit());

        webEngine.loadContent(ExpressionTreeWriterMathML.toStringBuilderMathML(exp.getExpressionTree()).toString());

        if (exp.amHealthy()) {
            populatePeeks(exp);
        }

        return exp;
    }

    private void populatePeeks(Expression exp) {
        rmPeekTextArea.setText("Problem encountered evaluating expression.");

        TaskInterface task = squidProject.getTask();

        List<ShrimpFractionExpressionInterface> refMatSpots = task.getReferenceMaterialSpots();
        List<ShrimpFractionExpressionInterface> unSpots = task.getUnknownSpots();

        ExpressionTreeInterface expTree = originalExpressionTree;

//        try {
//            task.evaluateTaskExpressions();


            ShrimpFractionExpressionInterface spot = refMatSpots.get(0);
            StringBuilder sb = new StringBuilder();
            sb.append(spot.getFractionID());
            sb.append("\t");
            if ((((ExpressionTree) expTree).hasRatiosOfInterest()) || !((ExpressionTree) expTree).isSquidSwitchSCSummaryCalculation()) {

                sb.append(Utilities.roundedToSize(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)[0][0], 12));

                if (((ExpressionTree) expTree).hasRatiosOfInterest()) {
                    sb.append("\t");
                    sb.append(Utilities.roundedToSize(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)[0][1], 12));
                }

                sb.append("\n");
            } else if (((ExpressionTree) expTree).isSquidSwitchSCSummaryCalculation()) {
                try {
                    sb.append(Utilities.roundedToSize(task.getTaskExpressionsEvaluationsPerSpotSet().get(expTree.getName()).getValues()[0][0], 12));
                    sb.append("\n");
                } catch (Exception e) {
                }
            }

            rmPeekTextArea.setText(sb.toString());

//        } catch (SquidException squidException) {
//        }
    }

    private void populateExpressionDetails(Expression expression) {
        if (expression != null) {

            currentExpression = expression;
          //  originalExpressionTree = ((ExpressionTree) currentExpression.getExpressionTree()).copy();
            originalExpressionTree = currentExpression.getExpressionTree();

            expressionNameTextField.setText(currentExpression.getName());
            expressionExcelTextArea.setText(currentExpression.getExcelExpressionString());

            refMatSwitchCheckBox.setSelected(((ExpressionTree) currentExpression.getExpressionTree()).isSquidSwitchSTReferenceMaterialCalculation());
            unknownsSwitchCheckBox.setSelected(((ExpressionTree) currentExpression.getExpressionTree()).isSquidSwitchSAUnknownCalculation());

            parseAndAuditCurrentExcelExpression();
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
                expressionsListView.getItems().remove(selectedExpression);
                expressionsListView.refresh();
            }
        });
        contextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Restore removed expressions.");
        menuItem.setOnAction((evt) -> {
            squidProject.getTask().restoreRemovedExpressions();
            populateExpressionsListView();
        });
        contextMenu.getItems().add(menuItem);

        return contextMenu;
    }

    @FXML
    private void newButtonAction(ActionEvent event) {
    }

    @FXML
    private void editButtonAction(ActionEvent event) {
        expressionExcelTextArea.setEditable(true);
        refMatSwitchCheckBox.setDisable(false);
        unknownsSwitchCheckBox.setDisable(false);
    }

    @FXML
    private void saveButtonAction(ActionEvent event) {
        if (currentExpression != null) {
            Expression exp = parseAndAuditCurrentExcelExpression();
            ExpressionTreeInterface expTree = exp.getExpressionTree();

            // until we have these in the edit box
            ((ExpressionTree) expTree).setSquidSwitchSTReferenceMaterialCalculation(refMatSwitchCheckBox.selectedProperty().getValue());
            ((ExpressionTree) expTree).setSquidSwitchSAUnknownCalculation(unknownsSwitchCheckBox.selectedProperty().getValue());
            ((ExpressionTree) expTree).setRatiosOfInterest(((ExpressionTree)originalExpressionTree).getRatiosOfInterest());          

            currentExpression.setExpressionTree(expTree);
            currentExpression.setExcelExpressionString(expressionExcelTextArea.getText().trim().replace("\n", ""));

            squidProject.getTask().setChanged(true);
            // update expressions
            squidProject.getTask().setupSquidSessionSpecs();

            squidProject.getTask().evaluateTaskExpressions();
            
            // reveal new ordering etc
            populateExpressionsListView();
            expressionsListView.refresh();
        }
    }

    @FXML
    private void cancelButtonAction(ActionEvent event) {
        populateExpressionDetails(currentExpression);
    }

    @FXML
    private void auditButtonAction(ActionEvent event) {
//        squidProject.getTask().setChanged(true);
//        // update expressions
//        squidProject.getTask().setupSquidSessionSpecs();

        parseAndAuditCurrentExcelExpression();
    }

    @FXML
    private void refMatSwitchCheckBoxOnAction(ActionEvent event) {
    }

    @FXML
    private void unknownsSwitchCheckBoxOnAction(ActionEvent event) {
    }

}
