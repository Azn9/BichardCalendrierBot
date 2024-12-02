package dev.azn9.bichardcalendrier.listener;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.TextInput;
import discord4j.rest.util.Permission;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SayCommandListener extends DiscordListener<ChatInputInteractionEvent> {

    public SayCommandListener() {
        super(ChatInputInteractionEvent.class);
    }

    @Override
    public Mono<?> handle(ChatInputInteractionEvent event) {
        if (!event.getCommandName().equals("say")) {
            return Mono.empty();
        }

        return event.getInteraction()
                .getMember()
                .map(Mono::just)
                .orElse(Mono.empty())
                .filterWhen(member -> {
                    return member.getBasePermissions().map(permissions -> {
                        return permissions.contains(Permission.ADMINISTRATOR);
                    });
                })
                .flatMap(ignored -> {
                    return event.presentModal("Envoyer un message", "say", List.of(ActionRow.of(
                            TextInput.paragraph("message", "Message").required(true)
                    )));
                });
    }
}