package pl.allegro.experiments.chi.chiserver.domain.statistics

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics
import spock.lang.Specification

class BayesianVerticalEqualizerSpec extends Specification {

    def "should  "(){
      when:
      def equalizer = new BayesianVerticalEqualizer(sampleStats())

      then:
      equalizer.bars.size() == 1
      def bar = equalizer.bars[0]

      bar.variantName.get() == "test-a"

      bar.improvingProbabilities == [0.7021, 0.5060, 0.2936, 0.1351, 0.0505]
      bar.worseningProbabilities == [0.2979, 0.1492, 0.0534, 0.0136, 0.0022]
    }

    BayesianExperimentStatistics sampleStats() {
        Gson gson = new GsonBuilder().create()
        gson.fromJson(SIMPLE_HIST, BayesianExperimentStatistics)
    }

    final String SIMPLE_HIST = """
{
    "experimentId" : "ux-404-new",
    "toDate" : "2018-05-13",
    "device" : "desktop",
    "variantBayesianStatistics" : [ 
        {
            "variantName" : "test-a",
            "samples" : {
                "values" : [ 
                    -0.1, 
                    -0.099, 
                    -0.098, 
                    -0.097, 
                    -0.096, 
                    -0.095, 
                    -0.094, 
                    -0.093, 
                    -0.092, 
                    -0.091, 
                    -0.09, 
                    -0.089, 
                    -0.088, 
                    -0.087, 
                    -0.086, 
                    -0.085, 
                    -0.084, 
                    -0.083, 
                    -0.082, 
                    -0.081, 
                    -0.08, 
                    -0.079, 
                    -0.078, 
                    -0.077, 
                    -0.076, 
                    -0.075, 
                    -0.074, 
                    -0.073, 
                    -0.072, 
                    -0.071, 
                    -0.07, 
                    -0.069, 
                    -0.068, 
                    -0.067, 
                    -0.066, 
                    -0.065, 
                    -0.064, 
                    -0.063, 
                    -0.062, 
                    -0.061, 
                    -0.06, 
                    -0.059, 
                    -0.058, 
                    -0.057, 
                    -0.056, 
                    -0.055, 
                    -0.054, 
                    -0.053, 
                    -0.052, 
                    -0.051, 
                    -0.05, 
                    -0.049, 
                    -0.048, 
                    -0.047, 
                    -0.046, 
                    -0.045, 
                    -0.044, 
                    -0.043, 
                    -0.042, 
                    -0.041, 
                    -0.04, 
                    -0.039, 
                    -0.038, 
                    -0.037, 
                    -0.036, 
                    -0.035, 
                    -0.034, 
                    -0.033, 
                    -0.032, 
                    -0.031, 
                    -0.03, 
                    -0.029, 
                    -0.028, 
                    -0.027, 
                    -0.026, 
                    -0.025, 
                    -0.024, 
                    -0.023, 
                    -0.022, 
                    -0.021, 
                    -0.02, 
                    -0.019, 
                    -0.018, 
                    -0.017, 
                    -0.016, 
                    -0.015, 
                    -0.014, 
                    -0.013, 
                    -0.012, 
                    -0.011, 
                    -0.01, 
                    -0.009, 
                    -0.008, 
                    -0.007, 
                    -0.006, 
                    -0.005, 
                    -0.004, 
                    -0.003, 
                    -0.002, 
                    -0.001, 
                    0.001, 
                    0.002, 
                    0.003, 
                    0.004, 
                    0.005, 
                    0.006, 
                    0.007, 
                    0.008, 
                    0.009, 
                    0.01, 
                    0.011, 
                    0.012, 
                    0.013, 
                    0.014, 
                    0.015, 
                    0.016, 
                    0.017, 
                    0.018, 
                    0.019, 
                    0.02, 
                    0.021, 
                    0.022, 
                    0.023, 
                    0.024, 
                    0.025, 
                    0.026, 
                    0.027, 
                    0.028, 
                    0.029, 
                    0.03, 
                    0.031, 
                    0.032, 
                    0.033, 
                    0.034, 
                    0.035, 
                    0.036, 
                    0.037, 
                    0.038, 
                    0.039, 
                    0.04, 
                    0.041, 
                    0.042, 
                    0.043, 
                    0.044, 
                    0.045, 
                    0.046, 
                    0.047, 
                    0.048, 
                    0.049, 
                    0.05, 
                    0.051, 
                    0.052, 
                    0.053, 
                    0.054, 
                    0.055, 
                    0.056, 
                    0.057, 
                    0.058, 
                    0.059, 
                    0.06, 
                    0.061, 
                    0.062, 
                    0.063, 
                    0.064, 
                    0.065, 
                    0.066, 
                    0.067, 
                    0.068, 
                    0.069, 
                    0.07, 
                    0.071, 
                    0.072, 
                    0.073, 
                    0.074, 
                    0.075, 
                    0.076, 
                    0.077, 
                    0.078, 
                    0.079, 
                    0.08, 
                    0.081, 
                    0.082, 
                    0.083, 
                    0.084, 
                    0.085, 
                    0.086, 
                    0.087, 
                    0.088, 
                    0.089, 
                    0.09, 
                    0.091, 
                    0.092, 
                    0.093, 
                    0.094, 
                    0.095, 
                    0.096, 
                    0.097, 
                    0.098, 
                    0.099, 
                    0.1
                ],
                "counts" : [ 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    1, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    2, 
                    0, 
                    0, 
                    3, 
                    2, 
                    3, 
                    3, 
                    1, 
                    3, 
                    3, 
                    5, 
                    4, 
                    9, 
                    8, 
                    11, 
                    10, 
                    15, 
                    17, 
                    23, 
                    25, 
                    43, 
                    34, 
                    52, 
                    60, 
                    70, 
                    82, 
                    104, 
                    122, 
                    123, 
                    150, 
                    181, 
                    190, 
                    224, 
                    278, 
                    255, 
                    316, 
                    401, 
                    405, 
                    427, 
                    495, 
                    571, 
                    612, 
                    660, 
                    647, 
                    793, 
                    851, 
                    947, 
                    990, 
                    1019, 
                    1110, 
                    1237, 
                    1328, 
                    1426, 
                    1499, 
                    1502, 
                    1463, 
                    1614, 
                    1782, 
                    1776, 
                    1850, 
                    1953, 
                    2066, 
                    2113, 
                    2058, 
                    2107, 
                    2222, 
                    2262, 
                    2282, 
                    2252, 
                    2252, 
                    2232, 
                    2233, 
                    2253, 
                    2184, 
                    2207, 
                    2092, 
                    2092, 
                    2034, 
                    1979, 
                    1929, 
                    1884, 
                    1817, 
                    1730, 
                    1660, 
                    1676, 
                    1567, 
                    1585, 
                    1357, 
                    1340, 
                    1236, 
                    1138, 
                    1022, 
                    1030, 
                    909, 
                    851, 
                    839, 
                    744, 
                    710, 
                    591, 
                    621, 
                    521, 
                    490, 
                    470, 
                    418, 
                    366, 
                    330, 
                    276, 
                    266, 
                    243, 
                    212, 
                    185, 
                    174, 
                    145, 
                    132, 
                    112, 
                    103, 
                    87, 
                    88, 
                    63, 
                    56, 
                    38, 
                    47, 
                    35, 
                    36, 
                    24, 
                    24, 
                    18, 
                    18, 
                    12, 
                    8, 
                    11, 
                    10, 
                    4, 
                    9, 
                    5, 
                    5, 
                    2, 
                    3, 
                    2, 
                    1, 
                    1, 
                    1, 
                    1, 
                    1, 
                    0, 
                    0, 
                    1, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0
                ],
                "labels" : [ 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.02%", 
                    "0.02%", 
                    "0.03%", 
                    "0.03%", 
                    "0.04%", 
                    "0.05%", 
                    "0.06%", 
                    "0.07%", 
                    "0.08%", 
                    "0.10%", 
                    "0.12%", 
                    "0.15%", 
                    "0.19%", 
                    "0.22%", 
                    "0.28%", 
                    "0.34%", 
                    "0.41%", 
                    "0.49%", 
                    "0.59%", 
                    "0.71%", 
                    "0.84%", 
                    "0.99%", 
                    "1.17%", 
                    "1.36%", 
                    "1.58%", 
                    "1.86%", 
                    "2.12%", 
                    "2.43%", 
                    "2.83%", 
                    "3.24%", 
                    "3.67%", 
                    "4.16%", 
                    "4.73%", 
                    "5.34%", 
                    "6.00%", 
                    "6.65%", 
                    "7.44%", 
                    "8.29%", 
                    "9.24%", 
                    "10.23%", 
                    "11.25%", 
                    "12.36%", 
                    "13.60%", 
                    "14.92%", 
                    "16.35%", 
                    "17.85%", 
                    "19.35%", 
                    "20.82%", 
                    "22.43%", 
                    "24.21%", 
                    "25.99%", 
                    "27.84%", 
                    "29.79%", 
                    "70.21%", 
                    "68.14%", 
                    "66.03%", 
                    "63.97%", 
                    "61.87%", 
                    "59.64%", 
                    "57.38%", 
                    "55.10%", 
                    "52.85%", 
                    "50.60%", 
                    "48.36%", 
                    "46.13%", 
                    "43.88%", 
                    "41.69%", 
                    "39.49%", 
                    "37.40%", 
                    "35.30%", 
                    "33.27%", 
                    "31.29%", 
                    "29.36%", 
                    "27.48%", 
                    "25.66%", 
                    "23.93%", 
                    "22.27%", 
                    "20.59%", 
                    "19.03%", 
                    "17.44%", 
                    "16.09%", 
                    "14.74%", 
                    "13.51%", 
                    "12.37%", 
                    "11.35%", 
                    "10.32%", 
                    "9.41%", 
                    "8.56%", 
                    "7.72%", 
                    "6.98%", 
                    "6.27%", 
                    "5.67%", 
                    "5.05%", 
                    "4.53%", 
                    "4.04%", 
                    "3.57%", 
                    "3.16%", 
                    "2.79%", 
                    "2.46%", 
                    "2.18%", 
                    "1.92%", 
                    "1.67%", 
                    "1.46%", 
                    "1.28%", 
                    "1.10%", 
                    "0.96%", 
                    "0.83%", 
                    "0.71%", 
                    "0.61%", 
                    "0.52%", 
                    "0.44%", 
                    "0.37%", 
                    "0.32%", 
                    "0.28%", 
                    "0.23%", 
                    "0.20%", 
                    "0.16%", 
                    "0.14%", 
                    "0.11%", 
                    "0.10%", 
                    "0.08%", 
                    "0.07%", 
                    "0.06%", 
                    "0.05%", 
                    "0.04%", 
                    "0.03%", 
                    "0.02%", 
                    "0.02%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%"
                ]
            }
        }, 
        {
            "variantName" : "base",
            "samples" : {
                "values" : [ 
                    -0.1, 
                    -0.099, 
                    -0.098, 
                    -0.097, 
                    -0.096, 
                    -0.095, 
                    -0.094, 
                    -0.093, 
                    -0.092, 
                    -0.091, 
                    -0.09, 
                    -0.089, 
                    -0.088, 
                    -0.087, 
                    -0.086, 
                    -0.085, 
                    -0.084, 
                    -0.083, 
                    -0.082, 
                    -0.081, 
                    -0.08, 
                    -0.079, 
                    -0.078, 
                    -0.077, 
                    -0.076, 
                    -0.075, 
                    -0.074, 
                    -0.073, 
                    -0.072, 
                    -0.071, 
                    -0.07, 
                    -0.069, 
                    -0.068, 
                    -0.067, 
                    -0.066, 
                    -0.065, 
                    -0.064, 
                    -0.063, 
                    -0.062, 
                    -0.061, 
                    -0.06, 
                    -0.059, 
                    -0.058, 
                    -0.057, 
                    -0.056, 
                    -0.055, 
                    -0.054, 
                    -0.053, 
                    -0.052, 
                    -0.051, 
                    -0.05, 
                    -0.049, 
                    -0.048, 
                    -0.047, 
                    -0.046, 
                    -0.045, 
                    -0.044, 
                    -0.043, 
                    -0.042, 
                    -0.041, 
                    -0.04, 
                    -0.039, 
                    -0.038, 
                    -0.037, 
                    -0.036, 
                    -0.035, 
                    -0.034, 
                    -0.033, 
                    -0.032, 
                    -0.031, 
                    -0.03, 
                    -0.029, 
                    -0.028, 
                    -0.027, 
                    -0.026, 
                    -0.025, 
                    -0.024, 
                    -0.023, 
                    -0.022, 
                    -0.021, 
                    -0.02, 
                    -0.019, 
                    -0.018, 
                    -0.017, 
                    -0.016, 
                    -0.015, 
                    -0.014, 
                    -0.013, 
                    -0.012, 
                    -0.011, 
                    -0.01, 
                    -0.009, 
                    -0.008, 
                    -0.007, 
                    -0.006, 
                    -0.005, 
                    -0.004, 
                    -0.003, 
                    -0.002, 
                    -0.001, 
                    0.001, 
                    0.002, 
                    0.003, 
                    0.004, 
                    0.005, 
                    0.006, 
                    0.007, 
                    0.008, 
                    0.009, 
                    0.01, 
                    0.011, 
                    0.012, 
                    0.013, 
                    0.014, 
                    0.015, 
                    0.016, 
                    0.017, 
                    0.018, 
                    0.019, 
                    0.02, 
                    0.021, 
                    0.022, 
                    0.023, 
                    0.024, 
                    0.025, 
                    0.026, 
                    0.027, 
                    0.028, 
                    0.029, 
                    0.03, 
                    0.031, 
                    0.032, 
                    0.033, 
                    0.034, 
                    0.035, 
                    0.036, 
                    0.037, 
                    0.038, 
                    0.039, 
                    0.04, 
                    0.041, 
                    0.042, 
                    0.043, 
                    0.044, 
                    0.045, 
                    0.046, 
                    0.047, 
                    0.048, 
                    0.049, 
                    0.05, 
                    0.051, 
                    0.052, 
                    0.053, 
                    0.054, 
                    0.055, 
                    0.056, 
                    0.057, 
                    0.058, 
                    0.059, 
                    0.06, 
                    0.061, 
                    0.062, 
                    0.063, 
                    0.064, 
                    0.065, 
                    0.066, 
                    0.067, 
                    0.068, 
                    0.069, 
                    0.07, 
                    0.071, 
                    0.072, 
                    0.073, 
                    0.074, 
                    0.075, 
                    0.076, 
                    0.077, 
                    0.078, 
                    0.079, 
                    0.08, 
                    0.081, 
                    0.082, 
                    0.083, 
                    0.084, 
                    0.085, 
                    0.086, 
                    0.087, 
                    0.088, 
                    0.089, 
                    0.09, 
                    0.091, 
                    0.092, 
                    0.093, 
                    0.094, 
                    0.095, 
                    0.096, 
                    0.097, 
                    0.098, 
                    0.099, 
                    0.1
                ],
                "counts" : [ 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    2, 
                    1, 
                    1, 
                    0, 
                    0, 
                    1, 
                    3, 
                    2, 
                    1, 
                    1, 
                    3, 
                    3, 
                    4, 
                    4, 
                    6, 
                    8, 
                    6, 
                    15, 
                    9, 
                    19, 
                    29, 
                    27, 
                    37, 
                    35, 
                    48, 
                    48, 
                    75, 
                    68, 
                    87, 
                    108, 
                    121, 
                    133, 
                    149, 
                    189, 
                    180, 
                    211, 
                    291, 
                    287, 
                    328, 
                    333, 
                    425, 
                    455, 
                    460, 
                    530, 
                    591, 
                    651, 
                    732, 
                    792, 
                    884, 
                    961, 
                    984, 
                    1075, 
                    1170, 
                    1198, 
                    1328, 
                    1405, 
                    1474, 
                    1585, 
                    1587, 
                    1760, 
                    1793, 
                    1801, 
                    1925, 
                    1998, 
                    1982, 
                    2179, 
                    2111, 
                    2174, 
                    2185, 
                    2219, 
                    2210, 
                    2243, 
                    2273, 
                    2275, 
                    2249, 
                    2201, 
                    2199, 
                    2150, 
                    2099, 
                    2086, 
                    2038, 
                    2067, 
                    1957, 
                    1952, 
                    1862, 
                    1769, 
                    1643, 
                    1640, 
                    1462, 
                    1474, 
                    1416, 
                    1266, 
                    1166, 
                    1167, 
                    1060, 
                    976, 
                    948, 
                    887, 
                    813, 
                    699, 
                    682, 
                    578, 
                    593, 
                    483, 
                    466, 
                    434, 
                    379, 
                    331, 
                    317, 
                    269, 
                    240, 
                    223, 
                    207, 
                    155, 
                    135, 
                    125, 
                    100, 
                    115, 
                    100, 
                    77, 
                    60, 
                    56, 
                    49, 
                    53, 
                    36, 
                    33, 
                    33, 
                    20, 
                    21, 
                    17, 
                    15, 
                    11, 
                    6, 
                    7, 
                    9, 
                    9, 
                    5, 
                    2, 
                    4, 
                    3, 
                    1, 
                    2, 
                    1, 
                    1, 
                    1, 
                    1, 
                    0, 
                    1, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0, 
                    0
                ],
                "labels" : [ 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.02%", 
                    "0.02%", 
                    "0.03%", 
                    "0.03%", 
                    "0.04%", 
                    "0.05%", 
                    "0.06%", 
                    "0.07%", 
                    "0.09%", 
                    "0.12%", 
                    "0.14%", 
                    "0.18%", 
                    "0.22%", 
                    "0.27%", 
                    "0.31%", 
                    "0.39%", 
                    "0.46%", 
                    "0.54%", 
                    "0.65%", 
                    "0.77%", 
                    "0.91%", 
                    "1.05%", 
                    "1.24%", 
                    "1.42%", 
                    "1.63%", 
                    "1.93%", 
                    "2.21%", 
                    "2.54%", 
                    "2.87%", 
                    "3.30%", 
                    "3.75%", 
                    "4.21%", 
                    "4.74%", 
                    "5.33%", 
                    "5.99%", 
                    "6.72%", 
                    "7.51%", 
                    "8.39%", 
                    "9.35%", 
                    "10.34%", 
                    "11.41%", 
                    "12.58%", 
                    "13.78%", 
                    "15.11%", 
                    "16.51%", 
                    "17.99%", 
                    "19.57%", 
                    "21.16%", 
                    "22.92%", 
                    "24.71%", 
                    "26.51%", 
                    "28.44%", 
                    "30.44%", 
                    "32.42%", 
                    "34.60%", 
                    "36.71%", 
                    "38.88%", 
                    "41.07%", 
                    "43.29%", 
                    "45.50%", 
                    "47.74%", 
                    "50.01%", 
                    "49.99%", 
                    "47.71%", 
                    "45.46%", 
                    "43.26%", 
                    "41.06%", 
                    "38.91%", 
                    "36.81%", 
                    "34.73%", 
                    "32.69%", 
                    "30.62%", 
                    "28.67%", 
                    "26.71%", 
                    "24.85%", 
                    "23.08%", 
                    "21.44%", 
                    "19.80%", 
                    "18.34%", 
                    "16.86%", 
                    "15.45%", 
                    "14.18%", 
                    "13.02%", 
                    "11.85%", 
                    "10.79%", 
                    "9.81%", 
                    "8.87%", 
                    "7.98%", 
                    "7.17%", 
                    "6.47%", 
                    "5.78%", 
                    "5.21%", 
                    "4.61%", 
                    "4.13%", 
                    "3.66%", 
                    "3.23%", 
                    "2.85%", 
                    "2.52%", 
                    "2.20%", 
                    "1.93%", 
                    "1.69%", 
                    "1.47%", 
                    "1.26%", 
                    "1.11%", 
                    "0.97%", 
                    "0.85%", 
                    "0.75%", 
                    "0.63%", 
                    "0.53%", 
                    "0.46%", 
                    "0.40%", 
                    "0.34%", 
                    "0.29%", 
                    "0.24%", 
                    "0.20%", 
                    "0.17%", 
                    "0.14%", 
                    "0.12%", 
                    "0.10%", 
                    "0.08%", 
                    "0.06%", 
                    "0.05%", 
                    "0.05%", 
                    "0.04%", 
                    "0.03%", 
                    "0.02%", 
                    "0.02%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.01%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%", 
                    "0.00%"
                ]
            }
        }
    ]
}
"""
}
