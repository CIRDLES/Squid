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
package org.cirdles.squid.parameters.parameterModels.commonPbModels;

import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_EPSILON;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class StaceyKramerCommonLeadModelTest {

    public StaceyKramerCommonLeadModelTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of staceyKramerSingleStagePbR method, of class
     * StaceyKramerCommonLeadModel.
     */
    @Test
    public void testStaceyKramerSingleStagePbR() {
        System.out.println("staceyKramerSingleStagePbR");
        ParametersModel physicalConstantsModel
                = PhysicalConstantsModel.getDefaultModel("EARTHTIME Physical Constants Model", "1.1");
        StaceyKramerCommonLeadModel.updatePhysicalConstants(physicalConstantsModel);
        StaceyKramerCommonLeadModel.updateU_Ratio(137.88);
        double targetAge = 3.7E9;
        double[] expResult = new double[]{11.152, 12.998, 31.23};
        double[] result
                = StaceyKramerCommonLeadModel.staceyKramerSingleStagePbR(targetAge);
        assertArrayEquals(expResult, result, SQUID_EPSILON);

    }

}
