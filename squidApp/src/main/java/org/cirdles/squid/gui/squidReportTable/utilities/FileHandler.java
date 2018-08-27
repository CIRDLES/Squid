/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable.utilities;

import javafx.stage.FileChooser;

import java.io.File;

import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;


/**
 * @author ryanb
 */
public class FileHandler {
    public static File getFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("CSV Selector");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV Files", "*.csv", "*.CSV"));

        File csvFile = fc.showOpenDialog(primaryStageWindow);

        return csvFile;
    }
}
