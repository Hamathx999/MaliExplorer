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
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idQuestion;

    @Column(nullable = false, length = 300)
    private String nomQuestion;

    @Column(nullable = false, length = 200)
    private String reponse;

    @Column(columnDefinition = "TEXT")
    private String explication;

    @Builder.Default
    private Integer point = 10;

    @Builder.Default
    private Integer duree = 30; // en secondes

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_propositions", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "proposition", length = 200)
    @Builder.Default
    private List<String> propositions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quiz")
    private Quiz quiz;

    public Long getId() {
        return this.idQuestion;
    }

    public void setId(Long id) {
        this.idQuestion = id;
    }
}
