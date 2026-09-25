package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlat;

    @Column(nullable = false, length = 150)
    private String nomPlat;

    @Column(length = 150)
    private String nomAlternatif;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String ingredients;

    private Integer tempsPreparation;

    @Column(length = 500)
    private String imageUrl;

    private Long idAdministrateur;

    @ManyToMany(mappedBy = "plats")
    @Builder.Default
    private List<Region> regions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "plat_ethnie",
            joinColumns = @JoinColumn(name = "plat_id"),
            inverseJoinColumns = @JoinColumn(name = "ethnie_id")
    )
    @Builder.Default
    private List<Ethnie> ethnies = new ArrayList<>();

    public Long getId() {
        return this.idPlat;
    }

    public void setId(Long id) {
        this.idPlat = id;
    }

    public String getNom() {
        return this.nomPlat;
    }

    public void setNom(String nom) {
        this.nomPlat = nom;
    }
}
