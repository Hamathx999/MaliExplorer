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

    @NotBlank(message = "Le titre du quiz est obligatoire")
    private String nomQuiz;

    private String description;
    private String imageQuiz;    private String categorie;
}
