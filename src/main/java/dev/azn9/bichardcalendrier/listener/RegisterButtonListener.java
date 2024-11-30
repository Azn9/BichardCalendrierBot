package dev.azn9.bichardcalendrier.listener;

import dev.azn9.bichardcalendrier.manager.EventManager;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RegisterButtonListener extends DiscordListener<ButtonInteractionEvent> {

    private static final Logger LOGGER = LogManager.getLogger(RegisterButtonListener.class);
    private final EventManager eventManager;

    @Autowired
    public RegisterButtonListener(EventManager eventManager) {
        super(ButtonInteractionEvent.class);

        this.eventManager = eventManager;
    }

    @Override
    public Mono<?> handle(ButtonInteractionEvent event) {
        if (!event.getCustomId().equals("register")) {
            return Mono.empty();
        }

        User user = event.getInteraction().getUser();
        long userId = user.getId().asLong();

        return event.deferReply()
                .withEphemeral(true)
                .then(this.eventManager.getUserThreadId(userId))
                .flatMap(aLong -> {
                    return event.createFollowup("Vous êtes déjà inscrit ! (<#%d>)".formatted(aLong))
                            .withEphemeral(true)
                            .thenReturn(aLong);
                })
                .switchIfEmpty(event.getInteraction().getChannel().ofType(TextChannel.class)
                        .flatMap(textChannel -> {
                            return this.eventManager.createData(user, textChannel);
                        })
                        .flatMap(aLong -> {
                            return event.createFollowup("Vous êtes maintenant inscrit ! (<#%d>)".formatted(aLong))
                                    .withEphemeral(true)
                                    .thenReturn(aLong);
                        }))
                .then()
                .onErrorResume(throwable -> {
                    RegisterButtonListener.LOGGER.error(throwable);

                    return event.createFollowup("Une erreur est survenue, veuillez réessayer !").withEphemeral(true)
                            .then();
                });
    }

}
