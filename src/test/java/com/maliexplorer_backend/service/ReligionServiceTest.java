package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.ReligionRequestDTO;
import com.maliexplorer_backend.dto.ReligionResponseDTO;
import com.maliexplorer_backend.model.ReligionModel;
import com.maliexplorer_backend.repository.ReligionRepository;
import com.maliexplorer_backend.serviceimpl.ReligionServiceImpl;
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
 * Tests unitaires de la couche service ReligionServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class ReligionServiceTest {

    @Mock
    private ReligionRepository religionRepository;

    @InjectMocks
    private ReligionServiceImpl religionService;

    @Test
    @DisplayName("getPublishedReligions retourne uniquement les religions publiées")
    void testGetPublishedReligions() {
        ReligionModel r = ReligionModel.builder()
                .id(1L)
                .nom("Islam")
                .description("Religion musulmane de paix")
                .histoire("Présente depuis le IXe siècle")
                .proportionPopulation("94.8%")
                .publie(true)
                .ordre(1)
                .build();

        when(religionRepository.findByPublieTrueOrderByOrdreAscIdAsc()).thenReturn(List.of(r));

        List<ReligionResponseDTO> list = religionService.getPublishedReligions();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Islam", list.get(0).getNom());
        verify(religionRepository, times(1)).findByPublieTrueOrderByOrdreAscIdAsc();
    }

    @Test
    @DisplayName("createReligion persiste une nouvelle religion et renvoie le DTO")
    void testCreateReligion() {
        ReligionRequestDTO req = ReligionRequestDTO.builder()
                .nom("Animisme")
                .description("Traditions ancestrales du Mandé et du Pays Dogon")
                .proportionPopulation("2.0%")
                .publie(true)
                .build();

        ReligionModel saved = ReligionModel.builder()
                .id(3L)
                .nom("Animisme")
                .description("Traditions ancestrales du Mandé et du Pays Dogon")
                .proportionPopulation("2.0%")
                .publie(true)
                .build();

        when(religionRepository.save(any(ReligionModel.class))).thenReturn(saved);

        ReligionResponseDTO res = religionService.createReligion(req);

        assertNotNull(res);
        assertEquals(3L, res.getId());
        assertEquals("Animisme", res.getNom());
        verify(religionRepository, times(1)).save(any(ReligionModel.class));
    }

    @Test
    @DisplayName("togglePublication bascule l'état de publication d'une religion")
    void testTogglePublication() {
        ReligionModel r = ReligionModel.builder()
                .id(1L)
                .nom("Islam")
                .publie(true)
                .build();

        when(religionRepository.findById(1L)).thenReturn(Optional.of(r));
        when(religionRepository.save(any(ReligionModel.class))).thenAnswer(inv -> inv.getArgument(0));

        ReligionResponseDTO res = religionService.togglePublication(1L);

        assertNotNull(res);
        assertFalse(res.getPublie());
        verify(religionRepository, times(1)).save(r);
    }
}

