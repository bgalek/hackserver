package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class ClientExperimentsV3E2ESpec extends BaseE2EIntegrationSpec {

    @Unroll
    def "should ignore grouped experiments in client api version #apiVersion"() {
        given:
        def firstExperiment = draftExperiment()
        def secondExperiment = draftExperiment()
        createExperimentGroup([firstExperiment.id, secondExperiment.id])

        and:
        startExperiment(firstExperiment.id as String, 30)
        startExperiment(secondExperiment.id as String, 30)

        when:
        def experimentIds = fetchClientExperiments(apiVersion).collect { it.id }

        then:
        !experimentIds.contains(firstExperiment.id)
        !experimentIds.contains(secondExperiment.id)

        where:
        apiVersion << ['v1', 'v2']
    }

    def "should return grouped experiments in client api version #description"() {
        given:
        def firstExperiment = startedExperiment()
        def secondExperiment = draftExperiment()

        and:
        createExperimentGroup([firstExperiment.id, secondExperiment.id])

        and:
        startExperiment(secondExperiment.id as String, 30)

        when:
        def experiments = fetchClientExperiments(apiVersion)

        then:
        experiments.find { it.id == firstExperiment.id }.variants == [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt  : firstExperiment.id
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 0, to: 10]],
                                        salt  : firstExperiment.id
                                ]
                        ]
                ]
        ]

        and:
        experiments.find { it.id == secondExperiment.id }.variants == [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt  : firstExperiment.id
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 10, to: 20]],
                                        salt  : firstExperiment.id
                                ]
                        ]
                ]
        ]

        where:
        description | apiVersion
        'v3'        | 'v3'
        'latest'    | ''
    }

    @Unroll
    def "should ignore #status experiments when rendering grouped experiments"() {
        given:
        def firstExperiment = experimentWithStatus(status)
        def secondExperiment = draftExperiment()
        createExperimentGroup([firstExperiment.id, secondExperiment.id])

        when:
        def experimentIds = fetchClientExperiments().collect { it.id }

        then:
        !experimentIds.contains(firstExperiment.id)

        where:
        status << allExperimentStatusValuesExcept(DRAFT, ACTIVE, FULL_ON)
    }

    def "should not ignore DRAFT and ACTIVE experiments when rendering grouped experiments"() {
        given:
        def firstExperiment = startedExperiment()
        def secondExperiment = draftExperiment()
        createExperimentGroup([firstExperiment.id, secondExperiment.id])

        when:
        def experiments = fetchClientExperiments()

        then:
        experiments.collect {it.id}.contains(firstExperiment.id)
        experiments.collect {it.id}.contains(secondExperiment.id)

        and:
        experiments.find { it.id == firstExperiment.id }.variants == [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt  : firstExperiment.id
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 0, to: 10]],
                                        salt  : firstExperiment.id
                                ]
                        ]
                ]
        ]
    }

    def "should render grouped experiments ranges in deterministic manner"() {
        given:
        def experiment1 = startedExperiment([percentage: 5])
        def experiment2 = draftExperiment([percentage: 10])
        def experiment3 = draftExperiment([percentage: 20])
        def experiment4 = draftExperiment([percentage: 15])

        and:
        def experimentIds = [experiment2.id, experiment3.id, experiment4.id, experiment1.id]
        createExperimentGroup(experimentIds as List<String>)

        and:
        def expectedExperiment1Variants = expectedExperiment1State(experiment1.id as String)
        def expectedExperiment2Variants = expectedExperiment2State(experiment1.id as String)
        def expectedExperiment3Variants = expectedExperiment3State(experiment1.id as String)
        def expectedExperiment4Variants = expectedExperiment4State(experiment1.id as String)

        when:
        def experiments = fetchClientExperiments()

        then:
        experiments.collect {it.id}.containsAll(experimentIds)
        experiments.find {it.id == experiment1.id}.variants == expectedExperiment1Variants

        when:
        startExperiment(experiment2.id as String, 30)
        experiments = fetchClientExperiments()

        then:
        experiments.collect {it.id}.containsAll(experimentIds)

        and:
        experiments.find {it.id == experiment1.id}.variants == expectedExperiment1Variants
        experiments.find {it.id == experiment2.id}.variants == expectedExperiment2Variants

        when:
        startExperiment(experiment3.id as String, 30)
        experiments = fetchClientExperiments()

        then:
        experiments.collect {it.id}.containsAll(experimentIds)

        and:
        experiments.find {it.id == experiment1.id}.variants == expectedExperiment1Variants
        experiments.find {it.id == experiment2.id}.variants == expectedExperiment2Variants
        experiments.find {it.id == experiment3.id}.variants == expectedExperiment3Variants

        when:
        startExperiment(experiment4.id as String, 30)
        experiments = fetchClientExperiments()

        then:
        experiments.collect {it.id}.containsAll(experimentIds)

        and:
        experiments.find {it.id == experiment1.id}.variants == expectedExperiment1Variants
        experiments.find {it.id == experiment2.id}.variants == expectedExperiment2Variants
        experiments.find {it.id == experiment3.id}.variants == expectedExperiment3Variants
        experiments.find {it.id == experiment4.id}.variants == expectedExperiment4Variants

        when:
        pauseExperiment(experiment2.id as String)
        experiments = fetchClientExperiments()

        then:
        experimentIds.remove(experiment2.id)
        experiments.collect {it.id}.containsAll(experimentIds)
        !experiments.collect {it.id}.contains(experiment2.id)

        and:
        experiments.find {it.id == experiment1.id}.variants == expectedExperiment1Variants
        experiments.find {it.id == experiment3.id}.variants == expectedExperiment3Variants
        experiments.find {it.id == experiment4.id}.variants == expectedExperiment4Variants

        when:
        stopExperiment(experiment3.id as String)
        experiments = fetchClientExperiments()

        then:
        experimentIds.remove(experiment3.id)
        experiments.collect {it.id}.containsAll(experimentIds)
        !experiments.collect {it.id}.contains(experiment2.id)
        !experiments.collect {it.id}.contains(experiment3.id)

        and:
        experiments.find {it.id == experiment1.id}.variants == expectedExperiment1Variants
        experiments.find {it.id == experiment4.id}.variants == expectedExperiment4Variants

        when:
        pauseExperiment(experiment1.id as String)
        experiments = fetchClientExperiments()

        then:
        experimentIds.remove(experiment1.id)
        experiments.collect {it.id}.containsAll(experimentIds)
        !experiments.collect {it.id}.contains(experiment1.id)
        !experiments.collect {it.id}.contains(experiment2.id)
        !experiments.collect {it.id}.contains(experiment3.id)

        and:
        experiments.find {it.id == experiment4.id}.variants == expectedExperiment4Variants

        when:
        resumeExperiment(experiment1.id as String)
        experiments = fetchClientExperiments()

        then:
        experimentIds.add(experiment1.id)
        experiments.collect {it.id}.containsAll(experimentIds)
        !experiments.collect {it.id}.contains(experiment2.id)
        !experiments.collect {it.id}.contains(experiment3.id)

        and:
        experiments.find {it.id == experiment1.id}.variants == expectedExperiment1Variants
        experiments.find {it.id == experiment4.id}.variants == expectedExperiment4Variants

        when:
        resumeExperiment(experiment2.id as String)
        experiments = fetchClientExperiments()

        then:
        experimentIds.add(experiment2.id)
        experiments.collect {it.id}.containsAll(experimentIds)
        !experiments.collect {it.id}.contains(experiment3.id)

        and:
        experiments.find {it.id == experiment1.id}.variants == expectedExperiment1Variants
        experiments.find {it.id == experiment2.id}.variants == expectedExperiment2Variants
        experiments.find {it.id == experiment4.id}.variants == expectedExperiment4Variants
    }

    List expectedExperiment1State(String experimentId) {
        [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 95, to: 100]],
                                        salt  : experimentId
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 0, to: 5]],
                                        salt  : experimentId
                                ]
                        ]
                ]
        ]
    }

    List expectedExperiment2State(String experimentId) {
        [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt  : experimentId
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 5, to: 15]],
                                        salt  : experimentId
                                ]
                        ]
                ]
        ]
    }

    List expectedExperiment3State(String experimentId) {
        [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 80, to: 100]],
                                        salt  : experimentId
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 15, to: 35]],
                                        salt  : experimentId
                                ]
                        ]
                ]
        ]
    }

    List expectedExperiment4State(String experimentId) {
        [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 85, to: 100]],
                                        salt  : experimentId
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 35, to: 50]],
                                        salt  : experimentId
                                ]
                        ]
                ]
        ]
    }
}