/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import org.cirdles.squid.constants.Squid3Constants;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFAULT_BACKGROUND_MASS_LABEL;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REQUIRED_NOMINAL_MASSES;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REQUIRED_RATIO_NAMES;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import org.cirdles.squid.utilities.IntuitiveStringComparator;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TaskFolderBrowserController implements Initializable {

    private List<TaskInterface> taskFilesInFolder = new ArrayList<>();
    public static File tasksFolder;

    private ListView<TaskInterface> listViewOfTasksInFolder;

    @FXML
    private VBox vboxMaster;
    @FXML
    private ScrollPane taskListScrollPane;
    @FXML
    private AnchorPane taskListAnchorPane;
    @FXML
    private ScrollPane taskScrollPane;
    @FXML
    private AnchorPane taskDetailAnchorPane;
    @FXML
    private HBox taskFolderBrowserTitleBar;
    @FXML
    private Label nameOfTasksFolderLabel;
    @FXML
    private GridPane taskManagerGridPane;
    @FXML
    private TextField authorsNameTextField;
    @FXML
    private TextField labNameTextField;
    @FXML
    private ToggleGroup primaryAgeToggleGroup;
    @FXML
    private TextField uncorrConstPbUExpressionText;
    @FXML
    private Label uncorrConstPbUlabel;
    @FXML
    private Label uncorrConstPbThlabel;
    @FXML
    private TextField uncorrConstPbThExpressionText;
    @FXML
    private TextFlow defaultMassesListTextFlow;
    @FXML
    private TextFlow defaultRatiosListTextFlow;
    @FXML
    private TextField taskNameTextField;
    @FXML
    private Label projectModeLabel;
    @FXML
    private TextField taskDescriptionTextField;
    @FXML
    private TextField provenanceTextField;
    @FXML
    private TextField pb208Th232ExpressionText;
    @FXML
    private TextField parentConcExpressionText;
    @FXML
    private Label th232U238Label;
    @FXML
    private Label parentConcLabel;
    @FXML
    private ToggleGroup dirctALTtoggleGroup;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateListOfTasks();
    }

    private void populateListOfTasks() {

        nameOfTasksFolderLabel.setText("Browsing Tasks Folder: " + tasksFolder.getName());

        taskFilesInFolder = new ArrayList<>();
        if (tasksFolder != null) {
            // collect Tasks if any
            for (File file : tasksFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    return name.toLowerCase().endsWith(".xml");
                }
            }
            )) {
                // check if task 
                try {
                    TaskInterface task = (Task) ((XMLSerializerInterface) squidProject.getTask()).readXMLObject(file.getAbsolutePath(), false);
                    if (task != null) {
                        taskFilesInFolder.add(task);
                    }
                } catch (Exception /*| com.thoughtworks.xstream.mapper.CannotResolveClassException*/ e) {
//                    System.out.println(">>BAD FILE");
                }
            };

        }

        listViewOfTasksInFolder = new ListView<>();
        listViewOfTasksInFolder.setCellFactory(
                (parameter)
                -> new TaskDisplayName()
        );

        ObservableList<TaskInterface> items = FXCollections.observableArrayList(taskFilesInFolder);
        IntuitiveStringComparator<String> intuitiveStringComparator = new IntuitiveStringComparator<>();
        items = items.sorted((TaskInterface task1, TaskInterface task2) -> {
            return intuitiveStringComparator.compare(task1.getName(), task2.getName());
        });
        listViewOfTasksInFolder.setItems(items);
        listViewOfTasksInFolder.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TaskInterface>() {
            @Override
            public void changed(ObservableValue<? extends TaskInterface> observable, TaskInterface oldValue, TaskInterface selectedTask) {
                taskNameTextField.setText(selectedTask.getName());
                projectModeLabel.setText(((Task) selectedTask).getTaskType().getName());
                taskDescriptionTextField.setText(selectedTask.getDescription());
                authorsNameTextField.setText(selectedTask.getAuthorName());
                labNameTextField.setText(selectedTask.getLabName());
                provenanceTextField.setText(selectedTask.getProvenance());
                populateMasses(selectedTask);
                populateRatios(selectedTask);
                populateDirectives(selectedTask);
                showPermutationPlayers();

            }
        });

        if (taskFilesInFolder.size() > 0) {
            listViewOfTasksInFolder.getSelectionModel().selectFirst();
        }

        taskListAnchorPane.getChildren().add(listViewOfTasksInFolder);

        taskListAnchorPane.prefHeightProperty().bind(taskListScrollPane.heightProperty());
        taskListAnchorPane.prefWidthProperty().bind(taskListScrollPane.widthProperty());
        listViewOfTasksInFolder.prefHeightProperty().bind(taskListAnchorPane.prefHeightProperty());
        listViewOfTasksInFolder.prefWidthProperty().bind(taskListAnchorPane.prefWidthProperty());

        taskDetailAnchorPane.prefHeightProperty().bind(taskScrollPane.heightProperty());
        taskDetailAnchorPane.prefWidthProperty().bind(taskScrollPane.widthProperty());
        taskManagerGridPane.prefHeightProperty().bind(taskDetailAnchorPane.heightProperty());
        taskManagerGridPane.prefWidthProperty().bind(taskDetailAnchorPane.widthProperty());
    }

    private void populateMasses(TaskInterface task) {
        defaultMassesListTextFlow.getChildren().clear();
        defaultMassesListTextFlow.setMaxHeight(30);
        List<String> allMasses = new ArrayList<>();
        allMasses.addAll(task.getNominalMasses());

        Collections.sort(allMasses, new IntuitiveStringComparator<>());

        allMasses.remove(DEFAULT_BACKGROUND_MASS_LABEL);
        if (task.getIndexOfBackgroundSpecies() >= 0) {
            allMasses.add((allMasses.size() > task.getIndexOfBackgroundSpecies()
                    ? task.getIndexOfBackgroundSpecies() : allMasses.size()),
                    DEFAULT_BACKGROUND_MASS_LABEL);
        }
        int count = 1;
        for (String mass : allMasses) {
            StackPane massText;
            if (REQUIRED_NOMINAL_MASSES.contains(mass)) {
                massText = TaskDesignerController.makeMassStackPane(mass, "pink");
            } else {
                massText = TaskDesignerController.makeMassStackPane(mass, "white");
            }
            massText.setTranslateX(1 * count++);
            defaultMassesListTextFlow.getChildren().add(massText);
        }
    }

    private void populateRatios(TaskInterface task) {
        defaultRatiosListTextFlow.getChildren().clear();
        defaultRatiosListTextFlow.setMaxHeight(30);

        int count = 1;
        List<String> ratioNames = task.getRatioNames();
        for (String ratioName : ratioNames) {
            VBox ratio = TaskDesignerController.makeRatioVBox(ratioName);
            if (REQUIRED_RATIO_NAMES.contains(ratioName)) {
                ratio.setStyle(ratio.getStyle() + "-fx-border-color: black;-fx-background-color: pink;");
            } else {
                ratio.setStyle(ratio.getStyle() + "-fx-border-color: black;");
            }

            ratio.setTranslateX(1 * count++);

            defaultRatiosListTextFlow.getChildren().add(ratio);
        }
    }

    private void updateDirectiveButtons(TaskInterface task) {
        ((RadioButton) taskManagerGridPane.lookup("#232")).setDisable(task.getSelectedIndexIsotope().compareTo(Squid3Constants.IndexIsoptopesEnum.PB_208) == 0);
        ((RadioButton) taskManagerGridPane.lookup("#direct")).setDisable(task.getSelectedIndexIsotope().compareTo(Squid3Constants.IndexIsoptopesEnum.PB_208) == 0);
    }
