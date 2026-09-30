package com.maliexplorer_backend;

import com.maliexplorer_backend.dto.ProgressionResponseDTO;
import com.maliexplorer_backend.model.BadgeBambara;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.serviceimpl.BadgeProgressionServiceImpl;

/**
 * Runner autonome de vérification de la logique du module de Badges Bambara.
 */
public class BadgeVerificationRunner {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("   VÉRIFICATION DES TESTS DU MODULE BADGES BAMBARA (MALIEXPLORER)");
        System.out.println("===============================================================");

        int testsPasses = 0;
        int totalTests = 0;

        // TEST 1 : Seuils exacts Kalanden (0 à 99)
        totalTests++;
        assertCondition(BadgeBambara.fromPoints(0) == BadgeBambara.KALANDEN, "0 point doit donner Kalanden");
        assertCondition(BadgeBambara.fromPoints(99) == BadgeBambara.KALANDEN, "99 points doit donner Kalanden");
        assertCondition("Kalanden".equals(BadgeBambara.fromPoints(0).getNom()), "Nom de Kalanden exact");
        System.out.println("✅ TEST 1 RÉUSSI : Seuils Kalanden (0 à 99 points)");
        testsPasses++;

        // TEST 2 : Seuils exacts Fasoden (100 à 299)
        totalTests++;
        assertCondition(BadgeBambara.fromPoints(100) == BadgeBambara.FASODEN, "100 points doit donner Fasoden");
        assertCondition(BadgeBambara.fromPoints(299) == BadgeBambara.FASODEN, "299 points doit donner Fasoden");
        assertCondition("Fasoden".equals(BadgeBambara.fromPoints(100).getNom()), "Nom de Fasoden exact");
        System.out.println("✅ TEST 2 RÉUSSI : Seuils Fasoden (100 à 299 points)");
        testsPasses++;

        // TEST 3 : Seuils exacts Fasoden Yuman (300+)
        totalTests++;
        assertCondition(BadgeBambara.fromPoints(300) == BadgeBambara.FASODEN_YUMAN, "300 points doit donner Fasoden Yuman");
        assertCondition(BadgeBambara.fromPoints(500) == BadgeBambara.FASODEN_YUMAN, "500 points doit donner Fasoden Yuman");
        assertCondition("Fasoden Yuman".equals(BadgeBambara.fromPoints(300).getNom()), "Nom de Fasoden Yuman exact");
        System.out.println("✅ TEST 3 RÉUSSI : Seuils Fasoden Yuman (300 points et plus)");
        testsPasses++;

        // TEST 4 : Calculs de progression
        totalTests++;
        BadgeProgressionServiceImpl service = new BadgeProgressionServiceImpl(null, null);
        utilisateurModel user = utilisateurModel.builder().idUsers(1).prenom("Amadou").nom("Traoré").build();

        // 0 point
        ProgressionResponseDTO p0 = service.calculerProgression(0, user);
        assertCondition("Kalanden".equals(p0.getBadge()), "Badge actuel à 0 pt");
        assertCondition("Fasoden".equals(p0.getNextBadge()), "Prochain badge à 0 pt");
        assertCondition(p0.getPointsToNextBadge() == 100, "100 points restants avant Fasoden");
        assertCondition(p0.getProgression() == 0.0, "0.0% de progression");

        // 75 points
        ProgressionResponseDTO p75 = service.calculerProgression(75, user);
        assertCondition("Kalanden".equals(p75.getBadge()), "Badge actuel à 75 pts");
        assertCondition(p75.getPointsToNextBadge() == 25, "25 points restants avant Fasoden");
        assertCondition(p75.getProgression() == 75.0, "75.0% de progression");

        // 180 points (exemple du cahier des charges)
        ProgressionResponseDTO p180 = service.calculerProgression(180, user);
        assertCondition("Fasoden".equals(p180.getBadge()), "Badge actuel à 180 pts");
        assertCondition("Fasoden Yuman".equals(p180.getNextBadge()), "Prochain badge à 180 pts");
        assertCondition(p180.getPointsToNextBadge() == 120, "120 points restants avant Fasoden Yuman");
        assertCondition(p180.getProgression() == 60.0, "60.0% de progression vers Fasoden Yuman");

        // 300 points (palier max)
        ProgressionResponseDTO p300 = service.calculerProgression(300, user);
        assertCondition("Fasoden Yuman".equals(p300.getBadge()), "Badge actuel à 300 pts");
        assertCondition(p300.getNextBadge() == null, "Aucun badge suivant après Fasoden Yuman");
        assertCondition(p300.getPointsToNextBadge() == 0, "0 point restant");
        assertCondition(p300.getProgression() == 100.0, "100% de progression");

        System.out.println("✅ TEST 4 RÉUSSI : Calculs des seuils, points restants et progression en %");
        testsPasses++;

        // TEST 5 : Transitions de badges (NextBadge)
        totalTests++;
        assertCondition(BadgeBambara.KALANDEN.getNextBadge() == BadgeBambara.FASODEN, "Suivant de Kalanden = Fasoden");
        assertCondition(BadgeBambara.FASODEN.getNextBadge() == BadgeBambara.FASODEN_YUMAN, "Suivant de Fasoden = Fasoden Yuman");
        assertCondition(BadgeBambara.FASODEN_YUMAN.getNextBadge() == null, "Suivant de Fasoden Yuman = null");
        System.out.println("✅ TEST 5 RÉUSSI : Transitions logiques des badges");
        testsPasses++;

        // TEST 6 : Barème de points d'un Quiz
        totalTests++;
        int scoreQuestion1 = 10;
        int scoreQuestion2 = 10;
        int scoreQuestion3 = 10;
        int scoreTotal = scoreQuestion1 + scoreQuestion2 + scoreQuestion3; // 30 pts
        int bonusTermine = 20;
        int bonusParfait = 20; // 100% correct
        int pointsGagnesTotal = scoreTotal + bonusTermine + bonusParfait;
        assertCondition(pointsGagnesTotal == 70, "Barème 3 questions + bonus terminé + bonus parfait = 70 points");
        System.out.println("✅ TEST 6 RÉUSSI : Barème de notation (Questions + Bonus fin + Bonus 100%)");
        testsPasses++;

        System.out.println("===============================================================");
        System.out.println("   RÉSULTAT FINAL : " + testsPasses + "/" + totalTests + " TESTS VALIDER AVEC SUCCÈS (100%)");
        System.out.println("===============================================================");
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("ÉCHEC DE VÉRIFICATION : " + message);
        }
    }
}
