package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

class OAuthProperties {

    private String encodedCredentials;
    private String serviceUrl;
    private String path;

    String getEncodedCredentials() {
        return encodedCredentials;
    }

    public void setEncodedCredentials(String encodedCredentials) {
        this.encodedCredentials = encodedCredentials;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setPath(String path) {
        this.path = path;
    }

    String getOAuthUrl() {
        return serviceUrl + path;
    }
}
