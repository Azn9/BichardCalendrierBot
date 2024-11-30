package dev.azn9.bichardcalendrier.listener;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.MessageReference;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class IgnoreButtonListener extends DiscordListener<ButtonInteractionEvent> {

    public IgnoreButtonListener() {
        super(ButtonInteractionEvent.class);
    }

    @Override
    public Mono<?> handle(ButtonInteractionEvent event) {
        if (!event.getCustomId().equals("ignore")) {
            return Mono.empty();
        }

        return event.getInteraction().getMessage().map(message -> {
            return message.getMessageReference()
                    .filter(messageReference -> {
                        return messageReference.getType() == MessageReference.Type.DEFAULT;
                    })
                    .map(messageReference -> {
                        return messageReference.getMessageId()
                                .map(snowflake -> {
                                    return event.getClient()
                                            .getMessageById(messageReference.getChannelId(), snowflake)
                                            .flatMap(Message::delete);
                                })
                                .orElse(Mono.empty());
                    })
                    .orElse(Mono.empty())
                    .then(message.delete());
        }).orElse(Mono.empty());
    }

}
