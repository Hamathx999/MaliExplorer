package com.maliexplorer_backend.model;

import java.sql.Date;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class utilisateurModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsers;

    @NotBlank(message = "Le prénom est obligatoire !")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères !")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire !")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères !")
    private String nom;

    @NotBlank(message = "L'email est obligatoire !")
    @Email(message = "Le format de l'email est invalide !")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire !")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères !")
    private String motDePasse;

    private String adresse;

    private String photoUrl;

    private Date dateCreation;

    @Enumerated(EnumType.STRING)
    private Role role;
}