//
//    private Expression makeExpression(String expressionName, final String expressionString) {
//        return makeExpressionForAudit(expressionName, expressionString, namedExpressionsMap);
//    }
//
//    private void buildShrimpSpeciesNodeMap(TaskInterface task) {
//        shrimpSpeciesNodeMap = new TreeMap<>();
//        for (int i = 0; i < REQUIRED_NOMINAL_MASSES.size(); i++) {
//            shrimpSpeciesNodeMap.put(
//                    REQUIRED_NOMINAL_MASSES.get(i),
//                    ShrimpSpeciesNode.buildShrimpSpeciesNode(new SquidSpeciesModel(REQUIRED_NOMINAL_MASSES.get(i)), "getPkInterpScanArray"));
//        }
//        for (int i = 0; i < task.getNominalMasses().size(); i++) {
//            shrimpSpeciesNodeMap.put(
//                    task.getNominalMasses().get(i),
//                    ShrimpSpeciesNode.buildShrimpSpeciesNode(new SquidSpeciesModel(task.getNominalMasses().get(i)), "getPkInterpScanArray"));
//        }
//    }
//
//    private Map<String, ExpressionTreeInterface> buildNamedExpressionsMap(TaskInterface task) {
//        buildShrimpSpeciesNodeMap(task);
//
//        Map<String, ExpressionTreeInterface> namedExpressionsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//
//        for (int i = 0; i < REQUIRED_RATIO_NAMES.size(); i++) {
//            String[] numDem = REQUIRED_RATIO_NAMES.get(i).split("/");
//            ExpressionTreeInterface ratio = new ExpressionTree(
//                    REQUIRED_RATIO_NAMES.get(i),
//                    shrimpSpeciesNodeMap.get(numDem[0]),
//                    shrimpSpeciesNodeMap.get(numDem[1]),
//                    Operation.divide());
//            ratio.setSquidSwitchSAUnknownCalculation(true);
//            ratio.setSquidSwitchSTReferenceMaterialCalculation(true);
//            namedExpressionsMap.put(REQUIRED_RATIO_NAMES.get(i), ratio);
//        }
//        for (int i = 0; i < task.getRatioNames().size(); i++) {
//            String[] numDem = task.getRatioNames().get(i).split("/");
//            ExpressionTreeInterface ratio = new ExpressionTree(
//                    task.getRatioNames().get(i),
//                    shrimpSpeciesNodeMap.get(numDem[0]),
//                    shrimpSpeciesNodeMap.get(numDem[1]),
//                    Operation.divide());
//            ratio.setSquidSwitchSAUnknownCalculation(true);
//            ratio.setSquidSwitchSTReferenceMaterialCalculation(true);
//            namedExpressionsMap.put(task.getRatioNames().get(i), ratio);
//        }
//        return namedExpressionsMap;
//    }

    private void populateDirectives(TaskInterface task) {
        updateDirectiveButtons(task);

        // namedExpressionsMap = buildNamedExpressionsMap(task);
        ((RadioButton) taskManagerGridPane.lookup("#" + task.getParentNuclide())).setSelected(true);
        ((RadioButton) taskManagerGridPane.lookup("#" + (String) (task.isDirectAltPD() ? "direct" : "indirect"))).setSelected(true);

        uncorrConstPbUlabel.setText(UNCOR206PB238U_CALIB_CONST + ":");
        String UTh_U = task.getSpecialSquidFourExpressionsMap().get(UNCOR206PB238U_CALIB_CONST);
        uncorrConstPbUExpressionText.setText(UTh_U);
        uncorrConstPbUExpressionText.setUserData(UNCOR206PB238U_CALIB_CONST);

        uncorrConstPbThlabel.setText(UNCOR208PB232TH_CALIB_CONST + ":");
        String UTh_Th = task.getSpecialSquidFourExpressionsMap().get(UNCOR208PB232TH_CALIB_CONST);
        uncorrConstPbThExpressionText.setText(UTh_Th);
        uncorrConstPbThExpressionText.setUserData(UNCOR208PB232TH_CALIB_CONST);

        th232U238Label.setText(TH_U_EXP_RM + ":");
        String thU = task.getSpecialSquidFourExpressionsMap().get(TH_U_EXP_DEFAULT);
        pb208Th232ExpressionText.setText(thU);
        pb208Th232ExpressionText.setUserData(TH_U_EXP_DEFAULT);

        parentConcLabel.setText(PARENT_ELEMENT_CONC_CONST + ":");
        String parentPPM = task.getSpecialSquidFourExpressionsMap().get(PARENT_ELEMENT_CONC_CONST);
        parentConcExpressionText.setText(parentPPM);
        parentConcExpressionText.setUserData(PARENT_ELEMENT_CONC_CONST);
    }

    private void showPermutationPlayers() {
        boolean uPicked = ((RadioButton) taskManagerGridPane.lookup("#238")).isSelected();
        boolean directPicked = ((RadioButton) taskManagerGridPane.lookup("#direct")).isSelected();

        boolean perm1 = uPicked && !directPicked;
        boolean perm2 = uPicked && directPicked;
        boolean perm3 = !uPicked && !directPicked;
        boolean perm4 = !uPicked && directPicked;

        updateExpressionStyle(uncorrConstPbUExpressionText, (perm1 || perm2 || perm4));
        updateExpressionStyle(uncorrConstPbThExpressionText, (perm2 || perm3 || perm4));
        updateExpressionStyle(pb208Th232ExpressionText, (perm1 || perm3));
        updateExpressionStyle(parentConcExpressionText, (perm1 || perm2 || perm3 || perm4));
    }

    private void updateExpressionStyle(TextField expressionText, boolean player) {
        String playerStyle = EXPRESSION_LIST_CSS_STYLE_SPECS
                + "-fx-background-color: white;-fx-border-color: red;-fx-border-width: 2;";
        String notPlayerStyle = EXPRESSION_LIST_CSS_STYLE_SPECS
                + "-fx-background-color: white;-fx-border-color: black;";

        expressionText.setStyle((player ? playerStyle : notPlayerStyle));
        if (!player) {
            expressionText.setText("Not Used");
        }
    }

    static class TaskDisplayName extends ListCell<TaskInterface> {

        @Override
        protected void updateItem(TaskInterface task, boolean empty) {
            super.updateItem(task, empty);
            if (task == null || empty) {
                setText(null);
            } else {
                setText(task.getName());
            }
        }
    };

}
