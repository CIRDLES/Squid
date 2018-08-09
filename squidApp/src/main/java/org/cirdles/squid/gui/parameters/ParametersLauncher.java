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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
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

    public ParametersLauncher() {
        try {
            squidLabDataStage = new Stage();
            squidLabDataStage.setMinHeight(600);
            squidLabDataStage.setMinWidth(900);
            squidLabDataStage.getIcons().add(new Image(SQUID_LOGO_SANS_TEXT_URL));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SquidParametersManagerGUI.fxml"));
            Scene scene = new Scene(loader.load());

            squidLabDataStage.setScene(scene);
            squidLabDataStage.setTitle("Squid Parameters Manager");

            squidLabDataStage.setOnCloseRequest((WindowEvent e) -> {
                squidLabDataStage.hide();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launchParametersManager(ParametersTab tab) {
        parametersManagerGUIController.chosenTab = tab;
        squidLabDataStage.centerOnScreen();
        squidLabDataStage.requestFocus();
        squidLabDataStage.show();
    }

    public enum ParametersTab {
        physConst, refMat, pbBlankIC
    }
}
