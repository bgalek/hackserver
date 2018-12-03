package pl.allegro.experiments.chi.chiserver.application;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping(value = {"/api/env"}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
class EnvController {

    private final Map<String, Object> config;

    @Autowired
    public EnvController(
            @Value("${security.oauth2.client.userAuthorizationUri}") String userAuthorizationUri,
            @Value("${security.enabled}") boolean securityEnabled,
            @Value("${security.oauth2.client.clientId}") String oauthClientId) {
        this.config = new ImmutableMap.Builder()
                        .put("userAuthorizationUri", userAuthorizationUri)
                        .put("securityEnabled", securityEnabled)
                        .put("oauthClientId", oauthClientId)
                        .build();

    }

    @GetMapping
    Map<String, Object> getAppConfig() {
        return config;
    }
}
