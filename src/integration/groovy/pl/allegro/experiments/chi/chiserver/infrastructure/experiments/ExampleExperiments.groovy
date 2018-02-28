package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

trait ExampleExperiments {
    Map internalExperiment() {
        [ id: 'internal_exp',
          variants: [
                  [ name: 'internal', predicates: [[type: 'INTERNAL']] ]
          ],
          reportingEnabled: false,
          groups: [],
          status: 'DRAFT'
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
          status: 'DRAFT'
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
          status: 'DRAFT'
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
         status: 'DRAFT'
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
          status: 'ACTIVE'
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
          status: 'PLANNED'
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
          status: 'ENDED'
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
          status: 'PAUSED'
        ]
    }
}