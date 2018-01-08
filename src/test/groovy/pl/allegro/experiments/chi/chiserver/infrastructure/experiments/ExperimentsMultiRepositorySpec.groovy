package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import spock.lang.Specification

class ExperimentsMultiRepositorySpec extends Specification {

    def "should return empty all and active without internal repositories"() {
        given:
        def repo = new ExperimentsMultiRepository([])

        expect:
        repo.all.size() == 0
        repo.active.size() == 0
    }

    def "should return experiment existing in one repo"() {
        given:
        def repo = new ExperimentsMultiRepository([simpleRepo(["aa"])])

        expect:
        repo.getExperiment("aa").id == "aa"
    }

    def "should return null for non existing experiment"() {
        given:
        def repo = new ExperimentsMultiRepository([simpleRepo(["xx"])])

        expect:
        repo.getExperiment("anything") is null
    }

    def "should return all/active size as sum of internal repos"() {
        given:
        def repo = new ExperimentsMultiRepository([simpleRepo(["y"]), simpleRepo(["z"])])

        expect:
        repo.all.size() == 2
        repo.active.size() == 2
    }

    def "shouldn't fail on internal repository refresh fail"() {
        given:
        def repo = new ExperimentsMultiRepository([Mock(ExperimentsRepository) {
            refresh() >> { throw new RuntimeException("refresh failed") }
        }])

        expect:
        repo.all.size() == 0
    }

    def "should return last valid response after internal repository fail"() {
        given:
        def repo = new ExperimentsMultiRepository([Mock(ExperimentsRepository) {
            1 * refresh()
            refresh() >> { throw new RuntimeException("failing repo") }
            1 * getAll() >> [experiment("one")]
            getAll() >> { throw new RuntimeException("failing repo") }
        }])

        when:
        repo.getExperiment("one")
        repo.refresh()
        def second = repo.getExperiment("one")

        then:
        second.id == "one"
    }

    def "should get experiments from all working experiments"() {
        given:
        def repo = new ExperimentsMultiRepository([
                simpleRepo(["a"]),
                Mock(ExperimentsRepository) {
                    refresh() >> { throw new RuntimeException("refresh failed") }
                },
                simpleRepo(["b"]),
        ])

        expect:
        repo.all.size() == 2
    }

    def "should merge same experiments from many repos"() {
        given:
        def repo = new ExperimentsMultiRepository([
                simpleRepo(["a", "z"]),
                simpleRepo(["b", "z"]),
                simpleRepo(["c", "z"]),
        ])

        expect:
        repo.all.size() == 4
        repo.getExperiment("a").id == "a"
        repo.getExperiment("b").id == "b"
        repo.getExperiment("c").id == "c"
        repo.getExperiment("z").id == "z"
    }

    def "should remove experiments after they disappear from repo"() {
        given:
        def repo = new ExperimentsMultiRepository([
                simpleRepo(["1", "2"]),
                Mock(ExperimentsRepository) {
                    1 * getAll() >> [experiment("xx"), experiment("4")]
                    getAll() >> [experiment("4")]
                }
        ])

        expect:
        repo.getExperiment("xx").id == "xx"

        when:
        repo.refresh()

        then:
        repo.getExperiment("xx") is null
    }

    def simpleRepo(experimentIds) {
        def experiments = experimentIds.collect { experiment(it) }
        return [
                getExperiment: { id -> experiments.find({ it.id == id }) },
                getAll       : { experiments },
                getActive    : { experiments },
                refresh      : {}
        ] as ExperimentsRepository
    }

    def experiment(id) {
        new Experiment(id, [new ExperimentVariant("x", [])], "", "", false, null, null, null)
    }
}