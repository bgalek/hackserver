package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

public class Notification {
    private final String expId;
    private final String actionMessage;
    private final String author;
    private final Severity severity;

    public Notification(String expId, String actionMessage, String author) {
        this(expId, actionMessage, author, Severity.MEDIUM);
    }

    public Notification(String expId, String actionMessage, String author, Severity severity) {
        this.expId = expId;
        this.actionMessage = actionMessage;
        this.author = author;
        this.severity = severity;
    }

    public String getExpId() {
        return expId;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public String getAuthor() {
        return author;
    }

    public Severity getSeverity() {
        return severity;
    }

    public enum Severity {
        MEDIUM, HIGH
    }

}
