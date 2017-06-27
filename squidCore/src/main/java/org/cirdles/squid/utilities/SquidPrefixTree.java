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
package org.cirdles.squid.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author James F. Bowring
 */
public class SquidPrefixTree {

    private final static Character ROOT = "/".toCharArray()[0];
    private final static Character LEAF = "\\".toCharArray()[0];

    private SquidPrefixTree parent;
    private List<SquidPrefixTree> children;
    private Character charValue;
    private String stringValue;
    private int countOfSpecies;
    private int countOfScans;
    private int countOfDups;
    private int countOfLeaves;
    private Map<Integer, Integer> mapOfSpeciesFrequencies;
    private Map<Integer, Integer> mapOfScansFrequencies;

    public SquidPrefixTree() {
        this(ROOT);
    }

    public SquidPrefixTree(Character value) {
        this.parent = null;
        this.children = new ArrayList<>();
        this.charValue = value;
        this.stringValue = value.toString().toUpperCase(Locale.US);
        this.countOfSpecies = 0;
        this.countOfScans = 0;
        this.countOfDups = 0;
        this.countOfLeaves = 1;
        mapOfSpeciesFrequencies = new HashMap<>();
        mapOfScansFrequencies = new HashMap<>();
    }

    public SquidPrefixTree insert(String myWord) {
        SquidPrefixTree target = this;
        String word = myWord.toUpperCase(Locale.US);
        for (int i = 0; i < word.length(); i++) {
            Character letter = word.charAt(i);
            target = target.findTargetChild(letter);
            target.stringValue = word.substring(0, i + 1);

            // add special leaf condition if last letter
            if (i == (word.length() - 1)) {
                SquidPrefixTree leaf = new SquidPrefixTree(LEAF);
                leaf.parent = target;
                target.children.add(leaf);
                if (target.stringValue.toUpperCase(Locale.US).contains("-DUP-")) {
                    leaf.countOfDups = 1;
                }
            }

        }

        return target;
    }

