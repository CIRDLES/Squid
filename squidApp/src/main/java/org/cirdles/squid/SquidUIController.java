/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * FXML Controller class
 *
 * @author bowring
 */
public class SquidUIController implements Initializable {

    @FXML
    private Color x2;
    @FXML
    private Font x1;
    @FXML
    private Color x4;
    @FXML
    private Font x3;
    @FXML
    private AnchorPane anchorView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            Pane splashScreen = FXMLLoader.load(getClass().getResource("aboutSquid.fxml"));
            splashScreen.setId("AboutSquid");
            VBox.setVgrow(splashScreen, Priority.ALWAYS);
            HBox.setHgrow(splashScreen, Priority.ALWAYS);
            anchorView.getChildren().set(0, splashScreen);
            splashScreen.setVisible(true);
        } catch (IOException iOException) {
        }


    }    
    
}
