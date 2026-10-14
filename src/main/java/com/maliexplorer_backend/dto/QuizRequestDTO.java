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
<<<<<<< HEAD
    @com.fasterxml.jackson.annotation.JsonAlias({"imageQuiz", "imageUrl", "logoUrl"})
=======

    @com.fasterxml.jackson.annotation.JsonAlias({"imageQuiz", "imageUrl", "logoUrl", "logo", "image"})
>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919
    private String imageQuiz;

    private String categorie;

    @com.fasterxml.jackson.annotation.JsonAlias({"points", "point"})
    private Integer point;
}
