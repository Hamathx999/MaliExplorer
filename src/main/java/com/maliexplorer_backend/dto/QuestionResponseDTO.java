package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionResponseDTO {

    private Long idQuestion;
    private String nomQuestion;
    private String reponse;
    private Integer points;
    private Integer duree;
}

