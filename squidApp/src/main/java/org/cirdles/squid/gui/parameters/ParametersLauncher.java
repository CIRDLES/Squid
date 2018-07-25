/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.parameters;

import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.parameters.parametersManagerGUIController.getModVersionName;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;

/**
 *
 * @author ryanb
 */
public class ParametersLauncher {

    public static Stage squidLabDataStage;
    public static TabPane tabs;
    public static ChoiceBox<String> physConstCB;
    public static ChoiceBox<String> refMatCB;

    public ParametersLauncher() {
        try {
            squidLabDataStage = new Stage();
            squidLabDataStage.setMinHeight(600);
            squidLabDataStage.setMinWidth(900);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SquidParametersManagerGUI.fxml"));
            Scene scene = new Scene(loader.load());

            Map<String, Object> obMap = loader.getNamespace();
            tabs = (TabPane) obMap.get("rootTabPane");
            physConstCB = (ChoiceBox<String>) obMap.get("physConstCB");
            refMatCB = (ChoiceBox<String>) obMap.get("refMatCB");

            squidLabDataStage.setScene(scene);
            squidLabDataStage.setTitle("Squid Parameters Manager");

            squidLabDataStage.setOnCloseRequest((WindowEvent e) -> {
                squidLabDataStage.hide();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launchParametersManager(boolean isRefMat) {
        if (isRefMat) {
            tabs.getSelectionModel().select(1);
        } else {
            tabs.getSelectionModel().select(0);

        }
        squidLabDataStage.centerOnScreen();
        squidLabDataStage.requestFocus();
        squidLabDataStage.show();
    }

    public static void setUpPhysConstCBItems() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        String selected = (String) physConstCB.getSelectionModel().getSelectedItem();
        for (PhysicalConstantsModel mod : squidLabData.getPhysicalConstantsModels()) {
            cbList.add(getModVersionName(mod));
        }
        physConstCB.setItems(cbList);
        physConstCB.getSelectionModel().select(selected);
    }

    public static void setUpRefMatCBItems() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        String selected = (String) refMatCB.getSelectionModel().getSelectedItem();
        for (ReferenceMaterial mod : squidLabData.getReferenceMaterials()) {
            cbList.add(getModVersionName(mod));
        }
        refMatCB.setItems(cbList);
        refMatCB.getSelectionModel().select(selected);
    }
}
