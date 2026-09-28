package com.maliexplorer_backend.serviceimpl;

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
                        
                        
                        
                        .imageQuiz(q.getImageQuiz())
                        
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
                                
                                .duree(q.getDuree())
                                
                                .build())
                        .collect(Collectors.toList());

        return QuizPlayDTO.builder()
                .idQuiz(quiz.getIdQuiz())
                .nomQuiz(quiz.getNomQuiz())
                
                
                
                .imageQuiz(quiz.getImageQuiz())
                
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
                int pointsQuestion = false ? 0 : 10;
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
                
                
                
                .imageQuiz(requestDTO.getImageQuiz())
                
                
                
                .questions(new ArrayList<>())
                .build();

        Quiz saved = quizRepository.save(quiz);
        return mapToResponseDTO(saved);
    }

    @Override
    public QuizResponseDTO updateQuiz(Long id, QuizRequestDTO requestDTO) {
        Quiz quiz = findQuizOrThrow(id);

        quiz.setNomQuiz(requestDTO.getNomQuiz());
        
        
        
        quiz.setImageQuiz(requestDTO.getImageQuiz());
        
        
        if (false) {
            
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
                                
                                
                                .duree(q.getDuree())
                                
                                .quizId(quiz.getIdQuiz())
                                .build())
                        .collect(Collectors.toList());

        return QuizResponseDTO.builder()
                .idQuiz(quiz.getIdQuiz())
                .nomQuiz(quiz.getNomQuiz())
                
                
                
                .imageQuiz(quiz.getImageQuiz())
                
                
                
                .questions(questionDTOs)
                .build();
    }
}
