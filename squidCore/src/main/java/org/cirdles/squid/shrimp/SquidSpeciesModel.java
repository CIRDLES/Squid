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
package org.cirdles.squid.shrimp;

import com.thoughtworks.xstream.XStream;
import java.io.Serializable;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class SquidSpeciesModel implements
        Comparable<SquidSpeciesModel>,
        Serializable,
        XMLSerializerInterface {

    private static final long serialVersionUID = 3001823455775925098L;

    public static final String SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL = "BKG";

    private int massStationIndex;
    private String massStationSpeciesName;
    private String isotopeName;
    private String elementName;
    private boolean isBackground;

    public SquidSpeciesModel() {
        this(-1, "NONE", "NONE", "NONE", false);
    }

    public SquidSpeciesModel(int massStationIndex, String massStationName, String isotopeName, String elementName, boolean isBackground) {
        this.massStationIndex = massStationIndex;
        this.massStationSpeciesName = massStationName;
        this.isotopeName = isotopeName;
        this.elementName = elementName;
        this.isBackground = isBackground;
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
            retVal = SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL;
        }
        return retVal;
    }

    /**
     * @param isotopeName the isotopeName to set
     */
    public void setIsotopeName(String isotopeName) {
        if (isotopeName.compareToIgnoreCase(SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL) != 0) {
            this.isotopeName = isotopeName;
        }
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
}
