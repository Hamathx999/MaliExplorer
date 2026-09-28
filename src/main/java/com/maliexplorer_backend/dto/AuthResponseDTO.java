package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.RoleModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'authentification contenant les informations utilisateur et le rôle")
public class AuthResponseDTO {

    private int idUsers;
    private String firebaseUid;
    private String prenom;
    private String nom;
    private String email;
    private String photoUrl;
    private String adresse;
    private RoleModel role;
    private String message;
}
