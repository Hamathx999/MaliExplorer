package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.QuizModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<QuizModel, Long> {

    List<QuizModel> findByCategorieIgnoreCase(String categorie);

    List<QuizModel> findByNiveauDifficulteIgnoreCase(String niveauDifficulte);

    List<QuizModel> findByNomQuizContainingIgnoreCase(String keyword);
}
