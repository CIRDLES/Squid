/*
 * Copyright 2006 - 2017 James F. Bowring, CIRDLES.org.
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
 * @author James F. Bowring
 */
public enum UThBearingEnum {

    U("U", "Uranium"), T("Th", "Thorium"), N("No", "No");

    private final String name;
    private final String description;

    private UThBearingEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static UThBearingEnum getByName(String name) {
        for (UThBearingEnum uth : UThBearingEnum.values()) {
            if (uth.name.compareTo(name) == 0) {
                return uth;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.description;
    }

}