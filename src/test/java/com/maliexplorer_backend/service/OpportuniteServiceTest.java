package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.OpportuniteResponseDTO;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.ArtisanRepository;
import com.maliexplorer_backend.repository.GuideRepository;
import com.maliexplorer_backend.repository.PromoteurRepository;
import com.maliexplorer_backend.serviceimpl.OpportuniteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpportuniteServiceTest {

    @Mock
    private ArtisanRepository artisanRepository;

    @Mock
    private PromoteurRepository promoteurRepository;

    @Mock
    private GuideRepository guideRepository;

    @InjectMocks
    private OpportuniteServiceImpl opportuniteService;

    private ArtisanModel artisan;
    private PromoteurModel promoteur;
    private GuideModel guide;

    @BeforeEach
    void setUp() {
        artisan = new ArtisanModel();
        artisan.setIdUsers(1);
        artisan.setPrenom("Mamadou");
        artisan.setNom("Coulibaly");
        artisan.setEmail("mamadou@artisanat.ml");
        artisan.setRole(RoleModel.artisan);
        artisan.setTypeArtisanat("Maroquinerie Touareg");

        promoteur = new PromoteurModel();
        promoteur.setIdUsers(2);
        promoteur.setPrenom("Fatoumata");
        promoteur.setNom("Traoré");
        promoteur.setEmail("fatou@festival.ml");
        promoteur.setRole(RoleModel.promoteur);
        promoteur.setNomOrganisation("Festival du Désert");

        guide = new GuideModel();
        guide.setIdUsers(3);
        guide.setPrenom("Ousmane");
        guide.setNom("Sissoko");
        guide.setEmail("ousmane@guide.ml");
        guide.setRole(RoleModel.guide);
        guide.setLangue("Bambara, Français");
    }

    @Test
    @DisplayName("Soumettre un projet de partenariat bascule en EN_ATTENTE_VALIDATION")
    void testSoumettreProjetPartenaire() {
        when(artisanRepository.findById(1)).thenReturn(Optional.of(artisan));
        when(artisanRepository.save(any(ArtisanModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.soumettreProjetPartenaire(
                1, "Atelier Cuir Authentique", "Recherche financement de 2.000.000 FCFA pour outillage", true);

        assertNotNull(res);
        assertTrue(res.isRecherchePartenariat());
        assertEquals("Atelier Cuir Authentique", res.getTitreProjet());
        assertEquals("Recherche financement de 2.000.000 FCFA pour outillage", res.getBesoinPartenariat());
        assertEquals(StatutModeration.EN_ATTENTE_VALIDATION, res.getStatutModeration());
        assertNull(res.getMotifRejet());
    }

    @Test
    @DisplayName("Validation admin valide le statut en VALIDE")
    void testValiderOpportunite() {
        artisan.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        when(artisanRepository.findById(1)).thenReturn(Optional.of(artisan));
        when(artisanRepository.save(any(ArtisanModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.validerOpportunite(1);

        assertNotNull(res);
        assertEquals(StatutModeration.VALIDE, res.getStatutModeration());
    }

    @Test
    @DisplayName("Rejet admin passe le statut en REJETE avec un motif explicite")
    void testRejeterOpportunite() {
        artisan.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        when(artisanRepository.findById(1)).thenReturn(Optional.of(artisan));
        when(artisanRepository.save(any(ArtisanModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.rejeterOpportunite(1, "Dossier incomplet, précisions budgétaires requises");

        assertNotNull(res);
        assertEquals(StatutModeration.REJETE, res.getStatutModeration());
        assertEquals("Dossier incomplet, précisions budgétaires requises", res.getMotifRejet());
    }

    @Test
    @DisplayName("Catalogue public B2B filtre par type et mot-clé")
    void testGetOpportunitesValideesAvecFiltres() {
        artisan.setRecherchePartenariat(true);
        artisan.setTitreProjet("Cuir de Tombouctou");
        artisan.setStatutModeration(StatutModeration.VALIDE);

        promoteur.setRecherchePartenariat(true);
        promoteur.setTitreProjet("Festival International de Musique");
        promoteur.setStatutModeration(StatutModeration.VALIDE);

        when(artisanRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE))
                .thenReturn(List.of(artisan));
        when(promoteurRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE))
                .thenReturn(List.of(promoteur));
        when(guideRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE))
                .thenReturn(Collections.emptyList());

        // Recherche tous types avec mot-clé "cuir"
        List<OpportuniteResponseDTO> filtrerCuir = opportuniteService.getOpportunitesValidees(null, "cuir");
        assertEquals(1, filtrerCuir.size());
        assertEquals("ARTISAN", filtrerCuir.get(0).getTypePartenaire());

        // Filtrage spécifique par type PROMOTEUR
        List<OpportuniteResponseDTO> promoteurs = opportuniteService.getOpportunitesValidees("PROMOTEUR", null);
        assertEquals(1, promoteurs.size());
        assertEquals("PROMOTEUR", promoteurs.get(0).getTypePartenaire());
    }

    @Test
    @DisplayName("Admin peut lister les opportunités en attente de modération")
    void testGetOpportunitesEnAttente() {
        artisan.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        when(artisanRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(List.of(artisan));
        when(promoteurRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(Collections.emptyList());
        when(guideRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(Collections.emptyList());

        List<OpportuniteResponseDTO> enAttente = opportuniteService.getOpportunitesEnAttente();
        assertEquals(1, enAttente.size());
        assertEquals("Mamadou Coulibaly", enAttente.get(0).getNomComplet());
    }
}
