package com.maliexplorer_backend.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class IngredientModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIngredient;
    private String nomPlat;

    @ManyToMany(mappedBy = "ingredients")
    private Set<PlatModel> plats = new HashSet<>();
}