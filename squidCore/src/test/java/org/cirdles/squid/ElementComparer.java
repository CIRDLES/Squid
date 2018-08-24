package org.cirdles.squid;

import org.jdom2.Element;

import java.util.List;

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
