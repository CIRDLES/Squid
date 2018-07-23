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
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;
import org.cirdles.squid.parameters.util.StringComparer;

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
    private TableView<ObservableList<String>> physConstCorrTable;
    @FXML
    private Label physConstCorrLabel;
    @FXML
    private TableView<ObservableList<String>> physConstCovTable;
    @FXML
    private Label physConstCovLabel;
    @FXML
    private Label refMatCorrLabel;
    @FXML
    private TableView<ObservableList<String>> refMatCorrTable;
    @FXML
    private Label refMatCovLabel;
    @FXML
    private TableView<ObservableList<String>> refMatCovTable;
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

    PhysicalConstantsModel physConstModel;
    ReferenceMaterial refMatModel;
    List<PhysicalConstantsModel> physConstModels;
    List<ReferenceMaterial> refMatModels;

    List<TextField> physConstReferences;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        physConstModels = squidLabData.getPhysicalConstantsModels();
        physConstModel = physConstModels.get(0);
        setUpPhysConstCB();
        physConstEditable(false);
        setUpPhysConstMenuItems(false, false);

        refMatModels = squidLabData.getReferenceMaterials();
        refMatModel = refMatModels.get(0);
        setUpRefMatCB();
        refMatEditable(false);
        setUpRefMatMenuItems(false, false);

        setUpLaboratoryName();
    }

    private void setUpPhysConstCovariancesAndCorrelations() {
        physConstModel.initializeCorrelations();
        physConstModel.generateCovariancesFromCorrelations();
    }

    private void setUpRefMatCovariancesAndCorrelations() {
        refMatModel.initializeCorrelations();
        refMatModel.generateCovariancesFromCorrelations();
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

    private void setUpPhysConstCB() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (PhysicalConstantsModel mod : physConstModels) {
            cbList.add(mod.getModelName() + " v." + mod.getVersion());
        }
        physConstCB.setItems(cbList);
        physConstCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setPhysConstModel(nv.intValue());
            }
        });
        physConstCB.getSelectionModel().selectFirst();
    }

    private void setPhysConstModel(int num) {
        physConstModel = physConstModels.get(num);
        setUpPhysConst();
        setUpPhysConstMenuItems(false, physConstModel.isEditable());
    }

    private void setUpRefMatCB() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (ReferenceMaterial mod : refMatModels) {
            cbList.add(mod.getModelName() + " v." + mod.getVersion());
        }
        refMatCB.setItems(cbList);
        refMatCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setRefMatModel(nv.intValue());
            }
        });
        refMatCB.getSelectionModel().selectFirst();
    }

    private void setRefMatModel(int num) {
        refMatModel = refMatModels.get(num);
        setUpRefMat();
        setUpRefMatMenuItems(false, refMatModel.isEditable());
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

    private static ObservableList<ObservableList<String>> getObListFromMatrix(AbstractMatrixModel matrix) {
        ObservableList<ObservableList<String>> obList = FXCollections.observableArrayList();
        if (matrix != null && matrix.getMatrix() != null) {
            Iterator<String> colIterator = matrix.getRows().values().iterator();
            ObservableList<String> colList = FXCollections.observableArrayList();
            colList.add("names ↓→");
            while (colIterator.hasNext()) {
                colList.add(colIterator.next());
            }
            obList.add(colList);

            double[][] matrixArray = matrix.getMatrix().getArray();
            Iterator<Entry<Integer, String>> rowIterator = matrix.getRows().entrySet().iterator();
            for (int i = 0; i < matrixArray.length; i++) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rowIterator.next().getValue());
                for (int j = 0; j < matrixArray[0].length; j++) {
                    row.add(Double.toString(matrixArray[i][j]));
                }
                obList.add(row);
            }
        }

        return obList;
    }

    private static void initializeTableWithObList(TableView<ObservableList<String>> table,
            ObservableList<ObservableList<String>> obList) {
        if (obList.size() > 0) {
            ObservableList<String> cols = obList.remove(0);
            table.getColumns().clear();
            for (int i = 0; i < cols.size(); i++) {
                TableColumn<ObservableList<String>, String> col = new TableColumn(cols.get(i));
                final int colNum = i;
                col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(colNum)));
                col.setComparator(new StringComparer());
                col.setCellFactory(TextFieldTableCell.<ObservableList<String>>forTableColumn());
                table.getColumns().add(col);
            }
            table.setItems(obList);
            table.refresh();
        }
    }

    private void setUpPhysConstData() {
        physConstDataTable.getColumns().clear();
        List<TableColumn> columns = getDataModelColumns();
        for (TableColumn col : columns) {
            physConstDataTable.getColumns().add(col);
        }
        physConstDataTable.setItems(getDataModelObList(physConstModel.getValues()));
        physConstDataTable.refresh();
    }

    private void setUpRefMatData() {
        refMatDataTable.getColumns().clear();
        List<TableColumn> columns = getDataModelColumns();
        for (TableColumn col : columns) {
            refMatDataTable.getColumns().add(col);
        }

        TableColumn measuredCol = new TableColumn("measured");
        measuredCol.setCellValueFactory(new PropertyValueFactory("isMeasured"));
        refMatDataTable.getColumns().add(measuredCol);

        final ObservableList<RefMatDataModel> obList = FXCollections.observableArrayList();
        ValueModel[] values = refMatModel.getValues();
        for (int i = 0; i < values.length; i++) {
            ValueModel valMod = values[i];
            Boolean isMeasured = refMatModel.getDataMeasured()[i];
            RefMatDataModel mod = new RefMatDataModel(valMod.getName(), valMod.getValue(),
                    valMod.getOneSigmaABS(), valMod.getOneSigmaPCT(), isMeasured);
            obList.add(mod);
        }
        refMatDataTable.setItems(obList);
        refMatDataTable.refresh();
    }

    private void setUpConcentrations() {
        refMatConcentrationsTable.getColumns().clear();
        List<TableColumn> columns = getDataModelColumns();
        for (TableColumn col : columns) {
            refMatConcentrationsTable.getColumns().add(col);
        }
        refMatConcentrationsTable.setItems(getDataModelObList(refMatModel.getConcentrations()));
        refMatConcentrationsTable.refresh();
    }

    private static List<TableColumn> getDataModelColumns() {
        List<TableColumn> columns = new ArrayList<>();

        TableColumn nameCol = new TableColumn("name");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        nameCol.setComparator(new StringComparer());
        nameCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        columns.add(nameCol);

        TableColumn valCol = new TableColumn("value");
        valCol.setCellValueFactory(new PropertyValueFactory("value"));
        valCol.setComparator(new StringComparer());
        valCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        columns.add(valCol);

        TableColumn absCol = new TableColumn("1σ ABS");
        absCol.setCellValueFactory(new PropertyValueFactory("oneSigmaABS"));
        absCol.setComparator(new StringComparer());
        absCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        columns.add(absCol);

        TableColumn pctCol = new TableColumn("1σ PCT");
        pctCol.setCellValueFactory(new PropertyValueFactory("oneSigmaPCT"));
        pctCol.setComparator(new StringComparer());
        pctCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        columns.add(pctCol);

        return columns;
    }

    private ObservableList<DataModel> getDataModelObList(ValueModel[] values) {
        final ObservableList<DataModel> obList = FXCollections.observableArrayList();
        for (int i = 0; i < values.length; i++) {
            ValueModel valMod = values[i];
            DataModel mod = new DataModel(valMod.getName(), valMod.getValue(),
                    valMod.getOneSigmaABS(), valMod.getOneSigmaPCT());
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

            Label lab = new Label(mod.getName() + ":");
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
            physConstCB.getItems().add(importedMod.getModelName() + " v." + importedMod.getVersion());
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
            refMatCB.getItems().add(importedMod.getModelName() + " v." + importedMod.getVersion());
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

        physConstCovTable.setEditable(isEditable);
        physConstCovTable.getColumns().get(0).setEditable(false);

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
                mod.getIsMeasured().setStyle("-fx-opacity: 1");
                mod.getIsMeasured().setDisable(true);
            } else {
                mod.getIsMeasured().setDisable(false);
            }

        }

        refMatConcentrationsTable.setEditable(isEditable);
        refMatConcentrationsTable.getColumns().get(0).setEditable(false);

        refMatCorrTable.setEditable(isEditable);
        refMatCorrTable.getColumns().get(0).setEditable(false);

        refMatCovTable.setEditable(isEditable);
        refMatCovTable.getColumns().get(0).setEditable(false);

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
        physConstCB.getItems().remove(physConstModel.getModelName() + " v." + physConstModel.getVersion());
        physConstCB.getSelectionModel().selectFirst();
        physConstEditable(false);
        setUpPhysConstMenuItems(false, physConstModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void physConstEditCurrMod(ActionEvent event) {
        physConstEditable(true);
        setUpPhysConstMenuItems(true, physConstModel.isEditable());
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
        physConstCB.getSelectionModel().selectFirst();
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
            values[i] = currVal;
        }
        physConstModel.setValues(values);

        Map<String, BigDecimal> masses = physConstModel.getMolarMasses();
        masses.clear();
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
        } catch (Exception e) {
            String message = "incorrect molar masses format: " + e.getMessage();
            SquidMessageDialog.showWarningDialog(message, primaryStageWindow);
        }

        physConstModel.setReferences(physConstReferencesArea.getText());
        physConstModel.setComments(physConstCommentsArea.getText());
        physConstModel.setRhos(getRhosFromTable(physConstCorrTable));

        physConstModels.add(physConstModel);
        physConstCB.getItems().add(physConstModel.getModelName() + " v." + physConstModel.getVersion());
        physConstCB.getSelectionModel().selectLast();
        physConstEditable(false);
        setUpPhysConst();
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

            isMeasures[i] = mod.getIsMeasured().isSelected();

            currVal.setUncertaintyType("ABS");
            values[i] = currVal;
        }
        refMatModel.setValues(values);
        refMatModel.setDataMeasured(isMeasures);

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

        refMatModels.add(refMatModel);
        refMatCB.getItems().add(refMatModel.getModelName() + "v." + refMatModel.getVersion());
        refMatCB.getSelectionModel().selectLast();
        refMatEditable(false);
        setUpRefMatMenuItems(false, refMatModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void refMatRemoveCurrMod(ActionEvent event) {
        refMatModels.remove(refMatModel);
        refMatCB.getItems().remove(refMatModel.getModelName() + " v." + refMatModel.getVersion());
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
    }

    private Map<String, BigDecimal> getRhosFromTable(TableView<ObservableList<String>> table) {
        Map<String, BigDecimal> rhos = new HashMap<>();

        if (table.getColumns().size() > 0) {
            ObservableList<TableColumn<ObservableList<String>, ?>> colHeaders = table.getColumns();
            ObservableList<ObservableList<String>> items = table.getItems();
            ObservableList<String> headerNames = FXCollections.observableArrayList();
            for (int i = 0; i < colHeaders.size(); i++) {
                headerNames.add(colHeaders.get(i).getText());
            }

            for (int i = 0; i < items.size(); i++) {
                ObservableList<String> currItem = items.get(i);
                for (int j = 1; j < currItem.size(); j++) {
                    if (i + 1 > j) {
                        String val = currItem.get(j);
                        if (Double.parseDouble(val) != 0) {
                            String key = "rhoR" + headerNames.get(j).substring(1)
                                    + "__" + currItem.get(0);
                            rhos.put(key, new BigDecimal(val));
                        }
                    }
                }
            }
        }

        return rhos;
    }

    public class DataModel {

        private SimpleStringProperty name;
        private SimpleStringProperty value;
        private SimpleStringProperty oneSigmaABS;
        private SimpleStringProperty oneSigmaPCT;

        public DataModel(String name, BigDecimal value,
                BigDecimal oneSigmaABS, BigDecimal oneSigmaPCT) {
            this.name = new SimpleStringProperty(name);
            this.value = new SimpleStringProperty(value.toPlainString());
            this.oneSigmaABS = new SimpleStringProperty(oneSigmaABS.toPlainString());
            this.oneSigmaPCT = new SimpleStringProperty(oneSigmaPCT.toPlainString());
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
            this.name.set(name);
        }

        public void setValue(SimpleStringProperty value) {
            this.value = value;
        }

        public void setOneSigmaABS(SimpleStringProperty oneSigmaABS) {
            this.oneSigmaABS = oneSigmaABS;
        }

        public void setOneSigmaPCT(SimpleStringProperty oneSigmaPCT) {
            this.oneSigmaPCT = oneSigmaPCT;
        }

    }

    public class RefMatDataModel extends DataModel {

        private CheckBox isMeasured;

        public RefMatDataModel(String name, BigDecimal value,
                BigDecimal oneSigmaABS, BigDecimal oneSigmaPCT,
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
