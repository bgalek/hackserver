package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

class WireMockUtils {
    static void teachWireMockJson(String path, String jsonPath) {
        String json = getResourceAsString(jsonPath)
        stubFor(get(urlEqualTo(path))
                .willReturn(aResponse()
                .withStatus(200)
                .withBody(json)))
    }

    static String getResourceAsString(String resourceName) {
        this.getClass().getResource(resourceName).text
    }

    static String resourceUrl(String endpoint, wireMock) {
        "http://localhost:${wireMock.port()}$endpoint"
    }
}
