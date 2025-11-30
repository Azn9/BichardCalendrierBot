package dev.azn9.bichardcalendrier.data;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public enum EventData {

    DAY1("\uD83C\uDF81 CASE 1 – ???", """
            ???"""),
    DAY2("\uD83C\uDF81 CASE 2 – ???", """
            ???"""),
    DAY3("\uD83C\uDF81 CASE 3 – ???", """
            ???"""),
    DAY4("\uD83C\uDF81 CASE 4 – ???", """
            ???"""),
    DAY5("\uD83C\uDF81 CASE 5 – ???", """
            ???"""),
    DAY6("\uD83C\uDF81 CASE 6 – ???", """
            ???"""),
    DAY7("\uD83C\uDF81 CASE 7 – ???", """
            ???"""),
    DAY8("\uD83C\uDF81 CASE 8 – ???", """
            ???"""),
    DAY9("\uD83C\uDF81 CASE 9 – ???", """
            ???"""),
    DAY10("\uD83C\uDF81 CASE 10 – ???", """
            ???"""),
    DAY11("\uD83C\uDF81 CASE 11 – ???", """
            ???"""),
    DAY12("\uD83C\uDF81 CASE 12 – ???", """
            ???"""),
    DAY13("\uD83C\uDF81 CASE 13 – ???", """
            ???"""),
    DAY14("\uD83C\uDF81 CASE 14 – ???", """
            ???"""),
    DAY15("\uD83C\uDF81 CASE 15 – ???", """
            ???"""),
    DAY16("\uD83C\uDF81 CASE 16 – ???", """
            ???"""),
    DAY17("\uD83C\uDF81 CASE 17 – ???", """
            ???"""),
    DAY18("\uD83C\uDF81 CASE 18 – ???", """
            ???"""),
    DAY19("\uD83C\uDF81 CASE 19 – ???", """
            ???"""),
    DAY20("\uD83C\uDF81 CASE 20 – ???", """
            ???"""),
    DAY21("\uD83C\uDF81 CASE 21 – ???", """
            ???"""),
    DAY22("\uD83C\uDF81 CASE 22 – ???", """
            ???"""),
    DAY23("\uD83C\uDF81 CASE 23 – ???", """
            ???"""),
    DAY24("\uD83C\uDF81 CASE 24 – ???", """
            ???""");

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
