package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

trait ExampleExperiments {
    Map internalExperiment() {
        [ id: 'internal_exp',
          variants: [
                  [ name: 'internal', predicates: [[type: 'INTERNAL']] ]
          ],
          reportingEnabled: false,
          groups: [],
          status: 'DRAFT',
          reportingType: 'BACKEND'
        ]
    }

    Map cmuidRegexpWithPhoneExperiment() {
        [ id: 'cmuid_regexp_with_phone',
          variants: [
                  [ name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$'], [type: 'DEVICE_CLASS', device: 'phone']] ]
          ],
          reportingEnabled: true,
          description: "Experiment description",
          author: "Experiment owner",
          groups: [],
          status: 'DRAFT',
          reportingType: 'BACKEND'
        ]
    }
    Map cmuidRegexpExperiment() {
        [ id: 'cmuid_regexp',
          variants: [
                  [ name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$']] ]
          ],
          reportingEnabled: true,
          description: "Experiment description",
          author: "Experiment owner",
          groups: [],
          status: 'DRAFT',
          reportingType: 'BACKEND'
        ]
    }

    Map hashVariantExperiment() {
        [ id: 'test_dev',
          variants: [
                  [ name: 'v1', predicates: [[type: 'HASH', from: 0, to: 50]] ],
                  [ name: 'v2', predicates: [[type: 'HASH', from: 50, to: 100]] ]
          ],
          reportingEnabled: true,
          groups: [],
          status: 'DRAFT',
          reportingType: 'BACKEND'
        ]
    }

    Map sampleExperiment() {
        [id: 'another_one',
         variants: [
                 [ name: 'v1', predicates: [[type: 'HASH', from: 0, to: 50]] ]
         ],
         reportingEnabled: true,
         description: "Another one",
         author: "Someone",
         groups: [],
         status: 'DRAFT',
         reportingType: 'BACKEND'
        ]
    }

    Map timeboundExperiment() {
        [ id: 'timed_internal_exp',
          activityPeriod: [
                  activeFrom: '2017-11-03T10:15:30+02:00',
                  activeTo: '2018-11-03T10:15:30+02:00'
          ],
          variants: [
                  [ name: 'internal', predicates: [[ type:'INTERNAL' ]] ]
          ],
          reportingEnabled: true,
          groups: [],
          status: 'ACTIVE',
          reportingType: 'BACKEND'
        ]
    }

    Map plannedExperiment() {
        [ id: 'planned_exp',
          activityPeriod: [
                  "activeFrom": "2050-11-03T10:15:30+02:00",
                  "activeTo": "2060-11-03T10:15:30+02:00"
          ],
          variants: [
                  [ name: 'internal', predicates: [[ type:'INTERNAL' ]] ]
          ],
          reportingEnabled: true,
          groups: [],
          status: 'PLANNED',
          reportingType: 'BACKEND'
        ]
    }

    Map experimentFromThePast() {
        [ id: 'experiment_from_the_past',
          activityPeriod: [
                  activeFrom: '2017-10-01T10:15:30+02:00',
                  activeTo: '2017-11-01T10:15:30+02:00'
          ],
          variants: [
                  [ name: 'internal', predicates: [[ type:'INTERNAL' ]] ]
          ],
          reportingEnabled: true,
          groups: [],
          status: 'ENDED',
          reportingType: 'BACKEND'
        ]
    }

    Map pausedExperiment() {
        [ id: 'paused_experiment',
          activityPeriod: [
                  activeFrom: '2050-11-03T10:15:30+02:00',
                  activeTo: '2060-11-03T10:15:30+02:00'
          ],
          variants: [
                  [ name: 'internal', predicates: [[ type:'INTERNAL' ]] ]
          ],
          reportingEnabled: true,
          groups: [],
          status: 'PAUSED',
          reportingType: 'BACKEND'
        ]
    }
}