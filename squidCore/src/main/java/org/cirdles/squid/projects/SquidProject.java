/*
 * Copyright 2017 cirdles.org.
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
package org.cirdles.squid.projects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.core.PrawnFileHandler;
import org.cirdles.calamari.prawn.PrawnFile;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.xml.sax.SAXException;

/**
 *
 * @author bowring
 */
public class SquidProject {

    private final PrawnFileHandler prawnFileHandler;

    public SquidProject() {
        prawnFileHandler = new PrawnFileHandler();
    }

    /**
     * @return the prawnFileHandler
     */
    public PrawnFileHandler getPrawnFileHandler() {
        return prawnFileHandler;
    }

    public File selectPrawnFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Prawn XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        fileChooser.setInitialDirectory(prawnFileHandler.currentPrawnFileLocationFolder());

        File prawnFile = fileChooser.showOpenDialog(null);

        if (prawnFile != null) {
            try {
                prawnFileHandler.setCurrentPrawnFileLocation(prawnFile.getCanonicalPath());
            } catch (IOException iOException) {
            }
        }

        return prawnFile;
    }

    public List<ShrimpFractionExpressionInterface> extractShrimpFractionsFromPrawnFileData() {
        List<ShrimpFractionExpressionInterface> myShrimpFractions = new ArrayList<>();
        try {
            myShrimpFractions
                    = prawnFileHandler.extractShrimpFractionsFromPrawnFile(prawnFileHandler.getCurrentPrawnFileLocation(), true, false, "T");
        } catch (IOException | JAXBException | SAXException ex) {
            Logger.getLogger(SquidProject.class.getName()).log(Level.SEVERE, null, ex);
        }

        return myShrimpFractions;
    }

    public PrawnFile deserializePrawnData()
            throws IOException, JAXBException, SAXException{
        //TODO: refactor 
        return prawnFileHandler.unmarshallCurrentPrawnFileXML();
    }

}
