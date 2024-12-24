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
    DAY10("10 – Danse rigolote", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 10ème case, choisis une musique rigolote et danse comme si personne ne te regardait. 💃
            Partage une vidéo pour valider.
            (Tout reste privé si vous l’envoyez ici, on vous le rappelle !)"""),
    DAY11("11 – Création de Noël", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 11ème case, réalise une création autour de Noël. 🎨
            Peu importe le support, tant qu’il est festif !
            Envoi nous le résultat ici ou dans le général (avec une capture d’écran) pour valider !"""),
    DAY12("12 – Terre plate", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 12ème case, deviens expert en théorie. Élabore une explication rationnelle avec des exemples concrets pour convaincre les autres que la Terre est plate. 🌍
            Publie ton argumentaire dans le général et prend une capture d’écran pour valider."""),
    DAY13("13 – Aboiement", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 13ème case, montre-nous ton plus bel aboiement. 🐕
            Fais entendre tes talents (audio ou vidéo) ici ou dans le général (avec une capture d’écran) et envoie pour valider !"""),
    DAY14("14 – Faire dire « Bonne fête à toi Bichard »", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 14ème case, tu vas devoir faire preuve de persuasion. Fais dire à un membre de ta famille, un ami ou quelqu’un qui ne connaît pas Bichard : « Bonne fête à toi Bichard » 🎉
            Enregistre ou filme ce moment et partage-le ici ou dans le général (avec une capture d’écran) pour valider !
            (Tout reste privé si vous l’envoyez ici, on vous le rappelle !)"""),
    DAY15("15 – Phrase cohérente", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 15ème case, participe à une phrase cohérente avec un mot écrit par chacun. ✍️
            Envoie ton mot ici pour contribuer."""),
    DAY16("16 – « Tu préfères »", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 16ème case, propose un « Tu préfères » bien loufoque dans le général. 🤔
            Fais-nous rire et partage ton idée !
            Penses à nous remettre une capture d’écran ici pour valider le défi !"""),
    DAY17("17 – Trouver Bichard sur Akinator", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 17ème case, tente de faire deviner Bichard à Akinator. 🧞
            Prends une capture écran pour prouver ton succès !"""),
    DAY18("18 – Hymne national", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 18ème case, compose un hymne national pour une nation fictive où Bichard serait président.
            Texte, musique ou les deux, partage ton œuvre pour valider !"""),
    DAY19("19 – « 7 meilleures vidéos de Bichard »", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 19ème case, réalise un classement des « 7 meilleures vidéos de Bichard », façon « 7 merveilles du monde ». 🎥
            Envoie ton classement pour valider !"""),
    DAY20("20 – Remerciement au staff", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 20ème case, écris un message de remerciement au staff dans le général pour le travail autour de la chaine Twitch et du Discord. 🙌
            Partage la capture d’écran ici pour valider !"""),
    DAY21("21 – Compliments à Bichard et Sami", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 21ème case, écris un beau message de compliments dans le général adressés à Bichard et Sami. 💌
            Partage la capture d’écran ici pour valider !"""),
    DAY22("22 – Vos décorations de noël", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 22ème case, envoyez-moi une photo de votre plus belle décoration de Noël 🎄"""),
    DAY23("23 – Référence à Bichard sur ton sapin", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 23ème case du Calendrier de l’Avent de Bichard, ton défi est de trouver une manière d’inclure une référence à Bichard sur ton sapin de Noël ! 🎄
            Une déco custom, une photo cachée, tout est permis. Prends une photo et envoie-la ici pour valider ta participation."""),
    DAY24("24 – Meilleure blague", """
            Bonjour à toi ! <:bichardSlt:1299815349319110861>
            Pour cette 24ème et dernière case, balance ta meilleure blague dans le général. 🤣
            Amuse-nous pour clôturer ce calendrier en beauté ! N'oublie pas d'envoyer une capture d'écran ici pour valider la case !""")
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
