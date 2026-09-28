package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.RegionRequestDTO;
import com.maliexplorer_backend.dto.RegionResponseDTO;
import com.maliexplorer_backend.model.RegionModel;
import com.maliexplorer_backend.repository.RegionRepository;
import com.maliexplorer_backend.serviceimpl.RegionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionServiceImpl regionService;

    private RegionModel testRegion;

    @BeforeEach
    void setUp() {
        testRegion = RegionModel.builder()
                .idRegion(1L)
                .nomRegion("Bamako")
                .nbreHbt(2500000L)
                .villes(new ArrayList<>())
                .ethnies(new ArrayList<>())
                .plats(new ArrayList<>())
                .build();
    }

    @Test
    void testGetAllRegions() {
        when(regionRepository.findAll()).thenReturn(List.of(testRegion));

        List<RegionResponseDTO> result = regionService.getAllRegions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bamako", result.get(0).getNom());
        verify(regionRepository, times(1)).findAll();
    }

    @Test
    void testGetRegionById() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(testRegion));

        RegionResponseDTO result = regionService.getRegionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bamako", result.getNom());
        assertEquals(2500000L, result.getNbreHbt());
    }

    @Test
    void testCreateRegion() {
        RegionRequestDTO request = RegionRequestDTO.builder()
                .nom("Sikasso")
                .nbreHbt(2600000L)
                .build();

        RegionModel savedRegion = RegionModel.builder()
                .idRegion(2L)
                .nomRegion("Sikasso")
                .nbreHbt(2600000L)
                .villes(new ArrayList<>())
                .ethnies(new ArrayList<>())
                .plats(new ArrayList<>())
                .build();

        when(regionRepository.existsByNomRegionIgnoreCase("Sikasso")).thenReturn(false);
        when(regionRepository.save(any(RegionModel.class))).thenReturn(savedRegion);

        RegionResponseDTO created = regionService.createRegion(request);

        assertNotNull(created);
        assertEquals(2L, created.getId());
        assertEquals("Sikasso", created.getNom());
        verify(regionRepository, times(1)).save(any(RegionModel.class));
    }
}
