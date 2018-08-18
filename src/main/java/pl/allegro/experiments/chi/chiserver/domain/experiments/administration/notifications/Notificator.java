package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

@FunctionalInterface
public interface Notificator {

    void send(Notification message);
}
