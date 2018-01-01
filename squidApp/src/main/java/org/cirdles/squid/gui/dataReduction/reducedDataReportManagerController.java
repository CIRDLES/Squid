/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.dataReduction;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import org.cirdles.squid.gui.SquidUI;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class reducedDataReportManagerController implements Initializable {

    @FXML
    private AnchorPane reducedUnknownsReportAnchorPane;
    @FXML
    private TextArea reducedUnknownsReportTextArea;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reducedUnknownsReportAnchorPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        reducedUnknownsReportAnchorPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        reducedUnknownsReportTextArea.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        reducedUnknownsReportTextArea.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));
      
        reducedUnknownsReportTextArea.setStyle(SquidUI.PEEK_LIST_CSS_STYLE_SPECS);
         // todo: fix demeters law violation
        squidProject.getTask().produceSummaryReportsForGUI();
        reducedUnknownsReportTextArea.setText(
                squidProject.getPrawnFileHandler().getReportsEngine().produceCalamariReportByFlavor(SquidUI.calamariReportFlavor).replaceAll(",", ""));
    }
}
