package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.StatutModeration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO de réponse pour le profil utilisateur enrichi avec statut et modération")
public class UtilisateurResponseDTO {

    private int idUsers;
    private String firebaseUid;
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String telephone;
    private String pieceIdentite;
    private String nomOrganisation;
    private String photoUrl;
    private Date dateCreation;
    private RoleModel role;
    private int points;
    private String statut;
    private StatutModeration statutModeration;
}
