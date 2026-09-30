package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.dto.ArticleResponseDTO;
import com.maliexplorer_backend.dto.FavoriResponseDTO;
import com.maliexplorer_backend.dto.LieuHistoriqueResponseDTO;
import com.maliexplorer_backend.dto.VilleSummaryDTO;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.ArticleRepository;
import com.maliexplorer_backend.repository.FavoriRepository;
import com.maliexplorer_backend.repository.LieuHistoriqueRepository;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.FavoriService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FavoriServiceImpl implements FavoriService {

    private final FavoriRepository favoriRepository;
    private final LieuHistoriqueRepository lieuHistoriqueRepository;
    private final ArticleRepository articleRepository;
    private final utilisateurRepository userRepository;

    @Override
    public FavoriResponseDTO ajouterLieuFavori(Long lieuId) {
        utilisateurModel user = getCurrentUser();
        LieuHistoriqueModel lieu = lieuHistoriqueRepository.findById(lieuId)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu historique introuvable avec l'ID : " + lieuId));

        if (favoriRepository.existsByUtilisateurIdUsersAndTypeContenuAndLieuIdLieu(user.getIdUsers(), TypeFavori.LIEU_HISTORIQUE, lieuId)) {
            throw new BadRequestException("Ce lieu historique est déjà dans vos favoris.");
        }

        FavoriModel favori = FavoriModel.builder()
                .utilisateur(user)
                .typeContenu(TypeFavori.LIEU_HISTORIQUE)
                .lieu(lieu)
                .dateAjout(LocalDateTime.now())
                .build();

        FavoriModel saved = favoriRepository.save(favori);
        log.info("Lieu historique ID={} ajouté aux favoris par l'utilisateur ID={}", lieuId, user.getIdUsers());
        return mapToDTO(saved, "Lieu historique ajouté aux favoris avec succès");
    }

    @Override
    public void supprimerLieuFavori(Long lieuId) {
        utilisateurModel user = getCurrentUser();
        FavoriModel favori = favoriRepository.findByUtilisateurIdUsersAndTypeContenuAndLieuIdLieu(user.getIdUsers(), TypeFavori.LIEU_HISTORIQUE, lieuId)
                .orElseThrow(() -> new ResourceNotFoundException("Ce lieu historique ne figure pas dans vos favoris."));

        favoriRepository.delete(favori);
        log.info("Lieu historique ID={} retiré des favoris par l'utilisateur ID={}", lieuId, user.getIdUsers());
    }

    @Override
    public FavoriResponseDTO ajouterArticleFavori(Long articleId) {
        utilisateurModel user = getCurrentUser();
        ArticleModel article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable avec l'ID : " + articleId));

        if (favoriRepository.existsByUtilisateurIdUsersAndTypeContenuAndArticleIdArticle(user.getIdUsers(), TypeFavori.ARTICLE, articleId)) {
            throw new BadRequestException("Cet article est déjà dans vos favoris.");
        }

        FavoriModel favori = FavoriModel.builder()
                .utilisateur(user)
                .typeContenu(TypeFavori.ARTICLE)
                .article(article)
                .dateAjout(LocalDateTime.now())
                .build();

        FavoriModel saved = favoriRepository.save(favori);
        log.info("Article ID={} ajouté aux favoris par l'utilisateur ID={}", articleId, user.getIdUsers());
        return mapToDTO(saved, "Article ajouté aux favoris avec succès");
    }

    @Override
    public void supprimerArticleFavori(Long articleId) {
        utilisateurModel user = getCurrentUser();
        FavoriModel favori = favoriRepository.findByUtilisateurIdUsersAndTypeContenuAndArticleIdArticle(user.getIdUsers(), TypeFavori.ARTICLE, articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cet article ne figure pas dans vos favoris."));

        favoriRepository.delete(favori);
        log.info("Article ID={} retiré des favoris par l'utilisateur ID={}", articleId, user.getIdUsers());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriResponseDTO> getMesFavoris() {
        utilisateurModel user = getCurrentUser();
        return favoriRepository.findByUtilisateurIdUsersOrderByDateAjoutDesc(user.getIdUsers())
                .stream()
                .map(f -> mapToDTO(f, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriResponseDTO> getMesFavorisLieux() {
        utilisateurModel user = getCurrentUser();
        return favoriRepository.findByUtilisateurIdUsersAndTypeContenuOrderByDateAjoutDesc(user.getIdUsers(), TypeFavori.LIEU_HISTORIQUE)
                .stream()
                .map(f -> mapToDTO(f, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriResponseDTO> getMesFavorisArticles() {
        utilisateurModel user = getCurrentUser();
        return favoriRepository.findByUtilisateurIdUsersAndTypeContenuOrderByDateAjoutDesc(user.getIdUsers(), TypeFavori.ARTICLE)
                .stream()
                .map(f -> mapToDTO(f, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estLieuFavori(Long lieuId) {
        utilisateurModel user = getCurrentUser();
        return favoriRepository.existsByUtilisateurIdUsersAndTypeContenuAndLieuIdLieu(user.getIdUsers(), TypeFavori.LIEU_HISTORIQUE, lieuId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estArticleFavori(Long articleId) {
        utilisateurModel user = getCurrentUser();
        return favoriRepository.existsByUtilisateurIdUsersAndTypeContenuAndArticleIdArticle(user.getIdUsers(), TypeFavori.ARTICLE, articleId);
    }

    // ======================== PRIVATE HELPERS ========================

    private utilisateurModel getCurrentUser() {
        utilisateurModel currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null && currentUser.getIdUsers() > 0) {
            return currentUser;
        }

        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            throw new BadRequestException("Utilisateur non authentifié");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profil utilisateur introuvable pour l'email : " + email));
    }

    private FavoriResponseDTO mapToDTO(FavoriModel favori, String message) {
        LieuHistoriqueResponseDTO lieuDTO = null;
        if (favori.getLieu() != null) {
            LieuHistoriqueModel l = favori.getLieu();
            VilleSummaryDTO villeDTO = null;
            if (l.getVille() != null) {
                villeDTO = VilleSummaryDTO.builder()
                        .id(l.getVille().getIdVille())
                        .nom(l.getVille().getNomVille())
                        .build();
            }
            lieuDTO = LieuHistoriqueResponseDTO.builder()
                    .idLieu(l.getIdLieu())
                    .nomLieuHisto(l.getNomLieu())
                    .description(l.getDescription())
                    .epoque(l.getEpoque())
                    .cordonnees(l.getCordonnees())
                    .latitude(l.getLatitude())
                    .longitude(l.getLongitude())
                    .panorama360Url(l.getPanorama360Url())
                    .ville(villeDTO)
                    .build();
        }

        ArticleResponseDTO articleDTO = null;
        if (favori.getArticle() != null) {
            ArticleModel a = favori.getArticle();
            articleDTO = ArticleResponseDTO.builder()
                    .idArticle(a.getIdArticle())
                    .nomArticle(a.getNomArticle())
                    .contenu(a.getContenu())
                    .datePublication(a.getDatePublication())
                    .vues(a.getVues())
                    .build();
        }

        return FavoriResponseDTO.builder()
                .idFavori(favori.getIdFavori())
                .idUsers(favori.getUtilisateur() != null ? favori.getUtilisateur().getIdUsers() : null)
                .userEmail(favori.getUtilisateur() != null ? favori.getUtilisateur().getEmail() : null)
                .typeContenu(favori.getTypeContenu())
                .dateAjout(favori.getDateAjout())
                .lieu(lieuDTO)
                .article(articleDTO)
                .message(message)
                .build();
    }
}
