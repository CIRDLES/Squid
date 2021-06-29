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
package org.cirdles.squid;

import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.projects.Squid3ProjectBasicAPI;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author bowring
 */
public interface Squid3API {

    // project management ++++++++++++++++++++++++++++++++++++++++++++++++++++++

    /**
     * @return
     */
    Squid3ProjectBasicAPI getSquid3Project();

    /**
     * @param prawnXMLFileSourcePath
     * @throws IOException
     * @throws JAXBException
     * @throws SAXException
     * @throws SquidException
     */
    void newSquid3GeochronProjectFromPrawnXML(Path prawnXMLFileSourcePath)
            throws IOException, JAXBException, SAXException, SquidException;

    /**
     * @param prawnXMLFileSourcePath
     * @throws IOException
     * @throws JAXBException
     * @throws SAXException
     * @throws SquidException
     */
    void newSquid3GeochronProjectFromZippedPrawnXML(Path prawnXMLFileSourcePath)
            throws IOException, JAXBException, SAXException, SquidException;

    /**
     * @param projectFilePath
     */
    void openSquid3Project(Path projectFilePath);

    /**
     * @return
     */
    List<String> retrieveSquid3ProjectListMRU();

    /**
     * @throws IOException
     */
    void openDemonstrationSquid3Project() throws IOException;

    /**
     * @throws IOException
     * @throws SquidException
     */
    void saveCurrentSquid3Project() throws IOException, SquidException;

    /**
     * @param squid3ProjectFileTarget
     * @throws IOException
     * @throws SquidException
     */
    void saveAsSquid3Project(File squid3ProjectFileTarget) throws IOException, SquidException;

    // project UI management
    void setUseSBM(boolean doUse);

    void setUseLinearRegression(boolean doUse);

    void setPreferredIndexIsotope(Squid3Constants.IndexIsoptopesEnum isotope);

    void setAutoExcludeSpots(boolean doAutoExclude);

    void setMinimumExternalSigma206238(double minExternalSigma);

    void setMinimumExternalSigma208232(double minExternalSigma);

    void setDefaultCommonPbModel(ParametersModel commonPbModel);

    void setDefaultPhysicalConstantsModel(ParametersModel physicalConstantsModel);

    void setDefaultParametersFromCurrentChoices();

    void refreshModelsAction();

    // reports management ++++++++++++++++++++++++++++++++++++++++++++++++++++++

    /**
     * @return
     * @throws IOException
     */
    Path generateReferenceMaterialSummaryExpressionsReport() throws IOException;

    /**
     * @return
     * @throws IOException
     */
    Path generateUnknownsSummaryExpressionsReport() throws IOException;

    /**
     * @return
     * @throws IOException
     */
    Path generateTaskSummaryReport() throws IOException;

    /**
     * @return
     * @throws IOException
     */
    Path generateProjectAuditReport() throws IOException;

    /**
     * @return
     * @throws IOException
     */
    Path generatePerScanReports() throws IOException;

    /**
     * @throws IOException
     */
    Path generateAllSquid3ProjectReports() throws IOException;

}