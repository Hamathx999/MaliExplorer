package com.maliexplorer_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "commentaires_evenement")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireEvenementModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID de l'événement est obligatoire")
    @Column(name = "id_evenement", nullable = false)
    private Long idEvenement;

    private Integer idUser;

    @NotBlank(message = "Le nom de l'auteur est obligatoire")
    private String nomAuteur;

    private String photoAuteur;

    @NotBlank(message = "Le contenu du commentaire ne peut être vide")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Builder.Default
    private int note = 5;

    private LocalDateTime dateCreation;

    @PrePersist
    void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}

