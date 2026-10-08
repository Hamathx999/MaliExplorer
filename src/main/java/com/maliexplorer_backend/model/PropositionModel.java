package com.maliexplorer_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "propositions")

public class PropositionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProposition;
    private String nomProposition;

    @ManyToOne
    @JoinColumn(name = "questions_idQuestion")
    private QuestionModel question;
}
