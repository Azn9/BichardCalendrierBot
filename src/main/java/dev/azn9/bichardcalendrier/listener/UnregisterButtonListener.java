package dev.azn9.bichardcalendrier.listener;

import dev.azn9.bichardcalendrier.manager.EventManager;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UnregisterButtonListener extends DiscordListener<ButtonInteractionEvent> {

    private final EventManager eventManager;

    public UnregisterButtonListener(EventManager eventManager) {
        super(ButtonInteractionEvent.class);

        this.eventManager = eventManager;
    }

    @Override
    public Mono<?> handle(ButtonInteractionEvent event) {
        if (!event.getCustomId().equals("unregister")) {
            return Mono.empty();
        }

        User user = event.getInteraction().getUser();
        long userId = user.getId().asLong();

        return event.deferReply()
                .withEphemeral(true)
                .then(this.eventManager.getUserThreadId(userId))
                .switchIfEmpty(Mono.just(-1L))
                .flatMap(aLong -> {
                    if (aLong < 0) {
                        return event.createFollowup("Vous n'êtes pas inscrit").withEphemeral(true);
                    }

                    return this.eventManager.removeData(userId)
                            .then(event.createFollowup("Vous avez bien été désinscrit").withEphemeral(true));
                });

    }

}
