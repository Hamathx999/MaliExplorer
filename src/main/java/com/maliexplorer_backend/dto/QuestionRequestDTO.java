package com.maliexplorer_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequestDTO {

    @NotBlank(message = "L'intitulé de la question est obligatoire")
    private String nomQuestion;

    @NotBlank(message = "La réponse correcte est obligatoire")
    private String reponse;

    @NotNull(message = "Le nombre de points est obligatoire")
    private Integer duree;

    @NotEmpty(message = "Au moins une proposition de réponse est requise")
    private Long quizId;
}
