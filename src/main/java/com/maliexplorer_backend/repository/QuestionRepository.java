package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.QuestionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionModel, Long> {

    List<QuestionModel> findByQuizIdQuiz(Long idQuiz);
}
