/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.builtinExpressions;

import java.util.ArrayList;
import java.util.List;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNTS_PERSEC_4_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNT_4_6_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_CONCEN_PPM_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_206_238;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_206_238_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_208_232;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_208_232_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.U_CONCEN_PPM_RM;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class BuiltInExpressionsAliased {

    public static final List<String> BUILTIN_EXPRESSION_ALIASEDNAMES = new ArrayList<>();

    static {
        // refmat 
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + OVER_COUNTS_PERSEC_4_8);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + OVER_COUNTS_PERSEC_4_8);

        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + OVER_COUNT_4_6_8);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + OVER_COUNT_4_6_8);

        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + TH_U_EXP_RM);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + TH_U_EXP_RM);

        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT);

        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + TH_CONCEN_PPM_RM);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + TH_CONCEN_PPM_RM);

        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + TOTAL_206_238_RM);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + TOTAL_206_238_RM);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB8CORR + TOTAL_206_238_RM);

        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + TOTAL_208_232_RM);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + TOTAL_208_232_RM);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB8CORR + TOTAL_208_232_RM);

        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + U_CONCEN_PPM_RM);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + U_CONCEN_PPM_RM);

        // unknowns
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + TH_U_EXP);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + TH_U_EXP);

        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + TOTAL_206_238);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + TOTAL_206_238);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB8CORR + TOTAL_206_238);

        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB4CORR + TOTAL_208_232);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB7CORR + TOTAL_208_232);
        BUILTIN_EXPRESSION_ALIASEDNAMES.add(PB8CORR + TOTAL_208_232);
    }
}
