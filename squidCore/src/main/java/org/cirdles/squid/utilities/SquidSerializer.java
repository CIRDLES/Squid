/*
 * SquidSerializer.java
 *
 * Copyright 2017 CIRDLES.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.utilities;

import java.io.*;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.exceptions.SquidMessageDialog;

/**
 *
 * @author James F. Bowring
 */
public final class SquidSerializer {

    /**
     * Creates a new instance of ETSerializer
     */
    public SquidSerializer() {
    }

    /**
     *
     * @param o
     * @param filename
     * @throws org.earthtime.exceptions.ETException
     */
    public static void SerializeObjectToFile(Object o, String filename) throws SquidException {
        try {
            // Serialize to a file
            FileOutputStream out = new FileOutputStream(filename);
            try (ObjectOutputStream s = new ObjectOutputStream(out)) {
                s.writeObject(o);
                s.flush();
            }

        } catch (FileNotFoundException ex) {
            throw new SquidException(null, "Cannot serialize to: " + filename);
        } catch (IOException ex) {
            throw new SquidException(null, "Cannot serialize to: " + filename);
        }
    }

    /**
     *
     * @param filename
     * @return
     */
    public static Object GetSerializedObjectFromFile(String filename) {
        FileInputStream in;
        ObjectInputStream s;
        Object o = null;

        try {
            in = new FileInputStream(filename);
            s = new ObjectInputStream(in);
            o = s.readObject();
        } catch (FileNotFoundException ex) {
            if (!filename.endsWith(SquidPersistentState.SQUID_PERSISTENT_STATE_FILE_NAME)) {
                SquidMessageDialog.showWarningDialog(
                    "The file you are attempting to open does not exist:\n"
                            + " " + filename);
            }
        } catch (IOException | ClassNotFoundException ex) {
            SquidMessageDialog.showWarningDialog(
                    "The file you are attempting to open is not compatible with this version of Squid3.");
        }

        return o;
    }

}
