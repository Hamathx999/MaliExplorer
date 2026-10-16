package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO représentant les 5 indicateurs clés de performance (KPIs) supérieurs du tableau de bord d'administration.
 * Tous les compteurs sont issus de données réelles calculées en base de données.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpisDTO {

    /** Total des contenus culturels et patrimoniaux publiés (somme des 8 entités) */
    private long contenusPublies;

    /** Tendance ou variation calculée pour les contenus publiés (ex : "+12%") */
    private String contenusPubliesDelta;

    /** Nombre total d'éléments en attente de modération administrative (promoteurs, guides, événements) */
    private long enAttenteValidation;

    /** Niveau d'urgence ou étiquette pour les validations en attente (ex : "Prioritaire", "À traiter") */
    private String enAttenteValidationDelta;

    /** Nombre total d'utilisateurs actifs enregistrés sur la plateforme */
    private long utilisateursActifs;

    /** Tendance d'acquisition des utilisateurs (ex : "+8%") */
    private String utilisateursActifsDelta;

    /** Nombre total de comptes partenaires actifs (promoteurs, guides, investisseurs) */
    private long partenaires;

    /** Mention d'évolution des partenariats (ex : "+3 ce mois") */
    private String partenairesDelta;

    /** Nombre d'événements futurs programmés (dateDebut >= aujourd'hui) */
    private long evenementsAVenir;

    /** Mention ou statut pour les événements futurs (ex : "À venir") */
    private String evenementsAVenirDelta;

    /** Décomposition détaillée des contenus par catégorie d'entité */
    private Map<String, Long> detailsContenus;

    /** Décomposition détaillée des partenaires par type de rôle */
    private Map<String, Long> detailsPartenaires;
}
