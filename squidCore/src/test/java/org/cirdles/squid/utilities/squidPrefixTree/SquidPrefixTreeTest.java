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
package org.cirdles.squid.utilities.squidPrefixTree;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * @author Casey Wilson
 */
public class SquidPrefixTreeTest {

    /**
     * Test of getCountOfDups method, of class SquidPrefixTree.
     */
    @Test
    public void testGetCountOfDups() {
        SquidPrefixTree spt = new SquidPrefixTree();
        spt.insert("t.2.1.1");
        spt.prepareStatistics();
        assertEquals(0, spt.getCountOfDups());

        spt = new SquidPrefixTree();
        spt.insert("t.2.1.1-DUP-1");
        assertEquals(0, spt.getCountOfDups());
        spt.prepareStatistics();
        assertEquals(1, spt.getCountOfDups());
        spt.insert("t.2.1.1-DUP-");
        spt.prepareStatistics();
        spt.insert("t.2.1.2-DUP-1");
        spt.insert("t.2.1.1-DUP-2");
        spt.insert("t.2.1.1-DU9P-1");
        spt.prepareStatistics();
        assertEquals(4, spt.getCountOfDups());
    }

    /**
     * Test of prepareStatistics method, of class SquidPrefixTree.
     */
    @Test
    public void testPrepareStatistics() {
        SquidPrefixTree spt = new SquidPrefixTree();
        spt.insert("uag.h-DUP-1");
        assertEquals(0, spt.getCountOfDups());
        assertEquals("/", spt.getStringValue());
        assertEquals(new HashMap<>(), spt.getMapOfScansFrequencies());
        assertEquals(new HashMap<>(), spt.getMapOfSpeciesFrequencies());

        spt.prepareStatistics();

        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        assertEquals(1, spt.getCountOfDups());
        assertEquals("G", spt.findTargetChild(new SquidPrefixTreeNode('g')).getStringValue());
        assertEquals(map, spt.getMapOfScansFrequencies());
        assertEquals(map, spt.getMapOfSpeciesFrequencies());
    }

    /**
     * Test of prettyPrint method, of class SquidPrefixTree.
     */
    @Test
    public void testPrettyPrint() {
        SquidPrefixTree spt = new SquidPrefixTree();
        //System.out.println(spt.prettyPrint());
        String expResult = "";
        String result = spt.prettyPrint().toString();
        assertEquals(expResult, result);

        spt = new SquidPrefixTree();
        spt.insert("a");
        spt.prepareStatistics();
        expResult = "A >>> A = 1\n";
        result = spt.prettyPrint().toString();
        assertEquals(expResult, result);

        spt = new SquidPrefixTree();
        spt.insert("5.3.fsdg-DUP-");
        spt.insert("2345.4");
        spt.insert("5443");
        spt.prepareStatistics();
        result = spt.prettyPrint().toString();

        expResult = "2 >>> 2 = 1\n"
                + "  23 >>> 3 = 1\n"
                + "    234 >>> 4 = 1\n"
                + "      2345 >>> 5 = 1\n"
                + "        2345. >>> . = 1\n"
                + "          2345.4 >>> 4 = 1\n"
                + "5 >>> 5 = 2\n"
                + "  54 >>> 4 = 1\n"
                + "    544 >>> 4 = 1\n"
                + "      5443 >>> 3 = 1\n"
                + "  5. >>> . = 1\n"
                + "    5.3 >>> 3 = 1\n"
                + "      5.3. >>> . = 1\n"
                + "        5.3.F >>> F = 1\n"
                + "          5.3.FS >>> S = 1\n"
                + "            5.3.FSD >>> D = 1\n"
                + "              5.3.FSDG >>> G = 1\n"
                + "                5.3.FSDG-DUP- >>> -DUP- = 1\n"
                + "";

        assertEquals(expResult, result);

    }

    /**
     * Test of getMapOfScansFrequencies method, of class SquidPrefixTree.
     */
    @Test
    public void testGetMapOfScansFrequencies() {
        SquidPrefixTree spt = new SquidPrefixTree();
        HashMap<Integer, Integer> map = new HashMap<>();
        spt.insert("5.3.fsdg-DUP-");
        spt.insert("2345.4");
        spt.insert("2345.45");
        spt.insert("5443");
        spt.prepareStatistics();
        map.put(0, 4);
        assertEquals(map, spt.getMapOfScansFrequencies());
    }

    /**
     * Test of getMapOfSpeciesFrequencies method, of class SquidPrefixTree.
     */
    @Test
    public void testGetMapOfSpeciesFrequencies() {
        SquidPrefixTree spt = new SquidPrefixTree();
        HashMap<Integer, Integer> map = new HashMap<>();
        spt.insert("t.2.1.1");
        spt.insert("t.2.1.1.2");

        spt.prepareStatistics();
        map.put(0, 2);
        assertEquals(map, spt.getMapOfSpeciesFrequencies());
    }

}