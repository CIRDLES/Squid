/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.prawn;

import java.io.File;
import java.util.Locale;

/**
 * @author James F. Bowring
 */
public class PrawnFileFilter extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File f) {
        boolean accept = f.isDirectory();

        if (!accept) {
            String suffix = getSuffix(f);

            if (suffix != null) {
                accept = suffix.equalsIgnoreCase("xml");
            }
        }
        return accept;
    }

    /**
     * @return
     */
    @Override
    public String getDescription() {
        return "Prawn files (*.xml)";
    }

    private String getSuffix(File f) {
        String s = f.getPath(), suffix = null;
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            suffix = s.substring(i + 1).toLowerCase(Locale.US);
        }

        return suffix;
    }
}
