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

/**
 * @author Casey Wilson
 */
public class SquidPrefixTreeNode {

    private String value;
    private boolean singleCharacter;

    private SquidPrefixTreeNode() {
    }

    public SquidPrefixTreeNode(Character character) {
        this.value = String.valueOf(character);
        this.singleCharacter = value.length() == 1;
    }

    public SquidPrefixTreeNode(String string) {
        this.value = string;
        this.singleCharacter = value.length() <= 1;
    }

    /**
     * Returns the string value of the node
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the singleCharacter
     */
    public boolean isSingleCharacter() {
        return singleCharacter;
    }

    /**
     * @param singleCharacter the singleCharacter to set
     */
    public void setSingleCharacter(boolean singleCharacter) {
        this.singleCharacter = singleCharacter;
    }

}