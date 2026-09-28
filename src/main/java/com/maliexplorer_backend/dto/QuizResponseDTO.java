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
public class QuizResponseDTO {

    private Long idQuiz;
    private String nomQuiz;
    private String description;
    private String imageQuiz;
private Long idUsers;
    private Long referenceId;
    private List<QuestionResponseDTO> questions;
}
