package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionSummaryDTO {

    private Long idQuestion;
    private String nomQuestion;
    private Integer point;
    private Integer duree;
}
