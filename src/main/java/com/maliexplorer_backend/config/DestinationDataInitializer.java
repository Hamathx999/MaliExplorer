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

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class DestinationDataInitializer implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final VilleRepository villeRepository;

    @Override
    public void run(String... args) {
        log.info("Vérification et synchronisation des régions et destinations clés du Mali...");

        try {
            // 1. KAYES
            RegionModel kayesRegion = initRegion(
                    "Région de Kayes",
                    "Kayes",
                    1996000L,
                    "119 743 km²",
                    "Première région administrative du Mali, berceau des chutes du Félou et du Fort de Médine.",
                    "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=800&q=80"
            );
            initVille(
                    "Kayes",
                    kayesRegion.getNom(),
                    "130 000",
                    "Carrefour commercial frontalier avec le Sénégal et la Mauritanie, sur les rives du fleuve Sénégal.",
                    "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=800&q=80",
                    14.4469,
                    -11.4445,
                    kayesRegion
            );

            // 2. KOULIKORO
            RegionModel koulikoroRegion = initRegion(
                    "Région de Koulikoro",
                    "Koulikoro",
                    2418000L,
                    "90 120 km²",
                    "Deuxième région administrative du Mali, terminus du fleuve Niger et terre des monts Mandingues.",
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=80"
            );
            initVille(
                    "Koulikoro",
                    koulikoroRegion.getNom(),
                    "43 000",
                    "Port terminal navigable sur le Niger et site légendaire de la disparition de Soumaoro Kanté.",
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=80",
                    12.8627,
                    -7.5599,
                    koulikoroRegion
            );

            // 3. KOUTIALA
            RegionModel koutialaRegion = initRegion(
                    "Région de Koutiala",
                    "Koutiala",
                    575000L,
                    "18 000 km²",
                    "Capitale de l’or blanc (coton) et pôle agro-industriel majeur du sud malien, fief de la culture Minianka.",
                    "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?auto=format&fit=crop&w=800&q=80"
            );
            initVille(
                    "Koutiala",
                    koutialaRegion.getNom(),
                    "140 000",
                    "Capitale agro-pastorale et centre cotonnier névralgique de la CMDT.",
                    "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?auto=format&fit=crop&w=800&q=80",
                    12.3917,
                    -5.4642,
                    koutialaRegion
            );

            // 4. SAN
            RegionModel sanRegion = initRegion(
                    "Région de San",
                    "San",
                    330000L,
                    "7 262 km²",
                    "Carrefour culturel du Bani célèbre pour sa pêche collective du Sanké Mô (UNESCO) et son église en banco.",
                    "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80"
            );
            initVille(
                    "San",
                    sanRegion.getNom(),
                    "68 000",
                    "Cité carrefour sur le fleuve Bani renommée pour ses traditions Bobo et son architecture soudanaise.",
                    "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80",
                    13.3034,
                    -4.8956,
                    sanRegion
            );

            // 5. KÉNIÉBA
            RegionModel keniebaRegion = initRegion(
                    "Cercle de Kéniéba",
                    "Kéniéba",
                    215000L,
                    "16 800 km²",
                    "Cœur historique du Bambouk et capitale de l’or traditionnel, adossée aux falaises du Tambaoura.",
                    "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=800&q=80"
            );
            initVille(
                    "Kéniéba",
                    keniebaRegion.getNom(),
                    "39 000",
                    "Cité aurifère ancestrale bordée par les spectaculaires falaises de grès du Tambaoura.",
                    "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=800&q=80",
                    12.8530,
                    -11.2260,
                    keniebaRegion
            );

            // 6. KITA
            RegionModel kitaRegion = initRegion(
                    "Région de Kita",
                    "Kita",
                    438000L,
                    "35 250 km²",
                    "Capitale de l’arachide, haut lieu spirituel et berceau incontesté des griots mandingues et de la kora.",
                    "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=800&q=80"
            );
            initVille(
                    "Kita",
                    kitaRegion.getNom(),
                    "55 000",
                    "Cité sacrée au pied du Kita Kourou, sanctuaire marial et carrefour des maîtres de la parole.",
                    "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=800&q=80",
                    13.0349,
                    -9.4895,
                    kitaRegion
            );

            // 7. BANDIAGARA
            RegionModel bandiagaraRegion = initRegion(
                    "Région de Bandiagara",
                    "Bandiagara",
                    320000L,
                    "10 520 km²",
                    "Capitale du Pays Dogon et des falaises inscrites au patrimoine mondial de l’UNESCO, patrie d’Amadou Hampâté Bâ.",
                    "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80"
            );
            initVille(
                    "Bandiagara",
                    bandiagaraRegion.getNom(),
                    "45 000",
                    "Porte d'accès au plateau et aux falaises Dogons inscrites au patrimoine mondial de l’UNESCO.",
                    "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80",
                    14.3501,
                    -3.6104,
                    bandiagaraRegion
            );

            log.info("Synchronisation des 7 nouvelles destinations terminée avec succès.");
        } catch (Exception e) {
            log.warn("Avertissement lors de la synchronisation des destinations : {}", e.getMessage());
        }
    }

    private RegionModel initRegion(String nomRegion, String chefLieu, Long nbreHbt, String superficie, String description, String imageUrl) {
        return regionRepository.findByNomRegionIgnoreCase(nomRegion)
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
        if (villeRepository.findByNomVilleIgnoreCase(nomVille).isEmpty()) {
            log.info("Création de la ville : {}", nomVille);
            VilleModel ville = VilleModel.builder()
                    .nomVille(nomVille)
                    .region(regionName)
                    .nbreHbt(nbreHbt)
                    .description(description)
                    .imageUrl(imageUrl)
                    .latitude(lat)
                    .longitude(lng)
                    .cordonnees(lat + ", " + lng)
                    .regionParent(regionParent)
                    .build();
            villeRepository.save(ville);
        }
    }
}

