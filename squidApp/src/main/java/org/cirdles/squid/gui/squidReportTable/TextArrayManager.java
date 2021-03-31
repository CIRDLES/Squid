/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author ryanb
 */
public class TextArrayManager {

    private static TableView<ObservableList<String>> table;
    private static TableView<ObservableList<String>> boundCol;
    private static String[][] array;
    private static ObservableList<ObservableList<String>> data;
    private static final double characterSize = 9.5;
    private static final double columnHeaderCharacterSize = 11;
    private static final RowComparator comparator = new RowComparator();
    private static List<String> aliquots;
    private static SquidReportTableLauncher.ReportTableTab controllerType;

    private TextArrayManager() {
    }

    public static void textArrayManagerInitialize(
            TableView<ObservableList<String>> boundCol, TableView<ObservableList<String>> reportsTable, String[][] array, SquidReportTableLauncher.ReportTableTab controllerType) {
        TextArrayManager.boundCol = boundCol;
        TextArrayManager.table = reportsTable;
        TextArrayManager.array = array;
        TextArrayManager.controllerType = controllerType;
        aliquots = new ArrayList<>();
        data = FXCollections.observableArrayList();

        if (controllerType.equals(SquidReportTableLauncher.ReportTableTab.unknownCustom)) {
            initializeAliquots();
            initializeSortPolicies();
        }
        initializeRowFactories();
        setHeaders();
        setTableItems();
        initializeSelectionProperties();

        reportsTable.refresh();
        boundCol.refresh();
    }

    private static void initializeSelectionProperties() {
        boundCol.setOnMouseClicked(val -> table.getSelectionModel().select(boundCol.getSelectionModel().getSelectedIndex()));
        table.setOnMouseClicked(val -> boundCol.getSelectionModel().select(table.getSelectionModel().getSelectedIndex()));
    }

    private static void initializeAliquots() {
        aliquots.clear();
        int ii = Integer.parseInt(array[0][0]);
        String currentAliquot = array[ii][1];
        aliquots.add(currentAliquot);
        for (int i = ii + 1; i < array.length; i++) {
            if (!array[i][1].isEmpty() && !currentAliquot.equals(array[i][1])) {
                aliquots.add(array[i][1]);
                currentAliquot = array[i][1];
            }
        }
    }

    private static void initializeRowFactories() {
        Callback<TableView<ObservableList<String>>, TableRow<ObservableList<String>>> tableCallBack
                = new Callback<TableView<ObservableList<String>>, TableRow<ObservableList<String>>>() {
            @Override
            public TableRow<ObservableList<String>> call(TableView<ObservableList<String>> personTableView) {
                return new TableRow<ObservableList<String>>() {
                    @Override
                    protected void updateItem(ObservableList<String> list, boolean b) {
                        super.updateItem(list, b);
                        ObservableList<String> styles = getStyleClass();
                        if (list != null) {
                            if (aliquots.contains(list.get(1))) {
                                if (!styles.contains("table-row-aliquot")) {
                                    styles.removeAll(Collections.singleton("table-row-cell"));
                                    styles.add("table-row-aliquot");
                                }
                            } else {
                                styles.removeAll(Collections.singleton("table-row-aliquot"));
                                styles.add("table-row-cell");
                            }
                        }
                    }
                };
            }
        };
        table.setRowFactory(tableCallBack);
        boundCol.setRowFactory(tableCallBack);
    }

    private static void initializeSortPolicies() {
        Callback<TableView<ObservableList<String>>, Boolean> sortPolicy = t -> {
            Comparator<ObservableList<String>> rowComparator = (r1, r2)
                    -> t.getComparator() == null || r1.get(0) != r2.get(0)
                    || aliquots.contains(r1.get(1)) || aliquots.contains(r2.get(1))
                    ? 0 : t.getComparator().compare(r1, r2);
            FXCollections.sort(table.getItems(), rowComparator);
            return true;
        };
        table.setSortPolicy(sortPolicy);
        boundCol.setSortPolicy(sortPolicy);
    }

