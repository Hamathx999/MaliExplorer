package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.QuestionRequestDTO;
import com.maliexplorer_backend.dto.QuestionResponseDTO;

import java.util.List;

public interface QuestionService {

    List<QuestionResponseDTO> getAllQuestions();

    QuestionResponseDTO getQuestionById(Long id);

    List<QuestionResponseDTO> getQuestionsByQuiz(Long quizId);

    QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO);

    QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO);

    void deleteQuestion(Long id);
}
