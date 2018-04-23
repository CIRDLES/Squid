/*
* Copyright 2018 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.expressions;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.cirdles.ludwig.squid25.Utilities;
import org.cirdles.squid.ExpressionsForSquid2Lexer;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.SquidUI;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.OPERATOR_IN_EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.utilities.BrowserControl;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWriterMathML;
import org.cirdles.squid.tasks.expressions.functions.Function;
import static org.cirdles.squid.tasks.expressions.functions.Function.FUNCTIONS_MAP;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import static org.cirdles.squid.tasks.expressions.operations.Operation.OPERATIONS_MAP;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;



/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class ExpressionBuilderController implements Initializable {

    
    
    
    //BUTTONS
    @FXML
    private Button expressionCopyBtn;
    @FXML
    private Button expressionPasteBtn;
    @FXML
    private Button expressionAsTextBtn;
    @FXML
    private Button expressionClearBtn;
    @FXML
    private Button expressionUndoBtn;
    @FXML
    private Button expressionRedoBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    
    //TEXTS
    @FXML
    private TextFlow expressionTextFlow;
    @FXML
    private TextField textExpressionName;
    @FXML
    private TextArea textAudit;
    @FXML
    private TextArea unPeekTextArea;
    @FXML
    private TextArea rmPeekTextArea;
    private final TextArea expressionAsTextArea = new TextArea();;

    //RADIOS
    ToggleGroup toggleGroup;
    @FXML
    private RadioButton dragndropRightRadio;
    @FXML
    private RadioButton dragndropReplaceRadio;
    @FXML
    private RadioButton dragndropLeftRadio;

    //CHECKBOXES
    @FXML
    private CheckBox refMatSwitchCheckBox;
    @FXML
    private CheckBox unknownsSwitchCheckBox;
    @FXML
    private CheckBox concRefMatSwitchCheckBox;
    private CheckBox checkShowGraph;
    private CheckBox checkGraphBrowser;
    

    //LISTVIEWS
    @FXML
    private ListView<Expression> nuSwitchedExpressionsListView;
    @FXML
    private ListView<Expression> builtInExpressionsListView;
    @FXML
    private ListView<Expression> customExpressionsListView;
    @FXML
    private ListView<SquidRatiosModel> ratioExpressionsListView;
    @FXML
    private ListView<String> operationsListView;
    @FXML
    private ListView<String> mathFunctionsListView;
    @FXML
    private ListView<String> squidFunctionsListView;
    @FXML
    private ListView<String> constantsListView;
    @FXML
    private ListView<?> referenceMaterialsListView;

    //MISC
    @FXML
    private TitledPane builtInExpressionsTitledPane;
    @FXML
    private TitledPane graphPane;
    @FXML
    private TitledPane auditPane;
    @FXML
    private TitledPane peekPane;
    @FXML
    private TitledPane expressionPane;
    @FXML
    private Accordion expressionsAccordion;
    @FXML
    private WebView graphView;
    @FXML
    private SplitPane bigSplitPane;
    @FXML
    private SplitPane smallSplitPane;
    @FXML
    private VBox graphVBox;
    @FXML
    private VBox auditVBox;
    @FXML
    private VBox peekVBox;
    @FXML
    private VBox editorVBox;
    
    //PEEK TABS
    @FXML
    private Tab unkTab;
    @FXML
    private Tab refMatTab;
    @FXML
    private TabPane spotTabPane;
    
    
    
    
    
    
    
    
    
    
    
    private final String operationFlagDelimeter = " : ";
    private final String numberString = "NUMBER";
    private final String placeholder = " ";// \u2588 ";
    
    private boolean editAsText = false;
    
    private final ObjectProperty<ListCell<Expression>> dragExpressionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<SquidRatiosModel> dragSquidRatioModelSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragOperationOrFunctionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragNumberSource = new SimpleObjectProperty<>();
    private final ObjectProperty<ExpressionTextNode> dragTextSource = new SimpleObjectProperty<>();
    
    private final ListProperty<List<ExpressionTextNode>> undoListForExpressionTextFlow = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<List<ExpressionTextNode>> redoListForExpressionTextFlow = new SimpleListProperty<>(FXCollections.observableArrayList());
    
    private final ObjectProperty<Expression> editedExpression = new SimpleObjectProperty<>();
    
    private final List<String> listOperators = new ArrayList<>();
    
    
    
    
    
    
    
    
    
    
    
    
    //INIT
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        expressionsAccordion.setExpandedPane(builtInExpressionsTitledPane);
        initPanes();
        initRadios();
        initListViews();
        initExpressionTextFlow();
        initGraph();
        
        initPropertyBindings();
        
        initExpressionEdition();
    }
    
    private void initPropertyBindings(){
        editorVBox.disableProperty().bind(editedExpression.isNull());
        expressionUndoBtn.disableProperty().bind(undoListForExpressionTextFlow.emptyProperty());
        expressionRedoBtn.disableProperty().bind(redoListForExpressionTextFlow.emptyProperty());
    }

    private void initPanes(){
        graphPane.maxHeightProperty().bind(graphVBox.heightProperty().add(-3.0));
        auditPane.prefHeightProperty().bind(auditVBox.heightProperty().add(-3.0));
        peekPane.prefHeightProperty().bind(peekVBox.heightProperty().add(-3.0));
        graphPane.prefWidthProperty().bind(graphVBox.widthProperty().add(-3.0));
        auditPane.prefWidthProperty().bind(auditVBox.widthProperty().add(-3.0));
        peekPane.prefWidthProperty().bind(peekVBox.widthProperty().add(-3.0));
        
        expressionTextFlow.heightProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue()+28.0>expressionPane.heightProperty().get()){
                bigSplitPane.setPrefHeight(bigSplitPane.getHeight()-(newValue.doubleValue()+28.0-expressionPane.heightProperty().get()));
            }
        });
    }
    
    private void initRadios(){
        toggleGroup = new ToggleGroup();
        dragndropLeftRadio.setToggleGroup(toggleGroup);
        dragndropReplaceRadio.setToggleGroup(toggleGroup);
        dragndropRightRadio.setToggleGroup(toggleGroup);
        toggleGroup.selectToggle(dragndropRightRadio);
    }
    
    private void initListViews(){
        //EXPRESSIONS
        nuSwitchedExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        nuSwitchedExpressionsListView.setCursor(Cursor.CLOSED_HAND);
        nuSwitchedExpressionsListView.setCellFactory(new ExpressionCellFactory());
        builtInExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        builtInExpressionsListView.setCursor(Cursor.CLOSED_HAND);
        builtInExpressionsListView.setCellFactory(new ExpressionCellFactory());
        customExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        customExpressionsListView.setCursor(Cursor.CLOSED_HAND);
        customExpressionsListView.setCellFactory(new ExpressionCellFactory());
        populateExpressionListViews();
        
        //RATIOS
        ratioExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        ratioExpressionsListView.setCursor(Cursor.CLOSED_HAND);
        ratioExpressionsListView.setCellFactory(new expressionTreeCellFactory());
        populateRatiosListView();
        
        //OPERATIONS AND FUNCTIONS
        operationsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        operationsListView.setCursor(Cursor.CLOSED_HAND);
        operationsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));
        mathFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        mathFunctionsListView.setCursor(Cursor.CLOSED_HAND);
        mathFunctionsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));
        squidFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        squidFunctionsListView.setCursor(Cursor.CLOSED_HAND);
        squidFunctionsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));
        populateOperationOrFunctionListViews();
        
        //NUMBERS
        constantsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        constantsListView.setCursor(Cursor.CLOSED_HAND);
        constantsListView.setCellFactory(new StringCellFactory(dragNumberSource));
        populateNumberListViews();
    }
    
    private void initExpressionTextFlow(){
        expressionTextFlow.setOnDragOver((DragEvent event) -> {
            if (event.getDragboard().hasString()) {
                if(event.getGestureSource() instanceof ExpressionTextNode){
                    event.acceptTransferModes(TransferMode.MOVE);
                }else{
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });
        expressionTextFlow.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            
            double ord = expressionTextFlow.getChildren().size();
            if(toggleGroup.getSelectedToggle() == dragndropLeftRadio){
                ord = -1.0;
            }
            
            // if moving a node
            if(event.getGestureSource() instanceof ExpressionTextNode){
                ((ExpressionTextNode)event.getGestureSource()).setOrdinalIndex(ord);
                updateExpressionTextFlowChildren();
                success = true;
            }
            // if copying a node
            else if (db.hasString()) {
                String content = placeholder + db.getString().split(operationFlagDelimeter)[0] + placeholder;
                if ((dragOperationOrFunctionSource.get() != null) && (!db.getString().contains(operationFlagDelimeter))) {
                    // case of function, make a series of objects
                    insertFunctionIntoExpressionTextFlow(content, ord);
                }else if ((dragOperationOrFunctionSource.get() != null) && (db.getString().contains(operationFlagDelimeter))) {
                    // case of operation
                    insertOperationIntoExpressionTextFlow(content, ord);
                } else if ((dragNumberSource.get() != null) && content.contains(numberString)) {
                    // case of "NUMBER"
                    insertNumberIntoExpressionTextFlow(numberString, ord);
                }else{
                    // case of expression
                    insertExpressionIntoExpressionTextFlow(content, ord);
                }
                
                success = true;
            }
            
            event.setDropCompleted(success);
            
            event.consume();
            resetDragSources();
        });
    }
    
    private void initExpressionEdition(){
        
        expressionAsTextArea.prefHeightProperty().bind(expressionTextFlow.heightProperty());
        
        expressionAsTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            buildTextFlowFromString(newValue);
        });
        
        editedExpression.addListener((observable, oldValue, newValue) -> {
            undoListForExpressionTextFlow.clear();
            redoListForExpressionTextFlow.clear();
            if(editAsText){
                expressionAsTextAction(new ActionEvent());
            }
            if(newValue != null){
                textExpressionName.textProperty().set(newValue.getName());
                if(newValue.getExcelExpressionString().trim().length()>0){
                    buildTextFlowFromString(newValue.getExcelExpressionString());
                }
                refMatSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchSTReferenceMaterialCalculation());
                unknownsSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchSAUnknownCalculation());
                concRefMatSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchConcentrationReferenceMaterialCalculation());
            }else{
                textExpressionName.clear();
                expressionTextFlow.getChildren().clear();
                refMatSwitchCheckBox.setSelected(false);
                unknownsSwitchCheckBox.setSelected(false);
                concRefMatSwitchCheckBox.setSelected(false);
            }
            makeAndAuditExpression();
        });
    }
    
    private void initGraph(){
        
        graphPane.setText(null);
        Text label = new Text("Graph");
        checkShowGraph = new CheckBox("Show here");
        checkShowGraph.setSelected(true);
        checkGraphBrowser = new CheckBox("Show in browser");
        checkGraphBrowser.setSelected(false);
        HBox hbox = new HBox(label, checkShowGraph, checkGraphBrowser);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10.0);
        graphPane.setGraphic(hbox);
        
       checkShowGraph.setOnAction((event) -> {
           makeAndAuditExpression();
       });
       checkGraphBrowser.setOnAction((event) -> {
           makeAndAuditExpression();
       });
    }
    
    //CREATE SAVE CANCEL ACTIONS
    
    @FXML
    void newCustomExpressionAction(ActionEvent event) {
        editedExpression.set(null);
        editedExpression.set(new Expression("new_custom_expression", ""));
    }
    
    @FXML
    void cancelAction(ActionEvent event) {
        editedExpression.set(null);
    }

    @FXML
    void saveAction(ActionEvent event) {

    }
    
    
    
    
    //EXPRESSION ACTIONS
    
     @FXML
    void expressionClearAction(ActionEvent event) {
        expressionTextFlow.getChildren().clear();
        makeAndAuditExpression();
    }

    @FXML
    void expressionCopyAction(ActionEvent event) {
        String fullText = makeStringFromTextFlow();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(fullText);
        clipboard.setContent(content);
    }

    @FXML
    void expressionPasteAction(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String content = clipboard.getString();
        if(editAsText){
            expressionAsTextArea.setText(content);
        }else{
            buildTextFlowFromString(content);
        }
    }

    @FXML
    void expressionUndoAction(ActionEvent event) {
        expressionTextFlow.getChildren().clear();
        List<ExpressionTextNode> lastList = null;
        try {
            lastList = undoListForExpressionTextFlow.get(undoListForExpressionTextFlow.size() - 1);
            undoListForExpressionTextFlow.remove(lastList);
            redoListForExpressionTextFlow.add(lastList);
            if (undoListForExpressionTextFlow.size() > 0) {
                expressionTextFlow.getChildren().addAll(undoListForExpressionTextFlow.get(undoListForExpressionTextFlow.size() - 1));
            }
        } catch (Exception e) {
        }
        
        makeAndAuditExpression();
    }

    @FXML
    void expressionRedoAction(ActionEvent event) {
        List<ExpressionTextNode> lastList = null;
        try {
            lastList = redoListForExpressionTextFlow.get(redoListForExpressionTextFlow.size() - 1);
            redoListForExpressionTextFlow.remove(lastList);
            undoListForExpressionTextFlow.add(lastList);
            expressionTextFlow.getChildren().clear();
            expressionTextFlow.getChildren().addAll(lastList);
        } catch (Exception e) {
        }
        
        makeAndAuditExpression();
    }

    @FXML
    void expressionAsTextAction(ActionEvent event) {
        if(editAsText == false){
            
            editAsText = true;
            
            expressionAsTextArea.setText(makeStringFromTextFlow());
            expressionPane.setContent(expressionAsTextArea);
            expressionAsTextBtn.setText("Edit with drag and drop");
            
            expressionUndoBtn.disableProperty().unbind();
            expressionRedoBtn.disableProperty().unbind();
            expressionUndoBtn.setDisable(true);
            expressionRedoBtn.setDisable(true);
            
        }else{
            
            editAsText = false;
            
            expressionPane.setContent(expressionTextFlow);
            
            expressionAsTextBtn.setText("Edit as text");
            
            expressionUndoBtn.disableProperty().bind(undoListForExpressionTextFlow.emptyProperty());
            expressionRedoBtn.disableProperty().bind(redoListForExpressionTextFlow.emptyProperty());
            
            buildTextFlowFromString(makeStringFromTextFlow());
        }
    }
    
    
    
    
    //CHECKBOX ACTIONS
    @FXML
    void referenceMaterialCheckBoxAction(ActionEvent event) {

    }

    @FXML
    void unknownSamplesCheckBoxAction(ActionEvent event) {

    }

    @FXML
    void concRefMatCheckBoxAction(ActionEvent event) {

    }
    
    
    
    
    @FXML
    void howToUseAction(ActionEvent event) {

    }
    
    
    
    
    //POPULATE LISTS
    
    private void populateExpressionListViews() {
        List<Expression> namedExpressions = squidProject.getTask().getTaskExpressionsOrdered();
        
        List<Expression> sortedNUSwitchedExpressionsList = new ArrayList<>();
        List<Expression> sortedBuiltInExpressionsList = new ArrayList<>();
        List<Expression> sortedCustomExpressionsList = new ArrayList<>();
        
        for (Expression exp : namedExpressions) {
            if (exp.amHealthy() && exp.isSquidSwitchNU()) {
                sortedNUSwitchedExpressionsList.add(exp);
            } else if (exp.getExpressionTree().isSquidSpecialUPbThExpression() && exp.amHealthy() && !exp.isSquidSwitchNU()) {
                sortedBuiltInExpressionsList.add(exp);
            } else if (!exp.getExpressionTree().isSquidSpecialUPbThExpression() && exp.amHealthy() && !exp.isSquidSwitchNU()) {
                sortedCustomExpressionsList.add(exp);
            }
        }
        
        
        ObservableList<Expression> items = FXCollections.observableArrayList(sortedNUSwitchedExpressionsList);
        items = items.sorted((Expression exp1, Expression exp2) -> {
            return exp1.getName().compareToIgnoreCase(exp2.getName());
        });
        nuSwitchedExpressionsListView.setItems(items);
        
        
        items = FXCollections.observableArrayList(sortedBuiltInExpressionsList);
        items = items.sorted((Expression exp1, Expression exp2) -> {
            return exp1.getName().compareToIgnoreCase(exp2.getName());
        });
        builtInExpressionsListView.setItems(items);
        
        
        items = FXCollections.observableArrayList(sortedCustomExpressionsList);
        items = items.sorted((Expression exp1, Expression exp2) -> {
            return exp1.getName().compareToIgnoreCase(exp2.getName());
        });
        customExpressionsListView.setItems(items);
    }
    
    private void populateRatiosListView() {
        List<SquidRatiosModel> ratiosList = squidProject.getTask().getSquidRatiosModelList();
        
        ObservableList<SquidRatiosModel> items = FXCollections.observableArrayList(ratiosList);
        items = items.sorted((ratio1, ratio2) -> {
            return ratio1.getRatioName().compareToIgnoreCase(ratio2.getRatioName());
        });
        ratioExpressionsListView.setItems(items);
    }
    
    private void populateOperationOrFunctionListViews() {
        // operations ==========================================================
        List<String> operationStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : OPERATIONS_MAP.entrySet()) {
            operationStrings.add(op.getKey() + operationFlagDelimeter + op.getValue());
            listOperators.add(op.getKey());
        }
        
        ObservableList<String> items = FXCollections.observableArrayList(operationStrings);
        operationsListView.setItems(items);
        
        // Math Functions ======================================================
        List<String> functionStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : FUNCTIONS_MAP.entrySet()) {
            int argumentCount = Function.operationFactory(op.getValue()).getArgumentCount();
            // space-delimited
            String args = op.getKey() + "(";
            for (int i = 0; i < argumentCount; i++) {
                args += "ARG" + i + (i < (argumentCount - 1) ? "," : ")");
            }
            
            functionStrings.add(args);
        }
        
        items = FXCollections.observableArrayList(functionStrings);
        items = items.sorted();
        mathFunctionsListView.setItems(items);
        
    }
    
    private void populateNumberListViews() {
        // constants and numbers ===============================================
        List<String> constantStrings = new ArrayList<>();
        constantStrings.add(numberString + operationFlagDelimeter + "placeholder for number");
        
        for (Map.Entry<String, ExpressionTreeInterface> constant : squidProject.getTask().getNamedConstantsMap().entrySet()) {
            constantStrings.add(constant.getKey() + operationFlagDelimeter + ((ConstantNode) constant.getValue()).getValue());
        }
        
        for (Map.Entry<String, ExpressionTreeInterface> constant : squidProject.getTask().getNamedParametersMap().entrySet()) {
            constantStrings.add(constant.getKey() + operationFlagDelimeter + ((ConstantNode) constant.getValue()).getValue());
        }
        
        ObservableList<String> items = FXCollections.observableArrayList(constantStrings);
        constantsListView.setItems(items);
    }
    
    private void populatePeeks(Expression exp){
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

            if (exp.getExpressionTree() instanceof ConstantNode) {
                rmPeekTextArea.setText("Not used");
                unPeekTextArea.setText("Not used");
                if (exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                    try {
                        rmPeekTextArea.setText(exp.getName() + " = " + Utilities.roundedToSize((Double) ((ConstantNode) exp.getExpressionTree()).getValue(), 15));
                    } catch (Exception e) {
                    }
                }
                if (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                    try {
                        unPeekTextArea.setText(exp.getName() + " = " + Utilities.roundedToSize((Double) ((ConstantNode) exp.getExpressionTree()).getValue(), 15));
                    } catch (Exception e) {
                    }
                }

            } else if (exp.getExpressionTree().isSquidSwitchSCSummaryCalculation()) {
                SpotSummaryDetails spotSummary = task.getTaskExpressionsEvaluationsPerSpotSet().get(exp.getExpressionTree().getName());

                rmPeekTextArea.setText("No Summary");
                unPeekTextArea.setText("No Summary");

                if (spotSummary != null) {
                    if (exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            rmPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            rmPeekTextArea.setText("No Reference Materials");
                        }
                    }

                    if (exp.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            rmPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            rmPeekTextArea.setText("No Concentration Reference Materials");
                        }
                    }

                    if (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            unPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            unPeekTextArea.setText("No Unknowns");
                        }
                    }
                }

            } else {
                rmPeekTextArea.setText("Reference Materials not processed.");
                if (exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                    if (refMatSpots.size() > 0) {
                        rmPeekTextArea.setText(peekDetailsPerSpot(refMatSpots, exp.getExpressionTree()));
                    } else {
                        rmPeekTextArea.setText("No Reference Materials");
                    }
                } else if (exp.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                    if (concRefMatSpots.size() > 0) {
                        rmPeekTextArea.setText(peekDetailsPerSpot(concRefMatSpots, exp.getExpressionTree()));
                    } else {
                        rmPeekTextArea.setText("No Concentration Reference Materials");
                    }
                }

                unPeekTextArea.setText("Unknowns not processed.");
                if (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
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
            sb.append(Utilities.roundedToSize(spotSummary.getValues()[0][i], 15));
            sb.append("\n");
        }

        // handle special cases
        if (labels.length > 1) {
            sb.append("\t");
            sb.append(String.format("%1$-" + 13 + "s", labels[1][0]));
            sb.append(": ");
            // print list
            if (spotSummary.getValues()[1].length == 0) {
                sb.append("None");
            } else {
                for (int j = 0; j < spotSummary.getValues()[1].length; j++) {
                    sb.append((int) (spotSummary.getValues()[1][j]) + " ");
                }
            }
            sb.append("\n");
        }

        if (labels.length > 2) {
            sb.append("\t");
            sb.append(String.format("%1$-" + 13 + "s", labels[2][0]));
            sb.append(": ");
            // print list
            if (spotSummary.getValues()[2].length == 0) {
                sb.append("None");
            } else {
                for (int j = 0; j < spotSummary.getValues()[2].length; j++) {
                    sb.append((int) (spotSummary.getValues()[2][j])).append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String peekDetailsPerSpot(List<ShrimpFractionExpressionInterface> spots, ExpressionTreeInterface editedExp) {
        StringBuilder sb = new StringBuilder();

        if (editedExp instanceof ShrimpSpeciesNode) {
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
                        sb.append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(results[0][i], 15)));
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
                            sb.append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(results[0][i], 15)));
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
            if (((ExpressionTree) editedExp).getOperation() != null) {
                String[][] resultLabels = ((ExpressionTree) editedExp).getOperation().getLabelsForOutputValues();
                for (int i = 0; i < resultLabels[0].length; i++) {
                    try {
                        sb.append(String.format("%1$-" + 20 + "s", resultLabels[0][i]));
                    } catch (Exception e) {
                    }
                }
                if (((ExpressionTree) editedExp).hasRatiosOfInterest()) {
                    sb.append(String.format("%1$-" + 20 + "s", "1-sigma ABS"));
                }
            }
            sb.append("\n");

            for (ShrimpFractionExpressionInterface spot : spots) {
                if (spot.getTaskExpressionsEvaluationsPerSpot().get(editedExp) != null) {
                    sb.append(String.format("%1$-" + 15 + "s", spot.getFractionID()));
                    double[][] results
                            = spot.getTaskExpressionsEvaluationsPerSpot().get(editedExp);
                    for (int i = 0; i < results[0].length; i++) {
                        try {
                            sb.append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(results[0][i], 15)));
                        } catch (Exception e) {
                        }
                    }
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    private ContextMenu createExpressionTextNodeContextMenu(ExpressionTextNode etn) {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem menuItem = new MenuItem("Remove expression.");
        menuItem.setOnAction((evt) -> {
            expressionTextFlow.getChildren().remove(etn);
            updateExpressionTextFlowChildren();
        });
        contextMenu.getItems().add(menuItem);
        
        menuItem = new MenuItem("Move left.");
        menuItem.setOnAction((evt) -> {
            etn.setOrdinalIndex(etn.getOrdinalIndex() - 1.5);
            updateExpressionTextFlowChildren();
        });
        contextMenu.getItems().add(menuItem);
        
        menuItem = new MenuItem("Move right.");
        menuItem.setOnAction((evt) -> {
            etn.setOrdinalIndex(etn.getOrdinalIndex() + 1.5);
            updateExpressionTextFlowChildren();
        });
        contextMenu.getItems().add(menuItem);
        
        menuItem = new MenuItem("Wrap in parentheses.");
        menuItem.setOnAction((evt) -> {
            wrapInParentheses(etn.getOrdinalIndex(),etn.getOrdinalIndex());
        });
        contextMenu.getItems().add(menuItem);
        
        // make editable node
        if (etn instanceof NumberTextNode) {
            TextField editText = new TextField(etn.getText());
            editText.setPrefWidth((editText.getText().trim().length() + 2) * editText.getFont().getSize());
            editText.textProperty().addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
                editText.setPrefWidth((editText.getText().trim().length() + 2) * editText.getFont().getSize());
            });
            menuItem = new MenuItem("<< Edit value then click here to save.", editText);
            menuItem.setOnAction((evt) -> {
                //etn.setText(editText.getText());
                // this allows for redo of content editing
                NumberTextNode etn2 = new NumberTextNode(editText.getText());
                etn2.setOrdinalIndex(etn.getOrdinalIndex());
                expressionTextFlow.getChildren().remove(etn);
                expressionTextFlow.getChildren().add(etn2);
                updateExpressionTextFlowChildren();
            });
            contextMenu.getItems().add(menuItem);
        }
        
        contextMenu.setOnHiding((WindowEvent event) -> {
            etn.popupShowing = false;
        });
        
        return contextMenu;
    }
    
    private void updateExpressionTextFlowChildren() {
        // extract and sort
        List<ExpressionTextNode> children = new ArrayList<>();
        for (Node etn : expressionTextFlow.getChildren()) {
            children.add((ExpressionTextNode) etn);
        }
        // sort
        children.sort((Node o1, Node o2) -> {
            int retVal = 0;
            if (o1 instanceof ExpressionTextNode && o2 instanceof ExpressionTextNode) {
                retVal = Double.compare(((ExpressionTextNode) o1).getOrdinalIndex(), ((ExpressionTextNode) o2).getOrdinalIndex());
            }
            return retVal;
        });
        
        // reset ordinals to integer values
        double ordIndex = 0;
        for (ExpressionTextNode etn : children) {
            etn.setOrdinalIndex(ordIndex);
            //etn.setFill(Color.BLACK);
            ordIndex++;
        }
        
        expressionTextFlow.getChildren().setAll(children);
        
        undoListForExpressionTextFlow.add(children);
        redoListForExpressionTextFlow.clear();
        
        makeAndAuditExpression();
        
    }
    
    public void makeAndAuditExpression() {

        if (editedExpression.isNotNull().get()) {
            
            String fullText = makeStringFromTextFlow();
            
            Expression exp = squidProject.getTask().generateExpressionFromRawExcelStyleText(
                    textExpressionName.getText(),
                    fullText.trim().replace("\n", ""),
                    false);
            
            ExpressionTreeInterface expTree = exp.getExpressionTree();
            
            expTree.setSquidSwitchSTReferenceMaterialCalculation(refMatSwitchCheckBox.isSelected());
            expTree.setSquidSwitchSAUnknownCalculation(unknownsSwitchCheckBox.isSelected());
            expTree.setSquidSwitchConcentrationReferenceMaterialCalculation(concRefMatSwitchCheckBox.isSelected());
            
            textAudit.setText(exp.produceExpressionTreeAudit());
            
            graphExpressionTree(exp.getExpressionTree());
            
            populatePeeks(exp);
            
        }else {
            textAudit.setText("No expression");
            graphExpressionTree(null);
        }
    }
    
    private String makeStringFromTextFlow() {
        return makeStringFromTextList(expressionTextFlow.getChildren());
    }
    
    private String makeStringFromTextList(List<Node> list) {
        StringBuilder sb = new StringBuilder();
        for (Node node : list) {
            if (node instanceof Text) {
                sb.append(((Text) node).getText());
            }
        }
        return sb.toString();
    }
    
    private void buildTextFlowFromString(String string){
        
        String numberRegExp = "^\\d+(\\.\\d+)?$";
        
        List<ExpressionTextNode> children = new ArrayList<>();
        
        ExpressionsForSquid2Lexer lexer = new ExpressionsForSquid2Lexer(new ANTLRInputStream(string));
        
        List<? extends Token> tokens = lexer.getAllTokens();
        
        for(int i = 0 ; i<tokens.size() ; i++){
            Token token = tokens.get(i);
            String nodeText = token.getText().trim();
            
            ExpressionTextNode etn;
            
            if(nodeText.matches(numberRegExp)){
                etn = new NumberTextNode(' '+nodeText+' ');
            }else if(listOperators.contains(nodeText)){
                etn = new OperationTextNode(' '+nodeText+' ');
            }else{
                etn = new ExpressionTextNode(' '+nodeText+' ');
            }
            
            etn.setOrdinalIndex(i);
            children.add(etn);
        }
        
        undoListForExpressionTextFlow.add(children);
        redoListForExpressionTextFlow.clear();
        
        expressionTextFlow.getChildren().setAll(children);
        
        makeAndAuditExpression();
    }
    
    private void insertFunctionIntoExpressionTextFlow(String content, double ordinalIndex) {
        content = content.replaceAll("([(,)])", " $1 ");
        String[] funcCall = content.split(" ");
        for (int i = 0; i < funcCall.length; i++) {
            if (funcCall[i].compareTo("") != 0) {
                funcCall[i] = funcCall[i].replaceAll("([(,)])", " $1 ");
                ExpressionTextNode expressionTextNode = new ExpressionTextNode(funcCall[i]);
                // max 8 terms
                expressionTextNode.setOrdinalIndex(ordinalIndex - (9 - i) * 0.01);
                expressionTextFlow.getChildren().add(expressionTextNode);
            }
        }
        updateExpressionTextFlowChildren();
    }
    
    private void insertOperationIntoExpressionTextFlow(String content, double ordinalIndex) {
        ExpressionTextNode exp = new OperationTextNode(' '+content.trim()+' ');
        exp.setOrdinalIndex(ordinalIndex);
        expressionTextFlow.getChildren().add(exp);
        updateExpressionTextFlowChildren();
    }
    
    private void insertNumberIntoExpressionTextFlow(String content, double ordinalIndex) {
        ExpressionTextNode exp = new NumberTextNode(' '+content.trim()+' ');
        exp.setOrdinalIndex(ordinalIndex);
        expressionTextFlow.getChildren().add(exp);
        updateExpressionTextFlowChildren();
    }
    
    private void insertExpressionIntoExpressionTextFlow(String content, double ordinalIndex) {
        ExpressionTextNode exp = new ExpressionTextNode(' '+content.trim()+' ');
        exp.setOrdinalIndex(ordinalIndex);
        expressionTextFlow.getChildren().add(exp);
        updateExpressionTextFlowChildren();
    }
    
    private void wrapInParentheses(double ordLeft, double ordRight){
        ExpressionTextNode leftP = new ExpressionTextNode(" ( ");
        leftP.setOrdinalIndex(ordLeft - 0.5);
        ExpressionTextNode rightP = new ExpressionTextNode(" ) ");
        rightP.setOrdinalIndex(ordRight + 0.5);
        expressionTextFlow.getChildren().addAll(leftP, rightP);
        updateExpressionTextFlowChildren();
    }
    
    private void resetDragSources() {
        dragExpressionSource.set(null);
        dragOperationOrFunctionSource.set(null);
        dragSquidRatioModelSource.set(null);
        dragNumberSource.set(null);
    }

    private void graphExpressionTree(ExpressionTreeInterface expTree) {
        
        String contentLocalGraphingOff = "<html>\n"
                    + "<h3> &nbsp; </h3>\n"
                    + "<h3 style=\"text-align:center;\">Local graphing is off.</h3>\n"
                    + "</html>";
        
        String contentNoExpression = "";
        
        // decide where to graph expression
        String graphContents;
        if(expTree != null){
            graphContents = ExpressionTreeWriterMathML.toStringBuilderMathML(expTree).toString();
        }else{
            graphContents = contentNoExpression;
        }
        
        
        if (checkShowGraph.isSelected()) {
            graphView.getEngine().loadContent(graphContents);
        } else {
            graphView.getEngine().loadContent(contentLocalGraphingOff);
        }
        
                
        if (checkGraphBrowser.isSelected()) {
            try {
                Files.write(Paths.get("EXPRESSION.HTML"), graphContents.getBytes());
                BrowserControl.showURI("EXPRESSION.HTML");
            } catch (IOException iOException) {
            }
        }

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private class ExpressionCellFactory implements Callback<ListView<Expression>, ListCell<Expression>> {
        
        private final List<ContextMenu> contextMenus = new ArrayList<>();
        
        @Override
        public ListCell<Expression> call(ListView<Expression> param) {
            ListCell<Expression> cell = new ListCell<Expression>() {
                @Override
                public void updateItem(Expression expression, boolean empty) {
                    super.updateItem(expression, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(expression.getName());
                    }
                }
            };
            
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString("[\"" + cell.getItem().getName() + "\"]");
                    db.setContent(cc);
                    dragExpressionSource.set(cell);
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });
            
            cell.setOnDragDone((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });
            
            
            cell.setOnMouseClicked((MouseEvent event) -> {
                if (!cell.isEmpty()) {
                    if(event.getButton() == MouseButton.SECONDARY){
                        
                        //hide previous contextMenus
                        for (ContextMenu contextMenu : contextMenus) {
                            contextMenu.hide();
                        }
                        contextMenus.clear();
                        
                        ContextMenu contextMenu = new ContextMenu();
                        
                        if(customExpressionsListView.getItems().contains(cell.getItem())){
                            MenuItem menuItemEdit = new MenuItem("Edit expression");
                            menuItemEdit.setOnAction((evt) -> {
                                editedExpression.set(null);
                                editedExpression.set(cell.getItem());
                            });
                            contextMenu.getItems().add(menuItemEdit);
                        }
                        
                        MenuItem menuItem = new MenuItem("Create new expression from this one");
                        menuItem.setOnAction((evt) -> {
                            Expression exp = new Expression("new_custom_expression", cell.getItem().getExcelExpressionString());
                            
                            ExpressionTree expTreeCopy = (ExpressionTree) exp.getExpressionTree();
                            ExpressionTree expTree = (ExpressionTree) cell.getItem().getExpressionTree();
                            
                            expTreeCopy.setSquidSwitchSTReferenceMaterialCalculation(expTree.isSquidSwitchSTReferenceMaterialCalculation());
                            expTreeCopy.setSquidSwitchSAUnknownCalculation(expTree.isSquidSwitchSAUnknownCalculation());
                            expTreeCopy.setSquidSwitchConcentrationReferenceMaterialCalculation(expTree.isSquidSwitchConcentrationReferenceMaterialCalculation());
            
                            editedExpression.set(null);
                            editedExpression.set(exp);
                        });
                        contextMenu.getItems().add(menuItem);
                        
                        contextMenu.show(cell, event.getScreenX(), event.getScreenY());
                        
                        contextMenus.add(contextMenu);
                    }
                }
            });
            
            cell.setOnMousePressed((event) -> {
                if (!cell.isEmpty()) {
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });
            
            cell.setOnMouseReleased((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });
            
            cell.setCursor(Cursor.OPEN_HAND);

            return cell;
        }
        
    }
    
    
    
    
    private class expressionTreeCellFactory implements Callback<ListView<SquidRatiosModel>, ListCell<SquidRatiosModel>> {
        
        @Override
        public ListCell<SquidRatiosModel> call(ListView<SquidRatiosModel> param) {
            ListCell<SquidRatiosModel> cell = new ListCell<SquidRatiosModel>() {
                @Override
                public void updateItem(SquidRatiosModel expression, boolean empty) {
                    super.updateItem(expression, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(expression.getRatioName());
                    }
                }
            };
            
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString("[\"" + cell.getItem().getRatioName() + "\"]");
                    db.setContent(cc);
                    dragSquidRatioModelSource.set(cell.getItem());
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });
            
            cell.setOnDragDone((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });
            
            cell.setOnMousePressed((event) -> {
                if (!cell.isEmpty()) {
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });
            
            cell.setOnMouseReleased((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });
            
            cell.setCursor(Cursor.OPEN_HAND);
            
            return cell;
        }
        
    }
    
    
    
    
    private class StringCellFactory implements Callback<ListView<String>, ListCell<String>> {
        
        private ObjectProperty<String> dragSource;
        
        public StringCellFactory(ObjectProperty<String> dragSource) {
            this.dragSource = dragSource;
        }
        
        @Override
        public ListCell<String> call(ListView<String> param) {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                public void updateItem(String operationOrFunction, boolean empty) {
                    super.updateItem(operationOrFunction, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(operationOrFunction);
                    }
                }
            };
            
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(cell.getItem());
                    db.setContent(cc);
                    dragSource.set(cell.getItem());
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });
            
            cell.setOnDragDone((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });
            
            cell.setOnMousePressed((event) -> {
                if (!cell.isEmpty()) {
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });
            
            cell.setOnMouseReleased((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });
            
            cell.setCursor(Cursor.OPEN_HAND);
            
            return cell;
        }
    }
    
    
    
    
    private class OperationTextNode extends ExpressionTextNode {
        
        public OperationTextNode(String text) {
            super(text);
            setStyle(OPERATOR_IN_EXPRESSION_LIST_CSS_STYLE_SPECS);
        }
    }
    
    // this node signals user can edit in context menu
    private class NumberTextNode extends ExpressionTextNode {
        
        public NumberTextNode(String text) {
            super(text);
        }
    }
    
    private class ExpressionTextNode extends Text {
        
        /**
         * @return the ordinalIndex
         */
        public double getOrdinalIndex() {
            return ordinalIndex;
        }
        
        /**
         * @param ordinalIndex the ordinalIndex to set
         */
        public void setOrdinalIndex(double ordinalIndex) {
            this.ordinalIndex = ordinalIndex;
        }
        
        private String text;
        private double ordinalIndex;
        private boolean popupShowing;
        
        public ExpressionTextNode(String text) {
            super(text);
            this.text = text;
            this.popupShowing = false;
            
            setStyle(EXPRESSION_LIST_CSS_STYLE_SPECS);
            setCursor(Cursor.OPEN_HAND);
            
            setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton() == MouseButton.SECONDARY && event.getEventType() == MouseEvent.MOUSE_CLICKED && !popupShowing) {
                    createExpressionTextNodeContextMenu((ExpressionTextNode) event.getSource()).show((ExpressionTextNode) event.getSource(), event.getScreenX(), event.getScreenY());
                    popupShowing = true;
                }
            });
            
            setOnMousePressed((MouseEvent event) -> {
                setFill(Color.RED);
                setCursor(Cursor.CLOSED_HAND);
            });
            
            setOnMouseReleased((MouseEvent event) -> {
                setFill(Color.BLACK);
                setCursor(Cursor.OPEN_HAND);
            });
            
            setOnDragDetected((MouseEvent event) -> {
                setCursor(Cursor.CLOSED_HAND);
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                ClipboardContent cc = new ClipboardContent();
                cc.putString(text);
                db.setContent(cc);
                dragTextSource.set((ExpressionTextNode) event.getSource());
            });
            
            setOnDragDone((event) -> {
                setCursor(Cursor.OPEN_HAND);
            });
            
            setOnDragOver((DragEvent event) -> {
                if (event.getGestureSource() != (ExpressionTextNode) event.getSource()) {
                    event.acceptTransferModes(TransferMode.COPY,TransferMode.MOVE);
                }
                event.consume();
            });
            
            setOnDragDropped((DragEvent event) -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                double ord = 0.0;
                if(toggleGroup.getSelectedToggle() == dragndropLeftRadio){
                    ord = ordinalIndex - 0.5;
                }else if(toggleGroup.getSelectedToggle() == dragndropReplaceRadio){
                    ord = ordinalIndex;
                    expressionTextFlow.getChildren().remove(this);
                }else if(toggleGroup.getSelectedToggle() == dragndropRightRadio){
                    ord = ordinalIndex + 0.5;
                }
                if(event.getGestureSource() instanceof ExpressionTextNode){
                    ((ExpressionTextNode)event.getGestureSource()).setOrdinalIndex(ord);
                    updateExpressionTextFlowChildren();
                    success = true;
                }
                else if(db.hasString()){
                    String content = placeholder + db.getString().split(operationFlagDelimeter)[0] + placeholder;
                    if ((dragOperationOrFunctionSource.get() != null) && (!db.getString().contains(operationFlagDelimeter))) {
                        // case of function, make a series of objects
                        insertFunctionIntoExpressionTextFlow(content, ord);
                    }else if ((dragOperationOrFunctionSource.get() != null) && (db.getString().contains(operationFlagDelimeter))) {
                        // case of operation
                        insertOperationIntoExpressionTextFlow(content, ord);
                    } else if ((dragNumberSource.get() != null) && content.contains(numberString)) {
                        // case of "NUMBER"
                        insertNumberIntoExpressionTextFlow(numberString, ord);
                    }else{
                        // case of expression
                        insertExpressionIntoExpressionTextFlow(content, ord);
                    }

                    success = true;
                }
                
                event.setDropCompleted(success);
                
                event.consume();
                resetDragSources();
            });
        }
    }
    
}
