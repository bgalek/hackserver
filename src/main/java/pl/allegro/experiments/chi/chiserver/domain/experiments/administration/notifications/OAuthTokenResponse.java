package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthTokenResponse {

    private final String accessToken;
    private final String tokenType;

    @JsonCreator
    OAuthTokenResponse(@JsonProperty("access_token") String accessToken,
                       @JsonProperty("token_type") String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    String getAccessToken() {
        return accessToken;
    }

    String getTokenType() {
        return tokenType;
    }
}

