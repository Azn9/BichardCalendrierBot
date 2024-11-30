package dev.azn9.bichardcalendrier;

import discord4j.core.GatewayDiscordClient;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class BichardCalendrierApplication {

    public static void main(String[] args) {
        ReactorDebugAgent.init();
        SpringApplication.run(BichardCalendrierApplication.class, args);
    }

    private final GatewayDiscordClient gatewayDiscordClient;

    public BichardCalendrierApplication(GatewayDiscordClient gatewayDiscordClient) {
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    @PostConstruct
    public void init() {
        this.gatewayDiscordClient.onDisconnect().block();
    }

}
