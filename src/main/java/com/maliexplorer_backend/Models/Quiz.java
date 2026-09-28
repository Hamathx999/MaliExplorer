package com.maliexplorer_backend.Models;

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
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idQuiz;

    @Column(nullable = false, length = 150)
    private String nomQuiz;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Integer point = 100;

    @Column(length = 500)
    private String imageQuiz;

  
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    public Long getId() {
        return this.idQuiz;
    }

    public void setId(Long id) {
        this.idQuiz = id;
    }
}
