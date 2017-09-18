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
package org.cirdles.squid.tasks.builtinTasks;

import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus1;

/**
 *
 * @author James F. Bowring
 */
public class Squid3ExampleTaskexperiment extends Task {

    private static final long serialVersionUID = -914383068154435665L;

    /**
     *
     */
    public Squid3ExampleTaskexperiment() {
        super("Squid3ExampleTaskExperiemnt");

        this.type = "Geochron";
        this.description = "Example Squid3 Task for 10-peak zircon.";
        this.authorName = "Squid3 Team";
        this.labName = "GA";
        this.provenance = "Builtin task.";
        this.dateRevised = 0l;

        ratioNames.add("204/206");
        ratioNames.add("207/206");
        ratioNames.add("208/206");
        ratioNames.add("238/196");
        ratioNames.add("206/238");
        ratioNames.add("254/238");
        ratioNames.add("248/254");
        ratioNames.add("206/270");
        ratioNames.add("270/254");
        ratioNames.add("206/254");
        ratioNames.add("238/206");

        taskExpressionsOrdered.add(
                new Expression(new SquidExpressionMinus1(), SquidExpressionMinus1.excelExpressionString));

    }
}
