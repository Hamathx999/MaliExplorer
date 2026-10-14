package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.HomeSlideRequestDTO;
import com.maliexplorer_backend.dto.HomeSlideResponseDTO;

import java.util.List;

public interface HomeSlideService {
    List<HomeSlideResponseDTO> getActiveSlides();
    List<HomeSlideResponseDTO> getAllSlides();
    HomeSlideResponseDTO getSlideById(Long id);
    HomeSlideResponseDTO createSlide(HomeSlideRequestDTO requestDTO);
    HomeSlideResponseDTO updateSlide(Long id, HomeSlideRequestDTO requestDTO);
    void deleteSlide(Long id);
    void reorderSlides(List<Long> slideIds);
}

