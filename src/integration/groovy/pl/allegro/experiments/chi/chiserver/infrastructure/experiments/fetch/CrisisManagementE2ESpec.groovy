package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.utils.SampleClientExperiments
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository
import spock.lang.Shared

@TestPropertySource(properties = [
        "chi.crisisManagement.enabled: true",
        "chi.crisisManagement.whitelist: internal_exp"
])
class CrisisManagementE2ESpec extends BaseE2EIntegrationSpec{

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(port)

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    def setup() {
        WireMockUtils.teachWireMockJson("/experiments", 'some-experiments.json')
        WireMockUtils.teachWireMockJson("/invalid-experiments",'invalid-experiments.json')
    }

    def "should return only experiments listed on whitelist"() {
        given:
            fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
            fileBasedExperimentsRepository.secureRefresh()

        when:
            def experiments = fetchClientExperiments()

        then:
            experiments == [SampleClientExperiments.internalExperiment]
    }
}
