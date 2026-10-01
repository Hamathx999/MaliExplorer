package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.StatutModeration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoteurResponseDTO {

    private int idUsers;
    private String firebaseUid;
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String photoUrl;
    private Date dateCreation;
    private RoleModel role;
    private Long idPromoteur;
    private String nomOrganisation;
    private String pieceIdentite;
    private boolean recherchePartenariat;
    private String titreProjet;
    private String besoinPartenariat;
    private StatutModeration statutModeration;
}
