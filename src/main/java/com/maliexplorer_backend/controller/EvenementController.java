package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.model.EvenementModel;
import com.maliexplorer_backend.service.EvenementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
@Tag(name = "Événements", description = "Gestion et modération des événements culturels")
public class EvenementController {

    private final EvenementService service;

    @GetMapping
    @Operation(summary = "Lister les événements (filtre optionnel ?statut=EN_ATTENTE)")
    public ResponseEntity<List<EvenementModel>> getAll(@RequestParam(required = false) EvenementModel.Statut statut) {
        return ResponseEntity.ok(statut == null ? service.getAll() : service.getByStatut(statut));
    }

    @GetMapping("/en-attente")
    @Operation(summary = "Lister les événements en attente de validation")
    public ResponseEntity<List<EvenementModel>> getEnAttente() {
        return ResponseEntity.ok(service.getByStatut(EvenementModel.Statut.EN_ATTENTE));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un événement")
    public ResponseEntity<EvenementModel> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Soumettre un événement")
    public ResponseEntity<EvenementModel> create(@Valid @RequestBody EvenementModel evenement) {
        return new ResponseEntity<>(service.create(evenement), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un événement (Admin)")
    public ResponseEntity<EvenementModel> update(@PathVariable Long id, @Valid @RequestBody EvenementModel evenement) {
        return ResponseEntity.ok(service.update(id, evenement));
    }

    @PatchMapping("/{id}/approuver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approuver un événement (Admin)")
    public ResponseEntity<EvenementModel> approuver(@PathVariable Long id) {
        return ResponseEntity.ok(service.approuver(id));
    }

    @PatchMapping("/{id}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rejeter un événement avec un motif (Admin)")
    public ResponseEntity<EvenementModel> rejeter(@PathVariable Long id,
                                                  @RequestBody(required = false) Map<String, String> body) {
        return ResponseEntity.ok(service.rejeter(id, body != null ? body.get("motif") : null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un événement (Admin)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

