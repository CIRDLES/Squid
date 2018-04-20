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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.cirdles.squid.ExpressionsForSquid2Lexer;
import org.cirdles.squid.gui.SquidUI;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.OPERATOR_IN_EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.functions.Function;
import static org.cirdles.squid.tasks.expressions.functions.Function.FUNCTIONS_MAP;
import static org.cirdles.squid.tasks.expressions.operations.Operation.OPERATIONS_MAP;



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
    private TextArea textPeekUnk;
    @FXML
    private TextArea textPeekRM;

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
    private CheckBox referenceMaterialCheckBox;
    @FXML
    private CheckBox unknownSamplesCheckBox;
    @FXML
    private CheckBox concRefMatCheckBox;

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
    
    
    
    
    
    
    
    
    
    
    
    private final String operationFlagDelimeter = " : ";
    private final String numberString = "NUMBER";
    private final String placeholder = " ";// \u2588 ";
    
    private final ObjectProperty<ListCell<Expression>> dragExpressionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<SquidRatiosModel> dragSquidRatioModelSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragOperationOrFunctionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragNumberSource = new SimpleObjectProperty<>();
    private final ObjectProperty<ExpressionTextNode> dragTextSource = new SimpleObjectProperty<>();
    
    private final List<List<ExpressionTextNode>> undoListForExpressionTextFlow = new ArrayList<>();
    private final List<List<ExpressionTextNode>> redoListForExpressionTextFlow = new ArrayList<>();
    
    private final ObjectProperty<Expression> editedExpression = new SimpleObjectProperty<>();
    
    
    
    
    
    
    
    
    
    
    
    
    //INIT
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        expressionsAccordion.setExpandedPane(builtInExpressionsTitledPane);
        initPanes();
        initRadios();
        initListViews();
        initExpressionTextFlow();
        
        initPropertyBindings();
        
        initExpressionEdition();
    }
    
    private void initPropertyBindings(){
        editorVBox.disableProperty().bind(editedExpression.isNull());
    }

    private void initPanes(){
        graphPane.prefHeightProperty().bind(graphVBox.heightProperty().add(-3.0));
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
        expressionTextFlow.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getDragboard().hasString()) {
                    if(event.getGestureSource() instanceof ExpressionTextNode){
                        event.acceptTransferModes(TransferMode.MOVE);
                    }else{
                        event.acceptTransferModes(TransferMode.COPY);
                    }
                }
                event.consume();
            }
        });
        expressionTextFlow.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                double ord = expressionTextFlow.getChildren().size();
                if(toggleGroup.getSelectedToggle() == dragndropLeftRadio){
                    ord = -1.0;
                    System.out.println("coucou");
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
            }
        });
    }
    
    private void initExpressionEdition(){
        editedExpression.addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                textExpressionName.textProperty().set(newValue.getName());
                buildTextFlowFromString(newValue.getExcelExpressionString());
            }else{
                textExpressionName.clear();
                expressionTextFlow.getChildren().clear();
            }
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
    }

    @FXML
    void expressionCopyAction(ActionEvent event) {

    }

    @FXML
    void expressionPasteAction(ActionEvent event) {

    }

    @FXML
    void expressionUndoAction(ActionEvent event) {

    }

    @FXML
    void expressionRedoAction(ActionEvent event) {

    }

    @FXML
    void expressionAsTextAction(ActionEvent event) {

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
        }
        
        ObservableList<String> items = FXCollections.observableArrayList(operationStrings);
        operationsListView.setItems(items);
        
        // Math Functions ======================================================
        List<String> functionStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : FUNCTIONS_MAP.entrySet()) {
            int argumentCount = Function.operationFactory(op.getValue()).getArgumentCount();
            // space-delimited
            String args = op.getKey() + " ( ";
            for (int i = 0; i < argumentCount; i++) {
                args += "ARG_" + i + (i < (argumentCount - 1) ? " , " : " )");
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
        constantStrings.add(numberString + operationFlagDelimeter + "placeholder for custom number");
        
        for (Map.Entry<String, ExpressionTreeInterface> constant : squidProject.getTask().getNamedConstantsMap().entrySet()) {
            constantStrings.add(constant.getKey() + operationFlagDelimeter + ((ConstantNode) constant.getValue()).getValue());
        }
        
        for (Map.Entry<String, ExpressionTreeInterface> constant : squidProject.getTask().getNamedParametersMap().entrySet()) {
            constantStrings.add(constant.getKey() + operationFlagDelimeter + ((ConstantNode) constant.getValue()).getValue());
        }
        
        ObservableList<String> items = FXCollections.observableArrayList(constantStrings);
        constantsListView.setItems(items);
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
        
        contextMenu.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                etn.popupShowing = false;
            }
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
        
        makeAndAuditExpression();
        
    }
    
    public void makeAndAuditExpression() {
        
        String fullText = makeStringFromTextFlow();
        
        if (fullText.trim().length() > 0) {
            Expression exp = squidProject.getTask().generateExpressionFromRawExcelStyleText(
                    "Editing Expression",
                    fullText.trim().replace("\n", ""),
                    false);
            
            textAudit.setText(exp.produceExpressionTreeAudit());
        } else {
            textAudit.setText("Empty expression");
        }
    }
    
    private String makeStringFromTextFlow() {
        // make and audit expression
        StringBuilder sb = new StringBuilder();
        for (Node node : expressionTextFlow.getChildren()) {
            if (node instanceof Text) {
                sb.append(((Text) node).getText());
            }
        }
        return sb.toString();
    }
    
    private void buildTextFlowFromString(String string){
        
        expressionTextFlow.getChildren().clear();
        
        ExpressionsForSquid2Lexer lexer = new ExpressionsForSquid2Lexer(new ANTLRInputStream(string));
        
        List<? extends Token> tokens = lexer.getAllTokens();
        
        for(int i = 0 ; i<tokens.size() ; i++){
            Token token = tokens.get(i);
            String nodeText = token.getText();
            nodeText += " ";
            
            //TODO TYPES
            
            ExpressionTextNode etn = new ExpressionTextNode(nodeText);
            expressionTextFlow.getChildren().add(etn);
        }
        
        updateExpressionTextFlowChildren();
    }
    
    private void insertFunctionIntoExpressionTextFlow(String content, double ordinalIndex) {
        System.out.println(content);
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
