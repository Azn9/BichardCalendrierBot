package dev.azn9.bichardcalendrier.listener;

import dev.azn9.bichardcalendrier.configuration.BotConfiguration;
import dev.azn9.bichardcalendrier.manager.EventManager;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.emoji.Emoji;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.service.ApplicationService;
import discord4j.rest.util.Color;
import discord4j.rest.util.PermissionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotStartupListener extends DiscordListener<ReadyEvent> {

    private static final Logger LOGGER = LogManager.getLogger(BotStartupListener.class);

    private final BotConfiguration botConfiguration;
    private final EventManager eventManager;

    @Autowired
    public BotStartupListener(BotConfiguration botConfiguration, EventManager eventManager) {
        super(ReadyEvent.class);

        this.botConfiguration = botConfiguration;
        this.eventManager = eventManager;
    }

    @Override
    public Mono<?> handle(ReadyEvent event) {
        return Mono.when(
                this.validateMessage(event.getClient()),
                this.createCommands(event.getClient()),
                this.setPresence(event.getClient()),
                this.updatePlayerRoles(event.getClient())
        );
    }

    private Publisher<?> updatePlayerRoles(GatewayDiscordClient client) {
        Snowflake guildId = Snowflake.of(this.botConfiguration.getGuildId());
        Snowflake roleId = Snowflake.of(this.botConfiguration.getPlayerRoleId());

        return Mono.when(this.eventManager.getAllUserData()
                .flatMap(userData -> {
                    return client.getMemberById(guildId, Snowflake.of(userData.getUserId()))
                            .flatMap(member -> {
                                if (userData.isRegistered()) {
                                    if (member.getRoleIds().contains(roleId)) {
                                        return Mono.empty();
                                    } else {
                                        return member.addRole(roleId);
                                    }
                                } else {
                                    if (member.getRoleIds().contains(roleId)) {
                                        return member.removeRole(roleId);
                                    } else {
                                        return Mono.empty();
                                    }
                                }
                            })
                            .onErrorResume(throwable -> {
                                BotStartupListener.LOGGER.error(throwable);
                                return Mono.empty();
                            });
                }));
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
                                "forcesendcase",
                                "say"
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
                            case "say" -> ApplicationCommandRequest.builder()
                                    .name("say")
                                    .description("Envoie un message avec le bot")
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
                            .filter(reference -> reference.getMessage().getAuthor().map(User::isBot).orElse(false))
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
                                C’est avec une immense joie que moi, le Père Bichël, viens vous annoncer la réouverture du Calendrier de l'Avent de Bichard ! :santa::sparkles:
                                
                                Chaque jour à 08h00, du 1er au 24 décembre, une nouvelle case s'ouvrira sur notre calendrier ! À l'intérieur ? Des défis amusants et originaux, et bien sûr... des cadeaux à gagner en fonction de votre assiduité à relever les défis ! :gift:
                                
                                :dart: Comment ça fonctionne ?
                                
                                Rejoignez nous chaque jour pour ouvrir une nouvelle case.
                                Accomplissez les défis proposés et montrez nous vos prouesses ! :muscle:
                                Plus vous participez, plus vos chances de recevoir un cadeau magique augmentent ! :tada:
                                Alors, prêts à relever le défi et à faire briller votre esprit de Noël ? N’attendez plus, l'aventure commence dès demain à 8h ! :clock12:
                                
                                :gift: À très vite sous le sapin, mes lutins ! :gift:
                                
                                :sparkles: Père Bichël :sparkles:
                                """)
                        .color(Color.DEEP_SEA)
                        .build())
                .withComponents(ActionRow.of(
                        Button.success("register", Emoji.unicode("\uD83C\uDF81"), "S'inscrire !"),
                        Button.danger("unregister", "Se désinscrire")
                ));
    }

}
