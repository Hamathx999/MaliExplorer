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
public class QuizResultDTO {

    private Long quizId;
    private String nomQuiz;
    private Integer scoreTotalObtenu;
    private Integer scoreMaxPossible;
    private Double pourcentage;
    private Boolean reussi;
    private List<QuestionResultDetailDTO> detailsQuestions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionResultDetailDTO {
        private Long idQuestion;
        private String nomQuestion;
        private String reponseSoumise;
        private String bonneReponse;
        private Boolean estCorrect;
        private Integer pointsGagnes;
        private String explication;
    }
}
