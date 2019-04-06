package pl.allegro.tech.leaders.hackathon.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
class StaticResourcesConfiguration {

    private final Resource indexHtml;
    private final ClassPathResource location;

    StaticResourcesConfiguration(@Value("classpath:/static/index.html") Resource indexHtml) {
        this.indexHtml = indexHtml;
        this.location = new ClassPathResource("static/");
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.resources("/**", location)
                .andRoute(GET("/"), request -> ServerResponse
                        .ok()
                        .contentType(TEXT_HTML)
                        .syncBody(indexHtml)
                );
    }
}