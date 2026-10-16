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

    // Remplacement de List<String> par la relation OneToMany vers PropositionModel
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PropositionModel> propositions = new ArrayList<>();

    // Colonne pour assurer la rétrocompatibilité avec la colonne propositions de la table MySQL
    @Column(name = "propositions", columnDefinition = "TEXT")
    @Builder.Default
    private String propositionsJson = "[]";

    @PrePersist
    @PreUpdate
    public void syncPropositionsJson() {
        if (propositions != null && !propositions.isEmpty()) {
            List<String> list = propositions.stream()
                .map(PropositionModel::getNomProposition)
                .filter(s -> s != null && !s.isBlank())
                .toList();
            if (!list.isEmpty()) {
                this.propositionsJson = "[\"" + String.join("\", \"", list) + "\"]";
                return;
            }
        }
        if (this.propositionsJson == null || this.propositionsJson.isBlank()) {
            this.propositionsJson = "[]";
        }
    }

    @Builder.Default
    private Integer points = 0;

    @Column(length = 100)
    @Builder.Default
    private String theme = "Culture générale";

    @Builder.Default
    private Integer duree = 30; // en secondes

    @Column(columnDefinition = "TEXT")
    private String explication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quiz")
    private QuizModel quiz;

    // Getters / Setters personnalisés si nécessaire pour l'interface
    public Long getId() {
        return this.idQuestion;
    }

    public void setId(Long id) {
        this.idQuestion = id;
    }
}