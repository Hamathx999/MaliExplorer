package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.GuideResponseDTO;
import com.maliexplorer_backend.model.GuideModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.StatutModeration;
import com.maliexplorer_backend.repository.GuideRepository;
import com.maliexplorer_backend.serviceimpl.GuideServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuideServiceTest {

    @Mock
    private GuideRepository guideRepository;

    @InjectMocks
    private GuideServiceImpl guideService;

    private GuideModel guideValide;
    private GuideModel guideEnAttente;

    @BeforeEach
    void setUp() {
        guideValide = new GuideModel();
        guideValide.setIdUsers(101);
        guideValide.setPrenom("Boubacar");
        guideValide.setNom("Cissé");
        guideValide.setEmail("boubacar.cisse@guide.ml");
        guideValide.setLangue("Français, Bambara");
        guideValide.setRole(RoleModel.guide);
        guideValide.setStatutModeration(StatutModeration.VALIDE);

        guideEnAttente = new GuideModel();
        guideEnAttente.setIdUsers(102);
        guideEnAttente.setPrenom("Aminata");
        guideEnAttente.setNom("Diallo");
        guideEnAttente.setEmail("aminata.diallo@guide.ml");
        guideEnAttente.setLangue("Français");
        guideEnAttente.setRole(RoleModel.guide);
        guideEnAttente.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
    }

    @Test
    @DisplayName("obtenirTousLesGuides doit filtrer et ne retourner que les guides au statut VALIDE")
    void testObtenirTousLesGuides_NeRetourneQueLesGuidesValides() {
        when(guideRepository.findByStatutModeration(StatutModeration.VALIDE))
                .thenReturn(List.of(guideValide));

        List<GuideResponseDTO> result = guideService.obtenirTousLesGuides();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(101, result.get(0).getIdUsers());
        assertEquals("Boubacar", result.get(0).getPrenom());
        assertEquals(StatutModeration.VALIDE, result.get(0).getStatutModeration());
        verify(guideRepository, times(1)).findByStatutModeration(StatutModeration.VALIDE);
    }

    @Test
    @DisplayName("obtenirTousLesGuides doit retourner une liste vide si aucun guide validé n'existe")
    void testObtenirTousLesGuides_RetourneListeVideSiAucunGuideValide() {
        when(guideRepository.findByStatutModeration(StatutModeration.VALIDE))
                .thenReturn(Collections.emptyList());

        List<GuideResponseDTO> result = guideService.obtenirTousLesGuides();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(guideRepository, times(1)).findByStatutModeration(StatutModeration.VALIDE);
    }

    @Test
    @DisplayName("rechercherParLangue doit exclure les guides non validés même si la langue correspond")
    void testRechercherParLangue_ExclutGuidesNonValides() {
        when(guideRepository.findByLangueContainingIgnoreCase("Français"))
                .thenReturn(List.of(guideValide, guideEnAttente));

        List<GuideResponseDTO> result = guideService.rechercherParLangue("Français");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(101, result.get(0).getIdUsers());
        assertEquals("Boubacar", result.get(0).getPrenom());
    }
}
