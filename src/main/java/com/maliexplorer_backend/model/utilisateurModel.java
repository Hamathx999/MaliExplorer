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

    @Builder.Default
    private String telephone = "";

    @Builder.Default
    @Column(columnDefinition = "TEXT")
    private String photoUrl = "";

    private Date dateCreation;

    @Enumerated(EnumType.STRING)
    private RoleModel role;

    @Builder.Default
    private int points = 0;

    @Builder.Default
    private String statut = "ACTIF";

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String transientNomOrganisation;

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String transientPieceIdentite;

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String transientLangues;

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Integer transientExperience;

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String transientDescription;

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private StatutModeration transientStatutModeration;

    @Transient
    private String nomEvenement;

    @Transient
    private String dateEvenement;

    @Transient
    private String descriptionEvenement;

    @Transient
    private java.util.List<String> photosEvenement;

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public int getId() {
        return this.idUsers;
    }

    public void setId(int id) {
        this.idUsers = id;
    }

    public int getIdUsers() {
        return this.idUsers;
    }

    public void setIdUsers(int idUsers) {
        this.idUsers = idUsers;
    }

    public RoleModel getRole() {
        return this.role;
    }

    public void setRole(RoleModel role) {
        this.role = role;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("statutModeration")
    public StatutModeration getStatutModeration() {
        if (this instanceof PromoteurModel p && p.getStatutModeration() != null) {
            return p.getStatutModeration();
        }
        if (this instanceof GuideModel g && g.getStatutModeration() != null) {
            return g.getStatutModeration();
        }
        if (this.transientStatutModeration != null) {
            return this.transientStatutModeration;
        }
        if (this.role == RoleModel.promoteur || this.role == RoleModel.guide || this.role == RoleModel.partenaire) {
            if ("EN_ATTENTE".equalsIgnoreCase(this.statut)) {
                return StatutModeration.EN_ATTENTE_VALIDATION;
            }
            if ("REJETE".equalsIgnoreCase(this.statut) || "REFUSE".equalsIgnoreCase(this.statut)) {
                return StatutModeration.REJETE;
            }
            return StatutModeration.VALIDE;
        }
        return null;
    }

    public void setStatutModeration(StatutModeration statutModeration) {
        this.transientStatutModeration = statutModeration;
        if (this instanceof PromoteurModel p) {
            p.setStatutModeration(statutModeration);
        } else if (this instanceof GuideModel g) {
            g.setStatutModeration(statutModeration);
        }
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("nomOrganisation")
    public String getNomOrganisation() {
        if (this instanceof PromoteurModel p && p.getNomOrganisation() != null) {
            return p.getNomOrganisation();
        }
        return this.transientNomOrganisation;
    }

    public void setNomOrganisation(String nomOrganisation) {
        this.transientNomOrganisation = nomOrganisation;
        if (this instanceof PromoteurModel p) {
            p.setNomOrganisation(nomOrganisation);
        }
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("pieceIdentite")
    public String getPieceIdentite() {
        if (this instanceof PromoteurModel p && p.getPieceIdentite() != null) {
            return p.getPieceIdentite();
        }
        if (this instanceof GuideModel g && g.getPieceIdentite() != null) {
            return g.getPieceIdentite();
        }
        return this.transientPieceIdentite;
    }

    public void setPieceIdentite(String pieceIdentite) {
        this.transientPieceIdentite = pieceIdentite;
        if (this instanceof PromoteurModel p) {
            p.setPieceIdentite(pieceIdentite);
        } else if (this instanceof GuideModel g) {
            g.setPieceIdentite(pieceIdentite);
        }
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("langues")
    public String getLangues() {
        if (this instanceof GuideModel g && g.getLangue() != null) {
            return g.getLangue();
        }
        return this.transientLangues;
    }

    public void setLangues(String langues) {
        this.transientLangues = langues;
        if (this instanceof GuideModel g && langues != null && !langues.isBlank()) {
            g.setLangue(langues);
        }
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("experience")
    public int getExperience() {
        if (this instanceof GuideModel g) {
            return g.getExperience();
        }
        return this.transientExperience != null ? this.transientExperience : 1;
    }

    public void setExperience(int experience) {
        this.transientExperience = experience;
        if (this instanceof GuideModel g) {
            g.setExperience(experience);
        }
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    public String getDescription() {
        if (this instanceof GuideModel g && g.getDescription() != null) {
            return g.getDescription();
        }
        if (this instanceof PromoteurModel p && p.getBesoinPartenariat() != null) {
            return p.getBesoinPartenariat();
        }
        return this.transientDescription;
    }

    public void setDescription(String description) {
        this.transientDescription = description;
        if (this instanceof GuideModel g) {
            g.setDescription(description);
        }
    }
}
