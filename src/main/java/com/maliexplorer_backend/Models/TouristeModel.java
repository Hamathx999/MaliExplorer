package com.maliexplorer_backend.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "touristes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TouristeModel extends utilisateurModel {

    private int points = 0;

    public TouristeModel(String prenom, String nom, String email, String motDePasse, String adresse, String photoUrl,
            int points) {
        setPrenom(prenom);
        setNom(nom);
        setEmail(email);
        setMotDePasse(motDePasse);
        setAdresse(adresse);
        setPhotoUrl(photoUrl);
        setRole(Role.touriste);
        this.points = points;
    }
}
