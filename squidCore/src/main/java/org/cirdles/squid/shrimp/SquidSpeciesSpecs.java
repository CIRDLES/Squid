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

/**
 *
 * @author James F. Bowring
 */
public class SquidSpeciesSpecs implements Comparable<SquidSpeciesSpecs> {

    private int massStationIndex;
    private String massStationName;
    private String isotopeName;
    private String elementName;

    public SquidSpeciesSpecs(int massStationIndex, String massStationName, String isotopeName, String elementName) {
        this.massStationIndex = massStationIndex;
        this.massStationName = massStationName;
        this.isotopeName = isotopeName;
        this.elementName = elementName;
    }

    @Override
    public int compareTo(SquidSpeciesSpecs speciesSpecs) {
        return Integer.compare(massStationIndex, speciesSpecs.getMassStationIndex());
    }

    @Override
    public boolean equals(Object speciesSpecs) {
        boolean retVal = false;

        if (speciesSpecs instanceof SquidSpeciesSpecs) {
            retVal = massStationIndex == ((SquidSpeciesSpecs)speciesSpecs).getMassStationIndex();
        }
        
        return retVal;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
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
     * @return the massStationName
     */
    public String getMassStationName() {
        return massStationName;
    }

    /**
     * @param massStationName the massStationName to set
     */
    public void setMassStationName(String massStationName) {
        this.massStationName = massStationName;
    }

    /**
     * @return the isotopeName
     */
    public String getIsotopeName() {
        return isotopeName;
    }

    /**
     * @param isotopeName the isotopeName to set
     */
    public void setIsotopeName(String isotopeName) {
        this.isotopeName = isotopeName;
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

}
