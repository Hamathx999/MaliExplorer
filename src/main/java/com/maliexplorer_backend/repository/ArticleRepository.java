package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByCategorieIgnoreCase(String categorie, Pageable pageable);

    Page<Article> findByNomArticleContainingIgnoreCaseOrContenuContainingIgnoreCase(
            String keywordNom, String keywordContenu, Pageable pageable);

    List<Article> findTop5ByOrderByDatePublicationDesc();
}
