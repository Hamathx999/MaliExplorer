package com.maliexplorer_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionResponseDTO {

    private Long idQuestion;

    @JsonProperty("id")
    public Long getId() {
        return this.idQuestion;
    }

    private String nomQuestion;

    @JsonProperty("question")
    public String getQuestion() {
        return this.nomQuestion;
    }

    private String reponse;
    private Integer points;
    private Integer duree;
    private java.util.List<String> propositions;
}
