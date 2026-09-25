package com.maliexplorer_backend.serviceImpl;

import com.maliexplorer_backend.dto.*;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.Question;
import com.maliexplorer_backend.model.Quiz;
import com.maliexplorer_backend.repository.QuizRepository;
import com.maliexplorer_backend.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getAllQuizzes() {
        return quizRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSummaryDTO> getQuizSummaries() {
        return quizRepository.findAll()
                .stream()
                .map(q -> QuizSummaryDTO.builder()
                        .idQuiz(q.getIdQuiz())
                        .nomQuiz(q.getNomQuiz())
                        .description(q.getDescription())
                        .point(q.getPoint())
                        .niveauDifficulte(q.getNiveauDifficulte())
                        .imageUrl(q.getImageUrl())
                        .categorie(q.getCategorie())
                        .nombreQuestions(q.getQuestions() != null ? q.getQuestions().size() : 0)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(Long id) {
        Quiz quiz = findQuizOrThrow(id);
        return mapToResponseDTO(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizPlayDTO getQuizForPlay(Long id) {
        Quiz quiz = findQuizOrThrow(id);

        List<QuestionPlayDTO> playQuestions = quiz.getQuestions() == null ? Collections.emptyList() :
                quiz.getQuestions().stream()
                        .map(q -> QuestionPlayDTO.builder()
                                .idQuestion(q.getIdQuestion())
                                .nomQuestion(q.getNomQuestion())
                                .point(q.getPoint())
                                .duree(q.getDuree())
                                .propositions(q.getPropositions() != null ? new ArrayList<>(q.getPropositions()) : new ArrayList<>())
                                .build())
                        .collect(Collectors.toList());

        return QuizPlayDTO.builder()
                .idQuiz(quiz.getIdQuiz())
                .nomQuiz(quiz.getNomQuiz())
                .description(quiz.getDescription())
                .point(quiz.getPoint())
                .niveauDifficulte(quiz.getNiveauDifficulte())
                .imageUrl(quiz.getImageUrl())
                .categorie(quiz.getCategorie())
                .questions(playQuestions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResultDTO evaluateQuiz(QuizSubmissionDTO submission) {
        Quiz quiz = findQuizOrThrow(submission.getQuizId());
        Map<Long, String> reponsesSoumises = submission.getReponses() != null ? submission.getReponses() : Collections.emptyMap();

        int scoreTotalObtenu = 0;
        int scoreMaxPossible = 0;
        List<QuizResultDTO.QuestionResultDetailDTO> details = new ArrayList<>();

        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                int pointsQuestion = q.getPoint() != null ? q.getPoint() : 10;
                scoreMaxPossible += pointsQuestion;

                String reponseSoumise = reponsesSoumises.get(q.getIdQuestion());
                boolean estCorrect = false;

                if (reponseSoumise != null && q.getReponse() != null) {
                    estCorrect = reponseSoumise.trim().equalsIgnoreCase(q.getReponse().trim());
                }

                int pointsGagnes = estCorrect ? pointsQuestion : 0;
                scoreTotalObtenu += pointsGagnes;

                details.add(QuizResultDTO.QuestionResultDetailDTO.builder()
                        .idQuestion(q.getIdQuestion())
                        .nomQuestion(q.getNomQuestion())
                        .reponseSoumise(reponseSoumise)
                        .bonneReponse(q.getReponse())
                        .estCorrect(estCorrect)
                        .pointsGagnes(pointsGagnes)
                        .explication(q.getExplication())
                        .build());
            }
        }

        double pourcentage = scoreMaxPossible > 0 ? (scoreTotalObtenu * 100.0 / scoreMaxPossible) : 0.0;
        boolean reussi = pourcentage >= 50.0;

        return QuizResultDTO.builder()
                .quizId(quiz.getIdQuiz())
                .nomQuiz(quiz.getNomQuiz())
                .scoreTotalObtenu(scoreTotalObtenu)
                .scoreMaxPossible(scoreMaxPossible)
                .pourcentage(Math.round(pourcentage * 100.0) / 100.0)
                .reussi(reussi)
                .detailsQuestions(details)
                .build();
    }

    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO requestDTO) {
        Quiz quiz = Quiz.builder()
                .nomQuiz(requestDTO.getNomQuiz())
                .description(requestDTO.getDescription())
                .point(requestDTO.getPoint() != null ? requestDTO.getPoint() : 0)
                .niveauDifficulte(requestDTO.getNiveauDifficulte())
                .imageUrl(requestDTO.getImageUrl())
                .categorie(requestDTO.getCategorie())
                .idUsers(requestDTO.getIdUsers())
                .referenceId(requestDTO.getReferenceId())
                .questions(new ArrayList<>())
                .build();

        Quiz saved = quizRepository.save(quiz);
        return mapToResponseDTO(saved);
    }

    @Override
    public QuizResponseDTO updateQuiz(Long id, QuizRequestDTO requestDTO) {
        Quiz quiz = findQuizOrThrow(id);

        quiz.setNomQuiz(requestDTO.getNomQuiz());
        quiz.setDescription(requestDTO.getDescription());
        quiz.setPoint(requestDTO.getPoint());
        quiz.setNiveauDifficulte(requestDTO.getNiveauDifficulte());
        quiz.setImageUrl(requestDTO.getImageUrl());
        quiz.setCategorie(requestDTO.getCategorie());
        if (requestDTO.getIdUsers() != null) {
            quiz.setIdUsers(requestDTO.getIdUsers());
        }
        if (requestDTO.getReferenceId() != null) {
            quiz.setReferenceId(requestDTO.getReferenceId());
        }

        Quiz updated = quizRepository.save(quiz);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = findQuizOrThrow(id);
        quizRepository.delete(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getQuizzesByCategorie(String categorie) {
        return quizRepository.findByCategorieIgnoreCase(categorie)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getQuizzesByNiveau(String niveau) {
        return quizRepository.findByNiveauDifficulteIgnoreCase(niveau)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> searchQuizzes(String keyword) {
        return quizRepository.findByNomQuizContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private Quiz findQuizOrThrow(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz introuvable avec l'ID : " + id));
    }

    private QuizResponseDTO mapToResponseDTO(Quiz quiz) {
        List<QuestionResponseDTO> questionDTOs = quiz.getQuestions() == null ? Collections.emptyList() :
                quiz.getQuestions().stream()
                        .map(q -> QuestionResponseDTO.builder()
                                .idQuestion(q.getIdQuestion())
                                .nomQuestion(q.getNomQuestion())
                                .reponse(q.getReponse())
                                .explication(q.getExplication())
                                .point(q.getPoint())
                                .duree(q.getDuree())
                                .propositions(q.getPropositions() != null ? new ArrayList<>(q.getPropositions()) : new ArrayList<>())
                                .quizId(quiz.getIdQuiz())
                                .build())
                        .collect(Collectors.toList());

        return QuizResponseDTO.builder()
                .idQuiz(quiz.getIdQuiz())
                .nomQuiz(quiz.getNomQuiz())
                .description(quiz.getDescription())
                .point(quiz.getPoint())
                .niveauDifficulte(quiz.getNiveauDifficulte())
                .imageUrl(quiz.getImageUrl())
                .categorie(quiz.getCategorie())
                .idUsers(quiz.getIdUsers())
                .referenceId(quiz.getReferenceId())
                .questions(questionDTOs)
                .build();
    }
}
