package com.maliexplorer_backend.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lieux_historiques")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LieuHistorique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLieu;

    @Column(nullable = false, length = 150)
    private String nomHistoire;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String epoque;

    private String cordonnees;

    private Long idUsers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ville")
    private Ville ville;

    public Long getId() {
        return this.idLieu;
    }

    public void setId(Long id) {
        this.idLieu = id;
    }

    public String getNom() {
        return this.nomHistoire;
    }

    public void setNom(String nom) {
        this.nomHistoire = nom;
    }
}
