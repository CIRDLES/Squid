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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWriterMathML;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class ExpressionExplorerController implements Initializable {

    private WebEngine webEngine;

    @FXML
    private ListView<ExpressionTreeInterface> expressionListView;
    @FXML
    private TextField expressionText;
    @FXML
    private WebView browser;
    @FXML
    private Label expressionAuditLabel;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ExpressionTree.TASK = SquidUIController.squidProject.getTask();

        // initialize expressions tab
        ObservableList<ExpressionTreeInterface> items = FXCollections.observableArrayList(
                SquidUIController.squidProject.getTask().getTaskExpressionsOrdered());
//                CustomExpression_LnPbR_U.EXPRESSION,
//                CustomExpression_LnUO_U.EXPRESSION,
//                CustomExpression_Net204cts_sec.EXPRESSION,
//                CustomExpression_Net204BiWt.EXPRESSION,
//                SquidExpressionMinus1.EXPRESSION,
//                SquidExpressionMinus3.EXPRESSION,
//                SquidExpressionMinus4.EXPRESSION,
//                CustomExpression_Expo.EXPRESSION,
//                CustomExpression_TestIf.EXPRESSION);

        Iterator<String> ratioNameIterator = SquidRatiosModel.knownSquidRatiosModels.keySet().iterator();
        while (ratioNameIterator.hasNext()) {
            String ratioName = ratioNameIterator.next();
            items.add(SquidUIController.squidProject.getTask().buildRatioExpression(ratioName));
        }

        expressionListView.setItems(items);

        webEngine = browser.getEngine();

        expressionListView.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends ExpressionTreeInterface> ov, ExpressionTreeInterface old_val, ExpressionTreeInterface new_val) -> {

                    Expression holder = new Expression();
                    holder.setExpressionTree(new_val);
                    expressionAuditLabel.setText(holder.produceExpressionTreeAudit());
                    webEngine.loadContent(ExpressionTreeWriterMathML.toStringBuilderMathML(new_val).toString());

// PLAYGROUND
//            webEngine.documentProperty().addListener((obj, prev, newv) -> {
//
//                String heightText = webEngine.executeScript( // <- Some modification, which gives moreless the same result than the original
//                        "var body = document.body,"
//                        + "html = document.documentElement;"
//                        + "Math.max( body.scrollHeight , body.offsetHeight ), "
//                        + "html.clientHeight, html.scrollHeight , html.offsetHeight );"
//                ).toString();
//
//                System.out.println("heighttext: " + heightText);
//                Double height = Double.parseDouble(heightText.replace("px", "")) + 15.0;  // <- Why are this 15.0 required??
//                browser.setPrefHeight(height);
//                //this.setPrefHeight(height);
//            });
//
//            AnimationTimer timer = new AnimationTimer() {
//                private int pulseCounter = 0;
//
//                @Override
//                public void handle(long now) {
//                    pulseCounter += 1;
//                    if (pulseCounter > 300) {
//                        stop();
//                        File destFile = new File("test.png");
//                        WritableImage snapshot = browser.snapshot(new SnapshotParameters(), null);
//                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
//                        try {
//                            ImageIO.write(renderedImage, "png", destFile);
//                        } catch (IOException ex) {
//
//                        }
//                    }
//                }
//            };
//
//            // timer.start();
                });
    }

    @FXML
    private void handleParseButtonAction(ActionEvent event) {

        Expression exp = SquidUIController.squidProject.getTask().generateExpressionFromRawExcelStyleText("NoName", expressionText.getText());
       
        expressionAuditLabel.setText(exp.produceExpressionTreeAudit());
        webEngine.loadContent(ExpressionTreeWriterMathML.toStringBuilderMathML(exp.getExpressionTree()).toString());

// PLAYGROUND
//        AnimationTimer timer = new AnimationTimer() {
//            private int pulseCounter = 0;
//
//            @Override
//            public void handle(long now) {
//                pulseCounter += 1;
//                if (pulseCounter > 300) {
//                    stop();
//                    File destFile = new File("test.png");
//                    WritableImage snapshot = browser.snapshot(new SnapshotParameters(), null);
//                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
//                    try {
//                        ImageIO.write(renderedImage, "png", destFile);
//
//                        BufferedImage bi = ImageIO.read(destFile);
//                        BufferedImage crop = getCroppedImageII(bi);
//                        ImageIO.write(crop, "png", new File("crop.png"));
//
//                    } catch (IOException ex) {
//
//                    }
//                }
//            }
//        };
//
//        timer.start();
    }

    public BufferedImage getCroppedImage(BufferedImage source, double tolerance) {
        // Get our top-left pixel color as our "baseline" for cropping
        int baseColor = source.getRGB(0, 0);

        int width = source.getWidth();
        int height = source.getHeight();

        int topY = Integer.MAX_VALUE, topX = Integer.MAX_VALUE;
        int bottomY = -1, bottomX = -1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (colorWithinTolerance(baseColor, source.getRGB(x, y), tolerance)) {
                    if (x < topX) {
                        topX = x;
                    }
                    if (y < topY) {
                        topY = y;
                    }
                    if (x > bottomX) {
                        bottomX = x;
                    }
                    if (y > bottomY) {
                        bottomY = y;
                    }
                }
            }
        }

        BufferedImage destination = new BufferedImage((bottomX - topX + 1),
                (bottomY - topY + 1), BufferedImage.TYPE_INT_ARGB);

        destination.getGraphics().drawImage(source, 0, 0,
                destination.getWidth(), destination.getHeight(),
                topX, topY, bottomX, bottomY, null);

        return destination;
    }

    private boolean colorWithinTolerance(int a, int b, double tolerance) {
        int aAlpha = (int) ((a & 0xFF000000) >>> 24);   // Alpha level
        int aRed = (int) ((a & 0x00FF0000) >>> 16);   // Red level
        int aGreen = (int) ((a & 0x0000FF00) >>> 8);    // Green level
        int aBlue = (int) (a & 0x000000FF);            // Blue level

        int bAlpha = (int) ((b & 0xFF000000) >>> 24);   // Alpha level
        int bRed = (int) ((b & 0x00FF0000) >>> 16);   // Red level
        int bGreen = (int) ((b & 0x0000FF00) >>> 8);    // Green level
        int bBlue = (int) (b & 0x000000FF);            // Blue level

        double distance = Math.sqrt((aAlpha - bAlpha) * (aAlpha - bAlpha)
                + (aRed - bRed) * (aRed - bRed)
                + (aGreen - bGreen) * (aGreen - bGreen)
                + (aBlue - bBlue) * (aBlue - bBlue));

        // 510.0 is the maximum distance between two colors 
        // (0,0,0,0 -> 255,255,255,255)
        double percentAway = distance / 510.0d;

        return (percentAway > tolerance);
    }

    private BufferedImage getCroppedImageII(BufferedImage source) throws IOException {

//        boolean flag = false;
//        int upperBorder = -1;
//        do {
//            upperBorder++;
//            for (int c1 = 0; c1 < source.getWidth(); c1++) {
//                if (source.getRGB(c1, upperBorder) != Color.white.getRGB()) {
//                    flag = true;
//                    break;
//                }
//            }
//
//            if (upperBorder >= source.getHeight()) {
//                flag = true;
//            }
//        } while (!flag);
        BufferedImage destination = new BufferedImage(source.getWidth(), 100, BufferedImage.TYPE_INT_ARGB);
        // destination.getGraphics().drawImage(source, 0, 0, source.getWidth(), source.getHeight(), null);

        destination.getGraphics().drawImage(source, 0, 0,
                destination.getWidth(), destination.getHeight(),
                0, 0, destination.getWidth(), 100, null);

        return destination;

    }
}
