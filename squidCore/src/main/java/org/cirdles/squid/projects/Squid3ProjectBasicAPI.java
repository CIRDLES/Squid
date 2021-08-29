/*
 * Copyright 2021 James F. Bowring and CIRDLES.org.
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

import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.core.PrawnXMLFileHandler;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.utilities.squidPrefixTree.SquidPrefixTree;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author bowring
 */
public interface Squid3ProjectBasicAPI extends Serializable {

    /**
     * @return the analystName
     */
    String getAnalystName();

    /**
     * @param analystName the analystName to set
     */
    public void setAnalystName(String analystName);

    /**
     * @return the commonPbModel
     */
    ParametersModel getCommonPbModel();

    /**
     * @return the concentrationReferenceMaterialModel
     */
    ParametersModel getConcentrationReferenceMaterialModel();

    /**
     * @return the delimiterForUnknownNames
     */
    String getDelimiterForUnknownNames();

    /**
     * @return the extPErrTh
     */
    double getExtPErrTh();

    /**
     * @param extPErrTh the extPErrTh to set
     */
    void setExtPErrTh(double extPErrTh);

    /**
     * @return the extPErrU
     */
    double getExtPErrU();

    void setExtPErrU(double extPErrU);

    String getFilterForConcRefMatSpotNames();

    void updateFilterForConcRefMatSpotNames(String filterForConcRefMatSpotNames);

    /**
     * @return the filterForRefMatSpotNames
     */
    String getFilterForRefMatSpotNames();

    void updateFilterForRefMatSpotNames(String filterForRefMatSpotNames);

    Map<String, Integer> getFiltersForUnknownNames();

    void updateFiltersForUnknownNames(Map<String, Integer> filtersForUnknownNames);

    /**
     * @return the physicalConstantsModel
     */
    ParametersModel getPhysicalConstantsModel();

    /**
     * @return the prawnFileHandler
     */
    PrawnXMLFileHandler getPrawnFileHandler();

    String getPrawnFileLoginComment();

    List<PrawnFile.Run> getPrawnFileRuns();

    public void removeSpotsFromDataFile(List<String> spotNames);

    public List<String> retrieveRemovedSpotsByName();

    public void restoreSpotToDataFile(String spotName);

    public void restoreAllRunsToPrawnFile();

    String getPrawnFileShrimpSoftwareVersionName();

    String getPrawnSourceFileName();

    String getPrawnSourceFilePath();

    /**
     * @return the prefixTree
     */
    SquidPrefixTree getPrefixTree();

    /**
     * @return the projectName
     */
    String getProjectName();

    void setProjectName(String projectName);

    /**
     * @return the projectNotes
     */
    String getProjectNotes();

    /**
     * @param projectNotes the projectNotes to set
     */
    void setProjectNotes(String projectNotes);

    /**
     * @return the projectType
     */
    Squid3Constants.TaskTypeEnum getProjectType();

    /**
     * @return the referenceMaterialModel
     */
    ParametersModel getReferenceMaterialModel();

    List<PrawnFile.Run> getRemovedRuns();

    /**
     * @return the selectedIndexIsotope
     */
    Squid3Constants.IndexIsoptopesEnum getSelectedIndexIsotope();

    /**
     * @param selectedIndexIsotope the selectedIndexIsotope to set
     */
    void setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum selectedIndexIsotope);

    /**
     * @return the sessionDurationHours
     */
    double getSessionDurationHours();

    /**
     * @return the task
     */
    TaskInterface getTask();

    boolean hasReportsFolder();

    /**
     * @return the squidAllowsAutoExclusionOfSpots
     */
    boolean isSquidAllowsAutoExclusionOfSpots();

    /**
     * @param squidAllowsAutoExclusionOfSpots the
     *                                        squidAllowsAutoExclusionOfSpots to set
     */
    void setSquidAllowsAutoExclusionOfSpots(boolean squidAllowsAutoExclusionOfSpots);

    boolean isTypeGeochron();

    /**
     * @return the useSBM
     */
    boolean isUseSBM();

    /**
     * @param useSBM the useSBM to set
     */
    void setUseSBM(boolean useSBM);

    /**
     * @return the userLinFits
     */
    boolean isUserLinFits();


    /**
     * @param userLinFits the userLinFits to set
     */
    void setUserLinFits(boolean userLinFits);

    boolean prawnFileExists();

    void processPrawnSessionForDuplicateSpotNames();

    void divideSamples();

    SquidPrefixTree generatePrefixTreeFromSpotNames();

}