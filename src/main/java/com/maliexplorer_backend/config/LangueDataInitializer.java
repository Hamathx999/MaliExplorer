package com.maliexplorer_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maliexplorer_backend.model.LangueNationaleModel;
import com.maliexplorer_backend.repository.LangueNationaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Initialiseur de données pour les 13 langues nationales officielles du Mali.
 * Insère les descriptions, zones géographiques, locuteurs et lexiques d'apprentissage.
 */
@Slf4j
@Component
@Order(15)
@RequiredArgsConstructor
public class LangueDataInitializer implements CommandLineRunner {

    private final LangueNationaleRepository langueRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        if (langueRepository.count() > 0) {
            log.info("Les langues nationales sont déjà initialisées ({} trouvées).", langueRepository.count());
            return;
        }

        log.info("Initialisation des 13 langues nationales du Mali...");

        try {
            List<LangueNationaleModel> langues = List.of(
                    creerLangue(
                            "Bamanankan (Bambara)",
                            "Bamanankan",
                            "~80% de la population malienne",
                            "Bamako, Ségou, Koulikoro, Sikasso",
                            "Langue véhiculaire principale du Mali, membre de la famille des langues mandées. Elle est comprise dans presque toutes les grandes villes et sur les marchés.",
                            1,
                            List.of(
                                    item("I ni sogoma", "i-ni-so-go-ma", "Bonjour (le matin)", "Salutations"),
                                    item("I ni tilé", "i-ni-ti-lé", "Bonjour (l'après-midi)", "Salutations"),
                                    item("I ni sou", "i-ni-sou", "Bonsoir", "Salutations"),
                                    item("I ka kènè wa ?", "i-ka-kè-nè-wa", "Comment vas-tu ? (Es-tu en santé ?)", "Salutations"),
                                    item("Toro té", "to-ro-té", "Tout va bien (Pas de mal)", "Salutations"),
                                    item("I ni tché", "i-ni-tché", "Merci", "Courtoisie"),
                                    item("Aw ni baara", "aw-ni-baa-ra", "Bon travail / Bravo", "Courtoisie"),
                                    item("Hakèto", "ha-kè-to", "Pardon / Excusez-moi", "Courtoisie"),
                                    item("Joli don ?", "djo-li-don", "Combien ça coûte ?", "Marché & Commerce"),
                                    item("A ka guèlèn", "a-ka-guè-lèn", "C'est trop cher", "Marché & Commerce"),
                                    item("Dòoni dòoni bò a la", "dò-ni-dò-ni-bò-a-la", "Diminue un peu le prix", "Marché & Commerce"),
                                    item("Ne b'a fè", "né-ba-fè", "Je le veux / J'aimerais l'acheter", "Marché & Commerce"),
                                    item("Yòrò jumèn ?", "yò-rò-djou-mèn", "Où est cet endroit ?", "Orientation & Déplacement"),
                                    item("Sira jira ne na", "si-ra-dji-ra-né-na", "Montre-moi le chemin", "Orientation & Déplacement"),
                                    item("An b'a fò nògònna", "an-ba-fò-nò-gòn-na", "À la prochaine / Au revoir", "Expressions usuelles"),
                                    item("A tè foyi yé", "a-tè-fo-yi-yé", "De rien / Ce n'est rien", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Fulfulde (Peul)",
                            "Pulaar / Fulfulde",
                            "~15% de la population malienne",
                            "Mopti (Delta intérieur du Niger), Gao, Sikasso",
                            "Langue parlée par le peuple Peul à travers toute l'Afrique de l'Ouest, célèbre pour sa richesse poétique, ses traditions pastorales et le festival du Yaaral.",
                            2,
                            List.of(
                                    item("Jam waali", "djam-waa-li", "Bonjour (Avez-vous passé une nuit paisible ?)", "Salutations"),
                                    item("Jam ñalli", "djam-ñal-li", "Bonjour (Avez-vous passé une bonne journée ?)", "Salutations"),
                                    item("Jam hiiri", "djam-hii-ri", "Bonsoir", "Salutations"),
                                    item("No mbaddaa ?", "no-mbad-daa", "Comment vas-tu ?", "Salutations"),
                                    item("Jam tan", "djam-tan", "La paix seulement / Tout va bien", "Salutations"),
                                    item("A jaaraama", "a-djaa-raa-ma", "Merci", "Courtoisie"),
                                    item("Accee hakke", "at-chee-hak-ke", "Pardonnez-moi", "Courtoisie"),
                                    item("Noy coggu maggu ?", "noy-tsog-gou-mag-gou", "Quel est le prix ?", "Marché & Commerce"),
                                    item("Ina tiiɗi", "i-na-tii-di", "C'est cher", "Marché & Commerce"),
                                    item("Ustu seeɗa", "ous-tou-see-da", "Baisse un peu", "Marché & Commerce"),
                                    item("Hol to woni...", "hol-to-wo-ni", "Où se trouve...", "Orientation & Déplacement"),
                                    item("En jango", "en-djan-go", "À demain", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Songhaï (Koyraboro Senni)",
                            "Songhoy Senni",
                            "~10% de la population malienne",
                            "Tombouctou, Gao, Ménaka (Boucle du Niger)",
                            "Grande langue de l'empire Songhaï historique, présente le long des rives sahéliennes de Tombouctou à Ansongo.",
                            3,
                            List.of(
                                    item("M'baani", "m-baa-ni", "Bonjour / Paix sur vous", "Salutations"),
                                    item("Woy matenkene ?", "woy-ma-ten-ke-ne", "Comment allez-vous ?", "Salutations"),
                                    item("Baani samay", "baa-ni-sa-may", "Bien, tout va pour le mieux", "Salutations"),
                                    item("Fofo", "fo-fo", "Merci", "Courtoisie"),
                                    item("Yaaf'ay", "yaa-fay", "Pardonnez-moi", "Courtoisie"),
                                    item("Marje no ?", "mar-djé-no", "C'est combien ?", "Marché & Commerce"),
                                    item("A gaa caabandi", "a-gaa-tsaa-ban-di", "Diminue le prix", "Marché & Commerce"),
                                    item("Man no...", "man-no", "Où est...", "Orientation & Déplacement"),
                                    item("Kala han foo", "ka-la-han-foo", "À bientôt / Au revoir", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Soninké (Maraka)",
                            "Sooninkanxanne",
                            "~7% de la population malienne",
                            "Kayes, Nara, Nioro du Sahel",
                            "Langue fondatrice du prestigieux Empire du Wagadou (Ghana antique), renommée pour sa grande diaspora et ses traditions marchandes séculaires.",
                            4,
                            List.of(
                                    item("An do kenne", "an-do-ken-ne", "Bonjour", "Salutations"),
                                    item("Tan moxo wa ?", "tan-mo-xo-wa", "Comment vas-tu ?", "Salutations"),
                                    item("Jammu ngi", "djam-mou-ngi", "En paix / Tout va bien", "Salutations"),
                                    item("N'jaaraama", "n-djaa-raa-ma", "Merci", "Courtoisie"),
                                    item("Yafam", "ya-fam", "Pardonne-moi", "Courtoisie"),
                                    item("Mange ?", "man-guè", "Combien ?", "Marché & Commerce"),
                                    item("A jatti", "a-djat-ti", "C'est cher", "Marché & Commerce"),
                                    item("Mini na...", "mi-ni-na", "Où est...", "Orientation & Déplacement"),
                                    item("Soxona", "so-xo-na", "À la prochaine", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Tamasheq (Berbère touareg)",
                            "Tamasheq / Tamajeq",
                            "~5% de la population malienne",
                            "Kidal, Ménaka, Tombouctou, Gao",
                            "Langue berbère des Touaregs du Sahara (Kel Tamasheq), écrite avec l'alphabet millénaire Tifinagh et porteuse de poésie du désert.",
                            5,
                            List.of(
                                    item("Oywan", "oy-wan", "Bonjour / Salut", "Salutations"),
                                    item("Matoliyt ?", "ma-to-liyt", "Comment vas-tu ?", "Salutations"),
                                    item("Alxeyr ghas", "al-xeyr-rhas", "Seulement la paix / Bien", "Salutations"),
                                    item("Tanemmirt", "ta-nem-mirt", "Merci beaucoup", "Courtoisie"),
                                    item("Surf-iyi", "sourf-i-yi", "Excuse-moi", "Courtoisie"),
                                    item("Manik a iga ?", "ma-nik-a-i-ga", "Combien ça coûte ?", "Marché & Commerce"),
                                    item("Mani...", "ma-ni", "Où se trouve...", "Orientation & Déplacement"),
                                    item("Ar tufat", "ar-tou-fat", "À demain / Au revoir", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Dogonso (Dogon)",
                            "Toro So / Dogoso",
                            "~6% de la population malienne",
                            "Pays Dogon, Bandiagara, Falaise d'hombori",
                            "Ensemble de dialectes parlés au pied et sur le plateau de la mythique Falaise de Bandiagara (classée UNESCO), riches en cosmovision et rituels.",
                            6,
                            List.of(
                                    item("Aga po", "a-ga-po", "Bonjour (le matin)", "Salutations"),
                                    item("De po", "dé-po", "Bonjour (en journée)", "Salutations"),
                                    item("Ana po", "a-na-po", "Bonsoir", "Salutations"),
                                    item("Kuru go ?", "kou-rou-go", "Comment vas-tu ?", "Salutations"),
                                    item("Kuru", "kou-rou", "Bien / Tout va bien", "Salutations"),
                                    item("Bir po", "bir-po", "Merci / Bon travail", "Courtoisie"),
                                    item("Anje be...", "an-djé-bé", "Où est...", "Orientation & Déplacement")
                            )
                    ),
                    creerLangue(
                            "Khassonké (Xasonga)",
                            "Xasongaxanne",
                            "~5% de la population malienne",
                            "Kayes, Bafoulabé, Vallée du Haut-Sénégal",
                            "Langue mandingue occidentale parlée par les Khassonkés du bassin du fleuve Sénégal, réputée pour ses contes oraux et les chants de griots de Médine.",
                            7,
                            List.of(
                                    item("I ni sugu", "i-ni-sou-gou", "Bonjour (le matin)", "Salutations"),
                                    item("I ni tle", "i-ni-tlé", "Bonjour (l'après-midi)", "Salutations"),
                                    item("I ni su", "i-ni-sou", "Bonsoir", "Salutations"),
                                    item("Kene wa ?", "ké-né-wa", "Comment vas-tu ?", "Salutations"),
                                    item("Toro te", "to-ro-té", "Tout va bien (Pas de mal)", "Salutations"),
                                    item("A barika", "a-ba-ri-ka", "Merci", "Courtoisie"),
                                    item("Hake to", "ha-ké-to", "Excusez-moi / Pardon", "Courtoisie"),
                                    item("I ni baara", "i-ni-baa-ra", "Bon travail", "Courtoisie"),
                                    item("Joli le ?", "djo-li-lé", "Combien coûte cela ?", "Marché & Commerce"),
                                    item("A ka geleng", "a-ka-gué-lèng", "C'est cher", "Marché & Commerce"),
                                    item("Dooni boyi a la", "dô-ni-bo-yi-a-la", "Diminue un peu le prix", "Marché & Commerce"),
                                    item("Sira jiri n'na", "si-ra-dji-ri-n-na", "Montre-moi le chemin", "Orientation & Déplacement"),
                                    item("Yoro mingo ?", "yo-ro-min-go", "Où se trouve cet endroit ?", "Orientation & Déplacement"),
                                    item("Bisimila", "bi-si-mi-la", "Bienvenue", "Expressions usuelles"),
                                    item("An b'a fo nyoona", "an-ba-fo-nyoo-na", "À la prochaine / Au revoir", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Maninkakan (Malinké)",
                            "Maninkakan",
                            "~7% de la population malienne",
                            "Kita, Kangaba, Mandé, Kéniéba, Yanfolila",
                            "Langue de la grande épopée mandingue de Soundiata Keïta et de la Charte de Kouroukan Fouga (patrimoine UNESCO). Berceau des chasseurs dosso et des balafons.",
                            8,
                            List.of(
                                    item("I ni sogoma", "i-ni-so-go-ma", "Bonjour (le matin)", "Salutations"),
                                    item("I ni tile", "i-ni-ti-lé", "Bonjour (en journée)", "Salutations"),
                                    item("I ka kene wa ?", "i-ka-ké-né-wa", "Tu vas bien ?", "Salutations"),
                                    item("I ni tche", "i-ni-tché", "Merci beaucoup", "Courtoisie"),
                                    item("Hake to", "ha-ké-to", "Pardon / Excusez-moi", "Courtoisie"),
                                    item("Aw ni baara", "aw-ni-baa-ra", "Courage au travail", "Courtoisie"),
                                    item("Joli do ?", "djo-li-do", "Quel est le prix ?", "Marché & Commerce"),
                                    item("A gbara", "a-gba-ra", "C'est cher", "Marché & Commerce"),
                                    item("Do bo a la", "do-bo-a-la", "Baisse le prix s'il te plaît", "Marché & Commerce"),
                                    item("Yoro juman ?", "yo-ro-djou-man", "Où est cet endroit ?", "Orientation & Déplacement"),
                                    item("Sira djira ne la", "si-ra-dji-ra-né-la", "Indique-moi la route", "Orientation & Déplacement"),
                                    item("I bisimila", "i-bi-si-mi-la", "Bienvenue chez nous", "Expressions usuelles"),
                                    item("K'an ben", "kan-bèn", "Au revoir / À bientôt", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Sénoufo (Syenara)",
                            "Syenara / Senari",
                            "~9% de la population malienne",
                            "Sikasso, Kadiolo, Koutiala, Yorosso",
                            "Langue du Kénédougou et du sud agricole, emblématique de la culture agraire, des masques sculptés et de la musique de balafon des maîtres du bois.",
                            9,
                            List.of(
                                    item("Fotamana", "fo-ta-ma-na", "Bonjour / Salutations", "Salutations"),
                                    item("Mi yege wa ?", "mi-yé-gué-wa", "Comment ça va ?", "Salutations"),
                                    item("Kari gbaa", "ka-ri-gbaa", "Ça va bien / En paix", "Salutations"),
                                    item("Kaha", "ka-ha", "Merci", "Courtoisie"),
                                    item("Kafo", "ka-fo", "Excuse-moi", "Courtoisie"),
                                    item("Kpa megi ?", "kpa-mé-gui", "Combien coûte ceci ?", "Marché & Commerce"),
                                    item("Si baha", "si-ba-ha", "C'est trop cher", "Marché & Commerce"),
                                    item("Tolo nyini", "to-lo-nyi-ni", "Diminue un peu", "Marché & Commerce"),
                                    item("Ka tana ?", "ka-ta-na", "Où vas-tu / Où est la voie ?", "Orientation & Déplacement"),
                                    item("Fotamana", "fo-ta-ma-na", "Bienvenue", "Expressions usuelles"),
                                    item("Ka nyeen", "ka-nyeen", "À plus tard", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Minianka (Mamara)",
                            "Minyanka / Mamara",
                            "~4% de la population malienne",
                            "Koutiala, San, Bla, Tominian",
                            "Langue voltaïque proche du sénoufo, parlée dans le grand bassin cotonnier de Koutiala et les plaines fertiles de San.",
                            10,
                            List.of(
                                    item("N'fola", "n-fo-la", "Bonjour / Salutations", "Salutations"),
                                    item("Yege wa ?", "yé-gué-wa", "La santé est bonne ?", "Salutations"),
                                    item("Lafi", "la-fi", "Tout va bien", "Salutations"),
                                    item("Kaha", "ka-ha", "Merci", "Courtoisie"),
                                    item("Yafa n'ne", "ya-fa-n-né", "Pardonne-moi", "Courtoisie"),
                                    item("Wari joro ?", "wa-ri-djo-ro", "Quel montant ?", "Marché & Commerce"),
                                    item("A taha", "a-ta-ha", "C'est cher", "Marché & Commerce"),
                                    item("Dogi a na", "do-gui-a-na", "Baisse un peu", "Marché & Commerce"),
                                    item("Kulo be min ?", "kou-lo-bé-min", "Où se trouve la route ?", "Orientation & Déplacement"),
                                    item("K'an ben", "kan-bèn", "Au revoir", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Bozo (Boso)",
                            "Bososo / Boso",
                            "~3% de la population malienne",
                            "Mopti, Ségou, Djenné, Delta central du Niger",
                            "Langue ancestrale des « Maîtres du Fleuve Niger », pêcheurs et navigateurs en pinasses emblématiques de la boucle du fleuve.",
                            11,
                            List.of(
                                    item("A ni tie", "a-ni-tié", "Bonjour / Salut", "Salutations"),
                                    item("Kene wa ?", "ké-né-wa", "La forme ?", "Salutations"),
                                    item("Toro te", "to-ro-té", "Pas de problème", "Salutations"),
                                    item("A jaaraama", "a-djaa-raa-ma", "Merci", "Courtoisie"),
                                    item("Yafa", "ya-fa", "Pardon", "Courtoisie"),
                                    item("Joli le ?", "djo-li-lé", "C'est combien ?", "Marché & Commerce"),
                                    item("Yege wari", "yé-gué-wa-ri", "Prix du poisson", "Marché & Commerce"),
                                    item("Dogoni bo a la", "do-go-ni-bo-a-la", "Fais un rabais", "Marché & Commerce"),
                                    item("Kounou be min ?", "kou-nou-bé-min", "Où est la pirogue / le port ?", "Orientation & Déplacement"),
                                    item("An ben", "an-bèn", "À plus tard", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Bobo (Bomu)",
                            "Bomu / Bwamu",
                            "~3% de la population malienne",
                            "San, Tominian, Plaine du Bani",
                            "Langue du peuple Bwa, réputée pour ses traditions agraires, les masques de feuilles et la fête du Sanké à San.",
                            12,
                            List.of(
                                    item("A fowa", "a-fo-wa", "Bonjour", "Salutations"),
                                    item("Mi zaani ?", "mi-zaa-ni", "Comment vas-tu ?", "Salutations"),
                                    item("Zaani", "zaa-ni", "Bien", "Salutations"),
                                    item("A barka", "a-bar-ka", "Merci", "Courtoisie"),
                                    item("Yafa", "ya-fa", "Excusez-moi", "Courtoisie"),
                                    item("Kpa djo ?", "kpa-djo", "Combien ?", "Marché & Commerce"),
                                    item("A tian", "a-tian", "C'est cher", "Marché & Commerce"),
                                    item("Wa be min ?", "wa-bé-min", "Où se trouve la voie ?", "Orientation & Déplacement"),
                                    item("K'an ben", "kan-bèn", "Au revoir", "Expressions usuelles")
                            )
                    ),
                    creerLangue(
                            "Arabe Hassaniya",
                            "Kallam Hassaniya",
                            "~2% de la population malienne",
                            "Tombouctou, Taoudénit, Bassikounou, Nord malien",
                            "Dialecte arabe bédouin des Maures et commerçants transsahariens, au cœur des routes de caravanes d'azalaï de sel.",
                            13,
                            List.of(
                                    item("Salam aleikoum", "sa-lam-a-ley-koum", "Que la paix soit sur vous", "Salutations"),
                                    item("Mettariki ?", "met-ta-ri-ki", "Comment vas-tu ?", "Salutations"),
                                    item("Labass", "la-bass", "Rien de grave / Tout va bien", "Salutations"),
                                    item("Choukran", "chou-kran", "Merci", "Courtoisie"),
                                    item("Smahli", "smah-li", "Pardonne-moi", "Courtoisie"),
                                    item("Bkem tha ?", "b-kem-tha", "Combien coûte cela ?", "Marché & Commerce"),
                                    item("Ghaali yasser", "ghaa-li-yas-ser", "C'est très cher", "Marché & Commerce"),
                                    item("Nagass chwaya", "na-gass-chwa-ya", "Baisse un peu", "Marché & Commerce"),
                                    item("Vayn et-triq ?", "vayn-et-triq", "Où est la route ?", "Orientation & Déplacement"),
                                    item("Marhaban", "mar-ha-ban", "Bienvenue", "Expressions usuelles"),
                                    item("Maa salama", "maa-sa-la-ma", "Au revoir / Que la paix t'accompagne", "Expressions usuelles")
                            )
                    )
            );

            langueRepository.saveAll(langues);
            log.info("Succès : Les 13 langues nationales ont été insérées avec succès en base de données.");
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation des langues nationales : {}", e.getMessage(), e);
        }
    }

    private Map<String, String> item(String mot, String phonetique, String traduction, String categorie) {
        return Map.of(
                "mot", mot,
                "phonetique", phonetique,
                "traduction", traduction,
                "categorie", categorie
        );
    }

    private LangueNationaleModel creerLangue(
            String nom,
            String nomLocal,
            String locuteurs,
            String regions,
            String description,
            int ordre,
            List<Map<String, String>> lexique) {
        String json = "[]";
        try {
            json = objectMapper.writeValueAsString(lexique);
        } catch (Exception e) {
            log.warn("Impossible de convertir le lexique en JSON pour {}: {}", nom, e.getMessage());
        }

        return LangueNationaleModel.builder()
                .nom(nom)
                .nomLocal(nomLocal)
                .locuteurs(locuteurs)
                .regions(regions)
                .description(description)
                .ordreAffichage(ordre)
                .publie(true)
                .lexiqueJson(json)
                .build();
    }
}
