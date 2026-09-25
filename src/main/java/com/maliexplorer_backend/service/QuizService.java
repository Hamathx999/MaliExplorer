package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.*;

import java.util.List;

public interface QuizService {

    List<QuizResponseDTO> getAllQuizzes();

    List<QuizSummaryDTO> getQuizSummaries();

    QuizResponseDTO getQuizById(Long id);

    QuizPlayDTO getQuizForPlay(Long id);

    QuizResultDTO evaluateQuiz(QuizSubmissionDTO submission);

    QuizResponseDTO createQuiz(QuizRequestDTO requestDTO);

    QuizResponseDTO updateQuiz(Long id, QuizRequestDTO requestDTO);

    void deleteQuiz(Long id);

    List<QuizResponseDTO> getQuizzesByCategorie(String categorie);

    List<QuizResponseDTO> getQuizzesByNiveau(String niveau);

    List<QuizResponseDTO> searchQuizzes(String keyword);
}
