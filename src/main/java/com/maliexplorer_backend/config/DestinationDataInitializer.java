package com.maliexplorer_backend.config;

import com.maliexplorer_backend.model.ImageModel;
import com.maliexplorer_backend.model.RegionModel;
import com.maliexplorer_backend.model.VilleModel;
import com.maliexplorer_backend.repository.ImageRepository;
import com.maliexplorer_backend.repository.RegionRepository;
import com.maliexplorer_backend.repository.VilleRepository;
import com.maliexplorer_backend.service.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Initialiseur de données pour l'ensemble des 16 régions administratives du Mali,
 * du District de Bamako et des 17 villes patrimoniales majeures avec coordonnées GPS WGS84 réelles
 * et synchronisation des images vers Supabase Storage.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class DestinationDataInitializer implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final VilleRepository villeRepository;
    private final ImageRepository imageRepository;
    private final SupabaseStorageService supabaseStorageService;
    private final JdbcTemplate jdbcTemplate;

    private static final String SUPABASE_BASE = "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media";

    @Override
    public void run(String... args) {
        log.info("Vérification et synchronisation exhaustive des 16 régions et 17 villes du Mali...");

        try {
            ensureRegionSchema();
            syncLocalAssetsToSupabase();

            // 1. BAMAKO (District de la Capitale)
            String bamakoMain = SUPABASE_BASE + "/villes/72e5bb3b-0144-4ad7-aea5-0eb6d4b2e3f5_Bamako.jpg";
            String bamakoGal1 = SUPABASE_BASE + "/villes/bamako_gallery_1.jpeg";
            String bamakoGal2 = SUPABASE_BASE + "/villes/bamako_gallery_2.jpeg";
            RegionModel bamakoRegion = initRegion(
                    "District de Bamako",
                    "Bamako",
                    3000000L,
                    "252 km²",
                    12.6392,
                    -8.0029,
                    "Capitale politique, économique et culturelle du Mali, lovée dans la vallée du fleuve Niger au pied des monts Mandingues.",
                    bamakoMain
            );
            initVille(
                    "Bamako",
                    bamakoRegion.getNomRegion(),
                    "3 000 000",
                    "Métropole effervescente et carrefour de l'Afrique de l'Ouest, centre névralgique de l'artisanat et de la musique malienne.",
                    String.join(",", bamakoMain, bamakoGal1, bamakoGal2),
                    12.6392,
                    -8.0029,
                    bamakoRegion
            );

            // 2. KAYES (1ère région administrative)
            String kayesImg = SUPABASE_BASE + "/villes/a74e7a57-4181-4298-b98a-bf3c59702646_Fleuve_S_n_gal_Kayes_Mali.jpeg";
            RegionModel kayesRegion = initRegion(
                    "Région de Kayes",
                    "Kayes",
                    1996000L,
                    "119 743 km²",
                    14.4469,
                    -11.4445,
                    "Première région administrative du Mali, berceau des chutes du Félou et de Gouina, et du Fort historique de Médine.",
                    kayesImg
            );
            initVille(
                    "Kayes",
                    kayesRegion.getNomRegion(),
                    "130 000",
                    "Carrefour commercial frontalier avec le Sénégal et la Mauritanie, sur les rives du fleuve Sénégal.",
                    kayesImg,
                    14.4469,
                    -11.4445,
                    kayesRegion
            );

            // 3. KOULIKORO (2ème région administrative)
            String koulikoroImg = SUPABASE_BASE + "/villes/910921e0-844f-4af7-b5dd-5aab12fcbaef_vestibule-koulikoro.jpg";
            RegionModel koulikoroRegion = initRegion(
                    "Région de Koulikoro",
                    "Koulikoro",
                    2418000L,
                    "90 120 km²",
                    12.8627,
                    -7.5599,
                    "Deuxième région administrative du Mali, terminus du fleuve Niger et terre mythique des monts Mandingues.",
                    koulikoroImg
            );
            initVille(
                    "Koulikoro",
                    koulikoroRegion.getNomRegion(),
                    "43 000",
                    "Port terminal navigable sur le Niger et site légendaire de la disparition de Soumaoro Kanté.",
                    koulikoroImg,
                    12.8627,
                    -7.5599,
                    koulikoroRegion
            );

            // 4. SIKASSO (3ème région administrative)
            String sikassoMain = SUPABASE_BASE + "/villes/2ba6d709-eafa-4d39-929d-61fbcf2d2bee_Le-Mamelon-de-Sikasso-Sindebou-_StudioTamani.jpg";
            String sikassoGal1 = SUPABASE_BASE + "/villes/sikasso_gallery_1.jpg";
            String sikassoGal2 = SUPABASE_BASE + "/villes/sikasso_gallery_2.jpg";
            String sikassoGal3 = SUPABASE_BASE + "/villes/sikasso_gallery_3.jpeg";
            RegionModel sikassoRegion = initRegion(
                    "Région de Sikasso",
                    "Sikasso",
                    2625000L,
                    "70 280 km²",
                    11.3176,
                    -5.6665,
                    "Le grenier vert et agricole du Mali, capitale du grand Royaume du Kénédougou des rois Tiéba et Babemba Traoré.",
                    sikassoMain
            );
            initVille(
                    "Sikasso",
                    sikassoRegion.getNomRegion(),
                    "226 000",
                    "Capitale héroïque du Kénédougou, célèbre pour les ruines du Tata défensif et les chutes de Farako.",
                    String.join(",", sikassoMain, sikassoGal1, sikassoGal2, sikassoGal3),
                    11.3176,
                    -5.6665,
                    sikassoRegion
            );

            // 5. SÉGOU (4ème région administrative)
            String segouMain = SUPABASE_BASE + "/villes/046acd3d-6899-4076-b662-2502cbbda963_Segou__Mali___.jpeg";
            String segouGal1 = SUPABASE_BASE + "/villes/segou_gallery_1.jpg";
            String segouGal2 = SUPABASE_BASE + "/villes/segou_gallery_2.jpeg";
            String segouGal3 = SUPABASE_BASE + "/villes/segou_gallery_3.jpeg";
            RegionModel segouRegion = initRegion(
                    "Région de Ségou",
                    "Ségou",
                    2336000L,
                    "64 821 km²",
                    13.4317,
                    -6.2157,
                    "La prestigieuse Cité des Balanzans et berceau du puissant royaume Bambara fondé par Biton Coulibaly.",
                    segouMain
            );
            initVille(
                    "Ségou",
                    segouRegion.getNomRegion(),
                    "134 000",
                    "Cité royale le long du fleuve Niger réputée pour ses poteries traditionnelles de Kalabougou et son festival sur le Niger.",
                    String.join(",", segouMain, segouGal1, segouGal2, segouGal3),
                    13.4317,
                    -6.2157,
                    segouRegion
            );

            // 6. MOPTI (5ème région administrative)
            String moptiMain = SUPABASE_BASE + "/villes/d35072d2-51f7-4dc5-b906-f1199b4d2fa7_Mopti__Mali.jpeg";
            String moptiGal1 = SUPABASE_BASE + "/villes/mopti_gallery_1.jpg";
            String moptiGal2 = SUPABASE_BASE + "/villes/mopti_gallery_2.jpeg";
            String moptiGal3 = SUPABASE_BASE + "/villes/mopti_gallery_3.webp";
            RegionModel moptiRegion = initRegion(
                    "Région de Mopti",
                    "Mopti",
                    2037000L,
                    "79 017 km²",
                    14.4958,
                    -4.1873,
                    "La Venise malienne au confluent du Niger et du Bani, carrefour commercial et porte d'accès vers Djenné.",
                    moptiMain
            );
            initVille(
                    "Mopti",
                    moptiRegion.getNomRegion(),
                    "114 000",
                    "Port fluvial majeur du delta central avec ses chantiers de fabrication artisanale de grandes pinasses.",
                    String.join(",", moptiMain, moptiGal1, moptiGal2, moptiGal3),
                    14.4958,
                    -4.1873,
                    moptiRegion
            );
            String djenneMain = SUPABASE_BASE + "/villes/0d45c4b9-ddcb-4bfe-89fe-226739aacf74_Djenn_.jpg";
            String djenneGal1 = SUPABASE_BASE + "/villes/djenne_gallery_1.jpeg";
            String djenneGal2 = SUPABASE_BASE + "/villes/djenne_gallery_2.jpeg";
            initVille(
                    "Djenné",
                    moptiRegion.getNomRegion(),
                    "33 000",
                    "Joyau architectural universel en banco, abritant la plus grande mosquée en terre crue au monde inscrite à l'UNESCO.",
                    String.join(",", djenneMain, djenneGal1, djenneGal2),
                    13.9056,
                    -4.5533,
                    moptiRegion
            );

            // 7. TOMBOUCTOU (6ème région administrative)
            String tombouctouMain = SUPABASE_BASE + "/villes/3984e765-781e-4966-9c04-90b19fd64403_deuxieme_imagehome.jpeg";
            String tombouctou2 = SUPABASE_BASE + "/villes/Tombouctou2.jpg";
            String tombouctouGal1 = SUPABASE_BASE + "/villes/tombouctou_gallery_1.jpeg";
            String tombouctouGal2 = SUPABASE_BASE + "/villes/tombouctou_gallery_2.jpeg";
            RegionModel tombouctouRegion = initRegion(
                    "Région de Tombouctou",
                    "Tombouctou",
                    681000L,
                    "496 446 km²",
                    16.7666,
                    -3.0026,
                    "La mystique Cité des 333 saints aux portes du Sahara, phare du savoir islamique médiéval et des manuscrits anciens.",
                    tombouctouMain
            );
            initVille(
                    "Tombouctou",
                    tombouctouRegion.getNomRegion(),
                    "54 000",
                    "Cité millénaire classée à l'UNESCO abritant les célèbres mosquées de Djingareyber, Sankoré et Sidi Yahia.",
                    String.join(",", tombouctouMain, tombouctou2, tombouctouGal1, tombouctouGal2),
                    16.7666,
                    -3.0026,
                    tombouctouRegion
            );

            // 8. GAO (7ème région administrative)
            String gao1 = SUPABASE_BASE + "/villes/Gao1.jpg";
            String gao2 = SUPABASE_BASE + "/villes/Gao2.jpg";
            RegionModel gaoRegion = initRegion(
                    "Région de Gao",
                    "Gao",
                    544000L,
                    "170 572 km²",
                    16.2717,
                    -0.0447,
                    "L'illustre capitale de l'Empire Songhaï au XVe et XVIe siècle sous l'Askia Mohammed, abritant le Tombeau des Askia (UNESCO).",
                    gao1
            );
            initVille(
                    "Gao",
                    gaoRegion.getNomRegion(),
                    "86 000",
                    "Cité historique sahélienne au bord du Niger, réputée pour sa dune rose de Koïma et ses traditions songhaï.",
                    String.join(",", gao1, gao2),
                    16.2717,
                    -0.0447,
                    gaoRegion
            );

            // 9. KIDAL (8ème région administrative)
            String kidal1 = SUPABASE_BASE + "/villes/Kidal1.jpg";
            String kidal2 = SUPABASE_BASE + "/villes/Kidal2.jpeg";
            RegionModel kidalRegion = initRegion(
                    "Région de Kidal",
                    "Kidal",
                    67000L,
                    "151 450 km²",
                    18.4411,
                    1.4078,
                    "Région montagnarde et désertique de l'Adrar des Ifoghas, haut lieu de la culture nomade touarègue et de l'artisanat du cuir et de l'argent.",
                    kidal1
            );
            initVille(
                    "Kidal",
                    kidalRegion.getNomRegion(),
                    "25 000",
                    "Centre urbain niché dans les vallées de l'Adrar des Ifoghas, carrefour des traditions orales et poétiques du désert.",
                    String.join(",", kidal1, kidal2),
                    18.4411,
                    1.4078,
                    kidalRegion
            );

            // 10. TAOUDÉNIT (9ème région administrative)
            String taoudeni1 = SUPABASE_BASE + "/villes/Taoudeni1.JPG";
            RegionModel taoudenitRegion = initRegion(
                    "Région de Taoudénit",
                    "Taoudéni",
                    18000L,
                    "323 326 km²",
                    22.6781,
                    -3.9842,
                    "Vaste étendue désertique du grand nord malien, célèbre pour ses mines de sel gemme millénaires et les caravanes d'azalaï.",
                    taoudeni1
            );
            initVille(
                    "Taoudéni",
                    taoudenitRegion.getNomRegion(),
                    "3 000",
                    "Bourgade saharienne légendaire, cœur historique de l'extraction traditionnelle des barres de sel gemme.",
                    taoudeni1,
                    22.6781,
                    -3.9842,
                    taoudenitRegion
            );

            // 11. MÉNAKA (10ème région administrative)
            String menaka1 = SUPABASE_BASE + "/villes/Menaka1.jpg";
            String menaka2 = SUPABASE_BASE + "/villes/Menaka2.jpg";
            RegionModel menakaRegion = initRegion(
                    "Région de Ménaka",
                    "Ménaka",
                    54000L,
                    "81 040 km²",
                    15.9182,
                    2.4022,
                    "Région pastorale de l'extrême est du Mali frontalière du Niger, traversée par la vallée fossile de l'Azawagh.",
                    menaka1
            );
            initVille(
                    "Ménaka",
                    menakaRegion.getNomRegion(),
                    "20 000",
                    "Carrefour pastoral sahélien renommé pour ses foires d'élevage et son artisanat d'art nomade.",
                    String.join(",", menaka1, menaka2),
                    15.9182,
                    2.4022,
                    menakaRegion
            );

            // 12. KOUTIALA (Région administrative - Capitale du coton)
            String koutialaImg = SUPABASE_BASE + "/villes/2e0e6bf5-0023-494b-9157-f24aa881512b_koutiala.jpeg";
            RegionModel koutialaRegion = initRegion(
                    "Région de Koutiala",
                    "Koutiala",
                    575000L,
                    "18 000 km²",
                    12.3917,
                    -5.4642,
                    "Capitale de l’or blanc (coton) et pôle agro-industriel majeur du sud malien, fief de la culture Minianka.",
                    koutialaImg
            );
            initVille(
                    "Koutiala",
                    koutialaRegion.getNomRegion(),
                    "140 000",
                    "Capitale agro-pastorale et centre cotonnier névralgique de la CMDT.",
                    koutialaImg,
                    12.3917,
                    -5.4642,
                    koutialaRegion
            );

            // 13. SAN (Région administrative - Cité du Sanké)
            String sanImg = SUPABASE_BASE + "/villes/4aac1e77-f062-4bf8-8d8f-9b8ec39e1df0_san_sanke-mon.jpg";
            RegionModel sanRegion = initRegion(
                    "Région de San",
                    "San",
                    330000L,
                    "7 262 km²",
                    13.3034,
                    -4.8956,
                    "Carrefour culturel du Bani célèbre pour sa pêche collective du Sanké Mô (UNESCO) et son église néo-soudanaise en banco.",
                    sanImg
            );
            initVille(
                    "San",
                    sanRegion.getNomRegion(),
                    "68 000",
                    "Cité carrefour sur le fleuve Bani renommée pour ses traditions Bobo et son architecture soudanaise.",
                    sanImg,
                    13.3034,
                    -4.8956,
                    sanRegion
            );

            // 14. KÉNIÉBA (Cercle historique du Bambouk)
            String keniebaImg = SUPABASE_BASE + "/villes/20c147b6-b759-4b58-b974-3c4611f2b1cb_kenieba.jpeg";
            RegionModel keniebaRegion = initRegion(
                    "Cercle de Kéniéba",
                    "Kéniéba",
                    215000L,
                    "16 800 km²",
                    12.8530,
                    -11.2260,
                    "Cœur historique du Bambouk et capitale de l’or traditionnel, adossée aux falaises de grès du Tambaoura.",
                    keniebaImg
            );
            initVille(
                    "Kéniéba",
                    keniebaRegion.getNomRegion(),
                    "39 000",
                    "Cité aurifère ancestrale bordée par les spectaculaires falaises de grès du Tambaoura.",
                    keniebaImg,
                    12.8530,
                    -11.2260,
                    keniebaRegion
            );

            // 15. KITA (Région administrative - Cité des griots)
            String kitaImg = SUPABASE_BASE + "/villes/8a2b87e9-7ef4-4298-8698-ac341176f1ce_kita.jpeg";
            RegionModel kitaRegion = initRegion(
                    "Région de Kita",
                    "Kita",
                    438000L,
                    "35 250 km²",
                    13.0349,
                    -9.4895,
                    "Capitale de l’arachide, haut lieu spirituel et berceau incontesté des griots mandingues et de la kora.",
                    kitaImg
            );
            initVille(
                    "Kita",
                    kitaRegion.getNomRegion(),
                    "55 000",
                    "Cité sacrée au pied du Kita Kourou, sanctuaire marial et carrefour des maîtres de la parole.",
                    kitaImg,
                    13.0349,
                    -9.4895,
                    kitaRegion
            );

            // 16. BANDIAGARA (Région administrative - Pays Dogon)
            String bandiagaraImg = SUPABASE_BASE + "/villes/092d8b10-62e7-4254-9e2e-3b97571beb46_badiangara.jpeg";
            RegionModel bandiagaraRegion = initRegion(
                    "Région de Bandiagara",
                    "Bandiagara",
                    320000L,
                    "10 520 km²",
                    14.3501,
                    -3.6104,
                    "Capitale du Pays Dogon et des falaises inscrites au patrimoine mondial de l’UNESCO, patrie d’Amadou Hampâté Bâ.",
                    bandiagaraImg
            );
            initVille(
                    "Bandiagara",
                    bandiagaraRegion.getNomRegion(),
                    "45 000",
                    "Porte d'accès au plateau et aux falaises Dogons inscrites au patrimoine mondial de l’UNESCO.",
                    bandiagaraImg,
                    14.3501,
                    -3.6104,
                    bandiagaraRegion
            );

            // Déduplication des 8 régions brutes legacy (Bamako, Ségou, Mopti, Tombouctou, Sikasso, Gao, Kayes, Koulikoro)
            deduplicateBareRegions();

            log.info("Synchronisation des 16 régions et 17 villes du Mali terminée avec succès.");
        } catch (Exception e) {
            log.warn("Avertissement lors de la synchronisation des destinations : {}", e.getMessage());
        }
    }

    private void ensureRegionSchema() {
        try {
            jdbcTemplate.execute("ALTER TABLE regions MODIFY COLUMN image_url TEXT NULL");
        } catch (Exception ignored) {}
        try {
            jdbcTemplate.execute("ALTER TABLE regions ADD COLUMN latitude DOUBLE NULL");
        } catch (Exception ignored) {}
        try {
            jdbcTemplate.execute("ALTER TABLE regions ADD COLUMN longitude DOUBLE NULL");
        } catch (Exception ignored) {}
        try {
            jdbcTemplate.execute("ALTER TABLE villes MODIFY COLUMN image_url TEXT NULL");
        } catch (Exception ignored) {}
    }

    private Path resolveAssetsDir() {
        List<Path> candidates = List.of(
                Paths.get("../MaliExplorer-front-end/assets/images"),
                Paths.get("MaliExplorer-front-end/assets/images"),
                Paths.get("/home/joshua/Desktop/Mali_Explorer/MaliExplorer-front-end/assets/images")
        );
        for (Path p : candidates) {
            if (Files.isDirectory(p)) {
                return p;
            }
        }
        return null;
    }

    private void syncLocalAssetsToSupabase() {
        Path assetsDir = resolveAssetsDir();
        if (assetsDir == null) {
            return;
        }

        List<String> villeAssets = List.of(
                "Gao1.jpg", "Gao2.jpg",
                "Kidal1.jpg", "Kidal2.jpeg",
                "Menaka1.jpg", "Menaka2.jpg",
                "Taoudeni1.JPG",
                "Tombouctou2.jpg", "tombouctou_gallery_1.jpeg", "tombouctou_gallery_2.jpeg",
                "bamako_gallery_1.jpeg", "bamako_gallery_2.jpeg",
                "djenne_gallery_1.jpeg", "djenne_gallery_2.jpeg",
                "mopti_gallery_1.jpg", "mopti_gallery_2.jpeg", "mopti_gallery_3.webp",
                "segou_gallery_1.jpg", "segou_gallery_2.jpeg", "segou_gallery_3.jpeg",
                "sikasso_gallery_1.jpg", "sikasso_gallery_2.jpg", "sikasso_gallery_3.jpeg"
        );

        for (String fileName : villeAssets) {
            Path localFile = assetsDir.resolve(fileName);
            if (Files.exists(localFile)) {
                String url = supabaseStorageService.uploadLocalFileDeterministic(localFile, "villes", fileName);
                registerImageIfMissing(fileName, url, localFile, "VILLE", null);
            }
        }

        List<String> presidentAssets = List.of(
                "amadou_haya_sanogo.jpeg",
                "bah_ndaw.jpeg",
                "dioncounda_traore.png"
        );
        for (String fileName : presidentAssets) {
            Path localFile = assetsDir.resolve("presidents").resolve(fileName);
            if (Files.exists(localFile)) {
                String url = supabaseStorageService.uploadLocalFileDeterministic(localFile, "presidents", fileName);
                registerImageIfMissing(fileName, url, localFile, "PRESIDENT", null);
            }
        }

        Path langCover = assetsDir.resolve("languages_cover.jpeg");
        if (Files.exists(langCover)) {
            String url = supabaseStorageService.uploadLocalFileDeterministic(langCover, "langues", "languages_cover.jpeg");
            registerImageIfMissing("languages_cover.jpeg", url, langCover, "LANGUES", null);
        }
    }

    private void registerImageIfMissing(String fileName, String url, Path localFile, String entiteType, Long entiteId) {
        try {
            boolean exists = imageRepository.findAll().stream()
                    .anyMatch(img -> url.equalsIgnoreCase(img.getImageUrl()));
            if (!exists) {
                long size = Files.exists(localFile) ? Files.size(localFile) : 0L;
                String mime = Files.exists(localFile) ? Files.probeContentType(localFile) : "image/jpeg";
                ImageModel model = ImageModel.builder()
                        .nomFichier(fileName)
                        .imageUrl(url)
                        .typeMime(mime != null ? mime : "image/jpeg")
                        .taille(size)
                        .entiteType(entiteType)
                        .entiteId(entiteId)
                        .dateUpload(LocalDateTime.now())
                        .build();
                imageRepository.save(model);
            }
        } catch (Exception ignored) {}
    }

    private void deduplicateBareRegions() {
        Map<String, String> bareToCanonical = new LinkedHashMap<>();
        bareToCanonical.put("Bamako", "District de Bamako");
        bareToCanonical.put("Kayes", "Région de Kayes");
        bareToCanonical.put("Koulikoro", "Région de Koulikoro");
        bareToCanonical.put("Sikasso", "Région de Sikasso");
        bareToCanonical.put("Ségou", "Région de Ségou");
        bareToCanonical.put("Mopti", "Région de Mopti");
        bareToCanonical.put("Tombouctou", "Région de Tombouctou");
        bareToCanonical.put("Gao", "Région de Gao");

        for (Map.Entry<String, String> entry : bareToCanonical.entrySet()) {
            String bareName = entry.getKey();
            String canonicalName = entry.getValue();

            Optional<RegionModel> bareOpt = regionRepository.findByNomRegionIgnoreCase(bareName);
            Optional<RegionModel> canonicalOpt = regionRepository.findByNomRegionIgnoreCase(canonicalName);

            if (bareOpt.isPresent() && canonicalOpt.isPresent()) {
                RegionModel bare = bareOpt.get();
                RegionModel canonical = canonicalOpt.get();
                if (!bare.getIdRegion().equals(canonical.getIdRegion())) {
                    Long bareId = bare.getIdRegion();
                    Long canonicalId = canonical.getIdRegion();
                    try {
                        jdbcTemplate.update(
                                "UPDATE villes SET id_region = ?, region = ? WHERE id_region = ?",
                                canonicalId, canonicalName, bareId
                        );
                    } catch (Exception ignored) {}
                    try {
                        jdbcTemplate.update(
                                "UPDATE IGNORE region_ethnie SET region_id = ? WHERE region_id = ?",
                                canonicalId, bareId
                        );
                        jdbcTemplate.update("DELETE FROM region_ethnie WHERE region_id = ?", bareId);
                    } catch (Exception ignored) {}
                    try {
                        jdbcTemplate.update(
                                "UPDATE IGNORE region_plat SET region_id = ? WHERE region_id = ?",
                                canonicalId, bareId
                        );
                        jdbcTemplate.update("DELETE FROM region_plat WHERE region_id = ?", bareId);
                    } catch (Exception ignored) {}
                    try {
                        jdbcTemplate.update("DELETE FROM regions WHERE id_region = ?", bareId);
                        log.info("Doublon de région '{}' (ID {}) fusionné vers '{}' (ID {}) et supprimé.",
                                bareName, bareId, canonicalName, canonicalId);
                    } catch (Exception e) {
                        log.warn("Impossible de supprimer le doublon région ID {} : {}", bareId, e.getMessage());
                    }
                }
            }
        }
    }

    private boolean isPlaceholderOrWrongSlideImage(String currentUrl, String nom) {
        if (currentUrl == null || currentUrl.isBlank()) return true;
        if (currentUrl.startsWith("assets/")) return true;
        if (currentUrl.contains("unsplash.com")) return true;
        String lowerNom = nom != null ? nom.toLowerCase() : "";
        if (!lowerNom.contains("tombouctou") && currentUrl.contains("deuxieme_imagehome.jpeg")) {
            return true;
        }
        if (!lowerNom.contains("bandiagara") && !lowerNom.contains("dogon") && currentUrl.contains("Africa_-_Mali_-_Banani.jpeg")) {
            return true;
        }
        return false;
    }

    private RegionModel initRegion(
            String nomRegion,
            String chefLieu,
            Long nbreHbt,
            String superficie,
            Double latitude,
            Double longitude,
            String description,
            String imageUrl
    ) {
        return regionRepository.findByNomRegionIgnoreCase(nomRegion)
                .map(existing -> {
                    boolean updated = false;
                    if ((existing.getChefLieu() == null || existing.getChefLieu().isBlank()) && chefLieu != null) {
                        existing.setChefLieu(chefLieu);
                        updated = true;
                    }
                    if (existing.getNbreHbt() == null && nbreHbt != null) {
                        existing.setNbreHbt(nbreHbt);
                        updated = true;
                    }
                    if ((existing.getSuperficie() == null || existing.getSuperficie().isBlank()) && superficie != null) {
                        existing.setSuperficie(superficie);
                        updated = true;
                    }
                    if (existing.getLatitude() == null && latitude != null) {
                        existing.setLatitude(latitude);
                        updated = true;
                    }
                    if (existing.getLongitude() == null && longitude != null) {
                        existing.setLongitude(longitude);
                        updated = true;
                    }
                    if ((existing.getDescription() == null || existing.getDescription().isBlank()) && description != null) {
                        existing.setDescription(description);
                        updated = true;
                    }
                    if (isPlaceholderOrWrongSlideImage(existing.getImageUrl(), nomRegion) && imageUrl != null) {
                        existing.setImageUrl(imageUrl);
                        updated = true;
                    }
                    return updated ? regionRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    log.info("Création de la région : {}", nomRegion);
                    RegionModel region = RegionModel.builder()
                            .nomRegion(nomRegion)
                            .chefLieu(chefLieu)
                            .nbreHbt(nbreHbt)
                            .superficie(superficie)
                            .latitude(latitude)
                            .longitude(longitude)
                            .description(description)
                            .imageUrl(imageUrl)
                            .build();
                    return regionRepository.save(region);
                });
    }

    private void initVille(
            String nomVille,
            String regionName,
            String nbreHbt,
            String description,
            String imageUrl,
            Double lat,
            Double lng,
            RegionModel regionParent
    ) {
        villeRepository.findByNomVilleIgnoreCase(nomVille)
                .ifPresentOrElse(existing -> {
                    boolean updated = false;
                    if (existing.getLatitude() == null && lat != null) {
                        existing.setLatitude(lat);
                        existing.setLongitude(lng);
                        existing.setCordonnees(lat + ", " + lng);
                        updated = true;
                    }
                    if ((existing.getNbreHbt() == null || existing.getNbreHbt().isBlank()) && nbreHbt != null) {
                        existing.setNbreHbt(nbreHbt);
                        updated = true;
                    }
                    if ((existing.getDescription() == null || existing.getDescription().isBlank()) && description != null) {
                        existing.setDescription(description);
                        updated = true;
                    }
                    if (regionParent != null && (existing.getRegionParent() == null
                            || !regionParent.getIdRegion().equals(existing.getRegionParent().getIdRegion()))) {
                        existing.setRegionParent(regionParent);
                        existing.setRegion(regionName);
                        updated = true;
                    }
                    String currentImg = existing.getImageUrl();
                    if (imageUrl != null && (isPlaceholderOrWrongSlideImage(currentImg, nomVille)
                            || (imageUrl.contains(",") && (currentImg == null || !currentImg.contains(","))))) {
                        existing.setImageUrl(imageUrl);
                        updated = true;
                    }
                    if (updated) {
                        villeRepository.save(existing);
                    }
                }, () -> {
                    log.info("Création de la ville : {}", nomVille);
                    VilleModel ville = VilleModel.builder()
                            .nomVille(nomVille)
                            .region(regionName)
                            .nbreHbt(nbreHbt)
                            .description(description)
                            .imageUrl(imageUrl)
                            .latitude(lat)
                            .longitude(lng)
                            .cordonnees(lat != null && lng != null ? (lat + ", " + lng) : null)
                            .regionParent(regionParent)
                            .build();
                    villeRepository.save(ville);
                });
    }
}
