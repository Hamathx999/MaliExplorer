package com.maliexplorer_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "evenements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvenementModel {

    public enum Statut { EN_ATTENTE, APPROUVE, VALIDE, REFUSE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Le titre est obligatoire !")
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String nomOrganisateur;
    private String emailOrganisateur;
    private String telephoneOrganisateur;

    /** Dates au format affiché par l'admin (ex : 04/02/2026). */
    private String dateDebut;
    private String dateFin;
    private String heureDebut;
    private String heureFin;

    private String lieu;
    private String ville;
    private String region;
    private String categorie;
    private String prix;

    private String imageUrl;
    private String afficheUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Statut statut = Statut.EN_ATTENTE;

    @Column(columnDefinition = "TEXT")
    private String motifRejet;

    private LocalDateTime dateSoumission;

    @PrePersist
    void onCreate() {
        if (dateSoumission == null) dateSoumission = LocalDateTime.now();
        if (statut == null) statut = Statut.EN_ATTENTE;
    }
}

