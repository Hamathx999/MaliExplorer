package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.PropositionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropositionRepository extends JpaRepository<PropositionModel,Long> {
    List<PropositionModel> findByQuestionIdQuestion(Long idQuestion);
}
