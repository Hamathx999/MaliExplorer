package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.MarqueurCarteDTO;

import java.util.List;

public interface CarteService {

    List<MarqueurCarteDTO> getAllMarqueurs();

    List<MarqueurCarteDTO> getMarqueursByType(String type);

    List<MarqueurCarteDTO> getMarqueursWith360();
}
