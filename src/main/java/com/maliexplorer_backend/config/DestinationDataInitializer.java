package com.maliexplorer_backend.config;

import com.maliexplorer_backend.model.RegionModel;
import com.maliexplorer_backend.model.VilleModel;
import com.maliexplorer_backend.repository.RegionRepository;
import com.maliexplorer_backend.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Initialiseur de données pour l'ensemble des 16 régions administratives du Mali,
 * du District de Bamako et des villes patrimoniales majeures avec coordonnées GPS réelles.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class DestinationDataInitializer implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final VilleRepository villeRepository;

    @Override
    public void run(String... args) {
        log.info("Vérification et synchronisation exhaustive des 16 régions et villes du Mali...");

        try {
            // 1. BAMAKO (District de la Capitale)
            RegionModel bamakoRegion = initRegion(
                    "District de Bamako",
                    "Bamako",
                    3000000L,
                    "252 km²",
                    "Capitale politique, économique et culturelle du Mali, lovée dans la vallée du fleuve Niger au pied des monts Mandingues.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/72e5bb3b-0144-4ad7-aea5-0eb6d4b2e3f5_Bamako.jpg"
            );
            initVille(
                    "Bamako",
                    bamakoRegion.getNomRegion(),
                    "3 000 000",
                    "Métropole effervescente et carrefour de l'Afrique de l'Ouest, centre névralgique de l'artisanat et de la musique malienne.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/72e5bb3b-0144-4ad7-aea5-0eb6d4b2e3f5_Bamako.jpg",
                    12.6392,
                    -8.0029,
                    bamakoRegion
            );

            // 2. KAYES (1ère région administrative)
            RegionModel kayesRegion = initRegion(
                    "Région de Kayes",
                    "Kayes",
                    1996000L,
                    "119 743 km²",
                    "Première région administrative du Mali, berceau des chutes du Félou et de Gouina, et du Fort historique de Médine.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/a74e7a57-4181-4298-b98a-bf3c59702646_Fleuve_S_n_gal_Kayes_Mali.jpeg"
            );
            initVille(
                    "Kayes",
                    kayesRegion.getNomRegion(),
                    "130 000",
                    "Carrefour commercial frontalier avec le Sénégal et la Mauritanie, sur les rives du fleuve Sénégal.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/a74e7a57-4181-4298-b98a-bf3c59702646_Fleuve_S_n_gal_Kayes_Mali.jpeg",
                    14.4469,
                    -11.4445,
                    kayesRegion
            );

            // 3. KOULIKORO (2ème région administrative)
            RegionModel koulikoroRegion = initRegion(
                    "Région de Koulikoro",
                    "Koulikoro",
                    2418000L,
                    "90 120 km²",
                    "Deuxième région administrative du Mali, terminus du fleuve Niger et terre mythique des monts Mandingues.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/910921e0-844f-4af7-b5dd-5aab12fcbaef_vestibule-koulikoro.jpg"
            );
            initVille(
                    "Koulikoro",
                    koulikoroRegion.getNomRegion(),
                    "43 000",
                    "Port terminal navigable sur le Niger et site légendaire de la disparition de Soumaoro Kanté.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/910921e0-844f-4af7-b5dd-5aab12fcbaef_vestibule-koulikoro.jpg",
                    12.8627,
                    -7.5599,
                    koulikoroRegion
            );

            // 4. SIKASSO (3ème région administrative)
            RegionModel sikassoRegion = initRegion(
                    "Région de Sikasso",
                    "Sikasso",
                    2625000L,
                    "70 280 km²",
                    "Le grenier vert et agricole du Mali, capitale du grand Royaume du Kénédougou des rois Tiéba et Babemba Traoré.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/2ba6d709-eafa-4d39-929d-61fbcf2d2bee_Le-Mamelon-de-Sikasso-Sindebou-_StudioTamani.jpg"
            );
            initVille(
                    "Sikasso",
                    sikassoRegion.getNomRegion(),
                    "226 000",
                    "Capitale héroïque du Kénédougou, célèbre pour les ruines du Tata défensif et les chutes de Farako.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/2ba6d709-eafa-4d39-929d-61fbcf2d2bee_Le-Mamelon-de-Sikasso-Sindebou-_StudioTamani.jpg",
                    11.3176,
                    -5.6665,
                    sikassoRegion
            );

            // 5. SÉGOU (4ème région administrative)
            RegionModel segouRegion = initRegion(
                    "Région de Ségou",
                    "Ségou",
                    2336000L,
                    "64 821 km²",
                    "La prestigieuse Cité des Balanzans et berceau du puissant royaume Bambara fondé par Biton Coulibaly.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/046acd3d-6899-4076-b662-2502cbbda963_Segou__Mali___.jpeg"
            );
            initVille(
                    "Ségou",
                    segouRegion.getNomRegion(),
                    "134 000",
                    "Cité royale le long du fleuve Niger réputée pour ses poteries traditionnelles de Kalabougou et son festival sur le Niger.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/046acd3d-6899-4076-b662-2502cbbda963_Segou__Mali___.jpeg",
                    13.4317,
                    -6.2157,
                    segouRegion
            );

            // 6. MOPTI (5ème région administrative)
            RegionModel moptiRegion = initRegion(
                    "Région de Mopti",
                    "Mopti",
                    2037000L,
                    "79 017 km²",
                    "La Venise malienne au confluent du Niger et du Bani, carrefour commercial et porte d'accès vers Djenné.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/d35072d2-51f7-4dc5-b906-f1199b4d2fa7_Mopti__Mali.jpeg"
            );
            initVille(
                    "Mopti",
                    moptiRegion.getNomRegion(),
                    "114 000",
                    "Port fluvial majeur du delta central avec ses chantiers de fabrication artisanale de grandes pinasses.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/d35072d2-51f7-4dc5-b906-f1199b4d2fa7_Mopti__Mali.jpeg",
                    14.4958,
                    -4.1873,
                    moptiRegion
            );
            initVille(
                    "Djenné",
                    moptiRegion.getNomRegion(),
                    "33 000",
                    "Joyau architectural universel en banco, abritant la plus grande mosquée en terre crue au monde inscrite à l'UNESCO.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/0d45c4b9-ddcb-4bfe-89fe-226739aacf74_Djenn_.jpg",
                    13.9056,
                    -4.5533,
                    moptiRegion
            );

            // 7. TOMBOUCTOU (6ème région administrative)
            RegionModel tombouctouRegion = initRegion(
                    "Région de Tombouctou",
                    "Tombouctou",
                    681000L,
                    "496 446 km²",
                    "La mystique Cité des 333 saints aux portes du Sahara, phare du savoir islamique médiéval et des manuscrits anciens.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/3984e765-781e-4966-9c04-90b19fd64403_deuxieme_imagehome.jpeg"
            );
            initVille(
                    "Tombouctou",
                    tombouctouRegion.getNomRegion(),
                    "54 000",
                    "Cité millénaire classée à l'UNESCO abritant les célèbres mosquées de Djingareyber, Sankoré et Sidi Yahia.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/3984e765-781e-4966-9c04-90b19fd64403_deuxieme_imagehome.jpeg",
                    16.7666,
                    -3.0026,
                    tombouctouRegion
            );

            // 8. GAO (7ème région administrative)
            RegionModel gaoRegion = initRegion(
                    "Région de Gao",
                    "Gao",
                    544000L,
                    "170 572 km²",
                    "L'illustre capitale de l'Empire Songhaï au XVe et XVIe siècle sous l'Askia Mohammed, abritant le Tombeau des Askia (UNESCO).",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/93c7822b-966e-446c-aab2-04396325e942_deuxieme_imagehome.jpeg"
            );
            initVille(
                    "Gao",
                    gaoRegion.getNomRegion(),
                    "86 000",
                    "Cité historique sahélienne au bord du Niger, réputée pour sa dune rose de Koïma et ses traditions songhaï.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/93c7822b-966e-446c-aab2-04396325e942_deuxieme_imagehome.jpeg",
                    16.2717,
                    -0.0447,
                    gaoRegion
            );

            // 9. KIDAL (8ème région administrative)
            RegionModel kidalRegion = initRegion(
                    "Région de Kidal",
                    "Kidal",
                    67000L,
                    "151 450 km²",
                    "Région montagnarde et désertique de l'Adrar des Ifoghas, haut lieu de la culture nomade touarègue et de l'artisanat du cuir et de l'argent.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/0fbffaa1-70b6-47aa-8120-0e4f44226820_Africa_-_Mali_-_Banani.jpeg"
            );
            initVille(
                    "Kidal",
                    kidalRegion.getNomRegion(),
                    "25 000",
                    "Centre urbain niché dans les vallées de l'Adrar des Ifoghas, carrefour des traditions orales et poétiques du désert.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/0fbffaa1-70b6-47aa-8120-0e4f44226820_Africa_-_Mali_-_Banani.jpeg",
                    18.4411,
                    1.4078,
                    kidalRegion
            );

            // 10. TAOUDÉNIT (9ème région administrative)
            RegionModel taoudenitRegion = initRegion(
                    "Région de Taoudénit",
                    "Taoudéni",
                    18000L,
                    "323 326 km²",
                    "Vaste étendue désertique du grand nord malien, célèbre pour ses mines de sel gemme millénaires et les caravanes d'azalaï.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/93c7822b-966e-446c-aab2-04396325e942_deuxieme_imagehome.jpeg"
            );
            initVille(
                    "Taoudéni",
                    taoudenitRegion.getNomRegion(),
                    "3 000",
                    "Bourgade saharienne légendaire, cœur historique de l'extraction traditionnelle des barres de sel gemme.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/93c7822b-966e-446c-aab2-04396325e942_deuxieme_imagehome.jpeg",
                    22.6781,
                    -3.9842,
                    taoudenitRegion
            );

            // 11. MÉNAKA (10ème région administrative)
            RegionModel menakaRegion = initRegion(
                    "Région de Ménaka",
                    "Ménaka",
                    54000L,
                    "81 040 km²",
                    "Région pastorale de l'extrême est du Mali frontalière du Niger, traversée par la vallée fossile de l'Azawagh.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/0fbffaa1-70b6-47aa-8120-0e4f44226820_Africa_-_Mali_-_Banani.jpeg"
            );
            initVille(
                    "Ménaka",
                    menakaRegion.getNomRegion(),
                    "20 000",
                    "Carrefour pastoral sahélien renommé pour ses foires d'élevage et son artisanat d'art nomade.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/slides/0fbffaa1-70b6-47aa-8120-0e4f44226820_Africa_-_Mali_-_Banani.jpeg",
                    15.9182,
                    2.4022,
                    menakaRegion
            );

            // 12. KOUTIALA (Région administrative - Capitale du coton)
            RegionModel koutialaRegion = initRegion(
                    "Région de Koutiala",
                    "Koutiala",
                    575000L,
                    "18 000 km²",
                    "Capitale de l’or blanc (coton) et pôle agro-industriel majeur du sud malien, fief de la culture Minianka.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/2e0e6bf5-0023-494b-9157-f24aa881512b_koutiala.jpeg"
            );
            initVille(
                    "Koutiala",
                    koutialaRegion.getNomRegion(),
                    "140 000",
                    "Capitale agro-pastorale et centre cotonnier névralgique de la CMDT.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/2e0e6bf5-0023-494b-9157-f24aa881512b_koutiala.jpeg",
                    12.3917,
                    -5.4642,
                    koutialaRegion
            );

            // 13. SAN (Région administrative - Cité du Sanké)
            RegionModel sanRegion = initRegion(
                    "Région de San",
                    "San",
                    330000L,
                    "7 262 km²",
                    "Carrefour culturel du Bani célèbre pour sa pêche collective du Sanké Mô (UNESCO) et son église néo-soudanaise en banco.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/4aac1e77-f062-4bf8-8d8f-9b8ec39e1df0_san_sanke-mon.jpg"
            );
            initVille(
                    "San",
                    sanRegion.getNomRegion(),
                    "68 000",
                    "Cité carrefour sur le fleuve Bani renommée pour ses traditions Bobo et son architecture soudanaise.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/4aac1e77-f062-4bf8-8d8f-9b8ec39e1df0_san_sanke-mon.jpg",
                    13.3034,
                    -4.8956,
                    sanRegion
            );

            // 14. KÉNIÉBA (Cercle historique du Bambouk)
            RegionModel keniebaRegion = initRegion(
                    "Cercle de Kéniéba",
                    "Kéniéba",
                    215000L,
                    "16 800 km²",
                    "Cœur historique du Bambouk et capitale de l’or traditionnel, adossée aux falaises de grès du Tambaoura.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/20c147b6-b759-4b58-b974-3c4611f2b1cb_kenieba.jpeg"
            );
            initVille(
                    "Kéniéba",
                    keniebaRegion.getNomRegion(),
                    "39 000",
                    "Cité aurifère ancestrale bordée par les spectaculaires falaises de grès du Tambaoura.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/20c147b6-b759-4b58-b974-3c4611f2b1cb_kenieba.jpeg",
                    12.8530,
                    -11.2260,
                    keniebaRegion
            );

            // 15. KITA (Région administrative - Cité des griots)
            RegionModel kitaRegion = initRegion(
                    "Région de Kita",
                    "Kita",
                    438000L,
                    "35 250 km²",
                    "Capitale de l’arachide, haut lieu spirituel et berceau incontesté des griots mandingues et de la kora.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/8a2b87e9-7ef4-4298-8698-ac341176f1ce_kita.jpeg"
            );
            initVille(
                    "Kita",
                    kitaRegion.getNomRegion(),
                    "55 000",
                    "Cité sacrée au pied du Kita Kourou, sanctuaire marial et carrefour des maîtres de la parole.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/8a2b87e9-7ef4-4298-8698-ac341176f1ce_kita.jpeg",
                    13.0349,
                    -9.4895,
                    kitaRegion
            );

            // 16. BANDIAGARA (Région administrative - Pays Dogon)
            RegionModel bandiagaraRegion = initRegion(
                    "Région de Bandiagara",
                    "Bandiagara",
                    320000L,
                    "10 520 km²",
                    "Capitale du Pays Dogon et des falaises inscrites au patrimoine mondial de l’UNESCO, patrie d’Amadou Hampâté Bâ.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/092d8b10-62e7-4254-9e2e-3b97571beb46_badiangara.jpeg"
            );
            initVille(
                    "Bandiagara",
                    bandiagaraRegion.getNomRegion(),
                    "45 000",
                    "Porte d'accès au plateau et aux falaises Dogons inscrites au patrimoine mondial de l’UNESCO.",
                    "https://dzhqwkpwaljqsjwoqvso.supabase.co/storage/v1/object/public/maliexplorer-media/villes/092d8b10-62e7-4254-9e2e-3b97571beb46_badiangara.jpeg",
                    14.3501,
                    -3.6104,
                    bandiagaraRegion
            );

            log.info("Synchronisation des 16 régions et villes du Mali terminée avec succès.");
        } catch (Exception e) {
            log.warn("Avertissement lors de la synchronisation des destinations : {}", e.getMessage());
        }
    }

    private RegionModel initRegion(String nomRegion, String chefLieu, Long nbreHbt, String superficie, String description, String imageUrl) {
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
                    if ((existing.getDescription() == null || existing.getDescription().isBlank()) && description != null) {
                        existing.setDescription(description);
                        updated = true;
                    }
                    if ((existing.getImageUrl() == null || existing.getImageUrl().isBlank() || existing.getImageUrl().startsWith("assets/")) && imageUrl != null) {
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
                            .description(description)
                            .imageUrl(imageUrl)
                            .build();
                    return regionRepository.save(region);
                });
    }

    private void initVille(String nomVille, String regionName, String nbreHbt, String description, String imageUrl, Double lat, Double lng, RegionModel regionParent) {
        villeRepository.findByNomVilleIgnoreCase(nomVille)
                .ifPresentOrElse(existing -> {
                    boolean updated = false;
                    if (existing.getLatitude() == null && lat != null) {
                        existing.setLatitude(lat);
                        existing.setLongitude(lng);
                        existing.setCordonnees(lat + ", " + lng);
                        updated = true;
                    }
                    if (existing.getRegionParent() == null && regionParent != null) {
                        existing.setRegionParent(regionParent);
                        updated = true;
                    }
                    if ((existing.getImageUrl() == null || existing.getImageUrl().isBlank() || existing.getImageUrl().startsWith("assets/")) && imageUrl != null) {
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
