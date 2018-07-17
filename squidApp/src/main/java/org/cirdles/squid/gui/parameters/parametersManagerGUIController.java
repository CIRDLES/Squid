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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.parameters.ValueModel;
import org.cirdles.squid.parameters.matrices.AbstractMatrixModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;
import org.cirdles.squid.parameters.util.StringComparer;
import org.cirdles.squid.parameters.util.TextFieldComparer;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class parametersManagerGUIController implements Initializable {

    @FXML
    private MenuItem physConstImpXML;
    @FXML
    private MenuItem phyConstExpXML;
    @FXML
    private MenuItem remCurrPhyConst;
    @FXML
    private MenuItem editCopyOfCurrPhysConst;
    @FXML
    private MenuItem editNewEmpPhysConst;
    @FXML
    private MenuItem cancelEditOfPhysConst;
    @FXML
    private MenuItem saveAndRegCurrPhysConst;
    @FXML
    private Button physConstQuitButton;
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
    private MenuItem expRefMatXML;
    @FXML
    private MenuItem impRefMatXML;
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
    private Button refMatQuitButton;
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
    private Button okayButton;
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

    String laboratoryName;
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
        laboratoryName = "";

        File physConstFile = new File("src/main/resources/org/cirdles/squid/gui/parameters/EARTHTIME Physical Constants Model v.1.1.xml");
        physConstModel = PhysicalConstantsModel.getPhysicalConstantsModelFromETReduxXML(physConstFile);
        setUpPhysConstCovariancesAndCorrelations();
        physConstModels = new ArrayList<>();
        physConstModels.add(physConstModel);
        setUpPhysConstCB();

        File refMatFile = new File("src/main/resources/org/cirdles/squid/gui/parameters/Zircon-91500 v.1.0.xml");
        refMatModel = ReferenceMaterial.getReferenceMaterialFromETReduxXML(refMatFile);
        setUpRefMatCovariancesAndCorrelations();
        refMatModels = new ArrayList<>();
        refMatModels.add(refMatModel);
        setUpRefMatCB();
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
        setUpPhysConstData();
        setUpMolarMasses();
        setUpReferences();
        setUpPhysConstCov();
        setUpPhysConstCorr();
        setUpPhysConstEditableLabel();
    }

    private void setUpRefMat() {
        setUpRefMatData();
        setUpConcentrations();
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
        setPhysConstModel(0);
        physConstCB.getSelectionModel().selectFirst();
        physConstCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setPhysConstModel(nv.intValue());
            }
        });
    }

    private void setPhysConstModel(int num) {
        physConstModel = physConstModels.get(num);
        setUpPhysConst();
    }

    private void setUpRefMatCB() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (ReferenceMaterial mod : refMatModels) {
            cbList.add(mod.getModelName() + " v." + mod.getVersion());
        }
        refMatCB.setItems(cbList);
        setRefMatModel(0);
        refMatCB.getSelectionModel().selectFirst();
        refMatCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setRefMatModel(nv.intValue());
            }
        });
    }

    private void setRefMatModel(int num) {
        refMatModel = refMatModels.get(num);
        setUpRefMat();
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
                    valMod.getOneSigmaABS(), valMod.getOneSigmaPCT(),
                    isMeasured);
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
        columns.add(nameCol);

        TableColumn valCol = new TableColumn("value");
        valCol.setCellValueFactory(new PropertyValueFactory("value"));
        valCol.setComparator(new TextFieldComparer());
        columns.add(valCol);

        TableColumn absCol = new TableColumn("1σ ABS");
        absCol.setCellValueFactory(new PropertyValueFactory("oneSigmaABS"));
        absCol.setComparator(new TextFieldComparer());
        columns.add(absCol);

        TableColumn pctCol = new TableColumn("1σ PCT");
        pctCol.setCellValueFactory(new PropertyValueFactory("oneSigmaPCT"));
        pctCol.setComparator(new TextFieldComparer());
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

    private void setUpLaboratoryName() {
        labNameTextField.setText(laboratoryName);
        labNameTextField.setOnKeyReleased(value -> {
            laboratoryName = labNameTextField.getText();
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
            setUpRefMat();
        }
    }

    @FXML
    private void physConstRemoveCurrMod(ActionEvent event) {
        physConstModels.remove(physConstModel);
        refMatCB.getSelectionModel().selectFirst();
    }

    @FXML
    private void physConstEditCurrMod(ActionEvent event) {
    }

    @FXML
    private void physConstEditCopy(ActionEvent event) {

    }

    @FXML
    private void physConstEditEmptyMod(ActionEvent event) {
    }

    @FXML
    private void physConstCancelEdit(ActionEvent event) {
    }

    @FXML
    private void physConstSaveAndRegisterEdit(ActionEvent event) {
        physConstModel = new PhysicalConstantsModel();
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

            String currBigDec = mod.getValue().getText();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setValue(BigDecimal.ZERO);
            } else {
                currVal.setValue(new BigDecimal(currBigDec));
            }

            currBigDec = mod.getOneSigmaABS().getText();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setOneSigma(BigDecimal.ZERO);
            } else {
                currVal.setOneSigma(new BigDecimal(currBigDec));
            }

            currVal.setReference(physConstReferences.get(i).getText());
            currVal.setUncertaintyType("ABS");
        }

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

    @FXML
    private void refMatSaveAndRegisterEdit(ActionEvent event
    ) {
        getRhosFromTable(refMatCorrTable);
    }

    @FXML
    private void refMatRemoveCurrMod(ActionEvent event
    ) {
    }

    @FXML
    private void refMatCancelEdit(ActionEvent event
    ) {
    }

    @FXML
    private void refMateEditEmptyMod(ActionEvent event
    ) {
    }

    @FXML
    private void refMatEditCopy(ActionEvent event
    ) {
    }

    @FXML
    private void refMatEditCurrMod(ActionEvent event
    ) {
    }

    public class DataModel {

        private SimpleStringProperty name;
        private TextField value;
        private TextField oneSigmaABS;
        private TextField oneSigmaPCT;

        public DataModel(String name, BigDecimal value,
                BigDecimal oneSigmaABS, BigDecimal oneSigmaPCT) {
            this.name = new SimpleStringProperty(name);
            this.value = new TextField(value.toPlainString());
            this.value.setPrefWidth(Region.USE_COMPUTED_SIZE);
            this.oneSigmaABS = new TextField(oneSigmaABS.toPlainString());
            this.oneSigmaABS.setPrefWidth(Region.USE_COMPUTED_SIZE);
            this.oneSigmaPCT = new TextField(oneSigmaPCT.toPlainString());
            this.oneSigmaPCT.setPrefWidth(Region.USE_COMPUTED_SIZE);
        }

        public String getName() {
            return name.get();
        }

        public TextField getValue() {
            return value;
        }

        public TextField getOneSigmaABS() {
            return oneSigmaABS;
        }

        public TextField getOneSigmaPCT() {
            return oneSigmaPCT;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public void setValue(TextField value) {
            this.value = value;
        }

        public void setOneSigmaABS(TextField oneSigmaABS) {
            this.oneSigmaABS = oneSigmaABS;
        }

        public void setOneSigmaPCT(TextField oneSigmaPCT) {
            this.oneSigmaPCT = oneSigmaPCT;
        }

    }

    public class RefMatDataModel extends DataModel {

        CheckBox isMeasured;

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
