/*
 * AbstractRawDataView.java
 *
 * Created Jul 6, 2011
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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

import java.math.BigDecimal;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javax.swing.JLayeredPane;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractDataView extends Canvas {

    protected double x;
    protected double y;
    protected double width;
    protected double height;

    /**
     *
     */
    protected double[] myOnPeakData;
    /**
     *
     */
    protected double[] myOnPeakNormalizedAquireTimes;
    /**
     *
     */
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
    protected double displayOffsetY = 0;
    /**
     *
     */
    protected double displayOffsetX = 0;
    /**
     *
     */
    protected BigDecimal[] tics;

    /**
     *
     */
    public AbstractDataView() {
        super();
    }

    /**
     *
     * @param bounds
     */
    protected AbstractDataView(Rectangle bounds) {
        super(bounds.getWidth(), bounds.getHeight());
        x = bounds.getX();
        y = bounds.getY();

        this.myOnPeakData = null;

        width = bounds.getWidth();
        height = bounds.getHeight();
        graphWidth = (int) width - leftMargin;
        graphHeight = (int) height - topMargin;

        this.tics = null;


    }

    /**
     *
     *
     * @param sampleSessionDataView
     * @param tripoliFraction
     * @param bounds
     * @param invokeMouseListener
     * @param forStandards the value of forStandards
     */
    public AbstractDataView(//
            JLayeredPane sampleSessionDataView, //
            Rectangle bounds, //
            boolean invokeMouseListener,//
            boolean forStandards) {
        this(bounds);
    }

    /**
     *
     * @param g2d
     */
    protected void paintInit(GraphicsContext g2d) {
//        RenderingHints rh = g2d.getRenderingHints();
//        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        g2d.setRenderingHints(rh);
//
//        g2d.setPaint(Color.BLACK);
//        g2d.setStroke(new BasicStroke(1.0f));
//        g2d.setFont(new Font(
//                "SansSerif",
//                Font.BOLD,
//                10));

        relocate(x, y);
        g2d.clearRect(0, 0, width, height);
    }

    /**
     *
     * @param g2d
     */
    public void paint(GraphicsContext g2d) {
        paintInit(g2d);

        drawBorder(g2d);

        drawTicsYAxisInBackground(g2d);

    }

    private void drawBorder(GraphicsContext g2d) {
        g2d.setStroke(Paint.valueOf("BLACK"));
        g2d.setLineWidth(1);
        g2d.strokeRect(0, 0, width, height);

    }

    /**
     *
     * @param x
     * @return
     */
    public double mapX(double x) {
        return (((x - getMinX_Display()) / getRangeX_Display()) * graphWidth) + leftMargin;
    }

    /**
     *
     * @param y
     * @return
     */
    protected double mapY(double y) {
        return (((getMaxY_Display() - y) / getRangeY_Display()) * graphHeight) + topMargin;
    }

    /**
     *
     * @param g2d
     */
    protected void drawTicsYAxisInBackground(GraphicsContext g2d) {
//        // y -axis tics
//        Stroke savedStroke = g2d.getStroke();
//
//        // tics
//        if (tics != null) {
//            for (int i = 0; i < tics.length; i++) {
//                try {
//                    Shape ticMark = new Line2D.Double( //
//                            mapX(minX), mapY(tics[i].doubleValue()), mapX(maxX), mapY(tics[i].doubleValue()));
//
//                    g2d.setPaint(new Color(202, 202, 202));//pale gray
//                    g2d.setStroke(new BasicStroke(0.5f));
//                    g2d.draw(ticMark);
//                } catch (Exception e) {
//                }
//            }
//        } else {
//            double ticWidth = (maxY - minY) / 10;
//            if (ticWidth > 0.0) {
//                for (double tic = minY + ticWidth; tic < (maxY * 0.999); tic += ticWidth) {
//                    Shape ticMark = new Line2D.Double( //
//                            mapX(minX), mapY(tic), mapX(maxX), mapY(tic));
//                    g2d.setPaint(new Color(202, 202, 202));//pale gray
//                    g2d.setStroke(new BasicStroke(0.5f));
//                    g2d.draw(ticMark);
//                }
//            }
//        }
//
//        g2d.setStroke(savedStroke);
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public void refreshPanel(boolean doReScale, boolean inLiveMode) {
        try {
            preparePanel();
        } catch (Exception e) {
        }
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public abstract void preparePanel();

    /**
     * @return the graphWidth
     */
    public int getGraphWidth() {
        return graphWidth;
    }

    /**
     * @param graphWidth the graphWidth to set
     */
    public void setGraphWidth(int graphWidth) {
        this.graphWidth = graphWidth;
    }

    /**
     * @return the graphHeight
     */
    public int getGraphHeight() {
        return graphHeight;
    }

    /**
     * @param graphHeight the graphHeight to set
     */
    public void setGraphHeight(int graphHeight) {
        this.graphHeight = graphHeight;
    }

    /**
     * @return the topMargin
     */
    public int getTopMargin() {
        return topMargin;
    }

    /**
     * @param topMargin the topMargin to set
     */
    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    /**
     * @return the leftMargin
     */
    public int getLeftMargin() {
        return leftMargin;
    }

    /**
     * @param leftMargin the leftMargin to set
     */
    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

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
     *
     * @return
     */
    public double getMinX_Display() {
        return getMinX() + getDisplayOffsetX();
    }

    /**
     *
     * @param minX
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     *
     * @return
     */
    public double getMaxX_Display() {
        return getMaxX() + getDisplayOffsetX();
    }

    /**
     *
     * @param maxX
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     *
     * @return
     */
    public double getMinY_Display() {
        return getMinY() + getDisplayOffsetY();
    }

    /**
     *
     * @param minY
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     *
     * @return
     */
    public double getMaxY_Display() {
        return getMaxY() + getDisplayOffsetY();
    }

    /**
     *
     * @param maxY
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    /**
     *
     * @return
     */
    public double getRangeX_Display() {
        return (getMaxX_Display() - getMinX_Display());
    }

    /**
     *
     * @return
     */
    public double getRangeY_Display() {
        return (getMaxY_Display() - getMinY_Display());
    }

    /**
     * @return the minX
     */
    public double getMinX() {
        return minX;
    }

    /**
     * @return the maxX
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * @return the minY
     */
    public double getMinY() {
        return minY;
    }

    /**
     * @return the maxY
     */
    public double getMaxY() {
        return maxY;
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
     *
     * @param x
     * @return
     */
    protected int convertMouseXToValue(int x) {
        return //
                (int) Math.round(
                        (((double) (x - getLeftMargin())) / (double) getGraphWidth()) //
                        * getRangeX_Display()//
                        + getMinX_Display());
    }

    /**
     *
     * @param y
     * @return
     */
    protected double convertMouseYToValue(double y) {
        return //
                -1 * (((y - topMargin - 1) * getRangeY_Display() / graphHeight) //
                - getMaxY_Display());
    }

    /**
     * @param tics the tics to set
     */
    public void setTics(BigDecimal[] tics) {
        this.tics = tics.clone();
    }

    /**
     * @return the tics
     */
    public BigDecimal[] getTics() {
        return tics.clone();
    }


}
