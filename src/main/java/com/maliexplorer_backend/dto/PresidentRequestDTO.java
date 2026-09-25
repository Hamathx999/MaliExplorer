package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresidentRequestDTO {

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    private LocalDate dateNaissance;

    private LocalDate dateDeces;

    @Size(max = 100, message = "La période du mandat ne peut pas dépasser 100 caractères")
    private String periodeMandat;

    private String biographie;

    private String faitsMarquants;

    @Size(max = 500, message = "L'URL de la photo ne peut pas dépasser 500 caractères")
    private String photoUrl;

    private Long idUsers;
}
