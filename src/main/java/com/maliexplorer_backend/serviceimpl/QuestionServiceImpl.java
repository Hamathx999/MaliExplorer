package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.dto.QuestionRequestDTO;
import com.maliexplorer_backend.dto.QuestionResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.PropositionModel;
import com.maliexplorer_backend.model.QuestionModel;
import com.maliexplorer_backend.model.QuizModel;
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
        if (requestDTO.getQuizId() != null && requestDTO.getQuizId() > 0) {
            quiz = quizRepository.findById(requestDTO.getQuizId()).orElse(null);
        }
        if (quiz == null) {
            List<QuizModel> quizzes = quizRepository.findAll();
            if (!quizzes.isEmpty()) {
                quiz = quizzes.get(0);
            } else {
                quiz = quizRepository.save(QuizModel.builder()
                        .nomQuiz("Quiz MaliExplorer")
                        .description("Quiz par défaut MaliExplorer")
                        .categorie("Culture")
                        .point(100)
                        .build());
            }
        }

        QuestionModel question = QuestionModel.builder()
                .nomQuestion(requestDTO.getNomQuestion())
                .reponse(requestDTO.getReponse())
                .theme(requestDTO.getTheme() != null && !requestDTO.getTheme().isBlank() ? requestDTO.getTheme().trim() : "Culture générale")
                .duree(requestDTO.getDuree() != null ? requestDTO.getDuree() : 30)
                .points(requestDTO.getPoints() != null ? requestDTO.getPoints() : 10)
                .explication(requestDTO.getExplication())
                .quiz(quiz)
                .propositions(new ArrayList<>())
                .build();

        if (requestDTO.getPropositions() != null && !requestDTO.getPropositions().isEmpty()) {
            for (String prop : requestDTO.getPropositions()) {
                if (prop != null && !prop.isBlank()) {
                    PropositionModel p = new PropositionModel();
                    p.setNomProposition(prop.trim());
                    p.setQuestion(question);
                    question.getPropositions().add(p);
                }
            }
        }
        if (question.getPropositions().isEmpty() && requestDTO.getReponse() != null && !requestDTO.getReponse().isBlank()) {
            PropositionModel p = new PropositionModel();
            p.setNomProposition(requestDTO.getReponse().trim());
            p.setQuestion(question);
            question.getPropositions().add(p);
        }

        QuestionModel saved = questionRepository.save(question);
        return mapToResponseDTO(saved);
    }

    @Override
    public QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO) {
        QuestionModel question = findQuestionOrThrow(id);

        if (requestDTO.getQuizId() != null && requestDTO.getQuizId() > 0) {
            QuizModel quiz = quizRepository.findById(requestDTO.getQuizId()).orElse(null);
            if (quiz != null) {
                question.setQuiz(quiz);
            }
        }

        question.setNomQuestion(requestDTO.getNomQuestion());
        question.setReponse(requestDTO.getReponse());
        if (requestDTO.getTheme() != null && !requestDTO.getTheme().isBlank()) {
            question.setTheme(requestDTO.getTheme().trim());
        }
        if (requestDTO.getDuree() != null) {
            question.setDuree(requestDTO.getDuree());
        }
        if (requestDTO.getPoints() != null) {
            question.setPoints(requestDTO.getPoints());
        }
        if (requestDTO.getExplication() != null) {
            question.setExplication(requestDTO.getExplication());
        }

        if (requestDTO.getPropositions() != null && !requestDTO.getPropositions().isEmpty()) {
            question.getPropositions().clear();
            for (String prop : requestDTO.getPropositions()) {
                if (prop != null && !prop.isBlank()) {
                    PropositionModel p = new PropositionModel();
                    p.setNomProposition(prop.trim());
                    p.setQuestion(question);
                    question.getPropositions().add(p);
                }
            }
        }

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
        List<String> props = question.getPropositions() != null
                ? question.getPropositions().stream().map(PropositionModel::getNomProposition).collect(Collectors.toList())
                : List.of();

        return QuestionResponseDTO.builder()
                .idQuestion(question.getIdQuestion())
                .nomQuestion(question.getNomQuestion())
                .reponse(question.getReponse())
                .points(question.getPoints() != null ? question.getPoints() : 10)
                .duree(question.getDuree() != null ? question.getDuree() : 30)
                .theme(question.getTheme() != null ? question.getTheme() : "Culture générale")
                .explication(question.getExplication())
                .quizId(question.getQuiz() != null ? question.getQuiz().getIdQuiz() : null)
                .propositions(props)
                .build();
    }
}