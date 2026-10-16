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

    public Boolean getRecherchePartenariat() {
        return recherchePartenariat;
    }

    public void setRecherchePartenariat(Boolean recherchePartenariat) {
        this.recherchePartenariat = recherchePartenariat;
    }

    public String getTitreProjet() {
        return titreProjet;
    }

    public void setTitreProjet(String titreProjet) {
        this.titreProjet = titreProjet;
    }

    public String getBesoinPartenariat() {
        return besoinPartenariat;
    }

    public void setBesoinPartenariat(String besoinPartenariat) {
        this.besoinPartenariat = besoinPartenariat;
    }
}
