package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;
import pl.allegro.tech.leaders.hackathon.registration.events.TeamRegisteredEvent;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

class RegistrationEventPublisher implements ApplicationListener<TeamRegisteredEvent>, Consumer<FluxSink<TeamRegisteredEvent>> {

    private final Executor executor;
    private final BlockingQueue<TeamRegisteredEvent> queue = new LinkedBlockingQueue<>();

    public RegistrationEventPublisher(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onApplicationEvent(TeamRegisteredEvent teamRegisteredEvent) {
        this.queue.offer(teamRegisteredEvent);
    }

    @Override
    public void accept(FluxSink<TeamRegisteredEvent> sink) {
        this.executor.execute(() -> {
            while (true) {
                try {
                    TeamRegisteredEvent event = queue.take();
                    sink.next(event);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
            }
        });
    }
}
