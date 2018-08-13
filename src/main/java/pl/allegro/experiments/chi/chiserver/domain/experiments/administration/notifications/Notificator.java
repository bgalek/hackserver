package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

public interface Notificator {

    void send(String subject, String message);
}
