package dev.azn9.bichardcalendrier.listener;

import dev.azn9.bichardcalendrier.data.UserData;
import dev.azn9.bichardcalendrier.registry.UserDataRegistry;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.emoji.Emoji;
import discord4j.core.object.entity.channel.ThreadChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class ForcesendcaseCommandListener extends DiscordListener<ChatInputInteractionEvent> {

    private final UserDataRegistry userDataRegistry;

    public ForcesendcaseCommandListener(UserDataRegistry userDataRegistry) {
        super(ChatInputInteractionEvent.class);

        this.userDataRegistry = userDataRegistry;
    }

    @Override
    public Mono<?> handle(ChatInputInteractionEvent event) {
        if (!event.getCommandName().equals("forcesendcase")) {
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
                    LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"))
                            .withHour(7)
                            .withMinute(59);
                    Instant time = localDateTime.atZone(ZoneId.of("Europe/Paris")).toInstant();

                    return Mono.when(Mono.fromCallable(this.userDataRegistry::findAll)
                                    .flatMapMany(Flux::fromIterable)
                                    .filter(UserData::isRegistered)
                                    .flatMap(userData -> {
                                        return event.getClient()
                                                .getChannelById(Snowflake.of(userData.getThreadId()))
                                                .ofType(ThreadChannel.class)
                                                .flatMap(threadChannel -> {
                                                    return threadChannel.getMessagesAfter(Snowflake.of(time))
                                                            .filter(message -> {
                                                                return message.getAuthor().isPresent()
                                                                        && message.getAuthor().get().isBot();
                                                            })
                                                            .filter(message -> {
                                                                return message.getEmbeds().size() == 1
                                                                        && message.getEmbeds().getFirst().getTitle().map(s -> s.equals("Nouvelle journée !")).orElse(false);
                                                            })
                                                            .collectList()
                                                            .flatMap(messages -> {
                                                                if (messages.isEmpty()) {
                                                                    return threadChannel.createMessage(EmbedCreateSpec.builder()
                                                                                    .title("Nouvelle journée !")
                                                                                    .description("Il est l'heure d'ouvrir une nouvelle case ! Penses à réaliser le défi aujourd'hui et à le faire valider dans ce channel !")
                                                                                    .color(Color.MOON_YELLOW)
                                                                                    .build())
                                                                            .withComponents(ActionRow.of(
                                                                                    Button.primary("opencase", Emoji.unicode("\uD83C\uDF81"), "Ouvrir la case")
                                                                            )).withContent("<@%d>".formatted(userData.getUserId()));
                                                                }

                                                                return Mono.empty();
                                                            });
                                                });
                                    }))
                            .then(event.createFollowup("o7").withEphemeral(true));
                });
    }
}
