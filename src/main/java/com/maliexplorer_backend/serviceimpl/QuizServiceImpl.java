package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.dto.*;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.PropositionModel;
import com.maliexplorer_backend.model.QuestionModel;
import com.maliexplorer_backend.model.QuizModel;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.QuizRepository;
import com.maliexplorer_backend.service.BadgeProgressionService;
import com.maliexplorer_backend.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final BadgeProgressionService badgeProgressionService;

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
                        .categorie(q.getCategorie())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(Long id) {
        QuizModel quiz = findQuizOrThrow(id);
        return mapToResponseDTO(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizPlayDTO getQuizForPlay(Long id) {
        QuizModel quiz = findQuizOrThrow(id);

        List<QuestionPlayDTO> playQuestions = quiz.getQuestions() == null ? Collections.emptyList()
                : quiz.getQuestions().stream()
                        .map(q -> QuestionPlayDTO.builder()
                                .idQuestion(q.getIdQuestion())
                                .nomQuestion(q.getNomQuestion())
                                .duree(q.getDuree())
                                .points((q.getPoints() != null && q.getPoints() > 0) ? q.getPoints() : 10)
                                .reponse(q.getReponse())
                                .propositions(q.getPropositions() != null
                                        ? q.getPropositions().stream().map(PropositionModel::getNomProposition).collect(Collectors.toList())
                                        : Collections.emptyList())
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
    public QuizResultDTO evaluateQuiz(QuizSubmissionDTO submission) {
        QuizModel quiz = findQuizOrThrow(submission.getQuizId());
        Map<Long, String> reponsesSoumises = submission.getReponses() != null ? submission.getReponses()
                : Collections.emptyMap();

        int scoreQuestionsObtenu = 0;
        int scoreMaxPossible = 0;
        List<QuizResultDTO.QuestionResultDetailDTO> details = new ArrayList<>();

        if (quiz.getQuestions() != null) {
            for (QuestionModel q : quiz.getQuestions()) {
                int pointsQuestion = (q.getPoints() != null && q.getPoints() > 0) ? q.getPoints() : 10;
                scoreMaxPossible += pointsQuestion;

                String reponseSoumise = null;
                if (reponsesSoumises != null) {
                    reponseSoumise = reponsesSoumises.get(q.getIdQuestion());
                    if (reponseSoumise == null) {
                        // Support pour clé transmise sous forme de String
                        reponseSoumise = ((Map<?, String>) (Map<?, ?>) reponsesSoumises).get(String.valueOf(q.getIdQuestion()));
                    }
                }

                boolean estCorrect = areAnswersEqual(reponseSoumise, q.getReponse());

                int pointsGagnes = estCorrect ? pointsQuestion : 0;
                scoreQuestionsObtenu += pointsGagnes;

                log.info("Évaluation Quiz ID={} Q#{}: soumise='{}', attendue='{}', correct={}",
                        quiz.getIdQuiz(), q.getIdQuestion(), reponseSoumise, q.getReponse(), estCorrect);

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

        // ==================== BARÈME DE POINTS & GAMIFICATION ====================
        // - Bonne réponse : points réels de chaque question (+10 pts par défaut)
        // - Bonus quiz terminé : +20 points
        // - Bonus quiz parfait (100% de réussite) : +20 points supplémentaires
        int bonusTermine = 20;
        boolean estParfait = (scoreQuestionsObtenu > 0 && scoreQuestionsObtenu == scoreMaxPossible);
        int bonusParfait = estParfait ? 20 : 0;
        int pointsGagnesTotal = scoreQuestionsObtenu + bonusTermine + bonusParfait;

        int pointsTotauxUtilisateur = 0;
        String badgeActuel = "Kalanden";
        String prochainBadge = "Fasoden";
        double progressionPourcent = 0.0;
        String messageProgression = "Connectez-vous pour enregistrer vos points et gagner des badges Bambara !";

        utilisateurModel userConnecte = SecurityUtils.getCurrentUser();
        Integer targetUserId = null;
        if (userConnecte != null && userConnecte.getIdUsers() > 0) {
            targetUserId = Integer.valueOf(userConnecte.getIdUsers());
        } else if (submission.getUserId() != null && submission.getUserId() > 0) {
            targetUserId = submission.getUserId().intValue();
        }

        if (targetUserId != null && targetUserId > 0) {
            try {
                String descriptionGain = "Quiz : " + quiz.getNomQuiz()
                        + (estParfait ? " (Score parfait 100%)" : " (Terminé)");

                // Référence d'activité : si un sessionId est transmis par le client, on l'utilise pour garantir l'unicité par partie
                String refActivite = (submission.getSessionId() != null && !submission.getSessionId().isBlank())
                        ? "QUIZ_" + quiz.getIdQuiz() + "_" + submission.getSessionId()
                        : "QUIZ_" + quiz.getIdQuiz() + "_" + System.currentTimeMillis();

                ProgressionResponseDTO progression = badgeProgressionService.attribuerPoints(
                        targetUserId,
                        "QUIZ",
                        pointsGagnesTotal,
                        descriptionGain,
                        refActivite
                );

                pointsTotauxUtilisateur = progression.getPoints();
                badgeActuel = progression.getBadge();
                prochainBadge = progression.getNextBadge();
                progressionPourcent = progression.getProgression();
                messageProgression = progression.getMessage();
                log.info("Points attribués à l'utilisateur ID={} pour le Quiz ID={} : +{} points (Nouveau total={})",
                        targetUserId, quiz.getIdQuiz(), pointsGagnesTotal, pointsTotauxUtilisateur);
            } catch (Exception e) {
                log.warn("Impossible d'attribuer les points pour l'utilisateur ID={} : {}", targetUserId, e.getMessage());
                messageProgression = e.getMessage();
            }
        }

        double pourcentage = scoreMaxPossible > 0
                ? Math.round(((double) scoreQuestionsObtenu / scoreMaxPossible) * 1000.0) / 10.0
                : 0.0;
        boolean reussi = pourcentage >= 50.0;

        return QuizResultDTO.builder()
                .quizId(quiz.getIdQuiz())
                .nomQuiz(quiz.getNomQuiz())
                .scoreTotalObtenu(scoreQuestionsObtenu)
                .scoreMaxPossible(scoreMaxPossible)
                .pourcentage(pourcentage)
                .reussi(reussi)
                .detailsQuestions(details)
                .pointsGagnesActivite(pointsGagnesTotal)
                .totalPointsUtilisateur(pointsTotauxUtilisateur)
                .badgeActuel(badgeActuel)
                .prochainBadge(prochainBadge)
                .progressionProchainBadge(progressionPourcent)
                .messageProgression(messageProgression)
                .build();
    }

    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO requestDTO) {
        QuizModel quiz = QuizModel.builder()
                .nomQuiz(requestDTO.getNomQuiz())
                .description(requestDTO.getDescription())
                .imageQuiz(requestDTO.getImageQuiz())
                .categorie(requestDTO.getCategorie())
                .point(requestDTO.getPoint() != null ? requestDTO.getPoint() : 100)
                .build();

        QuizModel saved = quizRepository.save(quiz);
        return mapToResponseDTO(saved);
    }

    @Override
    public QuizResponseDTO updateQuiz(Long id, QuizRequestDTO requestDTO) {
        QuizModel quiz = findQuizOrThrow(id);

        quiz.setNomQuiz(requestDTO.getNomQuiz());
        quiz.setDescription(requestDTO.getDescription());
        quiz.setImageQuiz(requestDTO.getImageQuiz());
        quiz.setCategorie(requestDTO.getCategorie());
        if (requestDTO.getPoint() != null) {
            quiz.setPoint(requestDTO.getPoint());
        }

        QuizModel updated = quizRepository.save(quiz);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteQuiz(Long id) {
        QuizModel quiz = findQuizOrThrow(id);
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
    public List<QuizResponseDTO> searchQuizzes(String keyword) {
        return quizRepository.findByNomQuizContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ======================== PRIVATE HELPERS ========================

    private QuizModel findQuizOrThrow(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz introuvable avec l'ID : " + id));
    }

    private QuizResponseDTO mapToResponseDTO(QuizModel quiz) {
        List<QuestionResponseDTO> questionDTOs = quiz.getQuestions() == null ? Collections.emptyList()
                : quiz.getQuestions().stream()
                        .map(q -> QuestionResponseDTO.builder()
                                .idQuestion(q.getIdQuestion())
                                .nomQuestion(q.getNomQuestion())
                                .reponse(q.getReponse())
                                .points((q.getPoints() != null && q.getPoints() > 0) ? q.getPoints() : 10)
                                .duree(q.getDuree())
                                .propositions(q.getPropositions() != null
                                        ? q.getPropositions().stream().map(PropositionModel::getNomProposition).collect(Collectors.toList())
                                        : Collections.emptyList())
                                .build())
                        .collect(Collectors.toList());

        return QuizResponseDTO.builder()
                .idQuiz(quiz.getIdQuiz())
                .nomQuiz(quiz.getNomQuiz())
                .description(quiz.getDescription())
                .imageQuiz(quiz.getImageQuiz())
                .categorie(quiz.getCategorie())
                .point(quiz.getPoint())
                .questions(questionDTOs)
                .build();
    }

    private boolean areAnswersEqual(String ans1, String ans2) {
        if (ans1 == null || ans2 == null) {
            return false;
        }
        return normalizeText(ans1).equalsIgnoreCase(normalizeText(ans2));
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        return text.trim()
                .replace('\u00A0', ' ')
                .replace('\u2019', '\'')
                .replace('\u2018', '\'')
                .replace('`', '\'')
                .replaceAll("\\s+", " ");
    }
}