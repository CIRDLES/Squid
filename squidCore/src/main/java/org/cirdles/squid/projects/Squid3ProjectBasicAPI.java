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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.core.PrawnXMLFileHandler;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.utilities.squidPrefixTree.SquidPrefixTree;

/**
 *
 * @author bowring
 */
public interface Squid3ProjectBasicAPI extends Serializable {

    /**
     * @return the analystName
     */
    String getAnalystName();

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
     * @return the extPErrU
     */
    double getExtPErrU();

    String getFilterForConcRefMatSpotNames();

    /**
     * @return the filterForRefMatSpotNames
     */
    String getFilterForRefMatSpotNames();

    Map<String, Integer> getFiltersForUnknownNames();

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

    boolean isTypeGeochron();

    /**
     * @return the useSBM
     */
    boolean isUseSBM();

    /**
     * @return the userLinFits
     */
    boolean isUserLinFits();

    boolean prawnFileExists();
    
}
