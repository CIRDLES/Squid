/*
 * Copyright 2021 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.projects;

import org.cirdles.squid.parameters.parameterModels.ParametersModel;

/**
 * @author bowring
 */
public interface Squid3ProjectParametersAPI {

    /**
     * @return the commonPbModel
     */
    ParametersModel getCommonPbModel();

    /**
     * @return the concentrationReferenceMaterialModel
     */
    ParametersModel getConcentrationReferenceMaterialModel();

    /**
     * @return the physicalConstantsModel
     */
    ParametersModel getPhysicalConstantsModel();

    /**
     * @return the referenceMaterialModel
     */
    ParametersModel getReferenceMaterialModel();

    /**
     * @param commonPbModel the commonPbModel to set
     */
    void setCommonPbModel(ParametersModel commonPbModel);

    void setConcentrationReferenceMaterialModel(ParametersModel concentrationReferenceMaterialModel);

    /**
     * @param physicalConstantsModel the physicalConstantsModel to set
     */
    void setPhysicalConstantsModel(ParametersModel physicalConstantsModel);

    void setReferenceMaterialModel(ParametersModel referenceMaterialModel);

}