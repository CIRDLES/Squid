/*
 * Copyright 2017 CIRDLES.
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

/**
 *
 * @author James Bowring
 */
public class SquidMessageDialog extends Alert {

    //http://stackoverflow.com/questions/26341152/controlsfx-dialogs-deprecated-for-what/32618003#32618003
    private SquidMessageDialog(Alert.AlertType alertType, String message) {
        super(alertType);
        setTitle("Squid3 Message");
        setContentText(message);
        initStyle(StageStyle.UTILITY);
    }
    
    /**
     * 
     * @param message 
     */
    public static void showWarningDialog(String message){
        Alert alert = new SquidMessageDialog(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }
    
        /**
     * 
     * @param message 
     */
    public static void showInfoDialog(String message){
        Alert alert = new SquidMessageDialog(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

}
