/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.TaskEditorController.makeMassStackPane;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REQUIRED_NOMINAL_MASSES;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REQUIRED_RATIO_NAMES;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import org.cirdles.squid.tasks.squidTask25.TaskSquid25;
import org.cirdles.squid.tasks.taskDesign.TaskDesign;
import org.cirdles.squid.utilities.IntuitiveStringComparator;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;
import org.cirdles.squid.utilities.fileUtilities.FileValidator;
import org.xml.sax.SAXException;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TaskFolderBrowserController implements Initializable {

    private List<TaskInterface> taskFilesInFolder = new ArrayList<>();
    // folder or file
    public static File tasksBrowserTarget;
    public static String tasksBrowserType = ".xml";
    
    private ListView<TaskInterface> listViewOfTasksInFolder;
    
    private static Schema taskXMLSchema;

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
    @FXML
    private TextArea customExpressionTextArea;

    private boolean amGeochronMode;
    @FXML
    private Label directivesLabel;
    @FXML
    private Label primaryDPLabel;
    @FXML
    private Label secondaryDPLabel;
    @FXML
    private Button editTaskButton;
    @FXML
    private Button replaceTaskButton;
    @FXML
    private Button saveTaskButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            taskXMLSchema = sf.newSchema(new File(Squid3Constants.URL_STRING_FOR_SQUIDTASK_XML_SCHEMA_LOCAL));
        }
        catch (SAXException e){
            e.printStackTrace();
        }
        populateListOfTasks();
    }

    private void populateListOfTasks() {

        taskFilesInFolder = new ArrayList<>();
        if (tasksBrowserTarget != null) {
            if (tasksBrowserType.compareToIgnoreCase(".xml") == 0) {
                if (tasksBrowserTarget.isDirectory()) {
                    nameOfTasksFolderLabel.setText("Browsing Tasks Folder: " + tasksBrowserTarget.getName());
                    // collect Tasks if any
                    for (File file : tasksBrowserTarget.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String name) {
                            return name.toLowerCase().endsWith(".xml");
                        }
                    }
                    )) {
                        // check if task 
                        try {
                            if (FileValidator.validateFileIsXMLSerializedEntity(file, taskXMLSchema)){
                                TaskInterface task = (Task) ((XMLSerializerInterface)
                                        squidProject.getTask()).readXMLObject(file.getAbsolutePath(), false);
                                if (task != null) {
                                    taskFilesInFolder.add(task);
                                }
                            }
                        } catch (Exception e) {
                        }
                    };
                } else {
                    nameOfTasksFolderLabel.setText("Browsing Task: " + tasksBrowserTarget.getName());
                    // check if task 
                    try {
                        if (FileValidator.validateFileIsXMLSerializedEntity(tasksBrowserTarget, taskXMLSchema)){
                            TaskInterface task = (Task) ((XMLSerializerInterface)
                                    squidProject.getTask()).readXMLObject(tasksBrowserTarget.getAbsolutePath(), false);
                            if (task != null) {
                                taskFilesInFolder.add(task);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            } else {
                // case of excel squid25 tasks
                Path path = null;
                try {
                    path = Files.createTempDirectory("convertedTasks");
                } catch (IOException iOException) {
                }
                if (tasksBrowserTarget.isDirectory()) {
                    nameOfTasksFolderLabel.setText("Browsing Tasks Folder: " + tasksBrowserTarget.getName());

                    // collect Tasks if any
                    for (File file : tasksBrowserTarget.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String name) {
                            return name.toLowerCase().endsWith(".xls");
                        }
                    }
                    )) {
                        // check if task 
                        try {
                            // first convert to xml
                            TaskSquid25 taskSquid25 = TaskSquid25.importSquidTaskFile(file);
                            TaskInterface taskFromSquid25 = squidProject.makeTaskFromSquid25Task(taskSquid25);
                            File tempTaskFile = new File(path.toString() + File.separator + taskSquid25.getTaskName() + ".xml");
                            ((XMLSerializerInterface) taskFromSquid25)
                                    .serializeXMLObject(tempTaskFile.getAbsolutePath());

                            if (FileValidator.validateFileIsXMLSerializedEntity(tempTaskFile, taskXMLSchema)){
                                TaskInterface task = (Task) ((XMLSerializerInterface)
                                        squidProject.getTask()).readXMLObject(tempTaskFile.getAbsolutePath(), false);
                                if (task != null) {
                                    taskFilesInFolder.add(task);
                                }
                            }
                        } catch (Exception e) {
                        }
                    };

                } else {
                    nameOfTasksFolderLabel.setText("Browsing Task: " + tasksBrowserTarget.getName());
                    // check if task 
                    try {
                        // first convert to xml
                        TaskSquid25 taskSquid25 = TaskSquid25.importSquidTaskFile(tasksBrowserTarget);
                        TaskInterface taskFromSquid25 = squidProject.makeTaskFromSquid25Task(taskSquid25);
                        File tempTaskFile = new File(path.toString() + File.separator + taskSquid25.getTaskName() + ".xml");
                        ((XMLSerializerInterface) taskFromSquid25)
                                .serializeXMLObject(tempTaskFile.getAbsolutePath());

                        if (FileValidator.validateFileIsXMLSerializedEntity(tempTaskFile, taskXMLSchema)){
                            TaskInterface task = (Task) ((XMLSerializerInterface)
                                    squidProject.getTask()).readXMLObject(tempTaskFile.getAbsolutePath(), false);
                            if (task != null) {
                                taskFilesInFolder.add(task);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }

        if (taskFilesInFolder.isEmpty()) {
            if (tasksBrowserType.compareToIgnoreCase(".xml") == 0) {
                SquidMessageDialog.showWarningDialog("No valid Squid3 tasks found.", primaryStageWindow);
                nameOfTasksFolderLabel.setText("No Valid Squid3 Tasks Selected");
            } else {
                SquidMessageDialog.showWarningDialog("No valid Squid2.5 tasks found.", primaryStageWindow);
                nameOfTasksFolderLabel.setText("No Valid Squid2.5 Tasks Selected");
            }
            
            editTaskButton.setDisable(true);
            replaceTaskButton.setDisable(true);
            saveTaskButton.setDisable(true);

        } else {

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
                    amGeochronMode = selectedTask.getTaskType().compareTo(Squid3Constants.TaskTypeEnum.GEOCHRON) == 0;

                    populateMasses(selectedTask);
                    populateRatios(selectedTask);

                    if (amGeochronMode) {
                        populateDirectives(selectedTask);
                        showPermutationPlayers();
                    } else {
                        uncorrConstPbUlabel.setVisible(false);
                        uncorrConstPbThlabel.setVisible(false);
                        th232U238Label.setVisible(false);
                        parentConcLabel.setVisible(false);
                        uncorrConstPbUExpressionText.setVisible(false);
                        uncorrConstPbThExpressionText.setVisible(false);
                        parentConcExpressionText.setVisible(false);
                        pb208Th232ExpressionText.setVisible(false);
                        ((RadioButton) taskManagerGridPane.lookup("#232")).setVisible(false);
                        ((RadioButton) taskManagerGridPane.lookup("#238")).setVisible(false);
                        ((RadioButton) taskManagerGridPane.lookup("#direct")).setVisible(false);
                        ((RadioButton) taskManagerGridPane.lookup("#indirect")).setVisible(false);
                        directivesLabel.setVisible(false);
                        primaryDPLabel.setVisible(false);
                        secondaryDPLabel.setVisible(false);

                    }

                    populateCustomExpressionsText(selectedTask);

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
    }

    private void populateCustomExpressionsText(TaskInterface task) {
        StringBuilder customExp = new StringBuilder();
        customExp.append("List of Custom Expression Names: \n");
        int counter = 0;
        for (Expression exp : task.getCustomTaskExpressions()) {
            counter++;
            customExp.append("\t").append(exp.getName()).append(genSpaces(exp.getName()));
            if (counter % 3 == 0) {
                customExp.append("\n");
            }
        }

        customExpressionTextArea.setText(customExp.toString());
    }

    private String genSpaces(String name) {
        String retVal = "";
        for (int i = name.length(); i < 25; i++) {
            retVal += " ";
        }
        return retVal;
    }

    private void populateMasses(TaskInterface task) {
        defaultMassesListTextFlow.getChildren().clear();
        defaultMassesListTextFlow.setMaxHeight(30);
        List<String> allMasses = new ArrayList<>();
        allMasses.addAll(task.getNominalMasses());

        Collections.sort(allMasses, new IntuitiveStringComparator<>());

//        allMasses.remove(DEFAULT_BACKGROUND_MASS_LABEL);
//        if (task.getIndexOfBackgroundSpecies() >= 0) {
//            allMasses.add((allMasses.size() > task.getIndexOfBackgroundSpecies()
//                    ? task.getIndexOfBackgroundSpecies() : allMasses.size()),
//                    DEFAULT_BACKGROUND_MASS_LABEL);
//        }
        int count = 1;
        for (String mass : allMasses) {
            StackPane massText;
            if (count == task.getIndexOfBackgroundSpecies() + 1) {
                massText = makeMassStackPane(mass, "Aquamarine");
            } else if (REQUIRED_NOMINAL_MASSES.contains(mass)) {
                massText = makeMassStackPane(mass, "pink");
            } else {
                massText = makeMassStackPane(mass, "white");

//            if (REQUIRED_NOMINAL_MASSES.contains(mass)) {
//                massText = TaskEditorController.makeMassStackPane(mass, "pink");
//            } else {
//                massText = TaskEditorController.makeMassStackPane(mass, "white");
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
            VBox ratio = TaskEditorController.makeRatioVBox(ratioName);
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

    @FXML
    private void editTaskAction(ActionEvent event) {
        TaskEditorController.existingTaskToEdit = listViewOfTasksInFolder.getSelectionModel().selectedItemProperty().getValue();
        MenuItem menuItemExistingTaskEditorHidden = ((MenuBar) SquidUI.primaryStage.getScene()
                .getRoot().getChildrenUnmodifiable().get(0)).getMenus().get(2).getItems().get(2);
        menuItemExistingTaskEditorHidden.fire();
    }

    @FXML
    private void updateCurrentTaskWithThisTaskAction(ActionEvent event) {

        TaskInterface chosenTask = listViewOfTasksInFolder.getSelectionModel().selectedItemProperty().getValue();
        TaskDesign taskEditor = SquidPersistentState.getExistingPersistentState().getTaskDesign();

        if (squidProject.getTask().getTaskType().equals(chosenTask.getTaskType())) {
            // check the mass count
            boolean valid = (squidProject.getTask().getSquidSpeciesModelList().size()
                    == (chosenTask.getNominalMasses().size()));
            if (valid) {
                chosenTask.updateTaskDesignFromTask(taskEditor, true);
                squidProject.createNewTask();
                squidProject.getTask().updateTaskFromTaskDesign(taskEditor, false);

                MenuItem menuItemTaskManager = ((MenuBar) SquidUI.primaryStage.getScene()
                        .getRoot().getChildrenUnmodifiable().get(0)).getMenus().get(2).getItems().get(0);
                menuItemTaskManager.fire();

            } else {
                SquidMessageDialog.showInfoDialog(
                        "The data file has " + squidProject.getTask().getSquidSpeciesModelList().size()
                        + " masses, but this task has "
                        + chosenTask.getNominalMasses().size()
                        + ".",
                        primaryStageWindow);
            }
        } else {
            SquidMessageDialog.showInfoDialog(
                    "The Project is type  " + squidProject.getTask().getTaskType()
                    + ", but this task is type "
                    + chosenTask.getTaskType()
                    + ".",
                    primaryStageWindow);
        }
    }

    @FXML
    private void saveThisTaskAsXMLFileAction(ActionEvent event) {
        try {
            FileHandler.saveTaskFileXML(listViewOfTasksInFolder.getSelectionModel().selectedItemProperty().getValue(), SquidUI.primaryStageWindow);
            taskNameTextField.setText(listViewOfTasksInFolder.getSelectionModel().selectedItemProperty().getValue().getName());
        } catch (IOException iOException) {
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
