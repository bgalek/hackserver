package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.utils.ExperimentFactory
import spock.lang.Specification

class ExperimentsDoubleRepositorySpec extends Specification {

    def "should return size as sum of internal repos"() {
        given:
        def fileRepo = new InMemoryExperimentsRepository(
                [experiment("y1"), experiment("y2"), experiment("y3")])
        def mongoRepo = new InMemoryExperimentsRepository(
                [experiment("z"), experiment("z2")])
        def repo = new ExperimentsDoubleRepository(fileRepo, mongoRepo)

        expect:
        repo.all.size() == 5
        repo.assignable().size() == 5
    }

    def "should save experiments to mongo repo"() {
        given:
        def fileRepo = new InMemoryExperimentsRepository([])
        def mongoRepo = new InMemoryExperimentsRepository([])
        def repo = new ExperimentsDoubleRepository(fileRepo, mongoRepo)
        def experiment = experiment("1234")

        when:
        repo.save(experiment)

        then:
        !fileRepo.getExperiment("1234").isPresent()
        mongoRepo.getExperiment("1234").isPresent()
    }

    def "first repo should have priority when calling getById"() {
        given:
        def fileRepo = new InMemoryExperimentsRepository([experiment("1", "from stash")])
        def mongoRepo = new InMemoryExperimentsRepository([experiment("1", "from mongo")])

        def repo = new ExperimentsDoubleRepository(fileRepo, mongoRepo)

        when:
        def e = repo.getExperiment("1")

        then:
        e.description == "from stash"
    }

    def "should not allow to save when experiment is from stash"() {
        given:
        def fileRepo = new InMemoryExperimentsRepository([experiment("1", "from stash")])
        def mongoRepo = new InMemoryExperimentsRepository([experiment("2", "from mongo")])

        def repo = new ExperimentsDoubleRepository(fileRepo, mongoRepo)

        when:
        repo.save(experiment("1", 'from stash, v2'))

        then:
        thrown IllegalArgumentException
    }

    def "should show origin"() {
        given:
        def fileRepo = new InMemoryExperimentsRepository([experiment("1", "from stash")])
        def mongoRepo = new InMemoryExperimentsRepository([experiment("2", "from mongo")])

        def repo = new ExperimentsDoubleRepository(fileRepo, mongoRepo)

        expect:
        repo.getOrigin("1") == 'stash'
        repo.getOrigin("2") == 'mongo'
    }

    def "first repo should have priority when calling getAll"() {
        given:
        def fileRepo = new InMemoryExperimentsRepository(
                [experiment("2", "from stash"), experiment("ambiguous", "from stash")])
        def mongoRepo = new InMemoryExperimentsRepository(
                [experiment("ambiguous", "from mongo"), experiment("3", "from mongo")])

        def repo = new ExperimentsDoubleRepository(fileRepo, mongoRepo)

        expect:
        repo.all.size() == 3
        repo.all.find { it.id == "ambiguous" }.description == "from stash"
    }

    Experiment experiment(id) {
        experiment(id, null)
    }

    Experiment experiment(id, description) {
        List<ExperimentVariant> variants = [new ExperimentVariant("x", [])]
        return ExperimentFactory.experimentWithVariants(id, variants).mutate()
                .description(description)
                .build()

    }
}