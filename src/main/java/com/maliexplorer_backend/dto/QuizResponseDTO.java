package com.maliexplorer_backend.dto;

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
public class QuizResponseDTO {

    private Long idQuiz;
    private String nomQuiz;
    private String description;
    private String imageQuiz;
    private String categorie;
    private Integer point;
    private List<QuestionResponseDTO> questions;

    @JsonProperty("id")
    public Long getId() {
        return this.idQuiz;
    }

    @JsonProperty("titre")
    public String getTitre() {
        return this.nomQuiz;
    }

    @JsonProperty("points")
    public Integer getPoints() {
        return this.point;
    }

    @JsonProperty("imageUrl")
    public String getImageUrl() {
        return this.imageQuiz;
    }

    @JsonProperty("logoUrl")
    public String getLogoUrl() {
        return this.imageQuiz;
    }
}
