/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.util;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ryanb
 */
public class DateHelper {

    public static String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE;
        return dtf.format(LocalDateTime.now());
    }

}
