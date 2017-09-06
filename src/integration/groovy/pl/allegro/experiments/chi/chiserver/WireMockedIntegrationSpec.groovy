package pl.allegro.experiments.chi.chiserver

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo

class WireMockedIntegrationSpec extends BaseIntegrationSpec {
    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRuleForIntegration(0) // 0 means starting wiremock on random port

    void teachWireMockJson(String path, String json, Map<String, List> queryParams) {
        teachWireMock(path, json, HttpStatus.OK, MediaType.APPLICATION_JSON_VALUE, 0, queryParams, [:])
    }

    void teachWireMockJson(String path, String json) {
        teachWireMock(path, json, HttpStatus.OK, MediaType.APPLICATION_JSON_VALUE, 0, [:], [:])
    }

    void teachWireMockJson(String path, String json, HttpStatus status) {
        teachWireMock(path, json, status, MediaType.APPLICATION_JSON_VALUE, 0, [:], [:])
    }

    void teachWireMockJson(String path, String json, long fixedDelay) {
        teachWireMock(path, json, HttpStatus.OK, MediaType.APPLICATION_JSON_VALUE, fixedDelay, [:], [:])
    }

    void teachWireMockJson(String path, String json, Map<String, List> queryParams, long fixedDelay) {
        teachWireMock(path, json, HttpStatus.OK, MediaType.APPLICATION_JSON_VALUE, fixedDelay, queryParams, [:])
    }

    void teachWireMockHtml(String path, String text) {
        teachWireMock(path, text, HttpStatus.OK, MediaType.TEXT_PLAIN_VALUE, 0, [:], [:])
    }

    void teachWireMockError(String path, HttpStatus status) {
        teachWireMock(path, "", status, MediaType.TEXT_PLAIN_VALUE, 0, [:], [:])
    }

    void teachWireMock(String path,
                       String text,
                       HttpStatus status,
                       String contentType,
                       long fixedDelay,
                       Map<String, List> queryParams,
                       Map<String, String> headers) {
        def stubbing = get(urlPathEqualTo(path))
                .willReturn(aResponse()
                .withStatus(status.value())
                .withFixedDelay(fixedDelay.intValue())
                .withHeader("Content-Type", contentType)
                .withBody(text))


        queryParams.each { it -> it.value.each { it2 -> stubbing.withQueryParam(it.key, equalTo("" + it2)) } }

        headers.entrySet().each { stubbing = stubbing.withHeader(it.key, equalTo(it.value))}

        wireMock.stubFor(stubbing)
    }

    int getWireMockPort() {
        wireMock.port()
    }

    def setup() {
        clean()
    }

    private void clean() {
        wireMock.resetMappings()
        wireMock.resetRequests()
    }
}