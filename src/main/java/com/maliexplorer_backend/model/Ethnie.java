package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ethnies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ethnie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEthnie;

    @Column(nullable = false, unique = true, length = 100)
    private String nomEthnie;

    private String region;

    private String population;

    @Column(length = 100)
    private String langue;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String imageUrl;

    private Long idUsers;

    @ManyToMany(mappedBy = "ethnies")
    @Builder.Default
    private List<Region> regions = new ArrayList<>();

    @ManyToMany(mappedBy = "ethnies")
    @Builder.Default
    private List<Plat> plats = new ArrayList<>();

    public Long getId() {
        return this.idEthnie;
    }

    public void setId(Long id) {
        this.idEthnie = id;
    }

    public String getNom() {
        return this.nomEthnie;
    }

    public void setNom(String nom) {
        this.nomEthnie = nom;
    }
}