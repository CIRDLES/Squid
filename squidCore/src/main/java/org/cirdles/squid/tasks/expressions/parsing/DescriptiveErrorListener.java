/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.parsing;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * @author James F. Bowring
 */
public class DescriptiveErrorListener extends BaseErrorListener {

    // public static DescriptiveErrorListener INSTANCE = new DescriptiveErrorListener();

    private final boolean reportSyntaxErrors;
    private String syntaxErrors;

    public DescriptiveErrorListener(boolean reportSyntaxErrors) {
        syntaxErrors = "";
        this.reportSyntaxErrors = reportSyntaxErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e) {
        if (!reportSyntaxErrors) {
            return;
        }

//        String sourceName = recognizer.getInputStream().getSourceName();
//        if (!sourceName.isEmpty()) {
//            sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
//        }

//        System.err.println("CUSTOM:  "  + "line " + line + ":" + charPositionInLine + " " + msg);
        syntaxErrors = msg;
    }

    /**
     * @return the syntaxErrors
     */
    public String getSyntaxErrors() {
        return syntaxErrors;
    }
}