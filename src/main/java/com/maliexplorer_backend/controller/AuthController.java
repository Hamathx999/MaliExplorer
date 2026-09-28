package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.service.AuthService;
import com.maliexplorer_backend.dto.AuthResponseDTO;
import com.maliexplorer_backend.dto.LoginRequestDTO;
import com.maliexplorer_backend.dto.RegisterRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentification", description = "Gestion de la connexion, inscription et validation des tokens Firebase")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Connexion via Firebase ID Token", description = "Valide le token envoyé par le front-end Firebase et retourne les informations du compte utilisateur")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        AuthResponseDTO response = authService.login(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Enregistre un profil utilisateur dans la base de données et l'associe optionnellement à son compte Firebase")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        AuthResponseDTO response = authService.register(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Profil de l'utilisateur connecté", description = "Retourne les informations de l'utilisateur actuellement authentifié via son Bearer Token")
    public ResponseEntity<AuthResponseDTO> getCurrentUser() {
        AuthResponseDTO response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }
}
