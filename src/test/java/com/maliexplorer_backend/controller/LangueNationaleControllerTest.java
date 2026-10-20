package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.LangueRequestDTO;
import com.maliexplorer_backend.dto.LangueResponseDTO;
import com.maliexplorer_backend.service.LangueNationaleService;
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
 * Tests unitaires pour le contrôleur REST LangueNationaleController.
 */
@ExtendWith(MockitoExtension.class)
class LangueNationaleControllerTest {

    @Mock
    private LangueNationaleService langueService;

    @InjectMocks
    private LangueNationaleController langueController;

    @Test
    @DisplayName("GET /api/langues retourne la liste des langues publiées avec HTTP 200")
    void testGetPublishedLangues() {
        LangueResponseDTO dto = LangueResponseDTO.builder()
                .id(1L)
                .nom("Bamanankan (Bambara)")
                .ordreAffichage(1)
                .publie(true)
                .build();

        when(langueService.getPublishedLangues()).thenReturn(List.of(dto));

        ResponseEntity<List<LangueResponseDTO>> response = langueController.getPublishedLangues();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Bamanankan (Bambara)", response.getBody().get(0).getNom());
        verify(langueService, times(1)).getPublishedLangues();
    }

    @Test
    @DisplayName("GET /api/langues/all retourne toutes les langues avec HTTP 200")
    void testGetAllLangues() {
        when(langueService.getAllLangues()).thenReturn(List.of());

        ResponseEntity<List<LangueResponseDTO>> response = langueController.getAllLangues();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(langueService, times(1)).getAllLangues();
    }

    @Test
    @DisplayName("GET /api/langues/{id} retourne le détail d'une langue")
    void testGetLangueById() {
        LangueResponseDTO dto = LangueResponseDTO.builder().id(1L).nom("Bamanankan").build();
        when(langueService.getLangueById(1L)).thenReturn(dto);

        ResponseEntity<LangueResponseDTO> response = langueController.getLangueById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("POST /api/langues crée une langue et renvoie HTTP 201 CREATED")
    void testCreateLangue() {
        LangueRequestDTO req = LangueRequestDTO.builder().nom("Dogonso (Dogon)").build();
        LangueResponseDTO res = LangueResponseDTO.builder().id(6L).nom("Dogonso (Dogon)").build();

        when(langueService.createLangue(req)).thenReturn(res);

        ResponseEntity<LangueResponseDTO> response = langueController.createLangue(req);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(6L, response.getBody().getId());
    }

    @Test
    @DisplayName("PUT /api/langues/{id} met à jour et renvoie HTTP 200 OK")
    void testUpdateLangue() {
        LangueRequestDTO req = LangueRequestDTO.builder().nom("Songhaï").build();
        LangueResponseDTO res = LangueResponseDTO.builder().id(3L).nom("Songhaï").build();

        when(langueService.updateLangue(3L, req)).thenReturn(res);

        ResponseEntity<LangueResponseDTO> response = langueController.updateLangue(3L, req);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Songhaï", response.getBody().getNom());
    }

    @Test
    @DisplayName("DELETE /api/langues/{id} supprime et renvoie HTTP 204 NO_CONTENT")
    void testDeleteLangue() {
        doNothing().when(langueService).deleteLangue(1L);

        ResponseEntity<Void> response = langueController.deleteLangue(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(langueService, times(1)).deleteLangue(1L);
    }

    @Test
    @DisplayName("PATCH /api/langues/{id}/toggle-publication bascule et renvoie HTTP 200 OK")
    void testTogglePublication() {
        LangueResponseDTO res = LangueResponseDTO.builder().id(1L).publie(false).build();
        when(langueService.togglePublication(1L)).thenReturn(res);

        ResponseEntity<LangueResponseDTO> response = langueController.togglePublication(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getPublie());
    }
}
