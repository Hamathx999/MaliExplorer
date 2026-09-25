package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArticle;

    @Column(nullable = false, length = 200)
    private String nomArticle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(length = 100)
    private String auteur;

    @Column(length = 50)
    private String categorie;

    @Column(length = 500)
    private String imageUrl;

    private LocalDateTime datePublication;

    @Builder.Default
    private Long vues = 0L;

    private Long idUsers;

    @PrePersist
    public void prePersist() {
        if (this.datePublication == null) {
            this.datePublication = LocalDateTime.now();
        }
        if (this.vues == null) {
            this.vues = 0L;
        }
    }

    public Long getId() {
        return this.idArticle;
    }

    public void setId(Long id) {
        this.idArticle = id;
    }
}
