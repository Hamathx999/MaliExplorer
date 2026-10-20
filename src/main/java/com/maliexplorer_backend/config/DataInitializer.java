package com.maliexplorer_backend.config;

import com.maliexplorer_backend.model.HomeSlideModel;
import com.maliexplorer_backend.model.ReligionModel;
import com.maliexplorer_backend.model.VilleModel;
import com.maliexplorer_backend.repository.HomeSlideRepository;
import com.maliexplorer_backend.repository.ReligionRepository;
import com.maliexplorer_backend.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final HomeSlideRepository homeSlideRepository;
    private final ReligionRepository religionRepository;
    private final VilleRepository villeRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        initHomeSlides();
        initReligions();
        initHomeFeaturedVilles();
    }

    private void initHomeSlides() {
        try {
            jdbcTemplate.execute("ALTER TABLE home_slides MODIFY COLUMN image_url TEXT");
        } catch (Exception e) {
            log.debug("Vérification schéma table home_slides : {}", e.getMessage());
        }

        List<HomeSlideModel> defaultSlides = List.of(
                HomeSlideModel.builder()
                        .titre("Tombouctou la Mystérieuse")
                        .sousTitre("Patrimoine Mondial de l'UNESCO")
                        .legende("Cité des 333 saints, joyau historique du commerce transsaharien et des manuscrits séculaires.")
                        .imageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/93c7822b-966e-446c-aab2-04396325e942_deuxieme_imagehome.jpeg")
                        .lienAction("/city-detail")
                        .ordre(5)
                        .actif(true)
                        .prioritaire(false)
                        .build(),
                HomeSlideModel.builder()
                        .titre("Djenné & sa Grande Mosquée")
                        .sousTitre("Chef-d'œuvre de l'architecture soudano-sahélienne")
                        .legende("Le plus grand édifice au monde bâti en terre crue (banco), cœur battant du delta intérieur du Niger.")
                        .imageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/90af1bd4-e4df-45a1-8925-06ce135d4bf1_Great_Mosque_of_Djenn___1907___Mali_s_Mud_Icon___ArchEyes.jpeg")
                        .lienAction("/city-detail")
                        .ordre(3)
                        .actif(true)
                        .prioritaire(false)
                        .build(),
                HomeSlideModel.builder()
                        .titre("Le Pays Dogon & la Falaise de Bandiagara")
                        .sousTitre("Sanctuaire culturel et cosmogonique")
                        .legende("Un paysage culturel exceptionnel alliant architecture troglodytique, falaises grandioses et traditions vivantes.")
                        .imageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/0fbffaa1-70b6-47aa-8120-0e4f44226820_Africa_-_Mali_-_Banani.jpeg")
                        .lienAction("/region-detail")
                        .ordre(1)
                        .actif(true)
                        .prioritaire(true)
                        .build(),
                HomeSlideModel.builder()
                        .titre("Bamako & le Fleuve Niger")
                        .sousTitre("Capitale vibrante et carrefour d'Afrique de l'Ouest")
                        .legende("Une métropole effervescente traversée par le fleuve Djoliba, riche de ses marchés d'artisanat et musées.")
                        .imageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/d5f7889e-b437-4b62-953e-845b1f7120d6_Bamako.jpg")
                        .lienAction("/city-detail")
                        .ordre(2)
                        .actif(true)
                        .prioritaire(false)
                        .build(),
                HomeSlideModel.builder()
                        .titre("Ségou, Cité des Balanzans")
                        .sousTitre("Capitale historique du Royaume Bambara")
                        .legende("Terre de poterie, de bogolan et du festival sur le Niger, empreinte de dignité et d'élégance.")
                        .imageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/ebe5b68a-dfc2-46f8-aa33-d6444b1c22eb_La_cit__des_Balanza__w___mally_la_slameuse_Segou_.jpeg")
                        .lienAction("/city-detail")
                        .ordre(4)
                        .actif(true)
                        .prioritaire(false)
                        .build()
        );

        List<HomeSlideModel> existingSlides = homeSlideRepository.findAll();
        if (existingSlides.isEmpty()) {
            log.info("Initialisation des diapositives Supabase pour le Home...");
            homeSlideRepository.saveAll(defaultSlides);
            log.info("{} diapositives initialisées avec leurs URLs Supabase.", defaultSlides.size());
            return;
        }

        // Mise à niveau automatique des diapositives existantes qui pointent encore vers des assets locaux
        for (HomeSlideModel existing : existingSlides) {
            String currentUrl = existing.getImageUrl() != null ? existing.getImageUrl().trim() : "";
            if (currentUrl.isEmpty() || currentUrl.startsWith("assets/")) {
                String titreLower = existing.getTitre() != null ? existing.getTitre().toLowerCase() : "";
                for (HomeSlideModel def : defaultSlides) {
                    String defKey = def.getTitre().split(" ")[0].toLowerCase();
                    if (titreLower.contains(defKey)) {
                        existing.setImageUrl(def.getImageUrl());
                        homeSlideRepository.save(existing);
                        log.info("Diapositive '{}' synchronisée avec son image Supabase : {}", existing.getTitre(), def.getImageUrl());
                        break;
                    }
                }
            }
        }
    }

    private void initReligions() {
        try {
            jdbcTemplate.execute("ALTER TABLE religions MODIFY COLUMN proportion_population TEXT");
            jdbcTemplate.execute("ALTER TABLE religions MODIFY COLUMN nombre_pratiquants_estime TEXT");
            jdbcTemplate.execute("ALTER TABLE religions MODIFY COLUMN source_statistiques TEXT");
            jdbcTemplate.execute("ALTER TABLE religions MODIFY COLUMN annee_statistiques VARCHAR(100)");
            jdbcTemplate.execute("ALTER TABLE religions MODIFY COLUMN image_url TEXT");
        } catch (Exception e) {
            log.debug("Vérification/migration schéma table religions : {}", e.getMessage());
        }

        List<ReligionModel> defaultReligions = List.of(
                ReligionModel.builder()
                        .nom("Islam")
                        .categorie("Islam sunnite de rite malékite")
                        .description("L'Islam est la religion pratiquée par l'immense majorité de la population malienne. Introduit dès le IXe siècle le long des routes caravanières transsahariennes, il s'est profondément imprégné des valeurs de concorde, de tolérance mutuelle et de vivre-ensemble sahélien.")
                        .histoire("L'islamisation du Mali s'est opérée pacifiquement à travers le commerce transsaharien et l'enseignement des oulémas dans les empires du Ghana, du Mali (illustré par le pèlerinage fastueux de Kankou Moussa en 1324) et du Songhaï. L'université de Sankoré à Tombouctou a rayonné mondialement du XIVe au XVIe siècle comme épicentre théologique et juridique de rite malékite.")
                        .pratiques("La prière rituelle collective du vendredi (Jumu'ah), le jeûne sacré du mois de Ramadan, l'aumône légale (Zakat), la célébration du Maouloud (naissance du Prophète), la lecture des manuscrits anciens et la dévotion confrérique marquée par le Dhikr.")
                        .fetes("Aïd al-Fitr (Korité - fin du Ramadan), Aïd al-Adha (Tabaski - fête du sacrifice), Maouloud (naissance du Prophète Mohammed), Achoura (Tamkharit).")
                        .lieuxDeCulte("Grande Mosquée de Djenné, Mosquée Djingareyber et Mosquée Sankoré de Tombouctou, Mosquée de Komoguel à Mopti, Grande Mosquée Fayçal de Bamako, Mosquée de Niono.")
                        .regionsConcernees("Présent de manière majoritaire dans toutes les régions du Mali (Tombouctou, Gao, Kidal, Mopti, Ségou, Sikasso, Koulikoro, Kayes, San, Koutiala, Kita, Bandiagara et District de Bamako).")
                        .branches("• Sunnisme malékite (majorité écrasante des fidèles)\n• Confrérie Soufie Tijaniyya (très répandue, notamment via la branche Omarienne de Cheick Oumar Tall et la branche Hamalliste de Nioro du Sahel)\n• Confrérie Soufie Qadiriyya (historiquement ancrée au Nord et dans le delta central)\n• Courant réformiste / Wahhabisme / Salafisme (représenté notamment par des associations islamiques urbaines).")
                        .proportionPopulation("~94,8 %")
                        .nombrePratiquantsEstime("~20,8 millions de fidèles")
                        .sourceStatistiques("Recensement Général de la Population et de l'Habitat (RGPH-5, Institut National de la Statistique du Mali - INSTAT 2022) ; Rapport international sur la liberté religieuse du Département d'État (2023).")
                        .anneeStatistiques("2022-2023")
                        .methodologieStatistiques("Recensement national démographique exhaustif combiné aux enquêtes socio-démographiques harmonisées.")
                        .imageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/religions/da3688ff-8ecf-4226-9b1e-34864daec2fa_Islam.jpg")
                        .ordre(1)
                        .publie(true)
                        .build(),

                ReligionModel.builder()
                        .nom("Christianisme")
                        .categorie("Catholicisme et Protestantisme évangélique")
                        .description("Le christianisme au Mali regroupe une communauté dynamique et engagée, reconnue pour ses œuvres éducatives, ses structures hospitalières et son rôle actif dans le dialogue interreligieux national et la paix sociale.")
                        .histoire("La présence chrétienne contemporaine a débuté à la fin du XIXe siècle avec l'arrivée des Pères Blancs (Missionnaires d'Afrique) à Kita en 1888, puis à Ségou et Tombouctou. Des figures éminentes telles que le Cardinal Luc Auguste Sangaré ont profondément marqué l'histoire religieuse et républicaine du Mali par leur engagement pour la démocratie et la concorde entre communautés.")
                        .pratiques("La messe dominicale, le culte dominical chez les protestants, le catéchisme, le pèlerinage marial national annuel de Kita (rassemblant des dizaines de milliers de fidèles catholiques et de sympathisants chaque mois de novembre), les œuvres caritatives (Caritas Mali).")
                        .fetes("Noël (25 décembre, fête chômée au Mali), Pâques et Lundi de Pâques, Ascension, Assomption (15 août), Toussaint (1er novembre).")
                        .lieuxDeCulte("Cathédrale du Sacré-Cœur de Bamako, Sanctuaire Marial Notre-Dame de Kita, Cathédrale de Sikasso, Église de Mandiakuy, Paroisses de San et Ségou, temples de l'Église Évangélilique Protestante du Mali.")
                        .regionsConcernees("Présent principalement dans les régions de San, Ségou, Sikasso, Kita, Kayes, Mopti et dans le District de Bamako.")
                        .branches("• Église Catholique romaine : diocèses de Bamako, San, Sikasso, Ségou, Mopti et Kayes (environ deux tiers des chrétiens maliens)\n• Églises Protestantes et Évangéliques : Association des Groupements d'Églises et Missions Protestantes Évangéliques au Mali (AGEMPEM), Église Évangélique Protestante, Églises baptistes et pentecôtistes.")
                        .proportionPopulation("~2,4 %")
                        .nombrePratiquantsEstime("~530 000 fidèles")
                        .sourceStatistiques("Recensement Général de la Population et de l'Habitat (RGPH-5, INSTAT Mali 2022) ; Conférence Épiscopale du Mali (CEM) ; World Christian Database.")
                        .anneeStatistiques("2022")
                        .methodologieStatistiques("Recensement démographique officiel et registres paroissiaux déclarés.")
                        .imageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/religions/5c661a8e-a487-4ff3-b12d-a3f2fdacc544_spirituality-religion-women-religious-concepts-hands-praying-god-while-holding-cross-symbol-nun-caught-cross-his-hand.jpg")
                        .ordre(2)
                        .publie(true)
                        .build(),

                ReligionModel.builder()
                        .nom("Religions Traditionnelles Africaines (Animisme)")
                        .categorie("Spiritualités ancestrales et cosmogonies du terroir")
                        .description("Les religions traditionnelles incarnent le socle philosophique et cosmogonique séculaire des peuples du Mali. Fondées sur le respect du Créateur suprême, le culte des ancêtres et l'harmonie sacrée avec la nature, leurs valeurs irriguent profondément la culture et les coutumes maliennes.")
                        .histoire("Systèmes de pensée millénaires antérieurs aux religions monothéistes, les traditions spirituelles du Mandé, du Pays Dogon, du Kénédougou et du Bwatou ont structuré les sociétés initiatiques (Kôrêdugaw, Kômô, Nama, Ciwara, Poro sénoufo, Awa dogon). Ces institutions ont transmis les codes moraux, civiques, agricoles et médicinaux à travers les générations.")
                        .pratiques("Rites initiatiques d'initiation d'âge, libations et offrandes aux autels des ancêtres, danses de masques rituels (masques Dogon, Ciwara bambara), consultations géomantiques et rituels agraires lors des semailles et moissons.")
                        .fetes("Fêtes agraires traditionnelles, Fête du Sigui (tous les 60 ans chez les Dogon), Rites du Kamabloni de Kangaba (réfection septennale du toit de la case sacrée), Festival des masques et danses traditionnelles.")
                        .lieuxDeCulte("Bois sacrés communautaires, sanctuaire du Kamabloni à Kangaba, autels d'ancêtres des falaises de Bandiagara (Hogon), sanctuaires de Bwatou et du pays Sénoufo.")
                        .regionsConcernees("Présentes particulièrement dans le Pays Dogon (Bandiagara), les cercles de San et Tominian (Bwatou), la région de Sikasso et Koutiala (Sénoufo, Minianka), le Mandé (Koulikoro/Kita) et sous forme de syncrétisme culturel discret à travers tout le pays.")
                        .branches("• Cosmogonie et culte des ancêtres Dogon (dirigé spirituellement par le Hogon)\n• Société initiatique du Poro chez les Sénoufo\n• Rites initiatiques Bambara et Malinké (Kômô, Ciwara, Nama, société des Kôrêdugaw inscrite au patrimoine immatériel de l'UNESCO)\n• Traditions spirituelles Bwa et Bobo (culte du Do).")
                        .proportionPopulation("~2,0 %")
                        .nombrePratiquantsEstime("~440 000 adeptes exclusifs déclarés")
                        .sourceStatistiques("Recensement Général de la Population et de l'Habitat (RGPH-5, INSTAT Mali 2022) ; UNESCO (Patrimoine Culturel Immatériel).")
                        .anneeStatistiques("2022")
                        .methodologieStatistiques("Recensement officiel de la foi déclarée. Note méthodologique : De nombreux citoyens se déclarant musulmans ou chrétiens conservent également un attachement intime aux pratiques et valeurs coutumières ancestrales.")
                        .imageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/religions/008d855f-c051-4cfc-a117-4bc4a70cbffe_fetichisme.jpg")
                        .ordre(3)
                        .publie(true)
                        .build()
        );

        List<ReligionModel> existingReligions = religionRepository.findAll();
        if (existingReligions.isEmpty()) {
            log.info("Initialisation documentée et rigoureuse des religions au Mali...");
            religionRepository.saveAll(defaultReligions);
            log.info("{} fiches religieuses documentées initialisées avec rigueur statistique.", defaultReligions.size());
            return;
        }

        // Synchroniser automatiquement les URLs Supabase sur les fiches religieuses existantes qui ont encore des URLs Unsplash ou vides
        for (ReligionModel existing : existingReligions) {
            String currentUrl = existing.getImageUrl() != null ? existing.getImageUrl().trim() : "";
            if (currentUrl.isEmpty() || currentUrl.contains("unsplash.com")) {
                String nomLower = existing.getNom() != null ? existing.getNom().toLowerCase() : "";
                if (nomLower.contains("islam")) {
                    existing.setImageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/religions/da3688ff-8ecf-4226-9b1e-34864daec2fa_Islam.jpg");
                    religionRepository.save(existing);
                } else if (nomLower.contains("christ")) {
                    existing.setImageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/religions/5c661a8e-a487-4ff3-b12d-a3f2fdacc544_spirituality-religion-women-religious-concepts-hands-praying-god-while-holding-cross-symbol-nun-caught-cross-his-hand.jpg");
                    religionRepository.save(existing);
                } else if (nomLower.contains("animis") || nomLower.contains("tradition")) {
                    existing.setImageUrl("https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/religions/008d855f-c051-4cfc-a117-4bc4a70cbffe_fetichisme.jpg");
                    religionRepository.save(existing);
                }
            }
        }
    }

    private void initHomeFeaturedVilles() {
        List<VilleModel> villes = villeRepository.findAll();
        if (villes.isEmpty()) return;

        java.util.Map<String, String> supabaseVilleImages = java.util.Map.ofEntries(
                java.util.Map.entry("tombouctou", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/3984e765-781e-4966-9c04-90b19fd64403_deuxieme_imagehome.jpeg"),
                java.util.Map.entry("djenne", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/0d45c4b9-ddcb-4bfe-89fe-226739aacf74_Djenn_.jpg"),
                java.util.Map.entry("djenné", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/0d45c4b9-ddcb-4bfe-89fe-226739aacf74_Djenn_.jpg"),
                java.util.Map.entry("bamako", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/72e5bb3b-0144-4ad7-aea5-0eb6d4b2e3f5_Bamako.jpg"),
                java.util.Map.entry("mopti", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/d35072d2-51f7-4dc5-b906-f1199b4d2fa7_Mopti__Mali.jpeg"),
                java.util.Map.entry("segou", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/046acd3d-6899-4076-b662-2502cbbda963_Segou__Mali___.jpeg"),
                java.util.Map.entry("ségou", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/046acd3d-6899-4076-b662-2502cbbda963_Segou__Mali___.jpeg"),
                java.util.Map.entry("sikasso", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/2ba6d709-eafa-4d39-929d-61fbcf2d2bee_Le-Mamelon-de-Sikasso-Sindebou-_StudioTamani.jpg"),
                java.util.Map.entry("kayes", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/a74e7a57-4181-4298-b98a-bf3c59702646_Fleuve_S_n_gal_Kayes_Mali.jpeg"),
                java.util.Map.entry("koulikoro", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/910921e0-844f-4af7-b5dd-5aab12fcbaef_vestibule-koulikoro.jpg"),
                java.util.Map.entry("koutiala", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/2e0e6bf5-0023-494b-9157-f24aa881512b_koutiala.jpeg"),
                java.util.Map.entry("san", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/4aac1e77-f062-4bf8-8d8f-9b8ec39e1df0_san_sanke-mon.jpg"),
                java.util.Map.entry("kenieba", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/20c147b6-b759-4b58-b974-3c4611f2b1cb_kenieba.jpeg"),
                java.util.Map.entry("kéniéba", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/20c147b6-b759-4b58-b974-3c4611f2b1cb_kenieba.jpeg"),
                java.util.Map.entry("kita", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/8a2b87e9-7ef4-4298-8698-ac341176f1ce_kita.jpeg"),
                java.util.Map.entry("bandiagara", "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/092d8b10-62e7-4254-9e2e-3b97571beb46_badiangara.jpeg")
        );

        boolean hasFeatured = villes.stream().anyMatch(v -> Boolean.TRUE.equals(v.getMisEnAvantHome()));
        int order = 1;
        for (VilleModel v : villes) {
            boolean modified = false;
            String nom = v.getNomVille() != null ? v.getNomVille().trim().toLowerCase() : "";

            // Synchronisation automatique de l'image Supabase de la ville si absente, locale ou Unsplash
            String currentImg = v.getImageUrl() != null ? v.getImageUrl().trim() : "";
            if (currentImg.isEmpty() || currentImg.startsWith("assets/") || currentImg.contains("unsplash.com")) {
                for (java.util.Map.Entry<String, String> entry : supabaseVilleImages.entrySet()) {
                    if (nom.contains(entry.getKey())) {
                        v.setImageUrl(entry.getValue());
                        modified = true;
                        break;
                    }
                }
            }

            if (!hasFeatured && (nom.contains("tombouctou") || nom.contains("djenne") || nom.contains("djenné") ||
                    nom.contains("segou") || nom.contains("ségou") || nom.contains("mopti") ||
                    nom.contains("bamako") || nom.contains("sikasso") || nom.contains("kayes") ||
                    nom.contains("san") || nom.contains("koutiala") || nom.contains("kita") ||
                    nom.contains("bandiagara"))) {
                v.setMisEnAvantHome(true);
                v.setOrdreHome(order++);
                modified = true;
            }

            if (modified) {
                villeRepository.save(v);
            }
        }
    }
}

