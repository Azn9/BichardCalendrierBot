package dev.azn9.bichardcalendrier.listener;

import dev.azn9.bichardcalendrier.data.EventData;
import dev.azn9.bichardcalendrier.manager.EventManager;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OpencaseButtonListener extends DiscordListener<ButtonInteractionEvent> {

    private final EventManager eventManager;

    public OpencaseButtonListener(EventManager eventManager) {
        super(ButtonInteractionEvent.class);

        this.eventManager = eventManager;
    }

    @Override
    public Mono<?> handle(ButtonInteractionEvent event) {
        if (!event.getCustomId().equals("opencase")) {
            return Mono.empty();
        }

        int currentDay = this.eventManager.getCurrentDay();
        if (currentDay < 1 || currentDay > EventData.values().length) {
            return Mono.empty();
        }

        EventData eventData = EventData.values()[currentDay - 1];

        // TODO: validate pas déjà ouverte

        return event.reply()
                .withEmbeds(eventData.getMessage(event.getInteraction().getUser().getId().asLong()));
    }

}
