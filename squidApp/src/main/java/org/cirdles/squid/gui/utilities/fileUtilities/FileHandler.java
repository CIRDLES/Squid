/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.utilities.fileUtilities;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWriterMathML;
import org.cirdles.squid.utilities.csvSerialization.ReportSerializerToCSV;
import org.cirdles.squid.utilities.fileUtilities.ProjectFileUtilities;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.cirdles.squid.gui.SquidUIController.squidPersistentState;
import org.cirdles.squid.tasks.TaskInterface;

/**
 * @author James F. Bowring
 */
public class FileHandler {

    public static String selectProjectFile(Window ownerWindow)
            throws IOException {
        String retVal = "";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Project '.squid' file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid Project files", "*.squid"));
        File initDirectory = new File(squidPersistentState.getMRUProjectFolderPath());
        fileChooser.setInitialDirectory(initDirectory.exists() ? initDirectory : null);

        File projectFileNew = fileChooser.showOpenDialog(ownerWindow);

        if (projectFileNew != null) {
            retVal = projectFileNew.getCanonicalPath();
        }

        return retVal;
    }

    public static File saveProjectFile(SquidProject squidProject, Window ownerWindow)
            throws IOException {

        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project '.squid' file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid Project files", "*.squid"));
        File initDirectory = new File(squidPersistentState.getMRUProjectFolderPath());
        fileChooser.setInitialDirectory(initDirectory.exists() ? initDirectory : null);
//        fileChooser.setInitialFileName(squidProject.getProjectName().toUpperCase(Locale.ENGLISH) + ".squid");
        fileChooser.setInitialFileName(squidProject.getProjectName() + ".squid");

        File projectFileNew = fileChooser.showSaveDialog(ownerWindow);

        if (projectFileNew != null) {
            SquidProject.setProjectChanged(false);
            retVal = projectFileNew;
            // capture squid project file name from file for project itself
            squidProject.setProjectName(projectFileNew.getName().substring(0, projectFileNew.getName().lastIndexOf(".")));
            ProjectFileUtilities.serializeSquidProject(squidProject, projectFileNew.getCanonicalPath());
        }

        return retVal;
    }

    public static File selectPrawnXMLFile(Window ownerWindow)
            throws IOException, JAXBException, SAXException {
        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Prawn XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        File initDirectory = new File(squidPersistentState.getMRUPrawnFileFolderPath());
        fileChooser.setInitialDirectory(initDirectory.exists() ? initDirectory : null);

        File prawnXMLFileNew = fileChooser.showOpenDialog(ownerWindow);

        if (prawnXMLFileNew != null) {
            if (prawnXMLFileNew.getName().toLowerCase(Locale.US).endsWith(".xml")) {
                retVal = prawnXMLFileNew;
            } else {
                throw new IOException("Filename does not end with '.xml'");
            }
        }

        return retVal;
    }

    public static File selectZippedPrawnXMLFile(Window ownerWindow)
            throws IOException, JAXBException, SAXException {
        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Zipped Prawn XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Zipped Prawn XML files", "*.zip"));
        File initDirectory = new File(squidPersistentState.getMRUPrawnFileFolderPath());
        fileChooser.setInitialDirectory(initDirectory.exists() ? initDirectory : null);

        File zippedPrawnXMLFileNew = fileChooser.showOpenDialog(ownerWindow);

        if (zippedPrawnXMLFileNew != null) {
            if (zippedPrawnXMLFileNew.getName().toLowerCase(Locale.US).endsWith(".zip")) {
                retVal = zippedPrawnXMLFileNew;
            } else {
                throw new IOException("Filename does not end with '.zip'");
            }
        }

        return retVal;
    }

