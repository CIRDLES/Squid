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
package org.cirdles.squid.utilities.fileUtilities;

import java.io.File;

/**
 * @author ryanb
 */
public class FileNameFixer {

    public static String fixFileName(String name) {
        name = name.replaceAll(" ", "_");
        name = name.replaceAll("\\^", "");
        name = name.replaceAll("\\n", "");
        name = name.replaceAll("\\*", "");
        name = name.replaceAll("\\[", "");
        name = name.replaceAll("]", "");
        name = name.replaceAll("\\(", "");
        name = name.replaceAll("\\)", "");
        name = name.replaceAll("!", "");
        name = name.replaceAll("#", "");
        name = name.replaceAll("&", "");
        name = name.replaceAll("@", "");
        name = name.replaceAll("\\+", "");
        name = name.replaceAll("/", "");
        name = name.replaceAll("\\{", "");
        name = name.replaceAll("}", "");
        name = name.replaceAll(":", "");
        name = name.replaceAll("\\?", "");
        name = name.replaceAll(">", "");
        name = name.replaceAll("<", "");
        name = name.replaceAll("%", "");
        name = name.replaceAll("'", "");
        name = name.replaceAll("\"", "");
        name = name.replaceAll("~", "");
        name = name.replaceAll("`", "");
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