/*
 * SquidUserPreferences.java
 *
 * Copyright 2017 James F. Bowring and CIRDLES.org.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.utilities.stateUtilities;

import java.io.Serializable;

/**
 *
 * @author James F. Bowring
 */
public class SquidUserPreferences implements Serializable {

    // NOTE: java.utils.Properties should be considered for use here JFB
    // Class variables
    private static final long serialVersionUID = -936841271782482788L;

    // instance variables
    private String geochronUserName;
    private String geochronPassWord;

    /**
     * Creates a new instance of ReduxPreferences
     */
    public SquidUserPreferences() {

        this.geochronUserName = "username";
        this.geochronPassWord = "longpassword";
    }

    /**
     * @return the geochronUserName
     */
    public String getGeochronUserName() {
        if (geochronUserName == null) {
            geochronUserName = "username";
        }
        return geochronUserName;
    }

    /**
     * @param geochronUserName the geochronUserName to set
     */
    public void setGeochronUserName(String geochronUserName) {
        this.geochronUserName = geochronUserName;
    }

    /**
     * @return the geochronPassWord
     */
    public String getGeochronPassWord() {
        if (geochronPassWord == null) {
            geochronPassWord = "longpassword";
        }
        return geochronPassWord;
    }

    /**
     * @param geochronPassWord the geochronPassWord to set
     */
    public void setGeochronPassWord(String geochronPassWord) {
        this.geochronPassWord = geochronPassWord;
    }
}
