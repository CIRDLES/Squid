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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author James F. Bowring
 */
public class SquidSpeciesModel implements Comparable<SquidSpeciesModel>, Serializable {

    private static final long serialVersionUID = 3001823455775925098L;

    private int massStationIndex;
    private String massStationSpeciesName;
    private String isotopeName;
    private String elementName;
    private boolean isBackground;

    public static Map<String, SquidSpeciesModel> knownSquidSpeciesModels = new HashMap<>();

    public SquidSpeciesModel(int massStationIndex, String massStationName, String isotopeName, String elementName, boolean isBackground) {
        this.massStationIndex = massStationIndex;
        this.massStationSpeciesName = massStationName;
        this.isotopeName = isotopeName;
        this.elementName = elementName;
        this.isBackground = isBackground;

        knownSquidSpeciesModels.put(isotopeName, this);
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

    public static int selectBackgroundSpecies(SquidSpeciesModel ssm) {
        // there is at most one
        int retVal = -1;
        for (Map.Entry<String, SquidSpeciesModel> entry : knownSquidSpeciesModels.entrySet()) {
            if (entry.getValue().getIsBackground()) {
                entry.getValue().setIsBackground(false);
                retVal = entry.getValue().getMassStationIndex();
                break;
            }
        }

        if (ssm != null) {
            ssm.setIsBackground(true);
        }

       return retVal;
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
            retVal = "BKG";
        }
        return retVal;
    }

    /**
     * @param isotopeName the isotopeName to set
     */
    public void setIsotopeName(String isotopeName) {
        if (isotopeName.compareToIgnoreCase("BKG") != 0) {
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
