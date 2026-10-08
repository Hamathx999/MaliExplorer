package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.IngredientModel;
import com.maliexplorer_backend.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientServiceImpl {

    private final IngredientRepository ingredient;

    @Transactional(readOnly = true)
    public List<IngredientModel> listerIngredient() {
        return ingredient.findAll();
    }

    @Transactional(readOnly = true)
    public IngredientModel recupIngredientById(Long id) {
        return ingredient.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient introuvable avec l'ID: " + id));
    }

    public IngredientModel ajouterIngredient(IngredientModel model) {
        model.setId(null);
        if (model.getNomPlat() == null && model.getNom() != null) {
            model.setNomPlat(model.getNom());
        }
        return ingredient.save(model);
    }

    public IngredientModel modifierIngredient(Long id, IngredientModel model) {
        IngredientModel existant = recupIngredientById(id);
        if (model.getNom() != null) {
            existant.setNom(model.getNom());
            existant.setNomPlat(model.getNom());
        }
        if (model.getCategorie() != null) existant.setCategorie(model.getCategorie());
        if (model.getDescription() != null) existant.setDescription(model.getDescription());
        if (model.getImageUrl() != null) existant.setImageUrl(model.getImageUrl());
        return ingredient.save(existant);
    }

    public void suppIngredient(Long id) {
        if (ingredient.existsById(id)) {
            ingredient.deleteById(id);
        }
    }
}
