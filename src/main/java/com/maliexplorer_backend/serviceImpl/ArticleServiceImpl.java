package com.maliexplorer_backend.serviceImpl;

import com.maliexplorer_backend.dto.ArticleRequestDTO;
import com.maliexplorer_backend.dto.ArticleResponseDTO;
import com.maliexplorer_backend.dto.ArticleSummaryDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.Article;
import com.maliexplorer_backend.repository.ArticleRepository;
import com.maliexplorer_backend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleResponseDTO> getAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public ArticleResponseDTO getArticleById(Long id) {
        Article article = findArticleOrThrow(id);
        article.setVues(article.getVues() == null ? 1 : article.getVues() + 1);
        Article saved = articleRepository.save(article);
        return mapToResponseDTO(saved);
    }

    @Override
    public ArticleResponseDTO createArticle(ArticleRequestDTO requestDTO) {
        Article article = Article.builder()
                .nomArticle(requestDTO.getNomArticle())
                .contenu(requestDTO.getContenu())
                .auteur(requestDTO.getAuteur())
                .categorie(requestDTO.getCategorie())
                .imageUrl(requestDTO.getImageUrl())
                .datePublication(LocalDateTime.now())
                .vues(0L)
                .idUsers(requestDTO.getIdUsers())
                .build();

        Article saved = articleRepository.save(article);
        return mapToResponseDTO(saved);
    }

    @Override
    public ArticleResponseDTO updateArticle(Long id, ArticleRequestDTO requestDTO) {
        Article article = findArticleOrThrow(id);

        article.setNomArticle(requestDTO.getNomArticle());
        article.setContenu(requestDTO.getContenu());
        article.setAuteur(requestDTO.getAuteur());
        article.setCategorie(requestDTO.getCategorie());
        article.setImageUrl(requestDTO.getImageUrl());
        if (requestDTO.getIdUsers() != null) {
            article.setIdUsers(requestDTO.getIdUsers());
        }

        Article updated = articleRepository.save(article);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteArticle(Long id) {
        Article article = findArticleOrThrow(id);
        articleRepository.delete(article);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleResponseDTO> searchArticles(String keyword, Pageable pageable) {
        return articleRepository.findByNomArticleContainingIgnoreCaseOrContenuContainingIgnoreCase(
                        keyword, keyword, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleResponseDTO> getArticlesByCategorie(String categorie, Pageable pageable) {
        return articleRepository.findByCategorieIgnoreCase(categorie, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleSummaryDTO> getRecentArticles() {
        return articleRepository.findTop5ByOrderByDatePublicationDesc()
                .stream()
                .map(a -> ArticleSummaryDTO.builder()
                        .idArticle(a.getIdArticle())
                        .nomArticle(a.getNomArticle())
                        .auteur(a.getAuteur())
                        .categorie(a.getCategorie())
                        .imageUrl(a.getImageUrl())
                        .datePublication(a.getDatePublication())
                        .vues(a.getVues())
                        .build())
                .collect(Collectors.toList());
    }

    private Article findArticleOrThrow(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable avec l'ID : " + id));
    }

    private ArticleResponseDTO mapToResponseDTO(Article article) {
        return ArticleResponseDTO.builder()
                .idArticle(article.getIdArticle())
                .nomArticle(article.getNomArticle())
                .contenu(article.getContenu())
                .auteur(article.getAuteur())
                .categorie(article.getCategorie())
                .imageUrl(article.getImageUrl())
                .datePublication(article.getDatePublication())
                .vues(article.getVues())
                .idUsers(article.getIdUsers())
                .build();
    }
}
