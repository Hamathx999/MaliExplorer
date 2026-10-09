package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idQuiz;

    @Column(nullable = false, length = 150)
    private String nomQuiz;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Integer point = 100;

    @Column(name = "image_quiz", columnDefinition = "TEXT")
    private String imageQuiz;

    @Column(length = 100)
    private String categorie;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionModel> questions = new ArrayList<>();


    public Long getId() {
        return this.idQuiz;
    }

    public void setId(Long id) {
        this.idQuiz = id;
    }
}
