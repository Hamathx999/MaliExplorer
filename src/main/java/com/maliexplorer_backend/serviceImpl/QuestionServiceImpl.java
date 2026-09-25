package com.maliexplorer_backend.serviceImpl;

import com.maliexplorer_backend.dto.QuestionRequestDTO;
import com.maliexplorer_backend.dto.QuestionResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.Question;
import com.maliexplorer_backend.model.Quiz;
import com.maliexplorer_backend.repository.QuestionRepository;
import com.maliexplorer_backend.repository.QuizRepository;
import com.maliexplorer_backend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        Question question = findQuestionOrThrow(id);
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
        Quiz quiz = null;
        if (requestDTO.getQuizId() != null) {
            quiz = quizRepository.findById(requestDTO.getQuizId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quiz introuvable avec l'ID : " + requestDTO.getQuizId()));
        }

        Question question = Question.builder()
                .nomQuestion(requestDTO.getNomQuestion())
                .reponse(requestDTO.getReponse())
                .explication(requestDTO.getExplication())
                .point(requestDTO.getPoint() != null ? requestDTO.getPoint() : 10)
                .duree(requestDTO.getDuree() != null ? requestDTO.getDuree() : 30)
                .propositions(requestDTO.getPropositions() != null ? new ArrayList<>(requestDTO.getPropositions()) : new ArrayList<>())
                .quiz(quiz)
                .build();

        Question saved = questionRepository.save(question);
        return mapToResponseDTO(saved);
    }

    @Override
    public QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO) {
        Question question = findQuestionOrThrow(id);

        if (requestDTO.getQuizId() != null) {
            Quiz quiz = quizRepository.findById(requestDTO.getQuizId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quiz introuvable avec l'ID : " + requestDTO.getQuizId()));
            question.setQuiz(quiz);
        } else {
            question.setQuiz(null);
        }

        question.setNomQuestion(requestDTO.getNomQuestion());
        question.setReponse(requestDTO.getReponse());
        question.setExplication(requestDTO.getExplication());
        question.setPoint(requestDTO.getPoint());
        question.setDuree(requestDTO.getDuree());
        if (requestDTO.getPropositions() != null) {
            question.setPropositions(new ArrayList<>(requestDTO.getPropositions()));
        }

        Question updated = questionRepository.save(question);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteQuestion(Long id) {
        Question question = findQuestionOrThrow(id);
        questionRepository.delete(question);
    }

    private Question findQuestionOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question introuvable avec l'ID : " + id));
    }

    private QuestionResponseDTO mapToResponseDTO(Question question) {
        Long quizId = question.getQuiz() != null ? question.getQuiz().getIdQuiz() : null;

        return QuestionResponseDTO.builder()
                .idQuestion(question.getIdQuestion())
                .nomQuestion(question.getNomQuestion())
                .reponse(question.getReponse())
                .explication(question.getExplication())
                .point(question.getPoint())
                .duree(question.getDuree())
                .propositions(question.getPropositions() != null ? new ArrayList<>(question.getPropositions()) : new ArrayList<>())
                .quizId(quizId)
                .build();
    }
}
