/*
 * AbstractRawDataView.java
 *
 * Created Jul 6, 2011
 *
 * Copyright 2006 James F. Bowring and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.gui.dataViews;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.cirdles.squid.prawn.PrawnFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author James F. Bowring
 */
public abstract class AbstractDataView extends Canvas {

    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected static int indexOfSelectedSpot = -1;
    protected static int indexOfSecondSelectedSpotForMultiSelect = -1;
    protected static List<Integer> listOfSelectedIndices = new ArrayList<>();
    protected static  List<PrawnFile.Run> selectedRuns = new ArrayList<>();

    /**
     *
     */
    protected double[] myOnPeakData;
    /**
     *
     */
    protected double[] myOnPeakNormalizedAquireTimes;
    protected int graphWidth;
    /**
     *
     */
    protected int graphHeight;
    /**
     *
     */
    protected int topMargin = 0;
    /**
     *
     */
    protected int leftMargin = 0;
    /**
     *
     */
    protected double minX;
    /**
     *
     */
    protected double maxX;
    /**
     *
     */
    protected double minY;
    /**
     *
     */
    protected double maxY;
    /**
     *
     */
    protected BigDecimal[] ticsX;
    /**
     *
     */
    protected BigDecimal[] ticsY;
    protected BigDecimal[] ticsYII;
    protected boolean showTimeNormalized;
    protected boolean showspotLabels;
    /**
     *
     */
    private double displayOffsetY = 0;
    /**
     *
     */
    private double displayOffsetX = 0;


    /**
     *
     */
    private AbstractDataView() {
        super();
    }

    /**
     * @param bounds
     */
    protected AbstractDataView(Rectangle bounds, int leftMargin, int topMargin) {
        super(bounds.getWidth(), bounds.getHeight());
        x = bounds.getX();
        y = bounds.getY();
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;

        this.myOnPeakData = null;

        width = bounds.getWidth();
        height = bounds.getHeight();
        graphWidth = (int) width - leftMargin;
        graphHeight = (int) height - topMargin;

        this.ticsY = null;
    }

    public static List<Integer> getListOfSelectedIndices() {
        return listOfSelectedIndices;
    }

    public static void setListOfSelectedIndices(List<Integer> listOfSelectedIndices) {
        AbstractDataView.listOfSelectedIndices = listOfSelectedIndices;
    }

    public static void setSelectedRuns(List<PrawnFile.Run> selectedRuns) {
        AbstractDataView.selectedRuns = selectedRuns;
    }

    /**
     * @param g2d
     */
    protected void paintInit(GraphicsContext g2d) {
        relocate(x, y);
        g2d.clearRect(0, 0, width, height);
    }

    /**
     * @param g2d
     */
    public void paint(GraphicsContext g2d) {
        paintInit(g2d);

        drawBorder(g2d);
    }

    public void repaint() {
        paint(this.getGraphicsContext2D());
    }

    private void drawBorder(GraphicsContext g2d) {
        // fill it in
        g2d.setFill(Paint.valueOf("WHITE"));
        g2d.fillRect(0, 0, width, height);

        // draw border
        g2d.setStroke(Paint.valueOf("BLACK"));
        g2d.setLineWidth(1);
        g2d.strokeRect(1, 1, width - 1, height - 1);

    }

    /**
     * @param x
     * @return mapped x
     */
    public double mapX(double x) {
        return (((x - getMinX_Display()) / getRangeX_Display()) * graphWidth) + leftMargin;
    }

    /**
     * @param y
     * @return mapped y
     */
    protected double mapY(double y) {
        return (((getMaxY_Display() - y) / getRangeY_Display()) * graphHeight) + topMargin;
    }

    /**
     * @param doReScale  the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public void refreshPanel(boolean doReScale, boolean inLiveMode) {
        try {
            preparePanel();
            repaint();
        } catch (Exception e) {
        }
    }

    /**
     */
    public abstract void preparePanel();

    /**
     * @return the displayOffsetY
     */
    public double getDisplayOffsetY() {
        return displayOffsetY;
    }

    /**
     * @param displayOffsetY the displayOffsetY to set
     */
    public void setDisplayOffsetY(double displayOffsetY) {
        this.displayOffsetY = displayOffsetY;
    }

    /**
     * @return the displayOffsetX
     */
    public double getDisplayOffsetX() {
        return displayOffsetX;
    }

    /**
     * @param displayOffsetX the displayOffsetX to set
     */
    public void setDisplayOffsetX(double displayOffsetX) {
        this.displayOffsetX = displayOffsetX;
    }

    /**
     * @return minimum displayed x
     */
    public double getMinX_Display() {
        return minX + displayOffsetX;
    }

    /**
     * @return maximum displayed x
     */
    public double getMaxX_Display() {
        return maxX + displayOffsetX;
    }

    /**
     * @return minimum displayed y
     */
    public double getMinY_Display() {
        return minY + displayOffsetY;
    }

    /**
     * @return maximum displayed y
     */
    public double getMaxY_Display() {
        return maxY + displayOffsetY;
    }

    /**
     * @return
     */
    public double getRangeX_Display() {
        return (getMaxX_Display() - getMinX_Display());
    }

    /**
     * @return
     */
    public double getRangeY_Display() {
        return (getMaxY_Display() - getMinY_Display());
    }

    /**
     * @return the myOnPeakData
     */
    public double[] getMyOnPeakData() {
        return myOnPeakData.clone();
    }

    /**
     * @return the myOnPeakNormalizedAquireTimes
     */
    public double[] getMyOnPeakNormalizedAquireTimes() {
        return myOnPeakNormalizedAquireTimes.clone();
    }

    /**
     * @param x
     * @return
     */
    protected double convertMouseXToValue(double x) {
        double convertedX = ((x - leftMargin + 2) / (double) graphWidth) //
                * getRangeX_Display()//
                + getMinX_Display();

        return convertedX;
    }

    /**
     * @param y
     * @return
     */
    protected double convertMouseYToValue(double y) {
        return -1 * (((y - topMargin - 1) * getRangeY_Display() / graphHeight) //
                - getMaxY_Display());
    }

    protected boolean mouseInHouse(javafx.scene.input.MouseEvent evt) {
        return ((evt.getX() >= leftMargin)
                && (evt.getY() >= topMargin)
                && (evt.getY() < graphHeight + topMargin - 2)
                && (evt.getX() < (graphWidth + leftMargin - 2)));
    }

    public void setGraphWidth(int graphWidth) {
        this.graphWidth = graphWidth;
    }

    public void setMyWidth(double width) {
        this.width = width;
        setWidth(width);
    }
}