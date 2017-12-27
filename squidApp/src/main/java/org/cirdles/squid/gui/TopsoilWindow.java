/*
 * Copyright 2017 CIRDLES.org.
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
package org.cirdles.squid.gui;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author James F. Bowring
 */
public final class TopsoilWindow {

    private Stage primaryStage;
    private double xOffset = 0;
    private double yOffset = 0;
    private Stage topsoilPlotWindow;

    private TopsoilWindow() {
    }

    public TopsoilWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void loadTopsoilWindow(double x, int count) {
        try {
            Parent topsoilPlot = FXMLLoader.load(getClass().getResource("TopsoilPlot.fxml"));
            Scene topsoilPlotScene = new Scene(topsoilPlot, 600, 600);
            topsoilPlotWindow = new Stage(StageStyle.DECORATED);
            // center on Squid
            topsoilPlotWindow.setX(x);//primaryStage.getX() + (primaryStage.getWidth() - 450) / 2);
            topsoilPlotWindow.setY(200);//primaryStage.getY() + (primaryStage.getHeight()- 600) / 2);
            topsoilPlotWindow.setResizable(true);
            topsoilPlotWindow.setScene(topsoilPlotScene);
            topsoilPlotWindow.setTitle("Topsoli Plot #" + count);
            

            topsoilPlotWindow.requestFocus();
            topsoilPlotWindow.initOwner(null);
            topsoilPlotWindow.initModality(Modality.NONE);



            topsoilPlotWindow.show();
        } catch (IOException iOException) {
        }
    }
    
    public void close(){
        topsoilPlotWindow.close();
    }

}
