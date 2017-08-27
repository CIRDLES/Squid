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
package org.cirdles.squid.tasks;

import java.util.List;
import java.util.Map;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public interface TaskInterface {

    public String getName();
    
    public void setName(String name);
    
    /**
     *
     * @return
     */
    public List<ExpressionTreeInterface> getTaskExpressionsOrdered();
    
    public void setTaskExpressionsOrdered(List<ExpressionTreeInterface> taskExpressionsOrdered);
    
    /**
     *
     * @param shrimpFractions
     */
    public void evaluateTaskExpressions(List<ShrimpFractionExpressionInterface> shrimpFractions);
    
    /**
     * @return the taskExpressionsEvaluationsPerSpotSet
     */
    public Map<String, SpotSummaryDetails> getTaskExpressionsEvaluationsPerSpotSet();

}
