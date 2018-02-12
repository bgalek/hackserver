package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import spock.lang.Ignore
import spock.lang.Specification

class ExperimentsDoubleRepositorySpec extends Specification {

    def "should return size as sum of internal repos"() {
        given:
        def fileRepo = Stub(FileBasedExperimentsRepository) {
            getAll() >> [experiment("y1"), experiment("y2"), experiment("y3")]
        }
        def mongoRepo = Stub(MongoExperimentsRepository) {
            getAll() >> [experiment("z"), experiment("z2")]
        }
        def repo = new ExperimentsDoubleRepository(fileRepo, mongoRepo)

        expect:
        repo.all.size() == 5
        repo.overridable.size() == 5
    }

    @Ignore
    def "shouldn't fail on internal repository refresh fail"() {
        given:
        def mongoRepo = Stub(MongoExperimentsRepository) {
            getAll() >> [experiment("y1")]
        }
        def repo = new ExperimentsDoubleRepository(Stub(FileBasedExperimentsRepository) {
            refresh() >> { throw new RuntimeException("refresh failed")}
        }, mongoRepo)

        when:
        try {
            repo.refresh()
        } catch( e ) {

        }

        then:
        repo.all.size() == 1
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
        new Experiment(id,
                [new ExperimentVariant("x", [])],
                "",
                "",
                "",
                [],
                false,
                null,
                null,
                null)
    }
}