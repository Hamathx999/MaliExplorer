package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.ArticleRequestDTO;
import com.maliexplorer_backend.dto.ArticleResponseDTO;
import com.maliexplorer_backend.dto.ArticleSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {

    Page<ArticleResponseDTO> getAllArticles(Pageable pageable);

    ArticleResponseDTO getArticleById(Long id);

    ArticleResponseDTO createArticle(ArticleRequestDTO requestDTO);

    ArticleResponseDTO updateArticle(Long id, ArticleRequestDTO requestDTO);

    void deleteArticle(Long id);

    Page<ArticleResponseDTO> searchArticles(String keyword, Pageable pageable);

    Page<ArticleResponseDTO> getArticlesByCategorie(String categorie, Pageable pageable);

    List<ArticleSummaryDTO> getRecentArticles();
}
