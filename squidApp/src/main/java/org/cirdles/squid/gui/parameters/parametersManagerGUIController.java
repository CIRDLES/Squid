/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.parameters;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.control.Menu;
import javafx.scene.text.TextAlignment;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.parameters.ValueModel;
import org.cirdles.squid.parameters.matrices.AbstractMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class parametersManagerGUIController implements Initializable {

    @FXML
    private MenuItem editCopyOfCurrPhysConst;
    @FXML
    private MenuItem editNewEmpPhysConst;
    @FXML
    private MenuItem cancelEditOfPhysConst;
    @FXML
    private MenuItem saveAndRegCurrPhysConst;
    @FXML
    private ChoiceBox<String> physConstCB;
    @FXML
    private TextField physConstModelName;
    @FXML
    private TextField physConstLabName;
    @FXML
    private TextField physConstVersion;
    @FXML
    private TextField physConstDateCertified;
    @FXML
    private Label physConstIsEditableLabel;
    @FXML
    private MenuItem saveAndRegCurrRefMat;
    @FXML
    private MenuItem remCurrRefMat;
    @FXML
    private MenuItem canEditOfRefMat;
    @FXML
    private MenuItem editNewEmptyRefMat;
    @FXML
    private MenuItem editCopyOfCurrRefMat;
    @FXML
    private MenuItem editCurrRefMat;
    @FXML
    private ChoiceBox<String> refMatCB;
    @FXML
    private TextField refMatModelName;
    @FXML
    private TextField refMatLabName;
    @FXML
    private TextField refMatVersion;
    @FXML
    private TextField refMatDateCertified;
    @FXML
    private TextField labNameTextField;
    @FXML
    private TextArea physConstReferencesArea;
    @FXML
    private TextArea physConstCommentsArea;
    @FXML
    private TextArea refMatReferencesArea;
    @FXML
    private TextArea refMatCommentsArea;
    @FXML
    private Label refMatIsEditableLabel;
    @FXML
    private TableView<DataModel> physConstDataTable;
    @FXML
    private TableView<RefMatDataModel> refMatDataTable;
    @FXML
    private TextArea molarMassesTextArea;
    @FXML
    private AnchorPane referencesPane;
    @FXML
    private TableView<DataModel> refMatConcentrationsTable;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> physConstCorrTable;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> physConstCovTable;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> refMatCorrTable;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> refMatCovTable;
    @FXML
    private TabPane rootTabPane;
    @FXML
    private Menu physConstFileMenu;
    @FXML
    private Menu refMatFileMenu;
    @FXML
    private MenuItem editCurrPhysConst;
    @FXML
    private MenuItem remCurrPhysConst;
    @FXML
    private Button physConstDataNotationButton;
    @FXML
    private Button refMatDataNotationButton;
    @FXML
    private Button refMatConcentrationsNotationButton;

    PhysicalConstantsModel physConstModel;
    ReferenceMaterial refMatModel;
    List<PhysicalConstantsModel> physConstModels;
    List<ReferenceMaterial> refMatModels;

    List<TextField> physConstReferences;

    private boolean isEditingCurrPhysConst;
    private boolean isEditingCurrRefMat;

    private DecimalFormat physConstDataNotation;
    private DecimalFormat refMatDataNotation;
    private DecimalFormat refMatConcentrationsNotation;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        physConstDataNotation = getScientificNotationFormat();
        refMatDataNotation = getScientificNotationFormat();
        refMatConcentrationsNotation = getScientificNotationFormat();

        physConstModels = squidLabData.getPhysicalConstantsModels();
        physConstModel = physConstModels.get(0);
        setUpPhysConstCB();
        physConstEditable(false);
        setUpPhysConstMenuItems(false, false);
        isEditingCurrPhysConst = false;

        refMatModels = squidLabData.getReferenceMaterials();
        refMatModel = refMatModels.get(0);
        setUpRefMatCB();
        refMatEditable(false);
        setUpRefMatMenuItems(false, false);
        isEditingCurrRefMat = false;

        setUpLaboratoryName();
    }

    private void setUpPhysConst() {
        setUpPhysConstTextFields();
        setUpPhysConstData();
        setUpMolarMasses();
        setUpReferences();
        setUpPhysConstCovariancesAndCorrelations();
        setUpPhysConstCov();
        setUpPhysConstCorr();
        setUpPhysConstEditableLabel();
    }

    private void setUpRefMat() {
        setUpRefMatTextFields();
        setUpRefMatData();
        setUpConcentrations();
        setUpRefMatCovariancesAndCorrelations();
        setUpRefMatCov();
        setUpRefMatCorr();
        setUpRefMatEditableLabel();
    }

    private void setUpPhysConstCovariancesAndCorrelations() {
        physConstModel.initializeCorrelations();
        physConstModel.generateCovariancesFromCorrelations();
    }

    private void setUpRefMatCovariancesAndCorrelations() {
        refMatModel.initializeCorrelations();
        refMatModel.generateCovariancesFromCorrelations();
    }

    private void setUpPhysConstCB() {
        setUpPhysConstCBItems();
        physConstCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setPhysConstModel(nv.intValue());
            }
        });
        physConstCB.getSelectionModel().selectFirst();
    }

    private void setUpPhysConstCBItems() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (PhysicalConstantsModel mod : physConstModels) {
            cbList.add(getModVersionName(mod));
        }
        physConstCB.setItems(cbList);
    }

    private void setPhysConstModel(int num) {
        if (num > -1 && num < physConstModels.size()) {
            physConstModel = physConstModels.get(num);
            setUpPhysConst();
            setUpPhysConstMenuItems(false, physConstModel.isEditable());
            physConstEditable(false);
        }
    }

    private void setUpRefMatCB() {
        setUpRefMatCBItems();
        refMatCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setRefMatModel(nv.intValue());
            }
        });
        refMatCB.getSelectionModel().selectFirst();
    }

    private void setUpRefMatCBItems() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (ReferenceMaterial mod : refMatModels) {
            cbList.add(getModVersionName(mod));
        }
        refMatCB.setItems(cbList);
    }

    private void setRefMatModel(int num) {
        if (num > -1 && num < refMatModels.size()) {
            refMatModel = refMatModels.get(num);
            setUpRefMat();
            setUpRefMatMenuItems(false, refMatModel.isEditable());
            refMatEditable(false);
        }
    }

    private void setUpPhysConstCov() {
        initializeTableWithObList(physConstCovTable,
                getObListFromMatrix(physConstModel.getCovModel()));
    }

    private void setUpPhysConstCorr() {
        initializeTableWithObList(physConstCorrTable,
                getObListFromMatrix(physConstModel.getCorrModel()));
    }

    private void setUpRefMatCov() {
        initializeTableWithObList(refMatCovTable,
                getObListFromMatrix(refMatModel.getCovModel()));
    }

    private void setUpRefMatCorr() {
        initializeTableWithObList(refMatCorrTable,
                getObListFromMatrix(refMatModel.getCorrModel()));
    }

    private static ObservableList<ObservableList<SimpleStringProperty>> getObListFromMatrix(AbstractMatrixModel matrix) {
        ObservableList<ObservableList<SimpleStringProperty>> obList = FXCollections.observableArrayList();
        if (matrix != null && matrix.getMatrix() != null) {
            Iterator<String> colIterator = matrix.getRows().values().iterator();
            ObservableList<SimpleStringProperty> colList = FXCollections.observableArrayList();
            colList.add(new SimpleStringProperty("names ↓→"));
            while (colIterator.hasNext()) {
                colList.add(new SimpleStringProperty(colIterator.next()));
            }
            obList.add(colList);

            double[][] matrixArray = matrix.getMatrix().getArray();
            Iterator<Entry<Integer, String>> rowIterator = matrix.getRows().entrySet().iterator();
            for (int i = 0; i < matrixArray.length; i++) {
                ObservableList<SimpleStringProperty> row = FXCollections.observableArrayList();
                row.add(new SimpleStringProperty(rowIterator.next().getValue()));
                for (int j = 0; j < matrixArray[0].length; j++) {
                    row.add(new SimpleStringProperty(Double.toString(matrixArray[i][j])));
                }
                obList.add(row);
            }
        }
        return obList;
    }

    private void initializeTableWithObList(TableView<ObservableList<SimpleStringProperty>> table,
            ObservableList<ObservableList<SimpleStringProperty>> obList) {
        if (obList.size() > 0) {
            ObservableList<SimpleStringProperty> cols = obList.remove(0);
            table.getColumns().clear();
            for (int i = 0; i < cols.size(); i++) {
                TableColumn<ObservableList<SimpleStringProperty>, String> col
                        = new TableColumn<>(getRatioVisibleName(cols.get(i).get()));
                final int colNum = i;
                col.setSortable(false);
                col.setCellFactory(TextFieldTableCell.<ObservableList<SimpleStringProperty>>forTableColumn());
                col.setCellValueFactory(param
                        -> new ReadOnlyObjectWrapper<String>(getRatioVisibleName(param.getValue().get(colNum).get())));
                col.setOnEditCommit(value -> {
                    if (isNumeric(value.getNewValue()) && Double.parseDouble(value.getNewValue()) <= 1 && Double.parseDouble(value.getNewValue()) >= -1) {
                        ObservableList<ObservableList<SimpleStringProperty>> items = value.getTableView().getItems();
                        ObservableList<SimpleStringProperty> rows = items.get(value.getTablePosition().getRow());
                        rows.set(value.getTablePosition().getColumn(), new SimpleStringProperty(value.getNewValue()));
                    } else {
                        SquidMessageDialog.showWarningDialog("Value Out of Range: Only values"
                                + " in the range of [-1, 1] are allowed.", primaryStageWindow);
                        table.refresh();
                    }
                });
                table.getColumns().add(col);
            }
            table.setItems(obList);
            table.refresh();
        }
    }

    @FXML
    private void physConstDataNotationOnAction(ActionEvent event) {
        if (physConstDataNotation.equals(getScientificNotationFormat())) {
            physConstDataNotation = getStandardNotationFormat();
            physConstDataNotationButton.setText("Standard Notation");
        } else {
            physConstDataNotation = getScientificNotationFormat();
            physConstDataNotationButton.setText("Scientific Notation");
        }
        ObservableList<DataModel> models = physConstDataTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(physConstDataNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(physConstDataNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(physConstDataNotation.format(bigDec));
        }
        physConstDataTable.refresh();
    }

    @FXML
    private void refMatDataNotationOnAction(ActionEvent event) {
        if (refMatDataNotation.equals(getScientificNotationFormat())) {
            refMatDataNotation = getStandardNotationFormat();
            refMatDataNotationButton.setText("Standard Notation");
        } else {
            refMatDataNotation = getScientificNotationFormat();
            refMatDataNotationButton.setText("Scientific Notation");
        }
        ObservableList<RefMatDataModel> models = refMatDataTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(refMatDataNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(refMatDataNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(refMatDataNotation.format(bigDec));
        }
        refMatDataTable.refresh();
    }

    @FXML
    private void refMatConcentrationsNotationOnAction(ActionEvent event) {
        if (refMatConcentrationsNotation.equals(getScientificNotationFormat())) {
            refMatConcentrationsNotation = getStandardNotationFormat();
            refMatConcentrationsNotationButton.setText("Standard Notation");
        } else {
            refMatConcentrationsNotation = getScientificNotationFormat();
            refMatConcentrationsNotationButton.setText("Scientific Notation");
        }
        ObservableList<DataModel> models = refMatConcentrationsTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(refMatConcentrationsNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(refMatConcentrationsNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(refMatConcentrationsNotation.format(bigDec));
        }
        refMatConcentrationsTable.refresh();
    }

    private void setUpPhysConstData() {
        physConstDataTable.getColumns().clear();
        List<TableColumn<DataModel, String>> columns = getDataModelColumns();
        for (TableColumn<DataModel, String> col : columns) {
            physConstDataTable.getColumns().add(col);
        }
        physConstDataTable.setItems(getDataModelObList(physConstModel.getValues(), physConstDataNotation));
        physConstDataTable.refresh();
    }

    private void setUpRefMatData() {
        refMatDataTable.getColumns().clear();

        TableColumn<RefMatDataModel, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("name"));
        nameCol.setSortable(false);
        nameCol.setCellFactory(TextFieldTableCell.<RefMatDataModel>forTableColumn());
        refMatDataTable.getColumns().add(nameCol);

        TableColumn<RefMatDataModel, String> valCol = new TableColumn<>("value");
        valCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("value"));
        valCol.setSortable(false);
        valCol.setCellFactory(TextFieldTableCell.<RefMatDataModel>forTableColumn());
        valCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<RefMatDataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                mod.setValue(value.getNewValue());
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        refMatDataTable.getColumns().add(valCol);

        TableColumn<RefMatDataModel, String> absCol = new TableColumn<>("1σ ABS");
        absCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("oneSigmaABS"));
        absCol.setSortable(false);
        absCol.setCellFactory(TextFieldTableCell.<RefMatDataModel>forTableColumn());
        absCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<RefMatDataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                mod.setOneSigmaABS(value.getNewValue());
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        refMatDataTable.getColumns().add(absCol);

        TableColumn<RefMatDataModel, String> pctCol = new TableColumn<>("1σ PCT");
        pctCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("oneSigmaPCT"));
        pctCol.setSortable(false);
        pctCol.setCellFactory(TextFieldTableCell.<RefMatDataModel>forTableColumn());
        pctCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<RefMatDataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                mod.setOneSigmaPCT(value.getNewValue());
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        refMatDataTable.getColumns().add(pctCol);


        TableColumn<RefMatDataModel, ChoiceBox> measuredCol = new TableColumn<>("measured");
        measuredCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, ChoiceBox>("isMeasured"));
        measuredCol.setSortable(false);
        refMatDataTable.getColumns().add(measuredCol);

        final ObservableList<RefMatDataModel> obList = FXCollections.observableArrayList();
        ValueModel[] values = refMatModel.getValues();
        for (int i = 0; i < values.length; i++) {
            ValueModel valMod = values[i];
            Boolean isMeasured = refMatModel.getDataMeasured()[i];
            String value = refMatDataNotation.format(valMod.getValue());
            String oneSigmaABS = refMatDataNotation.format(valMod.getOneSigmaABS());
            String oneSigmaPCT = refMatDataNotation.format(valMod.getOneSigmaPCT());
            RefMatDataModel mod = new RefMatDataModel(getRatioVisibleName(valMod.getName()), value,
                    oneSigmaABS, oneSigmaPCT, isMeasured);
            obList.add(mod);
        }
        refMatDataTable.setItems(obList);
        refMatDataTable.refresh();
    }

    private void setUpConcentrations() {
        refMatConcentrationsTable.getColumns().clear();
        List<TableColumn<DataModel, String>> columns = getDataModelColumns();
        for (TableColumn<DataModel, String> col : columns) {
            refMatConcentrationsTable.getColumns().add(col);
        }
        refMatConcentrationsTable.setItems(getDataModelObList(refMatModel.getConcentrations(), refMatConcentrationsNotation));
        refMatConcentrationsTable.refresh();
    }

    private static List<TableColumn<DataModel, String>> getDataModelColumns() {
        List<TableColumn<DataModel, String>> columns = new ArrayList<>();

        TableColumn<DataModel, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("name"));
        nameCol.setSortable(false);
        nameCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        columns.add(nameCol);

        TableColumn<DataModel, String> valCol = new TableColumn<>("value");
        valCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("value"));
        valCol.setSortable(false);
        valCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        valCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<DataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                mod.setValue(value.getNewValue());
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        columns.add(valCol);

        TableColumn<DataModel, String> absCol = new TableColumn<>("1σ ABS");
        absCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("oneSigmaABS"));
        absCol.setSortable(false);
        absCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        absCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<DataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                mod.setOneSigmaABS(value.getNewValue());
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        columns.add(absCol);

        TableColumn<DataModel, String> pctCol = new TableColumn<>("1σ PCT");
        pctCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("oneSigmaPCT"));
        pctCol.setSortable(false);
        pctCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        pctCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<DataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                mod.setOneSigmaPCT(value.getNewValue());
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        columns.add(pctCol);

        return columns;
    }

    private ObservableList<DataModel> getDataModelObList(ValueModel[] values, DecimalFormat numberFormat) {
        final ObservableList<DataModel> obList = FXCollections.observableArrayList();
        for (int i = 0; i < values.length; i++) {
            ValueModel valMod = values[i];
            String value = numberFormat.format(valMod.getValue());
            String oneSigmaABS = numberFormat.format(valMod.getOneSigmaABS());
            String oneSigmaPCT = numberFormat.format(valMod.getOneSigmaPCT());
            DataModel mod = new DataModel(getRatioVisibleName(valMod.getName()), value,
                    oneSigmaABS, oneSigmaPCT);
            obList.add(mod);
        }
        return obList;
    }

    private void setUpMolarMasses() {
        molarMassesTextArea.clear();
        Iterator<Entry<String, BigDecimal>> molarMassesIterator
                = physConstModel.getMolarMasses().entrySet().iterator();
        Entry<String, BigDecimal> curr;
        if (molarMassesIterator.hasNext()) {
            curr = molarMassesIterator.next();
            molarMassesTextArea.appendText(curr.getKey() + " = " + curr.getValue());
        }
        while (molarMassesIterator.hasNext()) {
            curr = molarMassesIterator.next();
            molarMassesTextArea.appendText("\n" + curr.getKey() + " = "
                    + curr.getValue());
        }
    }

    private void setUpReferences() {
        referencesPane.getChildren().clear();
        ValueModel[] models = physConstModel.getValues();
        physConstReferences = new ArrayList<>();
        int currHeight = 0;
        for (int i = 0; i < models.length; i++) {
            ValueModel mod = models[i];

            Label lab = new Label(getRatioVisibleName(mod.getName()) + ":");
            referencesPane.getChildren().add(lab);
            lab.setLayoutY(currHeight + 5);
            lab.setLayoutX(10);
            lab.setTextAlignment(TextAlignment.RIGHT);
            lab.setPrefWidth(Region.USE_COMPUTED_SIZE);

            TextField text = new TextField(mod.getReference());
            referencesPane.getChildren().add(text);
            text.setLayoutY(currHeight);
            text.setLayoutX(lab.getLayoutX() + 100);
            text.setPrefWidth(Region.USE_COMPUTED_SIZE);
            text.setOnKeyReleased(value -> {
                mod.setReference(text.getText());
            });

            physConstReferences.add(text);
            currHeight += 40;
        }
    }

    private void setUpPhysConstEditableLabel() {
        if (physConstModel.isEditable()) {
            physConstIsEditableLabel.setText("editable");
        } else {
            physConstIsEditableLabel.setText("not editable");
        }
    }

    private void setUpRefMatEditableLabel() {
        if (refMatModel.isEditable()) {
            refMatIsEditableLabel.setText("editable");
        } else {
            refMatIsEditableLabel.setText("not editable");
        }
    }

    private void setUpPhysConstTextFields() {
        physConstModelName.setText(physConstModel.getModelName());
        physConstLabName.setText(physConstModel.getLabName());
        physConstVersion.setText(physConstModel.getVersion());
        physConstDateCertified.setText(physConstModel.getDateCertified());
    }

    private void setUpRefMatTextFields() {
        refMatModelName.setText(refMatModel.getModelName());
        refMatLabName.setText(refMatModel.getLabName());
        refMatVersion.setText(refMatModel.getVersion());
        refMatDateCertified.setText(refMatModel.getDateCertified());
    }

    private void setUpLaboratoryName() {
        labNameTextField.setText(squidLabData.getLaboratoryName());
        labNameTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    if (!squidLabData.getLaboratoryName().equals(labNameTextField.getText())) {
                        squidLabData.setLaboratoryName(labNameTextField.getText());
                        squidLabData.storeState();
                    }
                }
            }
        });
    }

    public static String getModVersionName(ParametersModel mod) {
        return mod.getModelName() + " v." + mod.getVersion();
    }

    private String getRatioVisibleName(String ratio) {
        String retVal = ratio.replaceAll("lambda", "λ");
        retVal = retVal.replaceAll("r206_207r", "206-Pb/207-Pb");
        retVal = retVal.replaceAll("r206_208r", "206-Pb/208-Pb");
        retVal = retVal.replaceAll("r206_238r", "206-Pb/238-U");
        retVal = retVal.replaceAll("r208_232r", "208-Pb/232-Th");
        retVal = retVal.replaceAll("r238_235s", "238-U/235-U");

        return retVal;
    }

    private String getRatioHiddenName(String ratio) {
        String retVal = ratio.replaceAll("λ", "lambda");
        retVal = retVal.replaceAll("206-Pb/207-Pb", "r206_207r");
        retVal = retVal.replaceAll("206-Pb/208-Pb", "r206_208r");
        retVal = retVal.replaceAll("206-Pb/238-U", "r206_238r");
        retVal = retVal.replaceAll("208-Pb/232-Th", "r208_232r");
        retVal = retVal.replaceAll("238-U/235-U", "r238_235s");

        return retVal;
    }

    @FXML
    private void physConstImpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectPhysicalConstantsXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            PhysicalConstantsModel importedMod = (PhysicalConstantsModel) physConstModel.readXMLObject(file.getAbsolutePath(), false);
            physConstModels.add(importedMod);
            physConstCB.getItems().add(getModVersionName(importedMod));
            physConstCB.getSelectionModel().selectLast();
            physConstModel = importedMod;
            setUpPhysConst();
            squidLabData.storeState();
        }
    }

    @FXML
    private void physConstExpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSavePhysicalConstantsXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            physConstModel.serializeXMLObject(file.getAbsolutePath());
        }
    }

    @FXML
    private void refMatExpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSaveReferenceMaterialXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            refMatModel.serializeXMLObject(file.getAbsolutePath());
        }
    }

    @FXML
    private void refMatImpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectReferenceMaterialXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            ReferenceMaterial importedMod = (ReferenceMaterial) refMatModel.readXMLObject(file.getAbsolutePath(), false);
            refMatModels.add(importedMod);
            refMatCB.getItems().add(getModVersionName(importedMod));
            refMatCB.getSelectionModel().selectLast();
            refMatModel = importedMod;
            squidLabData.storeState();
        }
    }

    private void physConstEditable(boolean isEditable) {
        physConstModelName.setEditable(isEditable);
        physConstLabName.setEditable(isEditable);
        physConstVersion.setEditable(isEditable);
        physConstDateCertified.setEditable(isEditable);

        physConstDataTable.setEditable(isEditable);
        physConstDataTable.getColumns().get(0).setEditable(false);

        physConstCorrTable.setEditable(isEditable);
        physConstCorrTable.getColumns().get(0).setEditable(false);

        physConstCovTable.setEditable(false);

        molarMassesTextArea.setEditable(isEditable);

        for (int i = 0; i < physConstReferences.size(); i++) {
            physConstReferences.get(i).setEditable(isEditable);
        }

        physConstCommentsArea.setEditable(isEditable);
        physConstReferencesArea.setEditable(isEditable);
    }

    private void refMatEditable(boolean isEditable) {
        refMatModelName.setEditable(isEditable);
        refMatLabName.setEditable(isEditable);
        refMatVersion.setEditable(isEditable);
        refMatDateCertified.setEditable(isEditable);

        refMatDataTable.setEditable(isEditable);
        refMatDataTable.getColumns().get(0).setEditable(false);
        ObservableList<RefMatDataModel> refMatData = refMatDataTable.getItems();
        for (RefMatDataModel mod : refMatData) {
            if (!isEditable) {
                mod.getIsMeasured().setDisable(true);
                mod.getIsMeasured().setStyle("-fx-opacity: 1");
            } else {
                mod.getIsMeasured().setDisable(false);
            }
        }

        refMatConcentrationsTable.setEditable(isEditable);
        refMatConcentrationsTable.getColumns().get(0).setEditable(false);

        refMatCorrTable.setEditable(isEditable);
        refMatCorrTable.getColumns().get(0).setEditable(false);

        refMatCovTable.setEditable(false);

        refMatCommentsArea.setEditable(isEditable);
        refMatReferencesArea.setEditable(isEditable);
    }

    private void setUpRefMatMenuItems(boolean isEditing, boolean isEditable) {
        refMatFileMenu.setDisable(isEditing);
        saveAndRegCurrRefMat.setDisable(!isEditing);
        remCurrRefMat.setDisable(!isEditable || isEditing);
        canEditOfRefMat.setDisable(!isEditing);
        editNewEmptyRefMat.setDisable(isEditing);
        editCopyOfCurrRefMat.setDisable(isEditing);
        editCurrRefMat.setDisable(!isEditable || isEditing);
        refMatCB.setDisable(isEditing);
    }

    private void setUpPhysConstMenuItems(boolean isEditing, boolean isEditable) {
        physConstFileMenu.setDisable(isEditing);
        saveAndRegCurrPhysConst.setDisable(!isEditing);
        remCurrPhysConst.setDisable(!isEditable || isEditing);
        cancelEditOfPhysConst.setDisable(!isEditing);
        editNewEmpPhysConst.setDisable(isEditing);
        editCopyOfCurrPhysConst.setDisable(isEditing);
        editCurrPhysConst.setDisable(!isEditable || isEditing);
        physConstCB.setDisable(isEditing);
    }

    @FXML
    private void physConstRemoveCurrMod(ActionEvent event) {
        physConstModels.remove(physConstModel);
        setUpPhysConstCBItems();
        physConstCB.getSelectionModel().selectFirst();
        physConstEditable(false);
        setUpPhysConstMenuItems(false, physConstModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void physConstEditCurrMod(ActionEvent event) {
        physConstEditable(true);
        setUpPhysConstMenuItems(true, physConstModel.isEditable());
        isEditingCurrPhysConst = true;
    }

    @FXML
    private void physConstEditCopy(ActionEvent event) {
        physConstModel = physConstModel.clone();
        setUpPhysConst();
        physConstEditable(true);
        setUpPhysConstMenuItems(true, true);
    }

    @FXML
    private void physConstEditEmptyMod(ActionEvent event) {
        physConstModel = new PhysicalConstantsModel();
        setUpPhysConst();
        physConstEditable(true);
        setUpPhysConstMenuItems(true, true);
    }

    @FXML
    private void physConstCancelEdit(ActionEvent event) {
        if (!isEditingCurrPhysConst) {
            physConstCB.getSelectionModel().selectFirst();
        } else {
            isEditingCurrPhysConst = false;
            physConstCB.getSelectionModel().select(getModVersionName(physConstModel));
        }
        physConstEditable(false);
        setUpPhysConstMenuItems(false, physConstModel.isEditable());
    }

    @FXML
    private void physConstSaveAndRegisterEdit(ActionEvent event) {
        physConstModel.setIsEditable(true);
        physConstModel.setModelName(physConstModelName.getText());
        physConstModel.setVersion(physConstVersion.getText());
        physConstModel.setDateCertified(physConstDateCertified.getText());
        physConstModel.setLabName(physConstLabName.getText());

        ObservableList<DataModel> dataModels = physConstDataTable.getItems();
        ValueModel[] values = new ValueModel[dataModels.size()];
        for (int i = 0; i < values.length; i++) {
            DataModel mod = dataModels.get(i);
            ValueModel currVal = new ValueModel();

            currVal.setName(getRatioHiddenName(mod.getName()));

            String currBigDec = mod.getValue();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setValue(BigDecimal.ZERO);
            } else {
                currVal.setValue(new BigDecimal(currBigDec));
            }

            currBigDec = mod.getOneSigmaABS();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setOneSigma(BigDecimal.ZERO);
            } else {
                currVal.setOneSigma(new BigDecimal(currBigDec));
            }

            currVal.setReference(physConstReferences.get(i).getText());
            currVal.setUncertaintyType("ABS");
            values[i] = currVal;
        }
        physConstModel.setValues(values);

        Map<String, BigDecimal> masses = new HashMap<>();
        try {
            String[] lines = molarMassesTextArea.getText().split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (!lines[i].trim().equals("")) {
                    String[] currLine = lines[i].split(" = ");
                    String key = currLine[0];
                    String bigDec = currLine[1];
                    if (Double.parseDouble(bigDec) == 0) {
                        masses.put(key, BigDecimal.ZERO);
                    } else {
                        masses.put(key, new BigDecimal(bigDec));
                    }
                }
            }
            physConstModel.setMolarMasses(masses);
        } catch (Exception e) {
            String message = "incorrect molar masses format";
            SquidMessageDialog.showWarningDialog(message, primaryStageWindow);
        }

        physConstModel.setReferences(physConstReferencesArea.getText());
        physConstModel.setComments(physConstCommentsArea.getText());
        physConstModel.setRhos(getRhosFromTable(physConstCorrTable));

        if (!isEditingCurrPhysConst) {
            physConstModels.add(physConstModel);
        } else {
            isEditingCurrPhysConst = false;
        }
        setUpPhysConstCBItems();
        physConstCB.getSelectionModel().select(getModVersionName(physConstModel));
        physConstEditable(false);
        setUpPhysConstMenuItems(false, physConstModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void refMatSaveAndRegisterEdit(ActionEvent event) {
        refMatModel.setIsEditable(true);
        refMatModel.setModelName(refMatModelName.getText());
        refMatModel.setLabName(refMatLabName.getText());
        refMatModel.setVersion(refMatVersion.getText());
        refMatModel.setDateCertified(refMatDateCertified.getText());

        ObservableList<RefMatDataModel> dataModels = refMatDataTable.getItems();
        ValueModel[] values = new ValueModel[dataModels.size()];
        boolean[] isMeasures = new boolean[dataModels.size()];
        for (int i = 0; i < values.length; i++) {
            RefMatDataModel mod = dataModels.get(i);
            ValueModel currVal = new ValueModel();

            currVal.setName(getRatioHiddenName(mod.getName()));

            String currBigDec = mod.getValue();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setValue(BigDecimal.ZERO);
            } else {
                currVal.setValue(new BigDecimal(currBigDec));
            }

            currBigDec = mod.getOneSigmaABS();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setOneSigma(BigDecimal.ZERO);
            } else {
                currVal.setOneSigma(new BigDecimal(currBigDec));
            }

            isMeasures[i] = mod.getIsMeasured().isSelected();

            currVal.setUncertaintyType("ABS");
            values[i] = currVal;
        }
        refMatModel.setValues(values);
        refMatModel.setDataMeasured(isMeasures);

        refMatModel.setRhos(getRhosFromTable(refMatCorrTable));

        ObservableList<DataModel> concentrationsData = refMatConcentrationsTable.getItems();
        ValueModel[] concentrations = new ValueModel[concentrationsData.size()];
        for (int i = 0; i < concentrations.length; i++) {
            DataModel mod = concentrationsData.get(i);
            ValueModel currVal = new ValueModel();

            currVal.setName(mod.getName());

            String currBigDec = mod.getValue();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setValue(BigDecimal.ZERO);
            } else {
                currVal.setValue(new BigDecimal(currBigDec));
            }

            currBigDec = mod.getOneSigmaABS();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setOneSigma(BigDecimal.ZERO);
            } else {
                currVal.setOneSigma(new BigDecimal(currBigDec));
            }

            currVal.setReference(physConstReferences.get(i).getText());
            currVal.setUncertaintyType("ABS");
            concentrations[i] = currVal;
        }
        refMatModel.setConcentrations(concentrations);

        refMatModel.setReferences(refMatReferencesArea.getText());
        refMatModel.setComments(refMatCommentsArea.getText());

        if (!isEditingCurrRefMat) {
            refMatModels.add(refMatModel);
        } else {
            isEditingCurrRefMat = false;
        }
        setUpRefMatCBItems();
        refMatCB.getSelectionModel().select(getModVersionName(refMatModel));
        refMatEditable(false);
        setUpRefMatMenuItems(false, refMatModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void refMatRemoveCurrMod(ActionEvent event) {
        refMatModels.remove(refMatModel);
        setUpRefMatCBItems();
        refMatCB.getSelectionModel().selectFirst();
        refMatEditable(false);
        setUpRefMatMenuItems(false, refMatModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void refMatCancelEdit(ActionEvent event) {
        refMatEditable(false);
        refMatCB.getSelectionModel().selectFirst();
        setUpRefMatMenuItems(false, refMatModel.isEditable());
        isEditingCurrRefMat = false;
    }

    @FXML
    private void refMateEditEmptyMod(ActionEvent event) {
        refMatModel = new ReferenceMaterial();
        setUpRefMat();
        refMatEditable(true);
        setUpRefMatMenuItems(true, true);
    }

    @FXML
    private void refMatEditCopy(ActionEvent event) {
        refMatModel = refMatModel.clone();
        setUpRefMat();
        refMatEditable(true);
        setUpRefMatMenuItems(true, true);
    }

    @FXML
    private void refMatEditCurrMod(ActionEvent event) {
        refMatEditable(true);
        setUpRefMatMenuItems(true, true);
        isEditingCurrRefMat = true;
    }

    private Map<String, BigDecimal> getRhosFromTable(TableView<ObservableList<SimpleStringProperty>> table) {
        Map<String, BigDecimal> rhos = new HashMap<>();
        table.setEditable(false);

        if (table.getColumns().size() > 0) {
            ObservableList<TableColumn<ObservableList<SimpleStringProperty>, ?>> colHeaders = table.getColumns();
            ObservableList<ObservableList<SimpleStringProperty>> items = table.getItems();
            List<String> headerNames = new ArrayList<>();
            for (int i = 0; i < colHeaders.size(); i++) {
                headerNames.add(colHeaders.get(i).getText());
            }

            for (int i = 0; i < items.size(); i++) {
                ObservableList<SimpleStringProperty> currItem = items.get(i);
                for (int j = 1; j < currItem.size(); j++) {
                    if (i + 1 != j) {
                        String val = currItem.get(j).get();
                        if (Double.parseDouble(val) != 0) {
                            String colHeader = getRatioHiddenName(headerNames.get(j));
                            String rowHeader = getRatioHiddenName(currItem.get(0).get());
                            String reverseKey = "rho" + rowHeader.substring(0, 1).toUpperCase()
                                    + rowHeader.substring(1) + "__" + colHeader;
                            if (!rhos.containsKey(reverseKey)) {
                                String key = "rho" + colHeader.substring(0, 1).toUpperCase()
                                        + colHeader.substring(1) + "__" + rowHeader;
                                rhos.put(key, new BigDecimal(val));
                            }
                        }
                    }
                }
            }
        }

        return rhos;
    }

    public static String trimTrailingZeroes(String s) {
        String retVal;
        if (s.contains("E")) {
            retVal = s;
        } else {
            retVal = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
        }
        return retVal;
    }

    public static boolean isNumeric(String s) {
        boolean retVal;
        try {
            Double.parseDouble(s);
            retVal = true;
        } catch (Exception e) {
            retVal = false;
        }
        return retVal;
    }

    public static DecimalFormat getScientificNotationFormat() {
        return new DecimalFormat("0.0#######################E0#############");
    }

    public static DecimalFormat getStandardNotationFormat() {
        return new DecimalFormat("########################0.0################################");
    }

    public static void main(String[] args) {
        System.out.print(trimTrailingZeroes("1.5E-10000"));
    }

    public class DataModel {

        private SimpleStringProperty name;
        private SimpleStringProperty value;
        private SimpleStringProperty oneSigmaABS;
        private SimpleStringProperty oneSigmaPCT;

        public DataModel(String name, String value,
                String oneSigmaABS, String oneSigmaPCT) {
            this.name = new SimpleStringProperty(name);
            this.value = new SimpleStringProperty(trimTrailingZeroes(value));
            this.oneSigmaABS = new SimpleStringProperty(trimTrailingZeroes(oneSigmaABS));
            this.oneSigmaPCT = new SimpleStringProperty(trimTrailingZeroes(oneSigmaPCT));
        }

        public String getName() {
            return name.get();
        }

        public String getValue() {
            return value.get();
        }

        public String getOneSigmaABS() {
            return oneSigmaABS.get();
        }

        public String getOneSigmaPCT() {
            return oneSigmaPCT.get();
        }

        public void setName(String name) {
            this.name = new SimpleStringProperty(name);
        }

        public void setValue(String value) {
            this.value = new SimpleStringProperty(value);
        }

        public void setOneSigmaABS(String oneSigmaABS) {
            this.oneSigmaABS = new SimpleStringProperty(oneSigmaABS);
        }

        public void setOneSigmaPCT(String oneSigmaPCT) {
            this.oneSigmaPCT = new SimpleStringProperty(oneSigmaPCT);
        }

    }

    public class RefMatDataModel extends DataModel {

        private CheckBox isMeasured;

        public RefMatDataModel(String name, String value,
                String oneSigmaABS, String oneSigmaPCT,
                boolean isMeasured) {
            super(name, value, oneSigmaABS, oneSigmaPCT);
            this.isMeasured = new CheckBox();
            this.isMeasured.setSelected(isMeasured);
        }

        public CheckBox getIsMeasured() {
            return isMeasured;
        }

        public void setIsMeasured(CheckBox isMeasured) {
            this.isMeasured = isMeasured;
        }

    }
}