    public static List<File> selectForJoinTwoPrawnXMLFiles(Window ownerWindow)
            throws IOException, JAXBException, SAXException {
        List<File> retVal = new ArrayList<>();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Two Prawn XML files");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        File initDirectory = new File(squidPersistentState.getMRUPrawnFileFolderPath());
        fileChooser.setInitialDirectory(initDirectory.exists() ? initDirectory : null);

        List<File> prawnXMLFilesNew = fileChooser.showOpenMultipleDialog(ownerWindow);

        if (prawnXMLFilesNew != null) {
            if ((prawnXMLFilesNew.size() == 2)
                    && prawnXMLFilesNew.get(0).getName().toLowerCase(Locale.US).endsWith(".xml")
                    && prawnXMLFilesNew.get(1).getName().toLowerCase(Locale.US).endsWith(".xml")) {
                retVal = prawnXMLFilesNew;
            } else {
                throw new IOException("Please choose exactly 2 Prawn xml files to merge.");
            }
        }

        return retVal;
    }

    public static File savePrawnXMLFile(SquidProject squidProject, Window ownerWindow)
            throws IOException, JAXBException, SAXException {

        File retVal = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Prawn XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        if (squidProject.getPrawnFileHandler().currentPrawnFileLocationFolder().exists()) {
            fileChooser.setInitialDirectory(squidProject.getPrawnFileHandler().currentPrawnFileLocationFolder());
        }
        fileChooser.setInitialFileName(squidProject.getPrawnSourceFileName().toUpperCase(Locale.ENGLISH).replace(".XML", "-REV.xml"));

        File prawnXMLFileNew = fileChooser.showSaveDialog(ownerWindow);

        if (prawnXMLFileNew != null) {
            squidProject.savePrawnXMLFile(prawnXMLFileNew);
            retVal = prawnXMLFileNew;
        }

        return retVal;
    }

    public static File selectSquid25TaskFile(SquidProject squidProject, Window ownerWindow)
            throws SquidException, IOException, JAXBException, SAXException {
        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Squid 2.5 Task File in Excel '.xls");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid 2.5 Excel Task Files", "*.xls"));

        File initDirectory = new File(squidPersistentState.getMRUTaskFolderPath());
        fileChooser.setInitialDirectory(initDirectory.exists() ? initDirectory : null);

        File squidTaskFile = fileChooser.showOpenDialog(ownerWindow);

        if (squidTaskFile != null) {
            if (squidTaskFile.getName().toLowerCase(Locale.US).endsWith(".xls")) {
                retVal = squidTaskFile;
            } else {
                throw new SquidException("Filename does not end with '.xls'");
            }
        }

        return retVal;
    }

    public static File saveExpressionFileXML(Expression expression, Window ownerWindow)
            throws IOException {

        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Expression '.xml' file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Expression '.xml' files", "*.xml"));
        File mruFolder = new File(squidPersistentState.getMRUExpressionFolderPath());
        fileChooser.setInitialDirectory(mruFolder.isDirectory() ? mruFolder : null);
        fileChooser.setInitialFileName(expression.getName() + ".xml");

        File expressionFileXML = fileChooser.showSaveDialog(ownerWindow);

        if (expressionFileXML != null) {
            retVal = expressionFileXML;
            squidPersistentState.updateExpressionListMRU(expressionFileXML);
            ((XMLSerializerInterface) expression)
                    .serializeXMLObject(expressionFileXML.getAbsolutePath());
        }

        return retVal;
    }

    public static File saveTaskFileXML(TaskInterface task, Window ownerWindow)
            throws IOException {

        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Squid Task '.xml' file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid Task '.xml' files", "*.xml"));
        File mruFolder = new File(squidPersistentState.getMRUTaskXMLFolderPath());
        fileChooser.setInitialDirectory(mruFolder.isDirectory() ? mruFolder : null);
        
        // uipdate task name to match file name
        task.setName(task.getName().replaceAll(" ", "_").replaceAll("/", "-").replaceAll("\\\\", "-"));
        fileChooser.setInitialFileName(task.getName() + ".xml");

        File taskFileXML = fileChooser.showSaveDialog(ownerWindow);

        if (taskFileXML != null) {
            retVal = taskFileXML;
            squidPersistentState.updateTaskXMLFileListMRU(taskFileXML);
            ((XMLSerializerInterface) task)
                    .serializeXMLObject(taskFileXML.getAbsolutePath());
        }

        return retVal;
    }

