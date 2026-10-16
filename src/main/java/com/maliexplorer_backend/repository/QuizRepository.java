package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.QuizModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<QuizModel, Long> {

    List<QuizModel> findByNomQuizContainingIgnoreCase(String keyword);

    List<QuizModel> findByCategorieIgnoreCase(String categorie);

    Optional<QuizModel> findByNomQuizIgnoreCase(String nomQuiz);

    boolean existsByNomQuizIgnoreCase(String nomQuiz);
}
