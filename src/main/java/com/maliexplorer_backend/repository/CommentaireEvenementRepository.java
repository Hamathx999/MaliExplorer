package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.CommentaireEvenementModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaireEvenementRepository extends JpaRepository<CommentaireEvenementModel, Long> {

    List<CommentaireEvenementModel> findByIdEvenementOrderByDateCreationDesc(Long idEvenement);

    List<CommentaireEvenementModel> findByIdEvenementInOrderByDateCreationDesc(List<Long> idEvenements);

    long countByIdEvenement(Long idEvenement);
}

