package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

class ClientExperimentsV1E2ESpec extends BaseE2EIntegrationSpec {

    def "should return list of DRAFT and ACTIVE experiments in api v1"() {
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
        def experiments = fetchClientExperiments('v1')

        then:
        experiments.containsAll([
                draftClientExperiment + [
                        id: draftExperiment.id
                ],
                percentageClientExperiment + [
                        id        : percentageExperiment.id,
                        activeFrom: percentageExperiment.activityPeriod.activeFrom,
                        activeTo  : percentageExperiment.activityPeriod.activeTo,
                ],
                internalClientExperiment + [
                        id        : internalExperiment.id,
                        activeFrom: internalExperiment.activityPeriod.activeFrom,
                        activeTo  : internalExperiment.activityPeriod.activeTo,
                ],
                deviceClassClientExperiment + [
                        id        : deviceClassExperiment.id,
                        activeFrom: deviceClassExperiment.activityPeriod.activeFrom,
                        activeTo  : deviceClassExperiment.activityPeriod.activeTo,
                ]
        ])

        and:
        !experiments.collect { it.id }.contains(pausedExperiment.id)
        !experiments.collect { it.id }.contains(endedExperiment.id)
    }

    Map draftClientExperiment = [
            owner           : 'Root',
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
            owner           : 'Root',
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
            owner           : 'Root',
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
            owner           : 'Root',
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