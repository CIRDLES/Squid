/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.cirdles.calamari.core.PrawnFileHandler;
import org.cirdles.calamari.prawn.PrawnFileFilter;
import org.cirdles.calamari.utilities.FileHelper;
import org.cirdles.squid.projects.SquidProject;

/**
 * FXML Controller class
 *
 * @author bowring
 */
public class SquidUIController implements Initializable {

    @FXML
    private Pane centerPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            Pane splashScreen = FXMLLoader.load(getClass().getResource("aboutSquid.fxml"));
            splashScreen.setId("AboutSquid");
            VBox.setVgrow(splashScreen, Priority.ALWAYS);
            HBox.setHgrow(splashScreen, Priority.ALWAYS);
            centerPane.getChildren().set(0, splashScreen);
            splashScreen.setVisible(true);
        } catch (IOException iOException) {
        }

    }

    @FXML
    private void newSquidProjectAction(ActionEvent event) {
        SquidProject squidProject = new SquidProject();
        
        SquidUI.initCalamari(squidProject.getPrawnFileHandler());
        
        File prawnFIle = squidProject.selectPrawnFile();
    }

}
