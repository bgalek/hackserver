package pl.allegro.experiments.chi.chiserver.utils

class SampleClientExperiments {
    static Map internalExperiment = [
            id              : 'internal_exp',
            variants        : [
                    [name: 'internal', predicates: [[type: 'INTERNAL']]]
            ],
            reportingEnabled: true,
            status          : 'DRAFT'
    ]

    static Map cmuidRegexpWithPhoneExperiment = [
            id              : 'cmuid_regexp_with_phone',
            variants        : [
                    [name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$'], [type: 'DEVICE_CLASS', device: 'phone']]]
            ],
            reportingEnabled: true,
            status          : 'DRAFT',
    ]

    static Map cmuidRegexpExperiment = [
            id              : 'cmuid_regexp',
            variants        : [
                    [name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$']]]
            ],
            reportingEnabled: true,
            status          : 'DRAFT'
    ]

    static Map hashVariantExperiment = [
            id              : 'test_dev',
            variants        : [
                    [name: 'v1', predicates: [[type: 'HASH', from: 0, to: 50]]],
                    [name: 'v2', predicates: [[type: 'HASH', from: 50, to: 100]]]
            ],
            reportingEnabled: true,
            status          : 'DRAFT'
    ]

    static Map sampleExperiment = [
            id              : 'another_one',
            variants        : [
                    [name: 'v1', predicates: [[type: 'HASH', from: 0, to: 50]]]
            ],
            reportingEnabled: true,
            status          : 'DRAFT'
    ]

    static Map timeboundExperiment = [
            id              : 'timed_internal_exp',
            activityPeriod  : [
                    activeFrom: '2017-11-03T10:15:30+02:00',
                    activeTo  : '2018-11-03T10:15:30+02:00'
            ],
            variants        : [
                    [name: 'internal', predicates: [[type: 'INTERNAL']]]
            ],
            reportingEnabled: true,
            status          : 'ACTIVE'
    ]

    static Map plannedExperiment = [
            id              : 'planned_exp',
            activityPeriod  : [
                    "activeFrom": "2050-11-03T10:15:30+02:00",
                    "activeTo"  : "2060-11-03T10:15:30+02:00"
            ],
            variants        : [
                    [name: 'internal', predicates: [[type: 'INTERNAL']]]
            ],
            reportingEnabled: true,
            status          : 'PLANNED'
    ]

    static Map experimentFromThePast = [
            id              : 'experiment_from_the_past',
            activityPeriod  : [
                    activeFrom: '2017-10-01T10:15:30+02:00',
                    activeTo  : '2017-11-01T10:15:30+02:00'
            ],
            variants        : [
                    [name: 'internal', predicates: [[type: 'INTERNAL']]]
            ],
            reportingEnabled: true,
            status          : 'ENDED'
    ]
}