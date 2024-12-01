package dev.azn9.bichardcalendrier.listener;

import dev.azn9.bichardcalendrier.manager.EventManager;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.rest.util.Permission;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ForcedayCommandListener extends DiscordListener<ChatInputInteractionEvent> {

    private final EventManager eventManager;

    public ForcedayCommandListener(EventManager eventManager) {
        super(ChatInputInteractionEvent.class);

        this.eventManager = eventManager;
    }

    @Override
    public Mono<?> handle(ChatInputInteractionEvent event) {
        if (!event.getCommandName().equals("forceday")) {
            return Mono.empty();
        }

        return event.deferReply().withEphemeral(true)
                .then(event.getInteraction().getMember().map(Mono::just).orElse(Mono.empty()))
                .filterWhen(member -> {
                    return member.getBasePermissions().map(permissions -> {
                        return permissions.contains(Permission.ADMINISTRATOR);
                    });
                })
                .flatMap(ignored -> {
                    return Mono.justOrEmpty(event.getOption("day"));
                })
                .flatMap(applicationCommandInteractionOption -> {
                    return Mono.justOrEmpty(applicationCommandInteractionOption.getValue());
                })
                .map(ApplicationCommandInteractionOptionValue::asLong)
                .flatMap(aLong -> {
                    return this.eventManager.switchDay(Math.toIntExact(aLong))
                            .then(event.createFollowup("o7"));
                });
    }
}
