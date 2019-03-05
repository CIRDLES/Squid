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
package org.cirdles.squid.dialogs;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 * @author James Bowring
 */
public class SquidMessageDialog extends Alert {

    public SquidMessageDialog() {
        super(null);
    }
    
    //http://stackoverflow.com/questions/26341152/controlsfx-dialogs-deprecated-for-what/32618003#32618003
    private SquidMessageDialog(Alert.AlertType alertType, String message, String headerText, Window owner) {
        super(alertType);
        initOwner(owner);
        setTitle("Squid3 Alert");
        setContentText(message);
        setHeaderText(headerText);
        initStyle(StageStyle.DECORATED);        
        getDialogPane().setPrefSize(500, 200);
        getDialogPane().setStyle(getDialogPane().getStyle() + ";-fx-font-family: SansSerif Bold;-fx-font-size: 16");

    }

    /**
     *
     * @param message
     * @param owner
     */
    public static void showWarningDialog(String message, Window owner) {
        Alert alert = new SquidMessageDialog(Alert.AlertType.WARNING, message, "Squid3 warns you:", owner);
        alert.showAndWait();
    }

    /**
     *
     * @param message
     * @param owner
     */
    public static void showInfoDialog(String message, Window owner) {
        Alert alert = new SquidMessageDialog(Alert.AlertType.INFORMATION, message, "Squid3 informs you:", owner);
        alert.showAndWait();
    }

}
