package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.LangueRequestDTO;
import com.maliexplorer_backend.dto.LangueResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.LangueNationaleModel;
import com.maliexplorer_backend.repository.LangueNationaleRepository;
import com.maliexplorer_backend.serviceimpl.LangueNationaleServiceImpl;
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

/**
 * Tests unitaires pour le service de gestion des langues nationales du Mali.
 */
@ExtendWith(MockitoExtension.class)
class LangueNationaleServiceTest {

    @Mock
    private LangueNationaleRepository langueRepository;

    @InjectMocks
    private LangueNationaleServiceImpl langueService;

    private LangueNationaleModel bambara;
    private LangueNationaleModel peulBrouillon;

    @BeforeEach
    void setUp() {
        bambara = LangueNationaleModel.builder()
                .id(1L)
                .nom("Bamanankan (Bambara)")
                .nomLocal("Bamanankan")
                .locuteurs("~80% de la population")
                .regions("Bamako, Ségou")
                .description("Langue mandée principale")
                .ordreAffichage(1)
                .publie(true)
                .lexiqueJson("[{\"mot\":\"I ni sogoma\",\"traduction\":\"Bonjour\"}]")
                .build();

        peulBrouillon = LangueNationaleModel.builder()
                .id(2L)
                .nom("Fulfulde (Peul)")
                .nomLocal("Pulaar")
                .locuteurs("~15% de la population")
                .regions("Mopti")
                .description("Langue peule")
                .ordreAffichage(2)
                .publie(false)
                .build();
    }

    @Test
    @DisplayName("getPublishedLangues retourne uniquement les langues publiées triées")
    void testGetPublishedLangues() {
        when(langueRepository.findByPublieTrueOrderByOrdreAffichageAsc()).thenReturn(List.of(bambara));

        List<LangueResponseDTO> result = langueService.getPublishedLangues();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bamanankan (Bambara)", result.get(0).getNom());
        assertEquals("Bamanankan", result.get(0).getNomLocal());
        assertTrue(result.get(0).getPublie());
        verify(langueRepository, times(1)).findByPublieTrueOrderByOrdreAffichageAsc();
    }

    @Test
    @DisplayName("getAllLangues retourne toutes les langues y compris les brouillons")
    void testGetAllLangues() {
        when(langueRepository.findAllByOrderByOrdreAffichageAsc()).thenReturn(List.of(bambara, peulBrouillon));

        List<LangueResponseDTO> result = langueService.getAllLangues();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertFalse(result.get(1).getPublie());
        verify(langueRepository, times(1)).findAllByOrderByOrdreAffichageAsc();
    }

    @Test
    @DisplayName("getLangueById retourne la langue demandée si elle existe")
    void testGetLangueById_Succes() {
        when(langueRepository.findById(1L)).thenReturn(Optional.of(bambara));

        LangueResponseDTO result = langueService.getLangueById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bamanankan (Bambara)", result.getNom());
    }

    @Test
    @DisplayName("getLangueById lève ResourceNotFoundException si l'identifiant n'existe pas")
    void testGetLangueById_NotFound() {
        when(langueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> langueService.getLangueById(999L));
    }

    @Test
    @DisplayName("createLangue enregistre et retourne la nouvelle langue nationale")
    void testCreateLangue_Succes() {
        LangueRequestDTO req = LangueRequestDTO.builder()
                .nom("Soninké (Maraka)")
                .nomLocal("Sooninkanxanne")
                .locuteurs("~7% de la population")
                .regions("Kayes, Nara")
                .description("Langue de l'Empire du Ghana")
                .ordreAffichage(4)
                .publie(true)
                .build();

        LangueNationaleModel saved = LangueNationaleModel.builder()
                .id(3L)
                .nom("Soninké (Maraka)")
                .nomLocal("Sooninkanxanne")
                .locuteurs("~7% de la population")
                .regions("Kayes, Nara")
                .description("Langue de l'Empire du Ghana")
                .ordreAffichage(4)
                .publie(true)
                .build();

        when(langueRepository.save(any(LangueNationaleModel.class))).thenReturn(saved);

        LangueResponseDTO result = langueService.createLangue(req);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Soninké (Maraka)", result.getNom());
        verify(langueRepository, times(1)).save(any(LangueNationaleModel.class));
    }

    @Test
    @DisplayName("updateLangue modifie les champs de la langue existante")
    void testUpdateLangue_Succes() {
        LangueRequestDTO updateReq = LangueRequestDTO.builder()
                .nom("Bamanankan Moderne")
                .description("Nouvelle description mise à jour")
                .ordreAffichage(10)
                .build();

        when(langueRepository.findById(1L)).thenReturn(Optional.of(bambara));
        when(langueRepository.save(any(LangueNationaleModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LangueResponseDTO result = langueService.updateLangue(1L, updateReq);

        assertNotNull(result);
        assertEquals("Bamanankan Moderne", result.getNom());
        assertEquals("Nouvelle description mise à jour", result.getDescription());
        assertEquals(10, result.getOrdreAffichage());
    }

    @Test
    @DisplayName("deleteLangue supprime la langue si elle existe")
    void testDeleteLangue_Succes() {
        when(langueRepository.existsById(1L)).thenReturn(true);
        doNothing().when(langueRepository).deleteById(1L);

        assertDoesNotThrow(() -> langueService.deleteLangue(1L));
        verify(langueRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteLangue lève une exception si l'id n'existe pas")
    void testDeleteLangue_NotFound() {
        when(langueRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> langueService.deleteLangue(999L));
        verify(langueRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("togglePublication bascule le statut publie d'une langue")
    void testTogglePublication() {
        when(langueRepository.findById(1L)).thenReturn(Optional.of(bambara));
        when(langueRepository.save(any(LangueNationaleModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LangueResponseDTO result = langueService.togglePublication(1L);

        assertNotNull(result);
        assertFalse(result.getPublie());
    }
}
