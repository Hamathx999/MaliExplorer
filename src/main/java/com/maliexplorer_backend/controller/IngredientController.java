package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.model.IngredientModel;
import com.maliexplorer_backend.serviceimpl.IngredientServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredients", description = "Gestion des ingredients pour les plats traditionnels et gastronomie malienne")
public class IngredientController {
    private IngredientServiceImpl ingredientService;

    @PostMapping
    @Operation(summary = "Ajouter un ingredient")
    public ResponseEntity<IngredientModel> ajouterIngredient(@RequestBody IngredientModel ingredientModel){
        return ResponseEntity.ok(ingredientService.ajouterIngredient(ingredientModel));

    }

    @GetMapping
    @Operation(summary = "Lister tous les ingredients")
    public List<IngredientModel> listerIngredient(){
        return ingredientService.listerIngredient();

    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un ingredient")
    public ResponseEntity<IngredientModel> recupIngredientById(@PathVariable Long id){
        return ResponseEntity.ok(ingredientService.recupIngredientById(id));

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un ingredient")
    public ResponseEntity<Void> suppIngredient(@PathVariable Long id){
        ingredientService.suppIngredient(id);
        return ResponseEntity.noContent().build();
    }

}
