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
package org.cirdles.squid.gui.dataReductionReports.reportsManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ListViewWithDragAndDrop extends Application {

    @Override
    public void start(Stage primaryStage) {
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll("One", "Two", "Three", "Four");

        listView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            cell.setOnDragOver(e -> {
                Dragboard db = e.getDragboard();
                if (db.hasString()) {
                    e.acceptTransferModes(TransferMode.COPY);
                }
            });
            cell.setOnDragDropped(e -> {
                Dragboard db = e.getDragboard();
                if (db.hasString()) {
                    String data = db.getString();
                    if (cell.isEmpty()) {
                        System.out.println("Drop on empty cell: append data");
                        listView.getItems().add(data);
                    } else {
                        System.out.println("Drop on " + cell.getItem() + ": replace data");
                        int index = cell.getIndex();
                        listView.getItems().add(index, data);
                    }
                    e.setDropCompleted(true);
                }
            });

            // highlight cells when drag target. In real life, use an external CSS file
            // and CSS pseudoclasses....
            cell.setOnDragEntered(e -> cell.setStyle("-fx-background-color: gold;"));
            cell.setOnDragExited(e -> cell.setStyle(""));
            return cell;
        });

        TextField textField = new TextField();
        textField.setPromptText("Type text and drag to list view");
        textField.setOnDragDetected(e -> {
            Dragboard db = textField.startDragAndDrop(TransferMode.COPY);
            String data = textField.getText();
            Text text = new Text(data);
            db.setDragView(text.snapshot(null, null));
            ClipboardContent cc = new ClipboardContent();
            cc.putString(data);
            db.setContent(cc);
        });

        BorderPane root = new BorderPane(listView, textField, null, null, null);
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
