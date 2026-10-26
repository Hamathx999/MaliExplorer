package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.StatutModeration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'authentification contenant les informations utilisateur, le rôle et le statut de modération")
public class AuthResponseDTO {

    private int idUsers;
    private String firebaseUid;
    private String prenom;
    private String nom;
    private String email;
    private String photoUrl;
    private String adresse;
    private String telephone;
    private String pieceIdentite;
    private String nomOrganisation;
    private String statut;
    private StatutModeration statutModeration;
    private RoleModel role;
    private String message;
}
