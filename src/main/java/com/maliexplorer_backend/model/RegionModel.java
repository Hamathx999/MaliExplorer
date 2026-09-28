package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "regions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegion;

    @Column(nullable = false, unique = true, length = 100)
    private String nomRegion;

    private Long nbreHbt;

    @OneToMany(mappedBy = "regionParent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VilleModel> villes = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "region_ethnie", joinColumns = @JoinColumn(name = "region_id"), inverseJoinColumns = @JoinColumn(name = "ethnie_id"))
    @Builder.Default
    private List<EthnieModel> ethnies = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "region_plat", joinColumns = @JoinColumn(name = "region_id"), inverseJoinColumns = @JoinColumn(name = "plat_id"))
    @Builder.Default
    private List<PlatModel> plats = new ArrayList<>();

    public Long getId() {
        return this.idRegion;
    }

    public void setId(Long id) {
        this.idRegion = id;
    }

    public String getNom() {
        return this.nomRegion;
    }

    public void setNom(String nom) {
        this.nomRegion = nom;
    }
}
