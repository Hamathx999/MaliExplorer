package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.InfoPratiqueRequestDTO;
import com.maliexplorer_backend.dto.InfoPratiqueResponseDTO;
import com.maliexplorer_backend.service.InfoPratiqueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le contrôleur REST InfoPratiqueController.
 */
@ExtendWith(MockitoExtension.class)
class InfoPratiqueControllerTest {

    @Mock
    private InfoPratiqueService infoPratiqueService;

    @InjectMocks
    private InfoPratiqueController infoPratiqueController;

    @Test
    @DisplayName("GET /api/infos-pratiques retourne les fiches publiées avec HTTP 200 OK")
    void testGetPublishedInfosPratiques() {
        InfoPratiqueResponseDTO dto = InfoPratiqueResponseDTO.builder()
                .id(1L)
                .titre("1. Visas")
                .categorie("VISAS")
                .publie(true)
                .build();

        when(infoPratiqueService.getPublishedInfosPratiques()).thenReturn(List.of(dto));

        ResponseEntity<List<InfoPratiqueResponseDTO>> response = infoPratiqueController.getPublishedInfosPratiques();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("VISAS", response.getBody().get(0).getCategorie());
    }

    @Test
    @DisplayName("GET /api/infos-pratiques/all retourne toutes les fiches avec HTTP 200 OK")
    void testGetAllInfosPratiques() {
        when(infoPratiqueService.getAllInfosPratiques()).thenReturn(List.of());

        ResponseEntity<List<InfoPratiqueResponseDTO>> response = infoPratiqueController.getAllInfosPratiques();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(infoPratiqueService, times(1)).getAllInfosPratiques();
    }

    @Test
    @DisplayName("GET /api/infos-pratiques/categorie/{categorie} filtre par catégorie avec HTTP 200 OK")
    void testGetInfosPratiquesByCategorie() {
        when(infoPratiqueService.getInfosPratiquesByCategorie("SANTE")).thenReturn(List.of());

        ResponseEntity<List<InfoPratiqueResponseDTO>> response = infoPratiqueController.getInfosPratiquesByCategorie("SANTE");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(infoPratiqueService, times(1)).getInfosPratiquesByCategorie("SANTE");
    }

    @Test
    @DisplayName("GET /api/infos-pratiques/{id} retourne le détail d'une fiche")
    void testGetInfoPratiqueById() {
        InfoPratiqueResponseDTO dto = InfoPratiqueResponseDTO.builder().id(2L).titre("2. Santé").build();
        when(infoPratiqueService.getInfoPratiqueById(2L)).thenReturn(dto);

        ResponseEntity<InfoPratiqueResponseDTO> response = infoPratiqueController.getInfoPratiqueById(2L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2L, response.getBody().getId());
    }

    @Test
    @DisplayName("POST /api/infos-pratiques crée une fiche et retourne HTTP 201 CREATED")
    void testCreateInfoPratique() {
        InfoPratiqueRequestDTO req = InfoPratiqueRequestDTO.builder().titre("Devises").categorie("DEVISES").build();
        InfoPratiqueResponseDTO res = InfoPratiqueResponseDTO.builder().id(3L).titre("Devises").categorie("DEVISES").build();

        when(infoPratiqueService.createInfoPratique(req)).thenReturn(res);

        ResponseEntity<InfoPratiqueResponseDTO> response = infoPratiqueController.createInfoPratique(req);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(3L, response.getBody().getId());
    }

    @Test
    @DisplayName("PUT /api/infos-pratiques/{id} met à jour et retourne HTTP 200 OK")
    void testUpdateInfoPratique() {
        InfoPratiqueRequestDTO req = InfoPratiqueRequestDTO.builder().titre("Climat modifié").build();
        InfoPratiqueResponseDTO res = InfoPratiqueResponseDTO.builder().id(4L).titre("Climat modifié").build();

        when(infoPratiqueService.updateInfoPratique(4L, req)).thenReturn(res);

        ResponseEntity<InfoPratiqueResponseDTO> response = infoPratiqueController.updateInfoPratique(4L, req);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Climat modifié", response.getBody().getTitre());
    }

    @Test
    @DisplayName("DELETE /api/infos-pratiques/{id} supprime et retourne HTTP 204 NO_CONTENT")
    void testDeleteInfoPratique() {
        doNothing().when(infoPratiqueService).deleteInfoPratique(1L);

        ResponseEntity<Void> response = infoPratiqueController.deleteInfoPratique(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(infoPratiqueService, times(1)).deleteInfoPratique(1L);
    }

    @Test
    @DisplayName("PATCH /api/infos-pratiques/{id}/toggle-publication bascule et retourne HTTP 200 OK")
    void testTogglePublication() {
        InfoPratiqueResponseDTO res = InfoPratiqueResponseDTO.builder().id(1L).publie(false).build();
        when(infoPratiqueService.togglePublication(1L)).thenReturn(res);

        ResponseEntity<InfoPratiqueResponseDTO> response = infoPratiqueController.togglePublication(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getPublie());
    }
}
