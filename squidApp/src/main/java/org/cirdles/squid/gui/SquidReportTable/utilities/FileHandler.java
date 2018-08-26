/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.SquidReportTable.utilities;

import javafx.stage.FileChooser;

import java.io.File;

import static org.cirdles.SquidReportTable.SquidReportTable.primaryStageWindow;

/**
 *
 * @author ryanb
 */
public class FileHandler {
    public static File getFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("CSV Selector");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV Files", "*.csv", "*.CSV"));
//        fc.setInitialDirectory(new File("src/main/resources/org/cirdles/SquidReportTable/"));

        File csvFile = fc.showOpenDialog(primaryStageWindow);
        
        return csvFile;
    }
}
