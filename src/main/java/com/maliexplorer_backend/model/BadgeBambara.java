package com.maliexplorer_backend.model;

import lombok.Getter;

/**
 * Niveaux de badges Bambara de l'application MaliExplorer.
 * Exactement 3 paliers :
 * 1. Kalanden (0 à 99 points) - Niveau débutant / élève
 * 2. Fasoden (100 à 299 points) - Niveau intermédiaire / citoyen
 * 3. Fasoden Yuman (300 points et plus) - Niveau avancé / bon citoyen
 */
@Getter
public enum BadgeBambara {

    KALANDEN("Kalanden", "Niveau débutant / élève", 0, 99),
    FASODEN("Fasoden", "Niveau intermédiaire / citoyen", 100, 299),
    FASODEN_YUMAN("Fasoden Yuman", "Niveau avancé / bon citoyen", 300, Integer.MAX_VALUE);

    private final String nom;
    private final String description;
    private final int seuilMin;
    private final int seuilMax;

    BadgeBambara(String nom, String description, int seuilMin, int seuilMax) {
        this.nom = nom;
        this.description = description;
        this.seuilMin = seuilMin;
        this.seuilMax = seuilMax;
    }

    /**
     * Détermine le badge en fonction du total de points accumulés.
     * 0 à 99 -> Kalanden
     * 100 à 299 -> Fasoden
     * 300+ -> Fasoden Yuman
     */
    public static BadgeBambara fromPoints(int points) {
        if (points >= 300) {
            return FASODEN_YUMAN;
        } else if (points >= 100) {
            return FASODEN;
        } else {
            return KALANDEN;
        }
    }

    /**
     * Retourne le badge suivant, ou null si le niveau maximal est atteint.
     */
    public BadgeBambara getNextBadge() {
        return switch (this) {
            case KALANDEN -> FASODEN;
            case FASODEN -> FASODEN_YUMAN;
            case FASODEN_YUMAN -> null;
        };
    }
}
