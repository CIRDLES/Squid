/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.SquidReportTable.utilities;

import java.util.Comparator;

/**
 *
 * @author ryanb
 */
public class StringComparer implements Comparator<String> {

    @Override
    public int compare(String s1, String s2) {
        s1 = s1.replaceAll(" ", "");
        s2 = s2.replaceAll(" ", "");
        int retVal = 0;
        boolean done = false;
        int length;
        if (s1.length() > s2.length()) {
            length = s1.length();
        } else {
            length = s2.length();
        }
        for (int i = 0; i < length && !done; i++) {
            boolean s1notLongEnough = i >= s1.length();
            boolean s2notLongEnough = i >= s2.length();
            if (s1notLongEnough && !s2notLongEnough) {
                retVal = -1;
                done = true;
            }
            if (!s1notLongEnough && s2notLongEnough) {
                retVal = 1;
                done = true;
            }
            if (s1notLongEnough && s2notLongEnough) {
                done = true;
            }
            if (!done && !s1notLongEnough && !s2notLongEnough) {
                if (isNumber(s1.substring(i)) && isNumber(s2.substring(i))) {
                    Double arithmetic = Double.parseDouble(s1.substring(i)) - Double.parseDouble(s2.substring(i));
                    if (arithmetic < 0) {
                        retVal = -1;
                        done = true;
                    } else if (arithmetic > 0) {
                        retVal = 1;
                        done = true;
                    }
                } else {
                    char c1 = s1.charAt(i);
                    char c2 = s2.charAt(i);
                    int arithmetic = c1 - c2;
                    if (arithmetic > 0) {
                        retVal = 1;
                        done = true;
                    } else if (arithmetic < 0) {
                        retVal = -1;
                        done = true;
                    }
                }
            }
        }
        return retVal;
    }

    private boolean isNumber(String s) {
        boolean retVal = true;
        char[] sequence = s.toCharArray();
        int periodCount = 0;
        if (sequence.length > 0 && sequence[0] == '-' && sequence.length > 1) {
            sequence[0] = '0';
        }
        for (int i = 0; i < sequence.length && retVal; i++) {
            if (!Character.isDigit(sequence[i]) && sequence[i] != '.') {
                retVal = false;
            }
            if (sequence[i] == '.') {
                periodCount++;
            }
            if (periodCount > 1) {
                retVal = false;
            }
        }
        return retVal;
    }

}
