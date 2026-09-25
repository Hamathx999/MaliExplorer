package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmissionDTO {

    private Long userId;

    @NotNull(message = "L'ID du quiz est obligatoire")
    private Long quizId;

    // Map: idQuestion -> reponseChoisie
    @NotEmpty(message = "Les réponses sont obligatoires")
    private Map<Long, String> reponses;
}
