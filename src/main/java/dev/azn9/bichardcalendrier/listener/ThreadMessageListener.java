package dev.azn9.bichardcalendrier.listener;

import dev.azn9.bichardcalendrier.configuration.BotConfiguration;
import dev.azn9.bichardcalendrier.manager.EventManager;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.ThreadChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.MessageReferenceData;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ThreadMessageListener extends DiscordListener<MessageCreateEvent> {

    private final BotConfiguration botConfiguration;
    private final EventManager eventManager;

    public ThreadMessageListener(BotConfiguration botConfiguration, EventManager eventManager) {
        super(MessageCreateEvent.class);

        this.botConfiguration = botConfiguration;
        this.eventManager = eventManager;
    }

    @Override
    public Mono<?> handle(MessageCreateEvent event) {
        Message originalMessage = event.getMessage();

        return originalMessage
                .getChannel()
                .ofType(ThreadChannel.class)
                .flatMap(threadChannel -> {
                    return Mono.justOrEmpty(event.getMember())
                            .zipWith(Mono.just(threadChannel));
                })
                .flatMap(objects -> {
                    Member member = objects.getT1();
                    ThreadChannel channel = objects.getT2();

                    return this.eventManager.getUserThreadId(member.getId().asLong())
                            .filter(aLong -> aLong == channel.getId().asLong())
                            .flatMap(threadId -> {
                                return event.getClient()
                                        .getChannelById(Snowflake.of(this.botConfiguration.getGestionChannelId()))
                                        .ofType(MessageChannel.class)
                                        .flatMap(messageChannel -> {
                                            return originalMessage
                                                    .forward(messageChannel)
                                                    .flatMap(message -> {
                                                        return messageChannel.createMessage(EmbedCreateSpec.builder()
                                                                        .title("Nouveau message")
                                                                        .description("De <@%d> (<#%d>)".formatted(member.getId().asLong(), threadId))
                                                                        .color(Color.JAZZBERRY_JAM)
                                                                        .build())
                                                                .withComponents(ActionRow.of(
                                                                        Button.success("a-" + threadId + "-" + originalMessage.getId().asLong(), "Accepter"),
                                                                        Button.secondary("r-" + threadId + "-" + originalMessage.getId().asLong(), "Répondre"),
                                                                        Button.danger("d-" + threadId + "-" + originalMessage.getId().asLong(), "Refuser"),
                                                                        Button.secondary("ignore", "Ignorer")
                                                                ))
                                                                .withMessageReference(MessageReferenceData.builder()
                                                                        .messageId(message.getId().asLong())
                                                                        .build());
                                                    });
                                        });
                            });
                });
    }

}
