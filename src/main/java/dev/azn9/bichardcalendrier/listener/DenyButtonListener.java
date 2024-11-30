package dev.azn9.bichardcalendrier.listener;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.TextInput;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DenyButtonListener extends DiscordListener<ButtonInteractionEvent> {

    public DenyButtonListener() {
        super(ButtonInteractionEvent.class);
    }

    @Override
    public Mono<?> handle(ButtonInteractionEvent event) {
        String customId = event.getCustomId();

        if (!customId.startsWith("d-")) {
            return Mono.empty();
        }

        return event.presentModal("Raison", customId, List.of(ActionRow.of(
                TextInput.paragraph("reason", "Raison").required(true)
        )));
    }

}
