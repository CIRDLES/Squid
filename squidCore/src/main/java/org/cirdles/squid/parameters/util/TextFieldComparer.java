/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.util;

import java.util.Comparator;
import javafx.scene.control.TextField;

/**
 *
 * @author ryanb
 */
public class TextFieldComparer implements Comparator<TextField> {

    @Override
    public int compare(TextField t1, TextField t2) {
        StringComparer stringComparator = new StringComparer();
        return stringComparator.compare(t1.getText(), t2.getText());
    }
}
