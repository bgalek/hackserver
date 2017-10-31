package pl.allegro.experiments.chi.chiserver

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

@CompileStatic
@Configuration
class WireMockTestConfig {

    @Bean(destroyMethod = 'stop')
    WireMockServer wireMock() {
        WireMockConfiguration config = wireMockConfig().dynamicPort()
        WireMockServer wireMockServer = new WireMockServer(config)
        wireMockServer.start()
        return wireMockServer
    }

    private static String createWireMockUri(WireMockServer wireMock, String uri) {
        "http://localhost:${wireMock.port()}${uri}"
    }
}
