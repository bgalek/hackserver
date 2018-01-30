package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.FileBasedExperimentsRepositorySpec
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import spock.lang.Specification

class ExperimentsMultiRepositorySpec extends Specification {

    def "should return all/assignable size as sum of internal repos"() {
        given:
        def fileRepo = Stub(FileBasedExperimentsRepository) {
            getAll() >> [experiment("y")]
        }
        def mongoRepo = Stub(MongoExperimentsRepository) {
            getAll() >> [experiment("z")]
        }
        def repo = new ExperimentsDoubleRepository(fileRepo, mongoRepo)

        expect:
        repo.all.size() == 2
        repo.assignable.size() == 2
    }

    def "shouldn't fail on internal repository refresh fail"() {
        given:
        def mongoRepo = Stub(MongoExperimentsRepository) {
            getAll() >> []
        }
        def repo = new ExperimentsDoubleRepository(Stub(FileBasedExperimentsRepository) {
            refresh() >> { throw new RuntimeException("refresh failed")}
        }, mongoRepo)

        expect:
        repo.all.size() == 0
    }

    def "should save experiments to mongo repo"() {
        given:
        def fileRepo = Mock(FileBasedExperimentsRepository)
        def mongoRepo = Mock(MongoExperimentsRepository)
        def repo = new ExperimentsDoubleRepository(fileRepo, mongoRepo)
        def experiment = experiment("1234")

        when:
        repo.save(experiment)

        then:
        1 * mongoRepo.save(experiment)
        0 * fileRepo.save(experiment)
    }

    def experiment(id) {
        new Experiment(id, [new ExperimentVariant("x", [])], "", "", [], false, null, null)
    }
}