package pl.allegro.tech.leaders.hackathon.registration

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import reactor.core.publisher.Mono

import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.util.SocketUtils.findAvailableTcpPort

@TestPropertySource(properties = ["healthcheck.rate=50", "healthcheck.delay=20"])
class HealthCheckIntgSpec extends IntegrationSpec {

    def 'should be able to get registered team with it\'s health status'() {
        given:
            Team team = createTeam('127.0.0.1')
        when:
            registerTeam(team.getName(), team.getRemoteAddress().getPort())
        then:
            healthStatusIsEqual(false)
        when:
            enableHealthEndpoint(team)
            sleep(100)
        then:
            healthStatusIsEqual(true)
    }

    private static Team createTeam(String ip) {
        new Team('teamName', new InetSocketAddress(InetAddress.getByName(ip), findAvailableTcpPort()))
    }

    private healthStatusIsEqual(status) {
        return webClient.get()
                .uri('/registration')
                .exchange()
                .expectBody()
                .jsonPath('$[0].health')
                .isEqualTo(status)
    }

    private void enableHealthEndpoint(Team team) {
        mockWebServer.start(team.remoteAddress.address, team.remoteAddress.port)
        def dispatcher = new Dispatcher() {
            @Override
            MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                return new MockResponse()
                        .setResponseCode(200)
                        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .setBody("ok")
            }
        }
        mockWebServer.setDispatcher(dispatcher)
    }

    MockWebServer mockWebServer = new MockWebServer()

    void cleanup() {
        mockWebServer.shutdown()
    }

    private WebTestClient.ResponseSpec registerTeam(String teamName, int port) {
        webClient
                .post()
                .uri('/registration')
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just("""{ "name": "$teamName", "port": ${port}}""".toString()), String.class)
                .exchange()
    }
}

