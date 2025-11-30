package dev.azn9.bichardcalendrier.manager;

import dev.azn9.bichardcalendrier.configuration.BotConfiguration;
import dev.azn9.bichardcalendrier.data.EventData;
import dev.azn9.bichardcalendrier.data.UserData;
import dev.azn9.bichardcalendrier.registry.UserDataRegistry;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.emoji.Emoji;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.ThreadChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.ref.Cleaner;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class EventManager {

    private static final Logger LOGGER = LogManager.getLogger(EventManager.class);

    private final GatewayDiscordClient discordClient;
    private final UserDataRegistry userDataRegistry;
    private final BotConfiguration botConfiguration;
    private int currentDay;

    @Autowired
    public EventManager(@Lazy GatewayDiscordClient discordClient, UserDataRegistry userDataRegistry, BotConfiguration botConfiguration) {
        this.discordClient = discordClient;
        this.userDataRegistry = userDataRegistry;
        this.botConfiguration = botConfiguration;

        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Paris"));
        if (zonedDateTime.getHour() >= 8) {
            this.currentDay = zonedDateTime.getDayOfMonth();
        } else {
            this.currentDay = zonedDateTime.getDayOfMonth() - 1;
        }

        EventManager.LOGGER.info("Current day: {}", this.currentDay);

        Instant dayAt8 = zonedDateTime.plusDays(zonedDateTime.getHour() > 8 ? 1 : 0)
                .toLocalDate()
                .atStartOfDay(zonedDateTime.getZone())
                .withMonth(12)
                .withHour(8)
                .withMinute(0)
                .withSecond(0)
                .toInstant();
        Duration duration = Duration.between(Instant.now(), dayAt8);
        long nextDayAt8 = duration.getSeconds();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::run, nextDayAt8, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        Cleaner.create().register(this, executor::shutdown);
    }

    private void run() {
        this.switchDay(this.currentDay + 1).subscribe();
    }

    public Mono<UserData> getUserData(long userId) {
        return Mono.fromCallable(() -> this.userDataRegistry.findByUserId(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(5));
    }

    public Mono<UserData> getUserDataFromThread(long threadId) {
        return Mono.fromCallable(() -> this.userDataRegistry.findByThreadId(threadId))
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(5));
    }

    public Mono<Long> getUserThreadId(long userId) {
        return this.getUserData(userId)
                .filter(UserData::isRegistered)
                .mapNotNull(UserData::getThreadId);
    }

    public Mono<Long> createData(User user, TextChannel channel) {
        return this.getUserData(user.getId().asLong())
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(userData -> {
                    if (userData.getThreadId() == null || userData.getThreadId() == 0) {
                        return null;
                    }

                    return userData;
                })
                .doOnNext(userData -> {
                    userData.setRegistered(true);
                    this.userDataRegistry.save(userData);
                })
                .map(UserData::getThreadId)
                .switchIfEmpty(channel.startPrivateThread(user.getUsername())
                        .withInvitable(false)
                        .flatMap(threadChannel -> {
                            return threadChannel.join()
                                    .then(threadChannel.addMember(user))
                                    .then(this.createFirstMessage(threadChannel, user))
                                    .thenReturn(threadChannel);
                        })
                        .map(ThreadChannel::getId)
                        .map(Snowflake::asLong)
                        .publishOn(Schedulers.boundedElastic())
                        .doOnNext(threadId -> {
                            UserData userData = this.userDataRegistry.findByUserId(user.getId().asLong());
                            if (userData == null) {
                                userData = new UserData(user.getId().asLong(), threadId);
                            } else {
                                userData.setThreadId(threadId);
                            }

                            this.userDataRegistry.save(userData);
                        }))
                .flatMap(threadId -> {
                    return user.asMember(Snowflake.of(this.botConfiguration.getGuildId()))
                            .flatMap(member -> {
                                return member.addRole(Snowflake.of(this.botConfiguration.getPlayerRoleId()));
                            })
                            .thenReturn(threadId);
                });
    }

    private Mono<?> createFirstMessage(ThreadChannel threadChannel, User user) {
        return threadChannel.createMessage(user.getMention())
                .withEmbeds(EmbedCreateSpec.builder()
                        .title(":christmas_tree: **Bienvenue dans votre salon du Calendrier de l'Avent de Bichard !** :christmas_tree:")
                        .description("""
                                C'est ici que la magie du **Calendrier de l'Avent de Bichard** prendra vie chaque jour ! :santa::sparkles:
                                
                                Voici ce que vous devez savoir pour bien démarrer :
                                
                                :star2: **Chaque matin à partir de 8h00**, une nouvelle case du calendrier s’ouvrira dans ce salon. Un **nouveau défi** sera alors lancé, et vous aurez **24 heures** (de 8h00 à 8h00) pour le réaliser. :alarm_clock:
                                
                                :dart: **Comment participer ?**
                                
                                - **Ouvrez la case** chaque matin à 8h00 pour découvrir le défi du jour.
                                - **Répondez au défi** directement ici ! Vous pouvez soit envoyer votre réponse dans le salon, soit partager une **capture d'écran** comme preuve de votre participation.
                                - Vous avez **24 heures** à la réception de la case pour accomplir le défi, alors soyez rapides ! Le temps file ! :hourglass_flowing_sand:
                                
                                :mag: **Validation des défis :**
                                
                                - Une fois que vous avez soumis votre défi ici, notre équipe de vérification se chargera de le valider.
                                - Vous recevrez une **confirmation** si votre défi est accepté... ou un **refus** si quelque chose manque ou semble incorrect.
                                - **Attention** : la validation peut prendre un peu de temps, alors soyez patients ! :blush:
                                
                                Alors, préparez-vous à relever de super défis et à répandre la magie de Noël avec nous ! :tada::santa:
                                
                                :sparkles: **Que la chasse aux cases commence !** :sparkles:
                                """)
                        .color(Color.SUMMER_SKY)
                        .build())
                .then(Mono.just(this.currentDay)
                        .filter(i -> i >= 1 && i < 25)
                        .delayUntil(unused -> {
                            return threadChannel.createMessage(EmbedCreateSpec.builder()
                                            .title("Nouvelle journée !")
                                            .description("Il est l'heure d'ouvrir une nouvelle case ! Penses à réaliser le défi aujourd'hui et à le faire valider dans ce channel !")
                                            .color(Color.MOON_YELLOW)
                                            .build())
                                    .withComponents(ActionRow.of(
                                            Button.primary("opencase", Emoji.unicode("\uD83C\uDF81"), "Ouvrir la case")
                                    )).withContent("<@%d>".formatted(user.getId().asLong()));
                        }));
    }

    public Mono<Void> switchDay(int newDay) {
        if (newDay < 1 || newDay > EventData.values().length) {
            return Mono.error(new IllegalStateException("Invalid new day (" + newDay + ")"));
        }

        this.currentDay = newDay;

        return Mono.when(this.getAllUserData()
                        .filter(UserData::isRegistered)
                        .flatMap(userData -> {
                            return this.discordClient.getChannelById(Snowflake.of(userData.getThreadId()))
                                    .ofType(ThreadChannel.class)
                                    .flatMap(threadChannel -> {
                                        return threadChannel.createMessage(EmbedCreateSpec.builder()
                                                        .title("Nouvelle journée !")
                                                        .description("Il est l'heure d'ouvrir une nouvelle case ! Penses à réaliser le défi aujourd'hui et à le faire valider dans ce channel !")
                                                        .color(Color.MOON_YELLOW)
                                                        .build())
                                                .withComponents(ActionRow.of(
                                                        Button.primary("opencase", Emoji.unicode("\uD83C\uDF81"), "Ouvrir la case")
                                                )).withContent("<@%d>".formatted(userData.getUserId()));
                                    })
                                    .onErrorResume(throwable -> {
                                        EventManager.LOGGER.error(throwable);
                                        return Mono.empty();
                                    });
                        })
                        .onErrorResume(throwable -> {
                            EventManager.LOGGER.error(throwable);
                            return Mono.empty();
                        }))
                .then(this.discordClient.getChannelById(Snowflake.of(this.botConfiguration.getGestionChannelId()))
                        .ofType(MessageChannel.class)
                        .flatMap(channel -> {
                            return channel.createMessage(EmbedCreateSpec.builder()
                                    .title("Nouveau jour : " + this.currentDay)
                                    .color(Color.CINNABAR)
                                    .build());
                        }))
                .then();
    }

    public Flux<UserData> getAllUserData() {
        return Mono.fromCallable(this.userDataRegistry::findAll)
                .flatMapMany(Flux::fromIterable);
    }

    public int getCurrentDay() {
        return this.currentDay;
    }

    public Mono<Void> removeData(long userId) {
        return this.getUserData(userId)
                .flatMap(userData -> {
                    return Mono.fromRunnable(() -> {
                        userData.setRegistered(false);
                        this.userDataRegistry.save(userData);
                    }).publishOn(Schedulers.boundedElastic());
                })
                .then(this.discordClient.getMemberById(Snowflake.of(this.botConfiguration.getGuildId()), Snowflake.of(userId))
                        .flatMap(member -> {
                            return member.removeRole(Snowflake.of(this.botConfiguration.getPlayerRoleId()));
                        }))
                .then();
    }
}
