package dev.azn9.bichardcalendrier.listener;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.TextInput;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ReplyButtonListener extends DiscordListener<ButtonInteractionEvent> {

    public ReplyButtonListener() {
        super(ButtonInteractionEvent.class);
    }

    @Override
    public Mono<?> handle(ButtonInteractionEvent event) {
        String customId = event.getCustomId();

        if (!customId.startsWith("r-")) {
            return Mono.empty();
        }

        return event.presentModal("Message", customId, List.of(ActionRow.of(
                TextInput.paragraph("message", "Message").required(true)
        )));
    }

}
