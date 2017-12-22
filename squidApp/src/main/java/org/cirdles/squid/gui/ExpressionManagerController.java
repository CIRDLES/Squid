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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import org.cirdles.ludwig.squid25.Utilities;
import org.cirdles.squid.exceptions.SquidException;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.utilities.BrowserControl;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWriterMathML;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;

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
    @FXML
    private TabPane spotTabPane;
    @FXML
    private Tab refMatTab;
    @FXML
    private Tab unkTab;
    @FXML
    private CheckBox concRefMatSwitchCheckBox;
    @FXML
    private CheckBox graphHereCheckbox;
    @FXML
    private CheckBox graphBrowserCheckbox;
    private boolean graphLocal = true;
    private boolean graphBrowser = false;
    private ExpressionTreeInterface graphedExpressionTree;
    private ExpressionSortOrder expressionSortOrder;
    private ExpressionChoiceFilter expressionChoiceFilter;

    @FXML
    private ChoiceBox<ExpressionSortOrder> expressionSorterChoiceBox;
    @FXML
    private ChoiceBox<ExpressionChoiceFilter> expressionChooserChoiceBox;

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

        expressionSortOrder = ExpressionSortOrder.EVAL;
        expressionChoiceFilter = ExpressionChoiceFilter.ALL;
        // update 
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport();

        initializeExpressionsChoiceFilterChoiceBox();
        initializeExpressionsSortFilterChoiceBox();
        initializeExpressionsListView();

        rmPeekTextArea.setStyle(SquidUI.PEEK_LIST_CSS_STYLE_SPECS);
        unPeekTextArea.setStyle(SquidUI.PEEK_LIST_CSS_STYLE_SPECS);

        webEngine = expressionWebView.getEngine();

        Tooltip tooltip = new Tooltip();
        tooltip.setText("Choose local to see graph in panel to right.  De-select if expressions look poorly renedered and select browser instead.");
        graphHereCheckbox.setTooltip(tooltip);
        graphBrowserCheckbox.setTooltip(tooltip);
    }

    private void initializeExpressionsChoiceFilterChoiceBox() {
        expressionChooserChoiceBox.setItems(FXCollections.observableArrayList(ExpressionChoiceFilter.values()));
        expressionChooserChoiceBox.getSelectionModel().select(expressionChoiceFilter);
        expressionChooserChoiceBox.setConverter(new StringConverter<ExpressionChoiceFilter>() {
            @Override
            public String toString(ExpressionChoiceFilter object) {
                return object.getDisplayName();
            }

            @Override
            public ExpressionChoiceFilter fromString(String string) {
                return null;
            }
        });
        expressionChooserChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ExpressionChoiceFilter>() {
            @Override
            public void changed(ObservableValue observable, ExpressionChoiceFilter oldValue, ExpressionChoiceFilter newValue) {
                expressionChoiceFilter = newValue;
                populateExpressionsListView();
            }
        });
    }

    private void initializeExpressionsSortFilterChoiceBox() {
        expressionSorterChoiceBox.setItems(FXCollections.observableArrayList(ExpressionSortOrder.values()));
        expressionSorterChoiceBox.getSelectionModel().select(expressionSortOrder);
        expressionSorterChoiceBox.setConverter(new StringConverter<ExpressionSortOrder>() {
            @Override
            public String toString(ExpressionSortOrder object) {
                return object.getDisplayName();
            }

            @Override
            public ExpressionSortOrder fromString(String string) {
                return null;
            }
        });
        expressionSorterChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ExpressionSortOrder>() {
            @Override
            public void changed(ObservableValue observable, ExpressionSortOrder oldValue, ExpressionSortOrder newValue) {
                expressionSortOrder = newValue;
                populateExpressionsListView();
            }
        });
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
                + String.format("%1$-" + 25 + "s", "From:            Sorted:"));
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

    private void populateExpressionsListView() {
        SortedSet<Expression> namedExpressions = squidProject.getTask().getTaskExpressionsOrdered();
        List<Expression> sortedExpressionsList = new ArrayList<>();

        switch (expressionChoiceFilter) {
            case ALL:
                sortedExpressionsList.addAll(namedExpressions);
                break;
            case BUILTIN:
                for (Expression exp : namedExpressions) {
                    if (exp.getExpressionTree().isSquidSpecialUPbThExpression()) {
                        sortedExpressionsList.add(exp);
                    }
                }
                break;
            case CUSTOM:
                for (Expression exp : namedExpressions) {
                    if (!exp.getExpressionTree().isSquidSpecialUPbThExpression()) {
                        sortedExpressionsList.add(exp);
                    }
                }
                break;
            case HEALTHY:
                for (Expression exp : namedExpressions) {
                    if (exp.amHealthy()) {
                        sortedExpressionsList.add(exp);
                    }
                }
                break;
            case UNHEALTHY:
                for (Expression exp : namedExpressions) {
                    if (!exp.amHealthy()) {
                        sortedExpressionsList.add(exp);
                    }
                }
                break;
        }

        sortedExpressionsList.sort(
                new Comparator<Expression>() {
            @Override
            public int compare(Expression exp1, Expression exp2) {
                int retVal = 0;
                switch (expressionSortOrder) {
                    case EVAL:
                        retVal = exp1.compareTo(exp2);
                        break;
                    case NAME:
                        retVal = exp1.getName().compareToIgnoreCase(exp2.getName());
                        break;
                    case NU:
                        // true first
                        retVal = Boolean.compare(exp2.isSquidSwitchNU(), exp1.isSquidSwitchNU());
                        if (retVal == 0) {
                            retVal = exp1.getName().compareToIgnoreCase(exp2.getName());
                        }
                        break;
                    case BUILTIN:
                        // true first
                        retVal = Boolean.compare(exp2.getExpressionTree().isSquidSpecialUPbThExpression(), exp1.getExpressionTree().isSquidSpecialUPbThExpression());
                        if (retVal == 0) {
                            retVal = exp1.getName().compareToIgnoreCase(exp2.getName());
                        }
                        break;
                }

                return retVal;
            }
        }
        );

        ObservableList<Expression> items
                = FXCollections.observableArrayList(sortedExpressionsList);
        expressionsListView.setItems(items);
    }

    private enum ExpressionSortOrder {
        EVAL(" Evaluation Order"),
        NAME(" Name"),
        NU(" NU Switch"),
        BUILTIN(" BuiltIn/Custom");

        private String displayName;

        private ExpressionSortOrder(String displayName) {
            this.displayName = displayName;
        }

        /**
         * @return the displayName
         */
        public String getDisplayName() {
            return displayName;
        }
    }

    private enum ExpressionChoiceFilter {
        ALL("All"),
        HEALTHY("Healthy"),
        UNHEALTHY("Unhealthy"),
        BUILTIN("BuiltIn"),
        CUSTOM("Custom");

        private String displayName;

        private ExpressionChoiceFilter(String displayName) {
            this.displayName = displayName;
        }

        /**
         * @return the displayName
         */
        public String getDisplayName() {
            return displayName;
        }
    }

    private Expression parseAndAuditCurrentExcelExpression() {
        Expression exp = squidProject.getTask().generateExpressionFromRawExcelStyleText(
                expressionNameTextField.getText().length() == 0 ? "Anonymous" : expressionNameTextField.getText(),
                expressionExcelTextArea.getText().trim().replace("\n", ""),
                currentExpression.isSquidSwitchNU());

        graphedExpressionTree = exp.getExpressionTree();
        // to detect ratios of interest
        if (graphedExpressionTree instanceof BuiltInExpressionInterface) {
            ((BuiltInExpressionInterface) graphedExpressionTree).buildExpression(squidProject.getTask());
        }

        if (originalExpressionTree != null) {
            graphedExpressionTree.setSquidSwitchSAUnknownCalculation(originalExpressionTree.isSquidSwitchSAUnknownCalculation());
            graphedExpressionTree.setSquidSwitchSTReferenceMaterialCalculation(originalExpressionTree.isSquidSwitchSTReferenceMaterialCalculation());
            graphedExpressionTree.setSquidSwitchConcentrationReferenceMaterialCalculation(originalExpressionTree.isSquidSwitchConcentrationReferenceMaterialCalculation());
            graphedExpressionTree.setSquidSwitchSCSummaryCalculation(originalExpressionTree.isSquidSwitchSCSummaryCalculation());
            graphedExpressionTree.setSquidSpecialUPbThExpression(originalExpressionTree.isSquidSpecialUPbThExpression());
            graphedExpressionTree.setRootExpressionTree(originalExpressionTree.isRootExpressionTree());
        }

        expressionAuditTextArea.setText(exp.produceExpressionTreeAudit());

        graphExpressionTree();

        return exp;
    }

    private void graphExpressionTree() {
        // decide where to graph expression
        String graphContents = ExpressionTreeWriterMathML.toStringBuilderMathML(graphedExpressionTree).toString();
        if (graphLocal) {
            webEngine.loadContent(graphContents);
        } else {
            webEngine.loadContent("<html>\n"
                    + "<h3> &nbsp; </h3>\n"
                    + "<h3 style=\"text-align:center;\">Local graphing is off.</h3>\n"
                    + "</html>");
        }

        if (graphBrowser) {
            try {
                Files.write(Paths.get("EXPRESSION.HTML"), graphContents.getBytes());
                BrowserControl.showURI("EXPRESSION.HTML");
            } catch (IOException iOException) {
            }
        }
    }

    private void populatePeeks(Expression exp) {
        SingleSelectionModel<Tab> selectionModel = spotTabPane.getSelectionModel();

        if ((exp == null) || (!exp.amHealthy())) {
            rmPeekTextArea.setText("No expression.");
            unPeekTextArea.setText("No expression.");
            selectionModel.select(refMatTab);
        } else {

            TaskInterface task = squidProject.getTask();

            List<ShrimpFractionExpressionInterface> refMatSpots = task.getReferenceMaterialSpots();
            List<ShrimpFractionExpressionInterface> unSpots = task.getUnknownSpots();
            List<ShrimpFractionExpressionInterface> concRefMatSpots = task.getConcentrationReferenceMaterialSpots();

            // choose peek tab
            if (refMatTab.isSelected() & !refMatSwitchCheckBox.isSelected() & !concRefMatSwitchCheckBox.isSelected()) {
                selectionModel.select(unkTab);
            } else if (unkTab.isSelected() & !unknownsSwitchCheckBox.isSelected()) {
                selectionModel.select(refMatTab);
            }

            if (originalExpressionTree instanceof ConstantNode) {
                rmPeekTextArea.setText("Not used");
                unPeekTextArea.setText("Not used");
                if (originalExpressionTree.isSquidSwitchSTReferenceMaterialCalculation()) {
                    try {
                        rmPeekTextArea.setText(exp.getName() + " = " + Utilities.roundedToSize((Double) ((ConstantNode) originalExpressionTree).getValue(), 12));
                    } catch (Exception e) {
                    }
                }
                if (originalExpressionTree.isSquidSwitchSAUnknownCalculation()) {
                    try {
                        unPeekTextArea.setText(exp.getName() + " = " + Utilities.roundedToSize((Double) ((ConstantNode) originalExpressionTree).getValue(), 12));
                    } catch (Exception e) {
                    }
                }

            } else if (originalExpressionTree.isSquidSwitchSCSummaryCalculation()) {
                SpotSummaryDetails spotSummary = task.getTaskExpressionsEvaluationsPerSpotSet().get(originalExpressionTree.getName());

                rmPeekTextArea.setText("No Summary");
                unPeekTextArea.setText("No Summary");

                if (spotSummary != null) {
                    if (originalExpressionTree.isSquidSwitchSTReferenceMaterialCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            rmPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            rmPeekTextArea.setText("No Reference Materials");
                        }
                    }

                    if (originalExpressionTree.isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            rmPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            rmPeekTextArea.setText("No Concentration Reference Materials");
                        }
                    }

                    if (originalExpressionTree.isSquidSwitchSAUnknownCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            unPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            unPeekTextArea.setText("No Unknowns");
                        }
                    }
                }

            } else {
                rmPeekTextArea.setText("Reference Materials not processed.");
                if (originalExpressionTree.isSquidSwitchSTReferenceMaterialCalculation()) {
                    if (refMatSpots.size() > 0) {
                        rmPeekTextArea.setText(peekDetailsPerSpot(refMatSpots, exp.getExpressionTree()));
                    } else {
                        rmPeekTextArea.setText("No Reference Materials");
                    }
                } else if (originalExpressionTree.isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                    if (concRefMatSpots.size() > 0) {
                        rmPeekTextArea.setText(peekDetailsPerSpot(concRefMatSpots, exp.getExpressionTree()));
                    } else {
                        rmPeekTextArea.setText("No Concentration Reference Materials");
                    }
                }

                unPeekTextArea.setText("Unknowns not processed.");
                if (originalExpressionTree.isSquidSwitchSAUnknownCalculation()) {
                    if (unSpots.size() > 0) {
                        unPeekTextArea.setText(peekDetailsPerSpot(unSpots, exp.getExpressionTree()));
                    } else {
                        rmPeekTextArea.setText("No Unknowns");
                    }
                }
            }
        }
    }

    private String peekDetailsPerSummary(SpotSummaryDetails spotSummary) {
        String[][] labels = spotSummary.getOperation().getLabelsForOutputValues();
        StringBuilder sb = new StringBuilder();
        if (concRefMatSwitchCheckBox.isSelected()) {
            sb.append("Concentration Reference Materials Only\n\n");
        }
        for (int i = 0; i < labels[0].length; i++) {
            sb.append("\t");
            sb.append(String.format("%1$-" + 13 + "s", labels[0][i]));
            sb.append(": ");
            sb.append(Utilities.roundedToSize(spotSummary.getValues()[0][i], 12));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String peekDetailsPerSpot(List<ShrimpFractionExpressionInterface> spots, ExpressionTreeInterface editedExp) {
        StringBuilder sb = new StringBuilder();

        if (originalExpressionTree instanceof ShrimpSpeciesNode) {
            sb.append("Please specify property of species such as totalCps.");
        } else if (editedExp instanceof VariableNodeForIsotopicRatios) {
            // special case where the expressionTree is a ratio
            sb.append(String.format("%1$-" + 15 + "s", "Spot name"));
            sb.append(String.format("%1$-" + 20 + "s", editedExp.getName()));
            sb.append(String.format("%1$-" + 20 + "s", "1-sigma ABS"));
            sb.append("\n");

            for (ShrimpFractionExpressionInterface spot : spots) {
                sb.append(String.format("%1$-" + 15 + "s", spot.getFractionID()));
                double[][] results
                        = spot.getIsotopicRatioValuesByStringName(editedExp.getName());
                for (int i = 0; i < results[0].length; i++) {
                    try {
                        sb.append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(results[0][i], 12)));
                    } catch (Exception e) {
                    }
                }
                sb.append("\n");
            }

        } else if (editedExp instanceof SpotFieldNode) {
            // special case where the expressionTree is a field in spot (non-ratio)
            sb.append(String.format("%1$-" + 15 + "s", "Spot name"));
            sb.append(String.format("%1$-" + 20 + "s", editedExp.getName()));
            sb.append("\n");

            for (ShrimpFractionExpressionInterface spot : spots) {
                sb.append(String.format("%1$-" + 15 + "s", spot.getFractionID()));
                List<ShrimpFractionExpressionInterface> singleSpot = new ArrayList<>();
                singleSpot.add(spot);

                try {
                    double[][] results = ExpressionTreeInterface.convertObjectArrayToDoubles(editedExp.eval(singleSpot, null));
                    for (int i = 0; i < results[0].length; i++) {
                        try {
                            sb.append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(results[0][i], 12)));
                        } catch (Exception e) {
                        }
                    }
                    sb.append("\n");
                } catch (SquidException squidException) {
                }

            }

        } else {
            if (concRefMatSwitchCheckBox.isSelected()) {
                sb.append("Concentration Reference Materials Only\n\n");
            }
            sb.append(String.format("%1$-" + 15 + "s", "Spot name"));
            if (((ExpressionTree) originalExpressionTree).getOperation() != null) {
                String[][] resultLabels = ((ExpressionTree) originalExpressionTree).getOperation().getLabelsForOutputValues();
                for (int i = 0; i < resultLabels[0].length; i++) {
                    try {
                        sb.append(String.format("%1$-" + 20 + "s", resultLabels[0][i]));
                    } catch (Exception e) {
                    }
                }
                if (((ExpressionTree) originalExpressionTree).hasRatiosOfInterest()) {
                    sb.append(String.format("%1$-" + 20 + "s", "1-sigma ABS"));
                }
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
            concRefMatSwitchCheckBox.setSelected(((ExpressionTree) currentExpression.getExpressionTree()).isSquidSwitchConcentrationReferenceMaterialCalculation());

            populatePeeks(parseAndAuditCurrentExcelExpression());
        }
    }

    private ContextMenu createExpressionsListViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem = new MenuItem("Remove expression.");
        menuItem.setOnAction((evt) -> {
            Expression selectedExpression = expressionsListView.getSelectionModel().getSelectedItem();
            if (selectedExpression != null) {
                squidProject.getTask().removeExpression(selectedExpression);
                populateExpressionsListView();
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
            ((ExpressionTree) expTree).setSquidSwitchConcentrationReferenceMaterialCalculation(concRefMatSwitchCheckBox.selectedProperty().getValue());

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
        concRefMatSwitchCheckBox.setDisable(!editMode);

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
        if (refMatSwitchCheckBox.isSelected()) {
            concRefMatSwitchCheckBox.setSelected(false);
        }
    }

    @FXML
    private void unknownsSwitchCheckBoxOnAction(ActionEvent event) {
        if (unknownsSwitchCheckBox.isSelected()) {
            concRefMatSwitchCheckBox.setSelected(false);
        }
    }

    @FXML
    private void concRefMatSwitchCheckBoxOnAction(ActionEvent event) {
        if (concRefMatSwitchCheckBox.isSelected()) {
            refMatSwitchCheckBox.setSelected(false);
            unknownsSwitchCheckBox.setSelected(false);
        }
    }

    @FXML
    private void graphHereCheckboxAction(ActionEvent event) {
        graphLocal = !graphLocal;
        graphExpressionTree();
    }

    @FXML
    private void graphBrowserCheckboxAction(ActionEvent event) {
        graphBrowser = !graphBrowser;
        if (graphBrowser) {
            graphExpressionTree();
        }
    }

}
