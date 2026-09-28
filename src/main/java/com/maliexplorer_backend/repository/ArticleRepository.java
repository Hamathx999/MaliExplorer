package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.ArticleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleModel, Long> {

    Page<ArticleModel> findByCategorieIgnoreCase(String categorie, Pageable pageable);

    Page<ArticleModel> findByNomArticleContainingIgnoreCaseOrContenuContainingIgnoreCase(
            String keywordNom, String keywordContenu, Pageable pageable);

    List<ArticleModel> findTop5ByOrderByDatePublicationDesc();
}
