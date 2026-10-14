package com.maliexplorer_backend.model;

import java.sql.Date;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class utilisateurModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsers;

    @Column(unique = true)
    private String firebaseUid;

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


    @Builder.Default
    private String adresse = "";

<<<<<<< HEAD
    @Builder.Default
    private String telephone = "";

    @Builder.Default
    private String pieceIdentite = "";

    @Builder.Default
    private String nomOrganisation = "";

    @Builder.Default
    private String photoUrl = "";
=======
    private String telephone;

    private String photoUrl;
>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919

    private String pieceIdentite;

    private Date dateCreation;

    @Enumerated(EnumType.STRING)
    private RoleModel role;

    @Builder.Default
    private int points = 0;

    @Builder.Default
    private String statut = "ACTIF";

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public int getId() {
        return this.idUsers;
    }

    public void setId(int id) {
        this.idUsers = id;
    }
}
