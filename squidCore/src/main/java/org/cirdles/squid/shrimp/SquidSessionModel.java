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

import java.io.Serializable;
import java.util.*;

/**
 * @author James F. Bowring
 */
public class SquidSessionModel implements Serializable {

    private static final long serialVersionUID = -2969849117024998190L;

    private List<SquidSpeciesModel> squidSpeciesModelList;
    private List<SquidRatiosModel> squidRatiosModelList;
    private boolean useSBM;
    private boolean userLinFits;
    private int indexOfBackgroundSpecies;
    private String referenceMaterialNameFilter;
    private String concentrationReferenceMaterialNameFilter;
    private Map<String, Integer> filtersForUnknownNames;

    public SquidSessionModel(
            List<SquidSpeciesModel> squidSpeciesSpecsList,
            List<SquidRatiosModel> squidRatiosSpecsList,
            boolean useSBM, boolean userLinFits,
            int indexOfBackgroundSpecies,
            String referenceMaterialNameFilter,
            String concentrationReferenceMaterialNameFilter,
            Map<String, Integer> filtersForUnknownNames) {
        this.squidSpeciesModelList = squidSpeciesSpecsList == null ? new ArrayList<>() : squidSpeciesSpecsList;
        this.squidRatiosModelList = squidRatiosSpecsList == null ? new ArrayList<>() : squidRatiosSpecsList;
        this.useSBM = useSBM;
        this.userLinFits = userLinFits;
        this.indexOfBackgroundSpecies = indexOfBackgroundSpecies;
        this.referenceMaterialNameFilter = referenceMaterialNameFilter;
        this.concentrationReferenceMaterialNameFilter = concentrationReferenceMaterialNameFilter;
        this.filtersForUnknownNames = filtersForUnknownNames;
    }

    public boolean updateFields(
            List<SquidSpeciesModel> squidSpeciesSpecsList,
            List<SquidRatiosModel> squidRatiosSpecsList,
            boolean useSBM,
            boolean userLinFits,
            int indexOfBackgroundSpecies,
            String referenceMaterialNameFilter,
            String concentrationReferenceMaterialNameFilter,
            Map<String, Integer> filtersForUnknownNames) {

        boolean retVal = false;

        retVal = !(this.squidSpeciesModelList.containsAll(squidSpeciesSpecsList) && squidSpeciesSpecsList.containsAll(this.squidSpeciesModelList));
        if (!retVal) {
            retVal = !(this.squidRatiosModelList.containsAll(squidRatiosSpecsList) && squidRatiosSpecsList.containsAll(this.squidRatiosModelList));
        }
        if (!retVal) {
            retVal = !(this.useSBM == useSBM);
        }
        if (!retVal) {
            retVal = !(this.userLinFits == userLinFits);
        }
        if (!retVal) {
            retVal = !(this.indexOfBackgroundSpecies == indexOfBackgroundSpecies);
        }
        if (!retVal) {
            retVal = !(this.referenceMaterialNameFilter.compareToIgnoreCase(referenceMaterialNameFilter) == 0);
        }
        if (!retVal) {
            retVal = !(this.concentrationReferenceMaterialNameFilter.compareToIgnoreCase(concentrationReferenceMaterialNameFilter) == 0);
        }
        if (!retVal) {
            retVal = !(this.filtersForUnknownNames.keySet().equals(filtersForUnknownNames.keySet()));
        }

        return retVal;
    }

    public SortedSet<SquidRatiosModel> produceRatiosCopySortedSet() {
        SortedSet<SquidRatiosModel> isotopicRatiosII = new TreeSet<>();
        for (SquidRatiosModel srm : squidRatiosModelList) {
            isotopicRatiosII.add(srm.copy());
        }
        return isotopicRatiosII;
    }

    /**
     * @return the squidSpeciesModelList
     */
    public List<SquidSpeciesModel> getSquidSpeciesModelList() {
        return squidSpeciesModelList;
    }

    /**
     * @return the squidSpeciesModelList
     */
    public String[] getSquidSpeciesMassStationNames() {
        String[] squidSpeciesNames = new String[squidSpeciesModelList.size()];
        for (int i = 0; i < squidSpeciesModelList.size(); i++) {
            squidSpeciesNames[i] = squidSpeciesModelList.get(i).getMassStationSpeciesName();
        }

        return squidSpeciesNames;
    }

    /**
     * @param squidSpeciesModelList the squidSpeciesModelList to set
     */
    public void setSquidSpeciesModelList(List<SquidSpeciesModel> squidSpeciesModelList) {
        this.squidSpeciesModelList = squidSpeciesModelList;
    }

    /**
     * @return the squidRatiosModelList
     */
    public List<SquidRatiosModel> getSquidRatiosModelList() {
        return squidRatiosModelList;
    }

    /**
     * @param squidRatiosModelList the squidRatiosModelList to set
     */
    public void setSquidRatiosModelList(List<SquidRatiosModel> squidRatiosModelList) {
        this.squidRatiosModelList = squidRatiosModelList;
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
     * @return the indexOfBackgroundSpecies
     */
    public int getIndexOfBackgroundSpecies() {
        return indexOfBackgroundSpecies;
    }

    /**
     * @param indexOfBackgroundSpecies the indexOfBackgroundSpecies to set
     */
    public void setIndexOfBackgroundSpecies(int indexOfBackgroundSpecies) {
        this.indexOfBackgroundSpecies = indexOfBackgroundSpecies;
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

    /**
     * @return
     */
    public String getConcentrationReferenceMaterialNameFilter() {
        return concentrationReferenceMaterialNameFilter;
    }

    /**
     * @param concentrationReferenceMaterialNameFilter
     */
    public void setConcentrationReferenceMaterialNameFilter(String concentrationReferenceMaterialNameFilter) {
        this.concentrationReferenceMaterialNameFilter = concentrationReferenceMaterialNameFilter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSquidSpeciesModelList(), getSquidRatiosModelList(), isUseSBM(), isUserLinFits(), getIndexOfBackgroundSpecies(), getReferenceMaterialNameFilter(), getConcentrationReferenceMaterialNameFilter(), filtersForUnknownNames);
    }
}