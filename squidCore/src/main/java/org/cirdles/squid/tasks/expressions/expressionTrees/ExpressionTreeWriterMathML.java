/* 
 * Copyright 2006-2017 CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.expressionTrees;

import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;

/**
 * @see http://www.xmlmind.com/tutorials/MathML/
 * @see http://rypress.com/tutorials/mathml/advanced-formatting
 * @author James F. Bowring
 */
public class ExpressionTreeWriterMathML {

    /**
     *
     * @param expression
     * @return
     */
    public static StringBuilder toStringBuilderMathML(ExpressionTreeInterface expression) {
        //https://www.mathjax.org/cdn-shutting-down/
        /**
         * We recommend cdnjs which also uses CloudFlare for delivery (and on
         * the higher “enterprise” level!). We have been in touch with cdnjs’s
         * maintainers and will help push future MathJax releases to cdnjs.
         *
         * For example, if you have been using the latest MathJax version
         * (v2.7.0) change
         *
         * <script type="text/javascript" async
         * src="https://cdn.mathjax.org/mathjax/2.7-latest/MathJax.js?...">
         * </script>
         * to
         *
         * <script type="text/javascript" async
         * src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?...">
         * </script>
         * [Updated 2017/04/25] Other free CDN providers include
         *
         * rawgit, e.g.,
         * https://cdn.rawgit.com/mathjax/MathJax/2.7.1/MathJax.js. jsdelivr
         * plans to provide a (functional) copy of MathJax in the future.
         *
         */
        StringBuilder fileContents = new StringBuilder();

        if (expression == null) {
            fileContents.append("Expression not valid.");
        } else {
            fileContents.append(
                    "<!DOCTYPE html>\n"
                    + "<html>\n"
                    + "    <head>\n"
                    + "        <title>" + expression.getName() + "</title>\n"
                    + "        <meta charset=\"UTF-8\"/>\n"
//                    + "        <script type=\"text/javascript\" async\n"
//                    + "                src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-MML-AM_SVG\">\n"
//                    + "        </script>\n"
                    + "        <script type=\"text/javascript\" async\n"
                    + "                src=\"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.2/MathJax.js?config=TeX-MML-AM_SVG\">\n"
                    + "        </script>\n"
                    + "    </head>\n"
                    + "    <body>\n"
                    + "        <math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">\n"
                    + "        <mstyle  mathsize='100%'>\n"
            );

//            fileContents.append(
//                    "<mtext mathcolor='#cc0000'>[" + expression.getName() + "] = </mtext>\n"
//            );
            fileContents.append(
                    "<mspace depth=\"0.5ex\" height=\"0.5ex\" width=\"1ex\"/>\n"
            );

            fileContents.append("<mrow>\n");
            fileContents.append(expression.toStringMathML());
            fileContents.append("</mrow>\n");

            fileContents.append(
                    "          </mstyle>\n"
                    + "        </math>\n"
                    + "    </body>\n"
                    + "</html>");
        }

        return fileContents;

    }

    /**
     *
     * @param expression
     * @throws IOException
     */
    public static void writeExpressionToFileHTML(ExpressionTreeInterface expression)
            throws IOException {
        File expFile = new File(expression.getName() + ".html");
        Files.write(
                expFile.toPath(),
                toStringBuilderMathML(expression).toString().getBytes(UTF_8));

    }

}
