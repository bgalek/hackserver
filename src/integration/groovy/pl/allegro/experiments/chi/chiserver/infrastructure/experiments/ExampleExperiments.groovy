package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

trait ExampleExperiments {
    Map internalExperiment() {
        [id              : 'internal_exp',
         renderedVariants : [
                 [name: 'internal', predicates: [[type: 'INTERNAL']]]
         ],
         reportingEnabled: false,
         groups          : [],
         status          : 'DRAFT',
         reportingType   : 'BACKEND',
         eventDefinitions: [],
         variantNames    : ['internal'],
         editable        : false,
         origin          : 'STASH',
         measurements    : [lastDayVisits: 0]
        ]
    }

    Map cmuidRegexpWithPhoneExperiment() {
        [id              : 'cmuid_regexp_with_phone',
         renderedVariants : [
                 [name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$'], [type: 'DEVICE_CLASS', device: 'phone']]]
         ],
         reportingEnabled: true,
         description     : "Experiment description",
         author          : "Experiment owner",
         groups          : [],
         status          : 'DRAFT',
         reportingType   : 'BACKEND',
         eventDefinitions: [],
         variantNames    : ['v1'],
         editable        : false,
         origin          : 'STASH',
         measurements    : [lastDayVisits: 0]

        ]
    }

    Map cmuidRegexpExperiment() {
        [id              : 'cmuid_regexp',
         renderedVariants : [
                 [name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$']]]
         ],
         reportingEnabled: true,
         description     : "Experiment description",
         author          : "Experiment owner",
         groups          : [],
         status          : 'DRAFT',
         reportingType   : 'BACKEND',
         eventDefinitions: [],
         variantNames    : ['v1'],
         editable        : false,
         origin          : 'STASH',
         measurements    : [lastDayVisits: 0]

        ]
    }

    Map hashVariantExperiment() {
        [id              : 'test_dev',
         renderedVariants : [
                 [name: 'v1', predicates: [[type: 'HASH', from: 0, to: 50]]],
                 [name: 'v2', predicates: [[type: 'HASH', from: 50, to: 100]]]
         ],
         reportingEnabled: true,
         groups          : [],
         status          : 'DRAFT',
         reportingType   : 'BACKEND',
         eventDefinitions: [],
         variantNames    : ['v1', 'v2'],
         editable        : false,
         origin          : 'STASH',
         measurements    : [lastDayVisits: 0]
        ]
    }

    Map sampleExperiment() {
        [id              : 'another_one',
         renderedVariants : [
                 [name: 'v1', predicates: [[type: 'HASH', from: 0, to: 50]]]
         ],
         reportingEnabled: true,
         description     : "Another one",
         author          : "Someone",
         groups          : [],
         status          : 'DRAFT',
         reportingType   : 'BACKEND',
         eventDefinitions: [],
         variantNames    : ['v1'],
         editable        : false,
         origin          : 'STASH',
         measurements    : [lastDayVisits: 0]

        ]
    }

    Map timeboundExperiment() {
        [id              : 'timed_internal_exp',
         activityPeriod  : [
                 activeFrom: '2017-11-03T10:15:30+02:00',
                 activeTo  : '2018-11-03T10:15:30+02:00'
         ],
         renderedVariants : [
                 [name: 'internal', predicates: [[type: 'INTERNAL']]]
         ],
         reportingEnabled: true,
         groups          : [],
         status          : 'ACTIVE',
         reportingType   : 'BACKEND',
         eventDefinitions: [],
         variantNames    : ['internal'],
         editable        : false,
         origin          : 'STASH',
         measurements    : [lastDayVisits: 0]

        ]
    }

    Map plannedExperiment() {
        [id              : 'planned_exp',
         activityPeriod  : [
                 "activeFrom": "2050-11-03T10:15:30+02:00",
                 "activeTo"  : "2060-11-03T10:15:30+02:00"
         ],
         renderedVariants : [
                 [name: 'internal', predicates: [[type: 'INTERNAL']]]
         ],
         reportingEnabled: true,
         groups          : [],
         status          : 'PLANNED',
         reportingType   : 'BACKEND',
         eventDefinitions: [],
         variantNames    : ['internal'],
         editable        : false,
         origin          : 'STASH',
         measurements    : [lastDayVisits: 0]

        ]
    }

    Map experimentFromThePast() {
        [id              : 'experiment_from_the_past',
         activityPeriod  : [
                 activeFrom: '2017-10-01T10:15:30+02:00',
                 activeTo  : '2017-11-01T10:15:30+02:00'
         ],
         renderedVariants : [
                 [name: 'internal', predicates: [[type: 'INTERNAL']]]
         ],
         reportingEnabled: true,
         groups          : [],
         status          : 'ENDED',
         reportingType   : 'BACKEND',
         eventDefinitions: [],
         variantNames    : ['internal'],
         editable        : false,
         origin          : 'STASH',
         measurements    : [lastDayVisits: 0]

        ]
    }

    Map pausedExperiment() {
        [id              : 'paused_experiment',
         activityPeriod  : [
                 activeFrom: '2050-11-03T10:15:30+02:00',
                 activeTo  : '2060-11-03T10:15:30+02:00'
         ],
         renderedVariants : [
                 [name: 'internal', predicates: [[type: 'INTERNAL']]]
         ],
         reportingEnabled: true,
         groups          : [],
         status          : 'PAUSED',
         reportingType   : 'BACKEND',
         eventDefinitions: [],
         variantNames    : ['internal'],
         editable        : false,
         origin          : 'STASH',
         measurements    : [lastDayVisits: 0]
        ]
    }
}