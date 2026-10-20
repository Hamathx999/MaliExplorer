package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.InfoPratiqueRequestDTO;
import com.maliexplorer_backend.dto.InfoPratiqueResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.InfoPratiqueModel;
import com.maliexplorer_backend.repository.InfoPratiqueRepository;
import com.maliexplorer_backend.serviceimpl.InfoPratiqueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le service InfoPratiqueService.
 */
@ExtendWith(MockitoExtension.class)
class InfoPratiqueServiceTest {

    @Mock
    private InfoPratiqueRepository infoPratiqueRepository;

    @InjectMocks
    private InfoPratiqueServiceImpl infoPratiqueService;

    private InfoPratiqueModel visa;
    private InfoPratiqueModel sante;

    @BeforeEach
    void setUp() {
        visa = InfoPratiqueModel.builder()
                .id(1L)
                .titre("1. Visas & Formalités d'Entrée")
                .categorie("VISAS")
                .badge("Obligatoire")
                .badgeColor("#C53030")
                .icon("badge")
                .description("Formalités de visa")
                .conseils("Passeport valide requis")
                .ordreAffichage(1)
                .publie(true)
                .build();

        sante = InfoPratiqueModel.builder()
                .id(2L)
                .titre("2. Santé & Vaccinations")
                .categorie("SANTE")
                .badge("Exigé")
                .badgeColor("#DD6B20")
                .icon("medical_services")
                .description("Vaccination fièvre jaune")
                .ordreAffichage(2)
                .publie(true)
                .build();
    }

    @Test
    @DisplayName("getPublishedInfosPratiques retourne la liste des fiches pratiques publiées")
    void testGetPublishedInfosPratiques() {
        when(infoPratiqueRepository.findByPublieTrueOrderByOrdreAffichageAsc()).thenReturn(List.of(visa, sante));

        List<InfoPratiqueResponseDTO> result = infoPratiqueService.getPublishedInfosPratiques();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("VISAS", result.get(0).getCategorie());
        assertEquals("SANTE", result.get(1).getCategorie());
        verify(infoPratiqueRepository, times(1)).findByPublieTrueOrderByOrdreAffichageAsc();
    }

    @Test
    @DisplayName("getInfosPratiquesByCategorie filtre correctement par catégorie")
    void testGetInfosPratiquesByCategorie() {
        when(infoPratiqueRepository.findByCategorieIgnoreCaseOrderByOrdreAffichageAsc("VISAS"))
                .thenReturn(List.of(visa));

        List<InfoPratiqueResponseDTO> result = infoPratiqueService.getInfosPratiquesByCategorie("VISAS");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1. Visas & Formalités d'Entrée", result.get(0).getTitre());
    }

    @Test
    @DisplayName("getInfoPratiqueById retourne la fiche si présente")
    void testGetInfoPratiqueById_Succes() {
        when(infoPratiqueRepository.findById(1L)).thenReturn(Optional.of(visa));

        InfoPratiqueResponseDTO result = infoPratiqueService.getInfoPratiqueById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("VISAS", result.getCategorie());
    }

    @Test
    @DisplayName("getInfoPratiqueById lève ResourceNotFoundException si inconnue")
    void testGetInfoPratiqueById_NotFound() {
        when(infoPratiqueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> infoPratiqueService.getInfoPratiqueById(999L));
    }

    @Test
    @DisplayName("createInfoPratique enregistre et convertit en majuscule la catégorie")
    void testCreateInfoPratique_Succes() {
        InfoPratiqueRequestDTO req = InfoPratiqueRequestDTO.builder()
                .titre("3. Devises")
                .categorie("devises")
                .badge("FCFA")
                .build();

        InfoPratiqueModel saved = InfoPratiqueModel.builder()
                .id(3L)
                .titre("3. Devises")
                .categorie("DEVISES")
                .badge("FCFA")
                .publie(true)
                .build();

        when(infoPratiqueRepository.save(any(InfoPratiqueModel.class))).thenReturn(saved);

        InfoPratiqueResponseDTO result = infoPratiqueService.createInfoPratique(req);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("DEVISES", result.getCategorie());
    }

    @Test
    @DisplayName("updateInfoPratique modifie les champs de la fiche existante")
    void testUpdateInfoPratique_Succes() {
        InfoPratiqueRequestDTO req = InfoPratiqueRequestDTO.builder()
                .titre("1. Visas & Titres de Séjour")
                .badgeColor("#E53E3E")
                .build();

        when(infoPratiqueRepository.findById(1L)).thenReturn(Optional.of(visa));
        when(infoPratiqueRepository.save(any(InfoPratiqueModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InfoPratiqueResponseDTO result = infoPratiqueService.updateInfoPratique(1L, req);

        assertNotNull(result);
        assertEquals("1. Visas & Titres de Séjour", result.getTitre());
        assertEquals("#E53E3E", result.getBadgeColor());
    }

    @Test
    @DisplayName("deleteInfoPratique supprime la fiche si existante")
    void testDeleteInfoPratique_Succes() {
        when(infoPratiqueRepository.existsById(1L)).thenReturn(true);
        doNothing().when(infoPratiqueRepository).deleteById(1L);

        assertDoesNotThrow(() -> infoPratiqueService.deleteInfoPratique(1L));
        verify(infoPratiqueRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("togglePublication bascule le statut de publication")
    void testTogglePublication() {
        when(infoPratiqueRepository.findById(1L)).thenReturn(Optional.of(visa));
        when(infoPratiqueRepository.save(any(InfoPratiqueModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InfoPratiqueResponseDTO result = infoPratiqueService.togglePublication(1L);

        assertNotNull(result);
        assertFalse(result.getPublie());
    }
}
