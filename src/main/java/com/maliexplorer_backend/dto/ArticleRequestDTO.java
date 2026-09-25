package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleRequestDTO {

    @NotBlank(message = "Le titre de l'article est obligatoire")
    private String nomArticle;

    @NotBlank(message = "Le contenu de l'article est obligatoire")
    private String contenu;

    private String auteur;
    private String categorie;
    private String imageUrl;
    private Long idUsers;
}
