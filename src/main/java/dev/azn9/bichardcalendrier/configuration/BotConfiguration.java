package dev.azn9.bichardcalendrier.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BotConfiguration {

    @Value("${token}")
    private String token;

    @Value("${guildId}")
    private long guildId;

    @Value("${mainChannelId}")
    private long mainChannelId;

    @Value("${gestionChannelId}")
    private long gestionChannelId;

    @Value("${playerRoleId}")
    private long playerRoleId;

    public String getToken() {
        return this.token;
    }

    public long getGuildId() {
        return this.guildId;
    }

    public long getMainChannelId() {
        return this.mainChannelId;
    }

    public long getGestionChannelId() {
        return this.gestionChannelId;
    }

    public long getPlayerRoleId() {
        return this.playerRoleId;
    }
}
