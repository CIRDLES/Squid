/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.algorithms.weightedMeans;

/**
 * @author James F. Bowring
 */
public class DeletePointResults {

    private double[] y2;
    private double[][] sigRho2;
    private double[] x2;

    /**
     *
     */
    public DeletePointResults() {
        y2 = new double[0];
        sigRho2 = new double[0][0];
        x2 = new double[0];
    }

    /**
     * @return the y2
     */
    public double[] getY2() {
        return y2.clone();
    }

    /**
     * @param y2 the y2 to set
     */
    public void setY2(double[] y2) {
        this.y2 = y2.clone();
    }

    /**
     * @return the sigRho2
     */
    public double[][] getSigRho2() {
        return sigRho2.clone();
    }

    /**
     * @param sigRho2 the sigRho2 to set
     */
    public void setSigRho2(double[][] sigRho2) {
        this.sigRho2 = sigRho2.clone();
    }

    /**
     * @return the x2
     */
    public double[] getX2() {
        return x2.clone();
    }

    /**
     * @param x2 the x2 to set
     */
    public void setX2(double[] x2) {
        this.x2 = x2.clone();
    }

}