package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.utils.SampleAdminExperiments
import pl.allegro.experiments.chi.chiserver.utils.SampleClientExperiments
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository
import spock.lang.Shared

import static pl.allegro.experiments.chi.chiserver.utils.SampleExperimentRequests.sampleExperimentCreationRequestProperties

class ExperimentsE2ESpec extends BaseE2EIntegrationSpec {

    @ClassRule
    @Shared
    WireMockRule wireMock = new WireMockRule(port)

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    def setup() {
        WireMockUtils.teachWireMockJson("/experiments", 'some-experiments.json')
        WireMockUtils.teachWireMockJson("/invalid-experiments", 'invalid-experiments.json')
    }

    def setupSpec() {
    }

    def "should return single of experiment loaded from the backing HTTP resource"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        and:
        def experiment = fetchExperiment('cmuid_regexp')


        expect:
        experiment == [
                id              : 'cmuid_regexp',
                renderedVariants: [[name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$']]]],
                variantNames    : ['v1'],
                reportingEnabled: true,
                description     : "Experiment description",
                author          : "Experiment owner",
                measurements    : [lastDayVisits: 0],
                groups          : [],
                status          : 'DRAFT',
                editable        : false,
                origin          : 'STASH',
                reportingType   : 'BACKEND',
                eventDefinitions: []
        ]
    }

    def "should return list of assignable experiments in API v.2 for Client"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        and:
        def experiments = fetchClientExperiments()

        expect:
        experiments.containsAll(
                SampleClientExperiments.internalExperiment,
                SampleClientExperiments.cmuidRegexpExperiment,
                SampleClientExperiments.plannedExperiment,
                SampleClientExperiments.hashVariantExperiment,
                SampleClientExperiments.sampleExperiment,
                SampleClientExperiments.timeboundExperiment)
        !experiments.contains(SampleClientExperiments.experimentFromThePast)
    }

    def "should return all experiments as measured for admin"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        and:
        def experiments = fetchExperiments()

        expect:
        experiments.containsAll(
                SampleAdminExperiments.internalExperiment,
                SampleAdminExperiments.plannedExperiment,
                SampleAdminExperiments.cmuidRegexpExperiment,
                SampleAdminExperiments.cmuidRegexpWithPhoneExperiment,
                SampleAdminExperiments.hashVariantExperiment,
                SampleAdminExperiments.sampleExperiment,
                SampleAdminExperiments.timeboundExperiment,
                SampleAdminExperiments.experimentFromThePast,
                SampleAdminExperiments.pausedExperiment)
    }

    def "should return last valid list when file is corrupted"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/invalid-experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        and:
        def experiments = fetchClientExperiments()

        expect:
        experiments.containsAll(
                SampleClientExperiments.internalExperiment,
                SampleClientExperiments.plannedExperiment,
                SampleClientExperiments.cmuidRegexpExperiment,
                SampleClientExperiments.hashVariantExperiment,
                SampleClientExperiments.sampleExperiment,
                SampleClientExperiments.timeboundExperiment,
                SampleClientExperiments.cmuidRegexpWithPhoneExperiment)
    }

    def "should return BAD_REQUEST when predicate type is incorrect"() {
        given:
        def request = [
                id              : "some2",
                description     : "desc",
                variants        : [
                        [
                                name      : "v1",
                                predicates: [[type: "NOT_SUPPORTED_TYPE"]]
                        ]
                ],
                groups          : [],
                reportingEnabled: true
        ]

        when:
        createExperiment(request)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode.value() == 400
    }

    def "should return BAD_REQUEST when no percentage"() {
        given:
        def request = sampleExperimentCreationRequestProperties([percentage: null])

        when:
        createExperiment(request)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode.value() == 400
    }

    def "should append experiment group if experiment is in group"() {
        given:
        def experiment1 = startedExperiment()

        and:
        def groupId = UUID.randomUUID().toString()
        def experiment2 = pairedExperiment([experiment1.id], groupId)

        expect:
        fetchExperiment(experiment2.id as String).experimentGroup == [
                id: groupId,
                experiments: [experiment1.id, experiment2.id],
                salt: experiment1.id
        ]

        and:
        fetchExperiment(experiment2.id as String).experimentGroup == [
                id: groupId,
                experiments: [experiment1.id, experiment2.id],
                salt: experiment1.id
        ]
    }

}
