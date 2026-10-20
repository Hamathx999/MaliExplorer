package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.ReligionRequestDTO;
import com.maliexplorer_backend.dto.ReligionResponseDTO;
import com.maliexplorer_backend.service.ReligionService;
import com.maliexplorer_backend.serviceimpl.ReligionServiceImpl;
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
 * Tests unitaires pour le contrôleur REST des religions au Mali.
 */
@ExtendWith(MockitoExtension.class)
class ReligionControllerTest {

    @Mock
    private ReligionServiceImpl religionService;

    @InjectMocks
    private ReligionController religionController;

    @Test
    @DisplayName("GET /api/religions retourne la liste des religions publiées avec statut 200")
    void testGetPublishedReligions() {
        ReligionResponseDTO dto = ReligionResponseDTO.builder()
                .id(1L)
                .nom("Islam")
                .proportionPopulation("94.8%")
                .publie(true)
                .build();

        when(religionService.getPublishedReligions()).thenReturn(List.of(dto));

        ResponseEntity<List<ReligionResponseDTO>> response = religionController.getPublishedReligions();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Islam", response.getBody().get(0).getNom());
        verify(religionService, times(1)).getPublishedReligions();
    }

    @Test
    @DisplayName("GET /api/religions/all retourne toutes les religions pour l'administration")
    void testGetAllReligions() {
        when(religionService.getAllReligions()).thenReturn(List.of());

        ResponseEntity<List<ReligionResponseDTO>> response = religionController.getAllReligions();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(religionService, times(1)).getAllReligions();
    }

    @Test
    @DisplayName("POST /api/religions crée une religion avec statut 201")
    void testCreateReligion() {
        ReligionRequestDTO req = ReligionRequestDTO.builder()
                .nom("Christianisme")
                .description("Communauté chrétienne")
                .proportionPopulation("2.4%")
                .build();

        ReligionResponseDTO res = ReligionResponseDTO.builder()
                .id(2L)
                .nom("Christianisme")
                .description("Communauté chrétienne")
                .proportionPopulation("2.4%")
                .build();

        when(religionService.createReligion(req)).thenReturn(res);

        ResponseEntity<ReligionResponseDTO> response = religionController.createReligion(req);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Christianisme", response.getBody().getNom());
        verify(religionService, times(1)).createReligion(req);
    }

    @Test
    @DisplayName("PATCH /api/religions/{id}/toggle-publication bascule l'état de publication")
    void testTogglePublication() {
        ReligionResponseDTO res = ReligionResponseDTO.builder()
                .id(1L)
                .nom("Islam")
                .publie(false)
                .build();

        when(religionService.togglePublication(1L)).thenReturn(res);

        ResponseEntity<ReligionResponseDTO> response = religionController.togglePublication(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getPublie());
        verify(religionService, times(1)).togglePublication(1L);
    }
}

