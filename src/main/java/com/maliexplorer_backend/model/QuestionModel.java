package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idQuestion;

    @Column(nullable = false, length = 300)
    private String nomQuestion;

    @Column(nullable = false, length = 200)
    private String reponse;

    @Column(nullable = false, columnDefinition ="TEXT")
    private List<String> propositions;

    @Builder.Default
    private Integer points = 0;

    @Builder.Default
    private Integer duree = 30; // en secondes

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quiz")
    private QuizModel quiz;

    public Long getId() {
        return this.idQuestion;
    }

    public void setId(Long id) {
        this.idQuestion = id;
    }
}
