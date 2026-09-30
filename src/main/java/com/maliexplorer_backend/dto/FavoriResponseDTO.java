package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.TypeFavori;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavoriResponseDTO {

    private Long idFavori;
    private Integer idUsers;
    private String userEmail;
    private TypeFavori typeContenu;
    private LocalDateTime dateAjout;
    private LieuHistoriqueResponseDTO lieu;
    private ArticleResponseDTO article;
    private String message;
}
