package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleSummaryDTO {

    private Long idArticle;
    private String nomArticle;
    private LocalDateTime datePublication;
    private Long vues;
}
