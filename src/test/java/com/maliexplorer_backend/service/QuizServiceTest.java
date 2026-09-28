package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.QuizResultDTO;
import com.maliexplorer_backend.dto.QuizSubmissionDTO;
import com.maliexplorer_backend.model.QuestionModel;
import com.maliexplorer_backend.model.QuizModel;
import com.maliexplorer_backend.repository.QuizRepository;
import com.maliexplorer_backend.serviceimpl.QuizServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

        @Mock
        private QuizRepository quizRepository;

        @InjectMocks
        private QuizServiceImpl quizService;

        private QuizModel testQuiz;

        @BeforeEach
        void setUp() {
                QuestionModel q1 = QuestionModel.builder()
                                .idQuestion(101L)
                                .nomQuestion("Quelle est la capitale du Mali ?")
                                .reponse("Bamako")
                                .points(10)
                                .propositions(List.of("Bamako", "Sikasso", "Kayes", "Mopti"))
                                .build();

                QuestionModel q2 = QuestionModel.builder()
                                .idQuestion(102L)
                                .nomQuestion("Quel empereur a fondé l'Empire du Mali ?")
                                .reponse("Soundiata Keïta")
                                .points(20).propositions(List.of("Soundiata Keïta", "Kankan Moussa", "Askia Mohamed"))
                                .build();




                testQuiz = QuizModel.builder()
                                .idQuiz(1L)
                                .nomQuiz("Histoire et Géographie du Mali")
                                .point(30)
                                .questions(List.of(q1, q2))
                                .build();
        }

        @Test
        void testEvaluateQuizAllCorrect() {
                when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

                QuizSubmissionDTO submission = QuizSubmissionDTO.builder()
                                .quizId(1L)
                                .userId(12L)
                                .reponses(Map.of(
                                                101L, "Bamako",
                                                102L, "Soundiata Keïta"))
                                .build();

                QuizResultDTO result = quizService.evaluateQuiz(submission);

                assertNotNull(result);
                assertEquals(30, result.getScoreTotalObtenu());
                assertEquals(30, result.getScoreMaxPossible());
                assertEquals(100.0, result.getPourcentage());
                assertTrue(result.getReussi());
                assertEquals(2, result.getDetailsQuestions().size());
                assertTrue(result.getDetailsQuestions().get(0).getEstCorrect());
                assertTrue(result.getDetailsQuestions().get(1).getEstCorrect());
        }

        @Test
        void testEvaluateQuizPartialCorrect() {
                when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

                QuizSubmissionDTO submission = QuizSubmissionDTO.builder()
                                .quizId(1L)
                                .userId(12L)
                                .reponses(Map.of(
                                                101L, "Bamako",
                                                102L, "Kankan Moussa" // mauvaise réponse
                                ))
                                .build();

                QuizResultDTO result = quizService.evaluateQuiz(submission);

                assertNotNull(result);
                assertEquals(10, result.getScoreTotalObtenu());
                assertEquals(30, result.getScoreMaxPossible());
                assertFalse(result.getReussi()); // 10/30 = 33.33% < 50%
                assertTrue(result.getDetailsQuestions().get(0).getEstCorrect());
                assertFalse(result.getDetailsQuestions().get(1).getEstCorrect());
        }
}