    public SquidPrefixTree findTargetChild(Character value) {
        SquidPrefixTree tree = null;
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).charValue.compareTo(value) == 0) {
                tree = children.get(i);
            }
        }

        if (tree == null) {
            tree = new SquidPrefixTree(value);
            parent = this;
            children.add(tree);
        }

        return tree;
    }

    public SquidPrefixTree findPrefix(String prefix) {
        SquidPrefixTree target = this;
        for (int i = 0; i < prefix.length(); i++) {
            Character letter = prefix.charAt(i);
            target = target.findTargetChild(letter);
        }

        return target;
    }

    public void prepareStatistics() {
        sort();
        countAnalysisLeaves();
        countSpeciesFrequencies();
        countScansFrequencies();
        countDups();
    }

    private void sort() {
        sortChildren(children);
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).hasChildren()) {
                sortChildren(children.get(i).getChildren());
            }
        }
    }

    private void sortChildren(List<SquidPrefixTree> children) {
        Collections.sort(children, (SquidPrefixTree pt1, SquidPrefixTree pt2)
                -> (pt1.getCharValue().compareTo(pt2.charValue)));
    }

    private int countAnalysisLeaves() {
        int retVal = 0;
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).charValue.compareTo(LEAF) == 0) {
                retVal++;
            } else {
                int childCountOfLeaves = children.get(i).countAnalysisLeaves();
                // sum leaves recursively
                retVal += childCountOfLeaves;
                // set local count of leaves below this node
                children.get(i).setCountOfLeaves(childCountOfLeaves);
                // this will get overwritten except at root
                countOfLeaves = retVal;
            }
        }
        return retVal;
    }

    private Map<Integer, Integer> countSpeciesFrequencies() {

        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).charValue.compareTo(LEAF) == 0) {
                int childCountOfSpecies = children.get(i).countOfSpecies;
                mapOfSpeciesFrequencies.put(childCountOfSpecies, 1);
            } else {
                Map<Integer, Integer> speciesMap = children.get(i).countSpeciesFrequencies();
                updateFrequencyMap(mapOfSpeciesFrequencies, speciesMap);
            }
        }
        return mapOfSpeciesFrequencies;
    }

    private Map<Integer, Integer> countScansFrequencies() {

        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).charValue.compareTo(LEAF) == 0) {
                int childCountOfScans = children.get(i).countOfScans;
                mapOfScansFrequencies.put(childCountOfScans, 1);
            } else {
                Map<Integer, Integer> scansMap = children.get(i).countScansFrequencies();
                updateFrequencyMap(mapOfScansFrequencies, scansMap);
            }
        }
        return mapOfScansFrequencies;
    }

    private int countDups() {
        int retVal = 0;
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).charValue.compareTo(LEAF) == 0) {
                retVal += children.get(i).countOfDups;
            } else {
                int childCountOfDups = children.get(i).countDups();
                // sum leaves recursively
                retVal += childCountOfDups;
                // set local count of leaves below this node
                children.get(i).setCountOfDups(childCountOfDups);
                // this will get overwritten except at root
                countOfDups = retVal;
            }
        }
        return retVal;
    }

    private void updateFrequencyMap(Map<Integer, Integer> targetMap, Map<Integer, Integer> sourceMap) {
        for (Map.Entry<Integer, Integer> entry : sourceMap.entrySet()) {
            int key = entry.getKey();
            int count = entry.getValue();
            if (targetMap.containsKey(key)) {
                targetMap.put(key, targetMap.get(key) + count);
            } else {
                targetMap.put(key, count);
            }
        }
    }

    public String buildSummaryDataString() {
        // build species and scans count string        
        StringBuilder speciesCountBuffer = new StringBuilder();
        for (Integer count : mapOfSpeciesFrequencies.keySet()) {
            speciesCountBuffer.append("[" + String.format("%1$ 2d", count) + " in " + String.format("%1$ 3d", mapOfSpeciesFrequencies.get(count)) + "]");
        }
        String speciesCounts = speciesCountBuffer.toString();

        StringBuilder scansCountsBuffer = new StringBuilder();
        for (Integer count : mapOfScansFrequencies.keySet()) {
            scansCountsBuffer.append("[" + String.format("%1$ 2d", count) + " in " + String.format("%1$ 3d", mapOfScansFrequencies.get(count)) + "]");
        }
        String scansCounts = scansCountsBuffer.toString();

        String summary = " Analyses=" + String.format("%1$ 3d", countOfLeaves)
                + "; Dups=" + String.format("%1$ 3d", countOfDups)
                + "; Species:" + speciesCounts
                + "; Scans:" + scansCounts;

        return summary;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public boolean isleaf() {
        return charValue.compareTo(LEAF) == 0;
    }

    /**
     * @return the parent
     */
    public SquidPrefixTree getParent() {
        return parent;
    }

    /**
     * @return the children
     */
    public List<SquidPrefixTree> getChildren() {
        return children;
    }

    /**
     * @return the charValue
     */
    public Character getCharValue() {
        return charValue;
    }

    /**
     * @return the stringValue
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * @return the countOfSpecies
     */
    public int getCountOfSpecies() {
        return countOfSpecies;
    }

    /**
     * @param countOfSpecies the countOfSpecies to set
     */
    public void setCountOfSpecies(int countOfSpecies) {
        this.countOfSpecies = countOfSpecies;
    }

    /**
     * @return the countOfScans
     */
    public int getCountOfScans() {
        return countOfScans;
    }

    /**
     * @param countOfScans the countOfScans to set
     */
    public void setCountOfScans(int countOfScans) {
        this.countOfScans = countOfScans;
    }

    /**
     * @return the countOfDups
     */
    public int getCountOfDups() {
        return countOfDups;
    }

    /**
     * @param countOfDups the countOfDups to set
     */
    public void setCountOfDups(int countOfDups) {
        this.countOfDups = countOfDups;
    }

    /**
     * @return the countOfLeaves
     */
    public int getCountOfLeaves() {
        return countOfLeaves;
    }

    /**
     * @param countOfLeaves the countOfLeaves to set
     */
    public void setCountOfLeaves(int countOfLeaves) {
        this.countOfLeaves = countOfLeaves;
    }

    /**
     * @return the mapOfSpeciesFrequencies
     */
    public Map<Integer, Integer> getMapOfSpeciesFrequencies() {
        return mapOfSpeciesFrequencies;
    }

    /**
     * @return the mapOfScansFrequencies
     */
    public Map<Integer, Integer> getMapOfScansFrequencies() {
        return mapOfScansFrequencies;
    }

    public void prettyPrint(int depth) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).charValue.compareTo(LEAF) != 0) {
                System.out.println(children.get(i).stringValue + "  >>> " + children.get(i).charValue + "  = " + children.get(i).countAnalysisLeaves());
                for (int j = 0; j < depth; j++) {
                    System.out.print("   ");
                }
                children.get(i).prettyPrint(depth + 1);
            }
        }

    }

    public static void main(String[] args) {
        SquidPrefixTree root = new SquidPrefixTree();
        root.insert("uaf.g");
        root.insert("uaf.h");
        root.insert("ua");
        root.insert("abcd");

        root.countAnalysisLeaves();
        root.prettyPrint(0);
    }

}
