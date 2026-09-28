package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.VilleResponseDTO;
import com.maliexplorer_backend.model.VilleModel;
import com.maliexplorer_backend.repository.RegionRepository;
import com.maliexplorer_backend.repository.VilleRepository;
import com.maliexplorer_backend.serviceimpl.VilleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VilleServiceTest {

    @Mock
    private VilleRepository villeRepository;

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private VilleServiceImpl villeService;

    @Test
    void testGetVilleById() {
        VilleModel ville = VilleModel.builder()
                .idVille(1L)
                .nomVille("Tombouctou")
                .region("Tombouctou")
                .cordonnees("16.7666,-3.0026")
                .build();

        when(villeRepository.findById(1L)).thenReturn(Optional.of(ville));

        VilleResponseDTO response = villeService.getVilleById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Tombouctou", response.getNom());
        assertEquals("16.7666,-3.0026", response.getCordonnees());
    }

    @Test
    void testSearchVilles() {
        VilleModel ville = VilleModel.builder()
                .idVille(2L)
                .nomVille("Djenné")
                .build();

        when(villeRepository.findByNomVilleContainingIgnoreCase("Djen")).thenReturn(List.of(ville));

        List<VilleResponseDTO> results = villeService.searchVilles("Djen");

        assertEquals(1, results.size());
        assertEquals("Djenné", results.get(0).getNom());
    }
}
