package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favoris", indexes = {
        @Index(name = "idx_user_type", columnList = "id_users, type_contenu")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFavori;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", nullable = false)
    private utilisateurModel utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_contenu", nullable = false, length = 30)
    private TypeFavori typeContenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lieu")
    private LieuHistoriqueModel lieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_article")
    private ArticleModel article;

    @Builder.Default
    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout = LocalDateTime.now();
}
