package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant l'historique d'attribution des points à un utilisateur.
 * Permet la traçabilité des points et le blocage des doublons (anti-duplication).
 */
@Entity
@Table(name = "historique_points", indexes = {
        @Index(name = "idx_user_ref", columnList = "id_users, reference_activite")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriquePointModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", nullable = false)
    private utilisateurModel utilisateur;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(nullable = false)
    private int pointsGagnes;

    @Column(length = 255)
    private String description;

    @Column(name = "reference_activite", length = 100)
    private String referenceActivite;

    @Builder.Default
    @Column(name = "date_gain", nullable = false)
    private LocalDateTime dateGain = LocalDateTime.now();
}
