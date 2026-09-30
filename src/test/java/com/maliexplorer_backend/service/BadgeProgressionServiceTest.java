package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.ProgressionResponseDTO;
import com.maliexplorer_backend.model.BadgeBambara;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.serviceimpl.BadgeProgressionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadgeProgressionServiceTest {

    private final BadgeProgressionServiceImpl badgeProgressionService = new BadgeProgressionServiceImpl(null, null);

    @Test
    @DisplayName("Devrait attribuer le badge Kalanden pour 0 à 99 points")
    void testSeuilsKalanden() {
        assertEquals(BadgeBambara.KALANDEN, BadgeBambara.fromPoints(0));
        assertEquals(BadgeBambara.KALANDEN, BadgeBambara.fromPoints(50));
        assertEquals(BadgeBambara.KALANDEN, BadgeBambara.fromPoints(99));
        assertEquals("Kalanden", BadgeBambara.KALANDEN.getNom());
    }

    @Test
    @DisplayName("Devrait attribuer le badge Fasoden pour 100 à 299 points")
    void testSeuilsFasoden() {
        assertEquals(BadgeBambara.FASODEN, BadgeBambara.fromPoints(100));
        assertEquals(BadgeBambara.FASODEN, BadgeBambara.fromPoints(180));
        assertEquals(BadgeBambara.FASODEN, BadgeBambara.fromPoints(299));
        assertEquals("Fasoden", BadgeBambara.FASODEN.getNom());
    }

    @Test
    @DisplayName("Devrait attribuer le badge Fasoden Yuman pour 300 points et plus")
    void testSeuilsFasodenYuman() {
        assertEquals(BadgeBambara.FASODEN_YUMAN, BadgeBambara.fromPoints(300));
        assertEquals(BadgeBambara.FASODEN_YUMAN, BadgeBambara.fromPoints(450));
        assertEquals(BadgeBambara.FASODEN_YUMAN, BadgeBambara.fromPoints(1000));
        assertEquals("Fasoden Yuman", BadgeBambara.FASODEN_YUMAN.getNom());
    }

    @Test
    @DisplayName("Devrait calculer correctement la progression pour 180 points (Fasoden)")
    void testCalculProgression180Points() {
        utilisateurModel user = utilisateurModel.builder().idUsers(42).prenom("Fatoumata").nom("Diallo").email("fatou@test.ml").build();
        ProgressionResponseDTO progression = badgeProgressionService.calculerProgression(180, user);

        assertEquals("Fasoden", progression.getBadge());
        assertEquals("Fasoden Yuman", progression.getNextBadge());
        assertEquals(120, progression.getPointsToNextBadge());
        assertEquals(60.0, progression.getProgression());
        assertEquals(180, progression.getPoints());
    }

    @Test
    @DisplayName("Devrait plafonner la progression à 100% pour le badge maximal")
    void testCalculProgressionPalierMax() {
        utilisateurModel user = utilisateurModel.builder().idUsers(1).prenom("Moussa").nom("Keita").build();
        ProgressionResponseDTO progression = badgeProgressionService.calculerProgression(350, user);

        assertEquals("Fasoden Yuman", progression.getBadge());
        assertNull(progression.getNextBadge());
        assertEquals(0, progression.getPointsToNextBadge());
        assertEquals(100.0, progression.getProgression());
    }
}
