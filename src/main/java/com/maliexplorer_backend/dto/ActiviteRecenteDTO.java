package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant une entrée chronologique dans le fil d'actualité récente du tableau de bord.
 * Alimenté par les actions réelles survenues sur la plateforme (inscriptions, quiz, événements, partenariats).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiviteRecenteDTO {

    /** Identifiant unique de l'action */
    private Object id;

    /** Type d'action réalisée : "INSCRIPTION", "EVENEMENT", "QUIZ", "PARTENARIAT", "ARTICLE" */
    private String type;

    /** Titre explicite de l'activité survenue */
    private String titre;

    /** Mention temporelle conviviale (ex : "Il y a 10 min", "Aujourd'hui à 14:20", "14/10/2026") */
    private String date;

    /** Horodatage précis au format ISO 8601 pour le tri chronologique */
    private String dateIso;

    /** Nom complet ou pseudonyme de l'utilisateur ou entité ayant effectué l'action */
    private String auteur;

    /** Description détaillée ou contexte de l'opération */
    private String description;

    /** Nom de l'icône vectorielle pour l'affichage frontend (ex : "user-plus", "calendar", "award") */
    private String icone;

    /** Code couleur hexadécimal associé au type d'activité */
    private String couleur;
}
