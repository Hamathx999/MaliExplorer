package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lieux_historiques")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LieuHistoriqueModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLieu;

    @Column(nullable = false, length = 150)
    private String nomLieu;

    @Column(name = "nom_histoire", length = 150)
    private String nomHistoire;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String epoque;

    private String cordonnees;

    @Column(length = 500)
    private String panorama360Url;

    private Double latitude;

    private Double longitude;

    private Long idUsers;

    @Column(name = "nom_ville", length = 100)
    private String nomVille;

    @Column(length = 100)
    private String region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ville")
    private VilleModel ville;

    public Long getId() {
        return this.idLieu;
    }

    public void setId(Long id) {
        this.idLieu = id;
    }

    public String getNom() {
        return this.nomLieu;
    }

    public void setNom(String nom) {
        this.nomLieu = nom;
    }
}
