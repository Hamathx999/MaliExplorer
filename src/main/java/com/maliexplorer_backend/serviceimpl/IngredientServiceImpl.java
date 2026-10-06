package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.model.IngredientModel;
import com.maliexplorer_backend.repository.IngredientRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class IngredientServiceImpl {
    private IngredientRepository ingredient;

    public  IngredientModel ajouterIngredient (IngredientModel ingredientModel) {
       return ingredient.save(ingredientModel);


    }

    public List<IngredientModel> listerIngredient () {
        List<IngredientModel> listIngredient = ingredient.findAll();;
        return listIngredient;

    }

    public IngredientModel recupIngredientById (Long id) {
        IngredientModel existant=ingredient.findById(id).orElseThrow();
        return existant;

    }

    public void suppIngredient(Long id) {
        IngredientModel ingredientById =ingredient.findById(id).orElseThrow();
        ingredient.delete(ingredientById);
    }
}
