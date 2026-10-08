package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.QuizPlayDTO;
import com.maliexplorer_backend.model.PropositionModel;
import com.maliexplorer_backend.model.QuestionModel;
import com.maliexplorer_backend.model.QuizModel;
import com.maliexplorer_backend.repository.QuizRepository;
import com.maliexplorer_backend.serviceimpl.QuizServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    @DisplayName("Devrait retourner le quiz avec la liste des propositions pour chaque question lors du jeu")
    void testGetQuizForPlayContientPropositions() {
        QuestionModel question = QuestionModel.builder()
                .idQuestion(1L)
                .nomQuestion("Quelle est la capitale historique de l'Empire du Mali ?")
                .duree(30)
                .reponse("Niani")
                .build();

        PropositionModel p1 = new PropositionModel(1L, "Niani", null);
        PropositionModel p2 = new PropositionModel(2L, "Tombouctou", null);
        PropositionModel p3 = new PropositionModel(3L, "Gao", null);
        PropositionModel p4 = new PropositionModel(4L, "Kouroussa", null);

        question.setPropositions(List.of(p1, p2, p3, p4));

        QuizModel quiz = QuizModel.builder()
                .idQuiz(5L)
                .nomQuiz("Quiz des Empires Maliens")
                .questions(List.of(question))
                .build();

        when(quizRepository.findById(5L)).thenReturn(Optional.of(quiz));

        QuizPlayDTO playDTO = quizService.getQuizForPlay(5L);

        assertNotNull(playDTO);
        assertEquals(5L, playDTO.getIdQuiz());
        assertEquals("Quiz des Empires Maliens", playDTO.getNomQuiz());
        assertEquals(1, playDTO.getQuestions().size());

        var qPlay = playDTO.getQuestions().get(0);
        assertEquals("Quelle est la capitale historique de l'Empire du Mali ?", qPlay.getNomQuestion());
        assertNotNull(qPlay.getPropositions());
        assertEquals(4, qPlay.getPropositions().size(), "L'application Flutter doit recevoir exactement 4 choix");
        assertTrue(qPlay.getPropositions().contains("Niani"));
        assertTrue(qPlay.getPropositions().contains("Tombouctou"));
        assertTrue(qPlay.getPropositions().contains("Gao"));
        assertTrue(qPlay.getPropositions().contains("Kouroussa"));
    }
}
