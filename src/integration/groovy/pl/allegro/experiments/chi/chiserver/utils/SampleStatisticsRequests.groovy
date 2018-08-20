package pl.allegro.experiments.chi.chiserver.utils

class SampleStatisticsRequests {
    static Map DEFAULT_CLASSIC_PROPERTIES = [
            experimentId  : UUID.randomUUID().toString(),
            durationMillis: 691200000L,
            toDate        : "2018-06-01",
            device        : "all",
            variantName   : "someVariant",
            metricName    : "gmv",
            data          : DEFAULT_CLASSIC_DATA_PROPERTIES
    ]

    static Map DEFAULT_CLASSIC_DATA_PROPERTIES = [
            value : 0.07,
            diff  : 0.0003,
            pValue: 0.3,
            count : 1684500
    ]

    static Map sampleClassicStatisticsRequest(Map customProperties = [:]) {
        def request = DEFAULT_CLASSIC_PROPERTIES + customProperties
        request.data = customProperties.data == null ? DEFAULT_CLASSIC_DATA_PROPERTIES
                : DEFAULT_CLASSIC_DATA_PROPERTIES + customProperties.data
        request
    }

    static Map DEFAULT_BAYESIAN_PROPERTIES = [
            experimentId: UUID.randomUUID().toString(),
            toDate      : '2018-04-01',
            device      : "all",
            variantName : 'variant-a',
            data        : [
                    samples      : [
                            values: [],
                            counts: []
                    ],
                    outliersLeft : 10,
                    outliersRight: 122
            ]
    ]

    static Map sampleBayesianStatisticsRequest(List values, List counts, Map customProperties = [:]) {
        def request = DEFAULT_BAYESIAN_PROPERTIES + customProperties
        request.data.samples.values = values
        request.data.samples.counts = counts
        request
    }
}
