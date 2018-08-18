package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

public class Notification {
    private final String expId;
    private final String actionMessage;
    private final String author;

    public Notification(String expId, String actionMessage, String author) {
        this.expId = expId;
        this.actionMessage = actionMessage;
        this.author = author;
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


}
