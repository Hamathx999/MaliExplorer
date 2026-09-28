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

    @Column(length = 50)
    private String niveauDifficulte; // FACILE, MOYEN, DIFFICILE

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 100)
    private String categorie; // Histoire, Culture, Gastronomie, Géographie

    private Long idUsers;

    private Long referenceId; // ex: idRegion ou idLieu associé si quiz thématique

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
