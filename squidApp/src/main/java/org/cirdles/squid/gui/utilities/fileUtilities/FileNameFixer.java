/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.utilities.fileUtilities;

import java.io.File;

/**
 *
 * @author ryanb
 */
public class FileNameFixer {
    
    public static String fixFileName(String name) {
        name = name.replaceAll(" ", "_");
        name = name.replaceAll("\\^", "");
        name = name.replaceAll("\\n", "");
        name = name.replaceAll("\\*", "");
        name = name.replaceAll("\\[", "");
        name = name.replaceAll("\\]", "");
        name = name.replaceAll("\\(", "");
        name = name.replaceAll("\\)", "");
        name = name.replaceAll("\\!", "");
        name = name.replaceAll("\\#", "");
        name = name.replaceAll("\\&", "");
        name = name.replaceAll("\\@", "");
        name = name.replaceAll("\\+", "");
        name = name.replaceAll("/", "");
        name = name.replaceAll("\\{", "");
        name = name.replaceAll("\\}", "");
        name = name.replaceAll("\\:", "");
        name = name.replaceAll("\\?", "");
        name = name.replaceAll("\\>", "");
        name = name.replaceAll("\\<", "");
        name = name.replaceAll("\\%", "");
        name = name.replaceAll("\\\'", "");
        name = name.replaceAll("\\\"", "");
        name = name.replaceAll("\\~", "");
        name = name.replaceAll("\\`", "");
        name = name.replaceAll("=", "");
        while (name.contains("__")) {
            name = name.replaceAll("__", "_");
        }
        
        return name;
    }
    
    public static String fixFileName(File file) {
        return fixFileName(file.toString());
    }
}
