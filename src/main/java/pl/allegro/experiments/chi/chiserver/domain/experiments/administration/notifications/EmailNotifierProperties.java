package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

import java.util.List;

public class EmailNotifierProperties {

    private String emailNotificationUrl;
    private String path;
    private String callerId;
    private List<String> recipients;

    public void setEmailNotificationUrl(String emailNotificationUrl) {
        this.emailNotificationUrl = emailNotificationUrl;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getEmailNotificationServerUrl() {
        return emailNotificationUrl;
    }

    public String getCallerId() {
        return callerId;
    }

    public String getNotificationUrl() {
        return emailNotificationUrl + path;
    }

    public List<String> getRecipients() {
        return recipients;
    }
}

