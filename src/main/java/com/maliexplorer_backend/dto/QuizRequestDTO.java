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
public class QuizRequestDTO {

    @com.fasterxml.jackson.annotation.JsonAlias({"titre", "nom", "nomQuiz"})
    @NotBlank(message = "Le titre du quiz est obligatoire")
    private String nomQuiz;

    private String description;

    @com.fasterxml.jackson.annotation.JsonAlias({"imageQuiz", "imageUrl", "logoUrl", "logo", "image"})
    private String imageQuiz;

    private String categorie;

    @com.fasterxml.jackson.annotation.JsonAlias({"points", "point"})
    private Integer point;
}
