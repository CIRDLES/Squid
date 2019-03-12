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

import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.RED;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.cirdles.ludwig.squid25.Utilities;
import org.cirdles.squid.ExpressionsForSquid2Lexer;
import static org.cirdles.squid.constants.Squid3Constants.SUPERSCRIPT_C_FOR_CONCREFMAT;
import static org.cirdles.squid.constants.Squid3Constants.SUPERSCRIPT_R_FOR_REFMAT;
import static org.cirdles.squid.constants.Squid3Constants.SUPERSCRIPT_U_FOR_UNKNOWN;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.SquidUI;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_TOOLTIP_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.utilities.BrowserControl;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWriterMathML;
import org.cirdles.squid.tasks.expressions.functions.Function;
import static org.cirdles.squid.tasks.expressions.functions.Function.FUNCTIONS_MAP;
import static org.cirdles.squid.tasks.expressions.functions.Function.LOGIC_FUNCTIONS_MAP;
import static org.cirdles.squid.tasks.expressions.functions.Function.MATH_FUNCTIONS_MAP;
import static org.cirdles.squid.tasks.expressions.functions.Function.SQUID_FUNCTIONS_MAP;
import org.cirdles.squid.tasks.expressions.functions.WtdMeanACalc;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import static org.cirdles.squid.tasks.expressions.operations.Operation.OPERATIONS_MAP;
import org.cirdles.squid.tasks.expressions.parsing.ShuntingYard;
import org.cirdles.squid.tasks.expressions.parsing.ShuntingYard.TokenTypes;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.EXPRESSION_BUILDER_DEFAULT_FONTSIZE;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.EXPRESSION_BUILDER_MAX_FONTSIZE;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.EXPRESSION_BUILDER_MIN_FONTSIZE;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;
import static org.cirdles.squid.constants.Squid3Constants.SUPERSCRIPT_SPACE;
import org.cirdles.squid.constants.Squid3Constants.SpotTypes;
import static org.cirdles.squid.gui.SquidUI.HEALTHY;
import static org.cirdles.squid.gui.SquidUI.PEEK_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.UNHEALTHY;
import org.cirdles.squid.tasks.expressions.functions.ShrimpSpeciesNodeFunction;
import org.cirdles.squid.utilities.IntuitiveStringComparator;
import static org.cirdles.squid.utilities.conversionUtilities.CloningUtilities.clone2dArray;
import static org.cirdles.squid.gui.SquidUIController.createCopyToClipboardContextMenu;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class ExpressionBuilderController implements Initializable {

    // handle for closing stage when Squid closes
    public static final Stage EXPRESSION_NOTES_STAGE = new Stage();

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
    @FXML
    private Button createExpressionBtn;
    @FXML
    private Button editExpressionBtn;
    @FXML
    private Button copyExpressionIntoCustomBtn;
    @FXML
    private Button showCurrentExpressionBtn;
    @FXML
    private Button showNotesBtn;
    @FXML
    private Button toggleWhiteSpacesBtn;
    @FXML
    private Button fontMinusBtn;
    @FXML
    private Button fontPlusBtn;

    //TEXTS
    @FXML
    private TextFlow expressionTextFlow;
    @FXML
    private TextField expressionNameTextField;
    @FXML
    private TextArea auditTextArea;
    @FXML
    private TextArea unPeekTextArea;
    @FXML
    private TextArea rmPeekTextArea;
    @FXML
    private Text hintHoverText;
    @FXML
    private Text hintSelectText;
    @FXML
    private ToggleGroup expressionsSortToggleGroup;

    private final TextArea expressionAsTextArea = new TextArea();

    {
        expressionAsTextArea.setFont(Font.font(expressionAsTextArea.getFont().getFamily(), EXPRESSION_BUILDER_DEFAULT_FONTSIZE));
    }

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
    @FXML
    private CheckBox summaryCalculationSwitchCheckBox;
    @FXML
    private CheckBox specialUPbThSwitchCheckBox;
    @FXML
    private CheckBox NUSwitchCheckBox;
    @FXML
    private CheckBox showGraphCheckBox;
    @FXML
    private CheckBox graphBrowserCheckBox;

    //LISTVIEWS
    @FXML
    private ListView<Expression> nuSwitchedExpressionsListView;
    @FXML
    private TitledPane nuSwitchedExpressionsTitledPane;

    @FXML
    private ListView<Expression> builtInExpressionsListView;
    @FXML
    private TitledPane builtInExpressionsTitledPane;

    @FXML
    private ListView<Expression> brokenExpressionsListView;
    @FXML
    private TitledPane brokenExpressionsTitledPane;

    @FXML
    private ListView<Expression> customExpressionsListView;
    @FXML
    private TitledPane customExpressionsTitledPane;

    @FXML
    private ListView<SquidRatiosModel> ratioExpressionsListView;
    @FXML
    private TitledPane ratioExpressionsTitledPane;

    @FXML
    private ListView<String> operationsListView;
    @FXML
    private TitledPane operationsTitledPane;

    @FXML
    private ListView<String> mathFunctionsListView;
    @FXML
    private TitledPane mathFunctionsTitledPane;

    @FXML
    private ListView<String> squidFunctionsListView;
    @FXML
    private TitledPane squidFunctionsTitledPane;

    @FXML
    private ListView<String> logicFunctionsListView;
    @FXML
    private TitledPane logicFunctionsTitledPane;

    @FXML
    private ListView<String> constantsListView;
    @FXML
    private TitledPane constantsTitledPane;

    @FXML
    private ListView<String> presentationListView;
    @FXML
    private TitledPane presentationTitledPane;

    @FXML
    private ListView<Expression> referenceMaterialsListView;
    @FXML
    private TitledPane referenceMaterialsTitledPane;

    @FXML
    private ListView<Expression> parametersListView;
    @FXML
    private TitledPane parametersTitledPane;

    //MISC
    @FXML
    private TitledPane graphPane;
    @FXML
    private TitledPane auditPane;
    @FXML
    private TitledPane peekPane;
    @FXML
    private AnchorPane expressionPane;
    @FXML
    private Accordion expressionsAccordion;
    @FXML
    private Accordion othersAccordion;
    @FXML
    private WebView graphView;
    @FXML
    private SplitPane mainPane;
    @FXML
    private SplitPane bigSplitPane;
    @FXML
    private SplitPane smallSplitPane;
    @FXML
    private SplitPane leftSplitPane;
    @FXML
    private VBox editorVBox;
    @FXML
    private HBox graphTitleHbox;
    @FXML
    private HBox toolBarHBox;
    @FXML
    private VBox toolBarVBox;
    @FXML
    private VBox selectSpotsVBox;
    @FXML
    private ScrollPane expressionScrollPane;
    @FXML
    private ComboBox<String> unknownGroupsComboBox;

    //PEEK TABS
    @FXML
    private Tab unkTab;
    @FXML
    private Tab refMatTab;
    @FXML
    private Tab selectSpotsTab;
    @FXML
    private TabPane spotTabPane;

    private static final String OPERATION_FLAG_DELIMITER = " : ";
    private static final String NUMBERSTRING = "NUMBER";
    private final BooleanProperty whiteSpaceVisible = new SimpleBooleanProperty(true);
    private static final String INVISIBLENEWLINEPLACEHOLDER = " \n";
    private static final String VISIBLENEWLINEPLACEHOLDER = "\u23CE\n";
    private static final String INVISIBLETABPLACEHOLDER = "  ";
    private static final String VISIBLETABPLACEHOLDER = " \u21E5";
    private static final String VISIBLEWHITESPACEPLACEHOLDER = "\u2423";
    private static final String INVISIBLEWHITESPACEPLACEHOLDER = " ";
    private final Map<String, String> presentationMap = new HashMap<>();

    {
        presentationMap.put("New line", VISIBLENEWLINEPLACEHOLDER);
        presentationMap.put("Tab", VISIBLETABPLACEHOLDER);
        presentationMap.put("White space", VISIBLEWHITESPACEPLACEHOLDER);
        whiteSpaceVisible.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue) {
                    presentationMap.replace("White space", VISIBLEWHITESPACEPLACEHOLDER);
                    presentationMap.replace("Tab", VISIBLETABPLACEHOLDER);
                    presentationMap.replace("New line", VISIBLENEWLINEPLACEHOLDER);
                } else {
                    presentationMap.replace("White space", INVISIBLEWHITESPACEPLACEHOLDER);
                    presentationMap.replace("Tab", INVISIBLETABPLACEHOLDER);
                    presentationMap.replace("New line", INVISIBLENEWLINEPLACEHOLDER);
                }
            }
        });
    }

    private int fontSizeModifier = 0;

    private final TextArea notesTextArea = new TextArea();

    {
        AnchorPane pane = new AnchorPane();
        pane.getChildren().setAll(notesTextArea);
        AnchorPane.setBottomAnchor(notesTextArea, 0.0);
        AnchorPane.setTopAnchor(notesTextArea, 0.0);
        AnchorPane.setRightAnchor(notesTextArea, 0.0);
        AnchorPane.setLeftAnchor(notesTextArea, 0.0);
        notesTextArea.setWrapText(true);
        EXPRESSION_NOTES_STAGE.setScene(new Scene(pane, 600, 150));
        EXPRESSION_NOTES_STAGE.setAlwaysOnTop(true);
    }

    private final ObjectProperty<String> dragOperationOrFunctionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragNumberSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragPresentationSource = new SimpleObjectProperty<>();

    private final ListProperty<String> undoListForExpression = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<String> redoListForExpression = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final ObjectProperty<Expression> selectedExpression = new SimpleObjectProperty<>();
    private final StringProperty expressionString = new SimpleStringProperty();
    private final BooleanProperty selectedExpressionIsEditable = new SimpleBooleanProperty(false);
    // Boolean to prevent editing of names of built-in expressions
    private final BooleanProperty selectedExpressionIsBuiltIn = new SimpleBooleanProperty(false);
    //Boolean to save whether or not the expression has been saved since the last modification
    private final BooleanProperty expressionIsSaved = new SimpleBooleanProperty(true);
    //Boolean to save whether the expression is currently edited as a textArea or with drag and drop
    private final BooleanProperty editAsText = new SimpleBooleanProperty(false);

    private final BooleanProperty hasRatioOfInterest = new SimpleBooleanProperty(false);

    private final ObjectProperty<Mode> currentMode = new SimpleObjectProperty<>(Mode.EDIT);

    @FXML
    private void showDependencyGraphsAction(ActionEvent event) {
        try {
            Files.write(
                    Paths.get("DEPENDENCIES.HTML"),
                    ("<html><pre>"
                            + task.printExpressionRequiresGraph(selectedExpression.getValue())
                            + "\n\n"
                            + task.printExpressionProvidesGraph(selectedExpression.getValue())
                            + "</pre></html>").getBytes());

            BrowserControl.showURI("DEPENDENCIES.HTML");
        } catch (IOException iOException) {
        }
    }

    private enum Mode {

        EDIT("Edit"),
        CREATE("Create"),
        VIEW("View");

        private final String printString;

        private Mode(String printString) {
            this.printString = printString;
        }

        @Override
        public String toString() {
            return printString;
        }
    }

    //List of operator used to detect if a string should be an operator node or not
    private final List<String> listOperators = new ArrayList<>();
    //List of all the expressions
    ObservableList<Expression> namedExpressions;

    List<Expression> removedExpressions = new ArrayList<>();

    private Expression selectedBeforeCreateOrCopy;
    private boolean expressionIsCopied;

    boolean changeFromUndoRedo = false;

    boolean needUpdateExpressions = false;

    Text insertIndicator = new Text("|");

    {
        insertIndicator.setFill(Color.RED);
        insertIndicator.setStyle(EXPRESSION_LIST_CSS_STYLE_SPECS);
    }

    public static Expression expressionToHighlightOnInit = null;

    private Map<String, Tooltip> tooltipsMap = new HashMap<>();

    private Map<KeyCode, Boolean> keyMap = new HashMap<>();

    private ObservableList<ExpressionTextNode> selectedNodes = FXCollections.observableArrayList();

    private TaskInterface task;

    //INIT
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        task = squidProject.getTask();
        // update 
        task.setupSquidSessionSpecsAndReduceAndReport();

        initPropertyBindings();
        initListViews();
        initRadios();
        initExpressionTextFlowAndTextArea();
        initGraph();
        initExpressionSelection();
        initNodeSelection();
        initKey();
        initPeekAreas();

        currentMode.set(Mode.VIEW);

        expressionAsTextArea.setWrapText(true);

        if (expressionToHighlightOnInit != null) {
            selectInAllPanes(expressionToHighlightOnInit, true);
            expressionToHighlightOnInit = null;
        } else if (!customExpressionsListView.getItems().isEmpty()) {
            selectInAllPanes(customExpressionsListView.getItems().get(0), true);
        }
    }

    private void initPeekAreas() {
        rmPeekTextArea.setStyle(SquidUI.PEEK_LIST_CSS_STYLE_SPECS);
        createCopyToClipboardContextMenu(rmPeekTextArea);
        unPeekTextArea.setStyle(SquidUI.PEEK_LIST_CSS_STYLE_SPECS);
        createCopyToClipboardContextMenu(unPeekTextArea);
    }

    private void initPropertyBindings() {
        //Disable bindings
        editorVBox.disableProperty().bind(selectedExpression.isNull());
        expressionUndoBtn.disableProperty().bind(undoListForExpression.sizeProperty().lessThan(1).or(currentMode.isEqualTo(Mode.VIEW)));
        expressionRedoBtn.disableProperty().bind(redoListForExpression.sizeProperty().lessThan(1).or(currentMode.isEqualTo(Mode.VIEW)));
        editExpressionBtn.disableProperty().bind(currentMode.isNotEqualTo(Mode.VIEW).or(selectedExpressionIsEditable.not()));
        copyExpressionIntoCustomBtn.disableProperty().bind(currentMode.isNotEqualTo(Mode.VIEW).or(selectedExpression.isNull()));
        createExpressionBtn.disableProperty().bind(currentMode.isNotEqualTo(Mode.VIEW));
        expressionClearBtn.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        expressionPasteBtn.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        saveBtn.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW).or(expressionNameTextField.textProperty().isEmpty()).or(expressionIsSaved));

        expressionAsTextBtn.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));

        refMatSwitchCheckBox.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW).or(selectedExpressionIsBuiltIn));
        unknownsSwitchCheckBox.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW).or(selectedExpressionIsBuiltIn));
        concRefMatSwitchCheckBox.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW).or(selectedExpressionIsBuiltIn));
        //specialUPbThSwitchCheckBox.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        summaryCalculationSwitchCheckBox.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW).or(selectedExpressionIsBuiltIn));
        NUSwitchCheckBox.setDisable(true);//NUSwitchCheckBox.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW).or(hasRatioOfInterest.not()));

        unknownGroupsComboBox.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW).or(selectedExpressionIsBuiltIn));
        unknownGroupsComboBox.visibleProperty().bind(unknownsSwitchCheckBox.selectedProperty()
                .and(refMatSwitchCheckBox.selectedProperty().not()).and(selectedExpressionIsBuiltIn.not()));
        unknownGroupsComboBox.setItems(FXCollections.observableArrayList(
                (String[]) task.getMapOfUnknownsBySampleNames().keySet().toArray(new String[0])));
        unknownGroupsComboBox.setValue(SpotTypes.UNKNOWN.getPlotType());
        unknownGroupsComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        setTextFill(RED);
                    }
                };
                return cell;
            }
        });
        unknownGroupsComboBox.setButtonCell(new TextFieldListCell<String>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }));

        expressionNameTextField.editableProperty().bind(currentMode.isNotEqualTo(Mode.VIEW).and(selectedExpressionIsBuiltIn.not()));

        showCurrentExpressionBtn.disableProperty().bind(selectedExpression.isNull().or(currentMode.isEqualTo(Mode.CREATE)));
