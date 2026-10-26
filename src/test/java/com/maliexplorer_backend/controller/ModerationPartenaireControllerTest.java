package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.OpportuniteResponseDTO;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.StatutModeration;
import com.maliexplorer_backend.service.OpportuniteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModerationPartenaireControllerTest {

    @Mock
    private OpportuniteService opportuniteService;

    @InjectMocks
    private ModerationPartenaireController moderationController;

    private OpportuniteResponseDTO dtoPromoteur;
    private OpportuniteResponseDTO dtoGuide;

    @BeforeEach
    void setUp() {
        dtoPromoteur = OpportuniteResponseDTO.builder()
                .idUsers(2)
                .nomComplet("Fatoumata Traoré")
                .role(RoleModel.promoteur)
                .typePartenaire("PROMOTEUR")
                .nomOrganisation("Festival du Désert")
                .statutModeration(StatutModeration.EN_ATTENTE_VALIDATION)
                .build();

        dtoGuide = OpportuniteResponseDTO.builder()
                .idUsers(3)
                .nomComplet("Ousmane Sissoko")
                .role(RoleModel.guide)
                .typePartenaire("GUIDE")
                .specialiteOuOrganisation("Langue : Bambara, Français")
                .statutModeration(StatutModeration.EN_ATTENTE_VALIDATION)
                .build();
    }

    @Test
    @DisplayName("GET /api/admin/moderation/opportunites sans paramètre retourne les dossiers en attente")
    void testGetOpportunites_SansParamStatut() {
        when(opportuniteService.getOpportunitesParStatut(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(List.of(dtoPromoteur, dtoGuide));

        ResponseEntity<List<OpportuniteResponseDTO>> response = moderationController.getOpportunites(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(opportuniteService, times(1)).getOpportunitesParStatut(StatutModeration.EN_ATTENTE_VALIDATION);
    }

    @Test
    @DisplayName("GET /api/admin/moderation/opportunites?statut=VALIDE retourne les dossiers validés")
    void testGetOpportunites_AvecStatutValide() {
        dtoPromoteur.setStatutModeration(StatutModeration.VALIDE);
        when(opportuniteService.getOpportunitesParStatut(StatutModeration.VALIDE)).thenReturn(List.of(dtoPromoteur));

        ResponseEntity<List<OpportuniteResponseDTO>> response = moderationController.getOpportunites("VALIDE");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(StatutModeration.VALIDE, response.getBody().get(0).getStatutModeration());
        verify(opportuniteService, times(1)).getOpportunitesParStatut(StatutModeration.VALIDE);
    }

    @Test
    @DisplayName("GET /api/admin/moderation/opportunites avec statut normalisé EN_ATTENTE")
    void testGetOpportunites_AvecStatutEnAttente() {
        when(opportuniteService.getOpportunitesParStatut(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(List.of(dtoPromoteur, dtoGuide));

        ResponseEntity<List<OpportuniteResponseDTO>> response = moderationController.getOpportunites("EN_ATTENTE");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(opportuniteService, times(1)).getOpportunitesParStatut(StatutModeration.EN_ATTENTE_VALIDATION);
    }

    @Test
    @DisplayName("PATCH /api/admin/moderation/opportunites/{idUsers}/valider valide le partenaire et renvoie 200 OK")
    void testValiderPartenaire() {
        dtoPromoteur.setStatutModeration(StatutModeration.VALIDE);
        when(opportuniteService.validerOpportunite(2)).thenReturn(dtoPromoteur);

        ResponseEntity<OpportuniteResponseDTO> response = moderationController.valider(2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(StatutModeration.VALIDE, response.getBody().getStatutModeration());
        verify(opportuniteService, times(1)).validerOpportunite(2);
    }

    @Test
    @DisplayName("PATCH /api/admin/moderation/opportunites/{idUsers}/rejeter accepte le motif via RequestParam")
    void testRejeterPartenaire_RequestParam() {
        dtoPromoteur.setStatutModeration(StatutModeration.REJETE);
        dtoPromoteur.setMotifRejet("Dossier incomplet");
        when(opportuniteService.rejeterOpportunite(2, "Dossier incomplet")).thenReturn(dtoPromoteur);

        ResponseEntity<OpportuniteResponseDTO> response = moderationController.rejeter(2, "Dossier incomplet", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(StatutModeration.REJETE, response.getBody().getStatutModeration());
        assertEquals("Dossier incomplet", response.getBody().getMotifRejet());
        verify(opportuniteService, times(1)).rejeterOpportunite(2, "Dossier incomplet");
    }

    @Test
    @DisplayName("PATCH /api/admin/moderation/opportunites/{idUsers}/rejeter accepte le motif via RequestBody JSON")
    void testRejeterPartenaire_RequestBody() {
        dtoPromoteur.setStatutModeration(StatutModeration.REJETE);
        dtoPromoteur.setMotifRejet("Pièce d'identité illisible");
        when(opportuniteService.rejeterOpportunite(2, "Pièce d'identité illisible")).thenReturn(dtoPromoteur);

        ResponseEntity<OpportuniteResponseDTO> response = moderationController.rejeter(2, null, Map.of("motif", "Pièce d'identité illisible"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(StatutModeration.REJETE, response.getBody().getStatutModeration());
        assertEquals("Pièce d'identité illisible", response.getBody().getMotifRejet());
        verify(opportuniteService, times(1)).rejeterOpportunite(2, "Pièce d'identité illisible");
    }
}
