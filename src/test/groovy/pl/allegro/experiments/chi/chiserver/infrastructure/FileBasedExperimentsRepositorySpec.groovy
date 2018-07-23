package pl.allegro.experiments.chi.chiserver.infrastructure

import pl.allegro.experiments.chi.chiserver.domain.experiments.InternalPredicate
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository
import pl.allegro.experiments.chi.chiserver.utils.ExperimentFactory
import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime

class FileBasedExperimentsRepositorySpec extends Specification {

    def "should ignore all experiments in a file when some experiment is in wrong format"() {
        expect:
        newFileBasedExperimentsRepository('file-containing-some-legacy-experiments.json').all == []
    }

    def "should load all experiments from the given file on classpath"(){
        given:
        def experimentsRepository = newFileBasedExperimentsRepository('some-experiments.json')

        expect:
        def hashExperiment = experimentsRepository.getExperiment("test_dev").get()
        def regexpExperiment = experimentsRepository.getExperiment("cmuid_regexp").get()
        def internalExperiment = experimentsRepository.getExperiment("internal_exp").get()
        def timedExp = experimentsRepository.getExperiment("timed_internal_exp").get()
        def phoneExperiment = experimentsRepository.getExperiment("cmuid_regexp_with_phone").get()
        def customParamExperiment = experimentsRepository.getExperiment("custom_param_test").get()

        with(phoneExperiment) {
            id == "cmuid_regexp_with_phone"
            variants.size() == 1
            variants[0].name == "v1"
            variants[0].predicates[0].pattern.toString() == ".*[0-3]\$"
            variants[0].predicates[1].device == "phone"
        }

        with(hashExperiment) {
            id == "test_dev"
            variants.size() == 2
            variants[0].name == "v1"
            variants[0].predicates[0].hashRange.from == 0
            variants[0].predicates[0].hashRange.to == 50

            variants[1].name == "v2"
            variants[1].predicates[0].hashRange.from == 50
            variants[1].predicates[0].hashRange.to == 100
        }

        with(regexpExperiment) {
            id == "cmuid_regexp"
            variants.size() == 1
            variants[0].name == "v1"
            variants[0].predicates[0].pattern.toString() == ".*[0-3]\$"
        }

        with(internalExperiment) {
            id == "internal_exp"
            variants.size() == 1
            variants[0].name == "internal"
            variants[0].predicates[0] instanceof InternalPredicate
        }

        with(timedExp) {
            id == "timed_internal_exp"
            activityPeriod.activeFrom == ZonedDateTime.of(2017, 11, 03, 10, 15, 30, 0, ZoneId.of('+02:00'))
            activityPeriod.activeTo == ZonedDateTime.of(2018, 11, 03, 10, 15, 30, 0, ZoneId.of('+02:00'))
        }

        with(customParamExperiment) {
            id == "custom_param_test"
            variants.size() == 1
            variants[0].predicates.size() == 1
            variants[0].predicates[0].name == "myCustomParamName"
            variants[0].predicates[0].value == "myCustomParamValue"

        }
    }

    def "should remove experiments not existing in the source file"(){
        given:
        def repo = new FileBasedExperimentsRepository(
                'some-experiments.json',
                { p -> new ClasspathContentLoader().localResource(p) },
                new JsonConfig().jsonConverter(),
                [ExperimentFactory.simple50("obsolete", "vA")])

        expect:
        !repo.getExperiment("obsolete") != null
    }

    FileBasedExperimentsRepository newFileBasedExperimentsRepository(String jsonUrl) {
        new FileBasedExperimentsRepository(
                jsonUrl,
                { p -> new ClasspathContentLoader().localResource(p) },
                new JsonConfig().jsonConverter(), [])
    }
}