//        cancelBtn.disableProperty().bind(selectedExpression.isNull());
        cancelBtn.disableProperty().bind(
                editExpressionBtn.disabledProperty().not()
                        // .or(selectedExpressionIsEditable.not()
                        .or(currentMode.isEqualTo(Mode.VIEW)));
        othersAccordion.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        hintHoverText.visibleProperty().bind(editAsText.not());
        hintSelectText.visibleProperty().bind(editAsText.not().and(currentMode.isNotEqualTo(Mode.VIEW)));
        BooleanProperty containsWhiteSpaces = new SimpleBooleanProperty(false);
        expressionString.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && (newValue.contains(" ") || newValue.contains("\t") || newValue.contains("\r") || newValue.contains("\n"))) {
                containsWhiteSpaces.set(true);
            } else {
                containsWhiteSpaces.set(false);
            }
        });
        toggleWhiteSpacesBtn.visibleProperty().bind(editAsText.not().and(containsWhiteSpaces));

        notesTextArea.editableProperty().bind(currentMode.isNotEqualTo(Mode.VIEW));
        notesTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshSaved();
        });
        EXPRESSION_NOTES_STAGE.titleProperty().bind(new SimpleStringProperty("Notes on ").concat(expressionNameTextField.textProperty()));
        EXPRESSION_NOTES_STAGE.setOnCloseRequest((event) -> {
            showNotesBtn.setText("Show notes");
        });
        mainPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false) {
                EXPRESSION_NOTES_STAGE.hide();
            }
        });

        toolBarHBox.visibleProperty().bind(currentMode.isNotEqualTo(Mode.VIEW));
        expressionClearBtn.visibleProperty().bind(currentMode.isNotEqualTo(Mode.VIEW));
        expressionPasteBtn.visibleProperty().bind(currentMode.isNotEqualTo(Mode.VIEW));
        expressionAsTextBtn.visibleProperty().bind(currentMode.isNotEqualTo(Mode.VIEW));
        expressionUndoBtn.visibleProperty().bind(currentMode.isNotEqualTo(Mode.VIEW));
        expressionRedoBtn.visibleProperty().bind(currentMode.isNotEqualTo(Mode.VIEW));

        currentMode.addListener((observable, oldValue, newValue) -> {
            //Updating peeks
            if (expressionString.isNotNull().get()) {
                populatePeeks(makeExpression(expressionNameTextField.getText(), expressionString.get()));
            }
            //Showing and hiding elements following mode
            if (oldValue == Mode.VIEW) {
                //toolBarVBox.getChildren().add(toolBarHBox);
                //toolBarHBox.setVisible(true);
                othersAccordion.setExpandedPane(operationsTitledPane);
                leftSplitPane.setDividerPositions(0.5);
            } else if (newValue == Mode.VIEW) {
                //toolBarVBox.getChildren().remove(toolBarHBox);
                //toolBarHBox.setVisible(false);
                othersAccordion.setExpandedPane(null);
                leftSplitPane.setDividerPositions(1.0);
            }

            //reset expressionIsCopied
            expressionIsCopied = false;
        });

        leftSplitPane.getDividers().get(0).positionProperty().addListener((o, ol, n) -> {
            //Block left pane divider in edit mode
            if (currentMode.get().equals(Mode.VIEW)) {
                leftSplitPane.setDividerPositions(1.0);
            }
        });

        //Accordions are growing only when they are expanded to avoid empty spaces
        othersAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                VBox.setVgrow(othersAccordion, null);
            } else {
                VBox.setVgrow(othersAccordion, Priority.ALWAYS);
            }
        });
        expressionsAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                VBox.setVgrow(expressionsAccordion, null);
            } else {
                VBox.setVgrow(expressionsAccordion, Priority.ALWAYS);
            }
        });

        //Prevent from clipping
        expressionTextFlow.maxWidthProperty().bind(expressionPane.widthProperty());
    }

    @FXML
    private void expressionSortToggleAction(ActionEvent event) {
        String flag = ((RadioButton) event.getSource()).getId();
        orderExpressionListsByFlag(flag);
    }

    private void orderExpressionListsByFlag(String flag) {
        orderListViewByFlag(customExpressionsListView, flag);
        orderListViewByFlag(nuSwitchedExpressionsListView, flag);
        orderListViewByFlag(builtInExpressionsListView, flag);
        orderListViewByFlag(brokenExpressionsListView, flag);

        // special cases
        orderListViewByFlag(referenceMaterialsListView, "NAME");
        orderListViewByFlag(parametersListView, "NAME");
    }

    private void orderListViewByFlag(ListView<Expression> listView, String flag) {
        ObservableList<Expression> items = listView.getItems();
        IntuitiveStringComparator<String> intuitiveStringComparator = new IntuitiveStringComparator<>();

        switch (flag) {
            case "EXEC":
                listView.setItems(items.sorted((exp1, exp2) -> {
                    if ((exp1.amHealthy() && exp2.amHealthy()) || (!exp1.amHealthy() && !exp2.amHealthy())) {
                        return namedExpressions.indexOf(exp1) - namedExpressions.indexOf(exp2);
                    } else if (!exp1.amHealthy() && exp2.amHealthy()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }));
                break;

            case "TARGET":
                // order by ConcRefMat then RU then R then U
                listView.setItems(items.sorted((exp1, exp2) -> {
                    // ConcRefMat
                    if (exp1.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()
                            && !exp2.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                        return -1;
                        // ConcRefMat
                    } else if (!exp1.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()
                            && exp2.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                        return 1;
                        //RU
                    } else if (exp1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                            && exp2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        return intuitiveStringComparator.compare(exp1.getName(), exp2.getName());
                        // RU
                    } else if (exp1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                            && (!exp2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            || !exp2.getExpressionTree().isSquidSwitchSAUnknownCalculation())) {
                        return -1;
                        // R
                    } else if (exp1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && !exp1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                            && exp2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && !exp2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        return intuitiveStringComparator.compare(exp1.getName(), exp2.getName());
                        // R
                    } else if (exp1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && !exp1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                            && !exp2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        return -1;
                        // U
                    } else if (!exp1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                            && !exp2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        return intuitiveStringComparator.compare(exp1.getName(), exp2.getName());
                    } else {
                        return 1;
                    }

                }));
                break;

            default://"NAME":
                listView.setItems(items.sorted((exp1, exp2) -> {
                    return exp1.getName().toLowerCase().compareTo(exp2.getName().toLowerCase());
                }));
                break;
        }
    }

    private void initRadios() {
        toggleGroup = new ToggleGroup();
        dragndropLeftRadio.setToggleGroup(toggleGroup);
        dragndropReplaceRadio.setToggleGroup(toggleGroup);
        dragndropRightRadio.setToggleGroup(toggleGroup);
        toggleGroup.selectToggle(dragndropRightRadio);
    }

    private void customizeBrokenExpressionsTitledPane() {
        if ((brokenExpressionsListView.getItems() == null) || (brokenExpressionsListView.getItems().isEmpty())) {
            brokenExpressionsTitledPane.setStyle("-fx-font-size: 12; -fx-text-fill: black; -fx-font-family: SansSerif;");
        } else {
            brokenExpressionsTitledPane.setStyle("-fx-font-size: 12; -fx-text-fill: red; -fx-font-family: SansSerif;");
        }
    }

    private void initListViews() {
        //EXPRESSIONS
        brokenExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        brokenExpressionsListView.setCellFactory(new ExpressionCellFactory(true));
        brokenExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //Listener to update the filter tab when a new value is selected in the broken expression category
        brokenExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {
                    if (currentMode.get().equals(Mode.VIEW)) {
                        selectedExpressionIsEditable.set(true);
                        selectedExpressionIsBuiltIn.set(false);
                        selectedExpression.set(newValue);
                    }
                    selectInAllPanes(newValue, false);
                }
                customizeBrokenExpressionsTitledPane();
            }
        });
        customizeBrokenExpressionsTitledPane();

        nuSwitchedExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        nuSwitchedExpressionsListView.setCellFactory(new ExpressionCellFactory());
        nuSwitchedExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //Same listener for each category
        nuSwitchedExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {
                    if (currentMode.get().equals(Mode.VIEW)) {
                        selectedExpressionIsEditable.set(true);
                        selectedExpressionIsBuiltIn.set(newValue.getExpressionTree().isSquidSpecialUPbThExpression());
                        selectedExpression.set(newValue);
                    }
                    selectInAllPanes(newValue, false);
                }
            }
        });

        builtInExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        builtInExpressionsListView.setCellFactory(new ExpressionCellFactory());
        builtInExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        builtInExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {
                    if (currentMode.get().equals(Mode.VIEW)) {
                        selectedExpressionIsEditable.set(true);
                        selectedExpressionIsBuiltIn.set(true);
                        selectedExpression.set(newValue);
                    }
                    selectInAllPanes(newValue, false);
                }
            }
        });

        customExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        customExpressionsListView.setCellFactory(new ExpressionCellFactory());
        customExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        customExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {
                    if (currentMode.get().equals(Mode.VIEW)) {
                        selectedExpressionIsEditable.set(true);
                        selectedExpressionIsBuiltIn.set(false);
                        selectedExpression.set(newValue);

                    }
                    selectInAllPanes(newValue, false);
                }
            }
        });

        // REFERERENCE MATERIAL VALUES        
        referenceMaterialsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        referenceMaterialsListView.setCellFactory(new ExpressionCellFactory());
        referenceMaterialsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        referenceMaterialsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {
                    if (currentMode.get().equals(Mode.VIEW)) {
                        selectedExpressionIsEditable.set(false);
                        selectedExpressionIsBuiltIn.set(false);
                        selectedExpression.set(newValue);
                    }
                    selectInAllPanes(newValue, false);
                }
            }
        });
        // PARAMETER VALUES        
        parametersListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        parametersListView.setCellFactory(new ExpressionCellFactory());
        parametersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        parametersListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {
                    if (currentMode.get().equals(Mode.VIEW)) {
                        selectedExpressionIsEditable.set(false);
                        selectedExpressionIsBuiltIn.set(false);
                        selectedExpression.set(newValue);
                    }
                    selectInAllPanes(newValue, false);
                }
            }
        });

        populateExpressionListViews();

        //RATIOS
        ratioExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        ratioExpressionsListView.setCellFactory(new ExpressionTreeCellFactory());
        ratioExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ratioExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SquidRatiosModel>() {
            @Override
            public void changed(ObservableValue<? extends SquidRatiosModel> observable, SquidRatiosModel oldValue, SquidRatiosModel newValue) {
                if (newValue != null) {
                    if (currentMode.get().equals(Mode.VIEW)) {
                        Expression expr = new Expression(
                                task.getNamedExpressionsMap().get(newValue.getRatioName()),
                                "[\"" + newValue.getRatioName() + "\"]", false, false, false);
                        expr.getExpressionTree().setSquidSpecialUPbThExpression(true);
                        expr.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                        expr.getExpressionTree().setSquidSwitchSAUnknownCalculation(true);
                        selectedExpressionIsEditable.set(false);
                        selectedExpressionIsBuiltIn.set(false);
                        selectedExpression.set(expr);
                    }
                    selectInAllPanes(null, false);
                }
            }
        });

        populateRatiosListView();

        //OPERATIONS AND FUNCTIONS
        operationsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        operationsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));

        mathFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        mathFunctionsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));

        squidFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        squidFunctionsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));

        logicFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        logicFunctionsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));

        populateOperationOrFunctionListViews();

        //NUMBERS
        constantsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        constantsListView.setCellFactory(new StringCellFactory(dragNumberSource));
        populateNumberListViews();

        //PRESENTATION
        presentationListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        presentationListView.setCellFactory(new StringCellFactory(dragPresentationSource));
        presentationListView.getItems().addAll(presentationMap.keySet());
    }

    private void selectInAllPanes(Expression exp, boolean scrollIfAlreadySelected) {
        Boolean found = false;
        //If nothing is selected or the selected value is not the new one
        if (brokenExpressionsListView.getSelectionModel().getSelectedItem() == null
                || !brokenExpressionsListView.getSelectionModel().getSelectedItem().equals(exp)) {
            //Clear selection
            brokenExpressionsListView.getSelectionModel().clearSelection();
            //If the new value is on this pane then select it
            if (brokenExpressionsListView.getItems().contains(exp)) {

                brokenExpressionsListView.getSelectionModel().select(exp);
                brokenExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(brokenExpressionsTitledPane);
            }
        } else {
            found = true;
            if (scrollIfAlreadySelected) {
                brokenExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(brokenExpressionsTitledPane);
            }
        }

        //Same thing for the other panes
        if (nuSwitchedExpressionsListView.getSelectionModel().getSelectedItem() == null
                || !nuSwitchedExpressionsListView.getSelectionModel().getSelectedItem().equals(exp)) {
            nuSwitchedExpressionsListView.getSelectionModel().clearSelection();
            if (nuSwitchedExpressionsListView.getItems().contains(exp)) {
                nuSwitchedExpressionsListView.getSelectionModel().select(exp);
                nuSwitchedExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(nuSwitchedExpressionsTitledPane);
            }
        } else {
            found = true;
            if (scrollIfAlreadySelected) {
                nuSwitchedExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(nuSwitchedExpressionsTitledPane);
            }
        }

        if (builtInExpressionsListView.getSelectionModel().getSelectedItem() == null
                || !builtInExpressionsListView.getSelectionModel().getSelectedItem().equals(exp)) {
            builtInExpressionsListView.getSelectionModel().clearSelection();
            if (builtInExpressionsListView.getItems().contains(exp)) {
                builtInExpressionsListView.getSelectionModel().select(exp);
                builtInExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(builtInExpressionsTitledPane);
            }
        } else {
            found = true;
            if (scrollIfAlreadySelected) {
                builtInExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(builtInExpressionsTitledPane);
            }
        }

        if (customExpressionsListView.getSelectionModel().getSelectedItem() == null
                || !customExpressionsListView.getSelectionModel().getSelectedItem().equals(exp)) {
            customExpressionsListView.getSelectionModel().clearSelection();
            if (customExpressionsListView.getItems().contains(exp)) {
                customExpressionsListView.getSelectionModel().select(exp);
                customExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(customExpressionsTitledPane);
            }
        } else {
            found = true;
            if (scrollIfAlreadySelected) {
                customExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(customExpressionsTitledPane);
            }
        }

        if (referenceMaterialsListView.getSelectionModel().getSelectedItem() == null
                || !referenceMaterialsListView.getSelectionModel().getSelectedItem().equals(exp)) {
            referenceMaterialsListView.getSelectionModel().clearSelection();
            if (referenceMaterialsListView.getItems().contains(exp)) {
                referenceMaterialsListView.getSelectionModel().select(exp);
                referenceMaterialsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(referenceMaterialsTitledPane);
            }
        } else {
            found = true;
            if (scrollIfAlreadySelected) {
                referenceMaterialsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(referenceMaterialsTitledPane);
            }
        }

        if (parametersListView.getSelectionModel().getSelectedItem() == null
                || !parametersListView.getSelectionModel().getSelectedItem().equals(exp)) {
            parametersListView.getSelectionModel().clearSelection();
            if (parametersListView.getItems().contains(exp)) {
                parametersListView.getSelectionModel().select(exp);
                parametersListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(parametersTitledPane);
            }
        } else {
            found = true;
            if (scrollIfAlreadySelected) {
                parametersListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(parametersTitledPane);
            }
        }
        if (found) {
            //If found in the expressions then it is not a ratio
            ratioExpressionsListView.getSelectionModel().clearSelection();
        }
    }

    private void initExpressionTextFlowAndTextArea() {

        //Init of the textarea
        expressionAsTextArea.setFont(Font.font("Monospaced"));

        expressionAsTextArea.textProperty().bindBidirectional(expressionString);
        expressionString.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshSaved();
                if (!makeStringFromTextFlow().equals(newValue)) {
                    makeTextFlowFromString(newValue);
                }
                if (!changeFromUndoRedo) {
                    if (oldValue != null) {
                        saveUndo(oldValue);
                    }
                } else {
                    changeFromUndoRedo = false;
                }
                updateEditor();
            }
        });

        expressionNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ((newValue != null) && newValue.compareTo(oldValue) != 0) {
                updateEditor();
                refreshSaved();
            }
        });

        editorVBox.setOnDragOver((event) -> {
            expressionTextFlow.getChildren().remove(insertIndicator);
        });

        //Init of the textflow following the mode
        currentMode.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case VIEW:
                    disableExpressionTextFlowDragAndDrop();
                    break;
                case CREATE:
                case EDIT:
                    activeExpressionTextFlowDragAndDrop();
            }
        });

    }

    private void activeExpressionTextFlowDragAndDrop() {

        expressionScrollPane.setOnDragOver((DragEvent event) -> {
            if (event.getDragboard().hasString()) {
                if (event.getGestureSource() instanceof ExpressionTextNode) {
                    //Move an other text node
                    event.acceptTransferModes(TransferMode.MOVE);
                } else {
                    //Or copy a new one from the lists
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });

        expressionScrollPane.setOnDragEntered((event) -> {
            //Show insert position indicator on enter
            expressionTextFlow.getChildren().remove(insertIndicator);
            if (dragndropLeftRadio.isSelected()) {
                expressionTextFlow.getChildren().add(0, insertIndicator);
            } else {
                expressionTextFlow.getChildren().add(insertIndicator);
            }
        });

        expressionScrollPane.setOnDragExited((event) -> {
            //Remove insert position indicator on exit
            expressionTextFlow.getChildren().remove(insertIndicator);
        });

        expressionScrollPane.setOnDragDropped((DragEvent event) -> {

            expressionTextFlow.getChildren().remove(insertIndicator);

            Dragboard db = event.getDragboard();
            boolean success = false;

            //By default, the node will be placed at the end of the textflow
            double ord = expressionTextFlow.getChildren().size();
            if (toggleGroup.getSelectedToggle() == dragndropLeftRadio) {
                //But if the dragndropLeftRadio is selected, the node will be placed at the begining of the textflow
                ord = -1.0;
            }

            // if moving a node
            if (event.getGestureSource() instanceof ExpressionTextNode) {
                if (selectedNodes.size() > 1) {
                    //If multiple nodes update all indexes
                    for (ExpressionTextNode etn : selectedNodes) {
                        etn.setOrdinalIndex(ord + 0.001 * etn.getOrdinalIndex());
                    }
                } else {
                    //Just change the index and then update
                    ((ExpressionTextNode) event.getGestureSource()).setOrdinalIndex(ord);
                }
                updateExpressionTextFlowChildren();
                success = true;
            } // if copying a node from the lists, insert depending of the type of node
            else if (db.hasString()) {
                String content = db.getString().split(OPERATION_FLAG_DELIMITER)[0];
                if ((dragOperationOrFunctionSource.get() != null) && (!db.getString().contains(OPERATION_FLAG_DELIMITER))) {
                    // case of function, make a series of objects
                    insertFunctionIntoExpressionTextFlow(content, ord);
                } else if ((dragOperationOrFunctionSource.get() != null) && (db.getString().contains(OPERATION_FLAG_DELIMITER))) {
                    // case of operation
                    insertOperationIntoExpressionTextFlow(content, ord);
                } else if ((dragNumberSource.get() != null) && content.contains(NUMBERSTRING)) {
                    // case of "NUMBER"
                    insertNumberIntoExpressionTextFlow(NUMBERSTRING, ord);
                } else if (dragPresentationSource.get() != null && presentationMap.containsKey(dragPresentationSource.get())) {
                    // case of presentation (new line, tab)
                    insertPresentationIntoExpressionTextFlow(presentationMap.get(dragPresentationSource.get()), ord);
                } else {
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

    private void disableExpressionTextFlowDragAndDrop() {
        expressionScrollPane.setOnDragOver((DragEvent event) -> {
            //Nothing
        });

        expressionScrollPane.setOnDragDropped((DragEvent event) -> {
            //Nothing
        });
    }

    private void initExpressionSelection() {
        //Listener that updates the whole builder when the expression to edit is changed
        selectedExpression.addListener((observable, oldValue, newValue) -> {
            if (needUpdateExpressions) {
                task.updateAllExpressions(true);
                needUpdateExpressions = false;
            }
            if (editAsText.get()) {
                expressionAsTextAction(new ActionEvent());
            }
            if (newValue != null) {
                expressionNameTextField.textProperty().set(newValue.getName());
                notesTextArea.setText(newValue.getNotes());
                refMatSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchSTReferenceMaterialCalculation());
                unknownsSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchSAUnknownCalculation());
                unknownGroupsComboBox.setValue(((ExpressionTree) newValue.getExpressionTree()).getUnknownsGroupSampleName());
                concRefMatSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchConcentrationReferenceMaterialCalculation());
                summaryCalculationSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchSCSummaryCalculation());
                specialUPbThSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSpecialUPbThExpression());
                NUSwitchCheckBox.setSelected(newValue.isSquidSwitchNU());
                expressionString.set(null);
                expressionString.set(newValue.getExcelExpressionString());
                hasRatioOfInterest.set(((ExpressionTree) newValue.getExpressionTree()).hasRatiosOfInterest());
                selectedExpressionIsBuiltIn.set(newValue.getExpressionTree().isSquidSpecialUPbThExpression());
                populateSpotsSelection(newValue);
            } else {
                expressionNameTextField.clear();
                expressionTextFlow.getChildren().clear();
                refMatSwitchCheckBox.setSelected(false);
                unknownsSwitchCheckBox.setSelected(false);
                unknownGroupsComboBox.setValue(SpotTypes.UNKNOWN.getPlotType());
                concRefMatSwitchCheckBox.setSelected(false);
                selectedExpressionIsEditable.set(false);
                selectedExpressionIsBuiltIn.set(false);
                expressionString.set("");
                hasRatioOfInterest.set(false);
            }
            undoListForExpression.clear();
            redoListForExpression.clear();
        });
    }

    private void initGraph() {

        //Update graph when change in preferences
        showGraphCheckBox.setOnAction((event) -> {
            graphExpressionTree(makeExpression(expressionNameTextField.getText(), expressionString.get()).getExpressionTree());
        });
        graphBrowserCheckBox.setOnAction((event) -> {
            graphExpressionTree(makeExpression(expressionNameTextField.getText(), expressionString.get()).getExpressionTree());
        });
    }

    private void initKey() {
        for (KeyCode key : KeyCode.values()) {
            keyMap.put(key, false);
        }
        mainPane.setOnKeyPressed((event) -> {
            keyMap.put(event.getCode(), true);
        });
        mainPane.setOnKeyReleased((event) -> {
            keyMap.put(event.getCode(), false);
        });
    }

    //CREATE EDIT DELETE SAVE CANCEL ACTIONS
    @FXML
    private void newCustomExpressionAction(ActionEvent event) {
        if (currentMode.get().equals(Mode.VIEW)) {
            selectedBeforeCreateOrCopy = selectedExpression.get();
            Expression exp = new Expression("", "");
            selectedExpression.set(exp);
            currentMode.set(Mode.CREATE);
            selectedExpressionIsEditable.setValue(true);
            refreshSaved();
            expressionNameTextField.requestFocus();
        }
    }

    @FXML
    private void copyIntoCustomExpressionAction(ActionEvent event) {
        if (currentMode.get().equals(Mode.VIEW)) {
            selectedBeforeCreateOrCopy = selectedExpression.get();
            Expression exp = copySelectedExpression();
            exp.setName("copy of " + exp.getName());
            selectedExpression.set(exp);
            currentMode.set(Mode.CREATE);
            refreshSaved();
            expressionIsCopied = true;
            expressionNameTextField.requestFocus();
        }
    }

    @FXML
    private void editCustomExpressionAction(ActionEvent event) {
        if (selectedExpressionIsEditable.get() && currentMode.get().equals(Mode.VIEW)) {
            currentMode.set(Mode.EDIT);
            refreshSaved();
        }
    }

    @FXML
    private void cancelAction(ActionEvent event) {
        if (!currentMode.get().equals(Mode.EDIT)) {
            currentMode.set(Mode.VIEW);
            selectedExpression.set(null);
            selectedExpression.set(selectedBeforeCreateOrCopy);
            selectInAllPanes(selectedBeforeCreateOrCopy, true);
            selectedBeforeCreateOrCopy = null;
        } else {
            currentMode.set(Mode.VIEW);
            //Reselect the unmodified expression
            Expression exp = selectedExpression.get();
            selectedExpression.set(null);
            selectedExpression.set(exp);
            selectInAllPanes(exp, true);
            selectedExpressionIsEditable.set(true);
        }
    }

    @FXML
    private void saveAction(ActionEvent event) {

        boolean nameExists = task.expressionExists(new Expression(expressionNameTextField.getText(), ""));

        if (!nameExists || (currentMode.get().equals(Mode.EDIT)
                && selectedExpression.get().getName().equals(expressionNameTextField.getText()))) {
            save();
        } else {
            //Case name already exists -> ask for replacing
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "An expression already exists with this name. Do you want to replace it?",
                    ButtonType.YES,
                    ButtonType.NO
            );
            alert.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
            alert.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
            alert.showAndWait().ifPresent((t) -> {
                if (t.equals(ButtonType.YES)) {
                    save();
                }
            });
        }
    }

    //EXPRESSION ACTIONS
    @FXML
    private void expressionClearAction(ActionEvent event) {
        //Clear the textflow
        if (!currentMode.get().equals(Mode.VIEW)) {
            expressionString.set("");
        }
    }

    @FXML
    private void expressionCopyAction(ActionEvent event) {
        //Copy in clipboard
        String fullText = expressionString.get();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(fullText);
        clipboard.setContent(content);
    }

    @FXML
    private void expressionPasteAction(ActionEvent event) {
        //Build textflow from clipboard
        if (!currentMode.get().equals(Mode.VIEW)) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            String content = clipboard.getString();
            expressionString.set(content);
        }
    }

    @FXML
    private void expressionUndoAction(ActionEvent event) {
        //Try to restore the last saved state
        try {
            String last = undoListForExpression.get(undoListForExpression.size() - 1);
            redoListForExpression.add(expressionString.get());
            changeFromUndoRedo = true;
            expressionString.set(last);
            undoListForExpression.remove(last);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
        }
    }

    @FXML
    private void expressionRedoAction(ActionEvent event) {
        try {
            String last = redoListForExpression.get(redoListForExpression.size() - 1);
            undoListForExpression.add(expressionString.get());
            changeFromUndoRedo = true;
            expressionString.set(last);
            redoListForExpression.remove(last);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
        }
    }

    @FXML
    private void expressionAsTextAction(ActionEvent event) {
        if (editAsText.get() == false) {
            //Case was editing with drag and drop -> switch to textArea

            editAsText.set(true);

            expressionPane.getChildren().setAll(expressionAsTextArea);
            AnchorPane.setBottomAnchor(expressionAsTextArea, 0.0);
            AnchorPane.setTopAnchor(expressionAsTextArea, 0.0);
            AnchorPane.setRightAnchor(expressionAsTextArea, 0.0);
            AnchorPane.setLeftAnchor(expressionAsTextArea, 0.0);
            expressionAsTextBtn.setText("Edit as d&d");
            expressionAsTextArea.requestFocus();

        } else {
            //Case was editing as textArea -> switch to drag and drop

            editAsText.set(false);

            expressionPane.getChildren().setAll(expressionScrollPane);
            AnchorPane.setBottomAnchor(expressionScrollPane, 0.0);
            AnchorPane.setTopAnchor(expressionScrollPane, 0.0);
            AnchorPane.setRightAnchor(expressionScrollPane, 0.0);
            AnchorPane.setLeftAnchor(expressionScrollPane, 0.0);
            expressionAsTextBtn.setText("Edit as text");

            //Rebuild because CSS doesn't apply
            expressionTextFlow.getChildren().clear();
            // remove spurious spaces from [expr]__[n] and expr  [n] ==> [][]
            String expression = expressionString.get();
            expression = expression.replaceAll("( )*\\[", "[");
            makeTextFlowFromString(expression);
        }
    }

    @FXML
    private void showCurrentExpressionAction() {
        if (selectedExpression.isNotNull().get()) {
            selectInAllPanes(selectedExpression.get(), true);
        }
    }

    @FXML
    private void showNotesAction() {
        if (!EXPRESSION_NOTES_STAGE.isShowing()) {
            showNotesBtn.setText("Hide notes");
            EXPRESSION_NOTES_STAGE.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 600) / 2);
            EXPRESSION_NOTES_STAGE.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
            EXPRESSION_NOTES_STAGE.show();
        } else {
            EXPRESSION_NOTES_STAGE.hide();
            showNotesBtn.setText("Show notes");
        }
    }

    //CHECKBOX ACTIONS
    @FXML
    private void referenceMaterialCheckBoxAction(ActionEvent event) {
        concRefMatSwitchCheckBox.setSelected(false);
        updateEditor();
        refreshSaved();
    }

    @FXML
    private void unknownSamplesCheckBoxAction(ActionEvent event) {
        concRefMatSwitchCheckBox.setSelected(false);
        updateEditor();
        refreshSaved();
    }

    @FXML
    private void unknownGroupsComboBoxAction(ActionEvent event) {
        updateEditor();
        refreshSaved();
    }

    @FXML
    private void concRefMatCheckBoxAction(ActionEvent event) {
        unknownsSwitchCheckBox.setSelected(false);
        refMatSwitchCheckBox.setSelected(false);
        updateEditor();
        refreshSaved();
    }

    @FXML
    private void summaryCalculationCheckBoxAction(ActionEvent event) {
        NUSwitchCheckBox.setSelected(false);
        updateEditor();
        refreshSaved();
    }

    @FXML
    private void specialUPbThCheckBoxAction(ActionEvent event) {
        refreshSaved();
    }

    @FXML
    private void NUSwitchCheckBoxAction(ActionEvent event) {
        summaryCalculationSwitchCheckBox.setSelected(false);
        refreshSaved();
    }

    private void howToUseAction(ActionEvent event) {
        BrowserControl.showURI("https://www.youtube.com/playlist?list=PLfF8bcNRe2WTWx2IuDaHW_XpLh36bWkUc");
    }

    @FXML
    private void fontMinusAction(ActionEvent event) {
        if (EXPRESSION_BUILDER_DEFAULT_FONTSIZE + this.fontSizeModifier > EXPRESSION_BUILDER_MIN_FONTSIZE) {
            this.fontSizeModifier += -1;
            expressionAsTextArea.setFont(Font.font(expressionAsTextArea.getFont().getFamily(), EXPRESSION_BUILDER_DEFAULT_FONTSIZE + fontSizeModifier));
            for (Node n : expressionTextFlow.getChildren()) {
                if (n instanceof ExpressionTextNode) {
                    ((ExpressionTextNode) n).updateFontSize();
                }
            }
            if (EXPRESSION_BUILDER_DEFAULT_FONTSIZE + this.fontSizeModifier <= EXPRESSION_BUILDER_MIN_FONTSIZE) {
                fontMinusBtn.setDisable(true);
            }
            if (EXPRESSION_BUILDER_DEFAULT_FONTSIZE + this.fontSizeModifier < EXPRESSION_BUILDER_MAX_FONTSIZE) {
                fontPlusBtn.setDisable(false);
            }
        }
    }

    @FXML
    private void fontPlusAction(ActionEvent event) {
        if (EXPRESSION_BUILDER_DEFAULT_FONTSIZE + this.fontSizeModifier < EXPRESSION_BUILDER_MAX_FONTSIZE) {
            this.fontSizeModifier += 1;
            expressionAsTextArea.setFont(Font.font(expressionAsTextArea.getFont().getFamily(), EXPRESSION_BUILDER_DEFAULT_FONTSIZE + fontSizeModifier));
            for (Node n : expressionTextFlow.getChildren()) {
                if (n instanceof ExpressionTextNode) {
                    ((ExpressionTextNode) n).updateFontSize();
                }
            }
            if (EXPRESSION_BUILDER_DEFAULT_FONTSIZE + this.fontSizeModifier >= EXPRESSION_BUILDER_MAX_FONTSIZE) {
                fontPlusBtn.setDisable(true);
            }
            if (EXPRESSION_BUILDER_DEFAULT_FONTSIZE + this.fontSizeModifier > EXPRESSION_BUILDER_MIN_FONTSIZE) {
                fontMinusBtn.setDisable(false);
            }
        }
    }

    @FXML
    private void toggleWhiteSpacesAction(ActionEvent event) {
        whiteSpaceVisible.set(!whiteSpaceVisible.get());
        if (whiteSpaceVisible.get()) {
            toggleWhiteSpacesBtn.setText("Hide white spaces tokens");
        } else {
            toggleWhiteSpacesBtn.setText("Show white spaces tokens");
        }
        for (Node n : expressionTextFlow.getChildren()) {
            if (n instanceof ExpressionTextNode) {
                ((ExpressionTextNode) n).updateFontSize();
            }
        }
    }

    //POPULATE LISTS
    private void populateExpressionListViews() {

        tooltipsMap.clear();

        namedExpressions = FXCollections.observableArrayList(task.getTaskExpressionsOrdered());

        List<Expression> sortedNUSwitchedExpressionsList = new ArrayList<>();
        List<Expression> sortedBuiltInExpressionsList = new ArrayList<>();
        List<Expression> sortedCustomExpressionsList = new ArrayList<>();
        List<Expression> sortedBrokenExpressionsList = new ArrayList<>();
        List<Expression> sortedReferenceMaterialValuesList = new ArrayList<>();
        List<Expression> sortedParameterValuesList = new ArrayList<>();

        for (Expression exp : namedExpressions) {
            if (exp.amHealthy() && exp.isSquidSwitchNU()
                    && !exp.aliasedExpression()) {
                sortedNUSwitchedExpressionsList.add(exp);
            } else if (exp.isReferenceMaterialValue() && exp.amHealthy()) {
                sortedReferenceMaterialValuesList.add(exp);
            } else if (exp.isParameterValue() && exp.amHealthy()) {
                sortedParameterValuesList.add(exp);
            } else if (exp.getExpressionTree().isSquidSpecialUPbThExpression()
                    && exp.amHealthy()
                    && !exp.isSquidSwitchNU()
                    && !exp.aliasedExpression()) {
                sortedBuiltInExpressionsList.add(exp);
            } else if (exp.isCustom() && exp.amHealthy()) {
                sortedCustomExpressionsList.add(exp);
            } else if (!exp.amHealthy()) {
                sortedBrokenExpressionsList.add(exp);
            }
        }

        ObservableList<Expression> items = FXCollections.observableArrayList(sortedNUSwitchedExpressionsList);
        nuSwitchedExpressionsListView.setItems(null);
        nuSwitchedExpressionsListView.setItems(items);

        items = FXCollections.observableArrayList(sortedBuiltInExpressionsList);
        builtInExpressionsListView.setItems(null);
        builtInExpressionsListView.setItems(items);

        items = FXCollections.observableArrayList(sortedCustomExpressionsList);
        customExpressionsListView.setItems(null);
        customExpressionsListView.setItems(items);

        items = FXCollections.observableArrayList(sortedBrokenExpressionsList);
        brokenExpressionsListView.setItems(null);
        brokenExpressionsListView.setItems(items);
        customizeBrokenExpressionsTitledPane();

        items = FXCollections.observableArrayList(sortedReferenceMaterialValuesList);
        referenceMaterialsListView.setItems(null);
        referenceMaterialsListView.setItems(items);

        items = FXCollections.observableArrayList(sortedParameterValuesList);
        parametersListView.setItems(null);
        parametersListView.setItems(items);

        // sort everyone
        String flag = ((RadioButton) expressionsSortToggleGroup.getSelectedToggle()).getId();
        orderExpressionListsByFlag(flag);

    }

    private void populateRatiosListView() {
        List<SquidRatiosModel> ratiosList = task.getSquidRatiosModelList();

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
            //Operator '$$' is not available to users
            if (!op.getKey().equals("$$")) {
                operationStrings.add(op.getKey() + OPERATION_FLAG_DELIMITER + op.getValue());
                listOperators.add(op.getKey());
            }
        }

        ObservableList<String> items = FXCollections.observableArrayList(operationStrings);
        operationsListView.setItems(items);

        // Math Functions ======================================================
        List<String> mathFunctionStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : MATH_FUNCTIONS_MAP.entrySet()) {
            int argumentCount = Function.operationFactory(op.getValue()).getArgumentCount();
            StringBuilder args = new StringBuilder();
            args.append(op.getKey()).append("(");
            for (int i = 0; i < argumentCount; i++) {
                args.append("Arg").append(i).append(i < (argumentCount - 1) ? "," : ")");
            }

            mathFunctionStrings.add(args.toString());
        }

        items = FXCollections.observableArrayList(mathFunctionStrings);
        items = items.sorted();
        mathFunctionsListView.setItems(items);

        // Squid Functions ======================================================
        List<String> squidFunctionStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : SQUID_FUNCTIONS_MAP.entrySet()) {
            int argumentCount = Function.operationFactory(op.getValue()).getArgumentCount();
            StringBuilder args = new StringBuilder();
            args.append(op.getKey()).append("(");
            for (int i = 0; i < argumentCount; i++) {
                args.append("Arg").append(i).append(i < (argumentCount - 1) ? "," : ")");
            }

            squidFunctionStrings.add(args.toString());
        }

        items = FXCollections.observableArrayList(squidFunctionStrings);
        IntuitiveStringComparator<String> intuitiveStringComparator = new IntuitiveStringComparator<>();
        items = items.sorted((String func1, String func2) -> {
            return intuitiveStringComparator.compare(func1, func2);
        });
        squidFunctionsListView.setItems(items);

        // Logic Functions ======================================================
        List<String> logicFunctionStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : LOGIC_FUNCTIONS_MAP.entrySet()) {
            if (!op.getKey().equalsIgnoreCase("sqIf")) {
                int argumentCount = Function.operationFactory(op.getValue()).getArgumentCount();
                StringBuilder args = new StringBuilder();
                args.append(op.getKey()).append("(");
                for (int i = 0; i < argumentCount; i++) {
                    args.append("Arg").append(i).append((i < (argumentCount - 1) ? "," : ")"));
                }

                logicFunctionStrings.add(args.toString());
            }
        }

        items = FXCollections.observableArrayList(logicFunctionStrings);
        items = items.sorted();
        logicFunctionsListView.setItems(items);
    }

    private void populateNumberListViews() {
        // constants and numbers ===============================================
        List<String> constantStrings = new ArrayList<>();
        constantStrings.add(NUMBERSTRING + OPERATION_FLAG_DELIMITER + "placeholder for number");

        for (Map.Entry<String, ExpressionTreeInterface> constant : task.getNamedConstantsMap().entrySet()) {
            constantStrings.add(constant.getKey() + OPERATION_FLAG_DELIMITER + ((ConstantNode) constant.getValue()).getValue());
        }

        for (Map.Entry<String, ExpressionTreeInterface> constant : task.getNamedParametersMap().entrySet()) {
            constantStrings.add(constant.getKey() + OPERATION_FLAG_DELIMITER + ((ConstantNode) constant.getValue()).getValue());
        }

        ObservableList<String> items = FXCollections.observableArrayList(constantStrings);
        constantsListView.setItems(items);
    }

    private String createPeekRM(Expression exp) {
        String result;
        if ((exp == null) || (!exp.amHealthy())) {
            result = "No expression.";
        } else {
            List<ShrimpFractionExpressionInterface> refMatSpots = task.getReferenceMaterialSpots();
            List<ShrimpFractionExpressionInterface> concRefMatSpots = task.getConcentrationReferenceMaterialSpots();
            if (exp.getExpressionTree() instanceof ConstantNode) {
                result = "Not used";
                if (exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                    try {
                        rmPeekTextArea.setText(exp.getName() + " = " + Utilities.roundedToSize((Double) ((ConstantNode) exp.getExpressionTree()).getValue(), 15));
                    } catch (Exception e) {
                    }
                }
            } else if (exp.getExpressionTree().isSquidSwitchSCSummaryCalculation()) {
                SpotSummaryDetails spotSummary = task.getTaskExpressionsEvaluationsPerSpotSet().get(exp.getExpressionTree().getName());
                result = "No Summary";
                if (spotSummary != null) {
                    if (exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            result = peekDetailsPerSummary(spotSummary);
                        } else {
                            result = "No Reference Materials";
                        }
                    }
                    if (exp.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            result = peekDetailsPerSummary(spotSummary);
                        } else {
                            result = "No Concentration Reference Materials";
                        }
                    }
                }
            } else {
                result = "Reference Materials not processed.";
                if (exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                    if (refMatSpots.size() > 0) {
                        result = peekDetailsPerSpot(refMatSpots, exp.getExpressionTree());
                    } else {
                        result = "No Reference Materials";
                    }
                } else if (exp.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                    if (concRefMatSpots.size() > 0) {
                        result = peekDetailsPerSpot(concRefMatSpots, exp.getExpressionTree());
                    } else {
                        result = "No Concentration Reference Materials";
                    }
                }
            }
        }
        return result;
    }

    private String createPeekUN(Expression exp) {
        String res;
        if ((exp == null) || (!exp.amHealthy())) {
            res = "No expression.";
        } else {
            List<ShrimpFractionExpressionInterface> unSpots
                    = task.getMapOfUnknownsBySampleNames().get(exp.getExpressionTree().getUnknownsGroupSampleName());
            if (exp.getExpressionTree() instanceof ConstantNode) {
                res = "Not used";
                if (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                    try {
                        unPeekTextArea.setText(exp.getName() + " = " + Utilities.roundedToSize((Double) ((ConstantNode) exp.getExpressionTree()).getValue(), 15));
                    } catch (Exception e) {
                    }
                }
            } else if (exp.getExpressionTree().isSquidSwitchSCSummaryCalculation()) {
                SpotSummaryDetails spotSummary = task.getTaskExpressionsEvaluationsPerSpotSet().get(exp.getExpressionTree().getName());
                res = "No Summary";
                if (spotSummary != null) {
                    if (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            res = peekDetailsPerSummary(spotSummary);
                        } else {
                            res = "No Unknowns";
                        }
                    }
                }
            } else {
                res = "Unknowns not processed.";
                if (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                    if (unSpots.size() > 0) {
                        res = peekDetailsPerSpot(unSpots, exp.getExpressionTree());
                    } else {
                        res = "No Unknowns";
                    }
                }
            }
        }
        return res;
    }

    private void populateSpotsSelection(Expression exp) {
        selectSpotsVBox.getChildren().clear();
        if (exp.getExpressionTree().isSquidSwitchSCSummaryCalculation()) {
            if (!spotTabPane.getTabs().contains(selectSpotsTab)) {
                spotTabPane.getTabs().add(selectSpotsTab);
            }
            selectSpotsTab.setDisable(false);
            SpotSummaryDetails spotSummaryDetail = task.getTaskExpressionsEvaluationsPerSpotSet().get(exp.getExpressionTree().getName());
            if (spotSummaryDetail != null) {

                String columnsFormat1 = "%-4s   %-10s   %-19s   %-17s";
                String columnsFormat2 = "%-4s   %-10s";

                List<ShrimpFractionExpressionInterface> selectedSpots = spotSummaryDetail.getSelectedSpots();
                ExpressionTree expTree = (ExpressionTree) exp.getExpressionTree();
                ExpressionTreeInterface etWMChild1 = null;
                ExpressionTreeInterface etWMChild2 = null;
                if (expTree.getOperation() instanceof WtdMeanACalc && exp.amHealthy() && expTree.getChildrenET().size() >= 2) {
                    etWMChild1 = expTree.getChildrenET().get(0);
                    etWMChild2 = expTree.getChildrenET().get(1);
                }

                CheckBox mainCB;
                List<CheckBox> cbs = new ArrayList<>();

                if (etWMChild1 == null || etWMChild2 == null) {
                    mainCB = new CheckBox(String.format(columnsFormat2, "All", "Spot name"));
                } else {
                    mainCB = new CheckBox(String.format(columnsFormat1, "All", "Spot name", "Value", "%err"));
                }
                mainCB.setOnAction((event) -> {
                    if (mainCB.isSelected()) {
                        cbs.forEach((t) -> {
                            t.setSelected(true);
                        });
                    } else {
                        cbs.forEach((t) -> {
                            t.setSelected(false);
                        });
                    }
                });
                selectSpotsVBox.getChildren().add(mainCB);
                mainCB.setStyle(PEEK_LIST_CSS_STYLE_SPECS);
                mainCB.setDisable(!spotSummaryDetail.isManualRejectionEnabled());
                mainCB.setOpacity(0.99);

                for (int i = 0; i < selectedSpots.size(); i++) {
                    int index = i;
                    ShrimpFractionExpressionInterface spot = selectedSpots.get(i);
                    String value = "";
                    String err = "";

                    CheckBox cb;

                    if (etWMChild1 == null || etWMChild2 == null) {
                        cb = new CheckBox(String.format(columnsFormat2, "#" + i, spot.getFractionID()));
                    } else {
                        Map<ExpressionTreeInterface, double[][]> map = spot.getTaskExpressionsEvaluationsPerSpot();
                        for (Entry<ExpressionTreeInterface, double[][]> entry : map.entrySet()) {
                            if (entry.getKey().getName().equals(etWMChild1.getName())) {
                                value = "" + Utilities.roundedToSize(entry.getValue()[0][0], 15);
                            }
                            if (entry.getKey().getName().equals(etWMChild2.getName())) {
                                err = "" + Utilities.roundedToSize(entry.getValue()[0][0], 15);
                            }
                        }
                        cb = new CheckBox(String.format(columnsFormat1, "#" + i, spot.getFractionID(), value, err));
                    }
                    cbs.add(cb);

                    cb.setStyle(PEEK_LIST_CSS_STYLE_SPECS);
                    if (spotSummaryDetail.getRejectedIndices().length > i) {
                        cb.setSelected(!spotSummaryDetail.getRejectedIndices()[i]);
                    } else {
                        cb.setSelected(true);
                    }
                    cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        try {
                            boolean[] reji = spotSummaryDetail.getRejectedIndices();
                            reji[index] = !newValue;
                            spotSummaryDetail.setRejectedIndices(reji);
                            spotSummaryDetail.setValues(spotSummaryDetail.eval(task));
                            populatePeeks(exp);
                            needUpdateExpressions = true;
                        } catch (SquidException ex) {
                            Logger.getLogger(ExpressionBuilderController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        boolean allSelected = true;
                        boolean anySelected = false;
                        for (CheckBox c : cbs) {
                            if (c.isSelected()) {
                                anySelected = true;
                            } else {
                                allSelected = false;
                            }
                            if (anySelected == true && allSelected == false) {
                                break;
                            }
                        }
                        if (allSelected) {
                            mainCB.setIndeterminate(false);
                            mainCB.setSelected(true);
                        } else if (anySelected) {
                            mainCB.setSelected(false);
                            mainCB.setIndeterminate(true);
                        } else {
                            mainCB.setIndeterminate(false);
                            mainCB.setSelected(false);
                        }
                    });
                    cb.setDisable(!spotSummaryDetail.isManualRejectionEnabled());
                    cb.setOpacity(0.99);
                    selectSpotsVBox.getChildren().add(cb);

                }
                boolean allSelected = true;
                boolean anySelected = false;
                for (CheckBox c : cbs) {
                    if (c.isSelected()) {
                        anySelected = true;
                    } else {
                        allSelected = false;
                    }
                    if (anySelected == true && allSelected == false) {
                        break;
                    }
                }
                if (allSelected) {
                    mainCB.setIndeterminate(false);
                    mainCB.setSelected(true);
                } else if (anySelected) {
                    mainCB.setSelected(false);
                    mainCB.setIndeterminate(true);
                } else {
                    mainCB.setIndeterminate(false);
                    mainCB.setSelected(false);
                }

            } else {
                selectSpotsTab.setDisable(true);
                spotTabPane.getTabs().remove(selectSpotsTab);
            }
        } else {
            selectSpotsTab.setDisable(true);
            spotTabPane.getTabs().remove(selectSpotsTab);
        }
    }

    private void populatePeeks(Expression exp) {
        SingleSelectionModel<Tab> selectionModel = spotTabPane.getSelectionModel();

        if ((exp == null) || (!exp.amHealthy())) {
            rmPeekTextArea.setText("No expression.");
            unPeekTextArea.setText("No expression.");
        } else if (!currentMode.get().equals(Mode.VIEW)) {
            rmPeekTextArea.setText("You must save the expression to get a peek.");
            unPeekTextArea.setText("You must save the expression to get a peek.");
        } else {
            // choose peek tab
            if (refMatTab.isSelected() && !refMatSwitchCheckBox.isSelected() && !concRefMatSwitchCheckBox.isSelected()) {
                selectionModel.select(unkTab);
            } else if (unkTab.isSelected() && !unknownsSwitchCheckBox.isSelected()) {
                selectionModel.select(refMatTab);
            } else if (selectSpotsTab.isSelected() && !summaryCalculationSwitchCheckBox.isSelected()) {
                if (unknownsSwitchCheckBox.isSelected()) {
                    selectionModel.select(unkTab);
                } else {
                    selectionModel.select(refMatTab);
                }
            }

            rmPeekTextArea.setText(createPeekRM(exp));
            unPeekTextArea.setText(createPeekUN(exp));
        }
    }

    private String peekDetailsPerSummary(SpotSummaryDetails spotSummary) {
        // context-sensitivity - we use Ma in Squid for display
        boolean isAge = ((ExpressionTree) spotSummary.getExpressionTree()).getName().toUpperCase(Locale.ENGLISH).contains("AGE");
        boolean isLambda = ((ExpressionTree) spotSummary.getExpressionTree()).getName().toUpperCase(Locale.ENGLISH).contains("LAMBDA2");
        boolean isConcen = ((ExpressionTree) spotSummary.getExpressionTree()).getName().toUpperCase(Locale.ENGLISH).contains("CONCEN");

        String[][] labels = clone2dArray(((ExpressionTree) spotSummary.getExpressionTree()).getOperation().getLabelsForOutputValues());
        if (isAge) {
            labels[0][0] = "Age (Ma)";
            if (labels[0].length > 1) {
                labels[0][1] += " (Ma)";
            }
            if (labels[0].length > 2) {
                labels[0][2] += " (Ma)";
            }
        }

        if (isLambda) {
            labels[0][0] = ((ExpressionTree) spotSummary.getExpressionTree()).getName() + " (Ma)";
        }

        if (isConcen) {
            labels[0][0] = "ppm";
        }

        StringBuilder sb = new StringBuilder();
        if (concRefMatSwitchCheckBox.isSelected()) {
            sb.append("Concentration Reference Materials Only\n\n");
        }
        for (int i = 0; i < labels[0].length; i++) {
            sb.append("\t");
            // show array index in Squid3
            sb.append("[").append(i).append("] ");
            sb.append(String.format("%1$-" + 16 + "s", labels[0][i]));
            sb.append(": ");
            sb.append(Utilities.roundedToSize(
                    spotSummary.getValues()[0][i] / (isAge ? 1.0e6 : ((isLambda ? 1.0e-6 : 1.0))), 15));
            sb.append("\n");
        }

        // handle special cases
        if (labels.length > 1) {
            sb.append("\t");
            sb.append("    ");
            sb.append(String.format("%1$-" + 16 + "s", labels[1][0]));
            sb.append(": ");
            // print list
            if (spotSummary.getValues()[1].length == 0) {
                sb.append("None");
            } else {
                for (int j = 0; j < spotSummary.getValues()[1].length; j++) {
                    sb.append((int) (spotSummary.getValues()[1][j])).append(" ");
                }
            }
            sb.append("\n");
        }

        if (labels.length > 2) {
            sb.append("\t");
            sb.append("    ");
            sb.append(String.format("%1$-" + 16 + "s", labels[2][0]));
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

        if (spotSummary.isManualRejectionEnabled()) {
            sb.append("\tManually rejected: ");
            boolean rejected = false;
            for (int i = 0; i < spotSummary.getRejectedIndices().length; i++) {
                if (spotSummary.getRejectedIndices()[i]) {
                    sb.append(i).append(" ");
                    rejected = true;
                }
            }
            if (!rejected) {
                sb.append("None");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String peekDetailsPerSpot(List<ShrimpFractionExpressionInterface> spots, ExpressionTreeInterface expTree) {
        StringBuilder sb = new StringBuilder();
        int sigDigits = 15;

        // context-sensitivity - we use Ma in Squid for display
        boolean isAge = expTree.getName().toUpperCase(Locale.ENGLISH).contains("AGE");
        String contextAgeFieldName = (isAge ? "Age(Ma)" : "Value");
        String contextAge1SigmaAbsName = (isAge ? "1\u03C3Abs(Ma)" : "1\u03C3Abs");
        // or it may be concentration ppm
        boolean isConcen = expTree.getName().toUpperCase(Locale.ENGLISH).contains("CONCEN");
        contextAgeFieldName = (isConcen ? "ppm" : contextAgeFieldName);

        if (expTree.isSquidSwitchConcentrationReferenceMaterialCalculation()) {
            sb.append("Concentration Reference Materials Only\n\n");
        }
        sb.append(String.format("%1$-" + 15 + "s", "SpotName"));
        String[][] resultLabels;
        if (((ExpressionTree) expTree).getOperation() != null) {
            if ((((ExpressionTree) expTree).getOperation().getName().compareToIgnoreCase("Value") == 0)) {
                if (((ExpressionTree) expTree).getChildrenET().get(0) instanceof VariableNodeForSummary) {
                    String uncertaintyDirective
                            = ((VariableNodeForSummary) ((ExpressionTree) expTree).getChildrenET().get(0)).getUncertaintyDirective();
                    if (uncertaintyDirective.length() > 0) {
                        if (uncertaintyDirective.compareTo("±") == 0) {
                            resultLabels = new String[][]{{contextAge1SigmaAbsName}, {}};
                        } else {
                            resultLabels = new String[][]{{"1\u03C3" + uncertaintyDirective}, {}};
                        }
                    } else {
                        resultLabels = new String[][]{{contextAgeFieldName}, {}};
                    }
                } else {
                    // i.e., ConstantNode
                    resultLabels = clone2dArray(((ExpressionTree) expTree).getOperation().getLabelsForOutputValues());
                }
            } else if (((ExpressionTree) expTree).getLeftET() instanceof ShrimpSpeciesNode) {
                // Check for functions of species
                if (((ExpressionTree) expTree).getOperation() instanceof ShrimpSpeciesNodeFunction) {
                    resultLabels = new String[][]{{((ShrimpSpeciesNodeFunction) ((ExpressionTree) expTree).getOperation()).getName()}, {}};
                } else {
                    // case of ratio
                    resultLabels = new String[][]{{expTree.getName(), "1\u03C3Abs", "1\u03C3%"}, {}};
                }

            } else if (((ExpressionTree) expTree).hasRatiosOfInterest()) {
                // case of NU switch
                String uncertaintyDirective
                        = ((ExpressionTree) expTree).getUncertaintyDirective();
                if (uncertaintyDirective.length() > 0) {
                    resultLabels = new String[][]{{"1\u03C3" + uncertaintyDirective}, {}};
                } else {
                    resultLabels = new String[][]{{contextAgeFieldName, "1\u03C3Abs", "1\u03C3%"}, {}};
                }
            } else {
                // some smarts
                String[][] resultLabelsFirst = clone2dArray(((ExpressionTree) expTree).getOperation().getLabelsForOutputValues());
                resultLabels = new String[1][resultLabelsFirst[0].length == 1 ? 1 : 3];
                resultLabels[0][0] = contextAgeFieldName;
                if (resultLabelsFirst[0].length > 1) {
                    resultLabels[0][1] = contextAge1SigmaAbsName;
                    resultLabels[0][2] = "1\u03C3%";
                }
            }

            for (int i = 0; i < resultLabels[0].length; i++) {
                try {
                    sb.append(String.format("%1$-" + (i >= 2 ? 23 : 20) + "s", resultLabels[0][i]));
                } catch (Exception e) {
                }
            }

            sb.append("\n");

            // produce values
            if (((ExpressionTree) expTree).getLeftET() instanceof ShrimpSpeciesNode) {
                // Check for functions of species
                if (((ExpressionTree) expTree).getOperation() instanceof ShrimpSpeciesNodeFunction) {
                    for (ShrimpFractionExpressionInterface spot : spots) {
                        sb.append(String.format("%1$-" + 15 + "s", spot.getFractionID()));
                        try {
                            double[][] results
                                    = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);
                            for (int i = 0; i < resultLabels[0].length; i++) {
                                try {
                                    sb.append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(results[0][i], sigDigits)));
                                } catch (Exception e) {
                                }
                            }
                        } catch (Exception e) {
                        }
                        sb.append("\n");
                    }
                } else {
                    // case of ratio
                    for (ShrimpFractionExpressionInterface spot : spots) {
                        sb.append(String.format("%1$-" + 15 + "s", spot.getFractionID()));
                        double[][] results
                                = Arrays.stream(spot.getIsotopicRatioValuesByStringName(expTree.getName())).toArray(double[][]::new);
                        double[] resultsWithPct = new double[3];
                        resultsWithPct[0] = results[0][0];
                        resultsWithPct[1] = results[0][1];
                        resultsWithPct[2] = calcPercentUnct(results[0]);
                        for (int i = 0; i < resultsWithPct.length; i++) {
                            try {
                                sb.append(String.format("%1$-" + (i >= 2 ? 23 : 20) + "s", Utilities.roundedToSize(resultsWithPct[i], sigDigits)));
                            } catch (Exception e) {
                            }
                        }
                        sb.append("\n");
                    }
                }
            } else {
                for (ShrimpFractionExpressionInterface spot : spots) {
                    if (spot.getTaskExpressionsEvaluationsPerSpot().get(expTree) != null) {
                        sb.append(String.format("%1$-" + 15 + "s", spot.getFractionID()));
                        double[][] results
                                = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);

                        double[] resultsWithPct = new double[0];
                        if ((resultLabels[0].length == 1) && (results[0].length >= 1)) {
                            resultsWithPct = new double[1];
                            resultsWithPct[0] = Utilities.roundedToSize(results[0][0] / (isAge ? 1.0e6 : 1.0), sigDigits);
                        } else if (results[0].length > 1) {
                            resultsWithPct = new double[3];
                            resultsWithPct[0] = Utilities.roundedToSize(results[0][0] / (isAge ? 1.0e6 : 1.0), sigDigits);
                            resultsWithPct[1] = Utilities.roundedToSize(results[0][1] / (isAge ? 1.0e6 : 1.0), sigDigits);
                            resultsWithPct[2] = calcPercentUnct(results[0]);
                        }

                        for (int i = 0; i < resultsWithPct.length; i++) {
                            try {
                                sb.append(String.format("%1$-" + (i >= 2 ? 23 : 20) + "s",
                                        Utilities.roundedToSize(resultsWithPct[i], sigDigits)));
                            } catch (Exception e) {
                            }
                        }
                        sb.append("\n");
                    }
                }
            }
        }

        return sb.toString();
    }

    private double calcPercentUnct(double[] valueModel) {
        return valueModel[1] / valueModel[0] * 100;
    }

    private ContextMenu createExpressionTextNodeContextMenu(ExpressionTextNode etn) {
        List<MenuItem> itemsForThisNode = new ArrayList<>();

        MenuItem menuItem = new MenuItem("Remove from expression");
        menuItem.setOnAction((evt) -> {
            expressionTextFlow.getChildren().remove(etn);
            updateExpressionTextFlowChildren();
        });
        itemsForThisNode.add(menuItem);

        menuItem = new MenuItem("Move left");
        menuItem.setOnAction((evt) -> {
            etn.setOrdinalIndex(etn.getOrdinalIndex() - 1.5);
            updateExpressionTextFlowChildren();
        });
        itemsForThisNode.add(menuItem);

        menuItem = new MenuItem("Move right");
        menuItem.setOnAction((evt) -> {
            etn.setOrdinalIndex(etn.getOrdinalIndex() + 1.5);
            updateExpressionTextFlowChildren();
        });
        itemsForThisNode.add(menuItem);

        Menu wrap = new Menu("Wrap in");
        itemsForThisNode.add(wrap);

        menuItem = new MenuItem("parentheses");
        menuItem.setOnAction((evt) -> {
            wrapInParentheses(etn.getOrdinalIndex(), etn.getOrdinalIndex());
        });
        wrap.getItems().add(menuItem);

        //For expressions -> allow wrap in brackets and quotes
        if (!(etn instanceof NumberTextNode || etn instanceof OperationTextNode) && !etn.getText().trim().matches("^[\\[\\](),]$") && !etn.getText().trim().matches("^\\[(±?)(%?)\"(.*)\"\\](\\[\\d\\])?$")) {
            menuItem = new MenuItem("brackets and quotes");
            menuItem.setOnAction((evt) -> {
                ExpressionTextNode etn2 = new ExpressionTextNode(" [\"" + etn.getText().trim() + "\"] ");
                etn2.setOrdinalIndex(etn.getOrdinalIndex());
                expressionTextFlow.getChildren().remove(etn);
                expressionTextFlow.getChildren().add(etn2);
                updateExpressionTextFlowChildren();
            });
            wrap.getItems().add(menuItem);
        }

        // provide special modification for uncertainty
        if (!(etn instanceof NumberTextNode || etn instanceof OperationTextNode)
                && (etn.getText().trim().matches("^\\[(±?)(%?)\"(.*)\"\\]( )*(\\[\\d\\]( )*)?$")
                || etn.getText().trim().matches("^(\\[(±?)(%?)\")?[a-zA-Z0-9_]*[a-zA-Z][a-zA-Z0-9]*(\"\\])?( )*(\\[\\d\\]( )*)?$"))) {

            String text = etn.getText().trim().replaceAll("(^(\\[(±?)(%?)\\\")?)|((\\\"\\])?( )*(\\[\\d\\]( )*)?)", "");
            Expression ex = task.getExpressionByName(text);
            if (((ex != null)
                    && (!ex.getExpressionTree().isSquidSwitchSCSummaryCalculation())
                    && ((((ExpressionTreeBuilderInterface) ex.getExpressionTree()).getOperation().getLabelsForOutputValues()[0].length > 1)
                    || ex.isSquidSwitchNU()))
                    || task.getRatioNames().contains(text)) {
                MenuItem menuItem1 = new MenuItem("1 \u03C3 (%)");
                menuItem1.setOnAction((evt) -> {
                    ExpressionTextNode etn2 = new ExpressionTextNode("[%\"" + text + "\"]");//    etn.getText().trim().replaceAll("\\[(±?)(%?)\"", "[%\""));
                    etn2.setOrdinalIndex(etn.getOrdinalIndex());
                    expressionTextFlow.getChildren().remove(etn);
                    expressionTextFlow.getChildren().add(etn2);
                    updateExpressionTextFlowChildren();
                });
                MenuItem menuItem2 = new MenuItem("1 \u03C3 abs (±)");
                menuItem2.setOnAction((evt) -> {
                    ExpressionTextNode etn2 = new ExpressionTextNode("[±\"" + text + "\"]");//    etn.getText().trim().replaceAll("\\[(±?)(%?)\"", "[±\""));
                    etn2.setOrdinalIndex(etn.getOrdinalIndex());
                    expressionTextFlow.getChildren().remove(etn);
                    expressionTextFlow.getChildren().add(etn2);
                    updateExpressionTextFlowChildren();
                });
                MenuItem menuItem3 = new MenuItem("Use value");
                menuItem3.setOnAction((evt) -> {
                    ExpressionTextNode etn2 = new ExpressionTextNode("[\"" + text + "\"]");//    etn.getText().trim().replaceAll("\\[(±?)(%?)\"", "[\""));
                    etn2.setOrdinalIndex(etn.getOrdinalIndex());
                    expressionTextFlow.getChildren().remove(etn);
                    expressionTextFlow.getChildren().add(etn2);
                    updateExpressionTextFlowChildren();
                });
                itemsForThisNode.add(new Menu("Select uncertainty...", null, menuItem1, menuItem2, menuItem3));
            }
        }

        // provide special modification for array access for summary expressions
        if (!(etn instanceof NumberTextNode || etn instanceof OperationTextNode)
                && (etn.getText().trim().matches("^\\[(±?)(%?)\"(.*)\"\\]( )*(\\[\\d\\]( )*)?$")
                || etn.getText().trim().matches("^(\\[(±?)(%?)\")?[a-zA-Z0-9_]*[a-zA-Z][a-zA-Z0-9]*(\"\\])?( )*(\\[\\d\\]( )*)?$"))) {
            String text = etn.getText().trim().replaceAll("(^(\\[(±?)(%?)\\\")?)|((\\\"\\])?( )*(\\[\\d\\]( )*)?)", "");
            Expression ex = task.getExpressionByName(text);
            if (((ex != null) && (ex.getExpressionTree().isSquidSwitchSCSummaryCalculation()))) {
                String[] outputLabels
                        = ((ExpressionTreeBuilderInterface) ex.getExpressionTree()).getOperation().getLabelsForOutputValues()[0];
                MenuItem[] menuItems = new MenuItem[outputLabels.length + 1];
                for (int i = 0; i < outputLabels.length; i++) {
                    final int ii = i;
                    MenuItem menuItemI = new MenuItem("[" + ii + "] = " + outputLabels[ii]);
                    menuItemI.setOnAction((evt) -> {
                        String placeHolder = etn.getText().trim().replaceAll("(\\[\\d\\]( )*)?", "").trim() + "&";
                        ExpressionTextNode etn2 = new ExpressionTextNode(placeHolder
                                .replaceAll("(\\&( )*(\\[\\d\\])?)", "\\[" + ii + "\\]"));
                        etn2.setOrdinalIndex(etn.getOrdinalIndex());
                        expressionTextFlow.getChildren().remove(etn);
                        expressionTextFlow.getChildren().add(etn2);
                        updateExpressionTextFlowChildren();
                    });
                    menuItems[i] = menuItemI;
                }

                MenuItem menuItemN = new MenuItem("None - same as '[0]'");
                menuItemN.setOnAction((evt) -> {
                    String placeHolder = etn.getText().trim().replaceAll("(\\[\\d\\]( )*)?", "").trim() + "&";
                    ExpressionTextNode etn2 = new ExpressionTextNode(placeHolder
                            .replaceAll("\\&", ""));
                    etn2.setOrdinalIndex(etn.getOrdinalIndex());
                    expressionTextFlow.getChildren().remove(etn);
                    expressionTextFlow.getChildren().add(etn2);
                    updateExpressionTextFlowChildren();
                });

                menuItems[outputLabels.length] = menuItemN;
                itemsForThisNode.add(new Menu("Select summary statistic by index ...", null, menuItems));
            }
        }

        //for true/false : allow to invert value
        if (!(etn instanceof NumberTextNode || etn instanceof OperationTextNode) && (etn.getText().trim().equalsIgnoreCase("true") || etn.getText().trim().equalsIgnoreCase("false"))) {
            menuItem = new MenuItem("Invert value");
            menuItem.setOnAction((evt) -> {
                String text;
                if (etn.getText().trim().equalsIgnoreCase("true")) {
                    text = " FALSE ";
                } else {
                    text = " TRUE ";
                }
                ExpressionTextNode etn2 = new ExpressionTextNode(text);
                etn2.setOrdinalIndex(etn.getOrdinalIndex());
                expressionTextFlow.getChildren().remove(etn);
                expressionTextFlow.getChildren().add(etn2);
                updateExpressionTextFlowChildren();
            });
            itemsForThisNode.add(menuItem);
        }

        // For numbers -> make an editable node
        if (etn instanceof NumberTextNode) {
            TextField editText = new TextField(etn.getText().trim());
            editText.setPrefWidth((editText.getText().trim().length() + 2) * editText.getFont().getSize());
            editText.textProperty().addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
                editText.setPrefWidth((editText.getText().trim().length() + 2) * editText.getFont().getSize());
            });
            menuItem = new MenuItem("<< Edit value then click here to save", editText);
            menuItem.setOnAction((evt) -> {
                //etn.setText(editText.getText());
                // this allows for redo of content editing
                NumberTextNode etn2 = new NumberTextNode(editText.getText());
                etn2.setOrdinalIndex(etn.getOrdinalIndex());
                expressionTextFlow.getChildren().remove(etn);
                expressionTextFlow.getChildren().add(etn2);
                updateExpressionTextFlowChildren();
            });
            itemsForThisNode.add(menuItem);
        }

        List<MenuItem> itemsForSelection = new ArrayList<>();

        //Menu items for multi-selection nodes
        if (selectedNodes.size() > 1) {
            menuItem = new MenuItem("Remove from expression");
            menuItem.setOnAction((evt) -> {
                for (ExpressionTextNode selected : selectedNodes) {
                    expressionTextFlow.getChildren().remove(selected);
                }
                updateExpressionTextFlowChildren();
            });
            itemsForSelection.add(menuItem);

            wrap = new Menu("Wrap in");
            itemsForSelection.add(wrap);

            menuItem = new MenuItem("parentheses");
            menuItem.setOnAction((evt) -> {
                List<ExpressionTextNode> nodesToAdd = new ArrayList<>();
                double begin = -1.0;
                double lastIndex = -1.0;
                for (Node node : expressionTextFlow.getChildren()) {
                    if (node instanceof ExpressionTextNode) {
                        if (selectedNodes.contains((ExpressionTextNode) node)) {
                            if (begin == -1.0) {
                                begin = ((ExpressionTextNode) node).getOrdinalIndex();
                            }
                            lastIndex = ((ExpressionTextNode) node).getOrdinalIndex();
                        } else {
                            if (begin != -1.0 && lastIndex != -1.0) {
                                ExpressionTextNode left = new ExpressionTextNode(" ( ");
                                left.setOrdinalIndex(begin - 0.5);
                                ExpressionTextNode right = new ExpressionTextNode(" ) ");
                                right.setOrdinalIndex(lastIndex + 0.5);
                                nodesToAdd.add(left);
                                nodesToAdd.add(right);
                                begin = -1.0;
                                lastIndex = -1.0;
                            }
                        }
                    }
                }
                if (begin != -1.0 && lastIndex != -1.0) {
                    ExpressionTextNode left = new ExpressionTextNode(" ( ");
                    left.setOrdinalIndex(begin - 0.5);
                    ExpressionTextNode right = new ExpressionTextNode(" ) ");
                    right.setOrdinalIndex(lastIndex + 0.5);
                    nodesToAdd.add(left);
                    nodesToAdd.add(right);
                }
                expressionTextFlow.getChildren().addAll(nodesToAdd);
                updateExpressionTextFlowChildren();
            });
            wrap.getItems().add(menuItem);

            menuItem = new MenuItem("brackets");
            menuItem.setOnAction((evt) -> {
                List<ExpressionTextNode> nodesToAdd = new ArrayList<>();
                double begin = -1.0;
                double lastIndex = -1.0;
                for (Node node : expressionTextFlow.getChildren()) {
                    if (node instanceof ExpressionTextNode) {
                        if (selectedNodes.contains((ExpressionTextNode) node)) {
                            if (begin == -1.0) {
                                begin = ((ExpressionTextNode) node).getOrdinalIndex();
                            }
                            lastIndex = ((ExpressionTextNode) node).getOrdinalIndex();
                        } else {
                            if (begin != -1.0 && lastIndex != -1.0) {
                                ExpressionTextNode left = new ExpressionTextNode(" [ ");
                                left.setOrdinalIndex(begin - 0.5);
                                ExpressionTextNode right = new ExpressionTextNode(" ] ");
                                right.setOrdinalIndex(lastIndex + 0.5);
                                nodesToAdd.add(left);
                                nodesToAdd.add(right);
                                begin = -1.0;
                                lastIndex = -1.0;
                            }
                        }
                    }
                }
                if (begin != -1.0 && lastIndex != -1.0) {
                    ExpressionTextNode left = new ExpressionTextNode(" [ ");
                    left.setOrdinalIndex(begin - 0.5);
                    ExpressionTextNode right = new ExpressionTextNode(" ] ");
                    right.setOrdinalIndex(lastIndex + 0.5);
                    nodesToAdd.add(left);
                    nodesToAdd.add(right);
                }
                expressionTextFlow.getChildren().addAll(nodesToAdd);
                updateExpressionTextFlowChildren();
            });
            wrap.getItems().add(menuItem);
        }

        ContextMenu contextMenu = new ContextMenu();

        if (!itemsForSelection.isEmpty()) {
            Menu menuNode = new Menu("Entity \"" + etn.getText().trim() + "\"");
            for (MenuItem mi : itemsForThisNode) {
                menuNode.getItems().add(mi);
            }
            contextMenu.getItems().add(menuNode);

            Menu menuSelection = new Menu("Selection");
            for (MenuItem mi : itemsForSelection) {
                menuSelection.getItems().add(mi);
            }
            contextMenu.getItems().add(menuSelection);
        } else {
            contextMenu.getItems().setAll(itemsForThisNode);
        }

        contextMenu.setOnHiding((WindowEvent event) -> {
            etn.popupShowing = false;
        });

        return contextMenu;
    }

    private String createPeekForTooltip(Expression ex) {
        String peek = "";
        if (ex.getExpressionTree().isSquidSwitchSCSummaryCalculation()) {
            if (ex.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation() || ex.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                peek += "Reference material :\n" + createPeekRM(ex) + "\n";
            }
            if (ex.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                peek += "Unknowns :\n" + createPeekUN(ex);
            }
        } else {
            if (ex.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation() || ex.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                String peekString = createPeekRM(ex);
                int lineNumber = 0;
                for (int n = 0; n < peekString.length(); n++) {
                    if (peekString.charAt(n) == '\n') {
                        lineNumber++;
                        if (lineNumber == 10) {
                            peekString = peekString.substring(0, n);
                            peekString += "\n...";
                            break;
                        }
                    }
                }
                peek += "Reference material :\n" + peekString + "\n";
            }
            if (ex.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                if (ex.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation() || ex.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                    peek += "\n";
                }
                String peekString = createPeekUN(ex);
                int lineNumber = 0;
                for (int n = 0; n < peekString.length(); n++) {
                    if (peekString.charAt(n) == '\n') {
                        lineNumber++;
                        if (lineNumber == 10) {
                            peekString = peekString.substring(0, n);
                            peekString += "\n...";
                            break;
                        }
                    }
                }
                peek += "Unknowns :\n" + peekString;
            }
        }
        if (!peek.equals("")) {
            peek = "Peek:\n\t" + peek.replaceAll("\n", "\n\t").trim();
        }
        return peek;
    }

    private Tooltip createFloatingTooltip(String nodeText) {
        Tooltip tooltip = tooltipsMap.get(nodeText);
        if (nodeText != null && tooltip == null) {
            String text = nodeText.replace(INVISIBLENEWLINEPLACEHOLDER, "\n");
            text = text.replace(VISIBLENEWLINEPLACEHOLDER, "\n");
            text = text.replace(INVISIBLETABPLACEHOLDER, "\t");
            text = text.replace(VISIBLETABPLACEHOLDER, "\t");
            text = text.replace(INVISIBLEWHITESPACEPLACEHOLDER, " ");
            text = text.replace(VISIBLEWHITESPACEPLACEHOLDER, " ");

            if (!text.matches("^[ \t\n\r]$")) {
                text = nodeText.replace(SUPERSCRIPT_R_FOR_REFMAT + SUPERSCRIPT_SPACE + " ", "");
                text = text.replace(SUPERSCRIPT_C_FOR_CONCREFMAT + SUPERSCRIPT_SPACE + " ", "");
                text = text.replace(SUPERSCRIPT_R_FOR_REFMAT + SUPERSCRIPT_U_FOR_UNKNOWN + " ", "");
                text = text.replace(SUPERSCRIPT_SPACE + SUPERSCRIPT_U_FOR_UNKNOWN + " ", "");
                text = text.trim();
            }

            ImageView imageView = new ImageView(UNHEALTHY);
            imageView.setFitHeight(12);
            imageView.setFitWidth(12);

            TokenTypes type = ShuntingYard.TokenTypes.getType(text);

            switch (type) {
                case OPERATOR_A:
                case OPERATOR_M:
                case OPERATOR_E:

                    tooltip = new Tooltip("Operation: " + text + " (" + OPERATIONS_MAP.get(text) + ")");
                    break;

                case LEFT_PAREN:
                case RIGHT_PAREN:

                    tooltip = new Tooltip("Parenthesis: " + text);
                    break;

                case NUMBER:

                    tooltip = new Tooltip("Number: " + text);
                    break;

                case NAMED_CONSTANT:

                    ConstantNode constant;
                    constant = (ConstantNode) task.getNamedConstantsMap().get(text);
                    if (constant == null) {
                        constant = (ConstantNode) task.getNamedParametersMap().get(text);
                    }
                    if (constant != null) {
                        tooltip = new Tooltip("Named constant: " + constant.getName() + "\n\nValue: " + constant.getValue());
                    }
                    break;

                case FUNCTION:

                    String str = FUNCTIONS_MAP.get(text);
                    if (str != null) {
                        OperationOrFunctionInterface fn = Function.operationFactory(str);
                        if (fn != null) {
                            tooltip = new Tooltip(
                                    "Function: " + fn.getName()
                                    + "\n\n" + fn.getArgumentCount()
                                    + " argument(s): " + fn.printInputValues().trim()
                                    + "\nOutputs: " + fn.printOutputValues().trim()
                                    + "\nDefinition: " + fn.getDefinition().trim());
                        }
                    }
                    break;

                case COMMA:

                    tooltip = new Tooltip("Comma: " + text);
                    break;

                case FORMATTER:

                    String tooltipText = "Presentation node: ";
                    switch (text) {
                        case "\t":
                            tooltipText += "tab";
                            break;
                        case "\n":
                            tooltipText += "new line";
                            break;
                        case " ":
                            tooltipText += "space";
                            break;
                        default:
                            tooltipText += "unknown";
                    }
                    tooltip = new Tooltip(tooltipText);
                    break;

                case NAMED_EXPRESSION_INDEXED:

                    text = text.replaceAll("\\[\\d\\]( )*$", "");

                case NAMED_EXPRESSION:
                    String exname = text;
                    String uncertainty = "";
                    if (text.matches("^\\[(±?)(%?)\"(.*?)\"\\]( )*$")) {
                        exname = text.replaceAll("(^\\[(%)?(±)?\")|(\"\\]( )*$)", "");
                        if (text.contains("[%\"")) {
                            uncertainty = "1 \u03C3 % uncertainty\n\n";
                        } else if (text.contains("[±\"")) {
                            uncertainty = "1 \u03C3 ± uncertainty\n\n";
                        }
                    }
                    // let's see if we have an array reference in the form of SUMMARY named_expression00
                    // this would be hard to catch with regex since ratios fit the pattern too
                    if (exname.length() > 2) {
                        String lastTwo = exname.substring(exname.length() - 2);
                        if (ShuntingYard.isNumber(lastTwo)) {
                            // index = first digit minus 1 (converting from vertical 1-based excel to horiz 0-based java
                            int index = Integer.parseInt(lastTwo.substring(0, 1)) - 1;
                            String baseExpressionName = exname.substring(0, exname.length() - 2);
                            if (index >= 0) {
                                ExpressionTreeInterface retExpTreeKnown = task.getNamedExpressionsMap().get(baseExpressionName);
                                if (retExpTreeKnown != null) {
                                    exname = baseExpressionName;
                                }
                            }
                        }
                    }

                    Expression ex = task.getExpressionByName(exname);
                    if (ex == null && text.matches("^\\.*\\d\\d$")) {
                        exname = text.replaceAll("\\d\\d$", "");
                        ex = task.getExpressionByName(exname);
                    }
                    //case expression
                    if (ex != null) {
                        boolean isCustom = ex.isCustom();
                        ExpressionTreeInterface expTree = ex.getExpressionTree();
                        tooltip = new Tooltip(
                                (isCustom ? "Custom expression: " : "Expression: ")
                                + "  " + ex.getName()
                                + "\n  Targets: "
                                + (expTree.isSquidSwitchConcentrationReferenceMaterialCalculation() ? "C" : "")
                                + (expTree.isSquidSwitchSTReferenceMaterialCalculation() ? "R" : "")
                                + (expTree.isSquidSwitchSAUnknownCalculation() ? "U" : "")
                                + "    Type: " + (expTree.isSquidSwitchSCSummaryCalculation() ? "Summary " : "")
                                + (ex.isSquidSwitchNU() ? "NU-switched " : "")
                                + (expTree.isSquidSpecialUPbThExpression() ? "Built-In" : "")
                                + "\n\nExpression string: "
                                + ex.getExcelExpressionString()
                                + "\n"
                                + uncertainty
                                + (ex.amHealthy() ? createPeekForTooltip(ex) : customizeExpressionTreeAudit(ex).trim())
                                + "\nNotes:\n"
                                + (ex.getNotes().equals("") ? "none" : ex.getNotes()));
                        if (!ex.amHealthy()) {
                            tooltip.setGraphic(imageView);
                        }
                    }
                    //case ratio
                    if (tooltip == null) {
                        for (SquidRatiosModel r : task.getSquidRatiosModelList()) {
                            if (exname.equalsIgnoreCase(r.getRatioName())) {
                                Expression exp = new Expression(
                                        task.getNamedExpressionsMap().get(r.getRatioName()),
                                        "[\"" + r.getRatioName() + "\"]", false, false, false);
                                exp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                                exp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                                exp.getExpressionTree().setSquidSwitchSAUnknownCalculation(true);
                                tooltip = new Tooltip(("Ratio: " + r.getRatioName() + "\n\n" + uncertainty) + createPeekForTooltip(exp));
                                break;
                            }
                        }
                    }
                    //case constant
                    if (tooltip == null) {
                        constant = (ConstantNode) task.getNamedConstantsMap().get(text);
                        if (constant == null) {
                            constant = (ConstantNode) task.getNamedParametersMap().get(text);
                        }
                        if (constant != null) {
                            tooltip = new Tooltip("Named constant: " + constant.getName() + "\n\nValue: " + constant.getValue());
                        }
                    }

                    //case SpotLookupField
                    if (tooltip == null) {
                        ExpressionTreeInterface spotLookupField = task.getNamedSpotLookupFieldsMap().get(text);
                        if (spotLookupField != null) {
                            tooltip = new Tooltip("Available lookup field for spots: " + spotLookupField.getName());
                        }
                    }

                    if (tooltip == null && text.equals(NUMBERSTRING)) {
                        tooltip = new Tooltip("Placeholder for number: " + NUMBERSTRING);
                    }

                    // case of mass labels or isotopes
                    if (tooltip == null) {
                        if (task.getNominalMasses().contains(exname)) {
                            tooltip = new Tooltip("Mass label: " + exname);
                        }
                    }

                    if (tooltip == null) {
                        tooltip = new Tooltip("Missing expression: " + exname);
                        tooltip.setGraphic(imageView);
                    }

                    break;

            }
            if (tooltip == null) {
                tooltip = new Tooltip("Unrecognized node: " + text);
                tooltip.setGraphic(imageView);
            }
            tooltip.setStyle(EXPRESSION_TOOLTIP_CSS_STYLE_SPECS);
            tooltipsMap.put(text, tooltip);
        }
        return tooltip;
    }

    private void updateExpressionTextFlowChildren() {
        // extract and sort
        List<Node> children = new ArrayList<>();
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
        for (Node etn : children) {
            ((ExpressionTextNode) etn).setOrdinalIndex(ordIndex);
            ordIndex++;
        }

        expressionTextFlow.getChildren().setAll(children);

        expressionString.set(makeStringFromTextFlow());

    }

    private String customizeExpressionTreeAudit(Expression exp) {
        String audit = exp.produceExpressionTreeAudit().trim();
        // add in secton reviewing health of dependent expressions
        StringBuilder depAudit = new StringBuilder().append("Dependency Audit:\n");
        List<String> dependentExpressionNames = ((Task) task).getRequiresExpressionsGraph().get(exp.getName());

        if (dependentExpressionNames == null) {
            // we may be on a new expression
            dependentExpressionNames = new ArrayList<>();
            List<ExpressionTreeInterface> children = ((ExpressionTreeBuilderInterface) exp.getExpressionTree()).getChildrenET();
            for (ExpressionTreeInterface child : children) {
                String calledName = child.getName();
                if (task.getNamedExpressionsMap().containsKey(calledName)
                        && !(child instanceof ShrimpSpeciesNode)
                        && (child.getName().compareTo("FALSE") != 0)
                        && (child.getName().compareTo("TRUE") != 0)) {
                    if (!dependentExpressionNames.contains(calledName)) {
                        dependentExpressionNames.add(calledName);
                    }
                }
            }
        }

        if (!dependentExpressionNames.isEmpty()) {
            for (String expressionName : dependentExpressionNames) {

                depAudit.append("\t")
                        .append(expressionName)
                        .append(" : ");
                if (task.getExpressionByName(expressionName) != null) {
                    depAudit.append(task.getExpressionByName(expressionName).amHealthy() ? "" : "UN").append("HEALTHY");
                } else {
                    // constant or ratio etc
                    depAudit.append("HEALTHY");
                }
                depAudit.append("\n");

            }
        } else {
            depAudit.append("\t")
                    .append("No dependent expressions.")
                    .append("\n");
        }

        depAudit.append("\n");

        return (depAudit.toString() + audit);
    }

    public void updateEditor() {

        if (selectedExpression.isNotNull().get()) {
            Expression exp;
            if (selectedExpressionIsEditable.get()) {
                exp = makeExpression(expressionNameTextField.getText(), expressionString.get());
            } else {
                exp = selectedExpression.get();
            }

            ((Task) task).evaluateTaskExpression(exp);
            boolean localAmHealthy = exp.amHealthy() && (exp.getName().length() > 0);
            auditTextArea.setText(customizeExpressionTreeAudit(exp));
            auditPane.setTextFill(localAmHealthy ? Paint.valueOf("black") : Paint.valueOf("red"));
            auditPane.setText(localAmHealthy ? "Audit" : "Audit - ALERT !!!" + ((exp.getName().length() == 0) ? "  Expression needs name" : ""));
            auditPane.setGraphic(localAmHealthy ? new ImageView(HEALTHY) : new ImageView(UNHEALTHY));
            ((ImageView) auditPane.getGraphic()).setFitHeight(16);
            ((ImageView) auditPane.getGraphic()).setFitWidth(16);
            graphExpressionTree(exp.getExpressionTree());
            populatePeeks(exp);

        } else {

            graphExpressionTree(null);
            auditTextArea.setText("");
            rmPeekTextArea.setText("");
            unPeekTextArea.setText("");
        }
    }

    /**
     * Creates a new expression from the modifications.
     *
     * @param expressionName the value of expressionName
     * @param expressionString the value of expressionString
     */
    private Expression makeExpression(String expressionName, final String expressionString) {

        Expression exp = task.generateExpressionFromRawExcelStyleText(
                expressionName,
                expressionString,
                NUSwitchCheckBox.isSelected(),
                selectedExpression.get().isReferenceMaterialValue(),
                selectedExpression.get().isParameterValue()
        );

        exp.setNotes(notesTextArea.getText());

        ExpressionTreeInterface expTree = exp.getExpressionTree();

        expTree.setSquidSwitchSTReferenceMaterialCalculation(refMatSwitchCheckBox.isSelected());
        expTree.setSquidSwitchSAUnknownCalculation(unknownsSwitchCheckBox.isSelected());
        expTree.setSquidSwitchConcentrationReferenceMaterialCalculation(concRefMatSwitchCheckBox.isSelected());
        expTree.setSquidSwitchSCSummaryCalculation(summaryCalculationSwitchCheckBox.isSelected());
        expTree.setSquidSpecialUPbThExpression(specialUPbThSwitchCheckBox.isSelected());
        expTree.setUnknownsGroupSampleName(unknownGroupsComboBox.getValue());

        // to detect ratios of interest
        if (expTree instanceof BuiltInExpressionInterface) {
            ((BuiltInExpressionInterface) expTree).buildExpression();
        }

        return exp;
    }

    private Expression copySelectedExpression() {

        Expression exp = task.generateExpressionFromRawExcelStyleText(selectedExpression.get().getName(),
                selectedExpression.get().getExcelExpressionString(),
                selectedExpression.get().isSquidSwitchNU(),
                selectedExpression.get().isReferenceMaterialValue(),
                selectedExpression.get().isParameterValue()
        );

        ExpressionTree expTreeCopy = (ExpressionTree) exp.getExpressionTree();
        ExpressionTree expTree = (ExpressionTree) selectedExpression.get().getExpressionTree();

        exp.setSquidSwitchNU(selectedExpression.get().isSquidSwitchNU());
        exp.setReferenceMaterialValue(selectedExpression.get().isReferenceMaterialValue());
        copyTreeTags(expTree, expTreeCopy);

        return exp;
    }

    private void copyTreeTags(ExpressionTreeInterface source, ExpressionTreeInterface dest) {
        dest.setSquidSwitchConcentrationReferenceMaterialCalculation(source.isSquidSwitchConcentrationReferenceMaterialCalculation());
        dest.setSquidSwitchSAUnknownCalculation(source.isSquidSwitchSAUnknownCalculation());
        dest.setSquidSwitchSCSummaryCalculation(source.isSquidSwitchSCSummaryCalculation());
        dest.setSquidSwitchSTReferenceMaterialCalculation(source.isSquidSwitchSTReferenceMaterialCalculation());
        dest.setSquidSpecialUPbThExpression(source.isSquidSpecialUPbThExpression());
        dest.setUnknownsGroupSampleName(source.getUnknownsGroupSampleName());

    }

    private void save() {
        //Saves the newly built expression
        // remove spurious spaces from [expr]__[n] and expr  [n] ==> [][]
        String expression = expressionString.get();
        expression = expression.replaceAll("( )*\\[", "[");
        Expression exp = makeExpression(expressionNameTextField.getText(), expression);
        // March 2019 change logic
        // if the replacement expression does not change its name or
        // if this is a new expression, we need the low-impact route
        // of just recreating and re-evaluating this expression
        // otherwise with a new name we need the full expressions list reevaluated
        // first detect if user should have used SummaryCalculation choice
        if (((ExpressionTreeBuilderInterface) exp.getExpressionTree()).getOperation().isSummaryCalc()
                && !summaryCalculationSwitchCheckBox.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Squid recommends choosing the Summary Calculation switch ... make it so?",
                    ButtonType.YES,
                    ButtonType.NO
            );
            alert.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
            alert.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
            alert.showAndWait().ifPresent((t) -> {
                if (t.equals(ButtonType.YES)) {
                    exp.getExpressionTree().setSquidSwitchSCSummaryCalculation(true);
                }
            });
        }
        task.removeExpression(exp, false);
        //Removes the old expression if the name has been changed
        if (currentMode.get().equals(Mode.EDIT) && !exp.getName().equalsIgnoreCase(selectedExpression.get().getName())) {
            task.removeExpression(selectedExpression.get(), false);
            task.addExpression(exp, true);
        } else {
            task.addExpression(exp, false);
        }

