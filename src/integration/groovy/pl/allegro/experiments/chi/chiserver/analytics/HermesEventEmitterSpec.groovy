package pl.allegro.experiments.chi.chiserver.analytics

import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import com.github.tomakehurst.wiremock.matching.UrlPattern
import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.WireMockTestConfig
import pl.allegro.experiments.chi.chiserver.analytics.infrastructure.HermesEventEmitter
import pl.allegro.experiments.chi.chiserver.analytics.infrastructure.HermesTopicProperties

import java.time.Instant

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern
import static org.springframework.http.HttpStatus.CREATED

class HermesEventEmitterSpec extends BaseIntegrationSpec {
    @Autowired
    HermesEventEmitter hermesEventEmitter

    @Autowired
    HermesTopicProperties hermesTopicProperties

    def 'should post to hermes when publishing experiment assignment'() {
        given:
            hermesAcceptsPostRequests()

        when:
            hermesEventEmitter.emit(sampleExperimentAssignment()).get()

        then:
            verifyHermesWasHit()
    }

    def 'should post to hermes experiment assignment with empty fields'() {
        given:
            hermesAcceptsPostRequests()

        when:
            hermesEventEmitter.emit(emptyExperimentAssignment()).get()

        then:
            verifyHermesWasHit()
    }

    void hermesAcceptsPostRequests() {
        wireMockServer.stubFor(post(urlPathEqualTo(getHermesUrlPath()))
            .willReturn(
                aResponse().withStatus(CREATED.value())
            )
        )
    }

    private void verifyHermesWasHit() {
        wireMockServer.verify(
                newRequestPattern(
                        RequestMethod.POST,
                        new UrlPattern(
                                new EqualToPattern(getHermesUrlPath()),
                                false
                        )
                )
        )
    }

    private String getHermesUrlPath() {
        return WireMockTestConfig.HERMES_PATH + '/topics/' + hermesTopicProperties.topic
    }

    private static ExperimentAssignmentEvent sampleExperimentAssignment() {
        return new ExperimentAssignmentEvent(
                'userId',
                'userCmId',
                'experimentId',
                'variantName',
                true,
                true,
                "iphone",
                Instant.now()
        )
    }

    private static ExperimentAssignmentEvent emptyExperimentAssignment() {
        return new ExperimentAssignmentEvent(
                null,
                null,
                'experimentId',
                'variantName',
                null,
                true,
                null,
                Instant.now()
        )
    }
}
