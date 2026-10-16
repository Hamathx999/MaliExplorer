package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.OpportuniteResponseDTO;
import com.maliexplorer_backend.model.*;
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
    private PromoteurRepository promoteurRepository;

    @Mock
    private GuideRepository guideRepository;

    @InjectMocks
    private OpportuniteServiceImpl opportuniteService;

    private PromoteurModel promoteur;
    private GuideModel guide;

    @BeforeEach
    void setUp() {
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
        when(promoteurRepository.findById(2)).thenReturn(Optional.of(promoteur));
        when(promoteurRepository.save(any(PromoteurModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.soumettreProjetPartenaire(
                2, "Festival du Désert 2027", "Recherche sponsors et mécènes culturels", true);

        assertNotNull(res);
        assertTrue(res.isRecherchePartenariat());
        assertEquals("Festival du Désert 2027", res.getTitreProjet());
        assertEquals("Recherche sponsors et mécènes culturels", res.getBesoinPartenariat());
        assertEquals(StatutModeration.EN_ATTENTE_VALIDATION, res.getStatutModeration());
        assertNull(res.getMotifRejet());
    }

    @Test
    @DisplayName("Validation admin valide le statut en VALIDE")
    void testValiderOpportunite() {
        promoteur.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        when(promoteurRepository.findById(2)).thenReturn(Optional.of(promoteur));
        when(promoteurRepository.save(any(PromoteurModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.validerOpportunite(2);

        assertNotNull(res);
        assertEquals(StatutModeration.VALIDE, res.getStatutModeration());
    }

    @Test
    @DisplayName("Rejet admin passe le statut en REJETE avec un motif explicite")
    void testRejeterOpportunite() {
        promoteur.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        when(promoteurRepository.findById(2)).thenReturn(Optional.of(promoteur));
        when(promoteurRepository.save(any(PromoteurModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.rejeterOpportunite(2, "Dossier incomplet, précisions budgétaires requises");

        assertNotNull(res);
        assertEquals(StatutModeration.REJETE, res.getStatutModeration());
        assertEquals("Dossier incomplet, précisions budgétaires requises", res.getMotifRejet());
    }

    @Test
    @DisplayName("Catalogue public B2B filtre par type et mot-clé")
    void testGetOpportunitesValideesAvecFiltres() {
        promoteur.setRecherchePartenariat(true);
        promoteur.setTitreProjet("Festival International de Musique");
        promoteur.setStatutModeration(StatutModeration.VALIDE);

        guide.setRecherchePartenariat(true);
        guide.setTitreProjet("Circuits Dogon et Tombouctou");
        guide.setStatutModeration(StatutModeration.VALIDE);

        when(promoteurRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE))
                .thenReturn(List.of(promoteur));
        when(guideRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE))
                .thenReturn(List.of(guide));

        // Recherche tous types avec mot-clé "festival"
        List<OpportuniteResponseDTO> filtrerFestival = opportuniteService.getOpportunitesValidees(null, "festival");
        assertEquals(1, filtrerFestival.size());
        assertEquals("PROMOTEUR", filtrerFestival.get(0).getTypePartenaire());

        // Filtrage spécifique par type GUIDE
        List<OpportuniteResponseDTO> guides = opportuniteService.getOpportunitesValidees("GUIDE", null);
        assertEquals(1, guides.size());
        assertEquals("GUIDE", guides.get(0).getTypePartenaire());
    }

    @Test
    @DisplayName("Admin peut lister les opportunités en attente de modération")
    void testGetOpportunitesEnAttente() {
        promoteur.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        when(promoteurRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(List.of(promoteur));
        when(guideRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(Collections.emptyList());

        List<OpportuniteResponseDTO> enAttente = opportuniteService.getOpportunitesEnAttente();
        assertEquals(1, enAttente.size());
        assertEquals("Fatoumata Traoré", enAttente.get(0).getNomComplet());
    }
}
