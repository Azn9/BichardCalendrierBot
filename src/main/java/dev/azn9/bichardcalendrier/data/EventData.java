package dev.azn9.bichardcalendrier.data;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public enum EventData {

    // Eh non, tu ne peux pas tricher !
    ;

    private final String title;
    private final String description;

    EventData(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public EmbedCreateSpec getMessage(long userId) {
        return EmbedCreateSpec.builder()
                .title(this.title)
                .description(this.description.formatted(userId))
                .color(Color.TAHITI_GOLD)
                .build();
    }

}
