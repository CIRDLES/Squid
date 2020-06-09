/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.dateInterpretations.plots.plotControllers;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import org.cirdles.squid.constants.Squid3Constants;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.fractionTypeSelected;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanRefreshInterface;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class ConcordiaControlNode extends HBox implements ToolBoxNodeInterface {

    private WeightedMeanRefreshInterface plotsController;

    public ConcordiaControlNode(WeightedMeanRefreshInterface plotsController) {
        super(4);

        this.plotsController = plotsController;

        initNode();

    }

    private void initNode() {
        setStyle("-fx-padding: 1;" + "-fx-background-color: white;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 0 2 0 2;"
                + "-fx-border-radius: 4;" + "-fx-border-color: blue;-fx-effect: null;");

        setPrefHeight(23);
        setHeight(23);
        setFillHeight(true);
        setAlignment(Pos.CENTER);

        HBox isotopeChoiceHBox;
        if (fractionTypeSelected.compareTo(Squid3Constants.SpotTypes.UNKNOWN) == 0) {
            isotopeChoiceHBox = new SamplesConcordiaToolBoxNode(plotsController);
        } else {
            isotopeChoiceHBox = new RefMatConcordiaToolBoxNode(plotsController);
        }

        getChildren().addAll(isotopeChoiceHBox);
    }

}
