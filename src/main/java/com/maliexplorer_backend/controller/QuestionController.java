package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.QuestionRequestDTO;
import com.maliexplorer_backend.dto.QuestionResponseDTO;
import com.maliexplorer_backend.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Questions de Quiz", description = "Gestion des questions, propositions et réponses des quiz")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "Lister toutes les questions")
    public ResponseEntity<List<QuestionResponseDTO>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une question par ID")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Lister toutes les questions associées à un quiz")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionService.getQuestionsByQuiz(quizId));
    }

    @PostMapping
    @Operation(summary = "Ajouter une nouvelle question à un quiz")
    public ResponseEntity<QuestionResponseDTO> createQuestion(@Valid @RequestBody QuestionRequestDTO requestDTO) {
        QuestionResponseDTO created = questionService.createQuestion(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une question existante")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequestDTO requestDTO) {
        return ResponseEntity.ok(questionService.updateQuestion(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une question")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
