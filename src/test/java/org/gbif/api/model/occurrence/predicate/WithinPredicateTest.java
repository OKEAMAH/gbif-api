/*
 * Copyright 2020 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.api.model.occurrence.predicate;

import org.gbif.utils.file.FileUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class WithinPredicateTest {

  // API no longer throws an exeception here.
  public void testEmptyConstructor() {
    new WithinPredicate("");
  }

  @Test
  public void testNullConstructor() {
    assertThrows(NullPointerException.class, () -> new WithinPredicate(null));
  }

  // API no longer throws an exeception here.
  public void testBadConstructor1() {
    new WithinPredicate("POLYGON");
  }

  @Test
  public void testGoodConstructor() {
    new WithinPredicate("POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))");
  }

  // API no longer throws an exeception here.
  public void testBadRectangle() {
    new WithinPredicate("POLYGON ((30 10, 100 100, 20 40, 40 40, 30 10))");
  }

  @Test
  public void testGoodComplexConstructor() throws Exception {
    byte[] enc = Files.readAllBytes(FileUtils.getClasspathFile("predicate/large-polygon.wkt").toPath());
    new WithinPredicate(new String(enc, StandardCharsets.UTF_8));
  }

  @Test
  public void testPolygonWithHole() {
    new WithinPredicate("POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30))");
  }

  @Test
  public void testGoodPolygonOverAntimeridian() {
    // A rectangle over the Bering sea
    new WithinPredicate("POLYGON((-206.71875 39.20502,-133.59375 39.20502,-133.59375 77.26611,-206.71875 77.26611,-206.71875 39.20502))");

    // A polygon around Taveuni, Fiji, as portal16 produces it:
    new WithinPredicate("POLYGON((-180.14832 -16.72643,-180.21423 -16.82899,-180.12085 -17.12058,-179.89838 -17.12845,-179.75006 -16.86054,-179.8764 -16.60277,-180.14832 -16.72643))");
    // Same place, but as Wicket draws it:
    new WithinPredicate("POLYGON(( 179.85168 -16.72643, 179.78577 -16.82899, 179.87915 -17.12058,-179.89838 -17.12845,-179.75006 -16.86054,-179.8764 -16.60277, 179.85168 -16.72643))");
    // Same, but as a MultiPolygon split over the antimeridian
    new WithinPredicate("MULTIPOLYGON (((180 -16.81285, 180 -16.98076, 179.99 -16.99, 179.9 -17.01, 179.88 -16.95, 180 -16.81285))," +
      "((-180 -16.98076, -180 -16.81285, -179.98 -16.79, -179.88 -16.68, -179.81 -16.78, -179.88 -16.87, -180 -16.98076)))");

    // Iceland (the island) from OpenStreetMap, converted and simplified using ogr2ogr
    new WithinPredicate("POLYGON ((-21.4671921 65.441761,-21.3157028 65.9990267,-22.46732 66.4657148,-23.196803 66.3490242,-22.362113 66.2703732,-22.9758561 66.228119,-22.3831844 66.0933255,-22.424131 65.8374539,-23.4703372 66.1972321,-23.2565264 65.6767322,-24.5319933 65.5027259,-21.684764 65.4547893,-24.0482947 64.8794291,-21.3551366 64.3842337,-22.7053151 63.8001572,-19.1269971 63.3980322,-13.4948065 65.076438,-15.1872897 66.1073781,-14.5302343 66.3783121,-16.0235596 66.5371808,-21.4671921 65.441761))");
  }

  @Test
  public void testDoubleHolePolygon() {
    // org.codehaus.jackson.map.JsonMappingException: Instantiation of [simple type, class org.gbif.api.model.occurrence.predicate.WithinPredicate] value failed: org.locationtech.spatial4j.exception.InvalidShapeException (through reference chain: org.gbif.api.model.occurrence.predicate.WithinPredicate["geometry"])
    new WithinPredicate("MULTIPOLYGON (((7.3316813 48.0563494,7.3228514 48.060052,7.3261277 48.0643411,7.3154803 48.0701735,7.3259359 48.0862557,7.3214871 48.0888072,7.3043858 48.0890742,7.3061047 48.0732654,7.2651668 48.0785946,7.2338755 48.0662021,7.1984405 48.0792295,7.1935418 48.0881433,7.2516593 48.1085801,7.2820136 48.1035337,7.2846343 48.1004992,7.2777031 48.0953582,7.2881932 48.0947832,7.2878348 48.1006099,7.2918776 48.0994659,7.2921114 48.099821,7.291151 48.1056706,7.304138 48.1063434,7.3021604 48.1160122,7.3291081 48.1142343,7.3271956 48.1165861,7.331492 48.1212266,7.3680187 48.1287668,7.3588999 48.1348856,7.3605461 48.1478154,7.3354393 48.1479747,7.3362361 48.1504455,7.3190912 48.1557655,7.3183498 48.1637729,7.3431171 48.1649953,7.3517792 48.176481,7.3628895 48.1682446,7.369424 48.1782191,7.3660567 48.1790895,7.3677885 48.1906358,7.375574 48.1908195,7.3829473 48.2037986,7.3955639 48.206989,7.4079415 48.202248,7.4281221 48.2020971,7.4354089 48.2174647,7.4080308 48.2239596,7.4040246 48.2313657,7.3538904 48.2485096,7.326155 48.2442722,7.3097262 48.2536971,7.3144023 48.2634505,7.3201798 48.2611183,7.3336314 48.2727533,7.3044692 48.2680753,7.2923191 48.2705232,7.2907635 48.2741292,7.3015072 48.2824187,7.2750773 48.3054519,7.2650689 48.3033705,7.2527353 48.3062723,7.2418626 48.3024616,7.2349642 48.3095822,7.1983663 48.3102622,7.1969956 48.3153235,7.1895414 48.3186783,7.1916462 48.3248083,7.1776452 48.3251424,7.1808736 48.3352009,7.1851454 48.3360746,7.1838848 48.3391728,7.1694175 48.3416371,7.152719 48.3331324,7.1185582 48.3333417,7.1124378 48.335221,7.1075108 48.3442763,7.0760085 48.3527223,7.0646271 48.3513593,7.0531268 48.3436702,7.0530382 48.3334971,7.0377648 48.3287575,7.03889 48.3213153,7.0331249 48.312728,7.0443075 48.314909,7.0393533 48.3080621,7.0432583 48.3027291,7.0488016 48.3083703,7.0461608 48.3132993,7.0536568 48.3155079,7.0837038 48.2966764,7.074878 48.295627,7.0728709 48.2904388,7.0565199 48.2812333,7.0478142 48.2840282,7.0492483 48.2799951,7.0470145 48.2752943,7.0442919 48.2763387,7.0469059 48.2743547,7.0416969 48.2704656,7.0573756 48.2655794,7.0560908 48.26043,7.060813 48.2520209,7.0442945 48.2470869,7.0353383 48.2505029,7.0379224 48.2520018,7.0332159 48.2548562,7.0357943 48.2569518,7.0128157 48.258504,7.0061855 48.2543414,7.0031157 48.243915,7.0075689 48.2445405,7.0299665 48.2298499,7.0338752 48.2207096,7.0158495 48.2268477,6.9688284 48.2248189,6.9487013 48.2297138,6.9133093 48.2215934,6.9038054 48.2232038,6.8973476 48.2164781,6.8903458 48.2159928,6.887592 48.2191567,6.8713642 48.219531,6.8642245 48.2250006,6.858866 48.2123812,6.8710421 48.2040009,6.8702483 48.1996817,6.8982036 48.2053365,6.8974378 48.2086612,6.914771 48.2060857,6.922253 48.2118279,6.9586759 48.2074111,6.9726431 48.209123,6.9796257 48.1975289,6.9649926 48.1886266,6.96299 48.178105,6.9587078 48.1758245,6.9627561 48.1642553,6.9597544 48.1594574,6.9647831 48.1538282,6.9555786 48.1470161,6.9446437 48.1516863,6.9155512 48.1669908,6.9153312 48.1731011,6.9098411 48.1787947,6.9160271 48.1808655,6.9169855 48.1829649,6.882223 48.1872728,6.8794543 48.1902936,6.8670352 48.1878315,6.863777 48.1772108,6.8414602 48.1672641,6.8363319 48.1575646,6.838507 48.1491376,6.8098638 48.1525965,6.7993383 48.1609054,6.7863999 48.1573589,6.7746174 48.1619331,6.7646957 48.1611616,6.7670097 48.1529193,6.7628422 48.1468517,6.7776206 48.1355266,6.7743346 48.1207331,6.7833364 48.1072105,6.768838 48.1072276,6.7657385 48.1018972,6.7763665 48.0875062,6.7760477 48.0792815,6.7860185 48.0693776,6.7833081 48.0534618,6.7681388 48.0584241,6.7622464 48.0475216,6.7518413 48.0595772,6.7407094 48.0616842,6.7381471 48.0521271,6.7309 48.0514947,6.7330793 48.0498305,6.7233968 48.0421747,6.7027357 48.0392572,6.6915981 48.0327395,6.7016218 48.0210236,6.6992785 48.0170767,6.7117375 48.01227,6.7049689 48.0037354,6.7108016 47.9996745,6.6914 47.9921956,6.7033263 47.9777812,6.6990953 47.9712085,6.7048075 47.9658077,6.7022893 47.96144,6.7170532 47.9431405,6.7353303 47.9385682,6.7416682 47.9323155,6.7537293 47.9325059,6.7575877 47.9257838,6.7715198 47.9248975,6.777985 47.9196068,6.7703248 47.9101663,6.7709851 47.9031709,6.7778566 47.8958867,6.7900801 47.9010668,6.8438912 47.9004961,6.8460271 47.8936228,6.8214459 47.8747202,6.8283333 47.8707375,6.8647949 47.8647161,6.8877689 47.8720027,6.898164 47.8691863,6.9083131 47.8719551,6.9072121 47.8639977,6.9189927 47.8580141,6.9202977 47.8501779,6.9068806 47.8458056,6.8954014 47.8326947,6.8671871 47.8293721,6.8577866 47.8231891,6.8390084 47.8245626,6.8340517 47.8180011,6.8229341 47.8136866,6.793163 47.8296494,6.788602 47.8346803,6.7919338 47.844817,6.7847167 47.8508782,6.7649463 47.8535292,6.759579 47.8588923,6.7379854 47.8610696,6.7372835 47.8651094,6.7326369 47.8635901,6.6958996 47.8293306,6.7041201 47.8260366,6.7029343 47.82504,6.703242 47.8234104,6.7125084 47.8228647,6.7190308 47.8147466,6.7091238 47.8068388,6.7139459 47.8031398,6.7040371 47.7973664,6.7312277 47.7982931,6.7497027 47.786346,6.7331291 47.7778271,6.727822 47.769889,6.7292105 47.7647943,6.7190603 47.7557664,6.6948699 47.7480455,6.6923928 47.7394972,6.6698137 47.7374166,6.6470742 47.7410819,6.614078 47.734054,6.6061146 47.7286575,6.6001842 47.7052211,6.5868808 47.69802,6.5906195 47.693015,6.5968551 47.6942382,6.588118 47.6809603,6.5888304 47.6737908,6.6063899 47.6625222,6.605251 47.6576279,6.6127347 47.655782,6.6263511 47.661203,6.6521044 47.6569173,6.6526475 47.6527954,6.6384991 47.6505083,6.6460345 47.6449228,6.6276229 47.6365651,6.6403242 47.6351072,6.6590137 47.6531108,6.6615139 47.6619423,6.6762813 47.6638299,6.6742302 47.659453,6.6793773 47.6628259,6.678149 47.6656305,6.7063575 47.668891,6.7198564 47.6666667,6.7308581 47.6598751,6.7415797 47.6592877,6.7408984 47.6539641,6.7282384 47.6457255,6.7258293 47.6386655,6.7332717 47.6323963,6.7518532 47.6289245,6.7642903 47.6331593,6.7545224 47.6424894,6.7572682 47.6504834,6.77273 47.6476549,6.7876643 47.6515288,6.7908187 47.6481816,6.7983152 47.6505185,6.7876491 47.6575295,6.7996978 47.6602801,6.8076843 47.6667747,6.8302855 47.6703334,6.8303257 47.6733713,6.8106467 47.6802325,6.7963508 47.690898,6.8115848 47.6943462,6.8133057 47.6919055,6.8312864 47.6991832,6.8620465 47.7001051,6.8814884 47.6984514,6.8916834 47.6922874,6.9013065 47.6945188,6.9042996 47.6904025,6.9185104 47.6885338,6.9386145 47.6939202,6.9545079 47.6928293,6.9502767 47.6964083,6.9522539 47.7058893,6.9491799 47.7090233,6.9539178 47.7171681,6.9488969 47.7228514,6.972708 47.7279158,6.999112 47.7459024,7.0126104 47.7410945,7.0293969 47.7496273,7.0551186 47.7408783,7.0661619 47.7452823,7.0714068 47.7614244,7.0802925 47.7642806,7.0921273 47.7623716,7.0948496 47.767952,7.1135801 47.7800916,7.1167914 47.7903038,7.1111995 47.7918099,7.1174212 47.7990345,7.1121914 47.806033,7.1218079 47.8237603,7.1388198 47.8079635,7.1655487 47.8124076,7.1633068 47.8165846,7.1693233 47.8191185,7.1771954 47.8136844,7.2085309 47.8113412,7.2208316 47.8202509,7.2235714 47.8294937,7.2042709 47.8383987,7.1945504 47.8479813,7.1725885 47.8527906,7.1736381 47.85858,7.1612973 47.8609898,7.2049889 47.8629399,7.2176577 47.8723491,7.2243153 47.8621024,7.2333784 47.8574834,7.2430048 47.8597696,7.2471979 47.873416,7.257592 47.8748713,7.2627045 47.8874833,7.242971 47.8903502,7.2298494 47.8967079,7.2409805 47.9111245,7.2546047 47.9118869,7.2712837 47.919531,7.2705686 47.9271293,7.2558557 47.9313747,7.2539073 47.9371759,7.2744055 47.9518616,7.2802907 47.9773023,7.3011217 47.976164,7.3029968 47.9846815,7.3095156 47.984797,7.3143086 48.0039475,7.3230034 48.006456,7.3104475 48.0100018,7.3034935 48.0146297,7.3057392 48.0171428,7.3010072 48.0176407,7.3056301 48.028794,7.2952231 48.0287443,7.3116171 48.0323066,7.3480708 48.0240327,7.3550365 48.0450388,7.3515268 48.0477431,7.3530977 48.0524075,7.3316813 48.0563494),(7.2748538 48.0268666,7.2848676 48.0324621,7.2853243 48.0436359,7.2665609 48.0401139,7.2663571 48.0369035,7.2763441 48.0340898,7.2748538 48.0268666),(7.1338046 48.0778144,7.1168167 48.0845482,7.1037605 48.1002125,7.0789205 48.1091256,7.0719504 48.1083475,7.0521933 48.0834017,7.0371434 48.0781016,7.0297861 48.0718579,7.0300091 48.0660847,7.0229899 48.0637208,7.0239669 48.0560865,7.0189868 48.0551139,7.0096566 48.0428716,7.0162127 48.037485,7.0090925 48.0328742,7.0118489 48.0316265,7.0016515 48.021952,6.9914472 48.0204902,6.9816855 48.009553,6.9803697 47.9855224,6.998902 47.96317,7.0258091 47.9474023,7.0376984 47.9528602,7.0509108 47.9477313,7.0784133 47.9534529,7.0851975 47.9642168,7.1260233 47.9806676,7.1147933 47.9910348,7.0870625 47.9992997,7.0871439 48.0082068,7.096909 48.0186146,7.087903 48.0349769,7.1195394 48.0478453,7.1161184 48.0628384,7.124599 48.0658106,7.1282518 48.0770592,7.1338046 48.0778144),(7.185943 48.0847525,7.1700662 48.0795638,7.1496357 48.0792396,7.1464727 48.0752594,7.1497611 48.0627661,7.1658117 48.0514262,7.1665754 48.0412141,7.1556028 48.0264156,7.1595112 48.0155778,7.1825941 48.0121885,7.1946786 48.0233072,7.2156997 48.0168082,7.2143143 48.0217437,7.2180123 48.0221995,7.2195494 48.0312372,7.2276368 48.0257227,7.2360989 48.025874,7.2327985 48.0332704,7.2356828 48.0367714,7.2248476 48.0546132,7.1819721 48.0754566,7.1812011 48.0804516,7.185943 48.0847525),(7.1803572 48.0057207,7.1806934 47.9988423,7.1772575 47.9964516,7.1641165 48.0000679,7.1644117 47.9956534,7.1479748 47.9766657,7.1576496 47.965493,7.1704867 47.9582681,7.1838127 47.9696367,7.1731891 47.9707774,7.1878632 47.9754411,7.2039732 47.9941075,7.1854783 47.9984789,7.1848353 48.0048888,7.1803572 48.0057207),(6.8918878 47.778736,6.9019837 47.7767679,6.905593 47.7666292,6.8978978 47.7651929,6.8904463 47.7544902,6.8578006 47.754656,6.8620069 47.7619624,6.8553805 47.7731314,6.8653221 47.784917,6.8918878 47.778736),(6.7804088 47.689622,6.7786492 47.675965,6.7530614 47.6816109,6.7590614 47.6884067,6.7697317 47.6899907,6.7662744 47.6968227,6.7699058 47.7034079,6.777487 47.6982241,6.7804088 47.689622)),((7.0488167 47.9912622,7.0386154 47.9930022,7.0316629 47.9902961,7.0327854 47.9865829,7.0277653 47.9893734,7.0271043 47.9978016,7.0427929 48.0033336,7.0417295 48.0057041,7.0294691 48.0068371,7.0286609 48.013682,7.0180574 48.0158122,7.0187688 48.0196393,7.010788 48.0228057,7.0016376 48.0168361,7.0088591 48.014751,7.0076053 48.0118782,6.9987733 48.0102395,6.999131 48.0039446,6.9938553 48.0032379,7.0052153 47.9963621,7.0061033 47.9898697,6.9951326 47.9795073,6.9981526 47.9742801,7.0217646 47.9849378,7.0336966 47.976681,7.0301753 47.9742827,7.0314808 47.9710113,7.0410751 47.9664727,7.0434466 47.9878646,7.0488167 47.9912622)),((6.7040021 47.8814391,6.7103198 47.8760353,6.7032507 47.8728656,6.7046998 47.8707234,6.6895733 47.8553495,6.6652671 47.8496916,6.6462245 47.851062,6.6429218 47.8440183,6.6490583 47.8420461,6.649067 47.8385502,6.6365599 47.8393639,6.6268039 47.8348024,6.6286194 47.8297626,6.6397813 47.8260836,6.6329585 47.8217139,6.6269289 47.8231275,6.6292115 47.8181769,6.5947754 47.8097815,6.5774611 47.8004129,6.5772533 47.7949856,6.5840235 47.7931406,6.5820514 47.7883818,6.5927509 47.7877823,6.5858109 47.7832425,6.5863306 47.779382,6.5939545 47.7692961,6.5914224 47.7692758,6.6054385 47.7611691,6.6068117 47.7555097,6.5856241 47.7538959,6.5812745 47.7474777,6.5732079 47.7459388,6.5784802 47.7436239,6.5749087 47.7409273,6.5787866 47.736667,6.5744497 47.7293681,6.568595 47.7273082,6.5612998 47.7316704,6.5498512 47.7308311,6.5431674 47.724386,6.5460772 47.7231026,6.5423602 47.7169918,6.5487629 47.7150216,6.5438844 47.7129905,6.5463717 47.7101058,6.5393644 47.6983538,6.5354092 47.6997133,6.5365724 47.7100005,6.5168185 47.7067709,6.5093308 47.7110609,6.4997439 47.7098122,6.4975585 47.7165997,6.5002115 47.7186955,6.4827597 47.7260797,6.4872373 47.7369849,6.510079 47.7345534,6.5061609 47.7381605,6.5090782 47.7404091,6.5249799 47.7441637,6.51384 47.7460735,6.5239979 47.766883,6.528913 47.7697495,6.5052967 47.7726131,6.5012591 47.7660627,6.4910562 47.7657284,6.4914023 47.7717458,6.4816554 47.773708,6.484967 47.7780751,6.4805872 47.7781468,6.4844311 47.7860672,6.4892754 47.7833941,6.4989158 47.7908517,6.4994255 47.7999595,6.5062637 47.7959131,6.526898 47.7999119,6.5257329 47.8029521,6.534772 47.807908,6.526461 47.821627,6.531006 47.823807,6.529702 47.826175,6.515229 47.827138,6.50757 47.815472,6.509684 47.813466,6.499319 47.801435,6.488404 47.804481,6.484452 47.797806,6.477718 47.798455,6.476921 47.792747,6.458856 47.791627,6.45397 47.787264,6.44987 47.796168,6.459673 47.802059,6.452096 47.802489,6.456033 47.818362,6.458177 47.820738,6.469691 47.816535,6.47664 47.830939,6.472647 47.834924,6.481758 47.836545,6.460916 47.8412287,6.4520527 47.8357198,6.445034 47.836433,6.4211343 47.8675105,6.4154685 47.8654205,6.4066562 47.8525211,6.3827873 47.8415496,6.3824032 47.8529705,6.3776583 47.8515992,6.3809755 47.8559502,6.3782799 47.8622184,6.3736741 47.8629105,6.3657341 47.8825484,6.3617843 47.8843934,6.3677337 47.892332,6.3661757 47.9006524,6.3790504 47.9102586,6.3786239 47.9151749,6.3712643 47.9231004,6.364637 47.923376,6.3765591 47.9328962,6.4082555 47.9431173,6.4051578 47.9529937,6.3924939 47.9561628,6.3913203 47.9604665,6.3793273 47.9584924,6.3656524 47.9632747,6.3822507 47.9689472,6.3844259 47.9801909,6.4007523 47.9829015,6.4036544 47.9858703,6.4015006 47.9905415,6.4078629 47.994801,6.4169925 47.9789269,6.4244857 47.9862595,6.44722 47.9902828,6.4831838 47.9886239,6.4771928 47.9859808,6.4784948 47.9807725,6.4856826 47.9846564,6.4857383 47.981778,6.4933149 47.9818982,6.5105562 47.988233,6.5221448 47.9868265,6.5429018 47.9920219,6.5676896 47.9793439,6.589092 47.9827872,6.6045572 47.9632285,6.5962676 47.9481789,6.6071555 47.9440609,6.6171551 47.9321318,6.6310483 47.9282245,6.6326307 47.9219687,6.6409762 47.9181024,6.6392915 47.9103188,6.643922 47.9048229,6.7040021 47.8814391)))");
  }

  /**
   * TODO: The JTS library does not properly support wrapping around a pole.
   * https://github.com/gbif/gbif-api/issues/44
   *
   * <a href="https://github.com/locationtech/spatial4j#why-not-use-jts-why-should-you-use-spatial4j">The README on
   * Spatial4J</a>, which is a derivative of JTS, says "it wraps JTS geometries to add dateline-wrap support (no pole wrap yet)."
   *
   * https://github.com/locationtech/spatial4j/issues/5
   */
  @Disabled
  @Test
  public void testGoodPolygonOverPole() {
    // A big polygon over the Arctic
    new WithinPredicate("POLYGON((-40 65,80 75,160 65, 220 75,280 55,-40 65))");
    // The same, but with all values ≤±180.
    new WithinPredicate("POLYGON((-40 65,80 75,160 65,-140 75,-80 55,-40 65))");

    // A more detailed polygon around the Arctic (CAFF polygon, vastly simplified).
    // https://dev.gbif.org/issues/browse/POR-3042/
    new WithinPredicate("POLYGON((-181 50,-180 50,-179 50,-170 51,-150 67,-130 62,-110 59,-90 55,-70 52,-50 59,-30 66,-10 62,10 66,30 66,50 66,70 65,90 64,110 63,130 63,150 62,170 53,178 50,179 50,-181 50))");
  }

  @Test
  public void testInterpolatedRectangleOverPole() {
    // The CAFF (ish) Arctic polygon, "fixed" by turning it into a WGS84 rectangle, and interpolating so the wrap-around doesn't have a distance of >180°.
    new WithinPredicate("POLYGON((-180 90,-180 50,-179 50,-170 51,-150 67,-130 62,-110 59,-90 55,-70 52,-50 59,-30 66,-10 62,10 66,30 66,50 66,70 65,90 64,110 63,130 63,150 62,170 53,178 50,179 50,180 50,180 90,60 90,-60 90,-180 90))");
  }

  @Test
  public void testOldDownload() {
    // A polygon that is no longer valid should still be read.
    new WithinPredicate("POLYGON((179.99706 50.42798,175.71729 50.32125,164.69551 54.46408,164.41784 58.23259,152.93250 62.42883,149.28338 61.42635,145.49908 63.19071,135.05338 62.47747,121.47856 63.88828,118.42940 61.72106,92.03338 63.48794,88.04893 65.83859,72.38348 64.83205,35.10765 65.27235,20.53798 66.27026,15.93477 63.64430,13.23228 64.16949,14.02329 66.20159,-3.98112 66.35560,-5.28656 60.41050,-24.62696 63.13864,-26.61983 66.37187,-35.26608 65.57732,-42.55152 58.69327,-51.84071 58.60134,-55.81483 49.91863,-85.07522 48.86776,-87.99834 53.35866,-109.22832 59.11742,-121.84178 55.37470,-134.08814 63.56701,-141.82291 64.54874,-141.93138 68.24804,-158.60607 65.39087,-150.05958 56.55879,-167.23505 51.12098,-180.00299 50.42801,-180.00000 90.00000,180.00000 90.00000,179.99706 50.42798))");
  }
}
