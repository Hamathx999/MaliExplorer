package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.StatutModeration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * DTO unifié pour l'Espace Investisseur B2B et la consultation des opportunités de partenariat.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportuniteResponseDTO {

    private int idUsers;
    private String nomComplet;
    private String email;
    private String adresse;
    private String photoUrl;
    private RoleModel role;
    private String typePartenaire; // "ARTISAN", "PROMOTEUR", "GUIDE"
    private String specialiteOuOrganisation; // Métier ou Organisation

    private boolean recherchePartenariat;
    private String titreProjet;
    private String besoinPartenariat;
    private StatutModeration statutModeration;
    private String motifRejet;
    private Date dateCreation;
}
