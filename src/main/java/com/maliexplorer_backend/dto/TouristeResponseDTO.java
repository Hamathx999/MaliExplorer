package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.RoleModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristeResponseDTO {

    private int idUsers;
    private Long idTouriste;
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String photoUrl;
    private Date dateCreation;
    private RoleModel role;
    private int points;

    private int nombreArticlesLus;
    private int nombreLieuxVisites;
    private int nombreVillesVisitees;
    private int nombreEthniesVues;
    private int nombrePlatsVus;
    private int nombreQuizJoues;

    private List<Long> idsLieuxVisites;
    private List<Long> idsArticlesLus;
}
