package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.QuizRequestDTO;
import com.maliexplorer_backend.dto.QuizResponseDTO;
import com.maliexplorer_backend.dto.QuizSummaryDTO;
import com.maliexplorer_backend.model.QuestionModel;
import com.maliexplorer_backend.model.QuizModel;
import com.maliexplorer_backend.repository.PropositionRepository;
import com.maliexplorer_backend.repository.QuestionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChallengerM4R7EmpiricalTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private PropositionRepository propositionRepository;

    @Mock
    private BadgeProgressionService badgeProgressionService;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    @DisplayName("R7: createQuiz doit rejeter catégoriquement les quiz parasites de test")
    void testCreateQuizRejectsTestTitles() {
        List<String> forbiddenTitles = List.of(
                "Quiz MaliExplorereeee",
                "rbjwbhrw",
                "Quiz MaliExplorer",
                "Quiz MaliExplorer 2",
                "test",
                "test quiz",
                "Qui sommes-nous ?"
        );

        for (String title : forbiddenTitles) {
            QuizRequestDTO req = new QuizRequestDTO();
            req.setNomQuiz(title);
            req.setDescription("Description de test");

            assertThrows(IllegalArgumentException.class, () -> {
                quizService.createQuiz(req);
            }, "La création du quiz avec le titre '" + title + "' aurait dû lever une IllegalArgumentException");
        }

        verify(quizRepository, never()).save(any());
    }

    @Test
    @DisplayName("R7: getAllQuizzes et getQuizSummaries doivent filtrer les quiz parasites même s'ils existent en base")
    void testGetAllQuizzesFiltersOutTestQuizzes() {
        QuizModel testQuiz1 = QuizModel.builder().idQuiz(1L).nomQuiz("Quiz MaliExplorereeee").build();
        QuizModel testQuiz2 = QuizModel.builder().idQuiz(2L).nomQuiz("rbjwbhrw").build();
        QuizModel testQuiz3 = QuizModel.builder().idQuiz(3L).nomQuiz("Quiz MaliExplorer").build();
        QuizModel testQuiz4 = QuizModel.builder().idQuiz(4L).nomQuiz("test").build();
        QuizModel validQuiz = QuizModel.builder().idQuiz(5L).nomQuiz("Histoire & Grands Empires du Mali").build();

        when(quizRepository.findAll()).thenReturn(List.of(testQuiz1, testQuiz2, testQuiz3, testQuiz4, validQuiz));

        List<QuizResponseDTO> allQuizzes = quizService.getAllQuizzes();
        assertEquals(1, allQuizzes.size(), "Un seul quiz valide doit subsister");
        assertEquals("Histoire & Grands Empires du Mali", allQuizzes.get(0).getNomQuiz());

        List<QuizSummaryDTO> summaries = quizService.getQuizSummaries();
        assertEquals(1, summaries.size(), "Un seul résumé valide doit subsister");
        assertEquals("Histoire & Grands Empires du Mali", summaries.get(0).getNomQuiz());
    }

    @Test
    @DisplayName("R7: searchQuizzes et getQuizzesByCategorie doivent exclure les données de test")
    void testSearchAndCategoryExcludeTestQuizzes() {
        QuizModel testQuiz = QuizModel.builder().idQuiz(2L).nomQuiz("Quiz MaliExplorereeee").categorie("Histoire").build();
        when(quizRepository.findByNomQuizContainingIgnoreCase("Mali")).thenReturn(List.of(testQuiz));
        when(quizRepository.findByCategorieIgnoreCase("Histoire")).thenReturn(List.of(testQuiz));

        List<QuizResponseDTO> searchResults = quizService.searchQuizzes("Mali");
        assertTrue(searchResults.isEmpty(), "La recherche ne doit renvoyer aucun quiz de test");

        List<QuizResponseDTO> categoryResults = quizService.getQuizzesByCategorie("Histoire");
        assertTrue(categoryResults.isEmpty(), "La recherche par catégorie ne doit renvoyer aucun quiz de test");
    }
}

