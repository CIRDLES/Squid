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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.core.PrawnFileHandler;
import org.cirdles.calamari.prawn.PrawnFile;
import org.cirdles.calamari.prawn.PrawnFile.Run;
import org.xml.sax.SAXException;

/**
 *
 * @author bowring
 */
public class SquidProject {

    private final PrawnFileHandler prawnFileHandler;
    private File prawnFileFile;
    private PrawnFile prawnFile;

    public SquidProject() {
        prawnFileHandler = new PrawnFileHandler();
    }

    /**
     * @return the prawnFileHandler
     */
    public PrawnFileHandler getPrawnFileHandler() {
        return prawnFileHandler;
    }

    public void selectPrawnFile()
            throws IOException, JAXBException,SAXException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Prawn XML file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Prawn XML files", "*.xml"));
        fileChooser.setInitialDirectory(prawnFileHandler.currentPrawnFileLocationFolder());

        prawnFileFile = fileChooser.showOpenDialog(null);

        if (prawnFileFile != null) {
                prawnFileHandler.setCurrentPrawnFileLocation(prawnFileFile.getCanonicalPath());
                prawnFile = prawnFileHandler.unmarshallCurrentPrawnFileXML();
        }
    }

    public PrawnFile deserializePrawnData()
            throws IOException, JAXBException, SAXException{
        return prawnFileHandler.unmarshallCurrentPrawnFileXML();
    }
    
    public String getPrawnXMLFileName(){
        return prawnFileFile.getName();
    }
    
    public String getPrawnFileShrimpSoftwareVersionName(){
        return prawnFile.getSoftwareVersion();
    }
    
    public ObservableList<PrawnFile.Run> getListOfPrawnFileRuns(){
        preProcessRunsForDuplicateSpotNames();
        return FXCollections.observableArrayList(prawnFile.getRun());
    }
    
    private void preProcessRunsForDuplicateSpotNames(){
        List<Run> runs = prawnFile.getRun();
        Map<String, Integer> spotNameCountMap = new HashMap<>();
        for (int i = 0; i < runs.size(); i ++){
            String spotName = runs.get(i).getPar().get(0).getValue();
            if (spotNameCountMap.containsKey(spotName)){
                int count = spotNameCountMap.get(spotName);
                count ++;
                spotNameCountMap.put(spotName, count);
                runs.get(i).getPar().get(0).setValue(spotName + "-DUP-" + count);
            } else {
                spotNameCountMap.put(spotName, 0);
            }
        }     
    }

}
