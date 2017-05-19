/*
 * SquidException.java
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
package org.cirdles.squid.exceptions;

/**
 *
 * @author James F. Bowring
 */
public class SquidException extends Exception {

    private SquidException() {
    }

    public SquidException(String message) {
        super(message);
    }

    public SquidException(Throwable cause) {
        super(cause);
    }

    public SquidException(String message, Throwable cause) {
        super(message, cause);
    }

    public SquidException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
