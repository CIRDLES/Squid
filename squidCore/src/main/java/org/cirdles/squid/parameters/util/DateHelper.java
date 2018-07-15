/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.util;


import java.util.GregorianCalendar;
/**
 *
 * @author ryanb
 */
public class DateHelper {
    
    public static String getCurrentDate() {
        return GregorianCalendar.getInstance().get(GregorianCalendar.MONTH) + 1 //months start at 0
                + "/" + GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH)
                + "/" + GregorianCalendar.getInstance().get(GregorianCalendar.YEAR);
    }
    
}
