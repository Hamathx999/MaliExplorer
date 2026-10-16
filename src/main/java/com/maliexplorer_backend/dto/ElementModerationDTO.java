package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant une fiche ou demande en attente de modération pour le tableau « À valider ».
 * Permet à l'administrateur d'accéder directement au formulaire d'approbation ou de rejet.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElementModerationDTO {

    /** Identifiant de l'élément (numérique ou clé fonctionnelle) */
    private Object id;

    /** Type d'entité en attente (ex : "Partenaire", "Guide", "Événement") */
    private String type;

    /** Titre du projet, de l'événement ou nom de l'organisation */
    private String titre;

    /** Date de soumission formatée pour affichage utilisateur (ex : "14/10/2026") */
    private String date;

    /** Statut actuel du processus de modération (ex : "EN_ATTENTE") */
    private String statut;

    /** Route de navigation Angular vers laquelle rediriger l'administrateur (ex : "/demandes-partenariat") */
    private String urlAction;

    /** Nom complet du demandeur, porteur de projet ou organisateur */
    private String demandeur;

    /** Détails succincts ou extrait de la description */
    private String details;

    /** URL de l'image de prévisualisation ou du logo */
    private String imageUrl;
}
