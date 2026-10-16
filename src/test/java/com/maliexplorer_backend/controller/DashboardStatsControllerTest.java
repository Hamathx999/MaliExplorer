package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.DashboardStatsResponseDTO;
import com.maliexplorer_backend.dto.KpisDTO;
import com.maliexplorer_backend.service.DashboardStatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour DashboardStatsController.
 * Vérifie le comportement du contrôleur REST et la transmission des données de statistiques.
 */
@ExtendWith(MockitoExtension.class)
class DashboardStatsControllerTest {

    @Mock
    private DashboardStatsService dashboardStatsService;

    @InjectMocks
    private DashboardStatsController dashboardStatsController;

    @Test
    @DisplayName("GET /api/admin/dashboard/stats retourne HTTP 200 OK avec le DTO complet")
    void testGetStats_Succes() {
        KpisDTO kpis = KpisDTO.builder()
                .contenusPublies(120)
                .enAttenteValidation(4)
                .utilisateursActifs(350)
                .partenaires(15)
                .evenementsAVenir(8)
                .build();

        DashboardStatsResponseDTO mockResponse = DashboardStatsResponseDTO.builder()
                .kpis(kpis)
                .build();

        when(dashboardStatsService.getDashboardStats()).thenReturn(mockResponse);

        ResponseEntity<DashboardStatsResponseDTO> response = dashboardStatsController.getStats();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(120, response.getBody().getKpis().getContenusPublies());
        assertEquals(4, response.getBody().getKpis().getEnAttenteValidation());
        verify(dashboardStatsService, times(1)).getDashboardStats();
    }
}
