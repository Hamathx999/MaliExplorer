package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.*;
import com.maliexplorer_backend.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "Gestion des quiz culturels, sessions de jeu et calcul des scores")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @Operation(summary = "Lister tous les quiz (avec leurs questions)")
    public ResponseEntity<List<QuizResponseDTO>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/catalogue")
    @Operation(summary = "Lister les résumés des quiz pour l'affichage en catalogue")
    public ResponseEntity<List<QuizSummaryDTO>> getQuizSummaries() {
        return ResponseEntity.ok(quizService.getQuizSummaries());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails complets d'un quiz par ID")
    public ResponseEntity<QuizResponseDTO> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @GetMapping("/{id}/jouer")
    @Operation(summary = "Obtenir un quiz pour une session de jeu (masque les réponses correctes)")
    public ResponseEntity<QuizPlayDTO> getQuizForPlay(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizForPlay(id));
    }

    @PostMapping("/soumettre")
    @Operation(summary = "Soumettre les réponses d'un quiz et recevoir le calcul du score et des résultats")
    public ResponseEntity<QuizResultDTO> submitQuiz(@Valid @RequestBody QuizSubmissionDTO submissionDTO) {
        QuizResultDTO result = quizService.evaluateQuiz(submissionDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau quiz")
    public ResponseEntity<QuizResponseDTO> createQuiz(@Valid @RequestBody QuizRequestDTO requestDTO) {
        QuizResponseDTO created = quizService.createQuiz(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un quiz existant")
    public ResponseEntity<QuizResponseDTO> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizRequestDTO requestDTO) {
        return ResponseEntity.ok(quizService.updateQuiz(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un quiz")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Lister les quiz par catégorie")
    public ResponseEntity<List<QuizResponseDTO>> getQuizzesByCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(quizService.getQuizzesByCategorie(categorie));
    }

    @GetMapping("/niveau/{niveau}")
    @Operation(summary = "Lister les quiz par niveau de difficulté (FACILE, MOYEN, DIFFICILE)")
    public ResponseEntity<List<QuizResponseDTO>> getQuizzesByNiveau(@PathVariable String niveau) {
        return ResponseEntity.ok(quizService.getQuizzesByNiveau(niveau));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des quiz par mot-clé")
    public ResponseEntity<List<QuizResponseDTO>> searchQuizzes(@RequestParam String q) {
        return ResponseEntity.ok(quizService.searchQuizzes(q));
    }
}
