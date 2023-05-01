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
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
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

    // project management ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
     * @param dataFileOPSourcePath
     * @throws IOException
     * @throws JAXBException
     * @throws SAXException
     * @throws SquidException
     */
    void newSquid3GeochronProjectFromDataFileOP(Path dataFileOPSourcePath)
            throws IOException, JAXBException, SAXException, SquidException;

    /**
     * @param projectFilePath
     */
    void openSquid3Project(Path projectFilePath) throws SquidException;

    /**
     * @return
     */
    List<String> retrieveSquid3ProjectListMRU();

    /**
     * @throws IOException
     */
    void openDemonstrationSquid3Project() throws IOException, SquidException;

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

    // project UI management +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    void setUseSBM(boolean doUse) throws SquidException;

    void setUseLinearRegression(boolean doUse) throws SquidException;

    void setPreferredIndexIsotope(Squid3Constants.IndexIsoptopesEnum isotope) throws SquidException;

    void setAutoExcludeSpots(boolean doAutoExclude) throws SquidException;

    void setMinimumExternalSigma206238(double minExternalSigma);

    void setMinimumExternalSigma208232(double minExternalSigma);

    void setDefaultCommonPbModel(ParametersModel commonPbModel) throws SquidException;

    void setDefaultPhysicalConstantsModel(ParametersModel physicalConstantsModel) throws SquidException;

    void setDefaultParametersFromCurrentChoices() throws SquidException;

    void refreshModelsAction() throws SquidException;

    // Sample UI management ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    String[] getArrayOfSampleNames();

    String[][] getArrayOfSpotSummariesFromSample(String sampleName);

    String getReferenceMaterialSampleName();

    void setReferenceMaterialSampleName(String refMatSampleName);

    String getConcReferenceMaterialSampleName();

    void setConcReferenceMaterialSampleName(String concRefMatSampleName);

    void updateSpotName(String oldSpotName, String spotName);

    void updateRefMatModelChoice(ParametersModel refMatModel) throws SquidException;

    void updateConcRefMatModelChoice(ParametersModel concRefMatModel) throws SquidException;

    /**
     * Produces 2 element array where [0] is three flags separated by semicolons with one for each of
     * dates 206_238; 207_206; 208_232 where flags are 0 = no change; 1 = change; F = bad Model.
     * If "F" is first flag, then produce message: "This reference material model is missing meaningful age data.
     * Please choose another model." if any flag is "1", produce message: "This reference material model is missing
     * key age(s), so Squid3 is temporarily substituting values (shown in red) and refreshing as follows:"
     * At this point append element [1], which reports the results of the audit and any temporary
     * changes made to the model to make it useful.
     *
     * @param curRefMatModel
     * @return
     */
    String[] produceAuditOfRefMatModel(ReferenceMaterialModel curRefMatModel);

    String get206_238DateMa(ReferenceMaterialModel curRefMatModel);

    String get207_206DateMa(ReferenceMaterialModel curRefMatModel);

    String get208_232DateMa(ReferenceMaterialModel curRefMatModel);

    String get238_235Abundance(ReferenceMaterialModel curRefMatModel);

    String getU_ppm(ReferenceMaterialModel curConcRefMatModel);

    String getTh_ppm(ReferenceMaterialModel curConcRefMatModel);

    /**
     * Squid3 maintains a list of removed spots so that they can be recovered at anytime
     *
     * @param spotNames
     */
    void removeSpotsFromDataFile(List<String> spotNames) throws SquidException;

    List<String> getRemovedSpotsByName();

    void restoreSpotToDataFile(String spotName) throws SquidException;

    void restoreAllSpotsToDataFile() throws SquidException;

    // Task management +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    // reports management ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
    Path generateAllSquid3ProjectReports() throws IOException, SquidException;

}