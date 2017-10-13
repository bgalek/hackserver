package pl.allegro.experiments.chi.chiserver

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.tech.hermes.client.HermesClient
import pl.allegro.tech.hermes.client.HermesClientBuilder
import pl.allegro.tech.hermes.client.HermesSender

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

@CompileStatic
@Configuration
class WireMockTestConfig {
    static String HERMES_PATH = "/hermes"

    @Bean(destroyMethod = 'stop')
    WireMockServer wireMock() {
        WireMockConfiguration config = wireMockConfig().dynamicPort()
        WireMockServer wireMockServer = new WireMockServer(config)
        wireMockServer.start()
        return wireMockServer
    }

    @Bean
    HermesClient hermesClient(
            @Qualifier("hermesSender") HermesSender sender,
            WireMockServer wireMock) {
        String hermesUrl = createWireMockUri(wireMock, HERMES_PATH)
        return HermesClientBuilder
                .hermesClient(sender)
                .withURI(URI.create(hermesUrl))
                .build()
    }

    private static String createWireMockUri(WireMockServer wireMock, String uri) {
        "http://localhost:${wireMock.port()}${uri}"
    }
}
