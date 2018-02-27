package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExampleExperiments
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsDoubleRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository
import spock.lang.Shared

@TestPropertySource(properties = [
        "chi.crisisManagement.enabled: true",
        "chi.crisisManagement.whitelist: internal_exp"
])
@DirtiesContext
class CrisisManagementE2ESpec extends BaseIntegrationSpec implements ExampleExperiments {

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(port)

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    UserProvider userProvider

    def setup() {
        if (!experimentsRepository instanceof ExperimentsDoubleRepository) {
            throw new RuntimeException("We should test real repository, not the fake one")
        }
        WireMockUtils.teachWireMockJson("/experiments", '/some-experiments.json')
        WireMockUtils.teachWireMockJson("/invalid-experiments",'/invalid-experiments.json')
    }

    def "should return only experiments listed on whitelist"() {
        given:
            fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
            fileBasedExperimentsRepository.secureRefresh()

        when:
            def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
            response.statusCode.value() == 200
            response.body.size() == 1

        and:
            response.body.contains(internalExperiment())
    }
}
