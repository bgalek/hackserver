package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit;

import java.time.ZonedDateTime;

public class CommitDetails {
    private final String author;
    private final ZonedDateTime date;
    private final String changelog;

    CommitDetails(String author, ZonedDateTime date, String changelog) {
        this.author = author;
        this.date = date;
        this.changelog = changelog;
    }

    public String getAuthor() {
        return author;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public String getChangelog() {
        return changelog;
    }
}
