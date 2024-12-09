package dev.azn9.bichardcalendrier.data;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public enum EventData {

    DAY1("1 – Liste au Père Bichël", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Cette première case du Calendrier de l’Avant de Bichard contiens une demande en personne du Père Bichël. Il n’a toujours pas reçu ta liste de cadeaux !
            Pour valider cette première case, choisis un ou des articles sur amazon.fr pour que le Père Bichël sache ce qui te ferait plaisir pour Noël !
            :warning: Mais attention, ta liste ne doit **pas dépasser 10 euros**"""),
    DAY2("2 – Happy Halloween ?", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette deuxième case du Calendrier de l’Avant de Bichard, tu vas devoir envoyer un beau message à l’un(e) de tes ami(e)s, pour lui souhaiter de passer un excellent Halloween, avec un peu de retard !
            Pour valider cette case, envoie-nous la capture d’écran/vidéo qui prouve que tu as souhaité un joyeux Halloween à ton ami !"""),
    DAY3("3 – FanArt pour Bichard", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour aujourd’hui, la case numéro 3 demande à ce que tu fasses un « fan art » de Bichard.
            Tu dois donc créer une œuvre artistique pour représenter/flatter Bichard.
            Tu as le droit d’utiliser n’importe quel support ! (numérique, classique, jeu vidéo, musique, …)
            Pour valider, tu peux l’envoyer dans #médias et prendre un screen à remettre ici, ou l’envoyer directement ici !"""),
    DAY4("4 – Faire le skin de Bichard en version Noël", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 4ème case du Calendrier de l’Avent de Bichard, ta mission est de créer une version Noël du skin de Bichard ! 🎅
            Laisse parler ta créativité pour le rendre festif et unique. Le meilleur skin sera joué en live par Bichard !
            Pour valider cette case, envoie ta création dans #médias ou ici en capture d’écran."""),
    DAY5("5 – Publicité pour sub à la chaîne", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 5ème case, il est temps de te transformer en agent publicitaire. Fais une publicité originale qui incite à sub à la chaîne de Bichard ! 💎
            Vidéo, image ou même texte, sois créatif et envoie ton œuvre ici ou dans le général et prend un screen à remettre ici pour valider la case."""),
    DAY6("6 – Panneau de ta ville", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 6ème case, prends une photo du panneau de ta ville. 🏙️
            Pas d’inquiétude, cette photo reste privée et sert uniquement pour valider la case."""),
    DAY7("7 – Avis honnête", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 7ème case, exprime-toi sincèrement sur le contenu récent de Bichard. 😊
            Donne ton avis honnête dans le général et poste la capture d’écran de ton message ici pour valider cette étape."""),
    DAY8("8 – Fanfiction Bichard", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 8ème case, c’est l’heure de la créativité narrative. Écris une fanfiction incluant Bichard. 📖
            Peu importe le genre ou le style, envoie ton texte ici ou dans le général (avec une capture d’écran) pour valider !"""),
    DAY9("9 – Texte élogieux", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 9ème case, écris un texte élogieux en positif uniquement sur la dernière personne ayant écrit dans le général. ✨
            Un peu de bienveillance ne fait jamais de mal !
            Screen ton message et remets le ici pour valider !"""),
    // Nope !
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
