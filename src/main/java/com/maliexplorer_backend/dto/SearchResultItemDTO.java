package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultItemDTO {
    private String id;
    private String titre;
    private String sousTitre;
    private String categorie;
    private String description;
    private String imageUrl;
    private String route;
    private Object payload;
}

