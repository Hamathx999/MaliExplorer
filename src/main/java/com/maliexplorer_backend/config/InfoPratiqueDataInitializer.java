package com.maliexplorer_backend.config;

import com.maliexplorer_backend.model.InfoPratiqueModel;
import com.maliexplorer_backend.repository.InfoPratiqueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initialiseur de données pour les 5 catégories d'informations pratiques pour voyager au Mali.
 */
@Slf4j
@Component
@Order(16)
@RequiredArgsConstructor
public class InfoPratiqueDataInitializer implements CommandLineRunner {

    private final InfoPratiqueRepository infoPratiqueRepository;

    @Override
    public void run(String... args) {
        if (infoPratiqueRepository.count() > 0) {
            log.info("Les informations pratiques sont déjà initialisées ({} trouvées).", infoPratiqueRepository.count());
            return;
        }

        log.info("Initialisation des 5 rubriques d'informations pratiques du voyageur...");

        try {
            List<InfoPratiqueModel> infos = List.of(
                    InfoPratiqueModel.builder()
                            .titre("1. Visas & Formalités d'Entrée")
                            .categorie("VISAS")
                            .badge("Obligatoire")
                            .badgeColor("#C53030")
                            .icon("badge")
                            .description("Formalités de voyage et exigences douanières à l'entrée en République du Mali.")
                            .conseils("• Passeport : Valide au moins 6 mois après la date de retour prévue.\n" +
                                    "• Visa d'entrée : Requis pour la plupart des ressortissants hors CEDEAO. Disponible auprès des consulats et ambassades du Mali ou via le portail officiel de visa biométrique.\n" +
                                    "• Ressortissants CEDEAO : Entrée sans visa sur présentation d'un passeport ou d'une carte d'identité biométrique CEDEAO en cours de validité.\n" +
                                    "• Enregistrement : Déclaration auprès des services de police/gendarmerie ou de l'hôtel à l'arrivée en province.")
                            .ordreAffichage(1)
                            .publie(true)
                            .build(),

                    InfoPratiqueModel.builder()
                            .titre("2. Santé & Vaccinations")
                            .categorie("SANTE")
                            .badge("Exigé à l'arrivée")
                            .badgeColor("#DD6B20")
                            .icon("medical_services")
                            .description("Recommandations médicales, vaccins obligatoires et prévention sanitaire.")
                            .conseils("• Fièvre Jaune : Vaccination strictement obligatoire. Le carnet international de vaccination (jaune) est systématiquement contrôlé à l'aéroport international Président Modibo Keïta de Bamako-Sénou.\n" +
                                    "• Paludisme : Traitement préventif fortement recommandé (chimioprophylaxie) toute l'année, particulièrement durant et après la saison des pluies. Prévoir répulsifs cutanés et moustiquaires.\n" +
                                    "• Vaccins recommandés : Hépatites A et B, Méningite A+C+Y+W135, Fièvre typhoïde, Tétanos.\n" +
                                    "• Eau & Alimentation : Boire de l'eau minérale capsulée ou préalablement filtrée/bouillie. Éviter les glaçons non sécurisés.")
                            .ordreAffichage(2)
                            .publie(true)
                            .build(),

                    InfoPratiqueModel.builder()
                            .titre("3. Devises, Monnaie & Paiements")
                            .categorie("DEVISES")
                            .badge("FCFA (XOF)")
                            .badgeColor("#2E7D32")
                            .icon("payments")
                            .description("Monnaie officielle, change, solutions bancaires et paiements mobiles.")
                            .conseils("• Monnaie officielle : Franc CFA d'Afrique de l'Ouest (XOF). Parité fixe avec l'Euro (1 EUR = 655,957 XOF).\n" +
                                    "• Mobile Money : Orange Money et Moov Money sont omniprésents et acceptés par la quasi-totalité des commerces, taxis et artisans.\n" +
                                    "• Cartes bancaires : Les cartes Visa et Mastercard sont acceptées dans les grands hôtels, supermarchés et aux distributeurs automatiques (GAB) à Bamako et grandes villes.\n" +
                                    "• Espèces : Indispensables pour les marchés artisanaux, transports fluviaux et zones rurales.")
                            .ordreAffichage(3)
                            .publie(true)
                            .build(),

                    InfoPratiqueModel.builder()
                            .titre("4. Climat & Meilleure Période")
                            .categorie("CLIMAT")
                            .badge("Saison fraîche : Nov - Fév")
                            .badgeColor("#3182CE")
                            .icon("wb_sunny")
                            .description("Repères météo et périodes optimales pour explorer les différentes régions du Mali.")
                            .conseils("• Saison fraîche et sèche (Novembre à Février) : Températures idéales (25°C - 32°C le jour, 15°C - 18°C la nuit). Période reine pour visiter les villes historiques, les festivals et naviguer sur le fleuve Niger.\n" +
                                    "• Saison chaude (Mars à Mai) : Chaleur intense (38°C à 44°C). Vent sec d'Harmattan.\n" +
                                    "• Saison des pluies / Hivernage (Juin à Octobre) : Végétation luxuriante, averses tropicales régulières, fleuve à son niveau maximal.")
                            .ordreAffichage(4)
                            .publie(true)
                            .build(),

                    InfoPratiqueModel.builder()
                            .titre("5. Numéros d'Urgence au Mali")
                            .categorie("URGENCES")
                            .badge("24h/24 & 7j/7")
                            .badgeColor("#C53030")
                            .icon("contact_phone")
                            .description("Contacts d'urgence des forces de sécurité et secours médicaux disponibles en permanence.")
                            .coordonnees("Police Secours : 17 / 80 00 11 15\n" +
                                    "Sapeurs-Pompiers (Incendie & Secours) : 18 / 80 00 12 01\n" +
                                    "Gendarmerie Nationale : 80 00 11 14\n" +
                                    "SAMU / Urgences Médicales Bamako : 15 / 20 22 27 77")
                            .conseils("En cas de sinistre ou d'incident sur le territoire national, composez sans attendre les numéros d'urgence gratuits ci-dessus ou adressez-vous au poste de police / brigade de gendarmerie la plus proche.")
                            .ordreAffichage(5)
                            .publie(true)
                            .build()
            );

            infoPratiqueRepository.saveAll(infos);
            log.info("Succès : Les 5 rubriques d'informations pratiques ont été insérées en base de données.");
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation des informations pratiques : {}", e.getMessage(), e);
        }
    }
}