//        //Remove if an expression already exists with the same name
//        task.removeExpression(exp, true);
//        //Removes the old expression if the name has been changed
//        if (currentMode.get().equals(Mode.EDIT) && !exp.getName().equalsIgnoreCase(selectedExpression.get().getName())) {
//            task.removeExpression(selectedExpression.get(), true);
//        }
//        task.addExpression(exp, false);
//
        //update lists
        populateExpressionListViews();
        //set the new expression as edited expression
        currentMode.set(Mode.VIEW);
        selectedExpression.set(null);
        selectedExpression.set(exp);
        refreshSaved();
        //Calculate peeks
        populatePeeks(exp);

        selectInAllPanes(exp, true);

        selectedBeforeCreateOrCopy = null;
    }

    public void refreshSaved() {
        boolean saved = true;
        if (currentMode.get().equals(Mode.EDIT) && selectedExpression.isNotNull().get()) {
            if (!selectedExpression.get().getName().equals(expressionNameTextField.getText())) {
                saved = false;
            }
            if (!selectedExpression.get().getExcelExpressionString().equals(expressionString.get())) {
                saved = false;
            }
            if (selectedExpression.get().isSquidSwitchNU() != NUSwitchCheckBox.isSelected()) {
                saved = false;
            }
            if (selectedExpression.get().getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation() != refMatSwitchCheckBox.isSelected()) {
                saved = false;
            }
            if (selectedExpression.get().getExpressionTree().isSquidSwitchSAUnknownCalculation() != unknownsSwitchCheckBox.isSelected()) {
                saved = false;
            }
            if (selectedExpression.get().getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation() != concRefMatSwitchCheckBox.isSelected()) {
                saved = false;
            }
            if (selectedExpression.get().getExpressionTree().isSquidSwitchSCSummaryCalculation() != summaryCalculationSwitchCheckBox.isSelected()) {
                saved = false;
            }
            if (selectedExpression.get().getExpressionTree().isSquidSpecialUPbThExpression() != specialUPbThSwitchCheckBox.isSelected()) {
                saved = false;
            }
            if (!selectedExpression.get().getNotes().equals(notesTextArea.getText())) {
                saved = false;
            }
            if (selectedExpression.get().getExpressionTree().getUnknownsGroupSampleName()
                    .compareToIgnoreCase(unknownGroupsComboBox.getValue()) != 0) {
                saved = false;
            }
        } else if (currentMode.get().equals(Mode.CREATE)) {
            saved = false;
        }
        expressionIsSaved.set(saved);
    }

    private String makeStringFromTextFlow() {
        return makeStringFromExpressionTextNodeList(expressionTextFlow.getChildren());
    }

    private String makeStringFromExpressionTextNodeList(List<Node> list) {
        StringBuilder sb = new StringBuilder();
        for (Node node : list) {
            if (node instanceof ExpressionTextNode) {
                switch (((ExpressionTextNode) node).getText()) {
                    case VISIBLENEWLINEPLACEHOLDER:
                    case INVISIBLENEWLINEPLACEHOLDER:
                        sb.append("\n");
                        break;
                    case VISIBLETABPLACEHOLDER:
                    case INVISIBLETABPLACEHOLDER:
                        sb.append("\t");
                        break;
                    case INVISIBLEWHITESPACEPLACEHOLDER:
                    case VISIBLEWHITESPACEPLACEHOLDER:
                        sb.append(" ");
                        break;
                    default:
                        String txt = ((ExpressionTextNode) node).getText().trim();
                        String nonLetter = "\t\n\r [](),+-*/<>=^\"";
                        if (sb.length() == 0 || nonLetter.indexOf(sb.charAt(sb.length() - 1)) != -1 || nonLetter.indexOf(txt.charAt(0)) != -1) {
                            sb.append(txt);
                        } else {
                            sb.append(" ").append(txt);
                        }
                        break;
                }
            }
        }
        return sb.toString();
    }

    private void makeTextFlowFromString(String string) {

        List<Node> children = new ArrayList<>();

        //The lexer separates the expression into tokens
        // updated to fix deprecations July 2018
        try {
            InputStream stream = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
            ExpressionsForSquid2Lexer lexer = new ExpressionsForSquid2Lexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
            List<? extends Token> tokens = lexer.getAllTokens();

            //Creates the notes from tokens
            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                String nodeText = token.getText();

                ExpressionTextNode etn;

                // Make a node of the corresponding type
                if (ShuntingYard.isNumber(nodeText) || NUMBERSTRING.equals(nodeText)) {
                    etn = new NumberTextNode(' ' + nodeText + ' ');
                } else if (listOperators.contains(nodeText)) {
                    etn = new OperationTextNode(' ' + nodeText + ' ');
                } else if (nodeText.equals("\n") || nodeText.equals("\r")) {
                    if (whiteSpaceVisible.get()) {
                        etn = new PresentationTextNode(VISIBLENEWLINEPLACEHOLDER);
                    } else {
                        etn = new PresentationTextNode(INVISIBLENEWLINEPLACEHOLDER);
                    }
                } else if (nodeText.equals("\t")) {
                    if (whiteSpaceVisible.get()) {
                        etn = new PresentationTextNode(VISIBLETABPLACEHOLDER);
                    } else {
                        etn = new PresentationTextNode(INVISIBLETABPLACEHOLDER);
                    }
                } else if (nodeText.equals(" ")) {
                    if (whiteSpaceVisible.get()) {
                        etn = new PresentationTextNode(VISIBLEWHITESPACEPLACEHOLDER);
                    } else {
                        etn = new PresentationTextNode(INVISIBLEWHITESPACEPLACEHOLDER);
                    }
                } else {
                    etn = new ExpressionTextNode(' ' + nodeText + ' ');
                }

                etn.setOrdinalIndex(i);
                children.add(etn);
            }
        } catch (IOException iOException) {
        }
        expressionTextFlow.getChildren().setAll(children);
    }

    private void insertFunctionIntoExpressionTextFlow(String content, double ordinalIndex) {
        //Add spaces in order to split
        content = content.replaceAll("([(,)])", " $1 ");
        String[] funcCall = content.split(" ");

        for (int i = 0; i < funcCall.length; i++) {
            if (funcCall[i].compareTo("") != 0) {
                //Add spaces
                funcCall[i] = funcCall[i].replaceAll("([(,)])", " $1 ");
                ExpressionTextNode expressionTextNode = new ExpressionTextNode(funcCall[i]);
                expressionTextNode.setOrdinalIndex(ordinalIndex - (9 - i) * 0.01);
                expressionTextFlow.getChildren().add(expressionTextNode);
            }
        }
        updateExpressionTextFlowChildren();
    }

    private void insertOperationIntoExpressionTextFlow(String content, double ordinalIndex) {
        //Add spaces
        ExpressionTextNode exp = new OperationTextNode(' ' + content.trim() + ' ');
        exp.setOrdinalIndex(ordinalIndex);
        expressionTextFlow.getChildren().add(exp);
        updateExpressionTextFlowChildren();
    }

    private void insertNumberIntoExpressionTextFlow(String content, double ordinalIndex) {
        //Add spaces
        ExpressionTextNode exp = new NumberTextNode(' ' + content.trim() + ' ');
        exp.setOrdinalIndex(ordinalIndex);
        expressionTextFlow.getChildren().add(exp);
        updateExpressionTextFlowChildren();
    }

    private void insertExpressionIntoExpressionTextFlow(String content, double ordinalIndex) {
        //Add spaces
        ExpressionTextNode exp = new ExpressionTextNode(' ' + content.trim() + ' ');
        exp.setOrdinalIndex(ordinalIndex);
        expressionTextFlow.getChildren().add(exp);
        updateExpressionTextFlowChildren();
    }

    private void insertPresentationIntoExpressionTextFlow(String content, double ordinalIndex) {
        ExpressionTextNode exp = new PresentationTextNode(content);
        exp.setOrdinalIndex(ordinalIndex);
        expressionTextFlow.getChildren().add(exp);
        updateExpressionTextFlowChildren();
    }

    private void wrapInParentheses(double ordLeft, double ordRight) {
        wrap(ordLeft, ordRight, "(", ")");
    }

    private void wrapInBracketsAndQuotes(double ordLeft, double ordRight) {
        wrap(ordLeft, ordRight, "[\"", "\"]");
    }

    private void wrap(double ordLeft, double ordRight, String stringLeft, String stringRight) {
        //Insert strings before ordLeft and after ordRight
        ExpressionTextNode left = new ExpressionTextNode(" " + stringLeft + " ");
        left.setOrdinalIndex(ordLeft - 0.5);
        ExpressionTextNode right = new ExpressionTextNode(" " + stringRight + " ");
        right.setOrdinalIndex(ordRight + 0.5);
        expressionTextFlow.getChildren().addAll(left, right);
        updateExpressionTextFlowChildren();
    }

    private void saveUndo(String v) {
        undoListForExpression.add(v);
        redoListForExpression.clear();
    }

    private void resetDragSources() {
        dragOperationOrFunctionSource.set(null);
        dragNumberSource.set(null);
        dragPresentationSource.set(null);
    }

    private void graphExpressionTree(ExpressionTreeInterface expTree) {

        String contentLocalGraphingOff = "<html>\n"
                + "<h3> &nbsp; </h3>\n"
                + "<h3 style=\"text-align:center;\">Local graphing is off.</h3>\n"
                + "</html>";

        String contentNoExpression = "";

        String graphContents;
        //Decides the content to show
        if (expTree != null) {
            graphContents = ExpressionTreeWriterMathML.toStringBuilderMathML(expTree).toString();
        } else {
            graphContents = contentNoExpression;
        }

        //Show in the software?
        if (showGraphCheckBox.isSelected()) {
            graphView.getEngine().loadContent(graphContents);
        } else {
            graphView.getEngine().loadContent(contentLocalGraphingOff);
        }

        //Show in the browser?
        if (graphBrowserCheckBox.isSelected()) {
            try {
                Files.write(Paths.get("EXPRESSION.HTML"), graphContents.getBytes());
                BrowserControl.showURI("EXPRESSION.HTML");

            } catch (IOException iOException) {
            }
        }

    }

    private class ExpressionCellFactory implements Callback<ListView<Expression>, ListCell<Expression>> {

        private final boolean showImage;

        public ExpressionCellFactory() {
            showImage = false;
        }

        public ExpressionCellFactory(boolean showImage) {
            this.showImage = showImage;
        }

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
                        String mainText = expression.buildShortSignatureString();
                        String postPend = (expression.isParameterValue() || (expression.isReferenceMaterialValue())) ? " (see Notes)" : "";
                        setText(mainText + postPend);
                        if (showImage) {
                            ImageView imageView;
                            if (expression.amHealthy()) {
                                imageView = new ImageView(HEALTHY);

                            } else {
                                imageView = new ImageView(UNHEALTHY);
                            }
                            imageView.setFitHeight(12);
                            imageView.setFitWidth(12);
                            setGraphic(imageView);
                        }
                        Tooltip toolTip = createFloatingTooltip("[\"" + mainText + "\"]");
                        setOnMouseEntered((event) -> {
                            showToolTip(event, this, toolTip);
                        });
                        setOnMouseExited((event) -> {
                            hideToolTip(toolTip, this);
                        });
                        setOnMouseMoved((event) -> {
                            showToolTip(event, this, toolTip);
                        });
                    }
                }
            };

            updateCellMode(currentMode.get(), cell);

            currentMode.addListener((observable, oldValue, newValue) -> {
                updateCellMode(newValue, cell);
            });

            expressionsAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
                //Reupdates the cell mode when changing pane because sometimes they are not in the right mode
                updateCellMode(currentMode.get(), cell);
            });

            return cell;
        }

        private void showToolTip(MouseEvent event, ListCell<Expression> cell, Tooltip toolTip) {
            if (toolTip != null) {
                if (keyMap.get(KeyCode.T)) {
                    toolTip.show(cell, event.getScreenX() + 10, event.getScreenY() + 10);
                } else {
                    hideToolTip(toolTip, cell);
                }
            }
        }

        private void hideToolTip(Tooltip t, ListCell<Expression> cell) {
            if (t != null) {
                t.hide();
            }
            switch (currentMode.get()) {
                case VIEW:
                    cell.setCursor(Cursor.HAND);
                    break;
                case CREATE:
                case EDIT:
                    cell.setCursor(Cursor.OPEN_HAND);
            }
        }

        private void updateCellMode(Mode mode, ListCell<Expression> cell) {
            switch (mode) {
                case VIEW:
                    setCellModeView(cell);
                    break;
                case CREATE:
                case EDIT:
                    setCellModeEditCreate(cell);
            }
        }

        private void setCellModeView(ListCell<Expression> cell) {
            cell.setOnDragDetected((event) -> {
                //Nothing
            });
            cell.setOnDragDone((event) -> {
                //Nothing
            });
            cell.setOnMousePressed((event) -> {
                //Nothing
            });
            cell.setOnMouseReleased((event) -> {
                //Nothing
            });
            cell.setCursor(Cursor.HAND);

            ContextMenu cm = new ContextMenu();

            cell.setOnMouseClicked((event) -> {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    cm.getItems().clear();

                    MenuItem remove = new MenuItem("Remove expression");
                    remove.setOnAction((t) -> {
                        int index = cell.getIndex();
                        ListView parent = cell.getListView();
                        removedExpressions.add(cell.getItem());
                        task.removeExpression(cell.getItem(), true);
                        selectedExpression.set(null);
                        populateExpressionListViews();

                        //Determines the new expression to select
                        int size = parent.getItems().size();
                        if (size <= index) {
                            index = size - 1;
                        }
                        if (index >= 0) {
                            selectInAllPanes((Expression) parent.getItems().get(index), false);
                        } else {
                            if (namedExpressions.size() > 0) {
                                selectInAllPanes(namedExpressions.get(0), true);
                            }
                        }
                    });
                    cm.getItems().add(remove);

                    MenuItem restore = new MenuItem("Restore removed expressions");
                    restore.setOnAction((t) -> {
                        for (Expression removedExp : removedExpressions) {
                            boolean nameExist;
                            do {
                                nameExist = false;
                                for (Expression e : namedExpressions) {
                                    if (e.getName().equalsIgnoreCase(removedExp.getName())) {
                                        removedExp.setName(removedExp.getName() + " [restored]");
                                        nameExist = true;
                                    }
                                }
                            } while (nameExist);

                            task.addExpression(removedExp, true);
                        }
                        removedExpressions.clear();
                        populateExpressionListViews();
                    });
                    restore.setDisable(removedExpressions.isEmpty());
                    cm.getItems().add(restore);

                    cm.getItems().add(new SeparatorMenuItem());

                    Menu export = new Menu("Export as");

                    MenuItem exportXML = new MenuItem("XML document");
                    exportXML.setOnAction((t) -> {
                        try {
                            FileHandler.saveExpressionFileXML(cell.getItem(), SquidUI.primaryStageWindow);
                        } catch (IOException ex) {
                        }
                    });
                    export.getItems().add(exportXML);

                    MenuItem exportHTML = new MenuItem("HTML document");
                    exportHTML.setOnAction((t) -> {
                        try {
                            FileHandler.saveExpressionGraphHTML(cell.getItem(), SquidUI.primaryStageWindow);
                        } catch (IOException ex) {
                        }
                    });
                    export.getItems().add(exportHTML);

                    cm.getItems().add(export);

                    cm.show(cell, event.getScreenX(), event.getScreenY());
                }
            });

        }

        private void setCellModeEditCreate(ListCell<Expression> cell) {
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    // detect if brackets and quotes needed
                    if (cell.getItem().getName().matches("^[a-zA-Z0-9_$]*$")) {
                        cc.putString(cell.getItem().getName());
                    } else {
                        cc.putString("[\"" + cell.getItem().getName() + "\"]");
                    }
                    db.setContent(cc);
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

            cell.setOnMouseClicked((event) -> {
                //Nothing
            });
        }

    }

    private class ExpressionTreeCellFactory implements Callback<ListView<SquidRatiosModel>, ListCell<SquidRatiosModel>> {

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
                        Tooltip t = createFloatingTooltip("[\"" + getText() + "\"]");
                        setOnMouseEntered((event) -> {
                            showToolTip(event, this, t);
                        });
                        setOnMouseExited((event) -> {
                            hideToolTip(t, this);
                        });
                        setOnMouseMoved((event) -> {
                            showToolTip(event, this, t);
                        });
                    }
                }
            };

            updateCellMode(currentMode.get(), cell);

            currentMode.addListener((observable, oldValue, newValue) -> {
                updateCellMode(newValue, cell);
            });

            expressionsAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
                updateCellMode(currentMode.get(), cell);
            });

            return cell;
        }

        private void showToolTip(MouseEvent event, ListCell<SquidRatiosModel> cell, Tooltip t) {
            if (t != null) {
                if (keyMap.get(KeyCode.T)) {
                    t.show(cell, event.getScreenX() + 10, event.getScreenY() + 10);
                } else {
                    hideToolTip(t, cell);
                }
            }
        }

        private void hideToolTip(Tooltip t, ListCell<SquidRatiosModel> cell) {
            if (t != null) {
                t.hide();
            }
            switch (currentMode.get()) {
                case VIEW:
                    cell.setCursor(Cursor.HAND);
                    break;
                case CREATE:
                case EDIT:
                    cell.setCursor(Cursor.OPEN_HAND);
            }
        }

        private void updateCellMode(Mode mode, ListCell<SquidRatiosModel> cell) {
            switch (mode) {
                case VIEW:
                    setCellModeView(cell);
                    break;
                case CREATE:
                case EDIT:
                    setCellModeEditCreate(cell);
            }
        }

        private void setCellModeView(ListCell<SquidRatiosModel> cell) {
            cell.setOnDragDetected(event -> {
                //Nothing
            });

            cell.setOnDragDone((event) -> {
                //Nothing
            });

            cell.setOnMousePressed((event) -> {
                //Nothing
            });

            cell.setOnMouseReleased((event) -> {
                //Nothing
            });

            cell.setCursor(Cursor.DEFAULT);
        }

        private void setCellModeEditCreate(ListCell<SquidRatiosModel> cell) {
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString("[\"" + cell.getItem().getRatioName() + "\"]");
                    db.setContent(cc);
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
        }

    }

    private class StringCellFactory implements Callback<ListView<String>, ListCell<String>> {

        private final ObjectProperty<String> dragSource;

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
                        Tooltip t
                                = createFloatingTooltip(getText()
                                        .replaceAll("(:.*|\\(.*\\))$", "").trim()
                                        .replaceAll("Tab", VISIBLETABPLACEHOLDER)
                                        .replaceAll("New line", VISIBLENEWLINEPLACEHOLDER)
                                        .replaceAll("White space", VISIBLEWHITESPACEPLACEHOLDER));
                        setOnMouseEntered((event) -> {
                            showToolTip(event, this, t);
                        });
                        setOnMouseExited((event) -> {
                            hideToolTip(t, this);
                        });
                        setOnMouseMoved((event) -> {
                            showToolTip(event, this, t);
                        });
                    }
                }
            };

            updateCellMode(currentMode.get(), cell);

            currentMode.addListener((observable, oldValue, newValue) -> {
                updateCellMode(newValue, cell);
            });

            expressionsAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
                updateCellMode(currentMode.get(), cell);
            });

            return cell;
        }

        private void showToolTip(MouseEvent event, ListCell<String> cell, Tooltip t) {
            if (t != null) {
                if (keyMap.get(KeyCode.T)) {
                    t.show(cell, event.getScreenX() + 10, event.getScreenY() + 10);
                } else {
                    hideToolTip(t, cell);
                }
            }
        }

        private void hideToolTip(Tooltip t, ListCell<String> cell) {
            if (t != null) {
                t.hide();
            }
            switch (currentMode.get()) {
                case VIEW:
                    cell.setCursor(Cursor.HAND);
                    break;
                case CREATE:
                case EDIT:
                    cell.setCursor(Cursor.OPEN_HAND);
            }
        }

        private void updateCellMode(Mode mode, ListCell<String> cell) {
            switch (mode) {
                case VIEW:
                    setCellModeView(cell);
                    break;
                case CREATE:
                case EDIT:
                    setCellModeEditCreate(cell);
            }
        }

        private void setCellModeView(ListCell<String> cell) {
            cell.setOnDragDetected(event -> {
                //Nothing
            });

            cell.setOnDragDone((event) -> {
                //Nothing
            });

            cell.setOnMousePressed((event) -> {
                //Nothing
            });

            cell.setOnMouseReleased((event) -> {
                //Nothing
            });

            cell.setCursor(Cursor.DEFAULT);
        }

        private void setCellModeEditCreate(ListCell<String> cell) {
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
                resetDragSources();
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
        }
    }

    private class PresentationTextNode extends ExpressionTextNode {

        public PresentationTextNode(String text) {
            super(text);
            this.fontSize = EXPRESSION_BUILDER_DEFAULT_FONTSIZE + 3;
            updateFontSize();
            if (text.equals(INVISIBLEWHITESPACEPLACEHOLDER) || text.equals(VISIBLEWHITESPACEPLACEHOLDER)) {
                this.isWhiteSpace = true;
                whiteSpaceVisible.addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        if (newValue) {
                            setText(VISIBLEWHITESPACEPLACEHOLDER);
                        } else {
                            setText(INVISIBLEWHITESPACEPLACEHOLDER);
                        }
                        updateMode(currentMode.get());
                    }
                });
            }
            if (text.equals(INVISIBLENEWLINEPLACEHOLDER) || text.equals(VISIBLENEWLINEPLACEHOLDER)) {
                this.isWhiteSpace = true;
                whiteSpaceVisible.addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        if (newValue) {
                            setText(VISIBLENEWLINEPLACEHOLDER);
                        } else {
                            setText(INVISIBLENEWLINEPLACEHOLDER);
                        }
                        updateMode(currentMode.get());
                    }
                });
            }
            if (text.equals(INVISIBLETABPLACEHOLDER) || text.equals(VISIBLETABPLACEHOLDER)) {
                this.isWhiteSpace = true;
                whiteSpaceVisible.addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        if (newValue) {
                            setText(VISIBLETABPLACEHOLDER);
                        } else {
                            setText(INVISIBLETABPLACEHOLDER);
                        }
                        updateMode(currentMode.get());
                    }
                });
            }
            updateMode(currentMode.get());
        }
    }

    private class OperationTextNode extends ExpressionTextNode {

        public OperationTextNode(String text) {
            super(text);
            this.regularColor = Color.GREEN;
            setFill(regularColor);
            this.fontSize = EXPRESSION_BUILDER_DEFAULT_FONTSIZE + 3;
            updateFontSize();
        }
    }

    // this node signals user can edit in context menu
    private class NumberTextNode extends ExpressionTextNode {

        public NumberTextNode(String text) {
            super(text);
        }

    }

    private void initNodeSelection() {
        selectedNodes.addListener((Change<? extends ExpressionTextNode> c) -> {
            while (c.next()) {
                for (ExpressionTextNode ex : c.getAddedSubList()) {
                    ex.setSelected(true);
                }
                for (ExpressionTextNode ex : c.getRemoved()) {
                    ex.setSelected(false);
                }
            }
        });

    }

    private class ExpressionTextNode extends Text {

        private boolean selected = false;
        protected boolean isWhiteSpace;

        private final String text;
        private double ordinalIndex;
        private boolean popupShowing;

        protected Color regularColor;
        protected Color selectedColor;
        protected Color opositeColor;

        protected int fontSize;

        //Saves where was the indicator before moving it in order to be able to restore it when drag is exiting this node
        int previousIndicatorIndex = -1;

        private Tooltip tooltip;

        ExpressionTextNode oppositeParenthese = null;

        public ExpressionTextNode(String text) {
            super(text);

            setFontSmoothingType(FontSmoothingType.LCD);
            setFont(Font.font("SansSerif"));

            this.selectedColor = Color.RED;
            this.regularColor = Color.BLACK;
            this.opositeColor = Color.LIME;

            setFill(regularColor);

            this.text = text;
            this.popupShowing = false;

            this.fontSize = EXPRESSION_BUILDER_DEFAULT_FONTSIZE;
            updateFontSize();

            updateMode(currentMode.get());

            currentMode.addListener((observable, oldValue, newValue) -> {
                updateMode(newValue);
            });

            setTooltip(createFloatingTooltip(text));
        }

        private void setSelected(boolean selected) {
            if (this.selected != selected) {
                this.selected = selected;
                if (selected) {
                    if (!keyMap.get(KeyCode.CONTROL)) {
                        selectedNodes.clear();
                    } else if (keyMap.get(KeyCode.SHIFT)) {
                        boolean hasSelectedNodeBefore = false;
                        ExpressionTextNode precNode = null;
                        for (int i = 0; i < expressionTextFlow.getChildren().size(); i++) {
                            if (expressionTextFlow.getChildren().get(i) instanceof ExpressionTextNode) {
                                ExpressionTextNode nodeLoop = (ExpressionTextNode) expressionTextFlow.getChildren().get(i);
                                if (selectedNodes.contains(nodeLoop)) {
                                    hasSelectedNodeBefore = true;
                                    precNode = null;
                                } else if (nodeLoop.equals(this)) {
                                    break;
                                } else {
                                    precNode = nodeLoop;
                                }
                            }
                        }
                        if (hasSelectedNodeBefore && precNode != null) {
                            precNode.setSelected(true);
                        }
                    }
                    if (!selectedNodes.contains(this)) {
                        selectedNodes.add(this);
                    }
                    setFill(selectedColor);
                } else {
                    if (selectedNodes.contains(this)) {
                        selectedNodes.remove(this);
                    }
                    setFill(regularColor);
                }
            }
        }

        public final void updateFontSize() {
            setFont(Font.font("SansSerif", FontWeight.SEMI_BOLD, fontSize + fontSizeModifier));
        }

        private void selectOppositeParenthese() {
            if (text.trim().matches("^[()\\[\\]]$")) {
                String current = text.trim();
                String opposite = "";
                Predicate<Double> predicate = (Double t) -> {
                    return false;
                };
                List<Node> nodes = expressionTextFlow.getChildren();
                switch (current) {
                    case "[":
                        opposite = "]";
                        predicate = (Double t) -> {
                            return t > ordinalIndex;
                        };
                        break;
                    case "]":
                        opposite = "[";
                        predicate = (Double t) -> {
                            return t < ordinalIndex;
                        };
                        nodes = Lists.reverse(nodes);
                        break;
                    case "(":
                        opposite = ")";
                        predicate = (Double t) -> {
                            return t > ordinalIndex;
                        };
                        break;
                    case ")":
                        opposite = "(";
                        predicate = (Double t) -> {
                            return t < ordinalIndex;
                        };
                        nodes = Lists.reverse(nodes);
                        break;
                }
                int cpt = 0;
                for (Node node : nodes) {
                    if (node instanceof ExpressionTextNode) {
                        ExpressionTextNode etn = (ExpressionTextNode) node;
                        if (predicate.test(etn.getOrdinalIndex())) {
                            if (etn.getText().trim().equals(opposite)) {
                                if (cpt == 0) {
                                    oppositeParenthese = etn;
                                    etn.setFill(etn.opositeColor);
                                    break;
                                } else {
                                    cpt--;
                                }
                            } else if (etn.getText().trim().equals(current)) {
                                cpt++;
                            }
                        }
                    }
                }
            }
        }

        public void resetColor() {
            if (selected) {
                setFill(selectedColor);
            } else {
                setFill(regularColor);
            }
        }

        private void unselectOppositeParenthese() {
            if (oppositeParenthese != null) {
                oppositeParenthese.resetColor();
                oppositeParenthese = null;
            }
        }

        private void setTooltip(Tooltip t) {
            tooltip = t;
            setOnMouseEntered((event) -> {
                showToolTip(event);
            });
            setOnMouseExited((event) -> {
                hideToolTip();
            });
            setOnMouseMoved((event) -> {
                showToolTip(event);
            });
        }

        private void showToolTip(MouseEvent event) {
            if (tooltip != null) {
                if (keyMap.get(KeyCode.T)) {
                    tooltip.show(this, event.getScreenX() + 10, event.getScreenY() + 10);
                } else {
                    hideToolTip();
                }
            }
        }

        private void hideToolTip() {
            if (tooltip != null) {
                tooltip.hide();
            }
        }

        protected final void updateMode(Mode mode) {
            if (isWhiteSpace && !whiteSpaceVisible.get()) {
                setNodeModeView();
            } else {
                switch (mode) {
                    case VIEW:
                        setNodeModeView();
                        break;
                    case CREATE:
                    case EDIT:
                        setNodeModeEditCreate();
                }
            }
        }

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

        private void setNodeModeEditCreate() {
            setCursor(Cursor.OPEN_HAND);

            setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton() == MouseButton.SECONDARY && event.getEventType() == MouseEvent.MOUSE_CLICKED && !popupShowing) {
                    if (!selected) {
                        setSelected(true);
                    }
                    createExpressionTextNodeContextMenu((ExpressionTextNode) event.getSource()).show((ExpressionTextNode) event.getSource(), event.getScreenX(), event.getScreenY());
                    popupShowing = true;
                } else if (event.getButton() == MouseButton.PRIMARY && event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    setSelected(!selected);
                }
            });

            setOnMousePressed((MouseEvent event) -> {
                hideToolTip();
                selectOppositeParenthese();
                setCursor(Cursor.CLOSED_HAND);
                expressionTextFlow.requestLayout();//fixes a javafx bug where the etn are sometimes not updated
            });

            setOnMouseReleased((MouseEvent event) -> {
                showToolTip(event);
                unselectOppositeParenthese();
                setCursor(Cursor.OPEN_HAND);
            });

            setOnDragDetected((MouseEvent event) -> {
                unselectOppositeParenthese();
                setCursor(Cursor.CLOSED_HAND);
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                ClipboardContent cc = new ClipboardContent();
                cc.putString(text);
                db.setContent(cc);
            });

            setOnDragDone((event) -> {
                setCursor(Cursor.OPEN_HAND);
                expressionTextFlow.getChildren().remove(insertIndicator);
                previousIndicatorIndex = -1;
            });

            setOnDragOver((DragEvent event) -> {
                if (event.getGestureSource() != (ExpressionTextNode) event.getSource()) {
                    event.acceptTransferModes(TransferMode.COPY, TransferMode.MOVE);
                }
                event.consume();
            });

            setOnDragEntered((event) -> {
                if (expressionTextFlow.getChildren().contains(insertIndicator)) {
                    //Save previous position and remove
                    previousIndicatorIndex = expressionTextFlow.getChildren().indexOf(insertIndicator);
                    expressionTextFlow.getChildren().remove(insertIndicator);
                } else {
                    //No previous position
                    previousIndicatorIndex = -1;
                }
                if (dragndropReplaceRadio.isSelected()) {
                    setFill(selectedColor);
                } else {
                    //determines index
                    int index = expressionTextFlow.getChildren().indexOf(this);
                    if (dragndropRightRadio.isSelected()) {
                        index++;
                    }
                    //And show indicator
                    expressionTextFlow.getChildren().add(index, insertIndicator);
                }
            });

            setOnDragExited((event) -> {
                //Remove indicator
                expressionTextFlow.getChildren().remove(insertIndicator);
                if (dragndropReplaceRadio.isSelected()) {
                    setFill(regularColor);
                }
                //And restore it if there is a previous position
                if (previousIndicatorIndex != -1) {
                    expressionTextFlow.getChildren().add(previousIndicatorIndex, insertIndicator);
                }
            });

            setOnDragDropped((DragEvent event) -> {
                //reset color and indicator
                if (dragndropReplaceRadio.isSelected()) {
                    setFill(regularColor);
                }
                expressionTextFlow.getChildren().remove(insertIndicator);
                previousIndicatorIndex = -1;

                Dragboard db = event.getDragboard();
                boolean success = false;
                double ord = 0.0;

                //Chosing where to put the new node
                if (toggleGroup.getSelectedToggle() == dragndropLeftRadio) {
                    ord = ordinalIndex - 0.5;
                } else if (toggleGroup.getSelectedToggle() == dragndropReplaceRadio) {
                    ord = ordinalIndex;
                    expressionTextFlow.getChildren().remove(this);
                } else if (toggleGroup.getSelectedToggle() == dragndropRightRadio) {
                    ord = ordinalIndex + 0.5;
                }

                //If moving an existing node
                if (event.getGestureSource() instanceof ExpressionTextNode) {
                    if (selectedNodes.size() > 1) {
                        //If multiple nodes update all indexes
                        for (ExpressionTextNode etn : selectedNodes) {
                            etn.setOrdinalIndex(ord + 0.001 * etn.getOrdinalIndex());
                        }
                    } else {
                        //Else just update the index
                        ((ExpressionTextNode) event.getGestureSource()).setOrdinalIndex(ord);
                    }
                    updateExpressionTextFlowChildren();
                    success = true;
                } //If copying a node from the lists
                else if (db.hasString()) {
                    String content = db.getString().split(OPERATION_FLAG_DELIMITER)[0];
                    //Insert the right type of node
                    if ((dragOperationOrFunctionSource.get() != null) && (!db.getString().contains(OPERATION_FLAG_DELIMITER))) {
                        // case of function, make a series of objects
                        insertFunctionIntoExpressionTextFlow(content, ord);
                    } else if ((dragOperationOrFunctionSource.get() != null) && (db.getString().contains(OPERATION_FLAG_DELIMITER))) {
                        // case of operation
                        insertOperationIntoExpressionTextFlow(content, ord);
                    } else if ((dragNumberSource.get() != null) && content.contains(NUMBERSTRING)) {
                        // case of "NUMBER"
                        insertNumberIntoExpressionTextFlow(NUMBERSTRING, ord);
                    } else if (dragPresentationSource.get() != null && presentationMap.containsKey(dragPresentationSource.get())) {
                        // case of presentation (new line, tab)
                        insertPresentationIntoExpressionTextFlow(presentationMap.get(dragPresentationSource.get()), ord);
                    } else {
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

        private void setNodeModeView() {
            setCursor(Cursor.DEFAULT);

            setOnMouseClicked((MouseEvent event) -> {
                //Nothing
            });

            setOnMousePressed((MouseEvent event) -> {
                hideToolTip();
                selectOppositeParenthese();
                setFill(selectedColor);
                expressionTextFlow.requestLayout();//fixes a javafx bug where the etn are sometimes not updated
            });

            setOnMouseReleased((MouseEvent event) -> {
                showToolTip(event);
                unselectOppositeParenthese();
                setFill(regularColor);
            });

            setOnDragDetected((MouseEvent event) -> {
                //Nothing
            });

            setOnDragDone((event) -> {
                //Nothing
            });

            setOnDragOver((DragEvent event) -> {
                //Nothing
            });

            setOnDragDropped((DragEvent event) -> {
                //Nothing
            });
        }
    }

}
