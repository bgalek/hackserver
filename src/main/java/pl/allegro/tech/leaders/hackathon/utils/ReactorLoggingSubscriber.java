package pl.allegro.tech.leaders.hackathon.utils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactorLoggingSubscriber<T> implements Subscriber<T> {
    private final Logger logger;
    private final String name;

    public ReactorLoggingSubscriber(String name, Class<?> loggerType) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerType);
    }

    public ReactorLoggingSubscriber(String name) {
        this(name, ReactorLoggingSubscriber.class);
    }

    @Override
    public void onSubscribe(Subscription s) {
        logger.info("Subscribed for {}", name);
    }

    @Override
    public void onNext(T t) {
        logger.info("Next {}: {}", name, t);
    }

    @Override
    public void onError(Throwable t) {
        logger.error("Error {}", name, t);
    }

    @Override
    public void onComplete() {
        logger.info("Completed {}", name);
    }
}
