package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "villes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVille;

    @Column(nullable = false, length = 100)
    private String nomVille;

    private String region;

    private String nbreHbts;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String cordonnees;

    private Double latitude;

    private Double longitude;

    @Column(name = "est_capitale")
    private Boolean estCapitale;

    @Column(length = 500)
    private String imageUrl;

    private Long idUsers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_region")
    private Region regionParent;

    @OneToMany(mappedBy = "ville", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LieuHistorique> lieuxHistoriques = new ArrayList<>();

    public Long getId() {
        return this.idVille;
    }

    public void setId(Long id) {
        this.idVille = id;
    }

    public String getNom() {
        return this.nomVille;
    }

    public void setNom(String nom) {
        this.nomVille = nom;
    }
}
