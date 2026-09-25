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
public class QuestionResponseDTO {

    private Long idQuestion;
    private String nomQuestion;
    private String reponse;
    private String explication;
    private Integer point;
    private Integer duree;
    private List<String> propositions;
    private Long quizId;
}
