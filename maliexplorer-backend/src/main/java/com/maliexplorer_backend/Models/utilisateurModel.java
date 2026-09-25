package com.maliexplorer_backend.Models;

import jakarta.validation.constraints.Email;

public abstract class utilisateurModel {
    private int idUsers;
    private String prenom;
    private String nom;
    private String email;
    private String motDePasse;
    private String adresse;
    private String photoUrl;
    private Date dateCreation;
}
