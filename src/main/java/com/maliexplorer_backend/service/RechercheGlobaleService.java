package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.SearchResultItemDTO;

import java.util.List;

public interface RechercheGlobaleService {
    List<SearchResultItemDTO> rechercher(String query);
}

