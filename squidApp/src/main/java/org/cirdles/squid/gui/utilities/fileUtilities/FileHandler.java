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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.xml.bind.JAXBException;
import org.cirdles.squid.exceptions.SquidException;
import static org.cirdles.squid.gui.SquidUIController.squidPersistentState;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWriterMathML;
import org.cirdles.squid.utilities.csvSerialization.ReportSerializerToCSV;
import org.cirdles.squid.utilities.fileUtilities.ProjectFileUtilities;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;
import org.xml.sax.SAXException;

/**
 *
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
        fileChooser.setInitialFileName(squidProject.getProjectName().toUpperCase(Locale.US) + ".squid");

        File projectFileNew = fileChooser.showSaveDialog(ownerWindow);

        if (projectFileNew != null) {
            SquidProject.setProjectChanged(false);
            retVal = projectFileNew;
            ProjectFileUtilities.serializeSquidProject(squidProject, projectFileNew.getCanonicalPath());
        }

        return retVal;
    }

    public static File selectPrawnFile(Window ownerWindow)
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

    public static List<File> selectForJoinTwoPrawnFiles(Window ownerWindow)
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

    public static File savePrawnFile(SquidProject squidProject, Window ownerWindow)
            throws IOException, JAXBException, SAXException {

        File retVal = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Prawn XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        if (squidProject.getPrawnFileHandler().currentPrawnFileLocationFolder().exists()) {
            fileChooser.setInitialDirectory(squidProject.getPrawnFileHandler().currentPrawnFileLocationFolder());
        }
        fileChooser.setInitialFileName(squidProject.getPrawnXMLFileName().toUpperCase(Locale.US).replace(".XML", "-REV.xml"));

        File prawnXMLFileNew = fileChooser.showSaveDialog(ownerWindow);

        if (prawnXMLFileNew != null) {
            squidProject.savePrawnFile(prawnXMLFileNew);
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
        chooser.setInitialDirectory(squidPersistentState.getCustomExpressionsFile() != null ?
                squidPersistentState.getCustomExpressionsFile().getParentFile() : new File(File.separator + System.getProperty("user.home")));

        retVal = chooser.showDialog(ownerWindow);

        if(retVal != null) {
            squidPersistentState.setCustomExpressionsFile(retVal);
        }

        return retVal;
    }

    public static File setCustomExpressionFolder(Window ownerWindow) {
        File retVal;

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Custom Expressions Folder");
        if(squidPersistentState.getCustomExpressionsFile() != null && squidPersistentState.getCustomExpressionsFile().isDirectory()) {
            chooser.setInitialDirectory(squidPersistentState.getCustomExpressionsFile().getParentFile());
        } else {
            File userHome = new File(File.separator + System.getProperty("user.home"));
            chooser.setInitialDirectory(userHome.isDirectory() ? userHome : null);
        }

        //directory chooser doesn't have an option to set initial folder name, find solution

        retVal = chooser.showDialog(ownerWindow);

        if(retVal != null) {
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
}
