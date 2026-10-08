package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.model.PropositionModel;
import com.maliexplorer_backend.service.PropositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propositions")
@RequiredArgsConstructor
@Tag(name = "Propositions de Quiz", description = "Gestion des choix et options des questions de quiz")
public class PropositionController {

    private final PropositionService propositionService;

    @GetMapping("/question/{questionId}")
    @Operation(summary = "Lister toutes les propositions pour une question")
    public ResponseEntity<List<PropositionModel>> getPropositionsParQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(propositionService.obtenirPropositionsParQuestion(questionId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une proposition par ID")
    public ResponseEntity<PropositionModel> getPropositionById(@PathVariable Long id) {
        return ResponseEntity.ok(propositionService.obtenirPropositionParId(id));
    }

    @PostMapping("/question/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle proposition pour une question")
    public ResponseEntity<PropositionModel> creerProposition(
            @PathVariable Long questionId,
            @RequestBody PropositionModel proposition) {
        PropositionModel cree = propositionService.creerProposition(proposition, questionId);
        return new ResponseEntity<>(cree, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une proposition existante")
    public ResponseEntity<PropositionModel> modifierProposition(
            @PathVariable Long id,
            @RequestBody PropositionModel propositionDetails) {
        return ResponseEntity.ok(propositionService.modifierProposition(id, propositionDetails));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une proposition par ID")
    public ResponseEntity<Void> supprimerProposition(@PathVariable Long id) {
        propositionService.supprimerProposition(id);
        return ResponseEntity.noContent().build();
    }
}
