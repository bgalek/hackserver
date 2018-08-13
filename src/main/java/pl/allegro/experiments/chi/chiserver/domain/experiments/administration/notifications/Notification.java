package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class Notification {

    private final static String TYPE = "EMAIL";

    private final String callerId;
    private final Content content;
    private final String message;
    private final List<String> recipients;


    private Notification(String callerId, Content content, String message, List<String> recipients) {
        this.callerId = callerId;
        this.content = content;
        this.message = message;
        this.recipients = recipients;
    }

    @JsonProperty
    String getType() {
        return TYPE;
    }

    @JsonProperty
    String getCallerId() {
        return callerId;
    }

    @JsonProperty("Subject")
    Content getContent() {
        return content;
    }

    @JsonProperty
    String getMessage() {
        return message;
    }

    @JsonProperty("recipient")
    List<String> getRecipients() {
        return recipients;
    }


    static Notification from(String callerId, String subject, String message, List<String> recipients) {
        return new Notification(callerId, Content.from(subject), message, recipients);
    }

    static class Content {
        private final String subject;

        private Content(String subject) {
            this.subject = subject;
        }

        @JsonProperty("Subject")
        String getSubject() {
            return subject;
        }

        static Content from(String subject) {
            return new Content(subject);
        }
    }
}
