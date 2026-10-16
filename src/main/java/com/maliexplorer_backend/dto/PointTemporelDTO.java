package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant un point d'activité temporel pour le graphique SVG dynamique du dashboard.
 * Fournit à la fois le format condensé (label, valeur, date) et les métriques détaillées.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointTemporelDTO {

    /** Libellé court affiché sur l'axe X (ex : "Lun", "S1", "Jan") */
    private String label;

    /** Valeur globale consolidée de l'activité sur la période */
    private long valeur;

    /** Date de référence au format ISO (ex : "2026-10-10") */
    private String date;

    /** Nombre de visiteurs ou utilisateurs distincts actifs durant cette période */
    private long visiteurs;

    /** Nombre total d'interactions ou de consultations durant cette période */
    private long pagesVues;

    /** Nombre de nouvelles inscriptions d'utilisateurs sur cette période */
    private long inscriptions;

    /** Nombre d'actions d'engagement (participations aux quiz, ajouts aux favoris, soumissions) */
    private long activites;
}
