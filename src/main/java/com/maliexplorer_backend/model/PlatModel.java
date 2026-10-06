package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "plats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlat;

    @Column(nullable = false, length = 150)
    private String nomPlat;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer nbrePersonnes;

    private Integer tempsPreparation;

    @Column(length = 500)
    private String imageUrl;

//    @Column(columnDefinition = "TEXT")
    @ManyToMany
    @JoinTable(
            name = "plat_ingredient",
            joinColumns = @JoinColumn(name = "plat_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<IngredientModel> ingredients = new HashSet<>();

//    private Long idIngredient;

    @ManyToMany(mappedBy = "plats")
    @Builder.Default
    private List<RegionModel> regions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "plat_ethnie",
            joinColumns = @JoinColumn(name = "plat_id"),
            inverseJoinColumns = @JoinColumn(name = "ethnie_id")
    )
    @Builder.Default
    private List<EthnieModel> ethnies = new ArrayList<>();

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
