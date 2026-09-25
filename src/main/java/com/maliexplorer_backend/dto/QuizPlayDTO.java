package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizPlayDTO {

    private Long idQuiz;
    private String nomQuiz;
    private String description;
    private Integer point;
    private String niveauDifficulte;
    private String imageUrl;
    private String categorie;
    private List<QuestionPlayDTO> questions;
}
