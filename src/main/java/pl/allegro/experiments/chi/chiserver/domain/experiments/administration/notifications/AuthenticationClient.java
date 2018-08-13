package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
class AuthenticationClient implements TokenRetriever {

    private final OAuthProperties oAuthProperties;
    private final RestTemplate restTemplate;

    AuthenticationClient(OAuthProperties oAuthProperties, RestTemplate restTemplate) {
        this.oAuthProperties = oAuthProperties;
        this.restTemplate = restTemplate;
    }

    public OAuthTokenResponse getOAuthTokenInformation() {
        Objects.requireNonNull(oAuthProperties.getEncodedCredentials(),
                (() -> "oauth.encodedCredentials property must be provided"));
        try {
            return restTemplate.postForEntity(oAuthProperties.getOAuthUrl(), getOAuthTokenRequest(), OAuthTokenResponse.class).getBody();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error during fetching oAuth token from authentication service", e);
        }
    }

    private HttpEntity<MultiValueMap<String, String>> getOAuthTokenRequest() {
        HttpHeaders headers = new HttpHeaders();
        setHeaders(headers);
        return new HttpEntity<>(getRequestBody(), headers);
    }

    private void setHeaders(HttpHeaders headers) {
        headers.add(HttpHeaders.AUTHORIZATION,"Basic " + oAuthProperties.getEncodedCredentials());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    private MultiValueMap<String, String> getRequestBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        return body;
    }
}
