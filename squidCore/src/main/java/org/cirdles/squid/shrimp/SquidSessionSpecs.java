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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author James F. Bowring
 */
public class SquidSessionSpecs implements Serializable {

    private static final long serialVersionUID = -2969849117024998190L;
//    
//    private void readObject(
//            ObjectInputStream stream)
//            throws IOException, ClassNotFoundException {
//        stream.defaultReadObject();
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(Class.forName(SquidSessionSpecs.class.getCanonicalName()));
//        long theSUID = myObject.getSerialVersionUID();
//        System.out.println("Customized De-serialization of SquidSessionSpecs " + theSUID);
//    }

    private List<SquidSpeciesSpecs> squidSpeciesSpecsList;
    private List<SquidRatiosSpecs> squidRatiosSpecsList;
    private boolean useSBM;
    private boolean userLinFits;
    private String referenceMaterialNameFilter;

    public SquidSessionSpecs(List<SquidSpeciesSpecs> squidSpeciesSpecsList, List<SquidRatiosSpecs> squidRatiosSpecsList, boolean useSBM, boolean userLinFits, String referenceMaterialNameFilter) {
        this.squidSpeciesSpecsList = squidSpeciesSpecsList == null ? new ArrayList<>() : squidSpeciesSpecsList;
        this.squidRatiosSpecsList = squidRatiosSpecsList == null ? new ArrayList<>() : squidRatiosSpecsList;
        this.useSBM = useSBM;
        this.userLinFits = userLinFits;
        this.referenceMaterialNameFilter = referenceMaterialNameFilter;
    }

    /**
     * @return the squidSpeciesSpecsList
     */
    public List<SquidSpeciesSpecs> getSquidSpeciesSpecsList() {
        return squidSpeciesSpecsList;
    }

    /**
     * @return the squidSpeciesSpecsList
     */
    public String[] getSquidSpeciesNames() {
        String[] squidSpeciesNames = new String[squidSpeciesSpecsList.size()];
        for (int i = 0; i < squidSpeciesSpecsList.size(); i++) {
            squidSpeciesNames[i] = squidSpeciesSpecsList.get(i).getMassStationSpeciesName();
        }

        return squidSpeciesNames;
    }

    /**
     * @param squidSpeciesSpecsList the squidSpeciesSpecsList to set
     */
    public void setSquidSpeciesSpecsList(List<SquidSpeciesSpecs> squidSpeciesSpecsList) {
        this.squidSpeciesSpecsList = squidSpeciesSpecsList;
    }

    /**
     * @return the squidRatiosSpecsList
     */
    public List<SquidRatiosSpecs> getSquidRatiosSpecsList() {
        return squidRatiosSpecsList;
    }

    /**
     * @param squidRatiosSpecsList the squidRatiosSpecsList to set
     */
    public void setSquidRatiosSpecsList(List<SquidRatiosSpecs> squidRatiosSpecsList) {
        this.squidRatiosSpecsList = squidRatiosSpecsList;
    }

    /**
     * @return the useSBM
     */
    public boolean isUseSBM() {
        return useSBM;
    }

    /**
     * @param useSBM the useSBM to set
     */
    public void setUseSBM(boolean useSBM) {
        this.useSBM = useSBM;
    }

    /**
     * @return the userLinFits
     */
    public boolean isUserLinFits() {
        return userLinFits;
    }

    /**
     * @param userLinFits the userLinFits to set
     */
    public void setUserLinFits(boolean userLinFits) {
        this.userLinFits = userLinFits;
    }

    /**
     * @return the referenceMaterialNameFilter
     */
    public String getReferenceMaterialNameFilter() {
        return referenceMaterialNameFilter;
    }

    /**
     * @param referenceMaterialNameFilter the referenceMaterialNameFilter to set
     */
    public void setReferenceMaterialNameFilter(String referenceMaterialNameFilter) {
        this.referenceMaterialNameFilter = referenceMaterialNameFilter;
    }

}