    private static void setHeaders() {
        table.getColumns().clear();

        TableColumn<ObservableList<String>, String> header = new TableColumn<>(array[0][3]);
        for (int i = 3; i < array[0].length - 1; i++) {
            if (!array[0][i].equals(header.getText()) && !array[0][i].isEmpty()) {
                table.getColumns().add(header);
                header = new TableColumn<>(array[0][i].trim());
            }
            String colName = getColumnName(i, array);
            int colLength = getMaxColumnHeaderLength(colName);
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
            col.setComparator(comparator);
            col.setPrefWidth(colLength * columnHeaderCharacterSize + 20);
            final int colNum = i - 1;
            col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(colNum)));
            header.getColumns().add(col);
        }
        table.getColumns().add(header);
        setUpBoundCol();
    }

    private static int getMaxColumnHeaderLength(String input) {
        int max = 0;
        String[] levels = input.split("\n");
        for (int i = 0; i < levels.length; i++) {
            max = (levels[i].length() > max) ? levels[i].length() : max;
        }
        return max;
    }

    private static String getColumnName(int col, String[][] textArray) {
        String retVal = "";
        for (int i = 1; i <= 4; i++) {
            String currVal = textArray[i][col].trim();
            retVal += currVal;
            if (i != 4) {
                retVal += "\n";
            }
        }

        if (col == textArray[0].length - 2) {
            retVal = "\n\n\nSpot";
        }

        return retVal;
    }

    private static void setTableItems() {
        data.clear();
        int startSpot = Integer.parseInt(array[0][0]);
        String aliquot = null;
        for (int i = startSpot; i < array.length; i++) {
            if (Boolean.parseBoolean(array[i][0])) {
                ObservableList<String> row = FXCollections.observableArrayList();
                if (controllerType == SquidReportTableLauncher.ReportTableTab.unknownCustom) {
                    if (aliquot == null || !array[i][1].isEmpty()) {
                        aliquot = array[i][1];
                    }
                    row.add(aliquot);
                    for (int j = 2; j < array[0].length - 1; j++) {
                        row.add((j == array[0].length - 2) ? array[i][j] + "    " : array[i][j]);
                    }

                } else {
                    for (int j = 1; j < array[0].length - 1; j++) {
                        row.add((j == array[0].length - 2) ? array[i][j] + "    " : array[i][j]);
                    }
                }
                data.add(row);
            }
        }
        table.setItems(data);
        boundCol.setItems(data);
        setUpWidths();
    }

    private static void setUpBoundCol() {
        boundCol.getColumns().clear();
        TableColumn<ObservableList<String>, String> header = new TableColumn<>("Squid3");
        TableColumn<ObservableList<String>, String> col = new TableColumn<>("\n\n\nSpot");
        col.setComparator(comparator);
        col.setPrefWidth(col.getPrefWidth() + 76);
        final int num = 1;
        col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(num)));
        header.getColumns().add(col);
        header.setPrefWidth(header.getMaxWidth());
        boundCol.getColumns().add(header);
    }

    private static void setUpWidths() {
        if (!data.isEmpty()) {
            int header = 0, counter = 0;
            for (int j = 2; j < data.get(0).size(); j++) {
                int max = 0;
                for (int i = 0; i < data.size(); i++) {
                    int length = data.get(i).get(j).length();
                    if (length > max) {
                        max = length;
                    }
                }
                if (table.getColumns().get(header).getColumns().size() <= counter) {
                    header++;
                    counter = 0;
                }
                Object col = table.getColumns().get(header).getColumns().get(counter);
                double prefWidth = max * characterSize;
                if (prefWidth > ((TableColumn) col).getPrefWidth()) {
                    ((TableColumn) col).setPrefWidth(prefWidth);
                }
                counter++;
            }
        }
    }
}
