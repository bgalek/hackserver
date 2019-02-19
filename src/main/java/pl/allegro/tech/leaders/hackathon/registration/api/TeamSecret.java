package pl.allegro.tech.leaders.hackathon.registration.api;

import java.util.Objects;

public interface TeamSecret {

    boolean isValid();

    String getSecret();

    static TeamSecret fromAuthorizationHeader(String authorization) {
        if (Objects.isNull(authorization) || authorization.isEmpty() || authorization.isBlank()) {
            return TeamSecret.empty();
        }
        String separator = " ";
        String[] headerTokenPart = authorization.split(separator, 2);
        if (headerTokenPart.length < 2 || headerTokenPart[headerTokenPart.length - 1].isBlank()) {
            return TeamSecret.empty();
        }
        return new ValidTeamSecret(headerTokenPart[1]);
    }

    static TeamSecret empty() {
        return new EmptyTeamSecret();
    }

    class EmptyTeamSecret implements TeamSecret {
        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public String getSecret() {
            return null;
        }
    }

    class ValidTeamSecret implements TeamSecret {
        private String secret;

        ValidTeamSecret(String secret) {
            this.secret = secret;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public String getSecret() {
            return secret;
        }
    }
}
