package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.model.IngredientModel;
import com.maliexplorer_backend.serviceimpl.IngredientServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients", description = "Gestion des ingredients pour les plats traditionnels et gastronomie malienne")
public class IngredientController {

    private final IngredientServiceImpl ingredientService;

    @GetMapping
    @Operation(summary = "Lister tous les ingredients")
    public ResponseEntity<List<IngredientModel>> listerIngredient() {
        return ResponseEntity.ok(ingredientService.listerIngredient());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un ingredient par son ID")
    public ResponseEntity<IngredientModel> recupIngredientById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ingredientService.recupIngredientById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ajouter un ingredient (Admin)")
    public ResponseEntity<IngredientModel> ajouterIngredient(@RequestBody IngredientModel ingredientModel) {
        return new ResponseEntity<>(ingredientService.ajouterIngredient(ingredientModel), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un ingredient (Admin)")
    public ResponseEntity<IngredientModel> modifierIngredient(
            @PathVariable("id") Long id,
            @RequestBody IngredientModel ingredientModel) {
        return ResponseEntity.ok(ingredientService.modifierIngredient(id, ingredientModel));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un ingredient (Admin)")
    public ResponseEntity<Void> suppIngredient(@PathVariable("id") Long id) {
        ingredientService.suppIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
