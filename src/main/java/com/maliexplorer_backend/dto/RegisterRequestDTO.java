package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.RoleModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'inscription pour un nouvel utilisateur")
public class RegisterRequestDTO {

    @Schema(description = "Firebase ID Token (optionnel, pour lier directement le compte Firebase)", example = "eyJhbGciOiJSUzI1NiIs...")
    private String idToken;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Schema(example = "Amadou")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Schema(example = "Diallo")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Schema(example = "amadou.diallo@example.ml")
    private String email;

    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Schema(example = "Secret123!", description = "Optionnel si inscription via Firebase Google/OAuth")
    private String motDePasse;

    @Schema(example = "Bamako, Commune IV")
    private String adresse;

    @Schema(example = "https://example.com/avatar.jpg")
    private String photoUrl;

    @Schema(description = "Rôle de l'utilisateur (par défaut: touriste)", example = "touriste")
    private RoleModel role;
}
