package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.dto.QuestionRequestDTO;
import com.maliexplorer_backend.dto.QuestionResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.QuestionModel;
import com.maliexplorer_backend.model.QuizModel;
import com.maliexplorer_backend.repository.QuestionRepository;
import com.maliexplorer_backend.repository.QuizRepository;
import com.maliexplorer_backend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionById(Long id) {
        QuestionModel question = findQuestionOrThrow(id);
        return mapToResponseDTO(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByQuiz(Long quizId) {
        return questionRepository.findByQuizIdQuiz(quizId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO) {
        QuizModel quiz = null;
        if (requestDTO.getQuizId() != null) {
            quiz = quizRepository.findById(requestDTO.getQuizId())
                    .orElseThrow(() -> new ResourceNotFoundException("QuizModel introuvable avec l'ID : " + requestDTO.getQuizId()));
        }

        QuestionModel question = QuestionModel.builder()
                .nomQuestion(requestDTO.getNomQuestion())
                .reponse(requestDTO.getReponse())
                .duree(requestDTO.getDuree() != null ? requestDTO.getDuree() : 30)
                .quiz(quiz)
                .build();

        QuestionModel saved = questionRepository.save(question);
        return mapToResponseDTO(saved);
    }

    @Override
    public QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO) {
        QuestionModel question = findQuestionOrThrow(id);

        if (requestDTO.getQuizId() != null) {
            QuizModel quiz = quizRepository.findById(requestDTO.getQuizId())
                    .orElseThrow(() -> new ResourceNotFoundException("QuizModel introuvable avec l'ID : " + requestDTO.getQuizId()));
            question.setQuiz(quiz);
        } else {
            question.setQuiz(null);
        }

        question.setNomQuestion(requestDTO.getNomQuestion());
        question.setReponse(requestDTO.getReponse());
        question.setDuree(requestDTO.getDuree());

        QuestionModel updated = questionRepository.save(question);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteQuestion(Long id) {
        QuestionModel question = findQuestionOrThrow(id);
        questionRepository.delete(question);
    }

    private QuestionModel findQuestionOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionModel introuvable avec l'ID : " + id));
    }

    private QuestionResponseDTO mapToResponseDTO(QuestionModel question) {
        boolean isAdmin = SecurityUtils.isAdmin();
        return QuestionResponseDTO.builder()
                .idQuestion(question.getIdQuestion())
                .nomQuestion(question.getNomQuestion())
                .reponse(isAdmin ? question.getReponse() : null)
                .points(question.getPoints())
                .duree(question.getDuree())
                .build();
    }
}