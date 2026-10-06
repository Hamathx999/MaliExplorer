package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
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
    @JsonAlias({"question", "nom"})
    private String nomQuestion;

    @NotBlank(message = "La réponse correcte est obligatoire")
    private String reponse;

    @Builder.Default
    private Integer duree = 30;

    @Builder.Default
    private Integer points = 10;

    @JsonAlias("theme")
    private String theme;

    private Long quizId;

    private String explication;

    private List<String> propositions;
}
