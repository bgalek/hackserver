package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

class ClientExperimentsV2E2ESpec extends BaseE2EIntegrationSpec {

    def "should return list of DRAFT and ACTIVE experiments in api v2"() {
        given:
        def draftExperiment = draftExperiment()
        def pausedExperiment = pausedExperiment()
        def endedExperiment = endedExperiment()
        def percentageExperiment = startedExperiment([
                variantNames: ['base', 'v1', 'v2'],
                percentage  : 10
        ])
        def internalExperiment = startedExperiment([
                variantNames       : ['base'],
                internalVariantName: 'internal'
        ])
        def deviceClassExperiment = startedExperiment([
                deviceClass: 'phone-android'
        ])

        when:
        def experiments = fetchClientExperiments('v2')

        then:
        experiments.containsAll([
                draftClientExperiment + [
                        id: draftExperiment.id
                ],
                percentageClientExperiment + [
                        id        : percentageExperiment.id,
                        activityPeriod: percentageExperiment.activityPeriod
                ],
                internalClientExperiment + [
                        id        : internalExperiment.id,
                        activityPeriod: internalExperiment.activityPeriod
                ],
                deviceClassClientExperiment + [
                        id        : deviceClassExperiment.id,
                        activityPeriod: deviceClassExperiment.activityPeriod
                ]
        ])

        and:
        !experiments.collect { it.id }.contains(pausedExperiment.id)
        !experiments.collect { it.id }.contains(endedExperiment.id)
    }

    Map draftClientExperiment = [
            status          : 'DRAFT',
            reportingEnabled: true,
            variants        : [
                    [
                            name      : 'base',
                            predicates: [[type: 'HASH', from: 0, to: 10]]
                    ],
                    [
                            name      : 'v1',
                            predicates: [[type: 'HASH', from: 50, to: 60]]
                    ]
            ]
    ]

    Map percentageClientExperiment = [
            status          : 'ACTIVE',
            reportingEnabled: true,
            variants        : [
                    [
                            name      : 'base',
                            predicates: [[type: 'HASH', from: 0, to: 10]]
                    ],
                    [
                            name      : 'v1',
                            predicates: [[type: 'HASH', from: 33, to: 43]]
                    ],
                    [
                            name      : 'v2',
                            predicates: [[type: 'HASH', from: 66, to: 76]]
                    ]
            ]
    ]

    Map internalClientExperiment = [
            status          : 'ACTIVE',
            reportingEnabled: true,
            variants        : [
                    [
                            name      : 'internal',
                            predicates: [[type: 'INTERNAL']]
                    ],
                    [
                            name      : 'base',
                            predicates: [[type: 'HASH', from: 0, to: 10]]
                    ]
            ]
    ]

    Map deviceClassClientExperiment = [
            status          : 'ACTIVE',
            reportingEnabled: true,
            variants        : [
                    [
                            name      : 'base',
                            predicates: [[type: 'HASH', from: 0, to: 10], [type: 'DEVICE_CLASS', device: 'phone-android']]
                    ],
                    [
                            name      : 'v1',
                            predicates: [[type: 'HASH', from: 50, to: 60], [type: 'DEVICE_CLASS', device: 'phone-android']]
                    ]
            ]
    ]
}
