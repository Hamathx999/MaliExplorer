package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartenariatSubmissionDTO {

    @NotNull(message = "L'état de recherche de partenariat est obligatoire")
    private Boolean recherchePartenariat;

    private String titreProjet;
    private String besoinPartenariat;
}
