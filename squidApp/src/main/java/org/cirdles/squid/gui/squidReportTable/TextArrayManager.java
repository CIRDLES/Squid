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
import org.cirdles.squid.utilities.IntuitiveStringComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author ryanb
 */
public class TextArrayManager {

    private TableView<ObservableList<String>> table;
    private TableView<ObservableList<String>> boundCol;
    private String[][] array;
    private ObservableList<ObservableList<String>> data;
    private final int characterSize;
    private final int columnHeaderCharacterSize;
    private final ComparatorRowSeparators comparator;
    private List<String> aliquots;

    public TextArrayManager(TableView<ObservableList<String>> boundCol, TableView<ObservableList<String>> table, String[][] array) {
        this.boundCol = boundCol;
        this.table = table;
        this.array = array;
        aliquots = new ArrayList<>();
        data = FXCollections.observableArrayList();
        characterSize = 10;
        columnHeaderCharacterSize = 11;
        comparator = new ComparatorRowSeparators();

        Callback<TableView<ObservableList<String>>, TableRow<ObservableList<String>>> tableCallBack =
                new Callback<TableView<ObservableList<String>>, TableRow<ObservableList<String>>>() {
                    @Override
                    public TableRow<ObservableList<String>> call(TableView<ObservableList<String>> personTableView) {
                        return new TableRow<ObservableList<String>>() {
                            @Override
                            protected void updateItem(ObservableList<String> list, boolean b) {
                                super.updateItem(list, b);
                                ObservableList<String> styles = getStyleClass();
                                boolean isAliquot = true;
                                for (int i = 1; isAliquot && i < list.size(); i++) {
                                    isAliquot = list.get(i).isEmpty();
                                }
                                if (isAliquot) {
                                    if (!styles.contains("table-row-cell")) {
                                        styles.add("table-row-cell");
                                    }
                                } else {
                                    styles.removeAll(Collections.singleton("table-row-cell"));
                                }
                            }
                        };
                    }
                };
        table.setRowFactory(tableCallBack);
        boundCol.setRowFactory(tableCallBack);
        setHeaders();
        setTableItems();
    }

    public void setHeaders() {
        table.getColumns().clear();

        TableColumn<ObservableList<String>, String> header = new TableColumn<>(array[0][3]);
        for (int i = 3; i < array[0].length - 1; i++) {
            if (!array[0][i].equals(header.getText())) {
                table.getColumns().add(header);
                header = new TableColumn<>(array[0][i].trim());
            }
            String colName = getColumnName(i, array);
            int colLength = getMaxColumnHeaderLength(colName);
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
            col.setComparator(comparator);
            col.setPrefWidth(colLength * columnHeaderCharacterSize + 20);
            final int colNum = i - 2;
            col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(colNum)));
            header.getColumns().add(col);
        }
        table.getColumns().add(header);
        setUpBoundCol();
    }

    public int getMaxColumnHeaderLength(String input) {
        int max = 0;
        String[] levels = input.split("\n");
        for (int i = 0; i < levels.length; i++) {
            max = (levels[i].length() > max) ? levels[i].length() : max;
        }
        return max;
    }

    public static String getColumnName(int col, String[][] textArray) {
        String retVal = "";
        for (int i = 1; i <= 4; i++) {
            String currVal = textArray[i][col].trim();
            retVal += currVal;
            if (i != 4) {
                retVal += "\n";
            }
        }

        if (col == textArray[0].length - 2) {
            retVal = "\n\n\nFractions";
        }

        return retVal;
    }

    public void setTableItems() {
        data.clear();
        int startSpot = Integer.parseInt(array[0][0]);
        String aliquot = null;
        for (int i = startSpot; i < array.length; i++) {
            if (aliquot == null || !aliquot.equals(array[i][1]) && !array[i][1].isEmpty()) {
                aliquot = array[i][1];
                ObservableList<String> aliquotRow = FXCollections.observableArrayList();
                aliquotRow.add(aliquot);
                for (int j = 2; j < array[0].length - 2; j++) {
                    aliquotRow.add("");
                }
                data.add(aliquotRow);
            }
            if (Boolean.parseBoolean(array[i][0]) == true) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int j = 2; j < array[0].length - 1; j++) {
                    row.add(array[i][j]);
                }
                data.add(row);
            }
        }
        table.setItems(data);
        boundCol.setItems(data);
        setUpWidths();
    }

    private void setUpBoundCol() {
        boundCol.getColumns().clear();
        TableColumn<ObservableList<String>, String> header = new TableColumn<>("Squid");
        TableColumn<ObservableList<String>, String> col = new TableColumn<>("\n\n\nFractions");
        col.setComparator(comparator);
        col.setPrefWidth(col.getPrefWidth() + 76);
        final int num = 0;
        col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(num)));
        header.getColumns().add(col);
        header.setPrefWidth(header.getMaxWidth());
        boundCol.getColumns().add(header);
    }

    public void setUpWidths() {
        if (!data.isEmpty()) {
            int header = 0, counter = 0;
            for (int j = 1; j < data.get(0).size(); j++) {
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

    public class ComparatorRowSeparators implements Comparator<String> {
        private IntuitiveStringComparator<String> intuitiveStringComparator = new IntuitiveStringComparator<>();

        public ComparatorRowSeparators() {
            aliquots = new ArrayList<>();
            int startSpot = Integer.parseInt(array[0][0]);
            aliquots.add(array[0][1]);
            for (int i = startSpot; i < array.length; i++) {
                if (!aliquots.contains(array[i][1])) {
                    aliquots.add(array[i][1]);
                }
            }
        }

        @Override
        public int compare(String s1, String s2) {
            int retVal;
            if ((!s1.isEmpty() && !s2.isEmpty() && !aliquots.contains(s1) && !aliquots.contains(s2))) {
                retVal = intuitiveStringComparator.compare(s1, s2);
            } else {
                retVal = 0;
            }
            return retVal;
        }
    }
}
