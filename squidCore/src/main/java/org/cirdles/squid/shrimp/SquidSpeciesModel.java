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
package org.cirdles.squid.shrimp;

import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

import java.io.Serializable;

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFAULT_BACKGROUND_MASS_LABEL;

/**
 * @author James F. Bowring
 */
public class SquidSpeciesModel implements
        Comparable<SquidSpeciesModel>,
        Serializable,
        XMLSerializerInterface {

    private static final long serialVersionUID = 3001823455775925098L;

    private int massStationIndex;
    private String massStationSpeciesName;
    private String isotopeName;
    private final String prawnFileIsotopeName;
    private String elementName;
    private boolean isBackground;
    private String uThBearingName;
    private boolean viewedAsGraph;
    // added July 2020 to accommodate Ratio mode
    private boolean numeratorRole;
    private boolean denominatorRole;

    public SquidSpeciesModel() {
        this(-1, "NONE", "NONE", "NONE", false, "No", false);
    }

    public SquidSpeciesModel(String isotopeName) {
        this(-1, "NONE", isotopeName, "NONE", false, "No", false);
    }

    public SquidSpeciesModel(
            int massStationIndex,
            String massStationName,
            String isotopeName,
            String elementName,
            boolean isBackground,
            String uThBearingAbbreviation,
            boolean viewedAsGraph) {
        this.massStationIndex = massStationIndex;
        this.massStationSpeciesName = massStationName;
        this.isotopeName = isotopeName;
        this.prawnFileIsotopeName = isotopeName;
        this.elementName = elementName;
        this.isBackground = isBackground;
        this.uThBearingName = uThBearingAbbreviation;
        this.viewedAsGraph = viewedAsGraph;

        this.numeratorRole = true;
        this.denominatorRole = true;
    }

    @Override
    public int compareTo(SquidSpeciesModel squidSpeciesModel) {
        return Integer.compare(massStationIndex, squidSpeciesModel.getMassStationIndex());
    }

    @Override
    public boolean equals(Object squidSpeciesModel) {
        boolean retVal = false;

        if (squidSpeciesModel instanceof SquidSpeciesModel) {
            retVal = massStationIndex == ((SquidSpeciesModel) squidSpeciesModel).getMassStationIndex();
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new SquidSpeciesModelXMLConverter());
        xstream.alias("SquidSpeciesModel", SquidSpeciesModel.class);
    }

    /**
     * @return the massStationIndex
     */
    public int getMassStationIndex() {
        return massStationIndex;
    }

    /**
     * @param massStationIndex the massStationIndex to set
     */
    public void setMassStationIndex(int massStationIndex) {
        this.massStationIndex = massStationIndex;
    }

    /**
     * @return the massStationSpeciesName
     */
    public String getMassStationSpeciesName() {
        return massStationSpeciesName;
    }

    /**
     * @param massStationSpeciesName the massStationSpeciesName to set
     */
    public void setMassStationSpeciesName(String massStationSpeciesName) {
        this.massStationSpeciesName = massStationSpeciesName;
    }

    /**
     * @return the isotopeName
     */
    public String getIsotopeName() {
        String retVal = isotopeName;
        if (isBackground) {
            retVal = DEFAULT_BACKGROUND_MASS_LABEL;
        }
        return retVal;
    }

    /**
     * @param isotopeName the isotopeName to set
     */
    public void setIsotopeName(String isotopeName) {
        if (isotopeName.compareToIgnoreCase(DEFAULT_BACKGROUND_MASS_LABEL) != 0) {
            this.isotopeName = isotopeName;
        }
    }

    /**
     * @return the prawnFileIsotopeName
     */
    public String getPrawnFileIsotopeName() {
        return prawnFileIsotopeName;
    }

    /**
     * @return the elementName
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * @param elementName the elementName to set
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * @return the isBackground
     */
    public boolean getIsBackground() {
        return isBackground;
    }

    /**
     * @param isBackground the isBackground to set
     */
    public void setIsBackground(boolean isBackground) {
        this.isBackground = isBackground;
    }

    public String getuThBearingName() {
        return uThBearingName;
    }

    public void setuThBearingName(String uThBearingName) {
        this.uThBearingName = uThBearingName;
    }

    /**
     * @return the viewedAsGraph
     */
    public boolean isViewedAsGraph() {
        return viewedAsGraph;
    }

    /**
     * @param viewedAsGraph the viewedAsGraph to set
     */
    public void setViewedAsGraph(boolean viewedAsGraph) {
        this.viewedAsGraph = viewedAsGraph;
    }

    /**
     * @return the numeratorRole
     */
    public boolean isNumeratorRole() {
        return numeratorRole;
    }

    /**
     * @param numeratorRole the numeratorRole to set
     */
    public void setNumeratorRole(boolean numeratorRole) {
        this.numeratorRole = numeratorRole;
    }

    /**
     * @return the denominatorRole
     */
    public boolean isDenominatorRole() {
        return denominatorRole;
    }

    /**
     * @param denominatorRole the denominatorRole to set
     */
    public void setDenominatorRole(boolean denominatorRole) {
        this.denominatorRole = denominatorRole;
    }

}