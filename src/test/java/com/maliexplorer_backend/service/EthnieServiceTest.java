package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.EthnieResponseDTO;
import com.maliexplorer_backend.Models.Ethnie;
import com.maliexplorer_backend.Repository.EthnieRepository;
import com.maliexplorer_backend.Repository.RegionRepository;
import com.maliexplorer_backend.serviceImpl.EthnieServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EthnieServiceTest {

    @Mock
    private EthnieRepository ethnieRepository;

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private EthnieServiceImpl ethnieService;

    @Test
    void testGetAllEthnies() {
        Ethnie ethnie = Ethnie.builder()
                .idEthnie(1L)
                .nomEthnie("Bambara")
                .langue("Bamanankan")
                .regions(new ArrayList<>())
                .plats(new ArrayList<>())
                .build();

        when(ethnieRepository.findAll()).thenReturn(List.of(ethnie));

        List<EthnieResponseDTO> ethnies = ethnieService.getAllEthnies();

        assertNotNull(ethnies);
        assertEquals(1, ethnies.size());
        assertEquals("Bambara", ethnies.get(0).getNom());
    }
}
