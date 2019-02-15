/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.utilities.conversionUtilities;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class CloningUtilities {

    /**
     *
     * @param myArray
     * @return
     */
    public static double[][] clone2dArray(double[][] myArray) {
        double[][] arrayCopy = new double[myArray.length][];
        for (int i = 0; i < myArray.length; i++) {
            arrayCopy[i] = myArray[i].clone();
        }

        return arrayCopy;
    }

    /**
     *
     * @param myArray
     * @return
     */
    public static int[][] clone2dArray(int[][] myArray) {
        int[][] arrayCopy = new int[myArray.length][];
        for (int i = 0; i < myArray.length; i++) {
            arrayCopy[i] = myArray[i].clone();
        }

        return arrayCopy;
    }

    /**
     *
     * @param myArray
     * @return
     */
    public static boolean[][] clone2dArray(boolean[][] myArray) {
        boolean[][] arrayCopy = new boolean[myArray.length][];
        for (int i = 0; i < myArray.length; i++) {
            arrayCopy[i] = myArray[i].clone();
        }

        return arrayCopy;
    }

    public static String[][] clone2dArray(String[][] myArray) {
        String[][] arrayCopy = new String[myArray.length][];
        for (int i = 0; i < myArray.length; i++) {
            arrayCopy[i] = myArray[i].clone();
        }

        return arrayCopy;
    }
}
