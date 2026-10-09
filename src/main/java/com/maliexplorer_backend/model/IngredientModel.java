package com.maliexplorer_backend.model;
//
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@ToString(exclude = "plats")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long idIngredient;

    private String nom;
    private String nomPlat;
    private String categorie;
    private String description;
    private String imageUrl;

    @JsonIgnore
    @ManyToMany(mappedBy = "ingredients")
    @Builder.Default
    private Set<PlatModel> plats = new HashSet<>();

    @JsonProperty("id")
    public Long getId() {
        return idIngredient;
    }

    public void setId(Long id) {
        this.idIngredient = id;
    }

    @JsonProperty("nom")
    public String getNom() {
        return (nom != null && !nom.isBlank()) ? nom : nomPlat;
    }

    public void setNom(String nom) {
        this.nom = nom;
        this.nomPlat = nom;
    }

    public void setNomPlat(String nomPlat) {
        this.nomPlat = nomPlat;
        if (this.nom == null || this.nom.isBlank()) {
            this.nom = nomPlat;
        }
    }
}