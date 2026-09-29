package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.RoleModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtisanResponseDTO {

    private int idUsers;
    private String firebaseUid;
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String photoUrl;
    private Date dateCreation;
    private RoleModel role;
    private String typeArtisanat;
}

