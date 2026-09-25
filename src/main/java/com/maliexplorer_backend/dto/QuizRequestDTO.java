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
    private Integer point;
    private String niveauDifficulte;
    private String imageUrl;
    private String categorie;
    private Long idUsers;
    private Long referenceId;
}
