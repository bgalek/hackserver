package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;

class HipChatNotification {

    private final String message;
    private final String color;

    HipChatNotification(Notification notification) {
        this.color = notification.getSeverity() == Notification.Severity.MEDIUM ? "green" : "red";
        this.message =
                "Experiment " +
                        getExpLink(notification.getExpId()) +
                        " " + notification.getActionMessage() +
                        " by " + notification.getAuthor() +
                        ".";
    }

    private String getExpLink(String expId) {
        return "<a href='https://chi.allegrogroup.com/#/experiments/"+expId+"'>"+expId+"</a>";
    }

    @JsonProperty("message_format")
    String getMessageFormat() {
        return "html";
    }

    @JsonProperty("notify")
    boolean isNotify() {
        return true;
    }

    @JsonProperty
    String getColor() {
        return color;
    }

    @JsonProperty
    String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
