package dev.azn9.bichardcalendrier.listener;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.MessageReference;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.ThreadChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.MessageReferenceData;
import discord4j.rest.util.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReplyModalListener extends DiscordListener<ModalSubmitInteractionEvent> {

    private static final Logger log = LogManager.getLogger(ReplyModalListener.class);

    public ReplyModalListener() {
        super(ModalSubmitInteractionEvent.class);
    }

    @Override
    public Mono<?> handle(ModalSubmitInteractionEvent event) {
        String customId = event.getCustomId();

        if (!customId.startsWith("r-")) {
            return Mono.empty();
        }

        String[] split = customId.split("-");

        long threadId = Long.parseLong(split[1]);
        long messageId = Long.parseLong(split[2]);

        String replyMessage = event.getComponents(TextInput.class).getFirst().getValue().orElse("?");

        return event.deferReply().withEphemeral(true)
                .then(event.getClient().getChannelById(Snowflake.of(threadId))
                        .ofType(ThreadChannel.class)
                        .flatMap(threadChannel -> {
                            return threadChannel.createMessage(EmbedCreateSpec.builder()
                                            .title("Réponse des lutins")
                                            .description(replyMessage)
                                            .color(Color.GRAY_CHATEAU)
                                            .build())
                                    .withMessageReference(MessageReferenceData.builder()
                                            .messageId(messageId)
                                            .build());
                        }))
                .then(event.getInteraction().getMessage().map(message -> {
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
                }).orElse(Mono.empty()))
                .then(event.createFollowup("Réponse envoyée").withEphemeral(true));
    }
}
