/* 
 * Copyright 2018 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid;

import java.util.List;
import org.jdom2.Element;

/**
 * compares 2 elements, if there is any difference then it will return false (does not compare mixed text in complex types)
 * 
 * @author ryanb
 */
public class ElementComparer {

    public static boolean compareElements(Element firstElement, Element secondElement) {
        boolean returnValue = true;
        List<Element> firstList = firstElement.getChildren();
        List<Element> secondList = secondElement.getChildren();

        if (firstList.size() == secondList.size()) {
            if (!firstList.isEmpty()) {
                while (!firstList.isEmpty() && returnValue) {
                    Element firstChild = firstList.remove(0);
                    Element secondChild = secondList.remove(0);
                    if (compareElements(firstChild, secondChild) == false) {
                        returnValue = false;
                    }
                }
            } else {
                if (!(firstElement.getText().equals(secondElement.getText()))) {
                    returnValue = false;
                }
            }
        } else {
            returnValue = false;
        }

        return returnValue;
    }
}
