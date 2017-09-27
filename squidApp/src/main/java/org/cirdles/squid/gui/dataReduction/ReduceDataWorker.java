/*
 * Copyright 2016 CIRDLES.
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
package org.cirdles.squid.gui.dataReduction;

import java.io.IOException;
import java.util.List;
import javafx.event.EventTarget;
import javafx.scene.control.ProgressIndicator;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import org.cirdles.squid.core.PrawnFileHandler;
import org.cirdles.squid.tasks.TaskInterface;
import org.xml.sax.SAXException;

/**
 * Reduces data in the background, updating the UI as it works.
 *
 * @author John Zeringue
 */
public class ReduceDataWorker extends SwingWorker<Void, Integer> {

    private final PrawnFileHandler prawnFileHandler;
    private final boolean useSBM;
    private final boolean userLinFits;
    private final String referenceMaterialLetter;
    private final TaskInterface task;
    private final EventTarget progressBar;

    /**
     *
     * @param prawnFileHandler the value of prawnFileHandler
     * @param useSBM the value of useSBM
     * @param userLinFits the value of userLinFits
     * @param referenceMaterialLetter the value of referenceMaterialLetter
     * @param task the value of task
     * @param progressBar the value of progressBar
     */
    public ReduceDataWorker(
            PrawnFileHandler prawnFileHandler,
            boolean useSBM,
            boolean userLinFits,
            String referenceMaterialLetter,
            TaskInterface task,
            EventTarget progressBar) {

        this.prawnFileHandler = prawnFileHandler;
        this.useSBM = useSBM;
        this.userLinFits = userLinFits;
        this.referenceMaterialLetter = referenceMaterialLetter;
        this.task = task;
        this.progressBar = progressBar;
    }

    @Override
    protected Void doInBackground() {
        ((ProgressIndicator)progressBar).setProgress(0);
        
        prawnFileHandler.setProgressSubscriber(progress -> publish(progress));

        try {
            prawnFileHandler.writeReportsFromPrawnFile(
                    prawnFileHandler.getCurrentPrawnFileLocation(),
                    useSBM,
                    userLinFits,
                    referenceMaterialLetter,
                    task);
        } catch (IOException | JAXBException | SAXException exception) {
            System.out.println("Exception extracting data: "
                    + exception.getStackTrace()[0].toString());
            String[] message
                    = ("Exception extracting data.;"
                            + "Please alert the development team.;"
                            + exception.toString()).split(";");
            JOptionPane.showMessageDialog(
                    null,
                    message,
                    "Calamari Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        prawnFileHandler.setProgressSubscriber(null);

        return null;
    }

    @Override
    protected void process(List<Integer> chunks) {
        double latestValue = chunks.get(chunks.size() - 1);
        ((ProgressIndicator)progressBar).setProgress(latestValue / 100.0);
    }

    @Override
    protected void done() {
       // ((ProgressIndicator)progressBar).setProgress(0);
    }

}
