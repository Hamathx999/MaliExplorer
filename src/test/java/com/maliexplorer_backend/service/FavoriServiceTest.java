package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.FavoriResponseDTO;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.ArticleRepository;
import com.maliexplorer_backend.repository.FavoriRepository;
import com.maliexplorer_backend.repository.LieuHistoriqueRepository;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.serviceimpl.FavoriServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriServiceTest {

    @Mock
    private FavoriRepository favoriRepository;

    @Mock
    private LieuHistoriqueRepository lieuHistoriqueRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private utilisateurRepository userRepository;

    @InjectMocks
    private FavoriServiceImpl favoriService;

    private utilisateurModel userConnecte;
    private LieuHistoriqueModel lieu;
    private ArticleModel article;

    @BeforeEach
    void setUp() {
        userConnecte = utilisateurModel.builder()
                .idUsers(1)
                .prenom("Awa")
                .nom("Coulibaly")
                .email("awa@maliexplorer.ml")
                .build();

        lieu = LieuHistoriqueModel.builder()
                .idLieu(100L)
                .nomLieu("Mosquée de Djenné")
                .epoque("XIIIe siècle")
                .build();

        article = ArticleModel.builder()
                .idArticle(200L)
                .nomArticle("Histoire des Mansa du Mali")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userConnecte, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Devrait ajouter un lieu historique aux favoris avec succès")
    void testAjouterLieuFavoriSucces() {
        when(lieuHistoriqueRepository.findById(100L)).thenReturn(Optional.of(lieu));
        when(favoriRepository.existsByUtilisateurIdUsersAndTypeContenuAndLieuIdLieu(1, TypeFavori.LIEU_HISTORIQUE, 100L))
                .thenReturn(false);

        FavoriModel savedFavori = FavoriModel.builder()
                .idFavori(1L)
                .utilisateur(userConnecte)
                .typeContenu(TypeFavori.LIEU_HISTORIQUE)
                .lieu(lieu)
                .dateAjout(LocalDateTime.now())
                .build();

        when(favoriRepository.save(any(FavoriModel.class))).thenReturn(savedFavori);

        FavoriResponseDTO result = favoriService.ajouterLieuFavori(100L);

        assertNotNull(result);
        assertEquals(TypeFavori.LIEU_HISTORIQUE, result.getTypeContenu());
        assertNotNull(result.getLieu());
        assertEquals("Mosquée de Djenné", result.getLieu().getNomLieuHisto());
        verify(favoriRepository).save(any(FavoriModel.class));
    }

    @Test
    @DisplayName("Devrait refuser l'ajout d'un doublon de lieu dans les favoris")
    void testAjouterLieuFavoriDoublon() {
        when(lieuHistoriqueRepository.findById(100L)).thenReturn(Optional.of(lieu));
        when(favoriRepository.existsByUtilisateurIdUsersAndTypeContenuAndLieuIdLieu(1, TypeFavori.LIEU_HISTORIQUE, 100L))
                .thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> favoriService.ajouterLieuFavori(100L));
        assertTrue(ex.getMessage().contains("déjà dans vos favoris"));
        verify(favoriRepository, never()).save(any(FavoriModel.class));
    }

    @Test
    @DisplayName("Devrait ajouter un article aux favoris avec succès")
    void testAjouterArticleFavoriSucces() {
        when(articleRepository.findById(200L)).thenReturn(Optional.of(article));
        when(favoriRepository.existsByUtilisateurIdUsersAndTypeContenuAndArticleIdArticle(1, TypeFavori.ARTICLE, 200L))
                .thenReturn(false);

        FavoriModel savedFavori = FavoriModel.builder()
                .idFavori(2L)
                .utilisateur(userConnecte)
                .typeContenu(TypeFavori.ARTICLE)
                .article(article)
                .dateAjout(LocalDateTime.now())
                .build();

        when(favoriRepository.save(any(FavoriModel.class))).thenReturn(savedFavori);

        FavoriResponseDTO result = favoriService.ajouterArticleFavori(200L);

        assertNotNull(result);
        assertEquals(TypeFavori.ARTICLE, result.getTypeContenu());
        assertNotNull(result.getArticle());
        assertEquals("Histoire des Mansa du Mali", result.getArticle().getNomArticle());
        verify(favoriRepository).save(any(FavoriModel.class));
    }

    @Test
    @DisplayName("Devrait vérifier l'existence d'un lieu dans les favoris")
    void testExisteLieuFavori() {
        when(favoriRepository.existsByUtilisateurIdUsersAndTypeContenuAndLieuIdLieu(1, TypeFavori.LIEU_HISTORIQUE, 100L))
                .thenReturn(true);

        boolean existe = favoriService.estLieuFavori(100L);
        assertTrue(existe);
    }

    @Test
    @DisplayName("Devrait supprimer un lieu des favoris avec succès")
    void testSupprimerLieuFavori() {
        FavoriModel favori = FavoriModel.builder()
                .idFavori(1L)
                .utilisateur(userConnecte)
                .typeContenu(TypeFavori.LIEU_HISTORIQUE)
                .lieu(lieu)
                .build();

        when(favoriRepository.findByUtilisateurIdUsersAndTypeContenuAndLieuIdLieu(1, TypeFavori.LIEU_HISTORIQUE, 100L))
                .thenReturn(Optional.of(favori));

        favoriService.supprimerLieuFavori(100L);
        verify(favoriRepository).delete(favori);
    }
}
