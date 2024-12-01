package dev.azn9.bichardcalendrier.listener;

import dev.azn9.bichardcalendrier.configuration.BotConfiguration;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.service.ApplicationService;
import discord4j.rest.util.Color;
import discord4j.rest.util.PermissionSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotStartupListener extends DiscordListener<ReadyEvent> {

    private final BotConfiguration botConfiguration;

    @Autowired
    public BotStartupListener(BotConfiguration botConfiguration) {
        super(ReadyEvent.class);

        this.botConfiguration = botConfiguration;
    }

    @Override
    public Mono<?> handle(ReadyEvent event) {
        return Mono.when(
                this.validateMessage(event.getClient()),
                this.createCommands(event.getClient()),
                this.setPresence(event.getClient())
        );
    }

    private Mono<?> setPresence(GatewayDiscordClient client) {
        return client.updatePresence(ClientPresence.online(ClientActivity.of(Activity.Type.PLAYING, "ouvrir des cases", null)));
    }

    private Mono<?> createCommands(GatewayDiscordClient client) {
        return client.getApplicationInfo().flatMap(applicationInfo -> {
            long applicationId = applicationInfo.getId().asLong();

            ApplicationService applicationService = client.getRestClient().getApplicationService();

            return Mono.when(applicationService
                    .getGuildApplicationCommands(applicationId, this.botConfiguration.getGuildId())
                    .collectList()
                    .map(dataList -> {
                        List<String> commands = List.of(
                                "forceday",
                                "forcesendcase"
                        );
                        List<String> missingCommands = new ArrayList<>();

                        for (String command : commands) {
                            if (dataList.stream().noneMatch(applicationCommandData -> {
                                return applicationCommandData.name().equals(command);
                            })) {
                                missingCommands.add(command);
                            }
                        }

                        return missingCommands;
                    })
                    .flatMapMany(Flux::fromIterable)
                    .mapNotNull(missingCommand -> {
                        return switch (missingCommand) {
                            case "forceday" -> ApplicationCommandRequest.builder()
                                    .name("forceday")
                                    .description("Force un jour spécifique")
                                    .defaultMemberPermissions(PermissionSet.none().getRawValue() + "")
                                    .addOption(ApplicationCommandOptionData.builder()
                                            .name("day")
                                            .description("Le n° de jour (1 - 24)")
                                            .type(ApplicationCommandOption.Type.INTEGER.getValue())
                                            .required(true)
                                            .build())
                                    .build();
                            case "forcesendcase" -> ApplicationCommandRequest.builder()
                                    .name("forcesendcase")
                                    .description("Force l'envoi du message")
                                    .defaultMemberPermissions(PermissionSet.none().getRawValue() + "")
                                    .build();
                            default -> null;
                        };
                    })
                    .flatMap(applicationCommandData -> {
                        return applicationService.createGuildApplicationCommand(applicationId, this.botConfiguration.getGuildId(), applicationCommandData);
                    }));
        });
    }

    private Mono<?> validateMessage(GatewayDiscordClient client) {
        return client.getChannelById(Snowflake.of(this.botConfiguration.getMainChannelId()))
                .ofType(TextChannel.class)
                .flatMap(channel -> {
                    return channel.getPinnedMessages()
                            .filter(message -> message.getAuthor().map(User::isBot).orElse(false))
                            .count()
                            .flatMap(aLong -> {
                                if (aLong == 0) {
                                    return this.createMainMessage(channel);
                                }

                                return Mono.empty();
                            });
                });
    }

    private Mono<?> createMainMessage(TextChannel channel) {
        return channel.createMessage(EmbedCreateSpec.builder()
                        .title("\uD83C\uDF84 Ho ho ho ! \uD83C\uDF84")
                        .description("""
                                C’est avec une immense joie que moi, le Père Bichël, viens vous annoncer l’ouverture officielle du Calendrier de l'Avent de Bichard ! 🎅✨
                                
                                Chaque jour à 08h00, du 1er au 24 décembre, une nouvelle case s'ouvrira sur notre calendrier ! À l'intérieur ? Des défis amusants et originaux, et bien sûr... des cadeaux à gagner en fonction de votre assiduité à relever les défis ! 🎁
                                
                                🎯 Comment ça fonctionne ?
                                
                                Rejoignez nous chaque jour pour ouvrir une nouvelle case.
                                Accomplissez les défis proposés et montrez nous vos prouesses ! 💪
                                Plus vous participez, plus vos chances de recevoir un cadeau magique augmentent ! 🎉
                                Alors, prêts à relever le défi et à faire briller votre esprit de Noël ? N’attendez plus, l'aventure commence dès demain à 8h ! 🕛
                                
                                🎁 À très vite sous le sapin, mes lutins ! 🎁
                                
                                ✨ Père Bichël ✨
                                """)
                        .color(Color.DEEP_SEA)
                        .build())
                .withComponents(ActionRow.of(
                        Button.success("register", ReactionEmoji.unicode("\uD83C\uDF81"), "S'inscrire !"),
                        Button.danger("unregister", "Se désinscrire")
                ));
    }

}