    public static File selectTaskXMLFile(Window ownerWindow)
            throws IOException, JAXBException, SAXException {
        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Squid Task XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid Task XML files", "*.xml"));
        File initDirectory = new File(squidPersistentState.getMRUTaskXMLFolderPath());
        fileChooser.setInitialDirectory(initDirectory.exists() ? initDirectory : null);

        File taskXMLFileNew = fileChooser.showOpenDialog(ownerWindow);

        if (taskXMLFileNew != null) {
            if (taskXMLFileNew.getName().toLowerCase(Locale.US).endsWith(".xml")) {
                squidPersistentState.setMRUTaskXMLFolderPath(taskXMLFileNew.getParent());
                retVal = taskXMLFileNew;
            } else {
                throw new IOException("Filename does not end with '.xml'");
            }
        }

        return retVal;
    }

    public static File saveExpressionGraphHTML(Expression expression, Window ownerWindow)
            throws IOException {

        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Expression graph '.html' file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Expression graph '.html' files", "*.html"));
        File mruFolder = new File(squidPersistentState.getMRUExpressionGraphFolderPath());
        fileChooser.setInitialDirectory(mruFolder.isDirectory() ? mruFolder : null);
        fileChooser.setInitialFileName(expression.getName() + ".html");

        File expressionGraphFileHTML = fileChooser.showSaveDialog(ownerWindow);

        if (expressionGraphFileHTML != null) {
            retVal = expressionGraphFileHTML;
            squidPersistentState.updateExpressionGraphListMRU(expressionGraphFileHTML);
            String content = ExpressionTreeWriterMathML.toStringBuilderMathML(expression.getExpressionTree()).toString();
            Files.write(Paths.get(expressionGraphFileHTML.getPath()), content.getBytes());
        }

        return retVal;
    }

    public static File selectExpressionXMLFile(Window ownerWindow)
            throws IOException, JAXBException, SAXException {
        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Expression xml File '.xml");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Expression xml Files", "*.xml"));
        File mruFolder = new File(squidPersistentState.getMRUExpressionFolderPath());
        fileChooser.setInitialDirectory(mruFolder.isDirectory() ? mruFolder : null);

        File expressionFileXML = fileChooser.showOpenDialog(ownerWindow);

        if (expressionFileXML != null) {
            if (expressionFileXML.getName().toLowerCase(Locale.US).endsWith(".xml")) {
                squidPersistentState.setMRUExpressionFolderPath(expressionFileXML.getParent());
                retVal = expressionFileXML;
            } else {
                throw new IOException("Filename does not end with '.xml'");
            }
        }

        return retVal;
    }

    public static File saveReportFileCSV(boolean rawOutput, String[][] report, Window ownerWindow)
            throws IOException {

        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report '.csv' file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Report '.csv' files", "*.csv"));
        File mruFolder = new File("");//squidPersistentState.getMRUExpressionFolderPath());
        fileChooser.setInitialDirectory(mruFolder.isDirectory() ? mruFolder : null);
        fileChooser.setInitialFileName(report[0][1] + ".csv");

        File reportFileCSV = fileChooser.showSaveDialog(ownerWindow);

        if (reportFileCSV != null) {
            retVal = reportFileCSV;
            //squidPersistentState.updateExpressionListMRU(reportFileCSV);

            ReportSerializerToCSV.writeCSVReport(rawOutput, reportFileCSV, report);
        }

        return retVal;
    }

    public static File getCustomExpressionFolder(Window ownerWindow) {
        File retVal;

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Custom Expressions Folder");
        chooser.setInitialDirectory(squidPersistentState.getCustomExpressionsFile() != null && squidPersistentState.getCustomExpressionsFile().isDirectory()
                ? squidPersistentState.getCustomExpressionsFile().getParentFile() : new File(File.separator + System.getProperty("user.home")));

        retVal = chooser.showDialog(ownerWindow);

        if (retVal != null) {
            squidPersistentState.setCustomExpressionsFile(retVal);
        }

        return retVal;
    }

