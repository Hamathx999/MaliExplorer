package com.maliexplorer_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de connexion avec token Firebase ou identifiants")
public class LoginRequestDTO {

    @NotBlank(message = "Le token Firebase ID est obligatoire")
    @Schema(description = "Firebase ID Token généré par le SDK client Firebase après authentification", example = "eyJhbGciOiJSUzI1NiIs...")
    private String idToken;
}
