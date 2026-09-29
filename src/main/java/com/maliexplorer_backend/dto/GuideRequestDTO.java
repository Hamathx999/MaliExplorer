package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
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
public class GuideRequestDTO {

    @NotBlank(message = "Le prénom est obligatoire !")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères !")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire !")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères !")
    private String nom;

    @NotBlank(message = "L'email est obligatoire !")
    @Email(message = "Le format de l'email est invalide !")
    private String email;

    private String motDePasse;
    private String adresse;
    private String photoUrl;

    @Min(value = 0, message = "L'expérience ne peut pas être négative !")
    private int experience;

    private String description;

    @NotBlank(message = "La langue parlée est obligatoire !")
    private String langue;

    private String pieceIdentite;
    private Integer idAdministrateur;
}

