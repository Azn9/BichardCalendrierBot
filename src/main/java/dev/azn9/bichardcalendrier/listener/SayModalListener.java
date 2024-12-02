package dev.azn9.bichardcalendrier.listener;

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.TextInput;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SayModalListener extends DiscordListener<ModalSubmitInteractionEvent> {

    public SayModalListener() {
        super(ModalSubmitInteractionEvent.class);
    }

    @Override
    public Mono<?> handle(ModalSubmitInteractionEvent event) {
        if (!event.getCustomId().equals("say")) {
            return Mono.empty();
        }

        String message = event.getComponents(TextInput.class).getFirst().getValue().orElse("");

        return event.deferReply().withEphemeral(true)
                .then(event.getInteraction()
                        .getChannel()
                        .flatMap(messageChannel -> {
                            return messageChannel.createMessage(message);
                        }))
                .then(event.deleteReply());
    }
}
