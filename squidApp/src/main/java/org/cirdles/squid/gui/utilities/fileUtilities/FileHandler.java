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
package org.cirdles.squid.gui.utilities.fileUtilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.xml.bind.JAXBException;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.utilities.fileUtilities.ProjectFileUtilities;
import org.xml.sax.SAXException;

/**
 *
 * @author James F. Bowring
 */
public class FileHandler {

    public static String selectProjectFile(String projectFolderPathMRU, Window ownerWindow)
            throws IOException {
        String retVal = "";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Project '.squid' file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid Project files", "*.squid"));
        File initDirectory = new File(projectFolderPathMRU);
        fileChooser.setInitialDirectory(initDirectory.exists()? initDirectory : null);

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
        fileChooser.setInitialDirectory(null);
        fileChooser.setInitialFileName(squidProject.getProjectName().toUpperCase(Locale.US) + ".squid");

        File projectFileNew = fileChooser.showSaveDialog(ownerWindow);

        if (projectFileNew != null) {
            retVal = projectFileNew;
            ProjectFileUtilities.serializeSquidProject(squidProject, projectFileNew.getCanonicalPath());
        }

        return retVal;
    }

    public static File selectPrawnFile(String prawnFileFolderMRU, Window ownerWindow)
            throws IOException, JAXBException, SAXException {
        File retVal = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Prawn XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        File initDirectory = new File(prawnFileFolderMRU);
        fileChooser.setInitialDirectory(initDirectory.exists()? initDirectory : null);

        File prawnXMLFileNew = fileChooser.showOpenDialog(ownerWindow);

        if (prawnXMLFileNew != null) {
            if (prawnXMLFileNew.getName().toLowerCase(Locale.US).endsWith(".xml")) {
//                squidProject.setupPrawnFile(prawnXMLFileNew);
                retVal = prawnXMLFileNew;
            } else {
                throw new IOException("Filename does not end with '.xml'");
            }
        }

        return retVal;
    }

    public static List<File> selectForJoinTwoPrawnFiles(String prawnFileFolderMRU, Window ownerWindow)
            throws IOException, JAXBException, SAXException {
        List<File> retVal = new ArrayList<>();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Two Prawn XML files");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        File initDirectory = new File(prawnFileFolderMRU);
        fileChooser.setInitialDirectory(initDirectory.exists()? initDirectory : null);

        List<File> prawnXMLFilesNew = fileChooser.showOpenMultipleDialog(ownerWindow);

        if (prawnXMLFilesNew != null) {
            if ((prawnXMLFilesNew.size() == 2)
                    && prawnXMLFilesNew.get(0).getName().toLowerCase(Locale.US).endsWith(".xml")
                    && prawnXMLFilesNew.get(1).getName().toLowerCase(Locale.US).endsWith(".xml")) {
//                squidProject.setupPrawnFileByJoin(prawnXMLFilesNew);
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
        fileChooser.setInitialDirectory(squidProject.getPrawnFileHandler().currentPrawnFileLocationFolder());
        fileChooser.setInitialFileName(squidProject.getPrawnXMLFileName().toUpperCase(Locale.US).replace(".XML", "-REV.xml"));

        File prawnXMLFileNew = fileChooser.showSaveDialog(ownerWindow);

        if (prawnXMLFileNew != null) {
            squidProject.savePrawnFile(prawnXMLFileNew);
            retVal = prawnXMLFileNew;
        }

        return retVal;
    }

    public static boolean selectSquid25TaskFile(SquidProject squidProject, Window ownerWindow)
            throws IOException, JAXBException, SAXException {
        boolean retVal = false;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Squid 2.5 Task File in Excel '.xls");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Squid 2.5 Excel Task Files", "*.xls"));

        File squidTaskFile = fileChooser.showOpenDialog(ownerWindow);

        if (squidTaskFile != null) {
            if (squidTaskFile.getName().toLowerCase(Locale.US).endsWith(".xls")) {
                squidProject.setupTaskSquid25File(squidTaskFile);
                retVal = true;
            } else {
                throw new IOException("Filename does not end with '.xls'");
            }
        }

        return retVal;
    }

}
