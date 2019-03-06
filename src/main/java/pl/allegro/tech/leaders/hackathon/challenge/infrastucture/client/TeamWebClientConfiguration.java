package pl.allegro.tech.leaders.hackathon.challenge.infrastucture.client;

import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
class TeamWebClientConfiguration {
    @Bean
    TeamWebClient TeamWebClient(
            @Value("${client.timeout}") int clientTimeoutMillis) {
        HttpClient httpClient = buildHttpClient(clientTimeoutMillis);
        WebClient webClient = buildWebClient(httpClient);
        return new TeamWebClient(webClient);
    }

    private WebClient buildWebClient(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private HttpClient buildHttpClient(int clientTimeoutMillis) {
        return HttpClient.create()
                .tcpConfiguration(client -> configureTcpClient(client, clientTimeoutMillis));
    }

    private TcpClient configureTcpClient(TcpClient client, int clientTimeoutMillis) {
        return client
                .option(CONNECT_TIMEOUT_MILLIS, clientTimeoutMillis)
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(clientTimeoutMillis, MILLISECONDS)));
    }
}
