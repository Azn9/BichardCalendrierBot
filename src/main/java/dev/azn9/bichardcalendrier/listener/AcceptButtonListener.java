package dev.azn9.bichardcalendrier.listener;

import dev.azn9.bichardcalendrier.manager.EventManager;
import dev.azn9.bichardcalendrier.registry.UserDataRegistry;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.MessageReference;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.ThreadChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.MessageReferenceData;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class AcceptButtonListener extends DiscordListener<ButtonInteractionEvent> {

    private final EventManager eventManager;
    private final UserDataRegistry userDataRegistry;

    public AcceptButtonListener(EventManager eventManager, UserDataRegistry userDataRegistry) {
        super(ButtonInteractionEvent.class);

        this.eventManager = eventManager;
        this.userDataRegistry = userDataRegistry;
    }

    @Override
    public Mono<?> handle(ButtonInteractionEvent event) {
        String customId = event.getCustomId();

        if (!customId.startsWith("a-")) {
            return Mono.empty();
        }

        String[] split = customId.split("-");

        long threadId = Long.parseLong(split[1]);
        long messageId = Long.parseLong(split[2]);

        return event.deferReply().withEphemeral(true)
                .then(this.eventManager.getUserDataFromThread(threadId)
                        .publishOn(Schedulers.boundedElastic())
                        .flatMap(userData -> {
                            Mono<Void> deleteMessage = event.getInteraction()
                                    .getMessage()
                                    .map(message -> {
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

                            if (userData.getPoints() >= this.eventManager.getCurrentDay()) {
                                return event.createFollowup("Ce joueur a déjà eu sa réponse validée pour aujourd'hui !")
                                        .withEphemeral(true)
                                        .then(deleteMessage);
                            }

                            return Mono.just(userData)
                                    .publishOn(Schedulers.boundedElastic())
                                    .doOnNext(userdata -> {
                                        userdata.setPoints(userdata.getPoints() + 1);
                                        this.userDataRegistry.save(userdata);
                                    })
                                    .then(event.getClient().getChannelById(Snowflake.of(threadId))
                                            .ofType(ThreadChannel.class)
                                            .flatMap(threadChannel -> {
                                                return threadChannel.createMessage(EmbedCreateSpec.builder()
                                                                .title("Réponse acceptée !")
                                                                .color(Color.SEA_GREEN)
                                                                .build())
                                                        .withMessageReference(MessageReferenceData.builder()
                                                                .messageId(messageId)
                                                                .build());
                                            }))
                                    .then(deleteMessage)
                                    .then(event.createFollowup("Réponse validée").withEphemeral(true));
                        }));
    }

}
