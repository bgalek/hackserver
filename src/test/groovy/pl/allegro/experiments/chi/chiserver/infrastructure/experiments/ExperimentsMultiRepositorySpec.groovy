package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.WritableExperimentsRepository
import spock.lang.Specification

import java.time.ZonedDateTime

class ExperimentsMultiRepositorySpec extends Specification {

    def "should return empty all and assignable without internal repositories"() {
        given:
        def repo = new ExperimentsMultiRepository([])

        expect:
        repo.all.size() == 0
        repo.assignable.size() == 0
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

    def "should return all/assignable size as sum of internal repos"() {
        given:
        def repo = new ExperimentsMultiRepository([simpleRepo(["y"]), simpleRepo(["z"])])

        expect:
        repo.all.size() == 2
        repo.assignable.size() == 2
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

    def "should get experiments from all working repositories"() {
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
        repo.all.size() == 4
        repo.assignable.size() == 4
        repo.getExperiment("xx").id == "xx"

        when:
        repo.refresh()

        then:
        repo.all.size() == 3
        repo.assignable.size() == 3
        repo.getExperiment("xx") is null
    }

    def "should throw exception on save with no writable repo configured"() {
        given:
        def repo = new ExperimentsMultiRepository([
                Stub(ExperimentsRepository), Stub(ExperimentsRepository)
        ])

        when:
        repo.save(experiment("1234"))

        then:
        thrown IllegalStateException
    }

    def "should save experiments to all writable repos"() {
        given:
        def repos = [Stub(WritableExperimentsRepository), Stub(WritableExperimentsRepository)]
        def repo = new ExperimentsMultiRepository(repos)
        def experiment = experiment("1234")

        when:
        repo.save(experiment)

        then:
        repos.each { it.save(experiment) }
    }

    def simpleRepo(experimentIds) {
        def experiments = experimentIds.collect { experiment(it) }
        return [
                getExperiment : { id -> experiments.find({ it.id == id }) },
                getAll        : { experiments },
                getAssignable : { experiments },
                refresh       : {}
        ] as ExperimentsRepository
    }

    def experiment(id) {
        new Experiment(id, [new ExperimentVariant("x", [])], "", "", [],
                false, new ActivityPeriod(ZonedDateTime.now().minusDays(10), ZonedDateTime.now().plusDays(10)), null)
    }
}