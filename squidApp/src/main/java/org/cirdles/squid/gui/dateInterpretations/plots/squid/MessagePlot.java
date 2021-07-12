/*
 * Copyright 2020 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.dateInterpretations.plots.squid;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dateInterpretations.plots.PlotDisplayInterface;

import java.util.List;
import java.util.Map;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class MessagePlot extends AbstractDataView implements PlotDisplayInterface {

    private String message;

    public MessagePlot(String message) {
        super(new Rectangle(1000, 600), 0, 0);
        this.message = message;

        leftMargin = 100;
        topMargin = 200;

        setOpacity(1.0);

        widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > 100) {
                    width = newValue.intValue();
                    setGraphWidth((int) width - 2 * leftMargin);
                    displayPlotAsNode();
                }
            }
        });

        heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > 100) {
                    height = newValue.intValue();
                    graphHeight = (int) height - topMargin - topMargin / 2;
                    displayPlotAsNode();
                }
            }
        });
    }

    // https://dlsc.com/2014/04/10/javafx-tip-1-resizable-canvas/
    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefHeight(double width) {
        return this.getHeight();
    }

    @Override
    public double prefWidth(double height) {
        return this.getWidth();
    }

    @Override
    public void preparePanel() {
    }


    /**
     * @param g2d
     */
    @Override
    public void paint(GraphicsContext g2d) {
        super.paint(g2d);

        g2d.setFont(Font.font("SansSerif", FontWeight.SEMI_BOLD, 15));

        g2d.setStroke(Paint.valueOf("BLACK"));
        g2d.setLineWidth(0.5);

        g2d.setFill(Paint.valueOf("RED"));

        g2d.fillText(message, 45, 45);

        g2d.setFill(Paint.valueOf("RED"));

    }

    @Override
    public Node displayPlotAsNode() {
        preparePanel();
        this.repaint();
        return this;
    }

    @Override
    public void setData(List<Map<String, Object>> data) {
    }

    @Override
    public void setProperty(String key, Object datum) {
    }

    @Override
    public List<Node> toolbarControlsFactory() {
        return null;
    }

    @Override
    public String makeAgeOrValueString(int index) {
        return null;
    }

}