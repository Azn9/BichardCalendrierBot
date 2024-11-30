package dev.azn9.bichardcalendrier.bean;

import dev.azn9.bichardcalendrier.configuration.BotConfiguration;
import dev.azn9.bichardcalendrier.listener.DiscordListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.shard.MemberRequestFilter;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration
public class DiscordClientBean {

    @Bean
    public GatewayDiscordClient client(BotConfiguration botConfiguration, List<DiscordListener<?>> listenerList) {
        return DiscordClientBuilder.create(botConfiguration.getToken())
                .build()
                .gateway()
                .setEnabledIntents(IntentSet.nonPrivileged().or(IntentSet.of(Intent.MESSAGE_CONTENT, Intent.GUILD_MEMBERS)))
                .setMemberRequestFilter(MemberRequestFilter.none())
                .withEventDispatcher(eventDispatcher -> {
                    return Flux.fromIterable(listenerList)
                            .flatMap(discordListener -> discordListener.registerEvent(eventDispatcher));
                })
                .login()
                .block();
    }

}
