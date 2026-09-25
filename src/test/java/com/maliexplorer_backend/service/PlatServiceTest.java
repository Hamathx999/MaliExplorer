package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.PlatResponseDTO;
import com.maliexplorer_backend.model.Plat;
import com.maliexplorer_backend.repository.EthnieRepository;
import com.maliexplorer_backend.repository.PlatRepository;
import com.maliexplorer_backend.repository.RegionRepository;
import com.maliexplorer_backend.serviceImpl.PlatServiceImpl;
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
class PlatServiceTest {

    @Mock
    private PlatRepository platRepository;

    @Mock
    private EthnieRepository ethnieRepository;

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private PlatServiceImpl platService;

    @Test
    void testGetAllPlats() {
        Plat plat = Plat.builder()
                .idPlat(1L)
                .nomPlat("Tigalèguèna")
                .nomAlternatif("Mafé")
                .ingredients("Pâte d'arachide, viande, légumes")
                .regions(new ArrayList<>())
                .ethnies(new ArrayList<>())
                .build();

        when(platRepository.findAll()).thenReturn(List.of(plat));

        List<PlatResponseDTO> plats = platService.getAllPlats();

        assertNotNull(plats);
        assertEquals(1, plats.size());
        assertEquals("Tigalèguèna", plats.get(0).getNom());
    }
}
