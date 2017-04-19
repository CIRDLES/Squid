/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.cirdles.commons.util.ResourceExtractor;

/**
 * FXML Controller class
 *
 * @author bowring
 */
public class AboutSquidController implements Initializable {

    @FXML
    private Label version;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        version.setText("version:  " + SquidUI.VERSION);
    }

}
