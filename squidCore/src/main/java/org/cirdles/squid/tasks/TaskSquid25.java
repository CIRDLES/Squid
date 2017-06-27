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
package org.cirdles.squid.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.extractor.ExcelExtractor;

/**
 *
 * @author James F. Bowring
 */
public class TaskSquid25 implements Serializable {

    private static final long serialVersionUID = -2805382700088270719L;

    private void readObject(
            ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ObjectStreamClass myObject = ObjectStreamClass.lookup(Class.forName(TaskSquid25.class.getCanonicalName()));
        long theSUID = myObject.getSerialVersionUID();
        System.out.println("Customized De-serialization of TaskSquid25 " + theSUID);
    }
    private String squidVersion;
    private String squidTaskFileName;
    private String taskType;
    private String taskName;
    private String taskDescription;
    private String[] ratioNames;

    public static TaskSquid25 importSquidTaskFile(File squidTaskFile) {

        TaskSquid25 taskSquid25 = null;

        try {
            InputStream inp = new FileInputStream(squidTaskFile);
            HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
            ExcelExtractor extractor = new org.apache.poi.hssf.extractor.ExcelExtractor(wb);

            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);
            String text = extractor.getText();

            String[] lines = text.split("\n");
            if (lines[0].startsWith("Created by SQUID")) {
                taskSquid25 = new TaskSquid25();

                taskSquid25.squidVersion = lines[0].split("\t")[1];

                int firstRow = Integer.valueOf(lines[1].split("\t")[1]) - 1;

                taskSquid25.squidTaskFileName = lines[firstRow].split("\t")[1];
                taskSquid25.taskType = lines[firstRow + 1].split("\t")[1];
                taskSquid25.taskName = lines[firstRow + 2].split("\t")[1];
                taskSquid25.taskDescription = lines[firstRow + 3].split("\t")[1];

                String[] ratioStrings = lines[firstRow + 15].split("\t");
                int countOfRatios = Integer.valueOf(ratioStrings[1]);
                taskSquid25.ratioNames = new String[countOfRatios];
                for (int i = 0; i < taskSquid25.ratioNames.length; i++) {
                    taskSquid25.ratioNames[i] = ratioStrings[i + 2];
                }
            }
        } catch (IOException iOException) {
        }

        return taskSquid25;
    }

    public String printSummaryData() {
        StringBuilder summary = new StringBuilder();
        summary.append("Squid2.5 Task Name: ");
        summary.append("\t");
        summary.append(taskName);
        summary.append("\n\n");

        summary.append("Task File Name: ");
        summary.append("\t");
        summary.append(squidTaskFileName);
        summary.append("\n\n");

        summary.append("Squid Version: ");
        summary.append("\t");
        summary.append(squidVersion);
        summary.append("\n\n");

        summary.append("Task Type: ");
        summary.append("\t\t");
        summary.append(taskType);
        summary.append("\n\n");

        summary.append("Task Description: ");
        summary.append("\t");
        summary.append(taskDescription.replaceAll(",", "\n\t\t\t\t"));
        summary.append("\n\n");

        summary.append("Task Ratios: ");
        summary.append("\t\t");
        for (int i = 0; i < ratioNames.length; i++) {
            summary.append(ratioNames[i]);
            summary.append((i < (ratioNames.length - 1)) ? ", " : "");
        }
        summary.append("\n\n");

        return summary.toString();
    }

    /**
     * @return the squidVersion
     */
    public String getSquidVersion() {
        return squidVersion;
    }

    /**
     * @return the squidTaskFileName
     */
    public String getSquidTaskFileName() {
        return squidTaskFileName;
    }

    /**
     * @return the taskType
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @return the taskDescription
     */
    public String getTaskDescription() {
        return taskDescription;
    }

    /**
     * @return the ratioNames
     */
    public String[] getRatioNames() {
        return ratioNames;
    }

}
