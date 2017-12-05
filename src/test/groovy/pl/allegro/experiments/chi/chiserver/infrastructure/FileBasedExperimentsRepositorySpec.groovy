package pl.allegro.experiments.chi.chiserver.infrastructure

import pl.allegro.experiments.chi.chiserver.domain.InternalPredicate

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
        def hashExperiment = experimentsRepository.getExperiment("test_dev")
        def regexpExperiment = experimentsRepository.getExperiment("cmuid_regexp")
        def internalExperiment = experimentsRepository.getExperiment("internal_exp")
        def timedExp = experimentsRepository.getExperiment("timed_internal_exp")
        def phoneExperiment = experimentsRepository.getExperiment("cmuid_regexp_with_phone")

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
            activeFrom == ZonedDateTime.of(2017, 11, 03, 10, 15, 30, 0, ZoneId.of('+02:00'))
            activeTo == ZonedDateTime.of(2018, 11, 03, 10, 15, 30, 0, ZoneId.of('+02:00'))
        }
    }

    def "should remove experiments not existing in the source file"(){
        given:
        def repo = new FileBasedExperimentsRepository('some-experiments.json',
                [ExperimentFactory.simple50("obsolete", "vA")],
                {p -> new ClasspathContentLoader().localResource(p)})

        expect:
        !repo.getExperiment("obsolete") != null
    }

    FileBasedExperimentsRepository newFileBasedExperimentsRepository(String jsonUrl) {
        new FileBasedExperimentsRepository(jsonUrl, { p -> new ClasspathContentLoader().localResource(p)})
    }
}
