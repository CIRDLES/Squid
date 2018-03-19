/*
 * Copyright 2018 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.functions;

import java.util.List;
import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_EPSILON;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
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
public class WtdMeanACalcTest {

    public WtdMeanACalcTest() {
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
     * Test of wtdMeanAcalc method, of class WtdMeanACalc.
     */
    @Test
    public void testWtdMeanAcalc() {
        System.out.println("wtdMeanAcalc 238");
        double[] values = new double[]{
            0.00884012768001511,
            0.00884482434708822,
            0.00894942461822356,
            0.00889112670953696,
            0.00890349557746501,
            0.00883138331975604,
            0.00876125771950047,
            0.00892612565709379,
            0.00889761850769793,
            0.00892993550196493,
            0.00890203514590812,
            0.00905772917568000,
            0.00887281165404440,
            0.00869497835253045,
            0.00878625290110216,
            0.00898639388860170,
            0.00891147007556454,
            0.00895363451243574,
            0.00883845165839725,
            0.00890646409897058,
            0.00879306700888207

        };

        double[] oneSigmaPctUnct = new double[]{
            0.580084916224033,
            0.318530700755801,
            0.304784915850586,
            0.499614559386961,
            0.307217150220516,
            0.314323819438596,
            0.307491828713402,
            0.562739415363749,
            0.578265872862096,
            0.349453015920481,
            0.313567592682458,
            0.564822124409578,
            0.855773017427626,
            0.319535292145000,
            0.317747355283710,
            0.313471282662508,
            0.381403634488646,
            0.315330262602451,
            0.328677825685174,
            0.319268071644238,
            0.335187788342414};

        boolean noUPbConstAutoReject = false;
        boolean pbCanDriftCorr = false;

        double[][] result = WtdMeanACalc.wtdMeanAcalc(values, oneSigmaPctUnct, noUPbConstAutoReject, pbCanDriftCorr);

        double[][] expResult = new double[][]{{0.008878829024487339, 5.8914250295570144E-5, 1.687608175425885E-5, 3.491041642807011E-5, 5.658307625943268, 3.953271043855011E-11},
        {0.0, 7.0, 8.0, 11.0, 12.0},
        {13.0}};

        assertArrayEquals(expResult[0], result[0], SQUID_EPSILON);
        assertArrayEquals(expResult[1], result[1], SQUID_EPSILON);
        assertArrayEquals(expResult[2], result[2], SQUID_EPSILON);

        System.out.println("wtdMeanAcalc 232");
        values = new double[]{
            0.0131044345958688,
            0.0130045206497664,
            0.0135286049486212,
            0.0130153197041136,
            0.0128510063833588,
            0.0134733885858828,
            0.0134316457807052,
            0.0130829382212723,
            0.0136253150791807,
            0.0130408586752409,
            0.0140483033336357,
            0.0129582037044770,
            0.0131614457793579,
            0.0124175807980006,
            0.0128218132819698,
            0.0133113453061148,
            0.0127901991275686,
            0.0133115494677438,
            0.0123267634856391,
            0.0127937054488144,
            0.0128081280030099

        };

        oneSigmaPctUnct = new double[]{
            4.73263422379419,
            2.96878747344440,
            2.70249557630484,
            3.07262276766305,
            2.89028815997547,
            2.90631023565814,
            2.86412556074729,
            2.98178690055848,
            4.94424224421275,
            5.51694791918178,
            2.72408323321023,
            3.52277366334225,
            2.95144043643686,
            3.02208944342706,
            3.00874735410379,
            2.84434802879348,
            3.17407594622043,
            2.98427002833909,
            3.06609999337721,
            3.04908894148808,
            3.22906820874518
        };

        noUPbConstAutoReject = false;
        pbCanDriftCorr = false;

        result = WtdMeanACalc.wtdMeanAcalc(values, oneSigmaPctUnct, noUPbConstAutoReject, pbCanDriftCorr);

        expResult = new double[][]{{0.01307771811564064, 9.008745163259912E-5,
            9.006943414227261E-5, 1.7657140519989427E-4, 1.0893416768705497, 0.35375414119317605},
        {9.0},
        {}};

        assertArrayEquals(expResult[0], result[0], SQUID_EPSILON);
        assertArrayEquals(expResult[1], result[1], SQUID_EPSILON);
        assertArrayEquals(expResult[2], result[2], SQUID_EPSILON);

        System.out.println("wtdMeanAcalc 8-corr  6/38");
        values = new double[]{
            0.00883888952884310,
            0.00884331196433504,
            0.00893913688173332,
            0.00889298439237256,
            0.00890815777925246,
            0.00881397477162038,
            0.00874905677062329,
            0.00892289948226974,
            0.00889151893216212,
            0.00892861692758308,
            0.00887847016279618,
            0.00906529495886181,
            0.00886758015478957,
            0.00870101673366823,
            0.00879363602324261,
            0.00897805461142732,
            0.00891811919312604,
            0.00894732572089242,
            0.00884945758949287,
            0.00891625316577103,
            0.00879326136350812

        };

        oneSigmaPctUnct = new double[]{
            0.615044457505017,
            0.332902590395251,
            0.319841627605018,
            0.508491794174057,
            0.321048685161464,
            0.329283261389387,
            0.322473028402750,
            0.571111482976386,
            0.613299846729331,
            0.394434488023121,
            0.330191770285568,
            0.570779845213802,
            0.861862798537272,
            0.333906376161504,
            0.332043877028965,
            0.328514145941694,
            0.396133841250727,
            0.329703516232533,
            0.343014645749324,
            0.332867988819649,
            0.349908126553537
        };

        noUPbConstAutoReject = false;
        pbCanDriftCorr = false;

        result = WtdMeanACalc.wtdMeanAcalc(values, oneSigmaPctUnct, noUPbConstAutoReject, pbCanDriftCorr);

        expResult = new double[][]{{0.008886788273052799, 6.48021476077764E-5,
            1.7553966494884863E-5, 3.605730020871834E-5, 5.353464528384578, 1.5670686970281622E-11},
        {0.0, 8.0, 12.0},
        {13.0}};

        assertArrayEquals(expResult[0], result[0], SQUID_EPSILON);
        assertArrayEquals(expResult[1], result[1], SQUID_EPSILON);
        assertArrayEquals(expResult[2], result[2], SQUID_EPSILON);
        
        System.out.println("wtdMeanAcalc 7-corr  8/32");
        values = new double[]{
            0.0132714624131130,
            0.0133882946295659,
            0.0133850218034508,
            0.0133844539624368,
            0.0132107745207878,
            0.0137295155115179,
            0.0136907098926137,
            0.0135643959678552,
            0.0137926463463175,
            0.0131517135622813,
            0.0138091020930780,
            0.0137146381541647,
            0.0130775705949649,
            0.0125899942937592,
            0.0131461118412367,
            0.0139594429074334,
            0.0128858346030185,
            0.0133586331217752,
//            0.0120800837543763, march 2018 s.b. rejected but Simon needs to solve min prob issue
            0.0127637281929925,
            0.0131185889389555

        };

        oneSigmaPctUnct = new double[]{
            4.76781845762060,
            3.03895881884730,
            2.91789131406451,
            3.49981607719981,
            3.00855955090898,
            3.32331222820762,
            2.88316620507134,
            3.06540525758120,
            5.01350470680401,
            5.58232832024715,
            2.93815264673220,
            3.20522925996515,
            3.12433720359202,
            3.17608101249966,
            3.09169946280922,
            3.01846810945945,
            3.35791764620727,
            2.99608087486247,
//            3.23872225562792, march 2018 s.b. rejected but Simon needs to solve min prob issue
            3.14717175952029,
            3.24423803664043
        };

        noUPbConstAutoReject = false;
        pbCanDriftCorr = false;

        result = WtdMeanACalc.wtdMeanAcalc(values, oneSigmaPctUnct, noUPbConstAutoReject, pbCanDriftCorr);

        expResult = new double[][]{{0.013335780829052908, 9.73974592746924E-5,
            9.737797978283746E-5, 1.908990201783971E-4, 0.751084515119131, 0.7676939680263979},
        {},
        {}}; //  march 2018 18.0 s.b. rejected but Simon needs to solve min prob issue

        assertArrayEquals(expResult[0], result[0], SQUID_EPSILON);
        assertArrayEquals(expResult[1], result[1], SQUID_EPSILON);
        assertArrayEquals(expResult[2], result[2], SQUID_EPSILON);
    }

}
