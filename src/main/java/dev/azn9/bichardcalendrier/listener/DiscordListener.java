package dev.azn9.bichardcalendrier.listener;

import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public abstract class DiscordListener<T extends Event> {

    private static final Logger LOGGER = LogManager.getLogger(DiscordListener.class);

    private final Class<T> eventClass;

    protected DiscordListener(Class<T> eventClass) {
        this.eventClass = eventClass;
    }

    public abstract Mono<?> handle(T event);

    public Publisher<?> registerEvent(EventDispatcher eventDispatcher) {
        return eventDispatcher.on(this.eventClass, this::handle)
                .onErrorResume(throwable -> Mono.fromRunnable(() -> {
                    DiscordListener.LOGGER.error(throwable);
                }));
    }
}
