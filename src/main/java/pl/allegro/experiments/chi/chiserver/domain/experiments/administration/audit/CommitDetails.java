package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit;

import java.time.LocalDateTime;

public class CommitDetails {
    private final String author;
    private final LocalDateTime date;
    private final String changelog;

    CommitDetails(String author, LocalDateTime date, String changelog) {
        this.author = author;
        this.date = date;
        this.changelog = changelog;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getChangelog() {
        return changelog;
    }
}
