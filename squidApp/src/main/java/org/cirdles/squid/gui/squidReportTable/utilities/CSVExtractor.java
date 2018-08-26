/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

/**
 *
 * @author ryanb
 */
public class CSVExtractor {

    public static String[][] extractCSVFile(File fileName) {
        String fileString = "";
        try {
            Scanner fileScanner = new Scanner(new FileInputStream(fileName));
            while (fileScanner.hasNextLine()) {
                fileString += fileScanner.nextLine() + "\n";
            }
        } catch (Exception e) {
            System.out.println("An error occured: " + e.getMessage());
            e.printStackTrace();
        }
        String[] fileRows = fileString.split("\n");
        String[] fileCol = fileRows[0].split(",");
        String[][] retVal = new String[fileRows.length][];
        for (int i = 0; i < fileRows.length; i++) {
            retVal[i] = fileRows[i].split(",");
        }

        boolean emptyLastRow = true;
        for (int i = 0; i < retVal.length; i++) {
            String[] newRow;
            if (retVal[i][retVal[i].length - 1].trim().equals("")) {
                newRow = new String[retVal[i].length - 1];
                for (int j = 0; j < newRow.length; j++) {
                    newRow[j] = retVal[i][j];
                }
                retVal[i] = newRow;
            }
        }

        return retVal;
    }

}
