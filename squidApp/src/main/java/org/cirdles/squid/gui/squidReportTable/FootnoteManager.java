/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

import javafx.scene.control.TextArea;

/**
 *
 * @author ryanb
 */
public class FootnoteManager {

    public static void setUpFootnotes(TextArea text, String[][] array) {
        text.clear();
        int i = 0;
        while (i < array[6].length && array[6][i].length() > 1) {
            text.setText(text.getText() + array[6][i].replaceAll("&", " ") + "\n");
            i++;
        }
    }
}