    public static File setCustomExpressionFolder(Window ownerWindow) {
        File retVal;

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Custom Expressions Folder");
        if (squidPersistentState.getCustomExpressionsFile() != null && squidPersistentState.getCustomExpressionsFile().isDirectory()) {
            chooser.setInitialDirectory(squidPersistentState.getCustomExpressionsFile().getParentFile());
        } else {
            File userHome = new File(File.separator + System.getProperty("user.home"));
            chooser.setInitialDirectory(userHome.isDirectory() ? userHome : null);
        }

        //directory chooser doesn't have an option to set initial folder name, find solution
        retVal = chooser.showDialog(ownerWindow);

        if (retVal != null) {
            squidPersistentState.setCustomExpressionsFile(retVal);
        }

        return retVal;
    }

    public static File parametersManagerSelectPhysicalConstantsXMLFile(Window ownerWindow) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Squid Physical Constants xml File '.xml");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Physical Constants xml Files", "*.xml"));

        return fileChooser.showOpenDialog(ownerWindow);
    }

    public static File parametersManagerSavePhysicalConstantsXMLFile(ParametersModel model, Window ownerWindow) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Squid Physical Constants xml File '.xml");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Physical Constants xml Files", "*.xml"));
        fileChooser.setInitialFileName(model.getModelName() + " v." + model.getVersion() + ".xml");

        return fileChooser.showSaveDialog(ownerWindow);

    }

    public static File parametersManagerSelectReferenceMaterialXMLFile(Window ownerWindow) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Squid Reference Material xml File '.xml");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Reference Material xml Files", "*.xml"));

        return fileChooser.showOpenDialog(ownerWindow);
    }

    public static File parametersManagerSaveReferenceMaterialXMLFile(ParametersModel model, Window ownerWindow) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Squid Reference Material xml File '.xml");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Reference Material xml Files", "*.xml"));
        fileChooser.setInitialFileName(model.getModelName() + " v." + model.getVersion() + ".xml");

        return fileChooser.showSaveDialog(ownerWindow);
    }

    public static File parametersManagerSelectCommonPbModelXMLFile(Window ownerWindow) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Squid Pb Blank IC Model xml File '.xml");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Pb Blank IC Model xml Files", "*.xml"));

        return fileChooser.showOpenDialog(ownerWindow);
    }

    public static File parametersManagerSaveCommonPbModelXMLFile(ParametersModel model, Window ownerWindow) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Squid Pb Blank IC Model xml File '.xml");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Pb Blank IC Model xml Files", "*.xml"));
        fileChooser.setInitialFileName(model.getModelName() + " v." + model.getVersion() + ".xml");

        return fileChooser.showSaveDialog(ownerWindow);
    }

    public static File saveSquidReportModelXMLFile(SquidReportTableInterface table, Window ownerWindow) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Squid Report Settings XML File '.xml");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid Report Settings XML Files", "*.xml"));
        fileChooser.setInitialFileName(table.getReportTableName() + ".xml");

        return fileChooser.showSaveDialog(ownerWindow);
    }

    public static File selectSquidReportModelXMLFile(Window ownerWindow) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Squid Report Settings XML File '.xml");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid Report Settings XML Files", "*.xml"));

        return fileChooser.showOpenDialog(ownerWindow);
    }

    /*
    public static File saveExpressionHTMLFile(Expression exp, Window ownerWindow) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Expression HTML File '.html");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Expression HTML Files", "*.html"));
        fc.setInitialFileName(FileNameFixer.fixFileName(exp.getName() + ".html"));

        return fc.showSaveDialog(ownerWindow);
    }
     */
    public static File selectOPFile(Window ownerWindow) throws IOException {
        File retVal;

        FileChooser fc = new FileChooser();
        fc.setTitle("Select OP File");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("OP Files", "*.op"));
        File mruFolder = new File(squidPersistentState.getMRUOPFileFolderPath());
        fc.setInitialDirectory(mruFolder.isDirectory() ? mruFolder : null);
        retVal = fc.showOpenDialog(ownerWindow);

        squidPersistentState.updateOPFileListMRU(retVal);

        return retVal;
    }
}
